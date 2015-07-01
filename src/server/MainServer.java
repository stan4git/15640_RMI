package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

import message.ExceptionMessage;
import message.GetStubMessage;
import message.MethodInvocationMessage;
import message.RMIMessage;
import message.ReturnMessage;
import exception.RemoteException;
import exception.RemoteException.Type;
import registry.RegistryClient;
import util.CommunicationUtil;
import util.Constants;
import util.Util;
/**
 * This is the main server thread.
 * It has three hashmap to manage object-id, hashcode-method object and ROR-instanceID mapping.
 */
public class MainServer {
	int port;
	private Integer instanceID = 0;
	private HashMap <Integer, Object> instanceList = new HashMap<Integer, Object>();
	private HashMap <Integer, HashMap <Long, Method>> methodList = new HashMap <Integer, HashMap <Long, Method>>();
	private HashMap <Object, Integer> keyList = new HashMap<Object, Integer>();
	static MainServer server;

	public MainServer(int port) {
		this.port = port;
	}
	
	public static void main(String args[]) {
		
		//step 1 : new MainServer instance
		if (args.length < 1) {
			server = new MainServer(Constants.DEFAULT_MAIN_SERVER_PORT);
		} else {
			server = new MainServer(Integer.parseInt(args[0]));
		}
		// step 2 : build the server socket for Server Registry
		Thread rs = new Thread(new MainServerRegisterService(server));
		rs.start();
		// step 3 : process the command
		server.processCommand();
	}

	/**
	 * This method is responsible for processing the message sent by client.
	 * Message types can be : 
	 * 
	 * (1) GetStubMessage -> The client may ask the server for the content of the specific stub class.
	 * (2) MethodInvocationMessage -> The client may ask the server to invoke specific method.
	 * 
	 * @param message
	 */
	
	public void processCommand () {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("->");
		String command;
		String args[];
		
		while(true) {
			try {
				command = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			args = command.split(" ");
			
			if(args.length == 1 && args[0].equals("quit")) {
				System.exit(0);
			} else if (args.length == 3 && args[0].equals("register")) {
				String className = args[1];
				String serviceName = args[2];

				try {
					Class<?> implClass = Class.forName(className);
					if (!(Remote.class.isAssignableFrom(implClass))) {
						System.err.println("The object is not a remote obejct!");
						continue;
					}
					Constructor<?>[] cons = implClass.getConstructors();
					Remote instance = (Remote) cons[0].newInstance();
					String localIP = InetAddress.getLocalHost().getHostAddress().toString();
					RemoteRef rr = new RemoteRef(localIP, port, instanceID,implClass.getName());
					addNewObject(instance);
					RegistryClient client = new RegistryClient(Constants.REGISTRY_IP, Constants.REGISTRY_PORT);
					client.rebind(serviceName, rr);
					continue;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			} else {
				System.err.println("Please provide correct command!");
				System.out.println("Usage: register ImplmentedClassName InstanceName");
				continue;
			}
		}
	}
	
	/**
	 * @param instance is the instance to be added
	 */
	public void addNewObject(Object instance) {
		Method[] methods = instance.getClass().getMethods();
		HashMap<Long, Method> tmpList = new HashMap<Long, Method>();
		for (Method method : methods) {
			tmpList.put(Util.computeMethodHash(method), method);
		}

		synchronized (instanceList) {
			synchronized (methodList) {
				synchronized(keyList){
					synchronized (instanceID) {
						methodList.put(instanceID, tmpList);
						instanceList.put(instanceID, instance);
						keyList.put(instance, instanceID);
						instanceID++;
					}
				}
			}
		}
	}
	
	
	/**
	 * @param message the message received.
	 * @param host the host where the message is sent from.
	 * @param port the port which the message is sent from.
	 */
	public void processMessage (RMIMessage message, String host, int port) {
		
		if(message instanceof GetStubMessage) {

			GetStubMessage gsm = (GetStubMessage) message.get();
			
			String objName = gsm.getObjectClassName();
			
			System.out.println(objName);
			
			int pos = objName.lastIndexOf(".");
			String packagename = objName.substring(0, pos);
			String filename = objName.substring(pos+1);
			
			byte[] classContent = Util.readFile(packagename +"/"+ filename +".class");
			ReturnMessage rm = new ReturnMessage();
			rm.set(classContent);
			try {
				CommunicationUtil.sendMessage(host, port, (RMIMessage) rm);
			} catch (RemoteException e) {
				System.err.println(e.getType().toString() + ": "+ e.getCause().toString());
			}
			System.out.println("The server has sent the content of the stub class to the client !");
			return;

		} 
		
		if (message instanceof MethodInvocationMessage) {

			MethodInvocationMessage mis  = (MethodInvocationMessage) message.get();
			long methodHashCode = mis.getMethodHashValue();
			int instanceID = mis.getInstanceID();
			Object instance = instanceList.get(instanceID);

			if(instance == null) {
				RemoteException e = new RemoteException(Type.NoSuchInstance, new Throwable("The server doesn't have such instance!"));
				ExceptionMessage em = new ExceptionMessage();
				em.set(e);
				try {
					CommunicationUtil.sendMessage(host, port, (RMIMessage)em);
				} catch (RemoteException re) {
					System.err.println(re.getType().toString() + ": "+ re.getCause().toString());
				}
				return;
			}

			Method method = methodList.get(instanceID).get(methodHashCode);
			Object[] params = mis.getParams();

			if(method == null) {
				RemoteException e = new RemoteException(Type.NoSuchMethod, new Throwable("The instance doesn't have such method!"));
				ExceptionMessage em = new ExceptionMessage();
				em.set(e);
				try {
					CommunicationUtil.sendMessage(host, port, (RMIMessage)em);
				} catch (RemoteException re) {
					System.err.println(re.getType().toString() + ": "+ re.getCause().toString());
				}
				return;
			} 

			try {
				if(params != null) {
					for(int i = 0;i < params.length;i++){
						if(RemoteStub.class.isAssignableFrom(params[i].getClass())) {
							params[i] = instanceList.get(((RemoteStub)params[i]).getRR().getInstanceID());
						}
					}
				}
				
				Object returnValue = method.invoke(instance, params);
				Class<?> ins = instance.getClass();
				
				/*
				 * the situations where the remote object need be returned, localise the corresponding remote
				 * object reference to get the stub and return it
				 */
				if( returnValue != null && returnValue.getClass().equals(ins)){
					
					String hostip = null;
					try {
						hostip = InetAddress.getLocalHost().getHostAddress().toString();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					addNewObject(returnValue);
					RemoteRef rr = new RemoteRef(hostip, this.port,keyList.get(returnValue), ins.getName());
					returnValue = rr.localise();
				}
				ReturnMessage rm = new ReturnMessage();
				rm.set(returnValue);
				System.out.println(method.getName() + " method invoked by instance "+instanceID+" has been executed");
				try {
					CommunicationUtil.sendMessage(host, port, (RMIMessage)rm);
				} catch (RemoteException e) {
					System.err.println(e.getType().toString() + ": "+ e.getCause().toString());
				}
				return;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} else {
			RemoteException e = new RemoteException(Type.WrongMessage, new Throwable("The server cannot handle such message!"));
			ExceptionMessage em = new ExceptionMessage();
			em.set(e);
			try {
				CommunicationUtil.sendMessage(host, port, (RMIMessage)em);
			} catch (RemoteException re) {
				System.err.println(re.getType().toString() + ": "+ re.getCause().toString());
			}
		}
	}
}

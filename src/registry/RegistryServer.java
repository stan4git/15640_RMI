package registry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import exception.RemoteException;
import exception.RemoteException.Type;
import message.ExceptionMessage;
import server.Remote;
import server.RemoteRef;
import util.Constants;

/**
 * This class is a core class in registry part. It also implements
 * the Registry interface. This class maintains a HashMap which was used
 * to record the service name and correlated Remote Object Reference.
 * This class provides 3 main methods: lookup(), rebind() and list()
 *
 */
public class RegistryServer implements Registry {

	private static final long serialVersionUID = 4751638760612530381L;
	private int port;
	private HashMap<String, RemoteRef> instanceList = new HashMap<String, RemoteRef>();

	/** 
     * constructor of RegistryServer class
     * 
     * @param port		the port number of the RMI registry server 
     */
	public RegistryServer(int port) {
		this.port = port;
	}

	/**
	 * This method return a remoteRef by matching the service name
	 */
	@Override
	public Remote lookup(String name) {
		synchronized(instanceList) {	
			if(!instanceList.containsKey(name)) {
				RemoteException e = new RemoteException(Type.NoSuchInstance, new Throwable("The server doesn't have such instance!"));
				ExceptionMessage em = new ExceptionMessage();
				em.set(e);
				return em;
			}
			return (Remote) instanceList.get(name);
		}
	}

	/**
	 * This method bind the service name and RemoteRef
	 */
	@Override
	public void rebind(String name, RemoteRef ref) {
		synchronized(instanceList) {
			instanceList.put(name, ref);
		}
		System.out.println("Service \"" + name + "\" has been registered!");
	}

	/**
	 * This method list all the registered service on the registry server
	 */
	@Override
	public String[] list() {
		String[] names = null;
		synchronized(instanceList) {
			names = new String[instanceList.size()];
			int i = 0;
			for(String name : instanceList.keySet()) {
				names[i++] = name;
			}
		}
		return names;
	}

	
	/**
	 * This main method is used to setup a registry server and 
	 * waiting for registry client to connect. After the client 
	 * connects to server, the server will add a listener to monitor 
	 * the clients message.
	 * @param args
	 */
	@SuppressWarnings("resource")
	public static void main (String args[]) {
		RegistryServer server;
		ServerSocket registryServerSocket = null;
		// step 1: new RegistryServer instance
		if(args.length < 1) {
			server = new RegistryServer(Constants.REGISTRY_PORT);
		} else {
			server = new RegistryServer(Integer.parseInt(args[0]));
		}
		// step 2 : build the server socket for Server Registry
		try {
			registryServerSocket = new ServerSocket(server.port);
			System.out.println("Registry server is running...");
		} catch (IOException e) {
			System.err.println("Registry server start up error!");
			System.exit(-1);
		}
		// step 3: build listener for each connection
		while(true) {
			Socket socket;
			try {
				socket = registryServerSocket.accept();
				System.out.println("Connection established to "
						+ socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
				Thread rs = new Thread(new RegistryService(socket, server));
				rs.start();
			} catch (IOException e) {
				System.err.println("Can not create a connection between the client and the server!");
			}
		}
	}

}

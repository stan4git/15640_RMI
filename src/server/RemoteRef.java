package server;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import exception.RemoteException;
import message.ExceptionMessage;
import message.GetStubMessage;
import message.RMIMessage;
import message.ReturnMessage;
import util.CommunicationUtil;
import util.Util;

/**
 * 
 *	This class stores all the information needed by client to get
 *	the reference of object on server side.
 */
public class RemoteRef implements Serializable, Remote{
	
	private static final long serialVersionUID = 5163325410199044171L;
	private String host;
	private int port;
	private int instanceID;
	private String interfaceName;
	
	/**
	 * @param host server ip address
	 * @param port server service port number
	 * @param instanceID instance id of the service object
	 * @param interfaceName name of interface to be implemented.
	 */
	public RemoteRef(String host, int port, int instanceID, String interfaceName) {
		this.host = host;
		this.port = port;
		this.instanceID = instanceID;
		this.interfaceName = interfaceName;
	}
	
	
	/**
	 * This method is used by client to check if stub exists.
	 * if not, it downloads stub automatically from server.
	 */
	public Object localise() {
		String stubClassName = this.interfaceName+"_Stub";
		Remote stub = null;
		
		try {
			Class<?> stubClass = Class.forName(stubClassName);
			Constructor<?> con = stubClass.getConstructor(this.getClass());
			stub = (Remote) con.newInstance(this);
		} catch (ClassNotFoundException e) {
			try {
				CommunicationUtil.sendMessage(this.host, this.port, new GetStubMessage(this.getHost(),this.port,stubClassName));
			} catch (RemoteException e1) {
				System.err.println(e1.getType().toString() + ": "+ e1.getCause().toString());
			}
			
			byte[] classContent = null;
			try {
				RMIMessage msg = CommunicationUtil.receiveMessage(host, port);
				if (msg instanceof ReturnMessage) {
					classContent = (byte[]) ((ReturnMessage) msg).get();
				} else {
					throw (RemoteException) ((ExceptionMessage) msg).get();
				}
			} catch (RemoteException e1) {
				System.err.println(e1.getType().toString() + ": "
						+ e1.getCause().toString());
			}
			
			if(classContent == null) {
				System.err.println("Server doesn't have such class file");
				return null;
			}
			
			int index = stubClassName.lastIndexOf('.');
			String packageName = stubClassName.substring(0, index);
			String className = stubClassName.substring(index + 1);
			Util.writeFile(packageName+"/"+className+".class", classContent);
			Class<?> stubClass = null;
			try {
				stubClass = this.getClass().getClassLoader().loadClass(packageName+"."+className);
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			
			if (stubClass != null) {
				try {
					Constructor<?> con = stubClass.getConstructor(this.getClass());
					stub = (Remote) con.newInstance(this);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		} catch ( IllegalArgumentException | InstantiationException
				| IllegalAccessException | InvocationTargetException 
				| SecurityException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		return stub;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getInstanceID() {
		return instanceID;
	}
	public void setInstanceID(int instanceID) {
		this.instanceID = instanceID;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	
}

package registry;

import java.io.Serializable;

import server.Remote;
import server.RemoteRef;

/**
 * This is a interface which is used to represent this is a 
 * Registry object. It also extends the Serializable interface
 * which was used to transfer through the network.
 *
 */
public interface Registry extends Remote, Serializable {
	
	/**
	 * This method is used to lookup the service which was maintained
	 * on the registry server. It returns a remote object reference.
	 * @param name data type is a String, this is the service's name
	 * @return RemoteRef
	 * @throws Exception
	 */
	public Remote lookup(String name) throws Exception;
	/**
	 * This method is used to bind the Remote Object Reference with 
	 * a service name
	 * @param name String, it represents the service name
	 * @param ref RemoteRef, it is a remote object reference
	 */
	public void rebind(String name, RemoteRef ref);
	/**
	 * This method is used to list all the registed service
	 * @return
	 */
	public String[] list();
	
}

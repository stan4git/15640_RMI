package registry;

import exception.RemoteException;
import server.Remote;
import server.RemoteRef;
import util.CommunicationUtil;
import message.ExceptionMessage;
import message.RMIMessage;
import message.RegistryMessage;
import message.RegistryMessage.Operation;
/**
 * This Class is used by the client which wants to use
 * the Registry Service. It implements the Registry interface.
 * It is a remote object also it extends the Serializable interface
 */
public class RegistryClient implements Registry {
	
	private static final long serialVersionUID = 4867198059237040685L;
	private String host;
	private int port;
	
	public RegistryClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	/**
	 * This is the implement method of superclass
	 */
	@Override
	public Remote lookup(String name) throws Exception {
		RegistryMessage rm = new RegistryMessage(name, null, Operation.LOOKUP);
		try {
			CommunicationUtil.sendMessage(host, port, (RMIMessage) rm);
			System.out.println("Message sent to registry...");
		} catch (RemoteException e) {
			System.err.println(e.getType().toString() + ": "+ e.getCause().toString());
		}
		
		RMIMessage message = null;
		try {
			message = CommunicationUtil.receiveMessage(host, port);
			System.out.println("Message received from registry...");
		} catch (RemoteException e) {
			System.err.println(e.getType().toString() + ": "+ e.getCause().toString());
		}
		
		if (message instanceof ExceptionMessage) {
			RemoteException e = (RemoteException) ((ExceptionMessage) message).get();
			throw (new Exception(e.getType().toString() + ": "+ e.getCause().toString()));
		} else {
			return (Remote) message.get();
		}
	}

	/**
	 * This is the implement method of superclass
	 */
	@Override
	public void rebind(String name, RemoteRef ref) {
		RegistryMessage rm = new RegistryMessage(name, ref, Operation.REBIND);
		try {
			CommunicationUtil.sendMessage(host, port, (RMIMessage) rm);
			System.out.println("The server has sent the registry information to the registry server !");
		} catch (RemoteException e) {
			System.err.println(e.getType().toString() + ": "+ e.getCause().toString());
		}
	}

	/**
	 * This is the implement method of superclass
	 */
	@Override
	public String[] list() {
		RegistryMessage rm = new RegistryMessage(null, null, Operation.LIST);
		try {
			CommunicationUtil.sendMessage(host, port, (RMIMessage) rm);
		} catch (RemoteException e) {
			System.err.println(e.getType().toString() + ": "+ e.getCause().toString());
		}
		RMIMessage message = null;
		try {
			message = CommunicationUtil.receiveMessage(host, port);
		} catch (RemoteException e) {
			System.err.println(e.getType().toString() + ": "+ e.getCause().toString());
		}
		return (String[]) message.get();
	}

}

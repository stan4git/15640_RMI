package registry;

import java.io.IOException;
import java.net.Socket;

import exception.RemoteException;
import exception.RemoteException.Type;
import server.Remote;
import server.RemoteRef;
import util.CommunicationUtil;
import message.ExceptionMessage;
import message.RMIMessage;
import message.RegistryMessage;
import message.ReturnMessage;
import message.RegistryMessage.Operation;

/**
 *  This class is a runnable class which was used to listen the 
 *  incoming message from registry client.
 *
 */
public class RegistryService implements Runnable{
	
	Socket socket;
	RegistryServer server;

	/** 
     * constructor of RegistryService class
     * 
     * @param socket the client's socket object
     * @param the registry server's instance
     */
	public RegistryService(Socket socket, RegistryServer server) throws IOException {
		this.socket = socket;
		this.server = server;
	}
	
	/**
	 * This run method is implemented to monitor the message from client
	 */
	@Override
	public void run() {
		// step 1: add the connection to the connection pool
		String host = socket.getInetAddress().getHostAddress().toString();
		int port = socket.getPort();
		String key = host +":" + port;
		CommunicationUtil.add(key, socket);
		// step 2 : listen the new incoming message
		while(true) {
			RMIMessage message;
			try {
				message = (RMIMessage) CommunicationUtil.receiveMessage(host, port);
			} catch (IOException e) {
				System.out.printf("Message received...");
				break;
			}
			// step 3 : process the message
			processMessage(message,host,port);
		}
	}
	
	/**
	 * This method is used to handle the incoming message.
	 * It solves the RegistryMessage which contains: LOOKUP,
	 * REBIND and LIST. If the message is not he RegistryMessage,
	 * it will throw out the remote Exception message.
	 * 
	 * @param message Clients' message
	 * @param host  client ip address
	 * @param port  client port 
	 */
	public void processMessage (RMIMessage message, String host, int port) {

		if(message instanceof RegistryMessage) {
			RegistryMessage rm = (RegistryMessage) message.get();
			if(rm.getOperation() == Operation.LOOKUP) {
				Remote obj = server.lookup(rm.getName());
				if (obj instanceof RemoteRef) {
					RemoteRef rr = (RemoteRef) server.lookup(rm.getName());
					ReturnMessage rtm = new ReturnMessage();
					rtm.set(rr);
					try {
						CommunicationUtil.sendMessage(host, port, (RMIMessage) rtm);
						System.out.println("Message replied to " + host + ":" + port +" ...");
					} catch (RemoteException e) {
						System.err.println(e.getType().toString() + ": "
								+ e.getCause().toString());
					}
				} else {
					ExceptionMessage errMsg = (ExceptionMessage) obj;
					System.err.println(((RemoteException) errMsg.get()).getMessage());
				}
				return;
			} 
			
			if (rm.getOperation() == Operation.LIST) {
				String[] names = server.list();
				ReturnMessage rtm = new ReturnMessage();
				rtm.set(names);
				try {
					CommunicationUtil.sendMessage(host, port, (RMIMessage) rtm);
				} catch (RemoteException e) {
					System.err.println(e.getType().toString() + ": "+ e.getCause().toString());
				}
				return;
			} 
			
			if (rm.getOperation() == Operation.REBIND) {
				server.rebind(rm.getName(), rm.getRr());
				return;
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

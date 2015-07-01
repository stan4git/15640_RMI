package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import exception.RemoteException;
import exception.RemoteException.Type;
import message.RMIMessage;

/**
 * This class contains a lot of common communication utilities.
 * Such as Send Message you just need to provide the host
 * IP, port and message, it will send the message to the destination.
 * If the socket has not been built, it will automatically build the 
 * connection. It also supplies the socket cache.
 * 
 * It provides 3 methods: sendMessage(), receiveMessage() and add().
 *
 */
public class CommunicationUtil {
	private static HashMap<String, Socket> socketList = new HashMap<String, Socket>();
	private static Socket socket;
	
	/**
	 * This method is used to send the message to the destination host.
	 * 
	 * @param host destination host's IP
	 * @param port destination host's port
	 * @param message the sending message
	 * @throws RemoteException
	 */
	public static void sendMessage (String host, int port, RMIMessage message) throws RemoteException {
		String identifier = host + ":" + port;
		try {
			if(!socketList.containsKey(identifier)) {
				socket = new Socket(host, port);
				synchronized(socketList) {
					socketList.put(identifier, socket);
				}
			} else {
				socket = socketList.get(identifier);
			}
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RemoteException(Type.SendMessageFail, new Throwable("There are problems with I/O!"));
		}
	}
	
	/**
	 * This method is used to receive the message from the sending host.
	 * 
	 * @param host sending host's IP
	 * @param port sending host's port
	 * @throws RemoteException
	 */
	public static RMIMessage receiveMessage (String host, int port) throws RemoteException {
		
		String identifier = host + ":" + port;
		RMIMessage message = null;
		try {
			if(!socketList.containsKey(identifier)) {
				socket = new Socket(host, port);
				synchronized(socketList) {
					socketList.put(identifier, socket);
				}
			} else {
				socket = socketList.get(identifier);
			}
//			System.out.println(socket.isConnected());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			message = (RMIMessage) in.readObject();
		} catch (IOException e) {
			throw new RemoteException(Type.SendMessageFail, new Throwable("There are problems with I/O!"));
		} catch (ClassNotFoundException e){
			e.printStackTrace();
		}
		return message;
	}
	
	/**
	 * This method is used to add the socket to the cache
	 * @param key IP+Port
	 * @param sock Socket object
	 */
	public static void add(String key, Socket sock) {
		synchronized(socketList) {
			socketList.put(key, sock);
		}
	}
}

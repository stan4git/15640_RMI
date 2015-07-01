package server;

import java.io.IOException;
import java.net.Socket;
import util.CommunicationUtil;
import message.RMIMessage;

/**
 * This class runs a thread to handle requests sent to server.
 * It will pass the message to main server.
 */
public class ListenerService implements Runnable {
	Socket socket;
	MainServer server;
	/**
	 * @param socket socket connection to whom sent.
	 * @param server main server instance, used for communication
	 * @throws IOException
	 */
	public ListenerService(Socket socket, MainServer server) throws IOException {
		this.socket = socket;
		this.server = server;
	}
	
	@Override
	public void run() {
		String host = socket.getInetAddress().getHostAddress().toString();
		int port = socket.getPort();
		String key = host +":" + port;
		CommunicationUtil.add(key, socket);
		while(true) {
			RMIMessage message;
			try {
				message = (RMIMessage)CommunicationUtil.receiveMessage(host, port);
				System.out.println("Client message received...");
			} catch (IOException e) {
				System.out.printf("Registry Server: Job Finished at %s:%d\n",host,port);
				break;
			}
			// step 3 : process the message
			server.processMessage(message,host,port);
		}
	}
}

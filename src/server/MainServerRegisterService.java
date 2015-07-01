package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * 
 * This class handles connection requests.
 */
public class MainServerRegisterService implements Runnable {

	ServerSocket mainServerSocket = null;
	MainServer server = null;
	/**
	 * @param server is the main server thread.
	 */
	public MainServerRegisterService(MainServer server) {
		this.server = server;
		try {
			mainServerSocket = new ServerSocket(server.port);
			System.out.println("Main ServerSocket setup successfully!");
		} catch (IOException e) {
			System.err.println("Main ServerSocket setup error!");
			System.exit(-1);
		}
	}

	public void run() {
		while (true) {
			Socket socket;
			try {
				socket = mainServerSocket.accept();
				System.out.println("Connection established to "
						+ socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
				Thread rs = new Thread(new ListenerService(socket, server));
				rs.start();
			} catch (IOException e) {
				System.err.println("Can not create a connection between the client and the server!");
			}
		}
	}

}

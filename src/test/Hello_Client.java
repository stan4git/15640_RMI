package test;

import registry.RegistryClient;
import server.RemoteRef;
import shareFolder.Hello_Interface;
import util.Constants;
/**
 *	This is the implementation class of test0.
 *
 */
public class Hello_Client implements Hello_Interface {
	private String host;
	private int port;
	
	@Override
	public String sayHello(String name) {
		RemoteRef ref = null;
		try {
			ref = (RemoteRef)(new RegistryClient(host,port).lookup("sayHello"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Hello_Interface hs = (Hello_Interface) ref.localise();
		return hs.sayHello(name);
	}

	@Override
	public String sayHello(String name, Long meters) {
		RemoteRef ref = null;
		try {
			ref = (RemoteRef)(new RegistryClient(host,port).lookup("sayHello"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Hello_Interface hs = (Hello_Interface) ref.localise();
		return hs.sayHello(name, meters);
	}

	public static void main(String[] args) {
		Hello_Client hello = new Hello_Client();
		if(args.length >= 2) {
			hello.host = args[0];
			hello.port = Integer.parseInt(args[1]);
		}
		else {
			hello.host = Constants.REGISTRY_IP;
			hello.port = Constants.REGISTRY_PORT;
		}
		
		System.out.println(hello.sayHello("Michael"));
		System.out.println(hello.sayHello("Michael",10000L));
	}

}

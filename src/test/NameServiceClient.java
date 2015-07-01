package test;

import java.io.FileNotFoundException;

import registry.RegistryClient;
import server.RemoteRef;
import shareFolder.NameServer;

/**
 *	This is the implementation class of test3.
 *
 */
public class NameServiceClient {
	public static void main(String[] args) throws FileNotFoundException {
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		String serviceName = args[2];

		// locate the registry and get ror.
		RegistryClient rc = new RegistryClient(host, port);
		RemoteRef ror = null;
		try {
			ror = (RemoteRef) rc.lookup(serviceName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// get (create) the stub out of ror.
		NameServer ns = (NameServer) ror.localise();
		
		ns = ns.add("Service1", ror, ns);
		ror = ns.match("Service1");
		if (ror == null) {
			System.out.println("Cannot find Service1");
			return;
		} 
		System.out.println("Service1 founded. ID:" + ror.getInstanceID());
		
//		ns = (NameServer) ror.localise();
		ns = ns.add("Service2", ror, ns);

		ror = ns.next().match("Service1");
		if (ror == null) {
			System.out.println("Cannot find Service1");
			return;
		}
		
		System.out.println("Test Sucess!");
		return;
	}
}

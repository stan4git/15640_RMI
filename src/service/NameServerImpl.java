package service;

import server.Remote;
import server.RemoteRef;
import shareFolder.NameServer;
/**
 * This is implementation of test3.
 *
 */
public class NameServerImpl implements NameServer, Remote {
	String serviceName;
	RemoteRef ref;
	NameServer next;

	public NameServerImpl() {
		serviceName = "";
		ref = null;
		next = null;
	}

	public NameServerImpl(String s, RemoteRef r, NameServer n) {
		serviceName = s;
		ref = r;
		next = n;
	}

	public NameServer add(String s, RemoteRef r, NameServer n) {
		return new NameServerImpl(s, r, this);
	}

	public RemoteRef match(String name) {
		if (name.equals(serviceName))
			return ref;
		else
			return null;
	}

	public NameServer next() {
		return next;
	}
}

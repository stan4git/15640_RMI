package shareFolder;

import server.RemoteRef;
/**
 *	This is the interface be implemented by client and server.
 *
 */
public interface NameServer // extends YourRemote 
{
    public RemoteRef match(String name);
    public NameServer add(String s, RemoteRef r, NameServer n);
    public NameServer next();
}


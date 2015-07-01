package message;

import java.io.Serializable;

import server.Remote;

/**
 * 
 *	This is the based class for all message.
 */

public interface RMIMessage extends Remote, Serializable {
	
	public Object get();
	
	public void set(Object obj);

}

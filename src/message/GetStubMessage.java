package message;

/**
 * 
 *	This class carry stub request information which will be sent to 
 *	Server.
 */
public class GetStubMessage implements RMIMessage{
	
	private static final long serialVersionUID = 5637892793938222465L;
	private String host;
	private int port;
	private String objectClassName;
	
	public GetStubMessage(String host, int port, String objectClassName) {
		this.host = host;
		this.port = port;
		this.objectClassName = objectClassName;
	}
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getObjectClassName() {
		return objectClassName;
	}
	public void setObjectClassName(String objectClassName) {
		this.objectClassName = objectClassName;
	}
	
	@Override
	public Object get() {
		return this;
	}

	@Override
	public void set(Object obj) {
	}
	
	
	

}

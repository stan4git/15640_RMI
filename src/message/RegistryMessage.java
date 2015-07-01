package message;

import server.RemoteRef;

/**
 * 
 *	This class is used by client to request RemoteObjectReference 
 *	from registry server.
 */
public class RegistryMessage implements RMIMessage{
	
	private static final long serialVersionUID = 5463924604192972666L;
	private String name;
	private RemoteRef rr;
	private Operation operation;
	
	/**
	 * 
	 * @param name is the name of service to be used
	 * @param rr is remote reference of server
	 * @param operation operation to be performed
	 */
	public RegistryMessage(String name, RemoteRef rr, Operation operation) {
		this.name = name;
		this.rr = rr;
		this.operation = operation;
		
	}
	
	public enum Operation {
		REBIND, LOOKUP, LIST
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RemoteRef getRr() {
		return rr;
	}

	public void setRr(RemoteRef rr) {
		this.rr = rr;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}
	
	@Override
	public Object get() {
		return this;
	}

	@Override
	public void set(Object obj) {
	}
	
	

}

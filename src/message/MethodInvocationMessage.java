package message;
/**
 * 
 *	This class carries method invocation information which is sent by client to server
 */
public class MethodInvocationMessage implements RMIMessage{
	
	private static final long serialVersionUID = -6064142633792325851L;
	private int instanceID;
	private long methodHashValue;
	private Object[] params;
	/**
	 * 
	 * @param instanceID is unique identifier for instance on server
	 * @param methodHashValue is computed by hashCode() method to identify the method to be invoked.
	 * @param params contains all parameters to be used for method invokation.
	 */
	public MethodInvocationMessage(int instanceID, long methodHashValue, Object[] params) {
		this.instanceID = instanceID;
		this.methodHashValue = methodHashValue;
		this.params = params;
	}

	public int getInstanceID() {
		return instanceID;
	}


	public void setInstanceID(int instanceID) {
		this.instanceID = instanceID;
	}

	public long getMethodHashValue() {
		return methodHashValue;
	}
	public void setMethodHashValue(long methodHashValue) {
		this.methodHashValue = methodHashValue;
	}
	public Object[] getParams() {
		return params;
	}
	public void setParams(Object[] params) {
		this.params = params;
	}
	
	@Override
	public Object get() {
		return this;
	}

	@Override
	public void set(Object obj) {
	}
}

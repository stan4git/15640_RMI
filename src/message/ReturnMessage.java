package message;
/**
 * This class is used by server to return process result.
 *
 */
public class ReturnMessage implements RMIMessage{
	
	private static final long serialVersionUID = -2920287837082768640L;
	Object returnInfo;

	@Override
	public Object get() {
		return returnInfo;
	}

	@Override
	public void set(Object obj) {
		this.returnInfo = obj;
	}
	

}


package message;
/**
 * 
 *	This class is used to return Exception message from Server
 *	It implements RMIMessage base class.
 */
public class ExceptionMessage implements RMIMessage {
	
	private static final long serialVersionUID = -7804293422327406625L;
	
	private Object exceptionInfo;

	@Override
	public Object get() {
		return exceptionInfo;
	}

	@Override
	public void set(Object obj) {
		this.exceptionInfo = obj;
	}
}

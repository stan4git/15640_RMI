package exception;

import java.io.IOException;

/**
 * 
 * This class is used for storing exception message
 * it will be packed into ExceptionMessage and send back to Client
 */
public class RemoteException extends IOException {
	
	private static final long serialVersionUID = 8845669616785991137L;
	public Throwable cause;
	private Type type;
	
	public enum Type {
		NoSuchMethod,
		NoSuchInstance,
		WrongMessage,
		SendMessageFail,
		ReceiveMessageFail
	}
	/**
	 * 
	 * @param type
	 * @param cause
	 */
	public RemoteException(Type type, Throwable cause) {
//		super(type.toString());
//		initCause(null);
		this.type = type;
        this.cause = cause;
    }
	
	public Throwable getCause() {
		return this.cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	public Type getType() {
		return this.type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
}

package cn.com.zwz.lock.support;

public class LockRuntimeException extends RuntimeException {
	
	private static final long serialVersionUID = 1223993898389L;

	public LockRuntimeException() {
	        super();
	    }

	    public LockRuntimeException(String message) {
	        super(message);
	    }
	    
	    public LockRuntimeException(String message, Throwable cause) {
	        super(message, cause);
	    }

	    public LockRuntimeException(Throwable cause) {
	        super(cause);
	    }
	    
	    protected LockRuntimeException(String message, Throwable cause,
	                               boolean enableSuppression,
	                               boolean writableStackTrace) {
	        super(message, cause, enableSuppression, writableStackTrace);
	    }
}

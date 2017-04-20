package cbit.vcell.message;

public class VCMessagingInvocationTargetException extends Exception {
	public VCMessagingInvocationTargetException(Throwable targetException){
		super(targetException);
	}
	public Throwable getTargetException(){
		return super.getCause();
	}
}

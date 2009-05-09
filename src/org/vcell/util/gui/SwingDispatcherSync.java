package org.vcell.util.gui;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.vcell.util.UserCancelException;


/**
 * Ensures that abstract method runWithException is run on the eventDispatchThread.
 * <p>Ensures that SwingUtilities.invokeAndWait returns the correct exception if necessary.
 * @author frm
 *
 */
public abstract class SwingDispatcherSync {

	private Object returnValue = null;
	
	private class SwingDispatchException extends RuntimeException{
		public SwingDispatchException(Throwable e){
			super(e);
		}
	};
	
	public abstract Object runSwing() throws Exception;

	private static Exception handleThrowableAsException(Throwable throwable){
		if (throwable instanceof Exception){
			return (Exception) throwable;
		}
		throw (Error) throwable;
	}
	private static RuntimeException handleThrowableAsRuntimeException(Throwable throwable) {
		if (throwable instanceof RuntimeException){
			return (RuntimeException) throwable;
		}
		if (throwable instanceof Exception){
			return new RuntimeException(throwable.getMessage(),throwable);
		}
		throw (Error) throwable;
	}
	
	private Object dispatch() throws Throwable{
		if (SwingUtilities.isEventDispatchThread()) {
			return runSwing();
		}
		Runnable runnable =	new Runnable(){
			public void run(){
				try{
					returnValue = runSwing();
				}catch(Throwable e){
					throw new SwingDispatchException(e);
				}
			}
		};
		try {
			SwingUtilities.invokeAndWait(runnable);
			return returnValue;
		} catch(Throwable e) {
			if (e instanceof InvocationTargetException){
				if (e.getCause() instanceof SwingDispatchException){
					throw e.getCause().getCause();
				}
				if (e.getCause() != null){
					throw e.getCause();
				}
			}
			throw e;
		}
	}	

	public Object dispatchWithException() throws Exception{
		try{
			return dispatch();
		} catch(Throwable throwable){
			printStack(throwable);
			throw handleThrowableAsException(throwable);
		}
	}
	public Object dispatchWrapRuntime(){
		try{
			return dispatch();
		}catch(Throwable throwable){
			printStack(throwable);
			throw handleThrowableAsRuntimeException(throwable);
		}
	}
	public Object dispatchConsumeException(){
		try{
			return dispatch();
		}catch(Throwable throwable){
			printStack(throwable);
			return null;
		}
	}
	private void printStack(Throwable throwable){
		if(!(throwable instanceof UserCancelException)){
			throwable.printStackTrace();
		}
	}
}

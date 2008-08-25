package cbit.util;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import cbit.vcell.client.task.UserCancelException;

/**
 * Ensures that abstract method runWithException is run on the eventDispatchThread.
 * <p>Ensures that SwingUtilities.invokeAndWait returns the correct exception if necessary.
 * @author frm
 *
 */
public abstract class EventDispatchRunWithException {

		private Object returnValue;
		private class RunnableException extends RuntimeException{
			public RunnableException(Throwable e){
				super(e);
			}
		};
		
		public abstract Object runWithException() throws Exception;

		private static Exception handleThrowableAsException(Throwable throwable){
			if(throwable instanceof Exception){
				return (Exception) throwable;
			}
			throw (Error) throwable;
		}
		private static RuntimeException handleThrowableAsRuntimeException(Throwable throwable){
			if(throwable instanceof RuntimeException){
				return (RuntimeException) throwable;
			}
			if(throwable instanceof Exception){
				return new RuntimeException(throwable.getMessage(),throwable);
			}
			throw (Error) throwable;
		}
		
		private Object runEventDispatchThreadSafely() throws Throwable{
			if(SwingUtilities.isEventDispatchThread()){
				return runWithException();
			}else{
				Runnable runnable =
					new Runnable(){
						public void run(){
							try{
								returnValue = runWithException();
							}catch(Throwable e){
								throw new RunnableException(e);
							}
				}};
				try{
					SwingUtilities.invokeAndWait(runnable);
					return returnValue;
				}catch(Throwable e){
					if(e instanceof InvocationTargetException){
						if(e.getCause() instanceof RunnableException){
							throw e.getCause().getCause();
						}
						if(e.getCause() != null){
							throw e.getCause();
						}
					}
					throw e;
				}
			}
		}

		public Object runEventDispatchThreadSafelyWithException() throws Exception{
			try{
				return runEventDispatchThreadSafely();
			}catch(Throwable throwable){
				printStack(throwable);
				throw handleThrowableAsException(throwable);
			}
		}
		public Object runEventDispatchThreadSafelyWrapRuntime(){
			try{
				return runEventDispatchThreadSafely();
			}catch(Throwable throwable){
				printStack(throwable);
				throw handleThrowableAsRuntimeException(throwable);
			}
		}
		public Object runEventDispatchThreadSafelyConsumeException(){
			try{
				return runEventDispatchThreadSafely();
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

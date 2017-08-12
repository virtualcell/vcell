package org.vcell.util.gui;

import java.awt.EventQueue;

import org.vcell.util.UserCancelException;
import org.vcell.util.UtilCancelException;


/**
 * execute functions which return a value on the Swing dispatch thread
 */
public class VCSwingFunction {
	
	public interface Producer <R>{
		R runSwing(  ) throws Exception;
	}
	public interface Doer {
		void runSwing(  ) throws Exception;
	}
	
	/**
	 * execute on AWT dispatch thread, waiting for completion before returning 
	 * @param r not null
	 */
	public static void runOnDispatchThread(Runnable r) throws Exception {
		if (EventQueue.isDispatchThread() ) {
			r.run();
		}
		else {
			EventQueue.invokeAndWait(r);
		}
	}
	
	/**
	 * execute on Swing
	 * @param supplier
	 * @return result produced
	 * @throws Exception
	 */
	public static <T> T execute(Producer<T> supplier) throws Exception{
		ProducerWrapper<T> wrapper = new ProducerWrapper<>(supplier);
		runOnDispatchThread(wrapper);
		if (wrapper.exception == null) {
			return wrapper.result;
		}
		throw wrapper.exception;
	}
	/**
	 * execute on Swing, throwing Exceptions 
      * @param d 
	 * @throws Exception
	 */
	public static void execute(Doer d) throws Exception {
		DoerWrapper wrapper = new DoerWrapper(d);
		runOnDispatchThread( wrapper);
		if (wrapper.exception != null) {
			throw wrapper.exception;
		}
	}	
	/**
	 * execute on Swing, converting checked exceptions to {@link RuntimeException}
	 * @param supplier
	 * @return result produced
	 * @throws RuntimeException
	 */
	public static <T> T executeAsRuntimeException(Producer<T> supplier) { 
		try {
			return execute(supplier);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * execute on Swing, converting checked exceptions to {@link RuntimeException}
	  * @param d 
	 * @throws RuntimeException
	 */
	public static void executeAsRuntimeException(Doer d) {
		try {
			execute(d);
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * execute on Swing, consuming exception 
	 * @param supplier
	 * @return result produced or null if exception
	 */
	public static <T> T executeConsumeException(Producer<T> supplier) { 
		try {
			return execute(supplier);
		} catch (UserCancelException e) {
			//ignore
		} catch (UtilCancelException e) {
			//ignore
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * execute on Swing, consuming exception 
	 * @param doer
	 */
	public static void executeConsumeException(Doer doer) {
		try {
			execute(doer);
		} catch (UserCancelException e) {
			//ignore
		} catch (UtilCancelException e) {
			//ignore
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static abstract class SwingFunc implements Runnable {
		Exception exception = null;
		abstract void execute( ) throws Exception;
		@Override
		public void run() {
			try {
				execute();
			} catch (Exception e) {
				exception = e;
			}
		}
	}
	
	 private static class ProducerWrapper <T> extends SwingFunc {
		final Producer<T> producer;
		T result;

		private ProducerWrapper(Producer<T> producer) {
			super();
			this.producer = producer;
		}

		@Override
		void execute( )  throws Exception {
				result = producer.runSwing();
		}
	}
	 
	 private static class DoerWrapper  extends SwingFunc {
		 final Doer doer;
		 
		private DoerWrapper(Doer doer) {
			super();
			this.doer = doer;
		}

		@Override
		void execute( )  throws Exception {
			doer.runSwing();
		}
		 
	 }
	
}

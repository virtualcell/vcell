package org.vcell.util.executable;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * write to process standard in using pipe
 */
public class PipedProcessInput extends PipedProcessHandler implements IProcessInput {
	
	/**
	 * standard input of launched process
	 */
	protected AtomicReference<PipedProcessInput.Writer> stdIn;

	public PipedProcessInput( )  {
		stdIn = new AtomicReference<Writer>();
	}
	
	@Override
	public Redirect source() {
		return Redirect.PIPE; 
	}

	@Override
	public void set(OutputStream outputStream, String label) {
		name = label;
		stdIn.set( new Writer(outputStream, "LiveProcess " + label + " in") );
	}
	
	public void send(String in) {
		PipedProcessInput.Writer w = stdIn.get( );
		if (!w.isAlive()) {
			w.start();
		}
		w.send(in);
	}
	
	public boolean isException( ) {
		return stdIn.get( ).getException() != null;
	}
	
	public Exception pollException( ) {
		return stdIn.get( ).pollException();
	}
	
	public void close( ) throws IOException {
		stdIn.get( ).close( );
	}
	
	private static class Writer extends Thread {
		private OutputStream out;
		private Exception writeException;
		private AtomicBoolean closed;
		private String data;
		
		final Lock lock;
		final Condition haveInput; 
		final Condition readyToSend; 
		
		public Writer(OutputStream out, String threadName) {
			super(threadName);
			this.out = out;
			
			lock = new ReentrantLock();
			haveInput = lock.newCondition();
			readyToSend = lock.newCondition();
			
			writeException = null;
			closed = new AtomicBoolean(false);
			data = null;
			setDaemon(true);
		}
		
		public synchronized Exception pollException() {
			Exception e = writeException;
			writeException = null;
			return e;
		}

		@Override
		public void run() {
			for (;;) {
				try {
					lock.lock();
					try {
						if (data == null) {
							haveInput.await();
						}
						if (closed.get( )) {
							return;
						}
						byte[] sendBytes = data.getBytes();
						data = null;
						out.write(sendBytes);
						out.flush();
						readyToSend.signal();
					}
					finally {
						lock.unlock();
					}
	
				} catch (Exception e) {
					synchronized (this) {
						writeException = e;
					}
				}
			}
		}
		
		/**
		 * set down the thread nicely
		 * @throws IOException
		 */
		public void close( ) throws IOException {
			closed.set(true);
			try {
				lock.lock();
				haveInput.signal();
			}
			finally {
				lock.unlock();
			}
			out.close( );
		}
		
		/**
		 * set the data to be sent
		 * blocks if last send not completed yet
		 * @param output
		 */
		public void send(String output)  {
			try {
				lock.lock();
				while (data != null) {
					try {
						readyToSend.await();
					} catch (InterruptedException e) { }
				}
				data = output;
				haveInput.signal();
			}
			finally {
				lock.unlock();
			}
		}
	
		public synchronized Exception getException() {
			return writeException;
		}
	}


}

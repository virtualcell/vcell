package org.vcell.util.executable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * piped output from standard in or standard out. Data buffered and read by thread
 * @author gweatherby
 */
public class PipedProcessOutput extends PipedProcessHandler implements IProcessOut {
	
	/**
	 * standard out of launched process
	 */
	protected AtomicReference<PipedProcessOutput.Reader> reader; 
	
	public PipedProcessOutput() {
		reader = new AtomicReference<PipedProcessOutput.Reader>(null);
	}

	@Override
	public Redirect destination() {
		return Redirect.PIPE; 
	}

	@Override
	public void set(InputStream inputStream, String label) {
		name = label;
		reader.set( new Reader(inputStream,label) );
		reader.get( ).start();
	}

	@Override
	public void close() throws IOException {
		reader.get( ).close();
	}
	
	public String read(long time, TimeUnit timeUnit) {
		return reader.get( ).getString(time,timeUnit); 
	}
	public boolean isException( ) {
		return reader.get( ).getException() != null;
	}
	
	public Exception pollException( ) {
		return reader.get( ).pollException();
	}
	
	private static class Reader extends Thread {
		public static final int STARTING__BUFFER = 60000;
		private InputStream in;
		private ByteArrayOutputStream oStream;
		private Exception readException;
		private AtomicBoolean closed;
		private Lock lock;
		private Condition dataAvail; 
		public Reader(InputStream in, String threadName) {
			super(threadName);
			this.in = in;
			oStream = new ByteArrayOutputStream(STARTING__BUFFER);
			readException = null;
			closed = new AtomicBoolean(false);
			lock = new ReentrantLock(false);
			dataAvail = lock.newCondition();
			setDaemon(true);
		}
		
		@Override
		public void run() {
			try {
				byte buffer[] = new byte[STARTING__BUFFER];
				for (;;) {
					int read = in.read(buffer);
					if (read > 0) {
						try {
							lock.lock();
							oStream.write(buffer, 0, read);
							dataAvail.signal();
						}
						finally {
							lock.unlock();
						}
						continue;
					}
					return;
				}  
			} catch (Exception e) {
				if (closed.get() && e instanceof IOException && e.getMessage().toLowerCase().contains("stream closed")) {
					return;
				}
				synchronized (this) {
					readException = e;
				}
			}
		}
		
		public void close( ) throws IOException {
			closed.set(true);
			in.close( );
		}
		
		/**
		 * get string from InputStream, waiting if desired 
		 * @param time how long to wait
		 * @param timeUnit units of time parameter
		 * @return string from process or empty string if times out
		 */ 
		public String getString(long time, TimeUnit timeUnit) {
			String r = "";
			try {
				lock.lock();
				if (oStream.size() == 0) {
					dataAvail.await(time, timeUnit);
				}
				r = oStream.toString();
				oStream.reset();
			} catch (InterruptedException e) { }
			finally {
				lock.unlock();
			}
			return r;
		}
	
		public synchronized Exception getException() {
			return readException;
		}
		
		public synchronized Exception pollException() {
			Exception e = readException;
			readException = null;
			return e;
		}
	}

	
	
	
	

}

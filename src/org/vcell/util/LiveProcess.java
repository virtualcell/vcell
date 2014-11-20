package org.vcell.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cbit.vcell.resource.ResourceUtil;

/**
 * alternate Executable implementation which uses threaded readers
 * @author gweatherby
 *
 */
public class LiveProcess {
	public enum LiveProcessStatus{
		READY, 
		RUNNING, 
		EXITED, 
		STOPPED,
		EXCEPTION, 
	}
	
	private String commands[];
	private File workingDir;
	private int exitCode;
	private AtomicReference<LiveProcessStatus> status;
	/**
	 * standard out of launched process
	 */
	private AtomicReference<Reader> stdOut; 
	/**
	 * standard error of launched process
	 */
	private AtomicReference<Reader> stdErr; 
	/**
	 * standard input of launched process
	 */
	private AtomicReference<Writer> stdIn; 
	
	/**
	 * the process
	 */
	private Process process;
	
	private Map<String,String> environmentals;

	public LiveProcess(String ... commands) {
		super();
		this.commands = commands;
		workingDir = null;
		status = new AtomicReference<LiveProcessStatus>(LiveProcessStatus.READY);
		stdOut = new AtomicReference<LiveProcess.Reader>( );
		stdErr = new AtomicReference<LiveProcess.Reader>( );
		stdIn = new AtomicReference<LiveProcess.Writer>();
		process = null;
		environmentals = null;
	}

	/**
	 * begin process without blocking
	 * lable with first command
	 * @throws org.vcell.util.ExecutableException
	 */
	public final void begin() throws org.vcell.util.ExecutableException {
		begin(commands[0]);
	}
	/**
	 * begin process without blocking
	 * @param label only used to label threads
	 * @throws org.vcell.util.ExecutableException
	 */
	public final void begin(String label) throws org.vcell.util.ExecutableException {
		try {
			ProcessBuilder pb = new ProcessBuilder(commands);
			ResourceUtil.setEnvForOperatingSystem(pb.environment());
			if (environmentals != null) {
				pb.environment().putAll(environmentals);
			}
			pb.directory(workingDir);
			process = pb.start();
			stdOut.set( new Reader(process.getInputStream(), "LiveProcess " + label + " out") );
			stdErr.set( new Reader(process.getErrorStream(), "LiveProcess " + label + " err") );
			stdIn.set( new Writer(process.getOutputStream(), "LiveProcess " + label + " in") );
			
			stdOut.get( ).start();
			stdErr.get( ).start();
			//stdIn.get( ).start();
			//note: no need to start input thread until client sends data
			
			status.set(LiveProcessStatus.RUNNING);
		} catch (Exception e)  {
			convertException(e);
		}
	}
	
	/**
	 * add environmental setting
	 * must be called before {@link #begin()} 
	 * @param key
	 * @param value
	 */
	public void setEnvironment(String key, String value) {
		if (environmentals == null) {
			environmentals = new HashMap<String, String>( );
		}
		environmentals.put(key, value);
	}
	
	/**
	 * set status and convert e into {@link ExecutableException}
	 * @param e
	 * @throws ExecutableException
	 */
	private void convertException(Exception e) throws ExecutableException  {
		status.set(LiveProcessStatus.EXCEPTION);
		throw new ExecutableException("Exception executing " + Arrays.toString(commands),e);
	}
	
	/**
	 * get process standard out, waiting for process if desired. Returns immediately if there is output
	 * @param time how long to wait
	 * @param timeUnit units of time parameter
	 * @return string from process or empty String if times out
	 */
	public String getStdoutString(long time, TimeUnit timeUnit) {
		return stdOut.get( ).getString(time,timeUnit); 
	}

	/**
	 * get process standard err, waiting for process if desired. Returns immediately if there is output
	 * @param time how long to wait
	 * @param timeUnit units of time parameter
	 * @return string from process or empty String if times out
	 */
	public String getStderrString(long time, TimeUnit timeUnit) {
		return stdErr.get( ).getString(time,timeUnit); 
	}
	
	public void send(String in) {
		Writer w = stdIn.get( );
		if (!w.isAlive()) {
			w.start();
		}
		w.send(in);
	}
	
	private boolean isAlive( ) {
		try {
			exitCode = process.exitValue();
			return false;
		}
		catch (IllegalThreadStateException ise) {
			return true;
		}
	}

	public synchronized LiveProcessStatus getStatus() {
		LiveProcessStatus es = status.get( );
		if (es == LiveProcessStatus.RUNNING && !isAlive()) {
			es = LiveProcessStatus.EXITED;
			status.set(es);
		}
		return es;
	}

	public java.lang.Integer getExitValue() {
		return exitCode; 
	}

	public String getCommand() {
		return Arrays.toString(commands);
	}

	public File getWorkingDir() {
		return workingDir;
	}

	public void setWorkingDir(File workingDir) {
		this.workingDir = workingDir;
	}
	
	/**
	 * stop process, if running
	 * @param time 
	 * @param  units
	 * @throws ExecutableException if process does not heed command to exit
	 */
	public synchronized final void stop(long time, TimeUnit units) throws ExecutableException {
		if (getStatus( ) == LiveProcessStatus.RUNNING) {
			long waitTime = units.toNanos(time);
			long now = System.nanoTime(); 
			process.destroy();
			while (System.nanoTime() - now < waitTime && isAlive( )) {
				try {
					Thread.sleep(0, 10);
				} catch (InterruptedException e) { }
			}
			if (getStatus( ) == LiveProcessStatus.EXITED) {
				status.set(LiveProcessStatus.STOPPED);
			}
			if (isAlive( )) {
				throw new ExecutableException("process for " + getCommand() + " failed to stop");

			}
		}
	}

	public static class Reader extends Thread {
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
				readException = e;
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

		public Exception getReadException() {
			return readException;
		}
	}

	public static class Writer extends Thread {
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
			haveInput.signal();
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

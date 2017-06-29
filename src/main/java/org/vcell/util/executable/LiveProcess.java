package org.vcell.util.executable;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.vcell.util.exe.ExecutableException;

import cbit.vcell.resource.ResourceUtil;

/**
 * alternate Executable implementation which uses threaded readers
 * @author gweatherby
 *
 */
public class LiveProcess<I extends IProcessInput, O extends IProcessOut, E extends IProcessOut> {
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
	final I inputHandler;
	final O outputHandler;
	final E errorHandler;
	
	/**
	 * the process
	 */
	protected Process process;
	
	private Map<String,String> environmentals;
	
	private static Logger lg = Logger.getLogger(LiveProcess.class);

	public LiveProcess(I inputHandler, O outputHandler, E errorHandler, String ... commands) {
		super();
		this.inputHandler = inputHandler;
		this.outputHandler = outputHandler;
		this.errorHandler = errorHandler;
		this.commands = commands;
		
		workingDir = null;
		status = new AtomicReference<LiveProcessStatus>(LiveProcessStatus.READY);
		process = null;
		environmentals = null;
	}

	public I getInputHandler() {
		return inputHandler;
	}

	public O getOutputHandler() {
		return outputHandler;
	}

	public E getErrorHandler() {
		return errorHandler;
	}

	/**
	 * begin process without blocking
	 * lable with first command
	 * @throws org.vcell.util.exe.ExecutableException
	 */
	public final void begin() throws org.vcell.util.exe.ExecutableException {
		begin(commands[0]);
	}
	/**
	 * begin process without blocking
	 * @param label only used to label threads
	 * @throws org.vcell.util.exe.ExecutableException
	 */
	public final void begin(String label) throws org.vcell.util.exe.ExecutableException {
		try {
			ProcessBuilder pb = new ProcessBuilder(commands);
			ResourceUtil.setEnvForOperatingSystem(pb.environment());
			if (environmentals != null) {
				pb.environment().putAll(environmentals);
			}
			pb.directory(workingDir);
			pb.redirectInput(inputHandler.source());
			pb.redirectOutput(outputHandler.destination());
			pb.redirectError(errorHandler.destination());
			process = pb.start();
			if (pb.redirectInput() == ProcessBuilder.Redirect.PIPE) {
				inputHandler.set(process.getOutputStream(), label);
			}
			if (pb.redirectOutput() == ProcessBuilder.Redirect.PIPE) {
				outputHandler.set(process.getInputStream(), label);
			}
			if (pb.redirectError() == ProcessBuilder.Redirect.PIPE) {
				errorHandler.set(process.getInputStream(), label);
			}
			
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
	 * close handler, logging any exceptions
	 * @param ips
	 */
	private void safeClose(Closeable ips) {
		try {
			ips.close();
		} catch (IOException e) {
			lg.warn("Close failed " + ips.toString(),e);
		}
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
			safeClose(inputHandler);
			safeClose(outputHandler);
			safeClose(errorHandler);
		}
	}
}

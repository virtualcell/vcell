/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.exe;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.TokenMangler;

import cbit.vcell.resource.ResourceUtil;

/**
 * Insert the type's description here.
 * Creation date: (10/22/2002 4:33:29 PM)
 * @author: Ion Moraru
 */
public class Executable implements IExecutable {
	private static final Logger lg = LogManager.getLogger(Executable.class);
	
	private String[] command = null;
	private Process process = null;
	protected String outputString = "";
	private String errorString = "";
	private Integer exitValue = null;
	private ExecutableStatus status = null;
	private long timeoutMS = 0;
	private File workingDir = null;
	private Thread runThread = null;
	private CountDownLatch stoppingLatch = null;
	private AtomicBoolean active = new AtomicBoolean(false);
	private HashMap<String, String> addedEnvironmentVariables;
	
/**
 * sometimes command and input file name have space, we need to escape space;
 * So the easiest way to run the command is to call Runtime.exec(String[] cmd)
 * where cmd[0] is the exectuable and the rest are the arguments. 
 * If we use this method, we dont have to escape the space at all. 
 * 
 * if we submit the job to PBS, then we need a real unix command, then we must escape the space. 
 * This is done in getCommand(). 
 * 
 * These are tests I have done.
 * 
	// Mac doesn't work
	// Linux doesn't work
	// Windows doesn't work	
	String command = exe + " " + path;

	// Mac doesn't work
	// Linux doesn't work
	// Windows works, weird, 	
	String command = exe + " " + escapedPath;
	
	// Mac doesn't work
	// Linux doesn't work
	// Windows works
	String command = escapedExe + " " + escapedPath;

	//Mac works
	// Linux works
	// Windows works
	String[] command = new String[] {exe, path};

	//Mac  doesn't work
	// Linux doesnt work
	// Windows works
	String[] command = new String[] {escapedExe, escapedPath};

	// Mac works	
	// Linux doesnt work
	// Windows works
	String[] command = new String[] {exe, escapedPath};
 * 
 */

public Executable(String[] command) {
	this(command, 0);
}

/**
 * Executable constructor comment.
 */
public Executable(String[] command, long arg_timeoutMS) {
	setCommand(command);
	setStatus(ExecutableStatus.READY);
	timeoutMS = arg_timeoutMS;
}


/**
 * This method was created by a SmartGuide.
 */
protected void executeProcess(int[] expectedReturnCodes) throws org.vcell.util.exe.ExecutableException {
	
	if (lg.isTraceEnabled()) lg.trace("Executable.executeProcess(" + getCommand() + ") starting...");
	try {
		try {
			active.set(true);
			runThread = Thread.currentThread(); //record for interruption via #stop
			// reset just in case
			setOutputString("");
			setErrorString("");
			setExitValue(null);
			ProcessBuilder pb = new ProcessBuilder(Arrays.asList(command));
			if(addedEnvironmentVariables != null){
				pb.environment().putAll(addedEnvironmentVariables);
			}
			ResourceUtil.setEnvForOperatingSystem(pb.environment());
			pb.directory(workingDir);
			// start the process
			Process p = pb.start();
			setProcess(p);

			// monitor the process; blocking call
			// will update the fields from StdOut and StdErr
			// will return the exit code once the process terminates
			int exitCode = monitorProcess(getProcess().getInputStream(), getProcess().getErrorStream(), 10);
			Thread.interrupted(); //clear interrupted status
			setExitValue(new Integer(exitCode));
		} finally {
			active.set(false);
		}
		// log what happened and update status
		if (getStatus().equals(org.vcell.util.exe.ExecutableStatus.STOPPED)) {
			if (lg.isWarnEnabled()) lg.warn("\nExecutable.executeProcess(" + getCommand() + ") STOPPED\n");
		} else {
			if (lg.isTraceEnabled()) lg.trace("\nExecutable.executeProcess(" + getCommand() + ") executable returned with returncode = " + getExitValue() + "\n");
			boolean bExpectedReturnCode = false;
			for (int expectedReturnCode : expectedReturnCodes){
				if (expectedReturnCode == getExitValue()){
					bExpectedReturnCode = true;
				}
			}
			if (bExpectedReturnCode){
				setStatus(ExecutableStatus.COMPLETE);
			}else{
				setStatus(ExecutableStatus.getError("executable failed, return code = " + getExitValue() + "\nstderr = '" + getErrorString() + "'"));				
			}
		}
		// log output
		if (lg.isTraceEnabled()) lg.trace("Executable.executeProcess(" + getCommand() + ") stdout:\n" + getOutputString());
		// finally, throw if it was a failure
		if (getStatus().isError()) {
			throw new Exception(getErrorString());
		}
	} catch (Throwable e) {
		lg.error(e.getMessage(),e);
		if (getStatus().isError()) {
			// process failed and we relay the exception thrown on error status finish above
			throw new ExecutableException(e.getMessage() + "\n\n(" + getCommand() + ")");
		} else {
			//something really unexpected happened, update status and log it before relaying...
			setStatus(ExecutableStatus.getError("error running executable " + e.getMessage()));
			throw new ExecutableException("Unexpected error: " + e.getMessage() + "\n\n(" + getCommand() + ")");
		}			
	} finally {
		close();
	}
}


/**
 * This method was created in VisualAge.
 */
public void finalize() {
	stop();
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 4:34:39 PM)
 * @return java.lang.String
 */
public String getCommand() {
	StringBuffer commandLine = new StringBuffer();
	for (int i = 0; i < command.length; i ++) {
		if (i > 0) {
			commandLine.append(" ");
		}		
		commandLine.append(TokenMangler.getEscapedPathName(command[i]));		
	}
	return commandLine.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 5:35:11 PM)
 * @return java.lang.String
 */
private java.lang.String getErrorString() {
	return errorString;
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 5:57:56 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getExitValue() {
	return exitValue;
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 5:35:11 PM)
 * @return java.lang.String
 */
private java.lang.String getOutputString() {
	return outputString;
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 4:46:10 PM)
 * @return java.lang.Process
 */
private java.lang.Process getProcess() {
	return process;
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2002 12:23:37 PM)
 * @return cbit.vcell.solvers.ExecutableStatus
 */
public synchronized org.vcell.util.exe.ExecutableStatus getStatus() {
	return status;
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2002 3:12:48 PM)
 * @return java.lang.String
 */
public String getStderrString() {
	return getErrorString();
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2002 3:12:48 PM)
 * @return java.lang.String
 */
public String getStdoutString() {
	return getOutputString();
}


public static void main(java.lang.String[] args) {
	try {
		Executable executable = new Executable(args);
		executable.start();
	}catch (ExecutableException e) {
		System.out.println("\nExecutable Exception thrown, normally handled upstream by other classes...");
	}
}


/**
 * This method was created in VisualAge.
 */
protected final int monitorProcess(InputStream inputStreamOut, InputStream inputStreamErr, long pollingIntervalMS) throws ExecutableException {
	long t = System.currentTimeMillis();
	
	char charArrayOut[] = new char[1000000];
	char charArrayErr[] = new char[1000000];
	String outString = new String();
	String errString = new String();
	int numReadOut = 0; int numReadErr = 0; int exitValue = 0;
	InputStreamReader inputStreamReaderOut = new InputStreamReader(inputStreamOut);
	InputStreamReader inputStreamReaderErr = new InputStreamReader(inputStreamErr);

	boolean running = true;
	while (running || (numReadOut > 0) || (numReadErr > 0)) {
		if (timeoutMS > 0 && System.currentTimeMillis() - t > timeoutMS) {
			throw new ExecutableException("Process timed out");
		}
		try {
			exitValue = getProcess().exitValue();
			running = false;
		} catch (IllegalThreadStateException e) {
			// process didn't exit yet, do nothing
		}
		try {
			if (pollingIntervalMS > 0) Thread.sleep(pollingIntervalMS);
		} catch (InterruptedException e) {
			if (getStatus( ) == ExecutableStatus.STOPPED) {
				close( );
				getLatch( ).countDown();
				Thread.interrupted( );
				return -1;
			}
		}
		try {
			if (inputStreamOut.available() > 0) {
				numReadOut = inputStreamReaderOut.read(charArrayOut, 0, charArrayOut.length);
			} else {
				numReadOut = 0;
			}
		} catch (IOException ioexc) {
			lg.error("EXCEPTION (process " + getCommand() + ") - IOException while reading StdOut: " + ioexc.getMessage(), ioexc);
			numReadOut = 0;
		}
		try {
			if (inputStreamErr.available() > 0) {
				numReadErr = inputStreamReaderErr.read(charArrayErr, 0, charArrayErr.length);
			} else {
				numReadErr = 0;
			}
		} catch (IOException ioexc) {
			lg.error("EXCEPTION (process " + getCommand() + ") - IOException while reading StdErr: " + ioexc.getMessage(), ioexc);
			numReadErr = 0;
		}
		if (numReadOut > 0) {
			String newInput = new String(charArrayOut, 0, numReadOut);
			setNewOutputString(newInput);
			outString += newInput;
//			System.err.println(newInput);
		}
		if (numReadErr > 0) {
			String newInput = new String(charArrayErr, 0, numReadErr);
			errString += newInput;
		}
		setOutputString(outString);
		setErrorString(errString);
	}
	try {
		inputStreamReaderOut.close();
		inputStreamReaderErr.close();
	} catch (IOException ioexc) {
		lg.error("EXCEPTION (process " + getCommand() + ") - IOException while closing streams: " + ioexc.getMessage(), ioexc);
		numReadOut = 0;
	}
	return exitValue;
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 4:34:39 PM)
 * @param newCommand java.lang.String
 */
private void setCommand(String[] newCommand) {
	command = newCommand;
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 5:35:11 PM)
 * @param newErrorString java.lang.String
 */
private void setErrorString(java.lang.String newErrorString) {
	errorString = newErrorString;
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 5:57:56 PM)
 * @param newExitValue java.lang.Integer
 */
private void setExitValue(java.lang.Integer newExitValue) {
	exitValue = newExitValue;
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 5:35:11 PM)
 * @param newOutputString java.lang.String
 */
protected void setOutputString(java.lang.String newOutputString) {
	outputString = newOutputString;
}
protected void setNewOutputString(java.lang.String newOutputString) {
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 4:46:10 PM)
 * @param newProcess java.lang.Process
 */
private void setProcess(java.lang.Process newProcess) {
	process = newProcess;
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2002 12:23:37 PM)
 * @param newStatus cbit.vcell.solvers.ExecutableStatus
 */
private synchronized void setStatus(org.vcell.util.exe.ExecutableStatus newStatus) {
	status = newStatus;
}

public final void start() throws org.vcell.util.exe.ExecutableException {
	start(new int[] { 0 });
}

/**
 * This method was created in VisualAge.
 */
public final void start(int[] expectedReturnCodes) throws org.vcell.util.exe.ExecutableException {

    setStatus(ExecutableStatus.RUNNING);
    try {
        executeProcess(expectedReturnCodes);
    } catch (ExecutableException e) {
        lg.error(e.getMessage(),e);
        setStatus(ExecutableStatus.getError("Executable Exception: " + e.getMessage()));
        throw e;
    }
}

/**
 * This method was created in VisualAge.
 */
public final void stop() {
	if (active.get( )) {
		try {
			CountDownLatch cdl = getLatch(); //get local copy to avoid race issues
			setStatus(ExecutableStatus.STOPPED);
			runThread.interrupt();
			cdl.await(1, TimeUnit.SECONDS);
		} catch (InterruptedException e) { 
			Thread.interrupted();
		}
		if (getProcess() != null) {
			lg.error("Executable.stop( ) failed to clean up properly");
		}
	}
}

/**
 * lazily create /return 1 step CountDownLatch
 * @return new or existing latch
 */
private synchronized CountDownLatch getLatch( ) {
	if (stoppingLatch == null) {
		stoppingLatch = new CountDownLatch(1);
	}
	return stoppingLatch;
}

private final void close(){
	if (getProcess() != null) {
		try {
			getProcess().getInputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			getProcess().getOutputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			getProcess().getErrorStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getProcess().destroy();
		setProcess(null);
	}
}

public File getWorkingDir() {
	return workingDir;
}

public void setWorkingDir(File workingDir) {
	this.workingDir = workingDir;
}
@Override
public void addEnvironmentVariable(String varName,String varValue){
	if(addedEnvironmentVariables == null){
		addedEnvironmentVariables = new HashMap<>();
	}
	addedEnvironmentVariables.put(varName,varValue);
}
}

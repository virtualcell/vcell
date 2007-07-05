package cbit.util;
import java.io.*;
/**
 * Insert the type's description here.
 * Creation date: (10/22/2002 4:33:29 PM)
 * @author: Ion Moraru
 */
public class Executable {
	private String command = null;
	private Process process = null;
	private String outputString = "";
	private String errorString = "";
	private Integer exitValue = null;
	private ExecutableStatus status = null;
	private long timeoutMS = 0;

/**
 * Executable constructor comment.
 */
public Executable(String command) {
	this(command, 0);
}


/**
 * Executable constructor comment.
 */
public Executable(String command, long arg_timeoutMS) {
	setCommand(command);
	setStatus(ExecutableStatus.READY);
	timeoutMS = arg_timeoutMS;
}


/**
 * This method was created by a SmartGuide.
 */
protected void executeProcess() throws cbit.util.ExecutableException {
	
	System.out.println("Executable.executeProcess(" + getCommand() + ") starting...");
	try {
		// reset just in case
		setOutputString("");
		setErrorString("");
		setExitValue(null);
		// start the process
		setProcess(Runtime.getRuntime().exec(getCommand()));
		// monitor the process; blocking call
		// will update the fields from StdOut and StdErr
		// will return the exit code once the process terminates
		int exitCode = monitorProcess(getProcess().getInputStream(), getProcess().getErrorStream(), 1000);
		setExitValue(new Integer(exitCode));
		// log what happened and update status
		if (getStatus().equals(cbit.util.ExecutableStatus.STOPPED)) {
			System.out.println("\nExecutable.executeProcess(" + getCommand() + ") STOPPED\n");
		} else if (getExitValue().intValue() == 0) {
			System.out.println("\nExecutable.executeProcess(" + getCommand() + ") executable successful\n");
			setStatus(ExecutableStatus.COMPLETE);
		} else {
			System.out.println("\nExecutable.executeProcess(" + getCommand() + ") executable failed, return code = " + getExitValue() + "\n");
			setStatus(ExecutableStatus.getError("executable failed, return code = " + getExitValue() + "\nstderr = '" + getErrorString() + "'"));
		}
		// log output
		System.out.println("Executable.executeProcess(" + getCommand() + ") stdout:\n" + getOutputString());
		// finally, throw if it was a failure
		if (getStatus().isError()) {
			throw new Exception("simulation failed, return code = " + getExitValue() + "\nstderr = '" + getErrorString()+"'");
		}
	} catch (Throwable e) {
		if (getStatus().isError()) {
			// process failed and we relay the exception thrown on error status finish above
			throw new ExecutableException("Executable.executeProcess(" + getCommand() + ") error running executable: " + e.getMessage());
		} else {
			//something really unexpected happened, update status and log it before relaying...
			setStatus(ExecutableStatus.getError("error running executable " + e.getMessage()));
			e.printStackTrace(System.out);
			throw new ExecutableException("Executable.executeProcess(" + getCommand() + ") unexpected error: " + e.getMessage());
		}			
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
public java.lang.String getCommand() {
	return command;
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
public cbit.util.ExecutableStatus getStatus() {
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
		Executable executable = new Executable(args[0]);
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
	
	char charArrayOut[] = new char[10000];
	char charArrayErr[] = new char[10000];
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
		}
		try {
			if (inputStreamOut.available() > 0) {
				numReadOut = inputStreamReaderOut.read(charArrayOut, 0, charArrayOut.length);
			} else {
				numReadOut = 0;
			}
		} catch (IOException ioexc) {
			System.out.println("EXCEPTION (process " + getCommand() + ") - IOException while reading StdOut: " + ioexc.getMessage());
			numReadOut = 0;
		}
		try {
			if (inputStreamErr.available() > 0) {
				numReadErr = inputStreamReaderErr.read(charArrayErr, 0, charArrayErr.length);
			} else {
				numReadErr = 0;
			}
		} catch (IOException ioexc) {
			System.out.println("EXCEPTION (process " + getCommand() + ") - IOException while reading StdErr: " + ioexc.getMessage());
			numReadErr = 0;
		}
		if (numReadOut > 0) {
			String newInput = new String(charArrayOut, 0, numReadOut);
			outString += newInput;
			if (numReadOut == charArrayOut.length) {
				outString += "\n(standard output truncated...)";
			}
		}
		if (numReadErr > 0) {
			String newInput = new String(charArrayErr, 0, numReadErr);
			errString += newInput;
			if (numReadErr == charArrayErr.length) {
				errString += "\n(standard output truncated...)";
			}
		}
		setOutputString(outString);
		setErrorString(errString);
	}
	try {
		inputStreamReaderOut.close();
		inputStreamReaderErr.close();
	} catch (IOException ioexc) {
		System.out.println("EXCEPTION (process " + getCommand() + ") - IOException while closing streams: " + ioexc.getMessage());
		numReadOut = 0;
	}
	return exitValue;
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2002 4:34:39 PM)
 * @param newCommand java.lang.String
 */
private void setCommand(java.lang.String newCommand) {
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
private void setStatus(cbit.util.ExecutableStatus newStatus) {
	status = newStatus;
}


/**
 * This method was created in VisualAge.
 */
public final void start() throws cbit.util.ExecutableException {

    setStatus(ExecutableStatus.RUNNING);
    try {
        executeProcess();
    } catch (ExecutableException e) {
        e.printStackTrace(System.out);
        setStatus(ExecutableStatus.getError("Executable Exception: " + e.getMessage()));
        throw e;
    }
}


/**
 * This method was created in VisualAge.
 */
public final void stop() {
	setStatus(ExecutableStatus.STOPPED);
	if (getProcess() != null) {
		getProcess().destroy();
	}
}
}
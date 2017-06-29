package org.vcell.util;

import java.io.File;

/**
 * provide interface to allow switching / testing different Executable implementations
 * @author gweatherby
 *
 */
public interface IExecutable {

	/**
	 * see {@link #start()}
	 */
	static final int[] DEFAULT_RETURN = { 0 };

	/**
	 * start process, wait for exit before returning
	 * @param expectedReturnCodes
	 * @throws org.vcell.util.ExecutableException if fails, or return code not in expectedReturnCodes
	 */
	public void start(int[] expectedReturnCodes)
			throws org.vcell.util.ExecutableException;

	/**
	 * start process, wait for exit before returning
	 * calls {@link #start(int[])} with {@link #DEFAULT_RETURN} codes
	 * @throws org.vcell.util.ExecutableException
	 */
	public void start() throws org.vcell.util.ExecutableException;

	public void stop();

	public String getStdoutString();

	public String getStderrString();

	public void setWorkingDir(File workingDir);

	public File getWorkingDir();

	public org.vcell.util.ExecutableStatus getStatus();

	public java.lang.Integer getExitValue();

	public String getCommand();

	/**
	 * add additional environmental variables to new process environment
	 * while retaining existing ones
	 * @param varName non null
	 * @param varValue non null
	 */
	void addEnvironmentVariable(String varName, String varValue);

}
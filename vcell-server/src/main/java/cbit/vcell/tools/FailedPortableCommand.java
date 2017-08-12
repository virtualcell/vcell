package cbit.vcell.tools;

import cbit.vcell.simdata.PortableCommand;

/**
 * PortableCommand which fails, for testing
 * @author gweatherby
 *
 */
public class FailedPortableCommand implements PortableCommand {

	/**
	 * @return 1
	 */
	@Override
	public int execute() {
		return 1;
	}

	/**
	 * @return RuntimeException 
	 */
	@Override
	public Exception exception() {
		return new RuntimeException("Failure for testing");
	}
}

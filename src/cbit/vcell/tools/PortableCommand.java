package cbit.vcell.tools;

import com.google.gson.Gson;

/**
 * abstract a command designed to be packaged using Json. See {@link Gson}. Note
 * complex abstractions (e.g. inner classes) may not package correctly.
 * Implementing classes should mark fields not designed to be packaged as transient.
 * @author gweatherby
 */
public interface PortableCommand {

	/**
	 * execute command and return status. If non-zero returned,
	 * {@link #exception()} should return non-null
	 * @return 0 for success
	 */
	int execute( );

	/**
	 * Exception explaining failure
	 * @return non-null if {@link #execute()} returns non-zero
	 */
	Exception exception( );


}

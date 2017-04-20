package org.vcell.util.executable;

import java.io.Closeable;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;

/**
 * define how to handle standard out or standard err
 */
public interface IProcessOut extends Closeable {
	/**
	 *  disposition of output
	 * @return non-null value
	 */
	public ProcessBuilder.Redirect destination( ) ;
	/**
	 * called if {@link #destination()} returns {@link Redirect#PIPE}
	 * @param inputStream
	 * @param label for object / thread identificatino
	 */
	public void set(InputStream inputStream, String label);
}

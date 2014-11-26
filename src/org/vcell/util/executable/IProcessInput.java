package org.vcell.util.executable;

import java.io.Closeable;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;

/**
 * define how to handle standard in
 */
public interface IProcessInput extends Closeable {
	/**
	 * disposition of standard in
	 * @return non-null value
	 */
	public ProcessBuilder.Redirect source( );
	/**
	 * called if {@link #source()} returns {@link Redirect#PIPE}
	 * @param outputStreamn
	 * @param label for object / thread identification
	 */
	public void set(OutputStream outputStream, String label);
}

package org.vcell.smoldyn.inputfile;

/**
 * Thrown to indicate an error during writing of a configuration file.
 * 
 * @author mfenwick
 *
 */
public class SmoldynWritingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -985013944900830403L;
	
	
	public SmoldynWritingException(String message) {
		super(message);
	}
	
	
	public SmoldynWritingException(String message, Throwable cause) {
		super(message, cause);
	}

}

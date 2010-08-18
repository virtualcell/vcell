package org.vcell.smoldyn.simulation;

/**
 * This exception is thrown to indicate an unacceptable condition or value within the Smoldyn conversion project.
 * 
 * @author mfenwick
 *
 */
public class SmoldynException extends Exception {

	private static final long serialVersionUID = -2421562698418431043L;

	
	public SmoldynException(String message) {
		super(message);
	}
	
	public SmoldynException(String message, Throwable cause) {
		super(message, cause);
	}
	
}

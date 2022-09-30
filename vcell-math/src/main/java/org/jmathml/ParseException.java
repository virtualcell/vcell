package org.jmathml;

/**
 * Thrown if tokeniser cannot parse the input string.
 * 
 * @author radams
 *
 */
public class ParseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParseException(String message) {
		super(message);
	}

}

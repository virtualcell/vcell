package org.vcell.smoldyn.inputfile.smoldynwriters;



/**
 * A class holding the common functionality that all writers may need,
 * for instance, writing warnings when attempting to use unimplemented 
 * features.
 * 
 * @author mfenwick
 *
 */
public class Utilities {

	/**
	 * Prints a warning to stderr, saying that a feature has not yet been implemented.
	 * Optionally throws an exception if the caller requests it.
	 * 
	 * @param feature
	 * @param throwexception
	 * @throws RuntimeException if requested
	 */
	public static void writeUnimplementedWarning(String feature, boolean throwexception) {
		System.err.println("warning: writing of " + feature + " has not yet been implemented");
		if(throwexception) {
			throw new RuntimeException("attempting to write " + feature + " caused a RuntimeException");
		}
	}
	
	/**
	 * Prints a warning to stderr, saying that implementation of a feature has been poorly
	 * tested, and that no guarantee is given to correctness.  Optionally throws an exception
	 * if the callers requests it.
	 * 
	 * @param warning
	 * @param throwexception
	 * @throws RuntimeException if requested
	 */
	public static void writeUncertainUsageWarning(String warning, boolean throwexception) {
		System.err.println("warning: usage of " + warning + " has not yet been fully tested");
		if(throwexception) {
			throw new RuntimeException("attempting to write " + warning + " caused a RuntimeException");
		}
	}
	
	
	/**
	 * 
	 * @param message
	 * @throws RuntimeException
	 */
	public static void throwUnexpectedException(String message) {
		throw new RuntimeException("unexpected conditions: " + message);
	}
	
	
	/**
	 * Prints a message to stderr.
	 * 
	 * @param message String
	 */
	public static void writeSmoldynWarning(String message) {
		System.err.println("writing warning:  " + message);
	}
}

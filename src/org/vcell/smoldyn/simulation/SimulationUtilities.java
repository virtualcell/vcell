package org.vcell.smoldyn.simulation;




/**
 * Centralizes some functionality of Model and its data members, such as exceptions,
 * debugging statements, and error messages.
 * 
 * @author mfenwick
 *
 */
public class SimulationUtilities {
	
	
	/**
	 * Throws a {@link SmoldynException} to indicate that a HashMap or HashSet does not
	 * have a corresponding key.
	 * 
	 * @param message
	 * @throws SmoldynException 
	 */
	public static void throwNoAssociatedValueException(String message) throws SmoldynException {
		throw new SmoldynException("no entry for " + message);
	}
	
	/**
	 * Throws a {@link SmoldynException} to indicate that a HashMap already
	 * has a key.
	 * 
	 * @param message
	 * @throws SmoldynException
	 */
	public static void throwAlreadyHasValueException(String message) throws SmoldynException {
		throw new SmoldynException("hash map already has value (" + message + ")");
	}
	
	
	/**
	 * Throws a {@link SmoldynException} to indicate that a HashMap already
	 * has a value.
	 * 
	 * @param message
	 * @throws SmoldynException
	 */
	public static void throwAlreadyHasKeyException(String message) throws SmoldynException {
		throw new SmoldynException("hashmap or hashtable already has key (" + message + ")");
	}


	/**
	 * Sends a warning to stderr.
	 * 
	 * @param string
	 */
	public static void printWarning(String string) {
		System.err.println("model warning: " + string);
	}
	
	
	/**
	 * Throws a {@link SmoldynException} to indicate an unimplemented feature.
	 * 
	 * @param message
	 * @throws SmoldynException
	 */
	public static void throwUnimplementedException(String message) throws SmoldynException {
		throw new SmoldynException(message + " is not yet implemented");
	}
	
	
	/**
	 * Throws a {@link SmoldynException}.
	 * 
	 * @param message
	 * @throws SmoldynException 
	 */
	public static void throwSmoldynException(String message) throws SmoldynException {
		throw new SmoldynException("SmoldynException requested with the following message: " + message);
	}
	
	
	/**
	 * Print a debugging statement to stdout.
	 * 
	 * @param message String
	 */
	public static void printDebuggingStatement(String message) {
		System.out.println("model debugging output:  " + message);
	}
	
	
	/**
	 * Throw an {@link IllegalArgumentException} with a specified message.
	 * 
	 * @param message 
	 * @throws IllegalArgumentException 
	 */
	public static void throwIllegalArgumentException(String message) {
		throw new IllegalArgumentException("illegal argument:  " + message);
	}
	
	
	/**
	 * @param message
	 * @param doubles
	 * @throws IllegalArgumentException if any of doubles is null
	 */
	public static void checkForNonNegative(String message, double ... doubles) {
		for(double d : doubles) {
			if(d < 0) {
				throw new IllegalArgumentException("value must be non-null: " + message);
			}
		}
	}
	
	/**
	 * @param message
	 * @param objects
	 * @throws NullPointerException if any of objects is null
	 */
	public static void checkForNull(String message, Object ... objects) {
		for(Object o : objects) {
			if(o == null) {
				throw new NullPointerException(message);
			}
		}
	}
	
	/**
	 * @param message
	 * @param doubles
	 * @throws IllegalArgumentException if any of doubles is not positive
	 */
	public static void checkForPositive(String message, double ... doubles) {
		for(double d : doubles) {
			if( d <= 0) {
				throw new IllegalArgumentException("value must be positive: " + message);
			}
		}
	}
	
	
	/**
	 * @param message
	 * @param b
	 * @throws RuntimeException if b is false
	 */
	public static void assertIsTrue(String message, boolean b) {
		if(!b) {
			throw new RuntimeException(message);
		}
	}
}

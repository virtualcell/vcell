package org.vcell.smoldyn.simulation;

import java.util.NoSuchElementException;





/**
 * Centralizes some functionality of Model and its data members, such as exceptions,
 * debugging statements, and error messages.
 * 
 * @author mfenwick
 *
 */
public class SimulationUtilities {

	/**
	 * Throws a NullPointerException pertaining to the argument.  Use if passed a null
	 * argument, when a non-null argument is needed.
	 * 
	 * @param component String
	 * @throws NullPointerException
	 */
	public static void throwNullPointerException(String component) {
		throw new NullPointerException(component + " was null (it must not be)");
	}
	
	
	/**
	 * Throws a {@link NoSuchElementException} to indicate that a HashMap or HashSet does not
	 * have a corresponding key.
	 * 
	 * @param message
	 * @throws NoSuchElementException
	 */
	public static void throwNoAssociatedValueException(String message) {
		throw new NoSuchElementException("no entry for " + message);
	}
	
	/**
	 * Throws a {@link RuntimeException} to indicate that a HashMap already
	 * has a key.
	 * 
	 * @param message
	 * @throws RuntimeException
	 */
	public static void throwAlreadyHasValueException(String message) {
		throw new RuntimeException("hash map already has value (" + message + ")");
	}
	
	
	/**
	 * Throws a {@link RuntimeException} to indicate that a HashMap already
	 * has a value.
	 * 
	 * @param message
	 * @throws RuntimeException
	 */
	public static void throwAlreadyHasKeyException(String message) {
		throw new RuntimeException("hashmap or hashtable already has key (" + message + ")");
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
	 * Throws a {@link RuntimeException} to indicate an unimplemented feature.
	 * 
	 * @param message
	 * @throws RuntimeException
	 */
	public static void throwUnimplementedException(String message) {
		throw new RuntimeException(message + " is not yet implemented");
	}
	
	
	/**
	 * Throws a {@link RuntimeException}.
	 * 
	 * @param message
	 */
	public static void throwRuntimeException(String message) {
		throw new RuntimeException("RuntimeException requested with the following message: " + message);
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
	 * @throws IllegalArgumentException with the specified message
	 */
	public static void throwIllegalArgumentException(String message) {
		throw new IllegalArgumentException("illegal argument:  " + message);
	}
	
	
	/**
	 * Throw a {@link RuntimeException} to indicate that something tried to violate
	 * a class-invariant. 
	 * 
	 * @param message String
	 * @throws RuntimeException with the specified message
	 */
	public static void throwInvariantConditionViolatedException(String message) {
		throw new RuntimeException("invariant condition violated:  " + message);
	}

	
	/**
	 * @param message
	 * @param d
	 */
	public static void checkForNonNegative(String message, double ... doubles) {
		for(double d : doubles) {
			if(d < 0) {
				throw new RuntimeException("value must be non-null: " + message);
			}
		}
	}
	
	/**
	 * @param message
	 * @param objects
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
	 * @param d
	 */
	public static void checkForPositive(String message, double d) {
		if( d <= 0) {
			throw new RuntimeException("value must be positive: " + message);
		}
	}
}

package org.vcell.util;

/**
 * runtime assertion facility that throws exception instead of errors
 * @author gweatherby
 *
 */
public class VCAssert {
	
	/**
	 * @param condition
	 * @param message to display if condition false
	 * @throws ProgrammingException if condition false
	 */
	public static void assertTrue(boolean condition, String message) {
		if (condition) {
			return;
		}
		throw new ProgrammingException(message);
	}
	
	/**
	 * @param condition
	 * @param message to display if condition true
	 * @throws ProgrammingException if condition true
	 */
	public static void assertFalse(boolean condition, String message) {
		if (!condition) {
			return;
		}
		throw new ProgrammingException(message);
	}

	/**
	 * prevent objects
	 */
	private VCAssert() {
		//make this class an interface in Java 8
	}

}

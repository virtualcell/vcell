package org.vcell.util;


/**
 * runtime assertion facility that throws exception instead of errors
 * @author gweatherby
 */
public class VCAssert {
	/**
	 * prefix for {@link ProgrammingException} messages
	 */
	private static final String MSG_PREFIX = "Programming error:  ";
	
	/**
	 * @param condition
	 * @param message to display if condition false
	 * @throws ProgrammingException if condition false
	 */
	public static void assertTrue(boolean condition, String message) {
		if (condition) {
			return;
		}
		throw new ProgrammingException(MSG_PREFIX + message);
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
		throw new ProgrammingException(MSG_PREFIX + message);
	}
	/**
	 * object not null
	 * @param obj
	 * @throws NullPointerException if it is
	 */
	public static void assertValid(Object obj) {
		if (obj == null) {
			throw new NullPointerException();
		}
	}
	
	/**
	 * validate object is of specified type
	 * @param fileFilter; not null
	 * @param clzz required type; non null
	 */
	public static <T> void ofType(Object obj, Class<T> clzz) {
		if (!clzz.isAssignableFrom(obj.getClass())) {
			throw new ProgrammingException(MSG_PREFIX + "Object of type " + obj.getClass().getName() 
					+ " not instance of type " + clzz.getName() );
		}
	}

	/**
	 * prevent objects
	 */
	private VCAssert() {
		//make this class an interface in Java 8
	}


}

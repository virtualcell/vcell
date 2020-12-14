package org.jlibsedml;

/*
 * Utility class for argument checking, package scoped.
 */
class Assert {
	// prevent subclassing
	private Assert (){}
	
	/*
	 * Checks any argument list for null and throws an IllegalArgumentException if they are.
	 */
	static void checkNoNullArgs (Object ... args) {
		for (Object o : args) {
			if (o == null){
				throw new IllegalArgumentException();
			}
		}
	}
	
	/*
	 * Throws IllegalArgumentException if strings are empty.
	 */
	 static void stringsNotEmpty(String ...args) {
		for (String o : args) {
			if (o.length()==0){
				throw new IllegalArgumentException();
			}
		}
		
	}

}

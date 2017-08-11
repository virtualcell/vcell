package org.vcell.util;


/**
 * equality utilities
 */
public class EqualsUtil {
	
	/**
	 * perform generic equality checks.<br>
	 * Returns true if a and b exactly the same object.<br>
	 * Returns false if either a or b null or not of exact same type<br> 
	 * Otherwise returns null
	 * @param a if null, returns false
	 * @param b if null, returns false
	 * @return result of comparison or null 
	 */
	public static Boolean typeCompare(Object a, Object b) {
		if (a == null || b == null) {
			return false;
		}
		if (a.getClass() != b.getClass())  {
			return false;
		}
		if (a == b) {
			return true;
		}
		return null;
	}

}

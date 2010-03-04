package org.vcell.sybil.util.java;

/*   NullUtil  --- by Oliver Ruebenacker, UCHC --- October 2008 to February 2010
 *   Deals with cases where you want to call methods of an object that may be null.
 *   For example, a.equals(b) would throw such an NullPointerException, if a was null, but
 *   ObjectUtil.equals(a, b) would always be save.
 */

public class NullUtil {

	static public int hashCode(Object o) { return o != null ? o.hashCode() : 0; }
	
	static public boolean equals(Object o1, Object o2) {
		return (o1 == null && o2 == null) || (o1 != null && o2 != null && o1.equals(o2));
	}
	
	static public <T extends Comparable<T>> int compare(T t1, T t2) {
		return t1 != null ? 
				(t2 != null ? t1.compareTo(t2) : 1) 
				: (t2 != null ? -1 : 0);
	}
	
	static public String toString(Object o) { return o != null ? o.toString() : "null"; }
	
}

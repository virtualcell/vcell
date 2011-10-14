package org.vcell.sybil.util;

public class JavaUtil {
	
	public static boolean equals(Object o1, Object o2) { return o1 == null ? o2 == null : o1.equals(o2); }
	public static int hashCode(Object o) { return o == null ? 0 : o.hashCode(); }

}

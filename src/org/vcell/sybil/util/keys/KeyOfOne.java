package org.vcell.sybil.util.keys;

/*   KeyOfOne  --- by Oliver Ruebenacker, UCHC --- September 2008 to November 2009
 *   A constant container of one object, with equality being defined as equal wrapped objects.
 */

public class KeyOfOne<A> {
	
	protected A a;
	
	public KeyOfOne(A a) { this.a = a; }
	
	public A a() { return a; }
	public int hashCode() { return a != null ? a.hashCode() : 0; }
	
	public boolean equals(Object o) { 
		if(o instanceof KeyOfOne<?>) {
			Object a2 = ((KeyOfOne<?>) o).a();
			return (a == null && a2 == null) || (a != null && a.equals(a2));
		}
		return false;
	}
	
	public String toString() { 
		return "(" + (a != null ? a.toString() : "null") + ")"; 
	}

}

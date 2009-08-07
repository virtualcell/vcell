package org.vcell.sybil.util.keys;

/*   KeyOfOne  --- by Oliver Ruebenacker, UCHC --- September 2008
 *   A constant container of two objects, with equality being defined as equal wrapped objects.
 */

public class KeyOfTwo<A, B> {
	
	protected A a;
	protected B b;
	
	public KeyOfTwo(A a, B b) { this.a = a; this.b = b; }
	
	public A a() { return a; }
	public B b() { return b; }
	
	public int hashCode() { 
		int aHashCode = a != null ? a.hashCode() : 0;
		int bHashCode = b != null ? b.hashCode() : 0;
		return aHashCode + bHashCode; 
	}
	
	public boolean equals(Object o) { 
		if(o instanceof KeyOfTwo) {
			Object a2 = ((KeyOfTwo<?, ?>) o).a();
			boolean aEqualsA2 = (a == null && a2 == null) || (a != null && a.equals(a2));
			Object b2 = ((KeyOfTwo<?, ?>) o).b();
			boolean bEqualsB2 = (b == null && b2 == null) || (b != null && b.equals(b2));
			return aEqualsA2 && bEqualsB2;
		}
		return false;
	}
	
	public String toString() { 
		String aString = a != null ? a.toString() : "null";
		String bString = b != null ? b.toString() : "null";
		return "(" + aString + ", " + bString + ")"; 
	}

}

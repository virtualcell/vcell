/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.keys;

/*   KeyOfOne  --- by Oliver Ruebenacker, UCHC --- September 2008 to November 2009
 *   A constant container of three objects, with equality being defined as equal wrapped objects.
 */

public class KeyOfThree<A, B, C> {
	
	protected A a;
	protected B b;
	protected C c;
	
	public KeyOfThree(A a, B b, C c) { this.a = a; this.b = b; this.c = c; }
	
	public A a() { return a; }
	public B b() { return b; }
	public C c() { return c; }
	
	@Override
	public int hashCode() { 
		int aHashCode = a != null ? a.hashCode() : 0;
		int bHashCode = b != null ? b.hashCode() : 0;
		int cHashCode = c != null ? c.hashCode() : 0;
		return aHashCode + bHashCode + cHashCode; 
	}
	
	@Override
	public boolean equals(Object o) { 
		if(o instanceof KeyOfThree<?, ?, ?>) {
			Object a2 = ((KeyOfThree<?, ?, ?>) o).a();
			boolean aEqualsA2 = (a == null && a2 == null) || (a != null && a.equals(a2));
			Object b2 = ((KeyOfThree<?, ?, ?>) o).b();
			boolean bEqualsB2 = (b == null && b2 == null) || (b != null && b.equals(b2));
			Object c2 = ((KeyOfThree<?, ?, ?>) o).c();
			boolean cEqualsC2 = (c == null && c2 == null) || (c != null && c.equals(c2));
			return aEqualsA2 && bEqualsB2 && cEqualsC2;
		}
		return false;
	}
	
	@Override
	public String toString() { 
		String aString = a != null ? a.toString() : "null";
		String bString = b != null ? b.toString() : "null";
		String cString = c != null ? c.toString() : "null";
		return "(" + aString + ", " + bString + ", " + cString + ")"; 
	}

}

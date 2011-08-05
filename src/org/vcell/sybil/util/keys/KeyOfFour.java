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

/*   KeyOfFour  --- by Oliver Ruebenacker, UCHC --- September 2008 to November 2009
 *   A constant container of four objects, with equality being defined as equal wrapped objects.
 */

public class KeyOfFour<A, B, C, D> {
	
	protected A a;
	protected B b;
	protected C c;
	protected D d;
	
	public KeyOfFour(A a, B b, C c, D d) { this.a = a; this.b = b; this.c = c; this.d = d; }
	
	public A a() { return a; }
	public B b() { return b; }
	public C c() { return c; }
	public D d() { return d; }
	
	@Override
	public int hashCode() { 
		int aHashCode = a != null ? a.hashCode() : 0;
		int bHashCode = b != null ? b.hashCode() : 0;
		int cHashCode = c != null ? c.hashCode() : 0;
		int dHashCode = d != null ? d.hashCode() : 0;
		return aHashCode + bHashCode + cHashCode + dHashCode; 
	}
	
	@Override
	public boolean equals(Object o) { 
		if(o instanceof KeyOfFour<?, ?, ?, ?>) {
			Object a2 = ((KeyOfFour<?, ?, ?, ?>) o).a();
			boolean aEqualsA2 = (a == null && a2 == null) || (a != null && a.equals(a2));
			Object b2 = ((KeyOfFour<?, ?, ?, ?>) o).b();
			boolean bEqualsB2 = (b == null && b2 == null) || (b != null && b.equals(b2));
			Object c2 = ((KeyOfFour<?, ?, ?, ?>) o).c();
			boolean cEqualsC2 = (c == null && c2 == null) || (c != null && c.equals(c2));
			Object d2 = ((KeyOfFour<?, ?, ?, ?>) o).d();
			boolean dEqualsD2 = (d == null && d2 == null) || (d != null && d.equals(d2));
			return aEqualsA2 && bEqualsB2 && cEqualsC2 && dEqualsD2;
		}
		return false;
	}
	
	@Override
	public String toString() { 
		String aString = a != null ? a.toString() : "null";
		String bString = b != null ? b.toString() : "null";
		String cString = c != null ? c.toString() : "null";
		String dString = d != null ? d.toString() : "null";
		return "(" + aString + ", " + bString + ", " + cString + ", " + dString + ")"; 
	}

}

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
 *   A constant container of two objects, with equality being defined as equal wrapped objects.
 */

public class KeyOfTwo<A, B> {
	
	protected A a;
	protected B b;
	
	public KeyOfTwo(A a, B b) { this.a = a; this.b = b; }
	
	public A a() { return a; }
	public B b() { return b; }
	
	@Override
	public int hashCode() { 
		int aHashCode = a != null ? a.hashCode() : 0;
		int bHashCode = b != null ? b.hashCode() : 0;
		return aHashCode + bHashCode; 
	}
	
	@Override
	public boolean equals(Object o) { 
		if(o instanceof KeyOfTwo<?, ?>) {
			Object a2 = ((KeyOfTwo<?, ?>) o).a();
			boolean aEqualsA2 = (a == null && a2 == null) || (a != null && a.equals(a2));
			Object b2 = ((KeyOfTwo<?, ?>) o).b();
			boolean bEqualsB2 = (b == null && b2 == null) || (b != null && b.equals(b2));
			return aEqualsA2 && bEqualsB2;
		}
		return false;
	}
	
	@Override
	public String toString() { 
		String aString = a != null ? a.toString() : "null";
		String bString = b != null ? b.toString() : "null";
		return "(" + aString + ", " + bString + ")"; 
	}

}

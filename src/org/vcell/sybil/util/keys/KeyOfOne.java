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
 *   A constant container of one object, with equality being defined as equal wrapped objects.
 */

public class KeyOfOne<A> {
	
	protected A a;
	
	public KeyOfOne(A a) { this.a = a; }
	
	public A a() { return a; }
	@Override
	public int hashCode() { return a != null ? a.hashCode() : 0; }
	
	@Override
	public boolean equals(Object o) { 
		if(o instanceof KeyOfOne<?>) {
			Object a2 = ((KeyOfOne<?>) o).a();
			return (a == null && a2 == null) || (a != null && a.equals(a2));
		}
		return false;
	}
	
	@Override
	public String toString() { 
		return "(" + (a != null ? a.toString() : "null") + ")"; 
	}

}

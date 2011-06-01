/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.category;

/*   CatNoParams  --- by Oliver Ruebenacker, UCHC --- October 2008 to November 2009
 *   Category with no parameters.
 */

public abstract class CategoryNoParams<C extends Category<C>> extends SimpleCategory<C> implements Category<C> {
	
	protected Class<?> c;
	
	public CategoryNoParams(Class<?> c) { this.c = c; }
	
	public Class<?> c() { return c; }
	public int hashCode() { return c.hashCode(); }
	
	public boolean equals(Object o) { 
		if(o instanceof CategoryNoParams<?>) {
			Class<?> c2 = ((CategoryNoParams<?>) o).c();
			return c.equals(c2);
		}
		return false;
	}
	
	public String toString() { return "(" +  c.toString() + ")"; }

}

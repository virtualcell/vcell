/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.lists;

/*   IterOfOne  --- by Oliver Ruebenacker, UCHC --- April 2008
 *   An iterator for a list with one element
 */

import java.util.ListIterator;
import java.util.NoSuchElementException;

public class IterOfOne<E> implements ListIterator<E> {

	protected E e;
	protected int i;
	
	public IterOfOne(E eNew) { e = eNew; }
	public IterOfOne(E eNew, int iNew) { e = eNew; i = iNew; }
	
	public boolean hasNext() { return i == 0; }
	public boolean hasPrevious() { return i == 1; }
	public int nextIndex() { return i; }
	public int previousIndex() { return i-1; }
	
	public E next() { 
		if(i == 0) { i = 1; return e; }
		throw new NoSuchElementException(); 
	}
	
	public E previous() { 
		if(i == 1) { i = 0; return e; }
		throw new NoSuchElementException(); 
	}
	
	public void add(E e) { throw new UnsupportedOperationException(); }
	public void remove() { throw new UnsupportedOperationException(); }
	public void set(E e) { throw new UnsupportedOperationException(); }

}

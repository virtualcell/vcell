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

/*   IterOfTwo  --- by Oliver Ruebenacker, UCHC --- April 2008
 *   An iterator for a list with three elements
 */

import java.util.ListIterator;
import java.util.NoSuchElementException;

public class IterOfThree<E> implements ListIterator<E> {

	protected E e1, e2, e3;
	protected int i;
	
	public IterOfThree(E e1New, E e2New, E e3New) { e1 = e1New; e2 = e2New; e3 = e3New; }
	public IterOfThree(E e1New, E e2New, E e3New, int iNew) { 
		e1 = e1New; e2 = e2New; e3 = e3New; i = iNew; 
	}
	
	public boolean hasNext() { return i < 3; }
	public boolean hasPrevious() { return i > -1; }
	public int nextIndex() { return i; }
	public int previousIndex() { return i-1; }
	
	public E next() { 
		if(i == 0) { i = 1; return e1; }
		else if(i == 1) { i = 2; return e2; }
		else if(i == 2) { i = 3; return e3; }
		throw new NoSuchElementException(); 
	}
	
	public E previous() { 
		if(i == 1) { i = 0; return e1; }
		else if(i == 2) { i = 1; return e2; }
		else if(i == 3) { i = 2; return e3; }
		throw new NoSuchElementException(); 
	}
	
	public void add(E e) { throw new UnsupportedOperationException(); }
	public void remove() { throw new UnsupportedOperationException(); }
	public void set(E e) { throw new UnsupportedOperationException(); }

}

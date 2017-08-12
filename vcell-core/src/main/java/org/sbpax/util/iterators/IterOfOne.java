/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.util.iterators;

/*   IterOfOne  --- by Oliver Ruebenacker, UCHC --- August 2007 to July 2008
 *   An iterator with exactly one element
 */

import java.util.NoSuchElementException;


public class IterOfOne<E> implements SmartIter<E> {

	private E element;
	private int number;
	
	public IterOfOne(E newElement) { element = newElement; };

	public boolean hasNext() { return number == 0; };

	public E next() {
		if(!hasNext()) throw new NoSuchElementException();
		++number;
		return element;
	}

	public int count() { return number; }
	public boolean isAtBoundary() { return true; }
	public boolean isAtInternalBoundary() { return false; }
	public int subCount() { return number; }
	public void remove() { throw new UnsupportedOperationException(); }

}

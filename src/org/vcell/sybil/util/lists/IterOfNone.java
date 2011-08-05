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

/*   IterOfNone  --- by Oliver Ruebenacker, UCHC --- April 2008
 *   An iterator for an empty list
 */

import java.util.ListIterator;
import java.util.NoSuchElementException;

public class IterOfNone<E> implements ListIterator<E> {

	public boolean hasNext() { return false; }
	public boolean hasPrevious() { return false; }
	public int nextIndex() { return 0; }
	public int previousIndex() { return -1; }
	public E next() { throw new NoSuchElementException(); }
	public E previous() { throw new NoSuchElementException(); }
	public void add(E e) { throw new UnsupportedOperationException(); }
	public void remove() { throw new UnsupportedOperationException(); }
	public void set(E e) { throw new UnsupportedOperationException(); }

}

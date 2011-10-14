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

/*   IterOfNone  --- by Oliver Ruebenacker, UCHC --- August 2007 to July 2008
 *   An empty iterator
 */

import java.util.NoSuchElementException;


public class IterOfNone<E> implements SmartIter<E> {

	public boolean hasNext() { return false; };
	public E next() { throw new NoSuchElementException(); }
	public int count() { return 0; }
	public boolean isAtBoundary() { return true; }
	public boolean isAtInternalBoundary() { return false; }
	public int subCount() { return 0; }
	public void remove() { throw new UnsupportedOperationException(); }
}

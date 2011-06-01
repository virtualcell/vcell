/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.iterators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BufferedIterator<E> implements Iterator<E> {

	protected Set<E> elements;
	protected Iterator<E> iter;
	
	public BufferedIterator(Iterator<E> oldIter) {
		elements = new HashSet<E>();
		while(oldIter.hasNext()) { elements.add(oldIter.next()); }
		iter = elements.iterator();
	}
	
	public boolean hasNext() { return iter.hasNext(); }
	public E next() { return iter.next(); }
	public void remove() { throw new UnsupportedOperationException(); }

}

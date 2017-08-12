/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.util.sets;

/*   SetOfNone  --- by Oliver Ruebenacker, UCHC --- December 2007
 *   A constant empty set
 */

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.sbpax.util.iterators.IterOfNone;


public class SetOfNone<E> implements Set<E> {

	public boolean add(E element) { throw new UnsupportedOperationException(); }
	public boolean addAll(Collection<? extends E> collection) { throw new UnsupportedOperationException(); }
	public void clear() { }
	public boolean contains(Object arg0) { return false; }
	
	public boolean containsAll(Collection<?> collection) {
		if(collection.size() == 0) { return true; }
		else { return false; }
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof Set) {
			Set<?> set = (Set<?>) object;
			return set.isEmpty();
		}
		return false;
	}
	
	@Override
	public int hashCode() { return 0; }

	public boolean isEmpty() { return true; }
	public Iterator<E> iterator() { return new IterOfNone<E>(); }

	public boolean remove(Object arg0) { throw new UnsupportedOperationException(); }
	public boolean removeAll(Collection<?> collection) { throw new UnsupportedOperationException(); }
	public boolean retainAll(Collection<?> collection) { throw new UnsupportedOperationException();	}
	public int size() { return 0; }

	public Object[] toArray() { return new Object[0]; }

	public <T> T[] toArray(T[] someArray) {
		HashSet<E> set = new HashSet<E>();
		return set.toArray(someArray);
	}

}

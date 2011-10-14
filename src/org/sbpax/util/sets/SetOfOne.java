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

/*   SetOfOne  --- by Oliver Ruebenacker, UCHC --- December 2007
 *   A constant set with one element
 */

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.sbpax.util.iterators.IterOfOne;


public class SetOfOne<E> implements Set<E> {

	protected E element;
	
	public SetOfOne(E element) { this.element = element; }
	
	public E getElement() { return element; }
	
	public boolean add(E element) { throw new UnsupportedOperationException(); }
	public boolean addAll(Collection<? extends E> collection) { throw new UnsupportedOperationException(); }
	public void clear() { throw new UnsupportedOperationException(); }
	public boolean contains(Object object) { return element.equals(object); }
	
	public boolean containsAll(Collection<?> collection) {
		for(Object object : collection) {
			if(!contains(object)) { return false; }
		}
		return true;
	}
	
	@Override
	public boolean equals(Object object) {
		if(object instanceof Set) {
			Set<?> set = (Set<?>) object;
			return set.size() == 1 && set.contains(element);
		}
		return false;
	}
	
	@Override
	public int hashCode() { return element.hashCode(); }

	public boolean isEmpty() { return false; }
	public Iterator<E> iterator() { return new IterOfOne<E>(element); }
	public boolean remove(Object arg0) { throw new UnsupportedOperationException(); }
	public boolean removeAll(Collection<?> collection) { throw new UnsupportedOperationException(); }
	public boolean retainAll(Collection<?> collection) { throw new UnsupportedOperationException();	}
	public int size() { return 1; }

	public Object[] toArray() { 
		HashSet<E> set = new HashSet<E>(this);
		return set.toArray();
	}

	public <T> T[] toArray(T[] someArray) {
		HashSet<E> set = new HashSet<E>(this);
		return set.toArray(someArray);
	}

}

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

/*   SetOfTwo  --- by Oliver Ruebenacker, UCHC --- December 2007
 *   A constant set with two elements
 */

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.sbpax.util.iterators.IterOfTwo;


public class SetOfTwo<E> implements Set<E> {

	protected E element1;
	protected E element2;
	
	public SetOfTwo(E element1, E element2) { this.element1 = element1; this.element2 = element2; }
	
	public E getElement1() { return element1; }
	public E getElement2() { return element2; }
	
	public boolean add(E element) { throw new UnsupportedOperationException(); }
	public boolean addAll(Collection<? extends E> collection) { throw new UnsupportedOperationException(); }
	public void clear() { throw new UnsupportedOperationException(); }
	public boolean contains(Object object) { return element1.equals(object) || element2.equals(object); }
	
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
			return set.size() == 2 && set.contains(element1) && set.contains(element2);
		}
		return false;
	}
	
	@Override
	public int hashCode() { return element1.hashCode() + element2.hashCode(); }

	public boolean isEmpty() { return false; }
	public Iterator<E> iterator() { return new IterOfTwo<E>(element1, element2); }
	public boolean remove(Object arg0) { throw new UnsupportedOperationException(); }
	public boolean removeAll(Collection<?> collection) { throw new UnsupportedOperationException(); }
	public boolean retainAll(Collection<?> collection) { throw new UnsupportedOperationException();	}
	public int size() { return 2; }

	public Object[] toArray() { 
		HashSet<E> set = new HashSet<E>(this);
		return set.toArray();
	}

	public <T> T[] toArray(T[] someArray) {
		HashSet<E> set = new HashSet<E>(this);
		return set.toArray(someArray);
	}

}

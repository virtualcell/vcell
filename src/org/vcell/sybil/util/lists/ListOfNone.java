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

/*   ListOfNone  --- by Oliver Ruebenacker, UCHC --- April 2008 to November 2009
 *   An empty list, in case we need one
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

public class ListOfNone<E> implements List<E> {

	public boolean contains(Object o) { return false; }

	public boolean containsAll(Collection<?> c) {
		if(c.size() == 0) { return true; }
		return false;
	}

	public E get(int index) { throw new IndexOutOfBoundsException(); }
	public int indexOf(Object o) { return -1; }
	public boolean isEmpty() { return true; }
	public Iterator<E> iterator() { return new IterOfNone<E>(); }
	public int lastIndexOf(Object o) { return -1; }
	public ListIterator<E> listIterator() { return new IterOfNone<E>(); }

	public ListIterator<E> listIterator(int index) {
		if(index == 0) return new IterOfNone<E>();
		throw new IndexOutOfBoundsException();
	}

	public int size() { return 0; }

	public List<E> subList(int fromIndex, int toIndex) {
		if(fromIndex == 0 && toIndex == 0) { return new ListOfNone<E>(); }
		throw new IndexOutOfBoundsException();
	}

	public Object[] toArray() { return (new Vector<E>()).toArray(); }
    public <T> T[] toArray(T[] a) { return (new Vector<E>()).toArray(a); }

	public boolean add(Object e) { throw new UnsupportedOperationException(); }
	public void add(int index, Object element) { throw new UnsupportedOperationException(); }
	public boolean addAll(Collection<? extends E> c) { throw new UnsupportedOperationException(); }

	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException(); 
	}

	public void clear() { throw new UnsupportedOperationException(); }
	public boolean remove(Object o) { throw new UnsupportedOperationException(); }
	public E remove(int index) { throw new UnsupportedOperationException(); }
	public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }
	public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }
	public E set(int index, E element) { throw new UnsupportedOperationException(); }
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof List<?>) { 
			List<?> l = (List<?>) o;
			return l.size() == 0;
		}
		return false;
	}
	
	@Override
	public int hashCode() { return 1; }
}

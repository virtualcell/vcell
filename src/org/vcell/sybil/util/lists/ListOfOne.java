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

/*   ListOfOne  --- by Oliver Ruebenacker, UCHC --- April 2008 to November 2009
 *   A list of one element
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

public class ListOfOne<E> implements List<E> {

	protected E e;
	
	public ListOfOne(E eNew) { e = eNew; }
	
	public boolean contains(Object o) { return e.equals(o); }

	public boolean containsAll(Collection<?> c) {
		for(Object e2 : c) { if(!e.equals(e2)) { return false; } }
		return true;
	}

	public E get(int index) { 
		if(index == 0) { return e; }
		throw new IndexOutOfBoundsException(); 
	}
	
	public int indexOf(Object o) { 
		if(e.equals(o)) { return 0; }
		return -1; 
	}

	public boolean isEmpty() { return false; }
	
	public Iterator<E> iterator() { return new IterOfOne<E>(e); }

	public int lastIndexOf(Object o) { 
		if(e.equals(o)) { return 0; }
		return -1; 
	}
	
	public ListIterator<E> listIterator() { return new IterOfOne<E>(e); }

	public ListIterator<E> listIterator(int index) {
		if(index > -1 && index < 2) return new IterOfOne<E>(e, index);
		throw new IndexOutOfBoundsException();
	}

	public int size() { return 1; }

	public List<E> subList(int fromIndex, int toIndex) {
		if(fromIndex == 0) { 
			if(toIndex == 0) { return new ListOfNone<E>(); }
			else if(toIndex == 1) { return new ListOfOne<E>(e); }
		} else if(fromIndex == 1 && toIndex == 1) { return new ListOfNone<E>(); }
		throw new IndexOutOfBoundsException();
	}

	public Object[] toArray() { 
		Vector<E> vector = new Vector<E>();
		vector.add(e);
		return vector.toArray(); 
	}
	
    public <T> T[] toArray(T[] a) { 
		Vector<E> vector = new Vector<E>();
		vector.add(e);    	
    	return vector.toArray(a); 
    }

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
			if(l.size() == 1) {
				return e.equals(l.get(0));
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() { return 31 + (e==null ? 0 : e.hashCode()); }

}

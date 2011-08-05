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

/*   ListOfThree  --- by Oliver Ruebenacker, UCHC --- April 2008 to November 2009
 *   A list with three elements
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

public class ListOfThree<E> implements List<E> {

	protected E e0, e1, e2;
	
	public ListOfThree(E e0New, E e1New, E e2New) { e0 = e0New; e1 = e1New; e2 = e2New; }
	
	public boolean contains(Object o) { return e0.equals(o) || e1.equals(o) || e2.equals(o); }

	public boolean containsAll(Collection<?> c) {
		for(Object eo : c) { if(!e0.equals(eo) && !e1.equals(eo) && !e2.equals(eo)) { return false; } }
		return true;
	}

	public E get(int index) { 
		if(index == 0) { return e0; }
		else if(index == 1) { return e1; }
		else if(index == 2) { return e2; }
		throw new IndexOutOfBoundsException(); 
	}
	
	public int indexOf(Object o) { 
		if(e0.equals(o)) { return 0; }
		else if(e1.equals(o)) { return 1; }
		else if(e2.equals(o)) { return 2; }
		return -1; 
	}

	public boolean isEmpty() { return false; }
	
	public Iterator<E> iterator() { return new IterOfThree<E>(e0, e1, e2); }

	public int lastIndexOf(Object o) { 
		if(e2.equals(o)) { return 2; }
		else if(e1.equals(o)) { return 1; }
		else if(e0.equals(o)) { return 0; }
		return -1; 
	}
	
	public ListIterator<E> listIterator() { return new IterOfThree<E>(e0, e1, e2); }

	public ListIterator<E> listIterator(int index) {
		if(index > -1 && index < 4) return new IterOfThree<E>(e0, e1, e2, index);
		throw new IndexOutOfBoundsException();
	}

	public int size() { return 3; }

	public List<E> subList(int fromIndex, int toIndex) {
		if(fromIndex == 0) { 
			if(toIndex == 0) { return new ListOfNone<E>(); }
			else if(toIndex == 1) { return new ListOfOne<E>(e0); }
			else if(toIndex == 2) { return new ListOfTwo<E>(e0, e1); }
			else if(toIndex == 3) { return new ListOfThree<E>(e0, e1, e2); }
		} else	if(fromIndex == 1) { 
			if(toIndex == 1) { return new ListOfNone<E>(); }
			else if(toIndex == 2) { return new ListOfOne<E>(e1); }
			else if(toIndex == 3) { return new ListOfTwo<E>(e1, e2); }
		} else	if(fromIndex == 2) { 
			if(toIndex == 2) { return new ListOfNone<E>(); }
			else if(toIndex == 3) { return new ListOfOne<E>(e2); }
		} else if(fromIndex == 3 && toIndex == 3) { return new ListOfNone<E>(); }
		throw new IndexOutOfBoundsException();
	}

	public Object[] toArray() { 
    	Vector<E> vector = new Vector<E>();
    	vector.add(e0);
    	vector.add(e1);
    	vector.add(e2);
		return vector.toArray(); 
	}
	
    public <T> T[] toArray(T[] a) { 
    	Vector<E> vector = new Vector<E>();
    	vector.add(e0);
    	vector.add(e1);
    	vector.add(e2);
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
			if(l.size() == 3) {
				return e0.equals(l.get(0)) && e1.equals(l.get(1)) && e2.equals(l.get(2));
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int hashCode = 31 + (e0==null ? 0 : e0.hashCode());
		hashCode = 31*hashCode + (e1==null ? 0 : e1.hashCode());
		hashCode = 31*hashCode + (e2==null ? 0 : e2.hashCode());
		return hashCode; 
	}



}

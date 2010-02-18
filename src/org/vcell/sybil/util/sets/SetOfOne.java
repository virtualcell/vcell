package org.vcell.sybil.util.sets;

/*   SetOfOne  --- by Oliver Ruebenacker, UCHC --- December 2007
 *   A constant set with one element
 */

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vcell.sybil.util.iterators.IterOfOne;


public class SetOfOne<E> implements Set<E> {

	protected E element;
	
	public SetOfOne(E element) { this.element = element; }
	
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

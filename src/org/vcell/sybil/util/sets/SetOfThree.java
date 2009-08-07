package org.vcell.sybil.util.sets;

/*   SetOfThree  --- by Oliver Ruebenacker, UCHC --- December 2007
 *   A constant set with three elements
 */

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vcell.sybil.util.iterators.IterOfThree;


public class SetOfThree<E> implements Set<E> {

	protected E element1;
	protected E element2;
	protected E element3;
	
	public SetOfThree(E element1, E element2, E element3) { 
		this.element1 = element1; 
		this.element2 = element2; 
		this.element3 = element3; 
	}
	
	public boolean add(E element) { throw new UnsupportedOperationException(); }
	public boolean addAll(Collection<? extends E> collection) { throw new UnsupportedOperationException(); }
	public void clear() { throw new UnsupportedOperationException(); }
	public boolean contains(Object object) { return element1.equals(object) || element2.equals(object) || element3.equals(object); }
	
	public boolean containsAll(Collection<?> collection) {
		for(Object object : collection) {
			if(!contains(object)) { return false; }
		}
		return true;
	}

	public boolean isEmpty() { return false; }
	public Iterator<E> iterator() { return new IterOfThree<E>(element1, element2, element3); }
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

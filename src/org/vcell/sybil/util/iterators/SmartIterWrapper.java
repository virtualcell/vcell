package org.vcell.sybil.util.iterators;

/*   SmartIterWrapper  --- by Oliver Ruebenacker, UCHC --- ? to September 2009
 *   A view of a resource representing an SBPAX object
 */

import java.util.Iterator;

public class SmartIterWrapper<E> implements SmartIter<E> {

	protected Iterator<E> iterator;
	protected int number;
	
	public SmartIterWrapper(Iterator<E> newEnumeration) { iterator = newEnumeration; }

	public int count() { 
		if(iterator instanceof SmartIter<?>) {
			return ((SmartIter<E>) iterator).count(); 
		} else {
			return number;
		}
	}

	public boolean hasNext() { return iterator.hasNext(); }
	public E next() { return iterator.next(); }

	public boolean isAtBoundary() {
		if(iterator instanceof SmartIter<?>) {
			((SmartIter<E>) iterator).isAtBoundary();
		} 
		return number == 0 || !iterator.hasNext();
	}

	public boolean isAtInternalBoundary() {
		if(iterator instanceof SmartIter<?>) {
			((SmartIter<E>) iterator).isAtBoundary();
		} 
		return false;
	}

	public int subCount() {
		if(iterator instanceof SmartIter<?>) {
			((SmartIter<E>) iterator).subCount();
		}
		return number;
	}

	public void remove() { throw new UnsupportedOperationException(); }

}

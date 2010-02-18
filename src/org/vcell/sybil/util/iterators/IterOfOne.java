package org.vcell.sybil.util.iterators;

/*   IterOfOne  --- by Oliver Ruebenacker, UCHC --- August 2007 to July 2008
 *   An iterator with exactly one element
 */

import java.util.NoSuchElementException;

public class IterOfOne<E> implements SmartIter<E> {

	private E element;
	private int number;
	
	public IterOfOne(E newElement) { element = newElement; };

	public boolean hasNext() { return number == 0; };

	public E next() {
		if(!hasNext()) throw new NoSuchElementException();
		++number;
		return element;
	}

	public int count() { return number; }
	public boolean isAtBoundary() { return true; }
	public boolean isAtInternalBoundary() { return false; }
	public int subCount() { return number; }
	public void remove() { throw new UnsupportedOperationException(); }

}

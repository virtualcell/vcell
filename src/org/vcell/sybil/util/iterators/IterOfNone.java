package org.vcell.sybil.util.iterators;

/*   IterOfNone  --- by Oliver Ruebenacker, UCHC --- August 2007 to July 2008
 *   An empty iterator
 */

import java.util.NoSuchElementException;

public class IterOfNone<E> implements SmartIter<E> {

	public boolean hasNext() { return false; };
	public E next() { throw new NoSuchElementException(); }
	public int count() { return 0; }
	public boolean isAtBoundary() { return true; }
	public boolean isAtInternalBoundary() { return false; }
	public int subCount() { return 0; }
	public void remove() { throw new UnsupportedOperationException(); }
}

package org.vcell.sybil.util.lists;

/*   IterOfNone  --- by Oliver Ruebenacker, UCHC --- April 2008
 *   An iterator for an empty list
 */

import java.util.ListIterator;
import java.util.NoSuchElementException;

public class IterOfNone<E> implements ListIterator<E> {

	public boolean hasNext() { return false; }
	public boolean hasPrevious() { return false; }
	public int nextIndex() { return 0; }
	public int previousIndex() { return -1; }
	public E next() { throw new NoSuchElementException(); }
	public E previous() { throw new NoSuchElementException(); }
	public void add(E e) { throw new UnsupportedOperationException(); }
	public void remove() { throw new UnsupportedOperationException(); }
	public void set(E e) { throw new UnsupportedOperationException(); }

}

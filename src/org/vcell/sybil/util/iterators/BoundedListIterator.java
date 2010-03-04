package org.vcell.sybil.util.iterators;

/*   BoundedListIterator  --- by Oliver Ruebenacker, UCHC --- July 2008
 *   An iterator for a bounded list (to catch use of Iterator.remove())
 */

import java.util.ListIterator;

import org.vcell.sybil.util.lists.BoundedList;

public class BoundedListIterator<E> implements ListIterator<E> {

	protected ListIterator<E> iterator;
	protected BoundedList<E> list;
	protected E lastRead;
	protected int lastReadInd;
	
	public BoundedListIterator(BoundedList<E> setNew, ListIterator<E> iteratorNew) {
		list = setNew;
		iterator = iteratorNew;
	}
	
	public boolean hasNext() { return iterator.hasNext(); }

	public E next() { 
		lastReadInd = iterator.nextIndex();
		return lastRead = iterator.next(); 
	}
	
	public void remove() { 
		if(lastRead == null) { throw new IllegalStateException(); }
		iterator.remove(); 
		list.fireIntervalRemoved(lastReadInd, lastReadInd);
		lastRead = null;
	}

	public void add(E e) {
		int ind = iterator.nextIndex();
		iterator.add(e);
		lastRead = null;
		list.fireIntervalAdded(ind, ind);
	}

	public boolean hasPrevious() { return iterator.hasPrevious(); }
	public int nextIndex() { return iterator.nextIndex(); }

	public E previous() {
		lastReadInd = iterator.previousIndex();
		return lastRead = iterator.previous(); 
	}

	public int previousIndex() { return iterator.previousIndex(); }

	public void set(E e) {
		if(lastRead == null) { throw new IllegalStateException(); }
		iterator.set(e);
		list.fireContentsChanged(lastReadInd, lastReadInd);
		lastRead = null;
	}

}

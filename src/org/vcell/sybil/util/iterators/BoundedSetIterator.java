package org.vcell.sybil.util.iterators;

/*   BoundedSetIterator  --- by Oliver Ruebenacker, UCHC --- June 2008 to August 2009
 *   An iterator for a bounded set (to catch use of Iterator.remove())
 */

import java.util.Iterator;
import org.vcell.sybil.util.sets.BoundedSet;

public class BoundedSetIterator<E> implements Iterator<E> {

	protected Iterator<E> iterator;
	protected BoundedSet<E> set;
	protected E element;
	
	public BoundedSetIterator(BoundedSet<E> setNew, Iterator<E> iteratorNew) {
		set = setNew;
		iterator = iteratorNew;
	}
	
	public boolean hasNext() { return iterator.hasNext(); }
	public E next() { return element = iterator.next(); }
	
	public void remove() { 
		if(element != null) { 
			iterator.remove();
			BoundedSet.EventRemove<E> event = new BoundedSet.EventRemove<E>(set, element);
			for(BoundedSet.Listener<E> listener : set.listeners()) { listener.fireEvent(event); }
		} 
	}

}

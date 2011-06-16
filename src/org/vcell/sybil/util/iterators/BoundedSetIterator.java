/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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

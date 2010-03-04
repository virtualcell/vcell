package org.vcell.sybil.util.iterators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BufferedIterator<E> implements Iterator<E> {

	protected Set<E> elements;
	protected Iterator<E> iter;
	
	public BufferedIterator(Iterator<E> oldIter) {
		elements = new HashSet<E>();
		while(oldIter.hasNext()) { elements.add(oldIter.next()); }
		iter = elements.iterator();
	}
	
	public boolean hasNext() { return iter.hasNext(); }
	public E next() { return iter.next(); }
	public void remove() { throw new UnsupportedOperationException(); }

}

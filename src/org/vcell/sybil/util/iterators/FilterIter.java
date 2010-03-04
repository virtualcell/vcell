package org.vcell.sybil.util.iterators;

import java.util.Iterator;

public class FilterIter<E> implements SmartIter<E> {

	static public interface Tester<E> { public boolean accepts(E e); }

	protected Iterator<E> iter;
	protected Tester<E> tester;
	protected E next;
	protected int count;
	
	public FilterIter(Iterator<E> iterNew, Tester<E> testerNew) {
		iter = iterNew;
		tester = testerNew;
		seekNext();
	}
	
	protected void seekNext() {
		while(true) {
			if(iter.hasNext()) { 
				next = iter.next(); 
				if(tester.accepts(next)) { break; }
				else { continue; }
			} else { 
				next = null; 
				break;
			}
		} 
	}
	
	public int count() { return count; }
	public boolean isAtBoundary() { return count == 0 || next == null; }
	public boolean isAtInternalBoundary() { return count == 0 || next == null; }
	public int subCount() { return count; }
	public boolean hasNext() { return next != null; }

	public E next() {
		E nextOld = next;
		seekNext();
		++count;
		return nextOld;
	}

	public void remove() { throw new UnsupportedOperationException(); }

}

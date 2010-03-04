package org.vcell.sybil.util.iterators;

import java.util.Iterator;
import java.util.Vector;

public class NestedIter<E> implements SmartIter<E> {

	protected Vector<SmartIter<E>> backup;
	protected SmartIter<E> current;
	protected int count;

	public NestedIter() {
		backup = new Vector<SmartIter<E>>();
		current = new IterOfNone<E>();
	}
	
	private void tryBackupIfCurrentEmpty() {
		while((!current.hasNext()) && (backup.size() > 0)) {
			current = backup.remove(0);
		}
	}
	
	public void add(Iterator<E> iterNew) {
		backup.add(new SmartIterWrapper<E>(iterNew));
	}
	
	public boolean hasNext() {
		tryBackupIfCurrentEmpty();
		return current.hasNext();
	}

	public E next() {
		tryBackupIfCurrentEmpty();
		++count;
		return current.next();
	}

	public int subCount() { return current.count(); }

	public int count() { return count; }

	public boolean isAtBoundary() { return current.count() == 0 || !current.hasNext(); }

	public boolean isAtInternalBoundary() { 
		return (current.count() == 0 && count() > 0) || (!current.hasNext() && backup.size() > 0); 
	}
	
	public void remove() { throw new UnsupportedOperationException(); }

}

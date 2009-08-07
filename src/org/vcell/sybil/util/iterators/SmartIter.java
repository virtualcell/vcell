package org.vcell.sybil.util.iterators;

import java.util.Iterator;

public interface SmartIter<E> extends Iterator<E> {

	public int count();
	public int subCount();
	public boolean isAtBoundary();
	public boolean isAtInternalBoundary();

}

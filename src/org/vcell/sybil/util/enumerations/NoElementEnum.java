package org.vcell.sybil.util.enumerations;

import java.util.NoSuchElementException;

public class NoElementEnum<E> implements SmartEnum<E> {

	public boolean hasMoreElements() { return false; };
	public E nextElement() { throw new NoSuchElementException(); }
	public int count() { return 0; }
	public boolean isAtBoundary() { return true; }
	public boolean isAtInternalBoundary() { return false; }
	public int subCount() { return 0; }

}

package org.vcell.sybil.util.enumerations;

import java.util.NoSuchElementException;

public class OneElementEnum<E> implements SmartEnum<E> {

	private E element;
	private int number;
	
	public OneElementEnum(E newElement) { element = newElement; };

	public boolean hasMoreElements() { return number == 0; };

	public E nextElement() {
		if(!hasMoreElements()) throw new NoSuchElementException();
		++number;
		return element;
	}

	public int count() { return number; }
	public boolean isAtBoundary() { return true; }
	public boolean isAtInternalBoundary() { return false; }
	public int subCount() { return number; }

}

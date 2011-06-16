/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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

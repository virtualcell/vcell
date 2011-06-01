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

public class NoElementEnum<E> implements SmartEnum<E> {

	public boolean hasMoreElements() { return false; };
	public E nextElement() { throw new NoSuchElementException(); }
	public int count() { return 0; }
	public boolean isAtBoundary() { return true; }
	public boolean isAtInternalBoundary() { return false; }
	public int subCount() { return 0; }

}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.util.iterators;

import java.util.Iterator;

public interface SmartIter<E> extends Iterator<E> {

	public int count();
	public int subCount();
	public boolean isAtBoundary();
	public boolean isAtInternalBoundary();

}

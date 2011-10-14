/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.comparator;

/*   ComparatorScore  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   A comparator based on assigning a score to each node
 */

import java.util.Comparator;

public abstract class ComparatorByScore<T> implements Comparator<T> {
	public int compare(T t1, T t2) { return score(t2) - score(t1); }
	public abstract int score(T t);
}

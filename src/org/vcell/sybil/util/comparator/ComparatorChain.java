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

/*   ComparatorChain  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   A comparator which is the chain of two given ones (use second if first is zero)
 */

import java.util.Comparator;

public class ComparatorChain<T> implements Comparator<T> {

	protected Comparator<? super T> c1, c2;
	
	public ComparatorChain(Comparator<? super T> c1New, Comparator<? super T> c2New) {
		c1 = c1New;
		c2 = c2New;
	}
	
	public int compare(T t1, T t2) {
		int comparison1 = c1.compare(t1, t2);
		return comparison1 != 0 ? comparison1 : c2.compare(t1, t2);
	
	}

}

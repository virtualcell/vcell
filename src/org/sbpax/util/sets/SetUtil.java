/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.util.sets;

/*   SetUtil  --- by Oliver Ruebenacker, UCHC --- May 2008 to March 2010
 *   A convenient set for adding many members in one line
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SetUtil {

	public static <E> Set<E> newSet(E...es) {
		return new HashSet<E>(Arrays.asList(es));
	}
	
	public static <E> Set<E> unionSet(Set<E>...sets) {
		HashSet<E> union = new HashSet<E>();
		for(Set<E> set : sets) { union.addAll(set); }
		return union;
	}
	
	public static <E> E pickAny(Set<E> set) {
		for(E element : set) { return element; }
		return null;
	}
	
}

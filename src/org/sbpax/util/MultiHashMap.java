/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.util;

/*   MultiHashMap  --- by Oliver Ruebenacker, UCHC --- July 2007
 *   Maps from a key to a set of values, using a HashMap of HashSets
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MultiHashMap<K, V> extends HashMap<K, Set<V>> implements MultiMap<K, V> {

	private static final long serialVersionUID = -8357500889616873516L;
	
	public void add(K key, V value) {
		Set<V> set = get(key);
		if(set != null) {
			set.add(value);
		} else {
			set = new HashSet<V>();
			set.add(value);
			put(key, set);
		}
	}

}

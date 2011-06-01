/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.collections.event;

/*   Listener  --- by Oliver Ruebenacker, UCHC --- November 2007
 *   A listener to a general purpose event
 */

import java.util.Map;

import org.vcell.sybil.util.collections.BoundedMap;

public interface MapListener<K, V> {

	public void mapEventClear(Map<K, V> map);
	public void mapEventPut(Map<K, V> map, K key, V valueOld, V valueNew);
	public void mapEventPut(Map<K, V> map, K keyOld, K keyNew, V valueOld, V valueNew);
	public void mapEventPutAll(Map<K, V> map, Map<? extends K, ? extends V> map2);
	public void mapEventRemove(Map<K, V> map, K key, V value);
	public void nowListeningTo(BoundedMap<K, V> source);
	
}

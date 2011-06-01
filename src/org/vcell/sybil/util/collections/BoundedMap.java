/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.collections;

/*   BoundedMap  --- by Oliver Ruebenacker, UCHC --- November 2007 to April 2008
 *   A Map with a set of listeners
 */

import java.util.Map;

import org.vcell.sybil.util.collections.event.MapListenerSet;

public interface BoundedMap<K, V> extends Map<K, V> {

	public MapListenerSet<K, V> listeners();
	
}

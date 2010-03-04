package org.vcell.sybil.util.collections;

/*   BoundedMap  --- by Oliver Ruebenacker, UCHC --- November 2007 to April 2008
 *   A Map with a set of listeners
 */

import java.util.Map;

import org.vcell.sybil.util.collections.event.MapListenerSet;

public interface BoundedMap<K, V> extends Map<K, V> {

	public MapListenerSet<K, V> listeners();
	
}

package org.vcell.sybil.util.collections.event;

/*   MapListenerHashSet  --- by Oliver Ruebenacker, UCHC --- November 2007 to April 2008
 *   A set of listeners to a general purpose event to be notified at once
 */

import java.util.HashSet;
import java.util.Map;

import org.vcell.sybil.util.collections.BoundedMap;

public class MapListenerHashSet<K, V> 
extends HashSet<MapListener<K, V>> implements MapListenerSet<K, V> {

	private static final long serialVersionUID = 5649505547686827739L;
	protected BoundedMap<K, V> map;
	
	public MapListenerHashSet(BoundedMap<K, V> mapNew) { this.map = mapNew; }
	
	public boolean add(MapListener<K, V> listener) {
		boolean result = super.add(listener);
		listener.nowListeningTo(map);
		return result;
	}
	
	public void nowListeningTo(BoundedMap<K, V> source) {
		for(MapListener<K, V> listener : this) { listener.nowListeningTo(source); }		
	}

	public void mapEventClear(Map<K, V> map) {
		for(MapListener<K, V> listener : this) { listener.mapEventClear(map); }
	}

	public void mapEventPut(Map<K, V> map, K key, V valueOld, V valueNew) {
		for(MapListener<K, V> listener : this) { 
			listener.mapEventPut(map, key, valueOld, valueNew); 
		}
	}

	public void mapEventPut(Map<K, V> map, K keyOld, K keyNew, V valueOld,
			V valueNew) {
		for(MapListener<K, V> listener : this) { 
			listener.mapEventPut(map, keyOld, keyNew, valueOld, valueNew); 
		}
	}

	public void mapEventPutAll(Map<K, V> map, Map<? extends K, ? extends V> map2) {
		for(MapListener<K, V> listener : this) { 
			listener.mapEventPutAll(map, map2);
		}
	}

	public void mapEventRemove(Map<K, V> map, K key, V value) {
		for(MapListener<K, V> listener : this) { 
			listener.mapEventRemove(map, key, value);
		}
	}

}

package org.vcell.sybil.util.collections;

/*   BoundedHashMap  --- by Oliver Ruebenacker, UCHC --- November 2007 to April 2008
 *   A HashMap with a set of listeners
 */

import java.util.HashMap;
import java.util.Map;

import org.vcell.sybil.util.collections.event.MapListenerHashSet;
import org.vcell.sybil.util.collections.event.MapListenerSet;

public class BoundedHashMap<K, V> extends HashMap<K, V> implements BoundedMap<K, V> {

	private static final long serialVersionUID = -4358732338269589914L;

	protected MapListenerSet<K, V> listeners;
	
	public BoundedHashMap() { listeners = new MapListenerHashSet<K, V>(this); }
	
	public MapListenerSet<K, V> listeners() { return listeners; }

	public void clear() { 
		super.clear(); 
		listeners.mapEventClear(this);
	}

	public V get(Object key) { return super.get(key); }

	public V put(K key, V value) { 
		V valueOld = super.put(key, value); 
		listeners.mapEventPut(this, key, valueOld, value);
		return valueOld;
	}

	public void putAll(Map<? extends K, ? extends V> map) { 
		putAll(map); 
		listeners.mapEventPutAll(this, map);
	}

	@SuppressWarnings("unchecked")
	public V remove(Object key) { 
		V value = super.remove(key); 
		listeners.mapEventRemove(this, (K) key, value);
		return value;
	}

}

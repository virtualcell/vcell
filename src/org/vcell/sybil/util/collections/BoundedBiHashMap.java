package org.vcell.sybil.util.collections;

/*   BoundedBiHashMap  --- by Oliver Ruebenacker, UCHC --- November 2007 to April 2008
 *   A BiMap with a set of listeners
 */

import java.util.Map;

import org.vcell.sybil.util.collections.event.MapListenerHashSet;
import org.vcell.sybil.util.collections.event.MapListenerSet;

public class BoundedBiHashMap<K, V> extends BiHashMap<K, V> implements BoundedMap<K, V> {

	private static final long serialVersionUID = 8598612755123403015L;

	protected MapListenerSet<K, V> listeners;
	
	public MapListenerSet<K, V> listeners() { return listeners; }

	public BoundedBiHashMap() { super(); listeners = new MapListenerHashSet<K, V>(this); }
	
	public BoundedBiHashMap(int initialCapacity) { 
		super(initialCapacity); 
		listeners = new MapListenerHashSet<K, V>(this); }

	public BoundedBiHashMap(int initialCapacity, float loadFactor) { 
		super(initialCapacity, loadFactor);
		listeners = new MapListenerHashSet<K, V>(this); 
	}
	
	public BoundedBiHashMap(Map<? extends K, ? extends V> map) {
		listeners = new MapListenerHashSet<K, V>(this); 
		putAll(map);
	}
		
	public void clear() { 
		super.clear(); 
		listeners.mapEventClear(this);
	}

	public V get(Object key) { return super.get(key); }

	public V put(K key, V value) { 
		K keyOld = super.getKey(value);
		V valueOld = super.put(key, value); 
		listeners.mapEventPut(this, keyOld, key, valueOld, value);
		return valueOld;
	}

	public void putAll(Map<? extends K, ? extends V> map) { 
		putAll(map); 
		listeners.mapEventPutAll(this, map);
	}

	@SuppressWarnings("unchecked")
	public V remove(Object key) {
		V value = super.remove(key); 
		if(value != null) { listeners.mapEventRemove(this, (K) key, value); }
		return value;			
	}
	
	public K removeValue(V value) {
		K key = super.removeValue(value);
		listeners.mapEventRemove(this, key, value);
		return key;
	}
	
}

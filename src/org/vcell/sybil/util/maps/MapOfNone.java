package org.vcell.sybil.util.maps;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.sbpax.util.sets.SetOfNone;

public class MapOfNone<K, V> implements Map<K, V> {

	protected void throwUnsupportedException() { throw new UnsupportedOperationException("Can not change constant map."); }
	
	public void clear() { throwUnsupportedException(); }

	public boolean containsKey(Object key) { return false; }
	public boolean containsValue(Object value) { return false; }
	public Set<Entry<K, V>> entrySet() { return new SetOfNone<Entry<K, V>>(); }
	public V get(Object key) { return null; }
	public boolean isEmpty() { return true; }
	public Set<K> keySet() { return new SetOfNone<K>(); }
	public V put(K key, V value) { throwUnsupportedException(); return null; }
	public void putAll(Map<? extends K, ? extends V> m) { throwUnsupportedException(); }
	public V remove(Object key) { throwUnsupportedException(); return null; }
	public int size() { return 0; }
	public Collection<V> values() { return new SetOfNone<V>(); }
	public int hashCode() { return 0; }
	
	public boolean equals(Object o) {
		if(o instanceof Map) { return ((Map<?, ?>) o).isEmpty(); }
		return false;
	}

}

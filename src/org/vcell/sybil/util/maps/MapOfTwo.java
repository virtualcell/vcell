/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.maps;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.sbpax.util.sets.SetOfTwo;
import org.vcell.sybil.util.JavaUtil;

public class MapOfTwo<K, V> implements Map<K, V> {

	protected void throwUnsupportedException() { throw new UnsupportedOperationException("Can not change constant map."); }

	protected final K key1, key2;
	protected final V value1, value2;
	protected final Entry<K, V> entry1, entry2;
	
	public MapOfTwo(K key1, V value1, K key2, V value2) { 
		this.key1 = key1; this.value1 = value1; this.entry1 = new SimpleMapEntry<K, V>(key1, value1); 
		this.key2 = key2; this.value2 = value2; this.entry2 = new SimpleMapEntry<K, V>(key2, value2); 
	}
	
	public void clear() { throwUnsupportedException(); }

	public boolean containsKey(Object key) { return JavaUtil.equals(key, key1) || JavaUtil.equals(key, key2); }
	public boolean containsValue(Object value) { return JavaUtil.equals(value, value1) || JavaUtil.equals(value, value2); }
	public Set<Entry<K, V>> entrySet() { return new SetOfTwo<Entry<K, V>>(entry1, entry2); }
	
	public V get(Object key) { 
		return JavaUtil.equals(key, key1) ? value1 : JavaUtil.equals(key, key2) ? value2 : null; 
	}
	
	public boolean isEmpty() { return false; }
	public Set<K> keySet() { return new SetOfTwo<K>(key1, key2); }
	public V put(K key, V value) { throwUnsupportedException(); return null; }
	public void putAll(Map<? extends K, ? extends V> m) { throwUnsupportedException(); }
	public V remove(Object key) { throwUnsupportedException(); return null; }
	public int size() { return 2; }
	public Collection<V> values() { return new SetOfTwo<V>(value1, value2); }
	public int hashCode() { return entry1.hashCode() + entry2.hashCode(); }
	
	public boolean equals(Object o) {
		if(o instanceof Map) { 
			return entrySet().equals(((Map<?, ?>) o).entrySet()); 
		}
		return false;
	}

}

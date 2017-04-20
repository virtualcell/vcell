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

import org.sbpax.util.sets.SetOfOne;
import org.vcell.sybil.util.JavaUtil;

public class MapOfOne<K, V> implements Map<K, V> {

	protected void throwUnsupportedException() { throw new UnsupportedOperationException("Can not change constant map."); }

	protected final K key;
	protected final V value;
	protected final Entry<K, V> entry;
	
	public MapOfOne(K key, V value) { this.key = key; this.value = value; this.entry = new SimpleMapEntry<K, V>(key, value); }
	
	public void clear() { throwUnsupportedException(); }

	public boolean containsKey(Object key) { return JavaUtil.equals(this.key, key); }
	public boolean containsValue(Object value) { return JavaUtil.equals(this.value, value); }
	public Set<Entry<K, V>> entrySet() { return new SetOfOne<Entry<K, V>>(entry); }
	public V get(Object key) { return JavaUtil.equals(key, this.key) ? value : null; }
	public boolean isEmpty() { return false; }
	public Set<K> keySet() { return new SetOfOne<K>(key); }
	public V put(K key, V value) { throwUnsupportedException(); return null; }
	public void putAll(Map<? extends K, ? extends V> m) { throwUnsupportedException(); }
	public V remove(Object key) { throwUnsupportedException(); return null; }
	public int size() { return 1; }
	public Collection<V> values() { return new SetOfOne<V>(value); }
	public int hashCode() { return entry.hashCode(); }
	
	public boolean equals(Object o) {
		if(o instanceof Map) { 
			return entrySet().equals(((Map<?, ?>) o).entrySet()); 
		}
		return false;
	}

}

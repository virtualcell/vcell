package org.vcell.sybil.util.collections;

/*   MultiHashMap  --- by Oliver Ruebenacker, UCHC --- July 2007
 *   Maps from a key to a set of values, using a HashMap of HashSets
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

	public boolean contains(K key, V value) {
		Set<V> set = get(key);
		if(set != null) { return set.contains(value); }
		else { return false; }
	}

	public Iterator<V> getAll(K key) {
		Set<V> set = get(key);
		if(set != null) { return set.iterator(); }
		else { return (new HashSet<V>()).iterator(); }
	}

	public void remove(K key, V value) {
		Set<V> set = get(key);
		if(set != null) { set.remove(value); }
	}

	public void removeAll(K key) { remove(key); }

}

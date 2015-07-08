package org.vcell.util.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * YAGNI implementation of Hash which maps lists
 * @author GWeatherby
 *
 * @param <K> key
 * @param <V> value
 */
@SuppressWarnings("serial")
public class HashListMap<K, V> implements Serializable {
	private final HashMap<K, List<V> > storage;
	/**
	 * create using {@link HashMap} and {@link ArrayList} implementation
	 */
	public HashListMap() {
		storage = new HashMap<>();
	}
	/**
	 * create deep copy
	 * @param source to copy from; not null
	 */
	public HashListMap(HashListMap<K,V> source) {
		this( );
		Objects.requireNonNull(source);
		for (K key : source.keySet()) {
			List<V> values = source.get(key);
			storage.put(key, new ArrayList<V> (values) );
		}
	}
	
	/**
	 * add value; creating backing list if necessary
	 * @param key; may be null
	 * @param value; may be null
	 */
	public void put(K key, V value) {
		if (storage.containsKey(key)) {
			storage.get(key).add(value);
			return;
		}
		ArrayList<V> list = new ArrayList<>();
		list.add(value);
		storage.put(key, list);
	}
	
	/**
	 * @param key; may be null
	 * @return unmodifiable list of keys; Collection will not be null but values may be
	 */
	public List<V> get(K key) {
		return Collections.unmodifiableList(storage.get(key));
	}
	
	/**
	 * @return true if no entries
	 */
	public boolean isEmpty( ) {
		return storage.isEmpty();
	}
	
	/**
	 * empties map
	 */
	public void clear( ) {
		storage.clear( );
	}
	
	/**
	 * @return number of Keys
	 */
	public int size( ) {
		return storage.size();
	}
	
	/**
	 * @return unmodifiable set of keys
	 */
	public Set<K> keySet( )  {
		return Collections.unmodifiableSet( storage.keySet() );
	}
	

}

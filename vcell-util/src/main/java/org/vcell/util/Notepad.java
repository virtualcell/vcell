package org.vcell.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * store auxiliary data without per object overhead 
 */
public class Notepad {
	
	private Map<WeakReference<Object>, List<Lookup> > storage = null; 
	
	/**
	 * remember specific type of data for object.
	 * subsequent calls with same key and clzz will overwrite existing data
	 * @param key not null
	 * @param clzz type of data
	 * @param data data for type
	 */
	public <T> void remember(Object key, Class<T> clzz, T data) {
		cleanup( );
		List<Lookup> lookups = listFor(key) ;
		for (Lookup lk : lookups) {
			if (lk.clzz == clzz) {
				lk.instance = data;
				return;
			}
		}
		lookups.add(new Lookup(data));
	}
	
	/**
	 * retrieve data, if available
	 * @param key not null
	 * @param clzz not null
	 * @return previously stored data or null
	 */
	@SuppressWarnings("unchecked")
	public <T> T recall(Object key, Class<T> clzz) {
		List<Lookup> lookups = listFor(key) ;
		for (Lookup lk : lookups) {
			if (lk.clzz == clzz) {
				return (T) lk.instance;
			}
		}
		
		return null;
	}
	
	private List<Lookup> listFor(Object key) {
		for (WeakReference<Object> wr  : storage.keySet()) {
			Object ref = wr.get( );
			if (ref == key) {
				return storage.get(wr);
			}
		}
		List<Lookup> newList = new ArrayList<>();
		storage.put(new WeakReference<Object>(key), newList);
		return newList;
	}
	

	
	
	private void cleanup( )  {
		if (storage == null) {
			storage = new HashMap<>();
		}
		List<WeakReference<Object>> dead = null;
		for (WeakReference<Object> wr : storage.keySet()) {
			if (wr.get( ) == null) {
				if (dead == null) {
					dead = new ArrayList<>();
				}
				dead.add(wr);
			}
		}
		if (dead != null) {
			for (WeakReference<Object> wr : dead) {
				storage.remove(wr);
			}
		}
	}
	
	private static class Lookup  {
		final Class<?> clzz;
		Object instance;
		public Lookup(Object instance) {
			super();
			this.instance = instance;
			clzz = instance.getClass();
		}
		@Override
		public int hashCode() {
			return instance.hashCode() ^ clzz.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Lookup other = (Lookup) obj;
			return other.clzz == clzz && other.instance == instance;
		}
	}
	

}

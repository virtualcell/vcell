package org.vcell.util.collections;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * List with maximum capacity; if exceeded, oldest elements silently dropped
 * @param <E>
 */
public class CircularList<E> extends AbstractCollection<E> {
	private final int cap;
	private final LinkedList<E> storage;
	

	public CircularList(int capacity) {
		super();
		this.cap = capacity;
		storage = new LinkedList<>( );
	}
	
	public int capacity() {
		return cap;
	}


	@Override
	public boolean add(E e) {
		storage.add(e);
		while (storage.size() > capacity()) {
			storage.pop();
		}
		return true;
	}

	@Override
	public Iterator<E> iterator() {
		return storage.iterator(); 
	}

	@Override
	public int size() {
		return storage.size( );
	}
}

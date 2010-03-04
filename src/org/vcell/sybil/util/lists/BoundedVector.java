package org.vcell.sybil.util.lists;

/*   BoundedVector  --- by Oliver Ruebenacker, UCHC --- June to July 2008
 *   A vector with change listeners
 */

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.event.ListDataListener;

import org.vcell.sybil.util.iterators.BoundedListIterator;

public class BoundedVector<E> extends Vector<E> implements BoundedList<E> {

	private static final long serialVersionUID = -1908982296214205104L;
	protected Set<ListDataListener> listeners = new HashSet<ListDataListener>();
	
	public BoundedVector() { super(); }
	public BoundedVector(Collection<? extends E> collection) { super(collection); }
	public BoundedVector(int capacity) { super(capacity); }
	public BoundedVector(int capacity, int capacityStep) { super(capacity, capacityStep); }
	
	public Set<ListDataListener> listeners() { return listeners; }

	public boolean add(E e) {
		int sizeOld = size();
		super.add(e);
		fireIntervalAdded(sizeOld, sizeOld);
		return true;
	}

	public void add(int ind, E e) {
		super.add(ind, e);
		fireIntervalAdded(ind, ind);
	}

	public boolean addAll(Collection<? extends E> c) {
		int sizeOld = size();
		int num = c.size();
		boolean hasChanged = super.addAll(c);
		if(hasChanged) { fireIntervalAdded(sizeOld, sizeOld + num - 1); }
		return hasChanged;
	}

	public boolean addAll(int ind, Collection<? extends E> c) {
		boolean hasChanged = super.addAll(ind, c);
		if(hasChanged) { fireIntervalAdded(ind, ind + c.size() -1); }
		return hasChanged;
	}

	public void clear() {
		int sizeOld = size();
		super.clear();
		fireIntervalRemoved(0, sizeOld - 1);
	}

	public Iterator<E> iterator() { return new BoundedListIterator<E>(this, super.listIterator()); }

	public ListIterator<E> listIterator() { 
		return new BoundedListIterator<E>(this, super.listIterator()); 
	}

	public ListIterator<E> listIterator(int index) {
		return new BoundedListIterator<E>(this, super.listIterator(index)); 
	}

	public boolean remove(Object o) {
		boolean hasChanged = super.remove(o);
		if(hasChanged) { fireContentsChanged(0, size()); }
		return hasChanged;
	}

	public E remove(int ind) {
		E element = super.remove(ind);
		fireIntervalRemoved(ind, ind);
		return element;
	}

	public boolean removeAll(Collection<?> c) {
		int sizeOld = size();
		boolean hasChanged = super.removeAll(c);
		if(hasChanged) { fireContentsChanged(0, sizeOld - 1); }
		return hasChanged;
	}

	public boolean retainAll(Collection<?> c) {
		int sizeOld = size();
		boolean hasChanged = super.retainAll(c);
		if(hasChanged) { fireContentsChanged(0, sizeOld - 1); }
		return hasChanged;
	}

	public E set(int ind, E eNew) {
		E eOld = super.set(ind, eNew);
		fireContentsChanged(ind, ind);
		return eOld;
	}

	public void removeRange(int ind1, int ind2) {
		if(ind1 < ind2) {
			super.removeRange(ind1, ind2);
			fireIntervalRemoved(ind1, ind2 - 1);			
		}
	}
	
	public void setSize(int sizeNew) { super.setSize(sizeNew); }
	public void setElementAt(E e, int ind) { set(ind, e); }
	public void removeElementAt(int ind) { remove(ind); }
	public void insertElementAt(E e, int ind) { add(ind, e); }
	public void addElement(E e) { add(e); }
	public boolean removeElement(Object o) { return remove(o); }
	public void removeAllElements() { clear(); }
	public Object clone() { return new BoundedVector<E>(this); }
		
	public List<E> subList(int ind1, int ind2) { 
		return new BoundedSubList<E>(this, subList(ind1, ind2), ind1 < ind2 ? ind1 : ind2); 
	}
	
	public void fire() { 
		Event<E> event = new Event<E>(this, Event.CONTENTS_CHANGED, 0, size() + 1);
		for(ListDataListener listener : listeners) { listener.contentsChanged(event); }	
	}
	
	public void fireContentsChanged(int ind1, int ind2) { 
		Event<E> event = new Event<E>(this, Event.CONTENTS_CHANGED, ind1, ind2);
		for(ListDataListener listener : listeners) { listener.contentsChanged(event); }	
	}
	
	public void fireIntervalAdded(int ind1, int ind2) { 
		Event<E> event = new Event<E>(this, Event.INTERVAL_ADDED, ind1, ind2);
		for(ListDataListener listener : listeners) { listener.intervalAdded(event); }	
	}
	
	public void fireIntervalRemoved(int ind1, int ind2) { 
		Event<E> event = new Event<E>(this, Event.INTERVAL_REMOVED, ind1, ind2);
		for(ListDataListener listener : listeners) { listener.intervalRemoved(event); }	
	}
	
}

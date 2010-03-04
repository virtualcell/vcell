package org.vcell.sybil.util.lists;

/*   BoundedSubList  --- by Oliver Ruebenacker, UCHC --- June 2008
 *   Sub list of a bounded vector
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.event.ListDataListener;

import org.vcell.sybil.util.iterators.BoundedListIterator;

public class BoundedSubList<E> implements BoundedList<E> {

	protected BoundedList<E> list;
	protected List<E> sublist;
	protected int offset;
	
	public BoundedSubList(BoundedList<E> listNew, List<E> sublistNew, int offsetNew) {
		list = listNew;
		sublist = sublistNew;
		offset = offsetNew;
	}
	
	public void fireContentsChanged(int ind1, int ind2) { 
		list.fireContentsChanged(ind1 + offset, ind2 + offset); 
	}
	
	public void fireIntervalAdded(int ind1, int ind2) { 
		list.fireIntervalAdded(ind1 + offset, ind2 + offset); 
	}
	
	public void fireIntervalRemoved(int ind1, int ind2) { 
		list.fireIntervalRemoved(ind1 + offset, ind2 + offset); 
	}

	public Set<ListDataListener> listeners() { return list.listeners(); }

	public boolean add(E e) { 
		int ind = sublist.size();
		sublist.add(e);
		fireIntervalAdded(ind, ind);
		return true; 
	}

	public void add(int ind, E e) { 
		sublist.add(ind, e); 
		fireIntervalAdded(ind, ind);
	}

	public boolean addAll(Collection<? extends E> c) {
		int num = c.size();
		boolean hasChanged = sublist.addAll(c);
		if(hasChanged) { fireIntervalAdded(0, num - 1); }
		return hasChanged;
	}

	public boolean addAll(int ind, Collection<? extends E> c) {
		int num = c.size();
		boolean hasChanged = sublist.addAll(ind, c);
		if(hasChanged) { fireIntervalAdded(ind, ind + num - 1); }
		return hasChanged;
	}

	public void clear() {
		int size = sublist.size();
		sublist.clear(); 
		fireIntervalRemoved(0, size - 1); 
	}
	
	public boolean contains(Object o) { return sublist.contains(o); }
	public boolean containsAll(Collection<?> c) { return sublist.containsAll(c); }
	public E get(int ind) { return sublist.get(ind); }
	public int indexOf(Object o) { return sublist.indexOf(o); }
	public boolean isEmpty() { return sublist.isEmpty(); }
	public Iterator<E> iterator() { return new BoundedListIterator<E>(list, sublist.listIterator()); }
	public int lastIndexOf(Object o) { return sublist.lastIndexOf(o); }

	public ListIterator<E> listIterator() {
		return new BoundedListIterator<E>(list, sublist.listIterator());
	}

	public ListIterator<E> listIterator(int ind) {
		return new BoundedListIterator<E>(list, sublist.listIterator(ind));
	}

	public boolean remove(Object o) { 
		boolean hasChanged = sublist.remove(o);
		if(hasChanged) { fireContentsChanged(0, sublist.size()); }
		return hasChanged; 
	}
	
	public E remove(int ind) { 
		E eOld = sublist.remove(ind);
		fireIntervalRemoved(ind, ind);
		return eOld;  
	}

	public boolean removeAll(Collection<?> c) {
		int sizeOld = sublist.size();
		boolean hasChanged = sublist.removeAll(c);
		if(hasChanged) { fireContentsChanged(0, sizeOld - 1); }
		return hasChanged;
	}

	public boolean retainAll(Collection<?> c) {
		int sizeOld = sublist.size();
		boolean hasChanged = sublist.retainAll(c);
		if(hasChanged) { fireContentsChanged(0, sizeOld - 1); }
		return hasChanged;
	}

	public E set(int ind, E e) {
		E eOld = sublist.set(ind, e);
		if(e != eOld) { fireContentsChanged(ind, ind); }
		return eOld;
	}

	public int size() { return sublist.size(); }

	public List<E> subList(int ind1, int ind2) {
		return new BoundedSubList<E>(list, sublist.subList(ind1, ind2), ind1 < ind2 ? ind1 : ind2);
	}

	public Object[] toArray() { return sublist.toArray(); }
	public <T> T[] toArray(T[] a) { return sublist.toArray(a); }

}

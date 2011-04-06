package org.vcell.sybil.util.sets;

/*   BoundedHashSet  --- by Oliver Ruebenacker, UCHC --- June 2008
 *   A set with listeners for changes
 */

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.vcell.sybil.util.iterators.BoundedSetIterator;

public class BoundedHashSet<E> extends HashSet<E> implements BoundedSet<E> {

	private static final long serialVersionUID = 3466348312372800350L;

	protected Set<BoundedSet.Listener<E>> listeners = new HashSet<BoundedSet.Listener<E>>();
	
	public Set<Listener<E>> listeners() { return listeners; }

	@Override
	public boolean add(E element) {
		boolean hasChanged = super.add(element);
		if(hasChanged) {
			EventAdd<E> event = new EventAdd<E>(this, element);
			for(Listener<E> listener : listeners) { listener.fireEvent(event); }			
		}
		return hasChanged;
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		boolean hasChanged = super.addAll(collection);
		if(hasChanged) {
			EventAddAll<E> event = new EventAddAll<E>(this, collection);
			for(Listener<E> listener : listeners) { listener.fireEvent(event); }		
		}
		return hasChanged;
	}

	@Override
	public void clear() {
		super.clear();
		EventClear<E> event = new EventClear<E>(this);
		for(Listener<E> listener : listeners) { listener.fireEvent(event); }				
	}

	@Override
	public Iterator<E> iterator() { return new BoundedSetIterator<E>(this, super.iterator()); }

	@Override
	public boolean remove(Object object) {
		boolean hasChanged = super.remove(object);
		if(hasChanged) {
			EventRemove<E> event = new EventRemove<E>(this, object);
			for(Listener<E> listener : listeners) { listener.fireEvent(event); }
		}
		return hasChanged;
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		boolean hasChanged = super.removeAll(collection);
		if(hasChanged) {
			EventRemoveAll<E> event = new EventRemoveAll<E>(this, collection);
			for(Listener<E> listener : listeners) { listener.fireEvent(event); }
		}
		return hasChanged;
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		boolean hasChanged = super.retainAll(collection);
		if(hasChanged) {
			EventRetainAll<E> event = new EventRetainAll<E>(this, collection);
			for(Listener<E> listener : listeners) { listener.fireEvent(event); }
		}
		return hasChanged;
	}
	
}

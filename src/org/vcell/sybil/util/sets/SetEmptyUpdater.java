package org.vcell.sybil.util.sets;

/*   SetEmptyUpdater  --- by Oliver Ruebenacker, UCHC --- June 2008 to November 2009
 *   Fires events when a bounded set begins or ceases to be empty
 */

import java.util.HashSet;
import java.util.Set;

import org.vcell.sybil.util.sets.BoundedSet.Event;
import org.vcell.sybil.util.sets.BoundedSet.EventPlus;
import org.vcell.sybil.util.sets.BoundedSet.EventMinus;
import org.vcell.sybil.util.sets.BoundedSet.Listener;

public class SetEmptyUpdater<E> implements Listener<E> {

	public static interface Listener<E> {
		public void eventBecameEmpty(BoundedSet<E> set);
		public void eventBecameNotEmpty(BoundedSet<E> set);
	}
	
	protected boolean hasBeenEmpty;
	protected Set<Listener<E>> listeners = new HashSet<Listener<E>>();
	
	public SetEmptyUpdater(BoundedSet<E> set) {
		this(set.isEmpty());
		set.listeners().add(this);
	}
	
	public SetEmptyUpdater(boolean hasBeenEmptyNew) {
		hasBeenEmpty = hasBeenEmptyNew;
	}
	
	public Set<Listener<E>> listeners() { return listeners; }
	public boolean hasBeenEmpty() { return hasBeenEmpty; }

	protected void maybeEmpty(BoundedSet<E> set) {
		if(!hasBeenEmpty && set.isEmpty()) {
			for(Listener<E> listener : listeners) { listener.eventBecameEmpty(set); }
			hasBeenEmpty = true;
		}
	}
	
	protected void maybeNotEmpty(BoundedSet<E> set) {
		if(hasBeenEmpty && !set.isEmpty()) {
			for(Listener<E> listener : listeners) { listener.eventBecameNotEmpty(set); }
			hasBeenEmpty = false;
		}
	}
	
	public void fireEvent(Event<E> event) {
		if(event instanceof EventMinus<?>) { maybeEmpty(event.set()); }
		if(event instanceof EventPlus<?>) { maybeNotEmpty(event.set()); }
	}

}

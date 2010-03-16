package org.vcell.sybil.util.event;

/*   Listener  --- by Oliver Ruebenacker, UCHC --- November 2007 to March 2010
 *   A listener to a general purpose event
 */

import java.util.HashSet;

public class ListenerHashSet<V> extends 
HashSet<Listener<V>> implements ListenerSet<V> {

	private static final long serialVersionUID = 2126881593370641035L;

	protected Bounded<V> bounded;
	
	public ListenerHashSet(Bounded<V> bounded) { this.bounded = bounded; }
	
	public boolean add(Listener<V> listener) {
		boolean result = super.add(listener);
		listener.setValue(bounded.value());
		return result;
	}
	
	public void setValue(V value) {
		for(Listener<V> listener : this) { listener.setValue(value); }
	}

}

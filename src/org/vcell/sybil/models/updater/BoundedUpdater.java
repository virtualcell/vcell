package org.vcell.sybil.models.updater;

/*   Bounded  --- by Oliver Ruebenacker, UCHC --- November 2007
 *   An updater setting a Bounded whenever receiving an event form another Bounded
 */

import org.vcell.sybil.util.event.Bounded;
import org.vcell.sybil.util.event.Listener;

public class BoundedUpdater<V> implements Listener<V> {
	protected Bounded<V> bounded;
	public BoundedUpdater(Bounded<V> bounded) { this.bounded = bounded; }
	
	public void setValue(V value) { bounded.setValue(value); }	
}

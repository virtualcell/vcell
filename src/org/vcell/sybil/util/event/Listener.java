package org.vcell.sybil.util.event;

/*   Listener  --- by Oliver Ruebenacker, UCHC --- November 2007
 *   A listener to a general purpose event
 */


public interface Listener<V> {

	public void setValue(V value);
	
}

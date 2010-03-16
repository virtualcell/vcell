package org.vcell.sybil.util.event;

/*   ListenerSet  --- by Oliver Ruebenacker, UCHC --- November 2007 to March 2010
 *   A set of listeners to a general purpose event to be notified at once
 */


import java.util.Set;

public interface ListenerSet<V> 
extends Set<Listener<V>>, Listener<V> {

}

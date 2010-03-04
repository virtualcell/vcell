package org.vcell.sybil.util.collections.event;

/*   ListenerSet  --- by Oliver Ruebenacker, UCHC --- November 2007 to April 2008
 *   A set of listeners to a general purpose event to be notified at once
 */

import java.util.Set;

public interface MapListenerSet<K, V> extends Set<MapListener<K, V>>, MapListener<K, V> {

}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.collections.event;

/*   ListenerSet  --- by Oliver Ruebenacker, UCHC --- November 2007 to April 2008
 *   A set of listeners to a general purpose event to be notified at once
 */

import java.util.Set;

public interface MapListenerSet<K, V> extends Set<MapListener<K, V>>, MapListener<K, V> {

}

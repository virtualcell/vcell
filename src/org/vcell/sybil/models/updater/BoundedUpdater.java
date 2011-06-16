/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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

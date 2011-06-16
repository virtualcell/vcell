/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

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
	
	@Override
	public boolean add(Listener<V> listener) {
		boolean result = super.add(listener);
		listener.setValue(bounded.value());
		return result;
	}
	
	public void setValue(V value) {
		for(Listener<V> listener : this) { listener.setValue(value); }
	}

}

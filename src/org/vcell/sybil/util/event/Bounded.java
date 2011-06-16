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

/*   Bounded  --- by Oliver Ruebenacker, UCHC --- November 2007
 *   A value and a set of listeners to be notified when the value changes 
 */


public class Bounded<V> {
	
	protected V value;
	protected ListenerSet<V> listeners;
	
	public Bounded() { listeners = new ListenerHashSet<V>(this); };
	public Bounded(V value) { this.value = value; listeners = 
		new ListenerHashSet<V>(this); }

	public V setValue(V valueNew) { 
		value = valueNew;
		listeners.setValue(valueNew);
		return valueNew;
	}

	public V value() { return value; }

	public ListenerSet<V> listeners() { return listeners; }

}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.graphlayout.shuffler;

/*   RepetitionTimer  --- by Oliver Ruebenacker, UCHC --- February 2009
 *   Sorts graph elements on a tiling
 */

public class RepetitionTimer {

	protected long min;
	protected long current;
	protected long lifetime;
	protected long timer;
	
	public RepetitionTimer(long minNew, long lifetimeNew) {
		min = minNew;
		current = min;
		lifetime = lifetimeNew;
		setTimer();
	}
	
	public long current() { return current; }

	public long adjustCurrent() {
		if(System.currentTimeMillis() - timer < lifetime) { current = current + current / 2; } 
		else { current = min + current / 2; }
		return current;
	}
	
	public void setTimer() { timer = System.currentTimeMillis(); }
	
}

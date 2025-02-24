/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

/**
 * 
 */
package org.vcell.util;

import java.io.Serializable;

public class Pair <One, Two> implements Serializable {
	public final One one;
	public final Two two;

	public Pair(One one, Two two) {
		this.one = one;
		this.two = two;
	}

	public String toString() {
		return "<" + (this.one == null ? "null" : this.one.toString()) + ", " +
				(this.two == null ? "null" : this.two.toString()) + ">";
	}

	public int hashCode() {
		int h = 13;
		h += h *37 + (this.one == null ? 0 : this.one.hashCode());
		h += h *37 + (this.two == null ? 0 : this.two.hashCode());
		return h;
	}

	public boolean equals( Object o ) {
		if( this == o ) return true;
		if( null == o ) return false;
		if(!(o instanceof Pair<?, ?> otherPair)) return false; // each individual member's equal will handle the generic types
		return this.equals(otherPair);
	}

	public boolean equals(Pair <?, ?> o) {
		return this.one.equals(o.one) && this.two.equals(o.two);
	}

}

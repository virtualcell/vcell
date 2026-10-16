/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util;

/**
 * VCell-specific interface designed to compare objects that are not meant to equal,
 * but are meant to be "matched" up regardless.
 */
public interface Matchable {
	/**
	 * Object specific method for checking the internal representation of objects
	 * not intended for keys from database
	 *
	 * @param matchable Matchable non-null matchable to test
	 * @return boolean
	 * @see Object#equals(Object)
	 */
	boolean compareEqual(Matchable matchable);

	/**
	 * null safe, static variant of compareEqual
	 *
	 * @param leftHandSide first object to test
	 * @param rightHandSide Matchable second object to test
	 * @return true if leftHandSide and rightHandSide both null, or leftHandSide.compareEqual(rightHandSide)
	 * @see Matchable#compareEqual(Matchable)
	 */
	static boolean areEqual(Matchable leftHandSide, Matchable rightHandSide) {
		if (null == leftHandSide) return null == rightHandSide;
		return leftHandSide.compareEqual(rightHandSide);
	}
}

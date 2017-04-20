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
 * This type was created in VisualAge.
 */
public interface Matchable {
/**
 * Checks for internal representation of objects, not keys from database<br>
 * @see {@link Object#equals(Object)}  
 * @return boolean
 * @param obj java.lang.Object not null
 */
boolean compareEqual(Matchable obj);

/**
 * null safe compare
 * @param lhs
 * @param rhs
 * @return true if lhs and rhs both null, or lhs.compareEqual(rhs)
 */
public static boolean areEqual(Matchable lhs, Matchable rhs) {
	if (lhs != null) {
		return lhs.compareEqual(rhs);
	}
	return rhs == null;
}
}

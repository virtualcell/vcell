/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.category;

/*   SimpleCat  --- by Oliver Ruebenacker, UCHC --- October 2008
 *   A simple implementation for compareTo() using rank().
 */

public abstract class SimpleCategory<C extends Category<C>> implements Category<C> {

	public abstract Class<?> c();
	public abstract int rank();
	public int compareTo(C cat) { return rank() - cat.rank(); }

}

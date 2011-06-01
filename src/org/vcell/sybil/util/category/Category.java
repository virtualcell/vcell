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

/*   Category  --- by Oliver Ruebenacker, UCHC --- October 2008
 *   An interface for categories. Categories are equal if the class and the parameters are the same
 */

public interface Category<C extends Category<C>> extends Comparable<C> {
	
	public Class<?> c();
	public int rank();

}

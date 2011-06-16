/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.collections;

/*   BiMap  --- by Oliver Ruebenacker, UCHC --- November 2007
 *   A bijective Map. Not only keys are unique, but values are unique, too. The method teg() is a reverse get,
 *   to obtain a key given a value. Note that trying to add a value that duplicates a present
 *   value may be undefined, or remove the the present key-value pair. When adding entire
 *   collections with duplicate values, this may be undefined or one key-value pair may be picked arbitrarily.
 */

import java.util.Map;

public interface BiMap<A, B> extends Map<A, B> {

	public A getKey(B b);
	public A removeValue(B b);
	
}

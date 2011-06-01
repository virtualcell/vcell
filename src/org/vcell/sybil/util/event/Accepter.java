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

/*   Acceptor  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Accept objects of some class
 */

public interface Accepter<T> {

	public void accept(T t);
	
}

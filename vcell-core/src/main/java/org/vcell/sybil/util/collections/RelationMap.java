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

/*   Relation  --- by Oliver Ruebenacker, UCHC --- January 2009
 *   A relation (set of pairs)
 */

import java.util.Map;
import java.util.Set;

public interface RelationMap<A, B> {

	public void clear();
	public void add(A a, B b);
	public void remove(A a, B b);
	public void removeA(A a);
	public void removeB(B b);
	public Set<A> aSet();
	public Set<B> bSet();
	public boolean contains(A a, B b);
	public boolean containsA(A a);
	public boolean containsB(B b);
	public Set<B> getBSet(A a);
	public Set<A> getASet(B b);
	public Map<A, Set<B>> a2bMap();
	public Map<B, Set<A>> b2aMap();
	
}

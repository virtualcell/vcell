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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sbpax.util.sets.SetOfNone;

public class RelationHashMap<A, B> implements RelationMap<A, B> {

	protected Map<A, Set<B>> a2bMap = new HashMap<A, Set<B>>();
	protected Map<B, Set<A>> b2aMap = new HashMap<B, Set<A>>();
	
	public void clear() {
		a2bMap.clear();
		b2aMap.clear();
	}
	
	public void add(A a, B b) {
		Set<B> bSet = a2bMap.get(a);
		if(bSet == null) { a2bMap.put(a, bSet = new HashSet<B>()); }
		bSet.add(b);
		Set<A> aSet = b2aMap.get(b);
		if(aSet == null) { b2aMap.put(b, aSet = new HashSet<A>()); }
		aSet.add(a);
	}

	public void remove(A a, B b) {
		Set<B> bSet = a2bMap.get(a);
		if(bSet != null) {
			bSet.remove(b);
			if(bSet.isEmpty()) { a2bMap.remove(a); }
		}
		Set<A> aSet = b2aMap.get(b);
		if(aSet != null) {
			aSet.remove(a);
			if(aSet.isEmpty()) { b2aMap.remove(a); }
		}
	}
	
	public void removeA(A a) {
		Set<B> bSet = a2bMap.get(a);
		if(bSet != null) {
			for(B b : bSet) {
				Set<A> aSet = b2aMap.get(b);
				if(aSet != null) {
					aSet.remove(a);
					if(aSet.isEmpty()) { b2aMap.remove(b); }
				}
			}
			a2bMap.remove(a);
		}
	}

	public void removeB(B b) {
		Set<A> aSet = b2aMap.get(b);
		if(aSet != null) {
			for(A a : aSet) {
				Set<B> bSet = a2bMap.get(a);
				if(bSet != null) {
					bSet.remove(b);
					if(bSet.isEmpty()) { a2bMap.remove(a); }
				}
			}
			b2aMap.remove(b);
		}
	}

	public Set<A> aSet() { return a2bMap.keySet(); }
	public Set<B> bSet() { return b2aMap.keySet(); }
	
	public boolean contains(A a, B b) {
		Set<B> bSet = a2bMap.get(a);
		if(bSet != null) { return bSet.contains(b); }
		return false;
	}

	public Set<B> getBSet(A a) { 
		Set<B> bSet = a2bMap.get(a);
		return bSet != null ? bSet : new SetOfNone<B>(); 
	}
	
	public Set<A> getASet(B b) { 
		Set<A> aSet = b2aMap.get(b);
		return aSet != null ? aSet : new SetOfNone<A>(); 
	}
	
	public boolean containsA(A a) { return a2bMap.containsKey(a); }
	public boolean containsB(B b) { return b2aMap.containsKey(b); }
	public Map<A, Set<B>> a2bMap() { return a2bMap; }
	public Map<B, Set<A>> b2aMap() { return b2aMap; }

}

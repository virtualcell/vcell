/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.sbo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class SBOSetUnion implements SBOSet {

	protected final Set<SBOSet> sets = new HashSet<SBOSet>();
	
	public SBOSetUnion(SBOSet set, SBOSet ...sets) {
		this.sets.add(set);
		this.sets.addAll(Arrays.asList(sets));
	}
	
	public Set<SBOSet> getSets() { return sets; }
	
	public boolean includes(SBOTerm term) {
		for(SBOSet set : sets) {
			if(set.includes(term)) { return true; }
		}
		return false;
	}

}

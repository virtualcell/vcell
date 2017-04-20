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

import java.util.Set;


public class SBOBranch implements SBOSet {

	public final SBOTerm term;
	public final Set<SBOTerm> descendents;
	
	public SBOBranch(SBOTerm term) {
		this.term = term;
		descendents = SBOUtil.getAllDescendents(term);
	}

	public SBOTerm getTerm() { return term; }
	public Set<SBOTerm> getDescendents() { return descendents; }
		
	public boolean includes(SBOTerm term) { return descendents.contains(term); }
	
}

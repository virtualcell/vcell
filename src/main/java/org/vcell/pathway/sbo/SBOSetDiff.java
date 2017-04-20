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


public class SBOSetDiff implements SBOSet {

	protected final SBOSet set1, set2;
	
	public SBOSetDiff(SBOSet set1, SBOSet set2) {
		this.set1 = set1;
		this.set2 = set2;
	}
	
	public SBOSet getSBOSet1() { return set1; }
	public SBOSet getSBOSet2() { return set2; }
	
	public boolean includes(SBOTerm term) {
		return set1.includes(term) && !set2.includes(term);
	}

}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.kinetics;

import java.util.Arrays;
import java.util.List;



public interface KineticLawBuilder {
	
	public static enum Match { NONE, SOME, MOSTLY, PERFECT;
		public boolean isBetterThan(Match s) { return ordinal() > s.ordinal(); }
	}
	
	public Match getMatch(KineticContext context);
	public void addKinetics(KineticContext context);
	
	public static final KineticLawBuilder HMM_IRREV_BUILDER = new SBPAXHMMIrrevLawBuilder();
	public static final KineticLawBuilder HMM_REV_BUILDER = new SBPAXHMMRevLawBuilder();
	
	public static final List<KineticLawBuilder> DEFAULT_LIST = 
		Arrays.<KineticLawBuilder>asList(HMM_IRREV_BUILDER, HMM_REV_BUILDER);
	
	
}
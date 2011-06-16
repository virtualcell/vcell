/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf.reason;

/*   SYBREAM  --- by Oliver Ruebenacker, UCHC --- June 2008 to March 2010
 *   Factory for various versions of the
 *   System Biological Reasoning Engine for Analysis and Modeling
 */

import java.util.List;

import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;

public class SYBREAMFactory {

	static GenericRuleReasoner defaultSYBREAM;
	
	public static GenericRuleReasoner defaultGenericRuleReasoner(List<Rule> rules) {
		GenericRuleReasoner reasoner = new GenericRuleReasoner(rules);
		reasoner.setMode(GenericRuleReasoner.FORWARD_RETE);
		return reasoner;
	}
	
	public static GenericRuleReasoner defaultSYBREAM() {
		if(defaultSYBREAM == null) { defaultSYBREAM = defaultGenericRuleReasoner(SYBREAMRules.rules); }
		return defaultSYBREAM;
	}
	
}

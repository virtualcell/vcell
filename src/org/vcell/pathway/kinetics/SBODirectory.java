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

import java.util.HashMap;
import java.util.Map;

public class SBODirectory {

	protected static Map<String, SBOTerm> idToTerm = new HashMap<String, SBOTerm>();
	
	public static SBOTerm createTerm(String id, String symbol, String name, String description) {
		SBOTerm sboTerm = new SBOTerm(id, symbol, name, description);
		idToTerm.put(id, sboTerm);
		return sboTerm;
	}
	
	public static SBOTerm getTermFromID(String id) { return idToTerm.get(id); }
	
	public static final SBOTerm sbo0000322 = createTerm("0000322", "Kms", "Michaelis constant for substrate",
			"Substrate concentration at which the velocity of product production by the forward activity of a reversible enzyme " + 
			"is half its maximum. ");
	
}

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

import java.util.HashMap;
import java.util.Map;


public class SBOList {

	protected static Map<String, SBOTerm> indexToTerm = new HashMap<String, SBOTerm>();
	
	public static SBOTerm createTerm(String index, String symbol, String name, String description) {
		SBOTerm sboTerm = SBOUtil.createSBOTermFromIndex(index, symbol, name, description);
		indexToTerm.put(index, sboTerm);
		return sboTerm;
	}
	
	public static SBOTerm getTermFromIndex(String index) { return indexToTerm.get(index); }
	
	public static final SBOTerm sbo0000027 = createTerm("0000027", "Km", "Michaelis constant",
			"Substrate concentration at which the velocity of reaction is half its maximum. Michaelis constant is an experimental " + 
			"parameter. According to the underlying molecular mechanism it can be interpreted differently in terms of microscopic " + 
			"constants.");
	public static final SBOTerm sbo0000186 = createTerm("0000186", "Vmax", "maximal velocity",
			"Limiting maximal velocity of an enzymatic reaction, reached when the substrate is in large excess and all the enzyme " + 
			"is complexed.");
	public static final SBOTerm sbo0000322 = createTerm("0000322", "Kms", "Michaelis constant for substrate",
			"Substrate concentration at which the velocity of product production by the forward activity of a reversible enzyme " + 
			"is half its maximum.");
	
}

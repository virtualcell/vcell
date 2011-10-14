/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.uome.core;

import java.util.HashSet;
import java.util.Set;

public class UOMEUnit {

	protected Set<String> names = new HashSet<String>();
	protected Set<String> symbols = new HashSet<String>();
	protected Set<UOMEVocabulary> terms = new HashSet<UOMEVocabulary>();
	protected Set<UOMEExpression> expressions = new HashSet<UOMEExpression>();
	
	public Set<String> getNames() { return names; }
	public Set<String> getSymbols() { return symbols; }
	public Set<UOMEVocabulary> getTerms() { return terms; }
	public Set<UOMEExpression> getExpressions() { return expressions;}
	
}

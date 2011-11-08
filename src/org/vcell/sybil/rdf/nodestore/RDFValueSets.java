/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf.nodestore;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Value;

public class RDFValueSets extends RDFNodeBasicSets {
	
	protected final Set<Value> values = new HashSet<Value>();

	public RDFValueSets(Set<Value> values) { this.values.addAll(values); }
	
	public Set<Value> getValues() { return values; }
	
}
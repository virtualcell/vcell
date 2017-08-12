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

import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

public interface RDFNodeSets {
		
	public Set<Value> getValues();
	public Value getValue();
	public Set<Resource> getResources();
	public Resource getResource();
	public Set<URI> getURIs();
	public URI getURI();
	public Set<BNode> getBNodes();
	public BNode getBNode();
	public Set<Literal> getLiterals();
	public Literal getLiteral();
	public Set<String> getStrings();
	public String getString();
	public Set<Double> getDoubles();
	public Double getDouble();
	public double getDoubleValue();
	
}
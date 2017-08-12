/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf.pool;

import java.util.Map;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.sbpax.util.sets.SetUtil;
import org.vcell.sybil.rdf.nodestore.RDFNodeSets;

public class RDFObjectSets<T> implements RDFNodeSets {

	protected final RDFNodeSets nodeSets;
	protected final Set<T> objects;
	protected final Map<Resource, Set<Resource>> unsupported;
	
	public RDFObjectSets(RDFNodeSets nodeSets, Set<T> objects, 
			Map<Resource, Set<Resource>> unsupported) {
		this.nodeSets = nodeSets;
		this.objects = objects;
		this.unsupported = unsupported;
	}
	
	public Set<Value> getValues() { return nodeSets.getValues(); }
	public Value getValue() { return nodeSets.getValue(); }
	public Set<Resource> getResources() { return nodeSets.getResources(); }
	public Resource getResource() { return nodeSets.getResource(); }
	public Set<URI> getURIs() { return nodeSets.getURIs(); }
	public URI getURI() { return nodeSets.getURI(); }
	public Set<BNode> getBNodes() { return nodeSets.getBNodes(); }
	public BNode getBNode() { return nodeSets.getBNode(); }
	public Set<Literal> getLiterals() { return nodeSets.getLiterals(); }
	public Literal getLiteral() { return nodeSets.getLiteral(); }
	public Set<String> getStrings() { return nodeSets.getStrings(); }
	public String getString() { return nodeSets.getString(); }
	public Set<Double> getDoubles() { return nodeSets.getDoubles(); }
	public Double getDouble() { return nodeSets.getDouble(); }
	public double getDoubleValue() { return nodeSets.getDoubleValue(); }
	public Set<T> getObjects() { return objects; }
	public T getObject() { return SetUtil.pickAny(objects); }
	public Map<Resource, Set<Resource>> getUnsupported() { return unsupported; }


}

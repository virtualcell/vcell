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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

public class RDFResourceSets extends RDFNodeBasicSets {
	
	protected final Set<Resource> resources = new HashSet<Resource>();

	public RDFResourceSets(Set<Resource> resources) {
		this.resources.addAll(resources);
	}
	
	
	public Set<Value> getValues() { return Collections.<Value>unmodifiableSet(resources); }
	public Set<Resource> getResources() { return resources; }
	
	public Set<URI> getURIs() {
		if(uris == null) {
			uris = new HashSet<URI>();
			for(Resource resource : getResources()) {
				if(resource instanceof URI) { uris.add((URI) resource); }
			}
		}
		return uris;
	}
	
	public Set<BNode> getBNodes() {
		if(bnodes == null) {
			bnodes = new HashSet<BNode>();
			for(Resource resource : getResources()) {
				if(resource instanceof BNode) { bnodes.add((BNode) resource); }
			}
		}
		return bnodes;
	}
	
	public Set<Literal> getLiterals() { return Collections.<Literal>emptySet(); }
	public Set<String> getStrings() { return Collections.<String>emptySet();	}
	
}
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

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.XMLSchema;
import org.sbpax.util.sets.SetUtil;

public abstract class RDFNodeBasicSets implements RDFNodeSets {
	
	protected Set<Resource> resources = null;
	protected Set<URI> uris = null;
	protected Set<BNode> bnodes = null;
	protected Set<Literal> literals = null;
	protected Set<String> strings = null;
	protected Set<Double> doubles = null;
	
	public abstract Set<Value> getValues();
	public Value getValue() { return SetUtil.pickAny(getValues()); }
	
	public Set<Resource> getResources() {
		if(resources == null) {
			resources = new HashSet<Resource>();
			for(Value value : getValues()) {
				if(value instanceof Resource) { resources.add((Resource) value); }
			}
		}
		return resources;
	}
	
	public Resource getResource() { return SetUtil.pickAny(getResources()); }
	
	public Set<URI> getURIs() {
		if(uris == null) {
			uris = new HashSet<URI>();
			for(Resource resource : getResources()) {
				if(resource instanceof URI) { uris.add((URI) resource); }
			}
		}
		return uris;
	}
	
	public URI getURI() { return SetUtil.pickAny(getURIs()); }

	public Set<BNode> getBNodes() {
		if(bnodes == null) {
			bnodes = new HashSet<BNode>();
			for(Resource resource : getResources()) {
				if(resource instanceof BNode) { bnodes.add((BNode) resource); }
			}
		}
		return bnodes;
	}
	
	public BNode getBNode() { return SetUtil.pickAny(getBNodes()); }

	public Set<Literal> getLiterals() {
		if(literals == null) {
			literals = new HashSet<Literal>();
			for(Value value : getValues()) {
				if(value instanceof Literal) { literals.add((Literal) value); }
			}
		}
		return literals;
	}
	
	public Literal getLiteral() { return SetUtil.pickAny(getLiterals()); }

	public Set<String> getStrings() {
		if(strings == null) {
			strings = new HashSet<String>();
			for(Literal literal : getLiterals()) {
				if(XMLSchema.STRING.equals(literal.getDatatype())) {
					strings.add(literal.stringValue());
				}
			}
		}
		return strings;
	}
	
	public String getString() { return SetUtil.pickAny(getStrings()); }

	public Set<Double> getDoubles() {
		if(doubles == null) {
			doubles = new HashSet<Double>();
			for(Literal literal : getLiterals()) {
				if(XMLSchema.DOUBLE.equals(literal.getDatatype())) {
					doubles.add(literal.doubleValue());
				}
			}
		}
		return doubles;
	}
	
	public Double getDouble() { return SetUtil.pickAny(getDoubles()); }
	public double getDoubleValue() { return getDouble().doubleValue(); }
	
}
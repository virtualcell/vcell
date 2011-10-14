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
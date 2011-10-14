package org.vcell.sybil.rdf.nodestore;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Value;

public class RDFValueSets extends RDFNodeBasicSets {
	
	protected final Set<Value> values = new HashSet<Value>();

	public RDFValueSets(Set<Value> values) { this.values.addAll(values); }
	
	public Set<Value> getValues() { return values; }
	
}
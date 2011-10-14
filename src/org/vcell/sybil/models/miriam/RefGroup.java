/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.miriam;

/*   RefGroupImp  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   A group of MIRIAM references (data type plus value, e.g. UniProt P00533)
 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.vcell.sybil.models.miriam.MIRIAMRef.URNParseFailureException;
import org.vcell.sybil.rdf.RDFBagUtil;
import org.vcell.sybil.util.keys.KeyOfOne;

public class RefGroup extends KeyOfOne<Resource> {

	public RefGroup(Resource bag) { super(bag); }

	public Resource getResource() { return a(); }
	
	public RefGroup add(Graph graph, MIRIAMRef ref) {
		Resource rRef = graph.getValueFactory().createURI(ref.urn());
		graph.add(getResource(), RDF.LI, rRef);
		return this;
	}

	public boolean contains(Graph graph, MIRIAMRef ref) {
		Resource rRef = graph.getValueFactory().createURI(ref.urn());
		Statement statement = graph.getValueFactory().createStatement(getResource(), RDF.LI, rRef);
		return graph.contains(statement);
	}

	public RefGroup removeAll(Graph graph) {
		Iterator<Statement> stmtIter = graph.match(getResource(), null, null);
		while(stmtIter.hasNext()) { 
			Statement statement = stmtIter.next();
			if(RDFBagUtil.isRDFContainerMembershipProperty(statement.getPredicate())) {
				stmtIter.remove(); 				
			}
		}
		return this;
	}

	public Set<MIRIAMRef> refs(Graph graph) {
		Iterator<Statement> stmtIter = graph.match(getResource(), null, null);
		Set<MIRIAMRef> refs = new HashSet<MIRIAMRef>();
		while(stmtIter.hasNext()) {
			Statement statement = stmtIter.next();
			if(RDFBagUtil.isRDFContainerMembershipProperty(statement.getPredicate())) {
				Value object = statement.getObject();
				if(object instanceof URI) {
					URI resourceRef = (URI) object;
					try { 
						refs.add(MIRIAMRef.createFromURN(resourceRef.stringValue()));
					} catch (URNParseFailureException e) { e.printStackTrace(); }
				}
			}			
		}
		return refs;
	}

	public RefGroup remove(Graph graph, MIRIAMRef ref) {
		Resource resourceRef = graph.getValueFactory().createURI(ref.urn());
		Iterator<Statement> stmtIter = graph.match(getResource(), null, resourceRef);
		while(stmtIter.hasNext()) { stmtIter.next(); stmtIter.remove(); }
		return this;
	}

	public void delete(Graph graph) {
		Iterator<Statement> iter = graph.match(getResource(), null, null);
		while(iter.hasNext()) { iter.next(); iter.remove(); }
		iter = graph.match(null, null, getResource());
		while(iter.hasNext()) { iter.next(); iter.remove(); }
	}
	
}

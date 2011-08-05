/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.models.miriam.imp;

/*   RefGroupImp  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   A group of MIRIAM references (data type plus value, e.g. UniProt P00533)
 */

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.vcell.sybil.models.miriam.MIRIAMRef;
import org.vcell.sybil.models.miriam.RefGroup;
import org.vcell.sybil.models.miriam.MIRIAMRef.URNParseFailureException;
import org.vcell.sybil.rdf.RDFBagWrapper;
import org.vcell.sybil.rdf.RDFBox;

public class RefGroupImp extends RDFBagWrapper implements RefGroup {

	public RefGroupImp(RDFBox box, Resource bag) { super(box, bag); }

	public RefGroupImp add(MIRIAMRef ref) {
		Resource rRef = box().getRdf().getValueFactory().createURI(ref.urn());
		box().getRdf().add(resource(), RDF.LI, rRef);
		return this;
	}

	public boolean contains(MIRIAMRef ref) {
		Resource rRef = box().getRdf().getValueFactory().createURI(ref.urn());
		Statement statement = box().getRdf().getValueFactory().createStatement(resource(), RDF.LI, rRef);
		return box().getRdf().contains(statement);
	}

	public RefGroupImp removeAll() {
		Iterator<Statement> stmtIter = box().getRdf().match(resource(), null, null);
		while(stmtIter.hasNext()) { 
			Statement statement = stmtIter.next();
			if(RDFBagWrapper.isRDFContainerMembershipProperty(statement.getPredicate())) {
				stmtIter.remove(); 				
			}
		}
		return this;
	}

	public Set<MIRIAMRef> refs() {
		Iterator<Statement> stmtIter = box().getRdf().match(resource(), null, null);
		Set<MIRIAMRef> refs = new HashSet<MIRIAMRef>();
		while(stmtIter.hasNext()) {
			Statement statement = stmtIter.next();
			if(RDFBagWrapper.isRDFContainerMembershipProperty(statement.getPredicate())) {
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

	public RefGroupImp remove(MIRIAMRef ref) {
		Resource resourceRef = box().getRdf().getValueFactory().createURI(ref.urn());
		Iterator<Statement> stmtIter = box().getRdf().match(resource(), null, resourceRef);
		while(stmtIter.hasNext()) { stmtIter.next(); stmtIter.remove(); }
		return this;
	}

	public void delete() {
		Iterator<Statement> iter = box().getRdf().match(resource(), null, null);
		while(iter.hasNext()) { iter.next(); iter.remove(); }
		iter = box().getRdf().match(null, null, resource());
		while(iter.hasNext()) { iter.next(); iter.remove(); }
	}
	
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.util.http.uniprot;

/*   UniProtExtractor  --- by Oliver Ruebenacker, UCHC --- January 2010
 *   Some information about UniProt useful for web requests.
 *   Note the complexity of the RDF obtained from UniProt:
 *   some URIs begin with "www", others with "purl", some contain
 *   the suffix ".rdf", others do not.
 */

import java.util.Iterator;

import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.sbpax.util.StringUtil;
import org.vcell.sybil.util.http.uniprot.box.UniProtBox;
import org.vcell.sybil.util.http.uniprot.box.imp.UniProtBoxImp;

public class UniProtExtractor {

	protected Graph model;
	
	public UniProtExtractor(Graph model) { this.model = model; }

	public Graph model() { return model; }
	
	public URI entry(String id) { return model.getValueFactory().createURI(UniProtConstants.uri(id)); }
	public String id(URI entry) { return entry.getLocalName(); }	
	
	public UniProtBox extractBox() {
		UniProtBoxImp box = new UniProtBoxImp();
		
		Iterator<Statement> stmtIter1 = model.match(null, UniProtConstants.replaces, null);
		while(stmtIter1.hasNext()) {
			Statement statement = stmtIter1.next();
			Resource subject = statement.getSubject();
			Value object = statement.getObject();
			if(subject instanceof URI && object instanceof URI) {
				String idSubject = id((URI) subject);
				String idObject = id((URI) object);
				if(StringUtil.notEmpty(idSubject) && StringUtil.notEmpty(idObject)) {
					UniProtBox.Entry entrySubject = box.entry(idSubject);
					UniProtBox.Entry entryObject = box.entry(idObject);
					box.setReplaces(entrySubject, entryObject);
				}
			}
		}
		Iterator<Statement> stmtIter2 = model.match(null, UniProtConstants.replacedBy, null);
		while(stmtIter2.hasNext()) {
			Statement statement = stmtIter2.next();
			Resource subject = statement.getSubject();
			Value object = statement.getObject();
			if(subject instanceof URI && object instanceof URI) {
				String idSubject = id((URI) subject);
				String idObject = id((URI) object);
				if(StringUtil.notEmpty(idSubject) && StringUtil.notEmpty(idObject)) {
					UniProtBox.Entry entrySubject = box.entry(idSubject);
					UniProtBox.Entry entryObject = box.entry(idObject);
					box.setReplaces(entryObject, entrySubject);
				}
			}
		} 
		
		Iterator<Statement> stmtIter3 = model.match(null, UniProtConstants.recommendedName, null);
		if (stmtIter3.hasNext()) {
			while(stmtIter3.hasNext()) {
				Statement statement = stmtIter3.next();
				Resource entryNode = statement.getSubject();
				Value nameNode = statement.getObject();
				if(nameNode instanceof Resource) {
					Resource nameResource = (Resource) nameNode;
					Iterator<Statement> stmtIter4 = model.match(nameResource, UniProtConstants.fullName, null);
						// System.out.println("hello world");
					while(stmtIter4.hasNext()) {
						Statement statement2 = stmtIter4.next();
						Value objectNode = statement2.getObject();
						// System.out.println("hello moon");
						if(entryNode instanceof URI && objectNode instanceof Literal) {
							String name = ((Literal) objectNode).stringValue();
							UniProtBox.Entry entry = box.entry(UniProtConstants.idFromResource((URI) entryNode));
							System.out.println("UniProtExtractor: name: " + name + "\tid: " + entry.id());
							entry.setRecommendedName(name);
						}
					}
				}
			}
		} else { 
			// sometimes, recommended name is not present for an uniprot id : check the full name
			stmtIter3 = model.match(null, UniProtConstants.fullName, null);
			while(stmtIter3.hasNext()) {
				Statement statement2 = stmtIter3.next();
				Resource entryNode = statement2.getSubject();
				Value objectNode = statement2.getObject();
				if(entryNode instanceof URI && objectNode instanceof Literal) {
					String name = ((Literal) objectNode).stringValue();
					UniProtBox.Entry entry = box.entry(UniProtConstants.idFromResource((URI) entryNode));
					entry.setRecommendedName(name);
					System.out.println("UniProtExtractor: name: " + name + "\tid: " + entry.id());
				}
			}
		}
		
		return box;
	}
	
	public static UniProtBox extractBox(Graph model) { return new UniProtExtractor(model).extractBox(); }
	
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf;

/*   OntUtil  --- by Oliver Ruebenacker, UCHC --- May to September 2008
 *   A few convenient methods for handling ontologies
 */

import java.util.Collection;
import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;

public class OntUtil {

	static public URI createOntologyNode(Graph model, String uri) {
		URI node = model.getValueFactory().createURI(uri);
		model.add(node, RDF.TYPE, OWL.ONTOLOGY);
		return node;
	}
	
	static public URI createAnnotationProperty(Graph model, String uri) {
		URI property = model.getValueFactory().createURI(uri);
		model.add(property, RDF.TYPE, OWL.ANNOTATIONPROPERTY);
		return property;
	}
	
	static public void makeAllDisjoint(Graph model, Collection<? extends Resource> classes) {
		for(Resource class1 : classes) {
			for(Resource class2 : classes) {
				if(!class1.equals(class2)) {
					model.add(class1, OWL.DISJOINTWITH, class2);
					model.add(class2, OWL.DISJOINTWITH, class1);
				}
			}
		}
	}

	static public void makeDisjointSubClasses(Graph model, Resource base, 
			Collection<? extends Resource> subClasses) {
		for(Resource subClass1 : subClasses) {
			model.add(subClass1, RDFS.SUBCLASSOF, base);
			for(Resource subClass2 : subClasses) {
				if(!subClass1.equals(subClass2)) {
					model.add(subClass1, OWL.DISJOINTWITH, subClass2);
					model.add(subClass2, OWL.DISJOINTWITH, subClass1);
				}
			}
		}

	}

	public static void addTypedComment(Graph schema, Resource resource, String comment) {
		schema.add(resource, RDFS.COMMENT, 
				schema.getValueFactory().createLiteral(comment, XMLSchema.STRING));
	}
	
	public static void addEnglishComment(Graph schema, Resource resource, String comment) {
		schema.add(resource, RDFS.COMMENT, schema.getValueFactory().createLiteral(comment, "en"));
	}

	
}

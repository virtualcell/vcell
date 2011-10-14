/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.schemas.util;

/*   OntUtil  --- by Oliver Ruebenacker, UCHC --- May to September 2008
 *   A few convenient methods for handling ontologies
 */

import java.util.ArrayList;
import org.openrdf.model.BNode;
import org.openrdf.model.Graph;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import java.util.Collection;

public class OntUtil {

	static public URI createOntologyNode(Graph model, String uri) {
		URI node = model.getValueFactory().createURI(uri);
		model.add(node, RDF.TYPE, OWL.ONTOLOGY);
		return node;
	}

	static public URI createClass(Graph model, String uri) {
		URI node = model.getValueFactory().createURI(uri);
		model.add(node, RDF.TYPE, OWL.CLASS);
		return node;
	}
	
	static public BNode createBIndividual(Graph model, Resource type) {
		BNode individual = model.getValueFactory().createBNode();
		model.add(individual, RDF.TYPE, type);
		return individual;
	}
	
	static public URI createURIIndividual(Graph model, String uri, Resource type) {
		URI individual = model.getValueFactory().createURI(uri);
		model.add(individual, RDF.TYPE, type);
		return individual;
	}
	
	static public Literal createTypedString(Graph model, String string) {
		return model.getValueFactory().createLiteral(string, XMLSchema.STRING);
	}
	
	static public URI createAnnotationProperty(Graph model, String uri) {
		URI property = model.getValueFactory().createURI(uri);
		model.add(property, RDF.TYPE, OWL.ANNOTATIONPROPERTY);
		return property;
	}
	
	static public URI createObjectProperty(Graph model, String uri) {
		URI property = model.getValueFactory().createURI(uri);
		model.add(property, RDF.TYPE, OWL.OBJECTPROPERTY);
		return property;
	}
	
	static public URI createDatatypeProperty(Graph model, String uri) {
		URI property = model.getValueFactory().createURI(uri);
		model.add(property, RDF.TYPE, OWL.DATATYPEPROPERTY);
		return property;
	}
	
	static public void makeAllDisjoint(Graph model, Resource ... classes) {
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

	public static Value createList(Graph graph, int offset, Value ... values) {
		if(offset < values.length) {
			BNode listNode = graph.getValueFactory().createBNode();
			graph.add(listNode, RDF.FIRST, values[offset]);
			Value restNode = createList(graph, offset + 1, values);
			graph.add(listNode, RDF.REST, restNode);
			return listNode;
		} else {
			return RDF.NIL;
		}
	}
	
	public static Value createList(Graph graph, Value ... values) { return createList(graph, 0, values); }
	
	public static BNode createUnion(Graph graph, Value ... values) {
		BNode unionNode = graph.getValueFactory().createBNode();
		graph.add(unionNode, RDF.TYPE, OWL.CLASS);
		Value listNode = createList(graph, values);
		graph.add(unionNode, OWL.UNIONOF, listNode);
		return unionNode;
	}

	public static URI DATARANGE = ValueFactoryImpl.getInstance().createURI(OWL.NAMESPACE + "DataRange");
	
	public static BNode createDataRange(Graph graph, Value ... values) {
		BNode unionNode = graph.getValueFactory().createBNode();
		graph.add(unionNode, RDF.TYPE, DATARANGE);
		Value listNode = createList(graph, values);
		graph.add(unionNode, OWL.ONEOF, listNode);
		return unionNode;
	}

	public static BNode createDataRange(Graph graph, String ... strings) {
		ArrayList<Value> valueList = new ArrayList<Value>();
		for(String string : strings) {
			valueList.add(graph.getValueFactory().createLiteral(string));
		}
		BNode unionNode = graph.getValueFactory().createBNode();
		graph.add(unionNode, RDF.TYPE, DATARANGE);
		Value listNode = createList(graph, valueList.toArray(new Value[0]));
		graph.add(unionNode, OWL.ONEOF, listNode);
		return unionNode;
	}
	
	public static BNode createRestriction(Graph graph, URI restrictionProperty, URI targetProperty, Value value) {
		BNode node = graph.getValueFactory().createBNode();
		graph.add(node, RDF.TYPE, OWL.RESTRICTION);
		graph.add(node, restrictionProperty, value);
		graph.add(node, OWL.ONPROPERTY, targetProperty);
		return node;
	}

	public static BNode createRestriction(Graph graph, URI restrictionProperty, URI targetProperty, 
			int number) {
		Literal numberNode = graph.getValueFactory().createLiteral(number);
		return createRestriction(graph, restrictionProperty, targetProperty, numberNode);
	}

}

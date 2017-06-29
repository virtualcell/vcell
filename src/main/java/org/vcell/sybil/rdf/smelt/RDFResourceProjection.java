/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.sybil.rdf.smelt;

/*   RDFResourceProjection  --- by Oliver Ruebenacker, UCHC --- August 2010
 *   Applies a map to project RDF resources, statements and models. 
 *   Assumes identity where the map is empty
 */

import java.util.Map;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.sbpax.impl.HashGraph;

public class RDFResourceProjection implements RDFSmelter {

	protected final Map<Resource, Resource> map;
	
	public RDFResourceProjection(Map<Resource, Resource> map) {
		this.map = map;
	}
	
	public Value project(Value node) {
		Value nodeMapped = map.get(node);
		if(nodeMapped == null) { nodeMapped = node; }
		return nodeMapped;
	}
	
	public Resource project(Resource resource) {
		Resource resourceMapped = map.get(resource);
		if(resourceMapped == null) { resourceMapped = resource; }
		return resourceMapped;
	}
	
	public URI project(URI property) {
		Resource resourceMapped = map.get(property);
		URI propertyMapped = property;
		if(resourceMapped instanceof URI) {
			propertyMapped = (URI) resourceMapped;
		}
		return propertyMapped;
	}
	
	public Statement project(Statement statement, ValueFactory factory) {
		Resource subjectMapped = project(statement.getSubject());
		URI predicateMapped = project(statement.getPredicate());
		Value objectMapped = project(statement.getObject());
		return factory.createStatement(subjectMapped, predicateMapped, objectMapped);
	}

	public Graph project(Graph model) {
		Graph modelMapped = new HashGraph();
		for(Statement statement : model) {
			modelMapped.add(project(statement, model.getValueFactory()));
		}
		return modelMapped;
	}
	
	public Graph smelt(Graph rdf) { return project(rdf); }

}

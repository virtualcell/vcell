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

/*   NamespaceAssimilator  --- by Oliver Ruebenacker, UCHC --- June to July 2009
 *   Changes a set of resources into the same namespace
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.OWL;
import org.sbpax.impl.HashGraph;

public class NamespaceAssimilator implements RDFSmelter {
	
	protected Set<Resource> resources;
	protected String namespace;
	protected Map<Resource, Resource> resourceMap = new HashMap<Resource, Resource>();
	
	public NamespaceAssimilator(Set<Resource> resources, String namespace) {
		this.resources = resources;
		this.namespace = namespace;
	}
	
	public Graph smelt(Graph rdf) {
		Graph rdfNew = new HashGraph();
		for(Statement statement : rdf) {
			Resource subjectNew = smelt(rdf, statement.getSubject());
			URI predicateNew = smelt(rdf, statement.getPredicate());
			Value objectNew = statement.getObject() instanceof Resource ? 
					smelt(rdf, (Resource) statement.getObject()) : statement.getObject();
			rdfNew.add(subjectNew, predicateNew, objectNew);
		}
		for(Map.Entry<Resource, Resource> entry : resourceMap.entrySet()) {
			rdfNew.add(entry.getKey(), OWL.SAMEAS, entry.getValue());
		}
		return rdfNew;
	}

	public Resource smelt(Graph rdf, Resource resource) {
		Resource resourceMapped = resource;
		if(!resources.contains(resource)) {
			if(resource instanceof URI) {
				URI uri = (URI) resource;
				if(!namespace.equals(uri.getNamespace())) {
					resourceMapped = resourceMap.get(resource);
					if(resourceMapped == null) {
						String localName = uri.getLocalName();
						String localNameNew = localName;
						resourceMapped = rdf.getValueFactory().createURI(namespace + localNameNew);
						resourceMap.put(resource, resourceMapped);
					}
				}
			} else {
				resourceMapped = resourceMap.get(resource);
				if(resourceMapped == null) { resourceMapped = resource; }
			}
		}
		return resourceMapped;
	}
	
	public URI smelt(Graph rdf, URI property) {
		URI propertyMapped = property;
		if(!resources.contains(property)) {
			if(!namespace.equals(property.getNamespace())) {
				Resource resourceMapped = resourceMap.get(property);
				if(resourceMapped instanceof URI) {
					propertyMapped = (URI) resourceMapped;
				} else if (resourceMapped instanceof Resource) {
					propertyMapped = rdf.getValueFactory().createURI(resourceMapped.stringValue());
					resourceMap.put(property, propertyMapped);
				} else {
					String localName = property.getLocalName();
					String localNameNew = localName;
					propertyMapped = rdf.getValueFactory().createURI(namespace + localNameNew);
					resourceMap.put(property, propertyMapped);
				} 
			}
		}
		return propertyMapped;
	}
	
	public Resource map(Resource resource) {
		Resource resourceMapped = resourceMap.get(resource);
		if(resourceMapped == null) { resourceMapped = resource; }
		return resourceMapped;
	}
	public Map<Resource, Resource> resourceMap() { return resourceMap; }

}

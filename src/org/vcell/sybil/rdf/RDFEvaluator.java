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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.RDF;
import org.vcell.sybil.rdf.nodestore.RDFResourceSets;
import org.vcell.sybil.rdf.nodestore.RDFValueSets;
import org.vcell.sybil.rdf.pool.RDFObjectSets;
import org.vcell.sybil.rdf.pool.RDFObjectPool;
import org.vcell.sybil.rdf.pool.UnsupportedRDFTypeException;

public class RDFEvaluator {

	protected final Set<Graph> graphs = new HashSet<Graph>();
	
	public RDFEvaluator(Graph graph) { this.graphs.add(graph); }
	public RDFEvaluator(Set<Graph> graphs) { this.graphs.addAll(graphs); }
	
	public RDFResourceSets getSubjects(URI property, Value object) {
		Set<Resource> subjects = new HashSet<Resource>();
		for(Graph graph : graphs) {
			Iterator<Statement> stmtIter = graph.match(null, property, object);
			while(stmtIter.hasNext()) {
				subjects.add(stmtIter.next().getSubject());
			}			
		}
		return new RDFResourceSets(subjects);
	}
	
	public RDFValueSets getProperties(Resource subject, URI property) {
		Set<Value> objects = new HashSet<Value>();
		for(Graph graph : graphs) {			
			Iterator<Statement> stmtIter = graph.match(subject, property, null);
			while(stmtIter.hasNext()) {
				objects.add(stmtIter.next().getObject());
			}
		}
		return new RDFValueSets(objects);
	}
	
	public <T> Set<T> createAllObjectsForPool(RDFObjectPool<T> pool) {
		Set<T> objects = new HashSet<T>();
		Set<Resource> supportedTypes = pool.getSupportedTypes();
		for(Graph graph : graphs) {
			for(Resource type : supportedTypes) {
				Iterator<Statement> stmtIter = graph.match(null, RDF.TYPE, type);
				while(stmtIter.hasNext()) {
					Statement statement = stmtIter.next();
					try {
						objects.add(
								pool.getOrCreateObject(statement.getSubject(), 
										Collections.singleton(type)));
					} catch (UnsupportedRDFTypeException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return objects;
	}

	public <T> RDFValueSets getProperties(T subject, RDFObjectPool<T> pool, URI property) {
		Set<Value> objects = new HashSet<Value>();
		Set<Resource> subjectResources = pool.getResources(subject);
		for(Graph graph : graphs) {			
			for(Resource subjectResource : subjectResources) {
				Iterator<Statement> stmtIter = graph.match(subjectResource, property, null);
				while(stmtIter.hasNext()) {
					objects.add(stmtIter.next().getObject());
				}
			}
		}
		return new RDFValueSets(objects);
	}
		
	public <T, T2> RDFObjectSets<T2> getProperties(T subject, RDFObjectPool<T> pool, URI property,
			RDFObjectPool<T2> pool2) {
		RDFValueSets valueSets = getProperties(subject, pool, property);
		Set<T2> objects = new HashSet<T2>();
		Map<Resource, Set<Resource>> unsupported = new HashMap<Resource, Set<Resource>>();
		for(Resource objectResource : valueSets.getResources()) {
			Set<Resource> types = getProperties(objectResource, RDF.TYPE).getResources();
			try {
				T2 object2 = pool2.getOrCreateObject(objectResource, types);
				objects.add(object2);
			} catch (UnsupportedRDFTypeException e) {
				unsupported.put(objectResource, types);
			}
		}
		return new RDFObjectSets<T2>(valueSets, objects, unsupported);
	}
		
}

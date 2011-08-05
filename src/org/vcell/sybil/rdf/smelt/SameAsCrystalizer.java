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

/*   SameAsCrystalizer  --- by Oliver Ruebenacker, UCHC --- July 2009 to August 2010
 *   Changes a set of resources into the same namespace
 */

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.Graph;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.OWL;
import org.vcell.sybil.rdf.compare.NodeComparatorByType;
import org.vcell.sybil.rdf.compare.NodeComparatorNS;

public class SameAsCrystalizer implements RDFSmelter {
	
	protected Comparator<Value> comparator;
	
	public SameAsCrystalizer() { comparator = new NodeComparatorByType(); }
	
	public SameAsCrystalizer(Comparator<Value> comparator) { 
		this.comparator = comparator; 
	}
	
	public SameAsCrystalizer(String namespace){
		comparator = new NodeComparatorNS(namespace);
	}
	
	
	public Graph smelt(Graph rdf) {
		Map<Resource, Set<Resource>> sameAsSetsMap = new HashMap<Resource, Set<Resource>>();
		Iterator<Statement> sameAsStmtIter = rdf.match(null, OWL.SAMEAS, null);
		while(sameAsStmtIter.hasNext()) {
			Statement sameAsStatement = sameAsStmtIter.next();
			Value objectNode = sameAsStatement.getObject();
			if(objectNode instanceof Resource) {
				Resource subject = sameAsStatement.getSubject();
				Resource object = (Resource) objectNode;
				Set<Resource> subjectSameAsSet = sameAsSetsMap.get(subject);
				Set<Resource> objectSameAsSet = sameAsSetsMap.get(object);
				if(subjectSameAsSet != null && objectSameAsSet != null) {
					if(!subjectSameAsSet.equals(objectSameAsSet)) {
						Set<Resource> sameAsUnionSet = new HashSet<Resource>();
						sameAsUnionSet.addAll(subjectSameAsSet);
						sameAsUnionSet.addAll(objectSameAsSet);
						sameAsSetsMap.put(subject, sameAsUnionSet);
						sameAsSetsMap.put(object, sameAsUnionSet);
					}
				} else if(subjectSameAsSet != null) {
					subjectSameAsSet.add(object);
					sameAsSetsMap.put(object, subjectSameAsSet);
				} else if(objectSameAsSet != null) {
					objectSameAsSet.add(subject);
					sameAsSetsMap.put(subject, objectSameAsSet);
				} else {
					Set<Resource> sameAsUnionSet = new HashSet<Resource>();
					sameAsUnionSet.add(subject);
					sameAsUnionSet.add(object);
					sameAsSetsMap.put(subject, sameAsUnionSet);
					sameAsSetsMap.put(object, sameAsUnionSet);
				}
			}
		}
		Set<Set<Resource>> sameAsSets = new HashSet<Set<Resource>>();
		for(Map.Entry<Resource, Set<Resource>> sameAsSetsEntry : sameAsSetsMap.entrySet()) {
			sameAsSets.add(sameAsSetsEntry.getValue());
		}
		Map<Resource, Resource> projectionMap = new HashMap<Resource, Resource>();
		for(Set<Resource> sameAsSet : sameAsSets) {
			Resource preferredResource = null;
			for(Resource resource : sameAsSet) {
				if(preferredResource == null || comparator.compare(resource, preferredResource) > 0) {
					preferredResource = resource;
				}
			}
			for(Resource resource : sameAsSet) {
				if(resource != preferredResource) {
					projectionMap.put(resource, preferredResource);
				}
			}
		}
		RDFResourceProjection rdfResourceProjection = new RDFResourceProjection(projectionMap);
		Graph rdfSmelted = rdfResourceProjection.smelt(rdf);
		Iterator<Statement> iter = rdfSmelted.match(null, OWL.SAMEAS, null);
		while(iter.hasNext()) { iter.next(); iter.remove(); }
		for(Map.Entry<Resource, Resource> projectionEntry : projectionMap.entrySet()) {
			Resource subject = projectionEntry.getValue();
			Resource object = projectionEntry.getKey();
			if(!subject.equals(object)) {
				rdfSmelted.add(subject, OWL.SAMEAS, object);				
			}
		}
		return rdfSmelted;
	}
	
}

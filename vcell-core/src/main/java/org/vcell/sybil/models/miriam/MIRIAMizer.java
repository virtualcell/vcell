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

/*   MIRIAMizerImp  --- by Oliver Ruebenacker, UCHC --- March 2010
 *   Handling MIRIAM annotations
 */

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
import org.vcell.sybil.models.AnnotationQualifiers;

import cbit.vcell.biomodel.meta.VCMetaData;

public class MIRIAMizer {

	public RefGroup newRefGroup(Graph graph, Resource resource, MIRIAMQualifier qualifier) {
		Resource bag = graph.getValueFactory().createBNode();
		graph.add(bag, RDF.TYPE, RDF.BAG);
		URI uri = qualifier.getProperty();
		graph.add(resource, uri, bag);
		RefGroup group = new RefGroup(bag);
		return group;		
	}
	
	public RefGroup newRefGroup(Graph graph, Resource resource, MIRIAMQualifier qualifier, MIRIAMRef ref) {
		Resource bag = graph.getValueFactory().createBNode();
		graph.add(bag, RDF.TYPE, RDF.BAG);
		graph.add(resource, qualifier.getProperty(), bag);
		RefGroup group = new RefGroup(bag);
		group.add(graph, ref);
		return group;		
	}
	
	public RefGroup newRefGroup(Graph graph, Resource resource, MIRIAMQualifier qualifier, Set<MIRIAMRef> refs) {
		Resource bag = graph.getValueFactory().createBNode();
		graph.add(bag, RDF.TYPE, RDF.BAG);
		graph.add(resource, qualifier.getProperty(), bag);
		RefGroup group = new RefGroup(bag);
		for(MIRIAMRef ref : refs) { group.add(graph, ref);	}
		return group;		
	}
	
	public Set<RefGroup> getRefGroups(Graph graph, Resource resource, MIRIAMQualifier qualifier) {
		Set<RefGroup> groups = new HashSet<RefGroup>();
		Iterator<Statement> iter = graph.match(resource, qualifier.getProperty(), null);
		while(iter.hasNext()) {
			Statement statement = iter.next();
			Value node = statement.getObject();
			if(node instanceof Resource) {
				groups.add(new RefGroup((Resource) node));
			}
		}
		return groups;
	}
	
	public Map<RefGroup, MIRIAMQualifier> getModelRefGroups(Graph graph, Resource resource) {
		Map<RefGroup, MIRIAMQualifier> map = new HashMap<RefGroup, MIRIAMQualifier>();
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAMMODEL_all) {
			Set<RefGroup> groups = getRefGroups(graph, resource, qualifier);
			for(RefGroup group : groups) { map.put(group, qualifier); }
		}
		return map;
	}

	public Map<RefGroup, MIRIAMQualifier> getBioRefGroups(Graph graph, Resource resource) {
		Map<RefGroup, MIRIAMQualifier> map = new HashMap<RefGroup, MIRIAMQualifier>();
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAMBIO_all) {
			Set<RefGroup> groups = getRefGroups(graph, resource, qualifier);
			for(RefGroup group : groups) { map.put(group, qualifier); }
		}
		return map;
	}

	public Map<RefGroup, MIRIAMQualifier> getAllRefGroups(Graph graph, Resource resource) {
		Map<RefGroup, MIRIAMQualifier> map = new HashMap<RefGroup, MIRIAMQualifier>();
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAM_all) {
			Set<RefGroup> groups = getRefGroups(graph, resource, qualifier);
			for(RefGroup group : groups) {
				map.put(group, qualifier);
			}
		}
		return map;
	}

	public void detachRefGroup(Graph graph, Resource resource, RefGroup group) {
		Iterator<Statement> iter = graph.match(resource, null, group.getResource());
		while(iter.hasNext()) {
			iter.next();
			iter.remove(); }
	}
	
	public void detachRefGroups(Graph graph, Resource resource, MIRIAMQualifier qualifier) {
		Iterator<Statement> iter = graph.match(resource, qualifier.getProperty(), null);
		while(iter.hasNext()) { iter.next(); iter.remove(); }
	}

	public void detachModelRefGroups(Graph graph, Resource resource) {
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAMMODEL_all) { detachRefGroups(graph, resource, qualifier); }
	}

	public void detachBioRefGroups(Graph graph, Resource resource) {
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAMBIO_all) { detachRefGroups(graph, resource, qualifier); }
	}

	public void detachAllRefGroups(Graph graph, Resource resource) {
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAM_all) { detachRefGroups(graph, resource, qualifier); }
	}

	public void deleteRefGroup(Graph graph, Resource resource, RefGroup group) {
		detachRefGroup(graph, resource, group);
		group.delete(graph);
	}
	public void deleteRefGroup(VCMetaData vcMetaData, Resource resource, RefGroup group) {
		Graph graph = vcMetaData.getRdfData();
		detachRefGroup(graph, resource, group);
		System.out.println(vcMetaData.printRdfStatements());

		group.delete(graph);
		System.out.println(vcMetaData.printRdfStatements());
	}

	public void deleteRefGroups(Graph graph, Resource resource, MIRIAMQualifier qualifier) {
		Set<RefGroup> groups = getRefGroups(graph, resource, qualifier);
		for(RefGroup group : groups) { deleteRefGroup(graph, resource, group); }
	}

	public void deleteModelRefGroups(Graph graph, Resource resource) {
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAMMODEL_all) { deleteRefGroups(graph, resource, qualifier); }		
	}

	public void deleteBioRefGroups(Graph graph, Resource resource) {
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAMBIO_all) { deleteRefGroups(graph, resource, qualifier); }		
	}

	public void deleteAllRefGroups(Graph graph, Resource resource) {
		for(MIRIAMQualifier qualifier : AnnotationQualifiers.MIRIAM_all) { deleteRefGroups(graph, resource, qualifier); }				
	}

}

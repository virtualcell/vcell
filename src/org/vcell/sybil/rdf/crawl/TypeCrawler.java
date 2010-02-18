package org.vcell.sybil.rdf.crawl;

/*   TypeCrawler  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Utilities for types
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.vcell.sybil.util.keys.KeyOfThree;
import org.vcell.sybil.util.keys.KeyOfTwo;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class TypeCrawler {

	public static Set<Resource> directSubTypes(Model model, Resource baseType) {
		Set<Resource> subTypes = new HashSet<Resource>();
		ResIterator resIter = model.listSubjectsWithProperty(RDFS.subClassOf, baseType);
		while(resIter.hasNext()) { subTypes.add(resIter.nextResource()); }
		return subTypes;
	}
	
	public static Set<Resource> subTypes(Model model, Resource baseType) {
		Set<Resource> subTypes = new HashSet<Resource>();
		subTypes.add(baseType);
		Set<Resource> subTypesAdd = directSubTypes(model, baseType);
		while(!subTypesAdd.isEmpty()) {
			Set<Resource> subTypesAddNext = new HashSet<Resource>();
			for(Resource subType : subTypesAdd) {
				if(!subTypes.contains(subType)) {
					subTypesAddNext.addAll(directSubTypes(model, subType));					
				}
			}
			subTypes.addAll(subTypesAdd);
			subTypesAdd = subTypesAddNext;
		}
		return subTypes;
	}
	
	public static Set<Resource> directSubClasses(Model model, Resource baseType) {
		Set<Resource> subClasses = new HashSet<Resource>();
		ResIterator resIter = model.listSubjectsWithProperty(RDFS.subClassOf, baseType);
		while(resIter.hasNext()) { 
			Resource subType = resIter.nextResource();
			if(model.contains(subType, RDF.type, OWL.Class)) {
				subClasses.add(subType);				
			}
		}
		return subClasses;
	}
	
	public static Set<Resource> subClasses(Model model, Resource baseType) {
		Set<Resource> subTypes = new HashSet<Resource>();
		subTypes.add(baseType);
		Set<Resource> subTypesAdd = directSubClasses(model, baseType);
		while(!subTypesAdd.isEmpty()) {
			Set<Resource> subTypesAddNext = new HashSet<Resource>();
			for(Resource subType : subTypesAdd) {
				if(!subTypes.contains(subType)) {
					subTypesAddNext.addAll(directSubClasses(model, subType));					
				}
			}
			subTypes.addAll(subTypesAdd);
			subTypesAdd = subTypesAddNext;
		}
		return subTypes;
	}
	
	public static Map<Resource, Integer> subClassesToRanks(Model model, Resource baseType) {
		Map<Resource, Integer> subTypes = new HashMap<Resource, Integer>();
		Integer rank = new Integer(0);
		subTypes.put(baseType, rank);
		Set<Resource> subTypesAdd = directSubClasses(model, baseType);
		while(!subTypesAdd.isEmpty()) {
			rank = new Integer((rank.intValue()) + 1);
			for(Resource subTypeAdd : subTypesAdd) { subTypes.put(subTypeAdd, rank); }
			Set<Resource> subTypesAddNext = new HashSet<Resource>();
			for(Resource subType : subTypesAdd) {
				if(!subTypes.containsKey(subType)) {
					subTypesAddNext.addAll(directSubClasses(model, subType));					
				}
			}
			subTypesAdd = subTypesAddNext;
		}
		return subTypes;
	}
	
	public static Set<Resource> instances(Model model, Resource baseType) {
		Set<Resource> instances = new HashSet<Resource>();
		for(Resource type : subTypes(model, baseType)) {
			ResIterator resIter = model.listResourcesWithProperty(RDF.type, type);
			while(resIter.hasNext()) {
				instances.add(resIter.nextResource());
			}
		}
		return instances;
	}
	
	public static interface InstanceWithType {
		public Resource instance();
		public Resource type();
	}
	
	protected static class InstanceWithTypeImp extends KeyOfTwo<Resource, Resource> 
	implements InstanceWithType {
		public InstanceWithTypeImp(Resource instance, Resource type) { super(instance, type); }
		public Resource instance() { return a(); }
		public Resource type() { return b(); }
	}
	
	public static Set<InstanceWithType> instancesWithType(Model model, Resource baseType) {
		Set<InstanceWithType> instances = new HashSet<InstanceWithType>();
		for(Resource type : subTypes(model, baseType)) {
			ResIterator resIter = model.listResourcesWithProperty(RDF.type, type);
			while(resIter.hasNext()) {
				instances.add(new InstanceWithTypeImp(resIter.nextResource(), type));
			}
		}
		return instances;
	}
	
	public static interface InstanceWithOntclass extends InstanceWithType {
		public Resource ontclass();
		public int ontClassRank();
	}
	
	protected static class InstanceWithOntclassImp extends KeyOfThree<Resource, Resource, Resource> 
	implements InstanceWithOntclass {
		protected int ontClassRank;
		public InstanceWithOntclassImp(Resource instance, Resource type, Resource ontclass,
				int ontClassRank) { 
			super(instance, type, ontclass); 
			this.ontClassRank = ontClassRank;
		}
		public Resource instance() { return a(); }
		public Resource type() { return b(); }
		public Resource ontclass() { return c(); }
		public int ontClassRank() { return ontClassRank; }
	}
	
	public static Set<InstanceWithOntclass> 
	instancesWithClass(Model model, Resource baseType, Model ontology) {
		Set<InstanceWithOntclass> instances = new HashSet<InstanceWithOntclass>();
		Map<Resource, Integer> subClassesToRanks = subClassesToRanks(ontology, baseType);
		for(Map.Entry<Resource, Integer> subClassToRank : subClassesToRanks.entrySet()) {
			Resource subClass = subClassToRank.getKey();
			int rank = subClassToRank.getValue().intValue();
			Set<InstanceWithType> instancesWithTypes = instancesWithType(model, subClass);
			for(InstanceWithType instanceWithType : instancesWithTypes) {
				instances.add(new InstanceWithOntclassImp(instanceWithType.instance(),
						instanceWithType.type(), subClass, rank));				
			}
		}
		return instances;
	}
	
}

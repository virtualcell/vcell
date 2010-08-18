package org.vcell.sybil.rdf.smelt;

/*   SameAsCrystalizer  --- by Oliver Ruebenacker, UCHC --- July 2009 to August 2010
 *   Changes a set of resources into the same namespace
 */

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.vcell.sybil.rdf.compare.NodeComparatorByType;
import org.vcell.sybil.rdf.compare.NodeComparatorNS;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;

public class SameAsCrystalizer implements RDFSmelter {
	
	protected Comparator<? super RDFNode> comparator;
	
	public SameAsCrystalizer() { comparator = new NodeComparatorByType(); }
	
	public SameAsCrystalizer(Comparator<? super RDFNode> comparator) { 
		this.comparator = comparator; 
	}
	
	public SameAsCrystalizer(String namespace){
		comparator = new NodeComparatorNS(namespace);
	}
	
	
	public Model smelt(Model rdf) {
		Map<Resource, Set<Resource>> sameAsSetsMap = new HashMap<Resource, Set<Resource>>();
		StmtIterator sameAsStmtIter = rdf.listStatements(null, OWL.sameAs, (RDFNode) null);
		while(sameAsStmtIter.hasNext()) {
			Statement sameAsStatement = sameAsStmtIter.nextStatement();
			RDFNode objectNode = sameAsStatement.getObject();
			if(objectNode.isResource()) {
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
		Model rdfSmelted = rdfResourceProjection.smelt(rdf);
		rdfSmelted.removeAll(null, OWL.sameAs, (RDFNode) null);
		for(Map.Entry<Resource, Resource> projectionEntry : projectionMap.entrySet()) {
			Resource subject = projectionEntry.getValue();
			Resource object = projectionEntry.getKey();
			if(!subject.equals(object)) {
				rdfSmelted.add(subject, OWL.sameAs, object);				
			}
		}
		return rdfSmelted;
	}
	
}

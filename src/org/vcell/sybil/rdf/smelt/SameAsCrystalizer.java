package org.vcell.sybil.rdf.smelt;

/*   SameAsCrystalizer  --- by Oliver Ruebenacker, UCHC --- July 2009
 *   Changes a set of resources into the same namespace
 */

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.vcell.sybil.rdf.compare.NodeComparatorByType;
import org.vcell.sybil.rdf.compare.NodeComparatorNS;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;

public class SameAsCrystalizer implements RDFSmelter {
	
	
	protected Comparator<? super RDFNode> comparator;
	protected Map<Resource, Resource> sameAsMap = new HashMap<Resource, Resource>();
	
	public SameAsCrystalizer() { comparator = new NodeComparatorByType(); }
	
	public SameAsCrystalizer(Comparator<? super RDFNode> comparator) { 
		this.comparator = comparator; 
	}
	
	public SameAsCrystalizer(String namespace){
		comparator = new NodeComparatorNS(namespace);
	}
	
	
	public Model smelt(Model rdf) {
		Model rdfNew = ModelFactory.createDefaultModel();
		rdfNew.add(rdf);
		rdfNew.setNsPrefixes(rdf);
		boolean progressWasMade = true;
		while(progressWasMade) {
			progressWasMade = false;
			StmtIterator stmtIter = rdfNew.listStatements(null, OWL.sameAs, (RDFNode) null);
			if(stmtIter.hasNext()) {
				progressWasMade = true;
				Model rdfNewNext = ModelFactory.createDefaultModel();
				rdfNewNext.setNsPrefixes(rdf);
				Statement statementSameAs = stmtIter.nextStatement();
				Resource resource1 = statementSameAs.getSubject();
				RDFNode node2 = statementSameAs.getObject();
				if(node2 instanceof Resource) {
					rdfNew.remove(statementSameAs);
					Resource resource2 = (Resource) node2;
					sameAsMap.put(resource1, resource2);
					stmtIter = rdfNew.listStatements();
					if(comparator.compare(resource1, resource2) > 0) {
						Resource tmp = resource1;
						resource1 = resource2;
						resource2 = tmp;
					}
					if(resource1.isURIResource() && resource2.isURIResource()) {
						Property property1 = rdfNew.createProperty(resource1.getURI());
						Property property2 = rdfNew.createProperty(resource2.getURI());
						while(stmtIter.hasNext()) {
							Statement statement = stmtIter.nextStatement();
							Resource subject = statement.getSubject();
							if(resource1.equals(subject)) { subject = resource2; }
							Property predicate = statement.getPredicate();
							if(property1.equals(predicate)) { predicate = property2; }
							RDFNode object = statement.getObject();
							if(resource1.equals(object)) { object = resource2; }
							rdfNewNext.add(subject, predicate, object);
						}
					} else {
						while(stmtIter.hasNext()) {
							Statement statement = stmtIter.nextStatement();
							Resource subject = statement.getSubject();
							if(resource1.equals(subject)) { subject = resource2; }
							Property predicate = statement.getPredicate();
							RDFNode object = statement.getObject();
							if(resource1.equals(object)) { object = resource2; }
							rdfNewNext.add(subject, predicate, object);
						}						
					}
					rdfNew.close();
					rdfNew = rdfNewNext;
				}
			}			
		}
//		for(Map.Entry<Resource, Resource> entry : sameAsMap.entrySet()) {
//			rdfNew.add(entry.getKey(), OWL.sameAs, entry.getValue());
//		}
		return rdfNew;
	}
	
	public Map<Resource, Resource> sameAsMap() { return sameAsMap; }

	public Model sameAsModel() { 
		Model rdf = ModelFactory.createDefaultModel();
		for(Map.Entry<Resource, Resource> entry : sameAsMap.entrySet()) {
			rdf.add(entry.getKey(), OWL.sameAs, entry.getValue());
		}
		return rdf;
	}
	
}

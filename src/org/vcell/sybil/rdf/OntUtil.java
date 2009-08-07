package org.vcell.sybil.rdf;

/*   OntUtil  --- by Oliver Ruebenacker, UCHC --- May to September 2008
 *   A few convenient methods for handling ontologies
 */

import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;

public class OntUtil {

	static public void emptyPrefixMapping(PrefixMapping mapping) {
		for(Object prefix : mapping.getNsPrefixMap().keySet()) {
			mapping.removeNsPrefix((String)prefix);
		}
	}
	
	static public void makeAllDisjoint(Set<OntClass> ontClasses) {
		for(OntClass ontClass1 : ontClasses) {
			for(OntClass ontClass2 : ontClasses) {
				if(!ontClass1.equals(ontClass2)) {
					ontClass1.addProperty(OWL.disjointWith, ontClass2);
					ontClass2.addProperty(OWL.disjointWith, ontClass1);
				}
			}
		}
	}

	static public class ClassifyType {}
	
	static public final ClassifyType EQUIV = new ClassifyType();
	static public final ClassifyType SAME = new ClassifyType();
	
	static public void classify(OntClass base, RDFList subClasses, ClassifyType type) {	
		Iterator<?> iter1 = subClasses.iterator();
		while(iter1.hasNext()) {
			RDFNode node1 = (RDFNode) iter1.next();		
			if(node1 instanceof Resource) {
				Resource subClass1 = (Resource) node1;
				base.addSubClass(subClass1);
				Iterator<?> iter2 = subClasses.iterator();
				while(iter2.hasNext()) {
					RDFNode node2 = (RDFNode) iter2.next();
					if(node2 instanceof Resource) {
						Resource subClass2 = (Resource) node2;
						if(!subClass1.equals(subClass2)) {
							subClass1.addProperty(OWL.disjointWith, subClass2);
							subClass2.addProperty(OWL.disjointWith, subClass1);
						}
					}
				}
			}
		}
		Model model = base.getModel();
		OntModel schema;
		if(model instanceof OntModel) { schema = (OntModel) model; }
		else { schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, model); }
		if(type == EQUIV) {
			base.addEquivalentClass(schema.createUnionClass(null, subClasses));				
		} else if(type == SAME) {
			schema.createUnionClass(base.getURI(), subClasses);				
		}
	}
	
	static public void makeDisjointSubClasses(OntClass base, Set<OntClass> subClasses) {
		for(OntClass subClass1 : subClasses) {
			base.addSubClass(subClass1);
			for(OntClass subClass2 : subClasses) {
				if(!subClass1.equals(subClass2)) {
					subClass1.addProperty(OWL.disjointWith, subClass2);
					subClass2.addProperty(OWL.disjointWith, subClass1);
				}
			}
		}

	}

	public static void addComment(Model schema, Resource resource, String comment) {
		schema.add(resource, RDFS.comment, schema.createTypedLiteral(comment));
	}

	
}

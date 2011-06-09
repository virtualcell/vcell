package org.vcell.sybil.rdf;

/*   OntUtil  --- by Oliver Ruebenacker, UCHC --- May to September 2008
 *   A few convenient methods for handling ontologies
 */

import java.util.Set;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class OntUtil {

	static public void emptyPrefixMapping(PrefixMapping mapping) {
		for(Object prefix : mapping.getNsPrefixMap().keySet()) {
			mapping.removeNsPrefix((String)prefix);
		}
	}
	
	static public Resource createOntologyNode(Model model, String uri) {
		Resource node = model.createResource(uri);
		model.add(node, RDF.type, OWL.Ontology);
		return node;
	}
	
	static public Property createAnnotationProperty(Model model, String uri) {
		Property property = model.createProperty(uri);
		model.add(property, RDF.type, OWL.AnnotationProperty);
		return property;
	}
	
	static public void makeAllDisjoint(Model model, Set<Resource> classes) {
		for(Resource class1 : classes) {
			for(Resource class2 : classes) {
				if(!class1.equals(class2)) {
					model.add(class1, OWL.disjointWith, class2);
					model.add(class2, OWL.disjointWith, class1);
				}
			}
		}
	}

	static public void makeDisjointSubClasses(Model model, Resource base, Set<Resource> subClasses) {
		for(Resource subClass1 : subClasses) {
			model.add(subClass1, RDFS.subClassOf, base);
			for(Resource subClass2 : subClasses) {
				if(!subClass1.equals(subClass2)) {
					model.add(subClass1, OWL.disjointWith, subClass2);
					model.add(subClass2, OWL.disjointWith, subClass1);
				}
			}
		}

	}

	public static void addTypedComment(Model schema, Resource resource, String comment) {
		schema.add(resource, RDFS.comment, schema.createTypedLiteral(comment));
	}

	
}

package org.vcell.sybil.rdf.schemas;

/*   DatasetOntology  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Resources used as keys to sub models in a dataset (e.g. for SPARQL querying)
 */

import java.util.List;

import org.vcell.sybil.rdf.NameSpace;
import org.vcell.sybil.util.lists.ListOfNone;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class DatasetOntology {
	
	public static final OntologyBox box = new OntologyBox() {
		public Model getRdf() { return schema; }
		public String uri() { return URI; }
		public NameSpace ns() { return ns; }
		public String label() { return label; }
		public List<DatatypeProperty> labelProperties() { return labelProperties; }
	};
	
	public static final String label = "Dataset Ontology";
	public static final List<DatatypeProperty> labelProperties = new ListOfNone<DatatypeProperty>();
	
	public static final String URI = "http://vcell.org/sybil/dataset";
	public static final NameSpace ns = new NameSpace("dataset", URI + "#");

	public static final OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

	public static final OntClass DataSubSet = schema.createClass(ns + "DataSubSet");
	
	public static Individual Data = schema.createIndividual(ns + "Data", DataSubSet);
	public static Individual Schema = schema.createIndividual(ns + "Schema", DataSubSet);
	
}

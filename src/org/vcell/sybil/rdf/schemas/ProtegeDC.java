package org.vcell.sybil.rdf.schemas;

/*   ProtegeDC  --- by Oliver Ruebenacker, UCHC --- May 2008
 *   Protege's version of the Dublin Core schema
 */

import java.util.List;

import org.vcell.sybil.rdf.NameSpace;
import org.vcell.sybil.rdf.OntUtil;
import org.vcell.sybil.util.lists.ListOfNone;

import com.hp.hpl.jena.ontology.AnnotationProperty;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ProtegeDC {

	public static final OntologyBox box = new OntologyBox() {
		public Model getRdf() { return schema; }
		public String uri() { return URI; }
		public NameSpace ns() { return ns; }
		public String label() { return label; }
		public List<DatatypeProperty> labelProperties() { return labelProperties; }
	};
	
	public static final String label = "Protege Dublin Core";
	public static final List<DatatypeProperty> labelProperties = new ListOfNone<DatatypeProperty>();
	
	public static final String URI = "http://protege.stanford.edu/plugins/owl/dc/protege-dc.owl";
	
	public static final NameSpace ns = new NameSpace("", "http://purl.org/dc/elements/1.1/");
	public static final NameSpace nsXSD = new NameSpace("xsd", "http://www.w3.org/2001/XMLSchema#");	
	public static final NameSpace nsDC = new NameSpace("dc", "http://purl.org/dc/elements/1.1/");	
	public static final NameSpace nsDCTerms = new NameSpace("dcterms", "http://purl.org/dc/terms/");	
	
	public static final OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

	static {
		OntUtil.emptyPrefixMapping(schema);
		schema.setNsPrefix("", ns.uri);
		schema.setNsPrefix(nsDC.prefix, nsDC.uri);
		schema.setNsPrefix("rdfs", RDFS.getURI());
		schema.setNsPrefix(nsXSD.prefix, nsXSD.uri);
		schema.setNsPrefix(nsDCTerms.prefix, nsDCTerms.uri);
		schema.setNsPrefix("owl", OWL.getURI());
		schema.setNsPrefix("rdf", RDF.getURI());
	}
	
	public static final Ontology ontology = schema.createOntology(URI);
	
	public static final AnnotationProperty relation = schema.createAnnotationProperty(ns + "relation");
	public static final AnnotationProperty description = 
		schema.createAnnotationProperty(ns + "description");
	public static final AnnotationProperty language = schema.createAnnotationProperty(ns + "language");
	public static final AnnotationProperty identifier = schema.createAnnotationProperty(ns + "identifier");
	public static final AnnotationProperty subject = schema.createAnnotationProperty(ns + "subject");
	public static final AnnotationProperty rights = schema.createAnnotationProperty(ns + "rights");
	public static final AnnotationProperty coverage = schema.createAnnotationProperty(ns + "coverage");
	public static final AnnotationProperty contributor = 
		schema.createAnnotationProperty(ns + "contributor");
	public static final AnnotationProperty format = schema.createAnnotationProperty(ns + "format");
	public static final AnnotationProperty type = schema.createAnnotationProperty(ns + "type");
	public static final AnnotationProperty creator = schema.createAnnotationProperty(ns + "creator");
	public static final AnnotationProperty date = schema.createAnnotationProperty(ns + "date");
	public static final AnnotationProperty created = schema.createAnnotationProperty(ns + "created");
	public static final AnnotationProperty publisher = schema.createAnnotationProperty(ns + "publisher");
	public static final AnnotationProperty source = schema.createAnnotationProperty(ns + "source");
	public static final AnnotationProperty title = schema.createAnnotationProperty(ns + "title");
	
}

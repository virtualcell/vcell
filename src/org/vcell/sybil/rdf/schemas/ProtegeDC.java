package org.vcell.sybil.rdf.schemas;

/*   ProtegeDC  --- by Oliver Ruebenacker, UCHC --- May 2008
 *   Protege's version of the Dublin Core schema
 */

import org.vcell.sybil.rdf.NameSpace;
import org.vcell.sybil.rdf.OntUtil;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class ProtegeDC {

	public static final String URI = "http://protege.stanford.edu/plugins/owl/dc/protege-dc.owl";
	
	public static final NameSpace ns = new NameSpace("", "http://purl.org/dc/elements/1.1/");
	public static final NameSpace nsXSD = new NameSpace("xsd", "http://www.w3.org/2001/XMLSchema#");	
	public static final NameSpace nsDC = new NameSpace("dc", "http://purl.org/dc/elements/1.1/");	
	public static final NameSpace nsDCTerms = new NameSpace("dcterms", "http://purl.org/dc/terms/");	
	
	public static final Model schema = ModelFactory.createDefaultModel();

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
	
	public static final Resource ontology = OntUtil.createOntologyNode(schema, URI);
	
	public static final Property relation = OntUtil.createAnnotationProperty(schema, ns + "relation");
	public static final Property description = 
		OntUtil.createAnnotationProperty(schema, ns + "description");
	public static final Property language = OntUtil.createAnnotationProperty(schema, ns + "language");
	public static final Property identifier = OntUtil.createAnnotationProperty(schema, ns + "identifier");
	public static final Property subject = OntUtil.createAnnotationProperty(schema, ns + "subject");
	public static final Property rights = OntUtil.createAnnotationProperty(schema, ns + "rights");
	public static final Property coverage = OntUtil.createAnnotationProperty(schema, ns + "coverage");
	public static final Property contributor = 
		OntUtil.createAnnotationProperty(schema, ns + "contributor");
	public static final Property format = OntUtil.createAnnotationProperty(schema, ns + "format");
	public static final Property type = OntUtil.createAnnotationProperty(schema, ns + "type");
	public static final Property creator = OntUtil.createAnnotationProperty(schema, ns + "creator");
	public static final Property date = OntUtil.createAnnotationProperty(schema, ns + "date");
	public static final Property created = OntUtil.createAnnotationProperty(schema, ns + "created");
	public static final Property publisher = OntUtil.createAnnotationProperty(schema, ns + "publisher");
	public static final Property source = OntUtil.createAnnotationProperty(schema, ns + "source");
	public static final Property title = OntUtil.createAnnotationProperty(schema, ns + "title");
	
	
}

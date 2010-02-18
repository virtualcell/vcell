package org.vcell.sybil.rdf.reason;

/*   SYBREAMO  --- by Oliver Ruebenacker, UCHC --- May 2008
 *   The SYBREAMO schema
 */

import org.vcell.sybil.rdf.NameSpace;
import org.vcell.sybil.rdf.OntUtil;
import org.vcell.sybil.rdf.schemas.SBPAX;

import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SYBREAMO {

	public static final String URI = "http://vcell.org/2008/11/sybreamo";
	
	public static final NameSpace ns = new NameSpace("sybream", URI + "#");
	
	public static final OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

	static {
		OntUtil.emptyPrefixMapping(schema);
		schema.setNsPrefix("", ns.uri);
		schema.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		schema.setNsPrefix("rdfs", RDFS.getURI());
		schema.setNsPrefix("owl", OWL.NS);
		schema.setNsPrefix("rdf", RDF.getURI());
		schema.setNsPrefix("sbpax", SBPAX.ns.uri);
	}
	
	public static final Ontology ontology = schema.createOntology(URI);
	
	static {
		ontology.addImport(SBPAX.ontology);
	}
	
	public static final OntClass Assumption = schema.createClass(ns + "Assumption");
	public static final OntClass SubstanceUnited = schema.createClass(ns + "SubstanceUnited");
	public static final OntClass SubstanceClassUnited = schema.createClass(ns + "SubstanceClassUnited");
	
	public static final ObjectProperty appliesTo = schema.createObjectProperty(ns + "appliesTo");
	public static final ObjectProperty appliesToClass = schema.createObjectProperty(ns + "appliesToClass");
	public static final ObjectProperty appliesToSubstance = 
		schema.createObjectProperty(ns + "appliesToSubstance");
	
	static {
		Assumption.addSubClass(SubstanceUnited);
		Assumption.addSubClass(SubstanceClassUnited);

		appliesTo.addSubProperty(appliesToClass);
		appliesTo.addSubProperty(appliesToSubstance);

		
		appliesTo.setDomain(Assumption);
		appliesTo.setRange(OWL.Thing);
		appliesToClass.setDomain(SubstanceClassUnited);
		appliesToClass.setRange(OWL.Class);
		appliesToSubstance.setDomain(SubstanceUnited);
		appliesToSubstance.setRange(SBPAX.Substance);

		schema.createCardinalityRestriction(null, appliesToSubstance, 1).addSubClass(SubstanceUnited);
		schema.createCardinalityRestriction(null, appliesToClass, 1).addSubClass(SubstanceClassUnited);
	}
	
}

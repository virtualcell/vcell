package org.vcell.sybil.rdf.schemas;

/*   BioPAX2  --- by Oliver Ruebenacker, UCHC --- April 2008 to March 2010
 *   Resources of the BioPAX Level 2 schema
 */

import java.util.List;
import java.util.Vector;

import org.vcell.sybil.rdf.NameSpace;
import org.vcell.sybil.rdf.OntUtil;
import org.vcell.sybil.util.sets.SetUtil;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class BioPAX2 {
	
	public static final OntologyBox box = new OntologyBox() {
		public Model getRdf() { return schema; }
		public String uri() { return URI; }
		public NameSpace ns() { return ns; }
		public String label() { return label; }
		public List<DatatypeProperty> labelProperties() { return labelProperties; }
	};

	public static final String label = "BioPAX L2";
	
	public static final String URI = "http://www.biopax.org/release/biopax-level2.owl";
	
	public static final NameSpace ns = new NameSpace("bp", URI + "#");
	
	public static final OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

	static {
		OntUtil.emptyPrefixMapping(schema);
		schema.setNsPrefix("", ns.uri);
		schema.setNsPrefix("rdfs", RDFS.getURI());
		schema.setNsPrefix("owl", OWL.NS);
		schema.setNsPrefix("rdf", RDF.getURI());
	}
	
	public static final Ontology ontology = schema.createOntology(URI);
	
	public static final OntClass biochemicalReaction = schema.createClass(ns + "biochemicalReaction");
	public static final OntClass bioSource = schema.createClass(ns + "bioSource");
	public static final OntClass catalysis = schema.createClass(ns + "catalysis");
	public static final OntClass chemicalStructure = schema.createClass(ns + "chemicalStructure");
	public static final OntClass complex = schema.createClass(ns + "complex");
	public static final OntClass complexAssembly = schema.createClass(ns + "complexAssembly");
	public static final OntClass confidence = schema.createClass(ns + "confidence");
	public static final OntClass control = schema.createClass(ns + "control");
	public static final OntClass conversion = schema.createClass(ns + "conversion");
	public static final OntClass dataSource = schema.createClass(ns + "dataSource");
	public static final OntClass deltaGprimeO = schema.createClass(ns + "deltaGprimeO");
	public static final OntClass dna = schema.createClass(ns + "dna");
	public static final OntClass entity = schema.createClass(ns + "entity");
	public static final OntClass evidence = schema.createClass(ns + "evidence");
	public static final OntClass experimentalForm = schema.createClass(ns + "experimentalForm");
	public static final OntClass externalReferenceUtilityClass = 
		schema.createClass(ns + "externalReferenceUtilityClass");
	public static final OntClass interaction = schema.createClass(ns + "interaction");
	public static final OntClass kPrime = schema.createClass(ns + "kPrime");
	public static final OntClass modulation = schema.createClass(ns + "modulation");
	public static final OntClass openControlledVocabulary = 
		schema.createClass(ns + "openControlledVocabulary");
	public static final OntClass pathway = schema.createClass(ns + "pathway");
	public static final OntClass physicalEntity = schema.createClass(ns + "physicalEntity");
	public static final OntClass physicalEntityParticipant = 
		schema.createClass(ns + "physicalEntityParticipant");
	public static final OntClass physicalInteraction = schema.createClass(ns + "physicalInteraction");
	public static final OntClass pathwayStep = schema.createClass(ns + "pathwayStep");
	public static final OntClass publicationXref = schema.createClass(ns + "publicationXref");
	public static final OntClass protein = schema.createClass(ns + "protein");
	public static final OntClass relationshipXref = schema.createClass(ns + "relationshipXref");	
	public static final OntClass rna = schema.createClass(ns + "rna");
	public static final OntClass sequenceFeature = schema.createClass(ns + "sequenceFeature");
	public static final OntClass sequenceInterval = schema.createClass(ns + "sequenceInterval");
	public static final OntClass sequenceLocation = schema.createClass(ns + "sequenceLocation");
	public static final OntClass sequenceParticipant = schema.createClass(ns + "sequenceParticipant");
	public static final OntClass sequenceSite = schema.createClass(ns + "sequenceSite");
	public static final OntClass smallMolecule = schema.createClass(ns + "smallMolecule");
	public static final OntClass transport = schema.createClass(ns + "transport");
	public static final OntClass transportWithBiochemicalReaction = 
		schema.createClass(ns + "transportWithBiochemicalReaction");
	public static final OntClass unificationXref = schema.createClass(ns + "unificationXref");
	public static final OntClass utilityClass = schema.createClass(ns + "utilityClass");
	public static final OntClass xref = schema.createClass(ns + "xref");
	
	public static final DatatypeProperty AUTHORS = schema.createDatatypeProperty(ns + "AUTHORS");
	public static final DatatypeProperty AVAILABILITY = schema.createDatatypeProperty(ns + "AVAILABILITY");
	public static final DatatypeProperty DB = schema.createDatatypeProperty(ns + "DB");
	public static final DatatypeProperty CHEMICAL_FORMULA = 
		schema.createDatatypeProperty(ns + "CHEMICAL-FORMULA");
	public static final DatatypeProperty CONFIDENCE_VALUE = 
		schema.createDatatypeProperty(ns + "CONFIDENCE-VALUE");
	public static final DatatypeProperty CONTROL_TYPE = 
		schema.createDatatypeProperty(ns + "CONTROL-TYPE");
	public static final DatatypeProperty COMMENT = schema.createDatatypeProperty(ns + "COMMENT");
	public static final DatatypeProperty DB_VERSION = schema.createDatatypeProperty(ns + "DB-VERSION");
	public static final DatatypeProperty DELTA_G_PRIME_O = 
		schema.createDatatypeProperty(ns + "DELTA-G-PRIME-O");
	public static final DatatypeProperty DELTA_H = schema.createDatatypeProperty(ns + "DELTA-H");
	public static final DatatypeProperty DELTA_S = schema.createDatatypeProperty(ns + "DELTA-S");
	public static final DatatypeProperty DIRECTION = schema.createDatatypeProperty(ns + "DIRECTION");
	public static final DatatypeProperty EC_NUMBER = schema.createDatatypeProperty(ns + "EC-NUMBER");
	public static final DatatypeProperty ID = schema.createDatatypeProperty(ns + "ID");
	public static final DatatypeProperty ID_VERSION = schema.createDatatypeProperty(ns + "ID-VERSION");
	public static final DatatypeProperty IONIC_STRENGTH = 
		schema.createDatatypeProperty(ns + "IONIC-STRENGTH");
	public static final DatatypeProperty K_PRIME = schema.createDatatypeProperty(ns + "K-PRIME");
	public static final DatatypeProperty MOLECULAR_WEIGHT = 
		schema.createDatatypeProperty(ns + "MOLECULAR-WEIGHT");
	public static final DatatypeProperty NAME = schema.createDatatypeProperty(ns + "NAME");
	public static final DatatypeProperty PH = schema.createDatatypeProperty(ns + "PH");
	public static final DatatypeProperty PMG = schema.createDatatypeProperty(ns + "PMG");
	public static final DatatypeProperty POSITION_STATUS = 
		schema.createDatatypeProperty(ns + "POSITION-STATUS");
	public static final DatatypeProperty RELATIONSHIP_TYPE = 
		schema.createDatatypeProperty(ns + "RELATIONSHIP-TYPE");
	public static final DatatypeProperty SEQUENCE = schema.createDatatypeProperty(ns + "SEQUENCE");
	public static final DatatypeProperty SEQUENCE_POSITION = 
		schema.createDatatypeProperty(ns + "SEQUENCE-POSITION");
	public static final DatatypeProperty SHORT_NAME = schema.createDatatypeProperty(ns + "SHORT-NAME");
	public static final DatatypeProperty SPONTANEOUS = schema.createDatatypeProperty(ns + "SPONTANEOUS");
	public static final DatatypeProperty SOURCE = schema.createDatatypeProperty(ns + "SOURCE");
	public static final DatatypeProperty STOICHIOMETRIC_COEFFICIENT = 
		schema.createDatatypeProperty(ns + "STOICHIOMETRIC-COEFFICIENT");
	public static final DatatypeProperty STRUCTURE_DATA = 
		schema.createDatatypeProperty(ns + "STRUCTURE-DATA");
	public static final DatatypeProperty STRUCTURE_FORMAT = 
		schema.createDatatypeProperty(ns + "STRUCTURE-FORMAT");
	public static final DatatypeProperty SYNONYMS = schema.createDatatypeProperty(ns + "SYNONYMS");
	public static final DatatypeProperty TEMPERATURE = schema.createDatatypeProperty(ns + "TEMPERATURE");
	public static final DatatypeProperty TERM = schema.createDatatypeProperty(ns + "TERM");
	public static final DatatypeProperty TITLE = schema.createDatatypeProperty(ns + "TITLE");
	public static final DatatypeProperty URL = schema.createDatatypeProperty(ns + "URL");
	public static final DatatypeProperty YEAR = schema.createDatatypeProperty(ns + "YEAR");
	
	public static final ObjectProperty CELLTYPE = schema.createObjectProperty(ns + "CELLTYPE");
	public static final ObjectProperty CELLULAR_LOCATION = 
		schema.createObjectProperty(ns + "CELLULAR-LOCATION");
	public static final ObjectProperty COFACTOR = schema.createObjectProperty(ns + "COFACTOR");
	public static final ObjectProperty COMPONENTS = schema.createObjectProperty(ns + "COMPONENTS");
	public static final ObjectProperty CONFIDENCE = schema.createObjectProperty(ns + "CONFIDENCE");
	public static final ObjectProperty CONTROLLED = schema.createObjectProperty(ns + "CONTROLLED");
	public static final ObjectProperty CONTROLLER = schema.createObjectProperty(ns + "CONTROLLER");
	public static final ObjectProperty DATA_SOURCE = schema.createObjectProperty(ns + "DATA-SOURCE");
	public static final ObjectProperty DELTA_G = schema.createObjectProperty(ns + "DELTA-G");
	public static final ObjectProperty EVIDENCE = schema.createObjectProperty(ns + "EVIDENCE");
	public static final ObjectProperty EVIDENCE_CODE = schema.createObjectProperty(ns + "EVIDENCE-CODE");
	public static final ObjectProperty EXPERIMENTAL_FORM = 
		schema.createObjectProperty(ns + "EXPERIMENTAL-FORM");
	public static final ObjectProperty EXPERIMENTAL_FORM_TYPE = 
		schema.createObjectProperty(ns + "EXPERIMENTAL-FORM-TYPE");
	public static final ObjectProperty FEATURE_LOCATION = 
		schema.createObjectProperty(ns + "FEATURE-LOCATION");
	public static final ObjectProperty FEATURE_TYPE = schema.createObjectProperty(ns + "FEATURE-TYPE");
	public static final ObjectProperty INTERACTION_TYPE = 
		schema.createObjectProperty(ns + "INTERACTION-TYPE");
	public static final ObjectProperty KEQ = schema.createObjectProperty(ns + "KEQ");
	public static final ObjectProperty LEFT = schema.createObjectProperty(ns + "LEFT");
	public static final ObjectProperty NEXT_STEP = schema.createObjectProperty(ns + "NEXT-STEP");
	public static final ObjectProperty ORGANISM = schema.createObjectProperty(ns + "ORGANISM");
	public static final ObjectProperty PARTICIPANT = schema.createObjectProperty(ns + "PARTICIPANT");
	public static final ObjectProperty PARTICIPANTS = schema.createObjectProperty(ns + "PARTICIPANTS");
	public static final ObjectProperty PATHWAY_COMPONENTS = 
		schema.createObjectProperty(ns + "PATHWAY-COMPONENTS");
	public static final ObjectProperty PHYSICAL_ENTITY = 
		schema.createObjectProperty(ns + "PHYSICAL-ENTITY");
	public static final ObjectProperty RIGHT = schema.createObjectProperty(ns + "RIGHT");
	public static final ObjectProperty SEQUENCE_INTERVAL_BEGIN = 
		schema.createObjectProperty(ns + "SEQUENCE-INTERVAL-BEGIN");
	public static final ObjectProperty SEQUENCE_INTERVAL_END = 
		schema.createObjectProperty(ns + "SEQUENCE-INTERVAL-END");
	public static final ObjectProperty SEQUENCE_FEATURE_LIST = 
		schema.createObjectProperty(ns + "SEQUENCE-FEATURE-LIST");
	public static final ObjectProperty STEP_INTERACTIONS = 
		schema.createObjectProperty(ns + "STEP-INTERACTIONS");
	public static final ObjectProperty STRUCTURE = schema.createObjectProperty(ns + "STRUCTURE");
	public static final ObjectProperty TAXON_XREF = schema.createObjectProperty(ns + "TAXON-XREF");
	public static final ObjectProperty TISSUE = schema.createObjectProperty(ns + "TISSUE");
	public static final ObjectProperty XREF = schema.createObjectProperty(ns + "XREF");

	static {
		biochemicalReaction.addSubClass(transportWithBiochemicalReaction);
		control.addSubClass(catalysis);
		control.addSubClass(modulation);
		conversion.addSubClass(biochemicalReaction);
		conversion.addSubClass(complexAssembly);
		conversion.addSubClass(transport);
		entity.addSubClass(interaction);
		entity.addSubClass(pathway);
		entity.addSubClass(physicalEntity);
		externalReferenceUtilityClass.addSubClass(bioSource);
		externalReferenceUtilityClass.addSubClass(dataSource);
		externalReferenceUtilityClass.addSubClass(openControlledVocabulary);
		externalReferenceUtilityClass.addSubClass(xref);
		interaction.addSubClass(physicalInteraction);
		physicalEntity.addSubClass(complex);
		physicalEntity.addSubClass(dna);
		physicalEntity.addSubClass(protein);
		physicalEntity.addSubClass(rna);
		physicalEntity.addSubClass(smallMolecule);
		physicalEntityParticipant.addSubClass(sequenceParticipant);
		physicalInteraction.addSubClass(conversion);
		physicalInteraction.addSubClass(control);
		sequenceLocation.addSubClass(sequenceInterval);
		sequenceLocation.addSubClass(sequenceSite);
		transport.addSubClass(transportWithBiochemicalReaction);		
		utilityClass.addSubClass(chemicalStructure);
		utilityClass.addSubClass(confidence);
		utilityClass.addSubClass(deltaGprimeO);
		utilityClass.addSubClass(evidence);
		utilityClass.addSubClass(experimentalForm);
		utilityClass.addSubClass(externalReferenceUtilityClass);
		utilityClass.addSubClass(kPrime);
		utilityClass.addSubClass(pathwayStep);
		utilityClass.addSubClass(physicalEntityParticipant);
		utilityClass.addSubClass(sequenceFeature);
		utilityClass.addSubClass(sequenceLocation);
		xref.addSubClass(publicationXref);
		xref.addSubClass(relationshipXref);
		xref.addSubClass(unificationXref);
		
		PARTICIPANTS.addSubProperty(COFACTOR);
		PARTICIPANTS.addSubProperty(CONTROLLED);
		PARTICIPANTS.addSubProperty(CONTROLLER);
		PARTICIPANTS.addSubProperty(LEFT);
		PARTICIPANTS.addSubProperty(RIGHT);
		
		AUTHORS.setDomain(publicationXref);
		AVAILABILITY.setDomain(entity);
		DATA_SOURCE.setDomain(entity);
		DB.setDomain(xref);
		CELLTYPE.setDomain(bioSource);
		CELLULAR_LOCATION.setDomain(physicalEntityParticipant);
		CHEMICAL_FORMULA.setDomain(smallMolecule);
		COFACTOR.setDomain(catalysis);
		COMMENT.setDomain(schema.createUnionClass(null, 
				schema.createList().with(utilityClass).with(entity)));
		COMPONENTS.setDomain(complex);
		CONFIDENCE_VALUE.setDomain(confidence);
		CONFIDENCE.setDomain(evidence);
		CONTROL_TYPE.setDomain(control);
		CONTROLLED.setDomain(control);
		CONTROLLER.setDomain(control);
		DB_VERSION.setDomain(xref);
		DELTA_G.setDomain(biochemicalReaction);
		DELTA_G_PRIME_O.setDomain(deltaGprimeO);
		DELTA_G.setDomain(biochemicalReaction);
		DELTA_H.setDomain(biochemicalReaction);
		DELTA_S.setDomain(biochemicalReaction);
		DIRECTION.setDomain(catalysis);
		EC_NUMBER.setDomain(biochemicalReaction);
		EVIDENCE.setDomain(schema.createUnionClass(null, 
				schema.createList().with(interaction).with(pathway)));
		EVIDENCE_CODE.setDomain(evidence);
		EXPERIMENTAL_FORM.setDomain(evidence);
		EXPERIMENTAL_FORM_TYPE.setDomain(experimentalForm);
		FEATURE_TYPE.setDomain(sequenceFeature);
		ID.setDomain(xref);
		ID_VERSION.setDomain(xref);
		INTERACTION_TYPE.setDomain(physicalInteraction);
		IONIC_STRENGTH.setDomain(schema.createUnionClass(null, 
				schema.createList().with(kPrime).with(deltaGprimeO)));
		K_PRIME.setDomain(kPrime);
		KEQ.setDomain(biochemicalReaction);
		LEFT.setDomain(conversion);
		MOLECULAR_WEIGHT.setDomain(smallMolecule);
		NAME.setDomain(schema.createUnionClass(null, 
				schema.createList().with(bioSource).with(entity).with(dataSource).with(sequenceFeature)));
		NEXT_STEP.setDomain(pathwayStep);
		ORGANISM.setDomain(schema.createUnionClass(null, 
				schema.createList().with(rna).with(protein).with(pathway).with(complex).with(dna)));
		PARTICIPANT.setDomain(experimentalForm);
		PARTICIPANTS.setDomain(interaction);
		PATHWAY_COMPONENTS.setDomain(pathway);
		PH.setDomain(schema.createUnionClass(null, schema.createList().with(kPrime).with(deltaGprimeO)));
		PHYSICAL_ENTITY.setDomain(physicalEntityParticipant);
		PMG.setDomain(schema.createUnionClass(null, schema.createList().with(kPrime).with(deltaGprimeO)));
		POSITION_STATUS.setDomain(sequenceSite);
		RELATIONSHIP_TYPE.setDomain(relationshipXref);
		RIGHT.setDomain(conversion);
		SEQUENCE.setDomain(schema.createUnionClass(null, 
				schema.createList().with(rna).with(protein).with(dna)));
		SEQUENCE_FEATURE_LIST.setDomain(sequenceParticipant);
		SEQUENCE_INTERVAL_BEGIN.setDomain(sequenceInterval);
		SEQUENCE_INTERVAL_END.setDomain(sequenceInterval);
		FEATURE_LOCATION.setDomain(sequenceFeature);
		SEQUENCE_POSITION.setDomain(sequenceSite);
		SHORT_NAME.setDomain(schema.createUnionClass(null, 
				schema.createList().with(sequenceFeature).with(entity)));
		SOURCE.setDomain(publicationXref);
		SPONTANEOUS.setDomain(conversion);
		STEP_INTERACTIONS.setDomain(pathwayStep);
		STOICHIOMETRIC_COEFFICIENT.setDomain(physicalEntityParticipant);
		STRUCTURE.setDomain(smallMolecule);
		STRUCTURE_DATA.setDomain(chemicalStructure);
		STRUCTURE_FORMAT.setDomain(chemicalStructure);
		SYNONYMS.setDomain(schema.createUnionClass(null, 
				schema.createList().with(sequenceFeature).with(entity)));
		TEMPERATURE.setDomain(schema.createUnionClass(null, 
				schema.createList().with(kPrime).with(deltaGprimeO)));
		TAXON_XREF.setDomain(bioSource);
		TISSUE.setDomain(bioSource);
		TERM.setDomain(openControlledVocabulary);
		TITLE.setDomain(publicationXref);
		URL.setDomain(publicationXref);
		YEAR.setDomain(publicationXref);
		XREF.setDomain(schema.createUnionClass(null, 
				schema.createList().with(openControlledVocabulary).with(entity)
				.with(sequenceFeature).with(dataSource).with(evidence).with(confidence)));
		
		AUTHORS.setRange(XSD.xstring);
		AVAILABILITY.setRange(XSD.xstring);
		DB.setRange(XSD.xstring);
		CELLTYPE.setRange(openControlledVocabulary);
		CELLULAR_LOCATION.setRange(openControlledVocabulary);
		CHEMICAL_FORMULA.setRange(XSD.xstring);
		COFACTOR.setRange(physicalEntityParticipant);
		COMMENT.setRange(XSD.xstring);		
		COMPONENTS.setRange(physicalEntityParticipant);
		CONFIDENCE.setRange(confidence);
		CONFIDENCE_VALUE.setRange(XSD.xstring);
		CONTROL_TYPE.setRange(schema.createDataRange(
				schema.createList().with(schema.createTypedLiteral("INHIBITION"))
				.with(schema.createTypedLiteral("ACTIVATION"))
				.with(schema.createTypedLiteral("INHIBITION-ALLOSTERIC"))
				.with(schema.createTypedLiteral("INHIBITION-COMPETITIVE"))
				.with(schema.createTypedLiteral("INHIBITION-IRREVERSIBLE"))
				.with(schema.createTypedLiteral("INHIBITION-NONCOMPETITIVE"))
				.with(schema.createTypedLiteral("INHIBITION-OTHER"))
				.with(schema.createTypedLiteral("INHIBITION-UNCOMPETITIVE"))
				.with(schema.createTypedLiteral("ACTIVATION-NONALLOSTERIC"))
				.with(schema.createTypedLiteral("ACTIVATION-ALLOSTERIC"))));
		CONTROLLED.setRange(schema.createUnionClass(null, 
				schema.createList().with(interaction).with(pathway)));
		CONTROLLER.setRange(physicalEntityParticipant);
		DATA_SOURCE.setRange(dataSource);
		DB_VERSION.setRange(XSD.xstring);
		DELTA_G.setRange(deltaGprimeO);
		DELTA_G_PRIME_O.setRange(XSD.xfloat);
		DELTA_G.setRange(deltaGprimeO);
		DELTA_H.setRange(XSD.xdouble);
		DELTA_S.setRange(XSD.xdouble);
		DIRECTION.setRange(schema.createDataRange(
				schema.createList().with(schema.createTypedLiteral("REVERSIBLE"))
				.with(schema.createTypedLiteral("PHYSIOL-LEFT-TO-RIGHT"))
				.with(schema.createTypedLiteral("PHYSIOL-RIGHT-TO-LEFT"))
				.with(schema.createTypedLiteral("IRREVERSIBLE-LEFT-TO-RIGHT"))
				.with(schema.createTypedLiteral("IRREVERSIBLE-RIGHT-TO-LEFT"))));
		EC_NUMBER.setRange(XSD.xstring);
		EVIDENCE.setRange(evidence);
		EVIDENCE_CODE.setRange(openControlledVocabulary);
		EXPERIMENTAL_FORM.setRange(experimentalForm);
		EXPERIMENTAL_FORM_TYPE.setRange(openControlledVocabulary);
		FEATURE_LOCATION.setRange(sequenceLocation);
		FEATURE_TYPE.setRange(openControlledVocabulary);
		ID.setRange(XSD.xstring);
		ID_VERSION.setRange(XSD.xstring);
		INTERACTION_TYPE.setRange(openControlledVocabulary);
		IONIC_STRENGTH.setRange(XSD.xfloat);
		K_PRIME.setRange(XSD.xfloat);
		KEQ.setRange(kPrime);
		LEFT.setRange(physicalEntityParticipant);
		MOLECULAR_WEIGHT.setRange(XSD.xdouble);
		NAME.setRange(XSD.xstring);
		NEXT_STEP.setRange(pathwayStep);
		ORGANISM.setRange(bioSource);
		PARTICIPANT.setRange(physicalEntityParticipant);
		PARTICIPANTS.setRange(schema.createUnionClass(null, 
				schema.createList().with(entity).with(physicalEntityParticipant)));
		PATHWAY_COMPONENTS.setRange(schema.createUnionClass(null, 
				schema.createList().with(interaction).with(pathwayStep).with(pathway)));
		PH.setRange(XSD.xfloat);
		PHYSICAL_ENTITY.setRange(physicalEntity);
		PMG.setRange(XSD.xfloat);
		POSITION_STATUS.setRange(schema.createDataRange(
				schema.createList().with(schema.createTypedLiteral("EQUAL"))
				.with(schema.createTypedLiteral("LESS-THAN"))
				.with(schema.createTypedLiteral("GREATER-THAN"))));
		RELATIONSHIP_TYPE.setRange(XSD.xstring);
		RIGHT.setRange(physicalEntityParticipant);
		SEQUENCE.setRange(XSD.xstring);		
		SEQUENCE_FEATURE_LIST.setRange(sequenceFeature);
		SEQUENCE_INTERVAL_BEGIN.setRange(sequenceSite);
		SEQUENCE_INTERVAL_END.setRange(sequenceSite);
		SEQUENCE_POSITION.setRange(XSD.integer);		
		SHORT_NAME.setRange(XSD.xstring);
		SOURCE.setRange(XSD.xstring);
		SPONTANEOUS.setRange(schema.createDataRange(
				schema.createList().with(schema.createTypedLiteral("L-R"))
				.with(schema.createTypedLiteral("R-L"))
				.with(schema.createTypedLiteral("NOT-SPONTANEOUS"))));
		STEP_INTERACTIONS.setRange(schema.createUnionClass(null, 
				schema.createList().with(interaction).with(pathway)));
		STOICHIOMETRIC_COEFFICIENT.setRange(XSD.xdouble);
		STRUCTURE.setRange(chemicalStructure);
		STRUCTURE_DATA.setRange(XSD.xstring);
		STRUCTURE_FORMAT.setRange(schema.createDataRange(
				schema.createList().cons(schema.createTypedLiteral("InChI"))
				.cons(schema.createTypedLiteral("SMILES"))
				.cons(schema.createTypedLiteral("CML"))));
		SYNONYMS.setRange(XSD.xstring);
		TAXON_XREF.setRange(unificationXref);
		TISSUE.setRange(openControlledVocabulary);
		TERM.setRange(XSD.xstring);
		TEMPERATURE.setRange(XSD.xfloat);
		TITLE.setRange(XSD.xstring);
		URL.setRange(XSD.xstring);
		YEAR.setRange(XSD.xint);
		XREF.setRange(xref);
		
		entity.setPropertyValue(RDFS.subClassOf, OWL.Thing);
		
		bioSource.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, TAXON_XREF, 1));
		chemicalStructure.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, STRUCTURE_DATA, 1));
		chemicalStructure.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, STRUCTURE_FORMAT, 1));
		confidence.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, CONFIDENCE_VALUE, 1));
		deltaGprimeO.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, DELTA_G_PRIME_O, 1));
		experimentalForm.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, PARTICIPANT, 1));
		kPrime.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, K_PRIME, 1));
		physicalEntityParticipant.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, PHYSICAL_ENTITY, 1));
		relationshipXref.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, DB, 1));
		relationshipXref.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, ID, 1));
		relationshipXref.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, RELATIONSHIP_TYPE, 1));
		sequenceFeature.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, FEATURE_TYPE, 1));
		sequenceInterval.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, SEQUENCE_INTERVAL_BEGIN, 1));
		sequenceInterval.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, SEQUENCE_INTERVAL_END, 1));
		sequenceSite.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, POSITION_STATUS, 1));
		sequenceSite.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, SEQUENCE_POSITION, 1));
		unificationXref.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, DB, 1));
		unificationXref.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, ID, 1));

		confidence.addProperty(RDFS.subClassOf, 
				schema.createMinCardinalityRestriction(null, XREF, 1));
		evidence.addProperty(RDFS.subClassOf, schema.createUnionClass(null, 
				schema.createList().with(schema.createMinCardinalityRestriction(null, CONFIDENCE, 1))
				.with(schema.createMinCardinalityRestriction(null, EVIDENCE_CODE, 1))
				.with(schema.createMinCardinalityRestriction(null, EXPERIMENTAL_FORM, 1))));
		experimentalForm.addProperty(RDFS.subClassOf, 
				schema.createMinCardinalityRestriction(null, EXPERIMENTAL_FORM_TYPE, 1));
		pathwayStep.addProperty(RDFS.subClassOf, 
				schema.createMinCardinalityRestriction(null, STEP_INTERACTIONS, 1));
		
		bioSource.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, CELLTYPE, 1));
		bioSource.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, NAME, 1));
		bioSource.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, TISSUE, 1));
		catalysis.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, CONTROLLED, 1));
		catalysis.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, CONTROLLER, 1));
		catalysis.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, DIRECTION, 1));
		control.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, CONTROL_TYPE, 1));
		conversion.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, SPONTANEOUS, 1));
		deltaGprimeO.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, IONIC_STRENGTH, 1));
		deltaGprimeO.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, PH, 1));
		deltaGprimeO.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, PMG, 1));
		deltaGprimeO.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, TEMPERATURE, 1));
		entity.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, NAME, 1));
		entity.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, SHORT_NAME, 1));
		kPrime.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, IONIC_STRENGTH, 1));
		kPrime.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, PH, 1));
		kPrime.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, PMG, 1));
		kPrime.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, TEMPERATURE, 1));
		modulation.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, CONTROLLED, 1));
		modulation.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, CONTROLLER, 1));
		pathway.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, ORGANISM, 1));
		physicalEntityParticipant.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, CELLULAR_LOCATION, 1));
		physicalEntityParticipant.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, STOICHIOMETRIC_COEFFICIENT, 1));
		protein.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, ORGANISM, 1));
		protein.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, SEQUENCE, 1));
		publicationXref.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, TITLE, 1));
		publicationXref.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, YEAR, 1));
		rna.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, ORGANISM, 1));
		rna.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, SEQUENCE, 1));
		sequenceFeature.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, NAME, 1));
		sequenceFeature.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, SHORT_NAME, 1));
		smallMolecule.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, CHEMICAL_FORMULA, 1));
		smallMolecule.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, MOLECULAR_WEIGHT, 1));
		xref.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, DB, 1));
		xref.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, DB_VERSION, 1));
		xref.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, ID, 1));
		xref.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, ID_VERSION, 1));

		interaction.addProperty(RDFS.subClassOf, 
				schema.createSomeValuesFromRestriction(null, PARTICIPANTS, schema.createUnionClass(null, 
						schema.createList().with(entity).with(physicalEntityParticipant))));
		physicalInteraction.addProperty(RDFS.subClassOf, 
				schema.createSomeValuesFromRestriction(null, PARTICIPANTS, physicalEntityParticipant));

		catalysis.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, CONTROLLED, conversion));
		confidence.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, XREF, publicationXref));
		conversion.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, PARTICIPANTS, physicalEntityParticipant));
		dataSource.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, XREF, schema.createUnionClass(null, 
						schema.createList().with(publicationXref).with(unificationXref))));
		modulation.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, CONTROLLED, catalysis));
		openControlledVocabulary.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, XREF, unificationXref));
		
		catalysis.addProperty(RDFS.subClassOf, 
				schema.createHasValueRestriction(null, CONTROL_TYPE, 
						schema.createTypedLiteral("ACTIVATION")));
		
		OntUtil.makeAllDisjoint(SetUtil.newSet(complex, dna, protein, rna, smallMolecule));
		OntUtil.makeAllDisjoint(SetUtil.newSet(conversion, control));
		OntUtil.makeAllDisjoint(SetUtil.newSet(complexAssembly, transport));
		OntUtil.makeAllDisjoint(SetUtil.newSet(biochemicalReaction, complexAssembly));
		OntUtil.makeAllDisjoint(SetUtil.newSet(chemicalStructure, confidence, deltaGprimeO, evidence, 
				experimentalForm, externalReferenceUtilityClass, kPrime, pathwayStep, 
				physicalEntityParticipant, sequenceFeature, sequenceLocation));
		OntUtil.makeAllDisjoint(SetUtil.newSet(interaction, pathway, physicalEntity));
		OntUtil.makeAllDisjoint(SetUtil.newSet(publicationXref, relationshipXref, unificationXref));
		OntUtil.makeAllDisjoint(SetUtil.newSet(sequenceFeature, sequenceInterval, sequenceSite));
		OntUtil.makeAllDisjoint(SetUtil.newSet(modulation, catalysis));
		OntUtil.makeAllDisjoint(SetUtil.newSet(openControlledVocabulary, dataSource, xref, bioSource));
		OntUtil.makeAllDisjoint(SetUtil.newSet(entity, utilityClass));

	}
	
	private static void setTypedComment(OntResource resource, String comment) {
		resource.setPropertyValue(RDFS.comment, schema.createTypedLiteral(comment));
	}
	
	private static void setEnglishComment(OntResource resource, String comment) {
		resource.setPropertyValue(RDFS.comment, schema.createLiteral(comment, "en"));
	}
	
	static {

		setTypedComment(ontology, 
				"This is version 1.0 of the BioPAX Level 2 ontology.  " + 
				"The goal of the BioPAX group is to develop a common exchange format for " + 
				"biological pathway data.  More information is available at http://www.biopax.org.  " + 
				"This ontology is freely available under the LGPL " + 
				"(http://www.gnu.org/copyleft/lesser.html).");
		
		setTypedComment(biochemicalReaction, 
				"Definition: A conversion interaction in which one or " + 
				"more entities (substrates) " + 
				"undergo covalent changes to become one or more other entities (products). " + 
				"The substrates of biochemical reactions are defined in terms of sums of species. " + 
				"This is convention in biochemistry, and, in principle, all of the EC reactions " + 
				"should be biochemical reactions.\n" +
				"Examples: ATP + H2O = ADP + Pi\n" +
				"Comment: In the example reaction above, ATP is considered to be an equilibrium " + 
				"mixture of several species, namely ATP4-, HATP3-, H2ATP2-, MgATP2-, MgHATP-, and " + 
				"Mg2ATP. Additional species may also need to be considered if other ions " + 
				"(e.g. Ca2+) that bind ATP are present. Similar considerations apply to ADP and to " + 
				"inorganic phosphate (Pi). When writing biochemical reactions, it is not necessary " + 
				"to attach charges to the biochemical reactants or to include ions such as H+ and " + 
				"Mg2+ in the equation. The reaction is written in the direction specified by the EC " + 
				"nomenclature system, if applicable, regardless of the physiological direction(s) " + 
				"in which the reaction proceeds. Polymerization reactions involving large polymers " + 
				"whose structure is not explicitly captured should generally be represented as " + 
				"unbalanced reactions in which the monomer is consumed but the polymer remains " + 
				"unchanged, e.g. glycogen + glucose = glycogen.");

		setTypedComment(bioSource, 
				"Definition: The biological source of an entity (e.g. protein, RNA or DNA). " + 
				"Some entities are considered source-neutral (e.g. small molecules), and the " + 
				"biological source of others can be deduced from their constituentss " + 
				"(e.g. complex, pathway).\n" +
				"Examples: HeLa cells, human, and mouse liver tissue.");
		
		setTypedComment(catalysis, 
				"Definition: A control interaction in which a physical entity (a catalyst) " + 
				"increases the rate of a conversion interaction by lowering its activation energy. " + 
				"Instances of this class describe a pairing between a catalyzing entity and a " + 
				"catalyzed conversion.\n" +
				"Comment: A separate catalysis instance should be created for each different " + 
				"conversion that a physicalEntity may catalyze and for each different physicalEntity " + 
				"that may catalyze a conversion. For example, a bifunctional enzyme that catalyzes " + 
				"two different biochemical reactions would be linked to each of those biochemical " + 
				"reactions by two separate instances of the catalysis class. Also, catalysis " + 
				"reactions from multiple different organisms could be linked to the same generic " + 
				"biochemical reaction (a biochemical reaction is generic if it only includes small " + 
				"molecules). Generally, the enzyme catalyzing a conversion is known and the use of " + 
				"this class is obvious. In the cases where a catalyzed reaction is known to occur " + 
				"but the enzyme is not known, a catalysis instance should be created without a " + 
				"controller specified (i.e. the CONTROLLER property should remain empty).\n" +
				"Synonyms: facilitation, acceleration.\n" +
				"Examples: The catalysis of a biochemical reaction by an enzyme, the enabling of " + 
				"a transport interaction by a membrane pore complex, and the facilitation of a " + 
				"complex assembly by a scaffold protein. " + 
				"Hexokinase -> (The \"Glucose + ATP -> Glucose-6-phosphate +ADP\" reaction). " + 
				"A plasma membrane Na+/K+ ATPase is an active transporter (antiport pump) using " + 
				"the energy of ATP to pump Na+ out of the cell and K+ in. Na+ from cytoplasm to " + 
				"extracellular space would be described in a transport instance. K+ from " + 
				"extracellular space to cytoplasm would be described in a transport instance. " + 
				"The ATPase pump would be stored in a catalysis instance controlling each of the " + 
				"above transport instances. A biochemical reaction that does not occur by itself " + 
				"under physiological conditions, but has been observed to occur in the presence of " + 
				"cell extract, likely via one or more unknown enzymes present in the extract, " + 
				"would be stored in the CONTROLLED property, with the CONTROLLER property empty.");
		
		setTypedComment(chemicalStructure, 
				"Definition: Describes a small molecule structure. Structure information is stored " + 
				"in the property STRUCTURE-DATA, in one of three formats: the CML format " + 
				"(see URL www.xml-cml.org), the SMILES format " + 
				"(see URL www.daylight.com/dayhtml/smiles/) or the InChI format " + 
				"(http://www.iupac.org/inchi/). The STRUCTURE-FORMAT property specifies which " + 
				"format is used.\n" +
				"Comment: By virtue of the expressivity of CML, an instance of this class can " + 
				"also provide additional information about a small molecule, such as its " + 
				"chemical formula, names, and synonyms, if CML is used as the structure format.\n" +
				"Examples: The following SMILES string, which describes the structure of " + 
				"glucose-6-phosphate:\n" +
				"'C(OP(=O)(O)O)[CH]1([CH](O)[CH](O)[CH](O)[CH](O)O1)'.");
		
		setTypedComment(complex, 
				"Definition: A physical entity whose structure is comprised of other physical " + 
				"entities bound to each other non-covalently, at least one of which is a " + 
				"macromolecule (e.g. protein, DNA, or RNA). Complexes must be stable enough to " + 
				"function as a biological unit; in general, the temporary association of an enzyme " + 
				"with its substrate(s) should not be considered or represented as a complex. " + 
				"A complex is the physical product of an interaction (complexAssembly) and is not " + 
				"itself considered an interaction.\n" +
				"Comment: In general, complexes should not be defined recursively so that " + 
				"smaller complexes exist within larger complexes, i.e. a complex should not be a " + 
				"COMPONENT of another complex (see comments on the COMPONENT property). " + 
				"The boundaries on the size of complexes described by this class are not defined " + 
				"here, although elements of the cell as large and dynamic as, e.g., a mitochondrion " + 
				"would typically not be described using this class (later versions of this ontology " + 
				"may include a cellularComponent class to represent these). The strength of binding " + 
				"and the topology of the components cannot be described currently, but may be " + 
				"included in future versions of the ontology, depending on community need.\n" +
				"Examples: Ribosome, RNA polymerase II. Other examples of this class include " + 
				"complexes of multiple protein monomers and complexes of proteins and small molecules.");

		setTypedComment(complexAssembly, 
				"Definition: A conversion interaction in which a set of physical entities, " + 
				"at least one being a macromolecule (e.g. protein, RNA, DNA), aggregate via " + 
				"non-covalent interactions. One of the participants of a complexAssembly must be " + 
				"an instance of the class complex (via a physicalEntityParticipant instance).\n" +
				"Comment: This class is also used to represent complex disassembly. The assembly " + 
				"or disassembly of a complex is often a spontaneous process, in which case the " + 
				"direction of the complexAssembly (toward either assembly or disassembly) should " + 
				"be specified via the SPONTANEOUS property.\n" +
				"Synonyms: aggregation, complex formation\n" +
				"Examples: Assembly of the TFB2 and TFB3 proteins into the TFIIH complex, and " + 
				"assembly of the ribosome through aggregation of its subunits.\n" +
				"Note: The following are not examples of complex assembly: Covalent phosphorylation " + 
				"of a protein (this is a biochemicalReaction); the TFIIH complex itself " + 
				"(this is an instance of the complex class, not the complexAssembly class).");
		
		setTypedComment(confidence, 
				"Definition: Confidence that the containing instance actually occurs or exists in " + 
				"vivo, usually a statistical measure. The xref must contain at least on publication " + 
				"that describes the method used to determine the confidence. There is currently no " + 
				"standard way of describing confidence values, so any string is valid for the " + 
				"confidence value. In the future, a controlled vocabulary of accepted confidence " + 
				"values could become available, in which case it will likely be adopted for use " + 
				"here to describe the value.\n" +
				"Examples: The statistical significance of a result, e.g. \"p<0.05\".");
		
		setTypedComment(control, 
				"Definition: An interaction in which one entity regulates, modifies, or otherwise " + 
				"influences another. Two types of control interactions are defined: activation and " + 
				"inhibition.\n" +
				"Comment: In general, the targets of control processes (i.e. occupants of the " + 
				"CONTROLLED property) should be interactions. Conceptually, physical entities are " + 
				"involved in interactions (or events) and the events should be controlled or " + 
				"modified, not the physical entities themselves. For example, a kinase activating " + 
				"a protein is a frequent event in signaling pathways and is usually represented as " + 
				"an 'activation' arrow from the kinase to the substrate in signaling diagrams. " + 
				"This is an abstraction that can be ambiguous out of context. " + 
				"In BioPAX, this information should be captured as the kinase catalyzing " + 
				"(via an instance of the catalysis class) a reaction in which the substrate is " + 
				"phosphorylated, instead of as a control interaction in which the kinase activates " + 
				"the substrate. Since this class is a superclass for specific types of control, " + 
				"instances of the control class should only be created when none of its subclasses " + 
				"are applicable.\n" +
				"Synonyms: regulation, mediation\n" +
				"Examples: A small molecule that inhibits a pathway by an unknown mechanism " + 
				"controls the pathway.");
		
		setTypedComment(conversion, 
				"Definition: An interaction in which one or more entities is physically " + 
				"transformed into one or more other entities.\n" +
				"Comment: This class is designed to represent a simple, single-step " + 
				"transformation. Multi-step transformations, such as the conversion of glucose " + 
				"to pyruvate in the glycolysis pathway, should be represented as pathways, " + 
				"if known. Since it is a highly abstract class in the ontology, instances of the " + 
				"conversion class should never be created.\n" +
				"Examples: A biochemical reaction converts substrates to products, the process of " + 
				"complex assembly converts single molecules to a complex, transport converts " + 
				"entities in one compartment to the same entities in another compartment.");
		
		setTypedComment(dataSource, 
				"Definition: The direct source of this data. This does not store the trail of " + 
				"sources from the generation of the data to this point, only the last known source, " + 
				"such as a database. The XREF property may contain a publicationXref referencing " + 
				"a publication describing the data source (e.g. a database publication). " + 
				"A unificationXref may be used e.g. when pointing to an entry in a database of " + 
				"databases describing this database.\n" +
				"Examples: A database or person name.");
		
		setTypedComment(dna, 
				"Definition: A physical entity consisting of a sequence of deoxyribonucleotide " + 
				"monophosphates; a deoxyribonucleic acid.\n" +
				"Comment: This is not a 'gene', since gene is a genetic concept, not a physical " + 
				"entity. The concept of a gene may be added later in BioPAX.\n" +
				"Examples: a chromosome, a plasmid. A specific example is chromosome 7 of Homo sapiens.");
		
		setTypedComment(deltaGprimeO, 
				"Definition: For biochemical reactions, this property refers to the standard " + 
				"transformed Gibbs energy change for a reaction written in terms of biochemical " + 
				"reactants (sums of species), delta-G'<sup>o</sup>.\n\n" +
				"  delta-G'<sup>o</sup> = -RT lnK'\n" +
				"and\n" +
				"  delta-G'<sup>o</sup> = delta-H'<sup>o</sup> - T delta-S'<sup>o</sup>\n\n" +
				"delta-G'<sup>o</sup> has units of kJ/mol.  Like K', it is a function of " + 
				"temperature (T), ionic strength (I), pH, and " + 
				"pMg (pMg = -log<sub>10</sub>[Mg<sup>2+</sup>]). Therefore, these quantities must " + 
				"be specified, and values for DELTA-G for biochemical reactions are represented as " + 
				"5-tuples of the form (delta-G'<sup>o</sup> T I pH pMg).  This property may have " + 
				"multiple values, representing different measurements for delta-G'<sup>o</sup> " + 
				"obtained under the different experimental conditions listed in the 5-tuple.\n\n" +
				"(This definition from EcoCyc)");
		
		setTypedComment(entity, 
				"Definition: " +
				"A discrete biological unit used when describing pathways.\n" +
				"Comment: This is the root class for all biological concepts in the ontology, " + 
				"which include pathways, interactions and physical entities. " + 
				"As the most abstract class in the ontology, instances of the entity class should " + 
				"never be created. Instead, more specific classes should be used.\n"
				+ "Synonyms: thing, object, bioentity.");

		setTypedComment(evidence, 
				"Definition: The support for a particular assertion, such as the existence of an " + 
				"interaction or pathway. At least one of CONFIDENCE, EVIDENCE-CODE, or " + 
				"EXPERIMENTAL-FORM must be instantiated when creating an evidence instance. " + 
				"XREF may reference a publication describing the experimental evidence using a " + 
				"publicationXref or may store a description of the experiment in an experimental " + 
				"description database using a unificationXref (if the referenced experiment is the " + 
				"same) or relationshipXref (if it is not identical, but similar in some way e.g. " + 
				"similar in protocol). Evidence is meant to provide more information than just an " + 
				"xref to the source paper.\n" +
				"Examples: A description of a molecular binding assay that was used to detect a " + 
				"protein-protein interaction.");
		
		setTypedComment(experimentalForm, 
				"Definition: The form of a physical entity in a particular experiment, as it may " + 
				"be modified for purposes of experimental design.\n" +
				"Examples: A His-tagged protein in a binding assay. A protein can be tagged by " + 
				"multiple tags, so can have more than 1 experimental form type terms");
		
		setTypedComment(externalReferenceUtilityClass, 
				"Definition: A pointer to an external object, such as an entry in a database or a " + 
				"term in a controlled vocabulary.\n" +
				"Comment: This class is for organizational purposes only; direct instances of this " + 
				"class should not be created.");
		
		setTypedComment(interaction, 
				"Definition: A single biological relationship between two or more entities. " + 
				"An interaction cannot be defined without the entities it relates.\n" +
				"Comment: Since it is a highly abstract class in the ontology, instances of the " + 
				"interaction class should never be created. Instead, more specific classes should " + 
				"be used. Currently this class only has subclasses that define physical " + 
				"interactions; later levels of BioPAX may define other types of interactions, " + 
				"such as genetic (e.g. synthetic lethal).\n" +
				"Naming rationale: A number of names were considered for this concept, " + 
				"including \"process\", \"synthesis\" and \"relationship\"; Interaction was chosen " + 
				"as it is understood by biologists in a biological context and is compatible with " + 
				"PSI-MI.\n" +
				"Examples: protein-protein interaction, biochemical reaction, enzyme catalysis");
		
		setTypedComment(kPrime, 
				"Definition: The apparent equilibrium constant, K', and associated values.  " + 
				"Concentrations in the equilibrium constant equation refer to the total " + 
				"concentrations of  all forms of particular biochemical reactants. For example, " + 
				"in the equilibrium constant equation for the biochemical reaction in which ATP " + 
				"is hydrolyzed to ADP and inorganic phosphate:\n\n" +
				"K' = [ADP][P<sub>i</sub>]/[ATP],\n\n" +
				"The concentration of ATP refers to the total concentration of all of the " + 
				"following species:\n\n" +
				"[ATP] = [ATP<sup>4-</sup>] + [HATP<sup>3-</sup>] + [H<sub>2</sub>ATP<sup>2-</sup>] " + 
				"+ [MgATP<sup>2-</sup>] + [MgHATP<sup>-</sup>] + [Mg<sub>2</sub>ATP].\n\n" +
				"The apparent equilibrium constant is formally dimensionless, and can be kept so " + 
				"by inclusion of as many of the terms (1 mol/dm<sup>3</sup>) in the numerator " + 
				"or denominator as necessary.  It is a function of temperature (T), " + 
				"ionic strength (I), pH, and pMg (pMg = -log<sub>10</sub>[Mg<sup>2+</sup>]). " + 
				"Therefore, these quantities must be specified to be precise, and values for KEQ " + 
				"for biochemical reactions may be represented as 5-tuples of the " + 
				"form (K' T I pH pMg).  This property may have multiple values, representing " + 
				"different measurements for K' obtained under the different experimental " + 
				"conditions listed in the 5-tuple. (This definition adapted from EcoCyc)\n\n" +
				"See http://www.chem.qmul.ac.uk/iubmb/thermod/ for a thermodynamics tutorial.");
		
		setTypedComment(modulation, 
				"Definition: A control interaction in which a physical entity modulates a " + 
				"catalysis interaction. Biologically, most modulation interactions describe an " + 
				"interaction in which a small molecule alters the ability of an enzyme to catalyze " + 
				"a specific reaction. Instances of this class describe a pairing between a " + 
				"modulating entity and a catalysis interaction.\n" +
				"Comment: A separate modulation instance should be created for each different " + 
				"catalysis instance that a physical entity may modulate and for each different " + 
				"physical entity that may modulate a catalysis instance. A typical modulation " + 
				"instance has a small molecule as the controller entity and a catalysis instance " + 
				"as the controlled entity.\n" +
				"Examples: Allosteric activation and competitive inhibition of an enzyme's " + 
				"ability to catalyze a specific reaction.");
		
		setTypedComment(openControlledVocabulary, 
				"Definition: Used to import terms from external controlled vocabularies (CVs) " + 
				"into the ontology. To support consistency and compatibility, open, freely " + 
				"available CVs should be used whenever possible, such as the Gene Ontology (GO) " + 
				"or other open biological CVs listed on the OBO website " + 
				"(http://obo.sourceforge.net/).\n" +
				"Comment: The ID property in unification xrefs to GO and other OBO ontologies " + 
				"should include the ontology name in the ID property " + 
				"(e.g. ID=\"GO:0005634\" instead of ID=\"0005634\").");
		
		setTypedComment(pathway, 
				"Definition: A set or series of interactions, often forming a network, which " + 
				"biologists have found useful to group together for organizational, historic, " + 
				"biophysical or other reasons.\n" +
				"Comment: It is possible to define a pathway without specifying the interactions " + 
				"within the pathway. In this case, the pathway instance could consist simply of a " + 
				"name and could be treated as a 'black box'.\n" +
				"Synonyms: network\n" +
				"Examples: glycolysis, valine biosynthesis");
		
		setTypedComment(pathwayStep, 
				"Definition: A step in a pathway.\n" +
				"Comment: Multiple interactions may occur in a pathway step, each should be listed " + 
				"in the STEP-INTERACTIONS property. Order relationships between pathway steps may " + 
				"be established with the NEXT-STEP slot. This order may not be temporally " + 
				"meaningful for specific steps, such as for a pathway loop or a reversible reaction, " + 
				"but represents a directed graph of step relationships that can be useful for " + 
				"describing the overall flow of a pathway, as may be useful in a pathway diagram.\n" +
				"Example: A metabolic pathway may contain a pathway step composed of one " + 
				"biochemical reaction (BR1) and one catalysis (CAT1) instance, where CAT1 " + 
				"describes the catalysis of BR1.");
		
		setTypedComment(physicalEntity, 
				"Definition: An entity with a physical structure. A pool of entities, not a " + 
				"specific molecular instance of an entity in a cell.\n" +
				"Comment: This class serves as the super-class for all physical entities, " + 
				"although its current set of subclasses is limited to molecules. As a highly " + 
				"abstract class in the ontology, instances of the physicalEntity class should " + 
				"never be created. Instead, more specific classes should be used.\n" +
				"Synonyms: part, interactor, object\n" +
				"Naming rationale: It's difficult to find a name that encompasses all of the " + 
				"subclasses of this class without being too general. E.g. PSI-MI uses " + 
				"'interactor', BIND uses 'object', BioCyc uses 'chemicals'. physicalEntity seems " + 
				"to be a good name for this specialization of entity.\n" +
				"Examples: protein, small molecule, RNA");
		
		setTypedComment(physicalEntityParticipant, 
				"Definition: Any additional special characteristics of a physical entity in the " + 
				"context of an interaction or complex. These currently include stoichiometric " + 
				"coefficient and cellular location, but this list may be expanded in later levels.\n" +
				"Comment: PhysicalEntityParticipants should not be used in multiple interaction " + 
				"or complex instances. Instead, each interaction and complex should reference its " + 
				"own unique set of physicalEntityParticipants. The reason for this is that a user " + 
				"may add new information about a physicalEntityParticipant for one interaction or " + 
				"complex, such as the presence of a previously unknown post-translational " + 
				"modification, and unwittingly invalidate the physicalEntityParticipant for the " + 
				"other interactions or complexes that make use of it.\n" +
				"Example: In the interaction describing the transport of L-arginine into the " + 
				"cytoplasm in E. coli, the LEFT property in the interaction would be filled with " + 
				"an instance of physicalEntityParticipant that specified the location of " + 
				"L-arginine as periplasm and the stoichiometric coefficient as one.");
		
		setTypedComment(physicalInteraction, 
				"Definition: An interaction in which at least one participant is a physical entity, " + 
				"e.g. a binding event.\n" +
				"Comment: This class should be used by default for representing molecular " + 
				"interactions, such as those defined by PSI-MI level 2. The participants in a " + 
				"molecular interaction should be listed in the PARTICIPANTS slot. Note that this " + 
				"is one of the few cases in which the PARTICPANT slot should be directly populated " + 
				"with instances (see comments on the PARTICPANTS property in the interaction " + 
				"class description). If sufficient information on the nature of a molecular " + 
				"interaction is available, a more specific BioPAX interaction class should be used.\n" +
				"Example: Two proteins observed to interact in a yeast-two-hybrid experiment where " + 
				"there is not enough experimental evidence to suggest that the proteins are forming " + 
				"a complex by themselves without any indirect involvement of other proteins. This " + 
				"is the case for most large-scale yeast two-hybrid screens.");
		
		setTypedComment(protein, 
				"Definition: A physical entity consisting of a sequence of amino acids; " + 
				"a protein monomer; a single polypeptide chain.\n" +
				"Examples: The epidermal growth factor receptor (EGFR) protein.");
		
		setTypedComment(publicationXref, 
				"Definition: An xref that defines a reference to a publication such as a book, " +
				"journal article, web page, or software manual. The reference may or may not be in " + 
				"a database, although references to PubMed are preferred when possible. " + 
				"The publication should make a direct reference to the instance it is attached to.\n" +
				"Comment: Publication xrefs should make use of PubMed IDs wherever possible. " + 
				"The DB property of an xref to an entry in PubMed should use the string \"PubMed\" " + 
				"and not \"MEDLINE\".\n" +
				"Examples: PubMed:10234245");
		
		setTypedComment(relationshipXref,
				"Definition: An xref that defines a reference to an entity in an external resource " + 
				"that does not have the same biological identity as the referring entity.\n" +
				"Comment: There is currently no controlled vocabulary of relationship types for " + 
				"BioPAX, although one will be created in the future if a need develops.\n" +
				"Examples: A link between a gene G in a BioPAX data collection, and the protein " + 
				"product P of that gene in an external database. This is not a unification xref " + 
				"because G and P are different biological entities (one is a gene and one is a " + 
				"protein). Another example is a relationship xref for a protein that refers to the " + 
				"Gene Ontology biological process, e.g. 'immune response,' that the protein is " + 
				"involved in.");

		setTypedComment(rna, 
				"Definition: A physical entity consisting of a sequence of ribonucleotide " + 
				"monophosphates; a ribonucleic acid.\n" +
				"Examples: messengerRNA, microRNA, ribosomalRNA. " + 
				"A specific example is the let-7 microRNA.");
		
		setTypedComment(sequenceFeature, 
				"Definition: A feature on a sequence relevant to an interaction, such as a binding " + 
				"site or post-translational modification.\n" +
				"Examples: A phosphorylation on a protein.");
		
		setTypedComment(sequenceInterval,
				"Definition: Describes an interval on a sequence. All of the sequence from the " + 
				"begin site to the end site (inclusive) is described, not any subset.");

		setTypedComment(sequenceLocation,
				"Definition: A location on a nucleotide or amino acid sequence.\n" +
				"Comment: For organizational purposes only; direct instances of this class should not " + 
				"be created.");
		
		setTypedComment(sequenceParticipant,
				"Definition: A DNA, RNA or protein participant in an interaction.\n" +
				"Comment: See physicalEntityParticipant for more documentation.");

		setTypedComment(sequenceSite, 
				"Definition: Describes a site on a sequence, i.e. the position of a single " + 
				"nucleotide or amino acid.");
		
		setTypedComment(smallMolecule, 
				"Definition: Any bioactive molecule that is not a peptide, " + 
				"DNA, or RNA. Generally these are non-polymeric, but complex carbohydrates are " + 
				"not explicitly modeled as classes in this version of the ontology, thus are " + 
				"forced into this class.\n" +
				"Comment: Recently, a number of small molecule databases have become available " + 
				"to cross-reference from this class.\n"+
				"Examples: glucose, penicillin, phosphatidylinositol");
		
		setTypedComment(transport, 
				"Definition: A conversion interaction in which an entity (or set of entities) " + 
				"changes location within or with respect to the cell. A transport interaction does " + 
				"not include the transporter entity, even if one is required in order for the " + 
				"transport to occur. Instead, transporters are linked to transport interactions via " + 
				"the catalysis class.\n" +
				"Comment: Transport interactions do not involve chemical changes of the " + 
				"participant(s). These cases are handled by the transportWithBiochemicalReaction " + 
				"class.\n" +
				"Synonyms: translocation.\n" +
				"Examples: The movement of Na+ into the cell through an open voltage-gated channel.");
		
		setTypedComment(transportWithBiochemicalReaction, 
				"Definition: A conversion interaction that is both a biochemicalReaction and a " + 
				"transport. In transportWithBiochemicalReaction interactions, one or more of the " + 
				"substrates change both their location and their physical structure. Active " + 
				"transport reactions that use ATP as an energy source fall under this category, " + 
				"even if the only covalent change is the hydrolysis of ATP to ADP.\n" +
				"Comment: This class was added to support a large number of transport events in " + 
				"pathway databases that have a biochemical reaction during the transport process. " + 
				"It is not expected that other double inheritance subclasses will be added to the " + 
				"ontology at the same level as this class.\n" +
				"Examples: In the PEP-dependent phosphotransferase system, transportation of sugar " + 
				"into an E. coli cell is accompanied by the sugar's phosphorylation as it crosses " + 
				"the plasma membrane.");
		
		setTypedComment(unificationXref, 
				"Definition: A unification xref defines a reference to an entity in an external " + 
				"resource that has the same biological identity as the referring entity. For example, " + 
				"if one wished to link from a database record, C, describing a chemical compound " + 
				"in a BioPAX data collection to a record, C', describing the same chemical compound " + 
				"in an external database, one would use a unification xref since records C and C' " + 
				"describe the same biological identity. Generally, unification xrefs should be used " + 
				"whenever possible, although there are cases where they might not be useful, such as " + 
				"application to application data exchange.\n" +
				"Comment: Unification xrefs in physical entities are essential for data integration, " + 
				"but are less important in interactions. This is because unification xrefs on the " + 
				"physical entities in an interaction can be used to compute the equivalence of two " + 
				"interactions of the same type. An xref in a protein pointing to a gene, e.g. in " + 
				"the LocusLink database17, would not be a unification xref since the two entities " + 
				"do not have the same biological identity (one is a protein, the other is a gene). " + 
				"Instead, this link should be a captured as a relationship xref. References to an " + 
				"external controlled vocabulary term within the OpenControlledVocabulary class " + 
				"should use a unification xref where possible (e.g. GO:0005737).\n" +
				"Examples: An xref in a protein instance pointing to an entry in the Swiss-Prot " + 
				"database, and an xref in an RNA instance pointing to the corresponding RNA sequence " + 
				"in the RefSeq database..");
		
		setTypedComment(utilityClass, 
				"Definition: Utility classes are created when simple slots are insufficient to " + 
				"describe an aspect of an entity or to increase compatibility of this ontology with " + 
				"other standards.  The utilityClass class is actually a metaclass and is only present " + 
				"to organize the other helper classes under one class hierarchy; instances of " + 
				"utilityClass should never be created.");

		setTypedComment(xref, 
				"Definition: A reference from an instance of a class in this ontology to an " + 
				"object in an external resource.\n" +
				"Comment: Instances of the xref class should never be created and more specific " + 
				"classes should be used instead.");
		
		setEnglishComment(AUTHORS, "The authors of this publication, one per property value.");
		
		setEnglishComment(AVAILABILITY, 
				"Describes the availability of this data (e.g. a copyright statement).");
		
		setEnglishComment(DB, "The name of the external database to which this xref refers.");
		
		setEnglishComment(CELLTYPE, 
				"A cell type, e.g. 'HeLa'. This should reference a term in a controlled vocabulary " + 
				"of cell types.");
		
		setEnglishComment(COFACTOR, 
				"Any cofactor(s) or coenzyme(s) required for catalysis of the conversion by the " + 
				"enzyme. COFACTOR is a sub-property of PARTICIPANTS.");
		
		setEnglishComment(COMPONENTS, 
				"Defines the physicalEntity subunits of this complex. This property should not " + 
				"contain other complexes, i.e. it should always be a flat representation of the " + 
				"complex. For example, if two protein complexes join to form a single larger " + 
				"complex via a complex assembly interaction, the COMPONENTS of the new complex " + 
				"should be the individual proteins of the smaller complexes, not the two smaller " + 
				"complexes themselves. Exceptions are black-box complexes (i.e. complexes in " + 
				"which the COMPONENTS property is empty), which may be used as COMPONENTS of " + 
				"other complexes (via a physicalEntityParticipant instance) because their " + 
				"constituent parts are unknown / unspecified. The reason for keeping complexes " + 
				"flat is to signify that there is no information stored in the way complexes are " + 
				"nested, such as assembly order.");
		
		setEnglishComment(CONTROLLED, 
				"The entity that is controlled, e.g., in a biochemical reaction, the reaction " + 
				"is controlled by an enzyme. CONTROLLED is a sub-property of PARTICIPANTS.");
		
		setEnglishComment(CONTROLLER, 
				"The controlling entity, e.g., in a biochemical reaction, an enzyme is the " + 
				"controlling entity of the reaction. CONTROLLER is a sub-property of PARTICIPANTS.");
		
		setEnglishComment(DB_VERSION, 
				"The version of the external database in which this xref was last known to be " + 
				"valid. Resources may have recommendations for referencing dataset versions. " + 
				"For instance, the Gene Ontology recommends listing the date the GO terms were " + 
				"downloaded.");
		
		setEnglishComment(ID, 
				"The primary identifier in the external database of the object to which this " + 
				"xref refers.");
		
		setEnglishComment(MOLECULAR_WEIGHT, "Defines the molecular weight of the molecule, in daltons.");
		
		setEnglishComment(NAME, "The preferred full name for this entity.");
		
		setEnglishComment(ORGANISM, 
				"An organism, e.g. 'Homo sapiens'. This is the organism that the entity is found " + 
				"in. Pathways may not have an organism associated with them, for instance, " + 
				"reference pathways from KEGG. Sequence-based entities (DNA, protein, RNA) may " + 
				"contain an xref to a sequence database that contains organism information, in " + 
				"which case the information should be consistent with the value for ORGANISM.");
		
		setEnglishComment(PHYSICAL_ENTITY, 
				"The physical entity annotated with stoichiometry and cellular location attributes " + 
				"from the physicalEntityParticipant instance.");
		
		setEnglishComment(SEQUENCE, 
				"Polymer sequence in uppercase letters. For DNA, usually A,C,G,T letters " + 
				"representing the nucleosides of adenine, cytosine, guanine and thymine, " + 
				"respectively; for RNA, usually A, C, U, G; for protein, usually the letters " + 
				"corresponding to the 20 letter IUPAC amino acid code.");
		
		setEnglishComment(STRUCTURE, 
				"Defines the chemical structure and other information about this molecule, using " + 
				"an instance of class chemicalStructure.");
		
		setEnglishComment(STRUCTURE_FORMAT, 
				"This property specifies which format is used to define chemical structure data.");
		
		setEnglishComment(SYNONYMS,
				"One or more synonyms for the name of this entity. This should include the values " + 
				"of the NAME and SHORT-NAME property so that it is easy to find all known names in " + 
				"one place.");
		
		setEnglishComment(TISSUE, "An external controlled vocabulary of tissue types.");
		
		setEnglishComment(TITLE, "The title of the publication.");
		
		setEnglishComment(URL,
				"The URL at which the publication can be found, if it is available through the Web.");

		setEnglishComment(XREF, 
				"Values of this property define external cross-references from this entity to " + 
				"entities in external databases.");
		
		setEnglishComment(YEAR, 
				"The year in which this publication was published.");
		
		setTypedComment(CELLULAR_LOCATION, 
				"A cellular location, e.g. 'cytoplasm'. This should reference a term in the " + 
				"Gene Ontology Cellular Component ontology. The location referred to by this " + 
				"property should be as specific as is known. If an interaction is known to occur " + 
				"in multiple locations, separate interactions (and physicalEntityParticipants) must " + 
				"be created for each different location. Note: If a location is unknown then the GO " + 
				"term for 'cellular component unknown' (GO:0008372) should be used in the LOCATION " + 
				"property. If the location of a participant in a complex is unspecified, it may be " + 
				"assumed to be the same location as that of the complex. In case of conflicting " + 
				"information, the location of the most outer layer of any nesting should be " + 
				"considered correct. Note: Cellular location describes a specific location of a " + 
				"physical entity as it would be used in e.g. a transport reaction. It does not " + 
				"describe all of the possible locations that the physical entity could be in the cell.");
		
		setTypedComment(CHEMICAL_FORMULA, 
				"The chemical formula of the small molecule. Note: chemical formula can also be " + 
				"stored in the STRUCTURE property (in CML). In case of disagreement between the " + 
				"value of this property and that in the CML file, the CML value takes precedence.");
		
		setTypedComment(COMMENT, 
				"Comment on the data in the container class. This property should be used instead " + 
				"of the OWL documentation elements (rdfs:comment) for instances because information " + 
				"in COMMENT is data to be exchanged, whereas the rdfs:comment field is used for " + 
				"metadata about the structure of the BioPAX ontology.");
		
		setTypedComment(CONFIDENCE, 
				"Confidence in the containing instance.  Usually a statistical measure.");
		
		setTypedComment(CONFIDENCE_VALUE, "The value of the confidence measure.");
		
		setTypedComment(CONTROL_TYPE, 
				"Defines the nature of the control relationship between the CONTROLLER and the " + "" +
						"CONTROLLED entities.\n\n" +
						"The following terms are possible values:\n\n" +
						"ACTIVATION: General activation. Compounds that activate the specified " +
						"enzyme activity by an unknown mechanism. The mechanism is defined as " + 
						"unknown, because either the mechanism has yet to be elucidated in the " + 
						"experimental literature, or the paper(s) curated thus far do not define " + 
						"the mechanism, and a full literature search has yet to be performed.\n\n" +
						"The following term can not be used in the catalysis class:\n" +
						"INHIBITION: General inhibition. Compounds that inhibit the specified " + 
						"enzyme activity by an unknown mechanism. The mechanism is defined as " + 
						"unknown, because either the mechanism has yet to be elucidated in the " + 
						"experimental literature, or the paper(s) curated thus far do not define " + 
						"the mechanism, and a full literature search has yet to be performed.\n\n" +
						"The following terms can only be used in the modulation class (these " + 
						"definitions from EcoCyc):\n" +
						"INHIBITION-ALLOSTERIC\n" +
						"Allosteric inhibitors decrease the specified enzyme activity by binding " + 
						"reversibly to the enzyme and inducing a conformational change that " + 
						"decreases the affinity of the enzyme to its substrates without affecting " + 
						"its VMAX. Allosteric inhibitors can be competitive or noncompetitive " + 
						"inhibitors, therefore, those inhibition categories can be used in " + 
						"conjunction with this category.\n\n" +
						"INHIBITION-COMPETITIVE\n" +
						"Competitive inhibitors are compounds that competitively inhibit the " + 
						"specified enzyme activity by binding reversibly to the enzyme and " + 
						"preventing the substrate from binding. Binding of the inhibitor and " + 
						"substrate are mutually exclusive because it is assumed that the inhibitor " + 
						"and substrate can both bind only to the free enzyme. A competitive " + 
						"inhibitor can either bind to the active site of the enzyme, directly " + 
						"excluding the substrate from binding there, or it can bind to another " + 
						"site on the enzyme, altering the conformation of the enzyme such that " + 
						"the substrate can not bind to the active site.\n\n" +
						"INHIBITION-IRREVERSIBLE\n" +
						"Irreversible inhibitors are compounds that irreversibly inhibit the " + 
						"specified enzyme activity by binding to the enzyme and dissociating so " + 
						"slowly that it is considered irreversible. For example, alkylating agents, " + 
						"such as iodoacetamide, irreversibly inhibit the catalytic activity of " + 
						"some enzymes by modifying cysteine side chains.\n\n" +
						"INHIBITION-NONCOMPETITIVE\n" +
						"Noncompetitive inhibitors are compounds that noncompetitively inhibit " + 
						"the specified enzyme by binding reversibly to both the free enzyme and to " + 
						"the enzyme-substrate complex. The inhibitor and substrate may be bound to " + 
						"the enzyme simultaneously and do not exclude each other. However, only " + 
						"the enzyme-substrate complex (not the enzyme-substrate-inhibitor complex) " + 
						"is catalytically active.\n\n" +
						"INHIBITION-OTHER\n" +
						"Compounds that inhibit the specified enzyme activity by a mechanism " + 
						"that has been characterized, but that cannot be clearly classified as " + 
						"irreversible, competitive, noncompetitive, uncompetitive, or allosteric.\n\n" +
						"INHIBITION-UNCOMPETITIVE\n" +
						"Uncompetitive inhibitors are compounds that uncompetitively inhibit the " + 
						"specified enzyme activity by binding reversibly to the enzyme-substrate " + 
						"complex but not to the enzyme alone.\n\n" +
						"ACTIVATION-NONALLOSTERIC\n" +
						"Nonallosteric activators increase the specified enzyme activity by means " + 
						"other than allosteric.\n\n" +
						"ACTIVATION-ALLOSTERIC\n" +
						"Allosteric activators increase the specified enzyme activity by binding " + 
						"reversibly to the enzyme and inducing a conformational change that " + 
						"increases the affinity of the enzyme to its substrates without " + 
						"affecting its VMAX.");
		
		setTypedComment(DATA_SOURCE, 
				"A free text description of the source of this data, e.g. a database or person name. " + 
				"This property should be used to describe the source of the data. This is meant to " + 
				"be used by databases that export their data to the BioPAX format or by systems " + 
				"that are integrating data from multiple sources. The granularity of use " + 
				"(specifying the data source in many or few instances) is up to the user. " + 
				"It is intended that this property report the last data source, not all data sources " + 
				"that the data has passed through from creation.");
		
		setTypedComment(DELTA_G,
				"For biochemical reactions, this property refers to the standard transformed Gibbs " + 
				"energy change for a reaction written in terms of biochemical reactants " + 
				"(sums of species), delta-G'<sup>o</sup>.\n\n" + 
				"  delta-G'<sup>o</sup> = -RT lnK'\n" + 
				"and\n" + 
				"  delta-G'<sup>o</sup> = delta-H'<sup>o</sup> - T delta-S'<sup>o</sup>\n\n" + 
				"delta-G'<sup>o</sup> has units of kJ/mol.  Like K', it is a function of" + 
				" temperature (T), ionic strength (I), pH, and pMg" + 
				" (pMg = -log<sub>10</sub>[Mg<sup>2+</sup>]). Therefore, these quantities must be" + 
				" specified, and values for DELTA-G for biochemical reactions are represented as" + 
				" 5-tuples of the form (delta-G'<sup>o</sup> T I pH pMg).  " + 
				"This property may have multiple values, representing different measurements for " + 
				"delta-G'<sup>o</sup> obtained under the different experimental conditions listed " + 
				"in the 5-tuple.\n\n" + 
				"(This definition from EcoCyc)");
		
		setTypedComment(DELTA_G_PRIME_O, 
				"For biochemical reactions, this property refers to the standard transformed " + 
				"Gibbs energy change for a reaction written in terms of biochemical reactants " + 
				"(sums of species), delta-G'<sup>o</sup>.\n\n" +
				"  delta-G'<sup>o</sup> = -RT lnK'\n" +
				"and\n" +
				"  delta-G'<sup>o</sup> = delta-H'<sup>o</sup> - T delta-S'<sup>o</sup>\n\n" +
				"delta-G'<sup>o</sup> has units of kJ/mol.  Like K', it is a function of " + 
				"temperature (T), ionic strength (I), pH, and pMg " + 
				"(pMg = -log<sub>10</sub>[Mg<sup>2+</sup>]). Therefore, these quantities must " + 
				"be specified, and values for DELTA-G for biochemical reactions are represented " + 
				"as 5-tuples of the form (delta-G'<sup>o</sup> T I pH pMg).\n\n" +
				"(This definition from EcoCyc)");
		
		setTypedComment(DELTA_H, 
				"For biochemical reactions, this property refers to the standard transformed " + 
				"enthalpy change for a reaction written in terms of biochemical reactants " + 
				"(sums of species), delta-H'<sup>o</sup>.\n\n" +
				"  delta-G'<sup>o</sup> = delta-H'<sup>o</sup> - T delta-S'<sup>o</sup>\n\n" +
				"Units: kJ/mole\n\n" +
				"(This definition from EcoCyc)");
		
		setTypedComment(DELTA_S, 
				"For biochemical reactions, this property refers to the standard transformed " + 
				"entropy change for a reaction written in terms of biochemical reactants " + 
				"(sums of species), delta-S'<sup>o</sup>.\n\n" +
				"  delta-G'<sup>o</sup> = delta-H'<sup>o</sup> - T delta-S'<sup>o</sup>\n\n" +
				"(This definition from EcoCyc)");
		
		setTypedComment(DIRECTION, 
				"Specifies the reaction direction of the interaction catalyzed by this instance of " + 
				"the catalysis class.\n\n" +
				"Possible values of this slot are:\n\n" +
				"REVERSIBLE: Interaction occurs in both directions in physiological settings.\n\n" +
				"PHYSIOL-LEFT-TO-RIGHT\n" +
				"PHYSIOL-RIGHT-TO-LEFT\n" +
				"The interaction occurs in the specified direction in physiological settings,  " + 
				"because of several possible factors including the energetics of the reaction, " + 
				"local concentrations\n" +
				"of reactants and products, and the regulation of the enzyme or its expression.\n\n" +
				"IRREVERSIBLE-LEFT-TO-RIGHT\n" +
				"IRREVERSIBLE-RIGHT-TO-LEFT\n" +
				"For all practical purposes, the interactions occurs only in the specified direction " + 
				"in physiological settings, because of chemical properties of the reaction.\n\n" +
				"(This definition from EcoCyc)");
		
		setTypedComment(EC_NUMBER, 
				"The unique number assigned to a reaction by the Enzyme Commission of the " + 
				"International Union of Biochemistry and Molecular Biology.\n\n" +
				"Note that not all biochemical reactions currently have EC numbers assigned to them.");
		
		setTypedComment(EVIDENCE, 
				"Scientific evidence supporting the existence of the entity as described.");
		
		setTypedComment(EVIDENCE_CODE, 
				"A pointer to a term in an external controlled vocabulary, such as the GO, PSI-MI " + 
				"or BioCyc evidence codes, that describes the nature of the support, such as " + 
				"'traceable author statement' or 'yeast two-hybrid'.");
		
		setTypedComment(EXPERIMENTAL_FORM, 
				"The experimental forms associated with an evidence instance.");
		
		setTypedComment(EXPERIMENTAL_FORM_TYPE, 
				"Descriptor of this experimental form from a controlled vocabulary.");
		
		setTypedComment(FEATURE_TYPE, "Description and classification of the feature.");

		setTypedComment(FEATURE_LOCATION, 
				"Location of the feature on the sequence of the interactor. One feature may have " + 
				"more than one location, used e.g. for features which involve sequence positions " + 
				"close in the folded, three-dimensional state of a protein, but non-continuous " + 
				"along the sequence.");
		
		setTypedComment(ID_VERSION, 
				"The version number of the identifier (ID). E.g. The RefSeq accession number " + 
				"NM_005228.3 should be split into NM_005228 as the ID and 3 as the ID-VERSION.");
		
		setTypedComment(INTERACTION_TYPE, 
				"External controlled vocabulary characterizing the interaction type, for " + 
				"example \"phosphorylation\".");
		
		setTypedComment(IONIC_STRENGTH, 
				"The ionic strength is defined as half of the total sum of the concentration " + 
				"(ci) of every ionic species (i) in the solution times the square of its charge (zi). " + 
				"For example, the ionic strength of a 0.1 M solution of CaCl2 is 0.5 x " + 
				"(0.1 x 22 + 0.2 x 12) = 0.3 M\n" +
				"(Definition from http://www.lsbu.ac.uk/biology/enztech/ph.html)");
		
		setTypedComment(K_PRIME, 
				"The apparent equilibrium constant K'. Concentrations in the equilibrium " + 
				"constant equation refer to the total concentrations of  all forms of " + 
				"particular biochemical reactants. For example, in the equilibrium constant " + 
				"equation for the biochemical reaction in which ATP is hydrolyzed to ADP and " + 
				"inorganic phosphate:\n\n" +
				"K' = [ADP][P<sub>i</sub>]/[ATP],\n\n" +
				"The concentration of ATP refers to the total concentration of all of the " + 
				"following species:\n\n" +
				"[ATP] = [ATP<sup>4-</sup>] + [HATP<sup>3-</sup>] + " + 
				"[H<sub>2</sub>ATP<sup>2-</sup>] + [MgATP<sup>2-</sup>] + " + 
				"[MgHATP<sup>-</sup>] + [Mg<sub>2</sub>ATP].\n\n" +
				"The apparent equilibrium constant is formally dimensionless, and can be kept so " + 
				"by inclusion of as many of the terms (1 mol/dm<sup>3</sup>) in the numerator or " + 
				"denominator as necessary.  It is a function of temperature (T), ionic strength (I), " + 
				"pH, and pMg (pMg = -log<sub>10</sub>[Mg<sup>2+</sup>]).\n" +
				"(Definition from EcoCyc)");
		
		setTypedComment(KEQ, 
				"This quantity is dimensionless and is usually a single number. The measured " + 
				"equilibrium constant for a biochemical reaction, encoded by the slot KEQ, is " + 
				"actually the apparent equilibrium constant, K'.  Concentrations in the " + 
				"equilibrium constant equation refer to the total concentrations of  all forms of " + 
				"particular biochemical reactants. For example, in the equilibrium constant equation " + 
				"for the biochemical reaction in which ATP is hydrolyzed to ADP and inorganic " + 
				"phosphate:\n\n" +
				"K' = [ADP][P<sub>i</sub>]/[ATP],\n\n" +
				"The concentration of ATP refers to the total concentration of all of the " + 
				"following species:\n\n" +
				"[ATP] = [ATP<sup>4-</sup>] + [HATP<sup>3-</sup>] + [H<sub>2</sub>ATP<sup>2-</sup>] " + 
				"+ [MgATP<sup>2-</sup>] + [MgHATP<sup>-</sup>] + [Mg<sub>2</sub>ATP].\n\n" +
				"The apparent equilibrium constant is formally dimensionless, and can be kept so " + 
				"by inclusion of as many of the terms (1 mol/dm<sup>3</sup>) in the numerator or " + 
				"denominator as necessary.  It is a function of temperature (T), ionic strength (I), " + 
				"pH, and pMg (pMg = -log<sub>10</sub>[Mg<sup>2+</sup>]). Therefore, these quantities " + 
				"must be specified to be precise, and values for KEQ for biochemical reactions may " + 
				"be represented as 5-tuples of the form (K' T I pH pMg).  This property may have " + 
				"multiple values, representing different measurements for K' obtained under the " + 
				"different experimental conditions listed in the 5-tuple. (This definition adapted " + 
				"from EcoCyc)");

		setTypedComment(LEFT, 
				"The participants on the left side of the conversion interaction. Since " + 
				"conversion interactions may proceed in either the left-to-right or right-to-left " + 
				"direction, occupants of the LEFT property may be either reactants or products. " + 
				"LEFT is a sub-property of PARTICIPANTS.");
		
		setTypedComment(NEXT_STEP, 
				"The next step(s) of the pathway.  Contains zero or more pathwayStep instances.  " + 
				"If there is no next step, this property is empty.");
		
		setTypedComment(PARTICIPANT, "The participant that has the experimental form being described.");
		
		setTypedComment(PARTICIPANTS, 
				"This property lists the entities that participate in this interaction. " + 
				"For example, in a biochemical reaction, the participants are the union of the " + 
				"reactants and the products of the reaction. This property has a number of " + 
				"sub-properties, such as LEFT and RIGHT in the biochemicalInteraction class. " + 
				"Any participant listed in a sub-property will automatically be assumed to also " + 
				"be in PARTICIPANTS by a number of software systems, including Protégé, so this " + 
				"property should not contain any instances if there are instances contained in a " + 
				"sub-property.");
		
		setTypedComment(PATHWAY_COMPONENTS, 
				"The set of interactions and/or pathwaySteps in this pathway/network. Each instance " + 
				"of the pathwayStep class defines: 1) a set of interactions that together define " + 
				"a particular step in the pathway, for example a catalysis instance and the " + 
				"conversion that it catalyzes; 2) an order relationship to one or more other " + 
				"pathway steps (via the NEXT-STEP property). Note: This ordering is not necessarily " + 
				"temporal - the order described may simply represent connectivity between adjacent " + 
				"steps. Temporal ordering information should only be inferred from the direction of " + 
				"each interaction.");
		
		setTypedComment(PH, 
				"A measure of acidity and alkalinity of a solution that is a number on a scale on " + 
				"which a value of 7 represents neutrality and lower numbers indicate increasing " + 
				"acidity and higher numbers increasing alkalinity and on which each unit of change " + 
				"represents a tenfold change in acidity or alkalinity and that is the negative " + 
				"logarithm of the effective hydrogen-ion concentration or hydrogen-ion activity " + 
				"in gram equivalents per liter of the solution. (Definition from Merriam-Webster " + 
				"Dictionary)");
		
		setTypedComment(PMG, 
				"A measure of the concentration of magnesium (Mg) in solution. " + 
				"(pMg = -log<sub>10</sub>[Mg<sup>2+</sup>])");
		
		setTypedComment(POSITION_STATUS, 
				"The confidence status of the sequence position. This could be:\n" +
				"EQUAL: The SEQUENCE-POSITION is known to be at the SEQUENCE-POSITION.\n" +
				"GREATER-THAN: The site is greater than the SEQUENCE-POSITION.\n" +
				"LESS-THAN: The site is less than the SEQUENCE-POSITION.");
		
		setTypedComment(RELATIONSHIP_TYPE, 
				"This property names the type of relationship between the BioPAX object linked from, " + 
				"and the external object linked to, such as 'gene of this protein', or 'protein " + 
				"with similar sequence'.");

		setTypedComment(RIGHT, 
				"The participants on the right side of the conversion interaction. Since " + 
				"conversion interactions may proceed in either the left-to-right or right-to-left " + 
				"direction, occupants of the RIGHT property may be either reactants or products. " + 
				"RIGHT is a sub-property of PARTICIPANTS.");
		
		setTypedComment(SEQUENCE_FEATURE_LIST, 
				"Sequence features relevant for the interaction, for example binding domains or " + 
				"modification sites. Warning: this property may be moved into a state class in Level 3.");
		
		setTypedComment(SEQUENCE_INTERVAL_BEGIN, "The begin position of a sequence interval.");
		
		setTypedComment(SEQUENCE_INTERVAL_END, "The end position of a sequence interval.");
		
		setTypedComment(SEQUENCE_POSITION, 
				"The integer listed gives the position. The first base or amino acid is position 1. " + 
				"In combination with the numeric value, the property 'POSITION-STATUS' allows to " + 
				"express fuzzy positions, e.g. 'less than 4'.");
		
		setTypedComment(SHORT_NAME, 
				"An abbreviated name for this entity, preferably a name that is short enough to be " + 
				"used in a visualization application to label a graphical element that represents " + 
				"this entity. If no short name is available, an xref may be used for this purpose " + 
				"by the visualization application.");
		
		setTypedComment(SOURCE, 
				"The source  in which the reference was published, such as: a book title, or a " + 
				"journal title and volume and pages.");
		
		setTypedComment(SPONTANEOUS,
				"Specifies whether a conversion occurs spontaneously (i.e. uncatalyzed, under " + 
				"biological conditions) left-to-right, right-to-left, or not at all. If the " + 
				"spontaneity is not known, the SPONTANEOUS property should be left empty.");
		
		setTypedComment(STEP_INTERACTIONS, 
				"The interactions that take place at this step of the pathway.");
		
		setTypedComment(STOICHIOMETRIC_COEFFICIENT, 
				"Each value of this property represents the stoichiometric coefficient for one of " + 
				"the physical entities in an interaction or complex. For a given interaction, " + 
				"the stoichiometry should always be used where possible instead of representing the " + 
				"number of participants with separate instances of each participant. If there are " + 
				"three ATP molecules, one ATP molecule should be represented as a participant and " + 
				"the stoichiometry should be set to 3.");
		
		setTypedComment(STRUCTURE_DATA, 
				"This property holds a string of data defining chemical structure or other " + 
				"information, in either the CML or SMILES format, as specified in property " + 
				"Structure-Format. If, for example, the CML format is used, then the value of " + 
				"this property is a string containing the XML encoding of the CML data.");
		
		setTypedComment(TEMPERATURE, "Temperature in Celsius");
		
		setTypedComment(TERM, "The external controlled vocabulary term.");
		
		setTypedComment(TAXON_XREF, 
				"An xref to an organism taxonomy database, preferably NCBI taxon. This should be " + 
				"an instance of unificationXref, unless the organism is not in an existing database.");
		
	}

	public static Vector<DatatypeProperty> labelProperties = new Vector<DatatypeProperty>();
	
	static {
		labelProperties.add(BioPAX2.SHORT_NAME);
		labelProperties.add(BioPAX2.NAME);
		labelProperties.add(BioPAX2.SYNONYMS);
	}
	
}

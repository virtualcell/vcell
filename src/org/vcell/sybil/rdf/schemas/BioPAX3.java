package org.vcell.sybil.rdf.schemas;

/*   BioPAX3  --- by Oliver Ruebenacker, UCHC --- April 2008 to November 2009
 *   The BioPAX Level 3 schema
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.vcell.sybil.rdf.ModelComparer;
import org.vcell.sybil.rdf.NameSpace;
import org.vcell.sybil.rdf.OntUtil;
import org.vcell.sybil.util.lists.ListOfNone;
import org.vcell.sybil.util.sets.SetOfFour;
import org.vcell.sybil.util.sets.SetOfThree;
import org.vcell.sybil.util.sets.SetOfTwo;

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

public class BioPAX3 {

	public static final OntologyBox box = new OntologyBox() {
		public Model getRdf() { return schema; }
		public String uri() { return URI; }
		public NameSpace ns() { return ns; }
		public String label() { return label; }
		public List<DatatypeProperty> labelProperties() { return labelProperties; }
	};
	
	public static final String label = "BioPAX3";
	public static final List<DatatypeProperty> labelProperties = new ListOfNone<DatatypeProperty>();
	
	public static final String URI = "http://www.biopax.org/release/biopax-level3.owl";
	
	public static final NameSpace ns = new NameSpace("bp3", URI + "#");
	
	public static final OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

	public static final Ontology ontology = schema.createOntology(URI);
	
	public static final OntClass BindingFeature = schema.createClass(ns + "BindingFeature");
	public static final OntClass BiochemicalPathwayStep = schema.createClass(ns + "BiochemicalPathwayStep");
	public static final OntClass BiochemicalReaction = schema.createClass(ns + "BiochemicalReaction");
	public static final OntClass BioSource = schema.createClass(ns + "BioSource");
	public static final OntClass Catalysis = schema.createClass(ns + "Catalysis");
	public static final OntClass CellularLocationVocabulary = 
		schema.createClass(ns + "CellularLocationVocabulary");
	public static final OntClass CellVocabulary = schema.createClass(ns + "CellVocabulary");
	public static final OntClass ChemicalStructure = schema.createClass(ns + "ChemicalStructure");
	public static final OntClass Complex = schema.createClass(ns + "Complex");
	public static final OntClass ComplexAssembly = schema.createClass(ns + "ComplexAssembly");
	public static final OntClass Control = schema.createClass(ns + "Control");
	public static final OntClass ControlledVocabulary = schema.createClass(ns + "ControlledVocabulary");
	public static final OntClass Conversion = schema.createClass(ns + "Conversion");
	public static final OntClass CovalentBindingFeature = schema.createClass(ns + "CovalentBindingFeature");
	public static final OntClass Degradation = schema.createClass(ns + "Degradation");
	public static final OntClass Dna = schema.createClass(ns + "Dna");
	public static final OntClass DnaReference = schema.createClass(ns + "DnaReference");
	public static final OntClass DnaRegion = schema.createClass(ns + "DnaRegion");
	public static final OntClass DnaRegionReference = schema.createClass(ns + "DnaRegionReference");
	public static final OntClass DeltaG = schema.createClass(ns + "DeltaG");
	public static final OntClass Gene = schema.createClass(ns + "Gene");
	public static final OntClass Entity = schema.createClass(ns + "Entity");
	public static final OntClass EntityFeature = schema.createClass(ns + "EntityFeature");
	public static final OntClass EntityReference = schema.createClass(ns + "EntityReference");
	public static final OntClass EntityReferenceTypeVocabulary = 
		schema.createClass(ns + "EntityReferenceTypeVocabulary");
	public static final OntClass Evidence = schema.createClass(ns + "Evidence");
	public static final OntClass EvidenceCodeVocabulary = schema.createClass(ns + "EvidenceCodeVocabulary");
	public static final OntClass ExperimentalForm = schema.createClass(ns + "ExperimentalForm");
	public static final OntClass ExperimentalFormVocabulary = 
		schema.createClass(ns + "ExperimentalFormVocabulary");
	public static final OntClass FragmentFeature = schema.createClass(ns + "FragmentFeature");
	public static final OntClass GeneticInteraction = schema.createClass(ns + "GeneticInteraction");
	public static final OntClass Interaction = schema.createClass(ns + "Interaction");
	public static final OntClass InteractionVocabulary = schema.createClass(ns + "InteractionVocabulary");
	public static final OntClass KPrime = schema.createClass(ns + "KPrime");
	public static final OntClass ModificationFeature = schema.createClass(ns + "ModificationFeature");
	public static final OntClass Modulation = schema.createClass(ns + "Modulation");
	public static final OntClass MolecularInteraction = schema.createClass(ns + "MolecularInteraction");
	public static final OntClass Pathway = schema.createClass(ns + "Pathway");
	public static final OntClass PathwayStep = schema.createClass(ns + "PathwayStep");
	public static final OntClass PhenotypeVocabulary = schema.createClass(ns + "PhenotypeVocabulary");
	public static final OntClass PhysicalEntity = schema.createClass(ns + "PhysicalEntity");
	public static final OntClass Protein = schema.createClass(ns + "Protein");
	public static final OntClass ProteinReference = schema.createClass(ns + "ProteinReference");
	public static final OntClass Provenance = schema.createClass(ns + "Provenance");
	public static final OntClass PublicationXref = schema.createClass(ns + "PublicationXref");
	public static final OntClass RelationshipXref = schema.createClass(ns + "RelationshipXref");
	public static final OntClass RelationshipTypeVocabulary = 
		schema.createClass(ns + "RelationshipTypeVocabulary");
	public static final OntClass Rna = schema.createClass(ns + "Rna");
	public static final OntClass RnaReference = schema.createClass(ns + "RnaReference");
	public static final OntClass RnaRegion = schema.createClass(ns + "RnaRegion");
	public static final OntClass RnaRegionReference = schema.createClass(ns + "RnaRegionReference");
	public static final OntClass Score = schema.createClass(ns + "Score");
	public static final OntClass SequenceInterval = schema.createClass(ns + "SequenceInterval");
	public static final OntClass SequenceLocation = schema.createClass(ns + "SequenceLocation");
	public static final OntClass SequenceModificationVocabulary = 
		schema.createClass(ns + "SequenceModificationVocabulary");
	public static final OntClass SequenceRegionVocabulary = 
		schema.createClass(ns + "SequenceRegionVocabulary");
	public static final OntClass SequenceSite = schema.createClass(ns + "SequenceSite");
	public static final OntClass SmallMolecule = schema.createClass(ns + "SmallMolecule");
	public static final OntClass SmallMoleculeReference = schema.createClass(ns + "SmallMoleculeReference");
	public static final OntClass Stoichiometry = schema.createClass(ns + "Stoichiometry");
	public static final OntClass TemplateReaction = schema.createClass(ns + "TemplateReaction");
	public static final OntClass TemplateReactionRegulation = 
		schema.createClass(ns + "TemplateReactionRegulation");
	public static final OntClass TissueVocabulary = schema.createClass(ns + "TissueVocabulary");
	public static final OntClass Transport = schema.createClass(ns + "Transport");
	public static final OntClass TransportWithBiochemicalReaction = 
		schema.createClass(ns + "TransportWithBiochemicalReaction");
	public static final OntClass UnificationXref = schema.createClass(ns + "UnificationXref");
	public static final OntClass UtilityClass = schema.createClass(ns + "UtilityClass");
	public static final OntClass Xref = schema.createClass(ns + "Xref");
	
	public static final ObjectProperty absoluteRegion = schema.createObjectProperty(ns + "absoluteRegion");
	public static final DatatypeProperty author = schema.createDatatypeProperty(ns + "author");
	public static final DatatypeProperty availability = schema.createDatatypeProperty(ns + "availability");
	public static final ObjectProperty bindsTo = schema.createObjectProperty(ns + "bindsTo");
	public static final DatatypeProperty catalysisDirection = 
		schema.createDatatypeProperty(ns + "catalysisDirection");
	public static final ObjectProperty cellType = schema.createObjectProperty(ns + "cellType");
	public static final ObjectProperty cellularLocation = 
		schema.createObjectProperty(ns + "cellularLocation");
	public static final DatatypeProperty chemicalFormula = 
		schema.createDatatypeProperty(ns + "chemicalFormula");
	public static final ObjectProperty cofactor = schema.createObjectProperty(ns + "cofactor");
	public static final DatatypeProperty comment = schema.createDatatypeProperty(ns + "comment");
	public static final ObjectProperty component = schema.createObjectProperty(ns + "component");
	public static final ObjectProperty componentStoichiometry = 
		schema.createObjectProperty(ns + "componentStoichiometry");
	public static final ObjectProperty confidence = schema.createObjectProperty(ns + "confidence");
	public static final ObjectProperty controlled = schema.createObjectProperty(ns + "controlled");
	public static final ObjectProperty controller = schema.createObjectProperty(ns + "controller");
	public static final DatatypeProperty controlType = schema.createDatatypeProperty(ns + "controlType");
	public static final DatatypeProperty conversionDirection = 
		schema.createDatatypeProperty(ns + "conversionDirection");
	public static final ObjectProperty dataSource = schema.createObjectProperty(ns + "dataSource");
	public static final DatatypeProperty db = schema.createDatatypeProperty(ns + "db");
	public static final DatatypeProperty dbVersion = schema.createDatatypeProperty(ns + "dbVersion");
	public static final ObjectProperty deltaG = schema.createObjectProperty(ns + "deltaG");
	public static final DatatypeProperty deltaGPrime0 = schema.createDatatypeProperty(ns + "deltaGPrime0");
	public static final DatatypeProperty deltaH = schema.createDatatypeProperty(ns + "deltaH");
	public static final DatatypeProperty deltaS = schema.createDatatypeProperty(ns + "deltaS");
	public static final DatatypeProperty displayName = schema.createDatatypeProperty(ns + "displayName");
	public static final DatatypeProperty eCNumber = schema.createDatatypeProperty(ns + "eCNumber");
	public static final ObjectProperty entityFeature = schema.createObjectProperty(ns + "entityFeature");
	public static final ObjectProperty entityReference = schema.createObjectProperty(ns + "entityReference");
	public static final ObjectProperty entityReferenceType = 
		schema.createObjectProperty(ns + "entityReferenceType");
	public static final ObjectProperty evidence = schema.createObjectProperty(ns + "evidence");
	public static final ObjectProperty evidenceCode = schema.createObjectProperty(ns + "evidenceCode");
	public static final ObjectProperty experimentalFeature = 
		schema.createObjectProperty(ns + "experimentalFeature");
	public static final ObjectProperty experimentalForm = 
		schema.createObjectProperty(ns + "experimentalForm");
	public static final ObjectProperty experimentalFormDescription = 
		schema.createObjectProperty(ns + "experimentalFormDescription");
	public static final ObjectProperty experimentalFormEntity = 
		schema.createObjectProperty(ns + "experimentalFormEntity");
	public static final ObjectProperty feature = schema.createObjectProperty(ns + "feature");
	public static final ObjectProperty featureLocation = schema.createObjectProperty(ns + "featureLocation");
	public static final ObjectProperty featureLocationType = 
		schema.createObjectProperty(ns + "featureLocationType");
	public static final DatatypeProperty id = schema.createDatatypeProperty(ns + "id");
	public static final DatatypeProperty idVersion = schema.createDatatypeProperty(ns + "idVersion");
	public static final ObjectProperty interactionScore = 
		schema.createObjectProperty(ns + "interactionScore");
	public static final ObjectProperty interactionType = 
		schema.createObjectProperty(ns + "interactionType");
	public static final DatatypeProperty intraMolecular = 
		schema.createDatatypeProperty(ns + "intraMolecular");
	public static final DatatypeProperty ionicStrength = schema.createDatatypeProperty(ns + "ionicStrength");
	public static final ObjectProperty kEQ = schema.createObjectProperty(ns + "kEQ");
	public static final DatatypeProperty kPrime = schema.createDatatypeProperty(ns + "kPrime");
	public static final ObjectProperty left = schema.createObjectProperty(ns + "left");
	public static final ObjectProperty memberEntityReference = 
		schema.createObjectProperty(ns + "memberEntityReference");
	public static final ObjectProperty memberFeature = schema.createObjectProperty(ns + "memberFeature");
	public static final ObjectProperty memberPhysicalEntity = 
		schema.createObjectProperty(ns + "memberPhysicalEntity");
	public static final ObjectProperty modificationType = 
		schema.createObjectProperty(ns + "modificationType");
	public static final DatatypeProperty molecularWeight = 
		schema.createDatatypeProperty(ns + "molecularWeight");
	public static final DatatypeProperty name = schema.createDatatypeProperty(ns + "name");
	public static final ObjectProperty nextStep = schema.createObjectProperty(ns + "nextStep");
	public static final ObjectProperty notFeature = schema.createObjectProperty(ns + "notFeature");
	public static final ObjectProperty organism = schema.createObjectProperty(ns + "organism");
	public static final ObjectProperty participant = schema.createObjectProperty(ns + "participant");
	public static final ObjectProperty participantStoichiometry = 
		schema.createObjectProperty(ns + "participantStoichiometry");
	public static final ObjectProperty pathwayComponent = 
		schema.createObjectProperty(ns + "pathwayComponent");
	public static final ObjectProperty pathwayOrder = schema.createObjectProperty(ns + "pathwayOrder");
	public static final DatatypeProperty patoData = schema.createDatatypeProperty(ns + "patoData");
	public static final DatatypeProperty ph = schema.createDatatypeProperty(ns + "ph");
	public static final ObjectProperty phenotype = schema.createObjectProperty(ns + "phenotype");
	public static final ObjectProperty physicalEntity = 
		schema.createObjectProperty(ns + "physicalEntity");
	public static final DatatypeProperty pMg = schema.createDatatypeProperty(ns + "pMg");
	public static final DatatypeProperty positionStatus = 
		schema.createDatatypeProperty(ns + "positionStatus");
	public static final ObjectProperty product = schema.createObjectProperty(ns + "product");
	public static final ObjectProperty regionOf = schema.createObjectProperty(ns + "regionOf");
	public static final ObjectProperty regionType = schema.createObjectProperty(ns + "regionType");
	public static final ObjectProperty relationshipType = 
		schema.createObjectProperty(ns + "relationshipType");
	public static final ObjectProperty right = schema.createObjectProperty(ns + "right");
	public static final ObjectProperty scoreSource = schema.createObjectProperty(ns + "scoreSource");
	public static final DatatypeProperty sequence = schema.createDatatypeProperty(ns + "sequence");
	public static final ObjectProperty sequenceIntervalBegin = 
		schema.createObjectProperty(ns + "sequenceIntervalBegin");
	public static final ObjectProperty sequenceIntervalEnd = 
		schema.createObjectProperty(ns + "sequenceIntervalEnd");
	public static final DatatypeProperty sequencePosition = 
		schema.createDatatypeProperty(ns + "sequencePosition");
	public static final DatatypeProperty source = schema.createDatatypeProperty(ns + "source");
	public static final DatatypeProperty spontaneous = schema.createDatatypeProperty(ns + "spontaneous");
	public static final DatatypeProperty standardName = schema.createDatatypeProperty(ns + "standardName");
	public static final DatatypeProperty stepDirection = schema.createDatatypeProperty(ns + "stepDirection");
	public static final ObjectProperty stepConversion = schema.createObjectProperty(ns + "stepConversion");
	public static final ObjectProperty stepProcess = schema.createObjectProperty(ns + "stepProcess");
	public static final DatatypeProperty stoichiometricCoefficient = 
		schema.createDatatypeProperty(ns + "stoichiometricCoefficient");
	public static final ObjectProperty structure = schema.createObjectProperty(ns + "structure");
	public static final DatatypeProperty structureData = 
		schema.createDatatypeProperty(ns + "structureData");
	public static final DatatypeProperty structureFormat = 
		schema.createDatatypeProperty(ns + "structureFormat");
	public static final ObjectProperty subRegion = schema.createObjectProperty(ns + "subRegion");
	public static final ObjectProperty taxonXref = schema.createObjectProperty(ns + "taxonXref");
	public static final DatatypeProperty temperature = schema.createDatatypeProperty(ns + "temperature");
	public static final ObjectProperty template = schema.createObjectProperty(ns + "template");
	public static final DatatypeProperty templateDirection = 
		schema.createDatatypeProperty(ns + "templateDirection");
	public static final DatatypeProperty term = schema.createDatatypeProperty(ns + "term");
	public static final ObjectProperty tissue = schema.createObjectProperty(ns + "tissue");
	public static final DatatypeProperty title = schema.createDatatypeProperty(ns + "title");
	public static final DatatypeProperty url = schema.createDatatypeProperty(ns + "url");
	public static final DatatypeProperty value = schema.createDatatypeProperty(ns + "value");
	public static final ObjectProperty xref = schema.createObjectProperty(ns + "xref");
	public static final DatatypeProperty year = schema.createDatatypeProperty(ns + "year");
	
	static {
		absoluteRegion.addProperty(RDF.type, OWL.FunctionalProperty);
		bindsTo.addProperty(RDF.type, OWL.FunctionalProperty);
		catalysisDirection.addProperty(RDF.type, OWL.FunctionalProperty);
		cellType.addProperty(RDF.type, OWL.FunctionalProperty);
		cellularLocation.addProperty(RDF.type, OWL.FunctionalProperty);
		chemicalFormula.addProperty(RDF.type, OWL.FunctionalProperty);
		controlled.addProperty(RDF.type, OWL.FunctionalProperty);
		controlType.addProperty(RDF.type, OWL.FunctionalProperty);
		conversionDirection.addProperty(RDF.type, OWL.FunctionalProperty);
		dbVersion.addProperty(RDF.type, OWL.FunctionalProperty);
		deltaGPrime0.addProperty(RDF.type, OWL.FunctionalProperty);
		displayName.addProperty(RDF.type, OWL.FunctionalProperty);
		entityReference.addProperty(RDF.type, OWL.FunctionalProperty);
		entityReferenceType.addProperty(RDF.type, OWL.FunctionalProperty);
		id.addProperty(RDF.type, OWL.FunctionalProperty);
		idVersion.addProperty(RDF.type, OWL.FunctionalProperty);
		intraMolecular.addProperty(RDF.type, OWL.FunctionalProperty);
		ionicStrength.addProperty(RDF.type, OWL.FunctionalProperty);
		db.addProperty(RDF.type, OWL.FunctionalProperty);
		kPrime.addProperty(RDF.type, OWL.FunctionalProperty);
		modificationType.addProperty(RDF.type, OWL.FunctionalProperty);
		molecularWeight.addProperty(RDF.type, OWL.FunctionalProperty);
		organism.addProperty(RDF.type, OWL.FunctionalProperty);
		patoData.addProperty(RDF.type, OWL.FunctionalProperty);
		ph.addProperty(RDF.type, OWL.FunctionalProperty);
		phenotype.addProperty(RDF.type, OWL.FunctionalProperty);
		physicalEntity.addProperty(RDF.type, OWL.FunctionalProperty);
		pMg.addProperty(RDF.type, OWL.FunctionalProperty);
		positionStatus.addProperty(RDF.type, OWL.FunctionalProperty);
		scoreSource.addProperty(RDF.type, OWL.FunctionalProperty);
		sequence.addProperty(RDF.type, OWL.FunctionalProperty);
		sequenceIntervalBegin.addProperty(RDF.type, OWL.FunctionalProperty);
		sequenceIntervalEnd.addProperty(RDF.type, OWL.FunctionalProperty);
		sequencePosition.addProperty(RDF.type, OWL.FunctionalProperty);
		spontaneous.addProperty(RDF.type, OWL.FunctionalProperty);
		standardName.addProperty(RDF.type, OWL.FunctionalProperty);
		stepConversion.addProperty(RDF.type, OWL.FunctionalProperty);
		stepDirection.addProperty(RDF.type, OWL.FunctionalProperty);
		stoichiometricCoefficient.addProperty(RDF.type, OWL.FunctionalProperty);
		structure.addProperty(RDF.type, OWL.FunctionalProperty);
		structureData.addProperty(RDF.type, OWL.FunctionalProperty);
		structureFormat.addProperty(RDF.type, OWL.FunctionalProperty);
		taxonXref.addProperty(RDF.type, OWL.FunctionalProperty);
		temperature.addProperty(RDF.type, OWL.FunctionalProperty);
		template.addProperty(RDF.type, OWL.FunctionalProperty);
		templateDirection.addProperty(RDF.type, OWL.FunctionalProperty);
		tissue.addProperty(RDF.type, OWL.FunctionalProperty);
		title.addProperty(RDF.type, OWL.FunctionalProperty);
		value.addProperty(RDF.type, OWL.FunctionalProperty);
		year.addProperty(RDF.type, OWL.FunctionalProperty);

		bindsTo.addProperty(RDF.type, OWL.InverseFunctionalProperty);
		component.addProperty(RDF.type, OWL.InverseFunctionalProperty);
		entityFeature.addProperty(RDF.type, OWL.InverseFunctionalProperty);
		
		bindsTo.addProperty(RDF.type, OWL.SymmetricProperty);
		bindsTo.addProperty(OWL.inverseOf, bindsTo);

		memberPhysicalEntity.addProperty(RDF.type, OWL.TransitiveProperty);
		memberEntityReference.addProperty(RDF.type, OWL.TransitiveProperty);
		memberFeature.addProperty(RDF.type, OWL.TransitiveProperty);
		subRegion.addProperty(RDF.type, OWL.TransitiveProperty);
		
		BindingFeature.addSubClass(CovalentBindingFeature);
		BiochemicalReaction.addSubClass(TransportWithBiochemicalReaction);
		Control.addSubClass(Modulation);
		Control.addSubClass(TemplateReactionRegulation);
		ControlledVocabulary.addSubClass(CellVocabulary);
		ControlledVocabulary.addSubClass(CellularLocationVocabulary);
		ControlledVocabulary.addSubClass(EntityReferenceTypeVocabulary);
		ControlledVocabulary.addSubClass(EvidenceCodeVocabulary);
		ControlledVocabulary.addSubClass(ExperimentalFormVocabulary);
		ControlledVocabulary.addSubClass(InteractionVocabulary);
		ControlledVocabulary.addSubClass(PhenotypeVocabulary);
		ControlledVocabulary.addSubClass(RelationshipTypeVocabulary);
		ControlledVocabulary.addSubClass(SequenceModificationVocabulary);
		ControlledVocabulary.addSubClass(SequenceRegionVocabulary);
		ControlledVocabulary.addSubClass(TissueVocabulary);
		Control.addSubClass(Catalysis);
		Conversion.addSubClass(BiochemicalReaction);
		Conversion.addSubClass(ComplexAssembly);
		Conversion.addSubClass(Degradation);
		Conversion.addSubClass(Transport);
		Entity.addSubClass(Interaction);
		Entity.addSubClass(Gene);
		Entity.addSubClass(Pathway);
		Entity.addSubClass(PhysicalEntity);
		EntityFeature.addSubClass(BindingFeature);
		EntityFeature.addSubClass(FragmentFeature);
		EntityFeature.addSubClass(ModificationFeature);
		EntityReference.addSubClass(DnaReference);
		EntityReference.addSubClass(DnaRegionReference);
		EntityReference.addSubClass(ProteinReference);
		EntityReference.addSubClass(RnaReference);
		EntityReference.addSubClass(RnaRegionReference);
		EntityReference.addSubClass(SmallMoleculeReference);
		Interaction.addSubClass(Control);
		Interaction.addSubClass(Conversion);
		Interaction.addSubClass(GeneticInteraction);
		Interaction.addSubClass(MolecularInteraction);
		Interaction.addSubClass(TemplateReaction);
		ModificationFeature.addSubClass(CovalentBindingFeature);
		PathwayStep.addSubClass(BiochemicalPathwayStep);
		PhysicalEntity.addSubClass(Complex);
		PhysicalEntity.addSubClass(Dna);
		PhysicalEntity.addSubClass(DnaRegion);
		PhysicalEntity.addSubClass(Protein);
		PhysicalEntity.addSubClass(Rna);
		PhysicalEntity.addSubClass(RnaRegion);
		PhysicalEntity.addSubClass(SmallMolecule);
		SequenceLocation.addSubClass(SequenceInterval);
		SequenceLocation.addSubClass(SequenceSite);
		Transport.addSubClass(TransportWithBiochemicalReaction);
		UtilityClass.addSubClass(BioSource);
		UtilityClass.addSubClass(ChemicalStructure);
		UtilityClass.addSubClass(ControlledVocabulary);
		UtilityClass.addSubClass(DeltaG);
		UtilityClass.addSubClass(EntityFeature);
		UtilityClass.addSubClass(EntityReference);
		UtilityClass.addSubClass(Evidence);
		UtilityClass.addSubClass(ExperimentalForm);
		UtilityClass.addSubClass(KPrime);		
		UtilityClass.addSubClass(PathwayStep);
		UtilityClass.addSubClass(Provenance);
		UtilityClass.addSubClass(Score);
		UtilityClass.addSubClass(SequenceLocation);
		UtilityClass.addSubClass(Stoichiometry);
		UtilityClass.addSubClass(Xref);
		Xref.addSubClass(PublicationXref);
		Xref.addSubClass(RelationshipXref);
		Xref.addSubClass(UnificationXref);
		
		name.addSubProperty(displayName);
		name.addSubProperty(standardName);
		participant.addSubProperty(cofactor);
		participant.addSubProperty(controlled);
		participant.addSubProperty(controller);
		participant.addSubProperty(left);
		participant.addSubProperty(product);
		participant.addSubProperty(right);
		participant.addSubProperty(template);
		stepProcess.addSubProperty(stepConversion);

		absoluteRegion.setDomain(schema.createUnionClass(null, schema.createList()
				.with(DnaRegionReference).with(RnaRegionReference)));
		absoluteRegion.setRange(SequenceLocation);
		author.setDomain(PublicationXref);
		author.setRange(XSD.xstring);
		availability.setDomain(Entity);
		availability.setRange(XSD.xstring);
		bindsTo.setDomain(BindingFeature);
		bindsTo.setRange(BindingFeature);
		catalysisDirection.setDomain(Catalysis);
		catalysisDirection.setRange(schema.createDataRange(
				schema.createList().with(schema.createTypedLiteral("LEFT-TO-RIGHT"))
				.with(schema.createTypedLiteral("RIGHT-TO-LEFT"))));
		cellType.setDomain(BioSource);
		cellType.setRange(CellVocabulary);
		cellularLocation.setDomain(PhysicalEntity);
		cellularLocation.setRange(CellularLocationVocabulary);
		chemicalFormula.setDomain(SmallMoleculeReference);
		chemicalFormula.setRange(XSD.xstring);
		cofactor.setDomain(Catalysis);
		cofactor.setRange(PhysicalEntity);
		comment.setDomain(schema.createUnionClass(null, schema.createList()
				.with(UtilityClass).with(Entity)));
		comment.setRange(XSD.xstring);
		component.setDomain(Complex);
		component.setRange(PhysicalEntity);
		componentStoichiometry.setDomain(Complex);
		componentStoichiometry.setRange(Stoichiometry);
		confidence.setDomain(Evidence);
		confidence.setRange(Score);
		controlled.setDomain(Control);
		controlled.setRange(schema.createUnionClass(null, schema.createList()
				.with(Interaction).with(Pathway)));
		controller.setDomain(Control);
		controller.setRange(schema.createUnionClass(null, schema.createList()
				.with(PhysicalEntity).with(Pathway)));
		controlType.setDomain(Control);
		controlType.setRange(schema.createDataRange(
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
		conversionDirection.setDomain(Conversion);
		conversionDirection.setRange(schema.createDataRange(
				schema.createList().with(schema.createTypedLiteral("REVERSIBLE"))
				.with(schema.createTypedLiteral("RIGHT-TO-LEFT"))
				.with(schema.createTypedLiteral("LEFT-TO-RIGHT"))));
		dataSource.setDomain(Entity);
		dataSource.setRange(Provenance);
		db.setDomain(Xref);
		db.setRange(XSD.xstring);
		dbVersion.setDomain(Xref);
		dbVersion.setRange(XSD.xstring);
		deltaG.setDomain(BiochemicalReaction);
		deltaG.setRange(DeltaG);
		deltaGPrime0.setDomain(DeltaG);
		deltaGPrime0.setRange(XSD.xfloat);
		deltaH.setDomain(BiochemicalReaction);
		deltaH.setRange(XSD.xfloat);
		deltaS.setDomain(BiochemicalReaction);
		deltaS.setRange(XSD.xfloat);
		displayName.setRange(XSD.xstring);
		eCNumber.setDomain(BiochemicalReaction);
		eCNumber.setRange(XSD.xstring);
		entityFeature.setDomain(EntityReference);
		entityFeature.setRange(EntityFeature);
		entityReference.setDomain(schema.createUnionClass(null, 
				schema.createList().with(Protein).with(Rna).with(Dna).with(SmallMolecule).with(DnaRegion)
				.with(RnaRegion)));
		entityReference.setRange(EntityReference);
		entityReferenceType.setDomain(EntityReference);
		entityReferenceType.setRange(EntityReferenceTypeVocabulary);
		evidence.setDomain(schema.createUnionClass(null, 
				schema.createList().with(PathwayStep).with(EntityFeature).with(EntityReference)
				.with(Entity)));
		evidence.setRange(Evidence);
		evidenceCode.setDomain(Evidence);
		evidenceCode.setRange(EvidenceCodeVocabulary);
		experimentalFeature.setDomain(ExperimentalForm);
		experimentalFeature.setRange(EntityFeature);
		experimentalForm.setDomain(Evidence);
		experimentalForm.setRange(ExperimentalForm);
		experimentalFormDescription.setDomain(ExperimentalForm);
		experimentalFormDescription.setRange(ExperimentalFormVocabulary);
		experimentalFormEntity.setDomain(ExperimentalForm);
		experimentalFormEntity.setRange(schema.createUnionClass(null, 
				schema.createList().with(PhysicalEntity).with(Gene)));
		feature.setDomain(PhysicalEntity);
		feature.setRange(EntityFeature);
		featureLocation.setDomain(EntityFeature);
		featureLocation.setRange(SequenceLocation);
		featureLocationType.setDomain(EntityFeature);
		featureLocationType.setRange(SequenceRegionVocabulary);
		id.setDomain(Xref);
		id.setRange(XSD.xstring);
		idVersion.setDomain(Xref);
		idVersion.setRange(XSD.xstring);
		interactionScore.setDomain(GeneticInteraction);
		interactionScore.setRange(Score);
		interactionType.setDomain(Interaction);
		interactionType.setRange(InteractionVocabulary);
		intraMolecular.setDomain(BindingFeature);
		intraMolecular.setRange(XSD.xboolean);
		ionicStrength.setDomain(schema.createUnionClass(null, schema.createList()
				.with(KPrime).with(DeltaG)));
		ionicStrength.setRange(XSD.xfloat);
		kEQ.setDomain(BiochemicalReaction);
		kEQ.setRange(KPrime);
		kPrime.setDomain(KPrime);
		kPrime.setRange(XSD.xfloat);
		left.setDomain(Conversion);
		left.setRange(PhysicalEntity);
		memberEntityReference.setDomain(EntityReference);
		memberEntityReference.setRange(EntityReference);
		memberFeature.setDomain(EntityFeature);
		memberFeature.setRange(EntityFeature);
		memberPhysicalEntity.setDomain(PhysicalEntity);
		memberPhysicalEntity.setRange(PhysicalEntity);
		modificationType.setDomain(ModificationFeature);
		modificationType.setRange(SequenceModificationVocabulary);
		molecularWeight.setDomain(SmallMoleculeReference);
		molecularWeight.setRange(XSD.xfloat);
		name.setDomain(schema.createUnionClass(null, schema.createList().with(EntityReference)
				.with(BioSource).with(Entity).with(Provenance)));
		name.setRange(XSD.xstring);
		nextStep.setDomain(PathwayStep);
		nextStep.setRange(PathwayStep);
		notFeature.setDomain(PhysicalEntity);
		notFeature.setRange(EntityFeature);
		organism.setDomain(schema.createUnionClass(null, schema.createList()
				.with(Pathway).with(DnaReference).with(RnaReference).with(ProteinReference).with(Gene)
				.with(DnaRegionReference).with(RnaRegionReference)));
		organism.setRange(BioSource);
		participant.setDomain(Interaction);
		participant.setRange(Entity);
		participantStoichiometry.setDomain(Conversion);
		participantStoichiometry.setRange(Stoichiometry);
		pathwayComponent.setDomain(Pathway);
		pathwayComponent.setRange(schema.createUnionClass(null, schema.createList()
				.with(Interaction).with(Pathway)));
		pathwayOrder.setDomain(Pathway);
		pathwayOrder.setRange(PathwayStep);
		patoData.setDomain(PhenotypeVocabulary);
		patoData.setRange(XSD.xstring);
		ph.setDomain(schema.createUnionClass(null, schema.createList().with(KPrime).with(DeltaG)));
		ph.setRange(XSD.xfloat);
		phenotype.setDomain(GeneticInteraction);
		phenotype.setRange(PhenotypeVocabulary);
		physicalEntity.setDomain(Stoichiometry);
		physicalEntity.setRange(PhysicalEntity);
		pMg.setDomain(schema.createUnionClass(null, schema.createList().with(KPrime).with(DeltaG)));
		pMg.setRange(XSD.xfloat);
		positionStatus.setDomain(SequenceSite);
		positionStatus.setRange(schema.createDataRange(
				schema.createList().with(schema.createTypedLiteral("EQUAL"))
				.with(schema.createTypedLiteral("LESS-THAN"))
				.with(schema.createTypedLiteral("GREATER-THAN"))));
		product.setDomain(TemplateReaction);
		product.setRange(schema.createUnionClass(null, schema.createList()
				.with(Dna).with(Protein).with(Rna)));
		regionOf.setDomain(schema.createUnionClass(null, schema.createList().with(DnaRegionReference)
				.with(RnaRegionReference)));
		regionOf.setRange(schema.createUnionClass(null, schema.createList().with(DnaReference)
				.with(RnaReference)));
		regionType.setDomain(schema.createUnionClass(null, schema.createList().with(DnaRegionReference)
				.with(RnaRegionReference)));
		regionType.setRange(SequenceRegionVocabulary);
		relationshipType.setDomain(RelationshipXref);
		relationshipType.setRange(RelationshipTypeVocabulary);
		right.setDomain(Conversion);
		right.setRange(PhysicalEntity);
		scoreSource.setDomain(Score);
		scoreSource.setRange(Provenance);
		sequence.setDomain(schema.createUnionClass(null, schema.createList().with(DnaReference)
				.with(RnaReference).with(ProteinReference).with(DnaRegionReference)
				.with(RnaRegionReference)));
		sequence.setRange(XSD.xstring);
		sequenceIntervalBegin.setDomain(SequenceInterval);
		sequenceIntervalBegin.setRange(SequenceSite);
		sequenceIntervalEnd.setDomain(SequenceInterval);
		sequenceIntervalEnd.setRange(SequenceSite);
		sequencePosition.setDomain(SequenceSite);
		sequencePosition.setRange(XSD.xint);
		source.setDomain(PublicationXref);
		source.setRange(XSD.xstring);
		spontaneous.setDomain(Conversion);
		spontaneous.setRange(XSD.xboolean);
		standardName.setRange(XSD.xstring);
		stepConversion.setDomain(BiochemicalPathwayStep);
		stepConversion.setRange(Conversion);
		stepDirection.setDomain(BiochemicalPathwayStep);
		stepDirection.setRange(schema.createDataRange(
				schema.createList().with(schema.createTypedLiteral("REVERSIBLE"))
				.with(schema.createTypedLiteral("RIGHT-TO-LEFT"))
				.with(schema.createTypedLiteral("LEFT-TO-RIGHT"))));
		stepProcess.setDomain(PathwayStep);
		stepProcess.setRange(schema.createUnionClass(null, schema.createList().with(Pathway)
				.with(Interaction)));
		stoichiometricCoefficient.setDomain(Stoichiometry);
		stoichiometricCoefficient.setRange(XSD.xfloat);
		structure.setDomain(SmallMoleculeReference);
		structure.setRange(ChemicalStructure);
		structureData.setDomain(ChemicalStructure);
		structureData.setRange(XSD.xstring);
		structureFormat.setDomain(ChemicalStructure);
		structureFormat.setRange(schema.createDataRange(
				schema.createList().with(schema.createTypedLiteral("CML"))
				.with(schema.createTypedLiteral("SMILES"))
				.with(schema.createTypedLiteral("InChI"))));
		subRegion.setDomain(schema.createUnionClass(null, schema.createList().with(DnaRegionReference)
				.with(RnaRegionReference)));
		subRegion.setRange(EntityReference);
		taxonXref.setDomain(BioSource);
		taxonXref.setRange(UnificationXref);
		temperature.setDomain(schema.createUnionClass(null, schema.createList().with(KPrime)
				.with(DeltaG)));
		temperature.setRange(XSD.xfloat);
		template.setDomain(TemplateReaction);
		template.setRange(schema.createUnionClass(null, schema.createList().with(DnaRegion)
				.with(RnaRegion).with(Dna).with(Rna)));
		templateDirection.setDomain(TemplateReaction);
		templateDirection.setRange(schema.createDataRange(
								schema.createList().with(schema.createTypedLiteral("FORWARD"))
								.with(schema.createTypedLiteral("REVERSE"))));
		term.setDomain(ControlledVocabulary);
		term.setRange(XSD.xstring);
		tissue.setDomain(BioSource);
		tissue.setRange(TissueVocabulary);
		title.setDomain(PublicationXref);
		title.setRange(XSD.xstring);
		url.setDomain(PublicationXref);
		url.setRange(XSD.xstring);
		value.setDomain(Score);
		value.setRange(XSD.xstring);
		xref.setDomain(schema.createUnionClass(null, schema.createList().with(ControlledVocabulary)
				.with(Entity).with(Provenance).with(Evidence).with(EntityReference)));
		xref.setRange(Xref);
		year.setDomain(PublicationXref);
		year.setRange(XSD.xint);

		BioSource.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, taxonXref, 1));
		ChemicalStructure.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, structureData, 1));
		ChemicalStructure.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, structureFormat, 1));
		DeltaG.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, deltaGPrime0, 1));
		GeneticInteraction.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, phenotype, 1));
		KPrime.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, kPrime, 1));
		Score.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, value, 1));
		Stoichiometry.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, physicalEntity, 1));
		Stoichiometry.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, stoichiometricCoefficient, 1));
		UnificationXref.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, db, 1));
		UnificationXref.addProperty(RDFS.subClassOf, 
				schema.createCardinalityRestriction(null, id, 1));
		
		GeneticInteraction.addProperty(RDFS.subClassOf, 
				schema.createMinCardinalityRestriction(null, participant, 2));
		ExperimentalForm.addProperty(RDFS.subClassOf, 
				schema.createMinCardinalityRestriction(null, experimentalFormDescription, 1));
		
		Catalysis.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, controller, 1));
		Gene.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, organism, 1));
		GeneticInteraction.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, interactionType, 1));
		Modulation.addProperty(RDFS.subClassOf, 
				schema.createMaxCardinalityRestriction(null, controller, 1));
		
		Catalysis.addProperty(RDFS.subClassOf, 
				schema.createHasValueRestriction(null, controlType, 
						schema.createTypedLiteral("ACTIVATION")));
		Degradation.addProperty(RDFS.subClassOf, 
				schema.createHasValueRestriction(null, conversionDirection, 
						schema.createTypedLiteral("LEFT-TO-RIGHT")));
		
		BiochemicalPathwayStep.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, stepProcess, Control));
		Catalysis.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, controlled, Conversion));
		Complex.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, memberPhysicalEntity, Complex));
		ControlledVocabulary.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, xref, UnificationXref));
		Conversion.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, participant, PhysicalEntity));
		Dna.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, entityReference, DnaReference));
		Dna.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, memberPhysicalEntity, Dna));
		DnaReference.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, memberEntityReference, DnaReference));
		DnaRegion.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, memberPhysicalEntity, DnaRegion));
		DnaRegion.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, entityReference, DnaRegionReference));
		DnaRegionReference.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, subRegion, DnaRegionReference));
		DnaRegionReference.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, regionOf, DnaReference));
		GeneticInteraction.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, participant, Gene));
		Modulation.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, controlled, Catalysis));
		Modulation.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, controller, PhysicalEntity));
		MolecularInteraction.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, participant, PhysicalEntity));
		Protein.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, entityReference, ProteinReference));
		Protein.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, memberPhysicalEntity, Protein));
		ProteinReference.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, memberEntityReference, ProteinReference));
		Provenance.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, xref, schema.createUnionClass(null, 
						schema.createList().with(PublicationXref).with(UnificationXref))));
		Rna.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, entityReference, RnaReference));
		Rna.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, memberPhysicalEntity, Rna));
		RnaReference.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, memberEntityReference, RnaReference));
		RnaRegion.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, memberPhysicalEntity, RnaRegion));
		RnaRegion.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, entityReference, RnaRegionReference));
		RnaRegionReference.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, regionOf, RnaReference));
		RnaRegionReference.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, subRegion, RnaRegionReference));
		SmallMolecule.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, entityReference, SmallMoleculeReference));
		SmallMolecule.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, feature, BindingFeature));
		SmallMolecule.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, memberPhysicalEntity, SmallMolecule));
		SmallMoleculeReference.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, memberEntityReference, SmallMoleculeReference));
		SmallMolecule.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, notFeature, BindingFeature));
		TemplateReaction.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, participant, PhysicalEntity));
		TemplateReactionRegulation.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, controlled, TemplateReaction));
		TemplateReactionRegulation.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, controller, PhysicalEntity));
		TemplateReactionRegulation.addProperty(RDFS.subClassOf, 
				schema.createAllValuesFromRestriction(null, controlType, 
						schema.createDataRange(
								schema.createList().with(schema.createTypedLiteral("ACTIVATION"))
								.with(schema.createTypedLiteral("INHIBITION")))));

		Evidence.addProperty(RDFS.subClassOf, schema.createUnionClass(null, schema.createList()
				.with(schema.createMinCardinalityRestriction(null, confidence, 1))
				.with(schema.createMinCardinalityRestriction(null, evidenceCode, 1))
				.with(schema.createMinCardinalityRestriction(null, experimentalForm, 1))));
		
		OntUtil.makeAllDisjoint(new SetOfThree<OntClass>(Protein, Dna, Rna));
		OntUtil.makeAllDisjoint(new SetOfTwo<OntClass>(GeneticInteraction, Control));
		OntUtil.makeAllDisjoint(new SetOfTwo<OntClass>(GeneticInteraction, TemplateReaction));
		OntUtil.makeAllDisjoint(new SetOfTwo<OntClass>(GeneticInteraction, MolecularInteraction));
		OntUtil.makeAllDisjoint(new SetOfFour<OntClass>(Interaction, PhysicalEntity, Pathway, Gene));
		OntUtil.makeAllDisjoint(new SetOfTwo<OntClass>(GeneticInteraction, Conversion));
	}

	
	private static void setTypedComment(OntResource resource, String comment) {
		resource.setPropertyValue(RDFS.comment, schema.createTypedLiteral(comment));
	}
	
	private static void setEnglishComment(OntResource resource, String comment) {
		resource.setPropertyValue(RDFS.comment, schema.createLiteral(comment, "en"));
	}
	
	static {

		setTypedComment(absoluteRegion, 
		"Absolute location as defined by the referenced sequence database record. E.g. an operon " + 
		"has a absolute region on the DNA molecule referenced by the UnificationXref.");
		
		setEnglishComment(author, 
		"The authors of this publication, one per property value.");
		
		setEnglishComment(availability, 
		"Describes the availability of this data (e.g. a copyright statement).");
		
		setTypedComment(BindingFeature, 
		"Specifies the binding domains of two entities in a complex that are non-covalently bound to " + 
		"each other. Note that this is a n-ary specifier class on the boundTo property. " + 
		"The difference between this class and modificationFeature is that this is non-covalent and " + 
		"modificationFeature is covalent.");
		
		setTypedComment(bindsTo, 
		"A binding feature represents a \"half\" of the bond between two entities. This property " + 
		"points to another binding feature which represents the other half. The bond can be " + 
		"covalent or non-covalent.");
		
		setTypedComment(BiochemicalPathwayStep, 
		"Imposes ordering on a step in a biochemical pathway. A biochemical reaction can be reversible " + 
		"by itself, but can be physiologically directed in the context of a pathway, for instance due " + 
		"to flux of reactants and products. Only one conversion interaction can be ordered at a time, " + 
		"but multiple catalysis or modulation instances can be part of one step.");
		
		setTypedComment(BiochemicalReaction, 
		"Definition: A conversion interaction in which one or more entities (substrates) undergo " + 
		"covalent changes to become one or more other entities (products). The substrates of " + 
		"biochemical reactions are defined in terms of sums of species. This is convention in " + 
		"biochemistry, and, in principle, all of the EC reactions should be biochemical reactions.\n" +
		"Examples: ATP + H2O = ADP + Pi\n" +
		"Comment: In the example reaction above, ATP is considered to be an equilibrium mixture of " + 
		"several species, namely ATP4-, HATP3-, H2ATP2-, MgATP2-, MgHATP-, and Mg2ATP. Additional " + 
		"species may also need to be considered if other ions (e.g. Ca2+) that bind ATP are present. " + 
		"Similar considerations apply to ADP and to inorganic phosphate (Pi). When writing " + 
		"biochemical reactions, it is not necessary to attach charges to the biochemical reactants " + 
		"or to include ions such as H+ and Mg2+ in the equation. The reaction is written in the " + 
		"direction specified by the EC nomenclature system, if applicable, regardless of the " + 
		"physiological direction(s) in which the reaction proceeds. Polymerization reactions " + 
		"involving large polymers whose structure is not explicitly captured should generally be " + 
		"represented as unbalanced reactions in which the monomer is consumed but the polymer " + 
		"remains unchanged, e.g. glycogen + glucose = glycogen.");
		
		setTypedComment(BioSource, 
		"Definition: The biological source of an entity (e.g. protein, RNA or DNA). Some entities " + 
		"are considered source-neutral (e.g. small molecules), and the biological source of others " + 
		"can be deduced from their constituentss (e.g. complex, pathway).\n" +
		"Examples: HeLa cells, human, and mouse liver tissue.");
		
		setTypedComment(Catalysis, 
		"Definition: A control interaction in which a physical entity (a catalyst) increases the rate " + 
		"of a conversion interaction by lowering its activation energy. Instances of this class " + 
		"describe a pairing between a catalyzing entity and a catalyzed conversion.\n" +
		"Comment: A separate catalysis instance should be created for each different conversion " + 
		"that a physicalEntity may catalyze and for each different physicalEntity that may catalyze " + 
		"a conversion. For example, a bifunctional enzyme that catalyzes two different biochemical " + 
		"reactions would be linked to each of those biochemical reactions by two separate instances " + 
		"of the catalysis class. Also, catalysis reactions from multiple different organisms could " + 
		"be linked to the same generic biochemical reaction (a biochemical reaction is generic if " + 
		"it only includes small molecules). Generally, the enzyme catalyzing a conversion is known " + 
		"and the use of this class is obvious. In the cases where a catalyzed reaction is known to " + 
		"occur but the enzyme is not known, a catalysis instance should be created without a " + 
		"controller specified (i.e. the CONTROLLER property should remain empty).\n" +
		"Synonyms: facilitation, acceleration.\n" +
		"Examples: The catalysis of a biochemical reaction by an enzyme, the enabling of a transport " + 
		"interaction by a membrane pore complex, and the facilitation of a complex assembly by a " + 
		"scaffold protein. Hexokinase -> (The \"Glucose + ATP -> Glucose-6-phosphate +ADP\" reaction). " + 
		"A plasma membrane Na+/K+ ATPase is an active transporter (antiport pump) using the energy of " + 
		"ATP to pump Na+ out of the cell and K+ in. Na+ from cytoplasm to extracellular space would " + 
		"be described in a transport instance. K+ from extracellular space to cytoplasm would be " + 
		"described in a transport instance. The ATPase pump would be stored in a catalysis " + 
		"instance controlling each of the above transport instances. A biochemical reaction that does " + 
		"not occur by itself under physiological conditions, but has been observed to occur in the " + 
		"presence of cell extract, likely via one or more unknown enzymes present in the extract, " + 
		"would be stored in the CONTROLLED property, with the CONTROLLER property empty.");
		
		setTypedComment(catalysisDirection, 
		"This property represents the direction of this catalysis under all\n" +
		"physiological conditions if there is one.\n\n" +
		"Note that chemically a catalyst will increase the rate of the reaction\n" +
		"in both directions. In biology, however, there are cases where the\n" +
		"enzyme is expressed only when the controlled bidirectional conversion is\n" +
		"on one side of the chemical equilibrium [todo : example]. If that is the\n" +
		"case and the controller, under biological conditions, is always\n" +
		"catalyzing the conversion in one direction then this fact can be\n" +
		"captured using this property. If the enzyme is active for both\n" +
		"directions, or the conversion is not bidirectional, this property should\n" +
		"be left empty.");
		
		setEnglishComment(cellType, 
		"A cell type, e.g. 'HeLa'. This should reference a term in a controlled vocabulary of cell " + 
		"types. Best practice is to refer to OBO Cell Ontology. " + 
		"http://www.obofoundry.org/cgi-bin/detail.cgi?id=cell");
		
		setTypedComment(CellVocabulary, 
		"A reference to the Cell Type Ontology (CL). Homepage at " + 
		"http://obofoundry.org/cgi-bin/detail.cgi?cell.  Browse at " + 
		"http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=CL");
		
		setTypedComment(cellularLocation, 
		"A cellular location, e.g. 'cytoplasm'. This should reference a term in the Gene Ontology " + 
		"Cellular Component ontology. The location referred to by this property should be as specific " + 
		"as is known. If an interaction is known to occur in multiple locations, separate " + 
		"interactions (and physicalEntities) must be created for each different location.  If the " + 
		"location of a participant in a complex is unspecified, it may be assumed to be the same " + 
		"location as that of the complex. \n\n" +
		" A molecule in two different cellular locations are considered two different physical entities.");
		
		setTypedComment(CellularLocationVocabulary, 
		"A reference to the Gene Ontology Cellular Component (GO CC) ontology. Homepage at " + 
		"http://www.geneontology.org.  Browse at " + 
		"http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=GO");
		
		setTypedComment(chemicalFormula, 
		"The chemical formula of the small molecule. Note: chemical formula can also be stored in " + 
		"the STRUCTURE property (in CML). In case of disagreement between the value of this property " + 
		"and that in the CML file, the CML value takes precedence.");

		setTypedComment(ChemicalStructure, 
		"Definition: Describes a small molecule structure. Structure information is stored in the " + 
		"property STRUCTURE-DATA, in one of three formats: the CML format (see URL www.xml-cml.org), " + 
		"the SMILES format (see URL www.daylight.com/dayhtml/smiles/) or the InChI format " + 
		"(http://www.iupac.org/inchi/). The STRUCTURE-FORMAT property specifies which format is used.\n" +
		"Comment: By virtue of the expressivity of CML, an instance of this class can also provide " + 
		"additional information about a small molecule, such as its chemical formula, names, " + 
		"and synonyms, if CML is used as the structure format.\n" +
		"Examples: The following SMILES string, which describes the structure of glucose-6-phosphate:\n" +
		"'C(OP(=O)(O)O)[CH]1([CH](O)[CH](O)[CH](O)[CH](O)O1)'.");
		
		setEnglishComment(cofactor, 
		"Any cofactor(s) or coenzyme(s) required for catalysis of the conversion by the enzyme. " + 
		"COFACTOR is a sub-property of PARTICIPANTS.");
		
		setTypedComment(comment, 
		"Comment on the data in the container class. This property should be used instead of the " + 
		"OWL documentation elements (rdfs:comment) for instances because information in 'comment' " + 
		"is data to be exchanged, whereas the rdfs:comment field is used for metadata about the " + 
		"structure of the BioPAX ontology.");
		
		setTypedComment(Complex, 
		"Definition: A physical entity whose structure is comprised of other physical entities bound " + 
		"to each other non-covalently, at least one of which is a macromolecule (e.g. protein, DNA, " + 
		"or RNA). Complexes must be stable enough to function as a biological unit; in general, " + 
		"the temporary association of an enzyme with its substrate(s) should not be considered or " + 
		"represented as a complex. A complex is the physical product of an " + 
		"interaction (complexAssembly) and is not itself considered an interaction.\n" +
		"Comment: In general, complexes should not be defined recursively so that smaller complexes " + 
		"exist within larger complexes, i.e. a complex should not be a COMPONENT of another " + 
		"complex (see comments on the COMPONENT property). The boundaries on the size of complexes " + 
		"described by this class are not defined here, although elements of the cell as large and " + 
		"dynamic as, e.g., a mitochondrion would typically not be described using this class (later " + 
		"versions of this ontology may include a cellularComponent class to represent these). The " + 
		"strength of binding and the topology of the components cannot be described currently, but " + 
		"may be included in future versions of the ontology, depending on community need.\n" +
		"Examples: Ribosome, RNA polymerase II. Other examples of this class include complexes of " + 
		"multiple protein monomers and complexes of proteins and small molecules.");
		
		setTypedComment(ComplexAssembly, 
		"Definition: A conversion interaction in which a set of physical entities, at least one being " + 
		"a macromolecule (e.g. protein, RNA, DNA), aggregate via non-covalent interactions. " + 
		"One of the participants of a complexAssembly must be an instance of the class complex.\n" +
		"Comment: This class is also used to represent complex disassembly. The assembly or disassembly " + 
		"of a complex is often a spontaneous process, in which case the direction of the " + 
		"complexAssembly (toward either assembly or disassembly) should be specified via the " + 
		"SPONTANEOUS property.\n" +
		"Synonyms: aggregation, complex formation\n" +
		"Examples: Assembly of the TFB2 and TFB3 proteins into the TFIIH complex, and assembly of the " + 
		"ribosome through aggregation of its subunits.\n" +
		"Note: The following are not examples of complex assembly: Covalent phosphorylation of a " + 
		"protein (this is a biochemicalReaction); the TFIIH complex itself (this is an instance of " + 
		"the complex class, not the complexAssembly class).");
		
		setTypedComment(componentStoichiometry, 
		"The stoichiometry of components in a complex");
		
		setTypedComment(confidence, 
		"Confidence in the containing instance.  Usually a statistical measure.");
		
		setTypedComment(Control, 
		"Definition: An interaction in which one entity regulates, modifies, or otherwise influences " + 
		"another. Two types of control interactions are defined: activation and inhibition.\n" +
		"Comment: In general, the targets of control processes (i.e. occupants of the CONTROLLED " + 
		"property) should be interactions. Conceptually, physical entities are involved in " + 
		"interactions (or events) and the events should be controlled or modified, not the physical " + 
		"entities themselves. For example, a kinase activating a protein is a frequent event in " + 
		"signaling pathways and is usually represented as an 'activation' arrow from the kinase to " + 
		"the substrate in signaling diagrams. This is an abstraction that can be ambiguous out of " + 
		"context. In BioPAX, this information should be captured as the kinase catalyzing (via an " + 
		"instance of the catalysis class) a reaction in which the substrate is phosphorylated, " + 
		"instead of as a control interaction in which the kinase activates the substrate. Since this " + 
		"class is a superclass for specific types of control, instances of the control class should " + 
		"only be created when none of its subclasses are applicable.\n" +
		"Synonyms: regulation, mediation\n" +
		"Examples: A small molecule that inhibits a pathway by an unknown mechanism controls the pathway.");
		
		setEnglishComment(controlled, 
		"The entity that is controlled, e.g., in a biochemical reaction, the reaction is controlled by " + 
		"an enzyme. CONTROLLED is a sub-property of PARTICIPANTS.");
		
		setEnglishComment(controller, 
		"The controlling entity, e.g., in a biochemical reaction, an enzyme is the controlling entity " + 
		"of the reaction. CONTROLLER is a sub-property of PARTICIPANTS.");
		
		setTypedComment(ControlledVocabulary, 
		"Definition: Used to import terms from external controlled vocabularies (CVs) into the ontology. " + 
		"To support consistency and compatibility, open, freely available CVs should be used " + 
		"whenever possible, such as the Gene Ontology (GO) or other open biological CVs listed on the " + 
		"OBO website (http://obo.sourceforge.net/).\n" + 
		"Comment: The ID property in unification xrefs to GO and other OBO ontologies should include " + 
		"the ontology name in the ID property (e.g. ID=\"GO:0005634\" instead of ID=\"0005634\").");

		setTypedComment(controlType, 
		"Defines the nature of the control relationship between the CONTROLLER and the CONTROLLED " + 
		"entities.\n\n" +
		"The following terms are possible values:\n\n" +
		"ACTIVATION: General activation. Compounds that activate the specified enzyme activity by an " + 
		"unknown mechanism. The mechanism is defined as unknown, because either the mechanism has yet " + 
		"to be elucidated in the experimental literature, or the paper(s) curated thus far do not " + 
		"define the mechanism, and a full literature search has yet to be performed.\n\n" +
		"The following term can not be used in the catalysis class:\n" +
		"INHIBITION: General inhibition. Compounds that inhibit the specified enzyme activity by " + 
		"an unknown mechanism. The mechanism is defined as unknown, because either the mechanism has " + 
		"yet to be elucidated in the experimental literature, or the paper(s) curated thus far do " + 
		"not define the mechanism, and a full literature search has yet to be performed.\n\n" +
		"The following terms can only be used in the modulation class (these definitions from EcoCyc):\n" +
		"INHIBITION-ALLOSTERIC\n" +
		"Allosteric inhibitors decrease the specified enzyme activity by binding reversibly to the " + 
		"enzyme and inducing a conformational change that decreases the affinity of the enzyme to " + 
		"its substrates without affecting its VMAX. Allosteric inhibitors can be competitive or " + 
		"noncompetitive inhibitors, therefore, those inhibition categories can be used in conjunction " + 
		"with this category.\n\n" +
		"INHIBITION-COMPETITIVE\n" +
		"Competitive inhibitors are compounds that competitively inhibit the specified enzyme " + 
		"activity by binding reversibly to the enzyme and preventing the substrate from binding. " + 
		"Binding of the inhibitor and substrate are mutually exclusive because it is assumed that " + 
		"the inhibitor and substrate can both bind only to the free enzyme. A competitive inhibitor " + 
		"can either bind to the active site of the enzyme, directly excluding the substrate from " + 
		"binding there, or it can bind to another site on the enzyme, altering the conformation of " + 
		"the enzyme such that the substrate can not bind to the active site.\n\n" +
		"INHIBITION-IRREVERSIBLE\n" +
		"Irreversible inhibitors are compounds that irreversibly inhibit the specified enzyme " + 
		"activity by binding to the enzyme and dissociating so slowly that it is considered " + 
		"irreversible. For example, alkylating agents, such as iodoacetamide, irreversibly inhibit " + 
		"the catalytic activity of some enzymes by modifying cysteine side chains.\n\n" +
		"INHIBITION-NONCOMPETITIVE\n" +
		"Noncompetitive inhibitors are compounds that noncompetitively inhibit the specified " + 
		"enzyme by binding reversibly to both the free enzyme and to the enzyme-substrate complex. " + 
		"The inhibitor and substrate may be bound to the enzyme simultaneously and do not exclude " + 
		"each other. However, only the enzyme-substrate complex (not the enzyme-substrate-inhibitor " + 
		"complex) is catalytically active.\n\n" +
		"INHIBITION-OTHER\n" +
		"Compounds that inhibit the specified enzyme activity by a mechanism that has been " + 
		"characterized, but that cannot be clearly classified as irreversible, competitive, " + 
		"noncompetitive, uncompetitive, or allosteric.\n\n" +
		"INHIBITION-UNCOMPETITIVE\n" +
		"Uncompetitive inhibitors are compounds that uncompetitively inhibit the specified enzyme " + 
		"activity by binding reversibly to the enzyme-substrate complex but not to the enzyme alone.\n\n" +
		"ACTIVATION-NONALLOSTERIC\n" +
		"Nonallosteric activators increase the specified enzyme activity by means other than " + 
		"allosteric.\n\n" +
		"ACTIVATION-ALLOSTERIC\n" +
		"Allosteric activators increase the specified enzyme activity by binding reversibly to the " + 
		"enzyme and inducing a conformational change that increases the affinity of the enzyme to " + 
		"its substrates without affecting its VMAX.");
		
		setTypedComment(Conversion, 
		"Definition: An interaction in which one or more entities is physically transformed into one " + 
		"or more other entities.\n" +
		"Comment: This class is designed to represent a simple, single-step transformation. " + 
		"Multi-step transformations, such as the conversion of glucose to pyruvate in the glycolysis " + 
		"pathway, should be represented as pathways, if known. Since it is a highly abstract class " + 
		"in the ontology, instances of the conversion class should never be created. More specific " + 
		"classes should be used instead.\n" +
		"Examples: A biochemical reaction converts substrates to products, the process of complex " + 
		"assembly converts single molecules to a complex, transport converts entities in one " + 
		"compartment to the same entities in another compartment.");
		
		setTypedComment(conversionDirection, 
		"This property represents the direction of the reaction. If a reaction is fundamentally " + 
		"irreversible, then it will run in a single direction under all contexts. Otherwise it is " + 
		"reversible.");
		
		setTypedComment(CovalentBindingFeature, 
		"Covalent bond within or between physical entities.");

		setTypedComment(dataSource, 
		"A free text description of the source of this data, e.g. a database or person name. " + 
		"This property should be used to describe the source of the data. This is meant to be used " + 
		"by databases that export their data to the BioPAX format or by systems that are integrating " + 
		"data from multiple sources. The granularity of use (specifying the data source in many or " + 
		"few instances) is up to the user. It is intended that this property report the last data " + 
		"source, not all data sources that the data has passed through from creation.");
		
		setEnglishComment(db, 
		"The name of the external database to which this xref refers.");

		setEnglishComment(dbVersion, 
		"The version of the external database in which this xref was last known to be valid. " + 
		"Resources may have recommendations for referencing dataset versions. For instance, the " + 
		"Gene Ontology recommends listing the date the GO terms were downloaded.");
		
		setTypedComment(Degradation, 
		"The process of degrading a physical entity. The right side of the conversion is not specified, " + 
		"indicating degraded components. The conversion is not spontaneous.");

		setTypedComment(DeltaG, 
		"Definition: For biochemical reactions, this property refers to the standard transformed " + 
		"Gibbs energy change for a reaction written in terms of biochemical reactants (sums of species), " + 
		"delta-G'<sup>0</sup>.\n\n" +
		"  delta-G'<sup>0</sup> = -RT lnK'\n" +
		"and\n" +
		"  delta-G'<sup>0</sup> = delta-H'<sup>0</sup> - T delta-S'<sup>0</sup>\n\n" +
		"delta-G'<sup>0</sup> has units of kJ/mol.  Like K', it is a function of temperature (T), " + 
		"ionic strength (I), pH, and pMg (pMg = -log<sub>10</sub>[Mg<sup>2+</sup>]). Therefore, these " + 
		"quantities must be specified, and values for DELTA-G for biochemical reactions are represented " + 
		"as 5-tuples of the form (delta-G'<sup>0</sup> T I pH pMg).  This property may have multiple " + 
		"values, representing different measurements for delta-G'<sup>0</sup> obtained under the " + 
		"different experimental conditions listed in the 5-tuple.\n\n" +
		"(This definition from EcoCyc)");
		
		setTypedComment(deltaG, 
		"For biochemical reactions, this property refers to the standard transformed Gibbs energy " + 
		"change for a reaction written in terms of biochemical reactants (sums of species), delta-G\n\n" +
		"Since Delta-G can change based on multiple factors including ionic strength and temperature " + 
		"a reaction can have multiple DeltaG values.");
		
		setTypedComment(deltaGPrime0, 
		"For biochemical reactions, this property refers to the standard transformed Gibbs energy " + 
		"change for a reaction written in terms of biochemical reactants (sums of species), " + 
		"delta-G'<sup>o</sup>.\n\n" +
		"  delta-G'<sup>o</sup> = -RT lnK'\n" +
		"and\n" +
		"  delta-G'<sup>o</sup> = delta-H'<sup>o</sup> - T delta-S'<sup>o</sup>\n\n" +
		"delta-G'<sup>o</sup> has units of kJ/mol.  Like K', it is a function of temperature (T), " + 
		"ionic strength (I), pH, and pMg (pMg = -log<sub>10</sub>[Mg<sup>2+</sup>]). Therefore, " + 
		"these quantities must be specified, and values for DELTA-G for biochemical reactions are " + 
		"represented as 5-tuples of the form (delta-G'<sup>o</sup> T I pH pMg).\n\n" +
		"(This definition from EcoCyc)");
		
		setTypedComment(deltaH, 
		"For biochemical reactions, this property refers to the standard transformed enthalpy change " + 
		"for a reaction written in terms of biochemical reactants (sums of species), " + 
		"delta-H'<sup>o</sup>.\n\n" +
		"  delta-G'<sup>o</sup> = delta-H'<sup>o</sup> - T delta-S'<sup>o</sup>\n\n" +
		"Units: kJ/mole\n\n" +
		"(This definition from EcoCyc)");
		
		setTypedComment(deltaS, 
		"For biochemical reactions, this property refers to the standard transformed entropy change " + 
		"for a reaction written in terms of biochemical reactants (sums of species), " + 
		"delta-S'<sup>o</sup>.\n\n" +
		"  delta-G'<sup>o</sup> = delta-H'<sup>o</sup> - T delta-S'<sup>o</sup>\n\n" +
		"(This definition from EcoCyc)");
		
		setTypedComment(displayName, 
		"An abbreviated name for this entity, preferably a name that is short enough to be used in " + 
		"a visualization application to label a graphical element that represents this entity. If " + 
		"no short name is available, an xref may be used for this purpose by the visualization " + 
		"application.");
		
		setTypedComment(Dna, 
		"Definition: A physical entity consisting of a sequence of deoxyribonucleotide monophosphates; " + 
		"a deoxyribonucleic acid.\n" +
		"Comment: This is not a 'gene', since gene is a genetic concept, not a physical entity. " + 
		"The concept of a gene may be added later in BioPAX.\n" +
		"Examples: a chromosome, a plasmid. A specific example is chromosome 7 of Homo sapiens.");
		
		setTypedComment(DnaReference, 
		"A DNA reference is a grouping of several DNA entities that are common in sequence and " + 
		"genomic position.  Members can differ in celular location, sequence features, SNPs, mutations " + 
		"and bound partners.\n\n" +
        "Comments : Note that this is not a reference gene. A gene can possibly span multiple " + 
        "DNA molecules, sometimes even across chromosomes due to regulatory regions. Similarly a gene " + 
        "is not necessarily made up of deoxyribonucleic acid and can be present in multiple copies " + 
        "( which are different DNA molecules).");

		setTypedComment(DnaRegion, 
		"A region of DNA.");

		setTypedComment(eCNumber, 
		"The unique number assigned to a reaction by the Enzyme Commission of the International " + 
		"Union of Biochemistry and Molecular Biology.\n\n" +
		"Note that not all biochemical reactions currently have EC numbers assigned to them.");
		
		setTypedComment(Entity, 
		"Definition: A discrete biological unit used when describing pathways. This is an interacting " + 
		"entity, not just any entity.\n" +
		"Comment: This is the root class for all interacting components in the ontology, " + 
		"which include pathways, interactions and physical entities. As the most abstract class " + 
		"in the ontology, instances of the entity class should never be created. " + 
		"Instead, more specific classes should be used.\n" +
		"Synonyms: thing, object, bioentity.");

		setTypedComment(EntityFeature, 
		"A feature or aspect of a physical entity that can be changed while the entity still retains " + 
		"its biological identity.");
		
		setTypedComment(entityFeature, 
		"Variable features that are observed for this entity - such as known PTM or methylation " + 
		"sites and non-covalent bonds.");
		
		setTypedComment(EntityReference, 
		"Definition: An entity reference is a grouping of several physical entities across different " + 
		"contexts and molecular states, that share common physical properties and often named and " + 
		"treated as a single entity with multiple states by biologists.\n\n" +
		"Entity references store the information common to a set of molecules in various states " + 
		"described in the BioPAX document, including database cross-references. For instance, the " + 
		"P53 protein can be phosphorylated in multiple different ways. Each separate P53 protein (pool) " + 
		"in a phosphorylation state would be represented as a different protein (child of " + 
		"physicalEntity) and all things common to all P53 proteins, including all possible " + 
		"phosphorylation sites, the sequence common to all of them and common references to " + 
		"protein databases containing more information about P53 would be stored in a " + 
		"Entity Reference.  \n\n" +
		"Comment: Many protein, small molecule and gene databases share this point of view, and such " + 
		"a grouping is an important prerequisite for interoperability with those databases. " + 
		"Biologists would often group different pools of molecules in different contexts under the " + 
		"same name. For example cytoplasmic and extracellular calcium have different effects on " + 
		"the cell's behavior, but they are still called calcium. This grouping has three " + 
		"semantic implications:\n\n" +
		"1.  Members of different pools share many physical and biochemical properties. " + 
		"This includes their chemical structure, sequence, organism and set of molecules they react " + 
		"with. They will also share a lot of secondary information such as their names, functional " + 
		"groupings, annotation terms and database identifiers.\n\n" +
		"2. A small number of transitions seperates these pools. In other words it is relatively easy " + 
		"and frequent for a molecule to transform from one physical entity to another that belong to " + 
		"the same reference entity. For example an extracellular calcium can become cytoplasmic, " + 
		"and p53 can become phosphorylated. However no calcium virtually becomes sodium, or no p53 " + 
		"becomes mdm2. In the former it is the sheer energy barrier of a nuclear reaction, in the " + 
		"latter sheer statistical improbability of synthesizing the same sequence without a template. " + 
		"If one thinks about the biochemical network as molecules transforming into each other, " + 
		"and remove edges that respond to transcription, translation, degradation and covalent " + 
		"modification of small molecules, each remaining component is a reference entity.\n\n" +
		"3. Some of the pools in the same group can overlap. p53-p@ser15 can overlap with p53-p@thr18. " + 
		"Most of the experiments in molecular biology will only check for one state variable, " + 
		"rarely multiple, and never for the all possible combinations. So almost all statements " + 
		"that refer to the state of the molecule talk about a pool that can overlap with other pools. " + 
		"However no overlaps is possible between molecules of different groups.");
		
		setTypedComment(entityReference, 
		"Reference entity for this physical entity.");
		
		setTypedComment(entityReferenceType, 
		"A controlled vocabulary term that is used to describe the type of grouping such as " + 
		"homology or functional group.");
		
		setTypedComment(EntityReferenceTypeVocabulary, 
		"A reference to a term from a reference entity group ontology. " + 
		"There is no \"best-practice\" vocabulary for this one.");

		setTypedComment(Evidence, 
		"Definition: The support for a particular assertion, such as the existence of an interaction " + 
		"or pathway. At least one of CONFIDENCE, EVIDENCE-CODE, or EXPERIMENTAL-FORM must be " + 
		"instantiated when creating an evidence instance. XREF may reference a publication describing " + 
		"the experimental evidence using a publicationXref or may store a description of the experiment " + 
		"in an experimental description database using a unificationXref (if the referenced experiment " + 
		"is the same) or relationshipXref (if it is not identical, but similar in some way e.g. similar " + 
		"in protocol). Evidence is meant to provide more information than just an xref to the " + 
		"source paper.\n" +
		"Examples: A description of a molecular binding assay that was used to detect a " + 
		"protein-protein interaction.");
		
		setTypedComment(evidence, 
		"Scientific evidence supporting the existence of the entity as described.");
		
		setTypedComment(evidenceCode, 
		"A pointer to a term in an external controlled vocabulary, such as the GO, PSI-MI or BioCyc " + 
		"evidence codes, that describes the nature of the support, such as 'traceable author " + 
		"statement' or 'yeast two-hybrid'.");
		
		setTypedComment(EvidenceCodeVocabulary, 
		"A reference to the PSI Molecular Interaction ontology (MI) experimental method types, " + 
		"including \"interaction detection method\", \"participant identification method\", \"feature " + 
		"detection method\". Homepage at http://www.psidev.info/.  " + 
		"Browse at http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI\n\n" +
		"Terms from the Pathway Tools Evidence Ontology may also be used. " + 
		"Homepage http://brg.ai.sri.com/evidence-ontology/");
		
		setTypedComment(experimentalFeature, 
		"A feature of the experimental form of the participant of the interaction, such as a " + 
		"protein tag. It is not expected to occur in vivo or be necessary for the interaction.");
		
		setTypedComment(ExperimentalForm, 
		"Definition: The form of a physical entity in a particular experiment, as it may be modified " + 
		"for purposes of experimental design.\n" +
		"Examples: A His-tagged protein in a binding assay. A protein can be tagged by multiple tags, " + 
		"so can have more than 1 experimental form type terms");

		setTypedComment(experimentalForm, 
		"The experimental forms associated with an evidence instance.");
		
		setTypedComment(experimentalFormDescription, 
		"Descriptor of this experimental form from a controlled vocabulary.");

		setTypedComment(experimentalFormEntity, 
		"The gene or physical entity that this experimental form describes.");
		
		setTypedComment(ExperimentalFormVocabulary, 
		"A reference to the PSI Molecular Interaction ontology (MI) participant identification method " + 
		"(e.g. mass spectrometry), experimental role (e.g. bait, prey), experimental preparation " + 
		"(e.g. expression level) type. Homepage at http://www.psidev.info/.  Browse " + 
		"http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI&termId=MI%3A0002&" + 
		"termName=participant%20identification%20method\n\n" +
		"http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI&termId=MI%3A0495&" + 
		"termName=experimental%20role\n\n" +
		"http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI&termId=MI%3A0346&" + 
		"termName=experimental%20preparation");
		
		setTypedComment(feature, 
		"Sequence features of the owner physical entity.");
		
		setTypedComment(featureLocation, 
		"Location of the feature on the sequence of the interactor. One feature may have more than " + 
		"one location, used e.g. for features which involve sequence positions close in the folded, " + 
		"three-dimensional state of a protein, but non-continuous along the sequence.");
		
		setTypedComment(featureLocationType, 
		"A controlled vocabulary term describing the type of the sequence location such as C-Terminal " + 
		"or SH2 Domain.");
		
		setTypedComment(FragmentFeature, 
		"Definition: Represents the results of a cleavage or degradation event. \n" +
		"There are multiple cases:\n" +
		"1.    A protein with a single cleavage site that converts the protein into two fragments " + 
		"(e.g. pro-insulin converted to insulin and C-peptide). TODO: CV term for sequence fragment?  " + 
		"PSI-MI CV term for cleavage site?\n" +
		"2.    A protein with two cleavage sites that removes an internal sequence e.g. an intein " + 
		"i.e. ABC -> AC\n" +
		"3.    Cleavage of a circular sequence e.g. a plasmid.");
		
		setTypedComment(Gene, 
		"A continuant that encodes information that can be inherited through replication. This is " + 
		"a generalization of the prokaryotic and eukaryotic notion of a gene. This is used only for " + 
		"genetic interactions. Gene expression regulation makes use of DNA and RNA physical entities.");
		
		setTypedComment(GeneticInteraction, 
		"Genetic interactions between genes occur when two genetic perturbations (e.g. mutations) have " + 
		"a combined phenotypic effect not caused by either perturbation alone. This is not a " + 
		"physical interaction, but rather logical. For example, a synthetic lethal interaction " + 
		"occurs when cell growth is possible without either gene A OR B, but not without both gene " + 
		"A AND B. If you knock out A and B together, the cell will die. A gene participant in a " + 
		"genetic interaction represents the gene that is perturbed.");
		
		setEnglishComment(id, 
		"The primary identifier in the external database of the object to which this xref refers.");
		
		setTypedComment(idVersion, 
		"The version number of the identifier (ID). E.g. The RefSeq accession number NM_005228.3 " + 
		"should be split into NM_005228 as the ID and 3 as the ID-VERSION.");
		
		setTypedComment(Interaction, 
		"Definition: A single biological relationship between two or more entities. An interaction " + 
		"cannot be defined without the entities it relates.\n" + 
		"Comment: Since it is a highly abstract class in the ontology, instances of the interaction " + 
		"class should never be created. Instead, more specific classes should be used. Currently " + 
		"this class only has subclasses that define physical interactions; later levels of BioPAX " + 
		"may define other types of interactions, such as genetic (e.g. synthetic lethal).\n" +
		"Naming rationale: A number of names were considered for this concept, " + 
		"including \"process\", \"synthesis\" and \"relationship\"; Interaction was chosen as it " + 
		"is understood by biologists in a biological context and is compatible with PSI-MI.\n" +
		"Examples: protein-protein interaction, biochemical reaction, enzyme catalysis");
		
		setTypedComment(interactionType, 
		"External controlled vocabulary annotating the interaction type, for " + 
		"example \"phosphorylation\". This is annotation useful for e.g. display on a web page or " + 
		"database searching, but may not be suitable for other computing tasks, like reasoning.");
		
		setTypedComment(InteractionVocabulary, 
		"A reference to the PSI Molecular Interaction ontology (MI) interaction type. " + 
		"Homepage at http://www.psidev.info/.  " + 
		"Browse at http://www.ebi.ac.uk/ontology-lookup/browse.do?" + 
		"ontName=MI&termId=MI%3A0190&termName=interaction%20type");

		setTypedComment(interactionScore, 
		"The score of an interaction e.g. a genetic interaction score."); 
		
		setTypedComment(intraMolecular, 
		"This flag represents whether the binding feature is within the same molecule or not.");
		
		setTypedComment(ionicStrength, 
		"The ionic strength is defined as half of the total sum of the concentration (ci) of every " + 
		"ionic species (i) in the solution times the square of its charge (zi). For example, the " + 
		"ionic strength of a 0.1 M solution of CaCl2 is 0.5 x (0.1 x 22 + 0.2 x 12) = 0.3 M\n" +
		"(Definition from http://www.lsbu.ac.uk/biology/enztech/ph.html)");
		
		setTypedComment(kEQ, 
		"This quantity is dimensionless and is usually a single number. The measured equilibrium " + 
		"constant for a biochemical reaction, encoded by the slot KEQ, is actually the apparent " + 
		"equilibrium constant, K'.  Concentrations in the equilibrium constant equation refer to the " + 
		"total concentrations of  all forms of particular biochemical reactants. For example, in the " + 
		"equilibrium constant equation for the biochemical reaction in which ATP is hydrolyzed to ADP " + 
		"and inorganic phosphate:\n\n" +
		"K' = [ADP][P<sub>i</sub>]/[ATP],\n\n" +
		"The concentration of ATP refers to the total concentration of all of the following species:\n\n" +
		"[ATP] = [ATP<sup>4-</sup>] + [HATP<sup>3-</sup>] + " + "[H<sub>2</sub>ATP<sup>2-</sup>] + " + 
		"[MgATP<sup>2-</sup>] + [MgHATP<sup>-</sup>] + [Mg<sub>2</sub>ATP].\n\n" +
		"The apparent equilibrium constant is formally dimensionless, and can be kept so by inclusion " + 
		"of as many of the terms (1 mol/dm<sup>3</sup>) in the numerator or denominator as necessary.  " + 
		"It is a function of temperature (T), ionic strength (I), pH, and pMg " + 
		"(pMg = -log<sub>10</sub>[Mg<sup>2+</sup>]). Therefore, these quantities must be specified to " + 
		"be precise, and values for KEQ for biochemical reactions may be represented as 5-tuples of " + 
		"the form (K' T I pH pMg).  This property may have multiple values, representing different " + 
		"measurements for K' obtained under the different experimental conditions listed in the 5-tuple. " + 
		"(This definition adapted from EcoCyc)");

		setTypedComment(KPrime, 
		"Definition: The apparent equilibrium constant, K', and associated values.  Concentrations in " + 
		"the equilibrium constant equation refer to the total concentrations of  all forms of " + 
		"particular biochemical reactants. For example, in the equilibrium constant equation for the " + 
		"biochemical reaction in which ATP is hydrolyzed to ADP and inorganic phosphate:\n\n" +
		"K' = [ADP][P<sub>i</sub>]/[ATP],\n\n" + 
		"The concentration of ATP refers to the total concentration of all of the following species:\n\n" +
		"[ATP] = [ATP<sup>4-</sup>] + [HATP<sup>3-</sup>] + [H<sub>2</sub>ATP<sup>2-</sup>] + " + 
		"[MgATP<sup>2-</sup>] + [MgHATP<sup>-</sup>] + [Mg<sub>2</sub>ATP].\n\n" +
		"The apparent equilibrium constant is formally dimensionless, and can be kept so by inclusion " + 
		"of as many of the terms (1 mol/dm<sup>3</sup>) in the numerator or denominator as necessary.  " + 
		"It is a function of temperature (T), ionic strength (I), pH, and " + 
		"pMg (pMg = -log<sub>10</sub>[Mg<sup>2+</sup>]). Therefore, these quantities must be " + 
		"specified to be precise, and values for KEQ for biochemical reactions may be represented " + 
		"as 5-tuples of the form (K' T I pH pMg).  This property may have multiple values, " + 
		"representing different measurements for K' obtained under the different experimental " + 
		"conditions listed in the 5-tuple. (This definition adapted from EcoCyc)\n\n" +
		"See http://www.chem.qmul.ac.uk/iubmb/thermod/ for a thermodynamics tutorial.");

		setTypedComment(memberEntityReference, 
		"An entity reference that qualifies for the definition of this group. For example a member " + 
		"of a PFAM protein family.");

		setTypedComment(kPrime, 
		"The apparent equilibrium constant K'. Concentrations in the equilibrium constant equation " + 
		"refer to the total concentrations of  all forms of particular biochemical reactants. " + 
		"For example, in the equilibrium constant equation for the biochemical reaction in which " + 
		"ATP is hydrolyzed to ADP and inorganic phosphate:\n\n" +
		"K' = [ADP][P<sub>i</sub>]/[ATP],\n\n" +
		"The concentration of ATP refers to the total concentration of all of the following species:\n\n" +
		"[ATP] = [ATP<sup>4-</sup>] + [HATP<sup>3-</sup>] + [H<sub>2</sub>ATP<sup>2-</sup>] + " + 
		"[MgATP<sup>2-</sup>] + [MgHATP<sup>-</sup>] + [Mg<sub>2</sub>ATP].\n\n" +
		"The apparent equilibrium constant is formally dimensionless, and can be kept so by inclusion " + 
		"of as many of the terms (1 mol/dm<sup>3</sup>) in the numerator or denominator as necessary.  " + 
		"It is a function of temperature (T), ionic strength (I), pH, and " + 
		"pMg (pMg = -log<sub>10</sub>[Mg<sup>2+</sup>]).\n" +
		"(Definition from EcoCyc)");
		
		setTypedComment(left, 
		"The participants on the left side of the conversion interaction. Since conversion " + 
		"interactions may proceed in either the left-to-right or right-to-left direction, occupants " + 
		"of the LEFT property may be either reactants or products. LEFT is a sub-property of " + 
		"PARTICIPANTS.");
		
		setTypedComment(memberFeature, 
		"An entity feature  that belongs to this homology grouping. Example: a homologous " + 
		"phosphorylation site across a protein family.");
		
		setTypedComment(memberPhysicalEntity, 
		"This property stores the members of a generic physical entity. \n\n" +
		"For representing homology generics a better way is to use generic entity references and " + 
		"generic features. However not all generic logic can be captured by this, such as complex " + 
		"generics or rare cases where feature cardinality is variable. Usages of this property " + 
		"should be limited to such cases.");
		
		setTypedComment(ModificationFeature, 
		"Definition: A covalently modified feature on a sequence relevant to an interaction, such " + 
		"as a post-translational modification. The difference between this class  and bindingFeature " + 
		"is that this is covalent and bindingFeature is non-covalent.\n" +
		"Examples: A phosphorylation on a protein.");
		
		setTypedComment(modificationType, 
		"Description and classification of the feature.");
		
		setTypedComment(Modulation, 
		"Definition: A control interaction in which a physical entity modulates a catalysis " + 
		"interaction. Biologically, most modulation interactions describe an interaction in which a " + 
		"small molecule alters the ability of an enzyme to catalyze a specific reaction. Instances of " + 
		"this class describe a pairing between a modulating entity and a catalysis interaction.\n" +
		"Comment: A separate modulation instance should be created for each different catalysis " + 
		"instance that a physical entity may modulate and for each different physical entity that may " + 
		"modulate a catalysis instance. A typical modulation instance has a small molecule as the " + 
		"controller entity and a catalysis instance as the controlled entity.\n" +
		"Examples: Allosteric activation and competitive inhibition of an enzyme's ability to " + 
		"catalyze a specific reaction.");
		
		setTypedComment(MolecularInteraction, 
		"Definition: An interaction in which at least one participant is a physical entity, e.g. a " + 
		"binding event.\n" +
		"Comment: This class should be used by default for representing molecular interactions, " + 
		"such as those defined by PSI-MI level 2.5. The participants in a molecular interaction should " + 
		"be listed in the PARTICIPANTS slot. Note that this is one of the few cases in which the " + 
		"PARTICPANT slot should be directly populated with instances (see comments on the PARTICPANTS " + 
		"property in the interaction class description). If sufficient information on the nature of a " + 
		"molecular interaction is available, a more specific BioPAX interaction class should be used.\n" +
		"Example: Two proteins observed to interact in a yeast-two-hybrid experiment where there is " + 
		"not enough experimental evidence to suggest that the proteins are forming a complex by " + 
		"themselves without any indirect involvement of other proteins. This is the case for most " + 
		"large-scale yeast two-hybrid screens.");
		
		setEnglishComment(molecularWeight, 
		"Defines the molecular weight of the molecule, in daltons.");
		
		setEnglishComment(name, 
		"One or more synonyms for the name of this individual. This should include the values of " + 
		"the standardName and displayName property so that it is easy to find all known names " + 
		"in one place.");
		
		setTypedComment(nextStep, 
		"The next step(s) of the pathway.  Contains zero or more pathwayStep instances.  If there is " + 
		"no next step, this property is empty. Multiple pathwayStep instances indicate pathway branching.");
		
		setTypedComment(notFeature, 
		"Sequence features where the owner physical entity has a feature. If not specified, " + 
		"other potential features are not known.");

		setTypedComment(ontology, 
		"This is version 0.94 of the BioPAX Level 3 ontology.  The goal of the BioPAX group is to " + 
		"develop a common exchange format for biological pathway data.  More information is " + 
		"available at http://www.biopax.org.  This ontology is freely available under the LGPL " + 
		"(http://www.gnu.org/copyleft/lesser.html).");
		
		setEnglishComment(organism, 
		"An organism, e.g. 'Homo sapiens'. This is the organism that the entity is found in. " + 
		"Pathways may not have an organism associated with them, for instance, reference pathways " + 
		"from KEGG. Sequence-based entities (DNA, protein, RNA) may contain an xref to a sequence " + 
		"database that contains organism information, in which case the information should be " + 
		"consistent with the value for ORGANISM.");
		
		setTypedComment(participant, 
		"This property lists the entities that participate in this interaction. For example, in a " + 
		"biochemical reaction, the participants are the union of the reactants and the products of " + 
		"the reaction. This property has a number of sub-properties, such as LEFT and RIGHT used in " + 
		"the biochemicalInteraction class. Any participant listed in a sub-property will " + 
		"automatically be assumed to also be in PARTICIPANTS by a number of software systems, " + 
		"including Protege, so this property should not contain any instances if there are " + 
		"instances contained in a sub-property.");
		
		setTypedComment(participantStoichiometry, 
		"Stoichiometry of the left and right participants.");
		
		setTypedComment(Pathway, 
		"Definition: A set or series of interactions, often forming a network, which biologists have " + 
		"found useful to group together for organizational, historic, biophysical or other reasons.\n" +
		"Comment: It is possible to define a pathway without specifying the interactions within " + 
		"the pathway. In this case, the pathway instance could consist simply of a name and could " + 
		"be treated as a 'black box'.\n" +
		"Synonyms: network\n" +
		"Examples: glycolysis, valine biosynthesis");
		
		setTypedComment(pathwayComponent, 
		"The set of interactions and/or pathwaySteps in this pathway/network. Each instance of the " + 
		"pathwayStep class defines: 1) a set of interactions that together define a particular " + 
		"step in the pathway, for example a catalysis instance and the conversion that it catalyzes; " + 
		"2) an order relationship to one or more other pathway steps (via the NEXT-STEP property). " + 
		"Note: This ordering is not necessarily temporal - the order described may simply represent " + 
		"connectivity between adjacent steps. Temporal ordering information should only be inferred " + 
		"from the direction of each interaction.");
		
		setTypedComment(pathwayOrder, 
		"The ordering of components (interactions and pathways) in the context of this pathway. " + 
		"This is useful to specific circular or branched pathways or orderings when component " + 
		"biochemical reactions are normally reversible, but are directed in the context of this pathway.");
		
		setTypedComment(PathwayStep, 
		"Definition: A step in a pathway.\n" +
		"Comment: Multiple interactions may occur in a pathway step, each should be listed in the " + 
		"STEP-INTERACTION property. Order relationships between pathway steps may be established with " + 
		"the NEXT-STEP slot. This order may not be temporally meaningful for specific steps, such as " + 
		"for a pathway loop or a reversible reaction, but represents a directed graph of step " + 
		"relationships that can be useful for describing the overall flow of a pathway, as may be " + 
		"useful in a pathway diagram.\n" +
		"Example: A metabolic pathway may contain a pathway step composed of one biochemical reaction " + 
		"(BR1) and one catalysis (CAT1) instance, where CAT1 describes the catalysis of BR1. " + 
		"The M phase of the cell cycle, defined as a pathway, precedes the G1 phase, also defined as " + 
		"a pathway.");
		
		setTypedComment(patoData, 
		"The phenotype data from PATO, formatted as PhenoXML " + 
		"(defined at http://www.fruitfly.org/~cjm/obd/formats.html)");

		setTypedComment(ph, 
		"A measure of acidity and alkalinity of a solution that is a number on a scale on which a " + 
		"value of 7 represents neutrality and lower numbers indicate increasing acidity and " + 
		"higher numbers increasing alkalinity and on which each unit of change represents " + 
		"a tenfold change in acidity or alkalinity and that is the negative logarithm of the " + 
		"effective hydrogen-ion concentration or hydrogen-ion activity in gram equivalents per liter " + 
		"of the solution. (Definition from Merriam-Webster Dictionary)");

		setTypedComment(phenotype, 
		"The phenotype quality used to define this genetic interaction e.g. viability.");
		
		setTypedComment(PhenotypeVocabulary, 
		"The phenotype measured in the experiment e.g. growth rate or viability of a cell. This is " + 
		"only the type, not the value e.g. for a synthetic lethal interaction, the phenotype is " + 
		"viability, specified by ID: PATO:0000169, \"viability\", not the value (specified by " + 
		"ID: PATO:0000718, \"lethal (sensu genetics)\". A single term in a phenotype controlled " + 
		"vocabulary can be referenced using the xref, or the PhenoXML describing the PATO EQ model " + 
		"phenotype description can be stored as a string in PATO-DATA.");
		
		setTypedComment(PhysicalEntity, 
		"Definition: An entity with a physical structure. A pool of entities, not a specific " + 
		"molecular instance of an entity in a cell.\n" +
		"Comment: This class serves as the super-class for all physical entities, although its " + 
		"current set of subclasses is limited to molecules. As a highly abstract class in the " + 
		"ontology, instances of the physicalEntity class should never be created. Instead, more " + 
		"specific classes should be used.\n" +
		"Synonyms: part, interactor, object\n" +
		"Naming rationale: It's difficult to find a name that encompasses all of the subclasses of " + 
		"this class without being too general. E.g. PSI-MI uses 'interactor', BIND uses 'object', " + 
		"BioCyc uses 'chemicals'. physicalEntity seems to be a good name for this specialization " + 
		"of entity.\n" +
		"Examples: protein, small molecule, RNA");
		
		setEnglishComment(physicalEntity, 
		"The physical entity to be annotated with stoichiometry.");
		
		setTypedComment(pMg, 
		"A measure of the concentration of magnesium (Mg) in solution. " + 
		"(pMg = -log<sub>10</sub>[Mg<sup>2+</sup>])");
		
		setTypedComment(positionStatus, 
		"The confidence status of the sequence position. This could be:\n" +
		"EQUAL: The SEQUENCE-POSITION is known to be at the SEQUENCE-POSITION.\n" +
		"GREATER-THAN: The site is greater than the SEQUENCE-POSITION.\n" +
		"LESS-THAN: The site is less than the SEQUENCE-POSITION.");
		
		setTypedComment(product, 
		"The product of a template reaction.");
		
		setTypedComment(Protein, 
		"Definition: A physical entity consisting of a sequence of amino acids; a protein monomer; " + 
		"a single polypeptide chain.\n" + 
		"Examples: The epidermal growth factor receptor (EGFR) protein.");

		setTypedComment(ProteinReference, 
		"A protein reference is a grouping of several protein entities that are encoded " + 
		"by the same gene.  Members can differ in celular location, sequence features and " + 
		"bound partners. Currently conformational states (such as open and closed) are not covered.");

		setTypedComment(Provenance, 
		"Definition: The direct source of a pathway data or score. This does not store the trail of " + 
		"sources from the generation of the data to this point, only the last known source, such as a " + 
		"database. The XREF property may contain a publicationXref referencing a publication " + 
		"describing the data source (e.g. a database publication). A unificationXref may be used e.g. " + 
		"when pointing to an entry in a database of databases describing this database.\n" +
		"Examples: A database, scoring method or person name.");
		
		setTypedComment(PublicationXref, 
		"Definition: An xref that defines a reference to a publication such as a book, journal " + 
		"article, web page, or software manual. The reference may or may not be in a database, " + 
		"although references to PubMed are preferred when possible. The publication should make a " + 
		"direct reference to the instance it is attached to.\n" +
		"Comment: Publication xrefs should make use of PubMed IDs wherever possible. The DB property " + 
		"of an xref to an entry in PubMed should use the string \"PubMed\" and not \"MEDLINE\".\n" +
		"Examples: PubMed:10234245");
		
		setTypedComment(RelationshipTypeVocabulary, 
		"Vocabulary for defining relationship Xref types. A reference to the PSI Molecular " + 
		"Interaction ontology (MI) Cross Reference type. Homepage at http://www.psidev.info/.  " + 
		"Browse at http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI&termId=MI%3A0353&" + 
		"termName=cross-reference%20type");
		
		setTypedComment(RelationshipXref, 
		"Definition: An xref that defines a reference to an entity in an external resource that does " + 
		"not have the same biological identity as the referring entity.\n" +
		"Comment: There is currently no controlled vocabulary of relationship types for BioPAX, " + 
		"although one will be created in the future if a need develops.\n" +
		"Examples: A link between a gene G in a BioPAX data collection, and the protein product P " + 
		"of that gene in an external database. This is not a unification xref because G and P are " + 
		"different biological entities (one is a gene and one is a protein). Another example is a " + 
		"relationship xref for a protein that refers to the Gene Ontology biological process, " + 
		"e.g. 'immune response,' that the protein is involved in.");
		
		setTypedComment(right, 
		"The participants on the right side of the conversion interaction. Since conversion " + 
		"interactions may proceed in either the left-to-right or right-to-left direction, occupants " + 
		"of the RIGHT property may be either reactants or products. RIGHT is a sub-property of " + 
		"PARTICIPANTS.");
		
		setTypedComment(Rna, 
		"Definition: A physical entity consisting of a sequence of ribonucleotide monophosphates; " + 
		"a ribonucleic acid.\n" +
		"Examples: messengerRNA, microRNA, ribosomalRNA. A specific example is the let-7 microRNA.");
		
		setTypedComment(RnaReference, 
		"A RNA  reference is a grouping of several RNA entities that are either encoded by the same " + 
		"gene or replicates of the same genome.  Members can differ in celular location, sequence " + 
		"features and bound partners. Currently conformational states (such as hairpin) are not covered.");
		
		setTypedComment(RnaRegion, 
		"A region of RNA");

		setTypedComment(Score, 
		"Definition: A score associated with a publication reference describing how the score was " + 
		"determined, the name of the method and a comment briefly describing the method. The xref " + 
		"must contain at least one publication that describes the method used to determine the " + 
		"score value. There is currently no standard way of describing  values, so any string is valid.\n" + 
		"Examples: The statistical significance of a result, e.g. \"p<0.05\".");
		
		setEnglishComment(sequence, 
		"Polymer sequence in uppercase letters. For DNA, usually A,C,G,T letters representing the " + 
		"nucleosides of adenine, cytosine, guanine and thymine, respectively; for RNA, " + 
		"usually A, C, U, G; for protein, usually the letters corresponding to the 20 letter IUPAC " + 
		"amino acid code.");
		
		setTypedComment(SequenceInterval, 
		"Definition: Describes an interval on a sequence. All of the sequence from the begin site to " + 
		"the end site (inclusive) is described, not any subset.");
		
		setTypedComment(sequenceIntervalBegin, 
		"The begin position of a sequence interval.");

		setTypedComment(sequenceIntervalEnd, 
		"The end position of a sequence interval.");

		setTypedComment(SequenceLocation, 
		"Definition: A location on a nucleotide or amino acid sequence.\n" +
		"Comment: For organizational purposes only; direct instances of this class should not be created.");
		
		setTypedComment(sequencePosition, 
		"The integer listed gives the position. The first base or amino acid is position 1. " + 
		"In combination with the numeric value, the property 'POSITION-STATUS' allows to express " + 
		"fuzzy positions, e.g. 'less than 4'.");
		
		setTypedComment(SequenceModificationVocabulary, 
		"A reference to the PSI Molecular Interaction ontology (MI) of covalent sequence modifications. " + 
		"Homepage at http://www.psidev.info/.  Browse at " + 
		"http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI&termId=MI%3A0252&" + 
		"termName=biological%20feature. Only children that are covelent modifications at specific " + 
		"positions can be used.");
		
		setTypedComment(SequenceRegionVocabulary, 
		"A reference to a controlled vocabulary of sequence regions, such as InterPro or Sequence " + 
		"Ontology (SO). Homepage at http://www.sequenceontology.org/.  " + 
		"Browse at http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=SO");
		
		setTypedComment(SequenceSite, 
		"Definition: Describes a site on a sequence, i.e. the position of a single nucleotide or " + 
		"amino acid.");
		
		setTypedComment(source, 
		"The source  in which the reference was published, such as: a book title, or a journal title " + 
		"and volume and pages.");
		
		setTypedComment(SmallMolecule, 
		"Definition: A small bioactive molecule. Small is not precisely defined, but includes " + 
		"all metabolites and most drugs and does not include large polymers, including complex " + 
		"carbohydrates.\n" +
		"Comment: Recently, a number of small molecule databases have become available to " + 
		"cross-reference from this class.\n" +
		"Examples: glucose, penicillin, phosphatidylinositol\n" +
		"Note: Complex carbohydrates are not currently modeled in BioPAX or most pathway databases, " + 
		"due to the lack of a publicly available complex carbohydrate database."); 
		
		setTypedComment(SmallMoleculeReference, 
		"A small molecule reference is a grouping of several small molecule entities  that have the " + 
		"same chemical structure.  Members can differ in celular location and bound partners. " + 
		"Covalent modifications of small molecules are not considered as state changes but treated " + 
		"as different molecules.");
		
		setTypedComment(spontaneous, 
		"Specifies whether a conversion occurs spontaneously or not. If the spontaneity is not known, " + 
		"the SPONTANEOUS property should be left empty.");
		
		setEnglishComment(standardName, 
		"The preferred full name for this entity.");
		
		setTypedComment(stepConversion, 
		"The central process that take place at this step of the biochemical pathway.");

		setTypedComment(stepDirection, 
		"Direction of the conversion in this particular pathway context. \n" + 
		"This property can be used for annotating direction of enzymatic activity. Even if an enzyme " + 
		"catalyzes a reaction reversibly, the flow of matter through the pathway will force the " + 
		"equilibrium in a given direction for that particular pathway.");
		
		setTypedComment(stepProcess, 
		"An interaction or a pathway that are a part of this pathway step.");
		
		setTypedComment(Stoichiometry, 
		"Stoichiometric coefficient of a physical entity in the context of a conversion or complex.\n" +
		"For each participating element there must be 0 or 1 stoichiometry element. A non-existing " + 
		"stoichiometric element is treated as unknown.\n" +
		"This is an n-ary bridge for left, right and component properties.");
		
		setTypedComment(stoichiometricCoefficient, 
		"Stoichiometric coefficient for one of the entities in an interaction or complex. This value " + 
		"can be any rational number. Generic values such as \"n\" or \"n+1\" should not be used - " + 
		"polymers are currently not covered.");
		
		setEnglishComment(structure, 
		"Defines the chemical structure and other information about this molecule, using an instance " + 
		"of class chemicalStructure.");
		
		setTypedComment(structureData, 
		"This property holds a string of data defining chemical structure or other information, " + 
		"in either the CML or SMILES format, as specified in property Structure-Format. If, " + 
		"for example, the CML format is used, then the value of this property is a string containing " + 
		"the XML encoding of the CML data.");
		
		setEnglishComment(structureFormat, 
		"This property specifies which format is used to define chemical structure data.");
		
		setTypedComment(subRegion, 
		"The sub region of a region. The sub region must be wholly part of the region, not outside of it.");
		
		setTypedComment(taxonXref, 
		"An xref to an organism taxonomy database, preferably NCBI taxon. This should be an instance " + 
		"of unificationXref, unless the organism is not in an existing database.");
		
		setTypedComment(temperature, 
		"Temperature in Celsius");
		
		setTypedComment(template, 
		"The template molecule that is used in this template reaction.");
		
		setTypedComment(templateDirection, 
		"The direction of the template reaction on the template.");

		setTypedComment(TemplateReaction, 
		"Definiton: This class represents a polymerization of a macromolecule from a template. " + 
		"E.g. DNA to RNA is transcription, RNA to protein is translation and DNA to protein is " + 
		"protein expression from DNA. Other examples are possible. To store a promoter region, " + 
		"create a regulatory element and add a promoter feature on it, using the sequence " + 
		"region vocabulary.");
		
		setTypedComment(TemplateReactionRegulation, 
		"Definition: Regulation of the expression reaction by the controlling element such as a " + 
		"transcription factor or microRNA. E.g. To represent the binding of the transcription factor " + 
		"to a regulatory element in the TemplateReaction, create a complex of the transcription factor " + 
		"and the regulatory element and set that as the controller.");

		setTypedComment(term, 
		"The external controlled vocabulary term.");
		
		setEnglishComment(tissue, 
		"An external controlled vocabulary of tissue types.");

		setTypedComment(TissueVocabulary, 
		"A reference to the BRENDA (BTO). Homepage at http://www.brenda-enzymes.info/.  Browse at " + 
		"http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=BTO");
		
		setEnglishComment(title, 
		"The title of the publication.");
		
		setTypedComment(Transport, 
		"Definition: A conversion interaction in which an entity (or set of entities) changes location " + 
		"within or with respect to the cell. A transport interaction does not include the transporter " + 
		"entity, even if one is required in order for the transport to occur. Instead, transporters " + 
		"are linked to transport interactions via the catalysis class.\n" +
		"Comment: Transport interactions do not involve chemical changes of the participant(s). " + 
		"These cases are handled by the transportWithBiochemicalReaction class.\n" +
		"Synonyms: translocation.\n" +
		"Examples: The movement of Na+ into the cell through an open voltage-gated channel.");
		
		setTypedComment(TransportWithBiochemicalReaction, 
		"Definition: A conversion interaction that is both a biochemicalReaction and a transport. " + 
		"In transportWithBiochemicalReaction interactions, one or more of the substrates change both " + 
		"their location and their physical structure. Active transport reactions that use ATP as an " + 
		"energy source fall under this category, even if the only covalent change is the hydrolysis " + 
		"of ATP to ADP.\n" +
		"Comment: This class was added to support a large number of transport events in pathway " + 
		"databases that have a biochemical reaction during the transport process. It is not expected " + 
		"that other double inheritance subclasses will be added to the ontology at the same level as " + 
		"this class.\n" +
		"Examples: In the PEP-dependent phosphotransferase system, transportation of sugar into an " + 
		"E. coli cell is accompanied by the sugar's phosphorylation as it crosses the plasma membrane.");
		
		setTypedComment(UnificationXref, 
		"Definition: A unification xref defines a reference to an entity in an external resource that " + 
		"has the same biological identity as the referring entity. For example, if one wished to link " + 
		"from a database record, C, describing a chemical compound in a BioPAX data collection to a " + 
		"record, C', describing the same chemical compound in an external database, one would use a " + 
		"unification xref since records C and C' describe the same biological identity. Generally, " + 
		"unification xrefs should be used whenever possible, although there are cases where they " + 
		"might not be useful, such as application to application data exchange.\n" +
		"Comment: Unification xrefs in physical entities are essential for data integration, " + 
		"but are less important in interactions. This is because unification xrefs on the " + 
		"physical entities in an interaction can be used to compute the equivalence of two " + 
		"interactions of the same type. An xref in a protein pointing to a gene, e.g. in the " + 
		"LocusLink database17, would not be a unification xref since the two entities do not have " + 
		"the same biological identity (one is a protein, the other is a gene). Instead, this link " + 
		"should be a captured as a relationship xref. References to an external controlled " + 
		"vocabulary term within the OpenControlledVocabulary class should use a unification xref " + 
		"where possible (e.g. GO:0005737).\n" +
		"Examples: An xref in a protein instance pointing to an entry in the Swiss-Prot database, " + 
		"and an xref in an RNA instance pointing to the corresponding RNA sequence in the RefSeq database..");
		
		setTypedComment(UtilityClass, 
		"Definition: Utility classes are created when simple slots are insufficient to describe " + 
		"an aspect of an entity or to increase compatibility of this ontology with other standards.  " + 
		"The utilityClass class is actually a metaclass and is only present to organize the other " + 
		"helper classes under one class hierarchy; instances of utilityClass should never be created.");
		
		setEnglishComment(url, 
		"The URL at which the publication can be found, if it is available through the Web.");
		
		setTypedComment(value, 
		"The value of the score.");
		
		setTypedComment(Xref, 
		"Definition: A reference from an instance of a class in this ontology to an object in an " + 
		"external resource.\n" +
        "Comment: Instances of the xref class should never be created and more specific classes should " + 
        "be used instead.");
		
		setEnglishComment(xref, 
		"Values of this property define external cross-references from this entity to entities in " + 
		"external databases.");

		setEnglishComment(year, 
		"The year in which this publication was published.");
		
	}

	public static void main(String[] args) {
		Model bp3f = ModelFactory.createDefaultModel();
		String fileName = "bin/sybil/ontologies/biopax-level3.owl";
		try {
			InputStream fileStream = new FileInputStream(fileName);
			bp3f.read(fileStream, "");
			ModelComparer comparer = new ModelComparer(bp3f, schema);
			comparer.printReport(System.out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}

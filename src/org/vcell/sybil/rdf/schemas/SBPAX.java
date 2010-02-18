package org.vcell.sybil.rdf.schemas;

/*   SBPAX  --- by Oliver Ruebenacker, UCHC --- April 2008 to November 2009
 *   The SBPAX schema
 */

import java.util.List;
import org.vcell.sybil.rdf.NameSpace;
import org.vcell.sybil.rdf.OntSpecUtil;
import org.vcell.sybil.rdf.OntUtil;
import org.vcell.sybil.rdf.specgen.OntologyInfo;
import org.vcell.sybil.util.lists.ListOfNone;
import org.vcell.sybil.util.sets.SetOfThree;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.ontology.TransitiveProperty;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class SBPAX {

	public static final OntologyBox box = new OntologyBox() {
		public Model getRdf() { return schema; }
		public String uri() { return URI; }
		public NameSpace ns() { return ns; }
		public String label() { return label; }
		public List<DatatypeProperty> labelProperties() { return labelProperties; }
	};
	
	public static final String label = "SBPAX";
	public static final List<DatatypeProperty> labelProperties = new ListOfNone<DatatypeProperty>();
	
	public static final String URI = "http://vcell.org/2008/11/sbpax";
	
	public static final NameSpace ns = new NameSpace("sbpax", URI + "#");
	
	public static final OntModel schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

	static {
		OntUtil.emptyPrefixMapping(schema);
		schema.setNsPrefix("", ns.uri);
		schema.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
		schema.setNsPrefix("rdfs", RDFS.getURI());
		schema.setNsPrefix("owl", OWL.NS);
		schema.setNsPrefix("rdf", RDF.getURI());
		schema.setNsPrefix("bp2", BioPAX2.ns.uri);
		schema.setNsPrefix("bfo", BFO.ns.uri);
		schema.setNsPrefix("bfo-snap", BFO.nsSnap.uri);
		schema.setNsPrefix("bfo-span", BFO.nsSpan.uri);
	}
	
	public static final Ontology ontology = schema.createOntology(URI);
	
	static {
		ontology.addImport(BFO.ontology);
		ontology.addImport(BioPAX2.ontology);
	}
	
	public static final OntClass Amount = schema.createClass(ns + "Amount");
	public static final OntClass Interaction = schema.createClass(ns + "Interaction");
	public static final OntClass BinarySetIntersection = schema.createClass(ns + "BinarySetIntersection");
	public static final OntClass BinarySetOp = schema.createClass(ns + "BinarySetOp");
	public static final OntClass BinarySetUnion = schema.createClass(ns + "BinarySetUnion");
	public static final OntClass Location = schema.createClass(ns + "Location");
	public static final OntClass Model = schema.createClass(ns + "Model");
	public static final OntClass Process = schema.createClass(ns + "Process");
	public static final OntClass ProcessModel = schema.createClass(ns + "ProcessModel");
	public static final OntClass ProcessParticipant = schema.createClass(ns + "ProcessParticipant");
	public static final OntClass ProcessParticipantCatalyst = 
		schema.createClass(ns + "ProcessParticipantCatalyst");
	public static final OntClass ProcessParticipantLeft = 
		schema.createClass(ns + "ProcessParticipantLeft");
	public static final OntClass ProcessParticipantRight = 
		schema.createClass(ns + "ProcessParticipantRight");
	public static final OntClass Quantity = schema.createClass(ns + "Quantity");
	public static final OntClass SequenceFeature = schema.createClass(ns + "SequenceFeature");
	public static final OntClass SetOp = schema.createClass(ns + "SetOp");
	public static final OntClass Species = schema.createClass(ns + "Species");
	public static final OntClass SpeciesAmount = schema.createClass(ns + "SpeciesAmount");
	public static final OntClass Stoichiometry = schema.createClass(ns + "Stoichiometry");	
	public static final OntClass Substance = schema.createClass(ns + "Substance");
	public static final OntClass SubstanceAmount = schema.createClass(ns + "SubstanceAmount");
	public static final OntClass SystemModel = schema.createClass(ns + "SystemModel");
	public static final OntClass Unit = schema.createClass(ns + "Unit");	
	
	public static final OntClass Protein = schema.createClass(ns + "Protein");	
	public static final OntClass RNA = schema.createClass(ns + "RNA");	
	public static final OntClass DNA = schema.createClass(ns + "DNA");	
	public static final OntClass Complex = schema.createClass(ns + "Complex");	
	public static final OntClass Metabolite = schema.createClass(ns + "Metabolite");	

	public static final RDFList substanceClassList = schema.createList().with(Protein).with(DNA)
	.with(RNA).with(Complex).with(Metabolite);
	
	public static final Bag defaultUSTBag = schema.createBag(ns + "defaultUSTBag");
	
	static {
		defaultUSTBag.add(Metabolite);
	}
	
	public static final ObjectProperty consistsOf = schema.createObjectProperty(ns + "consistsOf");
	public static final ObjectProperty hasComponent = schema.createObjectProperty(ns + "hasComponent");
	public static final ObjectProperty hasModel = schema.createObjectProperty(ns + "hasModel");
	public static final ObjectProperty hasParticipant = 
		schema.createObjectProperty(ns + "hasParticipant");
	public static final ObjectProperty hasParticipantCatalyst = 
		schema.createObjectProperty(ns + "hasParticipantCatalyst");
	public static final ObjectProperty hasParticipantLeft = 
		schema.createObjectProperty(ns + "hasParticipantLeft");
	public static final ObjectProperty hasParticipantRight = 
		schema.createObjectProperty(ns + "hasParticipantRight");
	public static final ObjectProperty hasProcessModel = 
		schema.createObjectProperty(ns + "hasProcessModel");
	public static final ObjectProperty hasSequenceFeature = 
		schema.createObjectProperty(ns + "hasSequenceFeature");
	public static final ObjectProperty hasStoichiometry = 
		schema.createObjectProperty(ns + "hasStoichiometry");
	public static final ObjectProperty hasSystemModel = 
		schema.createObjectProperty(ns + "hasSystemModel");
	public static final ObjectProperty hasUnit = schema.createObjectProperty(ns + "hasUnit");
	public static final ObjectProperty involves = schema.createObjectProperty(ns + "involves");
	public static final ObjectProperty isOpOnSet = schema.createObjectProperty(ns + "isOpOnSet");
	public static final ObjectProperty isOpOnSetLeft = 
		schema.createObjectProperty(ns + "isOpOnSetLeft");
	public static final ObjectProperty isOpOnSetRight = 
		schema.createObjectProperty(ns + "isOpOnSetRight");
	public static final ObjectProperty lacksSequenceFeature = 
		schema.createObjectProperty(ns + "lacksSequenceFeature");
	public static final ObjectProperty locatedAt = schema.createObjectProperty(ns + "locatedAt");
	public static final ObjectProperty modelsProcess = schema.createObjectProperty(ns + "modelsProcess");
	public static final ObjectProperty modelsSpecies = schema.createObjectProperty(ns + "modelsSpecies");
	public static final ObjectProperty modelsSubstance = 
		schema.createObjectProperty(ns + "modelsSubstance");	
	public static final ObjectProperty quantifies = schema.createObjectProperty(ns + "quantifies");
	public static final ObjectProperty isSetFromOp = 
		schema.createObjectProperty(ns + "isSetFromOp");
	public static final ObjectProperty surroundedBy = schema.createObjectProperty(ns + "surroundedBy");

	public static final TransitiveProperty subSetOf = schema.createTransitiveProperty(ns + "subSetOf");
	
	public static final DatatypeProperty hasDimensions = 
		schema.createDatatypeProperty(ns + "hasDimensions");
	public static final DatatypeProperty hasNumber = schema.createDatatypeProperty(ns + "hasNumber");
	
	public static final Individual particleNumber = 
		schema.createIndividual(ns + "particleNumber", Unit);
	
	static {
		Amount.addSubClass(SpeciesAmount);
		Amount.addSubClass(SubstanceAmount);
		Amount.addSubClass(Stoichiometry);
		BinarySetOp.addSubClass(BinarySetIntersection);
		BinarySetOp.addSubClass(BinarySetUnion);
		Interaction.addSubClass(Process);
		Model.addSubClass(SystemModel);
		Model.addSubClass(ProcessModel);
		ProcessParticipant.addSubClass(ProcessParticipantLeft);
		ProcessParticipant.addSubClass(ProcessParticipantRight);
		ProcessParticipant.addSubClass(ProcessParticipantCatalyst);
		Quantity.addSubClass(Amount);
		SetOp.addSubClass(BinarySetOp);

		Substance.addSubClass(Protein);
		Substance.addSubClass(DNA);
		Substance.addSubClass(RNA);
		Substance.addSubClass(Complex);
		Substance.addSubClass(Metabolite);
		
		OntUtil.makeAllDisjoint(new SetOfThree<OntClass>(ProcessParticipantLeft, ProcessParticipantRight,
				ProcessParticipantCatalyst));
		
		isOpOnSet.addSubProperty(isOpOnSetLeft);
		isOpOnSet.addSubProperty(isOpOnSetRight);
		hasModel.addSubProperty(hasSystemModel);
		hasModel.addSubProperty(hasProcessModel);
		hasParticipant.addSubProperty(hasParticipantLeft);
		hasParticipant.addSubProperty(hasParticipantRight);
		hasParticipant.addSubProperty(hasParticipantCatalyst);

		// schema.add(subSetOf, RDFS.subPropertyOf, RDFS.subClassOf);
		
		consistsOf.setDomain(Species);
		consistsOf.setRange(Substance);
		hasDimensions.setDomain(Location);
		hasDimensions.setRange(XSD.integer);
		hasParticipant.setDomain(Process);
		hasParticipant.setRange(ProcessParticipant);
		hasParticipantCatalyst.setDomain(Process);
		hasParticipantCatalyst.setRange(ProcessParticipantCatalyst);
		hasParticipantLeft.setDomain(Process);
		hasParticipantLeft.setRange(ProcessParticipantLeft);
		hasParticipantRight.setDomain(Process);
		hasParticipantRight.setRange(ProcessParticipantRight);
		hasModel.setDomain(Model);
		hasModel.setRange(Model);
		hasSystemModel.setDomain(SystemModel);
		hasSystemModel.setRange(SystemModel);
		hasProcessModel.setDomain(SystemModel);
		hasProcessModel.setRange(ProcessModel);
		hasStoichiometry.setDomain(ProcessParticipant);
		hasStoichiometry.setRange(Stoichiometry);
		hasSequenceFeature.setDomain(Substance);
		hasSequenceFeature.setRange(SequenceFeature);
		involves.setDomain(ProcessParticipant);
		involves.setRange(Species);
		isOpOnSet.setDomain(SetOp);
		isOpOnSet.setRange(Substance);
		isOpOnSetLeft.setDomain(BinarySetOp);
		isOpOnSetRight.setDomain(BinarySetOp);
		isSetFromOp.setDomain(Substance);
		isSetFromOp.setRange(SetOp);
		lacksSequenceFeature.setDomain(Substance);
		lacksSequenceFeature.setRange(SequenceFeature);
		locatedAt.setDomain(Species);
		locatedAt.setRange(Location);
		modelsSpecies.setDomain(SystemModel);
		modelsSpecies.setRange(Species);
		modelsProcess.setDomain(ProcessModel);
		modelsProcess.setRange(Process);
		modelsSubstance.setDomain(SystemModel);
		modelsSubstance.setRange(Substance);
		quantifies.setDomain(Amount);
		quantifies.setRange(Substance);
		subSetOf.setDomain(Substance);
		subSetOf.setRange(Substance);
		surroundedBy.setDomain(Location);
		surroundedBy.setRange(Location);

		hasNumber.setDomain(Quantity);
		hasNumber.setRange(schema.createUnionClass(null, 
				schema.createList().with(XSD.xdouble).with(XSD.xfloat)));
		hasUnit.setDomain(Quantity);
		hasUnit.setRange(Unit);

		ProcessParticipant.addEquivalentClass(schema.createUnionClass(null, 
				schema.createList().with(ProcessParticipantLeft).with(ProcessParticipantRight)));
//		ProcessParticipantCatalyst.addEquivalentClass(schema.createIntersectionClass(null, 
//				schema.createList().with(ProcessParticipantLeft).with(ProcessParticipantRight)));
		
		schema.createCardinalityRestriction(null, hasStoichiometry, 1).addSubClass(ProcessParticipant);
		schema.createCardinalityRestriction(null, involves, 1).addSubClass(ProcessParticipant);
		schema.createCardinalityRestriction(null, consistsOf, 1).addSubClass(Species);
		schema.createCardinalityRestriction(null, locatedAt, 1).addSubClass(Species);
		schema.createCardinalityRestriction(null, modelsProcess, 1).addSubClass(ProcessModel);
		schema.createCardinalityRestriction(null, quantifies, 1).addSubClass(Amount);
		schema.createCardinalityRestriction(null, hasNumber, 1).addSubClass(Quantity);
		schema.createCardinalityRestriction(null, hasUnit, 1).addSubClass(Quantity);
	}
	
	static {
		schema.add(Model, RDFS.subClassOf, BFO.GenericallyDependentContinuant);
		schema.add(Quantity, RDFS.subClassOf, BFO.GenericallyDependentContinuant);
		schema.add(Process, RDFS.subClassOf, BFO.ProcessAggregate);
		schema.add(Species, RDFS.subClassOf, BFO.ObjectAggregate);
		schema.add(Substance, RDFS.subClassOf, BFO.ObjectAggregate);
		schema.add(ProcessParticipant, RDFS.subClassOf, BFO.ProcessualContext);		
		schema.add(Location, RDFS.subClassOf, BFO.Site);		
		schema.add(Stoichiometry, RDFS.subClassOf, BFO.GenericallyDependentContinuant);		
		schema.add(Unit, RDFS.subClassOf, BFO.GenericallyDependentContinuant);
	}
	
	static {
		schema.add(BioPAX2.physicalEntity, RDFS.subClassOf, Substance);
		schema.add(BioPAX2.interaction, RDFS.subClassOf, Interaction);		
		schema.add(BioPAX2.conversion, RDFS.subClassOf, Process);		
		schema.add(BioPAX2.sequenceFeature, RDFS.subClassOf, SequenceFeature);		
		schema.add(BioPAX2.physicalEntityParticipant, RDFS.subClassOf, ProcessParticipant);		
		// schema.add(Location, RDFS.subClassOf, BioPAX2.openControlledVocabulary);
		
		schema.add(BioPAX2.LEFT, RDFS.subPropertyOf, hasParticipantLeft);		
		schema.add(BioPAX2.RIGHT, RDFS.subPropertyOf, hasParticipantRight);	
		
		schema.add(BioPAX2.protein, RDFS.subClassOf, Protein);		
		schema.add(BioPAX2.dna, RDFS.subClassOf, DNA);		
		schema.add(BioPAX2.rna, RDFS.subClassOf, RNA);		
		schema.add(BioPAX2.complex, RDFS.subClassOf, Complex);		
		schema.add(BioPAX2.smallMolecule, RDFS.subClassOf, Metabolite);		
	}
	
	static {

		// OntUtil.addComment(schema, , "");

		OntUtil.addComment(schema, Amount, 
				"An amount of something, described by a number and a unit.");

		OntUtil.addComment(schema, BFO.ontology, "The BFO ontology.");

		OntUtil.addComment(schema, BioPAX2.ontology, "The BioPAX Level 2 ontology.");

		OntUtil.addComment(schema, OWL.Class, "Class from OWL.");

		OntUtil.addComment(schema, OWL.cardinality, "cardinality from OWL.");

		OntUtil.addComment(schema, RDFS.comment, "comment from RDF Schema.");

		OntUtil.addComment(schema, consistsOf, 
				"Each species consists of exactly one substance.");

		OntUtil.addComment(schema, BioPAX2.conversion, "conversion from BioPAX Level 2.");

		OntUtil.addComment(schema, OWL.DatatypeProperty, "DatatypeProperty from OWL.");

		OntUtil.addComment(schema, RDFS.domain, "domain from RDF.");

		OntUtil.addComment(schema, XSD.xdouble, "double from XML Schema.");

		OntUtil.addComment(schema, OWL.equivalentClass, "equivalentClass from OWL.");

		OntUtil.addComment(schema, RDF.first, "first from RDF");

		OntUtil.addComment(schema, XSD.xfloat, "float from XML Schema.");

		OntUtil.addComment(schema, BFO.GenericallyDependentContinuant, 
				"GenericallyDependentContinuant from BFO.");

		OntUtil.addComment(schema, hasDimensions, 
				"The number of spatial dimensions of a location within a model. For example, " +
				"volumes have usually three, membranes two. There really should be a " +
				"LocationModel for this property, but that is not implemented yet.");

		OntUtil.addComment(schema, hasNumber, 
				"An amount has exactly one number. For example, an amount may be one liter, " +
				"where one is the number and liter is the unit.");

		OntUtil.addComment(schema, hasStoichiometry, 
				"The stoichiometry of a participant. Each participant has exactly one. " +
				"A different stoichiometry implies a different participant, and a different " +
				"set of participants implies a different process.");

		OntUtil.addComment(schema, hasParticipantCatalyst, 
				"A process part which involves a species which is a catalyst for that process. " +
				"A catalyst is a substance without which " +
				"the process does not happen. If the process can happen with and without catalyst, " +
				"then with and without are considered two different processes.");

		OntUtil.addComment(schema, hasParticipantLeft, 
				"A process part which involves a species on the left side. " + 
				"Whether the species is a reactant or product " +
				"depends on the direction in which the proecess proceeds.");

		OntUtil.addComment(schema, hasParticipantRight, 
				"A process part which involves a species on the right side. " + 
				"Whether the species is a reactant or product " +
				"depends on the direction in which the proecess proceeds.");

		OntUtil.addComment(schema, hasParticipant, 
				"A process has a process part for each participating species. ");

		OntUtil.addComment(schema, hasProcessModel, 
				"A system model has a process model, if that process model, and by extension that " +
				"process, is included in the system model. In SBML, the process model, with " +
				"its process, becomes a reaction.");

		OntUtil.addComment(schema, OWL.imports, "imports from OWL.");

		OntUtil.addComment(schema, XSD.integer, "Integer from XMLSchema.");

		OntUtil.addComment(schema, OWL.intersectionOf, "intersectionOf from OWL.");

		OntUtil.addComment(schema, BioPAX2.LEFT, "LEFT from BioPAX Level 2.");

		OntUtil.addComment(schema, involves, 
				"Each process part involves exactly one species. While a process part represents " +
				"a substance and the amount of that substance that is turned over or required " + 
				"by a process, " +
				"a species represents the same substance and the amount " + 
				"that is located at some location.");

		OntUtil.addComment(schema, locatedAt, 
				"The location of a species.");

		OntUtil.addComment(schema, Location, 
				"Anywhere where something can be located. Could be a cell, an organelle, an organism, " +
				"a petri dish, a membrane, or any part of any of the above.");

		OntUtil.addComment(schema, modelsSpecies, 
				"A species model models a species if the species is included in it. " +
				"In SBML, the species model becomes a species of the model.");

		OntUtil.addComment(schema, modelsProcess, 
				"The process a process model is modeling.");

		OntUtil.addComment(schema, modelsSubstance, 
				"A system model models a substance if the substance is included in it. " +
				"In SBML, the substance becomes a speciesType of the model.");

		OntUtil.addComment(schema, RDF.nil, "nil from RDF.");

		OntUtil.addComment(schema, BFO.ObjectAggregate, "ObjectAggregate from BFO.");

		OntUtil.addComment(schema, OWL.ObjectProperty, "ObjectProperty from OWL.");

		OntUtil.addComment(schema, OWL.onProperty, "onProperty from OWL.");

		OntUtil.addComment(schema, OWL.Ontology, "Ontology from OWL.");

		OntUtil.addComment(schema, BioPAX2.openControlledVocabulary, 
				"openControlledVocabulary from BioPAX Level 2.");

		OntUtil.addComment(schema, ProcessParticipantCatalyst, 
				"A process part which involves a catalyst. ");
		
		OntUtil.addComment(schema, ProcessParticipant, 
				"A process is divided into process parts, one for each reactant, product or catalyst." +
				"Whether the left or right " +
				"are reactants or products respectively depends on the direction, for example, if the " +
				"direction is left to right, then the left participants are reactants and the " +
				"right participants are products.");

		OntUtil.addComment(schema, ProcessParticipantLeft, 
				"A participant on the left side.");

		OntUtil.addComment(schema, ProcessParticipantRight, 
				"A participant on the right side.");

		OntUtil.addComment(schema, BioPAX2.physicalEntity, "physicalEntity from BioPAX Level 2.");

		OntUtil.addComment(schema, BioPAX2.physicalEntityParticipant, 
				"physicalEntityParticipant from BioPAX Level 2.");
		
		OntUtil.addComment(schema, Species, 
				"A substance at some location.");

		OntUtil.addComment(schema, SpeciesAmount, 
				"The amount of substance constituting a species.");

		OntUtil.addComment(schema, Process, 
				"A process that changes the amounts of substances at certain locations, for " +
				"example a transport or a chemical (or even nuclear) reaction. A process " +
				"may be considered an ensemble of individual processes such as " +
				"the individual reactions among molecules. A process is characterized by " +
				"how much of a substance is consumed or produced at a certain location or various " +
				"certain locations, as well as a substance that is required at certain locations " +
				"for the process to happen. In the case of a process being an ensemble of individual " +
				"processes, the number of individuals consumed or produced can be given.");

		OntUtil.addComment(schema, BFO.ProcessAggregate, "ProcessAggregate from BFO.");

		OntUtil.addComment(schema, ProcessModel, 
				"A model of a process. While a process represents objective reality, " +
				"a process model includes modelers choices, meaning information depending on " +
				"preferences. For example, participant, locations and stoichiometric coefficients " +
				"are considered objective reality, but rate laws are considered subjective, " +
				"because the choice between different rate laws may depend on modeler's choices.");

		OntUtil.addComment(schema, BFO.ProcessualContext, "ProcessualContext from BFO.");

		OntUtil.addComment(schema, quantifies, 
				"The substance quantified by an amount. " + 
				"In other words, the substance the amount is amount of.");

		OntUtil.addComment(schema, Quantity, 
				"Any physical quantity. For example, a temperature, a voltage, or an amount.");

		OntUtil.addComment(schema, RDFS.range, "range from RDF Schema.");

		OntUtil.addComment(schema, RDF.rest, "rest from RDF.");

		OntUtil.addComment(schema, OWL.Restriction, "Restriction from OWL.");

		OntUtil.addComment(schema, BioPAX2.RIGHT, "Right from BioPAX Level 2.");

		OntUtil.addComment(schema, ontology, "The SBPAX ontology");

		OntUtil.addComment(schema, BFO.Site, "Site from BFO.");

		OntUtil.addComment(schema, Stoichiometry, 
				"The amount a process adds to or removes from a species.");

		OntUtil.addComment(schema, Substance, 
				"Anything that can have an amount at a location. Typically something that " +
				"has an amount at each of multiple locations, which changes by certain " +
				"amounts by one or more processes. In the context of molecular reaction networks " +
				"or pathways, " +
				"substance is used to describe collections of molecules and other compounds, but it " +
				"could also describe a variety of other things, such as cells, heat, light, " +
				"charge, organisms, stress, strain, pressure, energy. " +
				"Naturally, it can also be a group of kinds of proteins, or a certain kind of protein, " +
				"or a certain kind of protein in a certain phosphorylation state, which would " +
				"then relate to each other through sub set relationships.");

		OntUtil.addComment(schema, SubstanceAmount, 
				"Amount of substance, for example, a liter of water, or three molecules of EGFR");

		OntUtil.addComment(schema, RDFS.subClassOf, "subClassOf from RDF Schema.");

		OntUtil.addComment(schema, RDFS.subPropertyOf, "subPropertyOf from RDF Schema.");

		OntUtil.addComment(schema, subSetOf, 
				"Some substance is sub set of another substance, if anything included in the first is " +
				"included in the second "+
				"For example, a certain protein is sub set of a set of proteins including it, " +
				" and a certain phosphorylation state of a protein is sub set the protein.");

		OntUtil.addComment(schema, surroundedBy, 
				"The location surrounding this location. Depending on model, the nucleoplasm is " +
				"surrounded by the nuclear membrane, the nuclear membrane by the cytoplasm," +
				"the cytoplasm by the cell membrane and the cell membrane by the " +
				"extracellular matrix. This should really be a property between " +
				"location models rather than locations, but that has not been implemented yet.");

		OntUtil.addComment(schema, SystemModel, 
				"A model of a process network, such as a molecular pathway or reaction network. " +
				"It can be any collection of ProcessModels, but typically " +
				"describes a connected network.");

		OntUtil.addComment(schema, OWL.TransitiveProperty, "TransitiveProperty from OWL.");

		OntUtil.addComment(schema, RDF.type, "type from RDF.");

		OntUtil.addComment(schema, OWL.unionOf, "unionOf from OWL.");

		// OntUtil.addComment(schema, , "");

	}
	
	public static void main(String[] args) {
		OntSpecUtil.writeToFiles(schema);
		//OntSpecUtil.listMissingComments(schema);
		OntologyInfo info = new OntologyInfo(schema);
		info.write();
	}

}

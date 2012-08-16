/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.sbpax.schemas;

/*   BioPAX3  --- by Oliver Ruebenacker, UCHC --- April 2008 to November 2009
 *   The BioPAX Level 3 schema
 */

import org.openrdf.model.Graph;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.OWL;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;
import org.sbpax.impl.HashGraph;
import org.sbpax.impl.IndexedGraph;
import org.sbpax.schemas.util.NameSpace;
import org.sbpax.schemas.util.OntUtil;

public class BioPAX3 {

	public static final String uri = "http://www.biopax.org/release/biopax-level3.owl";
	
	public static final NameSpace ns = new NameSpace("bp3", uri + "#");
	
	public static final Graph schema = new HashGraph();

	public static final URI ontology = OntUtil.createOntologyNode(schema, uri);
	
	public static final URI BindingFeature = OntUtil.createClass(schema, ns + "BindingFeature");
	public static final URI BiochemicalPathwayStep = 
		OntUtil.createClass(schema, ns + "BiochemicalPathwayStep");
	public static final URI BiochemicalReaction = OntUtil.createClass(schema, ns + "BiochemicalReaction");
	public static final URI BioSource = OntUtil.createClass(schema, ns + "BioSource");
	public static final URI Catalysis = OntUtil.createClass(schema, ns + "Catalysis");
	public static final URI CellularLocationVocabulary = 
		OntUtil.createClass(schema, ns + "CellularLocationVocabulary");
	public static final URI CellVocabulary = OntUtil.createClass(schema, ns + "CellVocabulary");
	public static final URI ChemicalStructure = OntUtil.createClass(schema, ns + "ChemicalStructure");
	public static final URI Complex = OntUtil.createClass(schema, ns + "Complex");
	public static final URI ComplexAssembly = OntUtil.createClass(schema, ns + "ComplexAssembly");
	public static final URI Control = OntUtil.createClass(schema, ns + "Control");
	public static final URI ControlledVocabulary = 
		OntUtil.createClass(schema, ns + "ControlledVocabulary");
	public static final URI Conversion = OntUtil.createClass(schema, ns + "Conversion");
	public static final URI CovalentBindingFeature = 
		OntUtil.createClass(schema, ns + "CovalentBindingFeature");
	public static final URI Degradation = OntUtil.createClass(schema, ns + "Degradation");
	public static final URI Dna = OntUtil.createClass(schema, ns + "Dna");
	public static final URI DnaReference = OntUtil.createClass(schema, ns + "DnaReference");
	public static final URI DnaRegion = OntUtil.createClass(schema, ns + "DnaRegion");
	public static final URI DnaRegionReference = OntUtil.createClass(schema, ns + "DnaRegionReference");
	public static final URI DeltaG = OntUtil.createClass(schema, ns + "DeltaG");
	public static final URI Gene = OntUtil.createClass(schema, ns + "Gene");
	public static final URI Entity = OntUtil.createClass(schema, ns + "Entity");
	public static final URI EntityFeature = OntUtil.createClass(schema, ns + "EntityFeature");
	public static final URI EntityReference = OntUtil.createClass(schema, ns + "EntityReference");
	public static final URI EntityReferenceTypeVocabulary = 
		OntUtil.createClass(schema, ns + "EntityReferenceTypeVocabulary");
	public static final URI Evidence = OntUtil.createClass(schema, ns + "Evidence");
	public static final URI EvidenceCodeVocabulary = 
		OntUtil.createClass(schema, ns + "EvidenceCodeVocabulary");
	public static final URI ExperimentalForm = OntUtil.createClass(schema, ns + "ExperimentalForm");
	public static final URI ExperimentalFormVocabulary = 
		OntUtil.createClass(schema, ns + "ExperimentalFormVocabulary");
	public static final URI FragmentFeature = OntUtil.createClass(schema, ns + "FragmentFeature");
	public static final URI GeneticInteraction = OntUtil.createClass(schema, ns + "GeneticInteraction");
	public static final URI Interaction = OntUtil.createClass(schema, ns + "Interaction");
	public static final URI InteractionVocabulary = 
		OntUtil.createClass(schema, ns + "InteractionVocabulary");
	public static final URI KPrime = OntUtil.createClass(schema, ns + "KPrime");
	public static final URI ModificationFeature = OntUtil.createClass(schema, ns + "ModificationFeature");
	public static final URI Modulation = OntUtil.createClass(schema, ns + "Modulation");
	public static final URI MolecularInteraction = 
		OntUtil.createClass(schema, ns + "MolecularInteraction");
	public static final URI Pathway = OntUtil.createClass(schema, ns + "Pathway");
	public static final URI PathwayStep = OntUtil.createClass(schema, ns + "PathwayStep");
	public static final URI PhenotypeVocabulary = OntUtil.createClass(schema, ns + "PhenotypeVocabulary");
	public static final URI PhysicalEntity = OntUtil.createClass(schema, ns + "PhysicalEntity");
	public static final URI Protein = OntUtil.createClass(schema, ns + "Protein");
	public static final URI ProteinReference = OntUtil.createClass(schema, ns + "ProteinReference");
	public static final URI Provenance = OntUtil.createClass(schema, ns + "Provenance");
	public static final URI PublicationXref = OntUtil.createClass(schema, ns + "PublicationXref");
	public static final URI RelationshipXref = OntUtil.createClass(schema, ns + "RelationshipXref");
	public static final URI RelationshipTypeVocabulary = 
		OntUtil.createClass(schema, ns + "RelationshipTypeVocabulary");
	public static final URI Rna = OntUtil.createClass(schema, ns + "Rna");
	public static final URI RnaReference = OntUtil.createClass(schema, ns + "RnaReference");
	public static final URI RnaRegion = OntUtil.createClass(schema, ns + "RnaRegion");
	public static final URI RnaRegionReference = OntUtil.createClass(schema, ns + "RnaRegionReference");
	public static final URI Score = OntUtil.createClass(schema, ns + "Score");
	public static final URI SequenceInterval = OntUtil.createClass(schema, ns + "SequenceInterval");
	public static final URI SequenceLocation = OntUtil.createClass(schema, ns + "SequenceLocation");
	public static final URI SequenceModificationVocabulary = 
		OntUtil.createClass(schema, ns + "SequenceModificationVocabulary");
	public static final URI SequenceRegionVocabulary = 
		OntUtil.createClass(schema, ns + "SequenceRegionVocabulary");
	public static final URI SequenceSite = OntUtil.createClass(schema, ns + "SequenceSite");
	public static final URI SmallMolecule = OntUtil.createClass(schema, ns + "SmallMolecule");
	public static final URI SmallMoleculeReference = 
		OntUtil.createClass(schema, ns + "SmallMoleculeReference");
	public static final URI Stoichiometry = OntUtil.createClass(schema, ns + "Stoichiometry");
	public static final URI TemplateReaction = OntUtil.createClass(schema, ns + "TemplateReaction");
	public static final URI TemplateReactionRegulation = 
		OntUtil.createClass(schema, ns + "TemplateReactionRegulation");
	public static final URI TissueVocabulary = OntUtil.createClass(schema, ns + "TissueVocabulary");
	public static final URI Transport = OntUtil.createClass(schema, ns + "Transport");
	public static final URI TransportWithBiochemicalReaction = 
		OntUtil.createClass(schema, ns + "TransportWithBiochemicalReaction");
	public static final URI UnificationXref = OntUtil.createClass(schema, ns + "UnificationXref");
	public static final URI UtilityClass = OntUtil.createClass(schema, ns + "UtilityClass");
	public static final URI Xref = OntUtil.createClass(schema, ns + "Xref");
	
	public static final URI absoluteRegion = OntUtil.createObjectProperty(schema, ns + "absoluteRegion");
	public static final URI author = OntUtil.createDatatypeProperty(schema, ns + "author");
	public static final URI availability = OntUtil.createDatatypeProperty(schema, ns + "availability");
	public static final URI bindsTo = OntUtil.createObjectProperty(schema, ns + "bindsTo");
	public static final URI catalysisDirection = 
		OntUtil.createDatatypeProperty(schema, ns + "catalysisDirection");
	public static final URI cellType = OntUtil.createObjectProperty(schema, ns + "cellType");
	public static final URI cellularLocation = 
		OntUtil.createObjectProperty(schema, ns + "cellularLocation");
	public static final URI chemicalFormula = 
		OntUtil.createDatatypeProperty(schema, ns + "chemicalFormula");
	public static final URI cofactor = OntUtil.createObjectProperty(schema, ns + "cofactor");
	public static final URI comment = OntUtil.createDatatypeProperty(schema, ns + "comment");
	public static final URI component = OntUtil.createObjectProperty(schema, ns + "component");
	public static final URI componentStoichiometry = 
		OntUtil.createObjectProperty(schema, ns + "componentStoichiometry");
	public static final URI confidence = OntUtil.createObjectProperty(schema, ns + "confidence");
	public static final URI controlled = OntUtil.createObjectProperty(schema, ns + "controlled");
	public static final URI controller = OntUtil.createObjectProperty(schema, ns + "controller");
	public static final URI controlType = OntUtil.createDatatypeProperty(schema, ns + "controlType");
	public static final URI conversionDirection = 
		OntUtil.createDatatypeProperty(schema, ns + "conversionDirection");
	public static final URI dataSource = OntUtil.createObjectProperty(schema, ns + "dataSource");
	public static final URI db = OntUtil.createDatatypeProperty(schema, ns + "db");
	public static final URI dbVersion = OntUtil.createDatatypeProperty(schema, ns + "dbVersion");
	public static final URI deltaG = OntUtil.createObjectProperty(schema, ns + "deltaG");
	public static final URI deltaGPrime0 = OntUtil.createDatatypeProperty(schema, ns + "deltaGPrime0");
	public static final URI deltaH = OntUtil.createDatatypeProperty(schema, ns + "deltaH");
	public static final URI deltaS = OntUtil.createDatatypeProperty(schema, ns + "deltaS");
	public static final URI displayName = OntUtil.createDatatypeProperty(schema, ns + "displayName");
	public static final URI eCNumber = OntUtil.createDatatypeProperty(schema, ns + "eCNumber");
	public static final URI entityFeature = OntUtil.createObjectProperty(schema, ns + "entityFeature");
	public static final URI entityReference = OntUtil.createObjectProperty(schema, ns + "entityReference");
	public static final URI entityReferenceType = 
		OntUtil.createObjectProperty(schema, ns + "entityReferenceType");
	public static final URI evidence = OntUtil.createObjectProperty(schema, ns + "evidence");
	public static final URI evidenceCode = OntUtil.createObjectProperty(schema, ns + "evidenceCode");
	public static final URI experimentalFeature = 
		OntUtil.createObjectProperty(schema, ns + "experimentalFeature");
	public static final URI experimentalForm = 
		OntUtil.createObjectProperty(schema, ns + "experimentalForm");
	public static final URI experimentalFormDescription = 
		OntUtil.createObjectProperty(schema, ns + "experimentalFormDescription");
	public static final URI experimentalFormEntity = 
		OntUtil.createObjectProperty(schema, ns + "experimentalFormEntity");
	public static final URI feature = OntUtil.createObjectProperty(schema, ns + "feature");
	public static final URI featureLocation = OntUtil.createObjectProperty(schema, ns + "featureLocation");
	public static final URI featureLocationType = 
		OntUtil.createObjectProperty(schema, ns + "featureLocationType");
	public static final URI id = OntUtil.createDatatypeProperty(schema, ns + "id");
	public static final URI idVersion = OntUtil.createDatatypeProperty(schema, ns + "idVersion");
	public static final URI interactionScore = 
		OntUtil.createObjectProperty(schema, ns + "interactionScore");
	public static final URI interactionType = 
		OntUtil.createObjectProperty(schema, ns + "interactionType");
	public static final URI intraMolecular = 
		OntUtil.createDatatypeProperty(schema, ns + "intraMolecular");
	public static final URI ionicStrength = OntUtil.createDatatypeProperty(schema, ns + "ionicStrength");
	public static final URI kEQ = OntUtil.createObjectProperty(schema, ns + "kEQ");
	public static final URI kPrime = OntUtil.createDatatypeProperty(schema, ns + "kPrime");
	public static final URI left = OntUtil.createObjectProperty(schema, ns + "left");
	public static final URI memberEntityReference = 
		OntUtil.createObjectProperty(schema, ns + "memberEntityReference");
	public static final URI memberFeature = OntUtil.createObjectProperty(schema, ns + "memberFeature");
	public static final URI memberPhysicalEntity = 
		OntUtil.createObjectProperty(schema, ns + "memberPhysicalEntity");
	public static final URI modificationType = 
		OntUtil.createObjectProperty(schema, ns + "modificationType");
	public static final URI molecularWeight = 
		OntUtil.createDatatypeProperty(schema, ns + "molecularWeight");
	public static final URI name = OntUtil.createDatatypeProperty(schema, ns + "name");
	public static final URI nextStep = OntUtil.createObjectProperty(schema, ns + "nextStep");
	public static final URI notFeature = OntUtil.createObjectProperty(schema, ns + "notFeature");
	public static final URI organism = OntUtil.createObjectProperty(schema, ns + "organism");
	public static final URI participant = OntUtil.createObjectProperty(schema, ns + "participant");
	public static final URI participantStoichiometry = 
		OntUtil.createObjectProperty(schema, ns + "participantStoichiometry");
	public static final URI pathwayComponent = 
		OntUtil.createObjectProperty(schema, ns + "pathwayComponent");
	public static final URI pathwayOrder = OntUtil.createObjectProperty(schema, ns + "pathwayOrder");
	public static final URI patoData = OntUtil.createDatatypeProperty(schema, ns + "patoData");
	public static final URI ph = OntUtil.createDatatypeProperty(schema, ns + "ph");
	public static final URI phenotype = OntUtil.createObjectProperty(schema, ns + "phenotype");
	public static final URI physicalEntity = 
		OntUtil.createObjectProperty(schema, ns + "physicalEntity");
	public static final URI pMg = OntUtil.createDatatypeProperty(schema, ns + "pMg");
	public static final URI positionStatus = 
		OntUtil.createDatatypeProperty(schema, ns + "positionStatus");
	public static final URI product = OntUtil.createObjectProperty(schema, ns + "product");
	public static final URI regionOf = OntUtil.createObjectProperty(schema, ns + "regionOf");
	public static final URI regionType = OntUtil.createObjectProperty(schema, ns + "regionType");
	public static final URI relationshipType = 
		OntUtil.createObjectProperty(schema, ns + "relationshipType");
	public static final URI right = OntUtil.createObjectProperty(schema, ns + "right");
	public static final URI scoreSource = OntUtil.createObjectProperty(schema, ns + "scoreSource");
	public static final URI sequence = OntUtil.createDatatypeProperty(schema, ns + "sequence");
	public static final URI sequenceIntervalBegin = 
		OntUtil.createObjectProperty(schema, ns + "sequenceIntervalBegin");
	public static final URI sequenceIntervalEnd = 
		OntUtil.createObjectProperty(schema, ns + "sequenceIntervalEnd");
	public static final URI sequencePosition = 
		OntUtil.createDatatypeProperty(schema, ns + "sequencePosition");
	public static final URI source = OntUtil.createDatatypeProperty(schema, ns + "source");
	public static final URI spontaneous = OntUtil.createDatatypeProperty(schema, ns + "spontaneous");
	public static final URI standardName = OntUtil.createDatatypeProperty(schema, ns + "standardName");
	public static final URI stepDirection = OntUtil.createDatatypeProperty(schema, ns + "stepDirection");
	public static final URI stepConversion = OntUtil.createObjectProperty(schema, ns + "stepConversion");
	public static final URI stepProcess = OntUtil.createObjectProperty(schema, ns + "stepProcess");
	public static final URI stoichiometricCoefficient = 
		OntUtil.createDatatypeProperty(schema, ns + "stoichiometricCoefficient");
	public static final URI structure = OntUtil.createObjectProperty(schema, ns + "structure");
	public static final URI structureData = 
		OntUtil.createDatatypeProperty(schema, ns + "structureData");
	public static final URI structureFormat = 
		OntUtil.createDatatypeProperty(schema, ns + "structureFormat");
	public static final URI subRegion = OntUtil.createObjectProperty(schema, ns + "subRegion");
	public static final URI taxonXref = OntUtil.createObjectProperty(schema, ns + "taxonXref");
	public static final URI temperature = OntUtil.createDatatypeProperty(schema, ns + "temperature");
	public static final URI template = OntUtil.createObjectProperty(schema, ns + "template");
	public static final URI templateDirection = 
		OntUtil.createDatatypeProperty(schema, ns + "templateDirection");
	public static final URI term = OntUtil.createDatatypeProperty(schema, ns + "term");
	public static final URI tissue = OntUtil.createObjectProperty(schema, ns + "tissue");
	public static final URI title = OntUtil.createDatatypeProperty(schema, ns + "title");
	public static final URI url = OntUtil.createDatatypeProperty(schema, ns + "url");
	public static final URI value = OntUtil.createDatatypeProperty(schema, ns + "value");
	public static final URI xref = OntUtil.createObjectProperty(schema, ns + "xref");
	public static final URI year = OntUtil.createDatatypeProperty(schema, ns + "year");
	
	static {
		schema.add(absoluteRegion, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(bindsTo, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(catalysisDirection, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(cellType, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(cellularLocation, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(chemicalFormula, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(controlled, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(controlType, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(conversionDirection, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(dbVersion, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(deltaGPrime0, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(displayName, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(entityReference, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(entityReferenceType, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(id, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(idVersion, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(intraMolecular, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(ionicStrength, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(db, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(kPrime, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(modificationType, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(molecularWeight, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(organism, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(patoData, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(ph, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(phenotype, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(physicalEntity, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(pMg, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(positionStatus, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(scoreSource, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(sequence, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(sequenceIntervalBegin, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(sequenceIntervalEnd, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(sequencePosition, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(spontaneous, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(standardName, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(stepConversion, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(stepDirection, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(stoichiometricCoefficient, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(structure, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(structureData, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(structureFormat, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(taxonXref, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(temperature, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(template, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(templateDirection, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(tissue, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(title, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(value, RDF.TYPE, OWL.FUNCTIONALPROPERTY);
		schema.add(year, RDF.TYPE, OWL.FUNCTIONALPROPERTY);

		schema.add(bindsTo, RDF.TYPE, OWL.INVERSEFUNCTIONALPROPERTY);
		schema.add(component, RDF.TYPE, OWL.INVERSEFUNCTIONALPROPERTY);
		schema.add(entityFeature, RDF.TYPE, OWL.INVERSEFUNCTIONALPROPERTY);
		
		schema.add(bindsTo, RDF.TYPE, OWL.SYMMETRICPROPERTY);
		schema.add(bindsTo, OWL.INVERSEOF, bindsTo);

		schema.add(memberPhysicalEntity, RDF.TYPE, OWL.TRANSITIVEPROPERTY);
		schema.add(memberEntityReference, RDF.TYPE, OWL.TRANSITIVEPROPERTY);
		schema.add(memberFeature, RDF.TYPE, OWL.TRANSITIVEPROPERTY);
		schema.add(subRegion, RDF.TYPE, OWL.TRANSITIVEPROPERTY);
		
		schema.add(CovalentBindingFeature, RDFS.SUBCLASSOF, BindingFeature);
		schema.add(TransportWithBiochemicalReaction, RDFS.SUBCLASSOF, BiochemicalReaction);
		schema.add(Modulation, RDFS.SUBCLASSOF, Control);
		schema.add(TemplateReactionRegulation, RDFS.SUBCLASSOF, Control);
		schema.add(CellVocabulary, RDFS.SUBCLASSOF, ControlledVocabulary);
		schema.add(CellularLocationVocabulary, RDFS.SUBCLASSOF, ControlledVocabulary);
		schema.add(EntityReferenceTypeVocabulary, RDFS.SUBCLASSOF, ControlledVocabulary);
		schema.add(EvidenceCodeVocabulary, RDFS.SUBCLASSOF, ControlledVocabulary);
		schema.add(ExperimentalFormVocabulary, RDFS.SUBCLASSOF, ControlledVocabulary);
		schema.add(InteractionVocabulary, RDFS.SUBCLASSOF, ControlledVocabulary);
		schema.add(PhenotypeVocabulary, RDFS.SUBCLASSOF, ControlledVocabulary);
		schema.add(RelationshipTypeVocabulary, RDFS.SUBCLASSOF, ControlledVocabulary);
		schema.add(SequenceModificationVocabulary, RDFS.SUBCLASSOF, ControlledVocabulary);
		schema.add(SequenceRegionVocabulary, RDFS.SUBCLASSOF, ControlledVocabulary);
		schema.add(TissueVocabulary, RDFS.SUBCLASSOF, ControlledVocabulary);
		schema.add(Catalysis, RDFS.SUBCLASSOF, Control);
		schema.add(BiochemicalReaction, RDFS.SUBCLASSOF, Conversion);
		schema.add(ComplexAssembly, RDFS.SUBCLASSOF, Conversion);
		schema.add(Degradation, RDFS.SUBCLASSOF, Conversion);
		schema.add(Transport, RDFS.SUBCLASSOF, Conversion);
		schema.add(Interaction, RDFS.SUBCLASSOF, Entity);
		schema.add(Gene, RDFS.SUBCLASSOF, Entity);
		schema.add(Pathway, RDFS.SUBCLASSOF, Entity);
		schema.add(PhysicalEntity, RDFS.SUBCLASSOF, Entity);
		schema.add(BindingFeature, RDFS.SUBCLASSOF, EntityFeature);
		schema.add(FragmentFeature, RDFS.SUBCLASSOF, EntityFeature);
		schema.add(ModificationFeature, RDFS.SUBCLASSOF, EntityFeature);
		schema.add(DnaReference, RDFS.SUBCLASSOF, EntityReference);
		schema.add(DnaRegionReference, RDFS.SUBCLASSOF, EntityReference);
		schema.add(ProteinReference, RDFS.SUBCLASSOF, EntityReference);
		schema.add(RnaReference, RDFS.SUBCLASSOF, EntityReference);
		schema.add(RnaRegionReference, RDFS.SUBCLASSOF, EntityReference);
		schema.add(SmallMoleculeReference, RDFS.SUBCLASSOF, EntityReference);
		schema.add(Control, RDFS.SUBCLASSOF, Interaction);
		schema.add(Conversion, RDFS.SUBCLASSOF, Interaction);
		schema.add(GeneticInteraction, RDFS.SUBCLASSOF, Interaction);
		schema.add(MolecularInteraction, RDFS.SUBCLASSOF, Interaction);
		schema.add(TemplateReaction, RDFS.SUBCLASSOF, Interaction);
		schema.add(CovalentBindingFeature, RDFS.SUBCLASSOF, ModificationFeature);
		schema.add(BiochemicalPathwayStep, RDFS.SUBCLASSOF, PathwayStep);
		schema.add(Complex, RDFS.SUBCLASSOF, PhysicalEntity);
		schema.add(Dna, RDFS.SUBCLASSOF, PhysicalEntity);
		schema.add(DnaRegion, RDFS.SUBCLASSOF, PhysicalEntity);
		schema.add(Protein, RDFS.SUBCLASSOF, PhysicalEntity);
		schema.add(Rna, RDFS.SUBCLASSOF, PhysicalEntity);
		schema.add(RnaRegion, RDFS.SUBCLASSOF, PhysicalEntity);
		schema.add(SmallMolecule, RDFS.SUBCLASSOF, PhysicalEntity);
		schema.add(SequenceInterval, RDFS.SUBCLASSOF, SequenceLocation);
		schema.add(SequenceSite, RDFS.SUBCLASSOF, SequenceLocation);
		schema.add(TransportWithBiochemicalReaction, RDFS.SUBCLASSOF, Transport);
		schema.add(BioSource, RDFS.SUBCLASSOF, UtilityClass);
		schema.add(ChemicalStructure, RDFS.SUBCLASSOF, UtilityClass);
		schema.add(ControlledVocabulary, RDFS.SUBCLASSOF, UtilityClass);
		schema.add(DeltaG, RDFS.SUBCLASSOF, UtilityClass);
		schema.add(EntityFeature, RDFS.SUBCLASSOF, UtilityClass);
		schema.add(EntityReference, RDFS.SUBCLASSOF, UtilityClass);
		schema.add(Evidence, RDFS.SUBCLASSOF, UtilityClass);
		schema.add(ExperimentalForm, RDFS.SUBCLASSOF, UtilityClass);
		schema.add(KPrime, RDFS.SUBCLASSOF, UtilityClass);
		schema.add(PathwayStep, RDFS.SUBCLASSOF, UtilityClass);
		schema.add(Provenance, RDFS.SUBCLASSOF, UtilityClass);
		schema.add(Score, RDFS.SUBCLASSOF, UtilityClass);
		schema.add(SequenceLocation, RDFS.SUBCLASSOF, UtilityClass);
		schema.add(Stoichiometry, RDFS.SUBCLASSOF, UtilityClass);
		schema.add(Xref, RDFS.SUBCLASSOF, UtilityClass);
		schema.add(PublicationXref, RDFS.SUBCLASSOF, Xref);
		schema.add(RelationshipXref, RDFS.SUBCLASSOF, Xref);
		schema.add(UnificationXref, RDFS.SUBCLASSOF, Xref);

		schema.add(displayName, RDFS.SUBPROPERTYOF, name);
		schema.add(standardName, RDFS.SUBPROPERTYOF, name);
		schema.add(cofactor, RDFS.SUBPROPERTYOF, participant);
		schema.add(controlled, RDFS.SUBPROPERTYOF, participant);
		schema.add(controller, RDFS.SUBPROPERTYOF, participant);
		schema.add(left, RDFS.SUBPROPERTYOF, participant);
		schema.add(product, RDFS.SUBPROPERTYOF, participant);
		schema.add(right, RDFS.SUBPROPERTYOF, participant);
		schema.add(template, RDFS.SUBPROPERTYOF, participant);
		schema.add(stepConversion, RDFS.SUBPROPERTYOF, stepProcess);
		
		schema.add(absoluteRegion, RDFS.DOMAIN, 
				OntUtil.createUnion(schema, DnaRegionReference, RnaRegionReference));
		schema.add(absoluteRegion, RDFS.RANGE, SequenceLocation);
		schema.add(author, RDFS.DOMAIN, PublicationXref);
		schema.add(author, RDFS.RANGE, XMLSchema.STRING);
		schema.add(availability, RDFS.DOMAIN, Entity);
		schema.add(availability, RDFS.RANGE, XMLSchema.STRING);
		schema.add(bindsTo, RDFS.DOMAIN, BindingFeature);
		schema.add(bindsTo, RDFS.RANGE, BindingFeature);
		schema.add(catalysisDirection, RDFS.DOMAIN, Catalysis);
		schema.add(catalysisDirection, RDFS.RANGE, OntUtil.createDataRange(schema, "LEFT-TO-RIGHT", "RIGHT-TO-LEFT"));
		schema.add(cellType, RDFS.DOMAIN, BioSource);
		schema.add(cellType, RDFS.RANGE, CellVocabulary);
		schema.add(cellularLocation, RDFS.DOMAIN, PhysicalEntity);
		schema.add(cellularLocation, RDFS.RANGE, CellularLocationVocabulary);
		schema.add(chemicalFormula, RDFS.DOMAIN, SmallMoleculeReference);
		schema.add(chemicalFormula, RDFS.RANGE, XMLSchema.STRING);
		schema.add(cofactor, RDFS.DOMAIN, Catalysis);
		schema.add(cofactor, RDFS.RANGE, Catalysis);
		schema.add(comment, RDFS.DOMAIN, OntUtil.createUnion(schema, UtilityClass, Entity));
		schema.add(comment, RDFS.RANGE, XMLSchema.STRING);
		schema.add(component, RDFS.DOMAIN, Complex);
		schema.add(component, RDFS.RANGE, PhysicalEntity);
		schema.add(componentStoichiometry, RDFS.DOMAIN, Complex);
		schema.add(componentStoichiometry, RDFS.RANGE, Stoichiometry);
		schema.add(confidence, RDFS.DOMAIN, Evidence);
		schema.add(confidence, RDFS.RANGE, Score);
		schema.add(controlled, RDFS.DOMAIN, Control);
		schema.add(controlled, RDFS.RANGE, OntUtil.createUnion(schema, Interaction, Pathway));
		schema.add(controller, RDFS.DOMAIN, Control);
		schema.add(controller, RDFS.RANGE, OntUtil.createUnion(schema, PhysicalEntity, Pathway));
		schema.add(controlType, RDFS.DOMAIN, Control);
		schema.add(controlType, RDFS.RANGE, OntUtil.createDataRange(schema, "INHIBITION", "ACTIVATION", 
				"INHIBITION-ALLOSTERIC", "INHIBITION-COMPETITIVE", "INHIBITION-IRREVERSIBLE", "INHIBITION-NONCOMPETITIVE", 
				"INHIBITION-OTHER", "INHIBITION-UNCOMPETITIVE", "ACTIVATION-NONALLOSTERIC", "ACTIVATION-ALLOSTERIC"));
		schema.add(conversionDirection, RDFS.DOMAIN, Conversion);
		schema.add(conversionDirection, RDFS.RANGE, OntUtil.createDataRange(schema, "REVERSIBLE", "RIGHT-TO-LEFT", 
				"LEFT-TO-RIGHT"));
		schema.add(dataSource, RDFS.DOMAIN, Entity);
		schema.add(dataSource, RDFS.RANGE, Provenance);
		schema.add(db, RDFS.DOMAIN, Xref);
		schema.add(db, RDFS.RANGE, XMLSchema.STRING);
		schema.add(dbVersion, RDFS.DOMAIN, Xref);
		schema.add(dbVersion, RDFS.RANGE, XMLSchema.STRING);
		schema.add(deltaG, RDFS.DOMAIN, BiochemicalReaction);
		schema.add(deltaG, RDFS.RANGE, DeltaG);
		schema.add(deltaGPrime0, RDFS.DOMAIN, DeltaG);
		schema.add(deltaGPrime0, RDFS.RANGE, XMLSchema.FLOAT);
		schema.add(deltaH, RDFS.DOMAIN, BiochemicalReaction);
		schema.add(deltaH, RDFS.RANGE, XMLSchema.FLOAT);
		schema.add(deltaS, RDFS.DOMAIN, BiochemicalReaction);
		schema.add(deltaS, RDFS.RANGE, XMLSchema.FLOAT);
		schema.add(displayName, RDFS.RANGE, XMLSchema.STRING);
		schema.add(eCNumber, RDFS.DOMAIN, BiochemicalReaction);
		schema.add(eCNumber, RDFS.RANGE, XMLSchema.STRING);
		schema.add(entityFeature, RDFS.DOMAIN, EntityReference);
		schema.add(entityFeature, RDFS.RANGE, EntityFeature);
		schema.add(entityReference, RDFS.DOMAIN, OntUtil.createUnion(schema, Protein, Rna, Dna, SmallMolecule, DnaRegion, 
				RnaRegion));
		schema.add(entityReference, RDFS.RANGE, EntityReference);
		schema.add(entityReferenceType, RDFS.DOMAIN, EntityReference);
		schema.add(entityReferenceType, RDFS.RANGE, EntityReferenceTypeVocabulary);
		schema.add(evidence, RDFS.DOMAIN, 
				OntUtil.createUnion(schema, PathwayStep, EntityFeature, EntityReference, Entity));
		schema.add(evidence, RDFS.RANGE, Evidence);
		schema.add(evidenceCode, RDFS.DOMAIN, Evidence);
		schema.add(evidenceCode, RDFS.RANGE, EvidenceCodeVocabulary);
		schema.add(experimentalFeature, RDFS.DOMAIN, ExperimentalForm);
		schema.add(experimentalFeature, RDFS.RANGE, EntityFeature);
		schema.add(experimentalForm, RDFS.DOMAIN, Evidence);
		schema.add(experimentalForm, RDFS.RANGE, ExperimentalForm);
		schema.add(experimentalFormDescription, RDFS.DOMAIN, ExperimentalForm);
		schema.add(experimentalFormDescription, RDFS.RANGE, ExperimentalFormVocabulary);
		schema.add(experimentalFormEntity, RDFS.DOMAIN, ExperimentalForm);
		schema.add(experimentalFormEntity, RDFS.RANGE, OntUtil.createUnion(schema, PhysicalEntity, Gene));
		schema.add(feature, RDFS.DOMAIN, PhysicalEntity);
		schema.add(feature, RDFS.RANGE, EntityFeature);
		schema.add(featureLocation, RDFS.DOMAIN, EntityFeature);
		schema.add(featureLocation, RDFS.RANGE, SequenceLocation);
		schema.add(featureLocationType, RDFS.DOMAIN, EntityFeature);
		schema.add(featureLocationType, RDFS.RANGE, SequenceRegionVocabulary);
		schema.add(id, RDFS.DOMAIN, Xref);
		schema.add(id, RDFS.RANGE, XMLSchema.STRING);
		schema.add(idVersion, RDFS.DOMAIN, Xref);
		schema.add(idVersion, RDFS.RANGE, XMLSchema.STRING);
		schema.add(interactionScore, RDFS.DOMAIN, GeneticInteraction);
		schema.add(interactionScore, RDFS.RANGE, Score);
		schema.add(interactionType, RDFS.DOMAIN, Interaction);
		schema.add(interactionType, RDFS.RANGE, InteractionVocabulary);
		schema.add(intraMolecular, RDFS.DOMAIN, BindingFeature);
		schema.add(intraMolecular, RDFS.RANGE, XMLSchema.BOOLEAN);
		schema.add(ionicStrength, RDFS.DOMAIN, OntUtil.createUnion(schema, KPrime, DeltaG));
		schema.add(ionicStrength, RDFS.RANGE, XMLSchema.FLOAT);
		schema.add(kEQ, RDFS.DOMAIN, BiochemicalReaction);
		schema.add(kEQ, RDFS.RANGE, KPrime);
		schema.add(kPrime, RDFS.DOMAIN, KPrime);
		schema.add(kPrime, RDFS.RANGE, XMLSchema.FLOAT);
		schema.add(left, RDFS.DOMAIN, Conversion);
		schema.add(left, RDFS.RANGE, PhysicalEntity);
		schema.add(memberEntityReference, RDFS.DOMAIN, EntityReference);
		schema.add(memberEntityReference, RDFS.RANGE, EntityReference);
		schema.add(memberFeature, RDFS.DOMAIN, EntityFeature);
		schema.add(memberFeature, RDFS.RANGE, EntityFeature);
		schema.add(memberPhysicalEntity, RDFS.DOMAIN, PhysicalEntity);
		schema.add(memberPhysicalEntity, RDFS.RANGE, PhysicalEntity);
		schema.add(modificationType, RDFS.DOMAIN, ModificationFeature);
		schema.add(modificationType, RDFS.RANGE, SequenceModificationVocabulary);
		schema.add(molecularWeight, RDFS.DOMAIN, SmallMoleculeReference);
		schema.add(molecularWeight, RDFS.RANGE, XMLSchema.FLOAT);
		schema.add(name, RDFS.DOMAIN, OntUtil.createUnion(schema, EntityReference, BioSource, Entity, Provenance));
		schema.add(name, RDFS.RANGE, XMLSchema.STRING);
		schema.add(nextStep, RDFS.DOMAIN, PathwayStep);
		schema.add(nextStep, RDFS.RANGE, PathwayStep);
		schema.add(notFeature, RDFS.DOMAIN, PhysicalEntity);
		schema.add(notFeature, RDFS.RANGE, EntityFeature);
		schema.add(organism, RDFS.DOMAIN, OntUtil.createUnion(schema, Pathway, DnaReference, RnaReference, 
				ProteinReference, Gene, DnaRegionReference, RnaRegionReference));
		schema.add(organism, RDFS.RANGE, BioSource);
		schema.add(participant, RDFS.DOMAIN, Interaction);
		schema.add(participant, RDFS.RANGE, Entity);
		schema.add(participantStoichiometry, RDFS.DOMAIN, Conversion);
		schema.add(participantStoichiometry, RDFS.RANGE, Stoichiometry);
		schema.add(pathwayComponent, RDFS.DOMAIN, Pathway);
		schema.add(pathwayComponent, RDFS.RANGE, OntUtil.createUnion(schema, Interaction, Pathway));
		schema.add(pathwayOrder, RDFS.DOMAIN, Pathway);
		schema.add(pathwayOrder, RDFS.RANGE, PathwayStep);
		schema.add(patoData, RDFS.DOMAIN, PhenotypeVocabulary);
		schema.add(patoData, RDFS.RANGE, XMLSchema.STRING);
		schema.add(ph, RDFS.DOMAIN, OntUtil.createUnion(schema, KPrime, DeltaG));
		schema.add(ph, RDFS.RANGE, XMLSchema.FLOAT);
		schema.add(phenotype, RDFS.DOMAIN, GeneticInteraction);
		schema.add(phenotype, RDFS.RANGE, PhenotypeVocabulary);
		schema.add(physicalEntity, RDFS.DOMAIN, Stoichiometry);
		schema.add(physicalEntity, RDFS.RANGE, PhysicalEntity);
		schema.add(pMg, RDFS.DOMAIN, OntUtil.createUnion(schema, KPrime, DeltaG));
		schema.add(pMg, RDFS.RANGE, XMLSchema.FLOAT);
		schema.add(positionStatus, RDFS.DOMAIN, SequenceSite);
		schema.add(positionStatus, RDFS.RANGE, OntUtil.createDataRange(schema, "EQUAL", "LESS-THAN","GREATER-THAN"));
		schema.add(product, RDFS.DOMAIN, TemplateReaction);
		schema.add(product, RDFS.RANGE, OntUtil.createUnion(schema, Dna, Protein, Rna));
		schema.add(regionOf, RDFS.DOMAIN, OntUtil.createUnion(schema, DnaRegionReference, RnaRegionReference));
		schema.add(regionOf, RDFS.RANGE, OntUtil.createUnion(schema, DnaReference, RnaReference));
		schema.add(regionType, RDFS.DOMAIN, OntUtil.createUnion(schema, DnaRegionReference, RnaRegionReference));
		schema.add(regionType, RDFS.RANGE, SequenceRegionVocabulary);
		schema.add(relationshipType, RDFS.DOMAIN, RelationshipXref);
		schema.add(relationshipType, RDFS.RANGE, RelationshipTypeVocabulary);
		schema.add(right, RDFS.DOMAIN, Conversion);
		schema.add(right, RDFS.RANGE, PhysicalEntity);
		schema.add(scoreSource, RDFS.DOMAIN, Score);
		schema.add(scoreSource, RDFS.RANGE, Provenance);
		schema.add(sequence, RDFS.DOMAIN, OntUtil.createUnion(schema, DnaReference, RnaReference, ProteinReference, 
				DnaRegionReference, RnaRegionReference));
		schema.add(sequence, RDFS.RANGE, XMLSchema.STRING);
		schema.add(sequenceIntervalBegin, RDFS.DOMAIN, SequenceInterval);
		schema.add(sequenceIntervalBegin, RDFS.RANGE, SequenceSite);
		schema.add(sequenceIntervalEnd, RDFS.DOMAIN, SequenceInterval);
		schema.add(sequenceIntervalEnd, RDFS.RANGE, SequenceSite);
		schema.add(sequencePosition, RDFS.DOMAIN, SequenceSite);
		schema.add(sequencePosition, RDFS.RANGE, XMLSchema.INT);
		schema.add(source, RDFS.DOMAIN, PublicationXref);
		schema.add(source, RDFS.RANGE, XMLSchema.STRING);
		schema.add(spontaneous, RDFS.DOMAIN, Conversion);
		schema.add(spontaneous, RDFS.RANGE, XMLSchema.BOOLEAN);
		schema.add(standardName, RDFS.RANGE, XMLSchema.STRING);
		schema.add(stepConversion, RDFS.DOMAIN, BiochemicalPathwayStep);
		schema.add(stepConversion, RDFS.RANGE, Conversion);
		schema.add(stepDirection, RDFS.DOMAIN, BiochemicalPathwayStep);
		schema.add(stepDirection, RDFS.RANGE, 
				OntUtil.createDataRange(schema, "REVERSIBLE", "RIGHT-TO-LEFT", "LEFT-TO-RIGHT"));
		schema.add(stepProcess, RDFS.DOMAIN, PathwayStep);
		schema.add(stepProcess, RDFS.RANGE, OntUtil.createUnion(schema, Pathway, Interaction));
		schema.add(stoichiometricCoefficient, RDFS.DOMAIN, Stoichiometry);
		schema.add(stoichiometricCoefficient, RDFS.RANGE, XMLSchema.STRING);
		schema.add(structure, RDFS.DOMAIN, SmallMoleculeReference);
		schema.add(structure, RDFS.RANGE, ChemicalStructure);
		schema.add(structureData, RDFS.DOMAIN, ChemicalStructure);
		schema.add(structureData, RDFS.RANGE, XMLSchema.STRING);
		schema.add(structureFormat, RDFS.DOMAIN, ChemicalStructure);
		schema.add(structureFormat, RDFS.RANGE, OntUtil.createDataRange(schema, "CML", "SMILES", "InChI"));
		schema.add(subRegion, RDFS.DOMAIN, OntUtil.createUnion(schema, DnaRegionReference, RnaRegionReference));
		schema.add(subRegion, RDFS.RANGE, EntityReference);
		schema.add(taxonXref, RDFS.DOMAIN, BioSource);
		schema.add(taxonXref, RDFS.RANGE, UnificationXref);
		schema.add(temperature, RDFS.DOMAIN, OntUtil.createUnion(schema, KPrime, DeltaG));
		schema.add(temperature, RDFS.RANGE, XMLSchema.FLOAT);
		schema.add(template, RDFS.DOMAIN, TemplateReaction);
		schema.add(template, RDFS.RANGE, OntUtil.createUnion(schema, DnaRegion, RnaRegion, Dna, Rna));
		schema.add(templateDirection, RDFS.DOMAIN, TemplateReaction);
		schema.add(templateDirection, RDFS.RANGE, OntUtil.createDataRange(schema, "FORWARD", "REVERSE"));
		schema.add(term, RDFS.DOMAIN, ControlledVocabulary);
		schema.add(term, RDFS.RANGE, XMLSchema.STRING);
		schema.add(tissue, RDFS.DOMAIN, BioSource);
		schema.add(tissue, RDFS.RANGE, TissueVocabulary);
		schema.add(title, RDFS.DOMAIN, PublicationXref);
		schema.add(title, RDFS.RANGE, XMLSchema.STRING);
		schema.add(url, RDFS.DOMAIN, PublicationXref);
		schema.add(url, RDFS.RANGE, XMLSchema.STRING);
		schema.add(value, RDFS.DOMAIN, Score);
		schema.add(value, RDFS.RANGE, XMLSchema.STRING);
		schema.add(xref, RDFS.DOMAIN, 
				OntUtil.createUnion(schema, ControlledVocabulary, Entity, Provenance, Evidence, EntityReference));
		schema.add(xref, RDFS.RANGE, Xref);
		schema.add(year, RDFS.DOMAIN, PublicationXref);
		schema.add(year, RDFS.RANGE, XMLSchema.INT);

		schema.add(BioSource, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, taxonXref, 1));
		schema.add(ChemicalStructure, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, structureData, 1));
		schema.add(ChemicalStructure, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, structureFormat, 1));
		schema.add(DeltaG, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, deltaGPrime0, 1));
		schema.add(GeneticInteraction, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, phenotype, 1));
		schema.add(KPrime, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, kPrime, 1));
		schema.add(Score, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, value, 1));
		schema.add(Stoichiometry, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, physicalEntity, 1));
		schema.add(Stoichiometry, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, stoichiometricCoefficient, 1));
		schema.add(UnificationXref, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, db, 1));
		schema.add(UnificationXref, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.CARDINALITY, id, 1));
		
		schema.add(GeneticInteraction, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.MINCARDINALITY, participant, 1));
		schema.add(ExperimentalForm, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.MINCARDINALITY, experimentalFormDescription, 1));
		
		schema.add(Catalysis, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.MAXCARDINALITY, controller, 1));
		schema.add(Gene, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.MAXCARDINALITY, organism, 1));
		schema.add(GeneticInteraction, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.MAXCARDINALITY, interactionType, 1));
		schema.add(Modulation, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.MAXCARDINALITY, controller, 1));
		
		schema.add(Catalysis, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.HASVALUE, controlType, 
						schema.getValueFactory().createLiteral("ACTIVATION")));
		schema.add(Degradation, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.HASVALUE, conversionDirection, 
						schema.getValueFactory().createLiteral("LEFT-TO-RIGHT")));
		
		schema.add(BiochemicalPathwayStep, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, stepProcess, Control));
		schema.add(Catalysis, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, controlled, Conversion));
		schema.add(Complex, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, memberPhysicalEntity, Complex));
		schema.add(ControlledVocabulary, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, xref, UnificationXref));
		schema.add(Conversion, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, participant, PhysicalEntity));
		schema.add(Dna, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, entityReference, DnaReference));
		schema.add(Dna, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, memberPhysicalEntity, Dna));
		schema.add(DnaReference, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, memberEntityReference, DnaReference));
		schema.add(DnaRegion, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, memberPhysicalEntity, DnaRegion));
		schema.add(DnaRegion, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, entityReference, DnaRegionReference));
		schema.add(DnaRegionReference, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, subRegion, DnaRegionReference));
		schema.add(DnaRegionReference, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, regionOf, DnaReference));
		schema.add(GeneticInteraction, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, participant, Gene));
		schema.add(Modulation, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, controlled, Catalysis));
		schema.add(Modulation, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, controller, PhysicalEntity));
		schema.add(MolecularInteraction, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, participant, PhysicalEntity));
		schema.add(Protein, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, entityReference, ProteinReference));
		schema.add(Protein, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, memberPhysicalEntity, Protein));
		schema.add(ProteinReference, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, memberEntityReference, ProteinReference));
		schema.add(Provenance, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, xref, 
						OntUtil.createUnion(schema, PublicationXref, UnificationXref)));
		schema.add(Rna, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, entityReference, RnaReference));
		schema.add(Rna, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, memberPhysicalEntity, Rna));
		schema.add(RnaReference, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, memberEntityReference, RnaReference));
		schema.add(RnaRegion, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, memberPhysicalEntity, RnaRegion));
		schema.add(RnaRegion, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, entityReference, RnaRegionReference));
		schema.add(RnaRegionReference, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, regionOf, RnaReference));
		schema.add(RnaRegionReference, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, subRegion, RnaRegionReference));
		schema.add(SmallMolecule, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, entityReference, SmallMoleculeReference));
		schema.add(SmallMolecule, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, feature, BindingFeature));
		schema.add(SmallMolecule, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, memberPhysicalEntity, SmallMolecule));
		schema.add(SmallMolecule, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, memberEntityReference, SmallMoleculeReference));
		schema.add(SmallMolecule, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, notFeature, BindingFeature));
		schema.add(TemplateReaction, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, participant, PhysicalEntity));
		schema.add(TemplateReactionRegulation, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, controlled, TemplateReaction));
		schema.add(TemplateReactionRegulation, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, controller, PhysicalEntity));
		schema.add(TemplateReactionRegulation, RDFS.SUBCLASSOF, 
				OntUtil.createRestriction(schema, OWL.ALLVALUESFROM, controlType, 
						OntUtil.createDataRange(schema, "ACTIVATION", "INHIBITION")));

		schema.add(Evidence, RDFS.SUBCLASSOF, OntUtil.createUnion(schema, 
				OntUtil.createRestriction(schema, OWL.MINCARDINALITY, confidence, 1),
				OntUtil.createRestriction(schema, OWL.MINCARDINALITY, evidenceCode, 1),
				OntUtil.createRestriction(schema, OWL.MINCARDINALITY, experimentalForm, 1)));
		
		OntUtil.makeAllDisjoint(schema, Protein, Dna, Rna);
		OntUtil.makeAllDisjoint(schema, GeneticInteraction, Control);
		OntUtil.makeAllDisjoint(schema, GeneticInteraction, TemplateReaction);
		OntUtil.makeAllDisjoint(schema, GeneticInteraction, MolecularInteraction);
		OntUtil.makeAllDisjoint(schema, Interaction, PhysicalEntity, Pathway, Gene);
		OntUtil.makeAllDisjoint(schema, GeneticInteraction, Conversion);
	}

	
	static {

		OntUtil.addTypedComment(schema, absoluteRegion, 
		"Absolute location as defined by the referenced sequence database record. E.g. an operon " + 
		"has a absolute region on the DNA molecule referenced by the UnificationXref.");
		
		OntUtil.addEnglishComment(schema, author, 
		"The authors of this publication, one per property value.");
		
		OntUtil.addEnglishComment(schema, availability, 
		"Describes the availability of this data (e.g. a copyright statement).");
		
		OntUtil.addTypedComment(schema, BindingFeature, 
		"Specifies the binding domains of two entities in a complex that are non-covalently bound to " + 
		"each other. Note that this is a n-ary specifier class on the boundTo property. " + 
		"The difference between this class and modificationFeature is that this is non-covalent and " + 
		"modificationFeature is covalent.");
		
		OntUtil.addTypedComment(schema, bindsTo, 
		"A binding feature represents a \"half\" of the bond between two entities. This property " + 
		"points to another binding feature which represents the other half. The bond can be " + 
		"covalent or non-covalent.");
		
		OntUtil.addTypedComment(schema, BiochemicalPathwayStep, 
		"Imposes ordering on a step in a biochemical pathway. A biochemical reaction can be reversible " + 
		"by itself, but can be physiologically directed in the context of a pathway, for instance due " + 
		"to flux of reactants and products. Only one conversion interaction can be ordered at a time, " + 
		"but multiple catalysis or modulation instances can be part of one step.");
		
		OntUtil.addTypedComment(schema, BiochemicalReaction, 
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
		
		OntUtil.addTypedComment(schema, BioSource, 
		"Definition: The biological source of an entity (e.g. protein, RNA or DNA). Some entities " + 
		"are considered source-neutral (e.g. small molecules), and the biological source of others " + 
		"can be deduced from their constituentss (e.g. complex, pathway).\n" +
		"Examples: HeLa cells, human, and mouse liver tissue.");
		
		OntUtil.addTypedComment(schema, Catalysis, 
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
		
		OntUtil.addTypedComment(schema, catalysisDirection, 
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
		
		OntUtil.addEnglishComment(schema, cellType, 
		"A cell type, e.g. 'HeLa'. This should reference a term in a controlled vocabulary of cell " + 
		"types. Best practice is to refer to OBO Cell Ontology. " + 
		"http://www.obofoundry.org/cgi-bin/detail.cgi?id=cell");
		
		OntUtil.addTypedComment(schema, CellVocabulary, 
		"A reference to the Cell Type Ontology (CL). Homepage at " + 
		"http://obofoundry.org/cgi-bin/detail.cgi?cell.  Browse at " + 
		"http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=CL");
		
		OntUtil.addTypedComment(schema, cellularLocation, 
		"A cellular location, e.g. 'cytoplasm'. This should reference a term in the Gene Ontology " + 
		"Cellular Component ontology. The location referred to by this property should be as specific " + 
		"as is known. If an interaction is known to occur in multiple locations, separate " + 
		"interactions (and physicalEntities) must be created for each different location.  If the " + 
		"location of a participant in a complex is unspecified, it may be assumed to be the same " + 
		"location as that of the complex. \n\n" +
		" A molecule in two different cellular locations are considered two different physical entities.");
		
		OntUtil.addTypedComment(schema, CellularLocationVocabulary, 
		"A reference to the Gene Ontology Cellular Component (GO CC) ontology. Homepage at " + 
		"http://www.geneontology.org.  Browse at " + 
		"http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=GO");
		
		OntUtil.addTypedComment(schema, chemicalFormula, 
		"The chemical formula of the small molecule. Note: chemical formula can also be stored in " + 
		"the STRUCTURE property (in CML). In case of disagreement between the value of this property " + 
		"and that in the CML file, the CML value takes precedence.");

		OntUtil.addTypedComment(schema, ChemicalStructure, 
		"Definition: Describes a small molecule structure. Structure information is stored in the " + 
		"property STRUCTURE-DATA, in one of three formats: the CML format (see URL www.xml-cml.org), " + 
		"the SMILES format (see URL www.daylight.com/dayhtml/smiles/) or the InChI format " + 
		"(http://www.iupac.org/inchi/). The STRUCTURE-FORMAT property specifies which format is used.\n" +
		"Comment: By virtue of the expressivity of CML, an instance of this class can also provide " + 
		"additional information about a small molecule, such as its chemical formula, names, " + 
		"and synonyms, if CML is used as the structure format.\n" +
		"Examples: The following SMILES string, which describes the structure of glucose-6-phosphate:\n" +
		"'C(OP(=O)(O)O)[CH]1([CH](O)[CH](O)[CH](O)[CH](O)O1)'.");
		
		OntUtil.addEnglishComment(schema, cofactor, 
		"Any cofactor(s) or coenzyme(s) required for catalysis of the conversion by the enzyme. " + 
		"COFACTOR is a sub-property of PARTICIPANTS.");
		
		OntUtil.addTypedComment(schema, comment, 
		"Comment on the data in the container class. This property should be used instead of the " + 
		"OWL documentation elements (rdfs:comment) for instances because information in 'comment' " + 
		"is data to be exchanged, whereas the rdfs:comment field is used for metadata about the " + 
		"structure of the BioPAX ontology.");
		
		OntUtil.addTypedComment(schema, Complex, 
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
		
		OntUtil.addTypedComment(schema, ComplexAssembly, 
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
		
		OntUtil.addTypedComment(schema, componentStoichiometry, 
		"The stoichiometry of components in a complex");
		
		OntUtil.addTypedComment(schema, confidence, 
		"Confidence in the containing instance.  Usually a statistical measure.");
		
		OntUtil.addTypedComment(schema, Control, 
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
		
		OntUtil.addEnglishComment(schema, controlled, 
		"The entity that is controlled, e.g., in a biochemical reaction, the reaction is controlled by " + 
		"an enzyme. CONTROLLED is a sub-property of PARTICIPANTS.");
		
		OntUtil.addEnglishComment(schema, controller, 
		"The controlling entity, e.g., in a biochemical reaction, an enzyme is the controlling entity " + 
		"of the reaction. CONTROLLER is a sub-property of PARTICIPANTS.");
		
		OntUtil.addTypedComment(schema, ControlledVocabulary, 
		"Definition: Used to import terms from external controlled vocabularies (CVs) into the ontology. " + 
		"To support consistency and compatibility, open, freely available CVs should be used " + 
		"whenever possible, such as the Gene Ontology (GO) or other open biological CVs listed on the " + 
		"OBO website (http://obo.sourceforge.net/).\n" + 
		"Comment: The ID property in unification xrefs to GO and other OBO ontologies should include " + 
		"the ontology name in the ID property (e.g. ID=\"GO:0005634\" instead of ID=\"0005634\").");

		OntUtil.addTypedComment(schema, controlType, 
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
		
		OntUtil.addTypedComment(schema, Conversion, 
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
		
		OntUtil.addTypedComment(schema, conversionDirection, 
		"This property represents the direction of the reaction. If a reaction is fundamentally " + 
		"irreversible, then it will run in a single direction under all contexts. Otherwise it is " + 
		"reversible.");
		
		OntUtil.addTypedComment(schema, CovalentBindingFeature, 
		"Covalent bond within or between physical entities.");

		OntUtil.addTypedComment(schema, dataSource, 
		"A free text description of the source of this data, e.g. a database or person name. " + 
		"This property should be used to describe the source of the data. This is meant to be used " + 
		"by databases that export their data to the BioPAX format or by systems that are integrating " + 
		"data from multiple sources. The granularity of use (specifying the data source in many or " + 
		"few instances) is up to the user. It is intended that this property report the last data " + 
		"source, not all data sources that the data has passed through from creation.");
		
		OntUtil.addEnglishComment(schema, db, 
		"The name of the external database to which this xref refers.");

		OntUtil.addEnglishComment(schema, dbVersion, 
		"The version of the external database in which this xref was last known to be valid. " + 
		"Resources may have recommendations for referencing dataset versions. For instance, the " + 
		"Gene Ontology recommends listing the date the GO terms were downloaded.");
		
		OntUtil.addTypedComment(schema, Degradation, 
		"The process of degrading a physical entity. The right side of the conversion is not specified, " + 
		"indicating degraded components. The conversion is not spontaneous.");

		OntUtil.addTypedComment(schema, DeltaG, 
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
		
		OntUtil.addTypedComment(schema, deltaG, 
		"For biochemical reactions, this property refers to the standard transformed Gibbs energy " + 
		"change for a reaction written in terms of biochemical reactants (sums of species), delta-G\n\n" +
		"Since Delta-G can change based on multiple factors including ionic strength and temperature " + 
		"a reaction can have multiple DeltaG values.");
		
		OntUtil.addTypedComment(schema, deltaGPrime0, 
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
		
		OntUtil.addTypedComment(schema, deltaH, 
		"For biochemical reactions, this property refers to the standard transformed enthalpy change " + 
		"for a reaction written in terms of biochemical reactants (sums of species), " + 
		"delta-H'<sup>o</sup>.\n\n" +
		"  delta-G'<sup>o</sup> = delta-H'<sup>o</sup> - T delta-S'<sup>o</sup>\n\n" +
		"Units: kJ/mole\n\n" +
		"(This definition from EcoCyc)");
		
		OntUtil.addTypedComment(schema, deltaS, 
		"For biochemical reactions, this property refers to the standard transformed entropy change " + 
		"for a reaction written in terms of biochemical reactants (sums of species), " + 
		"delta-S'<sup>o</sup>.\n\n" +
		"  delta-G'<sup>o</sup> = delta-H'<sup>o</sup> - T delta-S'<sup>o</sup>\n\n" +
		"(This definition from EcoCyc)");
		
		OntUtil.addTypedComment(schema, displayName, 
		"An abbreviated name for this entity, preferably a name that is short enough to be used in " + 
		"a visualization application to label a graphical element that represents this entity. If " + 
		"no short name is available, an xref may be used for this purpose by the visualization " + 
		"application.");
		
		OntUtil.addTypedComment(schema, Dna, 
		"Definition: A physical entity consisting of a sequence of deoxyribonucleotide monophosphates; " + 
		"a deoxyribonucleic acid.\n" +
		"Comment: This is not a 'gene', since gene is a genetic concept, not a physical entity. " + 
		"The concept of a gene may be added later in BioPAX.\n" +
		"Examples: a chromosome, a plasmid. A specific example is chromosome 7 of Homo sapiens.");
		
		OntUtil.addTypedComment(schema, DnaReference, 
		"A DNA reference is a grouping of several DNA entities that are common in sequence and " + 
		"genomic position.  Members can differ in celular location, sequence features, SNPs, mutations " + 
		"and bound partners.\n\n" +
        "Comments : Note that this is not a reference gene. A gene can possibly span multiple " + 
        "DNA molecules, sometimes even across chromosomes due to regulatory regions. Similarly a gene " + 
        "is not necessarily made up of deoxyribonucleic acid and can be present in multiple copies " + 
        "( which are different DNA molecules).");

		OntUtil.addTypedComment(schema, DnaRegion, 
		"A region of DNA.");

		OntUtil.addTypedComment(schema, eCNumber, 
		"The unique number assigned to a reaction by the Enzyme Commission of the International " + 
		"Union of Biochemistry and Molecular Biology.\n\n" +
		"Note that not all biochemical reactions currently have EC numbers assigned to them.");
		
		OntUtil.addTypedComment(schema, Entity, 
		"Definition: A discrete biological unit used when describing pathways. This is an interacting " + 
		"entity, not just any entity.\n" +
		"Comment: This is the root class for all interacting components in the ontology, " + 
		"which include pathways, interactions and physical entities. As the most abstract class " + 
		"in the ontology, instances of the entity class should never be created. " + 
		"Instead, more specific classes should be used.\n" +
		"Synonyms: thing, object, bioentity.");

		OntUtil.addTypedComment(schema, EntityFeature, 
		"A feature or aspect of a physical entity that can be changed while the entity still retains " + 
		"its biological identity.");
		
		OntUtil.addTypedComment(schema, entityFeature, 
		"Variable features that are observed for this entity - such as known PTM or methylation " + 
		"sites and non-covalent bonds.");
		
		OntUtil.addTypedComment(schema, EntityReference, 
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
		
		OntUtil.addTypedComment(schema, entityReference, 
		"Reference entity for this physical entity.");
		
		OntUtil.addTypedComment(schema, entityReferenceType, 
		"A controlled vocabulary term that is used to describe the type of grouping such as " + 
		"homology or functional group.");
		
		OntUtil.addTypedComment(schema, EntityReferenceTypeVocabulary, 
		"A reference to a term from a reference entity group ontology. " + 
		"There is no \"best-practice\" vocabulary for this one.");

		OntUtil.addTypedComment(schema, Evidence, 
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
		
		OntUtil.addTypedComment(schema, evidence, 
		"Scientific evidence supporting the existence of the entity as described.");
		
		OntUtil.addTypedComment(schema, evidenceCode, 
		"A pointer to a term in an external controlled vocabulary, such as the GO, PSI-MI or BioCyc " + 
		"evidence codes, that describes the nature of the support, such as 'traceable author " + 
		"statement' or 'yeast two-hybrid'.");
		
		OntUtil.addTypedComment(schema, EvidenceCodeVocabulary, 
		"A reference to the PSI Molecular Interaction ontology (MI) experimental method types, " + 
		"including \"interaction detection method\", \"participant identification method\", \"feature " + 
		"detection method\". Homepage at http://www.psidev.info/.  " + 
		"Browse at http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI\n\n" +
		"Terms from the Pathway Tools Evidence Ontology may also be used. " + 
		"Homepage http://brg.ai.sri.com/evidence-ontology/");
		
		OntUtil.addTypedComment(schema, experimentalFeature, 
		"A feature of the experimental form of the participant of the interaction, such as a " + 
		"protein tag. It is not expected to occur in vivo or be necessary for the interaction.");
		
		OntUtil.addTypedComment(schema, ExperimentalForm, 
		"Definition: The form of a physical entity in a particular experiment, as it may be modified " + 
		"for purposes of experimental design.\n" +
		"Examples: A His-tagged protein in a binding assay. A protein can be tagged by multiple tags, " + 
		"so can have more than 1 experimental form type terms");

		OntUtil.addTypedComment(schema, experimentalForm, 
		"The experimental forms associated with an evidence instance.");
		
		OntUtil.addTypedComment(schema, experimentalFormDescription, 
		"Descriptor of this experimental form from a controlled vocabulary.");

		OntUtil.addTypedComment(schema, experimentalFormEntity, 
		"The gene or physical entity that this experimental form describes.");
		
		OntUtil.addTypedComment(schema, ExperimentalFormVocabulary, 
		"A reference to the PSI Molecular Interaction ontology (MI) participant identification method " + 
		"(e.g. mass spectrometry), experimental role (e.g. bait, prey), experimental preparation " + 
		"(e.g. expression level) type. Homepage at http://www.psidev.info/.  Browse " + 
		"http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI&termId=MI%3A0002&" + 
		"termName=participant%20identification%20method\n\n" +
		"http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI&termId=MI%3A0495&" + 
		"termName=experimental%20role\n\n" +
		"http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI&termId=MI%3A0346&" + 
		"termName=experimental%20preparation");
		
		OntUtil.addTypedComment(schema, feature, 
		"Sequence features of the owner physical entity.");
		
		OntUtil.addTypedComment(schema, featureLocation, 
		"Location of the feature on the sequence of the interactor. One feature may have more than " + 
		"one location, used e.g. for features which involve sequence positions close in the folded, " + 
		"three-dimensional state of a protein, but non-continuous along the sequence.");
		
		OntUtil.addTypedComment(schema, featureLocationType, 
		"A controlled vocabulary term describing the type of the sequence location such as C-Terminal " + 
		"or SH2 Domain.");
		
		OntUtil.addTypedComment(schema, FragmentFeature, 
		"Definition: Represents the results of a cleavage or degradation event. \n" +
		"There are multiple cases:\n" +
		"1.    A protein with a single cleavage site that converts the protein into two fragments " + 
		"(e.g. pro-insulin converted to insulin and C-peptide). TODO: CV term for sequence fragment?  " + 
		"PSI-MI CV term for cleavage site?\n" +
		"2.    A protein with two cleavage sites that removes an internal sequence e.g. an intein " + 
		"i.e. ABC -> AC\n" +
		"3.    Cleavage of a circular sequence e.g. a plasmid.");
		
		OntUtil.addTypedComment(schema, Gene, 
		"A continuant that encodes information that can be inherited through replication. This is " + 
		"a generalization of the prokaryotic and eukaryotic notion of a gene. This is used only for " + 
		"genetic interactions. Gene expression regulation makes use of DNA and RNA physical entities.");
		
		OntUtil.addTypedComment(schema, GeneticInteraction, 
		"Genetic interactions between genes occur when two genetic perturbations (e.g. mutations) have " + 
		"a combined phenotypic effect not caused by either perturbation alone. This is not a " + 
		"physical interaction, but rather logical. For example, a synthetic lethal interaction " + 
		"occurs when cell growth is possible without either gene A OR B, but not without both gene " + 
		"A AND B. If you knock out A and B together, the cell will die. A gene participant in a " + 
		"genetic interaction represents the gene that is perturbed.");
		
		OntUtil.addEnglishComment(schema, id, 
		"The primary identifier in the external database of the object to which this xref refers.");
		
		OntUtil.addTypedComment(schema, idVersion, 
		"The version number of the identifier (ID). E.g. The RefSeq accession number NM_005228.3 " + 
		"should be split into NM_005228 as the ID and 3 as the ID-VERSION.");
		
		OntUtil.addTypedComment(schema, Interaction, 
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
		
		OntUtil.addTypedComment(schema, interactionType, 
		"External controlled vocabulary annotating the interaction type, for " + 
		"example \"phosphorylation\". This is annotation useful for e.g. display on a web page or " + 
		"database searching, but may not be suitable for other computing tasks, like reasoning.");
		
		OntUtil.addTypedComment(schema, InteractionVocabulary, 
		"A reference to the PSI Molecular Interaction ontology (MI) interaction type. " + 
		"Homepage at http://www.psidev.info/.  " + 
		"Browse at http://www.ebi.ac.uk/ontology-lookup/browse.do?" + 
		"ontName=MI&termId=MI%3A0190&termName=interaction%20type");

		OntUtil.addTypedComment(schema, interactionScore, 
		"The score of an interaction e.g. a genetic interaction score."); 
		
		OntUtil.addTypedComment(schema, intraMolecular, 
		"This flag represents whether the binding feature is within the same molecule or not.");
		
		OntUtil.addTypedComment(schema, ionicStrength, 
		"The ionic strength is defined as half of the total sum of the concentration (ci) of every " + 
		"ionic species (i) in the solution times the square of its charge (zi). For example, the " + 
		"ionic strength of a 0.1 M solution of CaCl2 is 0.5 x (0.1 x 22 + 0.2 x 12) = 0.3 M\n" +
		"(Definition from http://www.lsbu.ac.uk/biology/enztech/ph.html)");
		
		OntUtil.addTypedComment(schema, kEQ, 
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

		OntUtil.addTypedComment(schema, KPrime, 
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

		OntUtil.addTypedComment(schema, memberEntityReference, 
		"An entity reference that qualifies for the definition of this group. For example a member " + 
		"of a PFAM protein family.");

		OntUtil.addTypedComment(schema, kPrime, 
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
		
		OntUtil.addTypedComment(schema, left, 
		"The participants on the left side of the conversion interaction. Since conversion " + 
		"interactions may proceed in either the left-to-right or right-to-left direction, occupants " + 
		"of the LEFT property may be either reactants or products. LEFT is a sub-property of " + 
		"PARTICIPANTS.");
		
		OntUtil.addTypedComment(schema, memberFeature, 
		"An entity feature  that belongs to this homology grouping. Example: a homologous " + 
		"phosphorylation site across a protein family.");
		
		OntUtil.addTypedComment(schema, memberPhysicalEntity, 
		"This property stores the members of a generic physical entity. \n\n" +
		"For representing homology generics a better way is to use generic entity references and " + 
		"generic features. However not all generic logic can be captured by this, such as complex " + 
		"generics or rare cases where feature cardinality is variable. Usages of this property " + 
		"should be limited to such cases.");
		
		OntUtil.addTypedComment(schema, ModificationFeature, 
		"Definition: A covalently modified feature on a sequence relevant to an interaction, such " + 
		"as a post-translational modification. The difference between this class  and bindingFeature " + 
		"is that this is covalent and bindingFeature is non-covalent.\n" +
		"Examples: A phosphorylation on a protein.");
		
		OntUtil.addTypedComment(schema, modificationType, 
		"Description and classification of the feature.");
		
		OntUtil.addTypedComment(schema, Modulation, 
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
		
		OntUtil.addTypedComment(schema, MolecularInteraction, 
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
		
		OntUtil.addEnglishComment(schema, molecularWeight, 
		"Defines the molecular weight of the molecule, in daltons.");
		
		OntUtil.addEnglishComment(schema, name, 
		"One or more synonyms for the name of this individual. This should include the values of " + 
		"the standardName and displayName property so that it is easy to find all known names " + 
		"in one place.");
		
		OntUtil.addTypedComment(schema, nextStep, 
		"The next step(s) of the pathway.  Contains zero or more pathwayStep instances.  If there is " + 
		"no next step, this property is empty. Multiple pathwayStep instances indicate pathway branching.");
		
		OntUtil.addTypedComment(schema, notFeature, 
		"Sequence features where the owner physical entity has a feature. If not specified, " + 
		"other potential features are not known.");

		OntUtil.addTypedComment(schema, ontology, 
		"This is version 0.94 of the BioPAX Level 3 ontology.  The goal of the BioPAX group is to " + 
		"develop a common exchange format for biological pathway data.  More information is " + 
		"available at http://www.biopax.org.  This ontology is freely available under the LGPL " + 
		"(http://www.gnu.org/copyleft/lesser.html).");
		
		OntUtil.addEnglishComment(schema, organism, 
		"An organism, e.g. 'Homo sapiens'. This is the organism that the entity is found in. " + 
		"Pathways may not have an organism associated with them, for instance, reference pathways " + 
		"from KEGG. Sequence-based entities (DNA, protein, RNA) may contain an xref to a sequence " + 
		"database that contains organism information, in which case the information should be " + 
		"consistent with the value for ORGANISM.");
		
		OntUtil.addTypedComment(schema, participant, 
		"This property lists the entities that participate in this interaction. For example, in a " + 
		"biochemical reaction, the participants are the union of the reactants and the products of " + 
		"the reaction. This property has a number of sub-properties, such as LEFT and RIGHT used in " + 
		"the biochemicalInteraction class. Any participant listed in a sub-property will " + 
		"automatically be assumed to also be in PARTICIPANTS by a number of software systems, " + 
		"including Protege, so this property should not contain any instances if there are " + 
		"instances contained in a sub-property.");
		
		OntUtil.addTypedComment(schema, participantStoichiometry, 
		"Stoichiometry of the left and right participants.");
		
		OntUtil.addTypedComment(schema, Pathway, 
		"Definition: A set or series of interactions, often forming a network, which biologists have " + 
		"found useful to group together for organizational, historic, biophysical or other reasons.\n" +
		"Comment: It is possible to define a pathway without specifying the interactions within " + 
		"the pathway. In this case, the pathway instance could consist simply of a name and could " + 
		"be treated as a 'black box'.\n" +
		"Synonyms: network\n" +
		"Examples: glycolysis, valine biosynthesis");
		
		OntUtil.addTypedComment(schema, pathwayComponent, 
		"The set of interactions and/or pathwaySteps in this pathway/network. Each instance of the " + 
		"pathwayStep class defines: 1) a set of interactions that together define a particular " + 
		"step in the pathway, for example a catalysis instance and the conversion that it catalyzes; " + 
		"2) an order relationship to one or more other pathway steps (via the NEXT-STEP property). " + 
		"Note: This ordering is not necessarily temporal - the order described may simply represent " + 
		"connectivity between adjacent steps. Temporal ordering information should only be inferred " + 
		"from the direction of each interaction.");
		
		OntUtil.addTypedComment(schema, pathwayOrder, 
		"The ordering of components (interactions and pathways) in the context of this pathway. " + 
		"This is useful to specific circular or branched pathways or orderings when component " + 
		"biochemical reactions are normally reversible, but are directed in the context of this pathway.");
		
		OntUtil.addTypedComment(schema, PathwayStep, 
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
		
		OntUtil.addTypedComment(schema, patoData, 
		"The phenotype data from PATO, formatted as PhenoXML " + 
		"(defined at http://www.fruitfly.org/~cjm/obd/formats.html)");

		OntUtil.addTypedComment(schema, ph, 
		"A measure of acidity and alkalinity of a solution that is a number on a scale on which a " + 
		"value of 7 represents neutrality and lower numbers indicate increasing acidity and " + 
		"higher numbers increasing alkalinity and on which each unit of change represents " + 
		"a tenfold change in acidity or alkalinity and that is the negative logarithm of the " + 
		"effective hydrogen-ion concentration or hydrogen-ion activity in gram equivalents per liter " + 
		"of the solution. (Definition from Merriam-Webster Dictionary)");

		OntUtil.addTypedComment(schema, phenotype, 
		"The phenotype quality used to define this genetic interaction e.g. viability.");
		
		OntUtil.addTypedComment(schema, PhenotypeVocabulary, 
		"The phenotype measured in the experiment e.g. growth rate or viability of a cell. This is " + 
		"only the type, not the value e.g. for a synthetic lethal interaction, the phenotype is " + 
		"viability, specified by ID: PATO:0000169, \"viability\", not the value (specified by " + 
		"ID: PATO:0000718, \"lethal (sensu genetics)\". A single term in a phenotype controlled " + 
		"vocabulary can be referenced using the xref, or the PhenoXML describing the PATO EQ model " + 
		"phenotype description can be stored as a string in PATO-DATA.");
		
		OntUtil.addTypedComment(schema, PhysicalEntity, 
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
		
		OntUtil.addEnglishComment(schema, physicalEntity, 
		"The physical entity to be annotated with stoichiometry.");
		
		OntUtil.addTypedComment(schema, pMg, 
		"A measure of the concentration of magnesium (Mg) in solution. " + 
		"(pMg = -log<sub>10</sub>[Mg<sup>2+</sup>])");
		
		OntUtil.addTypedComment(schema, positionStatus, 
		"The confidence status of the sequence position. This could be:\n" +
		"EQUAL: The SEQUENCE-POSITION is known to be at the SEQUENCE-POSITION.\n" +
		"GREATER-THAN: The site is greater than the SEQUENCE-POSITION.\n" +
		"LESS-THAN: The site is less than the SEQUENCE-POSITION.");
		
		OntUtil.addTypedComment(schema, product, 
		"The product of a template reaction.");
		
		OntUtil.addTypedComment(schema, Protein, 
		"Definition: A physical entity consisting of a sequence of amino acids; a protein monomer; " + 
		"a single polypeptide chain.\n" + 
		"Examples: The epidermal growth factor receptor (EGFR) protein.");

		OntUtil.addTypedComment(schema, ProteinReference, 
		"A protein reference is a grouping of several protein entities that are encoded " + 
		"by the same gene.  Members can differ in celular location, sequence features and " + 
		"bound partners. Currently conformational states (such as open and closed) are not covered.");

		OntUtil.addTypedComment(schema, Provenance, 
		"Definition: The direct source of a pathway data or score. This does not store the trail of " + 
		"sources from the generation of the data to this point, only the last known source, such as a " + 
		"database. The XREF property may contain a publicationXref referencing a publication " + 
		"describing the data source (e.g. a database publication). A unificationXref may be used e.g. " + 
		"when pointing to an entry in a database of databases describing this database.\n" +
		"Examples: A database, scoring method or person name.");
		
		OntUtil.addTypedComment(schema, PublicationXref, 
		"Definition: An xref that defines a reference to a publication such as a book, journal " + 
		"article, web page, or software manual. The reference may or may not be in a database, " + 
		"although references to PubMed are preferred when possible. The publication should make a " + 
		"direct reference to the instance it is attached to.\n" +
		"Comment: Publication xrefs should make use of PubMed IDs wherever possible. The DB property " + 
		"of an xref to an entry in PubMed should use the string \"PubMed\" and not \"MEDLINE\".\n" +
		"Examples: PubMed:10234245");
		
		OntUtil.addTypedComment(schema, RelationshipTypeVocabulary, 
		"Vocabulary for defining relationship Xref types. A reference to the PSI Molecular " + 
		"Interaction ontology (MI) Cross Reference type. Homepage at http://www.psidev.info/.  " + 
		"Browse at http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI&termId=MI%3A0353&" + 
		"termName=cross-reference%20type");
		
		OntUtil.addTypedComment(schema, RelationshipXref, 
		"Definition: An xref that defines a reference to an entity in an external resource that does " + 
		"not have the same biological identity as the referring entity.\n" +
		"Comment: There is currently no controlled vocabulary of relationship types for BioPAX, " + 
		"although one will be created in the future if a need develops.\n" +
		"Examples: A link between a gene G in a BioPAX data collection, and the protein product P " + 
		"of that gene in an external database. This is not a unification xref because G and P are " + 
		"different biological entities (one is a gene and one is a protein). Another example is a " + 
		"relationship xref for a protein that refers to the Gene Ontology biological process, " + 
		"e.g. 'immune response,' that the protein is involved in.");
		
		OntUtil.addTypedComment(schema, right, 
		"The participants on the right side of the conversion interaction. Since conversion " + 
		"interactions may proceed in either the left-to-right or right-to-left direction, occupants " + 
		"of the RIGHT property may be either reactants or products. RIGHT is a sub-property of " + 
		"PARTICIPANTS.");
		
		OntUtil.addTypedComment(schema, Rna, 
		"Definition: A physical entity consisting of a sequence of ribonucleotide monophosphates; " + 
		"a ribonucleic acid.\n" +
		"Examples: messengerRNA, microRNA, ribosomalRNA. A specific example is the let-7 microRNA.");
		
		OntUtil.addTypedComment(schema, RnaReference, 
		"A RNA  reference is a grouping of several RNA entities that are either encoded by the same " + 
		"gene or replicates of the same genome.  Members can differ in celular location, sequence " + 
		"features and bound partners. Currently conformational states (such as hairpin) are not covered.");
		
		OntUtil.addTypedComment(schema, RnaRegion, 
		"A region of RNA");

		OntUtil.addTypedComment(schema, Score, 
		"Definition: A score associated with a publication reference describing how the score was " + 
		"determined, the name of the method and a comment briefly describing the method. The xref " + 
		"must contain at least one publication that describes the method used to determine the " + 
		"score value. There is currently no standard way of describing  values, so any string is valid.\n" + 
		"Examples: The statistical significance of a result, e.g. \"p<0.05\".");
		
		OntUtil.addEnglishComment(schema, sequence, 
		"Polymer sequence in uppercase letters. For DNA, usually A,C,G,T letters representing the " + 
		"nucleosides of adenine, cytosine, guanine and thymine, respectively; for RNA, " + 
		"usually A, C, U, G; for protein, usually the letters corresponding to the 20 letter IUPAC " + 
		"amino acid code.");
		
		OntUtil.addTypedComment(schema, SequenceInterval, 
		"Definition: Describes an interval on a sequence. All of the sequence from the begin site to " + 
		"the end site (inclusive) is described, not any subset.");
		
		OntUtil.addTypedComment(schema, sequenceIntervalBegin, 
		"The begin position of a sequence interval.");

		OntUtil.addTypedComment(schema, sequenceIntervalEnd, 
		"The end position of a sequence interval.");

		OntUtil.addTypedComment(schema, SequenceLocation, 
		"Definition: A location on a nucleotide or amino acid sequence.\n" +
		"Comment: For organizational purposes only; direct instances of this class should not be created.");
		
		OntUtil.addTypedComment(schema, sequencePosition, 
		"The integer listed gives the position. The first base or amino acid is position 1. " + 
		"In combination with the numeric value, the property 'POSITION-STATUS' allows to express " + 
		"fuzzy positions, e.g. 'less than 4'.");
		
		OntUtil.addTypedComment(schema, SequenceModificationVocabulary, 
		"A reference to the PSI Molecular Interaction ontology (MI) of covalent sequence modifications. " + 
		"Homepage at http://www.psidev.info/.  Browse at " + 
		"http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=MI&termId=MI%3A0252&" + 
		"termName=biological%20feature. Only children that are covelent modifications at specific " + 
		"positions can be used.");
		
		OntUtil.addTypedComment(schema, SequenceRegionVocabulary, 
		"A reference to a controlled vocabulary of sequence regions, such as InterPro or Sequence " + 
		"Ontology (SO). Homepage at http://www.sequenceontology.org/.  " + 
		"Browse at http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=SO");
		
		OntUtil.addTypedComment(schema, SequenceSite, 
		"Definition: Describes a site on a sequence, i.e. the position of a single nucleotide or " + 
		"amino acid.");
		
		OntUtil.addTypedComment(schema, source, 
		"The source  in which the reference was published, such as: a book title, or a journal title " + 
		"and volume and pages.");
		
		OntUtil.addTypedComment(schema, SmallMolecule, 
		"Definition: A small bioactive molecule. Small is not precisely defined, but includes " + 
		"all metabolites and most drugs and does not include large polymers, including complex " + 
		"carbohydrates.\n" +
		"Comment: Recently, a number of small molecule databases have become available to " + 
		"cross-reference from this class.\n" +
		"Examples: glucose, penicillin, phosphatidylinositol\n" +
		"Note: Complex carbohydrates are not currently modeled in BioPAX or most pathway databases, " + 
		"due to the lack of a publicly available complex carbohydrate database."); 
		
		OntUtil.addTypedComment(schema, SmallMoleculeReference, 
		"A small molecule reference is a grouping of several small molecule entities  that have the " + 
		"same chemical structure.  Members can differ in celular location and bound partners. " + 
		"Covalent modifications of small molecules are not considered as state changes but treated " + 
		"as different molecules.");
		
		OntUtil.addTypedComment(schema, spontaneous, 
		"Specifies whether a conversion occurs spontaneously or not. If the spontaneity is not known, " + 
		"the SPONTANEOUS property should be left empty.");
		
		OntUtil.addEnglishComment(schema, standardName, 
		"The preferred full name for this entity.");
		
		OntUtil.addTypedComment(schema, stepConversion, 
		"The central process that take place at this step of the biochemical pathway.");

		OntUtil.addTypedComment(schema, stepDirection, 
		"Direction of the conversion in this particular pathway context. \n" + 
		"This property can be used for annotating direction of enzymatic activity. Even if an enzyme " + 
		"catalyzes a reaction reversibly, the flow of matter through the pathway will force the " + 
		"equilibrium in a given direction for that particular pathway.");
		
		OntUtil.addTypedComment(schema, stepProcess, 
		"An interaction or a pathway that are a part of this pathway step.");
		
		OntUtil.addTypedComment(schema, Stoichiometry, 
		"Stoichiometric coefficient of a physical entity in the context of a conversion or complex.\n" +
		"For each participating element there must be 0 or 1 stoichiometry element. A non-existing " + 
		"stoichiometric element is treated as unknown.\n" +
		"This is an n-ary bridge for left, right and component properties.");
		
		OntUtil.addTypedComment(schema, stoichiometricCoefficient, 
		"Stoichiometric coefficient for one of the entities in an interaction or complex. This value " + 
		"can be any rational number. Generic values such as \"n\" or \"n+1\" should not be used - " + 
		"polymers are currently not covered.");
		
		OntUtil.addEnglishComment(schema, structure, 
		"Defines the chemical structure and other information about this molecule, using an instance " + 
		"of class chemicalStructure.");
		
		OntUtil.addTypedComment(schema, structureData, 
		"This property holds a string of data defining chemical structure or other information, " + 
		"in either the CML or SMILES format, as specified in property Structure-Format. If, " + 
		"for example, the CML format is used, then the value of this property is a string containing " + 
		"the XML encoding of the CML data.");
		
		OntUtil.addEnglishComment(schema, structureFormat, 
		"This property specifies which format is used to define chemical structure data.");
		
		OntUtil.addTypedComment(schema, subRegion, 
		"The sub region of a region. The sub region must be wholly part of the region, not outside of it.");
		
		OntUtil.addTypedComment(schema, taxonXref, 
		"An xref to an organism taxonomy database, preferably NCBI taxon. This should be an instance " + 
		"of unificationXref, unless the organism is not in an existing database.");
		
		OntUtil.addTypedComment(schema, temperature, 
		"Temperature in Celsius");
		
		OntUtil.addTypedComment(schema, template, 
		"The template molecule that is used in this template reaction.");
		
		OntUtil.addTypedComment(schema, templateDirection, 
		"The direction of the template reaction on the template.");

		OntUtil.addTypedComment(schema, TemplateReaction, 
		"Definiton: This class represents a polymerization of a macromolecule from a template. " + 
		"E.g. DNA to RNA is transcription, RNA to protein is translation and DNA to protein is " + 
		"protein expression from DNA. Other examples are possible. To store a promoter region, " + 
		"create a regulatory element and add a promoter feature on it, using the sequence " + 
		"region vocabulary.");
		
		OntUtil.addTypedComment(schema, TemplateReactionRegulation, 
		"Definition: Regulation of the expression reaction by the controlling element such as a " + 
		"transcription factor or microRNA. E.g. To represent the binding of the transcription factor " + 
		"to a regulatory element in the TemplateReaction, create a complex of the transcription factor " + 
		"and the regulatory element and set that as the controller.");

		OntUtil.addTypedComment(schema, term, 
		"The external controlled vocabulary term.");
		
		OntUtil.addEnglishComment(schema, tissue, 
		"An external controlled vocabulary of tissue types.");

		OntUtil.addTypedComment(schema, TissueVocabulary, 
		"A reference to the BRENDA (BTO). Homepage at http://www.brenda-enzymes.info/.  Browse at " + 
		"http://www.ebi.ac.uk/ontology-lookup/browse.do?ontName=BTO");
		
		OntUtil.addEnglishComment(schema, title, 
		"The title of the publication.");
		
		OntUtil.addTypedComment(schema, Transport, 
		"Definition: A conversion interaction in which an entity (or set of entities) changes location " + 
		"within or with respect to the cell. A transport interaction does not include the transporter " + 
		"entity, even if one is required in order for the transport to occur. Instead, transporters " + 
		"are linked to transport interactions via the catalysis class.\n" +
		"Comment: Transport interactions do not involve chemical changes of the participant(s). " + 
		"These cases are handled by the transportWithBiochemicalReaction class.\n" +
		"Synonyms: translocation.\n" +
		"Examples: The movement of Na+ into the cell through an open voltage-gated channel.");
		
		OntUtil.addTypedComment(schema, TransportWithBiochemicalReaction, 
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
		
		OntUtil.addTypedComment(schema, UnificationXref, 
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
		
		OntUtil.addTypedComment(schema, UtilityClass, 
		"Definition: Utility classes are created when simple slots are insufficient to describe " + 
		"an aspect of an entity or to increase compatibility of this ontology with other standards.  " + 
		"The utilityClass class is actually a metaclass and is only present to organize the other " + 
		"helper classes under one class hierarchy; instances of utilityClass should never be created.");
		
		OntUtil.addEnglishComment(schema, url, 
		"The URL at which the publication can be found, if it is available through the Web.");
		
		OntUtil.addTypedComment(schema, value, 
		"The value of the score.");
		
		OntUtil.addTypedComment(schema, Xref, 
		"Definition: A reference from an instance of a class in this ontology to an object in an " + 
		"external resource.\n" +
        "Comment: Instances of the xref class should never be created and more specific classes should " + 
        "be used instead.");
		
		OntUtil.addEnglishComment(schema, xref, 
		"Values of this property define external cross-references from this entity to entities in " + 
		"external databases.");

		OntUtil.addEnglishComment(schema, year, 
		"The year in which this publication was published.");
		
	}

}

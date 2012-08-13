/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.pathway.persistence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.jdom.Element;
import org.jdom.Namespace;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.vcell.pathway.BindingFeature;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.BioSource;
import org.vcell.pathway.BiochemicalPathwayStep;
import org.vcell.pathway.BiochemicalReaction;
import org.vcell.pathway.Catalysis;
import org.vcell.pathway.ChemicalStructure;
import org.vcell.pathway.Complex;
import org.vcell.pathway.ComplexAssembly;
import org.vcell.pathway.Control;
import org.vcell.pathway.ControlledVocabulary;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.CovalentBindingFeature;
import org.vcell.pathway.Degradation;
import org.vcell.pathway.DeltaG;
import org.vcell.pathway.Dna;
import org.vcell.pathway.DnaReference;
import org.vcell.pathway.DnaRegion;
import org.vcell.pathway.DnaRegionReference;
import org.vcell.pathway.Entity;
import org.vcell.pathway.EntityFeature;
import org.vcell.pathway.EntityReference;
import org.vcell.pathway.Evidence;
import org.vcell.pathway.ExperimentalForm;
import org.vcell.pathway.Gene;
import org.vcell.pathway.GeneticInteraction;
import org.vcell.pathway.GroupObject;
import org.vcell.pathway.Interaction;
import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.InteractionVocabulary;
import org.vcell.pathway.KPrime;
import org.vcell.pathway.ModificationFeature;
import org.vcell.pathway.MolecularInteraction;
import org.vcell.pathway.Pathway;
import org.vcell.pathway.PathwayModel;
import org.vcell.pathway.PathwayStep;
import org.vcell.pathway.PhenotypeVocabulary;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.pathway.Protein;
import org.vcell.pathway.ProteinReference;
import org.vcell.pathway.Provenance;
import org.vcell.pathway.PublicationXref;
import org.vcell.pathway.RelationshipTypeVocabulary;
import org.vcell.pathway.RelationshipXref;
import org.vcell.pathway.Rna;
import org.vcell.pathway.RnaReference;
import org.vcell.pathway.RnaRegion;
import org.vcell.pathway.RnaRegionReference;
import org.vcell.pathway.Score;
import org.vcell.pathway.SequenceInterval;
import org.vcell.pathway.SequenceLocation;
import org.vcell.pathway.SequenceRegionVocabulary;
import org.vcell.pathway.SequenceSite;
import org.vcell.pathway.SmallMolecule;
import org.vcell.pathway.SmallMoleculeReference;
import org.vcell.pathway.Stoichiometry;
import org.vcell.pathway.TemplateReaction;
import org.vcell.pathway.TemplateReactionRegulation;
import org.vcell.pathway.TransportWithBiochemicalReaction;
import org.vcell.pathway.Xref;
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.pathway.sbpax.SBEntityImpl;
import org.vcell.pathway.sbpax.SBMeasurable;
import org.vcell.pathway.sbpax.SBVocabulary;
import org.vcell.pathway.sbpax.UnitOfMeasurement;

import org.vcell.pathway.id.URIUtil;
import static org.vcell.pathway.PathwayXMLHelper.*;

public class PathwayProducerBiopax3 {

	public Element biopaxElement = null;
	protected final RDFXMLContext context;
	private static final Namespace bp = Namespace.getNamespace("bp", "http://www.biopax.org/release/biopax-level3.owl#");
	private static final Namespace rdf = Namespace.getNamespace("rdf", DefaultNameSpaces.RDF.uri);
	private static final Namespace sbx3 = Namespace.getNamespace("sbx3", "http://vcell.org/sbpax3#");

	private HashSet<BioPaxObject> objectsPrinted = new HashSet<BioPaxObject>();
	private HashSet<BioPaxObject> objectsToPrint = new HashSet<BioPaxObject>();
	
	public void mustPrintObject(BioPaxObject bioPaxObject){
		if(!bioPaxObject.hasID()) {
			return;
		}
		if (objectsPrinted.contains(bioPaxObject)){
			return;
		}
		objectsToPrint.add(bioPaxObject);
	}
	
	public PathwayProducerBiopax3(RDFXMLContext context) {
		this.context = context;
	}

	public void getXML(PathwayModel pathwayModel, Element rootElement) {
		objectsToPrint.addAll(pathwayModel.getBiopaxObjects());
		
		biopaxElement = rootElement;
		biopaxElement.addNamespaceDeclaration(bp);
		biopaxElement.addNamespaceDeclaration(rdf);
		
		if(pathwayModel.getDiagramObjects().size() > 0) {
			for(String diagramObjectID : pathwayModel.getDiagramObjects()) {
				if(diagramObjectID != null) {
					Element e = new Element("DiagramObjectsID");
					e.setText(diagramObjectID);
					biopaxElement.addContent(e);
				}
			}
		}

		while (objectsToPrint.size()>0){
			BioPaxObject bpObject = objectsToPrint.iterator().next();
			if(bpObject == null) {
				System.out.println("PathwayProducerBiopax3: null BioPaxObject in the PathwayModel.");
				continue;
			}
			
			String className = bpObject.getClass().getName().substring(1+bpObject.getClass().getName().lastIndexOf('.'));
//			System.out.println("object: " + className);
			if(className.equals("Test")){
				System.out.println("PathwayProducerBiopax3::getXML()");
			// sbPAX section
			}else if (className.equals("SBMeasurable")){
				biopaxElement.addContent(addObjectSBMeasurable(bpObject, className));
			}else if (className.equals("SBState")){
				biopaxElement.addContent(addObjectSBState(bpObject, className));
			}else if (className.equals("SBVocabulary")){
				biopaxElement.addContent(addObjectSBVocabulary(bpObject, className));
			// bioPAX section
			}else if (className.equals("Pathway")){
				biopaxElement.addContent(addObjectPathway(bpObject, className));
			}else if (className.equals("BiochemicalReactionImpl")){
				biopaxElement.addContent(addObjectBiochemicalReaction(bpObject, "BiochemicalReaction"));
			}else if (className.equals("ConversionImpl")){
				biopaxElement.addContent(addObjectConversion(bpObject, "Conversion"));
			}else if (className.equals("CellularLocationVocabulary")){
				biopaxElement.addContent(addObjectCellularLocationVocabulary(bpObject, className));
			}else if (className.equals("CellVocabulary")){
				biopaxElement.addContent(addObjectCellVocabulary(bpObject, className));
			}else if (className.equals("EntityReferenceTypeVocabulary")){
				biopaxElement.addContent(addObjectEntityReferenceTypeVocabulary(bpObject, className));
			}else if (className.equals("EvidenceCodeVocabulary")){
				biopaxElement.addContent(addObjectEvidenceCodeVocabulary(bpObject, className));
			}else if (className.equals("ExperimentalFormVocabulary")){
				biopaxElement.addContent(addObjectExperimentalFormVocabulary(bpObject, className));
			}else if (className.equals("InteractionVocabulary")){
				biopaxElement.addContent(addObjectInteractionVocabulary(bpObject, className));
			}else if (className.equals("PhenotypeVocabulary")){
				biopaxElement.addContent(addObjectPhenotypeVocabulary(bpObject, className));
			}else if (className.equals("RelationshipTypeVocabulary")){
				biopaxElement.addContent(addObjectRelationshipTypeVocabulary(bpObject, className));
			}else if (className.equals("SequenceModificationVocabulary")){
				biopaxElement.addContent(addObjectSequenceModificationVocabulary(bpObject, className));
			}else if (className.equals("SequenceRegionVocabulary")){
				biopaxElement.addContent(addObjectSequenceRegionVocabulary(bpObject, className));
			}else if (className.equals("TissueVocabulary")){
				biopaxElement.addContent(addObjectTissueVocabulary(bpObject, className));
			}else if (className.equals("PublicationXref")){
				biopaxElement.addContent(addObjectPublicationXref(bpObject, className));
			}else if (className.equals("RelationshipXref")){
				biopaxElement.addContent(addObjectRelationshipXref(bpObject, className));
			}else if (className.equals("UnificationXref")){
 				biopaxElement.addContent(addObjectUnificationXref(bpObject, className));
			}else if (className.equals("DnaReference")){
				biopaxElement.addContent(addObjectDnaReference(bpObject, className));
			}else if (className.equals("DnaRegionReference")){
				biopaxElement.addContent(addObjectDnaRegionReference(bpObject, className));
			}else if (className.equals("ProteinReference")){
				biopaxElement.addContent(addObjectProteinReference(bpObject, className));
			}else if (className.equals("RnaReference")){
				biopaxElement.addContent(addObjectRnaReference(bpObject, className));
			}else if (className.equals("RnaRegionReference")){
				biopaxElement.addContent(addObjectRnaRegionReference(bpObject, className));
			}else if (className.equals("SmallMoleculeReference")){
				biopaxElement.addContent(addObjectSmallMoleculeReference(bpObject, className));
			}else if (className.equals("Provenance")){
				biopaxElement.addContent(addObjectProvenance(bpObject, className));
			}else if (className.equals("Control")){
				biopaxElement.addContent(addObjectControl(bpObject, className));
			}else if (className.equals("Catalysis")){
				biopaxElement.addContent(addObjectCatalysis(bpObject, className));
			}else if (className.equals("Modulation")){
				biopaxElement.addContent(addObjectModulation(bpObject, className));
			}else if (className.equals("TemplateReactionRegulation")){
				biopaxElement.addContent(addObjectTemplateReactionRegulation(bpObject, className));
			}else if (className.equals("Complex")){
				biopaxElement.addContent(addObjectComplex(bpObject, className));
			}else if (className.equals("Protein")){
				biopaxElement.addContent(addObjectProtein(bpObject, className));
			}else if (className.equals("Dna")){
				biopaxElement.addContent(addObjectDna(bpObject, className));
			}else if (className.equals("DnaRegion")){
				biopaxElement.addContent(addObjectDnaRegion(bpObject, className));
			}else if (className.equals("Rna")){
				biopaxElement.addContent(addObjectRna(bpObject, className));
			}else if (className.equals("RnaRegion")){
				biopaxElement.addContent(addObjectRnaRegion(bpObject, className));
			}else if (className.equals("SmallMolecule")){
				biopaxElement.addContent(addObjectSmallMolecule(bpObject, className));
			}else if (className.equals("BioSource")){
				biopaxElement.addContent(addObjectBioSource(bpObject, className));
			}else if (className.equals("EntityFeature")){
				biopaxElement.addContent(addObjectEntityFeature(bpObject, className));
			}else if (className.equals("ModificationFeature")){
				biopaxElement.addContent(addObjectModificationFeature(bpObject, className));
			}else if (className.equals("CovalentBindingFeature")){
				biopaxElement.addContent(addObjectCovalentBindingFeature(bpObject, className));
			}else if (className.equals("FragmentFeature")){
				biopaxElement.addContent(addObjectFragmentFeature(bpObject, className));
			}else if (className.equals("BindingFeature")){
				biopaxElement.addContent(addObjectBindingFeature(bpObject, className));
			}else if (className.equals("SequenceInterval")){
				biopaxElement.addContent(addObjectSequenceInterval(bpObject, className));
			}else if (className.equals("SequenceSite")){
				biopaxElement.addContent(addObjectSequenceSite(bpObject, className));
			}else if (className.equals("Stoichiometry")){
				biopaxElement.addContent(addObjectStoichiometry(bpObject, className));
			}else if (className.equals("PathwayStep")){
				biopaxElement.addContent(addObjectPathwayStep(bpObject, className));
			}else if (className.equals("BiochemicalPathwayStep")){
				biopaxElement.addContent(addObjectBiochemicalPathwayStep(bpObject, className));
			}else if (className.equals("PhysicalEntity")){
				biopaxElement.addContent(addObjectPhysicalEntity(bpObject, className));
//
//			// not found "as is" in tested sample
			}else if (className.equals("Gene")){
				biopaxElement.addContent(addObjectGene(bpObject, className));
			}else if (className.equals("Ontology")){
				//showIgnored(bpObject, className);
			}else if (className.equals("Interaction")){
				biopaxElement.addContent(addObjectInteraction(bpObject, className));
			}else if (className.equals("Transport")){
				biopaxElement.addContent(addObjectTransport(bpObject, className));
			}else if (className.equals("TransportWithBiochemicalReaction")){
				biopaxElement.addContent(addObjectTransportWithBiochemicalReaction(bpObject, className));
			}else if (className.equals("GeneticInteraction")){
				biopaxElement.addContent(addObjectGeneticInteraction(bpObject, className));
			}else if (className.equals("MolecularInteraction")){
				biopaxElement.addContent(addObjectMolecularInteraction(bpObject, className));
			}else if (className.equals("TemplateReaction")){
				biopaxElement.addContent(addObjectTemplateReaction(bpObject, className));
			}else if (className.equals("ComplexAssembly")){
				biopaxElement.addContent(addObjectComplexAssembly(bpObject, className));
			}else if (className.equals("Degradation")){
				biopaxElement.addContent(addObjectDegradation(bpObject, className));
			}else if (className.equals("ChemicalStructure")){
				biopaxElement.addContent(addObjectChemicalStructure(bpObject, className));
			}else if (className.equals("DeltaG")){
				biopaxElement.addContent(addObjectDeltaG(bpObject, className));
			}else if (className.equals("Evidence")){
				biopaxElement.addContent(addObjectEvidence(bpObject, className));
			}else if (className.equals("ExperimentalForm")){
				biopaxElement.addContent(addObjectExperimentalForm(bpObject, className));
			}else if (className.equals("KPrime")){
				biopaxElement.addContent(addObjectKPrime(bpObject, className));
			}else if (className.equals("Score")){
				biopaxElement.addContent(addObjectScore(bpObject, className));
			}else if (className.equals("GroupObject")){
				biopaxElement.addContent(addObjectGroupObject(bpObject, className));
			}else{
				showUnexpected(bpObject);
			}
			objectsPrinted.add(bpObject);
			objectsToPrint.remove(bpObject);
		}
		return;
	}

	private Element addObjectSBVocabulary(BioPaxObject bpObject, String className) {
		Element element = new Element(className, sbx3);
		element = addAttributes(bpObject, element);
		element = addContentSBVocabulary(bpObject, element);
		return element;
	}
	private Element addObjectSBState(BioPaxObject bpObject, String className) {
		Element element = new Element(className, sbx3);
		element = addAttributes(bpObject, element);
		element = addContentSBState(bpObject, element);
		return element;
	}
	private Element addObjectSBMeasurable(BioPaxObject bpObject, String className) {
		Element element = new Element(className, sbx3);
		element = addAttributes(bpObject, element);
		element = addContentSBMeasurable(bpObject, element);
		return element;
	}

	private Element addObjectScore(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentScore(bpObject, element);
		return element;
	}

	private Element addObjectKPrime(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentKPrime(bpObject, element);
		return element;
	}

	private Element addObjectExperimentalForm(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentExperimentalForm(bpObject, element);
		return element;
	}

	private Element addObjectEvidence(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentEvidence(bpObject, element);
		return element;
	}

	private Element addObjectEntityFeature(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentEntityFeature(bpObject, element);
		return element;
	}
	private Element addObjectFragmentFeature(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentFragmentFeature(bpObject, element);
		return element;
	}
	private Element addObjectCovalentBindingFeature(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentCovalentBindingFeature(bpObject, element);
		return element;
	}
	private Element addObjectModificationFeature(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentModificationFeature(bpObject, element);
		return element;
	}
	private Element addObjectBindingFeature(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentBindingFeature(bpObject, element);
		return element;
	}


	private Element addObjectDeltaG(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentDeltaG(bpObject, element);
		return element;
	}

	private Element addObjectChemicalStructure(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentChemicalStructure(bpObject, element);
		return element;
	}

	private Element addObjectDegradation(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentDegradation(bpObject, element);
		return element;
	}

	private Element addObjectComplexAssembly(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentComplexAssembly(bpObject, element);
		return element;
	}

	private Element addObjectTemplateReaction(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentTemplateReaction(bpObject, element);
		return element;
	}

	private Element addObjectMolecularInteraction(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentMolecularInteraction(bpObject, element);
		return element;
	}

	private Element addObjectGeneticInteraction(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentGeneticInteraction(bpObject, element);
		return element;
	}

	private Element addObjectTransportWithBiochemicalReaction(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentTransportWithBiochemicalReaction(bpObject, element);
		return element;
	}
	private Element addObjectTransport(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentTransport(bpObject, element);
		return element;
	}

	private Element addObjectInteraction(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentInteraction(bpObject, element);
		return element;
	}

	private Element addObjectGene(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentGene(bpObject, element);
		return element;
	}

	private Element addObjectPhysicalEntity(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentPhysicalEntity(bpObject, element);
		return element;
	}

	private Element addObjectPathwayStep(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentPathwayStep(bpObject, element);
		return element;
	}
	private Element addObjectBiochemicalPathwayStep(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentBiochemicalPathwayStep(bpObject, element);
		return element;
	}

	private Element addObjectStoichiometry(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentStoichiometry(bpObject, element);
		return element;
	}

	private Element addObjectSequenceSite(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentSequenceSite(bpObject, element);
		return element;
	}
	private Element addObjectSequenceInterval(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentSequenceInterval(bpObject, element);
		return element;
	}

	private Element addObjectBioSource(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentBioSource(bpObject, element);
		return element;
	}

	private Element addObjectSmallMolecule(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentSmallMolecule(bpObject, element);
		return element;
	}
	private Element addObjectRnaRegion(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentRnaRegion(bpObject, element);
		return element;
	}
	private Element addObjectRna(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentRna(bpObject, element);
		return element;
	}
	private Element addObjectDnaRegion(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentDnaRegion(bpObject, element);
		return element;
	}
	private Element addObjectDna(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentDna(bpObject, element);
		return element;
	}
	private Element addObjectProtein(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentProtein(bpObject, element);
		return element;
	}
	private Element addObjectComplex(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentComplex(bpObject, element);
		return element;
	}

	private Element addObjectGroupObject(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentGroupObject(bpObject, element);
		return element;
	}
	
	private Element addObjectTemplateReactionRegulation(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentTemplateReactionRegulation(bpObject, element);
		return element;
	}
	private Element addObjectModulation(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentModulation(bpObject, element);
		return element;
	}
	private Element addObjectCatalysis(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentCatalysis(bpObject, element);
		return element;
	}
	private Element addObjectControl(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentControl(bpObject, element);
		return element;
	}

	private Element addObjectProvenance(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentProvenance(bpObject, element);
		return element;
	}

	private Element addObjectDnaReference(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentDnaReference(bpObject, element);
		return element;
	}
	private Element addObjectDnaRegionReference(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentDnaRegionReference(bpObject, element);
		return element;
	}
	private Element addObjectProteinReference(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentProteinReference(bpObject, element);
		return element;
	}
	private Element addObjectRnaReference(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentRnaReference(bpObject, element);
		return element;
	}
	private Element addObjectRnaRegionReference(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentRnaRegionReference(bpObject, element);
		return element;
	}
	private Element addObjectSmallMoleculeReference(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentSmallMoleculeReference(bpObject, element);
		return element;
	}


	private Element addObjectUnificationXref(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentUnificationXref(bpObject, element);
		return element;
	}
	private Element addObjectRelationshipXref(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentRelationshipXref(bpObject, element);
		return element;
	}
	private Element addObjectPublicationXref(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentPublicationXref(bpObject, element);
		return element;
	}

	private Element addObjectTissueVocabulary(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentTissueVocabulary(bpObject, element);
		return element;
	}

	private Element addObjectSequenceRegionVocabulary(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentSequenceRegionVocabulary(bpObject, element);
		return element;
	}

	private Element addObjectSequenceModificationVocabulary(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentSequenceModificationVocabulary(bpObject, element);
		return element;
	}

	private Element addObjectRelationshipTypeVocabulary(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentRelationshipTypeVocabulary(bpObject, element);
		return element;
	}

	private Element addObjectPhenotypeVocabulary(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentPhenotypeVocabulary(bpObject, element);
		return element;
	}

	private Element addObjectInteractionVocabulary(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentInteractionVocabulary(bpObject, element);
		return element;
	}

	private Element addObjectExperimentalFormVocabulary(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentExperimentalFormVocabulary(bpObject, element);
		return element;
	}

	private Element addObjectEvidenceCodeVocabulary(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentEvidenceCodeVocabulary(bpObject, element);
		return element;
	}

	private Element addObjectEntityReferenceTypeVocabulary(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentEntityReferenceTypeVocabulary(bpObject, element);
		return element;
	}

	private Element addObjectCellVocabulary(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentCellVocabulary(bpObject, element);
		return element;
	}

	private Element addObjectCellularLocationVocabulary(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentCellularLocationVocabulary(bpObject, element);
		return element;
	}

	private Element addObjectBiochemicalReaction(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentBiochemicalReaction(bpObject, element);
		return element;
	}
	private Element addObjectConversion(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentConversion(bpObject, element);
		return element;
	}

	private Element addObjectPathway(BioPaxObject bpObject, String className) {
		Element element = new Element(className, bp);
		element = addAttributes(bpObject, element);
		element = addContentPathway(bpObject, element);
		return element;
	}
	
	//
	// -------------------  addContent...() group  -------------------------
	//
	
	private Element addContentSBVocabulary(BioPaxObject bpObject, Element element) {
		element = addContentControlledVocabulary(bpObject, element);
		return element;
	}
	private Element addContentSBState(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		
		ExperimentalForm ob = (ExperimentalForm)bpObject;
		Element tmpElement = null;

		return element;
	}
	private Element addContentSBMeasurable(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		
		SBMeasurable ob = (SBMeasurable)bpObject;
		Element tmpElement = null;

		if(ob.getUnit() != null && ob.getUnit().size() > 0) {
			List<UnitOfMeasurement> list = ob.getUnit();
			for(UnitOfMeasurement item : list) {
				tmpElement = new Element("hasUnit", sbx3);
				String id = item.getID();
				if(URIUtil.isAbsoluteURI(id)) {
					tmpElement.setAttribute("resource", context.relativizeURI(tmpElement, id), rdf);						
				} else {
					tmpElement.setAttribute("nodeID", id, rdf);												
				}
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getSBTerm() != null && ob.getSBTerm().size() > 0) {
			List<SBVocabulary> list = ob.getSBTerm();
			for(SBVocabulary item : list) {
				tmpElement = new Element("sbTerm", sbx3);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getNumber() != null && ob.getNumber().size() > 0) {
			List<Double> list = ob.getNumber();
			for(Double item : list) {
				tmpElement = new Element("hasNumber", sbx3);
				tmpElement.setAttribute("datatype", schemaDouble, rdf);
				tmpElement.setText(item.toString());
				element.addContent(tmpElement);
			}
		}
		return element;
	}

	private Element addContentScore(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		
		Score ob = (Score)bpObject;
		Element tmpElement = null;

		return element;
	}
	private Element addContentKPrime(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		
		KPrime ob = (KPrime)bpObject;
		Element tmpElement = null;

		return element;
	}

	private Element addContentExperimentalForm(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		
		ExperimentalForm ob = (ExperimentalForm)bpObject;
		Element tmpElement = null;

		return element;
	}
	private Element addContentEvidence(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		
		Evidence ob = (Evidence)bpObject;
		Element tmpElement = null;

		return element;
	}

	private Element addContentDeltaG(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		
		DeltaG ob = (DeltaG)bpObject;
		Element tmpElement = null;

		return element;
	}

	private Element addContentChemicalStructure(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		
		ChemicalStructure ob = (ChemicalStructure)bpObject;
		Element tmpElement = null;

		return element;
	}

	private Element addContentDegradation(BioPaxObject bpObject, Element element) {
		element = addContentConversion(bpObject, element);
		
		Degradation ob = (Degradation)bpObject;
		Element tmpElement = null;

		return element;
	}

	private Element addContentComplexAssembly(BioPaxObject bpObject, Element element) {
		element = addContentConversion(bpObject, element);
		
		ComplexAssembly ob = (ComplexAssembly)bpObject;
		Element tmpElement = null;

		return element;
	}

	private Element addContentTemplateReaction(BioPaxObject bpObject, Element element) {
		element = addContentInteraction(bpObject, element);
		
		TemplateReaction ob = (TemplateReaction)bpObject;
		Element tmpElement = null;

		return element;
	}

	private Element addContentMolecularInteraction(BioPaxObject bpObject, Element element) {
		element = addContentInteraction(bpObject, element);
		
		MolecularInteraction ob = (MolecularInteraction)bpObject;
		Element tmpElement = null;

		return element;
	}

	private Element addContentGeneticInteraction(BioPaxObject bpObject, Element element) {
		element = addContentInteraction(bpObject, element);
		
		GeneticInteraction ob = (GeneticInteraction)bpObject;
		Element tmpElement = null;

		return element;
	}

	//	deltaG 		DeltaG 		multiple 
	//	kEQ 		KPrime 		multiple 
	//	eCNumber 	String 		multiple 
	//	deltaS 		Float 		multiple 
	//	deltaH 		Float 		multiple 
	private Element addContentTransportWithBiochemicalReaction( BioPaxObject bpObject, Element element) {
//		element = addContentTransport(bpObject, element);
		element = addContentBiochemicalReaction(bpObject, element);
		
		TransportWithBiochemicalReaction ob = (TransportWithBiochemicalReaction)bpObject;
		Element tmpElement = null;

		if(ob.getDeltaG() != null && ob.getDeltaG().size() > 0) {
			List<DeltaG> list = ob.getDeltaG();
			for(DeltaG item : list) {
				tmpElement = new Element("deltaG", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getkEQ() != null && ob.getkEQ().size() > 0) {
			List<KPrime> list = ob.getkEQ();
			for(KPrime item : list) {
				tmpElement = new Element("kEQ", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getECNumber() != null && ob.getECNumber().size() > 0) {
			List<String> list = ob.getECNumber();
			for(String item : list) {
				tmpElement = new Element("eCNumber", bp);
				tmpElement.setAttribute("datatype", schemaString, rdf);
				tmpElement.setText(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getDeltaS() != null && ob.getDeltaS().size() > 0) {
			List<Double> list = ob.getDeltaS();
			for(Double item : list) {
				tmpElement = new Element("deltaS", bp);
				tmpElement.setAttribute("datatype", schemaDouble, rdf);
				tmpElement.setText(item.toString());
				element.addContent(tmpElement);
			}
		}
		if(ob.getDeltaH() != null && ob.getDeltaH().size() > 0) {
			List<Double> list = ob.getDeltaH();
			for(Double item : list) {
				tmpElement = new Element("deltaH", bp);
				tmpElement.setAttribute("datatype", schemaDouble, rdf);
				tmpElement.setText(item.toString());
				element.addContent(tmpElement);
			}
		}
		return element;
	}
	private Element addContentTransport(BioPaxObject bpObject, Element element) {
		element = addContentConversion(bpObject, element);
		return element;
	}

//	organism BioSource single 
	private Element addContentGene(BioPaxObject bpObject, Element element) {
		element = addContentEntity(bpObject, element);
		
		Gene ob = (Gene)bpObject;
		Element tmpElement = null;

		if(ob.getOrganism() != null) {
			tmpElement = new Element("organism", bp);
			addIDToProperty(tmpElement, ob.getOrganism());
			mustPrintObject(ob.getOrganism());
			element.addContent(tmpElement);
		}
		return element;
	}

//	stepProcess 	Interaction 	multiple 
//	stepProcess 	Pathway 		multiple 
//	nextStep 		PathwayStep 	multiple 
//	evidence 		Evidence 		multiple 
	private Element addContentPathwayStep(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		
		PathwayStep ob = (PathwayStep)bpObject;
		Element tmpElement = null;

		if(ob.getStepProcessInteraction() != null && ob.getStepProcessInteraction().size() > 0) {
			List<Interaction> list = ob.getStepProcessInteraction();
			for(Interaction item : list) {
				tmpElement = new Element("stepProcess", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getStepProcessPathway() != null && ob.getStepProcessPathway().size() > 0) {
			List<Pathway> list = ob.getStepProcessPathway();
			for(Pathway item : list) {
				tmpElement = new Element("stepProcess", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getNextStep() != null && ob.getNextStep().size() > 0) {
			List<PathwayStep> list = ob.getNextStep();
			for(PathwayStep item : list) {
				tmpElement = new Element("nextStep", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getEvidence() != null && ob.getEvidence().size() > 0) {
			List<Evidence> list = ob.getEvidence();
			for(Evidence item : list) {
				tmpElement = new Element("evidence", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		return element;
	}
//	stepConversion 	Conversion 	single 
//	stepDirection 	String 		single 
	private Element addContentBiochemicalPathwayStep(BioPaxObject bpObject, Element element) {
		element = addContentPathwayStep(bpObject, element);
		
		BiochemicalPathwayStep ob = (BiochemicalPathwayStep)bpObject;
		Element tmpElement = null;

		if(ob.getStepConversion() != null) {
			tmpElement = new Element("stepConversion", bp);
			addIDToProperty(tmpElement, ob.getStepConversion());
			mustPrintObject(ob.getStepConversion());
			element.addContent(tmpElement);
		}
		if(ob.getStepDirection() != null && ob.getStepDirection().length() > 0) {
			tmpElement = new Element("stepDirection", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getStepDirection());
			element.addContent(tmpElement);
		}
		return element;
	}

//	physicalEntity 				PhysicalEntity 	single 
//	stoichiometricCoefficient 	Float 			single 
	private Element addContentStoichiometry(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		
		Stoichiometry ob = (Stoichiometry)bpObject;
		Element tmpElement = null;

		if(ob.getPhysicalEntity() != null) {
			tmpElement = new Element("physicalEntity", bp);
			addIDToProperty(tmpElement, ob.getPhysicalEntity());
			mustPrintObject(ob.getPhysicalEntity());
			element.addContent(tmpElement);
		}
		if(ob.getStoichiometricCoefficient() != null) {
			tmpElement = new Element("stoichiometricCoefficient", bp);
			tmpElement.setAttribute("datatype", schemaDouble, rdf);
			tmpElement.setText(ob.getStoichiometricCoefficient().toString());
			element.addContent(tmpElement);
		}
		return element;
	}

	//	sequencePosition 	Integer 	single 
	//	positionStatus 		String 		single 
	private Element addContentSequenceSite(BioPaxObject bpObject, Element element) {
		element = addContentSequenceLocation(bpObject, element);
		
		SequenceSite ob = (SequenceSite)bpObject;
		Element tmpElement = null;

		if(ob.getSequencePosition() != null) {
			tmpElement = new Element("sequencePosition", bp);
			tmpElement.setAttribute("datatype", schemaInt, rdf);
			tmpElement.setText(ob.getSequencePosition().toString());
			element.addContent(tmpElement);
		}
		if(ob.getPositionStatus() != null && ob.getPositionStatus().length() > 0) {
			tmpElement = new Element("positionStatus", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getPositionStatus());
			element.addContent(tmpElement);
		}
		return element;
	}
	//	sequenceIntervalBegin 	SequenceSite 	single 
	//	sequenceIntervalEnd 	SequenceSite 	single 
	private Element addContentSequenceInterval(BioPaxObject bpObject, Element element) {
		element = addContentSequenceLocation(bpObject, element);
		
		SequenceInterval ob = (SequenceInterval)bpObject;
		Element tmpElement = null;
		
		if(ob.getSequenceIntervalBegin() != null) {
			tmpElement = new Element("sequenceIntervalBegin", bp);
			addIDToProperty(tmpElement, ob.getSequenceIntervalBegin());
			mustPrintObject(ob.getSequenceIntervalBegin());
			element.addContent(tmpElement);
		}
		if(ob.getSequenceIntervalEnd() != null) {
			tmpElement = new Element("sequenceIntervalEnd", bp);
			addIDToProperty(tmpElement, ob.getSequenceIntervalEnd());
			mustPrintObject(ob.getSequenceIntervalEnd());
			element.addContent(tmpElement);
		}
		return element;
	}
	private Element addContentSequenceLocation(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		return element;
	}

	//	featureLocation 		SequenceLocation 			multiple 
	//	featureLocationType 	SequenceRegionVocabulary 	multiple 
	//	memberFeature 			EntityFeature 				multiple 
	//	evidence 				Evidence 					multiple 
	private Element addContentEntityFeature(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		
		EntityFeature ob = (EntityFeature)bpObject;
		Element tmpElement = null;
		
		if(ob.getFeatureLocation() != null && ob.getFeatureLocation().size() > 0) {
			List<SequenceLocation> list = ob.getFeatureLocation();
			for(SequenceLocation item : list) {
				tmpElement = new Element("featureLocation", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getFeatureLocationType() != null && ob.getFeatureLocationType().size() > 0) {
			List<SequenceRegionVocabulary> list = ob.getFeatureLocationType();
			for(SequenceRegionVocabulary item : list) {
				tmpElement = new Element("featureLocationType", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getMemberFeature() != null && ob.getMemberFeature().size() > 0) {
			List<EntityFeature> list = ob.getMemberFeature();
			for(EntityFeature item : list) {
				tmpElement = new Element("memberFeature", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getEvidence() != null && ob.getEvidence().size() > 0) {
			List<Evidence> list = ob.getEvidence();
			for(Evidence item : list) {
				tmpElement = new Element("evidence", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		return element;
	}
	private Element addContentFragmentFeature(BioPaxObject bpObject, Element element) {
		element = addContentEntityFeature(bpObject, element);
		return element;
	}
	// if child of ModificationFeature element:
	//		intraMolecular 	Boolean 						single 
	//		bindsTo 		BindingFeature 					single 
	// if child of BindingFeature element:
	//	modificationType 	SequenceModificationVocabulary 	single 
	private Element addContentCovalentBindingFeature(BioPaxObject bpObject, Element element) {
		// we fill all 3 fields regardless of parent, final object will be valid either way
		element = addContentEntityFeature(bpObject, element);
		
		CovalentBindingFeature ob = (CovalentBindingFeature)bpObject;
		Element tmpElement = null;
		
		if(ob.getIntraMolecular() != null) {
			tmpElement = new Element("intraMolecular", bp);
			tmpElement.setAttribute("datatype", schemaBoolean, rdf);
			tmpElement.setText(ob.getIntraMolecular().toString());
			element.addContent(tmpElement);
		}
		if(ob.getBindsTo() != null) {
			tmpElement = new Element("bindsTo", bp);
			addIDToProperty(tmpElement, ob.getBindsTo());
			mustPrintObject(ob.getBindsTo());
			element.addContent(tmpElement);
		}
		if(ob.getModificationType() != null) {
			tmpElement = new Element("modificationType", bp);
			addIDToProperty(tmpElement, ob.getModificationType());
			mustPrintObject(ob.getModificationType());
			element.addContent(tmpElement);
		}
		return element;
	}
	//	modificationType 	SequenceModificationVocabulary 	single 
	private Element addContentModificationFeature(BioPaxObject bpObject, Element element) {
		element = addContentEntityFeature(bpObject, element);
		
		ModificationFeature ob = (ModificationFeature)bpObject;
		Element tmpElement = null;
		
		if(ob.getModificationType() != null) {
			tmpElement = new Element("modificationType", bp);
			addIDToProperty(tmpElement, ob.getModificationType());
			mustPrintObject(ob.getModificationType());
			element.addContent(tmpElement);
		}
		return element;
	}
	//	intraMolecular 	Boolean 		single 
	//	bindsTo 		BindingFeature 	single 
	private Element addContentBindingFeature(BioPaxObject bpObject, Element element) {
		element = addContentEntityFeature(bpObject, element);
		
		BindingFeature ob = (BindingFeature)bpObject;
		Element tmpElement = null;
		
		if(ob.getIntraMolecular() != null) {
			tmpElement = new Element("intraMolecular", bp);
			tmpElement.setAttribute("datatype", schemaBoolean, rdf);
			tmpElement.setText(ob.getIntraMolecular().toString());
			element.addContent(tmpElement);
		}
		if(ob.getBindsTo() != null) {
			tmpElement = new Element("bindsTo", bp);
			addIDToProperty(tmpElement, ob.getBindsTo());
			mustPrintObject(ob.getBindsTo());
			element.addContent(tmpElement);
		}
		return element;
	}

	//	tissue 		TissueVocabulary 	single 
	//	cellType 	CellVocabulary 		single 
	//	name 		String 				multiple 
	//	xref 		Xref 				multiple 
	private Element addContentBioSource(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		
		BioSource ob = (BioSource)bpObject;
		Element tmpElement = null;
		
		if(ob.getTissue() != null) {
			tmpElement = new Element("tissue", bp);
			addIDToProperty(tmpElement, ob.getTissue());
			mustPrintObject(ob.getTissue());
			element.addContent(tmpElement);
		}
		if(ob.getCellType() != null) {
			tmpElement = new Element("cellType", bp);
			addIDToProperty(tmpElement, ob.getCellType());
			mustPrintObject(ob.getCellType());
			element.addContent(tmpElement);
		}
		if(ob.getName() != null && ob.getName().size() > 0) {
			List<String> list = ob.getName();
			for(String item : list) {
				tmpElement = new Element("name", bp);
				tmpElement.setAttribute("datatype", schemaString, rdf);
				tmpElement.setText(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getxRef() != null && ob.getxRef().size() > 0) {
			List<Xref> list = ob.getxRef();
			for(Xref item : list) {
				tmpElement = new Element("xref", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		return element;
	}

//	entityReference 	EntityReference 	single 
	private Element addContentSmallMolecule(BioPaxObject bpObject, Element element) {
		element = addContentPhysicalEntity(bpObject, element);
		
		SmallMolecule ob = (SmallMolecule)bpObject;
		Element tmpElement = null;
		
		if(ob.getEntityReference() != null) {
			tmpElement = new Element("entityReference", bp);
			addIDToProperty(tmpElement, ob.getEntityReference());
			mustPrintObject(ob.getEntityReference());
			element.addContent(tmpElement);
		}
		return element;
	}
	//	entityReference 	EntityReference 	single 
	private Element addContentRnaRegion(BioPaxObject bpObject, Element element) {
		element = addContentPhysicalEntity(bpObject, element);
		
		RnaRegion ob = (RnaRegion)bpObject;
		Element tmpElement = null;
		
		if(ob.getEntityReference() != null) {
			tmpElement = new Element("entityReference", bp);
			addIDToProperty(tmpElement, ob.getEntityReference());
			mustPrintObject(ob.getEntityReference());
			element.addContent(tmpElement);
		}
		return element;
	}
	//	entityReference 	EntityReference 	single 
	private Element addContentRna(BioPaxObject bpObject, Element element) {
		element = addContentPhysicalEntity(bpObject, element);
		
		Rna ob = (Rna)bpObject;
		Element tmpElement = null;
		
		if(ob.getEntityReference() != null) {
			tmpElement = new Element("entityReference", bp);
			addIDToProperty(tmpElement, ob.getEntityReference());
			mustPrintObject(ob.getEntityReference());
			element.addContent(tmpElement);
		}
		return element;
	}
	//	entityReference 	EntityReference 	single 
	private Element addContentDnaRegion(BioPaxObject bpObject, Element element) {
		element = addContentPhysicalEntity(bpObject, element);
		
		DnaRegion ob = (DnaRegion)bpObject;
		Element tmpElement = null;
		
		if(ob.getEntityReference() != null) {
			tmpElement = new Element("entityReference", bp);
			addIDToProperty(tmpElement, ob.getEntityReference());
			mustPrintObject(ob.getEntityReference());
			element.addContent(tmpElement);
		}
		return element;
	}
	//	entityReference 	EntityReference 	single 
	private Element addContentDna(BioPaxObject bpObject, Element element) {
		element = addContentPhysicalEntity(bpObject, element);
		
		Dna ob = (Dna)bpObject;
		Element tmpElement = null;
		
		if(ob.getEntityReference() != null) {
			tmpElement = new Element("entityReference", bp);
			addIDToProperty(tmpElement, ob.getEntityReference());
			mustPrintObject(ob.getEntityReference());
			element.addContent(tmpElement);
		}
		return element;
	}
	//	entityReference 	EntityReference 	single 
	private Element addContentProtein(BioPaxObject bpObject, Element element) {
		element = addContentPhysicalEntity(bpObject, element);
		
		Protein ob = (Protein)bpObject;
		Element tmpElement = null;
		
		if(ob.getEntityReference() != null) {
			tmpElement = new Element("entityReference", bp);
			addIDToProperty(tmpElement, ob.getEntityReference());
			mustPrintObject(ob.getEntityReference());
			element.addContent(tmpElement);
		}
		return element;
	}
	//	componentStoichiometry 	Stoichiometry 		multiple 
	//	component 				PhysicalEntity 		multiple 
	private Element addContentComplex(BioPaxObject bpObject, Element element) {
		element = addContentPhysicalEntity(bpObject, element);
		
		Complex ob = (Complex)bpObject;
		Element tmpElement = null;
		
		if(ob.getComponentStoichiometry() != null && ob.getComponentStoichiometry().size() > 0) {
			List<Stoichiometry> list = ob.getComponentStoichiometry();
			for(Stoichiometry item : list) {
				tmpElement = new Element("componentStoichiometry", bp);
//				if(item instanceof RdfObjectProxy) {
//					tmpElement.setAttribute("resource", ((RdfObjectProxy)item).getResource(), rdf);
//				} else {
//					tmpElement.setAttribute("resource", item.resourceFromID(), rdf);
//				}
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getComponents() != null && ob.getComponents().size() > 0) {
			List<PhysicalEntity> list = ob.getComponents();
			for(PhysicalEntity item : list) {
				tmpElement = new Element("component", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		return element;
	}

	private Element addContentGroupObject(BioPaxObject bpObject, Element element) {
		element = addContentEntity(bpObject, element);
		
		GroupObject ob = (GroupObject)bpObject;
		Element tmpElement = null;
		
		if(ob.getGroupedObjects() != null && ob.getGroupedObjects().size() > 0) {
			HashSet<BioPaxObject> list = ob.getGroupedObjects();
			for(BioPaxObject item : list) {
				tmpElement = new Element("groupedObject", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getType() != null) {
			tmpElement = new Element("type", bp);
			tmpElement.setAttribute("resource",ob.getType().toString(), rdf);
			tmpElement.setText(ob.getType().toString());
			element.addContent(tmpElement);
		}
		return element;
	}	
	
	
	//	evidence 	Evidence 	multiple 
	private Element addContentTemplateReactionRegulation(BioPaxObject bpObject, Element element) {
		element = addContentControl(bpObject, element);
		
		TemplateReactionRegulation ob = (TemplateReactionRegulation)bpObject;
		Element tmpElement = null;

		if(ob.getEvidence() != null && ob.getEvidence().size() > 0) {
			List<Evidence> list = ob.getEvidence();
			for(Evidence item : list) {
				tmpElement = new Element("evidence", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		return element;
	}
	private Element addContentModulation(BioPaxObject bpObject, Element element) {
		element = addContentControl(bpObject, element);
		return element;
	}
	//	cofactor 			PhysicalEntity 	multiple 
	//	catalysisDirection 	String 			single 
	private Element addContentCatalysis(BioPaxObject bpObject, Element element) {
		element = addContentControl(bpObject, element);
		
		Catalysis ob = (Catalysis)bpObject;
		Element tmpElement = null;

		if(ob.getCofactors() != null && ob.getCofactors().size() > 0) {
			List<PhysicalEntity> list = ob.getCofactors();
			for(PhysicalEntity item : list) {
				tmpElement = new Element("cofactor", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getCatalysisDirection() != null && ob.getCatalysisDirection().length() > 0) {
			tmpElement = new Element("catalysisDirection", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getCatalysisDirection());
			element.addContent(tmpElement);
		}
		return element;
	}
	//	controller 		Pathway 		multiple 
	//	controller 		PhysicalEntity 	multiple 
	//	controlType 	String 			single 
	//	controlled 		Interaction 	single 
	//	controlled 		Pathway 		single 
	private Element addContentControl(BioPaxObject bpObject, Element element) {
		element = addContentInteraction(bpObject, element);
		
		Control ob = (Control)bpObject;
		Element tmpElement = null;

		if(ob.getPathwayControllers() != null && ob.getPathwayControllers().size() > 0) {
			List<Pathway> list = ob.getPathwayControllers();
			for(Pathway item : list) {
				tmpElement = new Element("controller", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getPhysicalControllers() != null && ob.getPhysicalControllers().size() > 0) {
			List<PhysicalEntity> list = ob.getPhysicalControllers();
			for(PhysicalEntity item : list) {
				tmpElement = new Element("controller", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getControlType() != null && ob.getControlType().length() > 0) {
			tmpElement = new Element("controlType", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getControlType());
			element.addContent(tmpElement);
		}
		if(ob.getControlledInteraction() != null) {
			tmpElement = new Element("controlled", bp);
			addIDToProperty(tmpElement, ob.getControlledInteraction());
			mustPrintObject(ob.getControlledInteraction());
			element.addContent(tmpElement);
		}
		if(ob.getControlledPathway() != null) {
			tmpElement = new Element("controlled", bp);
			addIDToProperty(tmpElement, ob.getControlledPathway());
			mustPrintObject(ob.getControlledPathway());
			element.addContent(tmpElement);
		}
		return element;
	}

	//	name 	String 	multiple 
	//	xref 	Xref 	multiple 
	private Element addContentProvenance(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		
		Provenance ob = (Provenance)bpObject;
		Element tmpElement = null;

		if(ob.getName() != null && ob.getName().size() > 0) {
			List<String> list = ob.getName();
			for(String item : list) {
				tmpElement = new Element("name", bp);
				tmpElement.setAttribute("datatype", schemaString, rdf);
				tmpElement.setText(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getxRef() != null && ob.getxRef().size() > 0) {
			List<Xref> list = ob.getxRef();
			for(Xref item : list) {
				tmpElement = new Element("xref", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		return element;
	}

	//	subRegion 	DnaRegionReference 	multiple 
	//	subRegion 	RnaRegionReference 	multiple 
	//	sequence 	String 				single 
	//	organism 	BioSource 			single 
	private Element addContentDnaReference(BioPaxObject bpObject, Element element) {
		element = addContentEntityReference(bpObject, element);
		
		DnaReference ob = (DnaReference)bpObject;
		Element tmpElement = null;

		if(ob.getDnaSubRegion() != null && ob.getDnaSubRegion().size() > 0) {
			List<DnaRegionReference> list = ob.getDnaSubRegion();
			for(DnaRegionReference item : list) {
				tmpElement = new Element("subRegion", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getRnaSubRegion() != null && ob.getRnaSubRegion().size() > 0) {
			List<RnaRegionReference> list = ob.getRnaSubRegion();
			for(RnaRegionReference item : list) {
				tmpElement = new Element("subRegion", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getSequence() != null && ob.getSequence().length() > 0) {
			tmpElement = new Element("sequence", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getSequence());
			element.addContent(tmpElement);
		}
		if(ob.getOrganism() != null) {
			tmpElement = new Element("organism", bp);
			addIDToProperty(tmpElement, ob.getOrganism());
			mustPrintObject(ob.getOrganism());
			element.addContent(tmpElement);
		}
		return element;
	}
	//	absoluteRegion 	SequenceLocation 			single 
	//	subRegion 		DnaRegionReference 			multiple 
	//	subRegion 		RnaRegionReference 			multiple 
	//	sequence 		String 						single 
	//	regionType 		SequenceRegionVocabulary 	single 
	//	organism 		BioSource 					single 
	private Element addContentDnaRegionReference(BioPaxObject bpObject, Element element) {
		element = addContentEntityReference(bpObject, element);
		
		DnaRegionReference ob = (DnaRegionReference)bpObject;
		Element tmpElement = null;

		if(ob.getAbsoluteRegion() != null) {
			tmpElement = new Element("absoluteRegion", bp);
			addIDToProperty(tmpElement, ob.getAbsoluteRegion());
			mustPrintObject(ob.getAbsoluteRegion());
			element.addContent(tmpElement);
		}
		if(ob.getDnaSubRegion() != null && ob.getDnaSubRegion().size() > 0) {
			List<DnaRegionReference> list = ob.getDnaSubRegion();
			for(DnaRegionReference item : list) {
				tmpElement = new Element("subRegion", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getRnaSubRegion() != null && ob.getRnaSubRegion().size() > 0) {
			List<RnaRegionReference> list = ob.getRnaSubRegion();
			for(RnaRegionReference item : list) {
				tmpElement = new Element("subRegion", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getSequence() != null && ob.getSequence().length() > 0) {
			tmpElement = new Element("sequence", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getSequence());
			element.addContent(tmpElement);
		}
		if(ob.getRegionType() != null) {
			tmpElement = new Element("regionType", bp);
			addIDToProperty(tmpElement, ob.getRegionType());
			mustPrintObject(ob.getRegionType());
			element.addContent(tmpElement);
		}
		if(ob.getOrganism() != null) {
			tmpElement = new Element("organism", bp);
			addIDToProperty(tmpElement, ob.getOrganism());
			mustPrintObject(ob.getOrganism());
			element.addContent(tmpElement);
		}
		return element;
	}
	//	sequence 	String 		single 
	//	organism 	BioSource 	single 
	private Element addContentProteinReference(BioPaxObject bpObject, Element element) {
		element = addContentEntityReference(bpObject, element);
		
		ProteinReference ob = (ProteinReference)bpObject;
		Element tmpElement = null;

		if(ob.getSequence() != null && ob.getSequence().length() > 0) {
			tmpElement = new Element("sequence", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getSequence());
			element.addContent(tmpElement);
		}
		if(ob.getOrganism() != null) {
			tmpElement = new Element("organism", bp);
			addIDToProperty(tmpElement, ob.getOrganism());
			mustPrintObject(ob.getOrganism());
			element.addContent(tmpElement);
		}
		return element;
	}
	//	subRegion 	DnaRegionReference 	multiple 
	//	subRegion 	RnaRegionReference 	multiple 
	//	sequence 	String 				single 
	//	organism 	BioSource 			single 
	private Element addContentRnaReference(BioPaxObject bpObject, Element element) {
		element = addContentEntityReference(bpObject, element);
		
		RnaReference ob = (RnaReference)bpObject;
		Element tmpElement = null;

		if(ob.getDnaSubRegion() != null && ob.getDnaSubRegion().size() > 0) {
			List<DnaRegionReference> list = ob.getDnaSubRegion();
			for(DnaRegionReference item : list) {
				tmpElement = new Element("subRegion", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getRnaSubRegion() != null && ob.getRnaSubRegion().size() > 0) {
			List<RnaRegionReference> list = ob.getRnaSubRegion();
			for(RnaRegionReference item : list) {
				tmpElement = new Element("subRegion", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getSequence() != null && ob.getSequence().length() > 0) {
			tmpElement = new Element("sequence", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getSequence());
			element.addContent(tmpElement);
		}
		if(ob.getOrganism() != null) {
			tmpElement = new Element("organism", bp);
			addIDToProperty(tmpElement, ob.getOrganism());
			mustPrintObject(ob.getOrganism());
			element.addContent(tmpElement);
		}
		return element;
	}
	//	absoluteRegion 	SequenceLocation 			single 
	//	subRegion 		DnaRegionReference 			multiple 
	//	subRegion 		RnaRegionReference 			multiple 
	//	sequence 		String 						single 
	//	regionType 		SequenceRegionVocabulary 	single 
	//	organism 		BioSource 					single 
	private Element addContentRnaRegionReference(BioPaxObject bpObject, Element element) {
		element = addContentEntityReference(bpObject, element);
		
		RnaRegionReference ob = (RnaRegionReference)bpObject;
		Element tmpElement = null;

		if(ob.getAbsoluteRegion() != null) {
			tmpElement = new Element("absoluteRegion", bp);
			addIDToProperty(tmpElement, ob.getAbsoluteRegion());
			mustPrintObject(ob.getAbsoluteRegion());
			element.addContent(tmpElement);
		}
		if(ob.getDnaSubRegion() != null && ob.getDnaSubRegion().size() > 0) {
			List<DnaRegionReference> list = ob.getDnaSubRegion();
			for(DnaRegionReference item : list) {
				tmpElement = new Element("subRegion", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getRnaSubRegion() != null && ob.getRnaSubRegion().size() > 0) {
			List<RnaRegionReference> list = ob.getRnaSubRegion();
			for(RnaRegionReference item : list) {
				tmpElement = new Element("subRegion", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getSequence() != null && ob.getSequence().length() > 0) {
			tmpElement = new Element("sequence", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getSequence());
			element.addContent(tmpElement);
		}
		if(ob.getRegionType() != null) {
			tmpElement = new Element("regionType", bp);
			addIDToProperty(tmpElement, ob.getRegionType());
			mustPrintObject(ob.getRegionType());
			element.addContent(tmpElement);
		}
		if(ob.getOrganism() != null) {
			tmpElement = new Element("organism", bp);
			addIDToProperty(tmpElement, ob.getOrganism());
			mustPrintObject(ob.getOrganism());
			element.addContent(tmpElement);
		}
		return element;
	}
	//	chemicalFormula 	String 				single 
	//	molecularWeight 	Float 				single 
	//	structure 			ChemicalStructure 	single 
	private Element addContentSmallMoleculeReference(BioPaxObject bpObject, Element element) {
		element = addContentEntityReference(bpObject, element);
		
		SmallMoleculeReference ob = (SmallMoleculeReference)bpObject;
		Element tmpElement = null;

		if(ob.getChemicalFormula() != null && ob.getChemicalFormula().length() > 0) {
			tmpElement = new Element("chemicalFormula", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getChemicalFormula());
			element.addContent(tmpElement);
		}
		if(ob.getMolecularWeight() != null) {
			tmpElement = new Element("molecularWeight", bp);
			tmpElement.setAttribute("datatype", schemaDouble, rdf);
			tmpElement.setText(ob.getMolecularWeight().toString());
			element.addContent(tmpElement);
		}
		if(ob.getStructure() != null) {
			tmpElement = new Element("structure", bp);
			addIDToProperty(tmpElement, ob.getStructure());
			mustPrintObject(ob.getStructure());
			element.addContent(tmpElement);
		}
		return element;
	}

	//	memberEntityReference	EntityReference					multiple 
	//	entityReferenceType		EntityReferenceTypeVocabulary	single 
	//	entityFeature			EntityFeature					multiple 
	//	name					String							multiple 
	//	xref					Xref							multiple 
	//	evidence				Evidence						multiple 
	private Element addContentEntityReference(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		
		EntityReference ob = (EntityReference)bpObject;
		Element tmpElement = null;

		if(ob.getMemberEntityReference() != null && ob.getMemberEntityReference().size() > 0) {
			List<EntityReference> list = ob.getMemberEntityReference();
			for(EntityReference item : list) {
				tmpElement = new Element("memberEntityReference", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getEntityReferenceType() != null) {
			tmpElement = new Element("entityReferenceType", bp);
			addIDToProperty(tmpElement, ob.getEntityReferenceType());
			mustPrintObject(ob.getEntityReferenceType());
			element.addContent(tmpElement);
		}
		if(ob.getEntityFeature() != null && ob.getEntityFeature().size() > 0) {
			List<EntityFeature> list = ob.getEntityFeature();
			for(EntityFeature item : list) {
				tmpElement = new Element("entityFeature", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getName() != null && ob.getName().size() > 0) {
			List<String> list = ob.getName();
			for(String item : list) {
				tmpElement = new Element("name", bp);
				tmpElement.setAttribute("datatype", schemaString, rdf);
				tmpElement.setText(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getxRef() != null && ob.getxRef().size() > 0) {
			List<Xref> list = ob.getxRef();
			for(Xref item : list) {
				tmpElement = new Element("xref", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getEvidence() != null && ob.getEvidence().size() > 0) {
			List<Evidence> list = ob.getEvidence();
			for(Evidence item : list) {
				tmpElement = new Element("evidence", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		return element;
	}

	//	feature					EntityFeature				multiple 
	//	notFeature				EntityFeature				multiple 
	//	memberPhysicalEntity	PhysicalEntity				multiple 
	//	cellularLocation		CellularLocationVocabulary	single 
	private Element addContentPhysicalEntity(BioPaxObject bpObject, Element element) {
		element = addContentEntity(bpObject, element);
		
		PhysicalEntity ob = (PhysicalEntity)bpObject;
		Element tmpElement = null;

		if(ob.getFeature() != null && ob.getFeature().size() > 0) {
			List<EntityFeature> list = ob.getFeature();
			for(EntityFeature item : list) {
				tmpElement = new Element("feature", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getNotFeature() != null && ob.getNotFeature().size() > 0) {
			List<EntityFeature> list = ob.getNotFeature();
			for(EntityFeature item : list) {
				tmpElement = new Element("notFeature", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getMemberPhysicalEntity() != null && ob.getMemberPhysicalEntity().size() > 0) {
			List<PhysicalEntity> list = ob.getMemberPhysicalEntity();
			for(PhysicalEntity item : list) {
				tmpElement = new Element("memberPhysicalEntity", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getCellularLocation() != null) {
			tmpElement = new Element("cellularLocation", bp);
			addIDToProperty(tmpElement, ob.getCellularLocation());
			mustPrintObject(ob.getCellularLocation());
			element.addContent(tmpElement);
		}
		return element;
	}

	private Element addContentTissueVocabulary(BioPaxObject bpObject, Element element) {
		element = addContentControlledVocabulary(bpObject, element);
		return element;
	}
	private Element addContentSequenceRegionVocabulary(BioPaxObject bpObject, Element element) {
		element = addContentControlledVocabulary(bpObject, element);
		return element;
	}
	private Element addContentSequenceModificationVocabulary(BioPaxObject bpObject, Element element) {
		element = addContentControlledVocabulary(bpObject, element);
		return element;
	}
	private Element addContentRelationshipTypeVocabulary(BioPaxObject bpObject, Element element) {
		element = addContentControlledVocabulary(bpObject, element);
		return element;
	}
	// patoData  String  single 
	private Element addContentPhenotypeVocabulary(BioPaxObject bpObject, Element element) {
		element = addContentControlledVocabulary(bpObject, element);
		
		PhenotypeVocabulary ob = (PhenotypeVocabulary)bpObject;
		Element tmpElement = null;

		if(ob.getPatoData() != null && ob.getPatoData().length() > 0) {
			tmpElement = new Element("patoData", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getPatoData());
			element.addContent(tmpElement);
		}
		return element;
	}
	private Element addContentInteractionVocabulary(BioPaxObject bpObject, Element element) {
		element = addContentControlledVocabulary(bpObject, element);
		return element;
	}
	private Element addContentExperimentalFormVocabulary(BioPaxObject bpObject,	Element element) {
		element = addContentControlledVocabulary(bpObject, element);
		return element;
	}
	private Element addContentEvidenceCodeVocabulary(BioPaxObject bpObject,	Element element) {
		element = addContentControlledVocabulary(bpObject, element);
		return element;
	}
	private Element addContentEntityReferenceTypeVocabulary(BioPaxObject bpObject, Element element) {
		element = addContentControlledVocabulary(bpObject, element);
		return element;
	}
	private Element addContentCellVocabulary(BioPaxObject bpObject, Element element) {
		element = addContentControlledVocabulary(bpObject, element);
		return element;
	}
	private Element addContentCellularLocationVocabulary(BioPaxObject bpObject, Element element) {
		element = addContentControlledVocabulary(bpObject, element);
		return element;
	}

	//	term	String		multiple 
	//	xref	Xref		multiple 
	private Element addContentControlledVocabulary(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);
		
		ControlledVocabulary ob = (ControlledVocabulary)bpObject;
		Element tmpElement = null;
			
		if(ob.getxRef() != null && ob.getxRef().size() > 0) {
			List<Xref> list = ob.getxRef();
			for(Xref item : list) {
				tmpElement = new Element("xref", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getTerm() != null && ob.getTerm().size() > 0) {
			List<String> list = ob.getTerm();
			for(String item : list) {
				tmpElement = new Element("term", bp);
				tmpElement.setAttribute("datatype", schemaString, rdf);
				tmpElement.setText(item);
				element.addContent(tmpElement);
			}
		}
		return element;
	}
	
	//	pathwayOrder 		PathwayStep 	multiple 
	//	pathwayComponent 	Interaction 	multiple 
	//	pathwayComponent 	Pathway 		multiple 
	//	organism 			BioSource 		single 
	private Element addContentPathway(BioPaxObject bpObject, Element element) {
		element = addContentEntity(bpObject, element);
			
		Pathway ob = (Pathway)bpObject;
		Element tmpElement = null;
			
		if(ob.getPathwayOrder() != null && ob.getPathwayOrder().size() > 0) {
			List<PathwayStep> list = ob.getPathwayOrder();
			for(PathwayStep item : list) {
				tmpElement = new Element("pathwayOrder", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		// TODO: make sure the reader solves the dillema during reconciliation phase
		// 		othervise both proxies will be filled out - while in reality one of them is "fake" and should be disposed of
		if(ob.getPathwayComponentInteraction() != null && ob.getPathwayComponentInteraction().size() > 0) {
			List<Interaction> list = ob.getPathwayComponentInteraction();
			for(Interaction item : list) {
				tmpElement = new Element("pathwayComponent", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getPathwayComponentPathway() != null && ob.getPathwayComponentPathway().size() > 0) {
			List<Pathway> list = ob.getPathwayComponentPathway();
			for(Pathway item : list) {
				tmpElement = new Element("pathwayComponent", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getOrganism() != null) {
			tmpElement = new Element("organism", bp);
			addIDToProperty(tmpElement, ob.getOrganism());
			mustPrintObject(ob.getOrganism());
			element.addContent(tmpElement);
		}
		return element;
	}

	//	deltaG 		DeltaG 		multiple 
	//	kEQ 		KPrime 		multiple 
	//	eCNumber 	String 		multiple 
	//	deltaS 		Float 		multiple 
	//	deltaH 		Float 		multiple 
	private Element addContentBiochemicalReaction(BioPaxObject bpObject, Element element) {
		element = addContentConversion(bpObject, element);
		
		BiochemicalReaction ob = (BiochemicalReaction)bpObject;
		Element tmpElement = null;
		
		if(ob.getDeltaG() != null && ob.getDeltaG().size() > 0) {
			List<DeltaG> list = ob.getDeltaG();
			for(DeltaG item : list) {
				tmpElement = new Element("deltaG", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getkEQ() != null && ob.getkEQ().size() > 0) {
			List<KPrime> list = ob.getkEQ();
			for(KPrime item : list) {
				tmpElement = new Element("kEQ", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getECNumber() != null && ob.getECNumber().size() > 0) {
			List<String> list = ob.getECNumber();
			for(String item : list) {
				tmpElement = new Element("eCNumber", bp);
				tmpElement.setAttribute("datatype", schemaString, rdf);
				tmpElement.setText(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getDeltaS() != null && ob.getDeltaS().size() > 0) {
			List<Double> list = ob.getDeltaS();
			for(Double item : list) {
				tmpElement = new Element("deltaS", bp);
				tmpElement.setAttribute("datatype", schemaDouble, rdf);
				tmpElement.setText(item.toString());
				element.addContent(tmpElement);
			}
		}
		if(ob.getDeltaH() != null && ob.getDeltaH().size() > 0) {
			List<Double> list = ob.getDeltaH();
			for(Double item : list) {
				tmpElement = new Element("deltaH", bp);
				tmpElement.setAttribute("datatype", schemaDouble, rdf);
				tmpElement.setText(item.toString());
				element.addContent(tmpElement);
			}
		}
		return element;
	}

	//	right 						PhysicalEntity 	multiple 
	//	participantStoichiometry 	Stoichiometry 	multiple 
	//	left 						PhysicalEntity 	multiple 
	//	spontaneous 				Boolean 		single 
	//	conversionDirection 		String 			single 
	private Element addContentConversion(BioPaxObject bpObject, Element element) {
		element = addContentInteraction(bpObject, element);
		
		Conversion ob = (Conversion)bpObject;
		Element tmpElement = null;
		
		if(ob.getLeft() != null && ob.getLeft().size() > 0) {
			List<PhysicalEntity> list = ob.getLeft();
			for(PhysicalEntity item : list) {
				tmpElement = new Element("left", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getRight() != null && ob.getRight().size() > 0) {
			List<PhysicalEntity> list = ob.getRight();
			for(PhysicalEntity item : list) {
				tmpElement = new Element("right", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getParticipantStoichiometry() != null && ob.getParticipantStoichiometry().size() > 0) {
			List<Stoichiometry> list = ob.getParticipantStoichiometry();
			for(Stoichiometry item : list) {
				tmpElement = new Element("participantStoichiometry", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getSpontaneous() != null) {
			tmpElement = new Element("spontaneous", bp);
			tmpElement.setAttribute("datatype", schemaBoolean, rdf);
			tmpElement.setText(ob.getSpontaneous().toString());
			element.addContent(tmpElement);
		}
		if(ob.getConversionDirection() != null && ob.getConversionDirection().length() > 0) {
			tmpElement = new Element("conversionDirection", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getConversionDirection());
			element.addContent(tmpElement);
		}
		return element;
	}

	//	interactionType InteractionVocabulary 	multiple 
	//	participant 	Entity 					multiple 
	private Element addContentInteraction(BioPaxObject bpObject, Element element) {
		element = addContentEntity(bpObject, element);
		
		Interaction ob = (Interaction)bpObject;
		Element tmpElement = null;
		
		if(ob.getInteractionTypes() != null && ob.getInteractionTypes().size() > 0) {
			List<InteractionVocabulary> list = ob.getInteractionTypes();
			for(InteractionVocabulary item : list) {
				tmpElement = new Element("interactionType", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getParticipants(InteractionParticipant.Type.PARTICIPANT) != null && ob.getParticipants(InteractionParticipant.Type.PARTICIPANT).size() > 0) {
			List<InteractionParticipant> list = ob.getParticipants(InteractionParticipant.Type.PARTICIPANT);
			for(InteractionParticipant item : list) {
				tmpElement = new Element("participant", bp);
//				tmpElement.setAttribute("resource", getResource(item), rdf);
//				mustPrintObject(item);
				addIDToProperty(tmpElement, item.getPhysicalEntity());
				mustPrintObject(item.getPhysicalEntity());
				element.addContent(tmpElement);
			}
		}
		return element;
	}

	//	dataSource		Provenance	multiple 
	//	availability	String		multiple 
	//	name			String		multiple 
	//	xref			Xref		multiple 
	//	evidence		Evidence	multiple 
	private Element addContentEntity(BioPaxObject bpObject, Element element) {
		element = addContentBioPaxObject(bpObject, element);
		
		Entity ob = (Entity)bpObject;
		Element tmpElement = null;
		if(ob.getDataSource() != null && ob.getDataSource().size() > 0) {
			ArrayList<Provenance> list = ob.getDataSource();
			for(Provenance item : list) {
				tmpElement = new Element("dataSource", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getAvailability() != null && ob.getAvailability().size() > 0) {
			ArrayList<String> list = ob.getAvailability();
			for(String item : list) {
				tmpElement = new Element("availability", bp);
				tmpElement.setAttribute("datatype", schemaString, rdf);
				tmpElement.setText(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getName() != null && ob.getName().size() > 0) {
			ArrayList<String> list = ob.getName();
			for(String item : list) {
				tmpElement = new Element("name", bp);
				tmpElement.setAttribute("datatype", schemaString, rdf);
				tmpElement.setText(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getxRef() != null && ob.getxRef().size() > 0) {
			ArrayList<Xref> list = ob.getxRef();
			for(Xref item : list) {
				String elementName = "xref";
				tmpElement = new Element(elementName, bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getEvidence() != null && ob.getEvidence().size() > 0) {
			ArrayList<Evidence> list = ob.getEvidence();
			for(Evidence item : list) {
				tmpElement = new Element("evidence", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}		
		if(ob.getSBSubEntity() != null && ob.getSBSubEntity().size() > 0) {
			ArrayList<SBEntity> list = ob.getSBSubEntity();
			for(SBEntity item : list) {
				tmpElement = new Element("sbSubEntity", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}		
		return element;
	}

	private Element addContentUnificationXref(BioPaxObject bpObject, Element element) {
		element = addContentXref(bpObject, element);
		return element;
	}
	private Element addContentRelationshipXref(BioPaxObject bpObject, Element element) {
		element = addContentXref(bpObject, element);
		
		RelationshipXref ob = (RelationshipXref)bpObject;
		Element tmpElement = null;
		
		if(ob.getRelationshipType() != null && ob.getRelationshipType().size() > 0) {
			ArrayList<RelationshipTypeVocabulary> list = ob.getRelationshipType();
			for(RelationshipTypeVocabulary item : list) {
				tmpElement = new Element("relationshipType", bp);
				addIDToProperty(tmpElement, item);
				mustPrintObject(item);
				element.addContent(tmpElement);
			}
		}
		return element;
	}
	private Element addContentPublicationXref(BioPaxObject bpObject, Element element) {
		element = addContentXref(bpObject, element);
		
		PublicationXref ob = (PublicationXref)bpObject;
		Element tmpElement = null;
		
		if(ob.getAuthor() != null && ob.getAuthor().size() > 0) {
			ArrayList<String> list = ob.getAuthor();
			for(String item : list) {
				tmpElement = new Element("author", bp);
				tmpElement.setAttribute("datatype", schemaString, rdf);
				tmpElement.setText(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getSource() != null && ob.getSource().size() > 0) {
			ArrayList<String> list = ob.getSource();
			for(String item : list) {
				tmpElement = new Element("source", bp);
				tmpElement.setAttribute("datatype", schemaString, rdf);
				tmpElement.setText(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getUrl() != null && ob.getUrl().size() > 0) {
			ArrayList<String> list = ob.getUrl();
			for(String item : list) {
				tmpElement = new Element("url", bp);
				tmpElement.setAttribute("datatype", schemaString, rdf);
				tmpElement.setText(item);
				element.addContent(tmpElement);
			}
		}
		if(ob.getYear() != null && ob.getYear() > 0) {
			tmpElement = new Element("year", bp);
			tmpElement.setAttribute("datatype", schemaInt, rdf);
			tmpElement.setText(ob.getYear().toString());
			element.addContent(tmpElement);
		}
		if(ob.getTitle() != null && ob.getTitle().length() > 0) {
			tmpElement = new Element("title", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getTitle());
			element.addContent(tmpElement);
		}
		return element;
	}
	private Element addContentXref(BioPaxObject bpObject, Element element) {
		element = addContentUtilityClass(bpObject, element);

		Xref ob = (Xref)bpObject;
		Element tmpElement = null;
		if(ob.getDb() != null && ob.getDb().length() > 0) {
			tmpElement = new Element("db", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getDb());
			element.addContent(tmpElement);
		}
		if(ob.getDbVersion() != null && ob.getDbVersion().length() > 0) {
			tmpElement = new Element("dbVersion", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getDbVersion());
			element.addContent(tmpElement);
		}
		if(ob.getId() != null && ob.getId().length() > 0) {
			tmpElement = new Element("id", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getId());
			element.addContent(tmpElement);
		}
		if(ob.getIdVersion() != null && ob.getIdVersion().length() > 0) {
			tmpElement = new Element("idVersion", bp);
			tmpElement.setAttribute("datatype", schemaString, rdf);
			tmpElement.setText(ob.getIdVersion());
			element.addContent(tmpElement);
		}
		return element;
	}
	private Element addContentUtilityClass(BioPaxObject bpObject, Element element) {
		element = addContentBioPaxObject(bpObject, element);
		return element;
	}
	private Element addContentBioPaxObject(BioPaxObject bpObject, Element element) {
		Element tmpElement = null;
		if(bpObject.getComments() != null && bpObject.getComments().size() > 0) {
			for(String sc : bpObject.getComments()) {
				tmpElement = new Element("comment", bp);
				tmpElement.setAttribute("datatype", schemaString, rdf);
				tmpElement.setText(sc);
				element.addContent(tmpElement);
			}
		}
		return element;
	}

	// ------------------------
	private Element addAttributes(BioPaxObject bpObject, Element element) {
		context.addIDToObject(element, bpObject.getID());
		return element;
	}
	
	private void addIDToProperty(Element element, BioPaxObject object) {
		context.addIDToProperty(element, object.getID());
	}
	
}

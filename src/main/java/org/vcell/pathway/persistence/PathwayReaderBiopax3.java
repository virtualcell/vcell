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

import static org.vcell.pathway.PathwayXMLHelper.showIgnored;
import static org.vcell.pathway.PathwayXMLHelper.showUnexpected;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.sbpax.schemas.util.DefaultNameSpaces;
import org.vcell.pathway.BindingFeature;
import org.vcell.pathway.BindingFeatureImpl;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.BioSource;
import org.vcell.pathway.BiochemicalPathwayStep;
import org.vcell.pathway.BiochemicalReaction;
import org.vcell.pathway.BiochemicalReactionImpl;
import org.vcell.pathway.Catalysis;
import org.vcell.pathway.CellVocabulary;
import org.vcell.pathway.CellularLocationVocabulary;
import org.vcell.pathway.ChemicalStructure;
import org.vcell.pathway.Complex;
import org.vcell.pathway.ComplexAssembly;
import org.vcell.pathway.Control;
import org.vcell.pathway.ControlledVocabulary;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.ConversionImpl;
import org.vcell.pathway.CovalentBindingFeature;
import org.vcell.pathway.Degradation;
import org.vcell.pathway.DeltaG;
import org.vcell.pathway.Dna;
import org.vcell.pathway.DnaReference;
import org.vcell.pathway.DnaRegion;
import org.vcell.pathway.DnaRegionReference;
import org.vcell.pathway.Entity;
import org.vcell.pathway.EntityFeature;
import org.vcell.pathway.EntityFeatureImpl;
import org.vcell.pathway.EntityReference;
import org.vcell.pathway.EntityReferenceTypeVocabulary;
import org.vcell.pathway.Evidence;
import org.vcell.pathway.EvidenceCodeVocabulary;
import org.vcell.pathway.ExperimentalForm;
import org.vcell.pathway.ExperimentalFormVocabulary;
import org.vcell.pathway.FragmentFeature;
import org.vcell.pathway.Gene;
import org.vcell.pathway.GeneticInteraction;
import org.vcell.pathway.GroupObject;
import org.vcell.pathway.Interaction;
import org.vcell.pathway.InteractionImpl;
import org.vcell.pathway.InteractionVocabulary;
import org.vcell.pathway.KPrime;
import org.vcell.pathway.ModificationFeature;
import org.vcell.pathway.ModificationFeatureImpl;
import org.vcell.pathway.Modulation;
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
import org.vcell.pathway.SequenceModificationVocabulary;
import org.vcell.pathway.SequenceRegionVocabulary;
import org.vcell.pathway.SequenceSite;
import org.vcell.pathway.SmallMolecule;
import org.vcell.pathway.SmallMoleculeReference;
import org.vcell.pathway.Stoichiometry;
import org.vcell.pathway.TemplateReaction;
import org.vcell.pathway.TemplateReactionRegulation;
import org.vcell.pathway.TissueVocabulary;
import org.vcell.pathway.Transport;
import org.vcell.pathway.TransportImpl;
import org.vcell.pathway.TransportWithBiochemicalReaction;
import org.vcell.pathway.UnificationXref;
import org.vcell.pathway.UtilityClass;
import org.vcell.pathway.Xref;
import org.vcell.pathway.persistence.BiopaxProxy.BioPaxObjectProxy;
import org.vcell.pathway.persistence.BiopaxProxy.BioSourceProxy;
import org.vcell.pathway.persistence.BiopaxProxy.CellVocabularyProxy;
import org.vcell.pathway.persistence.BiopaxProxy.CellularLocationVocabularyProxy;
import org.vcell.pathway.persistence.BiopaxProxy.ChemicalStructureProxy;
import org.vcell.pathway.persistence.BiopaxProxy.ConversionProxy;
import org.vcell.pathway.persistence.BiopaxProxy.DnaRegionReferenceProxy;
import org.vcell.pathway.persistence.BiopaxProxy.EntityFeatureProxy;
import org.vcell.pathway.persistence.BiopaxProxy.EntityReferenceProxy;
import org.vcell.pathway.persistence.BiopaxProxy.EntityReferenceTypeVocabularyProxy;
import org.vcell.pathway.persistence.BiopaxProxy.EvidenceCodeVocabularyProxy;
import org.vcell.pathway.persistence.BiopaxProxy.EvidenceProxy;
import org.vcell.pathway.persistence.BiopaxProxy.ExperimentalFormProxy;
import org.vcell.pathway.persistence.BiopaxProxy.ExperimentalFormVocabularyProxy;
import org.vcell.pathway.persistence.BiopaxProxy.GeneProxy;
import org.vcell.pathway.persistence.BiopaxProxy.InteractionProxy;
import org.vcell.pathway.persistence.BiopaxProxy.InteractionVocabularyProxy;
import org.vcell.pathway.persistence.BiopaxProxy.PathwayProxy;
import org.vcell.pathway.persistence.BiopaxProxy.PathwayStepProxy;
import org.vcell.pathway.persistence.BiopaxProxy.PhenotypeVocabularyProxy;
import org.vcell.pathway.persistence.BiopaxProxy.PhysicalEntityProxy;
import org.vcell.pathway.persistence.BiopaxProxy.ProvenanceProxy;
import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;
import org.vcell.pathway.persistence.BiopaxProxy.RelationshipTypeVocabularyProxy;
import org.vcell.pathway.persistence.BiopaxProxy.RnaRegionReferenceProxy;
import org.vcell.pathway.persistence.BiopaxProxy.SBEntityProxy;
import org.vcell.pathway.persistence.BiopaxProxy.SBMeasurableProxy;
import org.vcell.pathway.persistence.BiopaxProxy.SBStateProxy;
import org.vcell.pathway.persistence.BiopaxProxy.SBVocabularyProxy;
import org.vcell.pathway.persistence.BiopaxProxy.ScoreProxy;
import org.vcell.pathway.persistence.BiopaxProxy.SequenceLocationProxy;
import org.vcell.pathway.persistence.BiopaxProxy.SequenceModificationVocabularyProxy;
import org.vcell.pathway.persistence.BiopaxProxy.SequenceRegionVocabularyProxy;
import org.vcell.pathway.persistence.BiopaxProxy.SequenceSiteProxy;
import org.vcell.pathway.persistence.BiopaxProxy.StoichiometryProxy;
import org.vcell.pathway.persistence.BiopaxProxy.TissueVocabularyProxy;
import org.vcell.pathway.persistence.BiopaxProxy.XrefProxy;
import org.vcell.pathway.sbpax.SBEntity;
import org.vcell.pathway.sbpax.SBEntityImpl;
import org.vcell.pathway.sbpax.SBMeasurable;
import org.vcell.pathway.sbpax.SBState;
import org.vcell.pathway.sbpax.SBVocabulary;
import org.vcell.pathway.sbpax.UnitOfMeasurement;
import org.vcell.pathway.sbpax.UnitOfMeasurementPool;

import cbit.util.xml.XmlUtil;


public class PathwayReaderBiopax3 {
	
	protected final RDFXMLContext context;
	private PathwayModel pathwayModel = new PathwayModel();
	private static final Namespace bp = Namespace.getNamespace("bp", "http://www.biopax.org/release/biopax-level3.owl#");
	private static final Namespace rdf = Namespace.getNamespace("rdf", DefaultNameSpaces.RDF.uri);
	private static final Namespace sbx3 = Namespace.getNamespace("sbx", "http://vcell.org/sbpax3#");

	public final Namespace getNamespaceBp() {
		return bp;
	}
	public final Namespace getNamespaceRdf() {
		return rdf;
	}
	
	static private int unexpectedCount = 0;
	
	public PathwayReaderBiopax3(RDFXMLContext context) { this.context = context; }
	
	public static void main(String args[]){
		try {
			// usual testing pathway
			Document document = XmlUtil.readXML(new File("C:\\dan\\reactome biopax\\Reactome3_189445.owl"));
			// infinite recursion between pathway steps
			// http://www.pathwaycommons.org/pc/webservice.do?cmd=get_record_by_cpath_id&version=2.0&q=826249&output=biopax
//			Document document = XmlUtil.readXML(new File("C:\\dan\\reactome biopax\\recursive.owl"));
			PathwayReaderBiopax3 pathwayReader = new PathwayReaderBiopax3(new RDFXMLContext());
			System.out.println("starting parsing");
			PathwayModel pathwayModel = pathwayReader.parse(document.getRootElement(),true);
			System.out.println("ending parsing");
			pathwayModel.reconcileReferences(null);
			System.out.println(pathwayModel.show(false));
		}catch (Exception e){
			e.printStackTrace(System.out);			
		}
	}

	public PathwayModel parse(Element rootElement,boolean bExternal) {
		
		for (Object child : rootElement.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (childElement.getName().equals("Pathway")){
					pathwayModel.add(addObjectPathway(childElement));
				}else if (childElement.getName().equals("BiochemicalReaction")){
					pathwayModel.add(addObjectBiochemicalReaction(childElement));
				}else if (childElement.getName().equals("CellularLocationVocabulary")){
					pathwayModel.add(addObjectCellularLocationVocabulary(childElement));
				}else if (childElement.getName().equals("CellVocabulary")){
					pathwayModel.add(addObjectCellVocabulary(childElement));
				}else if (childElement.getName().equals("Conversion")){
					pathwayModel.add(addObjectConversion(childElement));
				}else if (childElement.getName().equals("EntityReferenceTypeVocabulary")){
					pathwayModel.add(addObjectEntityReferenceTypeVocabulary(childElement));
				}else if (childElement.getName().equals("EvidenceCodeVocabulary")){
					pathwayModel.add(addObjectEvidenceCodeVocabulary(childElement));
				}else if (childElement.getName().equals("ExperimentalFormVocabulary")){
					pathwayModel.add(addObjectExperimentalFormVocabulary(childElement));
				}else if (childElement.getName().equals("InteractionVocabulary")){
					pathwayModel.add(addObjectInteractionVocabulary(childElement));
				}else if (childElement.getName().equals("PhenotypeVocabulary")){
					pathwayModel.add(addObjectPhenotypeVocabulary(childElement));
				}else if (childElement.getName().equals("RelationshipTypeVocabulary")){
					pathwayModel.add(addObjectRelationshipTypeVocabulary(childElement));
				}else if (childElement.getName().equals("SequenceModificationVocabulary")){
					pathwayModel.add(addObjectSequenceModificationVocabulary(childElement));
				}else if (childElement.getName().equals("SequenceRegionVocabulary")){
					pathwayModel.add(addObjectSequenceRegionVocabulary(childElement));
				}else if (childElement.getName().equals("TissueVocabulary")){
					pathwayModel.add(addObjectTissueVocabulary(childElement));
				}else if (childElement.getName().equals("PublicationXref")){
					pathwayModel.add(addObjectPublicationXref(childElement));
				}else if (childElement.getName().equals("RelationshipXref")){
					pathwayModel.add(addObjectRelationshipXref(childElement));
				}else if (childElement.getName().equals("UnificationXref")){
					pathwayModel.add(addObjectUnificationXref(childElement));
				}else if (childElement.getName().equals("DnaReference")){
					pathwayModel.add(addObjectDnaReference(childElement));
				}else if (childElement.getName().equals("DnaRegionReference")){
					pathwayModel.add(addObjectDnaRegionReference(childElement));
				}else if (childElement.getName().equals("ProteinReference")){
					pathwayModel.add(addObjectProteinReference(childElement));
				}else if (childElement.getName().equals("RnaReference")){
					pathwayModel.add(addObjectRnaReference(childElement));
				}else if (childElement.getName().equals("RnaRegionReference")){
					pathwayModel.add(addObjectRnaRegionReference(childElement));
				}else if (childElement.getName().equals("SmallMoleculeReference")){
					pathwayModel.add(addObjectSmallMoleculeReference(childElement));
				}else if (childElement.getName().equals("Provenance")){
					pathwayModel.add(addObjectProvenance(childElement));
				}else if (childElement.getName().equals("Control")){
					pathwayModel.add(addObjectControl(childElement));
				}else if (childElement.getName().equals("Catalysis")){
					pathwayModel.add(addObjectCatalysis(childElement));
				}else if (childElement.getName().equals("Modulation")){
					pathwayModel.add(addObjectModulation(childElement));
				}else if (childElement.getName().equals("TemplateReactionRegulation")){
					pathwayModel.add(addObjectTemplateReactionRegulation(childElement));
				}else if (childElement.getName().equals("Complex")){
					pathwayModel.add(addObjectComplex(childElement));
				}else if (childElement.getName().equals("Protein")){
					pathwayModel.add(addObjectProtein(childElement));
				}else if (childElement.getName().equals("Dna")){
					pathwayModel.add(addObjectDna(childElement));
				}else if (childElement.getName().equals("DnaRegion")){
					pathwayModel.add(addObjectDnaRegion(childElement));
				}else if (childElement.getName().equals("Rna")){
					pathwayModel.add(addObjectRna(childElement));
				}else if (childElement.getName().equals("RnaRegion")){
					pathwayModel.add(addObjectRnaRegion(childElement));
				}else if (childElement.getName().equals("SmallMolecule")){
					pathwayModel.add(addObjectSmallMolecule(childElement));
				}else if (childElement.getName().equals("BioSource")){
					pathwayModel.add(addObjectBioSource(childElement));
				}else if (childElement.getName().equals("ModificationFeature")){
					pathwayModel.add(addObjectModificationFeature(childElement));
				}else if (childElement.getName().equals("CovalentBindingFeature")){
					pathwayModel.add(addObjectCovalentBindingFeature(childElement));
				}else if (childElement.getName().equals("FragmentFeature")){
					pathwayModel.add(addObjectFragmentFeature(childElement));
				}else if (childElement.getName().equals("SequenceInterval")){
					pathwayModel.add(addObjectSequenceInterval(childElement));
				}else if (childElement.getName().equals("SequenceSite")){
					pathwayModel.add(addObjectSequenceSite(childElement));
				}else if (childElement.getName().equals("Stoichiometry")){
					pathwayModel.add(addObjectStoichiometry(childElement));
				}else if (childElement.getName().equals("PathwayStep")){
					pathwayModel.add(addObjectPathwayStep(childElement));
				}else if (childElement.getName().equals("BiochemicalPathwayStep")){
					pathwayModel.add(addObjectBiochemicalPathwayStep(childElement));
				}else if (childElement.getName().equals("PhysicalEntity")){
					pathwayModel.add(addObjectPhysicalEntity(childElement));
				// not found "as is" in tested sample
				}else if (childElement.getName().equals("Gene")){
					pathwayModel.add(addObjectGene(childElement));
				}else if (childElement.getName().equals("Ontology")){
					//showIgnored(childElement);
				}else if (childElement.getName().equals("Interaction")){
					pathwayModel.add(addObjectInteraction(childElement));
				}else if (childElement.getName().equals("Transport")){
					pathwayModel.add(addObjectTransport(childElement));
				}else if (childElement.getName().equals("TransportWithBiochemicalReaction")){
					pathwayModel.add(addObjectTransportWithBiochemicalReaction(childElement));
				}else if (childElement.getName().equals("GeneticInteraction")){
					pathwayModel.add(addObjectGeneticInteraction(childElement));
				}else if (childElement.getName().equals("MolecularInteraction")){
					pathwayModel.add(addObjectMolecularInteraction(childElement));
				}else if (childElement.getName().equals("TemplateReaction")){
					pathwayModel.add(addObjectTemplateReaction(childElement));
				}else if (childElement.getName().equals("ComplexAssembly")){
					pathwayModel.add(addObjectComplexAssembly(childElement));
				}else if (childElement.getName().equals("Degradation")){
					pathwayModel.add(addObjectDegradation(childElement));
				}else if (childElement.getName().equals("ChemicalStructure")){
					pathwayModel.add(addObjectChemicalStructure(childElement));
				}else if (childElement.getName().equals("DeltaG")){
					pathwayModel.add(addObjectDeltaG(childElement));
				}else if (childElement.getName().equals("EntityFeature")){
					pathwayModel.add(addObjectEntityFeature(childElement));
				}else if (childElement.getName().equals("BindingFeature")){
					pathwayModel.add(addObjectBindingFeature(childElement));
				}else if (childElement.getName().equals("Evidence")){
					pathwayModel.add(addObjectEvidence(childElement));
				}else if (childElement.getName().equals("ExperimentalForm")){
					pathwayModel.add(addObjectExperimentalForm(childElement));
				}else if (childElement.getName().equals("KPrime")){
					pathwayModel.add(addObjectKPrime(childElement));
				}else if (childElement.getName().equals("Score")){
					pathwayModel.add(addObjectScore(childElement));
				}else if (childElement.getName().equals("SequenceLocation")){
					pathwayModel.add(addObjectSequenceLocation(childElement));
				}else if (childElement.getName().equals("GroupObject")){
					pathwayModel.add(addObjectGroupObject(childElement));					
				}else if (childElement.getName().equals("SBEntity")){
					pathwayModel.add(addObjectSBEntity(childElement));
				}else if (childElement.getName().equals("SBMeasurable")){
					pathwayModel.add(addObjectSBMeasurable(childElement));
				}else if (childElement.getName().equals("SBState")){
					pathwayModel.add(addObjectSBState(childElement));
				}else if (childElement.getName().equals("UnitOfMeasurement")){
					pathwayModel.add(addObjectUnitOfMeasurement(childElement));
				}else if (childElement.getName().equals("SBVocabulary")){
					pathwayModel.add(addObjectSBVocabulary(childElement));
				}else if (childElement.getName().equals("DiagramObjectsID")){
					String id = childElement.getTextTrim();
					if(id != null && !id.equals("")) {
						pathwayModel.getDiagramObjects().add((bExternal?context.unAbbreviateURI(childElement, id):id));
					}
				}else{
					showUnexpected(childElement);
				}
			}
		}
		return pathwayModel;
	}

	private BioPaxObject addObjectBioPaxObjectSubclass(Element childElement) {
		if (childElement.getName().equals("Pathway")){
			return addObjectPathway(childElement);
		}else if (childElement.getName().equals("BiochemicalReaction")){
			return addObjectBiochemicalReaction(childElement);
		}else if (childElement.getName().equals("CellularLocationVocabulary")){
			return addObjectCellularLocationVocabulary(childElement);
		}else if (childElement.getName().equals("CellVocabulary")){
			return addObjectCellVocabulary(childElement);
		}else if (childElement.getName().equals("EntityReferenceTypeVocabulary")){
			return addObjectEntityReferenceTypeVocabulary(childElement);
		}else if (childElement.getName().equals("EvidenceCodeVocabulary")){
			return addObjectEvidenceCodeVocabulary(childElement);
		}else if (childElement.getName().equals("ExperimentalFormVocabulary")){
			return addObjectExperimentalFormVocabulary(childElement);
		}else if (childElement.getName().equals("InteractionVocabulary")){
			return addObjectInteractionVocabulary(childElement);
		}else if (childElement.getName().equals("PhenotypeVocabulary")){
			return addObjectPhenotypeVocabulary(childElement);
		}else if (childElement.getName().equals("RelationshipTypeVocabulary")){
			return addObjectRelationshipTypeVocabulary(childElement);
		}else if (childElement.getName().equals("SequenceModificationVocabulary")){
			return addObjectSequenceModificationVocabulary(childElement);
		}else if (childElement.getName().equals("SequenceRegionVocabulary")){
			return addObjectSequenceRegionVocabulary(childElement);
		}else if (childElement.getName().equals("TissueVocabulary")){
			return addObjectTissueVocabulary(childElement);
		}else if (childElement.getName().equals("PublicationXref")){
			return addObjectPublicationXref(childElement);
		}else if (childElement.getName().equals("RelationshipXref")){
			return addObjectRelationshipXref(childElement);
		}else if (childElement.getName().equals("UnificationXref")){
			return addObjectUnificationXref(childElement);
		}else if (childElement.getName().equals("DnaReference")){
			return addObjectDnaReference(childElement);
		}else if (childElement.getName().equals("DnaRegionReference")){
			return addObjectDnaRegionReference(childElement);
		}else if (childElement.getName().equals("ProteinReference")){
			return addObjectProteinReference(childElement);
		}else if (childElement.getName().equals("RnaReference")){
			return addObjectRnaReference(childElement);
		}else if (childElement.getName().equals("RnaRegionReference")){
			return addObjectRnaRegionReference(childElement);
		}else if (childElement.getName().equals("SmallMoleculeReference")){
			return addObjectSmallMoleculeReference(childElement);
		}else if (childElement.getName().equals("Provenance")){
			return addObjectProvenance(childElement);
		}else if (childElement.getName().equals("Control")){
			return addObjectControl(childElement);
		}else if (childElement.getName().equals("Catalysis")){
			return addObjectCatalysis(childElement);
		}else if (childElement.getName().equals("Modulation")){
			return addObjectModulation(childElement);
		}else if (childElement.getName().equals("TemplateReactionRegulation")){
			return addObjectTemplateReactionRegulation(childElement);
		}else if (childElement.getName().equals("Complex")){
			return addObjectComplex(childElement);
		}else if (childElement.getName().equals("Protein")){
			return addObjectProtein(childElement);
		}else if (childElement.getName().equals("Dna")){
			return addObjectDna(childElement);
		}else if (childElement.getName().equals("DnaRegion")){
			return addObjectDnaRegion(childElement);
		}else if (childElement.getName().equals("Rna")){
			return addObjectRna(childElement);
		}else if (childElement.getName().equals("RnaRegion")){
			return addObjectRnaRegion(childElement);
		}else if (childElement.getName().equals("SmallMolecule")){
			return addObjectSmallMolecule(childElement);
		}else if (childElement.getName().equals("BioSource")){
			return addObjectBioSource(childElement);
		}else if (childElement.getName().equals("ModificationFeature")){
			return addObjectModificationFeature(childElement);
		}else if (childElement.getName().equals("CovalentBindingFeature")){
			return addObjectCovalentBindingFeature(childElement);
		}else if (childElement.getName().equals("FragmentFeature")){
			return addObjectFragmentFeature(childElement);
		}else if (childElement.getName().equals("SequenceInterval")){
			return addObjectSequenceInterval(childElement);
		}else if (childElement.getName().equals("SequenceSite")){
			return addObjectSequenceSite(childElement);
		}else if (childElement.getName().equals("Stoichiometry")){
			return addObjectStoichiometry(childElement);
		}else if (childElement.getName().equals("PathwayStep")){
			return addObjectPathwayStep(childElement);
		}else if (childElement.getName().equals("BiochemicalPathwayStep")){
			return addObjectBiochemicalPathwayStep(childElement);
		}else if (childElement.getName().equals("PhysicalEntity")){
			return addObjectPhysicalEntity(childElement);
		}else if (childElement.getName().equals("Gene")){
			return addObjectGene(childElement);
		}else if (childElement.getName().equals("Ontology")){
			showIgnored(childElement, "");
			return null;
		}else if (childElement.getName().equals("Interaction")){
			return addObjectInteraction(childElement);
		}else if (childElement.getName().equals("Transport")){
			return addObjectTransport(childElement);
		}else if (childElement.getName().equals("TransportWithBiochemicalReaction")){
			return addObjectTransportWithBiochemicalReaction(childElement);
		}else if (childElement.getName().equals("GeneticInteraction")){
			return addObjectGeneticInteraction(childElement);
		}else if (childElement.getName().equals("MolecularInteraction")){
			return addObjectMolecularInteraction(childElement);
		}else if (childElement.getName().equals("TemplateReaction")){
			return addObjectTemplateReaction(childElement);
		}else if (childElement.getName().equals("ComplexAssembly")){
			return addObjectComplexAssembly(childElement);
		}else if (childElement.getName().equals("Degradation")){
			return addObjectDegradation(childElement);
		}else if (childElement.getName().equals("ChemicalStructure")){
			return addObjectChemicalStructure(childElement);
		}else if (childElement.getName().equals("DeltaG")){
			return addObjectDeltaG(childElement);
		}else if (childElement.getName().equals("EntityFeature")){
			return addObjectEntityFeature(childElement);
		}else if (childElement.getName().equals("BindingFeature")){
			return addObjectBindingFeature(childElement);
		}else if (childElement.getName().equals("Evidence")){
			return addObjectEvidence(childElement);
		}else if (childElement.getName().equals("ExperimentalForm")){
			return addObjectExperimentalForm(childElement);
		}else if (childElement.getName().equals("KPrime")){
			return addObjectKPrime(childElement);
		}else if (childElement.getName().equals("Score")){
			return addObjectScore(childElement);
		}else if (childElement.getName().equals("SequenceLocation")){
			return addObjectSequenceLocation(childElement);
		}else if (childElement.getName().equals("GroupObject")){
			return addObjectGroupObject(childElement);
		}else if (childElement.getName().equals("SBMeasurable")){
			return addObjectSBMeasurable(childElement);
		}else if (childElement.getName().equals("SBEntity")){
			return addObjectSBEntity(childElement);
		}else if (childElement.getName().equals("SBState")){
			return addObjectSBState(childElement);
		}else if (childElement.getName().equals("UnitOfMeasurement")){
			return addObjectUnitOfMeasurement(childElement);
		}else if (childElement.getName().equals("SBVocabulary")){
			return addObjectSBVocabulary(childElement);
		}else{
			showUnexpected(childElement);
			return null;
		}
	}
	
	private void addAttributes(BioPaxObject bioPaxObject, Element element){
		int count = 0;
		for (Object attr : element.getAttributes()){
			Attribute attribute = (Attribute)attr;
			if (attribute.getName().equals("ID")){
				if (bioPaxObject instanceof RdfObjectProxy){
					showUnexpected(attribute);
				}else{
					String uri = context.unAbbreviateURI(element, attribute.getValue());
					bioPaxObject.setID(uri);
					count++;
				}
			}else if (attribute.getName().equals("resource")){
				if (bioPaxObject instanceof RdfObjectProxy){
					String uri = context.unRelativizeURI(element, attribute.getValue());
					bioPaxObject.setID(uri);
					count++;
				}else{
					showUnexpected(attribute);
				}
			}else if (attribute.getName().equals("about")){
				if (bioPaxObject instanceof RdfObjectProxy){		// TODO: can this ever happen?
					String uri = context.unRelativizeURI(element, attribute.getValue());
					bioPaxObject.setID(uri);
					count++;
				}else{
					String uri = context.unRelativizeURI(element, attribute.getValue());
					bioPaxObject.setID(uri);
					count++;
				}
			}else if (attribute.getName().equals("nodeID")){
				if (bioPaxObject instanceof RdfObjectProxy){		// TODO: can this ever happen?
					((RdfObjectProxy)bioPaxObject).setID(attribute.getValue());
					count++;
				}else{
					bioPaxObject.setID(attribute.getValue());
					count++;
				}
			}else{
				showUnexpected(attribute);
			}
		}
		if(count < 1) {
			System.out.println(element + " has neither ID nor resource attributes.");
		}
		if(count > 1) {
			System.out.println(element + " has multiple ID and/or resource attributes.");
		}
	}

	private String parseIDAttributes(Element element){
		int count = 0;
		String id = null;
		for (Object attr : element.getAttributes()){
			Attribute attribute = (Attribute)attr;
			if (attribute.getName().equals("ID")){
				id = context.unAbbreviateURI(element, attribute.getValue());
				count++;
			} else if (attribute.getName().equals("resource")){
				id = context.unRelativizeURI(element, attribute.getValue());
				count++;
			} else if (attribute.getName().equals("about")){
				id = context.unRelativizeURI(element, attribute.getValue());
				count++;
			} else if (attribute.getName().equals("nodeID")){
				id = attribute.getValue();
				count++;
			} else{
				showUnexpected(attribute);
			}
		}
		if(count < 1) {
			System.out.println(element + " has neither ID nor resource attributes.");
		}
		if(count > 1) {
			System.out.println(element + " has multiple ID and/or resource attributes.");
		}
		return id;
	}

//	if(bpObject.getID() != null) {
//		System.out.println(" --------------------------------------- Complex found");
//	}
	
	private boolean addContentUnitOfMeasurement(UnitOfMeasurement unit, Element element, Element child) {
		// TODO Auto-generated method stub
		return false;
	}


	private boolean addContentBioPaxObject(BioPaxObject biopaxObject, Element element, Element childElement){
		if (childElement.getName().equals("comment")){
			biopaxObject.getComments().add(childElement.getTextTrim());
			return true;
		}else{
			return false; // didn't consume the childElement
		}
	}
	
//	dataSource 		Provenance 	multiple 
//	availability 	String 		multiple 
//	name 			String 		multiple 
//	comment 		String 		multiple 
//	xref 			Xref 		multiple 
//	evidence 		Evidence 	multiple 
	private boolean addContentEntity(Entity entity, Element element, Element childElement){
		if (addContentSBEntity(entity, element, childElement)){
			return true;
		}
		if (childElement.getName().equals("dataSource")){
			entity.getDataSource().add(addObjectProvenance(childElement));
			return true;
		}else if (childElement.getName().equals("name")){
			entity.getName().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("standardName")){
			entity.getName().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("displayName")){
			// TODO: why insert in getName() again instead of its own getDisplayName()  ???
			// Why? Well, because a displayName is a name. 
			insertAtStart(entity.getName(),childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("availability")){
			entity.getAvailability().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("xref")){
			entity.getxRef().add(addObjectXref(childElement));
			return true;
		}else if (childElement.getName().equals("evidence")){
			entity.getEvidence().add(addObjectEvidence(childElement));
			return true;
		}else if (childElement.getName().equals("sbSubEntity")){
			entity.getSBSubEntity().add(addObjectSBEntity(childElement));
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentUtilityClass(UtilityClass utilityClass, Element element, Element childElement){
		if (addContentBioPaxObject(utilityClass, element, childElement)){	// "comment" property
			return true;
		}
		return false; // no match
	}
	
	private boolean addContentBioSource(BioSource bioSource, Element element, Element childElement){
		if (addContentUtilityClass(bioSource, element, childElement)){
			return true;
		}
		/**
		 * CellVocabulary cellType
		 * ArrayList<String> name
		 * TissueVocabulary tissue
		 * ArrayList<Xref> xRef
		 */
		if (childElement.getName().equals("name")){
			bioSource.getName().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("standardName")){
			bioSource.getName().add(childElement.getTextTrim());
			return true;
		} else if (childElement.getName().equals("displayName")){
			insertAtStart(bioSource.getName(),childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("xref")){
			bioSource.getxRef().add(addObjectXref(childElement));
			return true;
		}else if (childElement.getName().equals("cellType")){
			bioSource.setCellType(addObjectCellVocabulary(childElement));
			return true;
		}else if (childElement.getName().equals("tissue")){
			bioSource.setTissue(addObjectTissueVocabulary(childElement));
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentChemicalStructure(ChemicalStructure chemicalStructure, Element element, Element childElement){
		if (addContentUtilityClass(chemicalStructure, element, childElement)){
			return true;
		}
		/**
		 * String structureData
		 * String structureFormat
		 */
		if (childElement.getName().equals("structureData")){
			chemicalStructure.setStructureData(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("structureFormat")){
			chemicalStructure.setStructureFormat(childElement.getTextTrim());
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentControlledVocabulary(ControlledVocabulary controlledVocabulary, Element element, Element childElement){
		if (addContentUtilityClass(controlledVocabulary, element, childElement)){
			return true;
		}
		/**
		 * ArrayList<String> term
		 * ArrayList<Xref> xRef
		 */
		if (childElement.getName().equals("xref")){
			controlledVocabulary.getxRef().add(addObjectXref(childElement));
			return true;
		}else if (childElement.getName().equals("term")){
			controlledVocabulary.getTerm().add(childElement.getTextTrim());
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentEntityFeature(EntityFeature entityFeature, Element element, Element childElement){
		if (addContentUtilityClass(entityFeature, element, childElement)){
			return true;
		}
		/**
		 * ArrayList<SequenceLocation> featureLocation
		 * ArrayList<SequenceRegionVocabulary> featureLocationType
		 * ArrayList<EntityFeature> memberFeature
		 * ArrayList<Evidence> evidence
		 */
		if (childElement.getName().equals("featureLocation")){
			entityFeature.getFeatureLocation().add(addObjectSequenceLocation(childElement));
			return true;
		}else if (childElement.getName().equals("featureLocationType")){
			entityFeature.getFeatureLocationType().add(addObjectSequenceRegionVocabulary(childElement));
			return true;
		}else if (childElement.getName().equals("memberFeature")){
			entityFeature.getMemberFeature().add(addObjectEntityFeature(childElement));
			return true;
		}else if (childElement.getName().equals("evidence")){
			entityFeature.getEvidence().add(addObjectEvidence(childElement));
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentEntityReference(EntityReference entityReference, Element element, Element childElement){
		if (addContentUtilityClass(entityReference, element, childElement)){
			return true;
		}
		/**
		 * ArrayList<EntityFeature> entityFeature
		 * EntityReferenceTypeVocabulary entityReferenceType
		 * ArrayList<EntityReference> memberEntityReference
		 * ArrayList<String> name
		 * ArrayList<Xref> xRef
		 * ArrayList<Evidence> evidence
		 */
		if (childElement.getName().equals("name")){
			entityReference.getName().add(childElement.getTextTrim());
			return true;
		} else if (childElement.getName().equals("standardName")){
			entityReference.getName().add(childElement.getTextTrim());
			return true;
		} else if (childElement.getName().equals("displayName")){
			insertAtStart(entityReference.getName(),childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("xref")){
			entityReference.getxRef().add(addObjectXref(childElement));
			return true;
		}else if (childElement.getName().equals("evidence")){
			entityReference.getEvidence().add(addObjectEvidence(childElement));
			return true;
		}else if (childElement.getName().equals("entityFeature")){
			entityReference.getEntityFeature().add(addObjectEntityFeature(childElement));
			return true;
		}else if (childElement.getName().equals("entityReferenceType")){
			entityReference.setEntityReferenceType(addObjectEntityReferenceTypeVocabulary(childElement));
			return true;
		}else if (childElement.getName().equals("memberEntityReference")){
			entityReference.getMemberEntityReference().add(addObjectEntityReference(childElement));
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentEvidence(Evidence evidence, Element element, Element childElement){
		if (addContentUtilityClass(evidence, element, childElement)){
			return true;
		}
		/**
		 * ArrayList<Score> confidence
		 * ArrayList<Xref> xref
		 * ArrayList<EvidenceCodeVocabulary> evidenceCode
		 * ArrayList<ExperimentalForm> experimentalForm
		 */
		if (childElement.getName().equals("evidenceCode")){
			Element evidenceCodeVocabularyElement = childElement.getChild("EvidenceCodeVocabulary",bp);
			if (evidenceCodeVocabularyElement!=null){
				evidence.getEvidenceCode().add(addObjectEvidenceCodeVocabulary(evidenceCodeVocabularyElement));
				return true;
			}
			//entityReference.getName().add(childElement.getTextTrim());
			return false;
		}else if (childElement.getName().equals("experimentalForm")){
			evidence.getExperimentalForm().add(addObjectExperimentalForm(childElement));
			return true;
		}else if (childElement.getName().equals("confidence")){
			evidence.getConfidence().add(addObjectScore(childElement));
			return true;
		}else if (childElement.getName().equals("xref")){
			evidence.getxRef().add(addObjectXref(childElement));
			return true;
		}else{
			return false; // no match
		}
	}

	private boolean addContentExperimentalForm(ExperimentalForm experimentalForm, Element element, Element childElement){
		if (addContentUtilityClass(experimentalForm, element, childElement)){
			return true;
		}
		/**
		 * ArrayList<EntityFeature> experimentalFeature
		 * ArrayList<ExperimentalFormVocabulary> experimentalFormDescription
		 * ArrayList<Gene> experimentalFormGene
		 * ArrayList<PhysicalEntity> experimentalFormPhysicalEntity
		 */
		if (childElement.getName().equals("experimentalFeature")){
			experimentalForm.getExperimentalFeature().add(addObjectEntityFeature(childElement));
			return true;
		}else if (childElement.getName().equals("experimentalFormDescription")){
			experimentalForm.getExperimentalFormDescription().add(addObjectExperimentalFormVocabulary(childElement));
			return true;
		}else if (childElement.getName().equals("experimentalFormGene")){
			experimentalForm.getExperimentalFormGene().add(addObjectGene(childElement));
			return true;
		}else if (childElement.getName().equals("experimentalFormPhysicalEntity")){
			experimentalForm.getExperimentalFormPhysicalEntity().add(addObjectPhysicalEntity(childElement));
			return true;
		}else{
			return false; // no match
		}
	}

	private boolean addContentKPrime(KPrime kPrime, Element element, Element childElement){
		if (addContentUtilityClass(kPrime, element, childElement)){
			return true;
		}
		/**
		 * Double ionicStrength
		 * Double kPrime
		 * Double ph
		 * Double pMg
		 * Double temperature
		 */
		if (childElement.getName().equals("ionicStrength")){
			kPrime.setIonicStrength(Double.valueOf(childElement.getTextTrim()));
			return true;
		}else if (childElement.getName().equals("kPrime")){
			kPrime.setkPrime(Double.valueOf(childElement.getTextTrim()));
			return true;
		}else if (childElement.getName().equals("ph")){
			kPrime.setPh(Double.valueOf(childElement.getTextTrim()));
			return true;
		}else if (childElement.getName().equals("pMg")){
			kPrime.setpMg(Double.valueOf(childElement.getTextTrim()));
			return true;
		}else if (childElement.getName().equals("temperature")){
			kPrime.setTemperature(Double.valueOf(childElement.getTextTrim()));
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentProvenance(Provenance provenance, Element element, Element childElement){
		if (addContentUtilityClass(provenance, element, childElement)){
			return true;
		}
		/**
		 * ArrayList<String> name
		 * ArrayList<Xref> xRef
		 */
		if (childElement.getName().equals("name")){
			provenance.getName().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("standardName")){
			provenance.getName().add(childElement.getTextTrim());
			return true;
		} else if (childElement.getName().equals("displayName")){
			insertAtStart(provenance.getName(),childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("xref")){
			provenance.getxRef().add(addObjectXref(childElement));
			return true;
		}else{
			return false; // no match
		}
	}

	private boolean addContentScore(Score score, Element element, Element childElement){
		if (addContentUtilityClass(score, element, childElement)){
			return true;
		}
		/**
		 * Provenance scoreSource
		 * String value
		 */
		if (childElement.getName().equals("scoreSource")){
			score.setScoreSource(addObjectProvenance(childElement));
			return true;
		}else if (childElement.getName().equals("value")){
			score.setValue(childElement.getTextTrim());
			return true;
		}else{
			return false; // no match
		}
	}

	private boolean addContentSequenceLocation(SequenceLocation sequenceLocation, Element element, Element childElement){
		if (addContentUtilityClass(sequenceLocation, element, childElement)){
			return true;
		}
		/**
		 * 
		 */
		return false; // no match
	}
	
	private boolean addContentStoichiometry(Stoichiometry stoichiometry, Element element, Element childElement){
		if (addContentUtilityClass(stoichiometry, element, childElement)){
			return true;
		}
		/**
		 * PhysicalEntity physicalEntity
		 * Double stoichiometricCoefficient
		 */
		if (childElement.getName().equals("physicalEntity")){
			stoichiometry.setPhysicalEntity(addObjectPhysicalEntity(childElement));
			return true;
		}else if (childElement.getName().equals("stoichiometricCoefficient")){
			stoichiometry.setStoichiometricCoefficient(Double.valueOf(childElement.getTextTrim()));
			return true;
		}else{
			return false; // no match
		}
	}

	private boolean addContentXref(Xref xRef, Element element, Element childElement){
		if (addContentUtilityClass(xRef, element, childElement)){
			return true;
		}
		/**
		 * String db
		 * String dbVersion
		 * String id
		 * String idVersion
		 */
		if (childElement.getName().equals("db")){
			xRef.setDb(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("id")){
			xRef.setId(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("dbVersion")){
			xRef.setDbVersion(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("idVersion")){
			xRef.setIdVersion(childElement.getTextTrim());
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentPublicationXref(PublicationXref publicationXRef, Element element, Element childElement){
		if (addContentXref(publicationXRef, element, childElement)){
			return true;
		}
		/**
		 * ArrayList<String> author
		 * ArrayList<String> source
		 * String title
		 * ArrayList<String> url
		 * Integer year
		 */
		if (childElement.getName().equals("author")){
			publicationXRef.getAuthor().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("source")){
			publicationXRef.getSource().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("title")){
			publicationXRef.setTitle(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("url")){
			publicationXRef.getUrl().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("year")){
			publicationXRef.setYear(Integer.valueOf(childElement.getTextTrim()));
			return true;
		}else{
			return false; // no match
		}
	}

	private boolean addContentRelationshipXref(RelationshipXref relationshipXref, Element element, Element childElement){
		if (addContentXref(relationshipXref, element, childElement)){
			return true;
		}
		/**
		 * ArrayList<RelationshipTypeVocabulary> relationshipType
		 */
		if (childElement.getName().equals("relationshipType")){
			relationshipXref.getRelationshipType().add(addObjectRelationshipTypeVocabulary(childElement));
			return true;
		}else{
			return false; // no match
		}
	}

	private boolean addContentUnificationXref(UnificationXref unificationXref, Element element, Element childElement){
		if (addContentXref(unificationXref, element, childElement)){
			return true;
		}
		/**
		 * 
		 */
		return false; // no match
	}
	
	private boolean addContentSequenceInterval(SequenceInterval sequenceInterval, Element element, Element childElement){
		if (addContentSequenceLocation(sequenceInterval, element, childElement)){
			return true;
		}
		/**
		 * SequenceSite sequenceIntervalBegin
		 * SequenceSite sequenceIntervalEnd
		 */
		if (childElement.getName().equals("sequenceIntervalBegin")){
			sequenceInterval.setSequenceIntervalBegin(addObjectSequenceSite(childElement));
			return true;
		}else if (childElement.getName().equals("sequenceIntervalEnd")) {
			sequenceInterval.setSequenceIntervalEnd(addObjectSequenceSite(childElement));
			return true;
		}else {
			return false; // no match
		}
	}

	private boolean addContentSequenceSite(SequenceSite sequenceSite, Element element, Element childElement){
		if (addContentSequenceLocation(sequenceSite, element, childElement)){
			return true;
		}
		/**
		 * String positionStatusv
		 * Integer sequencePosition
		 */
		if (childElement.getName().equals("positionStatus")){
			sequenceSite.setPositionStatus(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("sequencePosition")) {
			sequenceSite.setSequencePosition(Integer.valueOf(childElement.getTextTrim()));
			return true;
		}else {
			return false; // no match
		}
	}
	
	// TODO: fix the items with empty body: <PathwayStep />
	private boolean addContentPathwayStep(PathwayStep pathwayStep, Element element, Element childElement){
		if (addContentUtilityClass(pathwayStep, element, childElement)){
			return true;
		}
		/**
		 * ArrayList<PathwayStep> nextStep
		 * ArrayList<Interaction> stepProcessInteraction
		 * ArrayList<Pathway> stepProcessPathway
		 * ArrayList<Evidence> evidence
		 */
		if (childElement.getName().equals("nextStep")){
			pathwayStep.getNextStep().add(addObjectPathwayStep(childElement));
			return true;
		}else if (childElement.getName().equals("stepProcess")){
			if (childElement.getChildren().size()==0){
				// if there are no children it must be a resource inside another object
				// we don't know yet whether it's Interaction or Pathway, we make a proxy for each
				// at the time when we solve the references we'll find out which is fake
				InteractionProxy proxyI = new InteractionProxy();
				addAttributes(proxyI, childElement);
				pathwayModel.add(proxyI);
				pathwayStep.getStepProcessInteraction().add(proxyI);
				PathwayProxy proxyP = new PathwayProxy();
				addAttributes(proxyP, childElement);
				pathwayModel.add(proxyP);
				pathwayStep.getStepProcessPathway().add(proxyP);
				return true;
			} else {
				// it's a real stepProcess object nested here - we ignore this situation for now
				showIgnored(childElement, "Found NESTED child.");
			}
 			return false;
		}else if (childElement.getName().equals("evidence")) {
			pathwayStep.getEvidence().add(addObjectEvidence(childElement));
			return true;
		}else{
			return false; // no match
		}
	}

	private boolean addContentBiochemicalPathwayStep(BiochemicalPathwayStep biochemicalPathwayStep, Element element, 
			Element childElement){
		if (addContentPathwayStep(biochemicalPathwayStep, element, childElement)){
			return true;
		}
		/**
		 * Conversion stepConversion
		 * String stepDirection
		 */
		if (childElement.getName().equals("stepConversion")){
			biochemicalPathwayStep.setStepConversion(addObjectConversion(childElement));
			return true;
		}else if (childElement.getName().equals("stepDirection")) {
			biochemicalPathwayStep.setStepDirection(childElement.getTextTrim());
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentProteinReference(ProteinReference proteinReference, Element element, Element childElement){
		if (addContentEntityReference(proteinReference, element, childElement)){
			return true;
		}
		/**
		 * BioSource organism
		 * String sequence
		 */
		if (childElement.getName().equals("organism")){
			proteinReference.setOrganism(addObjectBioSource(childElement));
			return true;
		}else if (childElement.getName().equals("sequence")) {
			proteinReference.setSequence(childElement.getTextTrim());
			return true;
		}else{
			return false; // no match
		}
	}

	private boolean addContentDnaReference(DnaReference dnaReference, Element element, Element childElement){
		if (addContentEntityReference(dnaReference, element, childElement)){
			return true;
		}
		/**
		 * ArrayList<DnaRegionReference> subRegion
		 * BioSource organism
		 * ArrayList<RnaRegionReference> subRegion
		 * String sequence
		 */
		if (childElement.getName().equals("organism")){
			dnaReference.setOrganism(addObjectBioSource(childElement));
			return true;
		}else if (childElement.getName().equals("dnaSubRegion")) {		// obsolete
			dnaReference.getDnaSubRegion().add(addObjectDnaRegionReference(childElement));
			return true;
		}else if (childElement.getName().equals("rnaSubRegion")) {		// obsolete
			dnaReference.getRnaSubRegion().add(addObjectRnaRegionReference(childElement));
			return true;
		}else if (childElement.getName().equals("sequence")) {
			dnaReference.setSequence(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("subRegion")){
			if (childElement.getChildren().size()==0){
				// if there are no children it must be a resource inside another object
				// we don't know yet whether it's DnaRegionReference or RnaRegionReference, we make a proxy for each
				// at the time when we solve the references we'll find out which is fake
				DnaRegionReference dReg = new DnaRegionReferenceProxy();
				addAttributes(dReg, childElement);
				pathwayModel.add(dReg);
				dnaReference.getDnaSubRegion().add(dReg);
				RnaRegionReference rReg = new RnaRegionReferenceProxy();
				addAttributes(rReg, childElement);
				pathwayModel.add(rReg);
				dnaReference.getRnaSubRegion().add(rReg);
				return true;
			} else {
				// it's the real object declaration - we ignore this situation for now
				showIgnored(childElement, "Found NESTED child.");
				return false;
			}
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentRnaReference(RnaReference rnaReference, Element element, Element childElement){
		if (addContentEntityReference(rnaReference, element, childElement)){
			return true;
		}
		/**
		 * ArrayList<DnaRegionReference> dnaSubRegion
		 * BioSource organism
		 * ArrayList<RnaRegionReference> rnaSubRegion
		 * String sequence
		 */
		if (childElement.getName().equals("organism")){
			rnaReference.setOrganism(addObjectBioSource(childElement));
			return true;
		}else if (childElement.getName().equals("dnaSubRegion")) {
			rnaReference.getDnaSubRegion().add(addObjectDnaRegionReference(childElement));
			return true;
		}else if (childElement.getName().equals("rnaSubRegion")) {
			rnaReference.getRnaSubRegion().add(addObjectRnaRegionReference(childElement));
			return true;
		}else if (childElement.getName().equals("sequence")) {
			rnaReference.setSequence(childElement.getTextTrim());
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentDnaRegionReference(DnaRegionReference dnaRegionReference, Element element, Element childElement
			){
		if (addContentEntityReference(dnaRegionReference, element, childElement)){
			return true;
		}
		/**
		 * SequenceLocation absoluteRegion
		 * ArrayList<DnaRegionReference> dnaSubRegion
		 * BioSource organism
		 * SequenceRegionVocabulary regionType;
		 * ArrayList<RnaRegionReference> rnaSubRegion
		 * String sequence
		 */
		if (childElement.getName().equals("organism")){
			dnaRegionReference.setOrganism(addObjectBioSource(childElement));
			return true;
		}else if (childElement.getName().equals("absoluteRegion")) {
			dnaRegionReference.setAbsoluteRegion(addObjectSequenceLocation(childElement));
			return true;
		}else if (childElement.getName().equals("dnaSubRegion")) {
			dnaRegionReference.getDnaSubRegion().add(addObjectDnaRegionReference(childElement));
			return true;
		}else if (childElement.getName().equals("rnaSubRegion")) {
			dnaRegionReference.getRnaSubRegion().add(addObjectRnaRegionReference(childElement));
			return true;
		}else if (childElement.getName().equals("regionType")) {
			dnaRegionReference.setRegionType(addObjectSequenceRegionVocabulary(childElement));
			return true;
		}else if (childElement.getName().equals("sequence")) {
			dnaRegionReference.setSequence(childElement.getTextTrim());
			return true;
		}else{
			return false; // no match
		}
	}

	private boolean addContentRnaRegionReference(RnaRegionReference rnaRegionReference, Element element, Element childElement){
		if (addContentEntityReference(rnaRegionReference, element, childElement)){
			return true;
		}
		/**
		 * SequenceLocation absoluteRegion
		 * ArrayList<DnaRegionReference> dnaSubRegion
		 * BioSource organism
		 * SequenceRegionVocabulary regionType;
		 * ArrayList<RnaRegionReference> rnaSubRegion
		 * String sequence
		 */
		if (childElement.getName().equals("organism")){
			rnaRegionReference.setOrganism(addObjectBioSource(childElement));
			return true;
		}else if (childElement.getName().equals("absoluteRegion")) {
			rnaRegionReference.setAbsoluteRegion(addObjectSequenceLocation(childElement));
			return true;
		}else if (childElement.getName().equals("dnaSubRegion")) {
			rnaRegionReference.getDnaSubRegion().add(addObjectDnaRegionReference(childElement));
			return true;
		}else if (childElement.getName().equals("rnaSubRegion")) {
			rnaRegionReference.getRnaSubRegion().add(addObjectRnaRegionReference(childElement));
			return true;
		}else if (childElement.getName().equals("regionType")) {
			rnaRegionReference.setRegionType(addObjectSequenceRegionVocabulary(childElement));
			return true;
		}else if (childElement.getName().equals("sequence")) {
			rnaRegionReference.setSequence(childElement.getTextTrim());
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentSmallMoleculeReference(SmallMoleculeReference smallMoleculeReference, Element element, 
			Element childElement){
		if (addContentEntityReference(smallMoleculeReference, element, childElement)){
			return true;
		}
		/**
		 * String chemicalFormula
		 * Double molecularWeight
		 * ChemicalStructure structure
		 */
		if (childElement.getName().equals("chemicalFormula")){
			smallMoleculeReference.setChemicalFormula(childElement.getTextTrim());
			return true;
		}else if(childElement.getName().equals("molecularWeight")) {	// why not float??
			smallMoleculeReference.setMolecularWeight(Double.valueOf(childElement.getTextTrim()));
			return true;
		}else if(childElement.getName().equals("structure")) {
			if (childElement.getChildren().size() == 0){
				ChemicalStructureProxy proxy = new ChemicalStructureProxy();
				addAttributes(proxy, childElement);
				pathwayModel.add(proxy);
				return true;
			}
			Element chemicalStructureElement = childElement.getChild("ChemicalStructure",bp);
			if (chemicalStructureElement!=null){
				smallMoleculeReference.setStructure(addObjectChemicalStructure(chemicalStructureElement));
				return true;
			}
			return false;
		}
		return false; // no match
	}

	private boolean addContentBindingFeature(BindingFeature bindingFeature, Element element, Element childElement){
		if (addContentEntityFeature(bindingFeature, element, childElement)){
			return true;
		}
		/**
		 * BindingFeature bindsTo
		 * Boolean intraMolecular
		 */
		return false; // no match
	}
	
	private boolean addContentFragmentFeature(FragmentFeature fragmentFeature, Element element, Element childElement){
		if (addContentEntityFeature(fragmentFeature, element, childElement)){
			return true;
		}
		/**
		 * 
		 */
		return false; // no match
	}
	
	private boolean addContentModificationFeature(ModificationFeature modificationFeature, Element element, Element childElement){
		if (addContentEntityFeature(modificationFeature, element, childElement)){
			return true;
		}
		/**
		 * SequenceModificationVocabulary modificationType
		 */
		if (childElement.getName().equals("modificationType")){
			modificationFeature.setModificationType(addObjectSequenceModificationVocabulary(childElement));
			return true;
		}else{
			return false; // no match
		}
	}

	private boolean addContentCovalentBindingFeature(CovalentBindingFeature covalentBindingFeature, Element element, 
			Element childElement){
		if (addContentBindingFeature(covalentBindingFeature, element, childElement)){
			return true;
		}
		if (addContentModificationFeature(covalentBindingFeature, element, childElement)){
			return true;
		}
		/**
		 * SequenceModificationVocabulary modificationType
		 */
		if (childElement.getName().equals("modificationType")){
			covalentBindingFeature.setModificationType(addObjectSequenceModificationVocabulary(childElement));
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentDeltaG(DeltaG deltaG, Element element, Element childElement){
		if (addContentUtilityClass(deltaG, element, childElement)){
			return true;
		}
		/**
		 * Double deltaGPrime0;
		 * Double ionicStrength;
		 * Double ph;
		 * Double pMg;
		 * Double temperature;
		 */
		if(childElement.getName().equals("deltaGPrime0")) {	// why not float??
			deltaG.setDeltaGPrime0(Double.valueOf(childElement.getTextTrim()));
			return true;
		}else if(childElement.getName().equals("ionicStrength")) {
			deltaG.setIonicStrength(Double.valueOf(childElement.getTextTrim()));
			return true;
		}else if(childElement.getName().equals("ph")) {
			deltaG.setPh(Double.valueOf(childElement.getTextTrim()));
			return true;
		}else if(childElement.getName().equals("pMg")) {
			deltaG.setpMg(Double.valueOf(childElement.getTextTrim()));
			return true;
		}else if(childElement.getName().equals("temperature")) {
			deltaG.setTemperature(Double.valueOf(childElement.getTextTrim()));
			return true;
		} else {
		return false; // no match
		}
	}

	private boolean addContentPhenotypeVocabulary(PhenotypeVocabulary phenotypeVocabulary, Element element, Element childElement){
		if (addContentControlledVocabulary(phenotypeVocabulary, element, childElement)){
			return true;
		}
		/**
		 * String patoData
		 */
		if(childElement.getName().equals("patoData")) {
			phenotypeVocabulary.setPatoData(childElement.getTextTrim());
			return true;
		}		
		return false; // no match
	}
	
	private boolean addContentPhysicalEntity(PhysicalEntity physicalEntity, Element element, Element childElement){
		if (addContentEntity(physicalEntity, element, childElement)){
			return true;
		}
		/**
		 * CellularLocationVocabulary cellularLocation
		 * ArrayList<EntityFeature> feature
		 * ArrayList<PhysicalEntity> memberPhysicalEntity
		 * ArrayList<EntityFeature> notFeature
		 */
		if (childElement.getName().equals("feature")){
			physicalEntity.getFeature().add(addObjectEntityFeature(childElement));
			return true;
		}else if (childElement.getName().equals("notFeature")){
			physicalEntity.getNotFeature().add(addObjectEntityFeature(childElement));
			return true;
		}else if (childElement.getName().equals("memberPhysicalEntity")){
			physicalEntity.getMemberPhysicalEntity().add(addObjectPhysicalEntity(childElement));
			return true;
		}else if (childElement.getName().equals("cellularLocation")){
			physicalEntity.setCellularLocation(addObjectCellularLocationVocabulary(childElement));
			return true;
		}else{
			return false; // no match
		}
	}

	private <T> void insertAtStart(ArrayList<T> list, T newElement){
		if (list.size()==0){
			list.add(newElement);
			return;
		}
		list.add(null);  // extend the array and copy (n-1) into (n).
		for (int i = list.size()-1; i>0; i--){
			list.set(i,list.get(i-1));
		}
		list.set(0,newElement);
	}
	
	private boolean addContentComplex(Complex complex, Element element, Element childElement){
		if (addContentPhysicalEntity(complex, element, childElement)){
			return true;
		}
		/**
		 * ArrayList<PhysicalEntity> component
		 * ArrayList<Stoichiometry> componentStoichiometry
		 */
		if (childElement.getName().equals("component")){
			complex.getComponents().add(addObjectPhysicalEntity(childElement));
			return true;
		}else if(childElement.getName().equals("componentStoichiometry")) {
			complex.getComponentStoichiometry().add(addObjectStoichiometry(childElement));
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentGroupObject(GroupObject gObject, Element element, Element childElement){
		if (addContentEntity(gObject, element, childElement)){
			return true;
		}
		/**
		 * HashSet<BioPaxObject> groupedObjects
		 * Type type
		 */
		if (childElement.getName().equals("groupedObject")){
			BioPaxObjectProxy proxyE = new BioPaxObjectProxy();
			addAttributes(proxyE, childElement);
			pathwayModel.add(proxyE);
			gObject.getGroupedObjects().add(proxyE);
			return true;
		}else if(childElement.getName().equals("type")) {
			gObject.setType(childElement.getTextTrim());
			return true;
		}else{
			return false; // no match
		}
	}

	private boolean addContentDna(Dna dna, Element element, Element childElement){
		if (addContentPhysicalEntity(dna, element, childElement)){
			return true;
		}
		/**
		 * EntityReference entityReference
		 */
		if(childElement.getName().equals("entityReference")) {
			dna.setEntityReference(addObjectEntityReference(childElement));
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentDnaRegion(DnaRegion dnaRegion, Element element, Element childElement){
		if (addContentPhysicalEntity(dnaRegion, element, childElement)){
			return true;
		}
		/**
		 * EntityReference entityReference
		 */
		if(childElement.getName().equals("entityReference")) {
			dnaRegion.setEntityReference(addObjectEntityReference(childElement));
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentProtein(Protein protein, Element element, Element childElement){
		if (addContentPhysicalEntity(protein, element, childElement)){
			return true;
		}
		/**
		 * EntityReference entityReference
		 */
		if(childElement.getName().equals("entityReference")) {
			protein.setEntityReference(addObjectEntityReference(childElement));
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentRna(Rna rna, Element element, Element childElement){
		if (addContentPhysicalEntity(rna, element, childElement)){
			return true;
		}
		/**
		 * EntityReference entityReference
		 */
		if(childElement.getName().equals("entityReference")) {
			rna.setEntityReference(addObjectEntityReference(childElement));
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentRnaRegion(RnaRegion rnaRegion, Element element, Element childElement){
		if (addContentPhysicalEntity(rnaRegion, element, childElement)){
			return true;
		}
		/**
		 * EntityReference entityReference
		 */
		if(childElement.getName().equals("entityReference")) {
			rnaRegion.setEntityReference(addObjectEntityReference(childElement));
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentSmallMolecule(SmallMolecule smallMolecule, Element element, Element childElement){
		if (addContentPhysicalEntity(smallMolecule, element, childElement)){	// memberPhysicalEntity
			return true;
		}
		/**
		 * EntityReference entityReference
		 */
		if(childElement.getName().equals("entityReference")) {
			smallMolecule.setEntityReference(addObjectEntityReference(childElement));
			return true;
		}else{
			return false; // no match
		}
	}

	private boolean addContentPathway(Pathway pathway, Element element, Element childElement){
		if (addContentEntity(pathway, element, childElement)){
			return true;
		}
		/**
		 * BioSource organism
		 * ArrayList<Interaction> pathwayComponentInteraction
		 * ArrayList<Pathway> pathwayComponentPathway
		 * ArrayList<PathwayStep> pathwayOrder
		 */
		if (childElement.getName().equals("organism")){
			pathway.setOrganism(addObjectBioSource(childElement));
			return true;
		}else if (childElement.getName().equals("pathwayOrder")){
			pathway.getPathwayOrder().add(addObjectPathwayStep(childElement));
			return true;
		}else if (childElement.getName().equals("pathwayComponent")){
			if (childElement.getChildren().size()==0){
				// if there are no children it must be a resource inside another object
				// we don't know yet whether it's Interaction or Pathway, we make a proxy for each
				// at the time when we solve the references we'll find out which is fake
				InteractionProxy proxyI = new InteractionProxy();
				addAttributes(proxyI, childElement);
				pathwayModel.add(proxyI);
				pathway.getPathwayComponentInteraction().add(proxyI);
				PathwayProxy proxyP = new PathwayProxy();
				addAttributes(proxyP, childElement);
				pathwayModel.add(proxyP);
				pathway.getPathwayComponentPathway().add(proxyP);
				return true;
			} else {
				// it's the real object declaration - could be an interaction or a pathway
				for (Object child : childElement.getChildren()){
					if (child instanceof Element){
						if(((Element)child).getName().equals("Pathway")) {
							Pathway thingie = addObjectPathway((Element)child);
							pathway.getPathwayComponentPathway().add(thingie);
							return true;
						} else {
							Interaction thingie = (Interaction)addObjectBioPaxObjectSubclass((Element)child);
							if (thingie == null) {
								return false;
							} else {
								pathway.getPathwayComponentInteraction().add(thingie);
								return true;
							}
						}
					}
				}
			}
 			return false;
		}else{
			return false;
		}
	}

	private boolean addContentInteraction(Interaction interaction, Element element, Element childElement){
		if (addContentEntity(interaction,element,childElement)){
			return true;
		}
		if (childElement.getName().equals("participant")){
//			interaction.getParticipants().add(addObjectEntity(childElement));
			showIgnored(childElement, "participant Entity not yet implemented for Interactions");
			return false;
		}else if(childElement.getName().equals("interactionType")) {
			interaction.getInteractionTypes().add(addObjectInteractionVocabulary(childElement));
			return true;
		}else{
			return false;
		}
	}

	private boolean addContentGeneticInteraction(GeneticInteraction geneticInteraction, Element element, Element childElement){
		if (addContentInteraction(geneticInteraction,element,childElement)){
			return true;
		}
		/**
		 * ArrayList<Score> interactionScore
		 * PhenotypeVocabulary phenotype;
		 */
		if (childElement.getName().equals("interactionScore")){
			geneticInteraction.getInteractionScore().add(addObjectScore(childElement));
			return true;
		}else if (childElement.getName().equals("phenotype")){
			geneticInteraction.setPhenotype(addObjectPhenotypeVocabulary(childElement));
			return true;
		}else{
			return false;
		}
	}

	private boolean addContentMolecularInteraction(MolecularInteraction molecularInteraction, Element element, Element childElement){
		if (addContentInteraction(molecularInteraction,element,childElement)){
			return true;
		}
		/**
		 */
		return false;
	}
	
	// TODO: implement this
	private boolean addContentTemplateReaction(TemplateReaction templateReaction, Element element, Element childElement){
		if (addContentInteraction(templateReaction,element,childElement)){
			return true;
		}
		/**
		 * ArrayList<Dna> productDna
		 * ArrayList<Protein> productProtein
		 * ArrayList<Rna> productRna
		 * String templateDirection
		 * Dna templateDna
		 * DnaRegion templateDnaRegion
		 * Rna templateRna
		 * RnaRegion templateRnaRegion
		 */
		showIgnored(childElement, "TemplateReaction not yet implemented");
		return false;
	}

	private boolean addContentControl(Control control, Element element, Element childElement){
		if (addContentInteraction(control,element,childElement)){
			return true;
		}
		/**
		 * InteractionImpl controlledInteraction
		 * Pathway controlledPathway
		 * String controlType
		 * ArrayList<Pathway> pathwayControllers
		 * ArrayList<PhysicalEntity> physicalControllers
		 */
		if(childElement.getName().equals("controller")) {
//			Attribute resourceAttribute = childElement.getAttribute("resource",rdf);
//			if (resourceAttribute != null){
			if (childElement.getChildren().size()==0){
				// if there are no children it must be a resource inside another object
				// we don't know yet whether it's Interaction or Pathway, we make a proxy for each
				// at the time when we solve the references we'll find out which is fake
				PhysicalEntityProxy proxyE = new PhysicalEntityProxy();
				addAttributes(proxyE, childElement);
				pathwayModel.add(proxyE);
				control.addPhysicalController(proxyE);
				PathwayProxy proxyP = new PathwayProxy();
				addAttributes(proxyP, childElement);
				pathwayModel.add(proxyP);
				control.getPathwayControllers().add(proxyP);
				return true;
			} else {
				for (Object child : element.getChildren()){
					if (child instanceof Element){
						if(((Element)child).getName().equals("Pathway")) {
							Pathway thingie = addObjectPathway((Element)child);
							control.getPathwayControllers().add(thingie);
							return true;
						} else if(((Element)child).getName().equals("Complex")) {
							Complex thingie = addObjectComplex((Element)child);
							control.getPhysicalControllers().add(thingie);
							return true;
						} else if(((Element)child).getName().equals("Dna")) {
							Dna thingie = addObjectDna((Element)child);
							control.getPhysicalControllers().add(thingie);
							return true;
						} else if(((Element)child).getName().equals("DnaRegion")) {
							DnaRegion thingie = addObjectDnaRegion((Element)child);
							control.getPhysicalControllers().add(thingie);
							return true;
						} else if(((Element)child).getName().equals("Rna")) {
							Rna thingie = addObjectRna((Element)child);
							control.getPhysicalControllers().add(thingie);
							return true;
						} else if(((Element)child).getName().equals("RnaRegion")) {
							RnaRegion thingie = addObjectRnaRegion((Element)child);
							control.getPhysicalControllers().add(thingie);
							return true;
						} else if(((Element)child).getName().equals("Protein")) {
							Protein thingie = addObjectProtein((Element)child);
							control.getPhysicalControllers().add(thingie);
							return true;
						} else if(((Element)child).getName().equals("SmallMolecule")) {
							SmallMolecule thingie = addObjectSmallMolecule((Element)child);
							control.getPhysicalControllers().add(thingie);
							return true;
						} else {
							return false;
						}
					}
				}
			}
			return false;	
		}else if(childElement.getName().equals("controlled")) {
			if (childElement.getChildren().size()==0){
				InteractionProxy proxyI = new InteractionProxy();
				addAttributes(proxyI, childElement);
				pathwayModel.add(proxyI);
				control.setControlledInteraction(proxyI);
				PathwayProxy proxyP = new PathwayProxy();
				addAttributes(proxyP, childElement);
				pathwayModel.add(proxyP);
				control.setControlledPathway(proxyP);
				return true;
			} else {
				for (Object child : childElement.getChildren()){
					if (child instanceof Element){
						
						if(((Element)child).getName().equals("Pathway")) {
							Pathway thingie = addObjectPathway((Element)child);
							control.setControlledPathway(thingie);
							return true;
						} else {
							Interaction thingie = (Interaction)addObjectBioPaxObjectSubclass((Element)child);
							if (thingie == null) {
								return false;
							} else {
								control.setControlledInteraction(thingie);
								return true;
							}
						}
						
//						if(((Element)child).getName().equals("Pathway")) {
//							Pathway thingie = addObjectPathway((Element)child);
//							control.setControlledPathway(thingie);
//							return true;
//						} else if(((Element)child).getName().equals("Interaction")) {
//							Interaction thingie = addObjectInteraction((Element)child);
//							control.setControlledInteraction(thingie);
//							return true;
//						} else if(((Element)child).getName().equals("Control")) {
//							Control thingie = addObjectControl((Element)child);
//							control.setControlledInteraction(thingie);
//							return true;
//						} else if(((Element)child).getName().equals("Modulation")) {
//							Modulation thingie = addObjectModulation((Element)child);
//							control.setControlledInteraction(thingie);
//							return true;
//						} else if(((Element)child).getName().equals("Catalysis")) {
//							Catalysis thingie = addObjectCatalysis((Element)child);
//							control.setControlledInteraction(thingie);
//							return true;
//						} else if(((Element)child).getName().equals("TemplateReactionRegulation")) {
//							TemplateReactionRegulation thingie = addObjectTemplateReactionRegulation((Element)child);
//							control.setControlledInteraction(thingie);
//							return true;
//						} else if(((Element)child).getName().equals("Conversion")) {
//							Conversion thingie = addObjectConversion((Element)child);
//							control.setControlledInteraction(thingie);
//							return true;
//						} else if(((Element)child).getName().equals("BiochemicalReaction")) {
//							BiochemicalReaction thingie = addObjectBiochemicalReaction((Element)child);
//							control.setControlledInteraction(thingie);
//							return true;
//						} else if(((Element)child).getName().equals("TransportWithBiochemicalReaction")) {
//							TransportWithBiochemicalReaction thingie = addObjectTransportWithBiochemicalReaction((Element)child);
//							control.setControlledInteraction(thingie);
//							return true;
//						} else if(((Element)child).getName().equals("ComplexAssembly")) {
//							ComplexAssembly thingie = addObjectComplexAssembly((Element)child);
//							control.setControlledInteraction(thingie);
//							return true;
//						} else if(((Element)child).getName().equals("Degradation")) {
//							Degradation thingie = addObjectDegradation((Element)child);
//							control.setControlledInteraction(thingie);
//							return true;
//						} else if(((Element)child).getName().equals("Transport")) {
//							Transport thingie = addObjectTransport((Element)child);
//							control.setControlledInteraction(thingie);
//							return true;
//						} else if(((Element)child).getName().equals("GeneticInteraction")) {
//							GeneticInteraction thingie = addObjectGeneticInteraction((Element)child);
//							control.setControlledInteraction(thingie);
//							return true;
//						} else if(((Element)child).getName().equals("MolecularInteraction")) {
//							MolecularInteraction thingie = addObjectMolecularInteraction((Element)child);
//							control.setControlledInteraction(thingie);
//							return true;
//						} else if(((Element)child).getName().equals("TemplateReaction")) {
//							TemplateReaction thingie = addObjectTemplateReaction((Element)child);
//							control.setControlledInteraction(thingie);
//							return true;
//						} else {
//							return false;
//						}
					}
				}
			}
			return false;	
		}else if (childElement.getName().equals("controlType")){
			control.setControlType(childElement.getTextTrim());
			return true;
		}else{
			return false;
		}
	}
	
	private boolean addContentConversion(Conversion conversion, Element element, Element childElement){
		if (addContentInteraction(conversion,element,childElement)){
			return true;
		}
		/**
		 * String getConversionDirection();
		 * ArrayList<PhysicalEntity> getLeft();
		 * ArrayList<Stoichiometry> getParticipantStoichiometry();
		 * ArrayList<PhysicalEntity> getRight();
		 * Boolean getSpontaneous();
		 */
		if (childElement.getName().equals("left")){
			conversion.addLeft(addObjectPhysicalEntity(childElement));
			return true;
		}else if (childElement.getName().equals("right")){
			conversion.addRight(addObjectPhysicalEntity(childElement));
			return true;
		}else if(childElement.getName().equals("participantStoichiometry")) {
			conversion.getParticipantStoichiometry().add(addObjectStoichiometry(childElement));
			return true;
		}else if(childElement.getName().equals("spontaneous")) {
			conversion.setSpontaneous(Boolean.valueOf(childElement.getTextTrim()));
			return true;
		}else if(childElement.getName().equals("conversionDirection")) {
			conversion.setConversionDirection(childElement.getTextTrim());
			return true;
		}else{
			return false;
		}
	}
	
	private boolean addContentComplexAssembly(ComplexAssembly complexAssembly, Element element, Element childElement){
		if (addContentConversion(complexAssembly,element,childElement)){
			return true;
		}
		/**
		 * 
		 */
		return false;
	}

	private boolean addContentDegradation(Degradation degradation, Element element, Element childElement){
		if (addContentConversion(degradation,element,childElement)){
			return true;
		}
		/**
		 * 
		 */
		return false;
	}
	
	private boolean addContentTransport(Transport transport, Element element, Element childElement){
		if (addContentConversion(transport,element,childElement)){
			return true;
		}
		/**
		 * 
		 */
		return false;
	}
	
	private boolean addContentBiochemicalReaction(BiochemicalReaction biochemicalReaction, Element element, Element childElement){
		if (addContentConversion(biochemicalReaction,element,childElement)){
			return true;
		}
		/**
		 * 	ArrayList<DeltaG> getDeltaG();
		 *  ArrayList<Double> getDeltaH();
		 *  ArrayList<Double> getDeltaS();
		 *  ArrayList<String> getECNumber();
		 *  ArrayList<KPrime> getkEQ();
		 */
		if(childElement.getName().equals("deltaG")) {
			biochemicalReaction.getDeltaG().add(addObjectDeltaG(childElement));
			return true;
		}else if(childElement.getName().equals("kEQ")) {
			biochemicalReaction.getkEQ().add(addObjectKPrime(childElement));
			return true;
		}else if(childElement.getName().equals("eCNumber")) {
			biochemicalReaction.getECNumber().add(childElement.getTextTrim());
			return true;
		}else if(childElement.getName().equals("deltaS")) {
			biochemicalReaction.getDeltaS().add(Double.valueOf(childElement.getTextTrim()));
			return true;
		}else if(childElement.getName().equals("deltaH")) {
			biochemicalReaction.getDeltaH().add(Double.valueOf(childElement.getTextTrim()));
			return true;
		}else{
			return false;
		}
	}
	
	private boolean addContentTransportWithBiochemicalReaction(TransportWithBiochemicalReaction transportWithBiochemicalReaction, 
			Element element, Element childElement){
		if (addContentBiochemicalReaction(transportWithBiochemicalReaction,element,childElement)){
			return true;
		}
//		if (addContentTransport(transportWithBiochemicalReaction,element,childElement)){
//			return true;
//		}
		/**
		 * 
		 */
		return false;
	}

	
	private boolean addContentCatalysis(Catalysis catalysis, Element element, Element childElement){
		if (addContentControl(catalysis,element,childElement)){
			return true;
		}
		/**
		 * String catalysisDirection
		 * ArrayList<PhysicalEntity> cofactors
		 */
		if (childElement.getName().equals("catalysisDirection")){
			catalysis.setCatalysisDirection(childElement.getTextTrim());
			return true;
		}else if(childElement.getName().equals("cofactor")) {
			catalysis.addCofactor(addObjectPhysicalEntity(childElement));
			return true;
		}else{
			return false;
		}
	}

	private boolean addContentModulation(Modulation modulation, Element element, Element childElement){
		if (addContentControl(modulation,element,childElement)){
			return true;
		}
		return false;
	}
	
	private boolean addContentTemplateReactionRegulation(TemplateReactionRegulation templateReactionRegulation, Element element, 
			Element childElement){
		if (addContentControl(templateReactionRegulation,element,childElement)){
			return true;
		}
		return false;
	}
	
	private boolean addContentGene(Gene gene, Element element, Element childElement){
		if (addContentEntity(gene,element,childElement)){
			return true;
		}
		return false;
	}
	
	private boolean addContentSBEntity(SBEntity sbEntity, Element element, Element child) {
		if (addContentBioPaxObject(sbEntity, element, child)){
			return true;
		}
		if (child.getName().equals("sbTerm")){
			sbEntity.getSBTerm().add(addObjectSBVocabulary(child));
			return true;
		} else if (child.getName().equals("sbSubEntity")){
			sbEntity.getSBSubEntity().add(addObjectSBEntity(child));
			return true;
		}else{
			return false;
		}
	}
		
	private boolean addContentSBState(SBState sbState, Element element, Element child) {
		if (addContentSBEntity(sbState, element, child)){
			return true;
		}
		return false;
	}


	private boolean addContentSBMeasurable(SBMeasurable sbMeasurable, Element element, Element childElement) {
		if (addContentSBEntity(sbMeasurable, element, childElement)){
			return true;
		}
		if (childElement.getName().equals("hasUnit")){
			sbMeasurable.getUnit().add(addObjectUnitOfMeasurement(childElement));
			return true;
		}else if(childElement.getName().equals("hasNumber")) {
			sbMeasurable.getNumber().add(Double.valueOf(childElement.getTextTrim()));
			return true;
		}else{
			return false; // no match
		}
	}

	
	// ---------------- addObject section ---------------------
	//
	private SBEntity addObjectSBEntity(Element element) {
		if (element.getChildren().size()==0){
			SBEntityProxy proxy = new SBEntityProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if(childElement.getName().equals("SBMeasurable")) {
					SBMeasurable thingie = addObjectSBMeasurable(childElement);
					pathwayModel.add(thingie);
					return thingie;
				} else if(childElement.getName().equals("SBState")) {
					SBState thingie = addObjectSBState(childElement);
					pathwayModel.add(thingie);
					return thingie;
				}
			}
		}
		SBEntity sbSubEntity = new SBEntityImpl();
		if (element.getAttributes().size()>0){
			addAttributes(sbSubEntity, element);
		}
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentSBEntity(sbSubEntity, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(sbSubEntity);
		return sbSubEntity;
	}
	private SBMeasurable addObjectSBMeasurable(Element element) {
		if (element.getChildren().size()==0){
			SBMeasurableProxy proxy = new SBMeasurableProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		SBMeasurable sbSubEntity = new SBMeasurable();
		addAttributes(sbSubEntity, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentSBMeasurable(sbSubEntity, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(sbSubEntity);
		return sbSubEntity;
	}
	private SBState addObjectSBState(Element element) {
		if (element.getChildren().size()==0){
			SBStateProxy proxy = new SBStateProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		SBState sbState = new SBState();
		addAttributes(sbState, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentSBState(sbState, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(sbState);
		return sbState;
	}
	private SBVocabulary addObjectSBVocabulary(Element element) {
		if (element.getChildren().size()==0){
			SBVocabularyProxy proxy = new SBVocabularyProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if(childElement.getName().equals("SBVocabulary")) {
					SBVocabulary thingie = addObjectSBVocabulary(childElement);
					pathwayModel.add(thingie);
					return thingie;
				}
			}
		}
		SBVocabulary sbVocabulary = new SBVocabulary();
		addAttributes(sbVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(sbVocabulary, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(sbVocabulary);
		return sbVocabulary;
	}
	private UnitOfMeasurement addObjectUnitOfMeasurement(Element element) {
		if(element.getChildren().size() == 0) {
			String id = parseIDAttributes(element);
			if(id != null) {
				return UnitOfMeasurementPool.getUnitById(id);				
			} else {
				return new UnitOfMeasurement();
			}
		}
		String id = null;
		List<String> symbols = new ArrayList<String>();
		id = parseIDAttributes(element);
		for(Object childObject : element.getChildren()) {
			if(childObject instanceof Element) {
				Element childElement = (Element) childObject;
				if(childElement.getName().equals("unitSymbol")) {
					String symbol = childElement.getTextTrim();
					symbols.add(symbol);					
				}
			}
		}
		UnitOfMeasurement unit = UnitOfMeasurementPool.getUnitByIdOrSymbol(id, symbols);
		
		addAttributes(unit, element);
		
		for (Object child : element.getChildren()){
			if (!addContentUnitOfMeasurement(unit,element,(Element)child)){
				showUnexpected((Element)child);
			}
		}
		pathwayModel.add(unit);
		return unit;
	}

	private ComplexAssembly addObjectComplexAssembly(Element element) {
		ComplexAssembly complexAssembly = new ComplexAssembly();
		addAttributes(complexAssembly,element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentComplexAssembly(complexAssembly, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(complexAssembly);
		return complexAssembly;
	}

	private Degradation addObjectDegradation(Element element) {
		Degradation degradation = new Degradation();
		addAttributes(degradation,element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentDegradation(degradation, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(degradation);
		return degradation;
	}

	private Transport addObjectTransport(Element element) {
		Transport transport = new TransportImpl();
		addAttributes(transport,element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentTransport(transport, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(transport);
		return transport;
	}

	private TransportWithBiochemicalReaction addObjectTransportWithBiochemicalReaction(Element element) {
		TransportWithBiochemicalReaction transportWithBiochemicalReaction = new TransportWithBiochemicalReaction();
		addAttributes(transportWithBiochemicalReaction,element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentTransportWithBiochemicalReaction(transportWithBiochemicalReaction, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(transportWithBiochemicalReaction);
		return transportWithBiochemicalReaction;
	}
	private Gene addObjectGene(Element element){
		if (element.getChildren().size()==0){
		// if there are no children it must be a resource inside another object
			GeneProxy proxy = new GeneProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		// it's the real object declaration
		Gene gene = new Gene();
		addAttributes(gene, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				if (!addContentGene(gene,element,(Element)child)){
					showUnexpected((Element)child);
				}
			}
		}
		pathwayModel.add(gene);
		return gene;
	}
	
	private Interaction addObjectInteraction(Element element) {
		if (element.getChildren().size()==0){
		// if there are no children it must be a resource inside another object
			InteractionProxy proxy = new InteractionProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		
		Interaction interaction = new InteractionImpl();
		addAttributes(interaction, element);
		for (Object child : element.getChildren()){
			if (!addContentInteraction(interaction,element,(Element)child)){
				showUnexpected((Element)child);
			}
		}
		pathwayModel.add(interaction);
		return interaction;
	}

	private GeneticInteraction addObjectGeneticInteraction(Element element) {
		GeneticInteraction geneticInteraction = new GeneticInteraction();
		addAttributes(geneticInteraction, element);
		for (Object child : element.getChildren()){
			if (!addContentGeneticInteraction(geneticInteraction,element,(Element)child)){
				showUnexpected((Element)child);
			}
		}
		pathwayModel.add(geneticInteraction);
		return geneticInteraction;
	}

	private MolecularInteraction addObjectMolecularInteraction(Element element) {
		MolecularInteraction molecularInteraction = new MolecularInteraction();
		addAttributes(molecularInteraction, element);
		for (Object child : element.getChildren()){
			if (!addContentMolecularInteraction(molecularInteraction,element,(Element)child)){
				showUnexpected((Element)child);
			}
		}
		pathwayModel.add(molecularInteraction);
		return molecularInteraction;
	}

	private TemplateReaction addObjectTemplateReaction(Element element) {
		TemplateReaction templateReaction = new TemplateReaction();
		addAttributes(templateReaction, element);
		for (Object child : element.getChildren()){
			if (!addContentTemplateReaction(templateReaction,element,(Element)child)){
				showUnexpected((Element)child);
			}
		}
		pathwayModel.add(templateReaction);
		return templateReaction;
	}

	private PhysicalEntity addObjectPhysicalEntity(Element element) {
		if (element.getChildren().size()==0){	// we got lucky: no children, it means a reference -> create a proxy
			PhysicalEntityProxy proxy = new PhysicalEntityProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				// example: the "component" below is a PhysicalEntity; should be just one child!!!
				//	-<bp:Complex rdf:ID="State_23615"> 
				//		-<bp:component> 
				//			+<bp:Protein rdf:ID="Protein_Afcs_A000037_19818[M71@unknown]_GO:0005783"> 
				//		</bp:component>
				if(childElement.getName().equals("Complex")) {
					Complex thingie = addObjectComplex(childElement);
					pathwayModel.add(thingie);
					return thingie;
				} else if(childElement.getName().equals("Dna")) {
					Dna thingie = addObjectDna(childElement);
					pathwayModel.add(thingie);
					return thingie;
				} else if(childElement.getName().equals("DnaRegion")) {
					DnaRegion thingie = addObjectDnaRegion(childElement);
					pathwayModel.add(thingie);
					return thingie;
				} else if(childElement.getName().equals("Rna")) {
					Rna thingie = addObjectRna(childElement);
					pathwayModel.add(thingie);
					return thingie;
				} else if(childElement.getName().equals("RnaRegion")) {
					RnaRegion thingie = addObjectRnaRegion(childElement);
					pathwayModel.add(thingie);
					return thingie;
				} else if(childElement.getName().equals("Protein")) {
					Protein thingie = addObjectProtein(childElement);
					pathwayModel.add(thingie);
					return thingie;
				} else if(childElement.getName().equals("SmallMolecule")) {
					SmallMolecule thingie = addObjectSmallMolecule(childElement);
					pathwayModel.add(thingie);
					return thingie;
				}
			}
		}
		PhysicalEntity physicalEntity = new PhysicalEntity();
		if (element.getAttributes().size()>0){			// real PhysicalEntity, with ID and everything
			addAttributes(physicalEntity,element);
		}
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentPhysicalEntity(physicalEntity, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(physicalEntity);
		return physicalEntity;
	}

	private Control addObjectControl(Element controlElement) {
		Control control = new Control();
		addAttributes(control, controlElement);
		for (Object child : controlElement.getChildren()){
			if (child instanceof Element){
				if (!addContentControl(control,controlElement,(Element)child)){
					showUnexpected((Element)child);
				}
			}
		}
		pathwayModel.add(control);
		return control;
	}

	private Conversion addObjectConversion(Element conversionElement) {
		if (conversionElement.getChildren().size()==0){
			ConversionProxy proxy = new ConversionProxy();
			addAttributes(proxy, conversionElement);
			pathwayModel.add(proxy);
			return proxy;
		}
		Conversion conversion = new ConversionImpl();
		addAttributes(conversion, conversionElement);
		for (Object child : conversionElement.getChildren()){
			if (child instanceof Element){
				if (!addContentConversion(conversion,conversionElement,(Element)child)){
					showUnexpected((Element)child);
				}
			}
		}
		pathwayModel.add(conversion);
		return conversion;
	}

	/*
- <bp:Catalysis rdf:ID="_5_aminolevulinate_synthase_activity_of_ALAS_homodimer__mitochondrial_matrix_">
  <bp:controller rdf:resource="#ALAS_homodimer__mitochondrial_matrix_" /> 
	...
  </bp:Catalysis>
  
- <bp:Complex rdf:ID="ALAS_homodimer__mitochondrial_matrix_">
	...
  </bp:Complex>
	*/
	private Catalysis addObjectCatalysis(Element element) {
		Catalysis catalysis = new Catalysis();
		addAttributes(catalysis, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				if (!addContentCatalysis(catalysis, element, (Element)child)){
					showUnexpected((Element)child);
				}
			}
		}
		pathwayModel.add(catalysis);
		return catalysis;
	}

	private Modulation addObjectModulation(Element element) {
		Modulation modulation = new Modulation();
		addAttributes(modulation, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				if (!addContentModulation(modulation, element, (Element)child)){
					showUnexpected((Element)child);
				}
			}
		}
		pathwayModel.add(modulation);
		return modulation;
	}

	private TemplateReactionRegulation addObjectTemplateReactionRegulation(Element element) {
		TemplateReactionRegulation templateReactionRegulation = new TemplateReactionRegulation();
		addAttributes(templateReactionRegulation, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				if (!addContentTemplateReactionRegulation(templateReactionRegulation, element, (Element)child)){
					showUnexpected((Element)child);
				}
			}
		}
		pathwayModel.add(templateReactionRegulation);
		return templateReactionRegulation;
	}

	private Complex addObjectComplex(Element element) {
		Complex complex = new Complex();
		addAttributes(complex, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentComplex(complex, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(complex);
		return complex;
	}
	
	private GroupObject addObjectGroupObject(Element element) {
		GroupObject gObject = new GroupObject();
		addAttributes(gObject, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentGroupObject(gObject, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(gObject);
		return gObject;
	}
	
	private Dna addObjectDna(Element element) {
		Dna dna = new Dna();
		addAttributes(dna, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentDna(dna, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(dna);
		return dna;
	}
	
	private DnaRegion addObjectDnaRegion(Element element) {
		DnaRegion dnaRegion = new DnaRegion();
		addAttributes(dnaRegion, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentDnaRegion(dnaRegion, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(dnaRegion);
		return dnaRegion;
	}

	private Xref addObjectXref(Element element){
		Namespace bp = Namespace.getNamespace("bp", "http://www.biopax.org/release/biopax-level3.owl#");
		if (element.getChild("UnificationXref",bp)!=null){
			UnificationXref xref = addObjectUnificationXref(element.getChild("UnificationXref",bp));
			return xref;
		}
		if (element.getChild("RelationshipXref",bp)!=null){
			RelationshipXref xref = addObjectRelationshipXref(element.getChild("RelationshipXref",bp));
			return xref;
		}
		if (element.getChild("PublicationXref",bp)!=null){
			PublicationXref xref = addObjectPublicationXref(element.getChild("PublicationXref",bp));
			return xref;
		}
		if (element.getChildren().size() == 0){
			XrefProxy xref = new XrefProxy();
			addAttributes(xref, element);
			pathwayModel.add(xref);
			return xref;
		}else{
			for (Object child : element.getChildren()){
				if (child instanceof Element){
					Element childElement = (Element)child;
					showUnexpected(childElement);
				}
			}
			Xref xref = new Xref();
			pathwayModel.add(xref);
			System.out.println("should never happen");
			return xref;
		}
	}

	private Protein addObjectProtein(Element element) {
		Protein protein = new Protein();
		addAttributes(protein, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentProtein(protein, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(protein);
		return protein;
	}

	private Rna addObjectRna(Element element) {
		Rna rna = new Rna();
		addAttributes(rna, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentRna(rna, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(rna);
		return rna;
	}

	private RnaRegion addObjectRnaRegion(Element element) {
		RnaRegion rnaRegion = new RnaRegion();
		addAttributes(rnaRegion, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentRnaRegion(rnaRegion, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(rnaRegion);
		return rnaRegion;
	}

	private SmallMolecule addObjectSmallMolecule(Element element) {
		SmallMolecule smallMolecule = new SmallMolecule();
		addAttributes(smallMolecule, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentSmallMolecule(smallMolecule, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(smallMolecule);
		return smallMolecule;
	}


	private BiochemicalReaction addObjectBiochemicalReaction(Element element) {
		Namespace bp = Namespace.getNamespace("bp", "http://www.biopax.org/release/biopax-level3.owl#");
		if (element.getChild("TransportWithBiochemicalReaction",bp)!=null){
			return addObjectTransportWithBiochemicalReaction(element.getChild("TransportWithBiochemicalReaction",bp));
		}
		BiochemicalReaction biochemicalReaction = new BiochemicalReactionImpl();
		addAttributes(biochemicalReaction, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentBiochemicalReaction(biochemicalReaction, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(biochemicalReaction);
		return biochemicalReaction;
	}

	private BioSource addObjectBioSource(Element element) {
		if (element.getChildren().size()==0){
			BioSourceProxy proxy = new BioSourceProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if(childElement.getName().equals("BioSource")) {
					BioSource thingie = addObjectBioSource(childElement);
					pathwayModel.add(thingie);
					return thingie;
				}
			}
		}
		BioSource bioSource = new BioSource();
		addAttributes(bioSource, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentBioSource(bioSource, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(bioSource);
		return bioSource;
	}

	private ChemicalStructure addObjectChemicalStructure(Element element) {
		if (element.getChildren().size()==0){
			ChemicalStructureProxy proxy = new ChemicalStructureProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		ChemicalStructure chemicalStructure = new ChemicalStructure();
		addAttributes(chemicalStructure, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentChemicalStructure(chemicalStructure, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(chemicalStructure);
		return chemicalStructure;
	}

	private DeltaG addObjectDeltaG(Element element) {
		DeltaG deltaG = new DeltaG();
		addAttributes(deltaG, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentDeltaG(deltaG, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(deltaG);
		return deltaG;
	}

	private ControlledVocabulary addObjectControlledVocabulary(Element element) {
		ControlledVocabulary controlledVocabulary = new ControlledVocabulary();
		addAttributes(controlledVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(controlledVocabulary, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(controlledVocabulary);
		return controlledVocabulary;
	}

	private CellularLocationVocabulary addObjectCellularLocationVocabulary(Element element) {
		if (element.getChildren().size()==0){
			CellularLocationVocabularyProxy proxy = new CellularLocationVocabularyProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if(childElement.getName().equals("CellularLocationVocabulary")) {
					CellularLocationVocabulary thingie = addObjectCellularLocationVocabulary(childElement);
					pathwayModel.add(thingie);
					return thingie;
				}
			}
		}
		CellularLocationVocabulary cellularLocationVocabulary = new CellularLocationVocabulary();
		addAttributes(cellularLocationVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(cellularLocationVocabulary, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(cellularLocationVocabulary);
		return cellularLocationVocabulary;
	}

	private CellVocabulary addObjectCellVocabulary(Element element) {
		if (element.getChildren().size()==0){
			CellVocabularyProxy proxy = new CellVocabularyProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		CellVocabulary cellVocabulary = new CellVocabulary();
		addAttributes(cellVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(cellVocabulary, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(cellVocabulary);
		return cellVocabulary;
	}

	private EntityFeature addObjectEntityFeature(Element element) {
		if (element.getChildren().size()==0){
			EntityFeatureProxy proxy = new EntityFeatureProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if(childElement.getName().equals("BindingFeature")) {
					BindingFeature thingie = addObjectBindingFeature(childElement);
					pathwayModel.add(thingie);
					return thingie;
				} else if(childElement.getName().equals("FragmentFeature")) {
					FragmentFeature thingie = addObjectFragmentFeature(childElement);
					pathwayModel.add(thingie);
					return thingie;
				} else if(childElement.getName().equals("ModificationFeature")) {
					ModificationFeature thingie = addObjectModificationFeature(childElement);
					pathwayModel.add(thingie);
					return thingie;
				}
			}
		}
		EntityFeature entityFeature = new EntityFeatureImpl();
		addAttributes(entityFeature, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentEntityFeature(entityFeature, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(entityFeature);
		return entityFeature;
	}

	private Evidence addObjectEvidence(Element element) {
		if (element.getChildren().size()==0){
			EvidenceProxy proxy = new EvidenceProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		Evidence evidence = new Evidence();
		addAttributes(evidence, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentEvidence(evidence, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(evidence);
		return evidence;
	}

	private ExperimentalForm addObjectExperimentalForm(Element element) {
		if (element.getChildren().size()==0){
			ExperimentalFormProxy proxy = new ExperimentalFormProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		ExperimentalForm experimentalForm = new ExperimentalForm();
		addAttributes(experimentalForm, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentExperimentalForm(experimentalForm, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(experimentalForm);
		return experimentalForm;
	}
	
	private KPrime addObjectKPrime(Element element) {
		KPrime kPrime = new KPrime();
		addAttributes(kPrime, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentKPrime(kPrime, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(kPrime);
		return kPrime;
	}

	// proxy thing done in addContentDnaReference
	private DnaReference addObjectDnaReference(Element element) {
		DnaReference dnaReference = new DnaReference();
		addAttributes(dnaReference, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentDnaReference(dnaReference, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(dnaReference);
		return dnaReference;
	}

	private DnaRegionReference addObjectDnaRegionReference(Element element) {
		if (element.getChildren().size()==0){
			DnaRegionReferenceProxy proxy = new DnaRegionReferenceProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		DnaRegionReference dnaRegionReference = new DnaRegionReference();
		addAttributes(dnaRegionReference, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentDnaRegionReference(dnaRegionReference, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(dnaRegionReference);
		return dnaRegionReference;
	}

	private ProteinReference addObjectProteinReference(Element element) {
		ProteinReference proteinReference = new ProteinReference();
		addAttributes(proteinReference, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentProteinReference(proteinReference, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(proteinReference);
		return proteinReference;
	}

	private RnaReference addObjectRnaReference(Element element) {
		RnaReference rnaReference = new RnaReference();
		addAttributes(rnaReference, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentRnaReference(rnaReference, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(rnaReference);
		return rnaReference;
	}

	private RnaRegionReference addObjectRnaRegionReference(Element element) {
		if (element.getChildren().size()==0){
			RnaRegionReferenceProxy proxy = new RnaRegionReferenceProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		RnaRegionReference rnaRegionReference = new RnaRegionReference();
		addAttributes(rnaRegionReference, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentRnaRegionReference(rnaRegionReference, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(rnaRegionReference);
		return rnaRegionReference;
	}

	private SmallMoleculeReference addObjectSmallMoleculeReference(Element element) {
		SmallMoleculeReference smallMoleculeReference = new SmallMoleculeReference();
		addAttributes(smallMoleculeReference, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentSmallMoleculeReference(smallMoleculeReference, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(smallMoleculeReference);
		return smallMoleculeReference;
	}

	private BindingFeature addObjectBindingFeature(Element element) {
		BindingFeature bindingFeature = new BindingFeatureImpl();
		addAttributes(bindingFeature, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentBindingFeature(bindingFeature, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(bindingFeature);
		return bindingFeature;
	}

	private FragmentFeature addObjectFragmentFeature(Element element) {
		FragmentFeature fragmentFeature = new FragmentFeature();
		addAttributes(fragmentFeature, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentFragmentFeature(fragmentFeature, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(fragmentFeature);
		return fragmentFeature;
	}
	
	private ModificationFeature addObjectModificationFeature(Element element) {
		ModificationFeature modificationFeature = new ModificationFeatureImpl();
		addAttributes(modificationFeature, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentModificationFeature(modificationFeature, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(modificationFeature);
		return modificationFeature;
	}

	private CovalentBindingFeature addObjectCovalentBindingFeature(Element element) {
		CovalentBindingFeature covalentBindingFeature = new CovalentBindingFeature();
		addAttributes(covalentBindingFeature, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentCovalentBindingFeature(covalentBindingFeature, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(covalentBindingFeature);
		return covalentBindingFeature;
	}

	private EntityReference addObjectEntityReference(Element element) {
		if (element.getChildren().size()==0){
			EntityReferenceProxy proxy = new EntityReferenceProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if(childElement.getName().equals("DnaReference")) {
					DnaReference thingie = addObjectDnaReference(childElement);
					pathwayModel.add(thingie);
					return thingie;
				} else if(childElement.getName().equals("DnaRegionReference")) {
					DnaRegionReference thingie = addObjectDnaRegionReference(childElement);
					pathwayModel.add(thingie);
					return thingie;
				} else if(childElement.getName().equals("ProteinReference")) {
					ProteinReference thingie = addObjectProteinReference(childElement);
					pathwayModel.add(thingie);
					return thingie;
				} else if(childElement.getName().equals("RnaReference")) {
					RnaReference thingie = addObjectRnaReference(childElement);
					pathwayModel.add(thingie);
					return thingie;
				} else if(childElement.getName().equals("RnaRegionReference")) {
					RnaRegionReference thingie = addObjectRnaRegionReference(childElement);
					pathwayModel.add(thingie);
					return thingie;
				} else if(childElement.getName().equals("SmallMoleculeReference")) {
					SmallMoleculeReference thingie = addObjectSmallMoleculeReference(childElement);
					pathwayModel.add(thingie);
					return thingie;
				}
			}
		}
		EntityReference entityReference = new EntityReference();
		addAttributes(entityReference, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentEntityReference(entityReference, element, childElement)){	// to self ??
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(entityReference);
		return entityReference;
	}

	private EntityReferenceTypeVocabulary addObjectEntityReferenceTypeVocabulary(Element element) {
		if (element.getChildren().size()==0){
			EntityReferenceTypeVocabularyProxy proxy = new EntityReferenceTypeVocabularyProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		EntityReferenceTypeVocabulary entityReferenceTypeVocabulary = new EntityReferenceTypeVocabulary();
		addAttributes(entityReferenceTypeVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(entityReferenceTypeVocabulary, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(entityReferenceTypeVocabulary);
		return entityReferenceTypeVocabulary;
	}

	private EvidenceCodeVocabulary addObjectEvidenceCodeVocabulary(Element element) {
		if (element.getChildren().size()==0){
			EvidenceCodeVocabularyProxy proxy = new EvidenceCodeVocabularyProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		EvidenceCodeVocabulary evidenceCodeVocabulary = new EvidenceCodeVocabulary();
		addAttributes(evidenceCodeVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(evidenceCodeVocabulary, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(evidenceCodeVocabulary);
		return evidenceCodeVocabulary;
	}

	private ExperimentalFormVocabulary addObjectExperimentalFormVocabulary(Element element) {
		if (element.getChildren().size()==0){
			ExperimentalFormVocabularyProxy proxy = new ExperimentalFormVocabularyProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		ExperimentalFormVocabulary experimentalFormVocabulary = new ExperimentalFormVocabulary();
		addAttributes(experimentalFormVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(experimentalFormVocabulary, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(experimentalFormVocabulary);
		return experimentalFormVocabulary;
	}

	private InteractionVocabulary addObjectInteractionVocabulary(Element element) {
		if (element.getChildren().size()==0){
			InteractionVocabularyProxy proxy = new InteractionVocabularyProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		InteractionVocabulary interactionVocabulary = new InteractionVocabulary();
		addAttributes(interactionVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(interactionVocabulary, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(interactionVocabulary);
		return interactionVocabulary;
	}

	private PhenotypeVocabulary addObjectPhenotypeVocabulary(Element element) {
		if (element.getChildren().size()==0){
			PhenotypeVocabularyProxy proxy = new PhenotypeVocabularyProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		PhenotypeVocabulary phenotypeVocabulary = new PhenotypeVocabulary();
		addAttributes(phenotypeVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentPhenotypeVocabulary(phenotypeVocabulary, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(phenotypeVocabulary);
		return phenotypeVocabulary;
	}

	private RelationshipTypeVocabulary addObjectRelationshipTypeVocabulary(Element element) {
		if (element.getChildren().size()==0){
			RelationshipTypeVocabularyProxy proxy = new RelationshipTypeVocabularyProxy();
				addAttributes(proxy, element);
				pathwayModel.add(proxy);
				return proxy;
		}
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if(childElement.getName().equals("RelationshipTypeVocabulary")) {
					RelationshipTypeVocabulary thingie = addObjectRelationshipTypeVocabulary(childElement);
					pathwayModel.add(thingie);
					return thingie;
				}
			}
		}
		RelationshipTypeVocabulary relationshipTypeVocabulary = new RelationshipTypeVocabulary();
		addAttributes(relationshipTypeVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(relationshipTypeVocabulary, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(relationshipTypeVocabulary);
		return relationshipTypeVocabulary;
	}

	private SequenceModificationVocabulary addObjectSequenceModificationVocabulary(Element element) {
		if (element.getChildren().size()==0){
			SequenceModificationVocabularyProxy proxy = new SequenceModificationVocabularyProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if(childElement.getName().equals("SequenceModificationVocabulary")) {
					SequenceModificationVocabulary thingie = addObjectSequenceModificationVocabulary(childElement);
					pathwayModel.add(thingie);
					return thingie;
				}
			}
		}
		SequenceModificationVocabulary sequenceModificationVocabulary = new SequenceModificationVocabulary();
		addAttributes(sequenceModificationVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(sequenceModificationVocabulary, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(sequenceModificationVocabulary);
		return sequenceModificationVocabulary;
	}

	private SequenceRegionVocabulary addObjectSequenceRegionVocabulary(Element element) {
		if (element.getChildren().size()==0){
			SequenceRegionVocabularyProxy proxy = new SequenceRegionVocabularyProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if(childElement.getName().equals("SequenceRegionVocabulary")) {
					SequenceRegionVocabulary thingie = addObjectSequenceRegionVocabulary(childElement);
					pathwayModel.add(thingie);
					return thingie;
				}
			}
		}
		SequenceRegionVocabulary sequenceRegionVocabulary = new SequenceRegionVocabulary();
		addAttributes(sequenceRegionVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(sequenceRegionVocabulary, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(sequenceRegionVocabulary);
		return sequenceRegionVocabulary;
	}

	private TissueVocabulary addObjectTissueVocabulary(Element element) {
		if (element.getChildren().size()==0){
			TissueVocabularyProxy proxy = new TissueVocabularyProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		TissueVocabulary tissueVocabulary = new TissueVocabulary();
		addAttributes(tissueVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(tissueVocabulary, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(tissueVocabulary);
		return tissueVocabulary;
	}

	// proxy thing done elsewhere
	private Pathway addObjectPathway(Element element) {
		Pathway pathway = new Pathway();
		addAttributes(pathway, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentPathway(pathway, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(pathway);
		return pathway;
	}

	private PathwayStep addObjectPathwayStep(Element element) {
		if (element.getChildren().size()==0){
			// if there are no children it must be a resource inside another object
			PathwayStepProxy proxy = new PathwayStepProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		PathwayStep pathwayStep = new PathwayStep();
		addAttributes(pathwayStep, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentPathwayStep(pathwayStep, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(pathwayStep);
		return pathwayStep;
	}

	private BiochemicalPathwayStep addObjectBiochemicalPathwayStep(Element element) {
		BiochemicalPathwayStep biochemicalPathwayStep = new BiochemicalPathwayStep();
		addAttributes(biochemicalPathwayStep, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentBiochemicalPathwayStep(biochemicalPathwayStep, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(biochemicalPathwayStep);
		return biochemicalPathwayStep;
	}

	private Provenance addObjectProvenance(Element element) {
		if (element.getChildren().size()==0){
		// if there are no children it must be a resource inside another object
			ProvenanceProxy proxy = new ProvenanceProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if(childElement.getName().equals("Provenance")) {
					Provenance thingie = addObjectProvenance(childElement);
					pathwayModel.add(thingie);
					return thingie;
				}
			}
		}
		Provenance provenance = new Provenance();
		addAttributes(provenance, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentProvenance(provenance, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(provenance);
		return provenance;
	}

	private Score addObjectScore(Element element) {
		if (element.getChildren().size()==0){
		// if there are no children it must be a resource inside another object
			ScoreProxy proxy = new ScoreProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		Score score = new Score();
		addAttributes(score, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentScore(score, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(score);
		return score;
	}

	private SequenceLocation addObjectSequenceLocation(Element element) {
		if (element.getChildren().size()==0){
		// if there are no children it must be a resource inside another object
			SequenceLocationProxy proxy = new SequenceLocationProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if(childElement.getName().equals("SequenceInterval")) {
					SequenceInterval thingie = addObjectSequenceInterval(childElement);
					pathwayModel.add(thingie);
					return thingie;
				}
			} else if (child instanceof Element){
				Element childElement = (Element)child;
				if(childElement.getName().equals("SequenceSite")) {
					SequenceSite thingie = addObjectSequenceSite(childElement);
					pathwayModel.add(thingie);
					return thingie;
				}
			}
		}
		SequenceLocation sequenceLocation = new SequenceLocation();
		addAttributes(sequenceLocation, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentSequenceLocation(sequenceLocation, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(sequenceLocation);
		return sequenceLocation;
	}

	private SequenceInterval addObjectSequenceInterval(Element element) {
		SequenceInterval sequenceInterval = new SequenceInterval();
		addAttributes(sequenceInterval, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentSequenceInterval(sequenceInterval, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(sequenceInterval);
		return sequenceInterval;
	}

	private SequenceSite addObjectSequenceSite(Element element) {
		if (element.getChildren().size()==0){
			// if there are no children it must be a resource inside another object
			SequenceSiteProxy proxy = new SequenceSiteProxy();
			addAttributes(proxy, element);
			pathwayModel.add(proxy);
			return proxy;
		}
		SequenceSite sequenceSite = new SequenceSite();
		addAttributes(sequenceSite, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentSequenceSite(sequenceSite, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(sequenceSite);
		return sequenceSite;
	}

	private Stoichiometry addObjectStoichiometry(Element element) {
		if (element.getChildren().size()==0){
		// if there are no children it must be a resource inside another object
			StoichiometryProxy proxy = new StoichiometryProxy();
		addAttributes(proxy, element);
		pathwayModel.add(proxy);
		return proxy;
	}
		Stoichiometry stoichiometry = new Stoichiometry();
		addAttributes(stoichiometry, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentStoichiometry(stoichiometry, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(stoichiometry);
		return stoichiometry;
	}

	private PublicationXref addObjectPublicationXref(Element element) {
		PublicationXref publicationXref = new PublicationXref();
		addAttributes(publicationXref, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentPublicationXref(publicationXref, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(publicationXref);
		return publicationXref;
	}

	private RelationshipXref addObjectRelationshipXref(Element element) {
		RelationshipXref relationshipXref = new RelationshipXref();
		addAttributes(relationshipXref, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentRelationshipXref(relationshipXref, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(relationshipXref);
		return relationshipXref;
	}

	private UnificationXref addObjectUnificationXref(Element element) {
		UnificationXref unificationXref = new UnificationXref();
		addAttributes(unificationXref, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentUnificationXref(unificationXref, element, childElement)){
					showUnexpected(childElement);
				}
			}
		}
		pathwayModel.add(unificationXref);
		return unificationXref;
	}

	
	
}

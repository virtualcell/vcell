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
import java.util.Iterator;
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
import org.vcell.pathway.Interaction;
import org.vcell.pathway.InteractionImpl;
import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.InteractionVocabulary;
import org.vcell.pathway.KPrime;
import org.vcell.pathway.ModificationFeature;
import org.vcell.pathway.Modulation;
import org.vcell.pathway.MolecularInteraction;
import org.vcell.pathway.Pathway;
import org.vcell.pathway.PathwayEvent;
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
import org.vcell.pathway.persistence.BiopaxProxy.CellularLocationVocabularyProxy;
import org.vcell.pathway.persistence.BiopaxProxy.InteractionOrPathwayProxy;
import org.vcell.pathway.persistence.BiopaxProxy.InteractionProxy;
import org.vcell.pathway.persistence.BiopaxProxy.PathwayProxy;
import org.vcell.pathway.persistence.BiopaxProxy.PathwayStepProxy;
import org.vcell.pathway.persistence.BiopaxProxy.PhysicalEntityProxy;
import org.vcell.pathway.persistence.BiopaxProxy.RdfObjectProxy;
import org.vcell.pathway.persistence.BiopaxProxy.XrefProxy;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.UserCancelException;

import cbit.util.xml.XmlUtil;

public class PathwayReader {
	
	protected final RDFXMLContext context;
	private PathwayModel pathwayModel = new PathwayModel();
	private Namespace bp = Namespace.getNamespace("bp", "http://www.biopax.org/release/biopax-level2.owl#");
	private Namespace rdf = Namespace.getNamespace("rdf", DefaultNameSpaces.RDF.uri);
	
	private int counterID = 0;

	public static void main(String args[]){
		try {
			Document document = XmlUtil.readXML(new File("C:\\Developer\\eclipse\\workspace\\VCell_Standard\\temp.xml"));
			PathwayReader pathwayReader = new PathwayReader(new RDFXMLContext());
			System.out.println("starting parsing");
			PathwayModel pathwayModel = pathwayReader.parse(document.getRootElement(), null);
			System.out.println("ending parsing");
			pathwayModel.reconcileReferences(null);
			System.out.println(pathwayModel.show(true));
			
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	
	public PathwayReader(RDFXMLContext context) { this.context = context; }

	public PathwayModel parse(Element rootElement, ClientTaskStatusSupport clientTaskStatusSupport) {
		try{
			pathwayModel.setDisableUpdate(true);
			int counterObjects = 0;
			@SuppressWarnings("unchecked")
			List<Element> children = rootElement.getChildren();
			int numChildren = children.size();
			Iterator<Element> iter = children.iterator();
			while(iter.hasNext()){
				Object child = iter.next();
				int prog = (int)(counterObjects*100.0/numChildren);
				if (clientTaskStatusSupport != null) {
					clientTaskStatusSupport.setMessage("Parsing Document...");
					clientTaskStatusSupport.setProgress(prog);
				}
				if(clientTaskStatusSupport != null && clientTaskStatusSupport.isInterrupted()){
					throw UserCancelException.CANCEL_GENERIC;
				}
				counterObjects++;
				if (child instanceof Element){
					Element childElement = (Element)child;
					if (childElement.getName().equals("pathway")){
						pathwayModel.add(addObjectPathway(childElement));
					}else if (childElement.getName().equals("modulation")){
						addObjectModulation(childElement);
					}else if (childElement.getName().equals("biochemicalReaction")){
						addObjectBiochemicalReaction(childElement);
					}else if (childElement.getName().equals("smallMolecule")){
						addObjectSmallMolecule(childElement);
					}else if (childElement.getName().equals("protein")){
						addObjectProtein(childElement);
					}else if (childElement.getName().equals("complex")){
						addObjectComplex(childElement);
					}else if (childElement.getName().equals("catalysis")){
						addObjectCatalysis(childElement);
					}else if (childElement.getName().equals("control")){
						addObjectControl(childElement);
					}else if (childElement.getName().equals("Ontology")){
						showIgnored(childElement, "Ontology not implemented in BioPAX 3.");
					}else if (childElement.getName().equals("interaction")){
						addObjectInteraction(childElement);
					// we deal with physicalInteraction as if it's a v3 interaction
					}else if (childElement.getName().equals("physicalInteraction")){
						addObjectMolecularInteraction(childElement);
					}else if (childElement.getName().equals("transport")){
						addObjectTransport(childElement);
					}else if (childElement.getName().equals("transportWithBiochemicalReaction")){
						addObjectTransportWithBiochemicalReaction(childElement);
					}else if (childElement.getName().equals("complexAssembly")){
						addObjectComplexAssembly(childElement);
					}else if (childElement.getName().equals("rna")){
						addObjectRna(childElement);
					}else if (childElement.getName().equals("physicalEntity")){
						addObjectPhysicalEntity(childElement);
					}else if (childElement.getName().equals("publicationXref")){
						pathwayModel.add(addObjectPublicationXref(childElement));
					}else if (childElement.getName().equals("relationshipXref")){
						pathwayModel.add(addObjectRelationshipXref(childElement));
					}else if (childElement.getName().equals("unificationXref")){
						pathwayModel.add(addObjectUnificationXref(childElement));
					}else{
						showUnexpected(childElement);
					}
				}
			}
			//System.out.println("Parsed " + counterObjects + " objects");
			pathwayModel.setDisableUpdate(false);
			pathwayModel.firePathwayChanged(new PathwayEvent(pathwayModel, PathwayEvent.CHANGED));
			return pathwayModel;
		}finally{
			pathwayModel.setDisableUpdate(false);
		}
	}

	private ComplexAssembly addObjectComplexAssembly(Element element) {
		ComplexAssembly complexAssembly = new ComplexAssembly();
		addAttributes(complexAssembly,element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentComplexAssembly(complexAssembly, element, childElement)){
					showUnexpected(childElement, complexAssembly);
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
					showUnexpected(childElement, degradation);
				}
			}
		}
		pathwayModel.add(degradation);
		return degradation;
	}

	private TransportWithBiochemicalReaction addObjectTransportWithBiochemicalReaction(Element element) {
		TransportWithBiochemicalReaction transportWithBiochemicalReaction = new TransportWithBiochemicalReaction();
		addAttributes(transportWithBiochemicalReaction,element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentTransportWithBiochemicalReaction(transportWithBiochemicalReaction, element, childElement)){
					showUnexpected(childElement, transportWithBiochemicalReaction);
				}
			}
		}
		pathwayModel.add(transportWithBiochemicalReaction);
		return transportWithBiochemicalReaction;
	}

	// returns a "resource" attribute if present or generates one from the ID
	private String generateResourceID(Element element) {
		for (Object attr : element.getAttributes()){
			Attribute attribute = (Attribute)attr;
			if (attribute.getName().equals("ID")){
				return ("#" + attribute.getValue());
			} else if (attribute.getName().equals("resource")) {
				return attribute.getValue();
			}
		}
		return null;
	}
	
	private void addAttributes(BioPaxObject bioPaxObject, Element element){
		for (Object attr : element.getAttributes()){
			Attribute attribute = (Attribute)attr;
			if (attribute.getName().equals("ID")){
				if (bioPaxObject instanceof RdfObjectProxy){
					showUnexpected(attribute, bioPaxObject);
				}else{
					String uri = context.unAbbreviateURI(element, attribute.getValue());
					bioPaxObject.setID(uri);
				}
			}else if (attribute.getName().equals("resource")){
				if (bioPaxObject instanceof RdfObjectProxy){
					String uri = context.unRelativizeURI(element, attribute.getValue());
					bioPaxObject.setID(uri);
				}else{
					showUnexpected(attribute, bioPaxObject);
				}
			}else{
				showUnexpected(attribute, bioPaxObject);
			}
		}

	}
	
	private Transport addObjectTransport(Element element) {
		Transport transport = new TransportImpl();
		addAttributes(transport,element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentTransport(transport, element, childElement)){
					showUnexpected(childElement, transport);
				}
			}
		}
		pathwayModel.add(transport);
		return transport;
	}


	private boolean addContentBioPaxObject(BioPaxObject biopaxObject, Element element, Element childElement){
		if (childElement.getName().equals("COMMENT")){
			biopaxObject.getComments().add((childElement.getTextTrim()));
			return true;
		}else{
			return false; // didn't consume the childElement
		}
	}
	
	private boolean addContentEntity(Entity entity, Element element, Element childElement){
		if (addContentBioPaxObject(entity, element, childElement)){
			return true;
		}
		if (childElement.getName().equals("DATA-SOURCE")){
			Element dataSourceElement = childElement.getChild("dataSource",bp);
			entity.getDataSource().add(addObjectProvenance(dataSourceElement));
			return true;
		}else if (childElement.getName().equals("SHORT-NAME")){
			insertAtStart(entity.getName(),childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("NAME")){
			entity.getName().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("XREF")){
			entity.getxRef().add(addObjectXref(childElement));
			return true;
		}else if (childElement.getName().equals("EVIDENCE")){
			entity.getEvidence().add(addObjectEvidence(childElement));
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentUtilityClass(UtilityClass utilityClass, Element element, Element childElement){
		if (addContentBioPaxObject(utilityClass, element, childElement)){
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
		if (childElement.getName().equals("NAME")){
			bioSource.getName().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("XREF")){
			bioSource.getxRef().add(addObjectXref(childElement));
			return true;
		}else if (childElement.getName().equals("TAXON-XREF")){
			bioSource.getxRef().add(addObjectXref(childElement));
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
		return false; // no match
	}
	
	private boolean addContentControlledVocabulary(ControlledVocabulary controlledVocabulary, Element element, Element childElement){
		if (addContentUtilityClass(controlledVocabulary, element, childElement)){
			return true;
		}
		/**
		 * ArrayList<String> term
		 * ArrayList<Xref> xRef
		 */
		if(childElement.getName().equals("openControlledVocabulary")){
			for (Object child : childElement.getChildren()){
				if (child instanceof Element){
					Element childElement1 = (Element)child;
					if (childElement1.getName().equals("XREF")){
						controlledVocabulary.getxRef().add(addObjectXref(childElement1));
						return true;
					}else if (childElement1.getName().equals("TERM")){
						String prefix = ((Element)element.getParent()).getName();
						if(prefix.equals("")){
							controlledVocabulary.getTerm().add(childElement1.getTextTrim());
						}else{
							controlledVocabulary.getTerm().add(prefix + " :: "+childElement1.getTextTrim());
						}
						return true;
					}else{
						return false; // no match
					}
				}
			}
		}
		if (childElement.getName().equals("XREF")){
			controlledVocabulary.getxRef().add(addObjectXref(childElement));
			return true;
		}else if (childElement.getName().equals("TERM")){
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
		return false; // no match
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
		 */
		if (childElement.getName().equals("NAME")){
			entityReference.getName().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("XREF")){
			entityReference.getxRef().add(addObjectXref(childElement));
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
		 * ArrayList<EvidenceCodeVocabulary> evidenceCode
		 * ArrayList<ExperimentalForm> experimentalForm
		 */
		if (childElement.getName().equals("EVIDENCE-CODE")){
			Element openControlledVocabularyElement = childElement.getChild("openControlledVocabulary",bp);
			if (openControlledVocabularyElement!=null){
				evidence.getEvidenceCode().add(addObjectEvidenceCodeVocabulary(openControlledVocabularyElement));
				return true;
			}
			//entityReference.getName().add(childElement.getTextTrim());
			return false;
		}else if (childElement.getName().equals("XREF")){
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
		return false; // no match
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
		return false; // no match
	}
	
	private boolean addContentProvenance(Provenance provenance, Element element, Element childElement){
		if (addContentUtilityClass(provenance, element, childElement)){
			return true;
		}
		/**
		 * ArrayList<String> name
		 * ArrayList<Xref> xRef
		 */
		if (childElement.getName().equals("NAME")){
			provenance.getName().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("XREF")){
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
		return false; // no match
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
		return false; // no match
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
		if (childElement.getName().equals("DB")){
			xRef.setDb(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("ID")){
			xRef.setId(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("DB-VERSION")){
			xRef.setDbVersion(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("ID-VERSION")){
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
		if (childElement.getName().equals("AUTHORS")){
			publicationXRef.getAuthor().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("SOURCE")){
			publicationXRef.getSource().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("TITLE")){
			publicationXRef.setTitle(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("URL")){
			publicationXRef.getUrl().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("YEAR")){
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
		if (childElement.getName().equals("RELATIONSHIP-TYPE")){
			Element relationshipTypeVocabularyElement = childElement.getChild("relationshipTypeVocabulary",bp);
			if (relationshipTypeVocabularyElement!=null){
				relationshipXref.getRelationshipType().add(
						addObjectRelationshipTypeVocabulary(relationshipTypeVocabularyElement));
				return true;
			}else if (childElement.getTextTrim().length()>0){
				RelationshipTypeVocabulary relationshipTypeVocabulary = new RelationshipTypeVocabulary();
				relationshipTypeVocabulary.getTerm().add(childElement.getTextTrim());
				relationshipXref.getRelationshipType().add(relationshipTypeVocabulary);
				return true;
			}
			return false;
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
		return false; // no match
	}
	
	private boolean addContentSequenceSite(SequenceSite sequenceSite, Element element, Element childElement){
		if (addContentSequenceLocation(sequenceSite, element, childElement)){
			return true;
		}
		/**
		 * String positionStatusv
		 * Integer sequencePosition
		 */
		return false; // no match
	}
	
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
		if (childElement.getName().equals("NEXT-STEP")){
			Element pathwayStepElement = childElement.getChild("pathwayStep",bp);
			if (pathwayStepElement != null){
				PathwayStep nextPathwayStep = addObjectPathwayStep(pathwayStepElement);
				pathwayStep.getNextStep().add(nextPathwayStep);
				pathwayModel.add(nextPathwayStep);
				return true;
			} else if (childElement.getChildren().size() == 0){
				PathwayStepProxy pathwayStepProxy = new PathwayStepProxy();
				addAttributes(pathwayStepProxy, childElement);
				pathwayStep.getNextStep().add(pathwayStepProxy);
				pathwayModel.add(pathwayStepProxy);
				return true;
			} else {
				return false;
			}
		}else if (childElement.getName().equals("STEP-INTERACTIONS")){
			if (childElement.getChildren().size()==0){
				// if there are no children it must be a resource inside another object
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
				showIgnored(childElement, "Found NESTED child.", pathwayStep);
			}
 			return false;
//		}else if (childElement.getName().equals("EVIDENCE")) {
//			pathwayStep.getEvidence().add(addObjectEvidence(childElement));
//			return true;
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
		return false; // no match
	}
	
	private boolean addContentProteinReference(ProteinReference proteinReference, Element element, Element childElement){
		if (addContentEntityReference(proteinReference, element, childElement)){
			return true;
		}
		/**
		 * BioSource organism
		 * String sequence
		 */
		if (childElement.getName().equals("ORGANISM")){
			proteinReference.setOrganism(addBioSource(childElement));
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
		 * ArrayList<DnaRegionReference> dnaSubRegion
		 * BioSource organism
		 * ArrayList<RnaRegionReference> rnaSubRegion
		 * String sequence
		 */
		if (childElement.getName().equals("ORGANISM")){
			dnaReference.setOrganism(addBioSource(childElement));
			return true;
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
		if (childElement.getName().equals("ORGANISM")){
			rnaReference.setOrganism(addBioSource(childElement));
			return true;
		}else{
			return false; // no match
		}
	}
	
	private boolean addContentDnaRegionReference(DnaRegionReference dnaRegionReference, Element element, Element childElement){
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
		if (childElement.getName().equals("ORGANISM")){
			dnaRegionReference.setOrganism(addBioSource(childElement));
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
		if (childElement.getName().equals("ORGANISM")){
			rnaRegionReference.setOrganism(addBioSource(childElement));
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
		return false; // no match
	}
	
	private boolean addContentCovalentBindingFeature(CovalentBindingFeature covalentBindingFeature, Element element, Element childElement){
		if (addContentBindingFeature(covalentBindingFeature, element, childElement)){
			return true;
		}
		if (addContentModificationFeature(covalentBindingFeature, element, childElement)){
			return true;
		}
		/**
		 * SequenceModificationVocabulary modificationType
		 */
		return false; // no match
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
		return false; // no match
	}
	
	private boolean addContentPhenotypeVocabulary(PhenotypeVocabulary phenotypeVocabulary, Element element, Element childElement){
		if (addContentControlledVocabulary(phenotypeVocabulary, element, childElement)){
			return true;
		}
		/**
		 * String patoData
		 */
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
		if (childElement.getName().equals("ORGANISM")){
			// TODO: is that not Provenance in v3?
			showIgnored(childElement,"organism is not in physical entity in level 3", physicalEntity);
			return true;
		}
	else if (childElement.getName().equals("SYNONYMS")){
			physicalEntity.getName().add(childElement.getTextTrim());
			return true;
		}else if (childElement.getName().equals("SHORT-NAME")){
			insertAtStart(physicalEntity.getName(),childElement.getTextTrim());
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
		if (childElement.getName().equals("COMPONENTS")){
			Element physicalEntityParticipantElement = childElement.getChild("physicalEntityParticipant",bp);
			if (physicalEntityParticipantElement!=null){
				Element physicalEntityPropertyElement = physicalEntityParticipantElement.getChild("PHYSICAL-ENTITY",bp);
				if (physicalEntityPropertyElement!=null){
					if (physicalEntityPropertyElement.getChildren().size()==0){
						PhysicalEntityProxy physicalEntityProxy = new PhysicalEntityProxy();
						addAttributes(physicalEntityProxy, physicalEntityPropertyElement);
						pathwayModel.add(physicalEntityProxy);
						complex.getComponents().add(physicalEntityProxy);
						return true;
					}
				}
			}
			Element sequenceParticipantElement = childElement.getChild("sequenceParticipant",bp);
			if (sequenceParticipantElement!=null){
				Element physicalEntityPropertyElement = sequenceParticipantElement.getChild("PHYSICAL-ENTITY",bp);
				if (physicalEntityPropertyElement!=null){
					if (physicalEntityPropertyElement.getChildren().size()==0){
						PhysicalEntityProxy physicalEntityProxy = new PhysicalEntityProxy();
						addAttributes(physicalEntityProxy, physicalEntityPropertyElement);
						pathwayModel.add(physicalEntityProxy);
						complex.getComponents().add(physicalEntityProxy);
						return true;
					}
				}
			}
			if (childElement.getChildren().size()==0){
//				PhysicalEntityProxy physicalEntityProxy = new PhysicalEntityProxy();
//				addAttributes(physicalEntityProxy, childElement);
//				pathwayModel.add(physicalEntityProxy);
//				if (childElement.getName().equals("LEFT")){
//					conversion.getLeftSide().add(physicalEntityProxy);
//				}else{
//					conversion.getRightSide().add(physicalEntityProxy);
//				}
				showIgnored(childElement, "complex/COMPONENTS assuming redundant sequenceParticipant or physicalEntityParticipant", complex);
				return true;
			}
			return false;
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
		return false; // no match
	}
	
	private boolean addContentDnaRegion(DnaRegion dnaRegion, Element element, Element childElement){
		if (addContentPhysicalEntity(dnaRegion, element, childElement)){
			return true;
		}
		/**
		 * EntityReference entityReference
		 */
		return false; // no match
	}
	
	private boolean addContentProtein(Protein protein, Element element, Element childElement){
		if (addContentPhysicalEntity(protein, element, childElement)){
			return true;
		}
		/**
		 * EntityReference entityReference
		 */
		if (childElement.getName().equals("SEQUENCE")){
			showIgnored(childElement,"sequence is in proteinReference, not protein in level 3", protein);
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
		return false; // no match
	}
	
	private boolean addContentRnaRegion(RnaRegion rnaRegion, Element element, Element childElement){
		if (addContentPhysicalEntity(rnaRegion, element, childElement)){
			return true;
		}
		/**
		 * EntityReference entityReference
		 */
		return false; // no match
	}
	
	private boolean addContentSmallMolecule(SmallMolecule smallMolecule, Element element, Element childElement){
		if (addContentPhysicalEntity(smallMolecule, element, childElement)){
			return true;
		}
		/**
		 * EntityReference entityReference
		 */
		return false; // no match
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
		if (childElement.getName().equals("ORGANISM")){
			Element bioSourceElement = childElement.getChild("bioSource",bp);
			if (bioSourceElement!=null){
				pathway.setOrganism(addObjectBioSource(bioSourceElement));
				return true;
			}
			return false;
		}else if (childElement.getName().equals("PATHWAY-COMPONENTS")){
			if (childElement.getChildren().size() == 0){			// no children, means it's a proxy
/*		
 * We do NOT solve references to pathway and pathway steps because of infinite recursion
 * Ex: Pathway "E-cadherin signalling in the nascent..."  
 * http://www.pathwaycommons.org/pc/webservice.do?cmd=get_record_by_cpath_id&version=2.0&q=826249&output=biopax
 * The paths CPATH-826249 and CPATH-826243 cross reference each other
 * 				// TODO: need a better solution
				InteractionProxy proxyI = new InteractionProxy();
				addAttributes(proxyI, childElement);
				pathwayModel.add(proxyI);
				pathway.getPathwayComponentInteraction().add(proxyI);
				PathwayProxy proxyP = new PathwayProxy();
				addAttributes(proxyP, childElement);
				pathwayModel.add(proxyP);
				pathway.getPathwayComponentPathway().add(proxyP);
				// in v3 PathwayStep objects become pathwayOrder (instead of pathwayComponent)
				PathwayStepProxy proxyS = new PathwayStepProxy();
				addAttributes(proxyS, childElement);
				pathwayModel.add(proxyS);
				pathway.getPathwayOrder().add(proxyS);
				return true;
 */
				return false;
			} else {
				Element pathwayElement = childElement.getChild("pathway",bp);
				if (pathwayElement != null){
					Pathway thing = addObjectPathway(pathwayElement);
					pathway.getPathwayComponentPathway().add(thing);
					pathwayModel.add(thing);
					return true;
				}
				Element interactionElement = childElement.getChild("interaction",bp);
				if (interactionElement != null){
					Interaction thing = addObjectInteraction(interactionElement);
					pathway.getPathwayComponentInteraction().add(thing);
					pathwayModel.add(thing);
					return true;
				}
				Element pathwayStepElement = childElement.getChild("pathwayStep",bp);
				if (pathwayStepElement != null){
					PathwayStep thing = addObjectPathwayStep(pathwayStepElement);
					pathway.getPathwayOrder().add(thing);
					pathwayModel.add(thing);
					return true;
				}
			}
			return false;
		}
		return false;
	}

	private boolean addContentInteraction(Interaction interaction, Element element, Element childElement){
		if (addContentEntity(interaction,element,childElement)){
			return true;
		}

		if (childElement.getName().equals("PARTICIPANTS")){
			Element physicalEntityParticipantElement = childElement.getChild("physicalEntityParticipant",bp);
			if (physicalEntityParticipantElement!=null){
				Element physicalEntityPropertyElement = physicalEntityParticipantElement.getChild("PHYSICAL-ENTITY",bp);
				if (physicalEntityPropertyElement!=null){
					if (physicalEntityPropertyElement.getChildren().size()==0){
						PhysicalEntityProxy physicalEntityProxy = new PhysicalEntityProxy();
						addAttributes(physicalEntityProxy, physicalEntityPropertyElement);
						pathwayModel.add(physicalEntityProxy);
						interaction.addPhysicalEntityAsParticipant(physicalEntityProxy, InteractionParticipant.Type.PARTICIPANT);
						return true;
					}
				}
			}
			Element sequenceParticipantElement = childElement.getChild("sequenceParticipant",bp);
			if (sequenceParticipantElement!=null){
				Element physicalEntityPropertyElement = sequenceParticipantElement.getChild("PHYSICAL-ENTITY",bp);
				if (physicalEntityPropertyElement!=null){
					if (physicalEntityPropertyElement.getChildren().size()==0){
						PhysicalEntityProxy physicalEntityProxy = new PhysicalEntityProxy();
						addAttributes(physicalEntityProxy, physicalEntityPropertyElement);
						pathwayModel.add(physicalEntityProxy);
						interaction.addPhysicalEntityAsParticipant(physicalEntityProxy, InteractionParticipant.Type.PARTICIPANT);
						return true;
					}
				}
			}
			return false;
		} else if(childElement.getName().equals("INTERACTION-TYPE")){
			interaction.getInteractionTypes().add(addObjectInteractionVocabulary(childElement));
			return true;
//			showIgnored(childElement, "Can't convert openControlledVocabulary (v2) to InteractionVocabulary (v3).", interaction);
//			return false;
		} else {
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
		return false;
	}
	
	private boolean addContentMolecularInteraction(MolecularInteraction molecularInteraction, Element element, Element childElement){
		if (addContentInteraction(molecularInteraction,element,childElement)){
			return true;
		}
		/**
		 */
		return false;
	}
	
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
		if (childElement.getName().equals("CONTROLLER")){
			Element physicalEntityParticipantElement = childElement.getChild("physicalEntityParticipant",bp);
			if (physicalEntityParticipantElement!=null){
				Element physicalEntityPropertyElement = physicalEntityParticipantElement.getChild("PHYSICAL-ENTITY",bp);
				if (physicalEntityPropertyElement!=null){
					if (physicalEntityPropertyElement.getChildren().size()==0){
						PhysicalEntityProxy physicalEntityProxy = new PhysicalEntityProxy();
						addAttributes(physicalEntityProxy, physicalEntityPropertyElement);
						pathwayModel.add(physicalEntityProxy);
						control.addPhysicalController(physicalEntityProxy);
						return true;
					}
				}
			}
			Element sequenceParticipantElement = childElement.getChild("sequenceParticipant",bp);
			if (sequenceParticipantElement!=null){
				Element physicalEntityPropertyElement = sequenceParticipantElement.getChild("PHYSICAL-ENTITY",bp);
				if (physicalEntityPropertyElement!=null){
					if (physicalEntityPropertyElement.getChildren().size()==0){
						PhysicalEntityProxy physicalEntityProxy = new PhysicalEntityProxy();
						addAttributes(physicalEntityProxy, physicalEntityPropertyElement);
						pathwayModel.add(physicalEntityProxy);
						control.addPhysicalController(physicalEntityProxy);
						return true;
					}
				}
			}
//			if (childElement.getChildren().size()==0){
//				showIgnored(childElement, "LEFT/RIGHT assuming redundant sequenceParticipant or physicalEntityParticipant");
//				return true;
//			}
			return false;
//			// is it a pathway controller?
//			control.getPathwayControllers().add(addPathway(childElement));
		}else if (childElement.getName().equals("CONTROLLED")){
			Attribute resourceAttribute = childElement.getAttribute("resource",rdf);
			if (resourceAttribute!=null){
				InteractionOrPathwayProxy controlledEntityProxy = new InteractionOrPathwayProxy();
				control.setControlledInteraction(controlledEntityProxy);
				pathwayModel.add(controlledEntityProxy);
				String uri = context.unRelativizeURI(childElement, resourceAttribute.getValue());
				controlledEntityProxy.setID(uri);
				return true;
			}
			return false;
		}else if (childElement.getName().equals("CONTROL-TYPE")){
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
		 * ArrayList<PhysicalEntity> getLeftSide();
		 * ArrayList<Stoichiometry> getParticipantStoichiometry();
		 * ArrayList<PhysicalEntity> getRightSide();
		 * Boolean getSpontaneous();
		 */
		if (childElement.getName().equals("LEFT") || childElement.getName().equals("RIGHT")){
			Element physicalEntityParticipantElement = childElement.getChild("physicalEntityParticipant",bp);
			if (physicalEntityParticipantElement!=null){
				boolean found = false;
				PhysicalEntity thePhysicalEntity = null;
				Element physicalEntityPropertyElement = physicalEntityParticipantElement.getChild("PHYSICAL-ENTITY",bp);
				// always create a proxy, the real PhysicalEntity (if present) is not ready to be inserted as is because it is incomplete
				// the reason is that PhysicalEntity object is a "species" in v2 but becomes a "species context" in v3
				PhysicalEntityProxy physicalEntityProxy = null;
				if (physicalEntityPropertyElement != null){
					found = true;
					physicalEntityProxy = new PhysicalEntityProxy();
					addAttributes(physicalEntityProxy, physicalEntityPropertyElement);
					pathwayModel.add(physicalEntityProxy);
					if (childElement.getName().equals("LEFT")){
						conversion.addLeft(physicalEntityProxy);
					}else{
						conversion.addRight(physicalEntityProxy);
					}
					if (physicalEntityPropertyElement.getChildren().size() > 0) {
						thePhysicalEntity = addObjectPhysicalEntity(physicalEntityPropertyElement);
					}
				}
				Element cellularLocationElement = physicalEntityParticipantElement.getChild("CELLULAR-LOCATION",bp);
				if(cellularLocationElement != null && found == true) {
					Element controlledVocabularyElement = cellularLocationElement.getChild("openControlledVocabulary", bp);
					if(controlledVocabularyElement != null) {
						CellularLocationVocabularyProxy cellularLocationVocabularyProxy = new CellularLocationVocabularyProxy();
						addAttributes(cellularLocationVocabularyProxy, controlledVocabularyElement);
						pathwayModel.add(cellularLocationVocabularyProxy);
						if(controlledVocabularyElement.getChildren().size() == 0) {
							physicalEntityProxy.setCellularLocation(cellularLocationVocabularyProxy);
						} else {
							physicalEntityProxy.setCellularLocation(
									addObjectCellularLocationVocabulary(controlledVocabularyElement));
						}
//						System.out.println(" -          " + controlledVocabularyElement.getName());
						// we'll use the extra info in this proxy during reconciliation phase to reconstruct a complete PhysicalEntity
					}
				}
				Element stoichiometricCoefficientElement = physicalEntityParticipantElement.getChild("STOICHIOMETRIC-COEFFICIENT",bp);
				if(stoichiometricCoefficientElement != null && found == true) {
//					System.out.println(" -          " + stoichiometricCoefficientElement.getTextTrim());
					// Create a Stoichiometry object from the stoichiometricCoefficient and from PhysicalEntity object
					Stoichiometry stoichiometry = new Stoichiometry();
					stoichiometry.setID(generateInstanceID());
					stoichiometry.setStoichiometricCoefficient(Double.valueOf(stoichiometricCoefficientElement.getTextTrim()));
					if(physicalEntityProxy == null) {		// reuse the proxy if we created one already
						physicalEntityProxy = new PhysicalEntityProxy();
						String uri = context.unRelativizeURI(physicalEntityPropertyElement, 
								generateResourceID(physicalEntityPropertyElement));
						physicalEntityProxy.setID(uri);
						pathwayModel.add(physicalEntityProxy);
					}
					stoichiometry.setPhysicalEntity(physicalEntityProxy);
					// Add the Stoichiometry object to the participantStoichiometry property of the conversion
					conversion.getParticipantStoichiometry().add(stoichiometry);
					// add the Stoichiometry object to the pathway model
					pathwayModel.add(stoichiometry);
					found = true;
				}
				return found;
			}
			Element sequenceParticipantElement = childElement.getChild("sequenceParticipant",bp);
			if (sequenceParticipantElement!=null){
				boolean found = false;
				PhysicalEntity thePhysicalEntity = null;
				Element physicalEntityPropertyElement = sequenceParticipantElement.getChild("PHYSICAL-ENTITY",bp);
				PhysicalEntityProxy physicalEntityProxy = null;
				if (physicalEntityPropertyElement!=null){
					found = true;
					if (physicalEntityPropertyElement.getChildren().size()==0){
						physicalEntityProxy = new PhysicalEntityProxy();
						addAttributes(physicalEntityProxy, physicalEntityPropertyElement);
						pathwayModel.add(physicalEntityProxy);
						if (childElement.getName().equals("LEFT")){
							conversion.addLeft(physicalEntityProxy);
						}else{
							conversion.addRight(physicalEntityProxy);
						}
//						return true;
					}else {
						thePhysicalEntity = addObjectPhysicalEntity(physicalEntityPropertyElement);
					}
					Element cellularLocationElement = sequenceParticipantElement.getChild("CELLULAR-LOCATION",bp);
					if(cellularLocationElement != null && found == true) {
						Element controlledVocabularyElement = cellularLocationElement.getChild("openControlledVocabulary", bp);
						if(controlledVocabularyElement != null) {
							CellularLocationVocabularyProxy cellularLocationVocabularyProxy = new CellularLocationVocabularyProxy();
							addAttributes(cellularLocationVocabularyProxy, controlledVocabularyElement);
							pathwayModel.add(cellularLocationVocabularyProxy);
							if(controlledVocabularyElement.getChildren().size() == 0) {
								physicalEntityProxy.setCellularLocation(cellularLocationVocabularyProxy);
							} else {
								physicalEntityProxy.setCellularLocation(
										addObjectCellularLocationVocabulary(controlledVocabularyElement));
							}
	//						System.out.println(" -          " + controlledVocabularyElement.getName());
							// we'll use the extra info in this proxy during reconciliation phase to reconstruct a complete PhysicalEntity
						}
					}
				}
				return found;
			}
			if (childElement.getChildren().size()==0){
//				PhysicalEntityProxy physicalEntityProxy = new PhysicalEntityProxy();
//				addAttributes(physicalEntityProxy, childElement);
//				pathwayModel.add(physicalEntityProxy);
//				if (childElement.getName().equals("LEFT")){
//					conversion.getLeftSide().add(physicalEntityProxy);
//				}else{
//					conversion.getRightSide().add(physicalEntityProxy);
//				}
				showIgnored(childElement, "conversion/LEFT or conversion/RIGHT assuming redundant sequenceParticipant or physicalEntityParticipant", conversion);
				return true;
			}
			return false;
//		}else if(childElement.getName().equals("participantStoichiometry")) {
//			conversion.getParticipantStoichiometry().add(addObjectStoichiometry(childElement));
//			return true;
		}else{
			return false;
		}
	}
	
	private String generateInstanceID() {
		return(new String("VCELL_LOCAL_ID_" + counterID++));
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
		if (childElement.getName().equals("EC-NUMBER")){
			biochemicalReaction.getECNumber().add(childElement.getTextTrim());
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
		if (childElement.getName().equals("DIRECTION")){
			catalysis.setCatalysisDirection(childElement.getTextTrim());
			return true;
		}else if(childElement.getName().equals("COFACTOR")) {
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
		/*
		 * 
		 */
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
	
	private Gene addObjectGene(Element element){
		Gene gene = new Gene();
		addAttributes(gene, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				if (!addContentGene(gene,element,(Element)child)){
					showUnexpected((Element)child, gene);
				}
			}
		}
		pathwayModel.add(gene);
		return gene;
	}
	
	private BioSource addBioSource(Element element){
		BioSource bioSource = new BioSource();
		addAttributes(bioSource, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				if (!addContentBioSource(bioSource,element,(Element)child)){
					showUnexpected((Element)child, bioSource);
				}
			}
		}
		pathwayModel.add(bioSource);
		return bioSource;
	}
	
	private ChemicalStructure addChemicalStructure(Element element){
		ChemicalStructure chemicalStructure = new ChemicalStructure();
		addAttributes(chemicalStructure, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				if (!addContentChemicalStructure(chemicalStructure,element,(Element)child)){
					showUnexpected((Element)child, chemicalStructure);
				}
			}
		}
		pathwayModel.add(chemicalStructure);
		return chemicalStructure;
	}
	
	private Interaction addObjectInteraction(Element element) {
		Interaction interaction = new InteractionImpl();
		addAttributes(interaction, element);
		for (Object child : element.getChildren()){
			if (!addContentInteraction(interaction,element,(Element)child)){
				showUnexpected((Element)child, interaction);
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
				showUnexpected((Element)child, geneticInteraction);
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
				showUnexpected((Element)child, molecularInteraction);
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
				showUnexpected((Element)child, templateReaction);
			}
		}
		pathwayModel.add(templateReaction);
		return templateReaction;
	}

	private PhysicalEntity addObjectPhysicalEntity(Element element) {
		PhysicalEntity physicalEntity = new PhysicalEntity();
		Namespace rdf = Namespace.getNamespace("rdf", DefaultNameSpaces.RDF.uri);
		if (element.getAttributes().size()>0){
			physicalEntity = new PhysicalEntity();
			addAttributes(physicalEntity,element);
		}
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentPhysicalEntity(physicalEntity, element, childElement)){
					showUnexpected(childElement, physicalEntity);
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
					showUnexpected((Element)child, control);
				}
			}
		}
		pathwayModel.add(control);
		return control;
	}

	private Catalysis addObjectCatalysis(Element element) {
		Catalysis catalysis = new Catalysis();
		addAttributes(catalysis, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				if (!addContentCatalysis(catalysis, element, (Element)child)){
					showUnexpected((Element)child, catalysis);
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
					showUnexpected((Element)child, modulation);
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
					showUnexpected((Element)child, templateReactionRegulation);
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
					showUnexpected(childElement, complex);
				}
			}
		}
		pathwayModel.add(complex);
		return complex;
	}
	
	private Dna addObjectDna(Element element) {
		Dna dna = new Dna();
		addAttributes(dna, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentDna(dna, element, childElement)){
					showUnexpected(childElement, dna);
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
					showUnexpected(childElement, dnaRegion);
				}
			}
		}
		pathwayModel.add(dnaRegion);
		return dnaRegion;
	}
		
	private Xref addObjectXref(Element element){
		Namespace bp = Namespace.getNamespace("bp", "http://www.biopax.org/release/biopax-level2.owl#");
		if (element.getChild("unificationXref",bp)!=null){
			UnificationXref xref = addObjectUnificationXref(element.getChild("unificationXref",bp));
			return xref;
		}
		if (element.getChild("relationshipXref",bp)!=null){
			RelationshipXref xref = addObjectRelationshipXref(element.getChild("relationshipXref",bp));
			return xref;
		}
		if (element.getChild("publicationXref",bp)!=null){
			PublicationXref xref = addObjectPublicationXref(element.getChild("publicationXref",bp));
			return xref;
		}
		
		if (element.getChildren().size() == 0){
			XrefProxy xref = new XrefProxy();
			addAttributes(xref, element);
			pathwayModel.add(xref);
			return xref;
		}else{
			Xref xref = new Xref();
			for (Object child : element.getChildren()){
				if (child instanceof Element){
					Element childElement = (Element)child;
					if (!addContentXref(xref, element, childElement)){
						showUnexpected(childElement, xref);
					}
				}
			}
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
					showUnexpected(childElement, protein);
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
					showUnexpected(childElement, rna);
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
					showUnexpected(childElement, rnaRegion);
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
					showUnexpected(childElement, smallMolecule);
				}
			}
		}
		pathwayModel.add(smallMolecule);
		return smallMolecule;
	}


	private BiochemicalReaction addObjectBiochemicalReaction(Element element) {
		BiochemicalReaction biochemicalReaction = new BiochemicalReactionImpl();
		addAttributes(biochemicalReaction, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentBiochemicalReaction(biochemicalReaction, element, (Element)child)){
					showUnexpected((Element)child, biochemicalReaction);
				}
			}
		}
		pathwayModel.add(biochemicalReaction);
		return biochemicalReaction;
	}

	private BioSource addObjectBioSource(Element element) {
		BioSource bioSource = new BioSource();
		addAttributes(bioSource, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentBioSource(bioSource, element, (Element)child)){
					showUnexpected((Element)child, bioSource);
				}
			}
		}
		pathwayModel.add(bioSource);
		return bioSource;
	}

	private ChemicalStructure addObjectChemicalStructure(Element element) {
		ChemicalStructure chemicalStructure = new ChemicalStructure();
		addAttributes(chemicalStructure, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentChemicalStructure(chemicalStructure, element, (Element)child)){
					showUnexpected((Element)child, chemicalStructure);
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
					showUnexpected(childElement, deltaG);
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
					showUnexpected(childElement, controlledVocabulary);
				}
			}
		}
		pathwayModel.add(controlledVocabulary);
		return controlledVocabulary;
	}

	private CellularLocationVocabulary addObjectCellularLocationVocabulary(Element element) {
		CellularLocationVocabulary cellularLocationVocabulary = new CellularLocationVocabulary();
		addAttributes(cellularLocationVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(cellularLocationVocabulary, element, childElement)){
					showUnexpected(childElement, cellularLocationVocabulary);
				}
			}
		}
		pathwayModel.add(cellularLocationVocabulary);
		return cellularLocationVocabulary;
	}

	private CellVocabulary addObjectCellVocabulary(Element element) {
		CellVocabulary cellVocabulary = new CellVocabulary();
		addAttributes(cellVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(cellVocabulary, element, childElement)){
					showUnexpected(childElement, cellVocabulary);
				}
			}
		}
		pathwayModel.add(cellVocabulary);
		return cellVocabulary;
	}

	private EntityFeature addObjectEntityFeature(Element element) {
		EntityFeature entityFeature = new EntityFeatureImpl();
		addAttributes(entityFeature, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentEntityFeature(entityFeature, element, childElement)){
					showUnexpected(childElement, entityFeature);
				}
			}
		}
		pathwayModel.add(entityFeature);
		return entityFeature;
	}

	private Evidence addObjectEvidence(Element element) {
		if (element.getName().equals("EVIDENCE")){
			Element evidenceChild = element.getChild("evidence",bp);
			if (evidenceChild != null){
				element = evidenceChild;
			}
		}
		Evidence evidence = new Evidence();
		addAttributes(evidence, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentEvidence(evidence, element, childElement)){
					showUnexpected(childElement, evidence);
				}
			}
		}
		pathwayModel.add(evidence);
		return evidence;
	}

	private ExperimentalForm addObjectExperimentalForm(Element element) {
		ExperimentalForm experimentalForm = new ExperimentalForm();
		addAttributes(experimentalForm, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentExperimentalForm(experimentalForm, element, childElement)){
					showUnexpected(childElement, experimentalForm);
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
					showUnexpected(childElement, kPrime);
				}
			}
		}
		pathwayModel.add(kPrime);
		return kPrime;
	}

	private DnaReference addObjectDnaReference(Element element) {
		DnaReference dnaReference = new DnaReference();
		addAttributes(dnaReference, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentDnaReference(dnaReference, element, childElement)){
					showUnexpected(childElement, dnaReference);
				}
			}
		}
		pathwayModel.add(dnaReference);
		return dnaReference;
	}

	private DnaRegionReference addObjectDnaRegionReference(Element element) {
		DnaRegionReference dnaRegionReference = new DnaRegionReference();
		addAttributes(dnaRegionReference, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentDnaRegionReference(dnaRegionReference, element, childElement)){
					showUnexpected(childElement, dnaRegionReference);
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
					showUnexpected(childElement, proteinReference);
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
					showUnexpected(childElement, rnaReference);
				}
			}
		}
		pathwayModel.add(rnaReference);
		return rnaReference;
	}

	private RnaRegionReference addObjectRnaRegionReference(Element element) {
		RnaRegionReference rnaRegionReference = new RnaRegionReference();
		addAttributes(rnaRegionReference, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentRnaRegionReference(rnaRegionReference, element, childElement)){
					showUnexpected(childElement, rnaRegionReference);
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
					showUnexpected(childElement, smallMoleculeReference);
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
					showUnexpected(childElement, bindingFeature);
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
					showUnexpected(childElement, fragmentFeature);
				}
			}
		}
		pathwayModel.add(fragmentFeature);
		return fragmentFeature;
	}

	private CovalentBindingFeature addObjectCovalentBindingFeature(Element element) {
		CovalentBindingFeature covalentBindingFeature = new CovalentBindingFeature();
		addAttributes(covalentBindingFeature, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentCovalentBindingFeature(covalentBindingFeature, element, childElement)){
					showUnexpected(childElement, covalentBindingFeature);
				}
			}
		}
		pathwayModel.add(covalentBindingFeature);
		return covalentBindingFeature;
	}

	private EntityReferenceTypeVocabulary addObjectEntityReferenceTypeVocabulary(Element element) {
		EntityReferenceTypeVocabulary entityReferenceTypeVocabulary = new EntityReferenceTypeVocabulary();
		addAttributes(entityReferenceTypeVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(entityReferenceTypeVocabulary, element, childElement)){
					showUnexpected(childElement, entityReferenceTypeVocabulary);
				}
			}
		}
		pathwayModel.add(entityReferenceTypeVocabulary);
		return entityReferenceTypeVocabulary;
	}

	private EvidenceCodeVocabulary addObjectEvidenceCodeVocabulary(Element element) {
		EvidenceCodeVocabulary evidenceCodeVocabulary = new EvidenceCodeVocabulary();
		addAttributes(evidenceCodeVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(evidenceCodeVocabulary, element, childElement)){
					showUnexpected(childElement, evidenceCodeVocabulary);
				}
			}
		}
		pathwayModel.add(evidenceCodeVocabulary);
		return evidenceCodeVocabulary;
	}

	private ExperimentalFormVocabulary addObjectExperimentalFormVocabulary(Element element) {
		ExperimentalFormVocabulary experimentalFormVocabulary = new ExperimentalFormVocabulary();
		addAttributes(experimentalFormVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(experimentalFormVocabulary, element, childElement)){
					showUnexpected(childElement, experimentalFormVocabulary);
				}
			}
		}
		pathwayModel.add(experimentalFormVocabulary);
		return experimentalFormVocabulary;
	}

	private InteractionVocabulary addObjectInteractionVocabulary(Element element) {
		InteractionVocabulary interactionVocabulary = new InteractionVocabulary();
		addAttributes(interactionVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(interactionVocabulary, element, childElement)){
					showUnexpected(childElement, interactionVocabulary);
				}
			}
		}
		pathwayModel.add(interactionVocabulary);
		return interactionVocabulary;
	}

	private PhenotypeVocabulary addObjectPhenotypeVocabulary(Element element) {
		PhenotypeVocabulary phenotypeVocabulary = new PhenotypeVocabulary();
		addAttributes(phenotypeVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentPhenotypeVocabulary(phenotypeVocabulary, element, childElement)){
					showUnexpected(childElement, phenotypeVocabulary);
				}
			}
		}
		pathwayModel.add(phenotypeVocabulary);
		return phenotypeVocabulary;
	}

	private RelationshipTypeVocabulary addObjectRelationshipTypeVocabulary(Element element) {
		RelationshipTypeVocabulary relationshipTypeVocabulary = new RelationshipTypeVocabulary();
		addAttributes(relationshipTypeVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(relationshipTypeVocabulary, element, childElement)){
					showUnexpected(childElement, relationshipTypeVocabulary);
				}
			}
		}
		pathwayModel.add(relationshipTypeVocabulary);
		return relationshipTypeVocabulary;
	}

	private SequenceModificationVocabulary addObjectSequenceModificationVocabulary(Element element) {
		SequenceModificationVocabulary sequenceModificationVocabulary = new SequenceModificationVocabulary();
		addAttributes(sequenceModificationVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(sequenceModificationVocabulary, element, childElement)){
					showUnexpected(childElement, sequenceModificationVocabulary);
				}
			}
		}
		pathwayModel.add(sequenceModificationVocabulary);
		return sequenceModificationVocabulary;
	}

	private SequenceRegionVocabulary addObjectSequenceRegionVocabulary(Element element) {
		SequenceRegionVocabulary sequenceRegionVocabulary = new SequenceRegionVocabulary();
		addAttributes(sequenceRegionVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(sequenceRegionVocabulary, element, childElement)){
					showUnexpected(childElement, sequenceRegionVocabulary);
				}
			}
		}
		pathwayModel.add(sequenceRegionVocabulary);
		return sequenceRegionVocabulary;
	}

	private TissueVocabulary addObjectTissueVocabulary(Element element) {
		TissueVocabulary tissueVocabulary = new TissueVocabulary();
		addAttributes(tissueVocabulary, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentControlledVocabulary(tissueVocabulary, element, childElement)){
					showUnexpected(childElement, tissueVocabulary);
				}
			}
		}
		pathwayModel.add(tissueVocabulary);
		return tissueVocabulary;
	}

	private Pathway addObjectPathway(Element element) {
		Pathway pathway = new Pathway();
		addAttributes(pathway, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentPathway(pathway, element, childElement)){
					showUnexpected(childElement, pathway);
				}
			}
		}
		pathwayModel.add(pathway);
		return pathway;
	}

	private PathwayStep addObjectPathwayStep(Element element) {
		PathwayStep pathwayStep = new PathwayStep();
		addAttributes(pathwayStep, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentPathwayStep(pathwayStep, element, childElement)){
					showUnexpected(childElement, pathwayStep);
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
					showUnexpected(childElement, biochemicalPathwayStep);
				}
			}
		}
		pathwayModel.add(biochemicalPathwayStep);
		return biochemicalPathwayStep;
	}

	private Provenance addObjectProvenance(Element element) {
		Provenance provenance = new Provenance();
		addAttributes(provenance, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentProvenance(provenance, element, childElement)){
					showUnexpected(childElement, provenance);
				}
			}
		}
		pathwayModel.add(provenance);
		return provenance;
	}

	private Score addObjectScore(Element element) {
		Score score = new Score();
		addAttributes(score, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentScore(score, element, childElement)){
					showUnexpected(childElement, score);
				}
			}
		}
		pathwayModel.add(score);
		return score;
	}

	private SequenceLocation addObjectSequenceLocation(Element element) {
		SequenceLocation sequenceLocation = new SequenceLocation();
		addAttributes(sequenceLocation, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentSequenceLocation(sequenceLocation, element, childElement)){
					showUnexpected(childElement, sequenceLocation);
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
					showUnexpected(childElement, sequenceInterval);
				}
			}
		}
		pathwayModel.add(sequenceInterval);
		return sequenceInterval;
	}

	private SequenceSite addObjectSequenceSite(Element element) {
		SequenceSite sequenceSite = new SequenceSite();
		addAttributes(sequenceSite, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentSequenceSite(sequenceSite, element, childElement)){
					showUnexpected(childElement, sequenceSite);
				}
			}
		}
		pathwayModel.add(sequenceSite);
		return sequenceSite;
	}

	private Stoichiometry addObjectStoichiometry(Element element) {
		Stoichiometry stoichiometry = new Stoichiometry();
		addAttributes(stoichiometry, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentStoichiometry(stoichiometry, element, childElement)){
					showUnexpected(childElement, stoichiometry);
				}
			}
		}
		pathwayModel.add(stoichiometry);
		return stoichiometry;
	}

	private Xref addObjectXRef(Element element) {
		Xref xRef = new Xref();
		addAttributes(xRef, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentXref(xRef, element, childElement)){
					showUnexpected(childElement, xRef);
				}
			}
		}
		pathwayModel.add(xRef);
		return xRef;
	}

	private PublicationXref addObjectPublicationXref(Element element) {
		PublicationXref publicationXref = new PublicationXref();
		addAttributes(publicationXref, element);
		for (Object child : element.getChildren()){
			if (child instanceof Element){
				Element childElement = (Element)child;
				if (!addContentPublicationXref(publicationXref, element, childElement)){
					showUnexpected(childElement, publicationXref);
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
					showUnexpected(childElement, relationshipXref);
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
					showUnexpected(childElement, unificationXref);
				}
			}
		}
		pathwayModel.add(unificationXref);
		return unificationXref;
	}

	
	
}

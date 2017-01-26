/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.relationship;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.pathway.BioPAXUtil;
import org.vcell.pathway.BioPAXUtil.Process;
import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.Complex;
import org.vcell.pathway.ComplexAssembly;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.pathway.Protein;
import org.vcell.pathway.SmallMolecule;
import org.vcell.pathway.Transport;
import org.vcell.pathway.kinetics.SBPAXKineticsExtractor;
import org.vcell.pathway.persistence.BiopaxProxy.PhysicalEntityProxy;
import org.vcell.util.TokenMangler;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.BioPaxObjectPropertiesPanel;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Model;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.ModelException;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.parser.ExpressionException;

public class PathwayMapping {
	
	public void createBioModelEntitiesFromBioPaxObjects(BioModel bioModel, ArrayList<ConversionTableRow> conversionTableRows, boolean addSubunits) throws Exception
	{
		//
		// TODO: add to the conversion table rows all the components (proteins, small molecules, etc) of any Complex already here
		//  so that molecular types can be created right away
		//
		ArrayList<ConversionTableRow> addList = new ArrayList<>();
		for(ConversionTableRow ctr : conversionTableRows) {
			BioPaxObject bpo = ctr.getBioPaxObject();
			if(bpo instanceof Complex) {
				Complex complex = (Complex)bpo;
				for(PhysicalEntity pe : complex.getComponents()) {
					ConversionTableRow row = new ConversionTableRow(pe, true);
					if(!addList.contains(row)) {
						addList.add(row);
					}
				}
			}
		}
		for(ConversionTableRow candidate : addList) {
			if(!conversionTableRows.contains(candidate)) {
				conversionTableRows.add(candidate);
			}
		}
		
		for(ConversionTableRow ctr : conversionTableRows) {
			BioPaxObject bpo = ctr.getBioPaxObject();
			// any physical entity that's not a complex is a molecular type
			// protein, small molecule, dna, dna region, rna, rna region
			if(bpo instanceof PhysicalEntity && !(bpo instanceof Complex || bpo instanceof PhysicalEntityProxy)) {
				createMolecularTypeFromBioPaxObject(bioModel, (PhysicalEntity)bpo, addSubunits);
			}
		}
		
		for(ConversionTableRow ctr : conversionTableRows) {
			BioPaxObject bpo = ctr.getBioPaxObject();
			if(bpo instanceof PhysicalEntity && !ctr.isMoleculeOnly()) {	// if not molecule only we need to create a species too
				createSpeciesContextFromTableRow(bioModel, (PhysicalEntity)bpo, ctr.stoich(), ctr.id(), ctr.location(), addSubunits);
			} else if(bpo instanceof ComplexAssembly) { // Conversion : ComplexAssembly
				createReactionStepsFromTableRow(bioModel, (ComplexAssembly)bpo, ctr.stoich(), ctr.id(), ctr.location(), conversionTableRows, addSubunits);
			} else if(bpo instanceof Transport) { // Conversion : Transport
				createReactionStepsFromTableRow(bioModel, (Transport)bpo, ctr.stoich(), ctr.id(), ctr.location(), conversionTableRows, addSubunits);
//			} else if(ctr.getBioPaxObject() instanceof Degradation) { // Conversion : Degradation 
//				// to do
			} else if(bpo instanceof Conversion) { // Conversion : BiochemicalReaction
				createReactionStepsFromTableRow(bioModel, (Conversion)bpo, ctr.stoich(), ctr.id(), ctr.location(), conversionTableRows, addSubunits);
			}
		}
	}
	
	private static void printSet(Set<RelationshipObject> set) {
		for (RelationshipObject ro : set) {
			System.out.println("ro:" + System.identityHashCode(ro));
			String n1 = "   bMod:" + ro.getBioModelEntityObject().getDisplayName();
			String n2 = "   bPax:" + ro.getBioPaxObject().getDisplayName();
			System.out.println(n1 + ", " + n2);
		}
		System.out.println("                             ---------");
	}
	
// TODO: not in use 
//	public void createBioModelEntitiesFromBioPaxObjects(BioModel bioModel, Object[] selectedObjects) throws Exception
//	{
//		for(int i = 0; i < selectedObjects.length; i++) {
//			if(selectedObjects[i] instanceof BioPaxObject) {
//				BioPaxObject bioPaxObject = (BioPaxObject)selectedObjects[i];
//				if(bioPaxObject instanceof PhysicalEntity && !(bioPaxObject instanceof Complex)) {
//					createMolecularTypeFromBioPaxObject(bioModel, (PhysicalEntity)bioPaxObject);
//				}
//			} else if(selectedObjects[i] instanceof ConversionTableRow) {
//				ConversionTableRow ctr = (ConversionTableRow)selectedObjects[i];
//				if(ctr.getBioPaxObject() instanceof PhysicalEntity && !(ctr.getBioPaxObject() instanceof Complex)) {
//					createMolecularTypeFromBioPaxObject(bioModel, (PhysicalEntity)ctr.getBioPaxObject());
//				}
//			}
//		}
//		
//		for(int i = 0; i < selectedObjects.length; i++) {
//			if(selectedObjects[i] instanceof BioPaxObject) {
//				BioPaxObject bioPaxObject = (BioPaxObject)selectedObjects[i];
//				if(bioPaxObject instanceof PhysicalEntity) {
//					createSpeciesContextFromBioPaxObject(bioModel, (PhysicalEntity)bioPaxObject);
//				} else if(bioPaxObject instanceof Conversion) {
//					createReactionStepsFromBioPaxObject(bioModel, (Conversion)bioPaxObject);
//				}
//			} else if(selectedObjects[i] instanceof ConversionTableRow) {
//				ConversionTableRow ctr = (ConversionTableRow)selectedObjects[i];
//				if(ctr.getBioPaxObject() instanceof PhysicalEntity) {
//					createSpeciesContextFromTableRow(bioModel, (PhysicalEntity)ctr.getBioPaxObject(), ctr.stoich(), ctr.id(), ctr.location());
//				} else if(ctr.getBioPaxObject() instanceof Conversion) {
//					createReactionStepsFromTableRow(bioModel, (Conversion)ctr.getBioPaxObject(), ctr.stoich(), ctr.id(), ctr.location());
//				}
//			}
//		}
//	}
	
	private MolecularType createMolecularTypeFromBioPaxObject(BioModel bioModel, PhysicalEntity bioPaxObject, boolean addSubunits) {

		String name;
		if(bioPaxObject.getName().size() == 0) {
			name = getSafetyName(bioPaxObject.getID());
		} else {
			name = getSafetyName(bioPaxObject.getName().get(0));
		}

		RbmModelContainer rbmmc = bioModel.getModel().getRbmModelContainer();
		if(rbmmc.getMolecularType(name) != null) {
			MolecularType mt = rbmmc.getMolecularType(name);		// already exists
			// check whether it links to pathway object, create relationship object if not
			if(!bioModel.getRelationshipModel().isRelationship(mt, bioPaxObject)) {
				RelationshipObject ro = new RelationshipObject(mt, bioPaxObject);
				bioModel.getRelationshipModel().addRelationshipObject(ro);
			}
			return mt;
		}
		
		int numSubunits = 0;
		if(addSubunits) {
			for (String comment : bioPaxObject.getComments()) {
				numSubunits = StringUtils.countMatches(comment, "SUBUNIT:");
			}
		}
		
		MolecularType mt = new MolecularType(name, bioModel.getModel());
		try {
			for(int i = 0; i<numSubunits; i++) {
				MolecularComponent mc = new MolecularComponent("Subunit"+i);
				mt.addMolecularComponent(mc);
			}
			rbmmc.addMolecularType(mt, true);
			// we know for sure that a relationship can't exist, so we make one
			RelationshipObject ro = new RelationshipObject(mt, bioPaxObject);
			bioModel.getRelationshipModel().addRelationshipObject(ro);
		} catch (ModelException | PropertyVetoException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> commentList = bioPaxObject.getComments();
		final String htmlStart = "<html><font face = \"Arial\"><font size =\"-2\">";
		final String htmlEnd = "</font></font></html>";
		if(commentList != null && !commentList.isEmpty()) {
			String comment = commentList.get(0);
			if(!comment.isEmpty()) {
				String text = BioPaxObjectPropertiesPanel.FormatDetails(comment);
				mt.setComment(htmlStart + text + htmlEnd);
			}
		} else {
			mt.setComment("");
		}
		return mt;
	}
	
	private SpeciesContext createSpeciesContextFromBioPaxObject(BioModel bioModel, PhysicalEntity bioPaxObject, boolean addSubunits) throws Exception
	{
		String name;
		if(bioPaxObject.getName().size() == 0) {
			name = getSafetyName(bioPaxObject.getID());
		} else {
			name = getSafetyName(bioPaxObject.getName().get(0));
		}
		Model model = bioModel.getModel();
		SpeciesContext freeSpeciesContext = model.getSpeciesContext(name);
		if(freeSpeciesContext == null) {
		// create the new speciesContex Object, and link it to the corresponding pathway object
			if(model.getSpecies(name) == null) {
				freeSpeciesContext = model.createSpeciesContext(model.getStructures()[0]);
			} else {
				 freeSpeciesContext = new SpeciesContext(model.getSpecies(name), model.getStructures()[0]);
			}
			freeSpeciesContext.setName(name);
			RelationshipObject newRelationship = new RelationshipObject(freeSpeciesContext, bioPaxObject);
			bioModel.getRelationshipModel().addRelationshipObject(newRelationship);		
		} else {
			// if it is in the bioModel, then check whether it links to pathway object or not
			HashSet<RelationshipObject> linkedReObjects = 
				bioModel.getRelationshipModel().getRelationshipObjects(freeSpeciesContext);
			if(linkedReObjects != null) {
				boolean flag = true;
				for(RelationshipObject reObject: linkedReObjects) {
					if(reObject.getBioPaxObject() == bioPaxObject) {
						flag = false;
						break;
					}
				}
				if(flag) {
					RelationshipObject newSpeciesContext = new RelationshipObject(freeSpeciesContext, bioPaxObject);
					bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
				}
			} else {
				RelationshipObject newSpeciesContext = new RelationshipObject(freeSpeciesContext, bioPaxObject);
				bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
			}
		}
		
		if(!freeSpeciesContext.hasSpeciesPattern()) {
			SpeciesPattern sp = generateSpeciesPattern(bioModel, bioPaxObject, addSubunits);
			if(sp != null && !sp.getMolecularTypePatterns().isEmpty()) {
				freeSpeciesContext.setSpeciesPattern(sp);
			}
		}
		return freeSpeciesContext;
	}
	
	private SpeciesContext createSpeciesContextFromTableRow(BioModel bioModel, PhysicalEntity bioPaxObject, 
			double stoich, String id, String location, boolean addSubunits) throws Exception
	{
		// use user defined id as the name of the speciesContext
		String safeId = getSafetyName(id);
		String name;
		if(bioPaxObject.getName().size() == 0) {
			name = getSafetyName(bioPaxObject.getID());
		} else {
			name = getSafetyName(bioPaxObject.getName().get(0));
		}
		
		Model model = bioModel.getModel();
		SpeciesContext freeSpeciesContext = model.getSpeciesContext(safeId);
		if(freeSpeciesContext == null) {
		// create the new speciesContex Object, and link it to the corresponding pathway object
			if(model.getSpecies(name) == null) {
				freeSpeciesContext = model.createSpeciesContext(model.getStructure(location));
			} else {
				freeSpeciesContext = new SpeciesContext(model.getSpecies(name), model.getStructure(location));
			}
			freeSpeciesContext.setName(safeId);
			RelationshipObject newRelationship = new RelationshipObject(freeSpeciesContext, bioPaxObject);
			bioModel.getRelationshipModel().addRelationshipObject(newRelationship);		
		} else {
			// if it is in the bioModel, then check whether it links to pathway object or not
			HashSet<RelationshipObject> linkedReObjects = bioModel.getRelationshipModel().getRelationshipObjects(freeSpeciesContext);
			if(linkedReObjects != null) {
				boolean flag = true;
				for(RelationshipObject reObject: linkedReObjects) {
					if(reObject.getBioPaxObject() == bioPaxObject) {
						flag = false;
						break;
					}
				}
				if(flag) {
					RelationshipObject newSpeciesContext = new RelationshipObject(freeSpeciesContext, bioPaxObject);
					bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
				}
			} else {
				RelationshipObject newSpeciesContext = new RelationshipObject(freeSpeciesContext, bioPaxObject);
				bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
			}
		}
		// new or existing species now will get a species pattern if they don't have one already
		// because of the association with a physical entity
		// if the physical entity is a complex then the species pattern may consist of several molecules
		// else if it's proteine, small molecule, dna, etc the sp will consist of one molecule
		if(!freeSpeciesContext.hasSpeciesPattern()) {
			SpeciesPattern sp = generateSpeciesPattern(bioModel, bioPaxObject, addSubunits);
			if(sp != null && !sp.getMolecularTypePatterns().isEmpty()) {
				freeSpeciesContext.setSpeciesPattern(sp);
				sp.initializeBonds(MolecularComponentPattern.BondType.None);

			}
		}
		return freeSpeciesContext;
	}
	private SpeciesPattern generateSpeciesPattern(BioModel bioModel, PhysicalEntity bioPaxObject, boolean addSubunits) {
		SpeciesPattern sp = new SpeciesPattern();

		if(bioPaxObject instanceof Complex) {
			Complex c = (Complex)bioPaxObject;
			for(PhysicalEntity pc : c.getComponents()) {
				MolecularType mt = createMolecularTypeFromBioPaxObject(bioModel, pc, addSubunits);
				MolecularTypePattern mtp = new MolecularTypePattern(mt);
				sp.addMolecularTypePattern(mtp);
			}
			return sp;
		} else {		//  else if(!(bioPaxObject instanceof Complex))
			MolecularType mt = createMolecularTypeFromBioPaxObject(bioModel, bioPaxObject, addSubunits);
			MolecularTypePattern mtp = new MolecularTypePattern(mt);
			sp.addMolecularTypePattern(mtp);
			return sp;
		}
//		return null;
	}
	
	private void createReactionStepsFromBioPaxObject(BioModel bioModel, Conversion conversion, boolean addSubunits) throws Exception
	{
		if (bioModel==null) {
			return;
		}
		for(Process process :BioPAXUtil.getAllProcesses(bioModel.getPathwayModel(), conversion)) {
			String name = process.getName();
			if(bioModel.getModel().getReactionStep(name) == null) {
				// create a new reactionStep object
				ReactionStep simpleReactionStep = bioModel.getModel().createSimpleReaction(bioModel.getModel().getStructures()[0]);
				simpleReactionStep.setName(name);
				RelationshipObject newRelationship = new RelationshipObject(simpleReactionStep, conversion);
				bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
				createReactionStepFromPathway(bioModel, process, simpleReactionStep, newRelationship, addSubunits);
			} else {
				// add missing parts for the existing reactionStep
				RelationshipObject newRelationship = new RelationshipObject(bioModel.getModel().getReactionStep(name), conversion);
				bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
				createReactionStepFromPathway(bioModel, process, bioModel.getModel().getReactionStep(name), newRelationship, addSubunits);
			}
		}
	}

	private void createReactionStepsFromTableRow(BioModel bioModel, Conversion bioPaxObject,
			double stoich, String id, String location, ArrayList<ConversionTableRow> conversionTableRows, boolean addSubunits) throws Exception
	{
		// use user defined id as the name of the reaction name
		// get participants of this reaction from table rows
		if (bioModel==null) {
			return;
		}
		for(Process process: BioPAXUtil.getAllProcesses(bioModel.getPathwayModel(), bioPaxObject)) {
			ArrayList<ConversionTableRow> participants = new ArrayList<ConversionTableRow>();
			for(ConversionTableRow ctr : conversionTableRows) {
				if(ctr.interactionId() == null) {
					continue;
				}
				if(ctr.interactionId().equals(bioPaxObject.getID())) {
					participants.add(ctr);
				}
			}
			// create reaction object
			String name = getSafetyName(process.getName() + "_" + location);
			ReactionStep reactionStep = bioModel.getModel().getReactionStep(name);
			if(reactionStep == null) {
				// create a new reactionStep object
				ReactionStep simpleReactionStep = bioModel.getModel().createSimpleReaction(bioModel.getModel().getStructure(location));
				simpleReactionStep.setName(name);
				RelationshipObject newRelationship = new RelationshipObject(simpleReactionStep, bioPaxObject);
				bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
				createReactionStep(bioModel, process, simpleReactionStep, newRelationship, participants, addSubunits);
				addKinetics(simpleReactionStep, process);
			} else {
				//			bioModel.getModel().getReactionStep(safeId).setStructure(bioModel.getModel().getStructure(location));
				// add missing parts for the existing reactionStep
				RelationshipObject newRelationship = new RelationshipObject(reactionStep, bioPaxObject);
				bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
				createReactionStep(bioModel, process, reactionStep, newRelationship, participants, addSubunits);
				addKinetics(reactionStep, process);
			}
		}
	}
	
	private void createReactionStepsFromTableRow(BioModel bioModel, ComplexAssembly bioPaxObject,
			double stoich, String id, String location, ArrayList<ConversionTableRow> conversionTableRows, boolean addSubunits) throws Exception
	{
		// use user defined id as the name of the reaction name
		// get participants from table rows
		if (bioModel==null){
			return;
		}
		// there is just one "normal" reaction but it is possible to have controls associated to it, we take them too
		for(Process process: BioPAXUtil.getAllProcesses(bioModel.getPathwayModel(), bioPaxObject)) {
			ArrayList<ConversionTableRow> participants = new ArrayList<ConversionTableRow>();
			for(ConversionTableRow ctr : conversionTableRows) {			// find the participants of this process
				if(ctr.interactionId() == null) {
					continue;		// proteins that are brought in as molecular type only have interaction id == null
									// because they don't participate to the interaction
				}
				if(ctr.interactionId().equals(bioPaxObject.getID())) {
					participants.add(ctr);
				}
			}
			// create reaction object
			String name = getSafetyName(process.getName() + "_" + location);
			ReactionStep reactionStep = bioModel.getModel().getReactionStep(name);
			if(reactionStep == null) {
				// create a new reactionStep object
				ReactionStep simpleReactionStep = bioModel.getModel().createSimpleReaction(bioModel.getModel().getStructure(location));
				simpleReactionStep.setName(name);
				RelationshipObject newRelationship = new RelationshipObject(simpleReactionStep, bioPaxObject);
				bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
				createReactionStep(bioModel, process, simpleReactionStep, newRelationship, participants, addSubunits);
				addKinetics(simpleReactionStep, process);
			} else {
				//			bioModel.getModel().getReactionStep(safeId).setStructure(bioModel.getModel().getStructure(location));
				// add missing parts for the existing reactionStep
				RelationshipObject newRelationship = new RelationshipObject(reactionStep, bioPaxObject);
				bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
				createReactionStep(bioModel, process, reactionStep, newRelationship, participants, addSubunits);
				addKinetics(reactionStep, process);
			}
		}
	}
	
	private void createReactionStepsFromTableRow(BioModel bioModel, Transport bioPaxObject,
			double stoich, String id, String location, ArrayList<ConversionTableRow> conversionTableRows, boolean addSubunits) throws Exception
	{
		if (bioModel==null) {
			return;
		}
		for(Process process: BioPAXUtil.getAllProcesses(bioModel.getPathwayModel(), bioPaxObject)) {
			// use user defined id as the name of the reaction name
			// get participants from table rows
			ArrayList<ConversionTableRow> participants = new ArrayList<ConversionTableRow>();
			for(ConversionTableRow ctr : conversionTableRows) {
				if(ctr.interactionId().equals(bioPaxObject.getID())) {
					participants.add(ctr);
				}
			}
			// create reaction object
			String name = getSafetyName(process.getName() + "_" + location);
			if(bioModel.getModel().getReactionStep(name) == null) {
				// create a new reactionStep object
				FluxReaction fluxReactionStep = bioModel.getModel().createFluxReaction((Membrane)bioModel.getModel().getStructure(location));
				fluxReactionStep.setName(name);
				RelationshipObject newRelationship = new RelationshipObject(fluxReactionStep, bioPaxObject);
				bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
				createReactionStep(bioModel, process, fluxReactionStep, newRelationship, participants, addSubunits);
			} else {
				//			bioModel.getModel().getReactionStep(safeId).setStructure(bioModel.getModel().getStructure(location));
				// add missing parts for the existing reactionStep
				RelationshipObject newRelationship = new RelationshipObject(bioModel.getModel().getReactionStep(name), bioPaxObject);
				bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
				createReactionStep(bioModel, process, bioModel.getModel().getReactionStep(name), newRelationship, participants, addSubunits);
			}
		}
	}
	
	/*
	 * for reaction:
	 * 1. annotate the selected vcell object using linked pathway conversion
	 * 2. add non-existing speciesContexts from linked pathway conversion
	 * 3. add links between relative vcell objects and pathway objects
	 * Questions:
	 * - how to deal with the case that the reaction is existing in the model?
	 * 		+ add it in no matter what? 
	 * 				(this is the version we have now: 
	 * 					add the duplicated reactions in without name changing, 
	 * 				 	all duplicated reactions share the same participant objects)
	 *      + just modify the existing one?
	 */
	private void createReactionStep(BioModel bioModel, Process process, ReactionStep reactionStep, 
			RelationshipObject relationshipObject, ArrayList<ConversionTableRow> participants, boolean addSubunits) 
	throws Exception
	{
		if (reactionStep == null || bioModel == null || bioModel.getRelationshipModel() == null || participants.size() < 1) {
			return;
		}
		ArrayList<ReactionParticipant> rplist = new ArrayList<ReactionParticipant>();
		// create and add reaction participants to list 
		for(ConversionTableRow ctr : participants) {
			if(ctr.getBioPaxObject() instanceof Conversion) continue;
			int stoich = ctr.stoich().intValue();
			String safeId = getSafetyName(ctr.id());

			// get speciesContext object based on its name
			// if the speciesContext is not existed, create a new one
			createSpeciesContextFromTableRow(bioModel, (PhysicalEntity)ctr.getBioPaxObject(), ctr.stoich(), ctr.id(), ctr.location(), addSubunits);
			
			// add the existed speciesContext objects or new speciesContext objects to reaction participant list
			if(ctr.participantType().equals("Reactant")) {
				if (reactionStep instanceof SimpleReaction || reactionStep instanceof FluxReaction) {
					rplist.add(new Reactant(null,reactionStep, bioModel.getModel().getSpeciesContext(safeId), stoich));
				}
			} else if(ctr.participantType().equals("Product")) {
				if (reactionStep instanceof SimpleReaction || reactionStep instanceof FluxReaction) {
					rplist.add(new Product(null,reactionStep, bioModel.getModel().getSpeciesContext(safeId), stoich));
				}
			}		// we do not add catalysts
		}
		ReactionParticipant[] rpArray = rplist.toArray(new ReactionParticipant[0]);
		reactionStep.setReactionParticipants(rpArray);
		
		// add Controls to the reaction
		Set<PhysicalEntity> controllers = process.getControllers();
		for(ConversionTableRow ctr : participants) {
			if(controllers.contains(ctr.getBioPaxObject())) {
				if(ctr.participantType().equals("Catalyst")) {
					String safeId = getSafetyName(ctr.id());
					/* 
					 * using addCatalyst() to create catalyst in reaction: 
					 * this function cannot allow an object to be catalyst and (reactant/product) in the same reaction
					 */
					//reactionStep.addCatalyst(bioModel.getModel().getSpeciesContext(safeId));

					/* However, in pathway interaction object, an physicalEntity can be catalyst and (reactant/product) in the same reaction
					 * So we just call create catalyst for the reaction no matter what rolls the object is playing in the reaction
					 * Switch back to the addCatalyst() function when it is necessary, but exceptions make be reported for some reactions
					 */
					reactionStep.addReactionParticipant(new Catalyst(null,reactionStep, bioModel.getModel().getSpeciesContext(safeId)));
				} else if(ctr.participantType().equals("Control")) {
					String safeId = getSafetyName(ctr.id());
					//reactionStep.addCatalyst(bioModel.getModel().getSpeciesContext(safeId));
					reactionStep.addReactionParticipant(new Catalyst(null,reactionStep, bioModel.getModel().getSpeciesContext(safeId)));
				}
			}
		}
	}
	
	private void createReactionStepsFromTableRow(BioModel bioModel, Conversion conversion,
			double stoich, String id, String location, boolean addSubunits) throws Exception
	{
		// use user defined id as the name of the reaction name
		if (bioModel==null) {
			return;
		}
		Set<Process> processes = BioPAXUtil.getAllProcesses(bioModel.getPathwayModel(), conversion);
		for(Process process : processes) {
			String name = getSafetyName(process.getName() + "_" + location);
			if(bioModel.getModel().getReactionStep(name) == null) {
				// create a new reactionStep object
				ReactionStep simpleReactionStep = bioModel.getModel().createSimpleReaction(bioModel.getModel().getStructure(location));
				simpleReactionStep.setName(name);
				RelationshipObject newRelationship = new RelationshipObject(simpleReactionStep, conversion);
				bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
				createReactionStepFromPathway( bioModel, process, simpleReactionStep, newRelationship, addSubunits);
			} else {
				bioModel.getModel().getReactionStep(name).setStructure(bioModel.getModel().getStructure(location));
			// add missing parts for the existing reactionStep
				RelationshipObject newRelationship = new RelationshipObject(bioModel.getModel().getReactionStep(name), conversion);
				bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
				createReactionStepFromPathway( bioModel, process, bioModel.getModel().getReactionStep(name), newRelationship, addSubunits);
			}			
		}
	}
		
	/* for SpeciesContext object: 
	 * 1. annotate the selected vcell object using linked pathway object
	 * 2. map all pathway neighbors to the selected vcell object -- not done yet!
	 * for duplicated SpeciesContext: we add them in without name changing.
	*/
	private void createSpeciesContextFromPathway(BioModel bioModel, SpeciesContext bioModelEntityObject, RelationshipObject relationshipObject) throws Exception
	{
		// annotate the selected vcell object using linked pathway object 
		if(((PhysicalEntity)relationshipObject.getBioPaxObject()).getName().size() == 0) {
			(bioModelEntityObject).setName(
					getSafetyName(((PhysicalEntity)relationshipObject.getBioPaxObject()).getID()));
		} else {
			(bioModelEntityObject).setName(
					getSafetyName(((PhysicalEntity)relationshipObject.getBioPaxObject()).getName().get(0)));
		}
	}
	
	/*
	 * for reaction:
	 * 1. annotate the selected vcell object using linked pathway conversion
	 * 2. add non-existing speciesContexts from linked pathway conversion
	 * 3. add links between relative vcell objects and pathway objects
	 * Questions:
	 * - how to deal with the case that the reaction is existing in the model?
	 * 		+ add it in no matter what? 
	 * 				(this is the version we have now: 
	 * 					add the duplicated reactions in without name changing, 
	 * 				 	all duplicated reactions share the same participant objects)
	 *      + just modify the existing one?
	 */
	private void createReactionStepFromPathway(BioModel bioModel, Process process, 
			ReactionStep reactionStep, RelationshipObject relationshipObject, boolean addSubunits) throws Exception
	{
		// annotate the selected vcell object using linked pathway object
		// add non-existing speciesContexts from linked pathway conversion
		ReactionParticipant[] rpArray = parseReaction(reactionStep, bioModel, relationshipObject);
		// create a hashtable for interaction Participants
		Hashtable<String, BioPaxObject> participantTable = new Hashtable<String, BioPaxObject>();
		for(BioPaxObject bpObject: ((Conversion)relationshipObject.getBioPaxObject()).getLeft()) {
			if(((PhysicalEntity)bpObject).getName().size() == 0) {
				participantTable.put(getSafetyName(((PhysicalEntity)bpObject).getID()), bpObject);
			} else {
				participantTable.put(getSafetyName(((PhysicalEntity)bpObject).getName().get(0)), bpObject);
			}
		}
		for(BioPaxObject bpObject: ((Conversion)relationshipObject.getBioPaxObject()).getRight()) {
			if(((PhysicalEntity)bpObject).getName().size() == 0) {
				participantTable.put(getSafetyName(((PhysicalEntity)bpObject).getID()), bpObject);
			} else {
				participantTable.put(getSafetyName(((PhysicalEntity)bpObject).getName().get(0)), bpObject);
			}
		}
		
		for (ReactionParticipant rp : rpArray) {
			SpeciesContext speciesContext = rp.getSpeciesContext();
			if (bioModel.getModel().getSpeciesContext(speciesContext.getName()) == null) {
			// if the speciesContext is not existed, then add it to the bioModel and link it to the corresponding pathway object 
				if(bioModel.getModel().getSpecies(speciesContext.getName()) == null) {
					bioModel.getModel().addSpecies(speciesContext.getSpecies());
				}
				bioModel.getModel().addSpeciesContext(speciesContext);
				RelationshipObject newSpeciesContext = new RelationshipObject(speciesContext, participantTable.get(speciesContext.getName()));
				bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
			} else {
			// if it is in the bioModel, then check whether it links to pathway object or not
				HashSet<RelationshipObject> linkedReObjects = 
					bioModel.getRelationshipModel().getRelationshipObjects(bioModel.getModel().getSpeciesContext(speciesContext.getName()));
				if(linkedReObjects != null) {
					boolean isLinked = false;
					for(RelationshipObject reObject: linkedReObjects) {
						if(reObject.getBioPaxObject() == participantTable.get(speciesContext.getName())) {
							isLinked = true;
							break;
						}
					}
					if(!isLinked) {
						RelationshipObject newSpeciesContext = new RelationshipObject(speciesContext, participantTable.get(speciesContext.getName()));
						bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
					}
				} else {
					RelationshipObject newSpeciesContext = new RelationshipObject(speciesContext, participantTable.get(speciesContext.getName()));
					bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
				}
			}
		}
		(reactionStep).setReactionParticipants(rpArray);
		// add Control to the reaction
		if(process.getControl() != null) {
			for(InteractionParticipant pe : process.getControl().getParticipants()) {
				SpeciesContext newSpeciescontext = createSpeciesContextFromBioPaxObject( bioModel, pe.getPhysicalEntity(), addSubunits);
				(reactionStep).addReactionParticipant(new Catalyst(null,reactionStep, newSpeciescontext));
			}
		}
		addKinetics(reactionStep, process);
	}
	
	private ReactionParticipant[] parseReaction(ReactionStep reactionStep, BioModel bioModel, RelationshipObject relationshipObject  ) 
			throws ExpressionException, PropertyVetoException {
		if (reactionStep == null || bioModel == null || bioModel.getRelationshipModel() == null) {
			return null;
		}
		// create the reaction equation string
		String leftHand = getParticipantsString(((Conversion)relationshipObject.getBioPaxObject()).getLeft());
		String rightHand = getParticipantsString(((Conversion)relationshipObject.getBioPaxObject()).getRight());
		StringTokenizer st = new StringTokenizer(leftHand, "+");
		HashMap<String, SpeciesContext> speciesContextMap = new HashMap<String, SpeciesContext>();
		ArrayList<ReactionParticipant> rplist = new ArrayList<ReactionParticipant>();
		// create and add reaction participants to list for left-hand side of equation
		Model model = bioModel.getModel();
		Structure structure = reactionStep.getStructure();
		while (st.hasMoreElements()) {
			String nextToken = st.nextToken().trim();
			if (nextToken.length() == 0) {
				continue;
			}
			int stoichiIndex = 0;
			while (true) {
				if (Character.isDigit(nextToken.charAt(stoichiIndex))) {
					stoichiIndex ++;
				} else {
					break;
				}
			}
			int stoichi = 1;
			String tmp = nextToken.substring(0, stoichiIndex);
			if (tmp.length() > 0) {
				stoichi = Integer.parseInt(tmp);
			}
			String var = nextToken.substring(stoichiIndex).trim();
			// get speciesContext object based on its name
			// if the speciesContext is not existed, create a new one
			SpeciesContext sc = model.getSpeciesContext(var);
			if (sc == null) {
				sc = speciesContextMap.get(var);
				if (sc == null) {
					// get species object based on its name
					// if the species is not existed, create a new one
					Species species = model.getSpecies(var);
					if (species == null) {
						species = new Species(var, null);
					}
					sc = new SpeciesContext(species, structure);
					sc.setName(var);
					speciesContextMap.put(var, sc);
				}
			}
			// add the existed speciesContext objects or new speciesContext objects to reaction participant list
			if (reactionStep instanceof SimpleReaction || reactionStep instanceof FluxReaction) {
				rplist.add(new Reactant(null,(SimpleReaction) reactionStep, sc, stoichi));
			}
		}
		// create and add reaction participants to list for right-hand side of equation
		st = new StringTokenizer(rightHand, "+");
		while (st.hasMoreElements()) {
			String nextToken = st.nextToken().trim();
			if (nextToken.length() == 0) {
				continue;
			}
			int stoichiIndex = 0;
			while (true) {
				if (Character.isDigit(nextToken.charAt(stoichiIndex))) {
					stoichiIndex ++;
				} else {
					break;
				}
			}
			int stoichi = 1;
			String tmp = nextToken.substring(0, stoichiIndex);
			if (tmp.length() > 0) {
				stoichi = Integer.parseInt(tmp);
			}
			String var = nextToken.substring(stoichiIndex);
			SpeciesContext sc = model.getSpeciesContext(var);
			if (sc == null) {
				sc = speciesContextMap.get(var);
				if (sc == null) {
					Species species = model.getSpecies(var);
					if (species == null) {
						species = new Species(var, null);
					}
					sc = new SpeciesContext(species, structure);
					sc.setName(var);
					speciesContextMap.put(var, sc);
				}
			}
			if (reactionStep instanceof SimpleReaction || reactionStep instanceof FluxReaction) {
				rplist.add(new Product(null,(SimpleReaction) reactionStep, sc, stoichi));
			}
		}
		return rplist.toArray(new ReactionParticipant[0]);
	}

	// create the reaction equation based on the pathway conversion information 
	private static String getParticipantsString(List<PhysicalEntity> physicalEntities) {
		if (physicalEntities == null){
			return null;
		}
		String participantString = "";
		for(PhysicalEntity physicalEntity : physicalEntities) {
			if(physicalEntity.getName().size() == 0) {
				participantString += getSafetyName(physicalEntity.getID()) + "+";
			} else {
				participantString += getSafetyName(physicalEntity.getName().get(0)) + "+";
			}
		}
		// remove the last "+" from the string
		if(participantString.length()>0) {
			participantString = participantString.substring(0, participantString.length()-1);
		}
		return participantString;
	}
	//convert the name of biopax object to safety vcell object name
	public static String getSafetyName(String oldValue) {
		return TokenMangler.fixTokenStrict(oldValue, 60);
	} 
	
	private void addKinetics(ReactionStep reactionStep, Process process) {
		try {
			SBPAXKineticsExtractor.extractKineticsExactMatch(reactionStep, process);
		} catch (ExpressionException e) {
			e.printStackTrace();
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}		
//		SBPAXKineticsExtractor.extractKineticsInferredMatch(reactionStep, Collections.<SBEntity>unmodifiableSet(process.getInteractions()));		
	}
	
}

package org.vcell.relationship;

import java.awt.Component;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.BiochemicalReaction;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.util.TokenMangler;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.BioModelEntityObject;
import cbit.vcell.model.Flux;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SimpleReaction;
import cbit.vcell.model.Species;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.parser.ExpressionException;

import com.ibm.icu.util.StringTokenizer;

public class PathwayMapping {
	
	public void createBioModelEntitiesFromBioPaxObjects(BioModel bioModel, Object[] selectedObjects) throws Exception
	{
		for(int i = 0; i < selectedObjects.length; i++){
			BioPaxObject bioPaxObject = (BioPaxObject)selectedObjects[i];
//			try
//			{
				if(bioPaxObject instanceof PhysicalEntity){
					createSpeciesContextFromBioPaxObject(bioModel, (PhysicalEntity)bioPaxObject);
				}else if(bioPaxObject instanceof BiochemicalReaction){
					createReactionStepFromBioPaxObject(bioModel, (BiochemicalReaction)bioPaxObject);
				}
//			}catch(Exception ex)
//			{
//				throw new Exception(ex);
//			}
		}
	}
	
	private void createSpeciesContextFromBioPaxObject(BioModel bioModel, PhysicalEntity bioPaxObject) throws Exception
	{
		String name = getSafetyName(bioPaxObject.getName().get(0));
		if(bioModel.getModel().getSpeciesContext(name) == null){
		// create the new speciesContex Object, and link it to the corresponding pathway object
//			try{
				SpeciesContext freeSpeciesContext = null;
				if(bioModel.getModel().getSpecies(name) == null){
					freeSpeciesContext = bioModel.getModel().createSpeciesContext(bioModel.getModel().getStructures()[0]);
				}else{
					 freeSpeciesContext = new SpeciesContext(bioModel.getModel().getSpecies(name), bioModel.getModel().getStructures()[0]);
				}
				freeSpeciesContext.setName(name);
				RelationshipObject newRelationship = new RelationshipObject(freeSpeciesContext, bioPaxObject);
				bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
//			}catch (Exception e){
//				throw new Exception(e);
//			}
		
		}else{
			// if it is in the bioModel, then check whether it links to pathway object or not
				HashSet<RelationshipObject> linkedReObjects = 
					bioModel.getRelationshipModel().getRelationshipObjects(bioModel.getModel().getSpeciesContext(name));
				if(linkedReObjects != null){
					boolean flag = true;
					for(RelationshipObject reObject: linkedReObjects){
						if(reObject.getBioPaxObject() == bioPaxObject){
							flag = false;
							break;
						}
					}
					if(flag){
						RelationshipObject newSpeciesContext = new RelationshipObject(
								bioModel.getModel().getSpeciesContext(name), bioPaxObject);
						bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
					}
				}else{
					RelationshipObject newSpeciesContext = new RelationshipObject(
							bioModel.getModel().getSpeciesContext(name), bioPaxObject);
					bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
				}
			}
	}
	
	private void createReactionStepFromBioPaxObject(BioModel bioModel, BiochemicalReaction bioPaxObject) throws Exception
	{
		String name = getSafetyName(bioPaxObject.getName().get(0));
//		try{
			if(bioModel.getModel().getReactionStep(name) == null){
				// create a new reactionStep object
				ReactionStep simpleReactionStep = bioModel.getModel().createSimpleReaction(bioModel.getModel().getStructures()[0]);
				simpleReactionStep.setName(name);
				RelationshipObject newRelationship = new RelationshipObject(simpleReactionStep, bioPaxObject);
				bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
				createReactionStepFromPathway( bioModel, simpleReactionStep, newRelationship);
			}else{
			// add missing parts for the existing reactionStep
				RelationshipObject newRelationship = new RelationshipObject(bioModel.getModel().getReactionStep(name), bioPaxObject);
				bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
				createReactionStepFromPathway( bioModel, bioModel.getModel().getReactionStep(name), newRelationship);
			}
//		}catch (Exception e){
//			throw new Exception(e);
//		}
	}
		
	public void createBioModelEntityFromPathway(BioModel bioModel, BioModelEntityObject bioModelEntityObject, RelationshipObject relationshipObject)throws Exception 
	{
		if(bioModel == null){
			return;
		}
		if(bioModel.getRelationshipModel() == null){
			return;
		}
		// convert biopax object to vcell object		
		if(bioModelEntityObject instanceof SpeciesContext ){
			// annotate the selected vcell object using linked pathway object 
			createSpeciesContextFromPathway( bioModel, (SpeciesContext) bioModelEntityObject, relationshipObject);
		} else if(bioModelEntityObject instanceof ReactionStep ){
//			try{
				((ReactionStep)bioModelEntityObject).setName(
						getSafetyName(((BiochemicalReaction)relationshipObject.getBioPaxObject()).getName().get(0)));
//			} catch (Exception e){
//				e.printStackTrace();
//				DialogUtils.showErrorDialog(component, e.getMessage(), e);
//			}
			createReactionStepFromPathway( bioModel, (ReactionStep) bioModelEntityObject, relationshipObject);
		}
	}
	
	/* for SpeciesContext object: 
	 * 1. annotate the selected vcell object using linked pathway object
	 * 2. map all pathway neighbors to the selected vcell object -- not done yet!
	 * for duplicated SpeciesContext: we add them in without name changing.
	*/
	private void createSpeciesContextFromPathway(BioModel bioModel, SpeciesContext bioModelEntityObject, RelationshipObject relationshipObject) throws Exception
	{
//		try{
			// annotate the selected vcell object using linked pathway object 
			((SpeciesContext)bioModelEntityObject).setName(
					getSafetyName(((PhysicalEntity)relationshipObject.getBioPaxObject()).getName().get(0)));
//		}catch (Exception e){
//			throw new Exception(e);
//		}
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
	private void createReactionStepFromPathway(BioModel bioModel, ReactionStep bioModelEntityObject, RelationshipObject relationshipObject) throws Exception
	{
//		try{
			// annotate the selected vcell object using linked pathway object
			// add non-existing speciesContexts from linked pathway conversion
			ReactionParticipant[] rpArray = parseReaction((ReactionStep)bioModelEntityObject, bioModel, relationshipObject);
			// create a hashtable for interaction Participants
			Hashtable<String, BioPaxObject> participantTable = new Hashtable<String, BioPaxObject>();
			for(BioPaxObject bpObject: ((BiochemicalReaction)relationshipObject.getBioPaxObject()).getLeft()){
				participantTable.put(getSafetyName(((PhysicalEntity)bpObject).getName().get(0)), bpObject);
			}
			for(BioPaxObject bpObject: ((BiochemicalReaction)relationshipObject.getBioPaxObject()).getRight()){
				participantTable.put(getSafetyName(((PhysicalEntity)bpObject).getName().get(0)), bpObject);
			}
			
			for (ReactionParticipant rp : rpArray) {
				SpeciesContext speciesContext = rp.getSpeciesContext();
				if (bioModel.getModel().getSpeciesContext(speciesContext.getName()) == null) {
				// if the speciesContext is not existed, then add it to the bioModel and link it to the corresponding pathway object 
					if(bioModel.getModel().getSpecies(speciesContext.getName()) == null){
						bioModel.getModel().addSpecies(speciesContext.getSpecies());
					}
					bioModel.getModel().addSpeciesContext(speciesContext);
					RelationshipObject newSpeciesContext = new RelationshipObject(speciesContext, participantTable.get(speciesContext.getName()));
					bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
				}else{
				// if it is in the bioModel, then check whether it links to pathway object or not
					HashSet<RelationshipObject> linkedReObjects = 
						bioModel.getRelationshipModel().getRelationshipObjects(bioModel.getModel().getSpeciesContext(speciesContext.getName()));
					if(linkedReObjects != null){
						boolean isLinked = false;
						for(RelationshipObject reObject: linkedReObjects){
							if(reObject.getBioPaxObject() == participantTable.get(speciesContext.getName())){
								isLinked = true;
								break;
							}
						}
						if(!isLinked){
							RelationshipObject newSpeciesContext = new RelationshipObject(speciesContext, participantTable.get(speciesContext.getName()));
							bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
						}
					}else{
						RelationshipObject newSpeciesContext = new RelationshipObject(speciesContext, participantTable.get(speciesContext.getName()));
						bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
					}
				}
			}
			((ReactionStep)bioModelEntityObject).setReactionParticipants(rpArray);	
//		} catch (Exception e){
//			throw new Exception(e);
//		}
	}

	private ReactionParticipant[] parseReaction(ReactionStep reactionStep, BioModel bioModel, RelationshipObject relationshipObject  ) 
			throws ExpressionException, PropertyVetoException {
		if (reactionStep == null || bioModel == null || bioModel.getRelationshipModel() == null) {
			return null;
		}
		// create the reaction equation string
		String leftHand = getParticipantsString(((BiochemicalReaction)relationshipObject.getBioPaxObject()).getLeft());
		String rightHand = getParticipantsString(((BiochemicalReaction)relationshipObject.getBioPaxObject()).getRight());
		StringTokenizer st = new StringTokenizer(leftHand, "+");
		ArrayList<ReactionParticipant> rplist = new ArrayList<ReactionParticipant>();
		// create and add reaction participants to list for left-hand side of equation
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
			SpeciesContext sc = bioModel.getModel().getSpeciesContext(var);
			if (sc == null) {
				// get species object based on its name
				// if the species is not existed, create a new one
				Species species = bioModel.getModel().getSpecies(var);
				if (species == null) {
					species = new Species(var, null);
				}
				sc = new SpeciesContext(species, reactionStep.getStructure());
				sc.setName(var);
			}
			// check whether this speciesContext object has been added to the list
			boolean isAdded = false;
			for(ReactionParticipant rp : rplist){
				if(rp.getName().equals(var))
					isAdded = true;
			}
			// add the existed speciesContext objects or new speciesContext objects to reaction participant list
			if (!isAdded && reactionStep instanceof SimpleReaction) {
				rplist.add(new Reactant(null,(SimpleReaction) reactionStep, sc, stoichi));
			} else if (!isAdded && reactionStep instanceof FluxReaction) {
				rplist.add(new Flux(null, (FluxReaction) reactionStep, sc));
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
			SpeciesContext sc = bioModel.getModel().getSpeciesContext(var);
			if (sc == null) {
				Species species = bioModel.getModel().getSpecies(var);
				if (species == null) {
					species = new Species(var, null);
				}
				sc = new SpeciesContext(species, reactionStep.getStructure());
				sc.setName(var);
			}
			// check whether this speciesContext object has been added to the list
			boolean isAdded = false;
			for(ReactionParticipant rp : rplist){
				if(rp.getName().equals(var)){
					isAdded = true;
				}
			}
			if (!isAdded && reactionStep instanceof SimpleReaction) {
				rplist.add(new Product(null,(SimpleReaction) reactionStep, sc, stoichi));
			} else if (!isAdded && reactionStep instanceof FluxReaction) {
				rplist.add(new Flux(null, (FluxReaction) reactionStep, sc));
			}
		}
		return rplist.toArray(new ReactionParticipant[0]);
	}

	// create the reaction equation based on the pathway conversion information 
	private static String getParticipantsString(ArrayList <PhysicalEntity> physicalEntities) {
		if (physicalEntities == null){
			return null;
		}
		String participantString = "";
		for(PhysicalEntity physicalEntity : physicalEntities){
			participantString += getSafetyName(physicalEntity.getName().get(0)) + "+";
		}
		// remove the last "+" from the string
		if(participantString.length()>0){
			participantString = participantString.substring(0, participantString.length()-1);
		}
		return participantString;
	}
	//convert the name of biopax object to safety vcell object name
	private static String getSafetyName(String oldValue){
		return TokenMangler.fixTokenStrict(oldValue, 60);
	} 
	
}

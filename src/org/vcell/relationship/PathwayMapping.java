package org.vcell.relationship;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.vcell.pathway.BioPaxObject;
import org.vcell.pathway.BiochemicalReaction;
import org.vcell.pathway.Catalysis;
import org.vcell.pathway.Control;
import org.vcell.pathway.Conversion;
import org.vcell.pathway.InteractionParticipant;
import org.vcell.pathway.PhysicalEntity;
import org.vcell.util.TokenMangler;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.desktop.biomodel.ConversionTableRow;
import cbit.vcell.model.BioModelEntityObject;
import cbit.vcell.model.Catalyst;
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
	
	public void createBioModelEntitiesFromBioPaxObjects(BioModel bioModel, ArrayList<ConversionTableRow> conversionTableRows) throws Exception
	{
		for(ConversionTableRow conversionTableRow : conversionTableRows){
			if(conversionTableRow.getBioPaxObject() instanceof PhysicalEntity){
				createSpeciesContextFromTableRow(bioModel, (PhysicalEntity)conversionTableRow.getBioPaxObject(), 
						conversionTableRow.stoich(), conversionTableRow.id(), conversionTableRow.location());
			}else if(conversionTableRow.getBioPaxObject() instanceof BiochemicalReaction){
				createReactionStepFromTableRow(bioModel, (BiochemicalReaction)conversionTableRow.getBioPaxObject(),
						conversionTableRow.stoich(), conversionTableRow.id(), conversionTableRow.location(), conversionTableRows);
			}
		}
	}
	
	public void createBioModelEntitiesFromBioPaxObjects(BioModel bioModel, Object[] selectedObjects) throws Exception
	{
		
		for(int i = 0; i < selectedObjects.length; i++){
			if(selectedObjects[i] instanceof BioPaxObject){
				BioPaxObject bioPaxObject = (BioPaxObject)selectedObjects[i];
				if(bioPaxObject instanceof PhysicalEntity){
					createSpeciesContextFromBioPaxObject(bioModel, (PhysicalEntity)bioPaxObject);
				}else if(bioPaxObject instanceof BiochemicalReaction){
					createReactionStepFromBioPaxObject(bioModel, (BiochemicalReaction)bioPaxObject);
				}
			}else if(selectedObjects[i] instanceof ConversionTableRow){
				ConversionTableRow conversionTableRow = (ConversionTableRow)selectedObjects[i];
				if(conversionTableRow.getBioPaxObject() instanceof PhysicalEntity){
					createSpeciesContextFromTableRow(bioModel, (PhysicalEntity)conversionTableRow.getBioPaxObject(), 
							conversionTableRow.stoich(), conversionTableRow.id(), conversionTableRow.location());
				}else if(conversionTableRow.getBioPaxObject() instanceof BiochemicalReaction){
					createReactionStepFromTableRow(bioModel, (BiochemicalReaction)conversionTableRow.getBioPaxObject(),
							conversionTableRow.stoich(), conversionTableRow.id(), conversionTableRow.location());
				}
			}
		}
	}
	
	private SpeciesContext createSpeciesContextFromBioPaxObject(BioModel bioModel, PhysicalEntity bioPaxObject) throws Exception
	{
		String name = getSafetyName(bioPaxObject.getName().get(0));
		SpeciesContext freeSpeciesContext = bioModel.getModel().getSpeciesContext(name);
		if(freeSpeciesContext == null){
		// create the new speciesContex Object, and link it to the corresponding pathway object
			if(bioModel.getModel().getSpecies(name) == null){
				freeSpeciesContext = bioModel.getModel().createSpeciesContext(bioModel.getModel().getStructures()[0]);
			}else{
				 freeSpeciesContext = new SpeciesContext(bioModel.getModel().getSpecies(name), bioModel.getModel().getStructures()[0]);
			}
			freeSpeciesContext.setName(name);
			RelationshipObject newRelationship = new RelationshipObject(freeSpeciesContext, bioPaxObject);
			bioModel.getRelationshipModel().addRelationshipObject(newRelationship);		
		}else{
			// if it is in the bioModel, then check whether it links to pathway object or not
			HashSet<RelationshipObject> linkedReObjects = 
				bioModel.getRelationshipModel().getRelationshipObjects(freeSpeciesContext);
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
							freeSpeciesContext, bioPaxObject);
					bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
				}
			}else{
				RelationshipObject newSpeciesContext = new RelationshipObject(
						freeSpeciesContext, bioPaxObject);
				bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
			}
		}
		return freeSpeciesContext;
	}
	
	private SpeciesContext createSpeciesContextFromTableRow(BioModel bioModel, PhysicalEntity bioPaxObject, 
			double stoich, String id, String location) throws Exception
	{
		// use user defined id as the name of the speciesContext
		String safeId = getSafetyName(id);
		String name = getSafetyName(bioPaxObject.getName().get(0));
		SpeciesContext freeSpeciesContext = bioModel.getModel().getSpeciesContext(safeId);
		if(freeSpeciesContext == null){
		// create the new speciesContex Object, and link it to the corresponding pathway object
			if(bioModel.getModel().getSpecies(name) == null){
				freeSpeciesContext = bioModel.getModel().createSpeciesContext(bioModel.getModel().getStructure(location));
			}else {
				freeSpeciesContext = new SpeciesContext(bioModel.getModel().getSpecies(name), bioModel.getModel().getStructure(location));
			}
			freeSpeciesContext.setName(safeId);
			RelationshipObject newRelationship = new RelationshipObject(freeSpeciesContext, bioPaxObject);
			bioModel.getRelationshipModel().addRelationshipObject(newRelationship);		
		}else{
			// if it is in the bioModel, then check whether it links to pathway object or not
			HashSet<RelationshipObject> linkedReObjects = 
				bioModel.getRelationshipModel().getRelationshipObjects(freeSpeciesContext);
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
							freeSpeciesContext, bioPaxObject);
					bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
				}
			}else{
				RelationshipObject newSpeciesContext = new RelationshipObject(
						freeSpeciesContext, bioPaxObject);
				bioModel.getRelationshipModel().addRelationshipObject(newSpeciesContext);
			}
		}
		return freeSpeciesContext;
	}
	
	private void createReactionStepFromBioPaxObject(BioModel bioModel, BiochemicalReaction bioPaxObject) throws Exception
	{
		String name = getSafetyName(bioPaxObject.getName().get(0));
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
	}
	
	private void createReactionStepFromTableRow(BioModel bioModel, BiochemicalReaction bioPaxObject,
			double stoich, String id, String location, ArrayList<ConversionTableRow> conversionTableRows) throws Exception
	{
		// use user defined id as the name of the reaction name
		String safeId = getSafetyName(id);
//		String name = getSafetyName(bioPaxObject.getName().get(0));
		// get participants from table rows
		ArrayList<ConversionTableRow> participants = new ArrayList<ConversionTableRow>();
		for(ConversionTableRow ctr : conversionTableRows){
			if(ctr.interactionName().equals(bioPaxObject.getName().get(0))){
				participants.add(ctr);
			}
		}
		// create reaction object
		if(bioModel.getModel().getReactionStep(safeId) == null){
			// create a new reactionStep object
			ReactionStep simpleReactionStep = bioModel.getModel().createSimpleReaction(bioModel.getModel().getStructure(location));
			simpleReactionStep.setName(safeId);
			RelationshipObject newRelationship = new RelationshipObject(simpleReactionStep, bioPaxObject);
			bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
			createReactionStep( bioModel, simpleReactionStep, newRelationship, participants);
		}else{
//			bioModel.getModel().getReactionStep(safeId).setStructure(bioModel.getModel().getStructure(location));
		// add missing parts for the existing reactionStep
			RelationshipObject newRelationship = new RelationshipObject(bioModel.getModel().getReactionStep(safeId), bioPaxObject);
			bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
			createReactionStep( bioModel, bioModel.getModel().getReactionStep(safeId), newRelationship, participants);
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
	private void createReactionStep(BioModel bioModel, ReactionStep reactionStep, RelationshipObject relationshipObject, 
			ArrayList<ConversionTableRow> participants) throws Exception
	{
		if (reactionStep == null || bioModel == null || bioModel.getRelationshipModel() == null || participants.size() < 1) {
			return;
		}
		ArrayList<ReactionParticipant> rplist = new ArrayList<ReactionParticipant>();
		// create and add reaction participants to list 
		for(ConversionTableRow ctr : participants){
			if(ctr.getBioPaxObject() instanceof Conversion) continue;
			int stoich = ctr.stoich().intValue();
			String safeId = getSafetyName(ctr.id());

			// get speciesContext object based on its name
			// if the speciesContext is not existed, create a new one
			createSpeciesContextFromTableRow(bioModel, (PhysicalEntity)ctr.getBioPaxObject(), 
					ctr.stoich(), ctr.id(), ctr.location());
			
			// add the existed speciesContext objects or new speciesContext objects to reaction participant list
			if(ctr.participantType().equals("Reactant")){
				if (reactionStep instanceof SimpleReaction) {
					rplist.add(new Reactant(null,(SimpleReaction) reactionStep, bioModel.getModel().getSpeciesContext(safeId), stoich));
				} else if (reactionStep instanceof FluxReaction) {
					rplist.add(new Flux(null, (FluxReaction) reactionStep, bioModel.getModel().getSpeciesContext(safeId)));
				}else{
				}
			}else if(ctr.participantType().equals("Product")){
				if (reactionStep instanceof SimpleReaction) {
					rplist.add(new Product(null,(SimpleReaction) reactionStep, bioModel.getModel().getSpeciesContext(safeId), stoich));
				} else if (reactionStep instanceof FluxReaction) {
					rplist.add(new Flux(null, (FluxReaction) reactionStep, bioModel.getModel().getSpeciesContext(safeId)));
				}
			}
		}
		ReactionParticipant[] rpArray = rplist.toArray(new ReactionParticipant[0]);
		reactionStep.setReactionParticipants(rpArray);
		
		// add Catalysts to the reaction
		for(ConversionTableRow ctr : participants){
			if(ctr.participantType().equals("Catalyst")){
				String safeId = getSafetyName(ctr.id());
				/* 
				 * using addCatalyst() to create catalyst in reaction: 
				 * this function cannot allow an object to be catalyst and (reactant/product) in the same reaction
				 */
				//	((ReactionStep)bioModelEntityObject).addCatalyst(bioModel.getModel().getSpeciesContext(safeId));
				
				/* However, in pathway interaction object, an physicalEntity can be catalyst and (reactant/product) in the same reaction
				 * So we just call create catalyst for the reaction no matter what rolls the object is playing in the reaction
				 * Switch back to the addCatalyst() function when it is necessary, but exceptions make be reported for some reactions
				 */
				reactionStep.addReactionParticipant(
						new Catalyst(null,reactionStep, bioModel.getModel().getSpeciesContext(safeId)));
			}
		}
	}
	
	private void createReactionStepFromTableRow(BioModel bioModel, BiochemicalReaction bioPaxObject,
			double stoich, String id, String location) throws Exception
	{
		// use user defined id as the name of the reaction name
		String name = getSafetyName(id);
		if(bioModel.getModel().getReactionStep(name) == null){
			// create a new reactionStep object
			ReactionStep simpleReactionStep = bioModel.getModel().createSimpleReaction(bioModel.getModel().getStructure(location));
			simpleReactionStep.setName(name);
			RelationshipObject newRelationship = new RelationshipObject(simpleReactionStep, bioPaxObject);
			bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
			createReactionStepFromPathway( bioModel, simpleReactionStep, newRelationship);
		}else{
			bioModel.getModel().getReactionStep(name).setStructure(bioModel.getModel().getStructure(location));
		// add missing parts for the existing reactionStep
			RelationshipObject newRelationship = new RelationshipObject(bioModel.getModel().getReactionStep(name), bioPaxObject);
			bioModel.getRelationshipModel().addRelationshipObject(newRelationship);
			createReactionStepFromPathway( bioModel, bioModel.getModel().getReactionStep(name), newRelationship);
		}
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
			((ReactionStep)bioModelEntityObject).setName(
					getSafetyName(((BiochemicalReaction)relationshipObject.getBioPaxObject()).getName().get(0)));
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
		// annotate the selected vcell object using linked pathway object 
		((SpeciesContext)bioModelEntityObject).setName(
				getSafetyName(((PhysicalEntity)relationshipObject.getBioPaxObject()).getName().get(0)));
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
		// add Catalysts to the reaction
		for(Catalysis catalyst : searchCatalyst(bioModel, relationshipObject)){
			for(InteractionParticipant pe : catalyst.getParticipants()){
				SpeciesContext newSpeciescontext = createSpeciesContextFromBioPaxObject( bioModel, pe.getPhysicalEntity());
				/* 
				 * using addCatalyst() to create catalyst in reaction: 
				 * this function cannot allow an object to be catalyst and (reactant/product) in the same reaction
				 */
				//	((ReactionStep)bioModelEntityObject).addCatalyst(newSpeciescontext);
				
				/* However, in pathway interaction object, an physicalEntity can be catalyst and (reactant/product) in the same reaction
				 * So we just call create catalyst for the reaction no matter what rolls the object is playing in the reaction
				 * Switch back to the addCatalyst() function when it is necessary, but exceptions make be reported for some reactions
				 */
				((ReactionStep)bioModelEntityObject).addReactionParticipant(new Catalyst(null,bioModelEntityObject, newSpeciescontext));
			}
			
		}
	}
	
	private ArrayList<Catalysis> searchCatalyst(BioModel bioModel, RelationshipObject relationshipObject){
		ArrayList<Catalysis> catalystList = new ArrayList<Catalysis>();
		for(BioPaxObject bpObject : bioModel.getPathwayModel().getBiopaxObjects()){
			if(bpObject instanceof Catalysis){
				if(((Catalysis)bpObject).getControlledInteraction() == relationshipObject.getBioPaxObject()){
					catalystList.add((Catalysis)bpObject);
				}
			}
		}
		return catalystList;
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
		HashMap<String, SpeciesContext> speciesContextMap = new HashMap<String, SpeciesContext>();
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
				sc = speciesContextMap.get(var);
				if (sc == null) {
					// get species object based on its name
					// if the species is not existed, create a new one
					Species species = bioModel.getModel().getSpecies(var);
					if (species == null) {
						species = new Species(var, null);
					}
					sc = new SpeciesContext(species, reactionStep.getStructure());
					sc.setName(var);
					speciesContextMap.put(var, sc);
				}
			}
			// add the existed speciesContext objects or new speciesContext objects to reaction participant list
			if (reactionStep instanceof SimpleReaction) {
				rplist.add(new Reactant(null,(SimpleReaction) reactionStep, sc, stoichi));
			} else if (reactionStep instanceof FluxReaction) {
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
				sc = speciesContextMap.get(var);
				if (sc == null) {
					Species species = bioModel.getModel().getSpecies(var);
					if (species == null) {
						species = new Species(var, null);
					}
					sc = new SpeciesContext(species, reactionStep.getStructure());
					sc.setName(var);
					speciesContextMap.put(var, sc);
				}
			}
			if (reactionStep instanceof SimpleReaction) {
				rplist.add(new Product(null,(SimpleReaction) reactionStep, sc, stoichi));
			} else if (reactionStep instanceof FluxReaction) {
				rplist.add(new Flux(null, (FluxReaction) reactionStep, sc));
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

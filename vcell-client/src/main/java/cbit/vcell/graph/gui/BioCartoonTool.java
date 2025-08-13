/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.graph.gui;

import cbit.gui.graph.gui.GraphPane;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.model.*;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Kinetics.KineticsProxyParameter;
import cbit.vcell.model.Membrane.MembraneVoltage;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.model.Model.StructureTopology;
import cbit.vcell.model.Structure.StructureSize;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.xml.XmlHelper;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.*;
import org.vcell.util.Issue.IssueCategory;
import org.vcell.util.IssueContext.ContextType;
import org.vcell.util.gui.DialogUtils;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

public abstract class BioCartoonTool extends cbit.gui.graph.gui.CartoonTool {
	private DocumentManager documentManager = null;

	/**
	 * This method was created by a SmartGuide.
	 * @param canvas cbit.vcell.graph.CartoonCanvas
	 */
	public BioCartoonTool() {
		super();
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (5/13/2003 8:26:55 PM)
	 * @return cbit.vcell.clientdb.DocumentManager
	 */
	public cbit.vcell.clientdb.DocumentManager getDocumentManager() {
		return documentManager;
	}

	/**
	 * printIssues : using the issue list passed as argument, gethers them in a string buffer and uses PopupGenerator.showWarning dialog to display 
	 * the issues to user. 
	 * @param issueVector : vector of 'Issue's to be printed
	 * @param guiRequestComponent : the parent component required to be passed to showWarningDialog
	 * @return
	 */
	public static boolean printIssues(Vector<Issue> issueVector, Component guiRequestComponent) {
		// now print out the issue List as a warning popup
		if (issueVector.size() > 0) {
			boolean hasErrors = false;
			StringBuffer messageBuffer = new StringBuffer("Issues encountered during pasting selected reactions:\n\n");
			for (int j = 0; j < issueVector.size(); j++) {
				Issue issue = issueVector.get(j);
				if (issue.getSeverity()==Issue.Severity.ERROR || issue.getSeverity()==Issue.Severity.WARNING) {
					if(issue.getSeverity()==Issue.Severity.ERROR) {
						hasErrors = true;
					}
					messageBuffer.append(j+1 + ". " + issue.getCategory()+" "+issue.getSeverity()+" : "+issue.getMessage()+"\n\n");
				}
			}
			if (issueVector.size() > 0) {
				String[] choices;
				if(hasErrors) {
					choices = new String[] {"Cancel"};
				} else {
					choices = new String[] {"Paste Anyway", "Cancel"};
				}
				String resultStr = DialogUtils.showWarningDialog(guiRequestComponent, messageBuffer.toString(), choices, "Cancel");
				if (resultStr != null && resultStr.equals("Paste Anyway")) {
					return true;
				}
			}
		}
		return false;
	}

	public interface RXPasteInterface {
		GraphPane getGraphPane();
		void saveDiagram();
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------
	private static void checkStructuresCompatibility(ReactionSpeciesCopy rsCopy, Model modelTo, Structure structTo,
			Vector<Issue> issueVector, IssueContext issueContext) {
		Structure fromStruct = rsCopy.getFromStructure();
		if(rsCopy.getFromStructure().getDimension() != structTo.getDimension()) {
			// the source and destination structures need to have the same dimension, can't paste from compartment to membrane or vice-versa
			String msg = "Unable to paste from a " + fromStruct.getTypeName() + " to a " + structTo.getTypeName();
			msg += ". Both source and destination Structures need to have the same dimension.";
			Issue issue = new Issue(fromStruct, issueContext, IssueCategory.CopyPaste, msg, Issue.Severity.ERROR);
			issueVector.add(issue);
		}
		for(Structure from : rsCopy.getStructuresArr()) {
			Structure to = modelTo.getStructure(from.getName());
			if(to != null && from.getDimension() != to.getDimension()) {
				String msg = "Unable to paste " + from.getTypeName() + " " + from.getName();
				msg += " because a " + to.getTypeName() + " with the same name already exists.";
				Issue issue = new Issue(from, issueContext, IssueCategory.CopyPaste, msg, Issue.Severity.ERROR);
				issueVector.add(issue);
			}
		}
	}
	// returns a list with the "from" molecules we need to add to the modelTo
	// it's a "strict" list, where we have eliminated any conflicting molecules and the ones already present
	private static Set<MolecularType> getMoleculesFromStrict(ReactionSpeciesCopy rsCopy, Model modelTo, 
			Vector<Issue> issueVector, IssueContext issueContext) {
		// molecular types
		Set<MolecularType> mtFromListStrict = new HashSet<>();
		Set<MolecularType> mtConflictList = new HashSet<>();
		Set<MolecularType> mtAlreadyList = new HashSet<>();
		RbmModelContainer rbmmcTo = modelTo.getRbmModelContainer();
		if(rsCopy.getMolecularTypeArr() != null) {
			for(MolecularType mtFrom : rsCopy.getMolecularTypeArr()) {	// the molecules we try to paste here
				MolecularType mtTo = rbmmcTo.getMolecularType(mtFrom.getName());
				if(mtTo == null) {
//					mtTo = new MolecularType(mtFrom, modelTo);
					mtFromListStrict.add(mtFrom);
				} else {
					if(!mtTo.compareEqual(mtFrom)) {
						// conflict: a different mt with the same name already exists
						String msg = "Molecule " + RbmUtils.toBnglString(mtFrom, null, CompartmentMode.hide);
						msg += " to be pasted conflicts with an existing molecule " + RbmUtils.toBnglString(mtTo, null, CompartmentMode.hide) + ".";
						Issue issue = new Issue(mtFrom, issueContext, IssueCategory.CopyPaste, msg, Issue.Severity.ERROR);
						issueVector.add(issue);
						mtConflictList.add(mtFrom);
					} else {
						mtAlreadyList.add(mtFrom);
					}
				}
			}
			if(!mtConflictList.isEmpty()) {
				System.out.println("Found " + mtConflictList.size() + " conflicting molecule(s).");
			}
			if(!mtAlreadyList.isEmpty()) {
				System.out.println("Found " + mtAlreadyList.size() + " molecule(s) already there.");
			}
		}
		return mtFromListStrict;
	}
	
//	private static void pasteMolecules(Set<MolecularType> mtFromListStrict, Model modelTo, Map<Structure, String> structuresMap) {
//		for(MolecularType mtFrom : mtFromListStrict) {
//			try {
//				MolecularType mtTo = new MolecularType(mtFrom, modelTo, structuresMap);
//				modelTo.getRbmModelContainer().addMolecularType(mtTo, false);
//			} catch (ModelException | PropertyVetoException e) {
//				e.printStackTrace();
//				throw new RuntimeException(e.getMessage());
//			}
//		}
//	}
	private static void pasteMolecules(Set<MolecularType> mtFromListStrict, Model modelTo, Map<Structure, String> structuresMap) {
		List<MolecularType> listOfMoleculesToAdd = new ArrayList<>();
		try {
			for(MolecularType mtFrom : mtFromListStrict) {
				MolecularType mtTo = new MolecularType(mtFrom, modelTo, structuresMap);
				listOfMoleculesToAdd.add(mtTo);
			}
			modelTo.getRbmModelContainer().addMolecularTypes(listOfMoleculesToAdd);
		} catch (ModelException | PropertyVetoException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	
	// map the structures to be pasted to existing structures in the cloned model
	// we may need to generate some name iteratively until we solve all naming conflicts
	private static Map<Structure, String> mapStructures(Component requester,
			ReactionSpeciesCopy rsCopy, Model modelTo, Structure structTo,
			IssueContext issueContext) {
		
		Vector <Issue> issueVector = new Vector<>();	// use internally only; we exit the dialog when there are no issues left
		Structure structFrom = rsCopy.getFromStructure();
		
		Map<Structure, String> fullyMappedStructures = new LinkedHashMap<> ();
		fullyMappedStructures.put(structFrom, structTo.getName());

		StructurePasteMappingPanel structureMappingPanel = null;
		do {
			issueVector.clear();
			if(structureMappingPanel == null) {
				structureMappingPanel = new StructurePasteMappingPanel(rsCopy, 
						modelTo, structTo,
						issueVector,  issueContext);
				structureMappingPanel.setPreferredSize(new Dimension(400, 220));
			}
			int result = DialogUtils.showComponentOKCancelDialog(requester, structureMappingPanel, "Assign 'From' structures to 'To' structures");
			if(result != JOptionPane.OK_OPTION) {
				throw UserCancelException.CANCEL_GENERIC;
			}
		} while(structureMappingPanel.hasErrors());
		
		for (Map.Entry<Structure, JComboBox<String>> entry : structureMappingPanel.getStructureMap().entrySet()) {
			if(entry.getValue().getSelectedItem().equals(StructurePasteMappingPanel.MAKE_NEW)) {
				String newNameTo = entry.getKey().getName();	// we generate a "to" structure name based on the "from" name
				while(modelTo.getStructure(newNameTo) != null) {
					newNameTo = org.vcell.util.TokenMangler.getNextEnumeratedToken(newNameTo);
					for(Structure sFrom : rsCopy.getStructuresArr()) {
						if(newNameTo.equals(sFrom.getName())) {	// the new name must not match any existing "from" name either
							newNameTo = org.vcell.util.TokenMangler.getNextEnumeratedToken(newNameTo);
							break;
						}
					}
				}
				try {
				// we need to make the new structures right away as we generate the names for them
				// as to avoid risk of duplicates / conflicting names
					if(entry.getKey() instanceof Membrane) {
						modelTo.addMembrane(newNameTo);
					} else {
						modelTo.addFeature(newNameTo);
					}
				} catch(ModelException | PropertyVetoException e) {
					throw new RuntimeException("Failed to generate the missing 'from' Structures in the cloned model, " + e.getMessage());
				}
				fullyMappedStructures.put(entry.getKey(), newNameTo);
			} else {		// name of an existing "to" structure
				fullyMappedStructures.put(entry.getKey(), (String) entry.getValue().getSelectedItem());
			}
		}
		return fullyMappedStructures;
	}	
	private static void mapStructures(Structure structFrom, Map<Structure, String> fullyMappedStructures, Model modelTo) {
		// all the 'from' structures are now fully mapped to unique names for the paste model
		// we only need to generate now those structures if they are missing, using these names
		for (Map.Entry<Structure, String> entry : fullyMappedStructures.entrySet()) {
			String nameTo = entry.getKey().getName();
			String otherName = entry.getValue();
			if(structFrom.getName().equals(nameTo)) {
				continue;	// this is the source structure and we know it's mapped to something, we don't want to duplicate it
			}
			if(modelTo.getStructure(nameTo) != null) {
				continue;	// this structure is in the pasteModel already, nothing to do, go to the next
			}
			try {
				if(entry.getKey() instanceof Membrane) {
					modelTo.addMembrane(nameTo);
				} else {
					modelTo.addFeature(nameTo);
				}
			} catch(ModelException | PropertyVetoException e) {
				throw new RuntimeException("Failed to generate the missing 'from' Structures in the paste model, " + e.getMessage());
			}
		}
	}
	private static List<ReactionRule> pasteRules(ReactionSpeciesCopy rsCopy,
			Model modelTo, Structure structTo,
			Vector<Issue> issueVector, IssueContext issueContext, 
			Map<Structure, String> structuresMap) throws ExpressionBindingException {
		List<ReactionRule> rulesTo = new ArrayList<> ();
		if(rsCopy.getReactionRuleArr() == null) {
			return rulesTo;
		}

		for(ReactionRule rrFrom : rsCopy.getReactionRuleArr()) {
			ReactionRule rrTo = ReactionRule.clone(modelTo, rrFrom, structTo, structuresMap, rsCopy);
			rulesTo.add(rrTo);
		}
		return rulesTo;
	}
	
	public static final void pasteReactionsAndRules(Component requester, ReactionSpeciesCopy rsCopy,
			Model pasteModel, Structure structTo, RXPasteInterface rxPasteInterface) {
		
		// TODO: add the plain reactions and species too
		// TODO: use Frank's dialog to map the species to be pasted to the existing species. Same for reactions
		// alternatively, map automatically all the matching names, paste everything else as new
		// TODO: when pasting, do not bring separately the compartment from where we copy, it's supposed to be the same as the destination
		PasteHelper[] pasteHelper = new PasteHelper[1];
		
		AsynchClientTask issueTask = new AsynchClientTask("Checking Issues...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				Model clonedModel = XmlHelper.cloneModel(pasteModel);
				clonedModel.refreshDependencies();
//				Model clonedModel = pasteModel;
				IssueContext issueContext = new IssueContext(ContextType.Model, clonedModel, null);
				Vector <Issue> issues = new Vector<>();
				checkStructuresCompatibility(rsCopy, clonedModel, structTo, issues, issueContext);
				Set<MolecularType> mtFromListStrict = getMoleculesFromStrict(rsCopy, clonedModel, issues, issueContext);
				if (issues.size() != 0) {	// at this point we can only have fatal error issues or no issues at all
					if (!printIssues(issues, requester)) {
						throw UserCancelException.CANCEL_GENERIC;
					}
				}

				// map all the structures of the reactions, rules and their participants to existing structures, at need make new structures
				// key is the "from" structure, value is the name of the equivalent "to" structure
				// on the first position we have the struct from where we copy (key) and the struct where we paste (value)
				Map<Structure, String> structuresMap;
				if(rsCopy.getStructuresArr().length > 1) {
					structuresMap = mapStructures(requester, rsCopy, clonedModel, structTo, issueContext);	// throws CANCEL_GENERIC if the user cancels
				} else {	// if length == 1 it's just structFrom which automatically maps to structTo
					structuresMap = new LinkedHashMap<> ();
					structuresMap.put(rsCopy.getFromStructure(), structTo.getName());
				}

				pasteMolecules(mtFromListStrict, clonedModel, structuresMap);
				
				List<ReactionRule> rulesTo = pasteRules(rsCopy, clonedModel, structTo, issues, issueContext, structuresMap);
//				for(ReactionRule rr : rulesTo) {
//					clonedModel.getRbmModelContainer().addReactionRule(rr);
//				}
				clonedModel.getRbmModelContainer().addReactionRules(rulesTo);
				// TODO: verify here id there's any name clash between species
				// TODO: paste all the seed species? We'll need to add seed species array to ReactionSpeciesCopy during the Copy action
				
				// TODO: make any final verifications in the cloned model here
				// if anything is wrong exit here with some helpful message
				// .....
				
				// otherwise go directly to populating the real model
				
				mapStructures(rsCopy.getFromStructure(), structuresMap, pasteModel);
				pasteMolecules(mtFromListStrict, pasteModel, structuresMap);
				
				// we repeat all the steps to paste the rules in the real model instead of the clone
				rulesTo = pasteRules(rsCopy, pasteModel, structTo, issues, issueContext, structuresMap);
				pasteModel.getRbmModelContainer().addReactionRules(rulesTo);
				
				LinkedHashSet<String> scMap = new LinkedHashSet<String>();
				SpeciesContext[] speciesContextArr = rsCopy.getSpeciesContextArr();
				
				if(speciesContextArr != null) {
					for(SpeciesContext scToPaste : speciesContextArr) {		// make list with all the scToPaste names
						scMap.add(scToPaste.getName());
					}
					for(SpeciesContext scToPaste : speciesContextArr) {
	
						int count = 0;				// generate unique name for the species
						String sName = null;
						String nameRoot = "s";
						while (true) {
							sName = nameRoot + count;	
							if (Model.isNameUnused(sName, pasteModel) && !scMap.contains(sName)) {
								break;
							}	
							count++;
						}
						Species sNew = new Species(sName, null);
						pasteModel.addSpecies(sNew);
						SpeciesPattern sp = scToPaste.getSpeciesPattern();
						// SpeciesContext(KeyValue key, String name, Species species, Structure structure, SpeciesPattern speciesPattern)
						SpeciesContext scNew = new SpeciesContext(null, scToPaste.getName(), sNew, structTo, sp);
						pasteModel.addSpeciesContext(scNew);
						System.out.println(sNew.getCommonName() + ", " + scNew.getName());
					}
									
	//				for(SpeciesContext sc : pasteModel.getSpeciesContexts()) {
	//					scMap.put(sc.getName(), sc);
	//				}
	//				for(Species s : pasteModel.getSpecies()) {
	//					sMap.put(s.getCommonName(), s);
	//				}
	//				SpeciesContext[] sca = new SpeciesContext[scMap.size()];
	//				scMap.values().toArray(sca);
	//				Species[] sa = new HashSet<Species>(sMap.values()).toArray(new Species[0]);
	//				pasteModel.setSpecies(sa);
	//				pasteModel.setSpeciesContexts(sca);
	
				}
				System.out.println("done");
			}
		};
		
		AsynchClientTask pasteRXTask = new AsynchClientTask("Pasting Reaction...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable)throws Exception {
				IssueContext issueContext = new IssueContext(ContextType.Model, pasteModel, null);
				
//				if (pasteHelper[0].issues.size() != 0) {
//					printIssues(pasteHelper[0].issues, requester);
//				}
//				if(rxPasteInterface != null){
//					for(BioModelEntityObject newBioModelEntityObject:pasteHelper[0].reactionsAndSpeciesContexts.keySet()) {
//						ReactionCartoonTool.copyRelativePosition(rxPasteInterface.getGraphPane().getGraphModel(), pasteHelper[0].reactionsAndSpeciesContexts.get(newBioModelEntityObject), newBioModelEntityObject);
//					}
//					ReactionCartoonTool.selectAndSaveDiagram(rxPasteInterface, new ArrayList<BioModelEntityObject>(pasteHelper[0].reactionsAndSpeciesContexts.keySet()));
////					//Setup to allow dispatcher to set focus on a specified component after it closes the ProgressPopup
//					setFinalWindow(hashTable, rxPasteInterface.getGraphPane());
//				}
			}
		};

		ClientTaskDispatcher.dispatch(requester, new Hashtable<>(), new AsynchClientTask[] {issueTask, pasteRXTask}, false);
	}
	
	public interface AssignmentHelper {
		JComboBox[] getSpeciesAssignmentJCB();
		JComboBox[] getStructureAssignmentJCB();
		ArrayList<JTextField> getFinalNames();
		Component getAssignmentInterface();
		Component getParent();
		void close();
	}
	
	/**
	 * pasteReactionSteps : this method clones the model argument and calls the private pasteReationSteps0 method with the cloned model to see if
	 * there are any issues with the paste operation. If so, the issue list is popped up in a warning dialog, and user is given the option of proceeding
	 * with the paste or cancelling the operation.
	 *  
	 * @param reactionStepsArr : reactions to be pasted
	 * @param model : model where reactions are to be pasted
	 * @param struct : strucure in 'model' where the reactions should be pasted
	 * @param bNew : is it 'paste' or 'paste new' reaction (new reaction Participants are created if 'bNew' is <true>).
	 * @param guiRequestComponent : the parent component for the warning dialog that pops up the issues, if any, encountered in the pasting process
	 * @throws Exception
	 */
	public static final void pasteReactionSteps(Component requester,
			RXPasteInterface rxPasteInterface,ReactionDescription resolvedReaction,
			Model fromModel,Structure fromStructure,ReactionStep fromRXStep,Model pasteToModel,Structure pasteToStructure,
			BioCartoonTool.AssignmentHelper assignmentHelper){
		
		
		AsynchClientTask getRXSourceModelTask = new AsynchClientTask("Get RX source model",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				if(assignmentHelper.getAssignmentInterface() != null) {
					int result = DialogUtils.showComponentOKCancelDialog(assignmentHelper.getParent(), assignmentHelper.getAssignmentInterface(), "Assign Reaction elements");
					if(result != JOptionPane.OK_OPTION) {
						throw UserCancelException.CANCEL_GENERIC;
					}
				}
				JComboBox[] speciesAssignmentJCB = assignmentHelper.getSpeciesAssignmentJCB();
				JComboBox[] structureAssignmentJCB = assignmentHelper.getStructureAssignmentJCB();
				ArrayList<JTextField> finalNamesJTF = assignmentHelper.getFinalNames();
				//Create user assignment preferences
				BioCartoonTool.UserResolvedRxElements userResolvedRxElements =
					new BioCartoonTool.UserResolvedRxElements();
				userResolvedRxElements.fromSpeciesContextArr = new SpeciesContext[resolvedReaction.elementCount()];
				userResolvedRxElements.toSpeciesArr = new Species[resolvedReaction.elementCount()];
				userResolvedRxElements.toStructureArr = new Structure[resolvedReaction.elementCount()];
				userResolvedRxElements.finalNames = new ArrayList<>();
				StringBuffer warningsSB = new StringBuffer();
				for (int i = 0; i < resolvedReaction.elementCount(); i++) {
					userResolvedRxElements.finalNames.add(finalNamesJTF.get(i));
					System.out.println(resolvedReaction.getOrigSpeciesContextName(i));
					userResolvedRxElements.fromSpeciesContextArr[i] =
						fromModel.getSpeciesContext(resolvedReaction.getOrigSpeciesContextName(i));
					userResolvedRxElements.toSpeciesArr[i] =
						(speciesAssignmentJCB[i].getSelectedItem() instanceof SpeciesContext?
								((SpeciesContext)speciesAssignmentJCB[i].getSelectedItem()).getSpecies():
									null);
					userResolvedRxElements.toStructureArr[i] =
						(Structure)structureAssignmentJCB[i].getSelectedItem();
					if(userResolvedRxElements.toSpeciesArr[i] != null){
						SpeciesContext fromSpeciesContext = userResolvedRxElements.fromSpeciesContextArr[i];
						Species toSpecies = userResolvedRxElements.toSpeciesArr[i];
						if(fromSpeciesContext.getSpecies().getDBSpecies() != null &&
								!Compare.isEqualOrNull(toSpecies.getDBSpecies(),fromSpeciesContext.getSpecies().getDBSpecies())){
							warningsSB.append(
								(warningsSB.length()>0?"\n":"")+							
								"'"+fromSpeciesContext.getSpecies().getCommonName()+"' formal("+
								(fromSpeciesContext.getSpecies().getDBSpecies() != null?fromSpeciesContext.getSpecies().getDBSpecies().getPreferredName():"null")+")"+
								"\nwill be re-assigned to\n"+
								"'"+toSpecies.getCommonName()+"' formal("+
								(toSpecies.getDBSpecies() != null?toSpecies.getDBSpecies().getPreferredName():"null")+")"
							);
						}				
					}
				}
				if(warningsSB.length() > 0){
					final String proceed = "Add reaction anyway";
					final String cancel = "Cancel";
					String result = DialogUtils.showWarningDialog(requester/*DBReactionWizardPanel.this*/,
							"A user choice selected under 'Assign to Model species' will force re-assignment of "+
							"the formal reference for one of the species in the reaction.\n"+warningsSB,
							new String[] {proceed,cancel}, cancel);
					if(result.equals(cancel)){
						throw UserCancelException.CANCEL_GENERIC;
					}
				}
				hashTable.put("fromRXStep", fromRXStep);
				hashTable.put("userResolvedRxElements", userResolvedRxElements);
			}
		};

		PasteHelper[] pasteHelper = new PasteHelper[1];
		AsynchClientTask issueTask = new AsynchClientTask("Checking Issues...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				ReactionStep[] reactionStepsArrOrig = new ReactionStep[] {(ReactionStep)hashTable.get("fromRXStep")};
				UserResolvedRxElements userResolvedRxElements = (UserResolvedRxElements)hashTable.get("userResolvedRxElements");
				Model clonedModel = XmlHelper.cloneModel(pasteToModel);
				clonedModel.refreshDependencies();
				IssueContext issueContext = new IssueContext(ContextType.Model, clonedModel, null);
				HashMap<String,HashMap<ReactionParticipant,Structure>> userResolved_rxPartMapStruct = null;
				if(userResolvedRxElements != null) {
					userResolved_rxPartMapStruct = new HashMap<>();
					for(int i=0;reactionStepsArrOrig!= null && i<reactionStepsArrOrig.length;i++){
						userResolved_rxPartMapStruct.put(reactionStepsArrOrig[i].getName(), new HashMap<ReactionParticipant,Structure>());
						ReactionParticipant[] reactionParticipantsOrig = reactionStepsArrOrig[i].getReactionParticipants();
						for (int j = 0; j < reactionParticipantsOrig.length; j++) {
							HashMap<Structure, Species> preferredReactionElement = UserResolvedRxElements.getPreferredReactionElement(userResolvedRxElements, reactionParticipantsOrig[j]);
							Entry<Structure, Species> userChosenStructSpecies = preferredReactionElement.entrySet().iterator().next();
							userResolved_rxPartMapStruct.get(reactionStepsArrOrig[i].getName()).put(reactionParticipantsOrig[j],userChosenStructSpecies.getKey());
						}
					}
//					pasteHelper[0].rxPartMapStruct = new_rxPartMapStruct;
				}
				pasteHelper[0] =
					pasteReactionSteps0(userResolved_rxPartMapStruct,requester, issueContext, reactionStepsArrOrig,
							clonedModel, clonedModel.getStructure(pasteToStructure.getName()), false,/*bUseDBSpecies,*/
							UserResolvedRxElements.createCompatibleUserResolvedRxElements(userResolvedRxElements, clonedModel));
				if (pasteHelper[0].issues.size() != 0) {
					if (!printIssues(pasteHelper[0].issues, requester)) {
						throw UserCancelException.CANCEL_GENERIC;
					}
				}
				if(pasteHelper[0].rxPartMapStruct != null){
					//Convert rxPartMapStruct instances from cloned to pasteModel
					HashMap<String,HashMap<ReactionParticipant,Structure>> new_rxPartMapStruct = new HashMap<>();
					for(int i=0;reactionStepsArrOrig!= null && i<reactionStepsArrOrig.length;i++){
						new_rxPartMapStruct.put(reactionStepsArrOrig[i].getName(), new HashMap<ReactionParticipant,Structure>());
						for(ReactionParticipant rxPart:pasteHelper[0].rxPartMapStruct.get(reactionStepsArrOrig[i].getName()).keySet()){
							ReactionParticipant[] origRXParts = reactionStepsArrOrig[i].getReactionParticipants();
							for(int j=0;j< origRXParts.length;j++){
								if(origRXParts[j].getName().equals(rxPart.getName())){
									new_rxPartMapStruct.get(reactionStepsArrOrig[i].getName()).put(origRXParts[j],pasteToModel.getStructure(pasteHelper[0].rxPartMapStruct.get(reactionStepsArrOrig[i].getName()).get(rxPart).getName()));
								}
							}
						}
					}
					pasteHelper[0].rxPartMapStruct = new_rxPartMapStruct;
				}
			}
		};
		AsynchClientTask pasteRXTask = new AsynchClientTask("Pasting Reaction...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
			@Override
			public void run(Hashtable<String, Object> hashTable)throws Exception {
				Issue[] checkedIssues = pasteHelper[0].issues.toArray(new Issue[0]);
				UserResolvedRxElements userResolvedRxElements = (UserResolvedRxElements)hashTable.get("userResolvedRxElements");
				ReactionStep[] reactionStepsArrOrig = new ReactionStep[] {(ReactionStep)hashTable.get("fromRXStep")};
				IssueContext issueContext = new IssueContext(ContextType.Model, pasteToModel, null);
				pasteHelper[0] = pasteReactionSteps0(pasteHelper[0].rxPartMapStruct,requester, issueContext, reactionStepsArrOrig,
						pasteToModel, pasteToStructure, false,/*bUseDBSpecies,*/userResolvedRxElements);
				Vector<Issue> uncheckedIssues = new Vector<>();
				if (pasteHelper[0].issues.size() != 0) {
					for (Issue issue : pasteHelper[0].issues) {
						boolean bFound = false;
						for (int i = 0; i < checkedIssues.length; i++) {
							if(checkedIssues[i].getMessage().equals(issue.getMessage())) {
								bFound = true;
								break;
							}
						}
						if(!bFound) {
							uncheckedIssues.add(issue);
						}
					}
				}
				if(uncheckedIssues.size() > 0) {
					printIssues(uncheckedIssues, requester);
				}
				if(rxPasteInterface != null){
					for(BioModelEntityObject newBioModelEntityObject:pasteHelper[0].reactionsAndSpeciesContexts.keySet()){
						ReactionCartoonTool.copyRelativePosition(rxPasteInterface.getGraphPane().getGraphModel(), pasteHelper[0].reactionsAndSpeciesContexts.get(newBioModelEntityObject), newBioModelEntityObject);
					}
					ReactionCartoonTool.selectAndSaveDiagram(rxPasteInterface, new ArrayList<BioModelEntityObject>(pasteHelper[0].reactionsAndSpeciesContexts.keySet()));
//					//Setup to allow dispatcher to set focus on a specified component after it closes the ProgressPopup
					setFinalWindow(hashTable,rxPasteInterface.getGraphPane());
				}
			}
		};

		AsynchClientTask reshowInterface = new AsynchClientTask("Reshow interface...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING,false,true) {
			@Override
			public void run(Hashtable<String, Object> hashTable) throws Exception {
				Object error = hashTable.get(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR);
				if(assignmentHelper.getAssignmentInterface() != null && error != null) {
					hashTable.remove(ClientTaskDispatcher.TASK_ABORTED_BY_ERROR);
					//Reshow the interface if there was an error
					new Thread(new Runnable() {
						@Override
						public void run() {
							DialogUtils.showErrorDialog(assignmentHelper.getParent(), ((Throwable)error).getMessage());
							BioCartoonTool.pasteReactionSteps(requester, rxPasteInterface, resolvedReaction, fromModel, fromStructure, fromRXStep, pasteToModel, pasteToStructure, assignmentHelper);
						}
					}).start();
				}else if(assignmentHelper.getAssignmentInterface() == null && error != null) {
					//DialogUtils.showErrorDialog(assignmentHelper.getParent(), ((Throwable)error).getMessage());
					System.out.println("got here");
				}else if(error == null) {
					assignmentHelper.close();
				}
			}
			
		};
		ClientTaskDispatcher.dispatch(requester, new Hashtable<>(), new AsynchClientTask[] {getRXSourceModelTask,issueTask,pasteRXTask,reshowInterface}, false);
	}


	public static class UserResolvedRxElements {
		public SpeciesContext[] fromSpeciesContextArr;
		public Species[] toSpeciesArr;//some elements can be null if user chose 'new' for them
		public Structure[] toStructureArr;
		public ArrayList<JTextField> finalNames;

		public static UserResolvedRxElements createCompatibleUserResolvedRxElements(
				UserResolvedRxElements origResolvedRxP,Model compatibleModel){

			if(origResolvedRxP == null){
				return null;
			}
			UserResolvedRxElements compatibleResolvedRxP = new UserResolvedRxElements();
			compatibleResolvedRxP.finalNames = origResolvedRxP.finalNames;
			compatibleResolvedRxP.fromSpeciesContextArr = origResolvedRxP.fromSpeciesContextArr;
			//Find species
			compatibleResolvedRxP.toSpeciesArr = new Species[origResolvedRxP.toSpeciesArr.length];
			for (int i = 0; i < compatibleResolvedRxP.toSpeciesArr.length; i++) {
				if(origResolvedRxP.toSpeciesArr[i] != null){
					compatibleResolvedRxP.toSpeciesArr[i] =
						compatibleModel.getSpecies(origResolvedRxP.toSpeciesArr[i].getCommonName());				
				}
			}
			//Find Structures
			compatibleResolvedRxP.toStructureArr = new Structure[origResolvedRxP.toStructureArr.length];
			for (int i = 0; i < compatibleResolvedRxP.toStructureArr.length; i++) {
				if(origResolvedRxP.toStructureArr[i] != null){
					compatibleResolvedRxP.toStructureArr[i] =
						compatibleModel.getStructure(origResolvedRxP.toStructureArr[i].getName());				
				}
			}
			//Check
			for (int i = 0; i < origResolvedRxP.fromSpeciesContextArr.length; i++) {
				if(!Compare.isEqualOrNull(origResolvedRxP.toSpeciesArr[i], compatibleResolvedRxP.toSpeciesArr[i])){
					throw new RuntimeException("Create compatible couldn't find matching Species in compatible model");
				}
				if(!Compare.isEqualOrNull(origResolvedRxP.toStructureArr[i], compatibleResolvedRxP.toStructureArr[i])){
					throw new RuntimeException("Create compatible couldn't find matching Structure in compatible model");
				}
			}
			return compatibleResolvedRxP;
		}
		public static HashMap<Structure, Species> getPreferredReactionElement(
				UserResolvedRxElements userResolvedRxElements,
				ReactionParticipant fromReactionParticipant){

			if(userResolvedRxElements == null){
				return null;
			}
			return userResolvedRxElements.getPreferredReactionElement(
					fromReactionParticipant.getSpecies(), fromReactionParticipant.getStructure());
		}
		public static HashMap<Structure, Species> getPreferredReactionElement(
				UserResolvedRxElements userResolvedRxElements,
				SpeciesContext fromSpeciesContext){

			if(userResolvedRxElements == null){
				return null;
			}
			return userResolvedRxElements.getPreferredReactionElement(
					fromSpeciesContext.getSpecies(), fromSpeciesContext.getStructure());
		}

		private HashMap<Structure, Species> getPreferredReactionElement(Species fromSpecies, Structure fromStructure){
			for (int i = 0; i < toSpeciesArr.length; i++) {
				if(fromSpeciesContextArr[i].getStructure().equals(fromStructure)){
					if(fromSpeciesContextArr[i].getSpecies().equals(fromSpecies)){
						HashMap<Structure, Species> result = new HashMap<Structure, Species>();
						result.put(toStructureArr[i], toSpeciesArr[i]);
						return result;
					}				
				}
			}
			throw new RuntimeException("Couldn't find 'from' ReactionParticipant Species="+fromSpecies+" Structure="+fromStructure);
		}
	}
	
//	private static final String DUMMY_CHOOSE = "ChooseCompartment...";
//	private static HashMap<ReactionParticipant,Structure> askUserResolveMembraneConnections(Component requester,Structure[] allStructures,Structure currentStruct,Structure fromRxnStruct,Structure toRxnStruct,
//			ReactionParticipant[] copyFromRxParticipantArr,StructureTopology toStructureTopology,StructureTopology structTopology){
//		HashMap<ReactionParticipant,Structure> userMap = new HashMap<>();
//		boolean bMembrane = toRxnStruct instanceof Membrane;
//		if(!bMembrane){
//			for(int i=0;i<copyFromRxParticipantArr.length;i+= 1){
//				userMap.put(copyFromRxParticipantArr[i],null);
//				for(Structure struct:allStructures){
//					if(fromRxnStruct.getName().equals(copyFromRxParticipantArr[i].getStructure().getName())){
//						userMap.put(copyFromRxParticipantArr[i],toRxnStruct);
//					}
//				}
//			}
////			return userMap;
//		}else{
//			for(int i=0;i<copyFromRxParticipantArr.length;i+= 1){
//				Structure pasteToStruct = currentStruct;
//				Membrane oldMembr = (Membrane)fromRxnStruct;
//				pasteToStruct =
//				matchMembraneAdjacentStructure(allStructures,currentStruct, copyFromRxParticipantArr[i].getStructure(), structTopology, toStructureTopology, oldMembr, pasteToStruct);
//				userMap.put(copyFromRxParticipantArr[i],pasteToStruct);
//			}
//		}
//		JScrollPane jScrollPane = null;
//		JPanel rxMapperPanel = null;
//		Hashtable<JLabel,ReactionParticipant> mapLabelToPart = null;
//		boolean bUnselected;
//		do{
//			bUnselected = false;
//			if(jScrollPane == null){
//				rxMapperPanel = new JPanel();
//				rxMapperPanel.setLayout(new BoxLayout(rxMapperPanel, BoxLayout.Y_AXIS));
//				//((BoxLayout)rxMapperPanel.getLayout())
//				int height = 0;
//				final int[] widthlabels = new int[] {0};
//				int widthcombo= 0;
//				mapLabelToPart = new Hashtable<>();
//				for(ReactionParticipant rxPart:userMap.keySet()){
//					if(!bMembrane || (rxPart.getStructure() instanceof Feature)){
//						JPanel row = new JPanel();
//						JLabel rxpartLabel = new JLabel(rxPart.getName());
//						row.add(rxpartLabel);
//						mapLabelToPart.put(rxpartLabel, rxPart);
//						JComboBox<Structure> structJC = new JComboBox<>();
//						structJC.setRenderer(new ListCellRenderer<Structure>() {
//							@Override
//							public Component getListCellRendererComponent(
//									JList<? extends Structure> list, Structure value, int index,
//									boolean isSelected, boolean cellHasFocus) {
//								// TODO Auto-generated method stub
//								JLabel label = new JLabel(value.getName());
//								widthlabels[0] = Math.max(widthlabels[0], label.getPreferredSize().width);
//								return label;
//							}
//						});
//						height+= structJC.getPreferredSize().getHeight();
//						widthcombo = Math.max(widthcombo, structJC.getPreferredSize().width);
//						try{
//							Feature dummyFeature = new Feature(DUMMY_CHOOSE);
//							structJC.addItem(dummyFeature);
//						}catch(Exception e){
//							e.printStackTrace();
//						}
//						for(Structure struct:allStructures){
//							if(!bMembrane || (struct instanceof Feature)){
//								structJC.addItem(struct);
//							}
//						}
//						structJC.setSelectedItem((userMap.get(rxPart) == null?0:userMap.get(rxPart)));
//						row.add(structJC);
//						rxMapperPanel.add(row);
//					}
//				}
//				height+= 25;
//				rxMapperPanel.setSize(widthcombo+widthlabels[0], height);
//				rxMapperPanel.setPreferredSize(new Dimension(widthcombo+widthlabels[0], height));
//				jScrollPane = new JScrollPane(rxMapperPanel);
//				jScrollPane.setPreferredSize(new Dimension(150,100));
//			}
//			if(rxMapperPanel.getComponentCount() != 0){
//				int result = DialogUtils.showComponentOKCancelDialog(requester, jScrollPane, "Assign Compartments for RX '"+copyFromRxParticipantArr[0].getReactionStep().getName()+"' Participants");
//				if(result != JOptionPane.OK_OPTION){
//					throw UserCancelException.CANCEL_GENERIC;
//				}
//				
//				for(int i =0;i< rxMapperPanel.getComponentCount();i++){
//					JLabel label0 = (JLabel)(((Container)rxMapperPanel.getComponent(i)).getComponent(0));
//					JComboBox<Structure> struct0 = (JComboBox<Structure>)(((Container)rxMapperPanel.getComponent(i)).getComponent(1));
//					if(((Structure)struct0.getSelectedItem()).getName().equals(DUMMY_CHOOSE)){
//						bUnselected = true;
//						DialogUtils.showWarningDialog(requester, "Choose a valid compartment for each ReactionParticipant");
//						break;
//					}
//					userMap.put(mapLabelToPart.get(label0),(Structure)struct0.getSelectedItem());
//				}
//			}
//		}while(bUnselected);
//		return userMap;
//	}
	
	private static Structure matchMembraneAdjacentStructure(Structure[] toAllStructures,Structure currentStruct,Structure rxPartStruct,StructureTopology structTopology,StructureTopology toStructureTopology,Membrane oldMembr,Structure pasteToStruct){
		if((structTopology.getOutsideFeature(oldMembr) != null && rxPartStruct.getName().equals(structTopology.getOutsideFeature(oldMembr).getName())) ||
			(structTopology.getInsideFeature(oldMembr) != null && rxPartStruct.getName().equals(structTopology.getInsideFeature(oldMembr).getName()))){
			for(Structure struct:toAllStructures){
				if(struct.getName().equals(rxPartStruct.getName())){
					return struct;
				}
			}
		}
		return pasteToStruct;
	}
	public static class PasteHelper {
		public Vector<Issue> issues;
		public HashMap<String,HashMap<ReactionParticipant,Structure>> rxPartMapStruct;
		public HashMap<BioModelEntityObject,BioModelEntityObject> reactionsAndSpeciesContexts;
		public PasteHelper(Vector<Issue> issues,HashMap<String,HashMap<ReactionParticipant,Structure>> rxPartMapStruct,HashMap<BioModelEntityObject,BioModelEntityObject> reactionsAndSpeciesContexts) {
			this.issues = issues;
			this.rxPartMapStruct = rxPartMapStruct;
			this.reactionsAndSpeciesContexts = reactionsAndSpeciesContexts;
		}
	}
	/**
	 * pasteReactionSteps0 : does the actual pasting. First called with a cloned model, to track issues. If user still wants to proceed, the paste
	 * is performed on the original model.
	 * 
	 * Insert the method's description here.
	 * Creation date: (5/10/2003 3:55:25 PM)
	 * @param pasteToModel cbit.vcell.model.Model
	 * @param pasteToStructure cbit.vcell.model.Structure
	 * @param bNew boolean
	 */
	private static final PasteHelper pasteReactionSteps0(HashMap<String,HashMap<ReactionParticipant,Structure>> rxPartMapStructure,Component parent,IssueContext issueContext,
			ReactionStep[] copyFromRxSteps,Model pasteToModel, Structure pasteToStructure,
			boolean bNew,/*boolean bUseDBSpecies,*/
			UserResolvedRxElements userResolvedRxElements) throws Exception {

		HashMap<BioModelEntityObject,BioModelEntityObject> reactionsAndSpeciesContexts = new HashMap<>();
		
		if(copyFromRxSteps == null || copyFromRxSteps.length == 0 || pasteToModel == null || pasteToStructure == null){
			throw new IllegalArgumentException("CartoonTool.pasteReactionSteps Error "+
					(copyFromRxSteps == null || copyFromRxSteps.length == 0?"reactionStepsArr empty ":"")+
					(pasteToModel == null?"model is null ":"")+
					(pasteToStructure == null?"struct is null ":"")
			);
		}

		if(!pasteToModel.contains(pasteToStructure)){
			throw new IllegalArgumentException("CartoonTool.pasteReactionSteps model "+pasteToModel.getName()+" does not contain structure "+pasteToStructure.getName());
		}

		//Check PasteToModel has preferred targets if set
		if(userResolvedRxElements != null){
			for (int i = 0; i < userResolvedRxElements.toSpeciesArr.length; i++) {
				if(userResolvedRxElements.toSpeciesArr[i] != null){
//					Structure toNewStruct = userResolvedRxElements.toStructureArr[i];
//					SpeciesContext[] toNewSC = pasteToModel.getSpeciesContexts(toNewStruct);
//					SpeciesContext[] usersSC = userResolvedRxElements.fromSpeciesContextArr;
//					boolean bFound = false;
//					for (int j = 0; j < toNewSC.length; j++) {
//						boolean structeql = toNewSC[j].getStructure().getName().equals(usersSC[i].getStructure().getName());
//						boolean specieseql = toNewSC[j].getSpecies().getCommonName().equals(usersSC[i].getSpecies().getCommonName());
//						System.out.println(toNewSC[j]+" "+structeql+" "+usersSC[i]+" "+specieseql);
//						if(structeql &&  specieseql) {
//							bFound = true;
//							break;
//						}
//					}
//					if(!bFound) {
//						throw new Exception("Expecting speciesContext '"+usersSC[i].getSpecies().getCommonName()+"' to exist already in structure "+toNewStruct.getName());
//					}
//
////					if(!pasteToModel.contains(userResolvedRxElements.toSpeciesArr[i])){
////						throw new RuntimeException("PasteToModel does not contain preferred Species "+userResolvedRxElements.toSpeciesArr[i]);
////					}
				}
//				else {
//					Structure toNewStruct = userResolvedRxElements.toStructureArr[i];
//					SpeciesContext[] toNewSC = pasteToModel.getSpeciesContexts(toNewStruct);
//					SpeciesContext[] usersSC = userResolvedRxElements.fromSpeciesContextArr;
//					boolean bFound = false;
//					for (int j = 0; j < toNewSC.length; j++) {
//						System.out.println(toNewSC[j]+" "+usersSC[i]);
//						if(toNewSC[j].getStructure().equals(usersSC[i].getStructure()) &&  toNewSC[j].getSpecies().equals(usersSC[i].getSpecies())) {
//							bFound = true;
//							break;
//						}
//					}
//					if(!bFound) {
//						throw new Exception("Expecting speciesContext '"+usersSC[i].getName()+"' to exist already");
//					}
//				}
				if(userResolvedRxElements.toStructureArr[i] != null){
					if(!pasteToModel.contains(userResolvedRxElements.toStructureArr[i])){
						throw new RuntimeException("PasteToModel does not contain preferred Structure "+userResolvedRxElements.toStructureArr[i]);
					}
				}
			}
		}
		int counter = 0;
		Structure currentStruct = pasteToStructure;
		String copiedStructName = copyFromRxSteps[counter].getStructure().getName();
		StructureTopology structTopology = (copyFromRxSteps[counter].getModel()==null?pasteToModel.getStructureTopology():copyFromRxSteps[counter].getModel().getStructureTopology());
		IdentityHashMap<Species, Species> speciesHash = new IdentityHashMap<Species, Species>();
		IdentityHashMap<SpeciesContext, SpeciesContext> speciesContextHash = new IdentityHashMap<SpeciesContext, SpeciesContext>();
		Vector<Issue> issueVector = new Vector<Issue>();
		do{
			// create a new reaction, instead of cloning the old one; set struc
			ReactionStep copyFromReactionStep = copyFromRxSteps[counter]; 
			String newName = copyFromReactionStep.getName();
			while(pasteToModel.getReactionStep(newName) != null){
				newName = org.vcell.util.TokenMangler.getNextEnumeratedToken(newName);
			}
			ReactionStep newReactionStep = null;

			if (copyFromReactionStep instanceof SimpleReaction) {
				newReactionStep = new SimpleReaction(pasteToModel, currentStruct, newName, copyFromReactionStep.isReversible());
			} else if (copyFromReactionStep instanceof FluxReaction && currentStruct instanceof Membrane) {
				newReactionStep = new FluxReaction(pasteToModel, (Membrane)currentStruct, null, newName, copyFromReactionStep.isReversible());
			}

			pasteToModel.addReactionStep(newReactionStep);
			reactionsAndSpeciesContexts.put(newReactionStep,copyFromReactionStep);
			Structure toRxnStruct = newReactionStep.getStructure();
			Structure fromRxnStruct = copyFromReactionStep.getStructure();

			if(!fromRxnStruct.getClass().equals(pasteToStructure.getClass())){
				throw new Exception("Cannot copy reaction from "+fromRxnStruct.getTypeName() + 
						" to " + pasteToStructure.getTypeName() + ".");
			}

			// add appropriate reactionParticipants to newReactionStep.
			StructureTopology toStructureTopology = pasteToModel.getStructureTopology();
			ReactionParticipant[] copyFromRxParticipantArr = copyFromReactionStep.getReactionParticipants();
			if(rxPartMapStructure == null){//null during 'issues' trial
				rxPartMapStructure = new HashMap<String,HashMap<ReactionParticipant,Structure>>();
			}
//			if(rxPartMapStructure.get(copyFromReactionStep.getName()) == null){// Ask user to assign species to compartments for each reaction to be pasted
//				rxPartMapStructure.put(copyFromReactionStep.getName(),
//						askUserResolveMembraneConnections(parent, pasteToModel.getStructures(), currentStruct,fromRxnStruct, toRxnStruct,copyFromRxParticipantArr, toStructureTopology, structTopology));
//			}
			for(int i=0;i<copyFromRxParticipantArr.length;i+= 1){
				Structure pasteToStruct = currentStruct;
//				if(toRxnStruct instanceof Membrane){
					pasteToStruct = rxPartMapStructure.get(copyFromReactionStep.getName()).get(copyFromRxParticipantArr[i]);
//					if(pasteToStruct == null){
//						for(ReactionParticipant myRXPart:rxPartMapStructure.get(copyFromReactionStep.getName()).keySet()){
//							if(myRXPart.getSpeciesContext().getName().equals(copyFromRxParticipantArr[i].getSpeciesContext().getName())){
//								pasteToStruct = rxPartMapStructure.get(copyFromReactionStep.getName()).get(myRXPart);
//								break;
//							}
//						}
//					}
//				}
				// this adds the speciesContexts and species (if any) to the model)
					SpeciesContext newSc = null;
					for (int j = 0; j < userResolvedRxElements.fromSpeciesContextArr.length; j++) {
						String forceName = userResolvedRxElements.finalNames.get(j).getText();
						if(userResolvedRxElements.fromSpeciesContextArr[j] == copyFromRxParticipantArr[i].getSpeciesContext()) {
							if(userResolvedRxElements.toSpeciesArr[j] == null) {
								newSc = pasteSpecies(parent, copyFromRxParticipantArr[i].getSpecies(),null,pasteToModel,pasteToStruct,bNew, /*bUseDBSpecies,*/speciesHash,
									UserResolvedRxElements.getPreferredReactionElement(userResolvedRxElements,copyFromRxParticipantArr[i]));
								changeName(userResolvedRxElements, newSc, j,pasteToModel,forceName);
								reactionsAndSpeciesContexts.put(newSc,copyFromRxParticipantArr[i].getSpeciesContext());
							}else {
								if(forceName != null && forceName.length()>0 && pasteToModel.getSpeciesContext(forceName) != null) {
									if(pasteToModel.getSpeciesContext(forceName).getStructure().getName() == userResolvedRxElements.toStructureArr[j].getName()) {
										throw new Exception("Paste custom name error:\nSpeciesContext name '"+forceName+"' in structure '"+userResolvedRxElements.toStructureArr[j].getName()+"' already used");										
									}
								}
								newSc = pasteToModel.getSpeciesContext(userResolvedRxElements.toSpeciesArr[j], userResolvedRxElements.toStructureArr[j]);
								if(newSc == null) {
									newSc = pasteSpecies(parent, copyFromRxParticipantArr[i].getSpecies(),null,pasteToModel,pasteToStruct,bNew, /*bUseDBSpecies,*/speciesHash,
											UserResolvedRxElements.getPreferredReactionElement(userResolvedRxElements,copyFromRxParticipantArr[i]));
										changeName(userResolvedRxElements, newSc, j,pasteToModel,forceName);									
								}else if(forceName != null && forceName.length()>0) {
									throw new Exception("Paste custom name error:\nCan't rename existing speciesContext '"+newSc.getName()+"' in structure '"+newSc.getStructure().getName()+"' to '"+forceName+"'");
								}
								reactionsAndSpeciesContexts.put(newSc,copyFromRxParticipantArr[i].getSpeciesContext());
//								String rootSC = ReactionCartoonTool.speciesContextRootFinder(copyFromRxParticipantArr[i].getSpeciesContext());
//								SpeciesContext[] matchSC = pasteToModel.getSpeciesContexts();
//								for(int k=0;matchSC != null && k<matchSC.length;k++){
//									String matchRoot = ReactionCartoonTool.speciesContextRootFinder(matchSC[k]);
//									if(matchRoot != null && matchRoot.equals(rootSC) && matchSC[k].getStructure().getName().equals(pasteToStruct.getName())){
//										newSc = matchSC[k];
//										reactionsAndSpeciesContexts.put(newSc, matchSC[k]);
//										break;
//									}
//								}
							}
							if(newSc == null) {
								throw new Exception("Couldn't assign speciesContext='"+copyFromRxParticipantArr[i].getSpeciesContext().getName()+"' to species='"+userResolvedRxElements.toSpeciesArr[j].getCommonName()+"' in structure='"+userResolvedRxElements.toStructureArr[j].getName()+"', species/structure not exist");
							}

						}
					}
//				String rootSC = ReactionCartoonTool.speciesContextRootFinder(copyFromRxParticipantArr[i].getSpeciesContext());
//				SpeciesContext newSc = null;
////				if(!bNew) {
//				SpeciesContext[] matchSC = pasteToModel.getSpeciesContexts();
//				for(int j=0;matchSC != null && j<matchSC.length;j++){
//					String matchRoot = ReactionCartoonTool.speciesContextRootFinder(matchSC[j]);
//					if(matchRoot != null && matchRoot.equals(rootSC) && matchSC[j].getStructure().getName().equals(pasteToStruct.getName())){
//						newSc = matchSC[j];
//						reactionsAndSpeciesContexts.put(newSc, matchSC[j]);
//						break;
//					}
//				}
////				}
//				
//				if(newSc == null){
//					newSc = pasteSpecies(parent, copyFromRxParticipantArr[i].getSpecies(),rootSC,pasteToModel,pasteToStruct,bNew, /*bUseDBSpecies,*/speciesHash,
//							UserResolvedRxElements.getPreferredReactionElement(userResolvedRxElements,copyFromRxParticipantArr[i]));
//					reactionsAndSpeciesContexts.put(newSc,copyFromRxParticipantArr[i].getSpeciesContext());
//				}
				// record the old-new speciesContexts (reactionparticipants) in the IdHashMap, this is useful, esp for 'Paste new', while replacing proxyparams.
				SpeciesContext oldSc = copyFromRxParticipantArr[i].getSpeciesContext();
				if (speciesContextHash.get(oldSc) == null) {
					speciesContextHash.put(oldSc, newSc);
				}
				if (copyFromRxParticipantArr[i] instanceof Reactant) {
					newReactionStep.addReactionParticipant(new Reactant(null, newReactionStep, newSc, copyFromRxParticipantArr[i].getStoichiometry()));
				} else if (copyFromRxParticipantArr[i] instanceof Product) {
					newReactionStep.addReactionParticipant(new Product(null, newReactionStep, newSc, copyFromRxParticipantArr[i].getStoichiometry()));
				} else if (copyFromRxParticipantArr[i] instanceof Catalyst) {
					newReactionStep.addCatalyst(newSc);
				}
			}

//			// If 'newReactionStep' is a fluxRxn, set its fluxCarrier
//			if (newReactionStep instanceof FluxReaction) {
//				if (fluxCarrierSp != null) {
//					((FluxReaction)newReactionStep).setFluxCarrier(fluxCarrierSp, pasteToModel);
//				} else {
//					throw new RuntimeException("Could not set FluxCarrier species for the flux reaction to be pasted");
//				}
//			}

			// For each kinetic parameter expression for new kinetics, replace the proxyParams from old kinetics with proxyParams in new kinetics
			// i.e., if the proxyParams are speciesContexts, replace with corresponding speciesContext in newReactionStep;
			// if the proxyParams are structureSizes or MembraneVoltages, replace with corresponding structure quantity in newReactionStep
			Kinetics oldKinetics = copyFromReactionStep.getKinetics();
			KineticsParameter[] oldKps = oldKinetics.getKineticsParameters(); 
			KineticsProxyParameter[] oldKprps = oldKinetics.getProxyParameters();
			Hashtable<String, Expression> paramExprHash = new Hashtable<String, Expression>();
			for (int i = 0; oldKps != null && i < oldKps.length; i++) {
				Expression newExpression = new Expression(oldKps[i].getExpression());
				for (int j = 0; oldKprps != null && j < oldKprps.length; j++) {
					// check if kinetic proxy parameter is in kinetic parameter expression
					if (newExpression.hasSymbol(oldKprps[j].getName())) {
						SymbolTableEntry ste = oldKprps[j].getTarget();
						Model pasteFromModel = copyFromReactionStep.getModel();
						if (ste instanceof SpeciesContext) {
							// if newRxnStruct is a feature/membrane, get matching spContexts from old reaction and replace them in new rate expr. 
							SpeciesContext oldSC = (SpeciesContext)ste;
							SpeciesContext newSC = speciesContextHash.get(oldSC);
							if (newSC == null) {
								// the speciesContext (ste) was not a rxnParticipant. If paste-model is different from copy/cut-model, 
								// check if oldSc is present in paste-model; if not, add it.
								if (!pasteToModel.equals(pasteFromModel)) {
									if (pasteToModel.getSpeciesContext(oldSC.getName()) == null) {
										// if paste-model has oldSc struct, paste it there, 
										Structure newSCStruct = pasteToModel.getStructure(oldSC.getStructure().getName()); 
										if (newSCStruct != null) {
											newSC = pasteSpecies(parent, oldSC.getSpecies(), null, pasteToModel, newSCStruct, bNew, /*bUseDBSpecies,*/speciesHash,
													UserResolvedRxElements.getPreferredReactionElement(userResolvedRxElements, oldSC));
											speciesContextHash.put(oldSC, newSC);
										} else {
											// oldStruct wasn't found in paste-model, paste it in newRxnStruct and add warning to issues list
											newSC = pasteSpecies(parent, oldSC.getSpecies(), null, pasteToModel, toRxnStruct, bNew, /*bUseDBSpecies,*/speciesHash,
													UserResolvedRxElements.getPreferredReactionElement(userResolvedRxElements, oldSC));
											speciesContextHash.put(oldSC, newSC);
											Issue issue = new Issue(oldSC, issueContext, IssueCategory.CopyPaste,
													"SpeciesContext '" + oldSC.getSpecies().getCommonName() + "' was not found in compartment '" +
													oldSC.getStructure().getName() + "' in the model; the species was added to the compartment '" +
													toRxnStruct.getName() + "' where the reaction was pasted.",
													Issue.SEVERITY_WARNING);
											issueVector.add(issue);
										}
									}
								}
								// if models are the same and newSc is null, then oldSc is not a rxnParticipant. Leave it as is in the expr. 
							}
							if (newSC != null) {
								reactionsAndSpeciesContexts.put(newSC,oldSC);
								newExpression.substituteInPlace(new Expression(ste.getName()), new Expression(newSC.getName()));
							}
							//							SpeciesContext sc = null;
							//							Species newSp = model.getSpecies(oldSc.getSpecies().getCommonName());
							//							if  (oldSc.getStructure() == (oldRxnStruct)) {
							//								sc = model.getSpeciesContext(newSp, newRxnStruct);
							//							} else {
							//								if (newRxnStruct instanceof Membrane) {
							//									// for a membrane, we need to make sure that inside-outside spContexts used are appropriately replaced.
							//									if (oldSc.getStructure() == ((Membrane)oldRxnStruct).getOutsideFeature()) {
							//										// old speciesContext is outside (old) membrane, new spContext should be outside new membrane
							//										sc = model.getSpeciesContext(newSp, ((Membrane)newRxnStruct).getOutsideFeature());
							//									} else if (oldSc.getStructure() == ((Membrane)oldRxnStruct).getInsideFeature()) {
							//										// old speciesContext is inside (old) membrane, new spContext should be inside new membrane
							//										sc = model.getSpeciesContext(newSp, ((Membrane)newRxnStruct).getInsideFeature());
							//									}
							//								}
							//							}
							//							if (sc != null) {
							//								newExpression.substituteInPlace(new Expression(ste.getName()), new Expression(sc.getName()));
							//							}
						} else if (ste instanceof StructureSize) {
							Structure str = ((StructureSize)ste).getStructure();
							// if the structure size used is same as the structure in which the reaction is present, change the structSize to appropriate new struct  
							if (str.compareEqual(fromRxnStruct)) {
								newExpression.substituteInPlace(new Expression(ste.getName()), new Expression(toRxnStruct.getStructureSize().getName()));
							} else {
								if (fromRxnStruct instanceof Membrane) {
									if (str.equals(structTopology.getOutsideFeature((Membrane)fromRxnStruct))) {
										newExpression.substituteInPlace(new Expression(ste.getName()), new Expression(structTopology.getOutsideFeature((Membrane)toRxnStruct).getStructureSize().getName()));
									} else if (str.equals(structTopology.getInsideFeature((Membrane)fromRxnStruct))) {
										newExpression.substituteInPlace(new Expression(ste.getName()), new Expression(structTopology.getInsideFeature((Membrane)toRxnStruct).getStructureSize().getName()));
									}
								}
							}
						} else if (ste instanceof MembraneVoltage) {
							Membrane membr = ((MembraneVoltage)ste).getMembrane();
							// if the MembraneVoltage used is same as that of the membrane in which the reaction is present, change the MemVoltage 
							if ((fromRxnStruct instanceof Membrane) && (membr.compareEqual(fromRxnStruct))) {
								newExpression.substituteInPlace(new Expression(ste.getName()), new Expression(((Membrane)toRxnStruct).getMembraneVoltage().getName()));
							} 
						} else if (ste instanceof ModelParameter) {
							// see if model has this global parameter (if rxn is being pasted into another model, it won't)
							if (!pasteToModel.equals(pasteFromModel)) {
								ModelParameter oldMp = (ModelParameter)ste;
								ModelParameter mp = pasteToModel.getModelParameter(oldMp.getName());
								boolean bNonNumeric = false;
								String newMpName = oldMp.getName();
								if (mp != null) {
									// new model has a model parameter with same name - are they the same param?
									if (!mp.getExpression().equals(oldMp.getExpression())) {
										// no, they are not the same param, so mangle the 'ste' name and add as global in the other model
										while (pasteToModel.getModelParameter(newMpName) != null) {
											newMpName = TokenMangler.getNextEnumeratedToken(newMpName);
										}
										// if expression if numeric, add it as such. If not, set it to 0.0 and add it as global
										Expression exp = oldMp.getExpression(); 
										if (!exp.flatten().isNumeric()) {
											exp = new Expression(0.0);
											bNonNumeric = true;
										}
										ModelParameter newMp = pasteToModel.new ModelParameter(newMpName, exp, Model.ROLE_UserDefined, oldMp.getUnitDefinition());
										String annotation = "Copied from model : " + pasteFromModel.getNameScope();
										newMp.setModelParameterAnnotation(annotation);
										pasteToModel.addModelParameter(newMp);
										// if global param name had to be changed, make sure newExpr is updated as well.
										if (!newMpName.equals(oldMp.getName())) {
											newExpression.substituteInPlace(new Expression(oldMp.getName()), new Expression(newMpName));
										}
									}
								} else {
									// no global param with same name was found in other model, so add it to other model.
									// if expression if numeric, add it as such. If not, set it to 0.0 and add it as global
									Expression exp = oldMp.getExpression(); 
									if (!exp.flatten().isNumeric()) {
										exp = new Expression(0.0);
										bNonNumeric = true;
									}
									ModelParameter newMp = pasteToModel.new ModelParameter(newMpName, exp, Model.ROLE_UserDefined, oldMp.getUnitDefinition());
									String annotation = "Copied from model : " + pasteFromModel.getNameScope();
									newMp.setModelParameterAnnotation(annotation);
									pasteToModel.addModelParameter(newMp);	
								}
								// if a non-numeric parameter was encountered in the old model, it was added as a numeric (0.0), warn user of change.
								if (bNonNumeric) {
									Issue issue = new Issue(oldMp, issueContext, IssueCategory.CopyPaste,
											"Global parameter '" + oldMp.getName() + "' was non-numeric; it has been added " +
											"as global parameter '" + newMpName + "' in the new model with value = 0.0. " +
											"Please update its value, if required, before using it.",
											Issue.SEVERITY_WARNING);
									issueVector.add(issue);
								}
							}
						}
					} // end - if newExpr.hasSymbol(ProxyParam)
				} // end for - oldKprps (old kinetic proxy parameters)
				// now if store <param names, new expression> in hashTable
				if (paramExprHash.get(oldKps[i].getName()) == null) {
					paramExprHash.put(oldKps[i].getName(), newExpression);
				}
			} // end for - oldKps (old kinetic parameters)

			// use this new expression to generate 'vcml' for the (new) kinetics (easier way to transfer all kinetic parameters)
			String newKineticsStr = oldKinetics.writeTokensWithReplacingProxyParams(paramExprHash);
			// convert the kinetics 'vcml' to tokens.
			CommentStringTokenizer kineticsTokens = new CommentStringTokenizer(newKineticsStr);
			// skip the first token; 
			kineticsTokens.nextToken();
			// second token is the kinetic type; use this to create a dummy kinetics
			String kineticType = kineticsTokens.nextToken();
			Kinetics newkinetics = KineticsDescription.fromVCMLKineticsName(kineticType).createKinetics(newReactionStep);
			// use the remaining tokens to construct the new kinetics
			newkinetics.fromTokens(newKineticsStr);
			// bind newkinetics to newReactionStep and add it to newReactionStep 
			newkinetics.bind(newReactionStep);
			newReactionStep.setKinetics(newkinetics);

			counter+= 1;
			if(counter == copyFromRxSteps.length){
				break;
			}

			if(!copiedStructName.equals(fromRxnStruct.getName())){
				if(currentStruct instanceof Feature){
					currentStruct = structTopology.getMembrane((Feature)currentStruct);
				}else if (currentStruct instanceof Membrane){
					currentStruct = structTopology.getInsideFeature((Membrane)currentStruct);
				}
			}
			copiedStructName = fromRxnStruct.getName();
		}while(true);
		return new PasteHelper(issueVector, rxPartMapStructure,reactionsAndSpeciesContexts);
	}


	public static void changeName(UserResolvedRxElements userResolvedRxElements, SpeciesContext newSc, int j,Model model,String forceName) throws PropertyVetoException {
		String newName = null;
		if(forceName != null && forceName.trim().length()>0) {
			newName = forceName.trim();
		}else if(userResolvedRxElements.fromSpeciesContextArr[j].getName().endsWith(userResolvedRxElements.toStructureArr[j].getName())) {
			newName = userResolvedRxElements.fromSpeciesContextArr[j].getName();
		}else {
			newName = userResolvedRxElements.fromSpeciesContextArr[j].getName()+"_"+userResolvedRxElements.toStructureArr[j].getName();
		}
		while(!Model.isNameUnused(newName, model)) {
			if(newName.endsWith("_"+newSc.getStructure().getName())) {
				newName = newName+"_";
			}
			newName = TokenMangler.getNextEnumeratedToken(newName);
		}
		newSc.setName(newName);
	}

	private static Species getNewSpecies(
			IdentityHashMap<Species, Species> speciesHash,
			Species copyFromSpecies,
			Model pasteToModel,
			boolean bNew,/* boolean bUseDBSpecies,*/
			Structure pasteToStruct,
			HashMap<Structure, Species> userPreferredToTarget) {

		Species newSpecies = speciesHash.get(copyFromSpecies);
		if (newSpecies != null) {
			return newSpecies;
		} 

		Species preferredToSpecies = 
			(userPreferredToTarget != null?userPreferredToTarget.values().iterator().next():null);
		try {
			if(bNew || (userPreferredToTarget != null && preferredToSpecies == null)){
				String newName = copyFromSpecies.getCommonName();
				while(pasteToModel.getSpecies(newName) != null){
					newName = org.vcell.util.TokenMangler.getNextEnumeratedToken(newName);
				}
				newSpecies = new Species(newName,copyFromSpecies.getAnnotation(),copyFromSpecies.getDBSpecies());
				pasteToModel.addSpecies(newSpecies);
				speciesHash.put(copyFromSpecies, newSpecies);
			} else {
				//Find a matching existing species if possible
				if(preferredToSpecies != null){
					return preferredToSpecies;
				}
				if(!pasteToModel.contains(copyFromSpecies)){// Doesn't have Species (==)
					//see if we have a species with DBSpecies that matches
//					Species[] speciesFromDBSpeciesArr = (copyFromSpecies.getDBSpecies() != null ? pasteToModel.getSpecies(copyFromSpecies.getDBSpecies()) : null);
//					if(/*bUseDBSpecies && */speciesFromDBSpeciesArr != null && speciesFromDBSpeciesArr.length > 0){//DBSpecies match
//						//Choose the species in struct if exists
//						newSpecies = speciesFromDBSpeciesArr[0];
//						for(int i=0;i<speciesFromDBSpeciesArr.length;i+= 1){
//							if(pasteToModel.getSpeciesContext(speciesFromDBSpeciesArr[i], pasteToStruct) != null){
//								newSpecies = speciesFromDBSpeciesArr[i];
//								break;
//							}
//						}
//					}else{// No DBSpecies match
						//See if there is a species with same name
						newSpecies = pasteToModel.getSpecies(copyFromSpecies.getCommonName());
						if( newSpecies == null){//No name matches
							String newName = copyFromSpecies.getCommonName();
							newSpecies = new Species(newName,copyFromSpecies.getAnnotation(),copyFromSpecies.getDBSpecies());
							pasteToModel.addSpecies(newSpecies);
							speciesHash.put(copyFromSpecies, newSpecies);
						}
//					}
				}else{// Has species (==)
					newSpecies = copyFromSpecies;
				}
			}
		}  catch (PropertyVetoException e) {
			e.printStackTrace(System.out);
			throw new RuntimeException("Could not add species to model : " + e.getMessage());
		}

		return newSpecies;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (5/10/2003 3:55:25 PM)
	 * @param pasteToModel cbit.vcell.model.Model
	 * @param pasteToStruct cbit.vcell.model.Structure
	 * @param bNew boolean
	 */
	protected static final SpeciesContext pasteSpecies(
			Component parent,
			Species copyFromSpecies,
			String useThisSpeciesContextRootName,
			Model pasteToModel,
			Structure pasteToStruct0,
			boolean bNew,/*boolean bUseDBSpecies, */
			IdentityHashMap<Species, Species> speciesHash,
			HashMap<Structure, Species> userPreferredToTarget) {

		Structure preferredToStructure = (userPreferredToTarget != null?userPreferredToTarget.keySet().iterator().next():pasteToStruct0);

		if(!pasteToModel.contains(preferredToStructure)){
			throw new IllegalArgumentException("CartoonTool.pasteSpecies model '"+pasteToModel.getName()+"' does not contain structure "+(preferredToStructure==null?null:"'"+preferredToStructure.getName()+"'"));
		}

		Species newSpecies = null;
		if(copyFromSpecies != null){
			try {
				newSpecies = getNewSpecies(speciesHash, copyFromSpecies, pasteToModel, bNew,preferredToStructure,userPreferredToTarget);
				//see if we have SpeciesContext
				SpeciesContext speciesContext = pasteToModel.getSpeciesContext(newSpecies,preferredToStructure);
				if(speciesContext == null){ //Has Species but not SpeciesContext
					speciesContext = pasteToModel.addSpeciesContext(newSpecies,preferredToStructure);
					String newSpeciesContextName = newSpecies.getCommonName();
					if(useThisSpeciesContextRootName != null){
						newSpeciesContextName = useThisSpeciesContextRootName+"_"+speciesContext.getStructure().getName();
						while(pasteToModel.getSpeciesContext(newSpeciesContextName) != null){
							newSpeciesContextName = TokenMangler.getNextEnumeratedToken(newSpeciesContextName);
						}
					}
					speciesContext.setName(newSpeciesContextName);
				}
			}catch(Exception e){
				DialogUtils.showErrorDialog(parent, e.getMessage(), e);
			}
		}
		return pasteToModel.getSpeciesContext(newSpecies,preferredToStructure);
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (5/13/2003 8:26:55 PM)
	 * @return cbit.vcell.clientdb.DocumentManager
	 */
	public void setDocumentManager(cbit.vcell.clientdb.DocumentManager argDocumentManager) {
		this.documentManager = argDocumentManager;
	}
}

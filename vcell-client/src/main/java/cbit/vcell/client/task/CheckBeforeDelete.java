/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.task;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeSet;

import org.vcell.util.UserCancelException;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.Version;
import org.vcell.util.document.VersionFlag;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.UserMessage;
import cbit.vcell.client.task.CheckBeforeDelete.LOW_PRECISION_SAVE;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.MathDescription;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class CheckBeforeDelete extends AsynchClientTask {
	
	public CheckBeforeDelete() {
		super("Checking for successful save and possible lost results", TASKTYPE_NONSWING_BLOCKING);
	}


	public static enum LostFlag {DIFF,LOW_PRECISION,DIFF_AND_LOW_PRECISION};
/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 3:44:03 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo[]
 * @param bioModel cbit.vcell.biomodel.BioModel
 */
private HashMap<Simulation, LostFlag> checkLostResults(BioModel oldBioModel, BioModel newlySavedBioModel, cbit.vcell.clientdb.DocumentManager documentManager, Simulation submittedSimulations[]) throws Exception {
	//
	// before deleting old version, prompt user if old simulation results will not be availlable in new edition
	//
	HashMap<Simulation, LostFlag> lostSimResultsMap = new HashMap<>();
//	Vector<Simulation> lostResultsSimulationList = new Vector<Simulation>();
	Simulation oldSimulations[] = oldBioModel.getSimulations();
	for (int i = 0; i < oldSimulations.length; i++){
		Simulation oldSimulation = oldSimulations[i];
		SimulationStatus simStatus = null;
		SimulationInfo oldSimInfo = oldSimulation.getSimulationInfo();
		if (oldSimInfo!=null){
			//
			// we need to ask for previous sim results (here we need possible translation to ask for parent's results).
			//
			simStatus = documentManager.getServerSimulationStatus(oldSimInfo.getAuthoritativeVCSimulationIdentifier());
		}
		if (simStatus != null && simStatus.getHasData()) {
			//
			// results exist in old version (the BioModel to be deleted) for SimulationInfo "oldSimInfo"
			// Users should be warned when they are going to loose any simulation results in any unexpected way.
			//
			// WARN if the lost data is because new simulation is not mathematically equivalent to old edition
			//       (different MathDescription key)
			//
			// IGNORE if the lost data is from edits of a Simulation only (same MathDescription)
			//       (same MathDescription key, different Simulation key)
			//
			// IGNORE if Simulation has been deleted 
			//       (Simulation not found in current BioModel)
			//
			// IGNORE if Simulation has been submitted for running 
			//
			boolean bDataInNewEdition = false;
			Simulation newSimulations[] = newlySavedBioModel.getSimulations();
			Simulation correspondingSimulation = null;
			for (int j = 0; j < newSimulations.length; j++){
				if (newSimulations[j].getName().equals(oldSimulation.getName())){
					correspondingSimulation = newSimulations[j];
					if (correspondingSimulation.getKey().equals(oldSimulation.getKey())){
						//
						// exactly same simulation (same key), so no lost data
						//
						bDataInNewEdition = true;
					}else if (correspondingSimulation.getSimulationVersion().getParentSimulationReference()!=null){
						//
						// new simulation changed but points to same results
						//
						bDataInNewEdition = true;
					}
					break;
				}
			}
			if (!bDataInNewEdition && correspondingSimulation!=null){
				//
				// result set (for "rsInfo") will be lost, should we ignore this fact?
				//
				boolean bIgnore = false;

				//
				// ignore if only Simulation has been edited (same MathDescription)
				//
				if (correspondingSimulation.getMathDescription().getKey().equals(oldSimulation.getMathDescription().getKey())){
					bIgnore = true;
				}

				//
				// ignore if new simulation has been submitted for running (who cares if the old data is lost)
				//
				for (int j = 0; submittedSimulations!=null && j < submittedSimulations.length; j++){
					if (correspondingSimulation.getName().equals(submittedSimulations[j].getName())){
						bIgnore = true;
					}
				}

				//
				// if don't ignore this data loss, then add results set to list of warnings
				//
				if (!bIgnore) {
					lostSimResultsMap.put(correspondingSimulation,LostFlag.DIFF);
				}
			}
			checkLowPrecisionChange(lostSimResultsMap,correspondingSimulation,oldSimulation);
		}
	}
	return lostSimResultsMap;
}

public static void checkLowPrecisionChange(HashMap<Simulation, LostFlag> lostSimResultsMap,Simulation correspondingSimulation,Simulation oldSimulation) {
	Long bmKey = null;
	if(oldSimulation.getSimulationOwner() instanceof SimulationContext && ((SimulationContext)oldSimulation.getSimulationOwner()).getBioModel().getVersion() != null) {
		bmKey = new Long(((SimulationContext)oldSimulation.getSimulationOwner()).getBioModel().getVersion().getVersionKey().toString());
	}
	if(correspondingSimulation!=null &&
		oldSimulation.getMathDescription().getVersion() != null &&
		oldSimulation.getMathDescription().getVersion().getVersionKey() != null &&
		MathDescription.originalHasLowPrecisionConstants.containsKey(bmKey) &&
		MathDescription.originalHasLowPrecisionConstants.get(bmKey).contains(oldSimulation.getMathDescription().getVersion().getVersionKey().toString())
		) {
		if(lostSimResultsMap.get(correspondingSimulation) == LostFlag.DIFF ) {
			lostSimResultsMap.put(correspondingSimulation,LostFlag.DIFF_AND_LOW_PRECISION);
		}else {
			lostSimResultsMap.put(correspondingSimulation,LostFlag.LOW_PRECISION);
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 3:44:03 PM)
 * @return cbit.vcell.solver.SolverResultSetInfo[]
 * @param mathmodel cbit.vcell.mathmodel.Mathmodel
 */
private HashMap<Simulation, LostFlag> checkLostResults(MathModel oldMathmodel, MathModel newlySavedMathmodel, cbit.vcell.clientdb.DocumentManager documentManager, Simulation submittedSimulations[]) throws Exception {
	//
	// before deleting old version, prompt user if old simulation results will not be availlable in new edition
	//
	HashMap<Simulation, LostFlag> lostSimResultsMap = new HashMap<>();
	Simulation oldSimulations[] = oldMathmodel.getSimulations();
	for (int i = 0; i < oldSimulations.length; i++){
		Simulation oldSimulation = oldSimulations[i];
//		SolverResultSetInfo rsInfo = null;
		SimulationStatus simStatus = null;
		SimulationInfo oldSimInfo = oldSimulation.getSimulationInfo();
		if (oldSimInfo!=null){
			//
			// we need to ask for previous sim results (here we need possible translation to ask for parent's results).
			//
			simStatus = documentManager.getServerSimulationStatus(oldSimInfo.getAuthoritativeVCSimulationIdentifier());
		}
		if (simStatus != null && simStatus.getHasData()) {
			//
			// results exist in old version (the Mathmodel to be deleted) for SimulationInfo "oldSimInfo"
			// Users should be warned when they are going to loose any simulation results in any unexpected way.
			//
			// WARN if the lost data is because new simulation is not mathematically equivalent to old edition
			//       (different MathDescription key)
			//
			// IGNORE if the lost data is from edits of a Simulation only (same MathDescription)
			//       (same MathDescription key, different Simulation key)
			//
			// IGNORE if Simulation has been deleted 
			//       (Simulation not found in current Mathmodel)
			//
			// IGNORE if Simulation has been submitted for running 
			//
			boolean bDataInNewEdition = false;
			Simulation newSimulations[] = newlySavedMathmodel.getSimulations();
			Simulation correspondingSimulation = null;
			for (int j = 0; j < newSimulations.length; j++){
				if (newSimulations[j].getName().equals(oldSimulation.getName())){
					correspondingSimulation = newSimulations[j];
					if (correspondingSimulation.getKey().equals(oldSimulation.getKey())){
						//
						// exactly same simulation (same key), so no lost data
						//
						bDataInNewEdition = true;
					}else if (correspondingSimulation.getSimulationVersion().getParentSimulationReference()!=null){
						//
						// new simulation changed but points to same results
						//
						bDataInNewEdition = true;
					}
					break;
				}
			}
			if (!bDataInNewEdition && correspondingSimulation!=null){
				//
				// result set (for "rsInfo") will be lost, should we ignore this fact?
				//
				boolean bIgnore = false;

				//
				// ignore if only Simulation has been edited (same MathDescription)
				//
				MathDescription mdCorr = correspondingSimulation.getMathDescription();
				MathDescription mdOld = oldSimulation.getMathDescription();
				if (mdCorr.getKey( ).equals(mdOld.getKey())) {
					bIgnore = true;
				}

				//
				// ignore if new simulation has been submitted for running (who cares if the old data is lost)
				//
				for (int j = 0; submittedSimulations!=null && j < submittedSimulations.length; j++){
					if (correspondingSimulation.getName().equals(submittedSimulations[j].getName())){
						bIgnore = true;
					}
				}

				//
				// if don't ignore this data loss, then add results set to list of warnings
				//
				if (!bIgnore) {
					lostSimResultsMap.put(correspondingSimulation,LostFlag.DIFF);
				}
			}
		}
	}
	return lostSimResultsMap;
}

//public static TreeSet<String> alreadyAskedAboutLostResults = new TreeSet<>();
public enum LOW_PRECISION_SAVE {KEEPOLDRESULTS,ASNEW};
/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws Exception {
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get(CommonTask.DOCUMENT_WINDOW_MANAGER.name);
//	JFrame currentDocumentWindow = (JFrame)hashTable.get("currentDocumentWindow");
	VCDocument currentDocument = documentWindowManager.getVCDocument();
	if (! hashTable.containsKey(SaveDocument.DOC_KEY)) {
		throw new RuntimeException("CheckBeforeDelete task called although SaveDocument task did not complete - should have aborted!!");
	}

	//Don't delete ARCHIVE or PUBLISH  documents
	if(!currentDocument.getVersion().getFlag().compareEqual(VersionFlag.Current)){
		hashTable.put("conditionalSkip", new String[] {DeleteOldDocument.class.getName()});
		return;
	}

	
	DocumentManager documentManager = (DocumentManager)hashTable.get(CommonTask.DOCUMENT_MANAGER.name);
	Simulation simulations[] = (Simulation[])hashTable.get("simulations");
	VCDocument savedDocument = (VCDocument)hashTable.get(SaveDocument.DOC_KEY);
	// just to make sure, verify that we actually did save a new edition
	Version oldVersion = currentDocument.getVersion();
	Version newVersion = savedDocument.getVersion();
	final boolean bNewDocumentWasNotSaved = newVersion.getVersionKey().compareEqual(oldVersion.getVersionKey());
	if (bNewDocumentWasNotSaved) {
		//throw new DataAccessException("CheckBeforeDelete task called but saved document has same version with current document");
		hashTable.put("conditionalSkip", new String[] {DeleteOldDocument.class.getName()});
		hashTable.remove(SaveDocument.DOC_KEY);
//		return;
	}
	// we saved a new one, now check for lost simulation data and warn the user
	HashMap<Simulation, LostFlag> simulationsWithLostResults = null;
	switch (currentDocument.getDocumentType()) {
		case BIOMODEL_DOC: {
			simulationsWithLostResults = checkLostResults((BioModel)currentDocument, (BioModel)savedDocument, documentManager, simulations);
			break;
		}
		case MATHMODEL_DOC: {
			simulationsWithLostResults = checkLostResults((MathModel)currentDocument, (MathModel)savedDocument, documentManager, simulations);
			break;
		}
		case GEOMETRY_DOC: 
		default:
			return; // nothing to check for in this case
	}

	boolean bLost = simulationsWithLostResults != null && simulationsWithLostResults.size() > 0;
	if (bLost) {
		UserMessage userMessage = UserMessage.question_LostResults;
		if(!simulationsWithLostResults.values().contains(LostFlag.DIFF) &&  simulationsWithLostResults.values().contains(LostFlag.LOW_PRECISION) && !simulationsWithLostResults.values().contains(LostFlag.DIFF_AND_LOW_PRECISION)) {
			userMessage = UserMessage.question_LostResultsLowPrecision;
//			if(alreadyAskedAboutLostResults.remove(currentDocument.getVersion().getVersionKey().toString())) {
//				hashTable.remove("conditionalSkip");
//				return;
//			}
		}
//		else if(!simulationsWithLostResults.values().contains(LostFlag.DIFF) && !simulationsWithLostResults.values().contains(LostFlag.LOW_PRECISION) &&  simulationsWithLostResults.values().contains(LostFlag.DIFF_AND_LOW_PRECISION)) {
//			userMessage = new UserMessage(UserMessage.question_LostResults.getMessage(null)+",also "+s, UserMessage.question_LostResults.getOptions(), UserMessage.question_LostResults.getDefaultSelection());			
//		}
		String choice = PopupGenerator.showWarningDialog(documentWindowManager, documentWindowManager.getUserPreferences(), userMessage, null);
		if (choice.equals(UserMessage.OPTION_SAVE_AS_NEW)){// from UserMessage.question_LostResultsLowPrecision
			//Delete the NEW document that was created on the server by default during save because we will save again with new name
			//Clear simulation versions on current document so when we SaveAs... new document will have no sim results because
			//we are re-saving as a result of lowPrecisionConstants
//			if(currentDocument instanceof BioModel) {
//				if(!bNewDocumentWasNotSaved) {documentWindowManager.getRequestManager().deleteDocument(documentWindowManager.getRequestManager().getDocumentManager().getBioModelInfo(savedDocument.getVersion().getVersionKey()), documentWindowManager,true);}
//				for (int i = 0; i < ((BioModel)currentDocument).getSimulations().length; i++) {
//					((BioModel)currentDocument).getSimulations()[i].clearVersion();
//				}
//			}
//			else if(currentDocument instanceof MathModel) {
//				if(!bNewDocumentWasNotSaved) {documentWindowManager.getRequestManager().deleteDocument(documentWindowManager.getRequestManager().getDocumentManager().getMathModelInfo(savedDocument.getVersion().getVersionKey()), documentWindowManager,true);}			
//				for (int i = 0; i < ((MathModel)currentDocument).getSimulations().length; i++) {
//					((MathModel)currentDocument).getSimulations()[i].clearVersion();
//				}
//			}
			hashTable.put(LOW_PRECISION_SAVE.class.getName(), CheckBeforeDelete.LOW_PRECISION_SAVE.ASNEW);
//			documentWindowManager.saveDocumentAsNew();
//			hashTable.put(SaveDocument.DOC_KEY, documentWindowManager.getVCDocument());
//			hashTable.put("conditionalSkip", new String[] {FinishSave.class.getName(),DeleteOldDocument.class.getName()});
		}else if (choice.equals(UserMessage.OPTION_CANCEL) ) {// from UserMessage.question_LostResults or UserMessage.question_LostResultsLowPrecision
			//Delete the NEW document that was created on the server by default during save because user doesn't want it,  don't delete original
			if(currentDocument instanceof BioModel) {
				if(!bNewDocumentWasNotSaved) {documentWindowManager.getRequestManager().deleteDocument(documentWindowManager.getRequestManager().getDocumentManager().getBioModelInfo(savedDocument.getVersion().getVersionKey()), documentWindowManager,true);}
			}else if(currentDocument instanceof MathModel) {
				if(!bNewDocumentWasNotSaved) {documentWindowManager.getRequestManager().deleteDocument(documentWindowManager.getRequestManager().getDocumentManager().getMathModelInfo(savedDocument.getVersion().getVersionKey()), documentWindowManager,true);}			
			}
			throw UserCancelException.CANCEL_GENERIC;
		}else if (choice.equals(UserMessage.OPTION_SAVE_AS_NEW_EDITION) ) {// from UserMessage.question_LostResults
			//NEW document has already been saved, prevent DeleteOldDocument from removing original doc
			hashTable.put("conditionalSkip", new String[] {DeleteOldDocument.class.getName()});
//			hashTable.put(SaveDocument.DOC_KEY, savedDocument);
			if (bNewDocumentWasNotSaved) {
				throw new Exception(this.getClass().getName()+" new document should have been saved");
//				documentWindowManager.saveDocument(false);
//				hashTable.put(SaveDocument.DOC_KEY, documentWindowManager.getVCDocument());
			}
		}else if (choice.equals(UserMessage.OPTION_DISCARD_RESULTS) ) {// from UserMessage.question_LostResults
			//do nothing here, NEW document has already been saved and original doc will be deleted
			if (bNewDocumentWasNotSaved) {
				throw new Exception(this.getClass().getName()+" detected real changes but new document was not saved");
				//hashTable.put("conditionalSkip", new String[] {DeleteOldDocument.class.getName()});
			}else {
				hashTable.remove("conditionalSkip");
			}
		}else if (choice.equals(UserMessage.OPTION_KEEP_OLD_RESULTS) ) {// from UserMessage.question_LostResultsLowPrecision
			//do nothing here, NEW document has already been saved and original doc will be deleted
			if (bNewDocumentWasNotSaved) {
//				documentWindowManager.getVCDocument().setDescription(documentWindowManager.getVCDocument().getDescription()+" ");
//				alreadyAskedAboutLostResults.add(currentDocument.getVersion().getVersionKey().toString());
				hashTable.put(LOW_PRECISION_SAVE.class.getName(), CheckBeforeDelete.LOW_PRECISION_SAVE.KEEPOLDRESULTS);
//				documentWindowManager.saveDocument(true);
//				hashTable.put("conditionalSkip", new String[] {FinishSave.class.getName(),DeleteOldDocument.class.getName()});
			}else {
				hashTable.remove("conditionalSkip");
			}
		}
	}else { 
		if (bNewDocumentWasNotSaved) {
			hashTable.put("conditionalSkip", new String[] {DeleteOldDocument.class.getName()});
		}
//		else {
//			hashTable.put(SaveDocument.DOC_KEY, savedDocument);
//		}
	}
	
}

public static AsynchClientTask getLowPrecisionConstantsNewNameTask() {
	return new AsynchClientTask("LowPrecisionConstant New Name...",AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if(hashTable.get(LOW_PRECISION_SAVE.class.getName()) != null && ((CheckBeforeDelete.LOW_PRECISION_SAVE)hashTable.get(LOW_PRECISION_SAVE.class.getName()) == LOW_PRECISION_SAVE.ASNEW)) {
				DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get(CommonTask.DOCUMENT_WINDOW_MANAGER.name);
				VCDocument currentDocument = documentWindowManager.getVCDocument();
				if(hashTable.get("newName") != null) {
					throw new Exception("Unexpected: New Name for saved document has already been defined elsewhere");
				}
				try {
					(new NewName()).run(hashTable);
				} catch (UserCancelException e) {
					VCDocument savedDocument = (VCDocument)hashTable.get(SaveDocument.DOC_KEY);
					if(savedDocument != null) {
						boolean bNewDocumentWasNotSaved = savedDocument.getVersion().getVersionKey().compareEqual(currentDocument.getVersion().getVersionKey());
						if(!bNewDocumentWasNotSaved) {//can only be BioModels with LowPrecisionCheck
							documentWindowManager.getRequestManager().deleteDocument(
								documentWindowManager.getRequestManager().getDocumentManager().getBioModelInfo(
									savedDocument.getVersion().getVersionKey()), documentWindowManager,true);
						}
					}
					throw e;
				}
			}
		}
	};
}
public static AsynchClientTask getLowPrecisionConstantsSaveTask() {
	return new AsynchClientTask("LowPrecisionConstant Save...",AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
		@Override
		public void run(Hashtable<String, Object> hashTable) throws Exception {
			if(hashTable.get(LOW_PRECISION_SAVE.class.getName()) != null) {
				hashTable.put("conditionalSkip", new String[] {DeleteOldDocument.class.getName()});
//				Simulation[] storedSims = (Simulation[])hashTable.get("simulations");
				if(((CheckBeforeDelete.LOW_PRECISION_SAVE)hashTable.get(LOW_PRECISION_SAVE.class.getName()) == LOW_PRECISION_SAVE.ASNEW)) {
//					hashTable.put("simulations", ((BioModel)currentDocument).getSimulations());
					//If LOWPRECISION saveASNew then clear sim results
//					try {
//						if(true) {throw new Exception("test");}
						//Save new document
						(new SaveDocument(false)).run(hashTable);
						//delete sim versions so they have to be re-run to use new HighPrecisionConstants
						//and re-save the new document
						BioModel lpcDoc = (BioModel)hashTable.remove(SaveDocument.DOC_KEY);
						hashTable.remove("newName");
						for (int i = 0; i < ((BioModel)lpcDoc).getSimulations().length; i++) {
							((BioModel)lpcDoc).getSimulations()[i].clearVersion();
						}
						Hashtable<String, Object> delSimVersionHash = new Hashtable<>();
						final DocumentWindowManager dwm = (DocumentWindowManager)hashTable.get(CommonTask.DOCUMENT_WINDOW_MANAGER.name);
						delSimVersionHash.put(CommonTask.DOCUMENT_MANAGER.name,dwm.getRequestManager().getDocumentManager());
						delSimVersionHash.put(CommonTask.DOCUMENT_WINDOW_MANAGER.name, hashTable.get(CommonTask.DOCUMENT_WINDOW_MANAGER.name));
						delSimVersionHash.put("LOW_PRECISION_CONSTANT_DOC", lpcDoc);
						//Save doc with sims that have cleared versions
						(new SaveDocument(false)).run(delSimVersionHash);
						hashTable.put(SaveDocument.DOC_KEY, delSimVersionHash.get(SaveDocument.DOC_KEY));
						//Remove previous new doc with uncleared sim versions
						if(!lpcDoc.getVersion().getVersionKey().equals(delSimVersionHash.get(SaveDocument.DOC_KEY))) {
							dwm.getRequestManager().deleteDocument(
								dwm.getRequestManager().getDocumentManager().getBioModelInfo(lpcDoc.getVersion().getVersionKey()), dwm,true);
						}
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
////						documentWindowManager.resetDocument(XmlHelper.XMLToBioModel(new XMLSource(originalCurrentDocStr)));
//						throw e;
//					}
				}else {
					try {
//						if(true) {throw new Exception("test");}
						(new SaveDocument(false)).run(hashTable);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw e;
					}

				}
			}
		}
	};
}
}

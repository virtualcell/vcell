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
import java.util.Hashtable;

import org.vcell.util.ServerRejectedSaveException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VersionableType;
import org.vcell.util.document.VersionableTypeVersion;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.MathModelWindowManager;
import cbit.vcell.client.RequestManager;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.solver.Simulation;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class SaveDocument extends AsynchClientTask {
	
	public static final String DOC_KEY = "savedDocument";
	private boolean bFailIfServerRejectSave = false;
	public SaveDocument(boolean bFailIfServerRejectSave) {
		super("Saving document to database", TASKTYPE_NONSWING_BLOCKING);
		this.bFailIfServerRejectSave = bFailIfServerRejectSave;
	}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	long l1 = System.currentTimeMillis();
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get(CommonTask.DOCUMENT_WINDOW_MANAGER.name);
	VCDocument currentDocument = documentWindowManager.getVCDocument();
	DocumentManager documentManager = (DocumentManager)hashTable.get(CommonTask.DOCUMENT_MANAGER.name);
	RequestManager requestManager = (RequestManager)hashTable.get("requestManager");
	boolean bAsNew = hashTable.containsKey("newName");
	String newName = bAsNew ? (String)hashTable.get("newName") : null;
	Simulation simulationsToRun[] = (Simulation[])hashTable.get("simulations");
	String independentSims[] = null;
	if (simulationsToRun!=null && simulationsToRun.length>0){
		independentSims = new String[simulationsToRun.length];
		for (int i = 0; i < simulationsToRun.length; i++){
			independentSims[i] = simulationsToRun[i].getName();
		}
	}
	VCDocument savedDocument = null;
	switch (currentDocument.getDocumentType()) {
		case BIOMODEL_DOC: {
			if (bAsNew) {
//				Substitute Field Func Names-----
				VersionableTypeVersion originalVersionableTypeVersion = null;
				if(currentDocument.getVersion() != null){//From Opened...
					originalVersionableTypeVersion =
						new VersionableTypeVersion(
								VersionableType.BioModelMetaData,
								currentDocument.getVersion());
				}
				documentManager.substituteFieldFuncNames(
						(BioModel)currentDocument,originalVersionableTypeVersion);
//				--------------------------------
				savedDocument = documentManager.saveAsNew((BioModel)currentDocument, newName, independentSims);
			} else {
				try {
					savedDocument = documentManager.save((BioModel)currentDocument, independentSims);
				}catch(ServerRejectedSaveException srse) {
					if(bFailIfServerRejectSave) {
						throw srse;
					}
					srse.printStackTrace();
					savedDocument = currentDocument;
				}
			}
			break;
		}
		case MATHMODEL_DOC: {
			if (bAsNew) {
//				Substitute Field Func Names-----
				VersionableTypeVersion originalVersionableTypeVersion =
					((MathModelWindowManager)documentWindowManager).getCopyFromBioModelAppVersionableTypeVersion();
				if(originalVersionableTypeVersion == null && currentDocument.getVersion() != null){//From Opened...
					originalVersionableTypeVersion =
						new VersionableTypeVersion(
								VersionableType.MathModelMetaData,
								currentDocument.getVersion());
				}
				documentManager.substituteFieldFuncNames(
						(MathModel)currentDocument,originalVersionableTypeVersion);
				//--------------------------------
				savedDocument = documentManager.saveAsNew((MathModel)currentDocument, newName, independentSims);
			} else {
				try {
					savedDocument = documentManager.save((MathModel)currentDocument, independentSims);
				}catch(ServerRejectedSaveException srse) {
					if(bFailIfServerRejectSave) {
						throw srse;
					}
					srse.printStackTrace();
					savedDocument = currentDocument;
				}
			}
			break;
		}
		case GEOMETRY_DOC: {
			if (bAsNew) {
				savedDocument = documentManager.saveAsNew((Geometry)currentDocument, newName);
			} else {
				savedDocument = documentManager.save((Geometry)currentDocument);
			}
			break;
		}
		default:{
			throw new RuntimeException("unexpected document type "+currentDocument.getDocumentType().name());
		}
	}
	documentWindowManager.prepareDocumentToLoad(savedDocument, false);
	
	hashTable.put(SaveDocument.DOC_KEY, savedDocument);
	
	// generate PerformanceMonitorEvent
	long l2 = System.currentTimeMillis();
	double duration = ((double)(l2 - l1)) / 1000;
//	requestManager.getAsynchMessageManager().reportPerformanceMonitorEvent(
//	    new PerformanceMonitorEvent(
//		    this, documentManager.getUser(), new PerformanceData(
//			    "SaveDocument.run()",
//			    MessageEvent.SAVING_STAT,
//			    new PerformanceDataEntry[] {
//				    new PerformanceDataEntry("document saved", savedDocument.getName()),
//				    new PerformanceDataEntry("remote call duration", Double.toString(duration))
//				    }
//		    )
//    	)
//	);

}

}

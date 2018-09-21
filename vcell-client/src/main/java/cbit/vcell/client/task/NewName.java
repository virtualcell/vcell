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

import java.awt.Component;
import java.util.Hashtable;

import org.vcell.util.UserCancelException;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocument.VCDocumentType;
import org.vcell.util.document.VCDocumentInfo;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.MDIManager;
import cbit.vcell.client.MathModelWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.clientdb.DocumentManager;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class NewName extends AsynchClientTask {
	
	public NewName() {
		super("Getting document name", TASKTYPE_SWING_BLOCKING);
	}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get(CommonTask.DOCUMENT_WINDOW_MANAGER.name);
	if(documentWindowManager.getUser() == null || User.isGuest(documentWindowManager.getUser().getName())){
		throw new IllegalArgumentException(User.createGuestErrorMessage("saveNewDocument"));
	}
	VCDocument document = documentWindowManager.getVCDocument();
	if (document.getDocumentType() == VCDocumentType.MATHMODEL_DOC) {
		if (((MathModelWindowManager)documentWindowManager).hasUnappliedChanges()) {
			String msg = "Changes have been made in VCML Editor, please click \"Apply Changes\" or \"Cancel\" to proceed.";
			PopupGenerator.showErrorDialog(documentWindowManager, msg);
			throw UserCancelException.CANCEL_UNAPPLIED_CHANGES;			
		}
	}

	MDIManager mdiManager = (MDIManager)hashTable.get("mdiManager");
	String oldName = document.getName();
	
	User user = mdiManager.getFocusedWindowManager().getRequestManager().getDocumentManager().getUser();
	DocumentManager documentManager = mdiManager.getFocusedWindowManager().getRequestManager().getDocumentManager();
	VCDocumentInfo[] vcDocumentInfos = new VCDocumentInfo[0];
	String documentTypeDescription = "unknown";
	if(document.getDocumentType() == VCDocumentType.MATHMODEL_DOC){
		documentTypeDescription = "MathModel";
		vcDocumentInfos = documentManager.getMathModelInfos();
	}else if(document.getDocumentType() == VCDocumentType.BIOMODEL_DOC){
		documentTypeDescription = "BioModel";
		vcDocumentInfos = documentManager.getBioModelInfos();
	}else if(document.getDocumentType() == VCDocumentType.GEOMETRY_DOC){
		documentTypeDescription = "Geometry";
		vcDocumentInfos = documentManager.getGeometryInfos();
	}
	String newDocumentName = (oldName==null?"New"+documentTypeDescription:oldName);
	while(true){
		newDocumentName =
			mdiManager.getDatabaseWindowManager().showSaveDialog(
					document.getDocumentType(),
					(Component)hashTable.get("currentDocumentWindow"),
					newDocumentName);
			if (newDocumentName == null || newDocumentName.trim().length()==0){
				newDocumentName = null;
				DialogUtils.showWarningDialog(
						(Component)hashTable.get("currentDocumentWindow"),
						"New "+documentTypeDescription+" name cannot be empty.");
				continue;
			}
			//Check name conflict
			boolean bNameConflict = false;
			for (int i = 0; i < vcDocumentInfos.length; i++) {
				if(vcDocumentInfos[i].getVersion().getOwner().compareEqual(user)){
					if(vcDocumentInfos[i].getVersion().getName().equals(newDocumentName)){
						bNameConflict = true;
						break;
					}
				}
			}
			if(bNameConflict){
				DialogUtils.showWarningDialog((Component)hashTable.get("currentDocumentWindow"),
				"A "+documentTypeDescription+" with name '"+newDocumentName+"' already exists.  Choose a different name.");
				continue;
			}else{
				break;
			}
		}
	hashTable.put("newName", newDocumentName);
}

}

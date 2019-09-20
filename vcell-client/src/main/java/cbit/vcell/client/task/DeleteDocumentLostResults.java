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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

import org.vcell.util.UserCancelException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VCDocument;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mathmodel.MathModel;

/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class DeleteDocumentLostResults extends AsynchClientTask implements CommonTask {
	public static final String B_DELETE_NEW_LOST_RESULTS_CANCEL = "B_DELETE_NEW_LOST_RESULTS";
public DeleteDocumentLostResults() {
		super("Deleting old document from database", TASKTYPE_NONSWING_BLOCKING);
	}


@Override
protected Collection<KeyInfo> requiredKeys() {
	ArrayList<KeyInfo> rval = new ArrayList<>();
	rval.add(DOCUMENT_WINDOW_MANAGER);
	rval.add(DOCUMENT_MANAGER);
	return rval;
}


@Override
public boolean skipIfCancel(UserCancelException exc) {
	return !exc.equals(UserCancelException.CANCEL_LOST_RESULTS);
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	VCDocument currentDocument = (VCDocument)hashTable.get(SaveDocument.DOC_KEY);
	if(currentDocument == null) {
		return;
	}
	hashTable.put(B_DELETE_NEW_LOST_RESULTS_CANCEL, new Boolean(true));
	DocumentManager documentManager = (DocumentManager)fetch(hashTable,DOCUMENT_MANAGER);
	switch (currentDocument.getDocumentType()) {
		case BIOMODEL_DOC: {
			documentManager.delete(documentManager.getBioModelInfo(currentDocument.getVersion().getVersionKey()));
			break;
		}
		case MATHMODEL_DOC: {
			documentManager.delete(documentManager.getMathModelInfo(currentDocument.getVersion().getVersionKey()));
			break;
		}
	default:
		break;
	}
}

}

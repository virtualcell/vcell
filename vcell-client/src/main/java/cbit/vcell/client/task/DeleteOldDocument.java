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
public class DeleteOldDocument extends AsynchClientTask implements CommonTask {
	
public DeleteOldDocument() {
		super("Deleting old document from database", TASKTYPE_NONSWING_BLOCKING);
	}


@Override
protected Collection<KeyInfo> requiredKeys() {
	ArrayList<KeyInfo> rval = new ArrayList<>();
	rval.add(DOCUMENT_WINDOW_MANAGER);
	rval.add(DOCUMENT_MANAGER);
	return rval;
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	DocumentWindowManager dwm = (DocumentWindowManager) fetch(hashTable,DOCUMENT_WINDOW_MANAGER);
	VCDocument currentDocument = dwm.getVCDocument();
	DocumentManager documentManager = (DocumentManager)fetch(hashTable,DOCUMENT_MANAGER);
	switch (currentDocument.getDocumentType()) {
		case BIOMODEL_DOC: {
			// make the info
			BioModel oldBioModel = (BioModel)currentDocument;
			BioModelInfo oldBioModelInfo = documentManager.getBioModelInfo(oldBioModel.getVersion().getVersionKey());
			// delete document
			documentManager.delete(oldBioModelInfo);
			break;
		}
		case MATHMODEL_DOC: {
			// make the info
			MathModel oldMathModel = (MathModel)currentDocument;
			MathModelInfo oldMathModelInfo = documentManager.getMathModelInfo(oldMathModel.getVersion().getVersionKey());
			// delete document
			documentManager.delete(oldMathModelInfo);
			break;
		}
		case GEOMETRY_DOC: {
			// make the info
			Geometry oldGeometry = (Geometry)currentDocument;
			GeometryInfo oldGeometryInfo = documentManager.getGeometryInfo(oldGeometry.getVersion().getVersionKey());
			// delete document
			documentManager.delete(oldGeometryInfo);
			break;
		}
	default:
		break;
	}
}

}

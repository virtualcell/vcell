package cbit.vcell.client.task;
import cbit.util.UserCancelException;
import cbit.util.document.BioModelInfo;
import cbit.util.document.MathModelInfo;
import cbit.util.document.VCDocument;
import cbit.util.document.VCDocumentInfo;
import cbit.vcell.geometry.*;
import cbit.vcell.mathmodel.*;
import cbit.vcell.client.*;
import java.util.*;

import cbit.vcell.client.database.DocumentManager;
import cbit.vcell.client.desktop.*;
import cbit.vcell.mapping.*;
import cbit.vcell.math.*;
import cbit.vcell.biomodel.*;
import cbit.vcell.desktop.controls.*;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class DocumentToExport extends AsynchClientTask {
/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return java.lang.String
 */
public java.lang.String getTaskName() {
	return "Fetching document to be exported";
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return int
 */
public int getTaskType() {
	return TASKTYPE_NONSWING_BLOCKING;
}


/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(java.util.Hashtable hashTable) throws java.lang.Exception {
	TopLevelWindowManager topLevelWindowManager = (TopLevelWindowManager)hashTable.get("topLevelWindowManager");
	DocumentManager documentManager = (DocumentManager)hashTable.get("documentManager");
	VCDocument doc = null;
	if (topLevelWindowManager instanceof DocumentWindowManager) {
		doc = ((DocumentWindowManager)topLevelWindowManager).getVCDocument();
	} else if (topLevelWindowManager instanceof DatabaseWindowManager) {
		VCDocumentInfo documentInfo = ((DatabaseWindowManager)topLevelWindowManager).getPanelSelection();
		if (documentInfo instanceof BioModelInfo) {
			BioModelInfo bmi = (BioModelInfo)documentInfo;
			doc = documentManager.getBioModel(bmi);
		} else if (documentInfo instanceof MathModelInfo) {
			MathModelInfo mmi = (MathModelInfo)documentInfo;
			doc = documentManager.getMathModel(mmi);
		} else if (documentInfo instanceof GeometryInfo) {
			GeometryInfo gmi = (GeometryInfo)documentInfo;
			doc = documentManager.getGeometry(gmi);
		}
	}
	hashTable.put("documentToExport", doc);
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 8:44:12 PM)
 * @return boolean
 */
public boolean skipIfAbort() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 4:39:26 PM)
 * @return boolean
 */
public boolean skipIfCancel(UserCancelException exc) {
	return true;
}
}
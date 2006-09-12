package cbit.vcell.client.task;
import cbit.sql.*;
import cbit.util.*;
import cbit.vcell.client.*;
import cbit.vcell.client.desktop.*;
import cbit.vcell.clientdb.*;
import cbit.vcell.geometry.*;
import cbit.vcell.mathmodel.*;
import cbit.vcell.server.*;
import cbit.vcell.mapping.*;
import cbit.vcell.math.*;
import cbit.vcell.biomodel.*;
import cbit.vcell.desktop.controls.*;
import cbit.vcell.document.*;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class DeleteOldDocument extends AsynchClientTask {
/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return java.lang.String
 */
public java.lang.String getTaskName() {
	return "Deleting old document from database";
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
	VCDocument currentDocument = ((DocumentWindowManager)hashTable.get("documentWindowManager")).getVCDocument();
	DocumentManager documentManager = (DocumentManager)hashTable.get("documentManager");
	switch (currentDocument.getDocumentType()) {
		case VCDocument.BIOMODEL_DOC: {
			// make the info
			BioModel oldBioModel = (BioModel)currentDocument;
			BioModelInfo oldBioModelInfo = documentManager.getBioModelInfo(oldBioModel.getVersion().getVersionKey());
			// delete document
			documentManager.delete(oldBioModelInfo);
			break;
		}
		case VCDocument.MATHMODEL_DOC: {
			// make the info
			MathModel oldMathModel = (MathModel)currentDocument;
			MathModelInfo oldMathModelInfo = documentManager.getMathModelInfo(oldMathModel.getVersion().getVersionKey());
			// delete document
			documentManager.delete(oldMathModelInfo);
			break;
		}
		case VCDocument.GEOMETRY_DOC: {
			// make the info
			Geometry oldGeometry = (Geometry)currentDocument;
			GeometryInfo oldGeometryInfo = documentManager.getGeometryInfo(oldGeometry.getVersion().getVersionKey());
			// delete document
			documentManager.delete(oldGeometryInfo);
			break;
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 8:44:08 PM)
 * @return boolean
 */
public boolean skipIfAbort() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 4:38:57 PM)
 * @return boolean
 */
public boolean skipIfCancel(UserCancelException exc) {
	return true;
}
}
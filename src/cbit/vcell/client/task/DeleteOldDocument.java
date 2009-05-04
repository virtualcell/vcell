package cbit.vcell.client.task;
import java.util.Hashtable;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mathmodel.MathModel;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VCDocument;

/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class DeleteOldDocument extends AsynchClientTask {
	
public DeleteOldDocument() {
		super("Deleting old document from database", TASKTYPE_NONSWING_BLOCKING);
	}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
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

}
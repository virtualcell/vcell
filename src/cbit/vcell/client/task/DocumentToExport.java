package cbit.vcell.client.task;
import java.util.Hashtable;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;
import org.vcell.util.document.VCDocument;
import org.vcell.util.document.VCDocumentInfo;

import cbit.vcell.client.DatabaseWindowManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.TopLevelWindowManager;
import cbit.vcell.clientdb.DocumentManager;
import cbit.vcell.geometry.GeometryInfo;
/**
 * Insert the type's description here.
 * Creation date: (5/31/2004 6:03:16 PM)
 * @author: Ion Moraru
 */
public class DocumentToExport extends AsynchClientTask {
	public DocumentToExport() {
		super("Fetching document to be exported", TASKTYPE_NONSWING_BLOCKING);
	}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
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

}
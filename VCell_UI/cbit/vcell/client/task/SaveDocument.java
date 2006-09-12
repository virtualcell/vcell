package cbit.vcell.client.task;
import cbit.rmi.event.*;
import cbit.util.UserCancelException;
import cbit.util.VCDocument;
import cbit.vcell.geometry.surface.*;
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
public class SaveDocument extends AsynchClientTask {
/**
 * Insert the method's description here.
 * Creation date: (5/31/2004 6:04:14 PM)
 * @return java.lang.String
 */
public java.lang.String getTaskName() {
	return "Saving document to database";
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
	long l1 = System.currentTimeMillis();
	DocumentWindowManager documentWindowManager = (DocumentWindowManager)hashTable.get("documentWindowManager");
	VCDocument currentDocument = documentWindowManager.getVCDocument();
	DocumentManager documentManager = (DocumentManager)hashTable.get("documentManager");
	boolean bAsNew = hashTable.containsKey("newName");
	String newName = bAsNew ? (String)hashTable.get("newName") : null;
	cbit.vcell.solver.Simulation simulationsToRun[] = (cbit.vcell.solver.Simulation[])hashTable.get("simulations");
	String independentSims[] = null;
	if (simulationsToRun!=null && simulationsToRun.length>0){
		independentSims = new String[simulationsToRun.length];
		for (int i = 0; i < simulationsToRun.length; i++){
			independentSims[i] = simulationsToRun[i].getName();
		}
	}
	VCDocument savedDocument = null;
	switch (currentDocument.getDocumentType()) {
		case VCDocument.BIOMODEL_DOC: {
			if (bAsNew) {
				savedDocument = documentManager.saveAsNew((BioModel)currentDocument, newName, independentSims);
			} else {
				savedDocument = documentManager.save((BioModel)currentDocument, independentSims);
			}
			break;
		}
		case VCDocument.MATHMODEL_DOC: {
			if (bAsNew) {
				savedDocument = documentManager.saveAsNew((MathModel)currentDocument, newName, independentSims);
			} else {
				savedDocument = documentManager.save((MathModel)currentDocument, independentSims);
			}
			break;
		}
		case VCDocument.GEOMETRY_DOC: {
			if (bAsNew) {
				savedDocument = documentManager.saveAsNew((Geometry)currentDocument, newName);
			} else {
				savedDocument = documentManager.save((Geometry)currentDocument);
			}
			break;
		}
	}
	hashTable.put("savedDocument", savedDocument);
	// generate PerformanceMonitorEvent
	long l2 = System.currentTimeMillis();
	double duration = ((double)(l2 - l1)) / 1000;
	documentWindowManager.performanceMonitorEvent(
	    new PerformanceMonitorEvent(
		    this, documentManager.getUser(), new PerformanceData(
			    "SaveDocument.run()",
			    MessageEvent.SAVING_STAT,
			    new PerformanceDataEntry[] {
				    new PerformanceDataEntry("document saved", savedDocument.getName()),
				    new PerformanceDataEntry("remote call duration", Double.toString(duration))
				    }
		    )
    	)
	);

}


/**
 * Insert the method's description here.
 * Creation date: (6/1/2004 8:44:23 PM)
 * @return boolean
 */
public boolean skipIfAbort() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (6/8/2004 4:41:05 PM)
 * @return boolean
 */
public boolean skipIfCancel(UserCancelException exc) {
	return true;
}
}
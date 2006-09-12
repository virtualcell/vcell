package cbit.vcell.geometry.gui;
import cbit.vcell.geometry.surface.GeometrySurfaceDescription;
/**
 * Insert the type's description here.
 * Creation date: (9/13/2004 4:41:33 PM)
 * @author: Jim Schaff
 */
public class SurfaceGenerationTask extends cbit.vcell.desktop.controls.AsynchClientTask {
/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 4:41:33 PM)
 * @return java.lang.String
 */
public String getTaskName() {
	return "creating new smoothed surface";
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 4:41:33 PM)
 * @return int
 */
public int getTaskType() {
	return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_NONSWING_BLOCKING;
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 4:41:33 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(java.util.Hashtable hashTable) throws Exception {
	GeometrySurfaceDescription geometrySurfaceDescription = (GeometrySurfaceDescription)hashTable.get("geometrySurfaceDescription");
	geometrySurfaceDescription.updateAll();
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 4:41:33 PM)
 * @return boolean
 */
public boolean skipIfAbort() {
	return true;
}


/**
 * Insert the method's description here.
 * Creation date: (9/13/2004 4:41:33 PM)
 * @return boolean
 */
public boolean skipIfCancel(cbit.util.UserCancelException exc) {
	return true;
}
}
package cbit.vcell.client.task;

//import cbit.vcell.numericstest.TestSuiteInfoNew;
//import cbit.vcell.clientdb.DocumentManager;
//import cbit.vcell.numericstest.AddTestSuiteOP;
import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.numericstest.TestSuiteInfoNew;
//import cbit.vcell.client.RequestManager;
import cbit.util.AsynchProgressPopup;
import cbit.util.DataAccessException;
/**
 * Insert the type's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @author: Frank Morgan
 */
public class TFUpdateRunningStatus extends AsynchClientTask {

	private TestingFrameworkWindowManager tfwm;
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFUpdateRunningStatus(TestingFrameworkWindowManager argtfwm) {
	
	tfwm = argtfwm;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @return java.lang.String
 */
public String getTaskName() {
	return "Updating Sim Status";
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @return int
 */
public int getTaskType() {
	return TASKTYPE_NONSWING_BLOCKING;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(java.util.Hashtable hashTable){

	AsynchProgressPopup pp = (AsynchProgressPopup)hashTable.get(ClientTaskDispatcher.PROGRESS_POPUP);
	String errors = tfwm.updateSimRunningStatus(pp);
	if(errors != null){
		hashTable.put(TFRefresh.TF_ERRORS,errors);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @return boolean
 */
public boolean skipIfAbort() {
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @return boolean
 */
public boolean skipIfCancel(UserCancelException exc) {
	return true;
}
}

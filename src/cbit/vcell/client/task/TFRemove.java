package cbit.vcell.client.task;

import cbit.vcell.server.DataAccessException;
import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.util.AsynchProgressPopup;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestSuiteNew;
/**
 * Insert the type's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @author: Frank Morgan
 */
public class TFRemove extends AsynchClientTask {

	private TestingFrameworkWindowManager tfwm;
	private TestCaseNew tcn;
	private TestSuiteInfoNew tsin;
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFRemove(TestingFrameworkWindowManager argtfwm,TestCaseNew argtcn) {
	
	tfwm = argtfwm;
	tcn = argtcn;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFRemove(TestingFrameworkWindowManager argtfwm,TestSuiteInfoNew argtsin) {
	
	tfwm = argtfwm;
	tsin = argtsin;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @return java.lang.String
 */
public String getTaskName() {
	return "Removing "+(tsin != null?"TestSuite":"")+(tcn != null?"TestCase":"");
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
 * Creation date: (11/18/2004 9:52:47 AM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(java.util.Hashtable hashTable) throws java.lang.Exception {
	
	
	AsynchProgressPopup pp = (AsynchProgressPopup)hashTable.get(ClientTaskDispatcher.PROGRESS_POPUP);
	
	if(tcn != null){
		tfwm.removeTestCase(tcn);
	}else if(tsin != null){
		tfwm.removeTestSuite(tsin);
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

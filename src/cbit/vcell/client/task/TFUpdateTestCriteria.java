package cbit.vcell.client.task;

import cbit.vcell.server.DataAccessException;
import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.util.AsynchProgressPopup;
import cbit.vcell.numericstest.TestCriteriaNew;
/**
 * Insert the type's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @author: Frank Morgan
 */
public class TFUpdateTestCriteria extends AsynchClientTask {

	private TestingFrameworkWindowManager tfwm;
	private TestCriteriaNew orig_tcrit;
	private TestCriteriaNew new_tcrit;
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFUpdateTestCriteria(TestingFrameworkWindowManager argtfwm,
			TestCriteriaNew argorigtcrit,TestCriteriaNew argnewtcrit) {
	
	tfwm = argtfwm;
	orig_tcrit = argorigtcrit;
	new_tcrit = argnewtcrit;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @return java.lang.String
 */
public String getTaskName() {
	return "Updating TestCriteria";
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
public void run(java.util.Hashtable hashTable) throws DataAccessException {

	AsynchProgressPopup pp = (AsynchProgressPopup)hashTable.get(ClientTaskDispatcher.PROGRESS_POPUP);
	
	tfwm.updateTestCriteria(orig_tcrit,new_tcrit);
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

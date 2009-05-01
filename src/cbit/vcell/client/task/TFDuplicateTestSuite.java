package cbit.vcell.client.task;

//import cbit.vcell.numericstest.TestSuiteInfoNew;
//import cbit.vcell.clientdb.DocumentManager;
import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;
//import cbit.vcell.numericstest.AddTestSuiteOP;
import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.numericstest.TestSuiteInfoNew;
//import cbit.vcell.client.RequestManager;
import cbit.util.AsynchProgressPopup;
/**
 * Insert the type's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @author: Frank Morgan
 */
public class TFDuplicateTestSuite extends AsynchClientTask {

	private TestingFrameworkWindowManager tfwm;
	private TestSuiteInfoNew newTSInfo;
	private TestSuiteInfoNew origTSInfo;
	private int regrRefFlag;
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFDuplicateTestSuite(TestingFrameworkWindowManager argtfwm,TestSuiteInfoNew argOrigTSInfo,TestSuiteInfoNew argNewTSInfo,int argRegrRefFlag) {
	
	tfwm = argtfwm;
	newTSInfo = argNewTSInfo;
	origTSInfo = argOrigTSInfo;
	regrRefFlag = argRegrRefFlag;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @return java.lang.String
 */
public String getTaskName() {
	return "Duplicate TestSuite";
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
public void run(java.util.Hashtable hashTable) throws DataAccessException{

	AsynchProgressPopup pp = (AsynchProgressPopup)hashTable.get(ClientTaskDispatcher.PROGRESS_POPUP);

	String errors = null;
	errors = tfwm.duplicateTestSuite(origTSInfo,newTSInfo,regrRefFlag,pp);

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

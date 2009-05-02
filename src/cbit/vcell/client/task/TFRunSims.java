package cbit.vcell.client.task;

//import cbit.vcell.numericstest.TestSuiteInfoNew;
//import cbit.vcell.clientdb.DocumentManager;
import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;
import org.vcell.util.gui.AsynchProgressPopup;
//import cbit.vcell.numericstest.AddTestSuiteOP;
import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.numericstest.TestSuiteInfoNew;
//import cbit.vcell.client.RequestManager;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCriteriaNew;
/**
 * Insert the type's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @author: Frank Morgan
 */
public class TFRunSims extends AsynchClientTask {

	private TestingFrameworkWindowManager tfwm;
	private TestCriteriaNew tcrit;
	private TestCaseNew tcn;
	private TestSuiteInfoNew tsin;
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFRunSims(TestingFrameworkWindowManager argtfwm,TestCaseNew argtcn) {
	
	tfwm = argtfwm;
	tcn = argtcn;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFRunSims(TestingFrameworkWindowManager argtfwm,TestCriteriaNew argtcrit) {
	
	tfwm = argtfwm;
	tcrit = argtcrit;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFRunSims(TestingFrameworkWindowManager argtfwm,TestSuiteInfoNew argtsinfo) {
	
	tfwm = argtfwm;
	tsin = argtsinfo;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @return java.lang.String
 */
public String getTaskName() {
	return "Running Sims";
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
	String errors = (String)hashTable.get(TFRefresh.TF_ERRORS);
	if(errors == null){
		errors="";
	}
	String error = "";
	if(tcrit != null){
		error = tfwm.startSimulations(new TestCriteriaNew[] {tcrit},pp);
	}else if(tcn != null){
		error = tfwm.startSimulations(tcn.getTestCriterias(),pp);
	}else if (tsin != null){
		error = tfwm.startTestSuiteSimulations(tsin,pp);
	}
	errors+= (error == null?"":error);
	if(errors != null && errors.length() > 0){
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

package cbit.vcell.client.task;

//import cbit.vcell.numericstest.TestSuiteInfoNew;
//import cbit.vcell.clientdb.DocumentManager;
import org.vcell.util.DataAccessException;
import org.vcell.util.UserCancelException;

import cbit.vcell.solver.SimulationInfo;
//import cbit.vcell.numericstest.AddTestSuiteOP;
import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.document.VCDocumentInfo;
import cbit.vcell.numericstest.TestSuiteInfoNew;
//import cbit.vcell.client.RequestManager;
import cbit.util.AsynchProgressPopup;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCriteriaNew;
/**
 * Insert the type's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @author: Frank Morgan
 */
public class TFGenerateReport extends AsynchClientTask {

	private TestingFrameworkWindowManager tfwm;
	private TestCriteriaNew tcrit = null;
	private TestCaseNew tcn = null;
	private TestSuiteInfoNew tsin = null;
	private SimulationInfo userDefinedRegrRef = null;
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFGenerateReport(TestingFrameworkWindowManager argtfwm,TestCaseNew argtcn) {
	
	tfwm = argtfwm;
	tcn = argtcn;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFGenerateReport(TestingFrameworkWindowManager argtfwm,TestCaseNew argtcn,TestCriteriaNew argtcrit,SimulationInfo userDefinedRegrRef) {
	
	tfwm = argtfwm;
	tcrit = argtcrit;
	tcn = argtcn;
	this.userDefinedRegrRef = userDefinedRegrRef;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFGenerateReport(TestingFrameworkWindowManager argtfwm,
			TestSuiteInfoNew argtsinfo) {
	
	tfwm = argtfwm;
	tsin = argtsinfo;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @return java.lang.String
 */
public String getTaskName() {
	return "Generating Report";
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
public void run(java.util.Hashtable hashTable) throws Exception{

	AsynchProgressPopup pp = (AsynchProgressPopup)hashTable.get(ClientTaskDispatcher.PROGRESS_POPUP);
	String report = (String)hashTable.get(TFRefresh.TF_REPORT);
	if(report == null){
		report = "";
	}
	//tfwm.updateSimRunningStatus(pp);
	if(tcrit != null && tcn != null){
		if(userDefinedRegrRef != null){
			report+= tfwm.generateTestCaseReport(tcn,tcrit,pp,userDefinedRegrRef);
		}else{
			report+= tfwm.generateTestCaseReport(tcn,tcrit,pp,null);
		}
	}else if(tcn != null){
		report+= tfwm.generateTestCaseReport(tcn,null,pp,null);
	}else if (tsin != null){
		report+= tfwm.generateTestSuiteReport(tsin,pp);
	}
	if(report != null && report.length() > 0){
		hashTable.put(TFRefresh.TF_REPORT,report);
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

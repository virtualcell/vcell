package cbit.vcell.client.task;

import java.util.Hashtable;

import org.vcell.util.gui.AsynchProgressPopup;

import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCriteriaNew;
/**
 * Insert the type's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @author: Frank Morgan
 */
public class TFRunSims extends AsynchClientTask {

	private TestingFrameworkWindowManager tfwm = null;
	private TestCriteriaNew tcrit = null;
	private TestCaseNew tcn = null;
	private TestSuiteInfoNew tsin = null;
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
	private TFRunSims() {
		super("Running Sims", TASKTYPE_NONSWING_BLOCKING);
	}
public TFRunSims(TestingFrameworkWindowManager argtfwm,TestCaseNew argtcn) {
	this();
	tfwm = argtfwm;
	tcn = argtcn;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFRunSims(TestingFrameworkWindowManager argtfwm,TestCriteriaNew argtcrit) {
	this();
	tfwm = argtfwm;
	tcrit = argtcrit;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFRunSims(TestingFrameworkWindowManager argtfwm,TestSuiteInfoNew argtsinfo) {
	this();
	tfwm = argtfwm;
	tsin = argtsinfo;
}

/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable){

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

}

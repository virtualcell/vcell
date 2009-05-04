package cbit.vcell.client.task;

import java.util.Hashtable;

import org.vcell.util.DataAccessException;
import org.vcell.util.gui.AsynchProgressPopup;

import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.numericstest.TestCaseNew;
/**
 * Insert the type's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @author: Frank Morgan
 */
public class TFAddTestCases extends AsynchClientTask {

	private TestingFrameworkWindowManager tfwm;
	private TestCaseNew[] tcns;
	private TestSuiteInfoNew tsin;
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFAddTestCases(TestingFrameworkWindowManager argtfwm,
			TestSuiteInfoNew argtsinfo,TestCaseNew[] argtcns) {
	super("Adding TestCases", TASKTYPE_NONSWING_BLOCKING);
	tfwm = argtfwm;
	tcns = argtcns;
	tsin = argtsinfo;
}

/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws DataAccessException {

	AsynchProgressPopup pp = (AsynchProgressPopup)hashTable.get(ClientTaskDispatcher.PROGRESS_POPUP);
	String errors = null;

	errors = tfwm.addTestCases(tsin,tcns,TestingFrameworkWindowManager.COPY_REGRREF,pp);
	
	if(errors != null){
		hashTable.put(TFRefresh.TF_ERRORS,errors);
	}
}

}

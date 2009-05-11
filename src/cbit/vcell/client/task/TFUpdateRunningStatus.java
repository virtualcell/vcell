package cbit.vcell.client.task;

import java.util.Hashtable;

import org.vcell.util.gui.AsynchProgressPopup;

import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.numericstest.TestSuiteInfoNew;
/**
 * Insert the type's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @author: Frank Morgan
 */
public class TFUpdateRunningStatus extends AsynchClientTask {

	private TestingFrameworkWindowManager tfwm;
	private TestSuiteInfoNew tsin;
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
//public TFUpdateRunningStatus(TestingFrameworkWindowManager argtfwm) {
//	
//	this(argtfwm,null);
//}
	public TFUpdateRunningStatus(TestingFrameworkWindowManager argtfwm,TestSuiteInfoNew argtsin) {
		super("Updating Sim Status", TASKTYPE_NONSWING_BLOCKING);
		tfwm = argtfwm;
		tsin = argtsin;
	}


/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable){

	String errors = tfwm.updateSimRunningStatus(getClientTaskStatusSupport(),tsin);
	if(errors != null){
		hashTable.put(TFRefresh.TF_ERRORS,errors);
	}
}

}

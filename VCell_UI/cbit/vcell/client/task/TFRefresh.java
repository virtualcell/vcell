package cbit.vcell.client.task;

import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.client.PopupGenerator;
import cbit.vcell.desktop.controls.AsynchClientTask;
import cbit.gui.AsynchProgressPopup;
import cbit.util.UserCancelException;
/**
 * Insert the type's description here.
 * Creation date: (11/17/2004 2:20:58 PM)
 * @author: Frank Morgan
 */
public class TFRefresh extends AsynchClientTask {

	public static final String TF_ERRORS = "TF_ERRORS";
	public static final String TF_REPORT = "TF_REPORT";
	
	private TestingFrameworkWindowManager tfwm;
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:17:09 PM)
 */
public TFRefresh(TestingFrameworkWindowManager argtfwm) {

	tfwm = argtfwm;
	
	}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:20:58 PM)
 * @return java.lang.String
 */
public String getTaskName() {
	return "Testing Framework Refresh";
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:20:58 PM)
 * @return int
 */
public int getTaskType() {
	return TASKTYPE_SWING_NONBLOCKING;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:20:58 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(java.util.Hashtable hashTable){
	
	tfwm.getTestingFrameworkWindowPanel().refreshTree();
	
	String errors = (String)hashTable.get(TF_ERRORS);
	if(errors != null){
		PopupGenerator.showErrorDialog(errors);
	}
	String report = (String)hashTable.get(TF_REPORT);
	if(report != null){
		PopupGenerator.showReportDialog(tfwm,report);
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:20:58 PM)
 * @return boolean
 */
public boolean skipIfAbort() {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:20:58 PM)
 * @return boolean
 */
public boolean skipIfCancel(UserCancelException exc) {
	return false;
}
}

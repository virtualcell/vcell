/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.task;

import java.util.Hashtable;

import cbit.vcell.client.PopupGenerator;
import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.numericstest.TestSuiteInfoNew;
/**
 * Insert the type's description here.
 * Creation date: (11/17/2004 2:20:58 PM)
 * @author: Frank Morgan
 */
public class TFRefresh extends AsynchClientTask {

	public static final String TF_ERRORS = "TF_ERRORS";
	public static final String TF_REPORT = "TF_REPORT";
	
	private TestingFrameworkWindowManager tfwm;
	private TestSuiteInfoNew tsin;
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:17:09 PM)
 */
public TFRefresh(TestingFrameworkWindowManager argtfwm) {

this(argtfwm,null);
	
}
public TFRefresh(TestingFrameworkWindowManager argtfwm,TestSuiteInfoNew argtsin) {
	super("Testing Framework Refresh", TASKTYPE_SWING_NONBLOCKING, false, false);
	tfwm = argtfwm;
	tsin = argtsin;
	
}

/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:20:58 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable){
	
	tfwm.getTestingFrameworkWindowPanel().refreshTree(tsin);
	
	String errors = (String)hashTable.get(TF_ERRORS);
	if(errors != null){
		PopupGenerator.showErrorDialog(tfwm, errors);
	}
	String report = (String)hashTable.get(TF_REPORT);
	if(report != null){
		PopupGenerator.showReportDialog(tfwm,report);
	}
	
}

}

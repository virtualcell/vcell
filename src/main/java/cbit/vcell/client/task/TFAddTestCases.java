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

import org.vcell.util.DataAccessException;

import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestSuiteInfoNew;
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

	String errors = tfwm.addTestCases(tsin,tcns,TestingFrameworkWindowManager.COPY_REGRREF, getClientTaskStatusSupport());	
	if(errors != null){
		hashTable.put(TFRefresh.TF_ERRORS,errors);
	}
}

}

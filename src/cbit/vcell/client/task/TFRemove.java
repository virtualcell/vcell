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

import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestSuiteInfoNew;
/**
 * Insert the type's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @author: Frank Morgan
 */
public class TFRemove extends AsynchClientTask {

	private TestingFrameworkWindowManager tfwm = null;
	private TestCaseNew tcn = null;
	private TestSuiteInfoNew tsin = null;
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
	
	private TFRemove(TestSuiteInfoNew tsin, TestCaseNew tcn) {
		super("Removing "+(tsin != null?"TestSuite":"")+(tcn != null?"TestCase":""), TASKTYPE_NONSWING_BLOCKING);
	}
	
public TFRemove(TestingFrameworkWindowManager argtfwm,TestCaseNew argtcn) {
	this((TestSuiteInfoNew)null, argtcn);
	tfwm = argtfwm;
	tcn = argtcn;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFRemove(TestingFrameworkWindowManager argtfwm,TestSuiteInfoNew argtsin) {
	this(argtsin, (TestCaseNew)null);
	tfwm = argtfwm;
	tsin = argtsin;
}

/**
 * Insert the method's description here.
 * Creation date: (11/18/2004 9:52:47 AM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws java.lang.Exception {
	
	
//	AsynchProgressPopup pp = (AsynchProgressPopup)hashTable.get(ClientTaskDispatcher.PROGRESS_POPUP);
	
	if(tcn != null){
		tfwm.removeTestCase(tcn);
	}else if(tsin != null){
		tfwm.removeTestSuite(tsin);
	}
}

}

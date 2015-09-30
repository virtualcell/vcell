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

import org.vcell.util.ClientTaskStatusSupport;

import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCriteriaNew;
import cbit.vcell.numericstest.TestSuiteInfoNew;
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

	ClientTaskStatusSupport pp = getClientTaskStatusSupport();
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

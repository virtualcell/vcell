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
import org.vcell.util.document.VCDocument;

import cbit.vcell.client.TestingFrameworkWindowManager;
import cbit.vcell.numericstest.TestCaseNew;
import cbit.vcell.numericstest.TestCriteriaNew;
import cbit.vcell.numericstest.TestSuiteInfoNew;
import cbit.vcell.solver.SimulationInfo;
/**
 * Insert the type's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @author: Frank Morgan
 */
public class TFGenerateReport extends AsynchClientTask {

	public static class VCDocumentAndSimInfo {
		private SimulationInfo simInfo;
		private VCDocument vcDocument;
		public VCDocumentAndSimInfo(SimulationInfo simInfo,VCDocument vcDocument) {
			super();
			this.simInfo = simInfo;
			this.vcDocument = vcDocument;
		}
		public SimulationInfo getSimInfo() {
			return simInfo;
		}
		public VCDocument getVCDocument() {
			return vcDocument;
		}
	}
	private TestingFrameworkWindowManager tfwm;
	private TestCriteriaNew tcrit = null;
	private TestCaseNew tcn = null;
	private TestSuiteInfoNew tsin = null;
	private VCDocumentAndSimInfo userDefinedRegrRef = null;

/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
	private TFGenerateReport() {
		super("Generating Report", TASKTYPE_NONSWING_BLOCKING);
	}
public TFGenerateReport(TestingFrameworkWindowManager argtfwm,TestCaseNew argtcn) {
	
	this();
	tfwm = argtfwm;
	tcn = argtcn;
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFGenerateReport(TestingFrameworkWindowManager argtfwm,TestCaseNew argtcn,TestCriteriaNew argtcrit,VCDocumentAndSimInfo userDefinedRegrRef) {
	this();
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
public void run(Hashtable<String, Object> hashTable) throws Exception{

	ClientTaskStatusSupport pp = getClientTaskStatusSupport();
	
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

}

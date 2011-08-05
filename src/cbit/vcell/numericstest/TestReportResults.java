/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.numericstest;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.Version;

/**
 * Insert the type's description here.
 * Creation date: (11/9/2004 4:15:26 PM)
 * @author: Anuradha Lakshminarayana
 */
public class TestReportResults {
	private String[] simReportStatus = null;
	private Version[] simVersions = null;
	private String reportString = null;
	private TestSuiteOP[] updateResultsOPs = null;

/**
 * TestReportResults constructor comment.
 */
public TestReportResults(String[] argStatus, Version[] argSimVersions, String argReportString, TestSuiteOP[] argUpdateResultOPs) {
	super();
	if (argStatus.length != argSimVersions.length) {
		throw new IllegalArgumentException("Report status and simulation versions arrays must be of equal length");
	}
	simReportStatus = argStatus;
	simVersions = argSimVersions;
	reportString = argReportString;
	updateResultsOPs = argUpdateResultOPs;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2004 4:18:47 PM)
 * @return java.lang.String
 */
public java.lang.String getReportString() {
	return reportString;
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2004 4:24:21 PM)
 * @return java.lang.String[]
 */
public boolean getSimReportFailed(KeyValue simKey) {
	for (int i = 0; i < simVersions.length; i++){
		if (simVersions[i].getVersionKey().compareEqual(simKey)) {
			return (simReportStatus[i] != null);
		}
	}
	throw new IllegalArgumentException("Simkey "+simKey+" not found");
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2004 4:24:21 PM)
 * @return java.lang.String[]
 */
public String getSimReportMessage(KeyValue simKey) {
	for (int i = 0; i < simVersions.length; i++){
		if (simVersions[i].getVersionKey().compareEqual(simKey)) {
			return simReportStatus[i];
		}
	}
	throw new IllegalArgumentException("Simkey "+simKey+" not found");
}


/**
 * Insert the method's description here.
 * Creation date: (11/9/2004 4:18:47 PM)
 * @return cbit.vcell.numericstest.TestSuiteOP[]
 */
public cbit.vcell.numericstest.TestSuiteOP[] getUpdateResultsOPs() {
	return updateResultsOPs;
}
}

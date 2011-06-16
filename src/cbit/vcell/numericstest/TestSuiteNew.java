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
/**
 * Insert the type's description here.
 * Creation date: (10/16/2004 1:55:38 PM)
 * @author: Frank Morgan
 */
public class TestSuiteNew implements java.io.Serializable {

	private TestSuiteInfoNew tsInfoNew;
	private TestCaseNew[] testCases;

/**
 * TestSuiteNew constructor comment.
 */
public TestSuiteNew(TestSuiteInfoNew argTSInfoNew,TestCaseNew[] argTCNArr) {

	tsInfoNew = argTSInfoNew;
	testCases = argTCNArr;
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2004 12:29:48 PM)
 * @return cbit.vcell.numericstest.TestCaseNew[]
 */
public cbit.vcell.numericstest.TestCaseNew[] getTestCases() {
	return testCases;
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2004 12:29:48 PM)
 * @return cbit.vcell.numericstest.TestSuiteInfoNew
 */
public TestSuiteInfoNew getTSInfoNew() {
	return tsInfoNew;
}
}

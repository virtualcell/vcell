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
import cbit.vcell.numericstest.TestSuiteInfoNew;
/**
 * Insert the type's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @author: Frank Morgan
 */
public class TFAddTestSuite extends AsynchClientTask {

	private TestingFrameworkWindowManager tfwm;
	private TestSuiteInfoNew newTSInfo;
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFAddTestSuite(TestingFrameworkWindowManager argtfwm,TestSuiteInfoNew argNewTSInfo) {
	super("Adding new TestSuite", TASKTYPE_NONSWING_BLOCKING);
	tfwm = argtfwm;
	newTSInfo = argNewTSInfo;
}

/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws DataAccessException{

	tfwm.saveNewTestSuiteInfo(newTSInfo);
}

}

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
import cbit.vcell.numericstest.TestCriteriaNew;
/**
 * Insert the type's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @author: Frank Morgan
 */
public class TFUpdateTestCriteria extends AsynchClientTask {

	private TestingFrameworkWindowManager tfwm;
	private TestCriteriaNew orig_tcrit;
	private TestCriteriaNew new_tcrit;
/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 3:06:56 PM)
 */
public TFUpdateTestCriteria(TestingFrameworkWindowManager argtfwm,
			TestCriteriaNew argorigtcrit,TestCriteriaNew argnewtcrit) {
	super("Updating TestCriteria", TASKTYPE_NONSWING_BLOCKING);
	tfwm = argtfwm;
	orig_tcrit = argorigtcrit;
	new_tcrit = argnewtcrit;
}

/**
 * Insert the method's description here.
 * Creation date: (11/17/2004 2:08:09 PM)
 * @param hashTable java.util.Hashtable
 * @param clientWorker cbit.vcell.desktop.controls.ClientWorker
 */
public void run(Hashtable<String, Object> hashTable) throws DataAccessException {

//	AsynchProgressPopup pp = (AsynchProgressPopup)hashTable.get(ClientTaskDispatcher.PROGRESS_POPUP);
	
	tfwm.updateTestCriteria(orig_tcrit,new_tcrit);
}

}

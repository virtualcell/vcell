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

import java.math.BigDecimal;
/**
 * Insert the type's description here.
 * Creation date: (10/19/2004 6:10:48 AM)
 * @author: Frank Morgan
 */
public abstract class AddTestCasesOP extends TestSuiteOP {

	private String testCaseType;
	private String annotation;

/**
 * Insert the method's description here.
 * Creation date: (10/19/2004 6:12:02 AM)
 */
public AddTestCasesOP(BigDecimal argTSKey,String argType,String argAnnot) {
	
	super(argTSKey);//argTSKey Must be null if we are child of AddTestSuiteOP

	if(argType == null){
		throw new IllegalArgumentException(this.getClass().getName()+" Type cannot be null");
	}
	
	testCaseType = argType;
	annotation = argAnnot;//Can be null
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 5:44:49 AM)
 * @return java.lang.String
 */
public java.lang.String getAnnotation() {
	return annotation;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 5:44:49 AM)
 * @return java.lang.String
 */
public java.lang.String getTestCaseType() {
	return testCaseType;
}
}

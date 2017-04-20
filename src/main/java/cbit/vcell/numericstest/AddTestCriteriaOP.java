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
 * Creation date: (10/19/2004 6:21:59 AM)
 * @author: Frank Morgan
 */
public abstract class AddTestCriteriaOP extends TestSuiteOP {

	private BigDecimal testCaseKey;
	private Double maxAbsoluteError;
	private Double maxRelativeError;
	private AddTestResultsOP addTestResultsOP;
/**
 * EditTestCriteria constructor comment.
 * @param tsin cbit.vcell.numericstest.TestSuiteInfoNew
 */
public AddTestCriteriaOP(BigDecimal argTCKey,Double argMAE,Double argMRE,AddTestResultsOP argATROP) {
	
	super(null);
	
	// argATROP can be null,but if not must have Matching testcriteria keys
	if(argATROP != null && argATROP.getTestSuiteKey() != null){
				throw new IllegalArgumentException(this.getClass().getName()+" overrides TestSuiteKeys in children");
	}
	addTestResultsOP = argATROP;
	
	testCaseKey = argTCKey;// Must be null if we are child of AddTestCaseOP

	maxAbsoluteError = argMAE;//Can be null
	maxRelativeError = argMRE;//Can be null
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 5:50:16 AM)
 * @return cbit.vcell.numericstest.AddTestResultsOP[]
 */
public cbit.vcell.numericstest.AddTestResultsOP getAddTestResultsOP() {
	return addTestResultsOP;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 5:50:16 AM)
 * @return java.lang.Double
 */
public java.lang.Double getMaxAbsoluteError() {
	return maxAbsoluteError;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 5:50:16 AM)
 * @return java.lang.Double
 */
public java.lang.Double getMaxRelativeError() {
	return maxRelativeError;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 6:17:29 AM)
 * @return java.math.BigDecimal
 */
public java.math.BigDecimal getTestCaseKey() {
	return testCaseKey;
}
}

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

public class EditTestSuiteOP extends TestSuiteOP {
	private BigDecimal[] testSuiteKeys;
	private String[] newAnnotations;
	private Boolean bLock = null;
	

	public EditTestSuiteOP(BigDecimal[] editTheseTestSuites,boolean bLock) {
		
		super(null);
		if(!bLock){
			throw new IllegalArgumentException("Only lock == true implemented for EditTestSuiteOP");
		}
		testSuiteKeys = editTheseTestSuites;
		this.bLock = new Boolean(true);
	}
/**
 * EditTestCriteria constructor comment.
 * @param tsin cbit.vcell.numericstest.TestSuiteInfoNew
 */
public EditTestSuiteOP(BigDecimal[] editTheseTestSuites,String[] argAnnots) {
	
	super(null);

	if(editTheseTestSuites.length != argAnnots.length){
		throw new IllegalArgumentException(this.getClass().getName()+" argument array lengths not equal");
	}
	
	testSuiteKeys = editTheseTestSuites;
	newAnnotations = argAnnots;
}
public Boolean isLock(){
	return bLock;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 6:32:46 AM)
 * @return java.lang.String[]
 */
public java.lang.String[] getNewAnnotations() {
	return newAnnotations;
}

/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 6:32:46 AM)
 * @return java.math.BigDecimal[]
 */
public java.math.BigDecimal[] getTestSuiteKeys() {
	return testSuiteKeys;
}

}

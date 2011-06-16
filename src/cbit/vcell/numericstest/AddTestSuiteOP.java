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
 * Creation date: (10/19/2004 6:10:48 AM)
 * @author: Frank Morgan
 */
public class AddTestSuiteOP extends TestSuiteOP {

	private AddTestCasesOP[] addTCOPs;
	private String testSuiteVersionID;
	private String vCellBuildVersionID;
	private String numericsBuildVersionID;
	private String tsAnnot;

/**
 * Insert the method's description here.
 * Creation date: (10/19/2004 6:12:02 AM)
 */
public AddTestSuiteOP(String argTSID,String argNewVCBuildVersion,String argNewNumericsBuildVersion,AddTestCasesOP[] argAddTCOPs,String argTSAnnot) {

	super(null);

	if(argNewVCBuildVersion == null || argNewVCBuildVersion.length() == 0 ||
		argNewNumericsBuildVersion == null || argNewNumericsBuildVersion.length() == 0){
			throw new IllegalArgumentException(this.getClass().getName()+" VCBuildID and NumericsID cannot be null");
	}
	
	// argAddTCOPs can be null,but if not must have all TestSuiteKeys null
	if(argAddTCOPs != null){
		if(argAddTCOPs.length == 0){
			throw new IllegalArgumentException(this.getClass().getName()+" TestCasesOPs are not null but are empty");
		}
		for(int i=0;i<argAddTCOPs.length;i+= 1){
			if(argAddTCOPs[i].getTestSuiteKey() != null){
				throw new IllegalArgumentException(this.getClass().getName()+" overrides TestSuiteKeys in children");
			}
		}
	}
	addTCOPs = argAddTCOPs;
	
	testSuiteVersionID = argTSID;// argTSID can be null, will be generated on server
	
	vCellBuildVersionID = argNewVCBuildVersion;
	numericsBuildVersionID = argNewNumericsBuildVersion;
	tsAnnot = argTSAnnot;
	
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 5:38:45 AM)
 * @return cbit.vcell.numericstest.AddTestCasesOP[]
 */
public cbit.vcell.numericstest.AddTestCasesOP[] getAddTestCasesOPs() {
	return addTCOPs;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 5:38:45 AM)
 * @return java.lang.String
 */
public java.lang.String getNumericsBuildVersionID() {
	return numericsBuildVersionID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 5:38:45 AM)
 * @return java.lang.String
 */
public java.lang.String getTestSuiteVersionID() {
	return testSuiteVersionID;
}
/**
 * Insert the method's description here.
 * Creation date: (10/20/2004 5:38:45 AM)
 * @return java.lang.String
 */
public java.lang.String getVCellBuildVersionID() {
	return vCellBuildVersionID;
}

public String getTestSuiteAnnotation(){
	return tsAnnot;
}
}

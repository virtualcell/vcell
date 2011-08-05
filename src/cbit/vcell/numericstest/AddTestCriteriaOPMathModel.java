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

import org.vcell.util.document.KeyValue;
/**
 * Insert the type's description here.
 * Creation date: (10/19/2004 6:21:59 AM)
 * @author: Frank Morgan
 */
public class AddTestCriteriaOPMathModel extends AddTestCriteriaOP {

	private KeyValue mathModelSimKey;
	private KeyValue regressionMathModelKey;
	private KeyValue regressionMathModelSimKey;
/**
 * EditTestCriteria constructor comment.
 * @param tsin cbit.vcell.numericstest.TestSuiteInfoNew
 */
public AddTestCriteriaOPMathModel(BigDecimal argTCKey,KeyValue argMMSimKey,KeyValue argRegrMMKey,KeyValue argRegrMMSimKey,Double argMAE,Double argMRE,AddTestResultsOP argATROP) {
	
	super(argTCKey,argMAE,argMRE,argATROP);
	
	if(argMMSimKey == null){
		throw new IllegalArgumentException(this.getClass().getName()+" simulationKey cannot be null");
	}

	mathModelSimKey = argMMSimKey;
	regressionMathModelKey = argRegrMMKey;
	regressionMathModelSimKey = argRegrMMSimKey;
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 5:04:01 PM)
 * @return cbit.sql.KeyValue
 */
public org.vcell.util.document.KeyValue getMathModelSimKey() {
	return mathModelSimKey;
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 5:04:01 PM)
 * @return cbit.sql.KeyValue
 */
public org.vcell.util.document.KeyValue getRegressionMathModelKey() {
	return regressionMathModelKey;
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 5:04:01 PM)
 * @return cbit.sql.KeyValue
 */
public org.vcell.util.document.KeyValue getRegressionMathModelSimKey() {
	return regressionMathModelSimKey;
}
}

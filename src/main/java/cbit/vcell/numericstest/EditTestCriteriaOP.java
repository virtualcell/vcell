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
public abstract class EditTestCriteriaOP extends TestSuiteOP {

	private BigDecimal testCriterium;
	private Double newMaxAbsError;
	private Double newMaxRelError;
	private String newReportStatus;
	private String newReportStatusMessage;

/**
 * EditTestCriteria constructor comment.
 * @param tsin cbit.vcell.numericstest.TestSuiteInfoNew
 */
protected EditTestCriteriaOP(
    BigDecimal editThisTCrit,
    Double argMaxAbsError,
    Double argMaxRelError,
    String argReportStatus,
    String argReportStatusMessage) {

    super(null);

    //if((editTheseTCrits.length != argMaxAbsErrors.length) || (editTheseTCrits.length != argMaxRelErrors.length)){
    //throw new IllegalArgumentException(this.getClass().getName()+" argument array lengths not equal");
    //}

    testCriterium = editThisTCrit;
    newMaxAbsError = argMaxAbsError;
    newMaxRelError = argMaxRelError;
    newReportStatus = argReportStatus;
    newReportStatusMessage = argReportStatusMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/2004 6:35:57 AM)
 * @return double[]
 */
public Double getNewMaxAbsError() {
	return newMaxAbsError;
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/2004 6:35:57 AM)
 * @return double[]
 */
public Double getNewMaxRelError() {
	return newMaxRelError;
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2004 1:24:14 PM)
 */
public String getNewReportStatus() {

	return newReportStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (11/15/2004 1:24:14 PM)
 */
public String getNewReportStatusMessage() {

	return newReportStatusMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (10/19/2004 6:35:57 AM)
 * @return cbit.vcell.numericstest.TestCriteriaNew[]
 */
public BigDecimal getTestCriteriaKey() {
	return testCriterium;
}
}

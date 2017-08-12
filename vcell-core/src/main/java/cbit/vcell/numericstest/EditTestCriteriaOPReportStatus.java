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
 * Creation date: (11/16/2004 6:56:32 AM)
 * @author: Frank Morgan
 */
public class EditTestCriteriaOPReportStatus extends EditTestCriteriaOP {
/**
 * EditTestCriteriaReportStatus constructor comment.
 * @param editThisTCrit java.math.BigDecimal
 * @param argMaxAbsError java.lang.Double
 * @param argMaxRelError java.lang.Double
 * @param argReportStatus java.lang.String
 */
public EditTestCriteriaOPReportStatus(
    java.math.BigDecimal editThisTCrit,
    String argReportStatus,String argReportStatusMessage) {
    super(editThisTCrit, null, null, argReportStatus,argReportStatusMessage);
}
}

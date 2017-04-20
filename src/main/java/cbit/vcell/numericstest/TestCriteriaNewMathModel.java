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
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.solver.test.*;
import cbit.vcell.solver.SimulationInfo;
/**
 * Insert the type's description here.
 * Creation date: (11/11/2004 1:32:38 PM)
 * @author: Frank Morgan
 */
public class TestCriteriaNewMathModel extends TestCriteriaNew {

	private MathModelInfo regrMathModelInfo;

/**
 * TestCriteriaNewMathModel constructor comment.
 * @param argTcritKey java.math.BigDecimal
 * @param argSimInfo cbit.vcell.solver.SimulationInfo
 * @param argMaxRelError java.lang.Double
 * @param argMaxAbsError java.lang.Double
 * @param argVariableComparisonSummary cbit.vcell.solver.test.VariableComparisonSummary[]
 */
public TestCriteriaNewMathModel(
    java.math.BigDecimal argTcritKey,
    SimulationInfo argSimInfo,
    MathModelInfo argregrMMInfo,
    SimulationInfo argregrSimInfo,
    Double argMaxRelError,
    Double argMaxAbsError,
    VariableComparisonSummary[] argVariableComparisonSummary,
    String argReportStatus,
    String argReportStatusMessage) {
    super(
        argTcritKey,
        argSimInfo,
        argregrSimInfo,
        argMaxRelError,
        argMaxAbsError,
        argVariableComparisonSummary,argReportStatus,argReportStatusMessage);

    regrMathModelInfo = argregrMMInfo;
}


/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 1:42:27 PM)
 */
public MathModelInfo getRegressionMathModelInfo() {
	return regrMathModelInfo;
}

@Override
public String describe() {
	if (regrMathModelInfo != null) {
		return regrMathModelInfo.toString() +  " " + getSimInfo().getName(); 
	}
	return "Math model sim (?) " + getSimInfo().getName(); 
}
}

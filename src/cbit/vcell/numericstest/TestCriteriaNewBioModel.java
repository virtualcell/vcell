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
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.Version;

import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.test.VariableComparisonSummary;
/**
 * Insert the type's description here.
 * Creation date: (11/11/2004 1:32:38 PM)
 * @author: Frank Morgan
 */
@SuppressWarnings("serial")
public class TestCriteriaNewBioModel extends TestCriteriaNew {

	private BioModelInfo regrBioModelInfo;
	private String regressionApplicationName;

/**
 * TestCriteriaNewMathModel constructor comment.
 * @param argTcritKey java.math.BigDecimal
 * @param argSimInfo cbit.vcell.solver.SimulationInfo
 * @param argMaxRelError java.lang.Double
 * @param argMaxAbsError java.lang.Double
 * @param argVariableComparisonSummary cbit.vcell.solver.test.VariableComparisonSummary[]
 */
public TestCriteriaNewBioModel(
    java.math.BigDecimal argTcritKey,
    SimulationInfo argSimInfo,
    BioModelInfo argregrBMInfo,
    String argregrAppName,
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

    regrBioModelInfo = argregrBMInfo;
    regressionApplicationName = argregrAppName;
}


/**
 * Insert the method's description here.
 * Creation date: (11/13/2004 2:53:00 PM)
 * @return java.lang.String
 */
public java.lang.String getRegressionApplicationName() {
	return regressionApplicationName;
}


/**
 * Insert the method's description here.
 * Creation date: (11/11/2004 1:42:27 PM)
 */
public BioModelInfo getRegressionBioModelInfo() {
	
	return regrBioModelInfo;
	
}


@Override
public String describe() {
	StringBuilder sb = new StringBuilder();
	if ( regrBioModelInfo != null) {
	Version v = regrBioModelInfo.getVersion();
	if (v != null) {
		sb.append(v.getName());
		sb.append(' ');
	}
	else {
		sb.append("unversioned ");
	}
	}
	else {
		sb.append("no model info");
	}
	SimulationInfo si = getSimInfo( );
	if (si != null) {
		sb.append(si.getName());
	}
	else {
		sb.append("no simulation");
	}
	return sb.toString();
}
}

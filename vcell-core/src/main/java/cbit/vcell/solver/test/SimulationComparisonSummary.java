/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.test;

import java.util.Vector;
/**
 * Insert the type's description here.
 * Creation date: (1/20/2003 10:29:01 AM)
 * @author: Jim Schaff
 */
public class SimulationComparisonSummary implements java.io.Serializable {
	private Vector<VariableComparisonSummary> variableComparisonSummaryList = new Vector<VariableComparisonSummary>();
	private static final double STANDARD_RELATIVE_ERROR_THRESHOLD = 1E-8;
/**
 * SimResultsDifferenceReport constructor comment.
 */
public SimulationComparisonSummary() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 11:11:12 AM)
 * @param varComparisonSummary cbit.vcell.solver.test.VariableComparisonSummary
 */
public void addVariableComparisonSummary(VariableComparisonSummary varComparisonSummary) {
	variableComparisonSummaryList.add(varComparisonSummary);
}
/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 10:31:16 AM)
 * @return double
 * @param varName java.lang.String
 */
public double getAbsoluteError(String varName) {
	return getVariableComparisonSummary(varName).getAbsoluteError().doubleValue();
}
/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 10:31:28 AM)
 * @return double
 */
public VariableComparisonSummary[] getFailingVariableComparisonSummaries(double absErrorThreshold, double relErrorThreshold) {
	Vector<VariableComparisonSummary> varComparisonSummaryList = new Vector<VariableComparisonSummary>();
	for (int i = 0; i < variableComparisonSummaryList.size(); i++){
		VariableComparisonSummary varComparisonSummary = (VariableComparisonSummary)variableComparisonSummaryList.elementAt(i);
		if(VariableComparisonSummary.isFailed(varComparisonSummary)){
			varComparisonSummaryList.add(varComparisonSummary);
		}
	}
	return (VariableComparisonSummary[])org.vcell.util.BeanUtils.getArray(varComparisonSummaryList,VariableComparisonSummary.class);
}
/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 10:31:28 AM)
 * @return double
 */
public double getMaxAbsoluteError() {
	double maxAbsoluteError = Double.NEGATIVE_INFINITY;
	for (int i = 0; i < variableComparisonSummaryList.size(); i++){
		VariableComparisonSummary varComparisonSummary = (VariableComparisonSummary)variableComparisonSummaryList.elementAt(i);
		maxAbsoluteError = Math.max(maxAbsoluteError,varComparisonSummary.getAbsoluteError().doubleValue());
	}
	return maxAbsoluteError;
}
/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 10:31:56 AM)
 * @return double
 */
public double getMaxRelativeError() {
	double maxRelativeError = Double.NEGATIVE_INFINITY;
	for (int i = 0; i < variableComparisonSummaryList.size(); i++){
		VariableComparisonSummary varComparisonSummary = (VariableComparisonSummary)variableComparisonSummaryList.elementAt(i);
		maxRelativeError = Math.max(maxRelativeError,varComparisonSummary.getRelativeError().doubleValue());
	}
	return maxRelativeError;
}
/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 10:31:44 AM)
 * @param varName java.lang.String
 */
public double getRelativeError(String varName) {
	return getVariableComparisonSummary(varName).getRelativeError().doubleValue();
}
/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 11:42:33 AM)
 * @return cbit.vcell.solver.test.VariableComparisonSummary[]
 */
public VariableComparisonSummary[] getVariableComparisonSummaries() {
	return (VariableComparisonSummary[])org.vcell.util.BeanUtils.getArray(variableComparisonSummaryList,VariableComparisonSummary.class);
}
/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 11:20:43 AM)
 * @return cbit.vcell.solver.test.VariableComparisonSummary
 * @param varName java.lang.String
 */
public VariableComparisonSummary getVariableComparisonSummary(String varName) {
	for (int i = 0; i < variableComparisonSummaryList.size(); i++){
		VariableComparisonSummary varComparisonSummary = (VariableComparisonSummary)variableComparisonSummaryList.elementAt(i);
		if (varComparisonSummary.getName().equals(varName)){
			return varComparisonSummary;
		}
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 2:29:05 PM)
 * @return java.lang.String
 */
public String toString() {
	return "SimulationComparisonSummary@"+Integer.toHexString(hashCode())+": "+variableComparisonSummaryList.size()+" variableComparisonSummaries";
}
}

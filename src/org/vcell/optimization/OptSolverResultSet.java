package org.vcell.optimization;

import java.util.ArrayList;

import cbit.vcell.opt.OptimizationStatus;

public class OptSolverResultSet implements java.io.Serializable {
	public static class ProfileDistribution {
		String fixedParamName;
		ArrayList<OptRunResultSet> optRunResultSetList = new ArrayList<OptRunResultSet>(); 
		public ProfileDistribution(String fixedParamName, ArrayList<OptRunResultSet> optRunResultSetList) {
			super();
			this.fixedParamName = fixedParamName;
			this.optRunResultSetList = optRunResultSetList;
		}
	}
	public static class OptRunResultSet {
		private double[] fieldParameterValues = null;
		private Double fieldObjectiveFunctionValue = null;
		private long numObjFunctionEvaluations = 0;		
		private OptimizationStatus optStatus = null;
		
		public OptRunResultSet(double[] parameterValues,
				Double objectiveFunctionValue,
				long numObjFunctionEvaluations, OptimizationStatus optStatus) {
			super();
			this.fieldParameterValues = parameterValues;
			this.fieldObjectiveFunctionValue = objectiveFunctionValue;
			this.numObjFunctionEvaluations = numObjFunctionEvaluations;
			this.optStatus = optStatus;
		}
		
	}
	private OptRunResultSet bestRunResultSet = null;
	private String[] fieldParameterNames = null;
	private ArrayList<ProfileDistribution> profileDistributionList = new ArrayList<ProfileDistribution>(); 

/**
 * OptimizationResultSet constructor comment.
 */
public OptSolverResultSet(String[] parameterNames, OptRunResultSet bestResult) {
	this(parameterNames, bestResult, null);
}

public OptSolverResultSet(String[] parameterNames, OptRunResultSet bestResult, ArrayList<ProfileDistribution> pdList) {
	this.fieldParameterNames = parameterNames;
	bestRunResultSet = bestResult;
	this.profileDistributionList = pdList;
}


/**
 * Insert the method's description here.
 * Creation date: (9/5/2005 11:48:10 AM)
 * @return java.lang.Double
 */
public java.lang.Double getObjectiveFunctionValue() {
	return bestRunResultSet.fieldObjectiveFunctionValue;
}


/**
 * Insert the method's description here.
 * Creation date: (12/15/2005 11:17:32 AM)
 * @return long
 */
public long getObjFunctionEvaluations() {
	return bestRunResultSet.numObjFunctionEvaluations;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2005 10:53:48 AM)
 * @return java.lang.String
 */
public OptimizationStatus getOptimizationStatus() {
	return bestRunResultSet.optStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/2005 12:05:02 AM)
 * @return java.lang.String[]
 */
public String[] getParameterNames() {
	return fieldParameterNames;
}


/**
 * Insert the method's description here.
 * Creation date: (8/29/2005 3:17:44 PM)
 * @return double[]
 */
public double[] getParameterValues() {
	return bestRunResultSet.fieldParameterValues;
}

public void show(){
	System.out.print("OptResults: numEvals=" + bestRunResultSet.numObjFunctionEvaluations
			+", bestObjFuncValue="+bestRunResultSet.fieldObjectiveFunctionValue
			+", status="+bestRunResultSet.optStatus.toString()+", params = [");
	for (int i = 0; i < fieldParameterNames.length; i++) {
		System.out.print(fieldParameterNames[i]+"="+bestRunResultSet.fieldParameterValues[i]);
		if (i<fieldParameterNames.length-1){
			System.out.print(", ");
		}
	}
	System.out.println("]");
}

}
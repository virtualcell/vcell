/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.opt;

import java.util.ArrayList;

public class OptSolverResultSet implements java.io.Serializable {
	//field variables
	private OptRunResultSet bestRunResultSet = null;
	private String[] fieldParameterNames = null;
	private ArrayList<ProfileDistribution> profileDistributionList = new ArrayList<ProfileDistribution>();
	
	//nested class (The class is currently only used by VirtualFRAP)
	public static class ProfileDistribution {
		String fixedParamName;
		ArrayList<OptRunResultSet> optRunResultSetList = new ArrayList<OptRunResultSet>(); 
		public ProfileDistribution(String fixedParamName, ArrayList<OptRunResultSet> optRunResultSetList) {
			super();
			this.fixedParamName = fixedParamName;
			this.optRunResultSetList = optRunResultSetList;
		}
		public ArrayList<OptRunResultSet> getOptRunResultSetList() {
			return optRunResultSetList;
		}
		public String getFixedParamName() {
			return fixedParamName;
		}
	}
	//nested class
	public static class OptRunResultSet implements java.io.Serializable{
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
		
		public Double getObjectiveFunctionValue() {
			return fieldObjectiveFunctionValue;
		}
		
		public double[] getParameterValues()
		{
			return fieldParameterValues;
		}
	}
	 
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
	 * This class assume that the fieldParameterValues holds the values for parameters named
	 * in fieldParameterNames by using the same index. The result can be used to find specific 
	 * fixed parameter value in fieldParameterValues of ProfileDistribution.
	 * @param fixedParamName
	 * @return fixed parameter index or -1(not found).
	 */
	public int getFixedParameterIndex(String fixedParamName)
	{
		if(bestRunResultSet != null && bestRunResultSet.fieldParameterValues != null &&
		   bestRunResultSet.getParameterValues().length == getParameterNames().length)
		{
			for(int i=0; i<fieldParameterNames.length; i++)
			{
				if(fieldParameterNames[i].equals(fixedParamName))
				{
					return i;
				}
			}
		}
		return -1;
	}

	public ArrayList<ProfileDistribution> getProfileDistributionList() {
		return profileDistributionList;
	}

	public Double getLeastObjectiveFunctionValue() {
		return bestRunResultSet.getObjectiveFunctionValue();
	}

	public long getObjFunctionEvaluations() {
		return bestRunResultSet.numObjFunctionEvaluations;
	}

	public OptimizationStatus getOptimizationStatus() {
		return bestRunResultSet.optStatus;
	}

	public String[] getParameterNames() {
		return fieldParameterNames;
	}

	public double[] getBestEstimates() {
		return bestRunResultSet.getParameterValues();
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

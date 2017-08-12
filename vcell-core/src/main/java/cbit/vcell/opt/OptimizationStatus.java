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


/**
 * Insert the type's description here.
 * Creation date: (3/3/00 12:24:17 AM)
 * @author: 
 */
@SuppressWarnings("serial")
public class OptimizationStatus implements java.io.Serializable {

	private int statusCode = 0;
	private String statusString = null;


	public static final int NORMAL_TERMINATION				= 0;
	public static final int STOPPED_BY_USER					= 1;
	public static final int UNKNOWN							= 2;
	public static final int NONFEASIBLE_LINEAR				= 3;
	public static final int NONFEASIBLE_NONLINEAR			= 4;	
	public static final int NOSOLUTION_ITERATIONS			= 5;
	public static final int NOSOLUTION_MACHINE_PRECISION	= 6;
	public static final int FAILED_CONSTRUCTING_D0			= 7;
	public static final int FAILED_CONSTRUCTING_D1			= 8;
	public static final int FAILED_INCONSISTENT_INPUT		= 9;
	public static final int FAILED_ITERATES_STALLED			= 10;
	public static final int FAILED_PENALTY_TOO_LARGE		= 11;
	public static final int FAILED							= 12;

	private static final int MIN_CODE						= 0;
	private static final int MAX_CODE						= 12;
	
	/*
	private String statusString[] = {
		"normal termination",
		"no feasible point found for linear constraints",
		"no feasible point found for nonlinear constraints",
		"no solution has been found in miter iterations",
		"stepsize smaller than machine precision before a successful new iterate is found",
		"failure in attempting to construct d0",
		"failure in attempting to construct d1",
		"inconsistent input data",
		"new iterate essentially identical to previous iterate, though stopping criterion not satisfied",
		"penalty parameter too large, unable to satisfy nonlinear equality constraint"
	};
	 */

	/**
	 * ConstraintType constructor comment.
	 */
	public OptimizationStatus(int argStatusCode, String argStatusMsg) {
		if (argStatusCode < MIN_CODE || argStatusCode > MAX_CODE) {
			throw new RuntimeException("Status code out of range");
		}
		this.statusCode = argStatusCode;
		this.statusString = argStatusMsg;
	}


	public boolean equals(Object obj) {
		if (obj instanceof OptimizationStatus){
			if (statusCode == ((OptimizationStatus)obj).statusCode){
				return true;
			}
		}
		return false;
	}


	public int hashCode() {
		return statusCode;
	}


	public int getReturnCode() {
		return statusCode;
	}

	public String getReturnMessage()
	{
		return statusString;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3/3/00 12:29:37 AM)
	 * @return boolean
	 */
	public boolean isNormal() {
		return (statusCode==NORMAL_TERMINATION);
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (3/3/00 12:57:51 AM)
	 * @return java.lang.String
	 */
	public String toString() {
		return "[" + statusCode + "," + statusString + "]";
	}
	
	public static int statusFromXMLName(String xmlName){
		if (xmlName.equals(OptXmlTags.OptSolverResultSetStatus_Attr_Failed)){
			return FAILED;
		}
		if (xmlName.equals(OptXmlTags.OptSolverResultSetStatus_Attr_FailedConstructingD0)){
			return FAILED_CONSTRUCTING_D0;
		}
		if (xmlName.equals(OptXmlTags.OptSolverResultSetStatus_Attr_FailedConstructingD1)){
			return FAILED_CONSTRUCTING_D1;
		}
		if (xmlName.equals(OptXmlTags.OptSolverResultSetStatus_Attr_FailedInconsistentInput)){
			return FAILED_INCONSISTENT_INPUT;
		}
		if (xmlName.equals(OptXmlTags.OptSolverResultSetStatus_Attr_FailedIteratesStalled)){
			return FAILED_ITERATES_STALLED;
		}
		if (xmlName.equals(OptXmlTags.OptSolverResultSetStatus_Attr_FailedPenaltyTooLarge)){
			return FAILED_PENALTY_TOO_LARGE;
		}
		if (xmlName.equals(OptXmlTags.OptSolverResultSetStatus_Attr_NonfeasibleLinear)){
			return NONFEASIBLE_LINEAR;
		}
		if (xmlName.equals(OptXmlTags.OptSolverResultSetStatus_Attr_NonfeasibleNonlinear)){
			return NONFEASIBLE_NONLINEAR;
		}
		if (xmlName.equals(OptXmlTags.OptSolverResultSetStatus_Attr_NormalTermination)){
			return NORMAL_TERMINATION;
		}
		if (xmlName.equals(OptXmlTags.OptSolverResultSetStatus_Attr_NoSolutionIterations)){
			return NOSOLUTION_ITERATIONS;
		}
		if (xmlName.equals(OptXmlTags.OptSolverResultSetStatus_Attr_NoSolutionMachinePrecision)){
			return NOSOLUTION_MACHINE_PRECISION;
		}
		if (xmlName.equals(OptXmlTags.OptSolverResultSetStatus_Attr_StoppedByUser)){
			return STOPPED_BY_USER;
		}
		if (xmlName.equals(OptXmlTags.OptSolverResultSetStatus_Attr_Unknown)){
			return UNKNOWN;
		}
		return UNKNOWN;
	}
}

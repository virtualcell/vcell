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

import org.vcell.util.Matchable;

/**
 * Insert the type's description here.
 * Creation date: (8/4/2005 12:48:37 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class OptimizationSolverSpec implements Matchable, java.io.Serializable {
	private String solverType = SOLVERTYPE_CFSQP;
	private double objectiveFunctionChangeTolerance;
	private int numOfRuns = 1;

	private CopasiOptimizationMethod copasiOptimizationMethod = null;
	
	public final static String SOLVERTYPE_CFSQP = "CFSQP";
	public final static String SOLVERTYPE_POWELL = "Powell Method";	
	public final static String[] SOLVER_TYPES = { SOLVERTYPE_CFSQP, SOLVERTYPE_POWELL};
		
	//public final static String SOLVERTYPE_CONJUGATE_GRADIENT = "Conjugate Gradient";	
	//public final static String SOLVERTYPE_SIMULTANEOUS = "Simultaneous Method";
	//public final static String DEPRECATED_SOLVERTYPE_MULTIPLESHOOTING = "Multiple Shooting Method";
	//public final static String[] SOLVER_TYPES = { SOLVERTYPE_CFSQP, SOLVERTYPE_SIMULTANEOUS, SOLVERTYPE_CONJUGATE_GRADIENT, SOLVERTYPE_POWELL};

/**
 * OptimizationSolverSpec constructor comment.
 */
public OptimizationSolverSpec(OptimizationSolverSpec optimizationSolverSpecToCopy) {
	super();
	this.solverType = optimizationSolverSpecToCopy.solverType;
	this.objectiveFunctionChangeTolerance = optimizationSolverSpecToCopy.objectiveFunctionChangeTolerance;
}

public OptimizationSolverSpec(CopasiOptimizationMethod argCopasiOptimizationMethod) {
	copasiOptimizationMethod = argCopasiOptimizationMethod; 
	/*
	if (solverType.equals(SOLVERTYPE_CONJUGATE_GRADIENT)){
		objectiveFunctionChangeTolerance = 1.0e-6;
	} else if (solverType.equals(SOLVERTYPE_POWELL)){
		objectiveFunctionChangeTolerance = 1.0e-6;
	}
	*/
}


/**
 * OptimizationSolverSpec constructor comment.
 */
public OptimizationSolverSpec(String argSolverName) {
	this(argSolverName,1e-6);
	/*
	if (solverType.equals(SOLVERTYPE_CONJUGATE_GRADIENT)){
		objectiveFunctionChangeTolerance = 1.0e-6;
	} else if (solverType.equals(SOLVERTYPE_POWELL)){
		objectiveFunctionChangeTolerance = 1.0e-6;
	}
	*/
}


/**
 * OptimizationSolverSpec constructor comment.
 */
public OptimizationSolverSpec(String argSolverName, double argObjectiveFunctionChangeTolerance) {
	super();
	boolean bFound = false;
	for (int i = 0; i < SOLVER_TYPES.length; i++){
		if (SOLVER_TYPES[i].equals(argSolverName)){
			bFound = true;
		}
	}
	if (bFound){		
		solverType = argSolverName;
	} else {
		solverType = SOLVERTYPE_CFSQP;		// default to CFSQP
	}
	this.objectiveFunctionChangeTolerance = argObjectiveFunctionChangeTolerance;
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	return equals(obj);
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 5:05:16 PM)
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean equals(Object obj) {
	if (obj instanceof OptimizationSolverSpec){
		OptimizationSolverSpec oss = (OptimizationSolverSpec)obj;
		if (copasiOptimizationMethod == null) {
			if (!solverType.equals(oss.solverType)){
				return false;
			}
			if (objectiveFunctionChangeTolerance != oss.objectiveFunctionChangeTolerance){
				return false;
			}
		} else {
			if (!copasiOptimizationMethod.equals(oss.copasiOptimizationMethod)) {
				return false;
			}
		}
		return true;
	}
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (8/4/2005 3:20:05 PM)
 * @return double
 */
public double getObjectiveFunctionChangeTolerance() {
	return this.objectiveFunctionChangeTolerance;
}


/**
 * Insert the method's description here.
 * Creation date: (8/4/2005 1:04:39 PM)
 * @return java.lang.String
 */
public String getSolverType() {
	return solverType;
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2006 5:07:24 PM)
 * @return int
 */
public int hashCode() {
	return (solverType+objectiveFunctionChangeTolerance).hashCode();
}


public final CopasiOptimizationMethod getCopasiOptimizationMethod() {
	return copasiOptimizationMethod;
}

public final void setCopasiOptimizationMethod(CopasiOptimizationMethod copasiOptimizationMethod)
{
	this.copasiOptimizationMethod = copasiOptimizationMethod;
}

public int getNumOfRuns() {
	return numOfRuns;
}

public void setNumOfRuns(int numOfRuns) {
	this.numOfRuns = numOfRuns;
}

}

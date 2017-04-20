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
/**
 * Insert the type's description here.
 * Creation date: (1/20/2003 11:07:17 AM)
 * @author: Jim Schaff
 */
public class VariableComparisonSummary implements java.io.Serializable {
	
	private Double maxRef;
	private Double minRef;
	private Double absError;
	private Double relError;
	private String varName = null;
	private Double mse;
	private Double timeAbsError;
	private Integer indexAbsError;
	private Double timeRelError;
	private Integer indexRelError;

public VariableComparisonSummary(	String argVarName,
									double argMinRef,
									double argMaxRef,
									double argAbsoluteError,
									double argRelativeError,
									double argMeanSqErr,
									double argTimeAbsError,
									int    argIndexAbsError,
									double argTimeRelError,
									int    argIndexRelError){

	this.varName = argVarName;
	this.minRef = 		new Double(argMinRef);
	this.maxRef = 		new Double(argMaxRef);
	this.absError = 	new Double(argAbsoluteError);
	this.relError = 	new Double(argRelativeError);
	this.mse = 			new Double(argMeanSqErr);
	this.timeAbsError =	new Double(argTimeAbsError);
	this.indexAbsError= new Integer(argIndexAbsError);
	this.timeRelError =	new Double(argTimeRelError);
	this.indexRelError= new Integer(argIndexRelError);
}


public static boolean isFailed(VariableComparisonSummary varCompSummary){
	return varCompSummary.getRelativeError().doubleValue() > 1.0;
}


public Double getAbsoluteError(){
	return absError;
}


/**
 * Insert the method's description here.
 * Creation date: (11/23/2004 7:44:17 PM)
 * @return java.lang.Double
 */
public Integer getIndexAbsoluteError() {
	return indexAbsError;
}


public Double getMaxRef(){
	return maxRef;
}


/**
 * Gets the meanSqError property (double) value.
 * @return The meanSqError property value.
 */
public Double getMeanSqError() {
	return mse;
}


public Double getMinRef(){
	return minRef;
}


/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 11:22:01 AM)
 * @return java.lang.String
 */
public String getName() {
	return varName;
}


public Double getRelativeError(){
	return relError;
}


/**
 * Insert the method's description here.
 * Creation date: (11/23/2004 7:44:17 PM)
 * @return java.lang.Double
 */
public Double getTimeAbsoluteError() {
	return timeAbsError;
}


/**
 * Insert the method's description here.
 * Creation date: (3/7/2004 2:28:06 PM)
 * @return java.lang.String
 */
public String toShortString() {
	return "var="+getName()+": \tMSE="+mse+",\tMAE="+absError+",\tMRE="+relError+",\tMnR="+minRef+",\tMxR="+maxRef+",\t@tA="+timeAbsError+",\t@indexA="+indexAbsError+",\t@tR="+timeRelError+",\t@indexR="+indexRelError;
}


/**
 * Insert the method's description here.
 * Creation date: (1/20/2003 11:30:12 AM)
 * @return java.lang.String
 */
public String toString() {
	return "VariableComparisonSummary@"+Integer.toHexString(hashCode())+": "+toShortString();
}


public Integer getIndexRelativeError() {
	return indexRelError;
}


public Double getTimeRelativeError() {
	return timeRelError;
}
}

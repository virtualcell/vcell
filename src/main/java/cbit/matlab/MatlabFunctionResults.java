/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.matlab;

/**
 * Insert the type's description here.
 * Creation date: (8/9/2005 2:18:06 PM)
 * @author: Jim Schaff
 */
public class MatlabFunctionResults {
	private String stdout = null;
	private java.util.Hashtable varHash = new java.util.Hashtable();
/**
 * MatlabFunctionResults constructor comment.
 */
MatlabFunctionResults() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2005 2:20:20 PM)
 * @param varName java.lang.String
 * @param value double[][]
 */
void addVariable(String varName, double[][] value) {
	if (varHash.get(varName) != null){
		throw new RuntimeException("varName = '"+varName+"' already added to Matlab result set");
	}
	if (value==null){
		throw new RuntimeException("value for \""+varName+"\" cannot be null for Matlab function result set");
	}
	varHash.put(varName,value);
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2005 2:20:46 PM)
 * @return java.lang.String
 */
public String getStdout() {
	return stdout;
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2005 2:21:05 PM)
 * @return double[][]
 * @param varName java.lang.String
 */
public double[][] getValue(String varName) {
	return (double[][])varHash.get(varName);
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2005 2:21:25 PM)
 * @return java.lang.String[]
 */
public String[] getVariableNames() {
	return (String[])org.vcell.util.BeanUtils.getArray(varHash.keys(),String.class);
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2005 2:20:36 PM)
 * @param stdout java.lang.String
 */
void setStdout(String argStdout) {
	this.stdout = argStdout;
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/2005 1:47:45 PM)
 */
public void show() {

	if (stdout != null) {
		System.out.println("StdOut = \""+ stdout+"\"");
	} else {
		System.out.println("StdOut = null");
	}
	for (int i = 0; i < getVariableNames().length; i++){
		double[][] values = getValue(getVariableNames()[i]);
		System.out.print(getVariableNames()[i]+"\t = [");
		for (int j = 0; j < values.length; j ++) {
			if (j > 0) {
				System.out.print("; ");
			}
			for (int k = 0; k < values[j].length; k++) {
				System.out.print(values[j][k]+"  ");
			}
		}
		System.out.println("]");
		 
	}
	System.out.println();
}
}

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
 * Creation date: (3/5/00 10:48:40 PM)
 * @author: 
 */
public class OptimizationException extends RuntimeException {
	private double[] parameterValues = null;

/**
 * OptimizationException constructor comment.
 * @param s java.lang.String
 */
public OptimizationException(String message) {
	super(message);
}


/**
 * OptimizationException constructor comment.
 * @param s java.lang.String
 */
public OptimizationException(String message, double[] argParameterValues) {
	super(message);
	this.parameterValues = argParameterValues;
}


/**
 * Insert the method's description here.
 * Creation date: (12/16/2005 2:22:46 PM)
 * @return java.lang.String
 */
public String getMessage() {
	StringBuffer buffer = new StringBuffer(super.getMessage());
	if (parameterValues!=null){
		buffer.append(" at parameters = [");
		for (int i = 0;parameterValues!=null && i < parameterValues.length; i++){
			if (i>0){
				buffer.append(",");
			}
			buffer.append(parameterValues[i]);
		}
		buffer.append("]");
	}
	return buffer.toString();
}


/**
 * Insert the method's description here.
 * Creation date: (12/16/2005 2:17:54 PM)
 * @return double[]
 */
public double[] getParameterValues() {
	return parameterValues;
}
}

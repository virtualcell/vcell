/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization;

import cbit.vcell.opt.Parameter;
/*
 * The class defines an element in a profile distribution. It contains the tuning parameter name,
 * the tuning parameter's current value, the squared error(likelihood) and the best estimates for 
 * other parameters with the tuning parameter fixed at the current value.
 * author: Tracy Li
 * version: 1.1
 */
public class ProfileDataElement 
{
	private String paramName = null;
	private double paramVal = 0;
	private double likelihood = 0;
	private Parameter[] bestParams = null;
	
	public ProfileDataElement(double arg_paramValue, double arg_likelihood, Parameter[] arg_bestParams)
	{
		this(null, arg_paramValue, arg_likelihood, arg_bestParams );
	}
	
	public ProfileDataElement(String arg_paramName, double arg_paramValue, double arg_likelihood, Parameter[] arg_bestParams)
	{
		this.paramName = arg_paramName;
		this.paramVal = arg_paramValue;
		this.likelihood = arg_likelihood;
		this.bestParams = arg_bestParams;
	}
	
	public String getParamName() {
		return paramName;
	}
	
	public double getParameterValue()
	{
		return paramVal;
	}
	public double getLikelihood()
	{
		return likelihood;
	}
	public Parameter[] getBestParameters()
	{
		return bestParams;
	}
}



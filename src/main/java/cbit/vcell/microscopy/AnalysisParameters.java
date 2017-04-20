/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.microscopy;

import cbit.vcell.opt.Parameter;

public class AnalysisParameters 
{
	private Parameter[] parameters = null;
	
	public Parameter[] getParameters() {
		return parameters;
	}

	public void setParameters(Parameter[] parameters) {
		this.parameters = parameters;
	}

	public AnalysisParameters(Parameter[] arg_parameters)
	{
		parameters = arg_parameters;
	}

	/*public boolean compareEqual(Matchable obj)
	{
		//should make Parameter implements matchable
//		Compare.isEqualOrNull(getParameters(), anaParameter.getParameters());
		return false;
	}*/

}

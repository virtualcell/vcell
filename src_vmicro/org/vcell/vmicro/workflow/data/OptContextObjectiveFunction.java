/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.vmicro.workflow.data;

import cbit.function.DefaultScalarFunction;

public class OptContextObjectiveFunction extends DefaultScalarFunction {
	
	private OptContext optContext = null;
	
	public OptContextObjectiveFunction(OptContext optContext){
		super();
		this.optContext = optContext;
	}
	
	@Override
	public double f(double[] x) {
		// 
		try{
		    double error = optContext.computeError(x);
		    return error;
		}catch(Exception e)
		{
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	@Override
	public int getNumArgs() {
		return 1;
	}
	

}


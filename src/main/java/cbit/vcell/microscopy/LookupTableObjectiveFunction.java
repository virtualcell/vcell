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

import cbit.function.DefaultScalarFunction;

public class LookupTableObjectiveFunction extends DefaultScalarFunction {

	private FRAPOptData optData = null;
	private boolean[] errorOfInterest = null;
	public LookupTableObjectiveFunction(FRAPOptData argOptData, boolean[] eoi)
	{
		super();
		optData = argOptData;
		errorOfInterest = eoi;
	}
	@Override
	public double f(double[] x) {
		// 
		try{
		    double error = optData.computeError(x, errorOfInterest);
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


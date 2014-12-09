/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.vmicro.workflow.scratch;

import org.vcell.vmicro.workflow.data.LocalWorkspace;

import cbit.function.DefaultScalarFunction;

public class LookupTableObjectiveFunction_old extends DefaultScalarFunction {

	private FRAPStudy frapStudy = null;
	private LocalWorkspace localWorkspace = null;
	private FRAPOptData optData = null;
	private boolean[] errorOfInterest = null;
	public LookupTableObjectiveFunction_old(FRAPOptData argOptData, boolean[] eoi, FRAPStudy frapStudy, LocalWorkspace localWorkspace)
	{
		super();
		optData = argOptData;
		errorOfInterest = eoi;
		this.frapStudy = frapStudy;
		this.localWorkspace = localWorkspace;
	}
	@Override
	public double f(double[] x) {
		// 
		try{
		    double error = optData.computeError(frapStudy, localWorkspace, x, errorOfInterest);
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


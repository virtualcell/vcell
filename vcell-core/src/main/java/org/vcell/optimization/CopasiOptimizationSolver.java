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

import cbit.vcell.modelopt.ParameterEstimationTask;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationResultSet;
import org.vcell.optimization.jtd.OptProblem;
import org.vcell.optimization.jtd.Vcellopt;


public class CopasiOptimizationSolver {
	public static OptimizationResultSet solveLocalPython(ParameterEstimationTask parameterEstimationTask)
			throws OptimizationException {
		
		try {
			parameterEstimationTask.refreshMappings();
			OptProblem optProblem = CopasiUtils.paramTaskToOptProblem(parameterEstimationTask);
			Vcellopt optRun = CopasiUtils.runCopasiParameterEstimation(optProblem);
			OptimizationResultSet copasiOptimizationResultSet = CopasiUtils.toOptResults(
					optRun,parameterEstimationTask, new ParameterEstimationTaskSimulatorIDA());
			return copasiOptimizationResultSet;
		} catch (Exception e){
			throw new OptimizationException(e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), e);
		}
	}
		
}

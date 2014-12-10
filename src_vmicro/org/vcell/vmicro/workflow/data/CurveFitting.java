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
import java.io.File;
import java.io.IOException;

import org.vcell.optimization.DefaultOptSolverCallbacks;
import org.vcell.optimization.OptSolverCallbacks;

import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.opt.ExplicitFitObjectiveFunction;
import cbit.vcell.opt.OptSolverResultSet;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.PdeObjectiveFunction;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.opt.SpatialReferenceData;
import cbit.vcell.opt.Weights;
import cbit.vcell.opt.solvers.NewOptimizationSolver;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
/**
 * Fitting utilities 
 */
public class CurveFitting {
	
	public static OptimizationResultSet solve(ExplicitFitObjectiveFunction.ExpressionDataPair[] expDataPairs, Parameter[] parameters, double[] time, double[][] data, String[] colNames, Weights weights) throws ExpressionException, OptimizationException, IOException {

		//choose optimization solver, currently we have Powell and CFSQP 
		NewOptimizationSolver optService = new NewOptimizationSolver();
		OptimizationSpec optSpec = new OptimizationSpec();
		//create simple reference data, columns: t + dataColumns
		double[][] realData = new double[1 + data.length][time.length];
		for(int i=0; i<time.length; i++) //add time column
		{
			realData[0][i] = time[i];
		}
		for(int i=0; i<data.length; i++) //add each data column to realData
		{
			for(int j=0; j<time.length; j++)
			{
				realData[1+i][j] = data[i][j];
			}
		}
		SimpleReferenceData refData = new SimpleReferenceData(colNames, weights, realData);	
		
		//send to optimization service	
		optSpec.setObjectiveFunction(new ExplicitFitObjectiveFunction(expDataPairs, refData));

		double parameterValues[] = new double[parameters.length];
		for (int i = 0; i < parameters.length; i++){
			parameterValues[i] = parameters[i].getInitialGuess();
			System.out.println("initial "+parameters[i].getName()+" = "+parameterValues[i] + ";\tLB = " + parameters[i].getLowerBound() + ";\tUB = " + parameters[i].getUpperBound());
		}
		// Add parameters to the optimizationSpec
		// get the initial guess to send it to the f() function. ....
		for (int i = 0; i < parameters.length; i++) {
			optSpec.addParameter(parameters[i]);
		}
		//Parameters in OptimizationSolverSpec are solver type and objective function change tolerance. 
		OptimizationSolverSpec optSolverSpec = new OptimizationSolverSpec(OptimizationSolverSpec.SOLVERTYPE_CFSQP,0.000001);
		OptSolverCallbacks optSolverCallbacks = new DefaultOptSolverCallbacks();
		OptimizationResultSet optResultSet = null;
		optResultSet = optService.solve(optSpec, optSolverSpec, optSolverCallbacks);
		
		//uncomment following statements for debug purpose
		/*OptSolverResultSet optSolverResultSet = optResultSet.getOptSolverResultSet();
		String[] paramNames = optSolverResultSet.getParameterNames();
		double[] paramValues = optSolverResultSet.getBestEstimates();
		
		for (int i = 0; i < paramNames.length; i++) {
			System.out.println("finally:   "+paramNames[i]+" = "+paramValues[i]);
		}
		optSolverCallbacks.showStatistics();*/ 

		return optResultSet;
	}

	public static OptimizationResultSet solveSpatial(Simulation sim, Parameter[] parameters, SpatialReferenceData refData, File dataDir, FieldDataIdentifierSpec[] fieldDataIDSs) throws ExpressionException, OptimizationException, IOException {

		//choose optimization solver, currently we have Powell and CFSQP 
		NewOptimizationSolver optService = new NewOptimizationSolver();
		OptimizationSpec optSpec = new OptimizationSpec();

		//send to optimization service	
		optSpec.setObjectiveFunction(new PdeObjectiveFunction(sim.getMathDescription(), refData, dataDir, fieldDataIDSs));

		double parameterValues[] = new double[parameters.length];
		for (int i = 0; i < parameters.length; i++){
			parameterValues[i] = parameters[i].getInitialGuess();
			System.out.println("initial "+parameters[i].getName()+" = "+parameterValues[i] + ";\tLB = " + parameters[i].getLowerBound() + ";\tUB = " + parameters[i].getUpperBound());
		}
		// Add parameters to the optimizationSpec
		// get the initial guess to send it to the f() function. ....
		for (int i = 0; i < parameters.length; i++) {
			optSpec.addParameter(parameters[i]);
		}
		//Parameters in OptimizationSolverSpec are solver type and objective function change tolerance. 
		OptimizationSolverSpec optSolverSpec = new OptimizationSolverSpec(OptimizationSolverSpec.SOLVERTYPE_CFSQP,0.000001);
		OptSolverCallbacks optSolverCallbacks = new DefaultOptSolverCallbacks();

		OptimizationResultSet optResultSet = null;
		
		optResultSet = optService.solve(optSpec, optSolverSpec, optSolverCallbacks);
		
		OptSolverResultSet optSolverResultSet = optResultSet.getOptSolverResultSet();
		String[] paramNames = optSolverResultSet.getParameterNames();
		double[] paramValues = optSolverResultSet.getBestEstimates();
		for (int i = 0; i < paramNames.length; i++) {
			System.out.println("finally:   "+paramNames[i]+" = "+paramValues[i]);
		}
		return optResultSet;
	}
	
}//end of class

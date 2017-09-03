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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.vcell.optimization.DefaultOptSolverCallbacks;
import org.vcell.optimization.OptSolverCallbacks;

import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.opt.ExplicitFitObjectiveFunction;
import cbit.vcell.opt.OptSolverResultSet;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.OptimizationStatus;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.PdeObjectiveFunction;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.opt.SpatialReferenceData;
import cbit.vcell.opt.VariableWeights;
import cbit.vcell.opt.Weights;
import cbit.vcell.opt.solvers.PowellOptimizationSolver;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionBindingException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.solver.Simulation;
/**
 * Fitting utilities 
 */
public class CurveFitting {
	
	public static Expression fitBleachWhileMonitoring(double[] time, double[] normalized_fluor, double[] outputParam) throws ExpressionException, OptimizationException, IOException
	{
		return CurveFitting.fitBleachWhileMonitoring(time, normalized_fluor, outputParam, null); 
	}

	/*
	 * @para: time, time points since the first post bleach
	 * @para: flour, average intensities under bleached region according to time points since the first post bleach
	 * @para: parameterValues, the array which will pass results back 
	 */
	public static Expression fitBleachWhileMonitoring(double[] time, double[] normalized_fluor, double[] outputParam, Weights weights) throws ExpressionException, OptimizationException, IOException
	{
		if (time.length!=normalized_fluor.length){
			throw new RuntimeException("Fluorecence and time arrays must be the same length");
		}
		//normaliztion for time by subtracting the starting time: time[0]
		double[] normalized_time = new double[time.length];
		for (int i = 0; i < time.length; i++){
			normalized_time[i] = time[i]-time[0];
		}
		Expression modelExp = null;
		OptimizationResultSet optResultSet = null;
		String[] paramNames = null;
		double[] paramValues = null;

		double cellFirstPostBleach = normalized_fluor[0];
		modelExp = new Expression(FRAPOptFunctions.FUNC_CELL_INTENSITY);
		modelExp.substituteInPlace(new Expression(FRAPOptFunctions.SYMBOL_I_inicell), new Expression(cellFirstPostBleach));
		// initialize starting guess, arguments in Parameter are name, Lower Bound, Upper Bound, Scale, Initial Guess
		Parameter parameters[] = new Parameter[] {new Parameter(FRAPOptFunctions.SYMBOL_BWM_RATE,
																FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
																FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
																FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),
																FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getInitialGuess())};
		
		bindExpressionToParametersAndTime(modelExp, parameters);

		// estimate blech while monitoring rate by minimizing the error between funtion values and reference data
		if(weights == null)
		{
			optResultSet = solve(modelExp,parameters,normalized_time,normalized_fluor);
		}
		else
		{
			optResultSet = solve(modelExp, parameters,normalized_time,normalized_fluor, weights);
		}
		OptSolverResultSet optSolverResultSet = optResultSet.getOptSolverResultSet();
		paramNames = optSolverResultSet.getParameterNames();
		paramValues = optSolverResultSet.getBestEstimates();
		// copy into "output" buffer for parameter values.
		for (int i = 0; i < paramValues.length; i++) {
			outputParam[i] = paramValues[i]; 
		}

		System.out.println(optSolverResultSet.getOptimizationStatus().toString());
		for (int i = 0; i < paramNames.length; i++) {
			System.out.println("finally:   "+paramNames[i]+" = "+paramValues[i]);
		}
		//investigate the return information 
		processReturnCode(OptimizationSolverSpec.SOLVERTYPE_POWELL, optSolverResultSet);
		
		//
		// construct final equation
		// 
		Expression fit = new Expression(modelExp);

		System.out.println("bleach while monitoring fit before subsituting parameters:"+fit.infix());
		//
		// substitute parameter values
		//
		for (int i = 0; i < paramValues.length; i++) {
			fit.substituteInPlace(new Expression(paramNames[i]), new Expression(paramValues[i]));
		}
		//
		// undo time shift
		//
		fit.substituteInPlace(new Expression(ReservedVariable.TIME.getName()), new Expression(ReservedVariable.TIME.getName()+"-"+time[0]));
		//
		// undo fluorescence normalization
		//
		System.out.println("bleach while monitoring fit equation after unnorm:" + fit.infix());
		return fit;
	}

	private static void bindExpressionToParametersAndTime(Expression modelExp, Parameter[] parameters)
			throws ExpressionBindingException {
		ArrayList<String> symbols = new ArrayList<String>();
		symbols.add("t");
		for (Parameter p : parameters){
			symbols.add(p.getName());
		}
		modelExp.bindExpression(new SimpleSymbolTable(symbols.toArray(new String[0])));
	}

	/*
	 * @para: time, time points since the first post bleach.
	 * @para: flour, average intensities under bleached region according to time points since the first post bleach.
	 * @para: bleachType, gaussian spot of half cell bleaching. model expressions vary based on bleaching type.
	 * @para: inputparam, bleaching while monitoring rate, will be substituted in the model expression.
	 * @para: outputParam, the array which will pass results back. 
	 */
	public static Expression fitRecovery_diffOnly(double[] time, double[] normalized_fluor, int bleachType, double[] inputparam, double[] outputParam) throws ExpressionException, OptimizationException, IOException
	{

		if (time.length!=normalized_fluor.length){
			throw new RuntimeException("Fluorecence and time arrays must be the same length");
		}

		//normaliztion for time by subtracting the starting time: time[0]
		double[] normalized_time = new double[time.length];
		for (int i = 0; i < time.length; i++){
			normalized_time[i] = time[i]-time[0];
		}
		Expression modelExp = null;
		OptimizationResultSet optResultSet = null;
		OptSolverResultSet optSolverResultSet = null;
		String[] paramNames = null;
		double[] paramValues = null;

		if(bleachType == FrapDataAnalysisResults.DiffusionOnlyAnalysisRestults.BleachType_GaussianSpot || bleachType == FrapDataAnalysisResults.DiffusionOnlyAnalysisRestults.BleachType_HalfCell){
			if(bleachType == FrapDataAnalysisResults.DiffusionOnlyAnalysisRestults.BleachType_GaussianSpot)
			{
				Expression muExp = new Expression(FRAPDataAnalysis.gaussianSpot_MuFunc);
				modelExp = new Expression(FRAPDataAnalysis.gaussianSpot_IntensityFunc);
				modelExp.substituteInPlace(new Expression(FRAPDataAnalysis.symbol_u), muExp);
			}
			else
			{
				Expression muExp = new Expression(FRAPDataAnalysis.halfCell_MuFunc);
				modelExp = new Expression(FRAPDataAnalysis.halfCell_IntensityFunc);
				modelExp.substituteInPlace(new Expression(FRAPDataAnalysis.symbol_u), muExp);
			}
			//inputparam[0] is the bleach while monitoring rate.
			double bleachWhileMonitoringRate = inputparam[0];
			modelExp = modelExp.getSubstitutedExpression(new Expression(FRAPDataAnalysis.symbol_bwmRate), new Expression(bleachWhileMonitoringRate));
			Parameter parameters[] = new Parameter[] {FRAPDataAnalysis.para_If, FRAPDataAnalysis.para_Io, FRAPDataAnalysis.para_tau};
			bindExpressionToParametersAndTime(modelExp, parameters);
			//estimate parameters by minimizing the errors between function values and reference data
			optResultSet = solve(modelExp.flatten(),parameters,normalized_time,normalized_fluor);
			
			optSolverResultSet = optResultSet.getOptSolverResultSet();
			paramNames = optSolverResultSet.getParameterNames();
			paramValues = optSolverResultSet.getBestEstimates();

			// copy into "output" buffer from parameter values.
			for (int i = 0; i < paramValues.length; i++) {
				outputParam[i] = paramValues[i]; 
			}
		}else{
			throw new IllegalArgumentException("Unknown bleach type "+bleachType);
		}

		for (int i = 0; i < paramNames.length; i++) {
			System.out.println("finally:   "+paramNames[i]+" = "+paramValues[i]);
		}
		//investigate the return information 
		processReturnCode(OptimizationSolverSpec.SOLVERTYPE_POWELL, optSolverResultSet);
		
		// construct recovery under bleached region by diffusion only expression
		Expression fit = new Expression(modelExp);

		System.out.println("fit before subsituting parameters:"+fit.infix());
		
		// substitute parameter values
		for (int i = 0; i < paramValues.length; i++) {
			fit.substituteInPlace(new Expression(paramNames[i]), new Expression(paramValues[i]));
		}
		
		// undo time shift
		fit.substituteInPlace(new Expression(ReservedVariable.TIME.getName()), new Expression(ReservedVariable.TIME.getName()+"-"+time[0]));
		
		// undo fluorescence normalization
		System.out.println("fit equation after unnorm:" + fit.infix());
		return fit;
	}

	/*
	 * @para: time, time points since the first post bleach.
	 * @para: normalized_data, first dimension index 0:average intensities under cell region, first dimension index 1: average intensities under bleached region
	 * @para: inputparam, index 0:cellROIAvg at time 0(first post bleach). index 1:bleachedROIAvg at time 0(first post bleach).
	 * @para: outputParam, results for 2 or 3 parameters, depending on if there is any fixed parameter. 
	 */
	public static double fitRecovery_reacKoffRateOnly(double[] time, double[][] normalized_data, double[] inputparam, double[] outputParam, Parameter fixedParameter, Weights weights) throws ExpressionException, OptimizationException, IOException
	{
		if (normalized_data != null && normalized_data.length > 0)
		{
			for(int i=0; i<normalized_data.length; i++)
			{
			    if(normalized_data[i] != null && time.length != normalized_data[i].length)
			    {
				    throw new RuntimeException("Fluorecence and time arrays must be the same length");
			    }
			}
		}

		//normaliztion for time by subtracting the starting time: time[0]
		double[] normalized_time = new double[time.length];
		for (int i = 0; i < time.length; i++){
			normalized_time[i] = time[i]-time[0];
		}
		//initiate variables
		OptimizationResultSet optResultSet = null;
		Expression bwmRateExp = new Expression(FRAPOptFunctions.FUNC_CELL_INTENSITY);
		Expression koffRateExp = new Expression(FRAPOptFunctions.FUNC_RECOVERY_BLEACH_REACTION_DOMINANT);
		//get parameters to be estimated(use all of the 3 if there is no fixed parameter. Or 2 out of the 3 if there is a fixed parameter)
		Parameter bwmParam = new Parameter(FRAPOptFunctions.SYMBOL_BWM_RATE,
							 FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
							 FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
							 FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),
							 FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getInitialGuess());
		Parameter koffParam = new Parameter(FRAPOptFunctions.SYMBOL_KOFF,
							 FRAPModel.REF_REACTION_OFF_RATE.getLowerBound(),
							 FRAPModel.REF_REACTION_OFF_RATE.getUpperBound(),
							 FRAPModel.REF_REACTION_OFF_RATE.getScale(),
							 FRAPModel.REF_REACTION_OFF_RATE.getInitialGuess());
		Parameter fittingParamA = new Parameter(FRAPOptFunctions.SYMBOL_A, /*binding site concentration is reused to store fitting parameter A, but the name can not be reused*/
							 FRAPModel.REF_BS_CONCENTRATION_OR_A.getLowerBound(),
							 FRAPModel.REF_BS_CONCENTRATION_OR_A.getUpperBound(),
							 FRAPModel.REF_BS_CONCENTRATION_OR_A.getScale(),
							 FRAPModel.REF_BS_CONCENTRATION_OR_A.getInitialGuess());
		
		//get column names for reference data to be constructed
		String[] columnNames = new String[]{ReservedVariable.TIME.getName(), "cellIntensityAvg", "bleachIntensityAvg"};
		
		if(fixedParameter == null)
		{
			//get fitting parameter array
			Parameter[] parameters = new Parameter[]{bwmParam, fittingParamA, koffParam};
			//get expression pairs
			bwmRateExp = bwmRateExp.getSubstitutedExpression(new Expression(FRAPOptFunctions.SYMBOL_I_inicell), new Expression(inputparam[0]));
			bindExpressionToParametersAndTime(bwmRateExp, parameters);
			ExplicitFitObjectiveFunction.ExpressionDataPair bwmRateExpDataPair = new ExplicitFitObjectiveFunction.ExpressionDataPair(bwmRateExp, 1);
			koffRateExp = koffRateExp.getSubstitutedExpression(new Expression(FRAPOptFunctions.SYMBOL_I_inibleached), new Expression(inputparam[1]));
			bindExpressionToParametersAndTime(koffRateExp, parameters);
			ExplicitFitObjectiveFunction.ExpressionDataPair koffRateExpDataPair = new ExplicitFitObjectiveFunction.ExpressionDataPair(koffRateExp, 2);
			ExplicitFitObjectiveFunction.ExpressionDataPair[] expDataPairs = new ExplicitFitObjectiveFunction.ExpressionDataPair[]{bwmRateExpDataPair, koffRateExpDataPair};
			//solve
			optResultSet = solve(expDataPairs, parameters, normalized_time, normalized_data, columnNames, weights);
			
		}
		else
		{
			if(fixedParameter != null && fixedParameter.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE]))
			{
				//get fitting parameter array
				Parameter[] parameters = new Parameter[]{fittingParamA, koffParam};
				//get expression pairs
				bwmRateExp = bwmRateExp.getSubstitutedExpression(new Expression(FRAPOptFunctions.SYMBOL_I_inicell), new Expression(inputparam[0]));
				bwmRateExp = bwmRateExp.getSubstitutedExpression(new Expression(FRAPOptFunctions.SYMBOL_BWM_RATE), new Expression(fixedParameter.getInitialGuess()));
				bindExpressionToParametersAndTime(bwmRateExp, parameters);
				ExplicitFitObjectiveFunction.ExpressionDataPair bwmRateExpDataPair = new ExplicitFitObjectiveFunction.ExpressionDataPair(bwmRateExp, 1);
				koffRateExp = koffRateExp.getSubstitutedExpression(new Expression(FRAPOptFunctions.SYMBOL_I_inibleached), new Expression(inputparam[1]));
				koffRateExp = koffRateExp.getSubstitutedExpression(new Expression(FRAPOptFunctions.SYMBOL_BWM_RATE), new Expression(fixedParameter.getInitialGuess()));
				bindExpressionToParametersAndTime(koffRateExp, parameters);
				ExplicitFitObjectiveFunction.ExpressionDataPair koffRateExpDataPair = new ExplicitFitObjectiveFunction.ExpressionDataPair(koffRateExp, 2);
				ExplicitFitObjectiveFunction.ExpressionDataPair[] expDataPairs = new ExplicitFitObjectiveFunction.ExpressionDataPair[]{bwmRateExpDataPair, koffRateExpDataPair};
				//solve
				optResultSet = solve(expDataPairs, parameters, normalized_time, normalized_data, columnNames, weights);
			}
			else if(fixedParameter != null && fixedParameter.getName().equals(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE]))
			{
				//get fitting parameter array
				Parameter[] parameters = new Parameter[]{bwmParam, fittingParamA};
				//get expression pairs
				bwmRateExp = bwmRateExp.getSubstitutedExpression(new Expression(FRAPOptFunctions.SYMBOL_I_inicell), new Expression(inputparam[0]));
				bindExpressionToParametersAndTime(bwmRateExp, parameters);
				ExplicitFitObjectiveFunction.ExpressionDataPair bwmRateExpDataPair = new ExplicitFitObjectiveFunction.ExpressionDataPair(bwmRateExp, 1);
				koffRateExp = koffRateExp.getSubstitutedExpression(new Expression(FRAPOptFunctions.SYMBOL_I_inibleached), new Expression(inputparam[1]));
				koffRateExp = koffRateExp.getSubstitutedExpression(new Expression(FRAPOptFunctions.SYMBOL_KOFF), new Expression(fixedParameter.getInitialGuess()));
				bindExpressionToParametersAndTime(koffRateExp, parameters);
				ExplicitFitObjectiveFunction.ExpressionDataPair koffRateExpDataPair = new ExplicitFitObjectiveFunction.ExpressionDataPair(koffRateExp, 2);
				ExplicitFitObjectiveFunction.ExpressionDataPair[] expDataPairs = new ExplicitFitObjectiveFunction.ExpressionDataPair[]{bwmRateExpDataPair, koffRateExpDataPair};
				//solve
				optResultSet = solve(expDataPairs, parameters, normalized_time, normalized_data, columnNames, weights);
			}
		}
		//get output parameter values		
		OptSolverResultSet optSolverResultSet = optResultSet.getOptSolverResultSet();
		String[] paramNames = optSolverResultSet.getParameterNames();
		double[] paramValues = optSolverResultSet.getBestEstimates();
		//copy into "output" buffer from parameter values.
		for (int i = 0; i < paramValues.length; i++) {
			outputParam[i] = paramValues[i]; 
		}
		//for debug purpose
		for (int i = 0; i < paramNames.length; i++) {
			System.out.println("finally:   "+paramNames[i]+" = "+paramValues[i]);
		}
		//investigate the return information 
		processReturnCode(OptimizationSolverSpec.SOLVERTYPE_POWELL, optSolverResultSet);
		//return objective function value
		return optSolverResultSet.getLeastObjectiveFunctionValue();
	}

	//legacy method, which takes one explicit function to fit one reference data column (colIndex = 1, index 0 is time column) with no Weights(or considered as the only one dependent variable weight is 1)
	public static OptimizationResultSet solve(Expression modelExp, Parameter[] parameters, double[] time, double[] data) throws ExpressionException, OptimizationException, IOException {
		return CurveFitting.solve(modelExp, parameters, time, data, null);
	}
	//legacy method, which taks one explicit function to fit one reference data column (colIndex = 1, index 0 is time column)
	//if weights is null, set the only one dependent variable weight is 1
	public static OptimizationResultSet solve(Expression modelExp, Parameter[] parameters, double[] time, double[] data, Weights weights) throws ExpressionException, OptimizationException, IOException {
		//one fit function and data pair
		ExplicitFitObjectiveFunction.ExpressionDataPair[] expDataPairs = new ExplicitFitObjectiveFunction.ExpressionDataPair[1];
		expDataPairs[0] = new ExplicitFitObjectiveFunction.ExpressionDataPair(modelExp, 1);
		//one column of reference data in two dimensional array
		double[][] refData = new double[1][];
		refData[0] = data;
		//column names
		String[] colNames = new String[]{ReservedVariable.TIME.getName(), "intensity"};
		//weights
		Weights dataWeights = weights;
		if(dataWeights == null)
		{
			dataWeights = new VariableWeights(new double[]{1.0});
		}
		return CurveFitting.solve(expDataPairs, parameters, time, refData, colNames, dataWeights);
	}
	
	public static OptimizationResultSet solve(ExplicitFitObjectiveFunction.ExpressionDataPair[] expDataPairs, Parameter[] parameters, double[] time, double[][] data, String[] colNames, Weights weights) throws ExpressionException, OptimizationException, IOException {

		//choose optimization solver, currently we have Powell 
		PowellOptimizationSolver optService = new PowellOptimizationSolver();
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
		OptimizationSolverSpec optSolverSpec = new OptimizationSolverSpec(OptimizationSolverSpec.SOLVERTYPE_POWELL,0.000001);
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

		//choose optimization solver, currently we have Powell 
		PowellOptimizationSolver optService = new PowellOptimizationSolver();
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
		OptimizationSolverSpec optSolverSpec = new OptimizationSolverSpec(OptimizationSolverSpec.SOLVERTYPE_POWELL,0.000001);
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
	
	private static void processReturnCode(String solverType, OptSolverResultSet optResultSet) throws OptimizationException
	{
		int returnCode = optResultSet.getOptimizationStatus().getReturnCode(); 
		//the normal termination code for both CFSQP and POWELL is 0. 
		if(solverType.equals(OptimizationSolverSpec.SOLVERTYPE_POWELL))
		{
			String messageToUser = "Return code is: " + returnCode + ". The explaination of the return code is : \n" + optResultSet.getOptimizationStatus().getReturnMessage();
			//if return code is abnormal termination, but we still can get best solution, let the program continue
			//exception only will be thrown when return code is abnormal and not best solution acquired.
			if(returnCode != OptimizationStatus.NORMAL_TERMINATION &&
			   optResultSet.getBestEstimates() == null)
			{
				throw new OptimizationException(messageToUser);
			}
		}
	}
}//end of class

package cbit.vcell.microscopy;
import java.io.File;
import java.io.IOException;

import org.vcell.optimization.OptSolverResultSet;

import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.opt.ExplicitFitObjectiveFunction;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.PdeObjectiveFunction;
import cbit.vcell.opt.SimpleReferenceData;
import cbit.vcell.opt.SpatialReferenceData;
import cbit.vcell.opt.solvers.NewOptimizationSolver;
import cbit.vcell.opt.solvers.OptSolverCallbacks;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.Simulation;
/**
 * Fitting utilities 
 */
public class CurveFitting {

	/*
	 * @para: time, time points since the first post bleach
	 * @para: flour, average intensities under bleached region according to time points since the first post bleach
	 * @para: parameterValues, the array which will pass results back 
	 */
	public static Expression fitBleachWhileMonitoring(double[] time, double[] normalized_fluor, double[] outputParam) throws ExpressionException, OptimizationException, IOException
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
		Parameter parameters[] = new Parameter[] {new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE],
																FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getLowerBound(),
																FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getUpperBound(),
																FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getScale(),
																FRAPModel.REF_BLEACH_WHILE_MONITOR_PARAM.getInitialGuess())};
		// estimate blech while monitoring rate by minimizing the error between funtion values and reference data
		optResultSet = solve(modelExp.flatten(),parameters,normalized_time,normalized_fluor);
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
		if (optSolverResultSet.getOptimizationStatus().isFailed()){
			//throw new OptimizationException("optimization failed",paramValues);
		}
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
		fit.substituteInPlace(new Expression("t"), new Expression("t-"+time[0]));
		//
		// undo fluorescence normalization
		//
		System.out.println("bleach while monitoring fit equation after unnorm:" + fit.infix());
		return fit;
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
		if (optSolverResultSet.getOptimizationStatus().isFailed()){
			throw new OptimizationException("optimization failed",paramValues);
		}

		//
		// construct recovery under bleached region by diffusion only expression
		// 
		Expression fit = new Expression(modelExp);

		System.out.println("fit before subsituting parameters:"+fit.infix());
		//
		// substitute parameter values
		//
		for (int i = 0; i < paramValues.length; i++) {
			fit.substituteInPlace(new Expression(paramNames[i]), new Expression(paramValues[i]));
		}
		//
		// undo time shift
		//
		fit.substituteInPlace(new Expression("t"), new Expression("t-"+time[0]));
		//
		// undo fluorescence normalization
		//
		System.out.println("fit equation after unnorm:" + fit.infix());
		return fit;
	}

	/*
	 * @para: time, time points since the first post bleach.
	 * @para: flour, average intensities under bleached region according to time points since the first post bleach.
	 * @para: inputparam, bleaching while monitoring rate, will be substituted in the model expression.
	 * @para: outputParam, the array which will pass results back. 
	 */
	public static Expression fitRecovery_reacKoffRateOnly(double[] time, double[] normalized_fluor, double[] inputparam, double[] outputParam) throws ExpressionException, OptimizationException, IOException
	{

		if (time.length!=normalized_fluor.length){
			throw new RuntimeException("Fluorecence and time arrays must be the same length");
		}

		//normaliztion for time by subtracting the starting time: time[0]
		double[] normalized_time = new double[time.length];
		for (int i = 0; i < time.length; i++){
			normalized_time[i] = time[i]-time[0];
		}
		Expression koffRateExp = null;
		OptimizationResultSet optResultSet = null;
		OptSolverResultSet optSolverResultSet = null;
		String[] paramNames = null;//estimated results' names.
		double[] paramValues = null;//estimated results' values for output.

		koffRateExp = new Expression(FRAPOptFunctions.FUNC_RECOVERY_BLEACH_REACTION_DOMINANT);
		//inputparam[0] is the first post bleach, inputparam[1] is the  bleach while monitoring rate.
		//substitute first post bleach and bleach while monitoring rate in the off rate expression.
		double iniBleachedIntensity = inputparam[0];
		double bleachWhileMonitoringRate = inputparam[1];
		koffRateExp = koffRateExp.getSubstitutedExpression(new Expression(FRAPOptFunctions.SYMBOL_I_inibleached), new Expression(iniBleachedIntensity));
		koffRateExp = koffRateExp.getSubstitutedExpression(new Expression(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_BLEACH_MONITOR_RATE]), new Expression(bleachWhileMonitoringRate));
		
		Parameter koffParam = new Parameter(FRAPModel.MODEL_PARAMETER_NAMES[FRAPModel.INDEX_OFF_RATE],
											FRAPModel.REF_REACTION_OFF_RATE.getLowerBound(),
											FRAPModel.REF_REACTION_OFF_RATE.getUpperBound(),
											FRAPModel.REF_REACTION_OFF_RATE.getScale(),
											FRAPModel.REF_REACTION_OFF_RATE.getInitialGuess());
		Parameter fittingParamA = new Parameter(FRAPOptFunctions.SYMBOL_A, /*binding site concentration is reused to store fitting parameter A, but the name can not be reused*/
												FRAPModel.REF_BS_CONCENTRATION_OR_A.getLowerBound(),
												FRAPModel.REF_BS_CONCENTRATION_OR_A.getUpperBound(),
												FRAPModel.REF_BS_CONCENTRATION_OR_A.getScale(),
												FRAPModel.REF_BS_CONCENTRATION_OR_A.getInitialGuess());
		Parameter parameters[] = new Parameter[]{koffParam, fittingParamA};
		//estimate parameters by minimizing the errors between function values and reference data
		optResultSet = solve(koffRateExp.flatten(), parameters, normalized_time, normalized_fluor);
		
		optSolverResultSet = optResultSet.getOptSolverResultSet();
		paramNames = optSolverResultSet.getParameterNames();
		paramValues = optSolverResultSet.getBestEstimates();

		// copy into "output" buffer from parameter values.
		for (int i = 0; i < paramValues.length; i++) {
			outputParam[i] = paramValues[i]; 
		}

		for (int i = 0; i < paramNames.length; i++) {
			System.out.println("finally:   "+paramNames[i]+" = "+paramValues[i]);
		}
		
		if (optSolverResultSet.getOptimizationStatus().isFailed()){
			throw new OptimizationException("optimization failed",paramValues);
		}

		//
		// construct recovery under bleached region by reaction only expression
		// 
		Expression fit = new Expression(koffRateExp);

		System.out.println("fit before subsituting parameters:"+fit.infix());
		//
		// substitute parameter values
		//
		for (int i = 0; i < paramValues.length; i++) {
			fit.substituteInPlace(new Expression(paramNames[i]), new Expression(paramValues[i]));
		}
		//
		// undo time shift
		//
		fit.substituteInPlace(new Expression("t"), new Expression("t-"+time[0]));
		//
		// undo fluorescence normalization
		//
		System.out.println("fit equation after unnorm:" + fit.infix());
		return fit;
	}
	
	public static OptimizationResultSet solve(Expression modelExp, Parameter[] parameters, double[] time, double[] data) throws ExpressionException, OptimizationException, IOException {

		if (time.length!=data.length){
			throw new RuntimeException("arrays must be the same length");
		}

		//choose optimization solver, currently we have Powell and CFSQP 
		NewOptimizationSolver optService = new NewOptimizationSolver();
		OptimizationSpec optSpec = new OptimizationSpec();
		//create simple reference data
		double[][] realData = new double[2][time.length];
		for(int i=0; i<time.length; i++)
		{
			realData[0][i] = time[i];
			realData[1][i]= data[i];
		}
		String[] colNames = new String[]{"t", "intensity"};
		double[] weights = new double[]{1.0,1.0};
		SimpleReferenceData refData = new SimpleReferenceData(colNames, weights, realData);
		//send to optimization service	
		optSpec.setObjectiveFunction(new ExplicitFitObjectiveFunction(modelExp, refData));

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
		OptSolverCallbacks optSolverCallbacks = new OptSolverCallbacks();

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
		OptSolverCallbacks optSolverCallbacks = new OptSolverCallbacks();

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
}
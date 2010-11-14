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
		modelExp = new Expression("cell_firstPostBleach*exp(-bleachRate*t)");
		modelExp.substituteInPlace(new Expression("cell_firstPostBleach"), new Expression(cellFirstPostBleach));
		// initialize starting guess, arguments in Parameter are name, Lower Bound, Upper Bound, Scale, Initial Guess
		Parameter parameters[] = new Parameter[] {new Parameter("bleachRate",0,0.1,1,0.001)};

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
	 * @para: time, time points since the first post bleach
	 * @para: flour, average intensities under bleached region according to time points since the first post bleach
	 * @para: parameterValues, the array which will pass results back 
	 */
	public static Expression fitRecovery(double[] time, double[] normalized_fluor, int bleachType, double[] inputparam, double[] outputParam) throws ExpressionException, OptimizationException, IOException
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

		if(bleachType == FrapDataAnalysisResults.BleachType_GaussianSpot || bleachType == FrapDataAnalysisResults.BleachType_HalfCell){
			if(bleachType == FrapDataAnalysisResults.BleachType_GaussianSpot)
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
			cbit.vcell.opt.Parameter parameters[] = new cbit.vcell.opt.Parameter[] {
					FRAPDataAnalysis.para_If, FRAPDataAnalysis.para_Io, FRAPDataAnalysis.para_tau};

			try {
				optResultSet = solve(modelExp.flatten(),parameters,normalized_time,normalized_fluor);
			} catch (OptimizationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
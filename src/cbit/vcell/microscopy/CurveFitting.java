package cbit.vcell.microscopy;
import java.io.File;
import java.io.IOException;


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
import cbit.vcell.parser.SimpleSymbolTable;
import cbit.vcell.solver.Simulation;
/**
 * Insert the type's description here.
 * Creation date: (2/2/2007 1:45:35 PM)
 * @author: Anuradha Lakshminarayana
 */
public class CurveFitting {


/**
 * Insert the method's description here.
 * Creation date: (2/2/2007 2:15:30 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		final double TIME_STEP = 0.324;
		final double TIME_OFFSET = 1.62;
		final double[] fluor_unscaled = new double[] {
				11552.65882,
				14380.63529,
				17219.81176,
				19456.72941,
				21508.85882,
				23357.58824,
				24638.8,
				25526.50588,
				26438.14118,
				27556.28235,
				28058.96471,
				29018.55294,
				29639.2,
				30348.07059,
				30291.31765,
				31041.47059,
				31251.67059,
				31749.76471,
				32302.08235,
				32633.09412,
				32546.72941,
				32745.67059,
				33406.18824,
				33367.82353,
				33475.75294,
				33723.85882,
				33760.27059,
				33784.67059,
				34246.34118,
				34121.54118,
				34439.02353,
				34458.61176,
				34671.17647,
				34811.91765,
				34587.48235,
				34848.22353,
				34816.62353,
				35011.38824,
				34970.81176,
				35146.96471,
				35215.71765,
				35488.35294,
				35146.27059,
				35476.6,
				35895.44706,
				35682.71765,
				36044.77647,
				35601.25882,
				35943.94118,
				35693.74118,
				35713.76471,
				35850.62353,
				35734.85882,
				35655.89412,
				35846.15294,
				35831.16471,
				35590.17647,
				36178.97647,
				35916.8,
				36319.8,
				35816.22353,
				35923.18824,
				35742.48235,
				36011.63529,
				35843.18824,
				35910.81176,
				35829.72941,
				35694.65882,
				36084.75294,
				35906.32941
		};
		final double[] fluor_scaled = new double[] {
				0,
				0.11612116,
				0.232702209,
				0.324553564,
				0.408817224,
				0.48472896,
				0.537337529,
				0.573788128,
				0.611221306,
				0.657133941,
				0.677774869,
				0.71717707,
				0.742661814,
				0.771769155,
				0.76943879,
				0.800241249,
				0.808872391,
				0.829324919,
				0.85200395,
				0.865595813,
				0.862049542,
				0.87021838,
				0.897340273,
				0.895764958,
				0.900196709,
				0.910384327,
				0.911879451,
				0.912881353,
				0.931838273,
				0.926713789,
				0.939750114,
				0.940554437,
				0.949282678,
				0.955061732,
				0.945846066,
				0.956552509,
				0.955254963,
				0.963252308,
				0.961586175,
				0.968819291,
				0.971642395,
				0.982837229,
				0.96879079,
				0.982354635,
				0.999553153,
				0.990818149,
				1.005684854,
				0.987473322,
				1.001544398,
				0.991270793,
				0.99209299,
				0.997712629,
				0.992959148,
				0.989716733,
				0.99752906,
				0.996913619,
				0.987018263,
				1.011195317,
				1.000429939,
				1.016977752,
				0.996300111,
				1.00069225,
				0.993272182,
				1.004324025,
				0.997407324,
				1.000184052,
				0.996854684,
				0.991308473,
				1.007326351,
				1
		};
		double[] fluor = fluor_unscaled;  // fluor_scaled;
		double[] time = new double[fluor.length];
		for (int i = 0; i < time.length; i++){
			time[i] = i*TIME_STEP + TIME_OFFSET;
		}

//		Random rand = new Random(200);
//		for (int i = 0; i < fluor.length; i++) {
//			fluor[i] = 55+ 43*(1-Math.exp(-time[i]/2.5)) + 10*rand.nextDouble();
//		}
		double[] inputParam = null;
		double[] paramValues = new double[3];
		Expression fittedCurve_new = CurveFitting.fitRecovery(time,fluor,FrapDataAnalysisResults.BleachType_CirularDisk, inputParam, paramValues);

		SimpleSymbolTable symbolTable = new SimpleSymbolTable(new String[] { "t" });
		fittedCurve_new.bindExpression(symbolTable);
		double[] values = new double[1];

		double[] curveValues = new double[fluor.length];
		double[] asymptote = new double[fluor.length];
		for (int i = 0; i < curveValues.length; i++) {
			values[0] = time[i];
			curveValues[i] = fittedCurve_new.evaluateVector(values);
			values[0] = 1000000.0;
			asymptote[i] = fittedCurve_new.evaluateVector(values);
		}
//		CurveFitting.showCurve(null, new String[] { "fluor", "fit", "asymptote" }, time, new double[][] { fluor, curveValues, asymptote } );
	} catch (ExpressionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

//public static Expression fitRecovery(double[] time, double[] fluor, double[] parameterValues) throws ExpressionException 
//{
//	return fitRecovery(time, fluor, parameterValues, 1);
//}

/*
 * @para: time, time points since the first post bleach
 * @para: flour, average intensities under bleached region according to time points since the first post bleach
 * @para: parameterValues, the array which will pass results back 
*/
public static Expression fitBleachWhileMonitoring(double[] time, double[] fluor, double[] outputParam) throws ExpressionException 
{
	double max_x = fluor[0];
    double min_x = fluor[0];
	for (int i = 0; i < fluor.length; i++){
		max_x = Math.max(max_x, fluor[i]);
		min_x = Math.min(min_x, fluor[i]);
	}
	System.out.println("min:"+min_x+"       max:"+max_x);
	
	if (time.length!=fluor.length){
		throw new RuntimeException("Fluorecence and time arrays must be the same length");
	}
	//normalization for the average intensties by subtracting the min average intenstiy and divided by the range of max and min intensities.
	double[] normalized_fluor = new double[fluor.length];
	for (int i = 0; i < fluor.length; i++){
		normalized_fluor[i] = (fluor[i]-min_x)/(max_x-min_x);
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

	modelExp = new Expression("Ii + A*exp(-bleachRate*t)");//Ii=Io, A=If-Io comparing with formula showing in software
	// initialize starting guess, arguments in Parameter are name, Lower Bound, Upper Bound, Scale, Initial Guess
	cbit.vcell.opt.Parameter parameters[] = new cbit.vcell.opt.Parameter[] {
			new Parameter("Ii",-1,1,1,0),
			new Parameter("A",.1,4,1,1),
			new Parameter("bleachRate",0,0.1,1,0.01)
	};
	
	try {
		optResultSet = solve(modelExp.flatten(),parameters,normalized_time,normalized_fluor);
	} catch (OptimizationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	paramNames = optResultSet.getParameterNames();
	paramValues = optResultSet.getParameterValues();
	// copy into "output" buffer for parameter values.
	for (int i = 0; i < paramValues.length; i++) {
		outputParam[i] = paramValues[i]; 
	}
	
	System.out.println(optResultSet.getOptimizationStatus().toString());
	for (int i = 0; i < paramNames.length; i++) {
		System.out.println("finally:   "+paramNames[i]+" = "+paramValues[i]);
	}
	if (optResultSet.getOptimizationStatus().isFailed()){
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
	System.out.println("bleach while monitoring fit equation before unnorm:" + fit.infix());
	fit = new Expression(min_x+" + "+(max_x-min_x)+" * ("+fit.infix()+")");
	System.out.println("bleach while monitoring fit equation after unnorm:" + fit.infix());
	return fit;
}

/*
 * @para: time, time points since the first post bleach
 * @para: flour, average intensities under bleached region according to time points since the first post bleach
 * @para: parameterValues, the array which will pass results back 
*/
public static Expression fitRecovery(double[] time, double[] fluor, int bleachType, double[] inputparam, double[] outputParam) throws ExpressionException 
{
	//max and min of the average intensities under bleached region after bleach
//	double max_x = -Double.MAX_VALUE;
//	double min_x = Double.MAX_VALUE;
	double max_x = fluor[0];
    double min_x = fluor[0];
	for (int i = 0; i < fluor.length; i++){
		max_x = Math.max(max_x, fluor[i]);
		min_x = Math.min(min_x, fluor[i]);
	}
	System.out.println("min:"+min_x+"       max:"+max_x);
	
	if (time.length!=fluor.length){
		throw new RuntimeException("Fluorecence and time arrays must be the same length");
	}
	//normalization for the average intensties by subtracting the min average intenstiy and divided by the range of max and min intensities.
	double[] normalized_fluor = new double[fluor.length];
	for (int i = 0; i < fluor.length; i++){
		normalized_fluor[i] = (fluor[i]-min_x)/(max_x-min_x);
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
	if(bleachType == FrapDataAnalysisResults.BleachType_CirularDisk){
		modelExp = new Expression(FRAPDataAnalysis.circularDisk_IntensityFunc);//Ii=Io, A=If-Io comparing with formula showing in software
		// initialize starting guess, arguments in Parameter are name, Lower Bound, Upper Bound, Scale, Initial Guess
		cbit.vcell.opt.Parameter parameters[] = new cbit.vcell.opt.Parameter[] {
				FRAPDataAnalysis.para_Ii, FRAPDataAnalysis.para_A, FRAPDataAnalysis.para_tau};
		
		try {
			optResultSet = solve(modelExp.flatten(),parameters,normalized_time,normalized_fluor);
		} catch (OptimizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		paramNames = optResultSet.getParameterNames();
		paramValues = optResultSet.getParameterValues();
		// copy into "output" buffer for parameter values.
		for (int i = 0; i < paramValues.length; i++) {
			outputParam[i] = paramValues[i]; 
		}
		// copy unnormalized (Ii + A) to output buffer
		outputParam[outputParam.length - 1] = (paramValues[0]+paramValues[1])*(max_x-min_x) + min_x; //unnormalize Ii+A
	}
	else if(bleachType == FrapDataAnalysisResults.BleachType_GaussianSpot || bleachType == FrapDataAnalysisResults.BleachType_HalfCell){
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
		//inputparam[0] is the fraction of the cell area bleached.
		modelExp = modelExp.getSubstitutedExpression(new Expression(FRAPDataAnalysis.symbol_fB), new Expression(inputparam[0]));
		cbit.vcell.opt.Parameter parameters[] = new cbit.vcell.opt.Parameter[] {
				FRAPDataAnalysis.para_If, FRAPDataAnalysis.para_I0, FRAPDataAnalysis.para_tau, FRAPDataAnalysis.para_R};
		
		try {
			optResultSet = solve(modelExp.flatten(),parameters,normalized_time,normalized_fluor);
		} catch (OptimizationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		paramNames = optResultSet.getParameterNames();
		paramValues = optResultSet.getParameterValues();
		
		// copy into "output" buffer for parameter values.
		for (int i = 0; i < paramValues.length; i++) {
			outputParam[i] = paramValues[i]; 
		}
	}else{
		throw new IllegalArgumentException("Unknown bleach type "+bleachType);
	}
	
	System.out.println(optResultSet.getOptimizationStatus().toString());
	for (int i = 0; i < paramNames.length; i++) {
		System.out.println("finally:   "+paramNames[i]+" = "+paramValues[i]);
	}
	if (optResultSet.getOptimizationStatus().isFailed()){
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
	System.out.println("fit equation before unnorm:" + fit.infix());
	fit = new Expression(min_x+" + "+(max_x-min_x)+" * ("+fit.infix()+")");
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
	try {
		optResultSet = optService.solve(optSpec, optSolverSpec, optSolverCallbacks);
	} catch (OptimizationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	String[] paramNames = optResultSet.getParameterNames();
	double[] paramValues = optResultSet.getParameterValues();
	for (int i = 0; i < paramNames.length; i++) {
		System.out.println("finally:   "+paramNames[i]+" = "+paramValues[i]);
	}
	return optResultSet;
}

public static OptimizationResultSet solveSpatial(Simulation sim, Parameter[] parameters, SpatialReferenceData refData, File dataDir, FieldDataIdentifierSpec[] fieldDataIDSs) throws ExpressionException, OptimizationException, IOException {

	//choose optimization solver, currently we have Powell and CFSQP 
	NewOptimizationSolver optService = new NewOptimizationSolver();
	OptimizationSpec optSpec = new OptimizationSpec();
	//create simple reference data
//	double[][] realData = new double[2][time.length];
//	for(int i=0; i<time.length; i++)
//	{
//		realData[0][i] = time[i];
//		realData[1][i]= data[i];
//	}
//	String[] colNames = new String[]{"t", "intensity"};
//	double[] weights = new double[]{1.0,1.0};
//	SpatialReferenceData refData = new SpatialReferenceData(colNames, weights, sim.getMeshSpecification().getSamplingSize(),);
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
	try {
		optResultSet = optService.solve(optSpec, optSolverSpec, optSolverCallbacks);
	} catch (OptimizationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	String[] paramNames = optResultSet.getParameterNames();
	double[] paramValues = optResultSet.getParameterValues();
	for (int i = 0; i < paramNames.length; i++) {
		System.out.println("finally:   "+paramNames[i]+" = "+paramValues[i]);
	}
	return optResultSet;
}
}
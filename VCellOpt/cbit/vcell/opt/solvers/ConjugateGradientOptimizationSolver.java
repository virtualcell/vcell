package cbit.vcell.opt.solvers;
import cbit.vcell.opt.solvers.OdeLSFunction;
import cbit.vcell.opt.solvers.ConjGradSolver;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationStatus;
import cbit.vcell.opt.OptimizationSolverSpec;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (3/5/00 11:16:39 PM)
 * @author: 
 */
public class ConjugateGradientOptimizationSolver implements cbit.vcell.opt.solvers.OptimizationSolver {
/**
 * CFSQPOptimizationSolver constructor comment.
 */
public ConjugateGradientOptimizationSolver() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (3/5/00 11:15:15 PM)
 * @return double[]
 * @param optSpec cbit.vcell.opt.OptimizationSpec
 * @exception java.io.IOException The exception description.
 * @exception cbit.vcell.parser.ExpressionException The exception description.
 * @exception cbit.vcell.opt.OptimizationException The exception description.
 */
public OptimizationResultSet solve(OptimizationSpec os, OptimizationSolverSpec optSolverSpec, OptSolverCallbacks optSolverCallbacks) throws java.io.IOException, cbit.vcell.parser.ExpressionException, OptimizationException {
	final double power = 2.0;
	final double MU_START = 1;
	final double MU_END = 100000.0;
	final double MU_STEP = 10.0;

	AugmentedObjectiveFunction augmentedObjFunc = OptUtils.getAugmentedObjectiveFunction(os,power,MU_START,optSolverCallbacks);

	//
	// initialize starting guess
	//
	Parameter parameters[] = os.getParameters();
	double scalings[] = os.getScaleFactors();
	double parameterValues[] = new double[parameters.length];
	for (int i = 0; i < parameters.length; i++){
		parameterValues[i] = parameters[i].getInitialGuess()/scalings[i];
		System.out.println("initial "+parameters[i].getName()+" = "+parameters[i].getInitialGuess()+", scale = "+scalings[i]+", parm = "+parameterValues[i]);
	}
		
	try {
		ConjGradSolver cgSolver = new ConjGradSolver();
		double fret = augmentedObjFunc.f(parameterValues);
		for (double mu = MU_START; mu<=MU_END;mu*=MU_STEP){
			if (optSolverCallbacks.getStopRequested()){
				throw new RuntimeException("optimization aborted");
			}
			augmentedObjFunc.setMu(mu);
			fret = cgSolver.conjGrad(parameterValues, optSolverSpec.getObjectiveFunctionChangeTolerance(), augmentedObjFunc);
			System.out.println("mu="+mu+", function value="+fret);
			if (augmentedObjFunc.getPenalty(parameterValues)==0.0){
				break;
			}
		}

		cgSolver = null;
		for (int i = 0; i < parameters.length; i++){
			System.out.println("final "+parameters[i].getName()+": scaled = "+parameterValues[i]+", unscaled = "+(parameterValues[i]*scalings[i]));
		}

		cbit.vcell.solver.ode.ODESolverResultSet odeSolverResultSet = null;
		cbit.function.ScalarFunction scalarFunction = augmentedObjFunc.getUnconstrainedScalarFunction();
		if (scalarFunction instanceof cbit.vcell.opt.solvers.OdeLSFunction){
			OdeLSFunction odeLSFunction = (OdeLSFunction)scalarFunction;
			odeLSFunction.f(parameterValues);
			odeSolverResultSet = odeLSFunction.getOdeSolverResultSet();
		}
		for (int i = 0; i < parameterValues.length; i++){
			parameterValues[i] *= scalings[i];
		}
		OptimizationStatus optStatus = new OptimizationStatus(OptimizationStatus.NORMAL_TERMINATION, "Normal Termination");
		return new OptimizationResultSet(os.getParameterNames(),parameterValues,new Double(fret),optSolverCallbacks.getEvaluationCount(),odeSolverResultSet, optStatus);
	}catch (OptimizationException e){
		OptimizationStatus optStatus = new OptimizationStatus(OptimizationStatus.FAILED, e.getMessage());
		cbit.vcell.solver.ode.ODESolverResultSet odeSolverResultSet = null;
		Double objFunctionValue = null;
		double parameterVector[] = null;
		if (optSolverCallbacks.getBestEvaluation()!=null){
			objFunctionValue = new Double(optSolverCallbacks.getBestEvaluation().objFunctionValue);
			parameterVector = optSolverCallbacks.getBestEvaluation().parameterVector;
		}
		String[] parmNames = os.getParameterNames(); // so that OptimizationResultSet doesn't complain about mismatched parameter names and values
		if (parameterVector==null){
			parmNames = null;
		}
		return new OptimizationResultSet(parmNames,parameterVector,objFunctionValue,optSolverCallbacks.getEvaluationCount(),odeSolverResultSet,optStatus);
	}
}
}
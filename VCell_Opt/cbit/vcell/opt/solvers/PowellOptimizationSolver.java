package cbit.vcell.opt.solvers;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.OptimizationException;
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
public class PowellOptimizationSolver implements OptimizationSolver {
/**
 * CFSQPOptimizationSolver constructor comment.
 */
public PowellOptimizationSolver() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (3/5/00 11:15:15 PM)
 * @return double[]
 * @param optSpec cbit.vcell.opt.OptimizationSpec
 * @exception java.io.IOException The exception description.
 * @exception org.vcell.expression.ExpressionException The exception description.
 * @exception cbit.vcell.opt.OptimizationException The exception description.
 */
public OptimizationResultSet solve(OptimizationSpec os, OptimizationSolverSpec optSolverSpec, OptSolverCallbacks optSolverCallbacks) throws java.io.IOException, org.vcell.expression.ExpressionException, OptimizationException {

	final double power = 2.0;
	final double MU_START = 0.1;
	final double MU_END = 100000.0;
	final double MU_STEP = 10.0;

	AugmentedObjectiveFunction augmentedObjFunc = OptUtils.getAugmentedObjectiveFunction(os,power,MU_START,optSolverCallbacks);

	//
	// initialize starting guess
	//
	Parameter parameters[] = os.getParameters();
	double parameterValues[] = new double[parameters.length];
	for (int i = 0; i < parameters.length; i++){
		parameterValues[i] = parameters[i].getInitialGuess();
		System.out.println("initial "+parameters[i].getName()+" = "+parameterValues[i]);
	}
	
	//
	// get initial direction set
	//
	double xi[][] = new double[parameterValues.length][parameterValues.length];
	for (int i=0;i<parameterValues.length;i++){
		for (int j=0;j<parameterValues.length;j++){
			xi[i][j]=(i == j ? 1.0 : 0.0);
		}
	}
	final double ftol = 1e-6;

	double fret = augmentedObjFunc.f(parameterValues);
	PowellSolver powellSolver = new PowellSolver();
	for (double mu = MU_START; mu<=MU_END;mu*=MU_STEP){
		if (optSolverCallbacks.getStopRequested()){
			throw new RuntimeException("optimization aborted");
		}
		augmentedObjFunc.setMu(mu);
		fret = powellSolver.powell(parameterValues.length,parameterValues,xi,ftol,augmentedObjFunc);
		System.out.println("mu="+mu+", function value="+fret);
		if (augmentedObjFunc.getPenalty(parameterValues)==0.0){
			break;
		}
	}

	for (int i = 0; i < parameters.length; i++){
		System.out.println("final "+parameters[i].getName()+" = "+parameterValues[i]);
	}

	cbit.vcell.simdata.ODESolverResultSet odeSolverResultSet = null;
	cbit.function.ScalarFunction scalarFunction = augmentedObjFunc.getUnconstrainedScalarFunction();
	if (scalarFunction instanceof OdeLSFunction){
		OdeLSFunction odeLSFunction = (OdeLSFunction)scalarFunction;
		odeLSFunction.f(parameterValues);
		odeSolverResultSet = odeLSFunction.getOdeSolverResultSet();
	}	
	
	cbit.vcell.opt.OptimizationStatus optStatus = new cbit.vcell.opt.OptimizationStatus(cbit.vcell.opt.OptimizationStatus.NORMAL_TERMINATION, "Normal Termination");
	return new OptimizationResultSet(os.getParameterNames(),parameterValues,new Double(fret),optSolverCallbacks.getEvaluationCount(),odeSolverResultSet, optStatus);
}
}
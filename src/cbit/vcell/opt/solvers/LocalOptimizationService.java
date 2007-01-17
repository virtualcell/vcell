package cbit.vcell.opt.solvers;
import cbit.vcell.solver.Simulation;
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.Parameter;
import cbit.vcell.opt.Constraint;
import cbit.vcell.opt.ConstraintType;
import cbit.vcell.opt.OptimizationStatus;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationSolverSpec;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.util.*;
import cbit.vcell.math.*;
import java.rmi.*;
import cbit.vcell.parser.*;
import java.io.*;
/**
 * Insert the type's description here.
 * Creation date: (3/16/00 3:07:48 PM)
 * @author: 
 */
public class LocalOptimizationService implements OptimizationService {
	OptimizationSolver optSolver = null;	

/**
 * OptimizationServiceImpl constructor comment.
 */
public LocalOptimizationService() {
	super();
}


/**
 * solve method comment.
 */
public OptimizationResultSet solve(OptimizationSpec optSpec, OptimizationSolverSpec optSolverSpec, OptSolverCallbacks optSolverCallbacks) throws OptimizationException {
	if (optSolverSpec.getSolverType().equals(OptimizationSolverSpec.SOLVERTYPE_POWELL)){
		optSolver = new PowellOptimizationSolver();
	//}else if (optSolverSpec.getSolverType().equals(OptimizationSolverSpec.SOLVERTYPE_CONJUGATE_GRADIENT)){
		//optSolver = new ConjugateGradientOptimizationSolver();
	}else if (optSolverSpec.getSolverType().equals(OptimizationSolverSpec.SOLVERTYPE_CFSQP) 
	//			|| optSolverSpec.getSolverType().equals(OptimizationSolverSpec.SOLVERTYPE_SIMULTANEOUS)
	) {
		optSolver = new NativeOptSolver();
	}else {
		throw new RuntimeException("unsupported solver type '"+optSolverSpec.getSolverType()+"'");
	}
	cbit.vcell.opt.OptimizationResultSet optResultSet = null;
	try {
		optResultSet = optSolver.solve(optSpec,optSolverSpec,optSolverCallbacks);
	}catch (IOException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}catch (ExpressionException e){
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}catch(OptimizationException e) {
		e.printStackTrace(System.out);
		throw new RuntimeException(e.getMessage());
	}finally{
		optSolver = null;
	}
	return optResultSet;
}
}
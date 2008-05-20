package cbit.vcell.opt.solvers;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.opt.OptimizationSpec;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationSolverSpec;
/**
 * Insert the type's description here.
 * Creation date: (3/5/00 11:14:15 PM)
 * @author: 
 */
public interface OptimizationSolver {
/**
 * Insert the method's description here.
 * Creation date: (3/5/00 11:15:15 PM)
 * @return double[]
 * @param optSpec cbit.vcell.opt.OptimizationSpec
 * @exception java.io.IOException The exception description.
 * @exception cbit.vcell.parser.ExpressionException The exception description.
 * @exception cbit.vcell.opt.OptimizationException The exception description.
 */
public abstract OptimizationResultSet solve(OptimizationSpec os, OptimizationSolverSpec optSolverSpec, OptSolverCallbacks optSolverCallbacks) throws java.io.IOException, cbit.vcell.parser.ExpressionException, OptimizationException;
}
/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.opt.solvers;
import org.vcell.optimization.OptSolverCallbacks;

import cbit.vcell.opt.OptimizationException;
import cbit.vcell.opt.OptimizationResultSet;
import cbit.vcell.opt.OptimizationSolverSpec;
import cbit.vcell.opt.OptimizationSpec;
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

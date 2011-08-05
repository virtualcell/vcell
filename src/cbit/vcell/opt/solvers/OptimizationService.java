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
import cbit.vcell.opt.OptimizationSpec;
/**
 * Insert the type's description here.
 * Creation date: (3/16/00 3:04:37 PM)
 * @author: 
 */
public interface OptimizationService {
/**
 * Insert the method's description here.
 * Creation date: (3/16/00 3:07:09 PM)
 * @return double[]
 * @param optSpec cbit.vcell.opt.OptimizationSpec
 * @exception java.rmi.RemoteException The exception description.
 * @exception cbit.vcell.opt.OptimizationException The exception description.
 */
OptimizationResultSet solve(OptimizationSpec optSpec, cbit.vcell.opt.OptimizationSolverSpec optSolverSpec, OptSolverCallbacks optSolverCallbacks) throws OptimizationException;
}

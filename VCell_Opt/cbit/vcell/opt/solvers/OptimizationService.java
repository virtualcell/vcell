package cbit.vcell.opt.solvers;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
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
OptimizationResultSet solve(OptimizationSpec optSpec, cbit.vcell.opt.OptimizationSolverSpec optSolverSpec, OptSolverCallbacks optSolverCallbacks) throws cbit.vcell.opt.OptimizationException;
}
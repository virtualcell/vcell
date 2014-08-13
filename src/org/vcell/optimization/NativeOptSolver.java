/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.optimization;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.ResourceUtil;

/**
 * Insert the type's description here.
 * Creation date: (4/10/2006 10:25:33 AM)
 * @author: Jim Schaff
 */
public class NativeOptSolver {
	static {
		NativeLib.NATIVE_SOLVERS.load();
	}

	
/**
 * NativeIDAOptSolver constructor comment.
 */
public NativeOptSolver() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (4/10/2006 10:27:22 AM)
 * @return double[]
 * @param optSpec cbit.vcell.opt.OptimizationSpec
 * @exception java.io.IOException The exception description.
 * @exception cbit.vcell.parser.ExpressionException The exception description.
 * @exception cbit.vcell.opt.OptimizationException The exception description.
 */
public native String nativeSolve_CFSQP(String optProblemXML, OptSolverCallbacks optSolverCallbacks) throws Exception;


}

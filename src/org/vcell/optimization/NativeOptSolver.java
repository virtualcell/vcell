package org.vcell.optimization;

/**
 * Insert the type's description here.
 * Creation date: (4/10/2006 10:25:33 AM)
 * @author: Jim Schaff
 */
public class NativeOptSolver {
	static {
		System.loadLibrary("NativeSolvers");
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
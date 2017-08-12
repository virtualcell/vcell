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
import org.vcell.optimization.DefaultOptSolverCallbacks;

import cbit.function.DynamicScalarFunction;
import cbit.function.DynamicVectorFunction;
import cbit.function.ScalarFunction;
import cbit.function.VectorFunction;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
/**
 * Insert the type's description here.
 * Creation date: (5/3/2002 2:44:49 PM)
 * @author: Michael Duff
 */
public class AugmentedObjectiveFunctionTest {
/**
 * PenaltyTest constructor comment.
 */
public AugmentedObjectiveFunctionTest() {
	super();
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 2:49:06 PM)
 * @return opt.AugmentedObjectiveFunction
 */
public static AugmentedObjectiveFunction getExample1() {
	try {
		Expression scalarFn_exp = new Expression("pow(x1-2,4)+pow(x1-2*x2,2)");
		Expression eqConstraints_exps[] = {new Expression("pow(x1,2)-x2")};
		Expression ineqConstraints_exps[] ={new Expression("pow(x1,2)-x2")};;
        String ids[] ={"x1","x2"};
        
		ScalarFunction scalarFn = new DynamicScalarFunction(scalarFn_exp, ids);
		VectorFunction eqConstraints = new DynamicVectorFunction(eqConstraints_exps, ids);
    	VectorFunction ineqConstraints = new DynamicVectorFunction(ineqConstraints_exps, ids);

		AugmentedObjectiveFunction aof = new AugmentedObjectiveFunction(scalarFn, eqConstraints, ineqConstraints, 2.0, 0.1, new DefaultOptSolverCallbacks());

		return aof;
	}catch (ExpressionException e){
		e.printStackTrace();
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.                    // Root-finding example
 * Creation date: (5/3/2002 2:49:06 PM)
 * @return opt.AugmentedObjectiveFunction
 */
public static AugmentedObjectiveFunction getExample2() {
	try {
		Expression scalarFn_exp = new Expression("1");
		Expression eqConstraints_exps[] = {new Expression("sin(x1+x2)-exp(x1-x2)"), new Expression("cos(x1+x2)-x1*x1*x2*x2")};
		Expression ineqConstraints_exps[] = {};
        String ids[] ={"x1","x2"};
		
		ScalarFunction scalarFn = new DynamicScalarFunction(scalarFn_exp, ids);
		VectorFunction eqConstraints = new DynamicVectorFunction(eqConstraints_exps, ids);
    	VectorFunction ineqConstraints = new DynamicVectorFunction(ineqConstraints_exps, ids);

		AugmentedObjectiveFunction aof = new AugmentedObjectiveFunction(scalarFn, eqConstraints, ineqConstraints, 2.0, 0.1, new DefaultOptSolverCallbacks());

		return aof;
	}catch (ExpressionException e){
		e.printStackTrace();
		throw new RuntimeException(e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 2:45:06 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {

	final double EPS  = 1.0e-5;
	final double FTOL = 1.0e-6;
	final double BETA = 10;
	double fret;
	double[] p = {2.0, 1.0};
		
	// AugmentedObjectiveFunction augObjFn = getExample1();
	AugmentedObjectiveFunction augObjFn = getExample2();

//	cbit.vcell.opt.solvers.ConjGradSolver cgSolver = new cbit.vcell.opt.solvers.ConjGradSolver();
//		
//	for(int i = 1; i <= 5; i++){
//		fret = cgSolver.conjGrad(p , 1.0e-6, augObjFn);
//		System.out.println("\n\n\t\t i = "+ i + " p0 = "+ p[0] + " p1 = " + p[1] +" fmin = " + fret);
//		augObjFn.setMu(augObjFn.getMu()*BETA);
//	}		
}
}

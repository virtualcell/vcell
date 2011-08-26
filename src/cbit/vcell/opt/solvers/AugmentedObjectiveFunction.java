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

import cbit.function.DefaultScalarFunction;
import cbit.function.ScalarFunction;
import cbit.function.VectorFunction;
import cbit.vcell.solver.ode.ODESolverResultSet;


/**
 * Insert the type's description here.
 * Creation date: (5/3/2002 1:30:40 PM)
 * @author: Michael Duff
 */

 
public class AugmentedObjectiveFunction extends DefaultScalarFunction {
	protected ScalarFunction fobj = null;
	protected VectorFunction equalityConstraints = null;
	protected VectorFunction inequalityConstraints = null;
	protected double penpow = 2;
	protected double mu = 0.1;
	protected OptSolverCallbacks optSolverCallbacks = null;

/**
 * PenaltyWrap constructor comment.
 */
public AugmentedObjectiveFunction(ScalarFunction argFobj, VectorFunction argEqConstraints, VectorFunction argIneqConstraints, double argPenpow, double argMu, OptSolverCallbacks argOptSolverCallbacks) {
	fobj = argFobj;
	equalityConstraints = argEqConstraints;
	inequalityConstraints = argIneqConstraints;
	penpow = argPenpow;
	mu = argMu;
	optSolverCallbacks = argOptSolverCallbacks;
}


/**
 * f method comment.
 */
public double f(double[] x) {
	double penalty = 0.0;
	try {
		optSolverCallbacks.setPenaltyMu(new Double(mu));
		penalty = getPenalty(x);
//		for (int i = 0; i < x.length; i++) {
//			System.out.print("x[" + i + "]=" + x[i] + " ");
//		}
		if (penalty > 1000){
			System.out.println("aborting point, penalty = "+penalty);
			return penalty;
		}
		double objFunValue = fobj.f(x)/1000.0;

		//
		// send info on evaluation
		//
		OptSolverCallbacks.EvaluationHolder evaluationHolder = new OptSolverCallbacks.EvaluationHolder((double[]) x.clone(), (penalty + objFunValue));
		
		ODESolverResultSet resultSet = null;
		if (fobj instanceof OdeLSFunction) {
			resultSet = ((OdeLSFunction) fobj).getOdeSolverResultSet();
		}
		optSolverCallbacks.addEvaluation(evaluationHolder, resultSet);
		

		return penalty + objFunValue;
	} catch (Exception e) {
		e.printStackTrace(System.out);
		System.out.println("penalty = "+penalty);
		if (penalty > 0) {
			System.out.println("too high ... aborting augmentedObjective function");
			return penalty+1;
		}else{
			throw new cbit.vcell.opt.OptimizationException(e.getMessage(), x);
		}
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 2:18:04 PM)
 * @return double
 */
public double getMu() {
	return mu;
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 2:12:49 PM)
 * @return int
 */
public int getNumArgs() {
	return fobj.getNumArgs();
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 2:17:39 PM)
 * @return double
 * @param x double[]
 */
public double getPenalty(double[] x) {
	
	double penalty=0;

	double[] inequalityConstraintImage = (inequalityConstraints!=null)?(inequalityConstraints.f(x)):null;
	double[] equalityConstraintImage = (equalityConstraints!=null)?(equalityConstraints.f(x)):null;
		
	for(int i=0;inequalityConstraintImage!=null && i<inequalityConstraintImage.length;i++){
		penalty += Math.pow(Math.max(0,inequalityConstraintImage[i]),penpow);
	}

	for(int i=0;equalityConstraintImage!=null && i<equalityConstraintImage.length;i++){
		penalty += Math.pow(Math.abs(equalityConstraintImage[i]),penpow);
	}
	
	penalty *= mu;
	return penalty;	
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 2:18:04 PM)
 * @return double
 */
public double getPenpow() {
	return penpow;
}


/**
 * Insert the method's description here.
 * Creation date: (9/6/2005 10:55:10 AM)
 * @return cbit.function.ScalarFunction
 */
public ScalarFunction getUnconstrainedScalarFunction() {
	return fobj;
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 2:18:04 PM)
 * @param newMu double
 */
public void setMu(double newMu) {
	mu = newMu;
}


/**
 * Insert the method's description here.
 * Creation date: (5/3/2002 2:18:04 PM)
 * @param newPenpow double
 */
public void setPenpow(double newPenpow) {
	penpow = newPenpow;
}
}

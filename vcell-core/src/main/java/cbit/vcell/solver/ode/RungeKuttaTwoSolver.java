/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode;

import java.io.File;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.SolverException;
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 9:00:07 PM)
 * @author: John Wagner
 */
public class RungeKuttaTwoSolver extends RungeKuttaSolver {
/**
 * ForwardEulerIntegrator constructor comment.
 * @param mathDesc cbit.vcell.math.MathDescription
 * @param sessionLog cbit.vcell.server.SessionLog
 * @param numVectors int
 */
public RungeKuttaTwoSolver(SimulationTask simTask, File directory) throws SolverException {
	super(simTask, directory, 2, 2);
}
/**
 * Integrate over time step using the forward Euler method (1st order explicit)
 * results must be stored in NumVectors-1 = vector(4);
 *  t is the current time
 *  h is the time step
 *  THIS METHOD HAS NOT BEEN TESTED YET...
 */
protected void step(double t, double h) throws cbit.vcell.solver.SolverException {
	try {
		double oldValues[] = getValueVector(0);
		double newValues[] = getValueVector(1);
		//
		// update time
		oldValues[getTimeIndex()] = t;
		//  newValues has time t, not t + h...it's a
		//  scratch array until the end...
		newValues[getTimeIndex()] = t;
		for (int i = 0; i < getStateVariableCount(); i++) {
			int I = getVariableIndex(i);
			newValues[I] = oldValues[I];
		}
		for (int i = 0; i < getStateVariableCount(); i++) {
			k[0][getVariableIndex(i)] = h * evaluate(newValues, i);
		}
		//
		newValues[getTimeIndex()] = t + 0.5 * h;
		for (int i = 0; i < getStateVariableCount(); i++) {
			int I = getVariableIndex(i);
			newValues[I] = oldValues[I] + 0.5 * k[0][I];
		}
		for (int i = 0; i < getStateVariableCount(); i++) {
			k[1][getVariableIndex(i)] = h * evaluate(newValues, i);
		}
		//
		for (int i = 0; i < getStateVariableCount(); i++) {
			int I = getVariableIndex(i);
			newValues[I] = oldValues[I] + k[1][I];
		}
	} catch (ExpressionException expressionException) {
		throw new cbit.vcell.solver.SolverException(expressionException.getMessage());
	}
}
}

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
 * Creation date: (8/19/2000 8:57:32 PM)
 * @author: John Wagner
 */
public class ForwardEulerSolver extends DefaultODESolver {
/**
 * ForwardEulerIntegrator constructor comment.
 * @param mathDesc cbit.vcell.math.MathDescription
 * @param sessionLog cbit.vcell.server.SessionLog
 * @param numVectors int
 */
public ForwardEulerSolver(SimulationTask simTask, File directory)  throws SolverException {
	super(simTask, directory, 2);
}
/**
 * Integrate over time step using the forward Euler method (1st order explicit)
 * results must be stored in NumVectors-1 = vector(1);
 */
protected void step(double t, double h) throws SolverException {
	try {
		// get value Vectors
		double oldValues[] = getValueVector(0);
		double newValues[] = getValueVector(1);
		// update time
		oldValues[getTimeIndex()] = t;
		newValues[getTimeIndex()] = t + h;
		// integrate
		for (int i = 0; i < getStateVariableCount(); i++) {
			int I = getVariableIndex(i);
			newValues[I] = oldValues[I] + h * evaluate(oldValues, i);
		}
	} catch (ExpressionException expressionException) {
		throw new SolverException(expressionException.getMessage());
	}
}
}

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
import java.io.IOException;

import org.vcell.util.SessionLog;

import cbit.vcell.math.MathException;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.UserStopException;
/**
 * Insert the type's description here.
 * Creation date: (4/22/00 12:05:08 AM)
 * @author: John Wagner
 */
public class AdamsMoultonFiveSolver extends AdamsSolver {
	double[][] k;
/**
 * AdamsMoultonFiveIntegrator constructor comment.
 * @param mathDescription cbit.vcell.math.MathDescription
 * @param sessionLog cbit.vcell.server.SessionLog
 * @param valueVectorCount int
 * @param temporaryVectorCount int
 */
public AdamsMoultonFiveSolver(SimulationTask simTask, File directory, SessionLog sessionLog)  throws SolverException {
	super(simTask, directory, sessionLog, 2, 5);
}
protected void initialize() throws SolverException {
	super.initialize();
	k = new double[fieldWorkArrayCount][];
	for (int i = 0; i < 4; i++) k[i] = createWorkArray();
}
/**
 * This method was created by a SmartGuide.
 *  THIS HAS NOT BEEN UPDATED LIKE ODEIntegrator.integrate () and
 *  RungeKuttaFehlbergIntegrator.integrate()...
 */
protected void integrate() throws SolverException, UserStopException, IOException {
	try {
		SolverTaskDescription taskDescription = simTask.getSimulation().getSolverTaskDescription();
		double timeStep = taskDescription.getTimeStep().getDefaultTimeStep();
		fieldCurrentTime = taskDescription.getTimeBounds().getStartingTime();

		// before computation begins, settle fast equilibrium
		if (getFastAlgebraicSystem() != null) {
			fieldValueVectors.copyValues(0, 1);
			getFastAlgebraicSystem().initVars(getValueVector(0), getValueVector(1));
			getFastAlgebraicSystem().solveSystem(getValueVector(0), getValueVector(1));
			fieldValueVectors.copyValues(1, 0);
		}
		// check for failure
		check(getValueVector(0));
		//  Evaluate
		for (int i = 0; i < getStateVariableCount(); i++) {
			f[0][getVariableIndex(i)] = evaluate(getValueVector(0), i);
		}
		// check for failure
		check(getValueVector(0));
		updateResultSet();
		//
		int iteration = 0;
		while (fieldCurrentTime < taskDescription.getTimeBounds().getEndingTime()) {
			checkForUserStop();
			if (iteration < 3) {
				//  Take Runge-Kutta step...
				prep(fieldCurrentTime, timeStep);
			} else {
				//  Take Adams-Moulton step...
				step(fieldCurrentTime, timeStep);
			}
			// update (old = new)
			fieldValueVectors.copyValuesDown();
			// compute fast system
			if (getFastAlgebraicSystem() != null) {
				fieldValueVectors.copyValues(0, 1);
				getFastAlgebraicSystem().initVars(getValueVector(0), getValueVector(1));
				getFastAlgebraicSystem().solveSystem(getValueVector(0), getValueVector(1));
				fieldValueVectors.copyValues(1, 0);
			}
			// check for failure
			check(getValueVector(0));
			if (iteration < 3) {
				for (int i = 0; i < getStateVariableCount(); i++) {
					f[iteration + 1][getVariableIndex(i)] = evaluate(getValueVector(0), i);
				}
				// check for failure
				check(f[iteration + 1]);
			} else {
				//  Evaluate
				for (int i = 0; i < getStateVariableCount(); i++) {
					f[4][getVariableIndex(i)] = evaluate(getValueVector(0), i);
				}
				// check for failure
				check(f[4]);
				shiftWorkArrays();
			}
			//fieldCurrentTime += timeStep;
			iteration++;
			fieldCurrentTime = taskDescription.getTimeBounds().getStartingTime() + iteration*timeStep;
			// store results if it coincides with a save interval
			if (taskDescription.getOutputTimeSpec().isDefault()) {
				int keepEvery = ((DefaultOutputTimeSpec)taskDescription.getOutputTimeSpec()).getKeepEvery();
				if ((iteration % keepEvery) == 0) updateResultSet();
			}
		}
		// store last time point
		if (taskDescription.getOutputTimeSpec().isDefault()) {
			int keepEvery = ((DefaultOutputTimeSpec)taskDescription.getOutputTimeSpec()).getKeepEvery();
			if ((iteration % keepEvery) == 0) updateResultSet();
		}
	} catch (ExpressionException expressionException) {
		throw new SolverException(expressionException.getMessage());
	} catch (MathException mathException) {
		throw new SolverException(mathException.getMessage());
	}
}
/**
 * Integrate over time step using the forward Euler method (1st order explicit)
 * results must be stored in NumVectors-1 = vector(4);
 *  t is the current time
 *  h is the time step
 */
protected void prep(double t, double h) throws SolverException {
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
		newValues[getTimeIndex()] = t + 0.5 * h;
		for (int i = 0; i < getStateVariableCount(); i++) {
			int I = getVariableIndex(i);
			newValues[I] = oldValues[I] + 0.5 * k[1][I];
		}
		for (int i = 0; i < getStateVariableCount(); i++) {
			k[2][getVariableIndex(i)] = h * evaluate(newValues, i);
		}
		//
		newValues[getTimeIndex()] = t + h;
		for (int i = 0; i < getStateVariableCount(); i++) {
			int I = getVariableIndex(i);
			newValues[I] = oldValues[I] + k[2][I];
		}
		for (int i = 0; i < getStateVariableCount(); i++) {
			k[3][getVariableIndex(i)] = h * evaluate(newValues, i);
		}
		//
		for (int i = 0; i < getStateVariableCount(); i++) {
			int I = getVariableIndex(i);
			newValues[I] = oldValues[I] + (k[0][I] + 2.0 * k[1][I] + 2.0 * k[2][I] + k[3][I]) / 6.0;
		}
	} catch (ExpressionException expressionException) {
		throw new SolverException(expressionException.getMessage());
	}
}
/**
 * Integrate over time step using the forward Euler method (1st order explicit)
 * results must be stored in NumVectors-1 = vector(4);
 *  t is the current time
 *  h is the time step
 */
protected void step(double t, double h) throws SolverException {
	try {
		double oldValues[] = getValueVector(0);
		double newValues[] = getValueVector(1);
		//
		// update time
		oldValues[getTimeIndex()] = t;
		newValues[getTimeIndex()] = t + h;
		//  Predict
		for (int i = 0; i < getStateVariableCount(); i++) {
			int I = getVariableIndex(i);
			newValues[I] = oldValues[I] + h * (55.0 * f[3][I] - 59.0 * f[2][I] + 37.0 * f[1][I] - 9.0 * f[0][I])/24.0;
		}
		//  Evaluate
		for (int i = 0; i < getStateVariableCount(); i++) {
			f[4][getVariableIndex(i)] = evaluate(newValues, i);
		}
		//  Correct
		for (int i = 0; i < getStateVariableCount(); i++) {
			int I = getVariableIndex(i);
			newValues[I] = oldValues[I] + h * (9.0 * f[4][I] + 19.0 * f[3][I] - 5.0 * f[2][I] + 1.0 * f[1][I])/24.0;
		}
	} catch (ExpressionException expressionException) {
		throw new SolverException(expressionException.getMessage());
	}
}
}

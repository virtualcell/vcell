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
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:59:50 PM)
 * @author: John Wagner
 */
public class RungeKuttaFehlbergSolver extends RungeKuttaSolver {
	private static double[] a = {0.0, 1.0 / 4.0, 3.0 / 8.0, 12.0 / 13.0, 1.0, 1.0 / 2.0};
	private static double[][] b = {
		{0.0, 0.0, 0.0, 0.0, 0.0},
		{1.0 / 4.0, 0.0, 0.0, 0.0, 0.0},
		{3.0 / 32.0, 9.0 / 32.0, 0.0, 0.0, 0.0},
		{1932.0 / 2197.0, -7200.0 / 2197.0, 7296.0 / 2197.0, 0.0, 0.0},
		{439.0 / 216.0, -8.0, 3680.0 / 513.0, -845.0 / 4104.0, 0.0},
		{-8.0 / 27.0, 2.0, -3544.0 / 2565.0, 1859.0 / 4104.0, -11.0 / 40.0}
	};
	private static double[] c = { 16.0/135.0, 0.0, 6656.0/12825.0, 28561.0/56430.0, -9.0/50.0, 2.0/55.0 };
	private static double[] e = { -1.0/360.0,  0.0, 128.0/4275.0, 2197.0/75240.0, -1.0/50.0, -2.0/55.0 };
/**
 * ForwardEulerIntegrator constructor comment.
 * @param mathDesc cbit.vcell.math.MathDescription
 * @param sessionLog cbit.vcell.server.SessionLog
 * @param numVectors int
 */
public RungeKuttaFehlbergSolver(SimulationTask simTask, File directory, SessionLog sessionLog) throws SolverException {
	super(simTask, directory, sessionLog, 3, 6);
}
public double calculateErrorTerm(int i) {
	int I = getVariableIndex(i);
	return (e[0]*k[0][I] + e[1]*k[1][I] + e[2]*k[2][I] + e[3]*k[3][I] + e[4]*k[4][I] + e[5]*k[5][I]);
}
protected void integrate() throws cbit.vcell.solver.SolverException, UserStopException, IOException {
	try {
		// Get machine epsilon
		final double DBL_EPSILON = 1.0E-12;
		final double epsilon = DBL_EPSILON;
		final double twentySixEpsilon = 26 * epsilon;
		//
		SolverTaskDescription taskDescription = simTask.getSimulation().getSolverTaskDescription();
		double startingTime = taskDescription.getTimeBounds().getStartingTime();
		double endingTime = taskDescription.getTimeBounds().getEndingTime();
		double relativeErrorTolerance = taskDescription.getErrorTolerance().getRelativeErrorTolerance();
		double absoluteErrorTolerance = taskDescription.getErrorTolerance().getAbsoluteErrorTolerance();
		fieldCurrentTime = taskDescription.getTimeBounds().getStartingTime();
		//
		double oldValues[] = getValueVector(0);
		double newValues[] = getValueVector(1);
		//  The minimumRelativeError is the minimum acceptable value of
		//  relativeError.  Attempts to obtain higher accuracy with this
		//  subroutine are usually very expensive and often unsuccessful.
		final double minimumRelativeError = 1e-12;

		//  Check input parameters...
		if (relativeErrorTolerance < 0.0 || absoluteErrorTolerance < 0.0 || startingTime >= endingTime) {
			throw new SolverException("Invalid parameters");
		}
		//  Restrict relative error tolerance to be at least as large as
		//  2*epsilon + minimumRelativeError to avoid limiting precision
		//  difficulties arising from impossible accuracy requests
		if (relativeErrorTolerance < 2 * epsilon + minimumRelativeError) {
			//  Or just set relativeError = 2 * epsilon + minimumRelativeError???
			throw new SolverException("Relative error too small");
		}
		// before computation begins, settle fast equilibrium
		if (getFastAlgebraicSystem() != null) {
			fieldValueVectors.copyValues(0, 1);
			getFastAlgebraicSystem().initVars(getValueVector(0), getValueVector(1));
			getFastAlgebraicSystem().solveSystem(getValueVector(0), getValueVector(1));
			fieldValueVectors.copyValues(1, 0);
		}
		// check for failure
		check(getValueVector(0));
		//
		double timeRemaining = endingTime - fieldCurrentTime;
		double tolerance = 0.0;
		double maximumTolerance = 0.0;
		final double maximumTimeStep = taskDescription.getTimeStep().getMaximumTimeStep();
		double h = Math.min(Math.abs(timeRemaining), taskDescription.getTimeStep().getMaximumTimeStep());
		for (int i = 0; i < getStateVariableCount(); i++) {
			int I = getVariableIndex(i);
			tolerance = relativeErrorTolerance * Math.abs(oldValues[I]) + absoluteErrorTolerance;
			if (tolerance > 0.0) {
				double yp = Math.abs(evaluate(oldValues, i));
				if (yp * Math.pow(h, 5.0) > tolerance) {
					h = Math.pow(tolerance / yp, 0.2);
				}
			}
			maximumTolerance = Math.max(maximumTolerance, tolerance);
		}
		if (maximumTolerance <= 0.0) h = 0.0;
		h = Math.max(h, twentySixEpsilon * Math.max(timeRemaining, Math.abs(fieldCurrentTime)));
		//  To avoid premature underflow in the error
		//  tolerance function,  scale the tolerances...
		final double scale = 2.0 / relativeErrorTolerance;
		final double scaledAbsoluteError = scale * absoluteErrorTolerance;
		boolean previousStepFailed = false;
		//  Check for failure...
		check(getValueVector(0));
		updateResultSet();
		//  Integrate...
		int iteration = 0;
		while (fieldCurrentTime < endingTime) {
			checkForUserStop();
			//  Set smallest allowable stepsize...
			final double minimumTimeStep = Math.max(
				twentySixEpsilon * Math.abs(fieldCurrentTime),
				taskDescription.getTimeStep().getMinimumTimeStep());
			h = Math.min(h, taskDescription.getTimeStep().getMaximumTimeStep());

			//  Adjust stepsize if necessary to hit the output point.
			//  Look ahead two steps to avoid drastic changes in the stepsize and
			//  thus lessen the impact of output points on the code.
			timeRemaining = endingTime - fieldCurrentTime;
			if (timeRemaining < 2 * h) {
				if (timeRemaining <= h) {
					h = timeRemaining;
				} else {
					h = 0.5 * timeRemaining;
				}
			}
			//  If too close to output point, extrapolate and return
			if (timeRemaining <= twentySixEpsilon * Math.abs(fieldCurrentTime)) {
				for (int i = 0; i < getStateVariableCount(); i++) {
					int I = getVariableIndex(i);
					newValues[I] = oldValues[I] + timeRemaining * evaluate(oldValues, i);
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
				fieldCurrentTime = endingTime;
				return;
			} else {
				// Advance an approximate solution over one step of length h
				//RungeKuttaStep(Model,y,t,h,ss);
				step(fieldCurrentTime, h);
				// compute fast system
				if (getFastAlgebraicSystem() != null) {
					fieldValueVectors.copyValues(1, 2);
					getFastAlgebraicSystem().initVars(getValueVector(1), getValueVector(2));
					getFastAlgebraicSystem().solveSystem(getValueVector(1), getValueVector(2));
					fieldValueVectors.copyValues(2, 1);
				}
				// check for failure
				check(getValueVector(1));

				// Compute and test allowable tolerances versus local error estimates
				// and remove scaling of tolerances. Note that relative error is
				// measured with respect to the average of the magnitudes of the
				// solution at the beginning and end of the step.
				double estimatedErrorOverErrorTolerance = 0.0;
				for (int i = 0; i < getStateVariableCount(); i++) {
					int I = getVariableIndex(i);
					double errorTolerance = Math.abs(oldValues[I]) + Math.abs(newValues[I]) + scaledAbsoluteError;
					// Inappropriate error tolerance
					if (errorTolerance <= 0.0) {
						throw new SolverException("Error tolerance too small");
					}
					double estimatedError = Math.abs(calculateErrorTerm(i));
					estimatedErrorOverErrorTolerance = Math.max(estimatedErrorOverErrorTolerance, estimatedError / errorTolerance);
				}
				double estimatedTolerance = h * estimatedErrorOverErrorTolerance * scale;
				if (estimatedTolerance > 1.0) {
					//  Unsuccessful step.  Reduce the stepsize and try again.
					//  The decrease is limited to a factor of 1/10...
					previousStepFailed = true;
					double s = 0.1;
					if (estimatedTolerance < 59049.0) {   // s = 0.1  @  estimatedTolerance = 59049
						s = 0.9 / Math.pow(estimatedTolerance, 0.2);
					}
					h *= s;
					if (h < minimumTimeStep) {
						throw new SolverException("Requested error unattainable at smallest allowable stepsize");
					}
				} else {
					// Successful step.  Store solution at t+h and evaluate derivatives there.
					fieldValueVectors.copyValuesDown();
					fieldCurrentTime += h;
					iteration++;
					if (taskDescription.getOutputTimeSpec().isDefault()) {
						int keepEvery = ((DefaultOutputTimeSpec)taskDescription.getOutputTimeSpec()).getKeepEvery();					
						if ((iteration % keepEvery) == 0) updateResultSet();
					}
					//
					// Choose next stepsize.  The increase is limited to a factor of 5. If
					// step failure has just occurred, next stepsize is not allowed to increase.
					//
					double s = 5;
					if (estimatedTolerance > 0.0001889568) {  // s = 5  @  estimatedTolerance = 0.0001889568
						s = 0.9 / Math.pow(estimatedTolerance, 0.2);
					}
					if (previousStepFailed) {
						s = Math.min(1.0, s);
					}
					h = Math.min(Math.max(minimumTimeStep, s * h), maximumTimeStep);
					previousStepFailed = false;
				}
			}
		}
		// store last time point
		if (taskDescription.getOutputTimeSpec().isDefault()) {
			int keepEvery = ((DefaultOutputTimeSpec)taskDescription.getOutputTimeSpec()).getKeepEvery();
			if ((iteration % keepEvery) != 0) updateResultSet();
		}
	} catch (ExpressionException expressionException) {
		expressionException.printStackTrace(System.out);
		throw new SolverException(expressionException.getMessage());
	} catch (MathException mathException) {
		mathException.printStackTrace(System.out);
		throw new SolverException(mathException.getMessage());
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
		//
		for (int i = 0; i < 6; i++) {
			newValues[getTimeIndex()] = t + a[i]*h;
			for (int j = 0; j < getStateVariableCount(); j++) {
				int J = getVariableIndex(j);
				newValues[J] = oldValues[J];
				for (int m = 0; m < i; m++) {
					newValues[J] += b[i][m]*k[m][J];
				}
			}
			for (int j = 0; j < getStateVariableCount(); j++) {
				k[i][getVariableIndex(j)] = h * evaluate(newValues, j);
			}
		}
		for (int i = 0; i < getStateVariableCount(); i++) {
			int I = getVariableIndex(i);
			newValues[I] = oldValues[I];
			for (int j = 0; j < 6; j++) {
				newValues[I] += c[j]*k[j][I];
			}
		}
	} catch (ExpressionException expressionException) {
		throw new SolverException(expressionException.getMessage());
	}
}
}

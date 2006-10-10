package cbit.vcell.solver.ode;

import cbit.util.SessionLog;
import java.io.*;

import org.vcell.expression.ExpressionException;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.simulation.*;
import cbit.vcell.solvers.SimulationJob;
import cbit.vcell.solvers.SolverException;
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
public ForwardEulerSolver(SimulationJob simulationJob, File directory, SessionLog sessionLog)  throws SolverException {
	super(simulationJob, directory, sessionLog, 2);
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

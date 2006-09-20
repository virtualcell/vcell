package cbit.vcell.solver.ode;

import cbit.util.SessionLog;
import java.io.*;
import cbit.vcell.solver.*;
import cbit.vcell.solvers.SimulationJob;
import cbit.vcell.solvers.SolverException;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 9:00:00 PM)
 * @author: John Wagner
 */
public abstract class RungeKuttaSolver extends DefaultODESolver {
	int fieldWorkArrayCount;
	double[][] k;
/**
 * RungeKuttaIntegrator constructor comment.
 * @param mathDescription cbit.vcell.math.MathDescription
 * @param sessionLog cbit.vcell.server.SessionLog
 * @param valueVectorCount int
 * @param temporaryVectorCount int
 */
public RungeKuttaSolver(SimulationJob simulationJob, File directory, SessionLog sessionLog, int valueVectorCount, int workArrayCount)  throws SolverException {
	super(simulationJob, directory, sessionLog, valueVectorCount);
	fieldWorkArrayCount = workArrayCount;
}
protected void initialize() throws cbit.vcell.solvers.SolverException {
	super.initialize();
	k = new double[fieldWorkArrayCount][];
	for (int i = 0; i < fieldWorkArrayCount; i++) k[i] = createWorkArray();
}
}

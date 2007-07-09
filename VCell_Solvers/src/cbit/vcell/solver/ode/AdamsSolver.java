package cbit.vcell.solver.ode;

import java.io.File;

import org.vcell.util.SessionLog;

import cbit.vcell.solvers.SimulationJob;
import cbit.vcell.solvers.SolverException;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the class' description here.
 * Creation date: (8/19/2000 8:55:35 PM)
 * @author: John Wagner
 */
public abstract class AdamsSolver extends DefaultODESolver {
	int fieldWorkArrayCount;
	double[][] f;
/**
 * AdamsIntegrator constructor comment.
 * @param mathDescription cbit.vcell.math.MathDescription
 * @param sessionLog cbit.vcell.server.SessionLog
 * @param valueVectorCount int
 * @param temporaryVectorCount int
 */
public AdamsSolver(SimulationJob simulationJob, File directory, SessionLog sessionLog, int valueVectorCount, int workArrayCount)  throws SolverException {
	super(simulationJob, directory, sessionLog, valueVectorCount);
	fieldWorkArrayCount = workArrayCount;
}
protected void initialize() throws cbit.vcell.solvers.SolverException {
	super.initialize();
	f = new double[fieldWorkArrayCount][];
	for (int i = 0; i < fieldWorkArrayCount; i++) f[i] = createWorkArray();
}
protected void shiftWorkArrays() {
	double[] t = f[0];
	for (int i = 0; i < f.length - 1; i++) f[i] = f[i+1];
	f[f.length-1] = t;
}
}

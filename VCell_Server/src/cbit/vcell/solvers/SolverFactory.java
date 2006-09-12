package cbit.vcell.solvers;

import cbit.util.SessionLog;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.Solver;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.ode.*;
import java.io.*;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * The Abstract definition for the solver factory that creates
 * solver-related objects.
 * Creation date: (8/24/2000 11:23:26 PM)
 * @author: John Wagner
 */
public class SolverFactory {
/**
 * createODESolver method comment.
 */
public static cbit.vcell.solver.Solver createSolver(SessionLog sessionLog, File directory, SimulationJob simJob) throws SolverException {
	SolverDescription solverDescription = simJob.getWorkingSim().getSolverTaskDescription().getSolverDescription();
	if (solverDescription == null) {
		throw new IllegalArgumentException("SolverDescription cannot be null");
	}
	Solver solver = null;
	if (solverDescription.equals(SolverDescription.ForwardEuler)) {
		solver = new ForwardEulerSolver(simJob, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.RungeKutta2)) {
		solver = new RungeKuttaTwoSolver(simJob, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.RungeKutta4)) {
		solver = new RungeKuttaFourSolver(simJob, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.AdamsMoulton)) {
		solver = new AdamsMoultonFiveSolver(simJob, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.RungeKuttaFehlberg)) {
		solver = new RungeKuttaFehlbergSolver(simJob, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.LSODA)) {
		solver = new IDASolverStandalone(simJob, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.FiniteVolume)) {
		solver = new FVSolver(simJob, directory, sessionLog);
	} else {
		throw new SolverException("Unknown solver: " + solverDescription);
	}
	sessionLog.print("LocalSolverFactory.createSolver() returning " + solverDescription);
	return solver;
}
}

package cbit.vcell.solver;

import cbit.vcell.solvers.FVSolver;
import cbit.vcell.solvers.FVSolverStandalone;
import cbit.vcell.solver.ode.*;
import cbit.vcell.solver.stoch.HybridSolver;

import java.io.*;
import cbit.vcell.server.*;
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
 * create Solvers according to the solver description.
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
	} else if (solverDescription.equals(SolverDescription.CVODE)) {
		solver = new CVodeSolverStandalone(simJob, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.FiniteVolume)) {
		String fvstandaloneExe = PropertyLoader.getProperty(PropertyLoader.finiteVolumeExecutableProperty, null);
		if (fvstandaloneExe == null) {
			solver = new FVSolver(simJob, directory, sessionLog);
		} else {
			solver = new FVSolverStandalone(simJob, directory, sessionLog);
		}
	} else if (solverDescription.equals(SolverDescription.StochGibson)) {
		solver = new cbit.vcell.solver.stoch.GibsonSolver(simJob, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.HybridEuler)) {
		solver = new cbit.vcell.solver.stoch.HybridSolver(simJob, directory, sessionLog, HybridSolver.EMIntegrator);
	} else if (solverDescription.equals(SolverDescription.HybridMilstein)) {
		solver = new cbit.vcell.solver.stoch.HybridSolver(simJob, directory, sessionLog, HybridSolver.MilsteinIntegrator);
	} else if (solverDescription.equals(SolverDescription.HybridMilAdaptive)) {
		solver = new cbit.vcell.solver.stoch.HybridSolver(simJob, directory, sessionLog, HybridSolver.AdaptiveMilsteinIntegrator);
	}
	else {
		throw new SolverException("Unknown solver: " + solverDescription);
	}
	sessionLog.print("LocalSolverFactory.createSolver() returning " + solverDescription);
	return solver;
}
}

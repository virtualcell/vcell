package cbit.vcell.solver;

import java.io.File;

import org.vcell.util.SessionLog;

import cbit.vcell.solver.ode.AdamsMoultonFiveSolver;
import cbit.vcell.solver.ode.CVodeSolverStandalone;
import cbit.vcell.solver.ode.ForwardEulerSolver;
import cbit.vcell.solver.ode.IDASolverStandalone;
import cbit.vcell.solver.ode.RungeKuttaFehlbergSolver;
import cbit.vcell.solver.ode.RungeKuttaFourSolver;
import cbit.vcell.solver.ode.RungeKuttaTwoSolver;
import cbit.vcell.solver.stoch.HybridSolver;
import cbit.vcell.solvers.CombinedSundialsSolver;
import cbit.vcell.solvers.FVSolver;
import cbit.vcell.solvers.FVSolverStandalone;

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
public static Solver createSolver(SessionLog sessionLog, File directory, SimulationJob simJob) throws SolverException {
	SolverDescription solverDescription = simJob.getSimulation().getSolverTaskDescription().getSolverDescription();
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
	} else if (solverDescription.equals(SolverDescription.IDA)) {
		solver = new IDASolverStandalone(simJob, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.CVODE)) {
		solver = new CVodeSolverStandalone(simJob, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.CombinedSundials)) {
		solver = new CombinedSundialsSolver(simJob, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.FiniteVolume)) {
		solver = new FVSolver(simJob, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.FiniteVolumeStandalone)) {
		solver = new FVSolverStandalone(simJob, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.SundialsPDE)) {
		solver = new FVSolverStandalone(simJob, directory, sessionLog);
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

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver;

import java.io.File;

import org.vcell.solver.smoldyn.SmoldynSolver;
import org.vcell.util.SessionLog;

import cbit.vcell.solver.ode.AdamsMoultonFiveSolver;
import cbit.vcell.solver.ode.CVodeSolverStandalone;
import cbit.vcell.solver.ode.ForwardEulerSolver;
import cbit.vcell.solver.ode.IDASolverStandalone;
import cbit.vcell.solver.ode.RungeKuttaFehlbergSolver;
import cbit.vcell.solver.ode.RungeKuttaFourSolver;
import cbit.vcell.solver.ode.RungeKuttaTwoSolver;
import cbit.vcell.solver.stoch.GibsonSolver;
import cbit.vcell.solver.stoch.HybridSolver;
import cbit.vcell.solvers.CombinedSundialsSolver;
import cbit.vcell.solvers.FVSolver;
import cbit.vcell.solvers.FVSolverStandalone;

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
public static Solver createSolver(SessionLog sessionLog, File directory, SimulationJob simJob, boolean bMessaging) throws SolverException {
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
		solver = new IDASolverStandalone(simJob, directory, sessionLog, bMessaging);
	} else if (solverDescription.equals(SolverDescription.CVODE)) {
		solver = new CVodeSolverStandalone(simJob, directory, sessionLog, bMessaging);
	} else if (solverDescription.equals(SolverDescription.CombinedSundials)) {
		solver = new CombinedSundialsSolver(simJob, directory, sessionLog, bMessaging);
	} else if (solverDescription.equals(SolverDescription.FiniteVolume)) {
		solver = new FVSolver(simJob, directory, sessionLog, bMessaging);
	} else if (solverDescription.equals(SolverDescription.FiniteVolumeStandalone)) {
		solver = new FVSolverStandalone(simJob, directory, sessionLog, bMessaging);
	} else if (solverDescription.equals(SolverDescription.SundialsPDE)) {
		solver = new FVSolverStandalone(simJob, directory, sessionLog, bMessaging);
	} else if (solverDescription.equals(SolverDescription.StochGibson)) {
		solver = new GibsonSolver(simJob, directory, sessionLog, bMessaging);
	} else if (solverDescription.equals(SolverDescription.HybridEuler)) {
		solver = new cbit.vcell.solver.stoch.HybridSolver(simJob, directory, sessionLog, HybridSolver.EMIntegrator, bMessaging);
	} else if (solverDescription.equals(SolverDescription.HybridMilstein)) {
		solver = new cbit.vcell.solver.stoch.HybridSolver(simJob, directory, sessionLog, HybridSolver.MilsteinIntegrator, bMessaging);
	} else if (solverDescription.equals(SolverDescription.HybridMilAdaptive)) {
		solver = new cbit.vcell.solver.stoch.HybridSolver(simJob, directory, sessionLog, HybridSolver.AdaptiveMilsteinIntegrator, bMessaging);
	} else if (solverDescription.equals(SolverDescription.Smoldyn)) {
		solver = new SmoldynSolver(simJob, directory, sessionLog, bMessaging);
	}
	else {
		throw new SolverException("Unknown solver: " + solverDescription);
	}
	sessionLog.print("LocalSolverFactory.createSolver() returning " + solverDescription);
	return solver;
}
}

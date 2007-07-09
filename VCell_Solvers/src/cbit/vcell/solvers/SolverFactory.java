package cbit.vcell.solvers;

import java.io.File;

import org.vcell.util.SessionLog;

import cbit.vcell.simulation.SolverDescription;
import cbit.vcell.solver.ode.AdamsMoultonFiveSolver;
import cbit.vcell.solver.ode.ForwardEulerSolver;
import cbit.vcell.solver.ode.IDASolverStandalone;
import cbit.vcell.solver.ode.RungeKuttaFehlbergSolver;
import cbit.vcell.solver.ode.RungeKuttaFourSolver;
import cbit.vcell.solver.ode.RungeKuttaTwoSolver;
import cbit.vcell.solver.stoch.GibsonSolver;
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
		} else if (solverDescription.equals(SolverDescription.StochGibson)) {
			solver = new GibsonSolver(simJob, directory, sessionLog);
		}
		else {
			throw new SolverException("Unknown solver: " + solverDescription);
		}
		sessionLog.print("LocalSolverFactory.createSolver() returning " + solverDescription);
		return solver;
	}}

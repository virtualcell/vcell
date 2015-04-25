/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.server;

import java.io.File;

import org.vcell.solver.nfsim.NFSimSolver;
import org.vcell.solver.smoldyn.SmoldynSolver;
import org.vcell.util.SessionLog;

import cbit.util.xml.XmlUtil;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
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
import cbit.vcell.solvers.FVSolverStandalone;
import cbit.vcell.xml.XmlHelper;

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
public static Solver createSolver(SessionLog sessionLog, File directory, SimulationTask simTask, boolean bMessaging) throws SolverException {
	SolverDescription solverDescription = simTask.getSimulationJob().getSimulation().getSolverTaskDescription().getSolverDescription();

	File simTaskFile = new File(directory,simTask.getSimulationJobID()+"_"+simTask.getTaskID()+".simtask.xml");
	if (directory.exists() && !simTaskFile.exists()){
		try {
			String simTaskXmlText = XmlHelper.simTaskToXML(simTask);
			XmlUtil.writeXMLStringToFile(simTaskXmlText, simTaskFile.toString(), true);
		}catch (Exception e){
			e.printStackTrace(System.out);
			throw new SolverException("unable to write SimulationTask file");
		}
	}

	if (solverDescription == null) {
		throw new IllegalArgumentException("SolverDescription cannot be null");
	}
	Solver solver = null;
	if (solverDescription.equals(SolverDescription.ForwardEuler)) {
		solver = new ForwardEulerSolver(simTask, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.RungeKutta2)) {
		solver = new RungeKuttaTwoSolver(simTask, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.RungeKutta4)) {
		solver = new RungeKuttaFourSolver(simTask, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.AdamsMoulton)) {
		solver = new AdamsMoultonFiveSolver(simTask, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.RungeKuttaFehlberg)) {
		solver = new RungeKuttaFehlbergSolver(simTask, directory, sessionLog);
	} else if (solverDescription.equals(SolverDescription.IDA)) {
		solver = new IDASolverStandalone(simTask, directory, sessionLog, bMessaging);
	} else if (solverDescription.equals(SolverDescription.CVODE)) {
		solver = new CVodeSolverStandalone(simTask, directory, sessionLog, bMessaging);
	} else if (solverDescription.equals(SolverDescription.CombinedSundials)) {
		solver = new CombinedSundialsSolver(simTask, directory, sessionLog, bMessaging);
	}else if (solverDescription.equals(SolverDescription.FiniteVolumeStandalone) || solverDescription.equals(SolverDescription.FiniteVolume)) {
		solver = new FVSolverStandalone(simTask, directory, sessionLog, bMessaging);
	} else if (solverDescription.equals(SolverDescription.SundialsPDE)) {
		solver = new FVSolverStandalone(simTask, directory, sessionLog, bMessaging);
	} else if (solverDescription.equals(SolverDescription.StochGibson)) {
		solver = new GibsonSolver(simTask, directory, sessionLog, bMessaging);
	} else if (solverDescription.equals(SolverDescription.HybridEuler)) {
		solver = new HybridSolver(simTask, directory, sessionLog, HybridSolver.EMIntegrator, bMessaging);
	} else if (solverDescription.equals(SolverDescription.HybridMilstein)) {
		solver = new HybridSolver(simTask, directory, sessionLog, HybridSolver.MilsteinIntegrator, bMessaging);
	} else if (solverDescription.equals(SolverDescription.HybridMilAdaptive)) {
		solver = new HybridSolver(simTask, directory, sessionLog, HybridSolver.AdaptiveMilsteinIntegrator, bMessaging);
	} else if (solverDescription.equals(SolverDescription.Smoldyn)) {
		solver = new SmoldynSolver(simTask, directory, sessionLog, bMessaging);
	} else if (solverDescription.equals(SolverDescription.Chombo)) {
		solver = new FVSolverStandalone(simTask, directory, sessionLog, bMessaging);
	} else if (solverDescription.equals(SolverDescription.NFSim)) {
		solver = new NFSimSolver(simTask, directory, sessionLog, bMessaging);
	}
	else {
		throw new SolverException("Unknown solver: " + solverDescription);
	}
	sessionLog.print("LocalSolverFactory.createSolver() returning " + solverDescription);
	return solver;
}
}

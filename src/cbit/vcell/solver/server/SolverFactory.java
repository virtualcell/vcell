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
import java.util.HashMap;
import java.util.Map;

import org.vcell.solver.comsol.ComsolSolver;
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
import cbit.vcell.solvers.MovingBoundarySolver;
import cbit.vcell.xml.XmlHelper;
import edu.uchc.connjur.wb.ExecutionTrace;

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
	
	private interface Maker {
		Solver make(SimulationTask task, File userDir, File parallelDir, SessionLog log, boolean messaging) throws SolverException;
	}
	
	private static final Map<SolverDescription,Maker> FACTORY = new HashMap<SolverDescription, SolverFactory.Maker>( );
	static {
		{  //finite volume Java solvers
			Maker fv = (t,d,pd,sl,m) -> new FVSolverStandalone(t, d, pd, sl,m);
			FACTORY.put(SolverDescription.FiniteVolumeStandalone, fv); 
			FACTORY.put(SolverDescription.FiniteVolume, fv); 
			FACTORY.put(SolverDescription.SundialsPDE, fv); 
			Maker chomboMaker = new Maker(){
				@Override
				public Solver make(SimulationTask t, File d, File pd, SessionLog sl, boolean m) throws SolverException {
					if (t.getSimulation().getSolverTaskDescription().isParallel()){
						return new FVSolverStandalone(t,pd,d,sl,m);
					}else{
						return new FVSolverStandalone(t,d,null,sl,m);
					}
				}
			};
			FACTORY.put(SolverDescription.Chombo, chomboMaker); 
		}

		FACTORY.put(SolverDescription.ForwardEuler, (t,d,pd,sl,m) -> new ForwardEulerSolver(t, d, sl) );
		FACTORY.put(SolverDescription.RungeKutta2, (t,d,pd,sl,m) -> new RungeKuttaTwoSolver(t, d, sl) );
		FACTORY.put(SolverDescription.RungeKutta4, (t,d,pd,sl,m) -> new RungeKuttaFourSolver(t, d, sl) );
		FACTORY.put(SolverDescription.AdamsMoulton, (t,d,pd,sl,m) -> new AdamsMoultonFiveSolver(t, d, sl) );
		FACTORY.put(SolverDescription.RungeKuttaFehlberg, (t,d,pd,sl,m) -> new RungeKuttaFehlbergSolver(t, d, sl) );
		FACTORY.put(SolverDescription.IDA, (t,d,pd,sl,m) -> new IDASolverStandalone(t, d, sl,m) ); 
		FACTORY.put(SolverDescription.CVODE, (t,d,pd,sl,m) -> new CVodeSolverStandalone (t, d, sl,m) ); 
		FACTORY.put(SolverDescription.CombinedSundials, (t,d,pd,sl,m) -> new CombinedSundialsSolver(t, d, sl,m) ); 
		FACTORY.put(SolverDescription.StochGibson, (t,d,pd,sl,m) -> new GibsonSolver(t, d, sl,m) ); 
		FACTORY.put(SolverDescription.HybridEuler, (t,d,pd,sl,m) -> new HybridSolver(t, d, sl,HybridSolver.EMIntegrator,m) ); 
		FACTORY.put(SolverDescription.HybridMilstein, (t,d,pd,sl,m) -> new HybridSolver(t, d, sl,HybridSolver.MilsteinIntegrator,m) ); 
		FACTORY.put(SolverDescription.HybridMilAdaptive , (t,d,pd,sl,m) -> new HybridSolver(t, d, sl,HybridSolver.AdaptiveMilsteinIntegrator,m) ); 
		FACTORY.put(SolverDescription.Smoldyn, (t,d,pd,sl,m) -> new SmoldynSolver(t, d, sl,m) ); 
		FACTORY.put(SolverDescription.NFSim, (t,d,pd,sl,m) -> new NFSimSolver(t, d, sl,m) ); 
		FACTORY.put(SolverDescription.MovingBoundary, (t,d,pd,sl,m) -> new MovingBoundarySolver(t, d, sl,m) ); 
		FACTORY.put(SolverDescription.Comsol, (t,d,pd,sl,m) -> new ComsolSolver(t, d, sl) ); 
	}
	
public static Solver createSolver(SessionLog sessionLog, File userDir, SimulationTask simTask, boolean bMessaging) throws SolverException {
	{
		return createSolver(sessionLog, userDir, null, simTask, bMessaging);
	}
}

public static Solver createSolver(SessionLog sessionLog, File userDir, File parallelDir, SimulationTask simTask, boolean bMessaging) throws SolverException {
	SolverDescription solverDescription = simTask.getSimulationJob().getSimulation().getSolverTaskDescription().getSolverDescription();

	File simTaskFile = new File(userDir,simTask.getSimulationJobID()+"_"+simTask.getTaskID()+".simtask.xml");
	if (userDir.exists() && !simTaskFile.exists()){
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
	Maker maker = FACTORY.get(solverDescription);
	if (maker != null) {
		Solver s = maker.make(simTask, userDir, parallelDir, sessionLog,bMessaging); 
		sessionLog.print("LocalSolverFactory.createSolver() returning " + solverDescription + " solver " + ExecutionTrace.justClassName(s));
		return s;
	}
	throw new SolverException("Unknown solver: " + solverDescription);
}
}

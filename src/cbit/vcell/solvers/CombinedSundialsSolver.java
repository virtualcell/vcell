package cbit.vcell.solvers;

import cbit.vcell.math.AnnotatedFunction;
import cbit.vcell.solver.*;
import cbit.vcell.solver.ode.CVodeSolverStandalone;
import cbit.vcell.solver.ode.IDASolverStandalone;
import java.io.*;
import java.util.Vector;

import org.vcell.util.SessionLog;

/**
 * Insert the type's description here.
 * Creation date: (9/26/2003 2:08:08 PM)
 * @author: Fei Gao
 */
public class CombinedSundialsSolver extends AbstractCompiledSolver {
	private AbstractCompiledSolver realSolver = null;

/**
 * LSFSolver constructor comment.
 * @param simulation cbit.vcell.solver.Simulation
 * @param directory java.io.File
 * @param sessionLog cbit.vcell.server.SessionLog
 * @exception cbit.vcell.solver.SolverException The exception description.
 */
public CombinedSundialsSolver(SimulationJob simJob, File directory, SessionLog sessionLog, boolean bMessaging) throws cbit.vcell.solver.SolverException {
	super(simJob, directory, sessionLog);
	if (simulationJob.getSimulation().getMathDescription().hasFastSystems()) {
		realSolver = new IDASolverStandalone(simJob, directory, sessionLog, bMessaging);
	} else {
		realSolver = new CVodeSolverStandalone(simJob, directory, sessionLog, bMessaging);
	}
	realSolver.addSolverListener(new SolverListener() {
		public final void solverAborted(SolverEvent event) {		
			fireSolverAborted(event.getSimulationMessage());
		}


		public final void solverFinished(SolverEvent event) {
			fireSolverFinished();
		}


		public final void solverPrinted(SolverEvent event) {
			fireSolverPrinted(event.getTimePoint());
		}


		public final void solverProgress(SolverEvent event) {
			fireSolverProgress(event.getProgress());
		}


		public final void solverStarting(SolverEvent event) {
			fireSolverStarting(event.getSimulationMessage());
		}


		public final void solverStopped(SolverEvent event) {
			fireSolverStopped();
		}
		
	});	
}

@Override
public void cleanup() {
	realSolver.cleanup();	
}

@Override
protected ApplicationMessage getApplicationMessage(String message) {
	return realSolver.getApplicationMessage(message);
}

@Override
protected void initialize() throws SolverException {
	realSolver.initialize();
	
}

@Override
protected MathExecutable getMathExecutable() {	
	return realSolver.getMathExecutable();
}

@Override
public Vector<AnnotatedFunction> createFunctionList() {
	return realSolver.createFunctionList();
}
}
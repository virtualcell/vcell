package cbit.vcell.solvers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.vcell.util.Executable;
import org.vcell.util.PropertyLoader;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.solver.*;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import cbit.vcell.messaging.server.SimulationTask;

/**
 * Insert the type's description here.
 * Creation date: (9/26/2003 2:08:08 PM)
 * @author: Fei Gao
 */
public abstract class HTCSolver extends AbstractSolver {
	protected AbstractSolver realSolver = null;
	protected java.lang.String cmdArguments = "";
	protected SimulationTask simulationTask = null;

/**
 * LSFSolver constructor comment.
 * @param simulation cbit.vcell.solver.Simulation
 * @param directory java.io.File
 * @param sessionLog cbit.vcell.server.SessionLog
 * @exception cbit.vcell.solver.SolverException The exception description.
 */
public HTCSolver(SimulationTask simTask, java.io.File directory, org.vcell.util.SessionLog sessionLog) throws cbit.vcell.solver.SolverException {
	super(simTask.getSimulationJob(), directory, sessionLog);
	simulationTask = simTask;
	if (!getSimulation().getSolverTaskDescription().getSolverDescription().isInterpretedSolver()) {
		realSolver = (AbstractSolver)SolverFactory.createSolver(sessionLog, directory, simTask.getSimulationJob());
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
	cmdArguments = "-tid " + String.valueOf(simTask.getTaskID());
}

/**
 *  This method takes the place of the old runUnsteady()...
 */
protected void initialize() throws SolverException {
	if (getSimulation().getSolverTaskDescription().getSolverDescription().isInterpretedSolver()) {
		writeJavaInputFile();
	} else {
		realSolver.initialize(); 
	}
}

protected void writeJavaInputFile() throws SolverException {
	PrintWriter pw = null;
	try {
		File inputFile = new File(getBaseName() + JAVA_INPUT_EXTENSION);
		if (!inputFile.exists()) {	// write input file which is just xml				
			String xmlString = XmlHelper.simToXML(getSimulationJob().getWorkingSim());
			pw = new PrintWriter(inputFile);
			pw.println(xmlString);
			pw.close();
		}		
	} catch (XmlParseException e) {		
		e.printStackTrace(System.out);
		throw new SolverException(e.getMessage());
	} catch (FileNotFoundException e) {
		e.printStackTrace(System.out);
		throw new SolverException(e.getMessage());
	} finally {
		if (pw != null) {
			pw.close();
		}
	}
	
}

public String getExecutableCommand() {
	if (realSolver instanceof AbstractCompiledSolver) {
		return ((AbstractCompiledSolver)realSolver).getMathExecutable().getCommand();
	} else {
		File inputFile = new File(getBaseName() + JAVA_INPUT_EXTENSION);
		Executable exe = new Executable(new String[]{PropertyLoader.getRequiredProperty(PropertyLoader.javaSimulationExecutable), 
				VCellServerID.getSystemServerID().toString(), inputFile.getParent(), inputFile.getName(), getJobIndex()+""});
		return exe.getCommand();
	}
}
}
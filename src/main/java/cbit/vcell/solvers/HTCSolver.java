/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solvers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.vcell.util.Executable;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solver.server.SolverListener;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

/**
 * Insert the type's description here.
 * Creation date: (9/26/2003 2:08:08 PM)
 * @author: Fei Gao
 */
public abstract class HTCSolver extends AbstractSolver {
	protected AbstractSolver realSolver = null;
	protected ArrayList<String> cmdArguments = new ArrayList<String>();
	protected SimulationTask simulationTask = null;

/**
 * LSFSolver constructor comment.
 * @param userDirectory java.io.File
 * @param parallelDirectory if required, may be null 
 * @param sessionLog cbit.vcell.server.SessionLog
 * @param simulation cbit.vcell.solver.Simulation
 * @exception cbit.vcell.solver.SolverException The exception description.
 */
public HTCSolver(SimulationTask simTask, File userDirectory, File parallelDirectory, SessionLog sessionLog) throws SolverException {
	super(simTask, userDirectory, sessionLog);
	simulationTask = simTask;
	if (!simTask.getSimulation().getSolverTaskDescription().getSolverDescription().isJavaSolver()) {
		realSolver = (AbstractSolver)SolverFactory.createSolver(sessionLog, userDirectory, parallelDirectory, simTask, true);
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
	cmdArguments.add("-tid");
	cmdArguments.add(String.valueOf(simTask.getTaskID()));
}

/**
 *  This method takes the place of the old runUnsteady()...
 */
protected void initialize() throws SolverException {
	if (simTask.getSimulation().getSolverTaskDescription().getSolverDescription().isJavaSolver()) {
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
			String xmlString = XmlHelper.simToXML(simTask.getSimulation());
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
				/*VCellServerID.getSystemServerID().toString(),*/ inputFile.getParent(), inputFile.getName(), getJobIndex()+""});
		return exe.getCommand();
	}
}
}

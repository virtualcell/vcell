/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode;

import java.io.PrintWriter;

import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.MathExecutable;
/**
 * Insert the type's description here.
 * Creation date: (10/23/2004 8:07:49 AM)
 * @author: Jim Schaff
 */
public class CVodeSolverStandalone extends SundialsSolver {
/**
 * IDASolverStandalone constructor comment.
 * @param simulation cbit.vcell.solver.Simulation
 * @param directory java.io.File
 * @param sessionLog cbit.vcell.server.SessionLog
 * @exception cbit.vcell.solver.SolverException The exception description.
 */
public CVodeSolverStandalone(SimulationTask simTask, java.io.File directory, SessionLog sessionLog, boolean bMessaging) throws SolverException {
	super(simTask, directory, sessionLog, bMessaging);
}
/**
 *  This method takes the place of the old runUnsteady()...
 */
protected void initialize() throws SolverException {
	
	fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INIT);
	super.initialize();

	String inputFilename = getInputFilename();

	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING, SimulationMessage.MESSAGE_SOLVER_RUNNING_INPUT_FILE));
	fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INPUT_FILE);

	PrintWriter pw = null;
	try {
		pw = new java.io.PrintWriter(inputFilename);
		CVodeFileWriter cvodeFileWriter = new CVodeFileWriter(pw, simTask, bMessaging);
		cvodeFileWriter.write();
	} catch (Exception e) {
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED, SimulationMessage.solverAborted("CVODE solver could not generate input file: " + e.getMessage())));
		e.printStackTrace(System.out);
		throw new SolverException("CVODE solver could not generate input file: " + e.getMessage());
	} finally {
		if (pw != null) {
			pw.close();
		}
	}

	setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING,SimulationMessage.MESSAGE_SOLVER_RUNNING_START));	
	
	setMathExecutable(new MathExecutable(getMathExecutableCommand(),getSaveDirectory()));
}

private String getInputFilename(){
	return getBaseName() + CVODEINPUT_DATA_EXTENSION;
}

private String getOutputFilename(){
	return getBaseName() + IDA_DATA_EXTENSION;
}

@Override
protected String[] getMathExecutableCommand() {
	String executableName = PropertyLoader.getRequiredProperty(PropertyLoader.sundialsSolverExecutableProperty);
	String inputFilename = getInputFilename();
	String outputFilename = getOutputFilename();
	return new String[] { executableName, inputFilename, outputFilename };
}
}

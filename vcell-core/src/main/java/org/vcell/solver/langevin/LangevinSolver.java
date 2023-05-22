/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.solver.langevin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.vcell.util.exe.ExecutableException;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.LangevinSimulationOptions;
import cbit.vcell.solver.NFsimSimulationOptions;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.ApplicationMessage;
import cbit.vcell.solvers.MathExecutable;
import cbit.vcell.solvers.SimpleCompiledSolver;

/**
 * Langevin solver
 * 
 */
public class LangevinSolver extends SimpleCompiledSolver {

	public LangevinSolver(SimulationTask simTask, java.io.File directory,
			boolean bMsging) throws SolverException {
		super(simTask, directory, bMsging);
	}

	/**
	 * Insert the method's description here. Creation date: (7/13/2006 9:00:41
	 * AM)
	 */
	public void cleanup() {
	}

	/**
	 * show progress. Creation date: (7/13/2006 9:00:41 AM)
	 * 
	 * @return cbit.vcell.solvers.ApplicationMessage
	 * @param message
	 *            java.lang.String
	 */
	protected ApplicationMessage getApplicationMessage(String message) {
		String SEPARATOR = ":";
		String DATA_PREFIX = "data:";
		String PROGRESS_PREFIX = "progress:";
		if (message.startsWith(DATA_PREFIX)) {
			double timepoint = Double.parseDouble(message.substring(message.lastIndexOf(SEPARATOR) + 1));
			setCurrentTime(timepoint);
			return new ApplicationMessage(ApplicationMessage.DATA_MESSAGE, getProgress(), timepoint, null, message);
		} else if (message.startsWith(PROGRESS_PREFIX)) {
			String progressString = message.substring(message.lastIndexOf(SEPARATOR) + 1, message.indexOf("%"));
			double progress = Double.parseDouble(progressString) / 100.0;
			// double startTime =
			// getSimulation().getSolverTaskDescription().getTimeBounds().getStartingTime();
			// double endTime =
			// getSimulation().getSolverTaskDescription().getTimeBounds().getEndingTime();
			// setCurrentTime(startTime + (endTime-startTime)*progress);
			return new ApplicationMessage(ApplicationMessage.PROGRESS_MESSAGE, progress, -1, null, message);
		} else {
			throw new RuntimeException("unrecognized message");
		}
	}

	/**
	 * This method takes the place of the old runUnsteady()...
	 */
	protected void initialize() throws SolverException {
		if (lg.isTraceEnabled()) lg.trace("LangevinSolver.initialize()");
		fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INIT);
		writeFunctionsFile();

		String inputFilename = getInputFilename();
		if (lg.isTraceEnabled()) lg.trace("LangevinSolver.initialize() inputFilename = " + getInputFilename()); 

		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING, SimulationMessage.MESSAGE_SOLVER_RUNNING_INPUT_FILE));
		fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INPUT_FILE);

		try (PrintWriter pw = new PrintWriter(inputFilename)) {
			LangevinFileWriter stFileWriter = new LangevinFileWriter(pw, simTask, bMessaging);
			stFileWriter.write();
		} catch (Exception e) {
			setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED,
					SimulationMessage.solverAborted("Could not generate input file: " + e.getMessage())));
			e.printStackTrace(System.out);
			throw new SolverException(e.getMessage());
		} 

		PrintWriter lg = null;
		String logFilename = getLogFilename();
		String outputFilename = getOutputFilename();
		try {
			lg = new PrintWriter(logFilename);
			String shortOutputFilename = outputFilename.substring(1 + outputFilename.lastIndexOf("\\"));
			lg.println(NFSIM_DATA_IDENTIFIER + " " + shortOutputFilename);
		} catch (Exception e) {
			setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED,
					SimulationMessage.solverAborted("Could not generate log file: " + e.getMessage())));
			e.printStackTrace(System.out);
			throw new SolverException(e.getMessage());
		} finally {
			if (lg != null) {
				lg.close();
			}
		}

		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING,
				SimulationMessage.MESSAGE_SOLVER_RUNNING_START));
		// get executable path+name.
		File saveDirectory = getSaveDirectory();
		String[] mathExecutableCommand = getMathExecutableCommand();
		setMathExecutable(new MathExecutable(mathExecutableCommand, saveDirectory));
		// setMathExecutable(new
		// cbit.vcell.solvers.MathExecutable(executableName + " gibson " +
		// getBaseName() + ".stochInput" + " " + getBaseName() + ".stoch"));
	}

	private String getInputFilename() {
		return getBaseName() + LANGEVIN_INPUT_FILE_EXTENSION;
	}

	private String getLogFilename() {
		return getBaseName() + LANGEVIN_OUTPUT_LOG_EXTENSION;
	}

	private String getOutputFilename() {
		return getBaseName() + LANGEVIN_OUTPUT_FILE_EXTENSION;
	}
	public String getSpeciesOutputFilename() {
		return getBaseName() + ".species";
	}

	@Override
	protected String[] getMathExecutableCommand() {
		String executableName = null;
		try {
			executableName = SolverUtilities.getExes(SolverDescription.Langevin)[0].getAbsolutePath();
		}catch (IOException e){
			throw new RuntimeException("failed to get executable for solver "+SolverDescription.Langevin.getDisplayLabel()+": "+e.getMessage(),e);
		}
		String inputFilename = getInputFilename();
		String outputFilename = getOutputFilename();
		String speciesOutputFilename = getSpeciesOutputFilename();
		String logFilename = getLogFilename();
		
		LangevinSimulationOptions lso = simTask.getSimulation().getSolverTaskDescription().getLangevinSimulationOptions();
		int numOfTrials = lso.getNumOfTrials();
		
		ArrayList<String> cmds = new ArrayList<String>();
		cmds.add(executableName);	// executable
		cmds.add(inputFilename);	// first argument
		cmds.add(numOfTrials + "");	// second argument
		cmds.add(logFilename);		// used for solver to send status info to client (3rd argument);
		
		return cmds.toArray(new String[cmds.size()]);
	}
	
	@Override
	public String translateSimulationMessage(String simulationMessage) {
		
		// TODO: translate langevin solver error codes into nice user messages
		
		return simulationMessage;
	}

	public void propertyChange(java.beans.PropertyChangeEvent event) {
		super.propertyChange(event);

		if (event.getSource() == getMathExecutable()
				&& event.getPropertyName().equals("applicationMessage")) {
			String messageString = (String) event.getNewValue();
			if (messageString == null || messageString.length() == 0) {
				return;
			}
			ApplicationMessage appMessage = getApplicationMessage(messageString);
			if (appMessage != null
					&& appMessage.getMessageType() == ApplicationMessage.PROGRESS_MESSAGE) {
				fireSolverPrinted(getCurrentTime());
			}
		}
	}

	public static void main(String[] args) {

		try {

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {

		}
	}
}


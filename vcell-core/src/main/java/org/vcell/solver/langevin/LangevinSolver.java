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

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.*;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.ApplicationMessage;
import cbit.vcell.solvers.MathExecutable;
import cbit.vcell.solvers.SimpleCompiledSolver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Langevin solver
 * 
 */
public class LangevinSolver extends SimpleCompiledSolver {

	/**
	 * LangevinNoVis01 output file names
	 */

	private String logFileString = null;	// logfile name as it appears in argument list
											// it is the value of getLogFilename(), but it's set late
	
	public LangevinSolver(SimulationTask simTask, File directory, boolean bMsging) throws SolverException {
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

	protected void initialize() throws SolverException {
		if (lg.isTraceEnabled()) lg.trace("LangevinSolver.initialize()");
		fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INIT);
		writeFunctionsFile();

		String inputFilename = getInputFilename();
		if (lg.isTraceEnabled()) lg.trace("LangevinSolver.initialize() inputFilename = " + getInputFilename()); 

		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING, SimulationMessage.MESSAGE_SOLVER_RUNNING_INPUT_FILE));
		fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INPUT_FILE);

		try (PrintWriter pw = new PrintWriter(inputFilename)) {
			// here we create the langevin input file
			LangevinFileWriter stFileWriter = new LangevinFileWriter(pw, simTask, bMessaging);
			stFileWriter.write();
		} catch (Exception e) {
			setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED,
					SimulationMessage.solverAborted("Could not generate input file: " + e.getMessage())));
			e.printStackTrace(System.out);
			throw new SolverException(e.getMessage());
		}

		if (bMessaging) {
			String messagingConfigFilename = getMessagingConfigFilename();
			try (PrintWriter pw = new PrintWriter(messagingConfigFilename)) {
				// here we create the langevin input file
				writeLangevinMessagingConfig(pw, simTask);
			} catch (Exception e) {
				setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED,
						SimulationMessage.solverAborted("Could not generate messaging config file: " + e.getMessage())));
				e.printStackTrace(System.out);
				throw new SolverException(e.getMessage());
			}
		}

		PrintWriter lg = null;
		String logFilename = getLogFilename();
		String outputFilename = getOutputFilename();
		try {
			lg = new PrintWriter(logFilename);
			String shortOutputFilename = new File(outputFilename).getName();
			lg.println(IDA_DATA_IDENTIFIER + "\n" + IDA_DATA_FORMAT_ID + "\n" + shortOutputFilename);
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
		
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING, SimulationMessage.MESSAGE_SOLVER_RUNNING_START));
		String[] mathExecutableCommand = getMathExecutableCommand();
		File saveDirectoryFile = getSaveDirectory();	// working directory (not needed?)

		MathExecutable me = new MathExecutable(mathExecutableCommand, saveDirectoryFile);
		setMathExecutable(me);

		setLogFileString(getLogFilename());		// variable is set "late"  TODO: may be not needed
	}

	private void writeLangevinMessagingConfig(PrintWriter pw, SimulationTask simTask){
		Simulation simulation = simTask.getSimulationJob().getSimulation();
		String jmsPassword = PropertyLoader.getSecretValue(PropertyLoader.jmsPasswordValue, PropertyLoader.jmsPasswordFile);
		String jmsUser = PropertyLoader.getRequiredProperty(PropertyLoader.jmsUser);
		String jmshost = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimHostExternal);
		String jmsrestport = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimRestPortExternal);
		//                broker_host=localhost
		//                broker_port=8165
		//                broker_username=msg_user
		//                broker_password=msg_pswd
		//                vc_username=vcell_user
		//                simKey=12334483837
		//                taskID=0
		//                jobIndex=0
		pw.println("broker_host="+jmshost);
		pw.println("broker_port="+jmsrestport);
		pw.println("broker_username="+"admin");
		pw.println("broker_password="+"admin");
		pw.println("vc_username="+simulation.getVersion().getOwner().getName());
		pw.println("simKey="+simulation.getVersion().getVersionKey());
		pw.println("taskID="+simTask.getTaskID());
		pw.println("jobIndex="+simTask.getSimulationJob().getJobIndex());
	}

	private String getInputFilename() {
		return getBaseName() + LANGEVIN_INPUT_FILE_EXTENSION;
	}

	private String getMessagingConfigFilename() {
		return getBaseName() + LANGEVIN_MESSAGINGCONFIG_FILE_EXTENSION;
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
		String executableName;
		try {
			executableName = SolverUtilities.getExes(SolverDescription.Langevin)[0].getAbsolutePath();
		}catch (IOException e){
			throw new RuntimeException("failed to get executable for solver "+SolverDescription.Langevin.getDisplayLabel()+": "+e.getMessage(),e);
		}
		String inputFilename = getInputFilename();
		String logFileOption = "--output-log=" + getLogFilename();
		String messagingConfigOption = "--vc-send-status-config=" + getMessagingConfigFilename();
		String localMessagingOption = "--vc-print-status";
		
		LangevinSimulationOptions lso = simTask.getSimulation().getSolverTaskDescription().getLangevinSimulationOptions();
		int runIndex = lso.getRunIndex();		// run index
		
		ArrayList<String> cmds = new ArrayList<>();
		cmds.add(executableName);	// executable
		cmds.add("simulate");		// the new langevin solver made by jim wants this argument
		cmds.add(logFileOption);	// used for solver to send status info to client (3rd argument);
		if (bMessaging){
			cmds.add(messagingConfigOption);	// used for solver to send status info to client;
		} else {
			cmds.add(localMessagingOption);	    // used for solver to send status to stdout/stderr;
		}
		cmds.add(inputFilename);	// first argument
		cmds.add(runIndex + "");	// second argument
		
		return cmds.toArray(new String[0]);
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

	public String getLogFileString() {
		return logFileString;
	}

	public void setLogFileString(String logFileString) {
		this.logFileString = logFileString;
	}
}


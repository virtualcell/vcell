/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.solver.nfsim;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.DefaultOutputTimeSpec;
import cbit.vcell.solver.NFsimSimulationOptions;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.ApplicationMessage;
import cbit.vcell.solvers.MathExecutable;
import cbit.vcell.solvers.SimpleCompiledSolver;

/**
 * Gibson solver Creation date: (7/13/2006 9:00:41 AM)
 * 
 * @author: Tracy LI
 */
public class NFSimSolver extends SimpleCompiledSolver {

	public NFSimSolver(SimulationTask simTask, java.io.File directory,
			SessionLog sessionLog, boolean bMsging) throws SolverException {
		super(simTask, directory, sessionLog, bMsging);
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
			double timepoint = Double.parseDouble(message.substring(message
					.lastIndexOf(SEPARATOR) + 1));
			setCurrentTime(timepoint);
			return new ApplicationMessage(ApplicationMessage.DATA_MESSAGE,
					getProgress(), timepoint, null, message);
		} else if (message.startsWith(PROGRESS_PREFIX)) {
			String progressString = message.substring(
					message.lastIndexOf(SEPARATOR) + 1, message.indexOf("%"));
			double progress = Double.parseDouble(progressString) / 100.0;
			// double startTime =
			// getSimulation().getSolverTaskDescription().getTimeBounds().getStartingTime();
			// double endTime =
			// getSimulation().getSolverTaskDescription().getTimeBounds().getEndingTime();
			// setCurrentTime(startTime + (endTime-startTime)*progress);
			return new ApplicationMessage(ApplicationMessage.PROGRESS_MESSAGE,
					progress, -1, null, message);
		} else {
			throw new RuntimeException("unrecognized message");
		}
	}

	/**
	 * This method takes the place of the old runUnsteady()...
	 */
	protected void initialize() throws SolverException {
		SessionLog sessionLog = getSessionLog();
		sessionLog.print("NFSimSolver.initialize()");
		fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INIT);
		writeFunctionsFile();

		String inputFilename = getInputFilename();
		sessionLog.print("NFSimSolver.initialize() inputFilename = " + getInputFilename()); 

		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING,
				SimulationMessage.MESSAGE_SOLVER_RUNNING_INPUT_FILE));
		fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INPUT_FILE);

		try (PrintWriter pw = new PrintWriter(inputFilename)) {
			NFSimFileWriter stFileWriter = new NFSimFileWriter(pw, simTask, bMessaging);
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
			String shortOutputFilename = outputFilename
					.substring(1 + outputFilename.lastIndexOf("\\"));
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
		setMathExecutable(new MathExecutable(getMathExecutableCommand()));
		// setMathExecutable(new
		// cbit.vcell.solvers.MathExecutable(executableName + " gibson " +
		// getBaseName() + ".stochInput" + " " + getBaseName() + ".stoch"));
	}

	private String getInputFilename() {
		return getBaseName() + NFSIM_INPUT_FILE_EXTENSION;
	}

	private String getLogFilename() {
		return getBaseName() + NFSIM_OUTPUT_LOG_EXTENSION;
	}

	private String getOutputFilename() {
		return getBaseName() + NFSIM_OUTPUT_FILE_EXTENSION;
	}
	private String getSpeciesOutputFilename() {
		return getBaseName() + ".species";
	}

	@Override
	protected String[] getMathExecutableCommand() {
		String executableName = PropertyLoader
				.getRequiredProperty(PropertyLoader.nfsimExecutableProperty);
		String inputFilename = getInputFilename();
		String outputFilename = getOutputFilename();
		String speciesOutputFilename = getSpeciesOutputFilename();
		
		NFsimSimulationOptions nfsso = simTask.getSimulation().getSolverTaskDescription().getNFSimSimulationOptions();
		ArrayList<String> adv = new ArrayList<String>();
		boolean observableComputationOff = nfsso.getObservableComputationOff();
		if(observableComputationOff == true) {
			adv.add("-notf");		// false is by default, no need to specify
		}
		Integer moleculeDistance = nfsso.getMoleculeDistance();
		if(moleculeDistance != null) {
			adv.add("-utl");
			adv.add(moleculeDistance+"");
		}
		boolean aggregateBookkeeping = nfsso.getAggregateBookkeeping();
		if(aggregateBookkeeping == true || simTask.getSimulation().getMathDescription().hasSpeciesObservable()) {
			adv.add("-cb");			// false is by default, no need to specify
		}
		Integer maxMoleculesPerType = nfsso.getMaxMoleculesPerType();
		if(maxMoleculesPerType != null) {
			adv.add("-gml");
			adv.add(maxMoleculesPerType+"");
		}
		Integer equilibrateTime = nfsso.getEquilibrateTime();
		if(equilibrateTime != null) {
			adv.add("-eq");
			adv.add(equilibrateTime+"");
		}
		boolean preventIntraBonds = nfsso.getPreventIntraBonds();
		if(preventIntraBonds == true) {
			adv.add("-bscb");			// false is by default, no need to specify
		}
		
		TimeBounds tb = getSimulationJob().getSimulation().getSolverTaskDescription().getTimeBounds();
		double dtime = tb.getEndingTime() - tb.getStartingTime();
		
		String timeSpecOption1 = "-oSteps";
		String timeSpecOption2 = "10";
		OutputTimeSpec outputTimeSpec = getSimulationJob().getSimulation().getSolverTaskDescription().getOutputTimeSpec();
		if(outputTimeSpec instanceof DefaultOutputTimeSpec) {
			DefaultOutputTimeSpec dots = (DefaultOutputTimeSpec) outputTimeSpec;
			int steps = dots.getKeepAtMost();
			timeSpecOption1 = "-oSteps";
			timeSpecOption2 = Integer.toString(steps);
		} else if(outputTimeSpec instanceof UniformOutputTimeSpec) {
			UniformOutputTimeSpec dots = (UniformOutputTimeSpec) outputTimeSpec;
			double steps = dtime / dots.getOutputTimeStep();
			timeSpecOption1 = "-oSteps";
			int stepsi = (int)Math.round(steps);
			timeSpecOption2 = Integer.toString(stepsi);
		} else {
			throw new RuntimeException("Unsupported output time spec class");
		}
		
		Integer seed = nfsso.getRandomSeed();
		
		String baseCommands[] = { "-xml", inputFilename, "-o", outputFilename, "-sim", Double.toString(dtime), "-ss", speciesOutputFilename };
		ArrayList<String> cmds = new ArrayList<String>();
		cmds.add(executableName);
		if(seed != null) {
			cmds.add("-seed");
			cmds.add(seed.toString());
		}
		
		cmds.add("-vcell");
		
		cmds.addAll(new ArrayList<String>(Arrays.asList(baseCommands)));
		cmds.add(timeSpecOption1);
		cmds.add(timeSpecOption2);
		
		cmds.addAll(adv);
		if (bMessaging) {
			cmds.add("-v");
		}
		return cmds.toArray(new String[cmds.size()]);
	}

	/**
	 * Insert the method's description here. Creation date: (10/11/2006 11:16:02
	 * AM)
	 */
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

}

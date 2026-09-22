/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.solver.fenics;

import cbit.util.xml.XmlUtil;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.ApplicationMessage;
import cbit.vcell.solvers.MathExecutable;
import cbit.vcell.solvers.SimpleCompiledSolver;
import cbit.vcell.xml.XmlHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * FEniCSx finite-element solver (<a href="https://github.com/virtualcell/vcell-fenics">vcell-fenics</a>).
 * <p>
 * Unlike the other compiled solvers there is no Java-side input writer: the solver reads the
 * SimulationTask XML that {@link cbit.vcell.solver.server.SolverFactory} writes for every solver, and
 * writes its own results bundle, {@code <SimID_..._>.fenics/} (VTU meshes + zarr fields), into the
 * save directory. Status is reported the Langevin way: {@code [[[progress/data]]]} markers on stdout
 * ({@code --vc-print-status}) for a local run, or REST WorkerEvents driven by a messaging-config file
 * ({@code --vc-send-status-config}) on the cluster. The contract is vcell-fenics ADR 011; the VCell
 * side is planned in docs/plan-fenics.md.
 * <p>
 * The executable is {@code vcell-fenics} on the container's PATH. On the cluster SlurmProxy supplies
 * the container prefix; for a local run the caller installs one with {@link #setCommandPrefix(List)}
 * (e.g. {@code docker run ... <image>}).
 */
public class FenicsSolver extends SimpleCompiledSolver {

	private static final Logger lg = LogManager.getLogger(FenicsSolver.class);

	public static final String EXECUTABLE_NAME = "vcell-fenics";

	private List<String> commandPrefix = List.of();

	public FenicsSolver(SimulationTask simTask, File directory, boolean bMsging) throws SolverException {
		super(simTask, directory, bMsging);
	}

	/**
	 * @param commandPrefix tokens placed before {@code vcell-fenics} in the command, e.g. a
	 * {@code docker run} invocation; empty (the default) runs {@code vcell-fenics} directly.
	 */
	public void setCommandPrefix(List<String> commandPrefix) {
		this.commandPrefix = List.copyOf(commandPrefix);
	}

	@Override
	public void cleanup() {
	}

	/**
	 * Parses the solver's stdout markers: {@code data:<t>} (value after the last ':') and
	 * {@code progress:NN.N%} (value between the last ':' and '%').
	 */
	@Override
	protected ApplicationMessage getApplicationMessage(String message) {
		if (message.startsWith(DATA_PREFIX)) {
			double timepoint = Double.parseDouble(message.substring(message.lastIndexOf(SEPARATOR) + 1));
			setCurrentTime(timepoint);
			return new ApplicationMessage(ApplicationMessage.DATA_MESSAGE, getProgress(), timepoint, null, message);
		} else if (message.startsWith(PROGRESS_PREFIX)) {
			String progressString = message.substring(message.lastIndexOf(SEPARATOR) + 1, message.indexOf("%"));
			double progress = Double.parseDouble(progressString) / 100.0;
			return new ApplicationMessage(ApplicationMessage.PROGRESS_MESSAGE, progress, -1, null, message);
		} else {
			throw new RuntimeException("unrecognized message from " + EXECUTABLE_NAME + ": " + message);
		}
	}

	@Override
	protected void initialize() throws SolverException {
		lg.trace("FenicsSolver.initialize()");
		fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING_INIT);

		// SolverFactory writes the task file only when the directory already exists; make sure it is there.
		File simTaskFile = getSimTaskFile();
		if (!simTaskFile.exists()) {
			try {
				XmlUtil.writeXMLStringToFile(XmlHelper.simTaskToXML(simTask), simTaskFile.getPath(), true);
			} catch (Exception e) {
				abort("Could not write SimulationTask file: " + e.getMessage(), e);
			}
		}

		if (bMessaging) {
			try (PrintWriter pw = new PrintWriter(getMessagingConfigFilename())) {
				writeMessagingConfig(pw, simTask);
			} catch (Exception e) {
				abort("Could not generate messaging config file: " + e.getMessage(), e);
			}
		}

		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING, SimulationMessage.MESSAGE_SOLVER_RUNNING_START));
		setMathExecutable(new MathExecutable(getMathExecutableCommand(), getSaveDirectory()));
	}

	private void abort(String message, Exception e) throws SolverException {
		setSolverStatus(new SolverStatus(SolverStatus.SOLVER_ABORTED, SimulationMessage.solverAborted(message)));
		lg.error(message, e);
		throw new SolverException(message, e);
	}

	/**
	 * The broker coordinates, in the properties format vcell-fenics shares with the Langevin solver
	 * (see {@code LangevinSolver.writeLangevinMessagingConfig}).
	 */
	static void writeMessagingConfig(PrintWriter pw, SimulationTask simTask) {
		Simulation simulation = simTask.getSimulationJob().getSimulation();
		String jmshost = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimHostExternal);
		String jmsrestport = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimRestPortExternal);
		pw.println("broker_host=" + jmshost);
		pw.println("broker_port=" + jmsrestport);
		// the same fixed REST credentials LangevinSolver writes
		pw.println("broker_username=" + "admin");
		pw.println("broker_password=" + "admin");
		pw.println("vc_username=" + simulation.getVersion().getOwner().getName());
		pw.println("simKey=" + simulation.getVersion().getVersionKey());
		pw.println("taskID=" + simTask.getTaskID());
		pw.println("jobIndex=" + simTask.getSimulationJob().getJobIndex());
	}

	/** {@code <saveDir>/SimID_<key>_<job>__<taskID>.simtask.xml}, the name SolverFactory writes. */
	File getSimTaskFile() {
		return new File(getSaveDirectory(), simTask.getSimulationJobID() + "_" + simTask.getTaskID() + ".simtask.xml");
	}

	String getMessagingConfigFilename() {
		return getBaseName() + FENICS_MESSAGINGCONFIG_FILE_EXTENSION;
	}

	/** the results bundle directory the solver writes, {@code <saveDir>/SimID_<key>_<job>_.fenics} */
	public File getBundleDirectory() {
		return new File(getBaseName() + FENICS_BUNDLE_EXTENSION);
	}

	/**
	 * {@code [prefix...] vcell-fenics --simtask <file> --out <saveDir> (--vc-send-status-config=<cfg> | --vc-print-status)}.
	 * On the cluster HtcSimulationWorker appends {@code -tid <taskID>}, which the CLI accepts.
	 */
	@Override
	protected String[] getMathExecutableCommand() {
		ArrayList<String> cmds = new ArrayList<>(commandPrefix);
		cmds.add(EXECUTABLE_NAME);
		cmds.add("--simtask");
		cmds.add(getSimTaskFile().getAbsolutePath());
		cmds.add("--out");
		cmds.add(getSaveDirectory().getAbsolutePath());
		if (bMessaging) {
			cmds.add("--vc-send-status-config=" + getMessagingConfigFilename());
		} else {
			cmds.add("--vc-print-status");
		}
		return cmds.toArray(new String[0]);
	}

	@Override
	public String translateSimulationMessage(String simulationMessage) {
		return simulationMessage;
	}
}

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
import org.vcell.util.OperatingSystemInfo;
import org.vcell.util.exe.ExecutableStatus;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
 * the container prefix; for a local run {@link #configureDocker} installs a {@code docker run} prefix.
 */
public class FenicsSolver extends SimpleCompiledSolver {

	private static final Logger lg = LogManager.getLogger(FenicsSolver.class);

	public static final String EXECUTABLE_NAME = "vcell-fenics";

	private List<String> commandPrefix = List.of();
	/** where the save directory appears to the solver process (differs inside a Windows container) */
	private String solverSideDirectory = null;
	private Map<String, String> environment = Map.of();
	private FenicsDocker docker = null;
	private String containerName = null;

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

	/**
	 * Runs the solver in the local Docker image: {@code docker run ... <image> vcell-fenics ...}, with
	 * the save directory bind-mounted (same path on Unix, {@code /simdata} on Windows).
	 *
	 * @param platform the {@code os/arch} to run (that of the pulled image), or null for the default
	 */
	public void configureDocker(FenicsDocker docker, String image, String platform) {
		OperatingSystemInfo osi = OperatingSystemInfo.getInstance();
		File saveDir = getSaveDirectory().getAbsoluteFile();
		String containerDir = FenicsDocker.containerDirectory(saveDir, osi);
		this.docker = docker;
		this.containerName = "vcell-fenics-" + simTask.getSimulationJobID().replaceAll("[^A-Za-z0-9_.-]", "") + "-" + UUID.randomUUID().toString().substring(0, 8);
		this.solverSideDirectory = containerDir;
		this.environment = docker.environment();
		setCommandPrefix(docker.runPrefix(image, platform, containerName, saveDir, containerDir, FenicsDocker.ownerOf(saveDir)));
	}

	String getContainerName() {
		return containerName;
	}

	/**
	 * After the run: if it did not complete (stopped by the user, or failed) make sure its container is
	 * gone. Stopping kills the docker CLI, which forwards the signal on Unix but not on Windows.
	 */
	@Override
	public void cleanup() {
		if (docker != null && containerName != null) {
			MathExecutable me = getMathExecutable();
			if (me == null || me.getStatus() != ExecutableStatus.COMPLETE) {
				docker.removeContainer(containerName);
			}
		}
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
		MathExecutable me = new MathExecutable(getMathExecutableCommand(), getSaveDirectory());
		environment.forEach(me::addEnvironmentVariable);
		setMathExecutable(me);
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
	 * {@code [prefix...] vcell-fenics --simtask <file> --out <saveDir> (--vc-send-status-config=<cfg> | --vc-print-status)},
	 * with the paths as the solver process sees them. On the cluster HtcSimulationWorker appends
	 * {@code -tid <taskID>}, which the CLI accepts.
	 */
	@Override
	protected String[] getMathExecutableCommand() {
		ArrayList<String> cmds = new ArrayList<>(commandPrefix);
		cmds.add(EXECUTABLE_NAME);
		cmds.add("--simtask");
		cmds.add(solverSidePath(getSimTaskFile().getName()));
		cmds.add("--out");
		cmds.add(solverSideDirectory != null ? solverSideDirectory : getSaveDirectory().getAbsolutePath());
		if (bMessaging) {
			cmds.add("--vc-send-status-config=" + solverSidePath(new File(getMessagingConfigFilename()).getName()));
		} else {
			cmds.add("--vc-print-status");
		}
		return cmds.toArray(new String[0]);
	}

	/** a file in the save directory, as the solver process sees it */
	private String solverSidePath(String fileName) {
		if (solverSideDirectory == null) {
			return new File(getSaveDirectory(), fileName).getAbsolutePath();
		}
		return solverSideDirectory.endsWith("/") ? solverSideDirectory + fileName : solverSideDirectory + "/" + fileName;
	}

	/**
	 * What in {@code simulation} the FEniCSx solver cannot solve yet, as user-facing sentences; empty if
	 * it can. Mirrors the solver's own refusals (vcell-fenics {@code backend/realize.py}): a 2D or 3D
	 * geometry whose subvolumes are all analytic. Checked before a run (see
	 * {@link cbit.vcell.solver.Simulation#gatherIssues}) so the user is told without starting a container.
	 */
	public static java.util.List<String> unsupportedReasons(cbit.vcell.solver.Simulation simulation) {
		java.util.List<String> reasons = new ArrayList<>();
		cbit.vcell.math.MathDescription math = simulation.getMathDescription();
		cbit.vcell.geometry.Geometry geometry = math == null ? null : math.getGeometry();
		if (geometry == null) {
			return reasons;
		}
		int dim = geometry.getDimension();
		if (dim != 2 && dim != 3) {
			reasons.add("The FEniCSx solver supports 2D and 3D geometries; this one is " + dim + "D.");
			return reasons;
		}
		if (math.isMovingMembrane()) {
			reasons.addAll(movingBoundaryReasons(math, dim));
		}
		// Analytic and image subvolumes are realized (image geometries body-fitted from their smoothed label
		// field: vcell-fenics ADR 012); CSG is not yet.
		for (cbit.vcell.geometry.SubVolume subVolume : geometry.getGeometrySpec().getSubVolumes()) {
			if (subVolume instanceof cbit.vcell.geometry.ImageSubVolume) {
				if (math.isMovingMembrane()) {
					reasons.add("The FEniCSx solver does not yet run moving-boundary simulations on image-based geometries; "
							+ "subvolume '" + subVolume.getName() + "' of geometry '" + geometry.getName() + "' is image-based.");
				}
			} else if (!(subVolume instanceof cbit.vcell.geometry.AnalyticSubVolume)) {
				String kind = subVolume instanceof cbit.vcell.geometry.CSGObject ? "CSG" : "non-analytic";
				reasons.add("The FEniCSx solver supports analytic and image-based geometries; subvolume '"
						+ subVolume.getName() + "' of geometry '" + geometry.getName() + "' is " + kind + ".");
			}
		}
		return reasons;
	}

	/**
	 * What the FEniCSx moving-boundary path solves (vcell-fenics' simtask checks, mirrored here so the user
	 * is told before a run): a 2D geometry, one moving membrane, and species only in the compartment it
	 * encloses.
	 */
	private static java.util.List<String> movingBoundaryReasons(cbit.vcell.math.MathDescription math, int dim) {
		java.util.List<String> reasons = new ArrayList<>();
		if (dim != 2) {
			reasons.add("The FEniCSx solver runs moving-boundary simulations in 2D; this geometry is " + dim + "D.");
		}
		java.util.List<cbit.vcell.math.MembraneSubDomain> moving = new ArrayList<>();
		for (cbit.vcell.math.SubDomain subDomain : java.util.Collections.list(math.getSubDomains())) {
			if (subDomain instanceof cbit.vcell.math.MembraneSubDomain membrane && membrane.isMoving()) {
				moving.add(membrane);
			}
		}
		if (moving.size() != 1) {
			reasons.add("The FEniCSx solver moves one membrane; this model moves " + moving.size() + ".");
			return reasons;
		}
		cbit.vcell.math.MembraneSubDomain front = moving.get(0);
		if (!front.getEquationCollection().isEmpty()) {
			reasons.add("The FEniCSx solver does not yet solve species on a moving membrane ('" + front.getName() + "').");
		}
		for (cbit.vcell.math.SubDomain subDomain : java.util.Collections.list(math.getSubDomains())) {
			if (subDomain instanceof cbit.vcell.math.CompartmentSubDomain compartment
					&& compartment != front.getInsideCompartment() && !compartment.getEquationCollection().isEmpty()) {
				reasons.add("The FEniCSx solver solves moving-boundary species inside the moving front ('"
						+ front.getInsideCompartment().getName() + "') only; '" + compartment.getName() + "' has species.");
			}
		}
		return reasons;
	}

	/**
	 * The solver's stderr carries its logging and, on failure, one {@code error: <Type>: <message>} line
	 * (vcell-fenics ADR 011 §4). Show just that line; fall back to the whole text if there is none.
	 */
	@Override
	public String translateSimulationMessage(String simulationMessage) {
		return errorLineOf(simulationMessage);
	}

	static String errorLineOf(String message) {
		if (message == null) {
			return null;
		}
		for (String line : message.split("\\R")) {
			String trimmed = line.trim();
			if (trimmed.startsWith("error:")) {
				return "FEniCSx solver " + trimmed;
			}
		}
		return message;
	}
}

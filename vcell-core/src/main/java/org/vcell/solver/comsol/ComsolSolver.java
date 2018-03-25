package org.vcell.solver.comsol;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.vcell.solver.comsol.model.VCCModel;
import org.vcell.solver.comsol.service.ComsolService;
import org.vcell.solver.comsol.service.ComsolServiceFactory;
import org.vcell.vis.vismesh.thrift.VisMesh;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData.SolverDataType;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverTaskDescription;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.AbstractSolver;
import cbit.vcell.solvers.CartesianMesh;

public class ComsolSolver extends AbstractSolver {
	private ComsolService comsolService = null;
	private VCCModel vccModel = null;
	private double progress = 0.0;
	private double time = 0.0;

	public ComsolSolver(SimulationTask simTask, File directory) throws SolverException {
		super(simTask, directory);
	}

	@Override
	public void startSolver() {
		try {
			setSolverStatus(new SolverStatus(SolverStatus.SOLVER_STARTING, SimulationMessage.MESSAGE_SOLVER_STARTING_INIT));
			initialize();
			setSolverStatus(new SolverStatus(SolverStatus.SOLVER_RUNNING, SimulationMessage.MESSAGE_SOLVER_RUNNING_START));
			fireSolverStarting(SimulationMessage.MESSAGE_SOLVEREVENT_STARTING);
			String filePrefix = simTask.getSimulationJob().getSimulationJobID();
			try {
				comsolService.connectComsol();
				File reportFile = new File(getSaveDirectory(), filePrefix+".comsoldat");
				File javaFile = new File(getSaveDirectory(), filePrefix+".java");
				File mphFile = new File(getSaveDirectory(), filePrefix+".mph");
				comsolService.solve(this.vccModel, reportFile, javaFile, mphFile);
			}finally{
				comsolService.disconnectComsol();
			}
			File logFile = new File(getSaveDirectory(),filePrefix+".log");
			FileUtils.writeStringToFile(logFile, createLogFileContents(simTask.getSimulation().getSolverTaskDescription()));
			VisMesh visMesh = new VisMesh();
			File reportFile = new File(getSaveDirectory(),filePrefix+".comsoldat");
			ComsolMeshReader.read(visMesh, reportFile);

			time = simTask.getSimulation().getSolverTaskDescription().getTimeBounds().getEndingTime();
			progress = 1.0;
			fireSolverPrinted(time);
			fireSolverProgress(progress);
			setSolverStatus(new SolverStatus(SolverStatus.SOLVER_FINISHED, SimulationMessage.MESSAGE_SOLVER_FINISHED));
			fireSolverFinished();
		}catch (Exception e){
			e.printStackTrace(System.out);
			setSolverStatus(new SolverStatus (SolverStatus.SOLVER_ABORTED, SimulationMessage.solverAborted(e.getMessage())));
			fireSolverAborted(SimulationMessage.solverAborted(e.getMessage()));
		}
	}
	
	public static String createLogFileContents(SolverTaskDescription solverTaskDescription){
		 StringBuffer buffer = new StringBuffer();
		 buffer.append(SolverDataType.COMSOL.name()+"\n");
		 double startingTime = solverTaskDescription.getTimeBounds().getStartingTime();
		 double endingTime = solverTaskDescription.getTimeBounds().getEndingTime();
		 TimeStep timeStep = solverTaskDescription.getTimeStep();
		 int iteration = 0;
		 double time = startingTime;
		 while (time < (endingTime+timeStep.getDefaultTimeStep()/2.0)){
			 time = startingTime + iteration*timeStep.getDefaultTimeStep();
			 iteration++;
			 buffer.append(time+"\n");
		 }
		 return buffer.toString();
	}
	
	public static double[] getTimesFromLogFile(File logFile) throws IOException{
		String logFileContents = FileUtils.readFileToString(logFile);
		ArrayList<Double> times = new ArrayList<Double>();
		StringTokenizer tokens = new StringTokenizer(logFileContents);
		String magicString = tokens.nextToken();
		if (!magicString.equals(SolverDataType.COMSOL.name())){
			throw new RuntimeException("expecting comsol log file to start with "+SolverDataType.COMSOL.name());
		}
		while (tokens.hasMoreTokens()){
			times.add(Double.parseDouble(tokens.nextToken()));
		}
		double[] timesArray = new double[times.size()];
		for (int i=0;i<timesArray.length;i++){
			timesArray[i] = times.get(i);
		}
		return timesArray;
	}

	@Override
	public void stopSolver() {
		comsolService.disconnectComsol();
	}
	
	@Override
	public double getCurrentTime() {
		return time;
	}

	@Override
	protected void initialize() throws SolverException {
		try {
			this.vccModel = ComsolModelBuilder.getVCCModel(getSimulationJob());
			
			// write mesh file
			File meshFile = new File(getSaveDirectory(), simTask.getSimulationJob().getSimulationJobID()+SimDataConstants.MESHFILE_EXTENSION);
			try (FileOutputStream fos = new FileOutputStream(meshFile)) {
				simTask.getSimulation().getMathDescription().getGeometry().getGeometrySurfaceDescription().updateAll();
				CartesianMesh mesh = CartesianMesh.createSimpleCartesianMesh(simTask.getSimulation().getMathDescription().getGeometry());
				mesh.write(new PrintStream(new BufferedOutputStream(fos)));
			} catch (Exception e) {
				e.printStackTrace(System.out);
				throw new SolverException(e.getMessage());
			}
			
			ComsolServiceFactory factory = ComsolServiceFactory.instance;
			if (factory==null){
				throw new RuntimeException("no Comsol Service available");
			}
			this.comsolService = factory.newComsolService();

		} catch (ExpressionException e) {
			e.printStackTrace();
			throw new SolverException("failed to generate VCell Comsol Model in ComsolSolver: "+e.getMessage(),e);
		}
	}

	@Override
	public double getProgress() {
		return progress;
	}

}

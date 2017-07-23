package org.vcell.vcellij;


import java.io.File;
import java.io.IOException;

import org.apache.thrift.TException;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.document.User;
import org.vcell.vcellij.api.Dataset;
import org.vcell.vcellij.api.SBMLModel;
import org.vcell.vcellij.api.SimulationInfo;
import org.vcell.vcellij.api.SimulationService;
import org.vcell.vcellij.api.SimulationSpec;
import org.vcell.vcellij.api.SimulationState;
import org.vcell.vcellij.api.SimulationStatus;
import org.vcell.vcellij.api.ThriftDataAccessException;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.ClientSimManager.TempSimulation;
import cbit.vcell.mapping.MathMappingCallbackTaskAdapter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.resource.StdoutSessionLog;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solver.server.SolverListener;
import cbit.vcell.solver.server.SolverStatus;


/**
 * Created by kevingaffney on 7/12/17.
 */
public class SimulationServiceImpl implements SimulationService.Iface {
	SimulationState simState = null;

    @Override
    public Dataset getDataset(SimulationInfo simInfo) throws ThriftDataAccessException, TException {
        return null;
//        Dataset dataset = new Dataset();
//        dataset.setFilepath("filepath");
//        return dataset;
    }

    @Override
    public SimulationInfo computeModel(SBMLModel model, SimulationSpec simSpec) throws ThriftDataAccessException, TException {
    	try {
	        cbit.util.xml.VCLogger logger = new cbit.util.xml.VCLogger() {
	            @Override
				public void sendMessage(Priority p, ErrorType et, String message) {
	                System.err.println("LOGGER: msgLevel="+p+", msgType="+et+", "+message);
	                if (p == VCLogger.Priority.HighPriority) {
	                	throw new RuntimeException("Import failed : " + message);
	                }
	            }
	            public void sendAllMessages() {
	            }
	            public boolean hasMessages() {
	                return false;
	            }
	        };
	    	SBMLImporter importer = new SBMLImporter(model.getFilepath(),logger,true);
	    	BioModel bioModel = importer.getBioModel();
	    	SimulationContext simContext = bioModel.getSimulationContext(0);
	    	ClientTaskStatusSupport statusCallback = new ClientTaskStatusSupport() {
				
				@Override
				public void setProgress(int progress) {
					System.out.println("math mapping: "+progress);
				}
				
				@Override
				public void setMessage(String message) {
					System.out.println("math mapping: "+message);
				}
				
				@Override
				public boolean isInterrupted() { return false; }
				
				@Override
				public int getProgress() { return 0; }
				
				@Override
				public void addProgressDialogListener(ProgressDialogListener progressDialogListener) {}
				
			};
			MathMappingCallback callback = new MathMappingCallbackTaskAdapter(statusCallback);
			Simulation newsim = simContext.addNewSimulation(SimulationOwner.DEFAULT_SIM_NAME_PREFIX,callback,NetworkGenerationRequirements.AllowTruncatedStandardTimeout);
	    	SimulationInfo simulationInfo = new SimulationInfo();
	        simulationInfo.setId(1);
	                	
        	// ----------- run simulation(s)
        	final File localSimDataDir = ResourceUtil.getLocalSimDir(User.tempUser.getName());	
			Simulation simulation = new TempSimulation(newsim, false);
			StdoutSessionLog log = new StdoutSessionLog("Quick run");
			SimulationTask simTask = new SimulationTask(new SimulationJob(simulation, 0, null),0);
			Solver solver = createQuickRunSolver(log, localSimDataDir, simTask);
			if (solver == null) {
				throw new RuntimeException("null solver");
			}
			solver.addSolverListener(new SolverListener() {
				public void solverStopped(SolverEvent event) {
					simState = SimulationState.failed;
					System.out.println(event.getSimulationMessage().getDisplayMessage());
				}
				public void solverStarting(SolverEvent event) {
					simState = SimulationState.running;
					String displayMessage = event.getSimulationMessage().getDisplayMessage();
					System.out.println(displayMessage);
				}
				public void solverProgress(SolverEvent event) {
					simState = SimulationState.running;
					System.out.println("Running..."+((int)(event.getProgress() * 100)));
				}
				public void solverPrinted(SolverEvent event) {
					simState = SimulationState.running;
					System.out.println("Running...");
				}
				public void solverFinished(SolverEvent event) {
					simState = SimulationState.done;
					System.out.println(event.getSimulationMessage().getDisplayMessage());
				}
				public void solverAborted(SolverEvent event) {
					simState = SimulationState.failed;
					System.err.println(event.getSimulationMessage().getDisplayMessage());
				}
			});
			solver.startSolver();

			while (true){
				try { 
					Thread.sleep(500); 
				} catch (InterruptedException e) {
				}

//				if (getClientTaskStatusSupport().isInterrupted()) {
//					solver.stopSolver();
//					throw UserCancelException.CANCEL_GENERIC;
//				}
				SolverStatus solverStatus = solver.getSolverStatus();
				if (solverStatus != null) {
					if (solverStatus.getStatus() == SolverStatus.SOLVER_ABORTED) {
						throw new RuntimeException(solverStatus.getSimulationMessage().getDisplayMessage());
					}
					if (solverStatus.getStatus() != SolverStatus.SOLVER_STARTING &&
						solverStatus.getStatus() != SolverStatus.SOLVER_READY &&
						solverStatus.getStatus() != SolverStatus.SOLVER_RUNNING){
						break;
					}
				}		
			}
			
	        return simulationInfo;
    	} catch (Exception e){
    		e.printStackTrace(System.out);
    		// remember the exceptiopn ... fail the status ... save the error message
    		return new SimulationInfo().setId(1);
    	}
    }
    public static Solver createQuickRunSolver(StdoutSessionLog sessionLog, File directory, SimulationTask simTask) throws SolverException, IOException {
    	SolverDescription solverDescription = simTask.getSimulation().getSolverTaskDescription().getSolverDescription();
    	if (solverDescription == null) {
    		throw new IllegalArgumentException("SolverDescription cannot be null");
    	}
    	
    	// ----- 'FiniteVolume, Regular Grid' solver (semi-implicit) solver is not supported for quick run; throw exception.
    	if (solverDescription.equals(SolverDescription.FiniteVolume)) {
    		throw new IllegalArgumentException("Semi-Implicit Finite Volume Compiled, Regular Grid (Fixed Time Step) solver not allowed for quick run of simulations.");
    	}
    	
    	SolverUtilities.prepareSolverExecutable(solverDescription);	
    	// create solver from SolverFactory
    	Solver solver = SolverFactory.createSolver(sessionLog, directory, simTask, false);

    	return solver;
    }

	@Override
	public SimulationStatus getStatus(SimulationInfo simInfo) throws ThriftDataAccessException, TException {
		return new SimulationStatus(SimulationState.running);
	}
}

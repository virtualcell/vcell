package org.vcell.vcellij;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.stream.XMLStreamException;

import org.apache.thrift.TException;
import org.sbml.jsbml.SBMLException;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.SBMLExporter.VCellSBMLDoc;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.ProgressDialogListener;
import org.vcell.util.SessionLog;
import org.vcell.util.document.User;
import org.vcell.vcellij.api.DomainType;
import org.vcell.vcellij.api.SBMLModel;
import org.vcell.vcellij.api.SimulationInfo;
import org.vcell.vcellij.api.SimulationService;
import org.vcell.vcellij.api.SimulationSpec;
import org.vcell.vcellij.api.SimulationState;
import org.vcell.vcellij.api.SimulationStatus;
import org.vcell.vcellij.api.ThriftDataAccessException;
import org.vcell.vcellij.api.VariableInfo;

import cbit.util.xml.VCLogger;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MathMappingCallbackTaskAdapter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.math.VariableType;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.resource.StdoutSessionLog;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.TempSimulation;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solver.server.SolverListener;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;


/**
 * Created by kevingaffney on 7/12/17.
 */
public class SimulationServiceImpl implements SimulationService.Iface {
	private static class SimulationServiceContext {
		SimulationInfo simInfo = null;
		Solver solver = null;
		SimulationState simState = null;
		SimulationTask simTask = null;
		VCSimulationDataIdentifier vcDataIdentifier = null;
		File localSimDataDir = null;
		File netcdfFile = null;
		DataIdentifier[] dataIdentifiers = null;
		double[] times = null;
	}
	
	HashMap<Integer,SimulationServiceContext> sims = new HashMap<Integer,SimulationServiceContext>();

//	private void writeNetcdfFile(SimulationServiceContext simServiceContext, SimulationData simData, OutputContext outputContext, File netcdfFile) {
//		NetcdfFileWriter dataFile = null;
//		try {
//			dataFile = NetcdfFileWriter.createNew(Version.netcdf4, netcdfFile.getAbsolutePath());
//
//			Simulation sim = simServiceContext.simTask.getSimulation();
//			ISize size = simData.getMesh().getISize();
//			int dimension = sim.getMathDescription().getGeometry().getDimension();
//			switch (dimension){
//			case 1:{
//				Dimension xDim = dataFile.addDimension(null, "x", size.getX());
//				Dimension tDim = dataFile.addDimension(null, "t", simServiceContext.times.length);
//				List<Dimension> xtDims = new ArrayList<Dimension>();
//				xtDims.add(xDim);
//				xtDims.add(tDim);
//				List<Dimension> tDims = new ArrayList<Dimension>();
//				tDims.add(tDim);
//				
//				ArrayList<Variable> volumeVars = new ArrayList<Variable>();
//				ArrayList<Variable> membraneVars = new ArrayList<Variable>();
//				for (DataIdentifier dataId : simServiceContext.dataIdentifiers){
//					VariableType varType = dataId.getVariableType();
//					switch (varType.getVariableDomain()){
//					case VARIABLEDOMAIN_MEMBRANE:{
//						Variable dataVar = dataFile.addVariable(null, dataId.getName(), DataType.DOUBLE, xtDims);
//						membraneVars.add(dataVar);
//						break;
//					}
//					case VARIABLEDOMAIN_VOLUME:{
//						Variable dataVar = dataFile.addVariable(null, dataId.getName(), DataType.DOUBLE, tDims);
//						volumeVars.add(dataVar);
//						break;
//					}
//					default:{
//						System.out.println("ignoring data variable "+dataId.getName()+" of type "+varType.getVariableDomain().getName());
//					}
//					}
//				}
//				dataFile.create();
//				double[] times = simServiceContext.times;
//				int nx = xDim.getLength();
//				Index volIndex = Index.factory(new int[] { nx, times.length });
//				for (Variable volVar : volumeVars){
//					ArrayDouble dataOut = new ArrayDouble.D2(xDim.getLength(), tDim.getLength());
//					for (int tindex=0; tindex<times.length; tindex++){
//						SimDataBlock simDataBlock = simData.getSimDataBlock(outputContext, volVar.getName(), times[tindex]);
//						for (int xindex=0;xindex<nx;xindex++){
//							volIndex.set(xindex, tindex);
//							dataOut.set(volIndex, simDataBlock.getData()[xindex]);
//						}
//					}
//					dataFile.write(volVar, dataOut);
//				}
//				Index memIndex = Index.factory(new int[] { times.length });
//				for (Variable memVar : membraneVars){
//					ArrayDouble dataOut = new ArrayDouble.D1(tDim.getLength());
//					for (int tindex=0; tindex<times.length; tindex++){
//						SimDataBlock simDataBlock = simData.getSimDataBlock(outputContext, memVar.getName(), times[tindex]);
//						double[] data = simDataBlock.getData();
//						if (data.length>1){
//							throw new RuntimeException("only 1 membrane element expected");
//						}
//						memIndex.set(tindex);
//						dataOut.set(memIndex, simDataBlock.getData()[0]);
//					}
//					dataFile.write(memVar, dataOut);
//				}
//				break;
//			}
//			default:{
//				throw new RuntimeException(dimension+" dimensional simulations not yet supported");
//			}
//			}
//		} catch (IOException | InvalidRangeException | DataAccessException | MathException e) {
//			e.printStackTrace();
//			throw new RuntimeException("failed to write netcdf file "+netcdfFile.getAbsolutePath());
//		} finally {
//			if (dataFile != null){
//				try {
//					dataFile.close();
//				} catch (Throwable e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}

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
	        simulationInfo.setId(Math.abs(new Random().nextInt(1000000)));
	        
        	// ----------- run simulation(s)
        	final File localSimDataDir = ResourceUtil.getLocalSimDir(User.tempUser.getName());	
			Simulation simulation = new TempSimulation(newsim, false);
			StdoutSessionLog log = new StdoutSessionLog("Quick run");
			
			
	        final SimulationServiceContext simServiceContext = new SimulationServiceContext();
	        simServiceContext.simInfo = simulationInfo;
	        simServiceContext.simState = SimulationState.running;
	        simServiceContext.simTask = new SimulationTask(new SimulationJob(simulation, 0, null),0);
    		simServiceContext.vcDataIdentifier = simServiceContext.simTask.getSimulationJob().getVCDataIdentifier();
	        simServiceContext.solver = createQuickRunSolver(log, localSimDataDir, simServiceContext.simTask );
	        simServiceContext.localSimDataDir = localSimDataDir;
	        if (simServiceContext.solver == null) {
	        	throw new RuntimeException("null solver");
	        }
	        sims.put(simulationInfo.id,simServiceContext);
	                	
			
			simServiceContext.solver.addSolverListener(new SolverListener() {
				public void solverStopped(SolverEvent event) {
					simServiceContext.simState = SimulationState.failed;
					System.out.println(event.getSimulationMessage().getDisplayMessage());
				}
				public void solverStarting(SolverEvent event) {
					simServiceContext.simState = SimulationState.running;
					String displayMessage = event.getSimulationMessage().getDisplayMessage();
					System.out.println(displayMessage);
				}
				public void solverProgress(SolverEvent event) {
					simServiceContext.simState = SimulationState.running;
					System.out.println("Running..."+((int)(event.getProgress() * 100)));
				}
				public void solverPrinted(SolverEvent event) {
					simServiceContext.simState = SimulationState.running;
					System.out.println("Running...");
				}
				public void solverFinished(SolverEvent event) {
					System.out.println(event.getSimulationMessage().getDisplayMessage());
					try {
						getDataSetController(simServiceContext).getDataSetTimes(simServiceContext.vcDataIdentifier);
						simServiceContext.simState = SimulationState.done;
					} catch (DataAccessException e) {
						simServiceContext.simState = SimulationState.failed;
						e.printStackTrace();
					}
				}
				public void solverAborted(SolverEvent event) {
					simServiceContext.simState = SimulationState.failed;
					System.err.println(event.getSimulationMessage().getDisplayMessage());
				}
			});
			simServiceContext.solver.startSolver();

//			while (true){
//				try { 
//					Thread.sleep(500); 
//				} catch (InterruptedException e) {
//				}
//
//				SolverStatus solverStatus = simServiceContext.solver.getSolverStatus();
//				if (solverStatus != null) {
//					if (solverStatus.getStatus() == SolverStatus.SOLVER_ABORTED) {
//						throw new RuntimeException(solverStatus.getSimulationMessage().getDisplayMessage());
//					}
//					if (solverStatus.getStatus() != SolverStatus.SOLVER_STARTING &&
//						solverStatus.getStatus() != SolverStatus.SOLVER_READY &&
//						solverStatus.getStatus() != SolverStatus.SOLVER_RUNNING){
//						break;
//					}
//				}		
//			}
			
	        return simServiceContext.simInfo;
    	} catch (Exception e){
    		e.printStackTrace(System.out);
    		// remember the exceptiopn ... fail the status ... save the error message
    		return new SimulationInfo().setId(1);
    	}
    }
    private static Solver createQuickRunSolver(StdoutSessionLog sessionLog, File directory, SimulationTask simTask) throws SolverException, IOException {
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
    
    private static DataSetControllerImpl getDataSetController(SimulationServiceContext simServiceContext){
		try {
			OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
			
			SessionLog log = new StdoutSessionLog("data");
			Cachetable cacheTable = new Cachetable(10000);
			DataSetControllerImpl datasetController = new DataSetControllerImpl(log,cacheTable,simServiceContext.localSimDataDir.getParentFile(), null);
			simServiceContext.times = datasetController.getDataSetTimes(simServiceContext.vcDataIdentifier);
			simServiceContext.dataIdentifiers = datasetController.getDataIdentifiers(outputContext, simServiceContext.vcDataIdentifier);
			return datasetController;
		} catch (IOException | DataAccessException e1) {
			e1.printStackTrace();
			throw new RuntimeException("failed to read dataset: "+e1.getMessage());
		}

    }

	@Override
	public SimulationStatus getStatus(SimulationInfo simInfo) throws ThriftDataAccessException, TException {
		SimulationServiceContext simServiceContext = sims.get(simInfo.id);
		return new SimulationStatus(simServiceContext.simState);
	}

	@Override
	public List<Double> getData(SimulationInfo simInfo, VariableInfo varInfo, int timeIndex)
			throws ThriftDataAccessException, TException {
        SimulationServiceContext simServiceContext = sims.get(simInfo.id);
        if (simServiceContext==null){
        	throw new RuntimeException("simulation results not found");
        }
        DataSetControllerImpl datasetController = getDataSetController(simServiceContext);
		try {
			double[] times = datasetController.getDataSetTimes(simServiceContext.vcDataIdentifier);
			OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
			SimDataBlock simDataBlock = datasetController.getSimDataBlock(outputContext, simServiceContext.vcDataIdentifier, varInfo.getVariableVtuName(), times[timeIndex]);
			double[] dataArray = simDataBlock.getData();
	        ArrayList<Double> dataList = new ArrayList<Double>();
			for (double d : dataArray){
				dataList.add(d);
			}
			return dataList;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ThriftDataAccessException("failed to retrieve data for variable "+varInfo.getVariableVtuName()+": "+e.getMessage());
		}
	}

	@Override
	public List<Double> getTimePoints(SimulationInfo simInfo) throws ThriftDataAccessException, TException {
        SimulationServiceContext simServiceContext = sims.get(simInfo.id);
        if (simServiceContext==null){
        	throw new ThriftDataAccessException("simulation results not found");
        }
        try {
	        DataSetControllerImpl datasetController = getDataSetController(simServiceContext);
	        ArrayList<Double> times = new ArrayList<Double>();
	        double[] timeArray;
			timeArray = datasetController.getDataSetTimes(simServiceContext.vcDataIdentifier);
			for (double t : timeArray){
				times.add(t);
			}
			return times;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ThriftDataAccessException("failed to retrieve times for simulation: "+e.getMessage());
		}
	}

	@Override
	public List<VariableInfo> getVariableList(SimulationInfo simInfo) throws ThriftDataAccessException, TException {
        SimulationServiceContext simServiceContext = sims.get(simInfo.id);
        if (simServiceContext==null){
        	throw new ThriftDataAccessException("simulation results not found");
        }
        try {
	        DataSetControllerImpl datasetController = getDataSetController(simServiceContext);
			OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
	        final DataIdentifier[] dataIdentifiers;
			try {
				dataIdentifiers = datasetController.getDataIdentifiers(outputContext, simServiceContext.vcDataIdentifier);
			} catch (IOException | DataAccessException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to retrieve variable information: "+e.getMessage(),e);
			}
	        ArrayList<VariableInfo> varInfos = new ArrayList<VariableInfo>();
	        for (DataIdentifier dataIdentifier : dataIdentifiers){
	        	final DomainType domainType;
	        	if (dataIdentifier.getVariableType().equals(VariableType.VOLUME)){
	        		domainType = DomainType.VOLUME;
	        	}else if (dataIdentifier.getVariableType().equals(VariableType.MEMBRANE)){
	        		domainType = DomainType.MEMBRANE;
	        	}else{
	        		continue;
	        	}
	        	String domainName = "";
	        	if (dataIdentifier.getDomain()!=null){
	        		domainName = dataIdentifier.getDomain().getName();
	        	}
				VariableInfo varInfo = new VariableInfo(dataIdentifier.getName(),dataIdentifier.getDisplayName(),domainName,domainType);
				varInfos.add(varInfo);
	        }
	        return varInfos;
        }catch (Exception e){
        	e.printStackTrace();
        	throw new ThriftDataAccessException("failed to retrieve variable list: "+e.getMessage());
        }
	}
	@Override
	public String getSBML(String vcml, String applicationName) throws ThriftDataAccessException, TException {
		try {
			BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcml));
			SimulationContext simContext = bioModel.getSimulationContext(applicationName);
			SBMLExporter exporter = new SBMLExporter(simContext,3,1,simContext.getGeometry().getDimension()>0);
			VCellSBMLDoc sbmlDoc = exporter.convertToSBML();
			return sbmlDoc.xmlString;
		} catch (SBMLException | XmlParseException | SbmlException | XMLStreamException e) {
			e.printStackTrace();
			throw new ThriftDataAccessException("failed to generate SBML document: "+e.getMessage());
		}
	}
}

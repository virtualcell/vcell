package org.vcell.sbmlsim.service;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.sbml.jsbml.SBMLException;
import org.scijava.service.AbstractService;
import org.vcell.sbml.SbmlException;
import org.vcell.sbml.vcell.SBMLExporter;
import org.vcell.sbml.vcell.SBMLExporter.VCellSBMLDoc;
import org.vcell.sbml.vcell.SBMLImporter;
import org.vcell.sbmlsim.api.common.DomainType;
import org.vcell.sbmlsim.api.common.SBMLModel;
import org.vcell.sbmlsim.api.common.SimData;
import org.vcell.sbmlsim.api.common.SimulationInfo;
import org.vcell.sbmlsim.api.common.SimulationSpec;
import org.vcell.sbmlsim.api.common.SimulationState;
import org.vcell.sbmlsim.api.common.SimulationStatus;
import org.vcell.sbmlsim.api.common.TimePoints;
import org.vcell.sbmlsim.api.common.VariableInfo;
import org.vcell.sbmlsim.api.common.VcmlToSbmlResults;
import org.vcell.util.ClientTaskStatusSupport;
import org.vcell.util.DataAccessException;
import org.vcell.util.ISize;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDocument;

import cbit.image.VCImage;
import cbit.util.xml.VCLogger;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.mapping.MathMappingCallbackTaskAdapter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.math.VariableType;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.resource.ResourceUtil;
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
import cbit.vcell.solver.TimeBounds;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solver.server.SolverListener;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;

/**
 * Created by kevingaffney on 7/12/17.
 */
public class SimulationServiceImpl extends AbstractService implements SimulationService {
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
	
	HashMap<Long,SimulationServiceContext> sims = new HashMap<Long,SimulationServiceContext>();

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
    public int sizeX(SimulationInfo simInfo) {
        return mesh(simInfo).getSizeX();
    }

	@Override
    public int sizeY(SimulationInfo simInfo) {
        return mesh(simInfo).getSizeY();
    }

	@Override
    public int sizeZ(SimulationInfo simInfo) {
        return mesh(simInfo).getSizeZ();
    }

    private CartesianMesh mesh(SimulationInfo simInfo) {
        SimulationServiceContext simServiceContext = sims.get(simInfo.getLocalId());
        try {
            DataSetControllerImpl datasetController = getDataSetController(simServiceContext);
            CartesianMesh mesh = datasetController.getMesh(simServiceContext.vcDataIdentifier);
            return mesh;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SimulationInfo computeModel(SBMLModel model, SimulationSpec simSpec) {
        try {
            SBMLImporter importer = new SBMLImporter(model.getFilepath().getAbsolutePath(),vcLogger(),true);
            BioModel bioModel = importer.getBioModel();
            return computeModel(bioModel, simSpec, null);
        }
        catch (Exception exc) {
            exc.printStackTrace(System.out);
            return null;
        }
    }

//    public SimulationInfo computeModel(Model sbmlModel, SimulationSpec simSpec) {
//        try {
//            SBMLImporter importer = new SBMLImporter(sbmlModel,vcLogger(),true);
//            BioModel bioModel = importer.getBioModel();
////            XmlUtil.writeXMLStringToFile(XmlHelper.bioModelToXML(bioModel), "C:\\Users\\frm\\vcGititImageJWorkspace\\vcell\\imagej_true.xml", true);
//			return computeModel(bioModel, simSpec, null);
//        }
//        catch (Exception exc) {
//            exc.printStackTrace(System.out);
//            return null;
//        }
//    }

    public static cbit.util.xml.VCLogger vcLogger() {
        return new cbit.util.xml.VCLogger() {
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
    }

    private SimulationInfo computeModel(VCDocument vcDoc, SimulationSpec simSpec, ClientTaskStatusSupport statusCallback) throws Exception{
    	
//    	try {
    	Simulation newsim = null;
    	if(vcDoc instanceof BioModel){
	    	SimulationContext simContext = ((BioModel)vcDoc).getSimulationContext(0);
			MathMappingCallback callback = new MathMappingCallbackTaskAdapter(statusCallback);
			newsim = simContext.addNewSimulation(SimulationOwner.DEFAULT_SIM_NAME_PREFIX,callback,NetworkGenerationRequirements.AllowTruncatedStandardTimeout);
    	}else if(vcDoc instanceof MathModel){
    		newsim = ((MathModel)vcDoc).addNewSimulation(SimulationOwner.DEFAULT_SIM_NAME_PREFIX);
    	}else{
    		throw new IllegalArgumentException("computeModel expecting BioModel or MathModel but got "+vcDoc.getClass().getName());
    	}
		GeometrySpec geometrySpec = newsim.getMathDescription().getGeometry().getGeometrySpec();
		VCImage image = geometrySpec.getImage();
		if (image!=null) {
			newsim.getMeshSpecification().setSamplingSize(new ISize(image.getNumX(), image.getNumY(), image.getNumZ()));
		}
//			newsim.getMeshSpecification().setSamplingSize(new ISize(simContext.getGeometry().getGeometrySpec().getImage().getNumX(), simContext.getGeometry().getGeometrySpec().getImage().getNumY(), simContext.getGeometry().getGeometrySpec().getImage().getNumZ()));
			newsim.getSolverTaskDescription().setTimeBounds(new TimeBounds(0, simSpec.getTotalTime()));
			newsim.getSolverTaskDescription().setOutputTimeSpec(new UniformOutputTimeSpec(simSpec.getOutputTimeStep()));
	    	SimulationInfo simulationInfo = new SimulationInfo(Math.abs(new Random().nextInt(1000000)));
	        
        	// ----------- run simulation(s)
        	final File localSimDataDir = ResourceUtil.getLocalSimDir(User.tempUser.getName());	
			Simulation simulation = new TempSimulation(newsim, false);
			
			
	        final SimulationServiceContext simServiceContext = new SimulationServiceContext();
	        simServiceContext.simInfo = simulationInfo;
	        simServiceContext.simState = SimulationState.running;
	        simServiceContext.simTask = new SimulationTask(new SimulationJob(simulation, 0, null),0);
    		simServiceContext.vcDataIdentifier = simServiceContext.simTask.getSimulationJob().getVCDataIdentifier();
	        simServiceContext.solver = createQuickRunSolver(localSimDataDir, simServiceContext.simTask );
	        simServiceContext.localSimDataDir = localSimDataDir;
	        if (simServiceContext.solver == null) {
	        	throw new RuntimeException("null solver");
	        }
	        sims.put(simulationInfo.getLocalId(),simServiceContext);
	                	
			
			simServiceContext.solver.addSolverListener(new SolverListener() {
				public void solverStopped(SolverEvent event) {
					simServiceContext.simState = SimulationState.failed;
					System.err.println("Simulation stopped");
				}
				public void solverStarting(SolverEvent event) {
					simServiceContext.simState = SimulationState.running;
					updateStatus(event);
				}
				public void solverProgress(SolverEvent event) {
					simServiceContext.simState = SimulationState.running;
					updateStatus(event);
				}
				public void solverPrinted(SolverEvent event) {
					simServiceContext.simState = SimulationState.running;
				}
				public void solverFinished(SolverEvent event) {
					try {
						getDataSetController(simServiceContext).getDataSetTimes(simServiceContext.vcDataIdentifier);
						simServiceContext.simState = SimulationState.done;
					} catch (DataAccessException e) {
						simServiceContext.simState = SimulationState.failed;
						e.printStackTrace();
					}
					updateStatus(event);
				}
				public void solverAborted(SolverEvent event) {
					simServiceContext.simState = SimulationState.failed;
					System.err.println(event.getSimulationMessage().getDisplayMessage());
				}
				private void updateStatus(SolverEvent event) {
					if (statusCallback == null) return;
					statusCallback.setMessage(event.getSimulationMessage().getDisplayMessage());
					statusCallback.setProgress((int) (event.getProgress() * 100));
				}
			});

			if(true){//debug
				if(vcDoc instanceof BioModel){
					XmlUtil.writeXMLStringToFile(XmlHelper.bioModelToXML(((BioModel)vcDoc)), "C:\\Users\\frm\\vcGititImageJWorkspace\\vcell\\imagej_BM_true.xml", true);
				}else if(vcDoc instanceof MathModel){
					XmlUtil.writeXMLStringToFile(XmlHelper.mathModelToXML(((MathModel)vcDoc)), "C:\\Users\\frm\\vcGititImageJWorkspace\\vcell\\imagej_MM_true.xml", true);
				}
			}
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
//    	} catch (Exception e){
//    		e.printStackTrace(System.out);
//    		// remember the exceptiopn ... fail the status ... save the error message
//    		return new SimulationInfo();
//    	}
    }
    private static Solver createQuickRunSolver(File directory, SimulationTask simTask) throws SolverException, IOException {
    	SolverDescription solverDescription = simTask.getSimulation().getSolverTaskDescription().getSolverDescription();
    	if (solverDescription == null) {
    		throw new IllegalArgumentException("SolverDescription cannot be null");
    	}
    	
    	// ----- 'FiniteVolume, Regular Grid' solver (semi-implicit) solver is not supported for quick run; throw exception.
    	if (solverDescription.equals(SolverDescription.FiniteVolumeStandalone)) {
    		throw new IllegalArgumentException("Semi-Implicit Finite Volume Compiled, Regular Grid (Fixed Time Step) solver not allowed for quick run of simulations.");
    	}
    	
    	SolverUtilities.prepareSolverExecutable(solverDescription);	
    	// create solver from SolverFactory
    	Solver solver = SolverFactory.createSolver(directory, simTask, false);

    	return solver;
    }
    
    private static DataSetControllerImpl getDataSetController(SimulationServiceContext simServiceContext){
		try {
			OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
			
			Cachetable cacheTable = new Cachetable(10000,1000000L);
			DataSetControllerImpl datasetController = new DataSetControllerImpl(cacheTable,simServiceContext.localSimDataDir.getParentFile(), null);
			simServiceContext.times = datasetController.getDataSetTimes(simServiceContext.vcDataIdentifier);
			simServiceContext.dataIdentifiers = datasetController.getDataIdentifiers(outputContext, simServiceContext.vcDataIdentifier);
			return datasetController;
		} catch (IOException | DataAccessException e1) {
			e1.printStackTrace();
			throw new RuntimeException("failed to read dataset: "+e1.getMessage());
		}

    }

    @Override
	public SimulationStatus getStatus(SimulationInfo simInfo) {
		SimulationServiceContext simServiceContext = sims.get(simInfo.getLocalId());
		return new SimulationStatus(simServiceContext.simState);
	}

	@Override
	public SimData getData(SimulationInfo simInfo, VariableInfo varInfo, int timeIndex)  throws Exception {
        SimulationServiceContext simServiceContext = sims.get(simInfo.getLocalId());
        if (simServiceContext==null){
        	throw new RuntimeException("simulation results not found");
        }
        DataSetControllerImpl datasetController = getDataSetController(simServiceContext);
		try {
			double[] times = datasetController.getDataSetTimes(simServiceContext.vcDataIdentifier);
			OutputContext outputContext = new OutputContext(new AnnotatedFunction[0]);
			SimDataBlock simDataBlock = datasetController.getSimDataBlock(outputContext, simServiceContext.vcDataIdentifier, varInfo.getVariableVtuName(), times[timeIndex]);
			double[] dataArray = simDataBlock.getData();
			SimData simData = new SimData();
			simData.setData(dataArray);
	        return simData;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("failed to retrieve data for variable "+varInfo.getVariableVtuName()+": "+e.getMessage());
		}
	}

	@Override
	public TimePoints getTimePoints(SimulationInfo simInfo) throws Exception {
        SimulationServiceContext simServiceContext = sims.get(simInfo.getLocalId());
        if (simServiceContext==null){
        	throw new Exception("simulation results not found");
        }
        try {
	        DataSetControllerImpl datasetController = getDataSetController(simServiceContext);
	        double[] timeArray = datasetController.getDataSetTimes(simServiceContext.vcDataIdentifier);
	        TimePoints timePoints = new TimePoints();
	        timePoints.setTimePoints(timeArray);
	        return timePoints;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("failed to retrieve times for simulation: "+e.getMessage());
		}
	}

	@Override
	public List<VariableInfo> getVariableList(SimulationInfo simInfo) throws Exception {
        SimulationServiceContext simServiceContext = sims.get(simInfo.getLocalId());
        if (simServiceContext==null){
        	throw new Exception("simulation results not found");
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
        	throw new Exception("failed to retrieve variable list: "+e.getMessage());
        }
	}
	
	@Override
	public VcmlToSbmlResults getSBML(File vcmlFile, String applicationName, File outputFile) throws Exception {
		try {
			BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlFile));
			SimulationContext simContext = bioModel.getSimulationContext(applicationName);
			SBMLExporter exporter = new SBMLExporter(simContext,3,1,simContext.getGeometry().getDimension()>0);
			VCellSBMLDoc sbmlDoc = exporter.convertToSBML();
			FileUtils.write(outputFile, sbmlDoc.xmlString);
			VcmlToSbmlResults results = new VcmlToSbmlResults();
			results.setSbmlFilePath(outputFile);
			return results;
		} catch (SBMLException | XmlParseException | SbmlException | XMLStreamException e) {
			e.printStackTrace();
			throw new Exception("failed to generate SBML document: "+e.getMessage());
		}
	}
}

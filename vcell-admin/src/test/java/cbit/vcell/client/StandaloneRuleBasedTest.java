package cbit.vcell.client;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.vcell.stochtest.StochtestFileUtils;
import org.vcell.stochtest.TimeSeriesMultitrialData;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.MathMappingCallbackTaskAdapter;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.ODEDataBlock;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.TempSimulation;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;


public class StandaloneRuleBasedTest {

	public static void main(String[] args) {
		try{
			PropertyLoader.loadProperties();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		final int numTrials = 40;
		final long bngTimeoutDurationMS = 30000; // 30 seconds
		
		VCDatabaseVisitor vcDatabaseVisitor = new VCDatabaseVisitor() {
			
			@Override
			public void visitMathModel(MathModel mathModel,PrintStream logFilePrintStream) {
				throw new IllegalArgumentException("Not Implemented");
			}
			
			@Override
			public void visitGeometry(Geometry geometry, PrintStream logFilePrintStream) {
				throw new IllegalArgumentException("Not Implemented");
			}
			
			@Override
			public void visitBioModel(BioModel bioModel, PrintStream logFilePrintStream) {
				SimulationContext[] simulationContexts = bioModel.getSimulationContexts();
				for(SimulationContext simContext : simulationContexts){
					if ((simContext.getApplicationType() == Application.NETWORK_STOCHASTIC) && simContext.getGeometry().getDimension() == 0){
						File baseDirectory = createDirFile(simContext);
						try{
							checkNonspatialStochasticSimContext(simContext,baseDirectory,numTrials,bngTimeoutDurationMS);
						}catch(Exception e){
							e.printStackTrace();
							if(!e.getMessage().contains("Only Mass Action Kinetics supported ")){
								StochtestFileUtils.writeMessageTofile(baseDirectory, e.getMessage());
							}
						}
					}
				}
			}
			
			@Override
			public boolean filterMathModel(MathModelInfo mathModelInfo) {
				return false;
			}
			
			@Override
			public boolean filterGeometry(GeometryInfo geometryInfo) {
				return false;
			}
			
			@Override
			public boolean filterBioModel(BioModelInfo bioModelInfo) {
				return
//					true;
//					bioModelInfo.getVersion().getName().equals("model");
					bioModelInfo.getVersion().getName().equals("simpleModel_Network_orig");
			}
		};
		
		String currentUserID = "schaff";
		String[] allUsers = new String[] {/*-all*/currentUserID,"-"};
		VCDatabaseScanner.scanBioModels(allUsers, vcDatabaseVisitor, false);
	}
	
	private static final String STOCH_SIM_NAME = "aUniqueNewStoch";
	private static final String ODE_SIM_NAME = "aUniqueNewODE";
	private static final String NFS_SIM_NAME = "aUniqueNewNFS";
	
	private static void checkNonspatialStochasticSimContext(SimulationContext srcSimContext, File baseDirectory, int numTrials, long bngTimeoutDuration) throws Exception{
		if(!srcSimContext.getApplicationType().equals(Application.NETWORK_STOCHASTIC) || srcSimContext.getGeometry().getDimension() != 0){
			throw new RuntimeException("simContext is of type "+srcSimContext.getApplicationType()+" and geometry dimension of "+srcSimContext.getGeometry().getDimension()+", expecting nonspatial stochastic");
		}
		
		BioModel origBioModel = srcSimContext.getBioModel();
		BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(XmlHelper.bioModelToXML(origBioModel)));
		bioModel.refreshDependencies();
		
		//create ODE and RuleBased
		SimulationContext newODEApp = SimulationContext.copySimulationContext(srcSimContext, "aUniqueNewODEApp", false, Application.NETWORK_DETERMINISTIC);
		SimulationContext newRuleBasedApp = SimulationContext.copySimulationContext(srcSimContext, "aUniqueNewRuleBasedApp", false, Application.RULE_BASED_STOCHASTIC);

		newODEApp.setBioModel(bioModel);
		newRuleBasedApp.setBioModel(bioModel);
		
		ArrayList<AnnotatedFunction> outputFunctionsList = srcSimContext.getOutputFunctionContext().getOutputFunctionsList();
//				OutputContext outputContext = new OutputContext(outputFunctionsList.toArray(new AnnotatedFunction[outputFunctionsList.size()]));
		newODEApp.getOutputFunctionContext().setOutputFunctions(outputFunctionsList);
		newRuleBasedApp.getOutputFunctionContext().setOutputFunctions(outputFunctionsList);
		
		NetworkGenerationRequirements networkGenRequirements = NetworkGenerationRequirements.getComputeFull(bngTimeoutDuration);
		
		bioModel.addSimulationContext(newODEApp);
		newODEApp.refreshMathDescription(new MathMappingCallbackTaskAdapter(null),networkGenRequirements);
		
		bioModel.addSimulationContext(newRuleBasedApp);
		newRuleBasedApp.refreshMathDescription(new MathMappingCallbackTaskAdapter(null),networkGenRequirements);
		
		srcSimContext.refreshMathDescription(new MathMappingCallbackTaskAdapter(null),networkGenRequirements);
	
		//Create non-spatialStoch, ODE and RuleBased sims
		Simulation nonspatialStochAppNewSim = 
				srcSimContext.addNewSimulation(STOCH_SIM_NAME/*SimulationOwner.DEFAULT_SIM_NAME_PREFIX*/,new MathMappingCallbackTaskAdapter(null),networkGenRequirements);

		Simulation newODEAppNewSim = 
			newODEApp.addNewSimulation(ODE_SIM_NAME,new MathMappingCallbackTaskAdapter(null),networkGenRequirements);

		Simulation newRuleBasedAppNewSim = 
			newRuleBasedApp.addNewSimulation(NFS_SIM_NAME,new MathMappingCallbackTaskAdapter(null),networkGenRequirements);

		nonspatialStochAppNewSim.setSimulationOwner(srcSimContext);
		newODEAppNewSim.setSimulationOwner(newODEApp);
		newRuleBasedAppNewSim.setSimulationOwner(newRuleBasedApp);
		
		try{

			bioModel.getModel().getSpeciesContexts();
			ArrayList<String> varNameList = new ArrayList<String>();
			for (SpeciesContextSpec scs : srcSimContext.getReactionContext().getSpeciesContextSpecs()){
				varNameList.add(scs.getSpeciesContext().getName());
			}
			String[] varNames = varNameList.toArray(new String[0]);
			OutputTimeSpec outputTimeSpec = nonspatialStochAppNewSim.getSolverTaskDescription().getOutputTimeSpec();

			ArrayList<Double> sampleTimeList = new ArrayList<Double>();
			if (outputTimeSpec instanceof UniformOutputTimeSpec){
				double endingTime = nonspatialStochAppNewSim.getSolverTaskDescription().getTimeBounds().getEndingTime();
				double dT = ((UniformOutputTimeSpec)outputTimeSpec).getOutputTimeStep();
				int currTimeIndex=0;
				while (currTimeIndex*dT <= (endingTime+1e-8)){
					sampleTimeList.add(currTimeIndex*dT);
					currTimeIndex++;
				}
			}
			double[] sampleTimes = new double[sampleTimeList.size()];
			for (int i=0;i<sampleTimes.length;i++){
				sampleTimes[i] = sampleTimeList.get(i);
			}
			
			TimeSeriesMultitrialData sampleDataStoch1 = new TimeSeriesMultitrialData("stochastic1",varNames, sampleTimes, numTrials);
			TimeSeriesMultitrialData sampleDataStoch2 = new TimeSeriesMultitrialData("stochastic2",varNames, sampleTimes, numTrials);
			TimeSeriesMultitrialData sampleDataDeterministic = new TimeSeriesMultitrialData("determinstic",varNames, sampleTimes, 1);
			runsolver(nonspatialStochAppNewSim,baseDirectory,numTrials,sampleDataStoch1);
			runsolver(newODEAppNewSim,baseDirectory,1,sampleDataDeterministic);
			runsolver(newRuleBasedAppNewSim,baseDirectory,numTrials,sampleDataStoch2);
			
			StochtestFileUtils.writeVarDiffData(new File(baseDirectory,VARDIFF_FILE),sampleDataStoch1,sampleDataStoch2);
			StochtestFileUtils.writeKolmogorovSmirnovTest(new File(baseDirectory,KS_TEST_FILE), sampleDataStoch1, sampleDataStoch2);
			StochtestFileUtils.writeChiSquareTest(new File(baseDirectory,ChiSquared_TEST_FILE), sampleDataStoch1, sampleDataStoch2);
			StochtestFileUtils.writeData(sampleDataStoch1, new File(baseDirectory,"data."+sampleDataStoch1.datasetName+".json"));
			StochtestFileUtils.writeData(sampleDataStoch2, new File(baseDirectory,"data."+sampleDataStoch2.datasetName+".json"));
			StochtestFileUtils.writeData(sampleDataDeterministic, new File(baseDirectory,"data."+sampleDataDeterministic.datasetName+".json"));
		}finally{
			srcSimContext.removeSimulation(nonspatialStochAppNewSim);
			newODEApp.removeSimulation(newODEAppNewSim);
			newRuleBasedApp.removeSimulation(newRuleBasedAppNewSim);
		}
	}
	
	private static final String VARDIFF_FILE = "vardiff.csv";
	private static final String KS_TEST_FILE = "ks_test.csv";
	private static final String ChiSquared_TEST_FILE = "chiSquared_test.csv";

	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
	
	private static File createDirFile(SimulationContext simulationContext){
		String userid = simulationContext.getBioModel().getVersion().getOwner().getName();
		String simContextDirName =
			TokenMangler.fixTokenStrict(userid)+"-"+
			TokenMangler.fixTokenStrict(simulationContext.getBioModel().getName())+"-"+
			TokenMangler.fixTokenStrict(simulationContext.getName())+"-"+
			TokenMangler.fixTokenStrict(simpleDateFormat.format(simulationContext.getBioModel().getVersion().getDate()));
//		simContextDirName = TokenMangler.fixTokenStrict(simContextDirName);
		File dirFile = new File("C:\\temp\\ruleBasedTestDir\\"+simContextDirName);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		return dirFile;
	}
	
//	private static void printout(String printThis){
//		System.out.flush();
//		disableSystemOut(false);
//		System.out.print(printThis);
//		System.out.flush();
//		disableSystemOut(true);
//	}
	
	private static void runsolver(Simulation newSimulation, File baseDirectory, int numRuns, TimeSeriesMultitrialData timeSeriesMultitrialData){
		Simulation versSimulation = null;
		File destDir = null;
//		int progress = 1;
		for(int trialIndex=0;trialIndex<numRuns;trialIndex++){
//			if(i >= (progress*numRuns/10)){
//				printout(progress+" ");
//				progress++;
//			}
			try{
				versSimulation = new TempSimulation(newSimulation, false);
//				printout(ruleBasedTestDir.getAbsolutePath());
				destDir = new File(baseDirectory,timeSeriesMultitrialData.datasetName);
				SimulationTask simTask = new SimulationTask(new SimulationJob(versSimulation, 0, null),0);
				Solver solver = ClientSimManager.createQuickRunSolver(destDir, simTask);
				solver.startSolver();
		
				while (true){
					try { 
						Thread.sleep(250); 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		
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
				SimulationData simData = new SimulationData(simTask.getSimulationJob().getVCDataIdentifier(), destDir, null, null);
				ODEDataBlock odeDataBlock = simData.getODEDataBlock();
				ODESimData odeSimData = odeDataBlock.getODESimData();
				timeSeriesMultitrialData.addDataSet(odeSimData,trialIndex);
			}catch(Exception e){
				e.printStackTrace();
				File file = new File(baseDirectory,Simulation.createSimulationID(versSimulation.getKey())+"_solverExc.txt");
				StochtestFileUtils.writeMessageTofile(file,e.getMessage());
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			StochtestFileUtils.clearDir(destDir);
		}
//		printout("\n");
	}
	
}

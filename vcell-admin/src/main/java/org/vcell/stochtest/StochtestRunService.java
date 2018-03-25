package org.vcell.stochtest;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.image.ImageException;
import cbit.sql.QueryHashtable;
import cbit.util.xml.XmlUtil;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.ClientSimManager;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.mapping.MathMapping;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.MathMappingCallback;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.math.MathDescription;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.modeldb.DatabasePolicySQL;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.ServerDocumentManager;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.ODEDataBlock;
import cbit.vcell.simdata.SimulationData;
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
import cbit.vcell.xml.XmlParseException;


public class StochtestRunService {
	
	private ConnectionFactory conFactory = null;
	private DatabaseServerImpl dbServerImpl = null;
	private KeyFactory keyFactory = null;
	private File baseDir = null;
	private int numTrials;
	private long bngTimeoutMS;


	public StochtestRunService(File baseDir, int numTrials, long bngTimeoutMS, ConnectionFactory argConFactory, KeyFactory argKeyFactory) 
			throws DataAccessException, SQLException {
		this.conFactory = argConFactory;
		this.keyFactory = argKeyFactory;
		this.dbServerImpl = new DatabaseServerImpl(conFactory,keyFactory);
		this.baseDir = baseDir;
		this.numTrials = numTrials;
		this.bngTimeoutMS = bngTimeoutMS;
	}

	public static void main(String[] args) {
		
	try {
		
		if (args.length!=3){
			System.out.println("Usage:  StochtestService baseDirectory numtrials bngTimeoutMS");
			System.exit(-1);
		}
		File baseDir = new File(args[0]);
		if (!baseDir.exists()){
			throw new RuntimeException("base directory "+baseDir.getPath()+" not found");
		}
		
		int numTrials = Integer.valueOf(args[1]);
		
		long bngTimeoutMS = Long.valueOf(args[2]);
		
		PropertyLoader.loadProperties();

		DatabasePolicySQL.bAllowAdministrativeAccess = true;
	    String driverName = PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName);
	    String connectURL = PropertyLoader.getRequiredProperty(PropertyLoader.dbConnectURL);
	    String dbSchemaUser = PropertyLoader.getRequiredProperty(PropertyLoader.dbUserid);
	    String dbPassword = PropertyLoader.getSecretValue(PropertyLoader.dbPasswordValue, PropertyLoader.dbPasswordFile);
	    //
	    // get appropriate database factory objects
	    //
	    ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory(
	    		driverName,connectURL,dbSchemaUser,dbPassword);
	    KeyFactory keyFactory = conFactory.getKeyFactory();
	    StochtestRunService stochtestService = new StochtestRunService(baseDir, numTrials, bngTimeoutMS, conFactory, keyFactory);
	    
	    while (true){
	    	stochtestService.runOne();
	    }
	    
	} catch (Throwable e) {
	    e.printStackTrace(System.out);
	}
    System.exit(0);
	}

	public void runOne() throws IllegalArgumentException, SQLException, DataAccessException, XmlParseException, PropertyVetoException, ExpressionException, MappingException, GeometryException, ImageException, IOException{
		
	    StochtestRun stochtestRun = StochtestDbUtils.acceptNextWaitingStochtestRun(conFactory);
	    String biomodelXML = null;
	    if (stochtestRun!=null){
	    	String networkGenProbs = null;
	    	try {
		    	User user = new User(PropertyLoader.ADMINISTRATOR_ACCOUNT, new KeyValue(PropertyLoader.ADMINISTRATOR_ID));
		    	ServerDocumentManager serverDocumentManager = new ServerDocumentManager(this.dbServerImpl);
		    	biomodelXML = serverDocumentManager.getBioModelXML(new QueryHashtable(), user, stochtestRun.stochtest.biomodelRef, true);
		    	BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(biomodelXML));
		    	bioModel.refreshDependencies();
		    	
		    	SimulationContext srcSimContext = null;
		    	for (SimulationContext sc : bioModel.getSimulationContexts()){
		    		if (sc.getKey().equals(stochtestRun.stochtest.simContextRef)){
		    			srcSimContext = sc;
		    		}
		    	}
		    	
		    	if (srcSimContext==null){
		    		throw new RuntimeException("cannot find simcontext with key="+stochtestRun.stochtest.simContextRef);
		    	}
		    	
		    	//
		    	// clear clamped attribute of speciesContexts (because they are not supported by Rule-based applications).
		    	//
		    	for (SpeciesContextSpec scs : srcSimContext.getReactionContext().getSpeciesContextSpecs()){
		    		scs.setConstant(false);
		    	}
		    	
		    	SimulationContext simContext = srcSimContext;
		    	StochtestMathType parentMathType = stochtestRun.parentMathType;
		    	StochtestMathType mathType = stochtestRun.mathType;
		    	if (parentMathType != mathType){
		    		if (parentMathType == StochtestMathType.nonspatialstochastic && mathType == StochtestMathType.rules){
		    			simContext = SimulationContext.copySimulationContext(srcSimContext, "generatedRules", false, Application.RULE_BASED_STOCHASTIC);
		    		}else if (parentMathType == StochtestMathType.rules && mathType == StochtestMathType.nonspatialstochastic){
		    			simContext = SimulationContext.copySimulationContext(srcSimContext, "generatedSSA", false, Application.NETWORK_STOCHASTIC);
		    	   	}else{
		    	   		throw new RuntimeException("unexpected copy of simcontext from "+parentMathType+" to "+mathType);
		    	   	}
		    		bioModel.addSimulationContext(simContext);
		    	}
		    	
		    	MathMappingCallback mathMappingCallback = new MathMappingCallback() {
					
					@Override
					public void setProgressFraction(float fractionDone) {
					}
					
					@Override
					public void setMessage(String message) {
					}
					
					@Override
					public boolean isInterrupted() {
						return false;
					}
				};
				
				MathMapping mathMapping = simContext.createNewMathMapping(mathMappingCallback, NetworkGenerationRequirements.ComputeFullStandardTimeout);
				MathDescription mathDesc = mathMapping.getMathDescription(mathMappingCallback);
				simContext.setMathDescription(mathDesc);
				
				if (simContext.isInsufficientIterations()){
					networkGenProbs = "insufficientIterations";
				} else if (simContext.isInsufficientMaxMolecules()){
					networkGenProbs = "insufficientMaxMolecules";
				}
		    	
		    	
				File baseDirectory = StochtestFileUtils.createDirFile(baseDir, stochtestRun);
				try {
					OutputTimeSpec outputTimeSpec = new UniformOutputTimeSpec(0.5);
					double endTime = 10.0;
					computeTrials(simContext, stochtestRun, baseDirectory, outputTimeSpec, endTime, numTrials);
					StochtestDbUtils.finalizeAcceptedStochtestRun(conFactory, stochtestRun, StochtestRun.StochtestRunStatus.complete,null,networkGenProbs);
				}finally{
					StochtestFileUtils.clearDir(baseDirectory);
				}
	    	}catch (Exception e){
				StochtestDbUtils.finalizeAcceptedStochtestRun(conFactory, stochtestRun, StochtestRun.StochtestRunStatus.failed,e.getMessage(),networkGenProbs);
				//
				// write original biomodelXML to a .vcml file
				//
				if (biomodelXML!=null){
					XmlUtil.writeXMLStringToFile(biomodelXML, new File(baseDir,"stochtestrun_"+stochtestRun.stochtest.key+".vcml").getPath(), false);
				}
				
				//
				// write exception trace to .txt file
				//
				StringWriter stringWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(stringWriter);
				e.printStackTrace(printWriter);
				printWriter.flush();
				System.out.println(stringWriter.getBuffer().toString());
				XmlUtil.writeXMLStringToFile(stringWriter.getBuffer().toString(), new File(baseDir,"stochtestrun_"+stochtestRun.stochtest.key+"_error.txt").getPath(), false);
	    	}
	    }else{
	    	System.out.println("no jobs waiting");
	    	try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	}
	
	private void computeTrials(SimulationContext simContext, StochtestRun stochtestRun, File baseDirectory, OutputTimeSpec outputTimeSpec, double endTime, int numTrials) throws PropertyVetoException, IOException {
		//
		// make simulation
		//
		MathMappingCallback callback = new MathMappingCallback() {
			@Override
			public void setProgressFraction(float fractionDone) { }
			@Override
			public void setMessage(String message) { }
			@Override
			public boolean isInterrupted() { return false; }
		};
		
		NetworkGenerationRequirements networkGenerationRequirements = NetworkGenerationRequirements.getComputeFull(bngTimeoutMS);
		simContext.refreshMathDescription(callback,networkGenerationRequirements);
		Simulation sim = simContext.addNewSimulation("stochtestrun_"+stochtestRun.key,callback,networkGenerationRequirements);
		sim.setSimulationOwner(simContext);
		
		//
		// get variables to save
		//
		simContext.getModel().getSpeciesContexts();
		ArrayList<String> varNameList = new ArrayList<String>();
		for (SpeciesContextSpec scs : simContext.getReactionContext().getSpeciesContextSpecs()){
			varNameList.add(scs.getSpeciesContext().getName());
		}
		String[] varNames = varNameList.toArray(new String[0]);

		//
		// get time points to save
		//
		ArrayList<Double> sampleTimeList = new ArrayList<Double>();
		if (outputTimeSpec instanceof UniformOutputTimeSpec){
			double dT = ((UniformOutputTimeSpec)outputTimeSpec).getOutputTimeStep();
			int currTimeIndex=0;
			while (currTimeIndex*dT <= (endTime+1e-8)){
				sampleTimeList.add(currTimeIndex*dT);
				currTimeIndex++;
			}
		}
		double[] sampleTimes = new double[sampleTimeList.size()];
		for (int i=0;i<sampleTimes.length;i++){
			sampleTimes[i] = sampleTimeList.get(i);
		}
		
		//
		// run N trials and save data
		//
		TimeSeriesMultitrialData sampleData = new TimeSeriesMultitrialData(sim.getName(),varNames, sampleTimes, numTrials);
		runsolver(sim,baseDirectory,numTrials,sampleData);
		StochtestFileUtils.writeData(sampleData, StochtestFileUtils.getStochtestRunDataFile(baseDir, stochtestRun));
	}
	
	
	private static void runsolver(Simulation newSimulation, File baseDirectory, int numRuns, TimeSeriesMultitrialData timeSeriesMultitrialData){
		Simulation versSimulation = null;
		File destDir = null;
		boolean bTimeout = false;
//		int progress = 1;
		for(int trialIndex=0;trialIndex<numRuns;trialIndex++){
			System.out.println("\n=====================================\n\nStarting trial "+(trialIndex+1)+" of "+numRuns+"\n\n==============================\n");
			long startTime = System.currentTimeMillis();
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
					
					if (System.currentTimeMillis() - startTime > 30*1000){
						// timeout after 30 seconds .. otherwise multiple runs will take forever
						bTimeout = true;
						solver.stopSolver();
						throw new RuntimeException("timed out");
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
				if (bTimeout){
					throw new RuntimeException("timed out");
				}else{
					throw new RuntimeException("solver failed : "+e.getMessage(),e);
				}
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

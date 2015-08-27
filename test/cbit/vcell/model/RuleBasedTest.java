package cbit.vcell.model;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.vcell.util.FileUtils;
import org.vcell.util.NumberUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.BioModelChildSummary.MathType;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.client.ClientSimManager;
import cbit.vcell.client.ClientTaskManager;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.gui.MathMappingCallbackTaskAdapter;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.ODEDataBlock;
import cbit.vcell.simdata.OutputContext;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.util.ColumnDescription;


public class RuleBasedTest {

	private static Hashtable<String, Hashtable<String,double[]>> dataAccum;
//	private static PrintStream sysout;
//	
//	private static class NullOutputStream extends OutputStream{
//		@Override
//		public void write(int b) throws IOException {
//			// do nothing
//		}
//	}
//	private static PrintStream nullPrintStream = new PrintStream(new NullOutputStream());
	
//	private static void disableSystemOut(boolean bDisable){
//		if(bDisable){
//			System.setOut(nullPrintStream);			
//		}else{
//			System.setOut(sysout);			
//		}
//	}
	public static void main(String[] args) {
		dataAccum = new Hashtable<String,Hashtable<String,double[]>>();
		
//		sysout = System.out;
//		disableSystemOut(true);
		
		try{
			PropertyLoader.loadProperties();
		}catch(Exception e){
			e.printStackTrace();
		}
		
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
				for(SimulationContext simContext:simulationContexts){
					try{
						checkSimcontext(simContext);
					}catch(Exception e){
						e.printStackTrace();
						if(!e.getMessage().contains("Only Mass Action Kinetics supported ")){
							writeMessageTofile(simContext, "uncaughtExc.txt", e.getMessage());
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
	private static void checkSimcontext(SimulationContext srcSimContext) throws Exception{
		if(srcSimContext.getApplicationType().equals(Application.NETWORK_STOCHASTIC) &&
			srcSimContext.getGeometry().getDimension() == 0/* &&
			srcSimContext.getBioModel().getName().startsWith("Hybrid_Test_suite")*/){
			
			SimulationContext newODEApp = null;
			SimulationContext newRuleBasedApp = null;
			BioModel bioModel = srcSimContext.getBioModel();
			try{
					
				//create ODE and RuleBased
				newODEApp =
					ClientTaskManager.copySimulationContext(srcSimContext, "aUniqueNewODEApp", false, Application.NETWORK_DETERMINISTIC);
				newRuleBasedApp =
						ClientTaskManager.copySimulationContext(srcSimContext, "aUniqueNewRuleBasedApp", false, Application.RULE_BASED_STOCHASTIC);
	
				newODEApp.setBioModel(bioModel);
				newRuleBasedApp.setBioModel(bioModel);
				
				ArrayList<AnnotatedFunction> outputFunctionsList = srcSimContext.getOutputFunctionContext().getOutputFunctionsList();
//				OutputContext outputContext = new OutputContext(outputFunctionsList.toArray(new AnnotatedFunction[outputFunctionsList.size()]));
				newODEApp.getOutputFunctionContext().setOutputFunctions(outputFunctionsList);
				newRuleBasedApp.getOutputFunctionContext().setOutputFunctions(outputFunctionsList);
				
				bioModel.addSimulationContext(newODEApp);
				newODEApp.refreshMathDescription(new MathMappingCallbackTaskAdapter(null),NetworkGenerationRequirements.AllowTruncatedNetwork);
				
				bioModel.addSimulationContext(newRuleBasedApp);
				newRuleBasedApp.refreshMathDescription(new MathMappingCallbackTaskAdapter(null),NetworkGenerationRequirements.AllowTruncatedNetwork);
				
				srcSimContext.refreshMathDescription(new MathMappingCallbackTaskAdapter(null),NetworkGenerationRequirements.AllowTruncatedNetwork);
			
				//Create non-spatialStoch, ODE and RuleBased sims
				Simulation NonSpatialStochAppNewSim = 
						srcSimContext.addNewSimulation(STOCH_SIM_NAME/*SimulationOwner.DEFAULT_SIM_NAME_PREFIX*/,new MathMappingCallbackTaskAdapter(null),NetworkGenerationRequirements.AllowTruncatedNetwork);
	
				Simulation newODEAppNewSim = 
					newODEApp.addNewSimulation(ODE_SIM_NAME,new MathMappingCallbackTaskAdapter(null),NetworkGenerationRequirements.AllowTruncatedNetwork);
	
				Simulation newRuleBasedAppNewSim = 
					newRuleBasedApp.addNewSimulation(NFS_SIM_NAME,new MathMappingCallbackTaskAdapter(null),NetworkGenerationRequirements.AllowTruncatedNetwork);
	
				NonSpatialStochAppNewSim.setSimulationOwner(srcSimContext);
				newODEAppNewSim.setSimulationOwner(newODEApp);
				newRuleBasedAppNewSim.setSimulationOwner(newRuleBasedApp);
				
				int numRuns = 10;
				try{
					File destDirStoch = runsolver(NonSpatialStochAppNewSim,srcSimContext,numRuns);
					runsolver(newODEAppNewSim,srcSimContext,numRuns);
					File destDirNFS = runsolver(newRuleBasedAppNewSim,srcSimContext,numRuns);
					
					writeData(destDirStoch,destDirNFS,numRuns);
				}finally{
					srcSimContext.removeSimulation(NonSpatialStochAppNewSim);
					newODEApp.removeSimulation(newODEAppNewSim);
					newRuleBasedApp.removeSimulation(newRuleBasedAppNewSim);
				}
			}finally{
				if(newODEApp != null && bioModel.getSimulationContext(newODEApp.getName()) != null){
					try{
						bioModel.removeSimulationContext(bioModel.getSimulationContext(newODEApp.getName()));
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				if(newRuleBasedApp != null && bioModel.getSimulationContext(newRuleBasedApp.getName()) != null){
					try{
						bioModel.removeSimulationContext(bioModel.getSimulationContext(newRuleBasedApp.getName()));
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}

		}
	}
	
	private static class MinMaxHelp {
		public double min,max,diff;
		public MinMaxHelp(double[] data) {
			min=data[0];
			max=data[0];
			for (int i = 0; i < data.length; i++) {
				if(data[i] < min){min = data[i];}
				if(data[i] > max){max = data[i];}
			}
			diff = ((max-min)==0?1:max-min);
		}
		
	}
	private static final String VARDIFF_FILE = "vardiff.csv";
	private static void writeData(File destDirStoch,File destDirNFS,int numRuns) throws Exception{
//		//average and write data
		Hashtable<String, double[]> stochData = dataAccum.get(destDirStoch.getName());
		Hashtable<String, double[]> nfsData = dataAccum.get(destDirNFS.getName());
		
		StringBuffer sb = new StringBuffer();
		Enumeration<String> stochDataEnumeration = stochData.keys();
		while(stochDataEnumeration.hasMoreElements()){
			String varName = stochDataEnumeration.nextElement();
			sb.append("\""+varName+"\"");
			double[] varDataStoch = stochData.get(varName);
			double[] varDataNFS = nfsData.get(varName);
			MinMaxHelp minmaxStoch = new MinMaxHelp(varDataStoch);
//			MinMaxHelp minmaxnfs = new MinMaxHelp(varDataNFS);
			for (int i = 0; i < varDataStoch.length; i++) {
				sb.append(","+((varDataStoch[i]/numRuns/minmaxStoch.diff)-(varDataNFS[i]/numRuns/minmaxStoch.diff)));
			}
			sb.append("\n");
		}
		
//		int[] lookupNFS = new int[stochData.size()];
//		Arrays.fill(lookupNFS, -1);
//		StringBuffer sb = new StringBuffer();
//		for (int i = 0; i < stochData.size(); i++) {
//			for (int j = 0; j <nfsData.size(); j++) {
//				if(stochColumnResultSet.getColumnDescriptions(i).getName().equals(nfsColumnResultSet.getColumnDescriptions(j).getName())){
//					lookupNFS[i] = j;
//					sb.append((sb.length()!=0?",":"")+stochColumnResultSet.getColumnDescriptions(i).getName());
//					break;
//				}
//			}
//		}
//		sb.append("\n");
		
//		for(int i=0;i<stochColumnResultSet.getRowCount();i++){
//			double[] stochTimeRow = stochColumnResultSet.getRow(i);
//			double[] nfsTimeRow = nfsColumnResultSet.getRow(i);
//			for (int j = 0; j < stochTimeRow.length; j++) {
//				if(lookupNFS[j] != -1){
//					sb.append((j!=0?",":"")+((stochTimeRow[j]/numRuns)-(nfsTimeRow[lookupNFS[j]]/numRuns)));
//				}else{
//					sb.append((j!=0?",":"")+"\"?\"");
//				}
//			}
//			sb.append("\n");
//		}

		FileUtils.writeByteArrayToFile(sb.toString().getBytes(), new File(destDirStoch.getParentFile(),VARDIFF_FILE));
//		while(dataDirNames.hasMoreElements()){
//			String dataDirName = dataDirNames.nextElement();
//			RowColumnResultSet accumData = dataAccum.get(currentDataDir.getName());
//		}

	}
	
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
			dirFile.mkdir();
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
	
	private static File runsolver(Simulation newSimulation,SimulationContext origSimcontext,int stochCount){
		int numRuns = 1;
		MathType simMathType = ((SimulationContext)newSimulation.getSimulationOwner()).getMathType();
		if(stochCount > 1 && (simMathType == MathType.Stochastic || simMathType == MathType.RuleBased)){
			numRuns = stochCount;
		}
		Simulation versSimulation = null;
		File destDir = null;
		File ruleBasedTestDir = null;
		int progress = 1;
		for(int i=0;i<numRuns;i++){
//			if(i >= (progress*numRuns/10)){
//				printout(progress+" ");
//				progress++;
//			}
			try{
				versSimulation = new ClientSimManager.TempSimulation(newSimulation, false);
				ruleBasedTestDir = createDirFile(origSimcontext);
//				printout(ruleBasedTestDir.getAbsolutePath());
				destDir = (simMathType == MathType.Deterministic?ruleBasedTestDir:new File(ruleBasedTestDir,(simMathType==MathType.Stochastic?"stoch":"nfs")));
				StdoutSessionLog sessionLog = new StdoutSessionLog(origSimcontext.getBioModel().getVersion().getOwner().getName());
				SimulationTask simTask = new SimulationTask(new SimulationJob(versSimulation, 0, null),0);
				Solver solver = ClientSimManager.createQuickRunSolver(sessionLog, destDir, simTask);
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
				
				if(simMathType != MathType.Deterministic){
					evaluateData(new SimulationData(simTask.getSimulationJob().getVCDataIdentifier(), destDir, null, null),
						newSimulation,
						destDir);
				}
			}catch(Exception e){
				e.printStackTrace();
				writeMessageTofile(origSimcontext,Simulation.createSimulationID(versSimulation.getKey())+"_solverExc.txt",e.getMessage());
			}
			clearDir(ruleBasedTestDir);
		}
//		printout("\n");
		return destDir;
	}
	
	private static void clearDir(File dirName){
		File[] files = dirName.listFiles();
		for (int i = 0; i < files.length; i++) {
			if(files[i].isDirectory()){
				clearDir(files[i]);
			}else{
				files[i].delete();
			}
		}
		if(dirName.equals("nfs") || dirName.equals("stoch")){
			dirName.delete();
		}
	}
	
	private static void evaluateData(SimulationData simData,Simulation newSimulation,File currentDataDir) throws Exception{
		 ArrayList<AnnotatedFunction> outputFunctionsList =
			((SimulationContext)newSimulation.getSimulationOwner()).getOutputFunctionContext().getOutputFunctionsList();
		 OutputContext outputContext = new OutputContext(outputFunctionsList.toArray(new AnnotatedFunction[outputFunctionsList.size()]));

		DataIdentifier[] dataIdentifiers = simData.getVarAndFunctionDataIdentifiers(outputContext);
		double[] times = simData.getDataTimes();
		System.out.println(dataIdentifiers.length);
		
//		Cachetable cachetable = new Cachetable(10*Cachetable.minute);
//		DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(new StdoutSessionLog(currentDataDir.getName()),null,currentDataDir,null);
//		ODEDataBlock odeDataBlock = dataSetControllerImpl.getODEDataBlock(simData.getVcDataId());

		ODEDataBlock odeDataBlock = simData.getODEDataBlock();
		ODESimData odeSimData = odeDataBlock.getODESimData();
//		SimulationWorkspaceModelInfo simulationWorkspaceModelInfo =
//				new SimulationWorkspaceModelInfo(newSimulation.getSimulationOwner(), newSimulation.getSimulationOwner().getName());
//		ODEDataInterfaceImpl odeDataInterfaceImpl = new ODEDataInterfaceImpl(odeSimData, simulationWorkspaceModelInfo);
		
		Hashtable<String,double[]> accumData = dataAccum.get(currentDataDir.getName());
		if(accumData == null){
			accumData = new Hashtable();
//			currentData = new double[odeSimData.getRowCount()/*times*/][odeSimData.getColumnDescriptionsCount()/*var*/];
			dataAccum.put(currentDataDir.getName(),accumData);
		}
		
		ColumnDescription[] columnDescriptions = odeSimData.getColumnDescriptions();
		for (int i = 0; i < columnDescriptions.length; i++) {
			String varName = columnDescriptions[i].getName();
			double[] varData = accumData.get(varName);
			if(varData == null){
				varData = new double[odeSimData.getRowCount()];
				accumData.put(varName, varData);
			}
			double[] newData = odeSimData.extractColumn(odeSimData.findColumn(varName));
			for (int j = 0; j < odeSimData.getRowCount(); j++) {
				varData[j]+= newData[j];
			}
		}
		
//		for(int i=0;i<odeSimData.getRowCount();i++){
//			ColumnDescription[] columnDescriptions = odeSimData.getColumnDescriptions();
//			for (int j = 0; j < columnDescriptions.length; j++) {
//				String varName = columnDescriptions[j].getName();
//				double[] newData = odeSimData.extractColumn(odeSimData.findColumn(varName));
//			}
//			
//			
////			double[] accumTimeRow = accumData.getRow(i);
////			double[] newTimeRow = odeSimData.getRow(i);
////			for (int j = 0; j < accumTimeRow.length; j++) {
////				accumTimeRow[j]+=newTimeRow[j];
////			}
//		}
		
		
//		//writeout averages to file
//		if(divide > 1){
//			for(int i=0;i<accumData.getRowCount();i++){
//				double[] accumTimeRow = accumData.getRow(i);
//				for (int j = 0; j < accumTimeRow.length; j++) {
//					accumTimeRow[j]+=newTimeRow[j];
//				}
//			}
//			
//		}
	}
	
	private static void writeMessageTofile(SimulationContext origSimcontext,String fileName,String message){
		try{
			FileUtils.writeByteArrayToFile(message.getBytes(), new File(createDirFile(origSimcontext),fileName));
		}catch(Exception e2){
			e2.printStackTrace();
		}	
	}
}

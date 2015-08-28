package cbit.vcell.client;

import java.io.File;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.MathModelInfo;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryInfo;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.SimulationContext.Application;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.SpeciesContextSpec;
import cbit.vcell.mapping.gui.MathMappingCallbackTaskAdapter;
import cbit.vcell.math.RowColumnResultSet;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.modeldb.VCDatabaseScanner;
import cbit.vcell.modeldb.VCDatabaseVisitor;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.ODEDataBlock;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverStatus;

import com.google.gson.Gson;


public class RuleBasedTest {

	public enum DataSetType {
		stochastic_1,
		stochastic_2,
		determinstic
	};
	
	public static class VarTimeTwoSampleData {
		public final String varName;
		public final double sampleTime;
		public final ArrayList<Double> stochasticData1 = new ArrayList<Double>();
		public final ArrayList<Double> stochasticData2 = new ArrayList<Double>();
		public Double determinsticSolution = null;
		
		public VarTimeTwoSampleData(String varName, double sampleTime){
			this.varName = varName;
			this.sampleTime = sampleTime;
		}

		public double getMean(DataSetType dataSetType) {
			switch (dataSetType){
			case stochastic_1:{
				double mean = 0;
				for (double data : stochasticData1){
					mean += data;
				}
				return mean/stochasticData1.size();
			}
			case stochastic_2:{
				double mean = 0;
				for (double data : stochasticData2){
					mean += data;
				}
				return mean/stochasticData2.size();
			}
			case determinstic:{
				if (determinsticSolution==null){
					throw new RuntimeException("determinstic solution not availlable for var "+varName+" at time "+sampleTime);
				}
				return determinsticSolution;
			}
			default:{
				throw new RuntimeException("unexpected data set type "+dataSetType);
			}
			}
		}

		public double kolmogorovSmirnovTest() {
			try {
				int numBins = 1 + (int)Math.ceil(Math.sqrt(stochasticData1.size()));
				double[] rawData1 = getData(stochasticData1);
double[] rawData2 = getData(stochasticData2);
//double[] rawData2 = getData(stochasticData1);
//rawData2 = ramp(0,10,rawData2.length);
				MinMaxHelp minMaxHelp1 = new MinMaxHelp(rawData1);
				MinMaxHelp minMaxHelp2 = new MinMaxHelp(rawData2);
				double min = Math.min(minMaxHelp1.min, minMaxHelp2.min);
				double max = Math.max(minMaxHelp1.max, minMaxHelp2.max);
				double[] cdf1 = calculateCDF(rawData1, min, max, numBins);
				double[] cdf2 = calculateCDF(rawData2, min, max, numBins);
				KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
				return test.kolmogorovSmirnovStatistic(cdf1, cdf2);
			}catch (Exception e){
				e.printStackTrace(System.out);
				return -1;
			}
		}
		
		public double chiSquaredTest() {
			try {
				int numBins = 1 + (int)Math.ceil(Math.sqrt(stochasticData1.size()));
				double[] rawData1 = getData(stochasticData1);
double[] rawData2 = getData(stochasticData2);
//double[] rawData2 = getData(stochasticData1);
//rawData2 = ramp(0,10,rawData2.length);
				MinMaxHelp minMaxHelp1 = new MinMaxHelp(rawData1);
				MinMaxHelp minMaxHelp2 = new MinMaxHelp(rawData2);
				double min = Math.min(minMaxHelp1.min, minMaxHelp2.min);
				double max = Math.max(minMaxHelp1.max, minMaxHelp2.max);
				long[] histogram1 = calcHistogram(rawData1, min, max, numBins);
				long[] histogram2 = calcHistogram(rawData2, min, max, numBins);
				
				//
				// remove histogram indices where both bins are zero
				//
				ArrayList<Long> histogram1List = new ArrayList<Long>();
				ArrayList<Long> histogram2List = new ArrayList<Long>();
				for (int i=0;i<histogram1.length;i++){
					if (histogram1[i] != 0 || histogram2[i] != 0){
						histogram1List.add(histogram1[i]);
						histogram2List.add(histogram2[i]);
//					}else{
//						histogram1List.add(new Long(1));
//						histogram2List.add(new Long(1));
					}
				}
				histogram1 = new long[histogram1List.size()];
				histogram2 = new long[histogram2List.size()];
				for (int i=0;i<histogram1List.size();i++){
					histogram1[i] = histogram1List.get(i);
					histogram2[i] = histogram2List.get(i);
				}
				
				if (histogram1.length==1){
					return 0.0;
				}
				ChiSquareTest chiSquareTest = new ChiSquareTest();
				
				return chiSquareTest.chiSquareTestDataSetsComparison(histogram1, histogram2);
			}catch (Exception e){
				e.printStackTrace(System.out);
				return -1;
			}
		}
		
		private double[] ramp(double min, double max, int length){
			double[] data = new double[length];
			for (int i=0;i<data.length;i++){
				data[i] = min + i*(max-min)/(length-1);
			}
			return data;
		}
		
		private double[] getData(ArrayList<Double> doubleList){
			double[] data = new double[doubleList.size()];
			for (int i=0;i<data.length;i++){
				data[i] = doubleList.get(i);
			}
			return data;
		}
		
		public static double[] calculateCDF(double[] data, double min, double max, int numBins){
			long[] histogram = calcHistogram(data, min, max, numBins);
			int totalCount = 0;
			for (long bin : histogram){
				totalCount += bin;
			}
			double[] cdf = new double[histogram.length];
			int cumulativeCount = 0;
			for (int i=0;i<numBins;i++){
				cumulativeCount += histogram[i];
				cdf[i] = cumulativeCount/totalCount;
			}
			return cdf;
		}
		
		public static long[] calcHistogram(double[] data, double min, double max, int numBins) {
			  final long[] result = new long[numBins];
			  final double binSize = (max - min)/numBins;

			  for (double d : data) {
			    int bin = (int) ((d - min) / binSize);
			    if (bin < 0) { /* this data is smaller than min */ }
			    else if (bin >= numBins) { /* this data point is bigger than max */ }
			    else {
			      result[bin] += 1;
			    }
			  }
			  return result;
			}
	}
	
	public static class TimeSeriesMultitrialData {
		public final String[] varNames;
		public final double[] times;
		public final VarTimeTwoSampleData[][] twoSampleData;
		
		public TimeSeriesMultitrialData(String[] varNames, double[] times){
			this.varNames = varNames;
			this.times = times;
			twoSampleData = new VarTimeTwoSampleData[varNames.length][times.length];
			for (int varNameIndex=0; varNameIndex<varNames.length; varNameIndex++){
				for (int timeIndex=0; timeIndex<times.length; timeIndex++){
					twoSampleData[varNameIndex][timeIndex] = new VarTimeTwoSampleData(varNames[varNameIndex], times[timeIndex]);
				}
			}
		}
		
		public void addDataSet(RowColumnResultSet simData, DataSetType dataSetType) throws ExpressionException{
			for (int varNameIndex=0; varNameIndex<varNames.length; varNameIndex++){
				String varName = varNames[varNameIndex];
				int columnIndex = simData.findColumn(varName);
				if (columnIndex<0){
					throw new RuntimeException("cannot find variable "+varName+" in dataset "+dataSetType);
				}
				double[] data = simData.extractColumn(columnIndex);
				if (data.length != this.times.length){
					throw new RuntimeException("data length of variable "+varName+" in dataset "+dataSetType+" is "+data.length+", expecting "+this.times.length);
				}
				switch (dataSetType){
				case determinstic:{
					for (int timeIndex=0; timeIndex<data.length; timeIndex++){
						this.twoSampleData[varNameIndex][timeIndex].determinsticSolution = data[timeIndex];
					}
					break;
				}
				case stochastic_1:{
					for (int timeIndex=0; timeIndex<data.length; timeIndex++){
						this.twoSampleData[varNameIndex][timeIndex].stochasticData1.add(data[timeIndex]);
					}
					break;
				}
				case stochastic_2:{
					for (int timeIndex=0; timeIndex<data.length; timeIndex++){					
						this.twoSampleData[varNameIndex][timeIndex].stochasticData2.add(data[timeIndex]);
					}
					break;
				}
				}
			}
		}

		public double[] getMeanTrajectory(String varName, DataSetType dataSetType) {
			int varNameIndex = findVarNameIndex(varName);
			double[] meanValues = new double[this.times.length];
			for (int timeIndex=0; timeIndex<this.times.length; timeIndex++){
				meanValues[timeIndex] = this.twoSampleData[varNameIndex][timeIndex].getMean(dataSetType);
			}
			return meanValues;
		}

		private int findVarNameIndex(String varName) {
			int varNameIndex = -1;
			for (int i=0;i<varNames.length;i++){
				if (varNames[i].equals(varName)){
					varNameIndex = i;
					break;
				}
			}
			if (varNameIndex<0){
				throw new RuntimeException("variable "+varName+" not found");
			}
			return varNameIndex;
		}

		public VarTimeTwoSampleData getVarTimeData(String varName, int timeIndex) {
			int varNameIndex = findVarNameIndex(varName);
			return this.twoSampleData[varNameIndex][timeIndex];
		}

		public double chiSquaredTest(String string, int timeIndex) {
			return getVarTimeData(string,timeIndex).chiSquaredTest();
		}
		
		public double kolmogorovSmirnovTest(String string, int timeIndex) {
			return getVarTimeData(string,timeIndex).kolmogorovSmirnovTest();
		}
		
	}
	
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
		
//		sysout = System.out;
//		disableSystemOut(true);
		
		try{
			PropertyLoader.loadProperties();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		final int numTrials = 40;
		
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
							checkNonspatialStochasticSimContext(simContext,baseDirectory,numTrials);
						}catch(Exception e){
							e.printStackTrace();
							if(!e.getMessage().contains("Only Mass Action Kinetics supported ")){
								writeMessageTofile(baseDirectory, e.getMessage());
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
	
	private static void checkNonspatialStochasticSimContext(SimulationContext srcSimContext, File baseDirectory, int numTrials) throws Exception{
		if(!srcSimContext.getApplicationType().equals(Application.NETWORK_STOCHASTIC) || srcSimContext.getGeometry().getDimension() != 0){
			throw new RuntimeException("simContext is of type "+srcSimContext.getApplicationType()+" and geometry dimension of "+srcSimContext.getGeometry().getDimension()+", expecting nonspatial stochastic");
		}
		
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
			Simulation nonspatialStochAppNewSim = 
					srcSimContext.addNewSimulation(STOCH_SIM_NAME/*SimulationOwner.DEFAULT_SIM_NAME_PREFIX*/,new MathMappingCallbackTaskAdapter(null),NetworkGenerationRequirements.AllowTruncatedNetwork);

			Simulation newODEAppNewSim = 
				newODEApp.addNewSimulation(ODE_SIM_NAME,new MathMappingCallbackTaskAdapter(null),NetworkGenerationRequirements.AllowTruncatedNetwork);

			Simulation newRuleBasedAppNewSim = 
				newRuleBasedApp.addNewSimulation(NFS_SIM_NAME,new MathMappingCallbackTaskAdapter(null),NetworkGenerationRequirements.AllowTruncatedNetwork);

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
				StdoutSessionLog log = new StdoutSessionLog(bioModel.getVersion().getOwner().getName());
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
				
				TimeSeriesMultitrialData sampleData = new TimeSeriesMultitrialData(varNames, sampleTimes);
				runsolver(nonspatialStochAppNewSim,log,baseDirectory,numTrials,sampleData,DataSetType.stochastic_1);
				runsolver(newODEAppNewSim,log,baseDirectory,1,sampleData,DataSetType.determinstic);
				runsolver(newRuleBasedAppNewSim,log,baseDirectory,numTrials,sampleData,DataSetType.stochastic_2);
				
				writeVarDiffData(baseDirectory,sampleData);
				writeKolmogorovSmirnovTest(baseDirectory, sampleData);
				writeChiSquareTest(baseDirectory, sampleData);
				writeData(baseDirectory, sampleData);
			}finally{
				srcSimContext.removeSimulation(nonspatialStochAppNewSim);
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
	private static final String KS_TEST_FILE = "ks_test.csv";
	private static final String ChiSquared_TEST_FILE = "chiSquared_test.csv";
	private static final String DATA_FILE = "data.csv";

	private static void writeVarDiffData(File baseDir,TimeSeriesMultitrialData sampleData) throws Exception{
		
		StringBuffer sb = new StringBuffer();
		for (int varIndex=0; varIndex<sampleData.varNames.length; varIndex++){
			String varName = sampleData.varNames[varIndex];
			sb.append("\""+varName+"\"");
			double[] varDataStoch = sampleData.getMeanTrajectory(varName,DataSetType.stochastic_1);
			double[] varDataNFS = sampleData.getMeanTrajectory(varName,DataSetType.stochastic_2);
			MinMaxHelp minmaxStoch = new MinMaxHelp(varDataStoch);
			for (int i = 0; i < varDataStoch.length; i++) {
				double diffOfMeans = (varDataStoch[i]/minmaxStoch.diff)-(varDataNFS[i]/minmaxStoch.diff);
				sb.append(","+varDataStoch[i]);
				sb.append(","+varDataNFS[i]);
				sb.append(","+diffOfMeans);
			}
			sb.append("\n");
		}
		FileUtils.writeByteArrayToFile(sb.toString().getBytes(), new File(baseDir,VARDIFF_FILE));
	}
	
	private static void writeKolmogorovSmirnovTest(File baseDir,TimeSeriesMultitrialData sampleData) throws Exception{
		
		StringBuffer sb = new StringBuffer();
		for (int varIndex=0; varIndex<sampleData.varNames.length; varIndex++){
			String varName = sampleData.varNames[varIndex];
			sb.append("\""+varName+"\"");
			for (int timeIndex = 0; timeIndex < sampleData.times.length; timeIndex++) {
				double ks_value = sampleData.kolmogorovSmirnovTest(sampleData.varNames[varIndex],timeIndex);
				sb.append(","+ks_value);
			}
			sb.append("\n");
		}
		FileUtils.writeByteArrayToFile(sb.toString().getBytes(), new File(baseDir,KS_TEST_FILE));
	}
	
	private static void writeData(File baseDir,TimeSeriesMultitrialData sampleData) throws Exception{
		Gson gson = new Gson();
		String json = gson.toJson(sampleData);
		FileUtils.writeByteArrayToFile(json.toString().getBytes(), new File(baseDir,DATA_FILE));
	}
	
	private static void writeChiSquareTest(File baseDir,TimeSeriesMultitrialData sampleData) throws Exception{
		
		StringBuffer sb = new StringBuffer();
		for (int varIndex=0; varIndex<sampleData.varNames.length; varIndex++){
			String varName = sampleData.varNames[varIndex];
			sb.append("\""+varName+"\"");
			for (int timeIndex = 0; timeIndex < sampleData.times.length; timeIndex++) {
				double xs_value = sampleData.chiSquaredTest(sampleData.varNames[varIndex],timeIndex);
				sb.append(","+xs_value);
			}
			sb.append("\n");
		}
		FileUtils.writeByteArrayToFile(sb.toString().getBytes(), new File(baseDir,ChiSquared_TEST_FILE));
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
	
	private static void runsolver(Simulation newSimulation, StdoutSessionLog sessionLog, File baseDirectory, int numRuns, TimeSeriesMultitrialData timeSeriesMultitrialData, DataSetType dataSetType){
		Simulation versSimulation = null;
		File destDir = null;
//		int progress = 1;
		for(int i=0;i<numRuns;i++){
//			if(i >= (progress*numRuns/10)){
//				printout(progress+" ");
//				progress++;
//			}
			try{
				versSimulation = new ClientSimManager.TempSimulation(newSimulation, false);
//				printout(ruleBasedTestDir.getAbsolutePath());
				destDir = new File(baseDirectory,dataSetType.name());
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
				SimulationData simData = new SimulationData(simTask.getSimulationJob().getVCDataIdentifier(), destDir, null, null);
				ODEDataBlock odeDataBlock = simData.getODEDataBlock();
				ODESimData odeSimData = odeDataBlock.getODESimData();
				timeSeriesMultitrialData.addDataSet(odeSimData, dataSetType);
			}catch(Exception e){
				e.printStackTrace();
				File file = new File(baseDirectory,Simulation.createSimulationID(versSimulation.getKey())+"_solverExc.txt");
				writeMessageTofile(file,e.getMessage());
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			clearDir(destDir);
		}
//		printout("\n");
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
		dirName.delete();
	}
	
	private static void writeMessageTofile(File file,String message){
		try{
			FileUtils.writeByteArrayToFile(message.getBytes(), file);
		}catch(Exception e2){
			e2.printStackTrace();
		}	
	}
}

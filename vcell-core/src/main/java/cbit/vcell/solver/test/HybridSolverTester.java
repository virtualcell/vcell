/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.vcell.util.BigString;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.FileUtils;
import org.vcell.util.TokenMangler;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.rmi.event.MessageEvent;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.StdoutSessionLog;
import cbit.vcell.server.SimulationJobStatusPersistent;
import cbit.vcell.server.SimulationStatus;
import cbit.vcell.server.SimulationStatusPersistent;
import cbit.vcell.server.VCellBootstrap;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.DataOperation;
import cbit.vcell.simdata.DataOperationResults;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.SimDataBlock;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.solvers.CartesianMesh;
import cbit.vcell.solvers.FVSolverStandalone;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
/**
 * The class does multiple hybrid stochastic runs and saves the results in one/multilple file(s).
 * Input parameter: String mathModelVCMLFileName, int startingTrialNo. , int numTrials, String varNamesStr(colon delimited), boolean bPrintTime.
 * The output will be files. One file for a variable. File contains the times as rows and variable concentration at each trial as columns.
 * The output file is named as simIDxxxx_varName_startingTrialNo.txt, tab delimited.
 * Creation date: (8/6/2012)
 * @author: Tracy LI
 */
public class HybridSolverTester {
	private static final String POSTPROC = "postproc";
	private static final String varNameDelimiter = ":";
	private String mathModelVCMLFileName = null;
	private int startTrialNo = 1;
	private int numRuns = 1;
	private String[] varNames = null;
	private boolean bPrintTime = true; //by default print out time
	private List<double[][]> results = new ArrayList<double[][]>(); //arraylist length is the num of var names, double[1+numRuns][numTimePoints] the first row is times
	
	public HybridSolverTester(String mathModelVCMLFileName, int startTrialNo, int numRuns, String varNamesStr, boolean bPrintTime){
		this.mathModelVCMLFileName = mathModelVCMLFileName;
		this.startTrialNo = startTrialNo;
		this.numRuns = numRuns;
		this.varNames = getVarNames(varNamesStr);
		this.bPrintTime = bPrintTime;
	}
	
	private String[] getVarNames(String varNamesStr) {
		if(varNamesStr != null && varNamesStr.length() > 0)
		{
			return varNamesStr.split(varNameDelimiter);
		}
		return null;
	}

	public void runHybridTest(String site){
		try {
			double[] timePoints = null;
			File mathModelFile = new File(mathModelVCMLFileName);
			XMLSource vcmlSource = new XMLSource(mathModelFile);
			MathModel mathModel = XmlHelper.XMLToMathModel(vcmlSource);
			
			File simDataDir = mathModelFile.getParentFile();
			Simulation sim = mathModel.getSimulations()[0];
						
			//run multiple trials and each run will have a simID = origSimID + i
			for(int i = 0; i < numRuns; i++){
				System.out.println("--------------Trial No: " + (startTrialNo + i) + "----------------");
				//replace random seed
				sim.getSolverTaskDescription().getSmoldynSimulationOptions().setRandomSeed(new Integer(startTrialNo + i));
				//create sim job
				int jobIndex = startTrialNo + i;
				SimulationTask simTask = new SimulationTask(new SimulationJob(sim,jobIndex, null),0);
				/*
				 * When you want to run the multiple trials on local uncomment the line below.
				 */
				//ResourceUtil.prepareSolverExecutable(sim.getSolverTaskDescription().getSolverDescription());
				/*
				 * When you want to run the multiple trials on server (without VCell user interface), use the next line of code 
				 * Corresponding changes should be made in the shell script runhybridtest for the location of executable on server
				 */
				FVSolverStandalone fvSolver = new FVSolverStandalone(simTask,simDataDir,new StdoutSessionLog(sim.getVersion().getOwner().getName()),false);		
				fvSolver.startSolver();
				
				SolverStatus status = fvSolver.getSolverStatus();
				while (status.getStatus() != SolverStatus.SOLVER_FINISHED && status.getStatus() != SolverStatus.SOLVER_ABORTED )
				{
					System.out.println("progress: " + (int)(fvSolver.getProgress()*100) + "%");
					Thread.sleep(2000); // ask status every 2 seconds
					status = fvSolver.getSolverStatus();
				}

				if(status.getStatus() == SolverStatus.SOLVER_FINISHED){
					//get data
					VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(sim.getVersion().getVersionKey(), sim.getVersion().getOwner());
					VCSimulationDataIdentifier vcSimDataID = new VCSimulationDataIdentifier(vcSimID, jobIndex);
					File hdf5File = new File(simDataDir, vcSimDataID.getID()+SimDataConstants.DATA_PROCESSING_OUTPUT_EXTENSION_HDF5);
					DataOperationResults.DataProcessingOutputInfo dataProcessingOutputInfo =
							(DataOperationResults.DataProcessingOutputInfo)DataSetControllerImpl.getDataProcessingOutput(new DataOperation.DataProcessingOutputInfoOP(vcSimDataID,false,null), hdf5File);

					//initialize for the first time
					
					if(i == 0){ //do only one time
						timePoints = dataProcessingOutputInfo.getVariableTimePoints();
						for(int j=0; j<varNames.length; j++){
							double[][] data = new double[numRuns+1][timePoints.length]; //row: numTimePoints, col:first col time + numRuns
							data[0] = timePoints;
							results.add(data);
						}
					}
					//write into results after each run
					for(int j=0; j<varNames.length; j++){
						results.get(j)[i+1] = dataProcessingOutputInfo.getVariableStatValues().get(varNames[j]);
					}
					
					//delete the file generated for this run
					deleteSimFiles(simDataDir, vcSimDataID);
				}else{
					throw new Exception("Sover did not finish normally." + status);
				}
			}
			
			//write to output file, tab delimited
			if(results != null && results.size() > 0)
			{
				for(int j=0; j<varNames.length; j++)
				{
					File file = new File(simDataDir, "SimID_" + sim.getVersion().getVersionKey().toString() + "_" + varNames[j] + "_" + startTrialNo + ".txt");
					PrintWriter pw = new PrintWriter(file);
					double[][] data = results.get(j);
					if(data != null){
						for(int k=0; k<data.length; k++){
							if(!bPrintTime && k==0)
							{
								continue;
							}
							String rowStr = (k==0)?"Time\t":("trialNo_" + (startTrialNo+k-1) + "\t");
							double[] rowData = data[k];
							for(int q=0; q<rowData.length; q++){
								rowStr += rowData[q] + "\t";
							}
							pw.println(rowStr);
						}
						
					}
					
					pw.close();	
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteSimFiles(File rootDir, final VCSimulationDataIdentifier vcSimDataID){
		
		File[] files = 	rootDir.listFiles( 
							new FileFilter(){
								public boolean accept(File file) {
									if (file.getName().startsWith(vcSimDataID.getID()) && (!file.getName().endsWith(".txt"))){
										return true;
									}
									return false;
								}
							}
						);
		for(File f:files){
			//System.out.println("Deleting file..." + f.getAbsolutePath());
			f.delete();
		}
			
	}
	private static class SimLocHelper {
		public final HashMap<Integer, ArrayList<Integer>> boxToLocs;
		public final HashMap<Integer, Integer> indexToBoxMap;
		public final HashMap<Integer, String> boxToID;
		public SimLocHelper(HashMap<Integer, ArrayList<Integer>> boxToLocs, HashMap<Integer, Integer> indexToBoxMap,
				HashMap<Integer, String> boxToID) {
			super();
			this.boxToLocs = boxToLocs;
			this.indexToBoxMap = indexToBoxMap;
			this.boxToID = boxToID;
		}
		
	}
	public static SimLocHelper calcSimLocs(String csSimLocs,CartesianMesh mesh){
//		ArrayList<Integer> simLocs = new ArrayList<Integer>();
		HashMap<Integer, ArrayList<Integer>> boxToLocs = new HashMap<>();
		HashMap<Integer, Integer> indexToBoxMap = new HashMap<>();
		HashMap<Integer, Integer> boxToSizeMap = new HashMap<>();
		HashMap<Integer, String> boxToID = new HashMap<>();
		StringTokenizer st = new StringTokenizer(csSimLocs, ":");
		int boxCount = 0;
		while(st.hasMoreTokens()){
			boxToSizeMap.put(boxCount, 0);
			boxToLocs.put(boxCount, new ArrayList<Integer>());
			String token = st.nextToken();
			boxToID.put(boxCount, token);
			if(token.contains("-")){//box
				CoordinateIndex coord0,coord1;
				StringTokenizer st2 = new StringTokenizer(token, "-");
				int volIndex = Integer.parseInt(st2.nextToken());
				coord0 = mesh.getCoordinateIndexFromVolumeIndex(volIndex);
				volIndex = Integer.parseInt(st2.nextToken());
				coord1 = mesh.getCoordinateIndexFromVolumeIndex(volIndex);
				for (int z = coord0.z; (coord0.z<coord1.z?z <= coord1.z:z >= coord1.z); z+= (Integer.signum(coord1.z-coord0.z)==0?-1:Integer.signum(coord1.z-coord0.z))) {
					
					for (int y = coord0.y; (coord0.y<coord1.y?y <= coord1.y:y >= coord1.y); y+= (Integer.signum(coord1.y-coord0.y)==0?-1:Integer.signum(coord1.y-coord0.y))) {
						
						for (int x = coord0.x; (coord0.x<coord1.x?x <= coord1.x:x >= coord1.x); x+= (Integer.signum(coord1.x-coord0.x)==0?-1:Integer.signum(coord1.x-coord0.x))) {
							
							int location = mesh.getVolumeIndex(new CoordinateIndex(x, y, z));
//							simLocs.add(location);
							boxToLocs.get(boxCount).add(location);
							indexToBoxMap.put(location, boxCount);
							boxToSizeMap.put(boxCount, boxToSizeMap.get(boxCount)+1);
						}
					}
				}
				boxCount++;
			}else{//points, considered to be a box with 1 element
				int location = Integer.parseInt(token);
//				simLocs.add(location);
				boxToLocs.get(boxCount).add(location);
				indexToBoxMap.put(location, boxCount);
				boxCount++;
			}
		}
		return new SimLocHelper(boxToLocs,indexToBoxMap,boxToID);
	}
	
	private static void makeAltCSV(AltArgsHelper altArgsHelper,FileWriter fw,int runIndex,File userSimDataDir) throws Exception{
		VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(new KeyValue(altArgsHelper.simID), altArgsHelper.user);
		boolean bInit = runIndex == 0;
		ArrayList<Double> simTimes = new ArrayList<Double>();
		StringTokenizer st = null;
		if(altArgsHelper.times.equals("all")){
		}else{
			st = new StringTokenizer(altArgsHelper.times, ":");
			while(st.hasMoreTokens()){
				double timePoint = Double.parseDouble(st.nextToken());
				simTimes.add(timePoint);
			}
		}
		
		SimLocHelper simLocHelper0 = null;
		
		ArrayList<String> simVars = new ArrayList<String>();
		st = new StringTokenizer(altArgsHelper.varnames, ":");
		while(st.hasMoreTokens()){
			String var = st.nextToken();
			simVars.add(var);
		}
		
		int jobCounter = 0;
		final int TIME_SPACE_EXTRA = 0;
		double[][][] trialData = null;
		while(true){
			VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimID, jobCounter);
			SimulationData simData = null;
			DataOperationResults.DataProcessingOutputInfo dataProcessingOutputInfo = null;
			try{
				simData = new SimulationData(vcSimulationDataIdentifier, userSimDataDir, null, null);
				dataProcessingOutputInfo = (DataOperationResults.DataProcessingOutputInfo)
					DataSetControllerImpl.getDataProcessingOutput(new DataOperation.DataProcessingOutputInfoOP(vcSimulationDataIdentifier, true, null), new File(userSimDataDir,SimulationData.createCanonicalPostProcessFileName(vcSimulationDataIdentifier)));
			}catch(FileNotFoundException e){
				if(jobCounter == 0){
					System.out.println("found no trials matching SimID="+altArgsHelper.simID+" in user dir "+userSimDataDir.getAbsolutePath());
				}else{
					System.out.println("found "+jobCounter+" trials in dir "+userSimDataDir.getAbsolutePath()+" matching SimID="+altArgsHelper.simID);
				}
				break;
			}catch(Exception e){
				e.printStackTrace();
			}
			if(dataProcessingOutputInfo == null){
				System.out.println("No postprocessing found for "+jobCounter+" trials in dir "+userSimDataDir.getAbsolutePath()+" matching SimID="+altArgsHelper.simID);
				jobCounter++;
				continue;
			}
			if(simLocHelper0 == null && !altArgsHelper.dataIndexes.equals(POSTPROC)){
				simLocHelper0 = calcSimLocs(altArgsHelper.dataIndexes,simData.getMesh());
			}
			if(jobCounter == 0){
				double[] allDatasetTimes = simData.getDataTimes();
				if(altArgsHelper.times.equals("all")){
					for(double thisTime:allDatasetTimes){
						simTimes.add(thisTime);
					}
				}else{
					//Convert user input times to actual data times
					for (int times = 0; times < simTimes.size(); times++) {
						double masterDelta = Double.POSITIVE_INFINITY;
						double timePoint = -1;
						for (int j = 0; j < allDatasetTimes.length; j++) {
							double tempDelta = Math.abs(simTimes.get(times)-allDatasetTimes[j]);
							if(tempDelta < masterDelta){
								masterDelta = tempDelta;
								timePoint = allDatasetTimes[j];
								if(tempDelta == 0){
									break;
								}
							}
						}
						System.out.println("User time="+simTimes.get(times)+" converted to dataset time="+timePoint);
						simTimes.set(times, timePoint);
				
					}
				}
				trialData = new double[simTimes.size()][(simLocHelper0==null?1:simLocHelper0.boxToLocs.size())][simVars.size()];
			}
			if(bInit && jobCounter == 0){
				//print state vars
				DataIdentifier[] dataIdentifiers = simData.getVarAndFunctionDataIdentifiers(null);
				for (int j = 0; j < dataIdentifiers.length; j++) {
					System.out.println(dataIdentifiers[j]);
				}
				
				printheader(altArgsHelper,simTimes, simLocHelper0, simVars, fw,TIME_SPACE_EXTRA);
			}

			for (int times = 0; times < simTimes.size(); times++) {
				double timePoint = simTimes.get(times);
				for (int vars = 0; vars < simVars.size(); vars++) {
					double[] data = null;
					if(altArgsHelper.dataIndexes.equals(POSTPROC)){
						data = dataProcessingOutputInfo.getVariableStatValues().get(simVars.get(vars)+"_average");
					}else{
						SimDataBlock simDataBlock = simData.getSimDataBlock(null, simVars.get(vars), timePoint);
						data = simDataBlock.getData();
					}
					for (int locs = 0; locs < trialData[times].length; locs++) {
						double val;
						if(simLocHelper0 != null && simLocHelper0.boxToLocs.get(locs).size() == 1){//point
//							System.out.println("pointIndex="+simLocHelper.boxToLocs.get(locs).get(0));
							val = data[simLocHelper0.boxToLocs.get(locs).get(0)];
						}else if (simLocHelper0 != null){//box, calculate the average, could be concentration or counts
							double accum = 0;
							for(Integer locIndex:simLocHelper0.boxToLocs.get(locs)){
//								System.out.println("boxIndex="+locIndex);
								accum+= data[locIndex];
							}
							val = accum / simLocHelper0.boxToLocs.get(locs).size();
						}else{//PostProcess
							if(times < data.length ){
								val = data[times];
							}else{
								val = Double.NaN;
							}
							
						}
						trialData[times][locs][vars] = val;
					}
				}
			}
			fw.write("r="+runIndex+" s="+jobCounter+",");
			for (int times = 0; times < simTimes.size(); times++) {
				for (int locs = 0; locs < trialData[times].length; locs++) {
					for (int vars = 0; vars < simVars.size(); vars++) {
//						System.out.println("job="+jobCounter+" time="+simTimes.get(times)+" loc="+simLocHelper.boxToID.get(locs)+" var="+simVars.get(vars)+" data="+trialData[times][locs][vars]);
						boolean isNan = Double.isNaN(trialData[times][locs][vars]);
						fw.write((isNan?"":trialData[times][locs][vars])+",");
					}
					fw.write(",");
				}
				for (int timeSpace = 0; timeSpace < TIME_SPACE_EXTRA; timeSpace++) {
					fw.write(",");
				}
			}
			fw.write("\n");
			jobCounter++;
		}
		fw.flush();
	}

	private static void runSim(UserLoginInfo userLoginInfo,VCSimulationIdentifier vcSimulationIdentifier,VCellConnectionHelper vcellConnectionHelper) throws Exception{
		SimulationStatusPersistent simulationStatus = vcellConnectionHelper.getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
		System.out.println("initial status="+simulationStatus);
		if(simulationStatus!=null && simulationStatus.isRunning()/*!simulationStatus.isNeverRan() && !simulationStatus.isCompleted()*/){
			throw new Exception("Sim in unexpected state "+simulationStatus);
		}
		
		int intialMaxTaskID = vcellConnectionHelper.getMaxTaskID(simulationStatus);
		BigString simXML = vcellConnectionHelper.getSimulationXML(vcSimulationIdentifier.getSimulationKey());
		Simulation sim = XmlHelper.XMLToSim(simXML.toString());

		int scanCount = sim.getScanCount();
		vcellConnectionHelper.startSimulation(vcSimulationIdentifier, scanCount);
		long startTime = System.currentTimeMillis();
		//wait until sim has stopped running
		while((simulationStatus = vcellConnectionHelper.getSimulationStatus(vcSimulationIdentifier.getSimulationKey())) == null ||
				(simulationStatus.isStopped() || simulationStatus.isCompleted() || simulationStatus.isFailed())){
			MessageEvent[] messageEvents = vcellConnectionHelper.getMessageEvents();
			if(vcellConnectionHelper.getMaxTaskID(simulationStatus) > intialMaxTaskID){//new sim must have started
				break;
			}
			Thread.sleep(250);
//			for(int i = 0;i<(messageEvents==null?0:messageEvents.length);i++){
//				System.out.println(messageEvents[i].toString());	
//			}
			if((System.currentTimeMillis()-startTime) > 120000){
				throw new Exception("Sim finished too fast or took too long to start");
			}
		}
		SimulationStatusPersistent lastSimStatus = simulationStatus;
		while(!simulationStatus.isStopped() && !simulationStatus.isCompleted() && !simulationStatus.isFailed()){
			Thread.sleep(3000);
			simulationStatus = vcellConnectionHelper.getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
			if(simulationStatus !=null && !simulationStatus.toString().equals(lastSimStatus.toString())){
				lastSimStatus = simulationStatus;
				System.out.println("running status="+simulationStatus);
			}
			MessageEvent[] messageEvents = vcellConnectionHelper.getMessageEvents();
//			for(int i = 0;i<(messageEvents==null?0:messageEvents.length);i++){
//				System.out.println(messageEvents[i].toString());	
//			}
		}
		System.out.println("last run simStatus="+simulationStatus);
	}
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_kkmmss");

	private static class AltArgsHelper {
		final String userid;
		final String simID;
		final String times;
		final String dataIndexes;
		final String varnames;
		final int numRuns;
		final File outputDir;
		final boolean bCalcOnly;
		final String dbPassword;
		final String useridKey;
		final String rmiServer;
		final String rmiPort;
		final String simPrefix;
		final File userSimDataDir;
		final User user;
		public AltArgsHelper(String userid, String simID, String times, String dataIndexes, String varnames,int numRuns,
				File outputDir, boolean bCalcOnly, String dbPassword, String useridKey, String rmiServer,
				String rmiPort) {
			super();
			this.userid = userid;
			this.simID = simID;
			this.numRuns = numRuns;
			this.times = times;
			this.dataIndexes = dataIndexes;
			this.varnames = varnames;
			this.outputDir = outputDir;
			this.bCalcOnly = bCalcOnly;
			this.dbPassword = dbPassword;
			this.useridKey = useridKey;
			// alpha rmi-alpha.cam.uchc.edu 40106 (DeployVCell->Configuration->deploymentProperties.xml->rmibootstrap->site)
			this.rmiServer = rmiServer;
			this.rmiPort = rmiPort;
			// 'boris' 21 (VCellDB->vc_userinfo->{id,userid})
			this.user = new User(this.userid, new KeyValue(this.useridKey));
			this.simPrefix  = "SimID_"+this.simID+"_";
//			this.userSimDataDir = new File("\\\\cfs02\\raid\\vcell\\users\\"+this.user.getName());
			this.userSimDataDir = new File("\\\\cfs05\\vcell\\users\\"+this.user.getName());
		}
		@Override
		public String toString() {
			return "userid='"+userid+"' "+
					"simID='"+simID+"' "+
					"numRuns='"+numRuns+"' "+
					"times='"+times+"' "+
					"dataIndexes='"+dataIndexes+"' "+
					"varnames='"+varnames+"' "+
					"outDir='"+outputDir+"' "+
					"bCalOnly='"+bCalcOnly+"' "+
					"dbPassWD='"+"xxxxxx"+"' "+
					"userIDKey='"+useridKey+"' "+
					"rmiServer='"+rmiServer+"' "+
					"rmiPort='"+rmiPort+"' "+
					"simDataDir='"+userSimDataDir+"' ";
		}
		
	}
	private static class VCellConnectionHelper {
		VCellConnection vcellConnection;
		UserLoginInfo userLoginInfo;
		String rmiUrl;
		public VCellConnectionHelper(UserLoginInfo userLoginInfo,String rmiUrl){
			this.userLoginInfo = userLoginInfo;
			this.rmiUrl = rmiUrl;
		}
		public VCellConnection getVCellConnection() throws Exception{
			int numTries = 0;
			while(numTries < 15){
				try{
					if(vcellConnection == null){
						VCellBootstrap vcellBootstrap = (VCellBootstrap)Naming.lookup(rmiUrl);
						vcellConnection = vcellBootstrap.getVCellConnection(userLoginInfo);
						vcellBootstrap = null;
					}
					vcellConnection.getUserMetaDbServer();//check connection
					return vcellConnection;
				}catch(Exception e){
					vcellConnection = null;
					System.out.println("Error getting VCell connection, Retrying: '"+e.getMessage()+"'");
				}
				try{Thread.sleep(60000);}catch(Exception e){System.out.println("sleep interrupted");}
			}
			throw new Exception("getVCellConnection failed after "+numTries+" tries");
		}
		public SimulationStatusPersistent getSimulationStatus(KeyValue simKey)throws Exception{
			int numTries = 0;
			while(numTries < 15){
				try{
					return getVCellConnection().getUserMetaDbServer().getSimulationStatus(simKey);
				}catch(Exception e){
					System.out.println("Error getting simstatus, '"+e.getMessage()+"'");
				}
				try{Thread.sleep(20000);}catch(Exception e){System.out.println("sleep interrupted");}
			}
			throw new Exception("getSimStatus failed after "+numTries+" tries");
		}
		public MessageEvent[] getMessageEvents()throws Exception{
			int numTries = 0;
			while(numTries < 15){
				try{
					return getVCellConnection().getMessageEvents();
				}catch(Exception e){
					System.out.println("Error getting messageEvents, '"+e.getMessage()+"'");
				}
				try{Thread.sleep(20000);}catch(Exception e){System.out.println("sleep interrupted");}
			}
			throw new Exception("getMessageEvents failed after "+numTries+" tries");
		}
		public BigString getSimulationXML(KeyValue simKey)throws Exception{
			int numTries = 0;
			while(numTries < 15){
				try{
					return getVCellConnection().getUserMetaDbServer().getSimulationXML(simKey);
				}catch(Exception e){
					System.out.println("Error getting SimulationXML, '"+e.getMessage()+"'");
				}
				try{Thread.sleep(20000);}catch(Exception e){System.out.println("sleep interrupted");}
			}
			throw new Exception("getSimulationXML failed after "+numTries+" tries");
		}
		//startSimulation(vcSimulationIdentifier, scanCount);
		public SimulationStatus startSimulation(VCSimulationIdentifier vcSimulationIdentifier, int scanCount) throws Exception{
			int numTries = 0;
			while(numTries < 15){
				try{
					return getVCellConnection().getSimulationController().startSimulation(vcSimulationIdentifier, scanCount);
				}catch(Exception e){
					System.out.println("Error startSimulation, '"+e.getMessage()+"'");
				}
				try{Thread.sleep(20000);}catch(Exception e){System.out.println("sleep interrupted");}
			}
			throw new Exception("startSimulation failed after "+numTries+" tries");
		}
		public static int getMaxTaskID(SimulationStatusPersistent simulationStatus){
			int maxTaskID = -1;
			SimulationJobStatusPersistent[] simulationJobStatusPersistent = simulationStatus.getJobStatuses();
			for (int i = 0; i < simulationJobStatusPersistent.length; i++) {
				maxTaskID = Math.max(maxTaskID, simulationJobStatusPersistent[i].getTaskID());
			}
			return maxTaskID;
		}
	}
	public static void main(java.lang.String[] args) {
		VCMongoMessage.enabled = false;
		boolean bAlternate = false;
		if(args.length == 12){
			bAlternate = true;
		}else if(args.length != 5){
			System.out.println("usage: HybridSolverTest userid SimID times(delimited by :) dataIndexes(delimited by :) varNames(delimited by :) numRuns outputFileDirectory bCalclOnly dbPassword useridKey rmiServer rmiPort");
			System.out.println("usage: HybridSolverTest userid SimID all postproc varNames(delimited by :) numRuns outputFileDirectory bCalclOnly dbPassword useridKey rmiServer rmiPort");
			System.out.println("usage: HybridSolverTest mathVCMLFileName startingTrialNo numTrials varNames(delimited by :) bPrintTime vcellSite(rel,beta,...)");
			System.exit(1);
		}
		
		FileWriter fw = null;
		try{
			if(bAlternate){
				AltArgsHelper altArgsHelper = new AltArgsHelper(args[0], args[1], args[2], args[3], args[4], Integer.parseInt(args[5]),
						new File(args[6]), Boolean.parseBoolean(args[7]), args[8], args[9], args[10], args[11]);
//				final String user = args[0];
//				final String simID = args[1];
//				final int numRuns = Integer.parseInt(args[5]);
//				
//				final String simPrefix  = "SimID_"+simID+"_";
//				File userSimDataDir = new File("\\\\cfs02\\raid\\vcell\\users\\"+user);
//				File outputDir = new File(args[6]);
//				boolean bCalcOnly = Boolean.parseBoolean(args[7]);
//				String dbPassword = args[8];
//				String useridKey = args[9];
//				String rmiServer = args[10];
//				String rmiPort = args[11];
				
				VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(new KeyValue(altArgsHelper.simID),altArgsHelper.user);
				UserLoginInfo userLoginInfo = new UserLoginInfo(altArgsHelper.user.getName(), new DigestedPassword(altArgsHelper.dbPassword));

				File[] trialList = null;
				VCellConnectionHelper vCellConnectionHelper = null;
				File emptyFile = File.createTempFile("hstempty",null);
				try{
					if(!altArgsHelper.bCalcOnly){
//						String rmiUrl = "//" + "rmi-alpha.cam.uchc.edu" + ":" + "40106" + "/"+"VCellBootstrapServer";
//						String rmiUrl = "//" + "rmi-alpha.cam.uchc.edu" + ":" + "40112" + "/"+"VCellBootstrapServer";
						String rmiUrl = "//" + altArgsHelper.rmiServer+".cam.uchc.edu" + ":" + altArgsHelper.rmiPort + "/"+"VCellBootstrapServer";

						vCellConnectionHelper = new VCellConnectionHelper(userLoginInfo, rmiUrl);

						for (int runIndex = 0; runIndex < altArgsHelper.numRuns; runIndex++) {
							System.out.println("-----     Starting run "+(runIndex+1)+" of "+altArgsHelper.numRuns);
							runSim(userLoginInfo,vcSimulationIdentifier,vCellConnectionHelper);
							if(trialList == null){
								System.out.println("Sim ran, getting trial list for "+altArgsHelper.simPrefix+" please wait...");
								trialList = altArgsHelper.userSimDataDir.listFiles(new FileFilter() {
								@Override
								public boolean accept(File pathname) {
//									if(pathname.getName().startsWith(altArgsHelper.simPrefix)){
//										System.out.println(pathname);
//									}
									return pathname.getName().startsWith(altArgsHelper.simPrefix) && !pathname.getName().endsWith(".simtask.xml");
								}
								});
							}
							System.out.println("-----     Copying run "+(runIndex+1)+" of "+altArgsHelper.numRuns);
							File outputRunDir = makeOutputRunDir(altArgsHelper.outputDir,runIndex);
							for (int j = 0; j < trialList.length; j++) {
								File localCopyFile = new File(outputRunDir,trialList[j].getName());
								try{
									FileUtils.copyFile(trialList[j],localCopyFile);//copy remote sim data file to local
									trialList[j].delete();//delete remote
								}catch(Exception e){
									System.out.println("failed to copy '"+trialList[j].getAbsolutePath()+"', error="+e.getMessage()+".  Putting empty file as placeholder");
									try{
										FileUtils.copyFile(emptyFile,localCopyFile);//copy empty file to local in place of problematic remote sim data file, later analysis will skip it
									}catch(Exception e2){
										System.out.println("Faild to copy file and failed to copy empty, "+e2.getMessage());
									}
								}
							}
						}
					}
					
					//calc stats
					String dateStr= dateFormat.format(new Date());
					String outPrefix = altArgsHelper.simID+"_"+dateStr+"_0";
					File outputFile = new File(altArgsHelper.outputDir,outPrefix+".csv");
					while(outputFile.exists()){
						outPrefix = TokenMangler.getNextEnumeratedToken(outPrefix);
						outputFile = new File(altArgsHelper.outputDir,outPrefix+".csv");
					}
					fw = new FileWriter(outputFile);
					fw.write("\""+altArgsHelper.toString()+"\"\n");
					for (int runIndex = 0; runIndex < altArgsHelper.numRuns; runIndex++) {
						makeAltCSV(altArgsHelper,fw,runIndex,makeOutputRunDir(altArgsHelper.outputDir,runIndex));
					}
					fw.close();
				}finally{
					if(!altArgsHelper.bCalcOnly){
						File[] straglers = altArgsHelper.userSimDataDir.listFiles(new FileFilter() {
							@Override
							public boolean accept(File pathname) {
								return pathname.getName().startsWith(altArgsHelper.simPrefix);
							}
							});
						if(straglers != null){
							for (int i = 0; i < straglers.length; i++) {
								straglers[i].delete();
							}
						}
					}
				}

				
			}else{
				String site = args[5];
				HybridSolverTester hst = new HybridSolverTester(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3], Boolean.parseBoolean(args[4]));
				hst.runHybridTest(site);
			}
		}catch(Exception e){
			e.printStackTrace(System.out);
			System.exit(1);
		}finally{
			if(fw !=null){try{fw.close();}catch(Exception e){e.printStackTrace();}}
		}
	}

	private static File makeOutputRunDir(File baseOutputDir,int runIndex){
		File outputRunDir = new File(baseOutputDir,"run"+runIndex);
		if(!outputRunDir.exists()){
			outputRunDir.mkdir();
		}
		return outputRunDir;
		
	}
	
	private static void printheader(AltArgsHelper altArgsHelper,ArrayList<Double> simTimes,SimLocHelper simLocHelper,ArrayList<String> simVars,FileWriter fw,int timeSpaceExtra) throws IOException{
		String commas = ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";
		String timeSpaceExtraCommas = "";
		if(timeSpaceExtra > 0){
			timeSpaceExtraCommas = commas.substring(0, timeSpaceExtra);
		}
		int locVarNum = (simLocHelper==null?1:simLocHelper.boxToLocs.size())*(simVars.size()+1);
		fw.write(",");
		for (int times = 0; times < simTimes.size(); times++) {
			double timePoint = simTimes.get(times);
			fw.write(timePoint+commas.substring(0, locVarNum));
			fw.write(timeSpaceExtraCommas);
		}
		fw.write("\n");
		if(!altArgsHelper.dataIndexes.equals(POSTPROC)){
			fw.write(",");
			for (int times = 0; times < simTimes.size(); times++) {
				for (int locs = 0; locs < simLocHelper.boxToLocs.size(); locs++) {
					if(simLocHelper.boxToLocs.get(locs).size() == 1){
						fw.write(simLocHelper.boxToID.get(locs)+commas.substring(0, simVars.size()+1));
					}else{
						fw.write(simLocHelper.boxToID.get(locs)+" ("+simLocHelper.boxToLocs.get(locs).size()+")"+commas.substring(0, simVars.size()+1));					
					}
				}
				fw.write(timeSpaceExtraCommas);
			}
			fw.write("\n");
		}
		fw.write(",");
		for (int times = 0; times < simTimes.size(); times++) {
			for (int locs = 0; locs < (simLocHelper==null?1:simLocHelper.boxToLocs.size()); locs++) {
				for (int vars = 0; vars < simVars.size(); vars++) {
					String var = simVars.get(vars);
					fw.write("\""+var+"\""+",");
				}
				fw.write(",");
			}
			fw.write(timeSpaceExtraCommas);
		}
		fw.write("\n");				

	}
}

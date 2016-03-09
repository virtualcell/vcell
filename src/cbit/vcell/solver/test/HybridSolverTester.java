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
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.vcell.util.BigString;
import org.vcell.util.CoordinateIndex;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.UserLoginInfo.DigestedPassword;

import cbit.rmi.event.MessageEvent;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
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
import cbit.vcell.solver.ode.gui.SimulationStatusPersistent;
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

	public void runHybridTest(){
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
				System.getProperties().put(PropertyLoader.finiteVolumeExecutableProperty, "/share/apps/vcell/deployed/test/numerics/cmake-build/bin/FiniteVolume_x64");	
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
	
	public static ArrayList<Integer> calcSimLocs(String csSimLocs,CartesianMesh mesh){
		ArrayList<Integer> simLocs = new ArrayList<Integer>();
		StringTokenizer st = new StringTokenizer(csSimLocs, ":");
		while(st.hasMoreTokens()){
			String token = st.nextToken();
			if(token.contains(",")){//box
				CoordinateIndex coord0,coord1;
				StringTokenizer st2 = new StringTokenizer(token, ",");
				int volIndex = Integer.parseInt(st2.nextToken());
				coord0 = mesh.getCoordinateIndexFromVolumeIndex(volIndex);
				volIndex = Integer.parseInt(st2.nextToken());
				coord1 = mesh.getCoordinateIndexFromVolumeIndex(volIndex);
				for (int z = coord0.z; (coord0.z<coord1.z?z <= coord1.z:z >= coord1.z); z+= (Integer.signum(coord1.z-coord0.z)==0?-1:Integer.signum(coord1.z-coord0.z))) {
					
					for (int y = coord0.y; (coord0.y<coord1.y?y <= coord1.y:y >= coord1.y); y+= (Integer.signum(coord1.y-coord0.y)==0?-1:Integer.signum(coord1.y-coord0.y))) {
						
						for (int x = coord0.x; (coord0.x<coord1.x?x <= coord1.x:x >= coord1.x); x+= (Integer.signum(coord1.x-coord0.x)==0?-1:Integer.signum(coord1.x-coord0.x))) {
							
							int location = mesh.getVolumeIndex(new CoordinateIndex(x, y, z));
							simLocs.add(location);
						}
					}
				}
			}else{//points
				int location = Integer.parseInt(token);
				simLocs.add(location);
			}
		}
		return simLocs;
	}
	
	private static void makeAltCSV(String csTimes,String csSimLocs,String csSimVars,FileWriter fw,String user,String simID,boolean bInit,File userSimDataDir) throws Exception{
		VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(new KeyValue(simID), new User(user, null));
		final String prefix = "SimID_"+simID+"_";
		
		ArrayList<Double> simTimes = new ArrayList<Double>();
		StringTokenizer st = new StringTokenizer(csTimes, ":");
		while(st.hasMoreTokens()){
			double timePoint = Double.parseDouble(st.nextToken());
			simTimes.add(timePoint);
		}
		
		ArrayList<Integer> simLocs = null;
//		ArrayList<Integer> simLocs = new ArrayList<Integer>();
//		st = new StringTokenizer(csSimLocs, ":");
//		while(st.hasMoreTokens()){
//			int location = Integer.parseInt(st.nextToken());
//			simLocs.add(location);
//		}
		
		ArrayList<String> simVars = new ArrayList<String>();
		st = new StringTokenizer(csSimVars, ":");
		while(st.hasMoreTokens()){
			String var = st.nextToken();
			simVars.add(var);
		}
		
		int jobCounter = 0;
		final int TIME_SPACE_EXTRA = 1;
		while(true){
			VCSimulationDataIdentifier vcSimulationDataIdentifier = new VCSimulationDataIdentifier(vcSimID, jobCounter);
			SimulationData simData = null;
			try{
				simData = new SimulationData(vcSimulationDataIdentifier, userSimDataDir, null, null);
			}catch(FileNotFoundException e){
				if(jobCounter == 0){
					System.out.println("found no trials matching SimID="+simID+" in user dir "+userSimDataDir.getAbsolutePath());
				}else{
					System.out.println("found "+jobCounter+" trials in dir "+userSimDataDir.getAbsolutePath()+" matching SimID="+simID);
				}
				break;
			}
			if(simLocs == null){
				simLocs = calcSimLocs(csSimLocs,simData.getMesh());
			}
			double[][][] trialData = new double[simTimes.size()][simLocs.size()][simVars.size()];
			if(jobCounter == 0){
				//Convert user input times to actual data times
				double[] allDatasetTimes = simData.getDataTimes();
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
			if(bInit && jobCounter == 0){
				//print state vars
				DataIdentifier[] dataIdentifiers = simData.getVarAndFunctionDataIdentifiers(null);
				for (int j = 0; j < dataIdentifiers.length; j++) {
					System.out.println(dataIdentifiers[j]);
				}
				
				printheader(simTimes, simLocs, simVars, fw,TIME_SPACE_EXTRA);
			}

			for (int times = 0; times < simTimes.size(); times++) {
				double timePoint = simTimes.get(times);
				for (int vars = 0; vars < simVars.size(); vars++) {
					SimDataBlock simDataBlock = simData.getSimDataBlock(null, simVars.get(vars), timePoint);
					double[] data = simDataBlock.getData();
					for (int locs = 0; locs < simLocs.size(); locs++) {
						int dataIndex = simLocs.get(locs);
						trialData[times][locs][vars] = data[dataIndex];
					}
				}
			}
			
			for (int times = 0; times < simTimes.size(); times++) {
				for (int locs = 0; locs < simLocs.size(); locs++) {
					for (int vars = 0; vars < simVars.size(); vars++) {
						fw.write(trialData[times][locs][vars]+",");
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

	private static VCellConnection runSim(UserLoginInfo userLoginInfo,VCSimulationIdentifier vcSimulationIdentifier,VCellConnection vcellConnection) throws Exception{
		if(vcellConnection == null){
			String rmiUrl = "//" + "rmi-alpha.cam.uchc.edu" + ":" + "40106" + "/"+"VCellBootstrapServer";
			VCellBootstrap vcellBootstrap = (VCellBootstrap)Naming.lookup(rmiUrl);
			vcellConnection = vcellBootstrap.getVCellConnection(userLoginInfo);
		}
		SimulationStatusPersistent simulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
		System.out.println("initial status="+simulationStatus);
		if(simulationStatus!=null && simulationStatus.isRunning()/*!simulationStatus.isNeverRan() && !simulationStatus.isCompleted()*/){
			throw new Exception("Sim in unexpected state "+simulationStatus);
		}
		BigString simXML = vcellConnection.getUserMetaDbServer().getSimulationXML(vcSimulationIdentifier.getSimulationKey());
		Simulation sim = XmlHelper.XMLToSim(simXML.toString());

		int scanCount = sim.getScanCount();
		vcellConnection.getSimulationController().startSimulation(vcSimulationIdentifier, scanCount);
		long startTime = System.currentTimeMillis();
		while((simulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(vcSimulationIdentifier.getSimulationKey())) == null ||
				(simulationStatus.isStopped() || simulationStatus.isCompleted() || simulationStatus.isFailed())){
			Thread.sleep(250);
			MessageEvent[] messageEvents = vcellConnection.getMessageEvents();
			if((System.currentTimeMillis()-startTime) > 60000){
				throw new Exception("Sim finished too fast or took too long to start");
			}
		}
		SimulationStatusPersistent lastSimStatus = simulationStatus;
		while(!simulationStatus.isStopped() && !simulationStatus.isCompleted() && !simulationStatus.isFailed()){
			Thread.sleep(3000);
			simulationStatus = vcellConnection.getUserMetaDbServer().getSimulationStatus(vcSimulationIdentifier.getSimulationKey());
			if(simulationStatus !=null && !simulationStatus.toString().equals(lastSimStatus.toString())){
				lastSimStatus = simulationStatus;
				System.out.println("running status="+simulationStatus);
			}
			MessageEvent[] messageEvents = vcellConnection.getMessageEvents();
		}
		System.out.println("last run simStatus="+simulationStatus);
		return vcellConnection;
	}
	
	public static void main(java.lang.String[] args) {
		VCMongoMessage.enabled = false;
		boolean bAlternate = false;
		if(args.length == 9){
			bAlternate = true;
		}else if(args.length != 5){
			System.out.println("usage: HybridSolverTest userid SimID times(delimited by :) dataIndexes(delimited by :) varNames(delimited by :) numRuns outputFileDirectory bCalclOnly dbPassword");
			System.out.println("usage: HybridSolverTest mathVCMLFileName startingTrialNo numTrials varNames(delimited by :) bPrintTime");
			System.exit(1);
		}
		
		FileWriter fw = null;
		try{
			if(bAlternate){
				final String user = args[0];
				final String simID = args[1];
				final int numRuns = Integer.parseInt(args[5]);
				
				final String simPrefix  = "SimID_"+simID+"_";
				File userSimDataDir = new File("\\\\cfs02\\raid\\vcell\\users\\"+user);
				File outputDir = new File(args[6]);
				boolean bCalcOnly = Boolean.parseBoolean(args[7]);
				String dbPassword = args[8];
				
				VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(new KeyValue(simID), new User(user, null));
				UserLoginInfo userLoginInfo = new UserLoginInfo(user, new DigestedPassword(dbPassword));

				File[] trialList = null;
				VCellConnection vCellConnection = null;
				try{
					if(!bCalcOnly){
						for (int runIndex = 0; runIndex < numRuns; runIndex++) {
							System.out.println("-----     Starting run "+(runIndex+1)+" of "+numRuns);
							vCellConnection = runSim(userLoginInfo,vcSimulationIdentifier,vCellConnection);
							if(trialList == null){
								trialList = userSimDataDir.listFiles(new FileFilter() {
								@Override
								public boolean accept(File pathname) {
									return pathname.getName().startsWith(simPrefix) && !pathname.getName().endsWith(".simtask.xml");
								}
								});
							}
							System.out.println("-----     Copying run "+(runIndex+1)+" of "+numRuns);
							File outputRunDir = makeOutputRunDir(outputDir,runIndex);
							for (int j = 0; j < trialList.length; j++) {
								FileUtils.copyFile(trialList[j], new File(outputRunDir,trialList[j].getName()));
								trialList[j].delete();
							}
						}
					}
					
					//calc stats
					final File outputFile = new File(outputDir,simID+".csv");
					if(outputFile.exists()){
						throw new Exception("Output File '"+outputFile+"' exists already.");
					}
					fw = new FileWriter(outputFile);
					for (int runIndex = 0; runIndex < numRuns; runIndex++) {
						makeAltCSV(args[2],args[3],args[4],fw,user,simID/*makeNewSimID(simID, runIndex)*/,runIndex==0,makeOutputRunDir(outputDir,runIndex));
					}
					fw.close();
				}finally{
					if(!bCalcOnly){
						File[] straglers = userSimDataDir.listFiles(new FileFilter() {
							@Override
							public boolean accept(File pathname) {
								return pathname.getName().startsWith(simPrefix);
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
				HybridSolverTester hst = new HybridSolverTester(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3], Boolean.parseBoolean(args[4]));
				hst.runHybridTest();
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
	
	private static void printheader(ArrayList<Double> simTimes,ArrayList<Integer> simLocs,ArrayList<String> simVars,FileWriter fw,int timeSpaceExtra) throws IOException{
		String commas = ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";
		String timeSpaceExtraCommas = "";
		if(timeSpaceExtra > 0){
			timeSpaceExtraCommas = commas.substring(0, timeSpaceExtra);
		}
		int locVarNum = simLocs.size()*(simVars.size()+1);
		for (int times = 0; times < simTimes.size(); times++) {
			double timePoint = simTimes.get(times);
			fw.write(timePoint+commas.substring(0, locVarNum));
			fw.write(timeSpaceExtraCommas);
		}
		fw.write("\n");
		
		for (int times = 0; times < simTimes.size(); times++) {
			for (int locs = 0; locs < simLocs.size(); locs++) {
				int loc = simLocs.get(locs);
				fw.write(loc+commas.substring(0, simVars.size()+1));
			}
			fw.write(timeSpaceExtraCommas);
		}
		fw.write("\n");
		
		for (int times = 0; times < simTimes.size(); times++) {
			for (int locs = 0; locs < simLocs.size(); locs++) {
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

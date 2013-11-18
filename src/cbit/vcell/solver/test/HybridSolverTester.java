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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;

import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.StdoutSessionLog;

import cbit.vcell.client.server.DataOperation;
import cbit.vcell.client.server.DataOperationResults;
import cbit.vcell.client.server.DataOperationResults.DataProcessingOutputInfo.PostProcessDataType;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.microscopy.FRAPStudy;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SolverStatus;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
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
							(DataOperationResults.DataProcessingOutputInfo)DataSetControllerImpl.getDataProcessingOutput(new DataOperation.DataProcessingOutputInfoOP(vcSimDataID), hdf5File);

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
	
	//arguments: vcml file name, starting random seed, number of runs, var names
	public static void main(java.lang.String[] args) {
		VCMongoMessage.enabled = false;
		if(args.length != 5)
		{
			System.out.println("usage: HybridSolverTest mathVCMLFileName startingTrialNo numTrials varNames(delimited by :) bPrintTime");
			System.exit(1);
		}
		try{
			HybridSolverTester hst = new HybridSolverTester(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3], Boolean.parseBoolean(args[4]));
			hst.runHybridTest();
		}catch(Exception e){
			e.printStackTrace(System.out);
			System.exit(1);
		}
	}

}

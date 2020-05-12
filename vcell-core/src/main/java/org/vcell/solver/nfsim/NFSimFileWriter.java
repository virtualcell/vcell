/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.solver.nfsim;

import java.io.PrintWriter;

import org.apache.commons.io.output.WriterOutputStream;
import org.jdom.Element;

import cbit.util.xml.XmlUtil;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.solver.NFsimSimulationOptions;
import cbit.vcell.solver.server.SolverFileWriter;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.simdata.DataSetControllerImpl;
import cbit.vcell.simdata.ODEDataBlock;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.util.ColumnDescription;

/**
 * The function reads model information from simulation and
 * generates the stochastic input file for simulation engine.
 * Creation date: (6/22/2006 4:22:59 PM)
 * @author: Tracy LI
 */
public class NFSimFileWriter extends SolverFileWriter 
{
	private long randomSeed = 0; //value assigned in the constructor
//	private RandomDataGenerator dist = new RandomDataGenerator();
	
	
public NFSimFileWriter(PrintWriter pw, SimulationTask simTask, boolean bMessaging) 
{
	super(pw, simTask, bMessaging);
	
	// TODO: this code doesn't seem to be functional, the random seed is properly set in NFSimSolver
	// commented out for now
//	//get user defined random seed. If it doesn't exist, we assign system time (in millisecond) to it.
//	NFsimSimulationOptions nfsimOptions = simTask.getSimulation().getSolverTaskDescription().getNFSimSimulationOptions();
//	
//	if (nfsimOptions.getRandomSeed() != null) {
//		this.randomSeed = nfsimOptions.getRandomSeed();
//	} else {
//		this.randomSeed = System.currentTimeMillis();
//	}
//	//We add jobindex to the random seed in case there is a parameter scan.
//	randomSeed = randomSeed + simTask.getSimulationJob().getJobIndex();
//	dist.reSeed(randomSeed);
}

@Override
public void write(String[] parameterNames) throws Exception {	
	WriterOutputStream wos = new WriterOutputStream(printWriter);
	NFsimSimulationOptions nfsimSimulationOptions = simTask.getSimulation().getSolverTaskDescription().getNFSimSimulationOptions();
	boolean bUseLocationMarks = true;
	Element root = NFsimXMLWriter.writeNFsimXML(simTask, randomSeed, nfsimSimulationOptions, bUseLocationMarks);
	
	if (bUseMessaging) {
		Element jms = super.xmlJMSParameters(); 
		root.addContent(jms);
	}
	
//	String resultString = XmlUtil.xmlToString(root);
//	resultString = XmlUtil.beautify(resultString);
//	resultString = resultString.replaceAll("DeleteMolecules=\"0\"", "DeleteMolecules=\"1\"");
//	
//	System.out.println(resultString);
//
//	StringReader stringReader = new StringReader(resultString);	// transform back to element
//	SAXBuilder builder = new SAXBuilder();
//	Document doc1 = builder.build(stringReader);
//	Element root1 = doc1.getRootElement();
//	XmlUtil.writeXmlToStream(root1, false, wos);	// modified
	XmlUtil.writeXmlToStream(root, false, wos);		// original

}

public static void main(String[] args) {
	try {
		//Change the following to match your environment
		String theSimIDOfParamScanFiles = "159452238";
		String myVCellUserName = "danv";
		String myWindowsUserName = "vasilescu";
//		String localDirName = "multiNFSim";
//		String localDirName = "multiODE";
		String localDirName = "multiStoch";
		
		File theLocalDirForSimFiles = new File("C:\\Users\\"+myWindowsUserName+"\\.vcell\\simdata\\"+localDirName);
		if(!theLocalDirForSimFiles.exists()) {
			if(!theLocalDirForSimFiles.mkdir()) {
				throw new Exception("Couldn't make directory "+theLocalDirForSimFiles.getAbsolutePath());
			}
		}
		File[] paramScanFiles = null;
		
		//Uncomment this section to download files from VCell server to your local dir
		//
		//BEGIN This section downloads files to your local computer-----
		//
//		System.out.println("Getting dir listing from server...");
//		File theVCellSimDirForUser = new File("\\\\cfs05\\vcell\\users\\"+myVCellUserName);
//		paramScanFiles = theVCellSimDirForUser.listFiles(new FileFilter() {
//			@Override
//			public boolean accept(File pathname) {
//				return pathname.isFile() && pathname.getName().startsWith("SimID_"+theSimIDOfParamScanFiles+"_");
//			}
//		});
//		for(File file:paramScanFiles) {
//			System.out.println("copying "+file);
//			FileUtils.copyFile(file, new File(theLocalDirForSimFiles,file.getName()));
//		}
		//
		//END This section downloads files to your local computer-----
		//
		// ============================================================================================		
		//
		//This section shows how to read ODE data (Your processing code will go somewhere in here)
		//
		int numTimePoints = 0;
		ColumnDescription[] columnDescriptions = null;
		// key is the jobIndex, value is the number of time points for that job index
		HashMap<Integer, Integer> jobTimePointsMap = new LinkedHashMap<> ();
		
		DataSetControllerImpl dsci = new DataSetControllerImpl(null, theLocalDirForSimFiles.getParentFile(), null);
		User user_orig = new User(theLocalDirForSimFiles.getName(), new KeyValue("0"));
		VCSimulationIdentifier vcsimid_orig = new VCSimulationIdentifier(new KeyValue(theSimIDOfParamScanFiles), user_orig);
		paramScanFiles = theLocalDirForSimFiles.listFiles();
		
		boolean haveDescriptions = false;
		for(File file:paramScanFiles) {
			System.out.println("reading "+file);
			if(file.getName().endsWith(SimulationData.LOGFILE_EXTENSION)) {
				StringTokenizer st =new StringTokenizer(file.getName(), "_");
				st.nextToken();
				st.nextToken();
				int jobIndex = Integer.parseInt(st.nextToken());
				VCDataIdentifier vcdid_orig = new VCSimulationDataIdentifier(vcsimid_orig, jobIndex);
				double[] times = dsci.getDataSetTimes(vcdid_orig);
				ODEDataBlock odeDataBlock = dsci.getODEDataBlock(vcdid_orig);
				ODESimData odeSimData = odeDataBlock.getODESimData();
				if(!haveDescriptions) {
					columnDescriptions = odeSimData.getColumnDescriptions();
					haveDescriptions = true;
				}
				boolean missingData = false;
				for(ColumnDescription columnDescription:columnDescriptions) {
					double[] data = odeSimData.extractColumn(odeSimData.findColumn(columnDescription.getName()));
					if(data.length < times.length) {	// some data is missing
						missingData = true;
						System.out.println("  data missing for job "+ jobIndex + ": " + columnDescription.getName()+" timesCnt="+times.length+" dataCnt="+data.length);
						break;
					}
				}
				if(missingData) {
					jobTimePointsMap.put(jobIndex, 0);		// we'll ignore this job because some data is missing
				} else {
					jobTimePointsMap.put(jobIndex, times.length);
					if(numTimePoints < times.length) {
						numTimePoints = times.length;
					}
				}
			}
		}			// end first pass
		
		
		
		
		int numValidJobs = 0;			// how many jobs have all the time points
		for(File file:paramScanFiles) {
			if(file.getName().endsWith(SimulationData.LOGFILE_EXTENSION)) {
				StringTokenizer st =new StringTokenizer(file.getName(), "_");
				st.nextToken();
				st.nextToken();
				int jobIndex = Integer.parseInt(st.nextToken());
				if(jobTimePointsMap.get(jobIndex) < numTimePoints) {
					// incomplete data, we don't touch this job
					continue;
				}
				numValidJobs++;
			}
		}
		
		System.out.println("time points: " + numTimePoints);
		System.out.println("valid jobs : " + numValidJobs + " out of " + jobTimePointsMap.size());
		System.out.println("variables  : " + columnDescriptions.length);
		
		// we now have the numValidJobs - which we'll use to compute averages
		ProcessedResultSets processedResults = new ProcessedResultSets(columnDescriptions.length, numTimePoints);
		for(File file:paramScanFiles) {
			if(file.getName().endsWith(SimulationData.LOGFILE_EXTENSION)) {
				StringTokenizer st =new StringTokenizer(file.getName(), "_");
				st.nextToken();
				st.nextToken();
				int jobIndex = Integer.parseInt(st.nextToken());
				if(jobTimePointsMap.get(jobIndex) < numTimePoints) {
					// incomplete data, we don't touch this job
					continue;
				}
				VCDataIdentifier vcdid_orig = new VCSimulationDataIdentifier(vcsimid_orig, jobIndex);
				double[] times = dsci.getDataSetTimes(vcdid_orig);
				ODEDataBlock odeDataBlock = dsci.getODEDataBlock(vcdid_orig);
				ODESimData odeSimData = odeDataBlock.getODESimData();

				for(int i=0; i<columnDescriptions.length; i++) {
					ColumnDescription columnDescription = columnDescriptions[i];
					double[] data = odeSimData.extractColumn(odeSimData.findColumn(columnDescription.getName()));
					for(int j = 0; j<data.length; j++) {
						if(processedResults.allResultSets[i].maxValue[j] < data[j]) {
							processedResults.allResultSets[i].maxValue[j] = data[j];
						}
						if(processedResults.allResultSets[i].minValue[j] > data[j]) {
							processedResults.allResultSets[i].minValue[j] = data[j];
						}
						processedResults.allResultSets[i].avgValue[j] += data[j]/numValidJobs;
					}
				}
			}
		}			// end second pass
		
		int i;		// index of some variable
		for(i=0; i<2; i++) {	// show results for the first 2 variables, time and AAAA_Count
			System.out.println(columnDescriptions[i].getName());
			// timepoints 0, 1, 2 ,3
			System.out.println("  max: " + processedResults.allResultSets[i].maxValue[0] + ", " + processedResults.allResultSets[i].maxValue[1] + ", " + processedResults.allResultSets[i].maxValue[2] + ", " + processedResults.allResultSets[i].maxValue[3]);
			System.out.println("  min: " + processedResults.allResultSets[i].minValue[0] + ", " + processedResults.allResultSets[i].minValue[1] + ", " + processedResults.allResultSets[i].minValue[2] + ", " + processedResults.allResultSets[i].minValue[3]);
			System.out.println("  avg: " + processedResults.allResultSets[i].avgValue[0] + ", " + processedResults.allResultSets[i].avgValue[1] + ", " + processedResults.allResultSets[i].avgValue[2] + ", " + processedResults.allResultSets[i].avgValue[3]);
		}
		System.out.println("Done");
	} catch (Exception e) {
		e.printStackTrace();
	}
}

final static class ProcessedResultSets {
	
	final static class EntityResultSet {
		double[] maxValue = null;	// max value for some variable across all jobs
		double[] minValue = null;
		double[] avgValue = null;
		protected EntityResultSet(int numTimePoints) {
			maxValue = new double[numTimePoints];
			minValue = new double[numTimePoints];
			avgValue = new double[numTimePoints];
			Arrays.fill(minValue, Double.MAX_VALUE);
		}
	}
	
	EntityResultSet[] allResultSets = null;
	protected ProcessedResultSets(int numEntities, int numTimePoints) {
		allResultSets = new EntityResultSet[numEntities];
		for(int i=0; i<numEntities; i++) {
			allResultSets[i] = new EntityResultSet(numTimePoints);
		}
	}
}



}

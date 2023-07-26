/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.batch.sim;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.ApplicationTerminator;
import org.vcell.util.FileUtils;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.simdata.SimulationData.SimDataAmplistorInfo;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverListener;
import cbit.vcell.solvers.HTCSolver;
import cbit.vcell.util.AmplistorUtils;
import cbit.vcell.xml.XmlHelper;
/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 4:14:09 PM)
 * @author: Jim Schaff
 */
public class SolverPreprocessor  {
	public static final Logger lg = LogManager.getLogger(SolverPreprocessor.class);
	/**
	 * Starts the application.
	 * @param args an array of command-line arguments
	 * @throws VCMessagingException 
	 */
	public static void sendFailureAndExit(HTCSolver htcSolver, SimulationTask simTask, String hostName, SimulationMessage simMessage) throws VCMessagingException{
		VCMessagingService service = new VCMessagingServiceActiveMQ();
		String jmshost = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimHostInternal);
		int jmsport = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimPortInternal));
		service.setConfiguration(new ServerMessagingDelegate(), jmshost, jmsport);
		VCMessageSession session = service.createProducerSession();
		try {
			WorkerEventMessage.sendFailed(session, htcSolver, simTask, hostName, simMessage);
			sleep(500);
			service.close();
			sleep(500);
		} catch (VCMessagingException e1) {
			lg.error(e1);
		} finally {
			System.exit(-1);
		}
	}

	private static void sleep(int ms){
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		}
	}

	public static void main(java.lang.String[] args) {
		if (args.length < 2) {
			System.out.print(SolverPreprocessor.class.getName()+" ");
			System.out.println(Arrays.toString(args));
			System.out.println("Missing arguments: " + SolverPreprocessor.class.getName() + " [simulationTaskFile] [userdir] <parallel dir> ");
			System.exit(1);
		}
		File parallelDirectory = null;
		if (args.length >= 3) {
			parallelDirectory = new File(args[2]);
			if (!parallelDirectory.exists()){
				parallelDirectory.mkdirs();
			}
			if (!parallelDirectory.isDirectory() || !parallelDirectory.canWrite()) {
				throw new IllegalArgumentException(parallelDirectory.getAbsolutePath() +  " is not a writeable directory");
			}
		}
		try {
			
			PropertyLoader.loadProperties();
			
			//
			File simulationFile = new File(args[0]);
			final SimulationTask simTask = XmlHelper.XMLToSimTask(FileUtils.readFileToString(simulationFile));
			if (parallelDirectory != null){
				// simulation task needs to be written to the "parallel directory" (a temporary directory) here (it is local to the cluster).
				FileUtils.copyFile(simulationFile, new File(parallelDirectory,simulationFile.getName()));
			}
			File userDirectory = new File(args[1]);
			final String hostName = null;

			VCMongoMessage.serviceStartup(ServiceName.solverPreprocessor, Integer.valueOf(simTask.getSimKey().toString()), args);

 	        final HTCSolver htcSolver = new HTCSolver(simTask, userDirectory,parallelDirectory) {
				public void startSolver() {
					try {
						super.initialize();
					} catch (Exception e) {
						lg.error(e.getMessage(), e);
						SimulationMessage simMessage = SimulationMessage.jobFailed(e.getMessage());
						try {
							sendFailureAndExit(this, simTask, hostName, simMessage);
						} catch (Exception e1) {
							lg.error(e1.getMessage(), e1);
						}
					}
				}
				public void stopSolver() {
				}
				public double getCurrentTime() {
					return 0;
				}
				public double getProgress() {
					return 0;
				}
			};
			SolverListener solverListener = new SolverListener() {
				public void solverStopped(SolverEvent event) {
					VCMongoMessage.sendSolverEvent(event);
					try {
						sendFailureAndExit(htcSolver, simTask, hostName, event.getSimulationMessage());
					} catch (Exception e) {
						lg.error(e.getMessage(), e);
					}
				}
				public void solverStarting(SolverEvent event) {
					VCMongoMessage.sendSolverEvent(event);
				}
				public void solverProgress(SolverEvent event) {
					VCMongoMessage.sendSolverEvent(event);
				}
				public void solverPrinted(SolverEvent event) {
					VCMongoMessage.sendSolverEvent(event);
				}
				public void solverFinished(SolverEvent event) {
					VCMongoMessage.sendSolverEvent(event);
				}
				public void solverAborted(SolverEvent event) {
					VCMongoMessage.sendSolverEvent(event);
					try {
						sendFailureAndExit(htcSolver, simTask, hostName, event.getSimulationMessage());
					} catch (VCMessagingException e) {
						lg.error(e);
					}
				}
			};
			htcSolver.addSolverListener(solverListener);
			htcSolver.startSolver();
			VCMongoMessage.sendInfo("preprocessor done");
			exitWithCode(0);
		} catch (Throwable e) {
			lg.error(e.getMessage(),e);
			exitWithCode(-1);
		}
	}
	
	/**
	 * commence shutdown countdown, shutdown mongo
	 * @param systemReturnCode
	 */
	private static void exitWithCode(int systemReturnCode) {
		ApplicationTerminator.beginCountdown(TimeUnit.SECONDS, 10, systemReturnCode); 
		long start = 0;
		if (lg.isDebugEnabled()) {
			lg.debug("starting mongo shutdown");
			start = System.currentTimeMillis();
		}
		VCMongoMessage.shutdown( );
		if (lg.isDebugEnabled()) {
			long stop = System.currentTimeMillis();
			double elapsed = (stop - start) / 1000.0;
			lg.debug("mongo shutdown " + elapsed + "seconds ");
		}
		System.exit(systemReturnCode);
	}

	public static void downloadFiles(SimDataAmplistorInfo simDataAmplistorInfo,ArrayList<String> fileNames,User user,File destinationUserDir) throws Exception{
		String amplistorUserPath = simDataAmplistorInfo.getAmplistorVCellUsersRootPath()+"/"+user.getName();
		for(String fileName:fileNames){
			File destinationFile = new File(destinationUserDir,fileName);
			if(!destinationFile.exists()){
				try {
					AmplistorUtils.getObjectDataPutInFile(amplistorUserPath+"/"+fileName, simDataAmplistorInfo.getAmplistorCredential(), destinationFile);
				} catch (Exception e) {
					lg.error(e.getMessage(), e);
					//ignore
				}
			}

		}
	}
	
	public static ArrayList<String> getAllMatchingSimData(SimDataAmplistorInfo simDataAmplistorInfo,KeyValue simKey,User user) throws FileNotFoundException,Exception{
		String match = Simulation.createSimulationID(simKey);
		String amplistorUserPath = simDataAmplistorInfo.getAmplistorVCellUsersRootPath()+"/"+user.getName();
		AmplistorUtils.AmplistorFileNameMatcher simidFilter = new AmplistorUtils.AmplistorFileNameMatcher() {
			@Override
			public boolean accept(String fileName) {
				return fileName.startsWith(match);
			}
		};
		ArrayList<String> matchedFileNames = AmplistorUtils.listDir(amplistorUserPath, simidFilter,simDataAmplistorInfo.getAmplistorCredential());
		return matchedFileNames;
	}


	private static void recoverLastSimulationData(SimulationTask simTask,File userDir, Logger lg){
		SimulationData.SimDataAmplistorInfo simDataAmplistorInfo = AmplistorUtils.getSimDataAmplistorInfoFromPropertyLoader();
		if(simDataAmplistorInfo != null){
			try{
				long startTime = 0;
				if (lg.isInfoEnabled( )) {
					startTime = System.currentTimeMillis();
				}
				if(SimulationData.AmplistorHelper.hasLogFileAnywhere(simDataAmplistorInfo, simTask.getSimKey(),simTask.getUser(),simTask.getSimulationJob().getJobIndex(), userDir)){
					//Get back all data since we are "restarting" a simulation
					ArrayList<String> amplistorList = getAllMatchingSimData(simDataAmplistorInfo, simTask.getSimKey(),simTask.getUser());
					if(amplistorList.size() > 0){
//						Collections.sort(amplistorList, new Comparator<String> () {
//						    public int compare(String a, String b) {
//						        return a.compareToIgnoreCase(b);
//						    }
//						});
//						//remove all sim zip file names except the last (latest) sim zip file
//						while(true){
//							boolean bDeleted = false;
//							for (int i = 0; i < amplistorList.size(); i++) {
//								if(amplistorList.get(i).endsWith(SimDataConstants.ZIPFILE_EXTENSION)){
//									if(i < (amplistorList.size()-1) && amplistorList.get(i+1).endsWith(SimDataConstants.ZIPFILE_EXTENSION)){
//										amplistorList.remove(i);
//										bDeleted = true;
//										break;
//									}
//								}
//							}
//							if(!bDeleted){
//								break;
//							}
//						}
						downloadFiles(simDataAmplistorInfo, amplistorList,simTask.getUser(), userDir);
					}
				}
				if (lg.isInfoEnabled( )) {
					lg.info("amplistor preprocess data restore time="+((System.currentTimeMillis()-startTime)/1000)+" seconds");
				}
			}catch(Exception e){
				//ignore, try to run sim
				lg.info("recoverLastSimulationData  exception " + e.getMessage(), e);
			}
		}

	}

}

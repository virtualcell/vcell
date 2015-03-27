/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.sim;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.vcell.util.ApplicationTerminator;
import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.logging.Log4jSessionLog;
import org.vcell.util.logging.Logging;
import org.vcell.util.logging.Logging.ConsoleDestination;

import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.message.server.jmx.VCellServiceMXBean;
import cbit.vcell.message.server.jmx.VCellServiceMXBeanImpl;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverListener;
import cbit.vcell.solvers.HTCSolver;
import cbit.vcell.xml.XmlHelper;
/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 4:14:09 PM)
 * @author: Jim Schaff
 */
public class SolverPreprocessor  {
	private static final String LOG_NAME = "solverPreprocessor";
	/**
	 * Starts the application.
	 * @param args an array of command-line arguments
	 * @throws VCMessagingException 
	 */
	public static void sendFailureAndExit(HTCSolver htcSolver, SimulationTask simTask, String hostName, SimulationMessage simMessage) throws VCMessagingException{
		VCMessagingService service = VCMessagingService.createInstance(new ServerMessagingDelegate());
		VCMessageSession session = service.createProducerSession();
		try {
			WorkerEventMessage.sendFailed(session, htcSolver, simTask, hostName, simMessage);
			sleep(500);
			service.closeAll();
			sleep(500);
		} catch (VCMessagingException e1) {
			e1.printStackTrace();
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
		if (args.length != 2) {
			System.out.print(SolverPreprocessor.class.getName()+" ");
			for (String arg : args){
				System.out.print(" "+arg+" ");
			}
			System.out.println();
			
			System.out.println("Missing arguments: " + SolverPreprocessor.class.getName() + " simulationTaskFile userdir");
			System.exit(1);
		}
		Logging.init();
		Logging.changeConsoleLogging(ConsoleDestination.STD_ERR, ConsoleDestination.STD_OUT); 
		Log4jSessionLog log = new Log4jSessionLog(LOG_NAME);
		try {
			
			PropertyLoader.loadProperties();
			
			//
			File simulationFile = new File(args[0]);
			final SimulationTask simTask = XmlHelper.XMLToSimTask(FileUtils.readFileToString(simulationFile));
			File userdir = new File(args[1]);
			recoverLastSimulationData(simTask,userdir,log.getLogger());
			final String hostName = null;

			VCMongoMessage.serviceStartup(ServiceName.solverPreprocessor, Integer.valueOf(simTask.getSimKey().toString()), args);

			//
			// JMX registration
			//
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			mbs.registerMBean(new VCellServiceMXBeanImpl(), new ObjectName(VCellServiceMXBean.jmxObjectName));
			
 	        final HTCSolver htcSolver = new HTCSolver(simTask, userdir,log) {
				public void startSolver() {
					try {
						initialize();
					} catch (Exception e) {
						e.printStackTrace();
						SimulationMessage simMessage = SimulationMessage.jobFailed(e.getMessage());
						try {
							sendFailureAndExit(this, simTask, hostName, simMessage);
						} catch (Exception e1) {
							e1.printStackTrace();
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
						e.printStackTrace();
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
						e.printStackTrace();
					}
				}
			};
			htcSolver.addSolverListener(solverListener);
			htcSolver.startSolver();
			VCMongoMessage.sendInfo("preprocessor done");
			exitWithCode(0, log.getLogger());
		} catch (Throwable e) {
			log.exception(e);
			exitWithCode(-1,log.getLogger());
		}
	}
	
	/**
	 * commence shutdown countdown, shutdown mongo
	 * @param systemReturnCode
	 */
	private static void exitWithCode(int systemReturnCode, Logger lg) {
		ApplicationTerminator.beginCountdown(TimeUnit.SECONDS, 10, systemReturnCode); 
		long start = 0;
		if (lg.isTraceEnabled()) {
			lg.trace("starting mongo shutdown");
			start = System.currentTimeMillis();
		}
		VCMongoMessage.shutdown( );
		if (lg.isTraceEnabled()) {
			long stop = System.currentTimeMillis();
			double elapsed = (stop - start) / 1000.0;
			lg.trace("mongo shutdown " + elapsed + "seconds ");
		}
		System.exit(systemReturnCode);
	}

	private static void recoverLastSimulationData(SimulationTask simTask,File userDir, Logger lg){
		String amplistor_VCell_Users_RootPath = PropertyLoader.getProperty(PropertyLoader.amplistorVCellUsersRootPath, null);
		if(amplistor_VCell_Users_RootPath != null){
			try{
				long startTime = 0;
				if (lg.isInfoEnabled( )) {
					startTime = System.currentTimeMillis();
				}
				if(SimulationData.AmplistorHelper.hasLogFileAnywhere(amplistor_VCell_Users_RootPath, simTask.getSimKey(),simTask.getUser(),simTask.getSimulationJob().getJobIndex(), userDir)){
					//Get back all data since we are "restarting" a simulation
					ArrayList<String> amplistorList = SimulationData.AmplistorHelper.getAllMatchingSimData(amplistor_VCell_Users_RootPath, simTask.getSimKey(),simTask.getUser());
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
						SimulationData.AmplistorHelper.downloadFiles(amplistor_VCell_Users_RootPath, amplistorList,simTask.getUser(), userDir);
					}
				}
				if (lg.isInfoEnabled( )) {
					lg.info("amplistor preprocess data restore time="+((System.currentTimeMillis()-startTime)/1000)+" seconds");
				}
			}catch(Exception e){
				//ignore, try to run sim
				System.out.println("recoverLastSimulationData exception " + e.getMessage());
				lg.info("recoverLastSimulationData",e);
			}
		}

	}

}

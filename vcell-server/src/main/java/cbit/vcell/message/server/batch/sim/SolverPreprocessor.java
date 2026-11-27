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
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverListener;
import cbit.vcell.solvers.HTCSolver;
import cbit.vcell.xml.XmlHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.ApplicationTerminator;
import org.vcell.util.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
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

}

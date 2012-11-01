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

import org.vcell.util.FileUtils;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;

import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.SolverEvent;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverListener;
import cbit.vcell.solvers.HTCSolver;
import cbit.vcell.xml.XmlHelper;
/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 4:14:09 PM)
 * @author: Jim Schaff
 */
public class SolverPreprocessor  {
	/**
	 * Starts the application.
	 * @param args an array of command-line arguments
	 * @throws VCMessagingException 
	 */
	public static void sendFailureAndExit(HTCSolver htcSolver, SimulationTask simTask, String hostName, SimulationMessage simMessage) throws VCMessagingException{
		VCMessagingService service = VCMessagingService.createInstance();
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

		SessionLog log = new StdoutSessionLog("solverPreprocessor");
		try {
			
			PropertyLoader.loadProperties();
			
			//
			File simulationFile = new File(args[0]);
			final SimulationTask simTask = XmlHelper.XMLToSimTask(FileUtils.readFileToString(simulationFile));
			File userdir = new File(args[1]);
			final String hostName = null;

			VCMongoMessage.serviceStartup(ServiceName.solverPreprocessor, Integer.valueOf(simTask.getSimKey().toString()), args);

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
	
			
			
//
// remove later
//
final long doneTimeMS = System.currentTimeMillis();
Thread daemonThread = new Thread(new Runnable() {
			
		@Override
		public void run() {
			while (true){
				try {
					Thread.sleep(1000);
				}catch (InterruptedException e){
				}
				System.err.println("still waiting to exit ..., elapsed time = "+String.valueOf(System.currentTimeMillis()-doneTimeMS)+" ms");
				Thread.dumpStack();
			}
		}
	},"Cleanup Thread");
daemonThread.setDaemon(true);
daemonThread.start();



	VCMongoMessage.flush();
			System.exit(0);
		} catch (Throwable e) {
			log.exception(e);
			VCMongoMessage.flush();
			System.exit(-1);
		}
	}


}

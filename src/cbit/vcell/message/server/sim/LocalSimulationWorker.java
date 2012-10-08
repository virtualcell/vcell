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
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.StringTokenizer;

import org.vcell.util.DataAccessException;
import org.vcell.util.MessageConstants;
import org.vcell.util.MessageConstants.ServiceType;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.RollbackException;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCQueueConsumer.QueueListener;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.messages.SimulationTaskMessage;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.Solver;
import cbit.vcell.solver.SolverEvent;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverListener;
/**
 * Insert the type's description here.
 * Creation date: (10/25/2001 4:14:09 PM)
 * @author: Jim Schaff
 */
public class LocalSimulationWorker extends ServiceProvider  {

	private VCQueueConsumer queueConsumer = null;
	private VCMessageSession workerProducerSession = null;
	private boolean isRunning = false;
	/**
	 * SimulationWorker constructor comment.
	 * @param argName java.lang.String
	 * @param argParentNode cbit.vcell.appserver.ComputationalNode
	 * @param argInitialContext javax.naming.Context
	 */
public LocalSimulationWorker(VCMessagingService vcMessagingService, ServiceInstanceStatus serviceInstanceStatus, SessionLog log) throws DataAccessException, FileNotFoundException, UnknownHostException {
	super(vcMessagingService, serviceInstanceStatus, log);
}

public final String getJobSelector() {
	String jobSelector = "(" + MessageConstants.MESSAGE_TYPE_PROPERTY + "='" + MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE + "')";
	String computeResources =  PropertyLoader.getRequiredProperty(PropertyLoader.htcComputeResources);
	StringTokenizer st = new StringTokenizer(computeResources, " ,");	
	jobSelector += " AND ((" + MessageConstants.COMPUTE_RESOURCE_PROPERTY + " IS NULL) OR (" + MessageConstants.COMPUTE_RESOURCE_PROPERTY + " IN (";
	int count = 0;
	while (st.hasMoreTokens()) {
		if (count > 0) {
			jobSelector = ", ";
		}
		jobSelector += "'" + st.nextToken() + "'";
		count ++;
	}
	jobSelector += ")))";
	
	return jobSelector;
}

private void initQueueConsumer() {
	QueueListener listener = new QueueListener() {
		
		public void onQueueMessage(VCMessage vcMessage, VCMessageSession session) throws RollbackException {
			SimulationTask simTask = null;
			try {
				SimulationTaskMessage simTaskMessage = new SimulationTaskMessage(vcMessage);
				simTask = simTaskMessage.getSimulationTask();
				File userdir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirProperty),simTask.getUserName());
				WorkerEventMessage.sendAccepted(session, LocalSimulationWorker.this, simTask, ManageUtils.getHostName(), null);
				session.commit();
				
				doSolverJob(simTask,userdir); // service routine won't return until the solver is done (hopefully this is not bad). Else, disable consumer temporarily.
				
			} catch (Exception e) {
				log.exception(e);
				if (simTask!=null){
					try {
						WorkerEventMessage.sendFailed(session,  LocalSimulationWorker.this, simTask, ManageUtils.getHostName(), SimulationMessage.jobFailed(e.getMessage()));
					} catch (VCMessagingException e1) {
						log.exception(e1);
					}
				}
			}
		}
	};

	
	VCellQueue queue = VCellQueue.SimJobQueue;
	VCMessageSelector selector = vcMessagingService.createSelector(getJobSelector());
	String threadName = "SimJob Queue Consumer";
	queueConsumer = new VCQueueConsumer(queue, listener, selector, threadName);
	vcMessagingService.addMessageConsumer(queueConsumer);
	
	this.workerProducerSession = vcMessagingService.createProducerSession();
}

private void doSolverJob(final SimulationTask currentTask, File userdir) throws SolverException {
	Solver currentSolver = cbit.vcell.solver.SolverFactory.createSolver(log,userdir,currentTask, true);
	currentSolver.addSolverListener(new SolverListener() {
		public final void solverAborted(SolverEvent event) {
			try {
				WorkerEventMessage.sendFailed(workerProducerSession, this, currentTask, ManageUtils.getHostName(), event.getSimulationMessage());
			} catch (VCMessagingException e) {
				e.printStackTrace();
			}
		}
		public final void solverFinished(SolverEvent event) {
			try {
				WorkerEventMessage.sendCompleted(workerProducerSession, this, currentTask, ManageUtils.getHostName(), event.getProgress(), event.getTimePoint(), event.getSimulationMessage());
			} catch (VCMessagingException e) {
				e.printStackTrace();
			}
		}
		public final void solverPrinted(SolverEvent event) {
			try {
				WorkerEventMessage.sendNewData(workerProducerSession, this, currentTask, ManageUtils.getHostName(), event.getProgress(), event.getTimePoint(), event.getSimulationMessage());
			} catch (VCMessagingException e) {
				e.printStackTrace();
			}
		}
		public final void solverProgress(SolverEvent event) {
			try {
				WorkerEventMessage.sendProgress(workerProducerSession, this, currentTask, ManageUtils.getHostName(), event.getProgress(), event.getTimePoint(), event.getSimulationMessage());
			} catch (VCMessagingException e) {
				e.printStackTrace();
			}
		}
		public final void solverStarting(SolverEvent event) {
			try {
				WorkerEventMessage.sendStarting(workerProducerSession, this, currentTask, ManageUtils.getHostName(), event.getSimulationMessage());
			} catch (VCMessagingException e) {
				e.printStackTrace();
			}
		}
		public final void solverStopped(SolverEvent event) {		
			log.print("Caught solverStopped(" + event.getSource() + ")");
			// Don't send message anymore because the dispatcher will update the database anyway no matter if the worker responds
			//workerMessaging.sendStopped(event.getProgress(), event.getTimePoint());
		}

	});
	currentSolver.startSolver();
	
	while (true){
		try { 
			Thread.sleep(5000); 
		} catch (InterruptedException e) {
		}

		if (!isRunning || !currentSolver.getSolverStatus().isRunning()) {
			log.print(currentTask + " is no longer running.");
			break;
		}
	}
}

/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	if (args.length < 1) {
		System.out.println("Missing arguments: " + LocalSimulationWorker.class.getName() + " serviceOrdinal [logdir]");
		System.exit(1);
	}
 		
	//
	// Create and install a security manager
	//
	try {
		PropertyLoader.loadProperties();
		
		int serviceOrdinal = Integer.parseInt(args[0]);	
		
		VCMongoMessage.serviceStartup(ServiceName.localWorker, new Integer(serviceOrdinal), args);
		String logdir = null;
		if (args.length > 3) {
			logdir = args[3];
		}
		ServiceInstanceStatus serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(), ServiceType.LOCALCOMPUTE, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);
		//initLog(logdir);
		
		VCMessagingService vcMessagingService = VCMessagingService.createInstance();
		
		SessionLog log = new StdoutSessionLog(serviceInstanceStatus.getID());
		LocalSimulationWorker localSimulationWorker = new LocalSimulationWorker(vcMessagingService, serviceInstanceStatus, log);
		localSimulationWorker.initControlTopicListener();
		localSimulationWorker.initQueueConsumer();
		
	} catch (Throwable e) {
		e.printStackTrace(System.out);
		VCMongoMessage.sendException(e);
		VCMongoMessage.flush();
		System.exit(-1);
	}
}




}

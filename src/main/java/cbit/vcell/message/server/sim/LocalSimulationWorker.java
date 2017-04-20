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
import java.lang.management.ManagementFactory;
import java.net.UnknownHostException;
import java.util.Date;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.vcell.util.DataAccessException;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.RollbackException;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCQueueConsumer.QueueListener;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.messages.SimulationTaskMessage;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.message.server.ServiceSpec.ServiceType;
import cbit.vcell.message.server.jmx.VCellServiceMXBean;
import cbit.vcell.message.server.jmx.VCellServiceMXBeanImpl;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverListener;
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
public LocalSimulationWorker(VCMessagingService vcMessagingService, ServiceInstanceStatus serviceInstanceStatus, SessionLog log, boolean bSlaveMode) throws DataAccessException, FileNotFoundException, UnknownHostException {
	super(vcMessagingService, serviceInstanceStatus, log, bSlaveMode);
}

public final String getJobSelector() {
	String jobSelector = "(" + VCMessagingConstants.MESSAGE_TYPE_PROPERTY + "='" + MessageConstants.MESSAGE_TYPE_SIMULATION_JOB_VALUE + "')";
	
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
	queueConsumer = new VCQueueConsumer(queue, listener, selector, threadName, MessageConstants.PREFETCH_LIMIT_SIM_JOB_LOCAL);
	vcMessagingService.addMessageConsumer(queueConsumer);
	
	this.workerProducerSession = vcMessagingService.createProducerSession();
}

private void doSolverJob(final SimulationTask currentTask, File userdir) throws SolverException {
	Solver currentSolver = cbit.vcell.solver.server.SolverFactory.createSolver(log,userdir,currentTask, true);
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
		if (args.length >= 2) {
			logdir = args[1];
		}
		ServiceInstanceStatus serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(), ServiceType.LOCALCOMPUTE, serviceOrdinal, ManageUtils.getHostName(), new Date(), true);
		initLog(serviceInstanceStatus, logdir);
		
		VCMessagingService vcMessagingService = VCMessagingService.createInstance(new ServerMessagingDelegate());
		
		//
		// JMX registration
		//
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		mbs.registerMBean(new VCellServiceMXBeanImpl(), new ObjectName(VCellServiceMXBean.jmxObjectName));
 
        SessionLog log = new StdoutSessionLog(serviceInstanceStatus.getID());
		LocalSimulationWorker localSimulationWorker = new LocalSimulationWorker(vcMessagingService, serviceInstanceStatus, log, false);
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

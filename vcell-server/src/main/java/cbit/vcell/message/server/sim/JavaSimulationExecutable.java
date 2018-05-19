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

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.FileUtils;
import org.vcell.util.document.KeyValue;

import cbit.vcell.message.MessagePropertyNotFoundException;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCTopicConsumer;
import cbit.vcell.message.VCTopicConsumer.TopicListener;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.message.messages.WorkerEventMessage;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServerMessagingDelegate;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.server.SimulationMessage;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solver.server.SolverListener;
import cbit.vcell.solver.server.SolverStatus;
import cbit.vcell.xml.XmlHelper;

public class JavaSimulationExecutable  {
	public static final Logger lg = LogManager.getLogger(JavaSimulationExecutable.class);

	String[] arguments = null;
	
	private boolean bProgress = true;	
	private SimulationTask simulationTask = null;
	private Solver solver = null;
	private long lastMsgTimeStamp = 0;

	private VCMessagingService vcMessagingService = null;
	private VCMessageSession workerEventSession = null;
	
	private String userDirectory = null;
	private String inputFile = null;
	
	class KeepAliveThread extends Thread {
		public KeepAliveThread() {
			super();
			setName("KeepAliveThread_Compute");
		}	
		public void run() {
			while (true) {
				try {
					sleep(MessageConstants.INTERVAL_PING_SERVER_MS);
				} catch (InterruptedException ex) {
				}
		
				long t = System.currentTimeMillis();
				if (lastMsgTimeStamp != 0 && t - lastMsgTimeStamp > MessageConstants.INTERVAL_PING_SERVER_MS) {
					if (lg.isTraceEnabled()) lg.trace("@@@@Worker:Sending alive message");
					sendAlive();
				}
			}
		}	
	}	
	
/**
 * SimulationWorker constructor comment.
 * @param argName java.lang.String
 * @param argParentNode cbit.vcell.appserver.ComputationalNode
 * @param argInitialContext javax.naming.Context
 */
public JavaSimulationExecutable(VCMessagingService vcMessagingService, String[] args) {
	this.vcMessagingService = vcMessagingService;
	this.arguments = args;
}

private void start() {
	try {
		if (arguments.length != 2) {
			throw new RuntimeException("Missing arguments: " + JavaSimulationExecutable.class.getName() + " simTaskFile userDir");
		}
		
		int argCount = 0;
		inputFile = arguments[argCount ++];
		userDirectory = arguments[argCount ++];
		
		String xmlString = FileUtils.readFileToString(new File(inputFile));
		simulationTask = XmlHelper.XMLToSimTask(xmlString);
		
		if (lg.isTraceEnabled()) lg.trace("Start keep alive thread");
		new KeepAliveThread().start();
		
		runSimulation();
	} catch (Throwable ex) {
		ex.printStackTrace();
		sendFailed(SimulationMessage.solverAborted(ex.getMessage()));
	}
}

protected void init() throws JMSException {
	//
	// session used for sending worker events.
	//
	this.workerEventSession = vcMessagingService.createProducerSession();
	
	//
	// message consumer for Service Control topic message (to stop simulation).
	//
	VCellTopic topic = VCellTopic.ServiceControlTopic;
	TopicListener listener = new TopicListener() {
		
		public void onTopicMessage(VCMessage vcMessage, VCMessageSession session) {
			if (lg.isTraceEnabled()) lg.trace("JavaSimulationExecutable::onControlTopicMessage(): " + vcMessage.show());
			try {
				String msgType = vcMessage.getStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY);

				if (msgType != null && msgType.equals(MessageConstants.MESSAGE_TYPE_STOPSIMULATION_VALUE)) {			
					Long longkey = vcMessage.getLongProperty(MessageConstants.SIMKEY_PROPERTY);
					KeyValue simKey = new KeyValue(longkey + "");
					int jobIndex = vcMessage.getIntProperty(MessageConstants.JOBINDEX_PROPERTY);
					
					if (simKey.equals(simulationTask.getSimKey()) && jobIndex == simulationTask.getSimulationJob().getJobIndex()) {
						solver.stopSolver();
					}
				} 	
			} catch (MessagePropertyNotFoundException ex) {
				lg.error(ex.getMessage(), ex);
				return;
			}
		}
		
	};
	VCMessageSelector selector = null;
	String threadName = "Service Control Topic Consumer";
	VCTopicConsumer serviceControlConsumer = new VCTopicConsumer(topic, listener, selector, threadName, MessageConstants.PREFETCH_LIMIT_SERVICE_CONTROL);
	vcMessagingService.addMessageConsumer(serviceControlConsumer);
}

private void runSimulation() throws SolverException {
	solver = SolverFactory.createSolver(new File(userDirectory), simulationTask, true);
	solver.addSolverListener(new SolverListener() {
		public final void solverAborted(SolverEvent event) {
			sendFailed(event.getSimulationMessage());
		}
		public final void solverFinished(SolverEvent event) {
			sendCompleted(event.getProgress(), event.getTimePoint(), event.getSimulationMessage());
		}
		public final void solverPrinted(SolverEvent event) {
			// can never get data messages here
			sendNewData(event.getProgress(), event.getTimePoint(), event.getSimulationMessage());
		}
		public final void solverProgress(SolverEvent event) {
			// can never get progress message here
			sendProgress(event.getProgress(), event.getTimePoint(), event.getSimulationMessage());
		}
		public final void solverStarting(SolverEvent event) {
			sendStarting(event.getSimulationMessage());
		}
		public final void solverStopped(SolverEvent event) {		
			if (lg.isTraceEnabled()) lg.trace("Caught solverStopped(" + event.getSource() + ")");
			// Don't send message anymore because the dispatcher will update the database anyway no matter if the worker responds
			//workerMessaging.sendStopped(event.getProgress(), event.getTimePoint());
		}
	});
	solver.startSolver();
	
	while (true){
		try { 
			Thread.sleep(500); 
		} catch (InterruptedException e) {
		}

		cbit.vcell.solver.server.SolverStatus solverStatus = solver.getSolverStatus();
		if (solverStatus != null) {
			if (solverStatus.getStatus() != SolverStatus.SOLVER_STARTING &&
				solverStatus.getStatus() != SolverStatus.SOLVER_READY &&
				solverStatus.getStatus() != SolverStatus.SOLVER_RUNNING){
				break;
			}
		}		
	}
}

/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(String[] args) {
	VCMessagingService vcMessagingService = null;
	JavaSimulationExecutable worker = null;
	try {
		PropertyLoader.loadProperties();
		VCMongoMessage.enabled = false;
		
		vcMessagingService = new VCMessagingServiceActiveMQ();
		String jmshost = PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimHostInternal);
		int jmsport = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsSimPortInternal));
		vcMessagingService.setConfiguration(new ServerMessagingDelegate(), jmshost, jmsport);
		
		worker = new JavaSimulationExecutable(vcMessagingService, args);
		worker.init();
		worker.start();
		
	} catch (Throwable ex) {
		ex.printStackTrace();
		if (worker!=null && worker.workerEventSession!=null){
			try {
				WorkerEventMessage.sendFailed(worker.workerEventSession, worker, worker.simulationTask, ManageUtils.getHostName(), SimulationMessage.jobFailed(ex.getMessage()));
			} catch (VCMessagingException e) {
				e.printStackTrace();
			}
		}

	} finally {
		try {
			if (vcMessagingService != null) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}
				vcMessagingService.close();
			}
		} catch (VCMessagingException ex) {
			ex.printStackTrace(System.out);
		}

		System.exit(0);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2003 2:49:54 PM)
 * @param message javax.jms.Message
 * @exception javax.jms.JMSException The exception description.
 */

private void sendAlive() {
	// have to keep sending the messages because it's important
	try {
		if (lg.isTraceEnabled()) lg.trace("sendWorkerAlive(" + simulationTask.getSimulationJobID() + ")");
		WorkerEventMessage.sendWorkerAlive(workerEventSession, this, simulationTask, ManageUtils.getHostName(), SimulationMessage.MESSAGE_WORKEREVENT_WORKERALIVE);
		
		lastMsgTimeStamp = System.currentTimeMillis();
	} catch (VCMessagingException jmse) {
        lg.error(jmse.getMessage(),jmse);
	}
}

private void sendFailed(SimulationMessage failureMessage) {		
	try {
		if (lg.isTraceEnabled()) lg.trace("sendFailure(" + simulationTask.getSimulationJobID() + "," + failureMessage +")");
		WorkerEventMessage.sendFailed(workerEventSession, this, simulationTask, ManageUtils.getHostName(), failureMessage);
	} catch (VCMessagingException ex) {
        lg.error(ex.getMessage(), ex);
	}
}

private void sendNewData(double progress, double timeSec, SimulationMessage simulationMessage) {	
	try {
		long t = System.currentTimeMillis();
		if (bProgress || t - lastMsgTimeStamp > MessageConstants.INTERVAL_PROGRESS_MESSAGE_MS) { // don't send data message too frequently
			if (lg.isTraceEnabled()) lg.trace("sendNewData(" + simulationTask.getSimulationJobID() + "," + (progress * 100) + "%," + timeSec + ")");		
			WorkerEventMessage.sendNewData(workerEventSession, this, simulationTask, ManageUtils.getHostName(), progress, timeSec, simulationMessage);
		
			lastMsgTimeStamp = System.currentTimeMillis();
			bProgress = false;
		}
	} catch (VCMessagingException e) {
		e.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 */
private void sendProgress(double progress, double timeSec, SimulationMessage simulationMessage) {
	try {
		long t = System.currentTimeMillis();
		if (!bProgress || t - lastMsgTimeStamp > MessageConstants.INTERVAL_PROGRESS_MESSAGE_MS 
				|| ((int)(progress * 100)) % 25 == 0) { // don't send progress message too frequently
			if (lg.isTraceEnabled()) lg.trace("sendProgress(" + simulationTask.getSimulationJobID() + "," + (progress * 100) + "%," + timeSec + ")");
			WorkerEventMessage.sendProgress(workerEventSession, this, simulationTask, ManageUtils.getHostName(), progress, timeSec, simulationMessage);
			
			lastMsgTimeStamp = System.currentTimeMillis();
			bProgress = true;
		}
	} catch (VCMessagingException e) {
		lg.error(e.getMessage(), e);
	}
}

private void sendCompleted(double progress, double timeSec, SimulationMessage simulationMessage) {
	// have to keep sending the messages because it's important
	try {
		if (lg.isTraceEnabled()) lg.trace("sendComplete(" + simulationTask.getSimulationJobID() + ")");
		WorkerEventMessage.sendCompleted(workerEventSession, this, simulationTask, ManageUtils.getHostName(),  progress, timeSec, simulationMessage);
	} catch (VCMessagingException jmse) {
        lg.error(jmse.getMessage(), jmse);
	}
}

private void sendStarting(SimulationMessage startingMessage) {
	try {
		if (lg.isTraceEnabled()) lg.trace("sendStarting(" + simulationTask.getSimulationJobID() + ")");
		WorkerEventMessage.sendStarting(workerEventSession, this, simulationTask, ManageUtils.getHostName(), startingMessage);
	} catch (VCMessagingException e) {
        lg.error(e.getMessage(),e);
	}
}

}

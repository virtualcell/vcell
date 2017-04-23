/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.messages;
import java.sql.SQLException;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.rmi.event.WorkerEvent;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;

/**
 * Insert the type's description here.
 * Creation date: (2/5/2004 12:35:20 PM)
 * @author: Fei Gao
 */
public class WorkerEventMessage {
	private WorkerEvent workerEvent = null;	
	
/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public WorkerEventMessage(WorkerEvent event) {
	workerEvent = event;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 * @throws SQLException 
 * @throws DataAccessException 
 */
public WorkerEventMessage(SimulationDatabase simulationDatabase, VCMessage message0) throws DataAccessException, SQLException {
	parseMessage(simulationDatabase, message0);
}

public static String getWorkerEventSelector_ProgressAndData(){
	return "(("+VCMessagingConstants.MESSAGE_TYPE_PROPERTY+"='"+MessageConstants.MESSAGE_TYPE_WORKEREVENT_VALUE+"') AND "+
			"("+MessageConstants.WORKEREVENT_STATUS+" IN ('"+WorkerEvent.JOB_PROGRESS+"', '"+WorkerEvent.JOB_DATA+"') ) )";
}

public static String getWorkerEventSelector_NotProgressAndData(){
	return "(("+VCMessagingConstants.MESSAGE_TYPE_PROPERTY+"='"+MessageConstants.MESSAGE_TYPE_WORKEREVENT_VALUE+"') AND "+
			"("+MessageConstants.WORKEREVENT_STATUS+" NOT IN ('"+WorkerEvent.JOB_PROGRESS+"', '"+WorkerEvent.JOB_DATA+"') ) )";
}


/**
 * Insert the method's description here.
 * Creation date: (3/11/2004 11:32:33 AM)
 * @return cbit.rmi.event.WorkerEvent
 */
public cbit.rmi.event.WorkerEvent getWorkerEvent() {
	return workerEvent;
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 2:19:48 PM)
 * @param message javax.jms.Message
 * @throws DataAccessException 
 * @throws SQLException 
 */
private void parseMessage(SimulationDatabase simDatabase, VCMessage message) throws DataAccessException, SQLException {
	if (message == null) {
		throw new RuntimeException("Null message");
	}	

	if (!message.propertyExists(VCMessagingConstants.MESSAGE_TYPE_PROPERTY)){
		throw new RuntimeException("Wrong message: expecting property "+VCMessagingConstants.MESSAGE_TYPE_PROPERTY);
	}
	String msgType = message.getStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY);
	if (!msgType.equals(MessageConstants.MESSAGE_TYPE_WORKEREVENT_VALUE)) {
		throw new RuntimeException("Wrong message type: "+msgType+", expecting: "+MessageConstants.MESSAGE_TYPE_WORKEREVENT_VALUE);
	}
			
	Object obj = message.getObjectContent();
	if (obj!=null){
		// from Java executable or htcWorker
		if (!(obj instanceof WorkerEvent)) {
			throw new IllegalArgumentException("Expecting object message with object " + WorkerEvent.class.getName() + ", found object :"+obj.getClass().getName());
		}
		workerEvent = (WorkerEvent)obj;

	} else {
		// from c++ executable
		int status = message.getIntProperty(MessageConstants.WORKEREVENT_STATUS);
		String hostname = message.getStringProperty(MessageConstants.HOSTNAME_PROPERTY);
		String username = message.getStringProperty(VCMessagingConstants.USERNAME_PROPERTY);
		int taskID = message.getIntProperty(MessageConstants.TASKID_PROPERTY);
		int jobIndex = message.getIntProperty(MessageConstants.JOBINDEX_PROPERTY);
		Long longkey = message.getLongProperty(MessageConstants.SIMKEY_PROPERTY);

		KeyValue simKey = new KeyValue(longkey + "");
//		Simulation sim = null;

		User user = simDatabase.getUser(username);
		VCSimulationIdentifier vcSimID = new VCSimulationIdentifier(simKey, user);
		
		String statusMessage = null;
		Double progress = null;
		Double timepoint = null;
		
		if (message.propertyExists(MessageConstants.WORKEREVENT_STATUSMSG)){
			statusMessage = message.getStringProperty(MessageConstants.WORKEREVENT_STATUSMSG);
		}

		if (message.propertyExists(MessageConstants.WORKEREVENT_PROGRESS)){
			progress = message.getDoubleProperty(MessageConstants.WORKEREVENT_PROGRESS);
		}
		if (message.propertyExists(MessageConstants.WORKEREVENT_TIMEPOINT)){
			timepoint = message.getDoubleProperty(MessageConstants.WORKEREVENT_TIMEPOINT);
		}
		
		SimulationMessage simulationMessage = SimulationMessage.fromSerializedMessage(statusMessage);
		if (simulationMessage == null) {			
			switch (status) {
			case WorkerEvent.JOB_ACCEPTED:
				throw new RuntimeException("unexpected job_accepted status");
			case WorkerEvent.JOB_STARTING:
				if (statusMessage == null) {
					simulationMessage = SimulationMessage.MESSAGE_WORKEREVENT_STARTING;
				} else {
					simulationMessage = SimulationMessage.workerStarting(statusMessage);
				}
				break;
			case WorkerEvent.JOB_DATA:
				simulationMessage = SimulationMessage.workerData(timepoint);
				break;
			case WorkerEvent.JOB_PROGRESS:
				simulationMessage = SimulationMessage.workerProgress(progress);
				break;
			case WorkerEvent.JOB_FAILURE:
				if (statusMessage == null) {
					simulationMessage = SimulationMessage.MESSAGE_WORKEREVENT_FAILURE;
				} else {
					simulationMessage = SimulationMessage.workerFailure(statusMessage);
				}
				break;
			case WorkerEvent.JOB_COMPLETED:
				if (statusMessage == null) {
					simulationMessage = SimulationMessage.MESSAGE_WORKEREVENT_COMPLETED;
				} else {
					simulationMessage = SimulationMessage.workerCompleted(statusMessage);
				}
				break;
			case WorkerEvent.JOB_WORKER_ALIVE:
				simulationMessage = SimulationMessage.MESSAGE_WORKEREVENT_WORKERALIVE;
				break;
			default:
				throw new RuntimeException("unexpected worker event status : " + status);
			}
		}

		ServiceName serviceName = VCMongoMessage.getServiceName();
		workerEvent = new WorkerEvent(status, serviceName, vcSimID, jobIndex, hostname, taskID, progress, timepoint, simulationMessage);		
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 * @throws VCMessagingException 
 */
public static WorkerEventMessage sendAccepted(VCMessageSession session, Object source, SimulationTask simTask, String hostName, HtcJobID htcJobID) throws VCMessagingException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_ACCEPTED, source, simTask, hostName, SimulationMessage.MESSAGE_JOB_ACCEPTED);
	workerEvent.setHtcJobID(htcJobID);
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public static WorkerEventMessage sendCompleted(VCMessageSession session, Object source, SimulationTask simTask, String hostName, double progress, double timePoint, SimulationMessage simulationMessage) throws VCMessagingException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_COMPLETED, source, simTask, hostName, new Double(progress), new Double(timePoint), simulationMessage);		
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public static WorkerEventMessage sendFailed(VCMessageSession session, Object source, SimulationTask simTask, String hostName, SimulationMessage failMessage) throws VCMessagingException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_FAILURE, source, simTask,	hostName, failMessage);
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public static WorkerEventMessage sendNewData(VCMessageSession session, Object source, SimulationTask simTask, String hostName, double progress, double timePoint, SimulationMessage simulationMessage) throws VCMessagingException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_DATA, source, simTask, hostName, new Double(progress), new Double(timePoint), simulationMessage);		
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public static WorkerEventMessage sendProgress(VCMessageSession session, Object source, SimulationTask simTask, String hostName, double progress, double timePoint, SimulationMessage simulationMessage) throws VCMessagingException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_PROGRESS, source, simTask, hostName, new Double(progress), new Double(timePoint), simulationMessage);		
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public static WorkerEventMessage sendStarting(VCMessageSession session, Object source, SimulationTask simTask, String hostName, SimulationMessage startMessage) throws VCMessagingException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_STARTING, source, simTask, hostName, startMessage);
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 */
public static WorkerEventMessage sendWorkerAlive(VCMessageSession session, Object source, SimulationTask simTask, String hostName, SimulationMessage simulationMessage) throws VCMessagingException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_WORKER_ALIVE, source, simTask, hostName, simulationMessage);
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (10/22/2001 11:20:37 PM)
 * @throws VCMessagingException 
 */
private void sendWorkerEvent(VCMessageSession session) throws VCMessagingException {
	session.sendQueueMessage(VCellQueue.WorkerEventQueue, toMessage(session), true, null);
}


/**
 * Insert the method's description here.
 * Creation date: (5/20/2003 1:36:36 PM)
 * @return javax.jms.Message
 */
private VCMessage toMessage(VCMessageSession session) {		
	VCMessage message = session.createObjectMessage(workerEvent);
	message.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_WORKEREVENT_VALUE);
	return message;
}


public static WorkerEventMessage sendWorkerExitNormal(VCMessageSession session, Object source, String hostName, VCSimulationIdentifier vcSimID, int jobIndex, int taskID, int solverExitCode) throws VCMessagingException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_WORKER_EXIT_NORMAL,source,vcSimID,jobIndex,hostName,taskID,null,null,SimulationMessage.WorkerExited(solverExitCode));
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}

public static WorkerEventMessage sendWorkerExitError(VCMessageSession session, Object source, String hostName, VCSimulationIdentifier vcSimID, int jobIndex, int taskID, int solverExitCode) throws VCMessagingException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_WORKER_EXIT_ERROR,source,vcSimID,jobIndex,hostName,taskID,null,null,SimulationMessage.WorkerExited(solverExitCode));
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}
/**
 * send ExitError with custom message
 * @param message describing the reason for the exit
 * @throws VCMessagingException
 */
public static WorkerEventMessage sendWorkerExitError(VCMessageSession session, Object source, String hostName, VCSimulationIdentifier vcSimID, int jobIndex, int taskID, SimulationMessage message) throws VCMessagingException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_WORKER_EXIT_ERROR,source,vcSimID,jobIndex,hostName,taskID,null,null,message);
	WorkerEventMessage workerEventMessage = new WorkerEventMessage(workerEvent);
	workerEventMessage.sendWorkerEvent(session);

	return workerEventMessage;
}
}
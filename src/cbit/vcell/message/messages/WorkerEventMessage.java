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
import org.vcell.util.MessageConstants;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.htc.PbsJobID;
import cbit.rmi.event.WorkerEvent;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.solver.SimulationInfo;
import cbit.vcell.solver.SimulationMessage;

/**
 * Insert the type's description here.
 * Creation date: (2/5/2004 12:35:20 PM)
 * @author: Fei Gao
 */
public class WorkerEventMessage {
	private WorkerEvent workerEvent = null;	
	private static final String MESSAGE_TYPE_WORKEREVENT_VALUE	= "WorkerEvent";

	public static final String WORKEREVENT_STATUS = "WorkerEvent_Status";
	public static final String WORKEREVENT_PROGRESS = "WorkerEvent_Progress";
	public static final String WORKEREVENT_TIMEPOINT = "WorkerEvent_TimePoint";
	public static final String WORKEREVENT_STATUSMSG = "WorkerEvent_StatusMsg";
	
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
	return "(("+MessageConstants.MESSAGE_TYPE_PROPERTY+"='"+MESSAGE_TYPE_WORKEREVENT_VALUE+"') AND "+
			"("+WORKEREVENT_STATUS+" IN ('"+WorkerEvent.JOB_PROGRESS+"', '"+WorkerEvent.JOB_DATA+"') ) )";
}

public static String getWorkerEventSelector_NotProgressAndData(){
	return "(("+MessageConstants.MESSAGE_TYPE_PROPERTY+"='"+MESSAGE_TYPE_WORKEREVENT_VALUE+"') AND "+
			"("+WORKEREVENT_STATUS+" NOT IN ('"+WorkerEvent.JOB_PROGRESS+"', '"+WorkerEvent.JOB_DATA+"') ) )";
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

	if (!message.propertyExists(MessageConstants.MESSAGE_TYPE_PROPERTY)){
		throw new RuntimeException("Wrong message: expecting property "+MessageConstants.MESSAGE_TYPE_PROPERTY);
	}
	String msgType = message.getStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY);
	if (!msgType.equals(MESSAGE_TYPE_WORKEREVENT_VALUE)) {
		throw new RuntimeException("Wrong message type: "+msgType+", expecting: "+MESSAGE_TYPE_WORKEREVENT_VALUE);
	}
			
	Object obj = message.getObjectContent();
	if (obj!=null){
		// from Java executable or pbsWorker
		if (!(obj instanceof WorkerEvent)) {
			throw new IllegalArgumentException("Expecting object message with object " + WorkerEvent.class.getName() + ", found object :"+obj.getClass().getName());
		}
		workerEvent = (WorkerEvent)obj;

	} else {
		// from c++ executable
		int status = message.getIntProperty(WORKEREVENT_STATUS);
		String hostname = message.getStringProperty(MessageConstants.HOSTNAME_PROPERTY);
		String username = message.getStringProperty(MessageConstants.USERNAME_PROPERTY);
		int taskID = message.getIntProperty(MessageConstants.TASKID_PROPERTY);
		int jobIndex = message.getIntProperty(MessageConstants.JOBINDEX_PROPERTY);
		Long longkey = message.getLongProperty(MessageConstants.SIMKEY_PROPERTY);

		KeyValue simKey = new KeyValue(longkey + "");
//		Simulation sim = null;

		User user = simDatabase.getUser(simKey, username);
		SimulationInfo simInfo = simDatabase.getSimulationInfo(user, simKey);			
		if (simInfo == null) {
			throw new RuntimeException("Null Simulation"); //wrong message	
		}
		
		String statusMessage = null;
		Double progress = null;
		Double timepoint = null;
		
		if (message.propertyExists(WORKEREVENT_STATUSMSG)){
			statusMessage = message.getStringProperty(WORKEREVENT_STATUSMSG);
		}

		if (message.propertyExists(WORKEREVENT_PROGRESS)){
			progress = message.getDoubleProperty(WORKEREVENT_PROGRESS);
		}
		if (message.propertyExists(WORKEREVENT_TIMEPOINT)){
			timepoint = message.getDoubleProperty(WORKEREVENT_TIMEPOINT);
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
		workerEvent = new WorkerEvent(status, serviceName, simInfo.getAuthoritativeVCSimulationIdentifier(), jobIndex, hostname, taskID, progress, timepoint, simulationMessage);		
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:53:34 PM)
 * @param param javax.jms.Message
 * @throws VCMessagingException 
 */
public static WorkerEventMessage sendAccepted(VCMessageSession session, Object source, SimulationTask simTask, String hostName, PbsJobID pbsJobID) throws VCMessagingException {
	WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_ACCEPTED, source, simTask, hostName, SimulationMessage.MESSAGE_JOB_ACCEPTED);
	workerEvent.setPbsJobID(pbsJobID);
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
	session.sendQueueMessage(VCellQueue.WorkerEventQueue, toMessage(session));
}


/**
 * Insert the method's description here.
 * Creation date: (5/20/2003 1:36:36 PM)
 * @return javax.jms.Message
 */
private VCMessage toMessage(VCMessageSession session) {		
	VCMessage message = session.createObjectMessage(workerEvent);
	message.setStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY, MESSAGE_TYPE_WORKEREVENT_VALUE);
	return message;
}
}

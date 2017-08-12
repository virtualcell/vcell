/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.rmi.event;

import org.vcell.util.document.User;

import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;

/**
 * Insert the type's description here.
 * Creation date: (2/5/2004 12:35:20 PM)
 * @author: Fei Gao
 */
public class WorkerEvent extends MessageEvent {
	
	public static final int JOB_ACCEPTED = 998;
	public static final int JOB_STARTING = 999;
	public static final int JOB_DATA = 1000;
	public static final int JOB_PROGRESS = 1001;
	public static final int JOB_FAILURE = 1002;
	public static final int JOB_COMPLETED = 1003;
	public static final int JOB_WORKER_ALIVE = 1004;
	public static final int JOB_WORKER_EXIT_NORMAL = 1015;
	public static final int JOB_WORKER_EXIT_ERROR = 1016;

	private VCSimulationIdentifier vcSimulationIdentifier = null;
	private int jobIndex = -1;
	private String hostName = null;
	private int taskID = -1;	
	private int eventType = -1;
	private Double progress = null;
	private Double timePoint = null;
	private SimulationMessage eventMessage = null;
	private HtcJobID htcJobID = null;

public WorkerEvent(int eventType0, Object source, VCSimulationIdentifier simId0, int jobIndex0, String hostName0, int taskID0, Double progress0, Double timePoint0, SimulationMessage arg_eventMessage) {
	super(source, new MessageSource(source, Simulation.createSimulationID(simId0.getSimulationKey())), new MessageData(new Double[] {progress0, timePoint0}));
	if (arg_eventMessage == null) {
		throw new RuntimeException("WorkerEvent : SimulationMessage should not be null");
	}	
	eventType = eventType0;
	vcSimulationIdentifier = simId0;
	jobIndex = jobIndex0;
	taskID = taskID0;
	hostName = hostName0;
	progress = progress0;
	timePoint = timePoint0;
	eventMessage = arg_eventMessage;
}

//public WorkerEvent(int eventType0, Object source, SimulationJob simJob, String hostName0, SimulationMessage message) {
//	this(eventType0, source, simJob.getVCDataIdentifier().getVcSimID(), simJob.getJobIndex(), hostName0, 0, null, null, message);
//}
//
//public WorkerEvent(int eventType0, Object source, SimulationJob simJob, String hostName0, Double progress0, Double timePoint0, SimulationMessage message) {
//	this(eventType0, source, simJob.getVCDataIdentifier().getVcSimID(), simJob.getJobIndex(), hostName0, 0, progress0, timePoint0, message);
//}
//
public WorkerEvent(int eventType0, Object source, SimulationTask simTask, String hostName0, Double progress0, Double timePoint0, SimulationMessage message) {
	this(eventType0, source, simTask.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), simTask.getSimulationJob().getJobIndex(), hostName0, simTask.getTaskID(), progress0, timePoint0, message);
}

public WorkerEvent(int eventType0, Object source, SimulationTask simTask, String hostName0, SimulationMessage message) {
	this(eventType0, source, simTask.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), simTask.getSimulationJob().getJobIndex(), hostName0, simTask.getTaskID(), null, null, message);
}

/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:37:47 AM)
 * @return java.lang.String
 */

public SimulationMessage getSimulationMessage() {
	return eventMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (3/11/2004 9:33:30 AM)
 * @return int
 */
public int getEventTypeID() {
	return eventType;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:59:27 PM)
 * @return java.lang.String
 */
public String getHostName() {
	return hostName;
}


/**
 * Insert the method's description here.
 * Creation date: (10/4/2005 1:34:00 PM)
 * @return int
 */
public int getJobIndex() {
	return jobIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:36:42 AM)
 * @return double
 */
public Double getProgress() {
	return progress;
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:58:18 PM)
 * @return double
 */
public int getTaskID() {
	return taskID;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:37:04 AM)
 * @return double
 */
public Double getTimePoint() {
	return timePoint;
}

public HtcJobID getHtcJobID(){
	return htcJobID;
}
/**
 * Insert the method's description here.
 * Creation date: (3/11/2004 9:33:30 AM)
 * @return cbit.vcell.server.User
 */
public User getUser() {
	return vcSimulationIdentifier.getOwner();
}


/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:59:27 PM)
 * @return java.lang.String
 */
public String getUserName() {
	return getUser().getName();
}

@Override
public boolean isIntendedFor(User user){
	if (user == null || getUser()==null){
		return true;
	}
	return user.equals(getUser());
}

/**
 * Insert the method's description here.
 * Creation date: (12/31/2003 12:56:45 PM)
 * @return cbit.vcell.solver.SimulationInfo
 */
public VCSimulationDataIdentifier getVCSimulationDataIdentifier() {
	return new VCSimulationDataIdentifier(vcSimulationIdentifier, jobIndex);
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:17:57 AM)
 * @return boolean
 */
public boolean isAcceptedEvent() {
	return eventType == JOB_ACCEPTED;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:17:57 AM)
 * @return boolean
 */
public boolean isCompletedEvent() {
	return eventType == JOB_COMPLETED;
}


/**
 * Insert the method's description here.
 * Creation date: (3/11/2004 9:33:30 AM)
 * @return boolean
 */
@Override
public boolean isSupercededBy(MessageEvent messageEvent) {
	if (messageEvent instanceof WorkerEvent){
		WorkerEvent workerEvent = (WorkerEvent)messageEvent;
		
		if (isProgressEvent() && workerEvent.isProgressEvent()){
			if (getProgress()<workerEvent.getProgress()){
				return true;
			}
		}
		if (isNewDataEvent() && workerEvent.isNewDataEvent()) {
			if (getTimePoint() < workerEvent.getTimePoint()){
				return true;
			}
		}
			
	}
		
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:17:57 AM)
 * @return boolean
 */
public boolean isFailedEvent() {
	return eventType == JOB_FAILURE;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:17:57 AM)
 * @return boolean
 */
public boolean isNewDataEvent() {
	return eventType == JOB_DATA;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:17:57 AM)
 * @return boolean
 */
public boolean isProgressEvent() {
	return eventType == JOB_PROGRESS;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:17:57 AM)
 * @return boolean
 */
public boolean isStartingEvent() {
	return eventType == JOB_STARTING;
}


/**
 * Insert the method's description here.
 * Creation date: (2/9/2004 8:17:57 AM)
 * @return boolean
 */
public boolean isWorkerAliveEvent() {
	return eventType == JOB_WORKER_ALIVE;
}

public boolean isWorkerExitErrorEvent() {
	return eventType == JOB_WORKER_EXIT_ERROR;
}

public boolean isWorkerExitNormalEvent() {
	return eventType == JOB_WORKER_EXIT_NORMAL;
}

public void setHtcJobID(HtcJobID htcJobID) {
	this.htcJobID = htcJobID;
}

public String show(){
	return "WorkerEvent(type="+eventType+",simID="+this.vcSimulationIdentifier+",job="+jobIndex+",task="+taskID+",progress="+progress+",timepoint="+timePoint+",msg="+eventMessage.toSerialization();
}

}

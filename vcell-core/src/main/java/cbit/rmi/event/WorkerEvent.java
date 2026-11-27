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

import java.util.ArrayList;

/**
 * Insert the type's description here.
 * Creation date: (2/5/2004 12:35:20 PM)
 * @author: Fei Gao
 */
public class WorkerEvent extends MessageEvent {

	private static final long serialVersionUID = -1561655122070882711L;  // value before estimatedTime was added; keeps old/new builds compatible

	public static final int JOB_ACCEPTED = 998;
	public static final int JOB_STARTING = 999;
	public static final int JOB_DATA = 1000;
	public static final int JOB_PROGRESS = 1001;
	public static final int JOB_FAILURE = 1002;
	public static final int JOB_COMPLETED = 1003;
	public static final int JOB_WORKER_ALIVE = 1004;
	public static final int JOB_WORKER_EXIT_NORMAL = 1015;
	public static final int JOB_WORKER_EXIT_ERROR = 1016;

	public static final ArrayList<Integer> ALL_JOB_EVENTS = new ArrayList<>(){{add(JOB_ACCEPTED);
	add(JOB_STARTING); add(JOB_DATA); add(JOB_PROGRESS); add(JOB_FAILURE); add(JOB_COMPLETED); add(JOB_WORKER_ALIVE);
	add(JOB_WORKER_EXIT_NORMAL); add(JOB_WORKER_EXIT_ERROR);}};

	private VCSimulationIdentifier vcSimulationIdentifier = null;
	private int jobIndex = -1;
	private String hostName = null;
	private int taskID = -1;	
	private int eventType = -1;
	private Double progress = null;
	private Integer estimatedTime = null;	// estimated total duration of the job in seconds, sent by the langevin watchdog; null or 0 = unknown
	private Double timePoint = null;
	private SimulationMessage eventMessage = null;
	private HtcJobID htcJobID = null;

public WorkerEvent(int eventType, Object source, VCSimulationIdentifier vcSimulationIdentifier, int jobIndex, String hostName, int taskID, Double progress, Integer estimatedTime, Double timePoint, SimulationMessage eventMessage) {
	super(source, new MessageSource(source, Simulation.createSimulationID(vcSimulationIdentifier.getSimulationKey())), new MessageData(new Double[] {progress, timePoint}));
	if (eventMessage == null) {
		throw new RuntimeException("WorkerEvent : SimulationMessage should not be null");
	}
	this.eventType = eventType;
	this.vcSimulationIdentifier = vcSimulationIdentifier;
	this.jobIndex = jobIndex;
	this.taskID = taskID;
	this.hostName = hostName;
	this.progress = progress;
	this.estimatedTime = estimatedTime;
	this.timePoint = timePoint;
	this.eventMessage = eventMessage;
}

public WorkerEvent(int eventType, Object source, VCSimulationIdentifier vcSimulationIdentifier, int jobIndex, String hostName, int taskID, Double progress, Double timePoint, SimulationMessage eventMessage) {
	this(eventType, source, vcSimulationIdentifier, jobIndex, hostName, taskID, progress, null, timePoint, eventMessage);
}

public WorkerEvent(int eventType, Object source, SimulationTask simTask, String hostName, Double progress, Double timePoint, SimulationMessage eventMessage) {
	this(eventType, source, simTask.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), simTask.getSimulationJob().getJobIndex(), hostName, simTask.getTaskID(), progress, timePoint, eventMessage);
}

public WorkerEvent(int eventType, Object source, SimulationTask simTask, String hostName, SimulationMessage eventMessage) {
	this(eventType, source, simTask.getSimulationInfo().getAuthoritativeVCSimulationIdentifier(), simTask.getSimulationJob().getJobIndex(), hostName, simTask.getTaskID(), null, null, eventMessage);
}

public SimulationMessage getSimulationMessage() {
	return eventMessage;
}
public int getEventTypeID() {
	return eventType;
}
public String getHostName() {
	return hostName;
}
public int getJobIndex() {
	return jobIndex;
}
public Double getProgress() {
	return progress;
}
public int getTaskID() {
	return taskID;
}
public Double getTimePoint() {
	return timePoint;
}
public Integer getEstimatedTime() {
	return estimatedTime;
}
public HtcJobID getHtcJobID(){
	return htcJobID;
}

public User getUser() {
	return vcSimulationIdentifier.getOwner();
}
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

public VCSimulationDataIdentifier getVCSimulationDataIdentifier() {
	return new VCSimulationDataIdentifier(vcSimulationIdentifier, jobIndex);
}


public boolean isAcceptedEvent() {
	return eventType == JOB_ACCEPTED;
}


public boolean isCompletedEvent() {
	return eventType == JOB_COMPLETED;
}


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


public boolean isFailedEvent() {
	return eventType == JOB_FAILURE;
}


public boolean isNewDataEvent() {
	return eventType == JOB_DATA;
}


public boolean isProgressEvent() {
	return eventType == JOB_PROGRESS;
}


public boolean isStartingEvent() {
	return eventType == JOB_STARTING;
}


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
	return "WorkerEvent(type="+eventType+",simID="+this.vcSimulationIdentifier+",job="+jobIndex+",task="+taskID+",progress="+progress+",estimatedTime="+estimatedTime+",timepoint="+timePoint+",msg="+eventMessage.toSerialization();
}

}

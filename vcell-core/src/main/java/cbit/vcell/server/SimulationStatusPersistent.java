/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;
import java.util.ArrayList;
import java.util.HashMap;

import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessagePersistent;

@SuppressWarnings("serial")
public class SimulationStatusPersistent implements java.io.Serializable {
	// possible status values
	private static final int UNKNOWN = 0;
	private static final int NEVER_RAN = 1;
	private static final int START_REQUESTED = 2;
	private static final int DISPATCHED = 3;
	private static final int WAITING = 4;
	private static final int QUEUED = 5;
	private static final int RUNNING = 6;
	private static final int COMPLETED = 7;
	private static final int FAILED = 8;
	private static final int STOP_REQUESTED = 9;
	private static final int STOPPED = 10;
	private static final int NOT_SAVED = 11;
	private static final String STATUS_NAMES[] = {
		"unknown", "never ran", "submitted...", "dispatched...", "waiting: too many jobs",
		"queued", "running...", "completed", "failed", "stopping...", "stopped", "not saved"
	};
	// actual info
	private int status = UNKNOWN;
	private String details = null;
	private boolean hasData = false;
	private SimulationJobStatusPersistent[] jobStatuses = null;

public SimulationStatusPersistent(SimulationJobStatusPersistent[] jobStatuses0) {
	//
	// list of jobStatus passed in contain status
	//
	HashMap<Integer, SimulationJobStatusPersistent> currentJobStatusMap = new HashMap<Integer,SimulationJobStatusPersistent>();
	ArrayList<SimulationJobStatusPersistent> oldJobStatusList = new ArrayList<SimulationJobStatusPersistent>();
	for (SimulationJobStatusPersistent status : jobStatuses0){
		SimulationJobStatusPersistent currentJobStatus = currentJobStatusMap.get(status.getJobIndex());
		if (currentJobStatus==null || status.getTaskID()>currentJobStatus.getTaskID()){
			SimulationJobStatusPersistent oldStatus = currentJobStatusMap.put(status.getJobIndex(),status);
			if (oldStatus!=null){
				oldJobStatusList.add(oldStatus);
			}
		}
	}
	this.jobStatuses = currentJobStatusMap.values().toArray(new SimulationJobStatusPersistent[0]);
	initStatus();
}

private SimulationStatusPersistent(int status, boolean hasData, int jobCount) {
	this.status = status;
	this.hasData = hasData;
	jobStatuses = new SimulationJobStatusPersistent[jobCount];
}

private SimulationStatusPersistent(SimulationStatusPersistent oldStatus) {
	this.details = oldStatus.details;
	this.hasData = oldStatus.hasData;
	this.jobStatuses = oldStatus.jobStatuses;
	this.status = oldStatus.status;
}

public java.lang.String getDetails() {
	return details != null ? details : getStatusString();
}

public boolean getHasData() {
	return hasData;
}

public SimulationMessagePersistent getJob0SimulationMessage() {
	return getJobStatuses()[0].getSimulationMessage();
}


public SimulationJobStatusPersistent getJobStatus(int index) {
	if (index >= jobStatuses.length) {
		return null;
	}
	return jobStatuses[index];
}

public String getFailedMessage() {
	if (jobStatuses.length == 1) {
		return getStatusString();
	}
	int failCount = 0;
	for (SimulationJobStatusPersistent jobStatus : jobStatuses) {
		if (jobStatus.getSchedulerStatus().isFailed()) {
			failCount ++;
		}
	}
	return failCount + " of " + jobStatuses.length + " failed";
}

public SimulationJobStatusPersistent[] getJobStatuses() {
	return jobStatuses;
}

public String getStatusString() {
	return STATUS_NAMES[status];
}

public VCSimulationIdentifier getVCSimulationIdentifier() {
	if (jobStatuses != null && jobStatuses.length > 0) {
		return jobStatuses[0].getVCSimulationIdentifier();
	} else {
		return null;
	}
}

private void initStatus() {
	
	boolean allNull = true;
	for (int i = 0; i < jobStatuses.length; i++){
		if (jobStatuses[i] != null) {
			allNull = false;
			break;
		}
	}
	if (allNull) {
		status = UNKNOWN;
		return;
	} else {
		int highStatusIndex = -1;
		boolean bRunning = false;
		
		for (int i = 0; i < jobStatuses.length; i++){

			hasData = hasData  || jobStatuses[i].hasData();			
			
			int currentStatus = status;
			if (jobStatuses[i].getSchedulerStatus().isWaiting()) {
				status = Math.max(status, WAITING);
				bRunning = true;
			} else if (jobStatuses[i].getSchedulerStatus().isQueued()) {
				status = Math.max(status, QUEUED);
				bRunning = true;
			} else if (jobStatuses[i].getSchedulerStatus().isDispatched()) {			
				status = Math.max(status, DISPATCHED);
				bRunning = true;
			} else if (jobStatuses[i].getSchedulerStatus().isRunning()) {
				status = Math.max(status, RUNNING);
				bRunning = true;
			} else if (jobStatuses[i].getSchedulerStatus().isCompleted()) {
				status = Math.max(status, COMPLETED);
			} else if (jobStatuses[i].getSchedulerStatus().isStopped()) {
				status = Math.max(status, STOPPED);
			} else if (jobStatuses[i].getSchedulerStatus().isFailed()) {
				status = Math.max(status, FAILED);
			}
			if (status > currentStatus) highStatusIndex = i;
		}

		details = jobStatuses[highStatusIndex].getSimulationMessage().getDisplayMessage();

		if ((status == COMPLETED || status == STOPPED || status == FAILED) && bRunning) status = RUNNING;
		
	}
}

public boolean isDispatched() {
	return status == DISPATCHED;
}

public boolean isFailed() {
	return status == FAILED;
}

public boolean isJob0Completed() {
	//
	// Convenience method used in TestingFrameworkWindowManager : 
	// In method 'updateSimRunningStatus', there is a need to check if simulation is completed. Since for the testing framework
	// only one simulation is considered, the jobindex is 0; hence we are checking the first jobStatus in the list of
	// SimulationJobStatuses corresponding to this simulationstatus.
	//
	return getJobStatuses()[0].getSchedulerStatus().isCompleted();
}

public boolean isNeverRan() {
	return status == NEVER_RAN;
}

public boolean isRunnable() {
	return status == NOT_SAVED || status == NEVER_RAN || status == COMPLETED || status == FAILED
		|| status == STOPPED || status == UNKNOWN;
}

public boolean isRunning() {
	return status == RUNNING;
}

public boolean isStartRequested() {
	return status == START_REQUESTED;
}

public boolean isStoppable() {
	return status == DISPATCHED || status == RUNNING || status == WAITING || status == QUEUED;
}

public boolean isStopRequested() {
	return status == STOP_REQUESTED;
}

public boolean isStopped() {
	return status == STOPPED;
}

public boolean isUnknown() {
	return status == UNKNOWN;
}

public static SimulationStatusPersistent newStartRequest(int jobCount) {
	SimulationStatusPersistent newStatus = new SimulationStatusPersistent(START_REQUESTED, false, jobCount);
	System.out.println("##  ##  ##  ##  ##  ##  ##  ##  >>>> NEW START REQUEST <<<<< ######################   newstatus=" + newStatus);
	return newStatus;
}

public static SimulationStatusPersistent newStartRequestFailure(String failMsg, int jobCount) {
	SimulationStatusPersistent newStatus = new SimulationStatusPersistent(FAILED, false, jobCount);
	newStatus.details = failMsg;
	System.out.println("##  ##  ##  ##  ##  ##  ##  ##  >>>> NEW START REQUEST FAILURE <<<<< ######################   newstatus=" + newStatus);
	return newStatus;
}

public static SimulationStatusPersistent newStopRequest(SimulationStatusPersistent currentStatus) {
	SimulationStatusPersistent newStatus = new SimulationStatusPersistent(currentStatus);
	newStatus.status = STOP_REQUESTED;
	newStatus.details = null;
	System.out.println("##  ##  ##  ##  ##  ##  ##  ##  >>>> NEW STOP REQUEST <<<< ###########   oldstatus=" + currentStatus + "\n###########  newstatus=" + newStatus + "\n###########");
	return newStatus;
}

public static SimulationStatusPersistent newUnknown(int jobCount) {
	SimulationStatusPersistent newStatus = new SimulationStatusPersistent(UNKNOWN, false, jobCount);
	return newStatus;
}

public int numberOfJobsDone() {
	int done = 0;
	for (int i = 0; i < getJobStatuses().length; i++){
		if (getJobStatuses()[i] != null && getJobStatuses()[i].getSchedulerStatus().isDone()) {
			done ++;
		}
	}
	return done;
}

public boolean isCompleted() {
	for (int i = 0; i < getJobStatuses().length; i++){
		if (getJobStatuses()[i] == null || !getJobStatuses()[i].getSchedulerStatus().isCompleted()) {
			return false;
		}
	}
	return true;
}

public String toString() {
	return "SimulationStatusPersistent["+getStatusString()+", hasData="+getHasData()+", details="+getDetails() + "]";
}
}

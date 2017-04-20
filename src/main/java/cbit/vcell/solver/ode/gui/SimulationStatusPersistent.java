/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.solver.ode.gui;
import java.util.ArrayList;
import java.util.HashMap;

import cbit.vcell.messaging.db.SimulationJobStatusPersistent;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessagePersistent;

/**
 * Insert the type's description here.
 * Creation date: (6/19/2001 3:00:45 PM)
 * @author: Ion Moraru
 */
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
	private HashMap<Integer, Double> progressHash = new HashMap<Integer, Double>();
	private String details = null;
	private boolean hasData = false;
	private SimulationJobStatusPersistent[] jobStatuses = null;

/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 1:28:48 PM)
 * @param status int
 * @param progress double
 * @param details java.lang.String
 * @param hasData boolean
 */
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


/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 1:28:48 PM)
 * @param status int
 * @param progress double
 * @param details java.lang.String
 * @param hasData boolean
 */
private SimulationStatusPersistent(int status, boolean hasData, int jobCount) {
	this.status = status;
	this.hasData = hasData;
	jobStatuses = new SimulationJobStatusPersistent[jobCount];
}


/**
 * Insert the method's description here.
 * Creation date: (10/18/2005 4:46:03 PM)
 * @param oldStatus cbit.vcell.solver.ode.gui.SimulationStatusPersistent
 */
private SimulationStatusPersistent(SimulationStatusPersistent oldStatus) {
	this.details = oldStatus.details;
	this.hasData = oldStatus.hasData;
	this.jobStatuses = oldStatus.jobStatuses;
	this.progressHash = oldStatus.progressHash;
	this.status = oldStatus.status;
}


/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 1:32:37 PM)
 * @return java.lang.String
 */
public java.lang.String getDetails() {
	return details != null ? details : getStatusString();
}


/**
 * Insert the method's description here.
 * Creation date: (6/28/2001 1:33:56 PM)
 * @return boolean
 */
public boolean getHasData() {
	return hasData;
}


/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 1:22:22 PM)
 * @return java.lang.String
 */
public SimulationMessagePersistent getJob0SimulationMessage() {
	return getJobStatuses()[0].getSimulationMessage();
}


/**
 * Insert the method's description here.
 * Creation date: (8/21/2006 10:00:40 AM)
 * @return cbit.vcell.messaging.db.SimulationJobStatusPersistent
 * @param index int
 */
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

/**
 * Insert the method's description here.
 * Creation date: (9/29/2005 12:25:24 PM)
 * @return cbit.vcell.messaging.db.SimulationJobStatusPersistent[]
 */
public SimulationJobStatusPersistent[] getJobStatuses() {
	return jobStatuses;
}


/**
 * Insert the method's description here.
 * Creation date: (6/22/2001 1:32:37 PM)
 * @return double
 */
public Double getProgress() {
	if (jobStatuses == null) return null;
	boolean bAllNullProgress = true;
	double progress = 0;
	for (int i = 0; i < jobStatuses.length; i++){
		if (jobStatuses[i] != null) {
			if (jobStatuses[i].getSchedulerStatus().isDone()) {
				progress += 1;
				bAllNullProgress = false;
			} else {
				Double jobProgress = progressHash.get(jobStatuses[i].getJobIndex());
				if (jobProgress != null) {
					bAllNullProgress = false;
					progress += jobProgress.doubleValue();
				}
			}
		}
	}
	if (bAllNullProgress) {
		return null;
	}
	return new Double(progress);
}


/**
 * Insert the method's description here.
 * Creation date: (8/21/2006 10:13:56 AM)
 * @return java.lang.Double
 * @param index int
 */
public Double getProgressAt(int index) {
	return progressHash.get(index);
}


/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 1:22:22 PM)
 * @return java.lang.String
 */
public String getStatusString() {
	return STATUS_NAMES[status];
}


/**
 * Insert the method's description here.
 * Creation date: (10/5/2005 12:50:07 PM)
 * @return cbit.vcell.solver.VCSimulationIdentifier
 */
public VCSimulationIdentifier getVCSimulationIdentifier() {
	if (jobStatuses != null && jobStatuses.length > 0) {
		return jobStatuses[0].getVCSimulationIdentifier();
	} else {
		return null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 11:25:17 AM)
 * @param newStatus int
 */
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


/**
 * Insert the method's description here.
 * Creation date: (7/3/2003 10:28:24 AM)
 * @return boolean
 */
public boolean isDispatched() {
	return status == DISPATCHED;
}


/**
 * Insert the method's description here.
 * Creation date: (7/3/2003 10:28:24 AM)
 * @return boolean
 */
public boolean isFailed() {
	return status == FAILED;
}


/**
 * Insert the method's description here.
 * Creation date: (7/3/2003 10:28:24 AM)
 * @return boolean
 */
public boolean isJob0Completed() {
	//
	// Convenience method used in TestingFrameworkWindowManager : 
	// In method 'updateSimRunningStatus', there is a need to check if simulation is completed. Since for the testing framework
	// only one simulation is considered, the jobindex is 0; hence we are checking the first jobStatus in the list of
	// SimulationJobStatuses corresponding to this simulationstatus.
	//
	return getJobStatuses()[0].getSchedulerStatus().isCompleted();
}


/**
 * Insert the method's description here.
 * Creation date: (7/3/2003 10:28:24 AM)
 * @return boolean
 */
public boolean isNeverRan() {
	return status == NEVER_RAN;
}


/**
 * Insert the method's description here.
 * Creation date: (7/3/2003 10:28:24 AM)
 * @return boolean
 */
public boolean isRunnable() {
	return status == NOT_SAVED || status == NEVER_RAN || status == COMPLETED || status == FAILED
		|| status == STOPPED || status == UNKNOWN;
}


/**
 * Insert the method's description here.
 * Creation date: (7/3/2003 10:28:24 AM)
 * @return boolean
 */
public boolean isRunning() {
	return status == RUNNING;
}


/**
 * Insert the method's description here.
 * Creation date: (7/3/2003 10:28:24 AM)
 * @return boolean
 */
public boolean isStartRequested() {
	return status == START_REQUESTED;
}


/**
 * Insert the method's description here.
 * Creation date: (7/3/2003 10:28:24 AM)
 * @return boolean
 */
public boolean isStoppable() {
	return status == DISPATCHED || status == RUNNING || status == WAITING || status == QUEUED;
}


/**
 * Insert the method's description here.
 * Creation date: (7/3/2003 10:28:24 AM)
 * @return boolean
 */
public boolean isStopRequested() {
	return status == STOP_REQUESTED;
}

public boolean isStopped() {
	return status == STOPPED;
}
/**
 * Insert the method's description here.
 * Creation date: (7/3/2003 10:28:24 AM)
 * @return boolean
 */
public boolean isUnknown() {
	return status == UNKNOWN;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 12:45:40 PM)
 * @return cbit.vcell.solver.ode.gui.SimulationStatusPersistent
 * @param jobStatus0 cbit.vcell.messaging.db.SimulationJobStatusPersistent
 * @param progress java.lang.Double
 */
public static SimulationStatusPersistent newNeverRan(int jobCount) {
	SimulationStatusPersistent newStatus = new SimulationStatusPersistent(NEVER_RAN, false, jobCount);
	System.out.println("##  ##  ##  ##  ##  ##  ##  ##  >>>> NEW NEVER RAN <<<<< ######################   newstatus=" + newStatus);
	return newStatus;
}

public static SimulationStatusPersistent newNotSaved(int jobCount) {
	SimulationStatusPersistent newStatus = new SimulationStatusPersistent(NOT_SAVED, false, jobCount);
	System.out.println("##  ##  ##  ##  ##  ##  ##  ##  >>>> NEW NOT SAVED <<<<< ######################   newstatus=" + newStatus);
	return newStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 12:45:40 PM)
 * @return cbit.vcell.solver.ode.gui.SimulationStatusPersistent
 * @param jobStatus0 cbit.vcell.messaging.db.SimulationJobStatusPersistent
 * @param progress java.lang.Double
 */
public static SimulationStatusPersistent newStartRequest(int jobCount) {
	SimulationStatusPersistent newStatus = new SimulationStatusPersistent(START_REQUESTED, false, jobCount);
	System.out.println("##  ##  ##  ##  ##  ##  ##  ##  >>>> NEW START REQUEST <<<<< ######################   newstatus=" + newStatus);
	return newStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 12:45:40 PM)
 * @return cbit.vcell.solver.ode.gui.SimulationStatusPersistent
 * @param jobStatus0 cbit.vcell.messaging.db.SimulationJobStatusPersistent
 * @param progress java.lang.Double
 */
public static SimulationStatusPersistent newStartRequestFailure(String failMsg, int jobCount) {
	SimulationStatusPersistent newStatus = new SimulationStatusPersistent(FAILED, false, jobCount);
	newStatus.details = failMsg;
	System.out.println("##  ##  ##  ##  ##  ##  ##  ##  >>>> NEW START REQUEST FAILURE <<<<< ######################   newstatus=" + newStatus);
	return newStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 12:45:40 PM)
 * @return cbit.vcell.solver.ode.gui.SimulationStatusPersistent
 * @param jobStatus0 cbit.vcell.messaging.db.SimulationJobStatusPersistent
 * @param progress java.lang.Double
 */
public static SimulationStatusPersistent newStopRequest(SimulationStatusPersistent currentStatus) {
	SimulationStatusPersistent newStatus = new SimulationStatusPersistent(currentStatus);
	newStatus.status = STOP_REQUESTED;
	newStatus.details = null;
	System.out.println("##  ##  ##  ##  ##  ##  ##  ##  >>>> NEW STOP REQUEST <<<< ###########   oldstatus=" + currentStatus + "\n###########  newstatus=" + newStatus + "\n###########");
	return newStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (2/10/2004 12:45:40 PM)
 * @return cbit.vcell.solver.ode.gui.SimulationStatusPersistent
 * @param jobStatus0 cbit.vcell.messaging.db.SimulationJobStatusPersistent
 * @param progress java.lang.Double
 */
public static SimulationStatusPersistent newUnknown(int jobCount) {
	SimulationStatusPersistent newStatus = new SimulationStatusPersistent(UNKNOWN, false, jobCount);
	return newStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (7/3/2003 10:28:24 AM)
 * @return boolean
 */
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

/**
 * Insert the method's description here.
 * Creation date: (8/3/2001 9:58:46 PM)
 * @return java.lang.String
 */
public String toString() {
	return "SimulationStatusPersistent["+getStatusString()+", hasData="+getHasData()+", progress="+getProgress()+", details="+getDetails() + "]";
}
}

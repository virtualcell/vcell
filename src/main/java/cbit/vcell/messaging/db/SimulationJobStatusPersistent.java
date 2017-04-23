/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.messaging.db;
import java.io.Serializable;
import java.util.Date;

import org.vcell.util.Compare;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.server.htc.pbs.PbsJobID;
import cbit.vcell.message.server.htc.sge.SgeJobID;
import cbit.vcell.message.server.htc.slurm.SlurmJobID;
import cbit.vcell.server.HtcJobID;
import cbit.vcell.server.HtcJobID.BatchSystemType;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessagePersistent;

/**
 * Insert the type's description here.
 * Creation date: (1/31/2003 11:02:54 AM)
 * @author: Jim Schaff
 */
public class SimulationJobStatusPersistent implements org.vcell.util.Matchable, Serializable {
	//
	// mandatory fields - not null
	//
	private Date fieldTimeDateStamp = null;
	private VCSimulationIdentifier fieldVCSimID = null;
	private Date fieldSubmitDate = null;
	private SchedulerStatus fieldSchedulerStatus; // define here
	private int fieldTaskID = 0;
	private SimulationMessagePersistent fieldSimulationMessage = null;
	private VCellServerID fieldServerID = null;
	private int fieldJobIndex;
	private SimulationQueueEntryStatusPersistent fieldSimulationQueueEntryStatus = null;	// may be null
	private SimulationExecutionStatusPersistent fieldSimulationExecutionStatus = null;	// may be null

	public static enum SchedulerStatus {
		WAITING(0,"waiting"),
		QUEUED(1,"queued"),
		DISPATCHED(2,"dispatched"),
		RUNNING(3,"running"),
		COMPLETED(4,"completed"),
		STOPPED(5,"stopped"),
		FAILED(6,"failed");
		
		private int databaseNumber;
		private String description;
		private SchedulerStatus(int databaseNumber, String desc){
			this.databaseNumber = databaseNumber;
			this.description = desc;
		}
		
		public int getDatabaseNumber(){
			return databaseNumber;
		}
		public String getDescription(){
			return description;
		}

		public boolean isSupercededBy(SchedulerStatus other) {
			return other.getDatabaseNumber() > getDatabaseNumber();
		}

		public boolean isWaiting() {
			return this.equals(WAITING);
		}
		public boolean isQueued() {
			return this.equals(QUEUED);
		}
		public boolean isDispatched() {
			return this.equals(DISPATCHED);
		}
		public boolean isRunning() {
			return this.equals(RUNNING);
		}
		public boolean isCompleted() {
			return this.equals(COMPLETED);
		}
		public boolean isStopped() {
			return this.equals(STOPPED);
		}
		public boolean isFailed() {
			return this.equals(FAILED);
		}
		public boolean inQueue(){
			return this.equals(WAITING)||this.equals(QUEUED);
		}
		public boolean isActive() {
			return (isRunning() || isWaiting() || isQueued() || isDispatched());
		}
		public boolean isDone() {
			return  (isStopped() || isFailed() || isCompleted());
		}

		public static SchedulerStatus fromDatabaseNumber(int databaseNumber) {
			for (SchedulerStatus status : values()){
				if (status.getDatabaseNumber()==databaseNumber){
					return status;
				}
			}
			throw new RuntimeException("unexpected SchedulerStatus database number "+databaseNumber);
		}
	}

	

	public static enum SimulationQueueID {
		QUEUE_ID_WAITING(0),
		QUEUE_ID_SIMULATIONJOB(1),
		QUEUE_ID_NULL(2);
		
		private int databaseNumber;
		private SimulationQueueID(int databaseNumber){
			this.databaseNumber = databaseNumber;
		}
		
		public int getDatabaseNumber(){
			return this.databaseNumber;
		}
		
		public static SimulationQueueID fromDatabaseNumber(int databaseNumber){
			if (databaseNumber == QUEUE_ID_NULL.databaseNumber){
				return QUEUE_ID_NULL;
			}else if (databaseNumber == QUEUE_ID_SIMULATIONJOB.databaseNumber){
				return QUEUE_ID_SIMULATIONJOB;
			}else if (databaseNumber == QUEUE_ID_WAITING.databaseNumber){
				return QUEUE_ID_WAITING;
			}else{
				return null;
			}
		}
	}



/**
 * SimulationJobStatus constructor comment.
 */
public SimulationJobStatusPersistent(VCellServerID serverID, VCSimulationIdentifier vcSimID, int jobIndex, Date submitDate, SchedulerStatus schedulerStatus, int taskID, SimulationMessagePersistent simMessage, SimulationQueueEntryStatusPersistent simQueueStatus, SimulationExecutionStatusPersistent simExeStatus){
	if (vcSimID == null) {
		throw new RuntimeException("SimulationJobStatus : VCSimID should not be null");
	}
	if (simMessage == null) {
		throw new RuntimeException("SimulationJobStatus : SimulationMessage should not be null");
	}
	if (schedulerStatus == null) {
		throw new RuntimeException("SimulationJobStatus : SchedulerStatus should not be null");
	}
	fieldServerID = serverID;
	fieldVCSimID = vcSimID;
	fieldSubmitDate = submitDate;
	fieldSchedulerStatus = schedulerStatus;
	fieldTaskID = taskID;
	fieldSimulationMessage = simMessage;
	fieldSimulationExecutionStatus = simExeStatus;
	fieldSimulationQueueEntryStatus = simQueueStatus;
	fieldJobIndex = jobIndex;
}

/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(org.vcell.util.Matchable obj) {
	if (obj instanceof SimulationJobStatusPersistent){
		SimulationJobStatusPersistent jobStatus = (SimulationJobStatusPersistent)obj;
		if (jobStatus.fieldSchedulerStatus != fieldSchedulerStatus){
			//System.out.println("getSchedulerStatus() not =");
			return false;
		}
		if (!jobStatus.fieldVCSimID.equals(fieldVCSimID)){
			//System.out.println("fieldSimKey not =");
			return false;
		}
		if (!Compare.isEqualOrNull(jobStatus.getSimulationExecutionStatus(), fieldSimulationExecutionStatus)) {
			//System.out.println("getSimulationExecutionStatus() not =");
			return false;
		}
		if (!Compare.isEqualOrNull(jobStatus.fieldSimulationQueueEntryStatus, fieldSimulationQueueEntryStatus)) {
			//System.out.println("getSimulationQueueEntryStatus() not =");
			return false;
		}
		if (fieldSimulationMessage != null && jobStatus.fieldSimulationMessage != null && !jobStatus.fieldSimulationMessage.equals(fieldSimulationMessage)){
			//System.out.println("fieldStatusMessage not =");
			return false;
		}
		if (fieldSubmitDate != null && jobStatus.fieldSubmitDate != null && jobStatus.fieldSubmitDate.getTime()/1000 != fieldSubmitDate.getTime()/1000){
			//System.out.println("fieldSubmitDate not =");
			return false;
		}
		if (jobStatus.fieldTaskID != fieldTaskID) {
			//System.out.println("fieldTaskID not =");
			return false;
		}
		if (jobStatus.fieldServerID != null && fieldServerID != null && !jobStatus.fieldServerID.equals(fieldServerID)) {
			return false;
		}
		if (jobStatus.fieldJobIndex != fieldJobIndex) {
			return false;
		}
		//
		// TIME STAMPS WILL BE DIFFERENT FOR EVERY OBJECT, IGNORE FOR THESE PURPOSES
		//
		//if (!jobStatus.getTimeDateStamp().equals(getTimeDateStamp())){
		//	return false;
		//}
		return true;
	}
	return false;
}

public boolean isSupercededBy(SimulationJobStatusPersistent simJobStatus, Double oldProgress, Double newProgress){
	
	if (!simJobStatus.fieldVCSimID.equals(fieldVCSimID)){
		throw new RuntimeException("comparing SimulationJobStatus from different simulations");
	}

	if (fieldTaskID < simJobStatus.fieldTaskID){
		return true;
	}
	
	if (fieldSchedulerStatus.isSupercededBy(simJobStatus.fieldSchedulerStatus)){
		return true;
	}else if (simJobStatus.fieldSchedulerStatus.isSupercededBy(fieldSchedulerStatus)){
		return false;
	}
	
	//
	// simJobStatus.schedulerStatus == fieldSchedulerStatus 
	//
	if (simJobStatus.fieldSchedulerStatus == SchedulerStatus.RUNNING && fieldSchedulerStatus == SchedulerStatus.RUNNING){
		if (oldProgress!=null && newProgress!=null){
			if (oldProgress < newProgress){
				return true;
			}
		}else if (oldProgress!=null && newProgress==null){
			return false;
		}else if (oldProgress==null && newProgress!=null){
			return true;
		}else{ // both old and new progress are null
			if (getSimulationMessage().getDetailedState().ordinal() < simJobStatus.getSimulationMessage().getDetailedState().ordinal()){
				return true;
			}
		}
	}
	
	return false;
}

/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.lang.String
 */
public java.lang.String getComputeHost() {
	if (fieldSimulationExecutionStatus == null) {
		return null;
	}
	return fieldSimulationExecutionStatus.getComputeHost();
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.util.Date
 */
public java.util.Date getEndDate() {
	if (fieldSimulationExecutionStatus == null) {
		return null;
	}
	return fieldSimulationExecutionStatus.getEndDate();
}


/**
 * Insert the method's description here.
 * Creation date: (9/28/2005 12:11:42 PM)
 * @return int
 */
public int getJobIndex() {
	return fieldJobIndex;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:15:48 AM)
 * @return int
 */
public SchedulerStatus getSchedulerStatus() {
	return fieldSchedulerStatus;
}

/**
 * Insert the method's description here.
 * Creation date: (4/27/2005 2:09:03 PM)
 * @return cbit.vcell.messaging.db.VCellServerID
 */
public VCellServerID getServerID() {
	return fieldServerID;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:31:57 AM)
 * @return cbit.vcell.solvers.SimulationExecutionStatus
 */
public SimulationExecutionStatusPersistent getSimulationExecutionStatus() {
	return fieldSimulationExecutionStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:31:57 AM)
 * @return cbit.vcell.solvers.SimulationQueueEntryStatus
 */
public SimulationQueueEntryStatusPersistent getSimulationQueueEntryStatus() {
	return fieldSimulationQueueEntryStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.util.Date
 */
public java.util.Date getStartDate() {
	if (fieldSimulationExecutionStatus == null) {
		return null;
	}
	return fieldSimulationExecutionStatus.getStartDate();
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 7:59:05 AM)
 * @return java.lang.String
 */
public SimulationMessagePersistent getSimulationMessage() {
	return fieldSimulationMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:15:48 AM)
 * @return java.util.Date
 */
public Date getSubmitDate() {
	return fieldSubmitDate;
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 11:50:03 AM)
 * @return int
 */
public int getTaskID() {
	return fieldTaskID;
}


/**
 * Insert the method's description here.
 * Creation date: (2/2/2004 12:00:50 PM)
 * @return java.util.Date
 */
public java.util.Date getTimeDateStamp() {
	return fieldTimeDateStamp;
}

/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:15:48 AM)
 * @return cbit.sql.KeyValue
 */
public VCSimulationIdentifier getVCSimulationIdentifier() {
	return fieldVCSimID;
}


/**
 * Insert the method's description here.
 * Creation date: (9/29/2004 9:45:39 AM)
 * @return boolean
 */
public boolean hasData() {
	if (fieldSimulationExecutionStatus == null) {
		return false;
	}
	return fieldSimulationExecutionStatus.hasData();
}


/**
 * Insert the method's description here.
 * Creation date: (2/2/2004 12:00:50 PM)
 * @param newFieldTimeDateStamp java.util.Date
 */
public void setTimeDateStamp(java.util.Date newFieldTimeDateStamp) {
	fieldTimeDateStamp = newFieldTimeDateStamp;
}


/**
 * Insert the method's description here.
 * Creation date: (5/29/2003 10:01:05 AM)
 * @return java.lang.String
 */
public String toString() {
	return "SimulationJobStatus[" + fieldVCSimID + ",status=" + fieldSchedulerStatus + ",job=" + fieldJobIndex + ",task=" + fieldTaskID + "," + fieldSimulationMessage 
	+ ",execStatus=" + fieldSimulationExecutionStatus+"]";
}

public static HtcJobID fromDatabase(String databaseString){
	String PBS_Prefix = HtcJobID.BatchSystemType.PBS.name()+":";
	String SGE_Prefix = HtcJobID.BatchSystemType.SGE.name()+":";
	String SLURM_Prefix = HtcJobID.BatchSystemType.SLURM.name()+":";
	if (databaseString.startsWith(PBS_Prefix)){
		return new PbsJobID(databaseString.substring(PBS_Prefix.length()));
	}else if (databaseString.startsWith(SLURM_Prefix)){
		return new SlurmJobID(databaseString.substring(SLURM_Prefix.length()));
	}else if (databaseString.startsWith(SGE_Prefix)){
		return new SgeJobID(databaseString.substring(SGE_Prefix.length()));
	}else {
		return new PbsJobID(databaseString);
	}
}
}

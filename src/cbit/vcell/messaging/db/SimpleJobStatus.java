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
import cbit.vcell.message.server.htc.HtcJobID;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.solver.VCSimulationIdentifier;
import java.math.BigDecimal;

import org.vcell.util.ComparableObject;
import org.vcell.util.document.KeyValue;

import cbit.vcell.solver.SolverTaskDescription;

/**
 * Insert the type's description here.
 * Creation date: (9/3/2003 10:39:26 AM)
 * @author: Fei Gao
 */
public class SimpleJobStatus implements ComparableObject {
	private String simname = null;
	private String userID = null;
	private SimulationJobStatus jobStatus = null;
	private SolverTaskDescription solverTaskDesc = null;
	private Long elapsedTime = null;
	private Integer meshSpecX = null;
	private Integer meshSpecY= null;
	private Integer meshSpecZ = null;
	private Integer scanCount = null;
	private BioModelLink bioModelLink = null;
	private MathModelLink mathModelLink = null;
	
	public static class BioModelLink {
		public final static String bmid = "bioModelKey";
		public final static String bmbranch = "bioModelBranchId";
		public final static String bmname = "bioModelName";
		public final static String scid = "simContextKey";
		public final static String scbranch = "simContextBranchId";
		public final static String scname = "simContextName";
		
		public String bioModelKey;
		public String bioModelBranchId;
		public final String bioModelName;
		public String simContextKey;
		public String simContextBranchId;
		public final String simContextName;
		
		public BioModelLink(String bioModelKey, String bioModelBranchId, String bioModelName, String simContextKey, String simContextBranchId, String simContextName) {
			this.bioModelKey = bioModelKey;
			this.bioModelBranchId = bioModelBranchId;
			this.bioModelName = bioModelName;
			this.simContextKey = simContextKey;
			this.simContextBranchId = simContextBranchId;
			this.simContextName = simContextName;
		}

		public String getBioModelKey() {
			return bioModelKey;
		}

		public String getBioModelBranchId() {
			return bioModelBranchId;
		}

		public String getBioModelName() {
			return bioModelName;
		}

		public String getSimContextKey() {
			return simContextKey;
		}

		public String getSimContextBranchId() {
			return simContextBranchId;
		}

		public String getSimContextName() {
			return simContextName;
		}
		
		public void clearZeroPadding(){
			while (bioModelKey.startsWith("0") && bioModelKey.length()>1){
				bioModelKey = bioModelKey.substring(1);
			}
			while (bioModelBranchId.startsWith("0") && bioModelBranchId.length()>1){
				bioModelBranchId = bioModelBranchId.substring(1);
			}
			while (simContextKey.startsWith("0") && simContextKey.length()>1){
				simContextKey = simContextKey.substring(1);
			}
			while (simContextBranchId.startsWith("0") && simContextBranchId.length()>1){
				simContextBranchId = simContextBranchId.substring(1);
			}
		}
		
	}

	public static class MathModelLink {
		public final static String mmid = "mathModelKey";
		public final static String mmbranch = "mathModelBranchId";
		public final static String mmname = "mathModelName";
		
		public String mathModelKey;
		public String mathModelBranchId;
		public final String mathModelName;
		
		public MathModelLink(String mathModelKey, String mathModelBranchId, String mathModelName) {
			this.mathModelKey = mathModelKey;
			this.mathModelBranchId = mathModelBranchId;
			this.mathModelName = mathModelName;
		}

		public String getMathModelKey() {
			return mathModelKey;
		}

		public String getMathModelBranchId() {
			return mathModelBranchId;
		}

		public String getMathModelName() {
			return mathModelName;
		}
		
		public void clearZeroPadding(){
			while (mathModelKey.startsWith("0") && mathModelKey.length()>1){
				mathModelKey = mathModelKey.substring(1);
			}
			while (mathModelBranchId.startsWith("0") && mathModelBranchId.length()>1){
				mathModelBranchId = mathModelBranchId.substring(1);
			}
		}
		
	}

/**
 * SimpleJobStatus constructor comment.
 */
public SimpleJobStatus(String simname, String user, SimulationJobStatus arg_jobStatus, SolverTaskDescription arg_solverTaskDesc, Integer meshSpecX, Integer meshSpecY, Integer meshSpecZ, Integer scanCount, BioModelLink bioModelLink, MathModelLink mathModelLink) {	
	super();
	this.simname = simname;
	this.userID = user;
	this.jobStatus = arg_jobStatus;
	this.solverTaskDesc = arg_solverTaskDesc;
	this.elapsedTime = null;
	if (getStartDate()!=null){
		if (getEndDate()!=null){
			this.elapsedTime = ((getEndDate().getTime()-getStartDate().getTime()));
		}else if (jobStatus.getSchedulerStatus().isRunning()){
			this.elapsedTime = ((System.currentTimeMillis()-getStartDate().getTime()));
		}
	}
	this.meshSpecX = meshSpecX;
	this.meshSpecY = meshSpecY;
	this.meshSpecZ = meshSpecZ;
	this.scanCount = scanCount;
	this.bioModelLink = bioModelLink;
	this.mathModelLink = mathModelLink;
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.lang.String
 */
public java.lang.String getComputeHost() {
	if (jobStatus == null) {
		return null;
	}	
	return jobStatus.getComputeHost();
}

public SchedulerStatus getSchedulerStatus(){
	return jobStatus.getSchedulerStatus();
}

public boolean hasData(){
	return jobStatus.hasData();
}

public String getSimName(){
	return this.simname;
}

public BioModelLink getBioModelLink(){
	return this.bioModelLink;
}

public MathModelLink getMathModelLink(){
	return this.mathModelLink;
}

/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.util.Date
 */
public java.util.Date getEndDate() {
	if (jobStatus == null) {
		return null;
	}
	return jobStatus.getEndDate();
}

public Integer getMeshSpecX(){
	return this.meshSpecX;
}

public Integer getMeshSpecY(){
	return this.meshSpecY;
}

public Integer getMeshSpecZ(){
	return this.meshSpecZ;
}

/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.lang.String
 */
public Integer getJobIndex() {
	if (jobStatus == null || jobStatus.getServerID() == null) {
		return null;
	}	
	return new Integer(jobStatus.getJobIndex());
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.lang.String
 */
public java.lang.String getServerID() {
	if (jobStatus == null || jobStatus.getServerID() == null) {
		return null;
	}	
	return jobStatus.getServerID().toString();
}


/**
 * Insert the method's description here.
 * Creation date: (7/8/2004 1:29:11 PM)
 * @return java.lang.String
 */
public String getSolverDescriptionVCML() {
	if (solverTaskDesc == null) {
		return "Error: Null Solver Description";
	}
	return solverTaskDesc.getVCML();
}

public String getMeshSampling(){
	if (this.meshSpecX==null){
		return "no mesh";
	}else if (this.meshSpecY!=null){
		if (this.meshSpecZ!=null){
			return "mesh ("+meshSpecX.intValue()+","+meshSpecY.intValue()+","+meshSpecZ.intValue()+") = "+getMeshSize()+" volume elements";
		}else{
			return "mesh ("+meshSpecX.intValue()+","+meshSpecY.intValue()+") = "+getMeshSize()+" volume elements";
		}
	}else{
		return "mesh ("+meshSpecX.intValue()+") = "+getMeshSize()+" volume elements";
	}
}

public long getMeshSize(){
	if (meshSpecX!=null){
		long size = meshSpecX.intValue();
		if (meshSpecY!=null){
			size *= meshSpecY.intValue();
		}
		if (meshSpecZ!=null){
			size *= meshSpecZ.intValue();
		}
		return size;
	}else{
		return 0;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.util.Date
 */
public java.util.Date getStartDate() {
	if (jobStatus == null) {
		return null;
	}
	return jobStatus.getStartDate();
}

/**
 * Insert the method's description here.
 * Creation date: (12/17/2003 2:47:11 PM)
 * @return java.lang.String
 */
public String getStatusMessage() {
	return jobStatus.getSimulationMessage().getDisplayMessage();
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.util.Date
 */
public java.util.Date getSubmitDate() {
	return jobStatus.getSubmitDate();
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.lang.String
 */
public Integer getTaskID() {
	if (jobStatus == null || jobStatus.getServerID() == null) {
		return null;
	}	
	return new Integer(jobStatus.getTaskID());
}


/**
 * Insert the method's description here.
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return java.lang.String
 */
public java.lang.String getUserID() {
	return userID;
}


/**
 * Insert the method's description here.
 * Creation date: (12/17/2003 2:54:17 PM)
 * @return cbit.sql.KeyValue
 */
public VCSimulationIdentifier getVCSimulationIdentifier() {
	return jobStatus.getVCSimulationIdentifier();
}


/**
 * Insert the method's description here.
 * Creation date: (7/19/2004 3:21:23 PM)
 * @return boolean
 */
public boolean isDone() {
	return jobStatus.getSchedulerStatus().isDone();
}


/**
 * Insert the method's description here.
 * Creation date: (5/7/2004 8:53:02 AM)
 * @return boolean
 */
public boolean isRunning() {
	return jobStatus.getSchedulerStatus().isRunning();
}


/**
 * Insert the method's description here.
 * Creation date: (9/3/2003 10:45:39 AM)
 * @return java.lang.String[]
 */
public Object[] toObjects() {	
	return new Object[] {(bioModelLink!=null)?("BM \""+bioModelLink.bioModelName+"\", APP \""+bioModelLink.simContextName+"\", SIM \""+simname+"\""):((mathModelLink!=null)?("MM \""+mathModelLink.mathModelName+"\", SIM \""+simname+"\""):("")), userID,  new BigDecimal(getVCSimulationIdentifier().getSimulationKey().toString()), getJobIndex(),getScanCount(),  
		solverTaskDesc == null || solverTaskDesc.getSolverDescription() == null ? "" : solverTaskDesc.getSolverDescription().getDisplayLabel(), 		
		"<"+getSchedulerStatus().getDescription()+"> "+getStatusMessage(), getComputeHost(), getServerID(), getTaskID(), getSubmitDate(), getStartDate(), getEndDate(),
		elapsedTime, new Long(getMeshSize())};
}


public HtcJobID getHtcJobID() {
	return jobStatus.getSimulationExecutionStatus().getHtcJobID();
}


public Integer getScanCount() {
	return scanCount;
}

}

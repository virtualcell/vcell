package cbit.vcell.messaging;
import cbit.util.ComparableObject;
import cbit.util.KeyValue;
import cbit.vcell.simulation.SolverTaskDescription;
import cbit.vcell.simulation.VCSimulationIdentifier;

import java.math.BigDecimal;
import java.util.Date;

import cbit.vcell.messaging.db.SimulationJobStatus;

/**
 * Insert the type's description here.
 * Creation date: (9/3/2003 10:39:26 AM)
 * @author: Fei Gao
 */
public class SimpleJobStatus implements ComparableObject {
	private String userID = null;
	private SimulationJobStatus jobStatus = null;
	private SolverTaskDescription solverTaskDesc = null;

/**
 * SimpleJobStatus constructor comment.
 */
public SimpleJobStatus(String user, SimulationJobStatus arg_jobStatus, SolverTaskDescription arg_solverTaskDesc) {	
	super();
	this.userID = user;
	this.jobStatus = arg_jobStatus;
	this.solverTaskDesc = arg_solverTaskDesc;
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
 * Creation date: (3/29/2004 2:09:31 PM)
 * @return int
 */
private int getStatus() {
	return jobStatus.getSchedulerStatus();
}


/**
 * Insert the method's description here.
 * Creation date: (12/17/2003 2:47:11 PM)
 * @return java.lang.String
 */
public String getStatusMessage() {
	return jobStatus.getStatusMessage();
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
	return jobStatus.isDone();
}


/**
 * Insert the method's description here.
 * Creation date: (5/7/2004 8:53:02 AM)
 * @return boolean
 */
public boolean isRunning() {
	return jobStatus.isRunning();
}


/**
 * Insert the method's description here.
 * Creation date: (9/3/2003 10:45:39 AM)
 * @return java.lang.String[]
 */
public Object[] toObjects() {	
	return new Object[] {userID,  new BigDecimal(getVCSimulationIdentifier().getSimulationKey().toString()), 
		solverTaskDesc == null || solverTaskDesc.getSolverDescription() == null ? "" : solverTaskDesc.getSolverDescription().getName(), 		
		SimulationJobStatus.getSchedulerStatusMessage(getStatus()), getComputeHost(), getServerID(), getTaskID(), getSubmitDate(), getStartDate(), getEndDate()};
}
}
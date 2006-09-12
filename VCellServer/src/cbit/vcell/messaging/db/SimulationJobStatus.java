package cbit.vcell.messaging.db;
import cbit.vcell.solver.VCSimulationIdentifier;
import java.util.Date;
import java.io.Serializable;

/**
 * Insert the type's description here.
 * Creation date: (1/31/2003 11:02:54 AM)
 * @author: Jim Schaff
 */
public class SimulationJobStatus implements cbit.util.Matchable, Serializable {
	//
	// mandatory fields - not null
	//
	private Date fieldTimeDateStamp = null;
	private VCSimulationIdentifier fieldVCSimID = null;
	private Date fieldSubmitDate = null;
	private int fieldSchedulerStatus; // define here
	private int fieldTaskID = 0;
	private String fieldStatusMessage = null;
	private VCellServerID fieldServerID = null;
	private int fieldJobIndex;

	public static final int SCHEDULERSTATUS_WAITING = 0;
	public static final int SCHEDULERSTATUS_QUEUED = 1;
	public static final int SCHEDULERSTATUS_DISPATCHED = 2;
	public static final int SCHEDULERSTATUS_RUNNING = 3;
	public static final int SCHEDULERSTATUS_COMPLETED = 4;
	public static final int SCHEDULERSTATUS_STOPPED = 5;
	public static final int SCHEDULERSTATUS_FAILED = 6;

	private SimulationQueueEntryStatus fieldSimulationQueueEntryStatus = null;	// may be null
	private SimulationExecutionStatus fieldSimulationExecutionStatus = null;	// may be null
	
	private final static String StatusString[] = {"waiting to be dispatched", "queued...", "dispatched...", 
		"running...", "completed", "stopped", "failed"};

/**
 * SimulationJobStatus constructor comment.
 */
public SimulationJobStatus(VCellServerID serverID, VCSimulationIdentifier vcSimID, int jobIndex, Date submitDate, int schedulerStatus, int taskID, String statusMessage, SimulationQueueEntryStatus simQueueStatus, SimulationExecutionStatus simExeStatus){
	fieldServerID = serverID;
	fieldVCSimID = vcSimID;
	fieldSubmitDate = submitDate;
	fieldSchedulerStatus = schedulerStatus;
	fieldTaskID = taskID;
	setStatusMessage(statusMessage);
	fieldSimulationExecutionStatus = simExeStatus;
	fieldSimulationQueueEntryStatus = simQueueStatus;
	fieldJobIndex = jobIndex;
}


/**
 * Checks for internal representation of objects, not keys from database
 * @return boolean
 * @param obj java.lang.Object
 */
public boolean compareEqual(cbit.util.Matchable obj) {
	if (obj instanceof SimulationJobStatus){
		SimulationJobStatus jobStatus = (SimulationJobStatus)obj;
		if (jobStatus.fieldSchedulerStatus != fieldSchedulerStatus){
			//System.out.println("getSchedulerStatus() not =");
			return false;
		}
		if (!jobStatus.fieldVCSimID.equals(fieldVCSimID)){
			//System.out.println("fieldSimKey not =");
			return false;
		}
		if (jobStatus.getSimulationExecutionStatus() != null && fieldSimulationExecutionStatus != null && 
				!jobStatus.getSimulationExecutionStatus().compareEqual(getSimulationExecutionStatus())){
			//System.out.println("getSimulationExecutionStatus() not =");
			return false;
		}
		if (jobStatus.fieldSimulationQueueEntryStatus != null && fieldSimulationQueueEntryStatus != null && 
				!jobStatus.getSimulationQueueEntryStatus().compareEqual(getSimulationQueueEntryStatus())){
			//System.out.println("getSimulationQueueEntryStatus() not =");
			return false;
		}
		if (fieldStatusMessage != null && jobStatus.fieldStatusMessage != null && !jobStatus.fieldStatusMessage.equals(fieldStatusMessage)){
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
public int getSchedulerStatus() {
	return fieldSchedulerStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (2/5/2004 7:59:05 AM)
 * @return java.lang.String
 */
public static java.lang.String getSchedulerStatusMessage(int status) {
	return StatusString[status];
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
public SimulationExecutionStatus getSimulationExecutionStatus() {
	return fieldSimulationExecutionStatus;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:31:57 AM)
 * @return cbit.vcell.solvers.SimulationQueueEntryStatus
 */
public SimulationQueueEntryStatus getSimulationQueueEntryStatus() {
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
public java.lang.String getStatusMessage() {
	return fieldStatusMessage;
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
 * Creation date: (1/31/2003 11:15:48 AM)
 * @return int
 */
public boolean inQueue() {
	return (fieldSchedulerStatus == SCHEDULERSTATUS_QUEUED || fieldSchedulerStatus == SCHEDULERSTATUS_WAITING) ? true : false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/11/2006 10:13:18 AM)
 * @return boolean
 */
public boolean isActive() {
	if (isRunning() || isWaiting() || isQueued() || isDispatched()) {
		return true;
	}
	
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:15:48 AM)
 * @return int
 */
public boolean isCompleted() {
	return  fieldSchedulerStatus == SCHEDULERSTATUS_COMPLETED ? true : false;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:15:48 AM)
 * @return int
 */
public boolean isDispatched() {
	return  fieldSchedulerStatus == SCHEDULERSTATUS_DISPATCHED ? true : false;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:15:48 AM)
 * @return int
 */
public boolean isDone() {
	return  (fieldSchedulerStatus == SCHEDULERSTATUS_STOPPED || fieldSchedulerStatus == SCHEDULERSTATUS_FAILED 
		|| fieldSchedulerStatus == SCHEDULERSTATUS_COMPLETED) ? true : false;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:15:48 AM)
 * @return int
 */
public boolean isFailed() {
	return  fieldSchedulerStatus == SCHEDULERSTATUS_FAILED ? true : false;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:15:48 AM)
 * @return int
 */
public boolean isQueued() {
	return  (fieldSchedulerStatus == SCHEDULERSTATUS_QUEUED) ? true : false;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:15:48 AM)
 * @return int
 */
public boolean isRunning() {
	return  fieldSchedulerStatus == SCHEDULERSTATUS_RUNNING ? true : false;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:15:48 AM)
 * @return int
 */
public boolean isStopped() {
	return  fieldSchedulerStatus == SCHEDULERSTATUS_STOPPED ? true : false;
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2003 11:15:48 AM)
 * @return int
 */
public boolean isWaiting() {
	return  (fieldSchedulerStatus == SCHEDULERSTATUS_WAITING) ? true : false;
}


/**
 * Insert the method's description here.
 * Creation date: (2/2/2004 12:00:50 PM)
 * @param newFieldTimeDateStamp java.util.Date
 */
private void setStatusMessage(String statusMsg) {
	fieldStatusMessage = (statusMsg != null) ? statusMsg : StatusString[fieldSchedulerStatus];
	if (fieldStatusMessage.length() > 2048) {
		fieldStatusMessage = fieldStatusMessage.substring(0, 2048);
	}
	if (fieldStatusMessage != null) {
		fieldStatusMessage = fieldStatusMessage.replace('\r', ' ');
		fieldStatusMessage = fieldStatusMessage.replace('\n', ' ');
		fieldStatusMessage = fieldStatusMessage.replace('\'', ' ');
	}
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
	return "SimulationJobStatus[" + fieldVCSimID + "," + fieldJobIndex + "," + fieldStatusMessage + "," + fieldTaskID + "]";
}
}
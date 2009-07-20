package cbit.vcell.messaging.db;
import java.util.Vector;
import cbit.vcell.messaging.db.*;
import java.text.*;
import cbit.vcell.solver.*;
import cbit.sql.*;
import cbit.image.*;
import cbit.vcell.server.*;
import java.sql.*;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.modeldb.*;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
 * This type was created in VisualAge.
 ©*/
public class SimulationJobTable extends cbit.sql.Table {
	private static final String TABLE_NAME = "vc_simulationjob";
	public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(" + Table.id_ColumnName + ")";

	public final Field simRef 			= new Field("simRef",			"integer",		"UNIQUE NOT NULL " + SimulationTable.REF_TYPE + " ON DELETE CASCADE");
	public final Field submitDate		= new Field("submitDate",		"date",			"NOT NULL");
	public final Field taskID			= new Field("taskID",			"integer",		"NOT NULL");		
	public final Field schedulerStatus	= new Field("schedulerStatus",	"integer",		"NOT NULL");
	public final Field statusMsg		= new Field("statusMsg",		"varchar(2048)",	"");	
		
	public final Field queueDate		= new Field("queueDate",		"date",			"");
	public final Field queuePriority	= new Field("queuePriority",	"integer",		"");	
	public final Field queueID			= new Field("queueID",			"integer",		"");	

	public final Field startDate		= new Field("startDate",		"date",			"");
	public final Field computeHost		= new Field("computeHost",		"varchar(255)",	"");
	public final Field latestUpdateDate = new Field("latestUpdateDate",	"date",			"NOT NULL");
	public final Field endDate			= new Field("endDate",			"date",			"");	
	public final Field hasData			= new Field("hasData",			"char(1)",		"");

	public final Field serverID			= new Field("serverID",			"varchar(20)",	"NOT NULL");
	public final Field jobIndex			= new Field("jobIndex",			"integer",		"");	
	
	private final Field fields[] = {simRef,submitDate, taskID, schedulerStatus, statusMsg,
		queueDate,queuePriority,queueID, 
		startDate, computeHost, latestUpdateDate, endDate, hasData, serverID, jobIndex};
	
	public static final SimulationJobTable table = new SimulationJobTable();

/**
 * ModelTable constructor comment.
 */
private SimulationJobTable() {
	super(TABLE_NAME);
	addFields(fields);
}


/**
 * This method was created in VisualAge.
 * @return VCImage
 * @param rset ResultSet
 * @param log SessionLog
 */
public SimulationJobStatus getSimulationJobStatus(ResultSet rset) throws SQLException {
	//serverid
	String serID = rset.getString(this.serverID.toString());
	if (rset.wasNull()) {
		serID = null;
	}
	
	//simRef
	KeyValue parsedSimKey = new KeyValue(rset.getBigDecimal(simRef.toString()));
	//userKey
	KeyValue userKey = new KeyValue(rset.getBigDecimal(SimulationTable.table.ownerRef.toString()));
	//userKey
	String userid = rset.getString(UserTable.table.userid.toString());
	org.vcell.util.document.User owner = new org.vcell.util.document.User(userid,userKey);
	//submitDate
	java.util.Date parsedSubmitDate = rset.getTimestamp(submitDate.toString());
	//taskID
	int parsedTaskID = rset.getInt(taskID.toString());
	//schedulerStatus
	int parsedSchedulerStatus = rset.getInt(schedulerStatus.toString());	
	//statusMsg
	String parsedStatusMsg = rset.getString(statusMsg.toString());
	
	SimulationMessage simulationMessage = SimulationMessage.fromSerialized(parsedSchedulerStatus,parsedStatusMsg);
	//
	// read queue stuff
	//
	//queueDate
	java.util.Date parsedQueuedDate = rset.getTimestamp(queueDate.toString());
	//queuePriority
	int parsedQueuePriority = rset.getInt(queuePriority.toString());
	if (rset.wasNull()) {
		parsedQueuePriority = -1;
	}
	//queueID	
	int parsedQueueID = rset.getInt(queueID.toString());
	if (rset.wasNull()) {
		parsedQueueID = -1;
	}
	SimulationQueueEntryStatus simQueueEntryStatus = new SimulationQueueEntryStatus(parsedQueuedDate,parsedQueuePriority,parsedQueueID);	
	
	//
	// read solver stuff
	//
	//startDate
	java.util.Date parsedStartDate = rset.getTimestamp(startDate.toString());
	//computeHost
	String parsedComputeHost = rset.getString(computeHost.toString());
	//latestUpdateDate
	java.util.Date parsedLatestUpdateDate = rset.getTimestamp(latestUpdateDate.toString());
	//endDate
	java.util.Date parsedEndDate = rset.getTimestamp(endDate.toString());
	//hasData
	String parsedHasData = rset.getString(hasData.toString());
	
	SimulationExecutionStatus simExeStatus = new SimulationExecutionStatus(parsedStartDate, parsedComputeHost, parsedLatestUpdateDate, parsedEndDate,parsedHasData != null);

	VCSimulationIdentifier parsedVCSimID = new VCSimulationIdentifier(parsedSimKey,owner);
	//jobIndex
	int parsedJobIndex = rset.getInt(jobIndex.toString());
	SimulationJobStatus simulationJobStatus = new SimulationJobStatus(VCellServerID.getServerID(serID), parsedVCSimID, parsedJobIndex, parsedSubmitDate,parsedSchedulerStatus,parsedTaskID, simulationMessage, simQueueEntryStatus, simExeStatus);
	//sysDate
	java.util.Date parsedSysDate = rset.getTimestamp(DatabaseConstants.SYSDATE_COLUMN_NAME);
	if (!rset.wasNull()) {
		simulationJobStatus.setTimeDateStamp(parsedSysDate);
	}
	
	return simulationJobStatus;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLUpdateList(SimulationJobStatus simulationJobStatus){

	StringBuffer buffer = new StringBuffer();

	//
	// basic info
	//
	//simRef
	buffer.append(simRef + "=" + simulationJobStatus.getVCSimulationIdentifier().getSimulationKey() + ",");
	//submiteDate
	buffer.append(submitDate + "=" + (simulationJobStatus.getSubmitDate() == null ? "sysdate," : VersionTable.formatDateToOracle(simulationJobStatus.getSubmitDate()) + ","));
	//taskID
	buffer.append(taskID + "=" + simulationJobStatus.getTaskID() + ",");
	//schedulerStatus
	buffer.append(schedulerStatus + "=" + simulationJobStatus.getSchedulerStatus() + ",");
	//statusMsg
	buffer.append(statusMsg + "='" + simulationJobStatus.getSimulationMessage().toSerialization() + "',");

	//
	// queue info
	//
	SimulationQueueEntryStatus simQueueEntryStatus = simulationJobStatus.getSimulationQueueEntryStatus();
	//queueDate
	buffer.append(queueDate + "=");
	if (simQueueEntryStatus != null && simQueueEntryStatus.getQueueDate() != null){
		buffer.append(VersionTable.formatDateToOracle(simQueueEntryStatus.getQueueDate()) + ",");
	} else {
		if (simulationJobStatus.inQueue()) {
			buffer.append("sysdate,");
		} else {
			buffer.append("null,");
		}
	}	
	//queuePriority
	buffer.append(queuePriority + "=");
	if (simQueueEntryStatus != null){
		buffer.append(simQueueEntryStatus.getQueuePriority() + ",");
	} else {
		buffer.append("null,");
	}
	//queueID
	buffer.append(queueID + "=");
	if (simQueueEntryStatus != null){
		buffer.append(simQueueEntryStatus.getQueueID() + ",");
	} else {
		buffer.append("null,");
	}
	
	//
	// execution status
	//
	SimulationExecutionStatus simExecutionStatus = simulationJobStatus.getSimulationExecutionStatus();	
	//startDate
	buffer.append(startDate + "=");
	if (simExecutionStatus != null && simExecutionStatus.getStartDate() != null){
		buffer.append(VersionTable.formatDateToOracle(simExecutionStatus.getStartDate()) + ",");
	} else {	
		if (simulationJobStatus.isWaiting()) {	
			buffer.append("null,");
		} else {
			buffer.append("sysdate,");
		}
	}
	//computeHost
	buffer.append(computeHost + "=");
	if (simExecutionStatus != null && simExecutionStatus.getComputeHost() != null){
		buffer.append("'" + simExecutionStatus.getComputeHost().toLowerCase() + "',");
	} else {
		buffer.append("null,");
	}
	//latestUpdateDate
	buffer.append(latestUpdateDate + "=sysdate,");

	//endDate
	buffer.append(endDate + "=");
	if (simExecutionStatus != null && simExecutionStatus.getEndDate() != null){
		buffer.append(VersionTable.formatDateToOracle(simExecutionStatus.getEndDate()) + ",");
	} else {
		if (simulationJobStatus.isDone()) {
			buffer.append("sysdate,");
		} else {
			buffer.append("null,");
		}
	}
	//hasData
	buffer.append(hasData + "=");
	if (simExecutionStatus != null && simExecutionStatus.hasData()){
		buffer.append("'Y',");
	}else{
		buffer.append("null,");
	}

	//serverID
	buffer.append(serverID + "=");
	if (simulationJobStatus.getServerID() != null) {
		buffer.append("'" + simulationJobStatus.getServerID() + "',");
	} else {
		buffer.append("null,");
	}

	//jobIndex
	buffer.append(jobIndex + "=");
	buffer.append(simulationJobStatus.getJobIndex());
	
	return buffer.toString();
}


/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param key KeyValue
 * @param modelName java.lang.String
 */
public String getSQLValueList(KeyValue key, SimulationJobStatus simulationJobStatus) {

	StringBuffer buffer = new StringBuffer();
	
	buffer.append("(");
	buffer.append(key + ",");
	//simRef
	buffer.append(simulationJobStatus.getVCSimulationIdentifier().getSimulationKey() + ",");
	//submitDate
	if (simulationJobStatus.getSubmitDate() == null) {
		buffer.append("sysdate,");
	} else {
		buffer.append(VersionTable.formatDateToOracle(simulationJobStatus.getSubmitDate()) + ",");
	}
		
	//taskID
	buffer.append(simulationJobStatus.getTaskID() + ",");
	//schedulerStatus
	buffer.append(simulationJobStatus.getSchedulerStatus() + ",");
	//statusMsg
	buffer.append("'" + simulationJobStatus.getSimulationMessage().toSerialization() + "',");
	
	// queue stuff
	SimulationQueueEntryStatus simQueueEntryStatus = simulationJobStatus.getSimulationQueueEntryStatus();
	//queueDate
	if (simulationJobStatus.inQueue()) {
		buffer.append("sysdate,");
	} else {		
		if (simQueueEntryStatus != null && simQueueEntryStatus.getQueueDate() != null) {
			buffer.append(VersionTable.formatDateToOracle(simQueueEntryStatus.getQueueDate()) + ",");
		} else {
			buffer.append("null,");
		}
	}
		
	if (simQueueEntryStatus == null){
		buffer.append("null,null,");
	} else {
		//queuePriority
		buffer.append(simQueueEntryStatus.getQueuePriority() + ",");
		//queueID
		buffer.append(simQueueEntryStatus.getQueueID() + ",");
	}
	
	// execution stuff
	SimulationExecutionStatus simExecutionStatus = simulationJobStatus.getSimulationExecutionStatus();
	if (simExecutionStatus == null){
		buffer.append("null,null,sysdate,null,null,");
	} else {
		//startDate
		if (simExecutionStatus.getStartDate() != null) {
			buffer.append(VersionTable.formatDateToOracle(simExecutionStatus.getStartDate()) + ",");
		} else {
			if (simulationJobStatus.getSchedulerStatus() == SimulationJobStatus.SCHEDULERSTATUS_COMPLETED) {
				buffer.append("sysdate,");
			} else {
				buffer.append("null,");
			}
		}
		//computeHost
		if (simExecutionStatus.getComputeHost() != null){
			buffer.append("'" + simExecutionStatus.getComputeHost().toLowerCase() + "',");
		} else {
			buffer.append("null,");
		}
		//LatestUpdateDate
		buffer.append("sysdate,");
		//endDate
		if (simExecutionStatus.getEndDate() != null) {
			buffer.append(VersionTable.formatDateToOracle(simExecutionStatus.getEndDate()) + ",");
		} else {
			if (simulationJobStatus.isDone()) {
				buffer.append("sysdate,");
			} else {
				buffer.append("null,");
			}
		}
		//hasData
		buffer.append(simExecutionStatus.hasData()? "'Y'," : "null,");		
	}	
	buffer.append(simulationJobStatus.getServerID() == null? "null," : "'" + simulationJobStatus.getServerID() + "',");
	buffer.append(simulationJobStatus.getJobIndex());
	buffer.append(")");
	
	return buffer.toString();
}
}
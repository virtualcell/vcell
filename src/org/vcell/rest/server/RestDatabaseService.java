package org.vcell.rest.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import cbit.vcell.messaging.db.SimpleJobStatus;
import cbit.vcell.messaging.db.SimulationJobTable;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.modeldb.BioModelTable;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.UserTable;

public class RestDatabaseService {
	
	private DatabaseServerImpl databaseServerImpl = null;
	private AdminDBTopLevel adminDbTopLevel = null;

	public RestDatabaseService(DatabaseServerImpl databaseServerImpl, AdminDBTopLevel adminDbTopLevel) {
		this.databaseServerImpl = databaseServerImpl;
		this.adminDbTopLevel = adminDbTopLevel;
	}

	public BioModelRep[] query(BiomodelsServerResource resource, User vcellUser) throws SQLException, DataAccessException {	
		
		Long bioModelID = resource.getLongQueryValue(BiomodelsServerResource.PARAM_BM_ID);
		Long savedLow = resource.getLongQueryValue(BiomodelsServerResource.PARAM_SAVED_LOW);
		Long savedHigh = resource.getLongQueryValue(BiomodelsServerResource.PARAM_SAVED_HIGH);
		Long maxRowsParam = resource.getLongQueryValue(BiomodelsServerResource.PARAM_MAX_ROWS);
		int maxRows = 10; // default
		if (maxRowsParam!=null){
			maxRows = maxRowsParam.intValue();
		}
		ArrayList<String> conditions = new ArrayList<String>();
		
		java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", java.util.Locale.US);
		
		if (savedLow != null){
			conditions.add("(" + BioModelTable.table.versionDate.getQualifiedColName() + " >= to_date('" + df.format(new Date(savedLow)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
		}
		if (savedHigh != null){
			conditions.add("(" + BioModelTable.table.versionDate.getQualifiedColName() + " <= to_date('" + df.format(new Date(savedHigh)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
		}
		if (bioModelID != null){
			conditions.add("(" + BioModelTable.table.id.getQualifiedColName() + " = " + bioModelID + ")");		
		}
	
		StringBuffer conditionsBuffer = new StringBuffer();
		for (String condition : conditions) {
			if (conditionsBuffer.length() > 0) {
				conditionsBuffer.append(" AND ");
			}
			conditionsBuffer.append(condition);
		}
		BioModelRep[] bioModelReps = databaseServerImpl.getBioModelReps(vcellUser, conditionsBuffer.toString(), maxRows);
	   	return bioModelReps;
	}

    public List<SimpleJobStatus> query(SimulationTasksServerResource resource, User vcellUser) throws SQLException, DataAccessException {	
    	
		String userID = vcellUser.getName();
		Long simid = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_SIM_ID);
		Long jobid = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_JOB_ID);
		Long taskid = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_TASK_ID);
		String computeHost = resource.getQueryValue(SimulationTasksServerResource.PARAM_COMPUTE_HOST);
		String serverID = resource.getQueryValue(SimulationTasksServerResource.PARAM_SERVER_ID);
		String hasData = resource.getQueryValue(SimulationTasksServerResource.PARAM_HAS_DATA);
		boolean statusWaiting = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_WAITING,false);
		boolean statusQueued = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_QUEUED,false);
		boolean statusDispatched = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_DISPATCHED,false);
		boolean statusRunning = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_RUNNING,false);
		boolean statusCompleted = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_COMPLETED,false);
		boolean statusFailed = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_FAILED,false);
		boolean statusStopped = resource.getBooleanQueryValue(SimulationTasksServerResource.PARAM_STATUS_STOPPED,false);
		Long submitLow = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_SUBMIT_LOW);
		Long submitHigh = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_SUBMIT_HIGH);
		Long startLow = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_START_LOW);
		Long startHigh = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_START_HIGH);
		Long endLow = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_END_LOW);
		Long endHigh = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_END_HIGH);
		Long maxRowsParam = resource.getLongQueryValue(SimulationTasksServerResource.PARAM_MAX_ROWS);
		int maxRows = 10; // default
		if (maxRowsParam!=null){
			maxRows = maxRowsParam.intValue();
		}
    	ArrayList<String> conditions = new ArrayList<String>();
    	
    	if (simid!=null){
   			conditions.add(SimulationJobTable.table.simRef.getQualifiedColName() + "=" + simid);
     	}

    	if (jobid!=null){
   			conditions.add(SimulationJobTable.table.jobIndex.getQualifiedColName() + "=" + jobid);
     	}

    	if (taskid!=null){
   			conditions.add(SimulationJobTable.table.taskID.getQualifiedColName() + "=" + taskid);
     	}

    	if (computeHost != null && computeHost.length()>0){
     		conditions.add("lower(" + SimulationJobTable.table.computeHost.getQualifiedColName() + ")='" + computeHost.toLowerCase() + "'");
    	}

    	if (serverID!=null && serverID.length()>0){
    		conditions.add("lower(" + SimulationJobTable.table.serverID.getQualifiedColName() + ")='" + serverID + "'");
    	}
    	
    	if (hasData!=null){
    		if (hasData.equalsIgnoreCase("yes") || hasData.equalsIgnoreCase("y") || hasData.equalsIgnoreCase("true") || hasData.equalsIgnoreCase("t")){
    			// return only records that have data
    			conditions.add("lower(" + SimulationJobTable.table.hasData.getQualifiedColName() + ")='y'");
    		} else if (hasData.equalsIgnoreCase("no") || hasData.equalsIgnoreCase("n") || hasData.equalsIgnoreCase("false") || hasData.equalsIgnoreCase("f")){
    			// return only records that don't have data
    			conditions.add(SimulationJobTable.table.hasData.getQualifiedColName() + " is null");
    		}
    	} // else all records.
    	
    	if (userID!=null && userID.length()>0){
    		conditions.add(UserTable.table.userid.getQualifiedColName() + "='" + userID + "'");
    	}

/**
 * 		w = WAITING(0,"waiting"),
 *		q = QUEUED(1,"queued"),
 *		d = DISPATCHED(2,"dispatched"),
 *		r = RUNNING(3,"running"),
 *		c = COMPLETED(4,"completed"),
 *		s = STOPPED(5,"stopped"),
 *		f = FAILED(6,"failed");
 *
 */
    	ArrayList<String> statusConditions = new ArrayList<String>();
    	if (statusWaiting){
    		statusConditions.add(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.WAITING.getDatabaseNumber());
    	}
    	if (statusQueued){
    		statusConditions.add(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.QUEUED.getDatabaseNumber());
    	}
    	if (statusDispatched){
    		statusConditions.add(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.DISPATCHED.getDatabaseNumber());
    	}
    	if (statusRunning){
    		statusConditions.add(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.RUNNING.getDatabaseNumber());
    	}
    	if (statusCompleted){
    		statusConditions.add(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.COMPLETED.getDatabaseNumber());
    	}
    	if (statusStopped){
    		statusConditions.add(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.STOPPED.getDatabaseNumber());
    	}
    	if (statusFailed){
    		statusConditions.add(SimulationJobTable.table.schedulerStatus.getQualifiedColName() + "=" + SchedulerStatus.FAILED.getDatabaseNumber());
    	}
    	if (statusConditions.size()>0){
	       	StringBuffer statusConditionsBuffer = new StringBuffer();
	    	for (String statusCondition : statusConditions) {
	    		if (statusConditionsBuffer.length() > 0) {
	    			statusConditionsBuffer.append(" OR ");
	    		}
	    		statusConditionsBuffer.append(statusCondition);
			}
     		conditions.add("(" + statusConditionsBuffer + ")");
    	}
     	
    	java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss", java.util.Locale.US);
    	
    	if (submitLow != null){
    		conditions.add("(" + SimulationJobTable.table.submitDate.getQualifiedColName() + " >= to_date('" + df.format(new Date(submitLow)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (submitHigh != null){
    		conditions.add("(" + SimulationJobTable.table.submitDate.getQualifiedColName() + " <= to_date('" + df.format(new Date(submitHigh)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (startLow != null){
    		conditions.add("(" + SimulationJobTable.table.startDate.getQualifiedColName() + " >= to_date('" + df.format(new Date(startLow)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (startHigh != null){
    		conditions.add("(" + SimulationJobTable.table.startDate.getQualifiedColName() + " <= to_date('" + df.format(new Date(startHigh)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (endLow != null){
    		conditions.add("(" + SimulationJobTable.table.endDate.getQualifiedColName() + " >= to_date('" + df.format(new Date(endLow)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}
    	if (endHigh != null){
    		conditions.add("(" + SimulationJobTable.table.endDate.getQualifiedColName() + " <= to_date('" + df.format(new Date(endHigh)) + "', 'mm/dd/yyyy HH24:MI:SS'))");		
    	}

//    	conditions.add("(" + "rownum" + " <= " + maxNumRows + ")");
    	
    	StringBuffer conditionsBuffer = new StringBuffer();
    	for (String condition : conditions) {
    		if (conditionsBuffer.length() > 0) {
    			conditionsBuffer.append(" AND ");
    		}
			conditionsBuffer.append(condition);
		}
    	
    	if (statusConditions.size()==0){
    		// no status conditions wanted ... nothing to query
    		return new ArrayList<SimpleJobStatus>();
    	}else{
	   		List<SimpleJobStatus> resultList = adminDbTopLevel.getSimulationJobStatus(conditionsBuffer.toString(), maxRows, true);
	   		return resultList;
    	}
    }

}

package org.vcell.rest.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.restlet.data.MediaType;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.RequestInfo;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.common.SimulationTaskRepresentation;
import org.vcell.rest.common.SimulationTasksResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import com.google.gson.Gson;

import cbit.vcell.messaging.db.SimpleJobStatus;
import cbit.vcell.messaging.db.SimulationExecutionStatus;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SimulationQueueID;
import cbit.vcell.messaging.db.SimulationJobTable;
import cbit.vcell.messaging.db.SimulationQueueEntryStatus;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.UserTable;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.VCSimulationIdentifier;

public class SimulationTasksServerResource extends WadlServerResource implements SimulationTasksResource {

    private static final String PARAM_USER = "user";
	private static final String PARAM_SIM_ID = "simId";
	private static final String PARAM_JOB_ID = "jobId";
	private static final String PARAM_TASK_ID = "taskId";
	private static final String PARAM_COMPUTE_HOST = "computeHost";
	private static final String PARAM_SERVER_ID = "serverId";
	private static final String PARAM_STATUS_WAITING = "waiting";
	private static final String PARAM_STATUS_QUEUED = "queued";
	private static final String PARAM_STATUS_DISPATCHED = "dispatched";
	private static final String PARAM_STATUS_RUNNING = "running";
	private static final String PARAM_STATUS_COMPLETED = "completed";
	private static final String PARAM_STATUS_FAILED = "failed";
	private static final String PARAM_STATUS_STOPPED = "stopped";
	private static final String PARAM_END_HIGH = "endHigh";
	private static final String PARAM_END_LOW = "endLow";
	private static final String PARAM_START_HIGH = "startHigh";
	private static final String PARAM_START_LOW = "startLow";
	private static final String PARAM_SUBMIT_HIGH = "submitHigh";
	private static final String PARAM_SUBMIT_LOW = "submitLow";
	private static final String PARAM_MAX_ROWS = "maxRows";

	@Override
    protected void describe(ApplicationInfo applicationInfo) {
        RepresentationInfo rep = new RepresentationInfo(MediaType.APPLICATION_JSON);
        rep.setIdentifier("simulationTask");
        applicationInfo.getRepresentations().add(rep);

        DocumentationInfo doc = new DocumentationInfo();
        doc.setTitle("SimulationTask");
        doc.setTextContent("jdom containing the simulation task status");
        rep.getDocumentations().add(doc);
    }

    @Override
    protected void doInit() throws ResourceException {
        setName("SimulationTask resource");
        setDescription("The resource containing the list of SimulationTasks status");
    }


	@Override
	protected List<Variant> getWadlVariants() {
		ArrayList<Variant> variants = new ArrayList<Variant>();
		variants.add(new Variant(MediaType.APPLICATION_WADL));
		return variants;
	}


	@Override
	protected void describeGet(MethodInfo info) {
		super.describeGet(info);
		RequestInfo requestInfo = new RequestInfo();
        List<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
        parameterInfos.add(new ParameterInfo(PARAM_USER,false,"string",ParameterStyle.TEMPLATE,"VCell user id"));
        parameterInfos.add(new ParameterInfo(PARAM_SIM_ID,false,"string",ParameterStyle.QUERY,"VCell simulation database id"));
        parameterInfos.add(new ParameterInfo(PARAM_JOB_ID,false,"string",ParameterStyle.QUERY,"VCell simulation job id (parameter scan index)"));
        parameterInfos.add(new ParameterInfo(PARAM_TASK_ID,false,"string",ParameterStyle.QUERY,"VCell simulation task id (retry index)"));
        parameterInfos.add(new ParameterInfo(PARAM_COMPUTE_HOST,false,"string",ParameterStyle.QUERY,"cluster node assigned to job"));
        parameterInfos.add(new ParameterInfo(PARAM_SERVER_ID,false,"string",ParameterStyle.QUERY,"VCell site which owns job (e.g. release, beta)"));
        parameterInfos.add(new ParameterInfo(PARAM_STATUS_WAITING,false,"string",ParameterStyle.QUERY,"include jobs status waiting (default=true)"));
        parameterInfos.add(new ParameterInfo(PARAM_STATUS_QUEUED,false,"string",ParameterStyle.QUERY,"include jobs status queued (default=true)"));
        parameterInfos.add(new ParameterInfo(PARAM_STATUS_DISPATCHED,false,"string",ParameterStyle.QUERY,"include jobs status dispatched (default=true)"));
        parameterInfos.add(new ParameterInfo(PARAM_STATUS_RUNNING,false,"string",ParameterStyle.QUERY,"include jobs status running (default=true)"));
        parameterInfos.add(new ParameterInfo(PARAM_STATUS_COMPLETED,false,"string",ParameterStyle.QUERY,"include jobs status completed (default=true)"));
        parameterInfos.add(new ParameterInfo(PARAM_STATUS_STOPPED,false,"string",ParameterStyle.QUERY,"include jobs status stopped (default=true)"));
        parameterInfos.add(new ParameterInfo(PARAM_STATUS_COMPLETED,false,"string",ParameterStyle.QUERY,"include jobs status completed (default=true)"));
        parameterInfos.add(new ParameterInfo(PARAM_SUBMIT_LOW,false,"string",ParameterStyle.QUERY,"earliest submission timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_SUBMIT_HIGH,false,"string",ParameterStyle.QUERY,"latest submission timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_START_LOW,false,"string",ParameterStyle.QUERY,"earliest start timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_START_HIGH,false,"string",ParameterStyle.QUERY,"latest start timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_END_LOW,false,"string",ParameterStyle.QUERY,"earliest end timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_END_HIGH,false,"string",ParameterStyle.QUERY,"latest end timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_MAX_ROWS,false,"string",ParameterStyle.QUERY,"max number of records returned (default is 100)"));
 		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}

	
	@Override
    public SimulationTaskRepresentation[] get_json() {
        return getSimulationTaskRepresentations();
    }
    
	@Override
	public Representation get_html() {
		SimulationTaskRepresentation[] simTasks = getSimulationTaskRepresentations();

		final String FIELD_MODELID		= "Model type:id";
		final String FIELD_SIMKEY		= "SimKey";
		final String FIELD_SIMNAME		= "SimName";
		final String FIELD_USERNAME		= "UserName";
		final String FIELD_USERKEY		= "UserKey";
		final String FIELD_JOBINDEX 	= "Job Index";
		final String FIELD_TASKID 		= "Task ID";
		final String FIELD_HTCJOBID		= "HTC JobID";
		final String FIELD_STATUS 		= "Status";
		final String FIELD_STARTDATE 	= "Start Date";
		final String FIELD_MESSAGE 		= "Message";
		final String FIELD_SITE 		= "Site";
		final String FIELD_HAS_DATA		= "has data";
		final String FIELD_COMPUTEHOST	= "ComputeHost";

		StringBuffer buffer = new StringBuffer();
		buffer.append("<!DOCTYPE html>\n");
		buffer.append("<html>\n");
		buffer.append("<head>\n");
		buffer.append("<title>Simulation Tasks</title>\n");
		buffer.append("</head>\n");
		buffer.append("<body>\n");
		
		buffer.append("<center><h2>Simulation Tasks</h2></center>");
		
		//
		// getting query parameters here so that we can populate query form with current values
		//
		String userID = getAttribute(PARAM_USER);
		Long simid = getLongQueryValue(PARAM_SIM_ID);
		Long jobid = getLongQueryValue(PARAM_JOB_ID);
		Long taskid = getLongQueryValue(PARAM_TASK_ID);
		String computeHost = getQueryValue(PARAM_COMPUTE_HOST);
		String serverID = getQueryValue(PARAM_SERVER_ID);
		boolean statusWaiting = getBooleanQueryValue(PARAM_STATUS_WAITING,false);
		boolean statusQueued = getBooleanQueryValue(PARAM_STATUS_QUEUED,false);
		boolean statusDispatched = getBooleanQueryValue(PARAM_STATUS_DISPATCHED,false);
		boolean statusRunning = getBooleanQueryValue(PARAM_STATUS_RUNNING,false);
		boolean statusCompleted = getBooleanQueryValue(PARAM_STATUS_COMPLETED,false);
		boolean statusFailed = getBooleanQueryValue(PARAM_STATUS_FAILED,false);
		boolean statusStopped = getBooleanQueryValue(PARAM_STATUS_STOPPED,false);
		Long submitLow = getLongQueryValue(PARAM_SUBMIT_LOW);
		Long submitHigh = getLongQueryValue(PARAM_SUBMIT_HIGH);
		Long startLow = getLongQueryValue(PARAM_START_LOW);
		Long startHigh = getLongQueryValue(PARAM_START_HIGH);
		Long endLow = getLongQueryValue(PARAM_END_LOW);
		Long endHigh = getLongQueryValue(PARAM_END_HIGH);
		Long maxRowsParam = getLongQueryValue(PARAM_MAX_ROWS);
		int maxRows = 100; // default
		if (maxRowsParam!=null){
			maxRows = maxRowsParam.intValue();
		}
		
		//
		// Search Form
		//
		buffer.append("<br/>");
		buffer.append("<center><form>\n");
		buffer.append("<div>Begin Timestamp: <input type='text' name='"+PARAM_SUBMIT_LOW+"'" + ((submitLow!=null)?(" value='"+submitLow+"'"):("")) + "/>&nbsp;&nbsp;&nbsp;\n");
		buffer.append("End Timestamp: <input type='text' name='"+PARAM_SUBMIT_HIGH+"'" + ((submitHigh!=null)?(" value='"+submitHigh+"'"):("")) + "/>&nbsp;&nbsp;&nbsp;\n");
		buffer.append("max num rows: <input type='text' name='"+PARAM_MAX_ROWS+"' value='"+maxRows+"'/></div><br/>\n");
		buffer.append("<div>ServerID: <input type='text' name='"+PARAM_SERVER_ID+"'" + ((serverID!=null)?(" value='"+serverID.toLowerCase()+"'"):("")) + "/>&nbsp;&nbsp;&nbsp;\n");
		buffer.append("Compute Host: <input type='text' name='"+PARAM_COMPUTE_HOST+"'" + ((computeHost!=null)?(" value='"+computeHost+"'"):("")) + "/></div><br/>\n");
		buffer.append("<div>Simulation ID: <input type='text' name='"+PARAM_SIM_ID+"'" + ((simid!=null)?(" value='"+simid+"'"):("")) + "/>&nbsp;&nbsp;&nbsp;\n");
		buffer.append("Job ID (parameter scan index): <input type='text' name='"+PARAM_JOB_ID+"'" + ((jobid!=null)?(" value='"+jobid+"'"):("")) + "/>&nbsp;&nbsp;&nbsp;\n");
		buffer.append("Task ID (retry index): <input type='text' name='"+PARAM_TASK_ID+"'" + ((taskid!=null)?(" value='"+taskid+"'"):("")) + "/></div><br/>\n");
		buffer.append("<br/>");
		buffer.append("<div>Simulation Status (choose at least one):<br/>\n");
		buffer.append("waiting<input type='checkbox' name='"+PARAM_STATUS_WAITING+"'" + ((statusWaiting)?(" checked='yes'"):(""))+"'/>&nbsp;&nbsp;<br/>\n");
		buffer.append("queued<input type='checkbox' name='"+PARAM_STATUS_QUEUED+"'" + ((statusQueued)?(" checked='yes'"):(""))+"'/>&nbsp;&nbsp;\n");
		buffer.append("dispatched<input type='checkbox' name='"+PARAM_STATUS_DISPATCHED+"'" + ((statusDispatched)?(" checked='yes'"):(""))+"'/>&nbsp;&nbsp;\n");
		buffer.append("running<input type='checkbox' name='"+PARAM_STATUS_RUNNING+"'" + ((statusRunning)?(" checked='yes'"):(""))+"'/>&nbsp;&nbsp;<br/>\n");
		buffer.append("completed<input type='checkbox' name='"+PARAM_STATUS_COMPLETED+"'" + ((statusCompleted)?(" checked='yes'"):(""))+"'/>&nbsp;&nbsp;\n");
		buffer.append("failed<input type='checkbox' name='"+PARAM_STATUS_FAILED+"'" + ((statusFailed)?(" checked='yes'"):(""))+"'/>&nbsp;&nbsp;\n");
		buffer.append("stopped<input type='checkbox' name='"+PARAM_STATUS_STOPPED+"'" + ((statusStopped)?(" checked='yes'"):(""))+"'/>&nbsp;&nbsp;</div>\n");
		buffer.append("<br/><br/>");
		buffer.append("<input type='submit' value='Search' style='font-size:large'>\n");
//		buffer.append("<font size='+3'><input type='submit' value='Search' style='font-size:large; height:50px; width:100px'></font>\n");
		buffer.append("</form></center>\n");
		buffer.append("<br/>");
		
		if (simTasks.length==0){
			buffer.append("<h3>query returned no results</h3>\n");
		}else{
			buffer.append("<h3>query returned "+simTasks.length+" results</h3>\n");
			
			//
			// results table
			//
			buffer.append("<table border='1'>\n");
			buffer.append("<tr>\n");
			buffer.append("<th>"+FIELD_MODELID+"</th>\n");
			buffer.append("<th>"+FIELD_SIMKEY+"</th>\n");
			buffer.append("<th>"+FIELD_SIMNAME+"</th>\n");
			buffer.append("<th>"+FIELD_USERNAME+"</th>\n");
			buffer.append("<th>"+FIELD_USERKEY+"</th>\n");
			buffer.append("<th>"+FIELD_JOBINDEX+"</th>\n");
			buffer.append("<th>"+FIELD_TASKID+"</th>\n");
			buffer.append("<th>"+FIELD_HTCJOBID+"</th>\n");
			buffer.append("<th>"+FIELD_HAS_DATA+"</th>\n");
			buffer.append("<th>"+FIELD_STATUS+"</th>\n");
			buffer.append("<th>"+FIELD_STARTDATE+"</th>\n");
			buffer.append("<th>"+FIELD_MESSAGE+"</th>\n");
			buffer.append("<th>"+FIELD_SITE+"</th>\n");
			buffer.append("<th>"+FIELD_COMPUTEHOST+"</th>\n");
			buffer.append("</tr>\n");
			//
			// one row in table per SimulationTaskRepresentation.
			//
			for (SimulationTaskRepresentation simulationTaskRepresentation : simTasks) {
				buffer.append("<tr>\n");
				
				buffer.append("<td>"+simulationTaskRepresentation.modelType+":"+simulationTaskRepresentation.modelID+"</td>\n");
				buffer.append("<td>"+simulationTaskRepresentation.simKey+"</td>\n");
				buffer.append("<td>"+simulationTaskRepresentation.simName+"</td>\n");
				buffer.append("<td>"+simulationTaskRepresentation.userName+"</td>\n");
				buffer.append("<td>"+simulationTaskRepresentation.userKey+"</td>\n");
				buffer.append("<td>"+simulationTaskRepresentation.jobIndex+"</td>\n");
				buffer.append("<td>"+simulationTaskRepresentation.taskId+"</td>\n");
				buffer.append("<td>"+simulationTaskRepresentation.htcJobId+"</td>\n");
				buffer.append("<td>"+simulationTaskRepresentation.hasData+"</td>\n");
				buffer.append("<td>"+simulationTaskRepresentation.status+"</td>\n");
				buffer.append("<td>"+new Date(simulationTaskRepresentation.startdate)+"</td>\n");
				buffer.append("<td>"+simulationTaskRepresentation.message+"</td>\n");
				buffer.append("<td>"+simulationTaskRepresentation.site+"</td>\n");
				buffer.append("<td>"+simulationTaskRepresentation.computeHost+"</td>\n");
	
				buffer.append("</tr>\n");
			}
			buffer.append("</table>\n");
		}
		
		Gson gson = new Gson();
		buffer.append("<br/>"+gson.toJson(simTasks)+"<br/>");
		
		buffer.append("</body>\n");
		buffer.append("</html>\n");
		
		return new StringRepresentation(buffer.toString(), MediaType.TEXT_HTML);
	}

	private boolean getBooleanQueryValue(String paramName, boolean bDefault) {
		String stringValue = getQueryValue(paramName);
		if (stringValue!=null && stringValue.length()>0){
			return (stringValue.equalsIgnoreCase("true") || stringValue.equalsIgnoreCase("on") || stringValue.equalsIgnoreCase("yes"));
		}else{
			return bDefault;
		}
	}

	private Long getLongQueryValue(String paramName){
		String stringValue = getQueryValue(paramName);
		Long longValue = null;
		if (stringValue!=null && stringValue.length()>0){
			try {
				longValue = Long.parseLong(stringValue);
			}catch (NumberFormatException e){
			}
		}
		return longValue;
	}
	
	   
	private SimulationTaskRepresentation[] getSimulationTaskRepresentations() {
		ArrayList<SimulationTaskRepresentation> simTaskReps = new ArrayList<SimulationTaskRepresentation>();
		AdminDBTopLevel adminDbTopLevel = ((VCellApiApplication)getApplication()).adminDBTopLevel;
		if (adminDbTopLevel!=null){
			try {
				List<SimpleJobStatus> simJobStatusList = query(adminDbTopLevel);
				for (SimpleJobStatus simpleJobStatus : simJobStatusList) {
					SimulationTaskRepresentation simTaskRep = new SimulationTaskRepresentation(simpleJobStatus);
					simTaskReps.add(simTaskRep);
				}
			} catch (DataAccessException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to retrieve active jobs from VCell Database : "+e.getMessage());
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to retrieve active jobs from VCell Database : "+e.getMessage());
			}
		}else{
			simTaskReps.add(new SimulationTaskRepresentation(new SimulationJobStatus(VCellServerID.getServerID("JIMS"),
							new VCSimulationIdentifier(new KeyValue("123"),new User("schaff",new KeyValue("222"))), 
							0, // jobIndex
							new Date(), // submission date
							SchedulerStatus.RUNNING,
							0, // taskID
							SimulationMessage.MESSAGE_JOB_RUNNING_UNKNOWN,
							new SimulationQueueEntryStatus(new Date(), 1, SimulationQueueID.QUEUE_ID_SIMULATIONJOB),
							new SimulationExecutionStatus(new Date(), "myhost", new Date(), null, false, null))));
			simTaskReps.add(new SimulationTaskRepresentation(new SimulationJobStatus(VCellServerID.getServerID("JIMS"),
							new VCSimulationIdentifier(new KeyValue("124"),new User("schaff",new KeyValue("222"))), 
							0, // jobIndex
							new Date(), // submission date
							SchedulerStatus.QUEUED,
							0, // taskID
							SimulationMessage.MESSAGE_JOB_RUNNING_UNKNOWN,
							new SimulationQueueEntryStatus(new Date(), 1, SimulationQueueID.QUEUE_ID_SIMULATIONJOB),
							new SimulationExecutionStatus(new Date(), "myhost", new Date(), null, false, null))));
		}
		return simTaskReps.toArray(new SimulationTaskRepresentation[0]);
	}

    private List<SimpleJobStatus> query(AdminDBTopLevel adminDbTop) throws SQLException, DataAccessException {	
    	
		String userID = getAttribute(PARAM_USER);
		Long simid = getLongQueryValue(PARAM_SIM_ID);
		Long jobid = getLongQueryValue(PARAM_JOB_ID);
		Long taskid = getLongQueryValue(PARAM_TASK_ID);
		String computeHost = getQueryValue(PARAM_COMPUTE_HOST);
		String serverID = getQueryValue(PARAM_SERVER_ID);
		boolean statusWaiting = getBooleanQueryValue(PARAM_STATUS_WAITING,false);
		boolean statusQueued = getBooleanQueryValue(PARAM_STATUS_QUEUED,false);
		boolean statusDispatched = getBooleanQueryValue(PARAM_STATUS_DISPATCHED,false);
		boolean statusRunning = getBooleanQueryValue(PARAM_STATUS_RUNNING,false);
		boolean statusCompleted = getBooleanQueryValue(PARAM_STATUS_COMPLETED,false);
		boolean statusFailed = getBooleanQueryValue(PARAM_STATUS_FAILED,false);
		boolean statusStopped = getBooleanQueryValue(PARAM_STATUS_STOPPED,false);
		Long submitLow = getLongQueryValue(PARAM_SUBMIT_LOW);
		Long submitHigh = getLongQueryValue(PARAM_SUBMIT_HIGH);
		Long startLow = getLongQueryValue(PARAM_START_LOW);
		Long startHigh = getLongQueryValue(PARAM_START_HIGH);
		Long endLow = getLongQueryValue(PARAM_END_LOW);
		Long endHigh = getLongQueryValue(PARAM_END_HIGH);
		Long maxRowsParam = getLongQueryValue(PARAM_MAX_ROWS);
		int maxRows = 100; // default
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
	   		List<SimpleJobStatus> resultList = adminDbTop.getSimulationJobStatus(conditionsBuffer.toString(), maxRows, true);
	   		return resultList;
    	}
    }
 }

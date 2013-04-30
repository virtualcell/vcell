package org.vcell.rest.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.RequestInfo;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.common.SimulationTaskRepresentation;
import org.vcell.rest.common.SimulationTasksResource;
import org.vcell.util.DataAccessException;

import cbit.vcell.messaging.db.SimpleJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.messaging.db.SimulationJobTable;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.UserTable;

import com.google.gson.Gson;

import freemarker.template.Configuration;

public class SimulationTasksServerResource extends AbstractServerResource implements SimulationTasksResource {

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
	private static final String PARAM_HAS_DATA = "hasData";
	private static final String PARAM_MAX_ROWS = "maxRows";

	@Override
	protected void doInit() throws ResourceException {
		setName("SimulationTask resource");
		setDescription("The resource containing the list of SimulationTasks status");
	}
	
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
        parameterInfos.add(new ParameterInfo(PARAM_HAS_DATA,false,"string",ParameterStyle.QUERY,"include jobs if has data (all/yes/no - default=all)"));
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
		Map<String,Object> dataModel = new HashMap<String,Object>();
		
		dataModel.put("userId", getAttribute(PARAM_USER));
		dataModel.put("simId", getLongQueryValue(PARAM_SIM_ID));
		dataModel.put("jobId",  getLongQueryValue(PARAM_JOB_ID));
		dataModel.put("taskId",  getLongQueryValue(PARAM_TASK_ID));
		dataModel.put("computeHost", getQueryValue(PARAM_COMPUTE_HOST));
		dataModel.put("serverId", getQueryValue(PARAM_SERVER_ID));
		dataModel.put("hasData", getQueryValue(PARAM_HAS_DATA));
		dataModel.put("waiting", getBooleanQueryValue(PARAM_STATUS_WAITING,false));
		dataModel.put("queued", getBooleanQueryValue(PARAM_STATUS_QUEUED,false));
		dataModel.put("dispatched", getBooleanQueryValue(PARAM_STATUS_DISPATCHED,false));
		dataModel.put("running", getBooleanQueryValue(PARAM_STATUS_RUNNING,false));
		dataModel.put("completed", getBooleanQueryValue(PARAM_STATUS_COMPLETED,false));
		dataModel.put("failed", getBooleanQueryValue(PARAM_STATUS_FAILED,false));
		dataModel.put("stopped", getBooleanQueryValue(PARAM_STATUS_STOPPED,false));
		dataModel.put("submitLow", getLongQueryValue(PARAM_SUBMIT_LOW));
		dataModel.put("submitHigh", getLongQueryValue(PARAM_SUBMIT_HIGH));
		dataModel.put("startLow", getLongQueryValue(PARAM_START_LOW));
		dataModel.put("startHigh", getLongQueryValue(PARAM_START_HIGH));
		dataModel.put("endLow", getLongQueryValue(PARAM_END_LOW));
		dataModel.put("endHigh", getLongQueryValue(PARAM_END_HIGH));
		Long maxRowsParam = getLongQueryValue(PARAM_MAX_ROWS);
		if (maxRowsParam!=null){
			dataModel.put("maxRows", maxRowsParam);
		}else{
			dataModel.put("maxRows", 100);
		}

		dataModel.put("simTasks", Arrays.asList(simTasks));
		
		
		org.restlet.security.User autheticatedUser = getClientInfo().getUser();
		if (autheticatedUser!=null){
			dataModel.put("userid",autheticatedUser.getIdentifier());
		}
		
		Gson gson = new Gson();
		dataModel.put("jsonResponse",gson.toJson(simTasks));
		
		VCellApiApplication application = (VCellApiApplication)getApplication();
		Configuration templateConfiguration = application.templateConfiguration;

		Representation formFtl = new ClientResource(LocalReference.createClapReference("/simulationTasks.ftl")).get();
		TemplateRepresentation templateRepresentation = new TemplateRepresentation(formFtl, templateConfiguration, dataModel, MediaType.TEXT_HTML);
		return templateRepresentation;
	}


	private SimulationTaskRepresentation[] getSimulationTaskRepresentations() {
		ArrayList<SimulationTaskRepresentation> simTaskReps = new ArrayList<SimulationTaskRepresentation>();
		AdminDBTopLevel adminDbTopLevel = ((VCellApiApplication)getApplication()).adminDBTopLevel;
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
		return simTaskReps.toArray(new SimulationTaskRepresentation[0]);
	}

    private List<SimpleJobStatus> query(AdminDBTopLevel adminDbTop) throws SQLException, DataAccessException {	
    	
		String userID = getAttribute(PARAM_USER);
		Long simid = getLongQueryValue(PARAM_SIM_ID);
		Long jobid = getLongQueryValue(PARAM_JOB_ID);
		Long taskid = getLongQueryValue(PARAM_TASK_ID);
		String computeHost = getQueryValue(PARAM_COMPUTE_HOST);
		String serverID = getQueryValue(PARAM_SERVER_ID);
		String hasData = getQueryValue(PARAM_HAS_DATA);
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
	   		List<SimpleJobStatus> resultList = adminDbTop.getSimulationJobStatus(conditionsBuffer.toString(), maxRows, true);
	   		return resultList;
    	}
    }
 }

package org.vcell.rest.admin;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.ext.json.JsonRepresentation;
import org.vcell.api.common.SimpleJobStatusRepresentation;
import org.vcell.rest.VCellApiApplication;

import com.google.gson.Gson;

import cbit.vcell.server.SimpleJobStatus;
import cbit.vcell.server.SimpleJobStatusQuerySpec;

public final class AdminJobsRestlet extends Restlet {
	
	public static final String PARAM_SUBMIT_LOW			= "submitLowMS";
	public static final String PARAM_SUBMIT_HIGH			= "submitHighMS";
	public static final String PARAM_START_LOW			= "startLowMS";
	public static final String PARAM_START_HIGH			= "startHighMS";
	public static final String PARAM_END_LOW				= "endLowMS";
	public static final String PARAM_END_HIGH			= "endHighMS";
	public static final String PARAM_START_ROW			= "startRow";
	public static final String PARAM_MAX_ROWS			= "maxRows";
	public static final String PARAM_SERVERID			= "serverId";
	public static final String PARAM_COMPUTEHOST			= "computeHost";
	public static final String PARAM_USERID				= "userid";
	public static final String PARAM_SIMID				= "simId";
	public static final String PARAM_JOBID				= "jobId";
	public static final String PARAM_TASKID				= "taskId";
	public static final String PARAM_HAS_DATA			= "hasData";
	public static final String PARAM_STATUS_WAITING		= "waiting";
	public static final String PARAM_STATUS_QUEUED		= "queued";
	public static final String PARAM_STATUS_DISPATCHED	= "dispatched";
	public static final String PARAM_STATUS_RUNNING		= "running";
	public static final String PARAM_STATUS_COMPLETED		= "completed";
	public static final String PARAM_STATUS_FAILED		= "failed";
	public static final String PARAM_STATUS_STOPPED		= "stopped";
	public static final String PARAM_JSON_QUERY			= "query";
		
	public AdminJobsRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request req, Response response) {
		if (req.getMethod().equals(Method.GET)){
			try {
				VCellApiApplication application = ((VCellApiApplication)getApplication());
//				User vcellUser = application.getVCellUser(req.getChallengeResponse(),AuthenticationPolicy.prohibitInvalidCredentials);
//				User adminUser = new User(PropertyLoader.ADMINISTRATOR_ACCOUNT, new KeyValue(PropertyLoader.ADMINISTRATOR_ID));
//				if (!vcellUser.equals(adminUser)) {
//					getLogger().severe("AdminJobsRestlet: user '"+vcellUser.toString()+"' is not authorized");
//					response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
//					response.setEntity("not authorized for this service", MediaType.TEXT_PLAIN);
//					return;
//				}
				HttpRequest request = (HttpRequest)req;
				Form form = request.getResourceRef().getQueryAsForm();
				String jsonQuery = form.getFirstValue(PARAM_JSON_QUERY, true);
				
	    			Gson gson = new Gson();
	    			SimpleJobStatusQuerySpec querySpec = new SimpleJobStatusQuerySpec();
	    			if (jsonQuery != null) {
	    				querySpec = gson.fromJson(jsonQuery, SimpleJobStatusQuerySpec.class);
	    			}else {
	    				querySpec.submitLowMS	= getLongParam(form, PARAM_SUBMIT_LOW, null);
	    				querySpec.submitHighMS	= getLongParam(form, PARAM_SUBMIT_HIGH, null);
	    				querySpec.startLowMS		= getLongParam(form, PARAM_START_LOW, null);
	    				querySpec.startHighMS	= getLongParam(form, PARAM_START_HIGH, null);
	    				querySpec.endLowMS		= getLongParam(form, PARAM_END_LOW, null);
	    				querySpec.endHighMS		= getLongParam(form, PARAM_END_HIGH, null);
	    				querySpec.startRow		= getIntegerParam(form, PARAM_START_ROW, 0);
	    				querySpec.maxRows		= getIntegerParam(form, PARAM_MAX_ROWS, 100);
	    				querySpec.serverId		= getStringParam(form, PARAM_SERVERID, null);
	    				querySpec.computeHost	= getStringParam(form, PARAM_COMPUTEHOST, null);
	    				querySpec.userid			= getStringParam(form, PARAM_USERID, null);
	    				querySpec.simId			= getLongParam(form, PARAM_SIMID, null);
	    				querySpec.jobId			= getLongParam(form, PARAM_JOBID, null);
	    				querySpec.taskId			= getLongParam(form, PARAM_TASKID, null);
	    				querySpec.hasData		= getBooleanParam(form, PARAM_HAS_DATA, null);
	    				querySpec.waiting		= getBooleanParam(form, PARAM_STATUS_WAITING, true);
	    				querySpec.queued			= getBooleanParam(form, PARAM_STATUS_QUEUED, true);
	    				querySpec.dispatched		= getBooleanParam(form, PARAM_STATUS_DISPATCHED, true);
	    				querySpec.running		= getBooleanParam(form, PARAM_STATUS_RUNNING, true);
	    				querySpec.completed		= getBooleanParam(form, PARAM_STATUS_COMPLETED, true);
	    				querySpec.failed			= getBooleanParam(form, PARAM_STATUS_FAILED, true);
	    				querySpec.stopped		= getBooleanParam(form, PARAM_STATUS_STOPPED, true);
	    			}
	    			if (querySpec.serverId != null) {
	    				querySpec.serverId = querySpec.serverId.toLowerCase();
	    			}

				AdminService adminService = application.getAdminService();
				SimpleJobStatus[] jobStatusArray = adminService.query(querySpec);
				SimpleJobStatusRepresentation[] reps = new SimpleJobStatusRepresentation[jobStatusArray.length];
				for (int i=0;i<jobStatusArray.length;i++) {
					reps[i] = jobStatusArray[i].toRep();
				}
				String jobStatusArrayJson = gson.toJson(reps);
				response.setStatus(Status.SUCCESS_OK);
				response.setEntity(new JsonRepresentation(jobStatusArrayJson));
				
			} catch (Exception e) {
				getLogger().severe("failed to retrieve job status: "+e.getMessage());
				e.printStackTrace();
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				response.setEntity("failed to retrieve job status: "+e.getMessage(), MediaType.TEXT_PLAIN);
			}
		}
	}

	private Long getLongParam(Form form, String param, Long defaultValue) {
		String value = form.getFirstValue(param);
		if (value != null) {
			return Long.parseLong(value);
		}else {
			return defaultValue;
		}
	}
	
	private Integer getIntegerParam(Form form, String param, Integer defaultValue) {
		String value = form.getFirstValue(param);
		if (value != null) {
			return Integer.parseInt(value);
		}else {
			return defaultValue;
		}
	}
	
	private String getStringParam(Form form, String param, String defaultValue) {
		String value = form.getFirstValue(param);
		if (value != null) {
			return value;
		}else {
			return defaultValue;
		}
	}
	
	private Boolean getBooleanParam(Form form, String param, Boolean defaultValue) {
		String value = form.getFirstValue(param);
		if (value != null) {
			return Boolean.parseBoolean(value);
		}else {
			return defaultValue;
		}
	}
}
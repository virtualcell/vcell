package org.vcell.rest.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
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
import org.vcell.rest.VCellApiApplication.AuthenticationPolicy;
import org.vcell.rest.common.SimulationStatusRepresentation;
import org.vcell.rest.common.SimulationStatusesResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

import com.google.gson.Gson;

import freemarker.template.Configuration;

public class SimulationStatusServerResource extends AbstractServerResource implements SimulationStatusesResource {

    public static final String PARAM_USER = "user";
    public static final String PARAM_SIM_ID = "simId";
	public static final String PARAM_STATUS_NEVERRAN = "neverran";
	public static final String PARAM_STATUS_ACTIVE = "active";
	public static final String PARAM_STATUS_COMPLETED = "completed";
	public static final String PARAM_STATUS_FAILED = "failed";
	public static final String PARAM_STATUS_STOPPED = "stopped";
	public static final String PARAM_END_HIGH = "endHigh";
	public static final String PARAM_END_LOW = "endLow";
	public static final String PARAM_START_HIGH = "startHigh";
	public static final String PARAM_START_LOW = "startLow";
	public static final String PARAM_SUBMIT_HIGH = "submitHigh";
	public static final String PARAM_SUBMIT_LOW = "submitLow";
	public static final String PARAM_HAS_DATA = "hasData";
	public static final String PARAM_MAX_ROWS = "maxRows";
	public static final String PARAM_START_ROW = "startRow";

	@Override
	protected void doInit() throws ResourceException {
		setName("SimulationTask resource");
		setDescription("The resource containing the list of SimulationTasks status");
	}
	
	@Override
    protected void describe(ApplicationInfo applicationInfo) {
        RepresentationInfo rep = new RepresentationInfo(MediaType.APPLICATION_JSON);
        rep.setIdentifier(VCellApiApplication.SIMTASK);
        applicationInfo.getRepresentations().add(rep);

        DocumentationInfo doc = new DocumentationInfo();
        doc.setTitle(VCellApiApplication.SIMTASK);
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
        parameterInfos.add(new ParameterInfo(PARAM_HAS_DATA,false,"string",ParameterStyle.QUERY,"include jobs if has data (all/yes/no - default=all)"));
        parameterInfos.add(new ParameterInfo(PARAM_STATUS_NEVERRAN,false,"string",ParameterStyle.QUERY,"include jobs which never had been submitted (default=false)"));
        parameterInfos.add(new ParameterInfo(PARAM_STATUS_ACTIVE,false,"string",ParameterStyle.QUERY,"include jobs status waiting, queued, dispatched, running (default=false)"));
        parameterInfos.add(new ParameterInfo(PARAM_STATUS_COMPLETED,false,"string",ParameterStyle.QUERY,"include jobs status completed (default=false)"));
        parameterInfos.add(new ParameterInfo(PARAM_STATUS_STOPPED,false,"string",ParameterStyle.QUERY,"include jobs status stopped (default=false)"));
        parameterInfos.add(new ParameterInfo(PARAM_STATUS_COMPLETED,false,"string",ParameterStyle.QUERY,"include jobs status completed (default=false)"));
        parameterInfos.add(new ParameterInfo(PARAM_SUBMIT_LOW,false,"string",ParameterStyle.QUERY,"earliest submission timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_SUBMIT_HIGH,false,"string",ParameterStyle.QUERY,"latest submission timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_START_LOW,false,"string",ParameterStyle.QUERY,"earliest start timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_START_HIGH,false,"string",ParameterStyle.QUERY,"latest start timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_END_LOW,false,"string",ParameterStyle.QUERY,"earliest end timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_END_HIGH,false,"string",ParameterStyle.QUERY,"latest end timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_START_ROW,false,"string",ParameterStyle.QUERY,"starting row of records returned (default is 1)"));
        parameterInfos.add(new ParameterInfo(PARAM_MAX_ROWS,false,"string",ParameterStyle.QUERY,"max number of records returned (default is 10)"));
 		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}
	
	@Override
    public SimulationStatusRepresentation[] get_json() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.prohibitInvalidCredentials);
		
        return getSimulationStatusRepresentations(vcellUser);
    }
    
	@Override
	public Representation get_html() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
		
		SimulationStatusRepresentation[] simStatuses = getSimulationStatusRepresentations(vcellUser);
		Map<String,Object> dataModel = new HashMap<String,Object>();
		
		dataModel.put("loginurl", "/"+VCellApiApplication.LOGINFORM);  // +"?"+VCellApiApplication.REDIRECTURL_FORMNAME+"="+getRequest().getResourceRef().toUrl());
		dataModel.put("logouturl", "/"+VCellApiApplication.LOGOUT+"?"+VCellApiApplication.REDIRECTURL_FORMNAME+"="+Reference.encode(getRequest().getResourceRef().toUrl().toString()));
		if (vcellUser!=null){
			dataModel.put("userid",vcellUser.getName());
		}
		dataModel.put("userId", getAttribute(PARAM_USER));
		dataModel.put("simId", getQueryValue(PARAM_SIM_ID));
		dataModel.put("hasData", getQueryValue(PARAM_HAS_DATA));
		dataModel.put("neverran", getBooleanQueryValue(PARAM_STATUS_NEVERRAN,false));
		dataModel.put("active", getBooleanQueryValue(PARAM_STATUS_ACTIVE,false));
		dataModel.put("completed", getBooleanQueryValue(PARAM_STATUS_COMPLETED,false));
		dataModel.put("failed", getBooleanQueryValue(PARAM_STATUS_FAILED,false));
		dataModel.put("stopped", getBooleanQueryValue(PARAM_STATUS_STOPPED,false));
		dataModel.put("submitLow", getLongQueryValue(PARAM_SUBMIT_LOW));
		dataModel.put("submitHigh", getLongQueryValue(PARAM_SUBMIT_HIGH));
		dataModel.put("startLow", getLongQueryValue(PARAM_START_LOW));
		dataModel.put("startHigh", getLongQueryValue(PARAM_START_HIGH));
		dataModel.put("endLow", getLongQueryValue(PARAM_END_LOW));
		dataModel.put("endHigh", getLongQueryValue(PARAM_END_HIGH));
		Long startRowParam = getLongQueryValue(PARAM_START_ROW);
		if (startRowParam!=null){
			dataModel.put("startRow", startRowParam);
		}else{
			dataModel.put("startRow", 1);
		}
		Long maxRowsParam = getLongQueryValue(PARAM_MAX_ROWS);
		if (maxRowsParam!=null){
			dataModel.put("maxRows", maxRowsParam);
		}else{
			dataModel.put("maxRows", 10);
		}

		dataModel.put("simStatuses", Arrays.asList(simStatuses));
		
		
		
		Gson gson = new Gson();
		dataModel.put("jsonResponse",gson.toJson(simStatuses));
		
		Configuration templateConfiguration = application.getTemplateConfiguration();

		Representation formFtl = new ClientResource(LocalReference.createClapReference("/simulationStatus.ftl")).get();
		TemplateRepresentation templateRepresentation = new TemplateRepresentation(formFtl, templateConfiguration, dataModel, MediaType.TEXT_HTML);
		return templateRepresentation;
	}


	private SimulationStatusRepresentation[] getSimulationStatusRepresentations(User vcellUser) {
		RestDatabaseService restDatabaseService = ((VCellApiApplication)getApplication()).getRestDatabaseService();
		try {
			SimulationStatusRepresentation[] simStatusRepresentations = restDatabaseService.query(this, vcellUser);
			return simStatusRepresentations;
		} catch (PermissionException ee){
			throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "permission denied to requested resource");
		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to retrieve active jobs from VCell Database : "+e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to retrieve active jobs from VCell Database : "+e.getMessage());
		}
	}

 }

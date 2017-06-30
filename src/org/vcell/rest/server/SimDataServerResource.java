package org.vcell.rest.server;

import java.util.ArrayList;
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
import org.vcell.rest.common.SimDataRepresentation;
import org.vcell.rest.common.SimDataResource;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

import com.google.gson.Gson;

import cbit.vcell.modeldb.SimulationRep;
import cbit.vcell.simdata.DataSetMetadata;
import freemarker.template.Configuration;

public class SimDataServerResource extends AbstractServerResource implements SimDataResource {

	public static final String PARAM_USER = "user";
	public static final String PARAM_SIM_ID = "simId";
	public static final String PARAM_START_ROW = "startRow";
	public static final String PARAM_MAX_ROWS = "maxRows";

	@Override
	protected void doInit() throws ResourceException {
		setName("SimulationTask resource");
		setDescription("The resource containing the list of Biomodels");
	}
	
	@Override
    protected void describe(ApplicationInfo applicationInfo) {
        RepresentationInfo rep = new RepresentationInfo(MediaType.APPLICATION_JSON);
        rep.setIdentifier(VCellApiApplication.BIOMODEL);
        applicationInfo.getRepresentations().add(rep);

        DocumentationInfo doc = new DocumentationInfo();
        doc.setTitle(VCellApiApplication.BIOMODEL);
        doc.setTextContent("jdom containing list of biomodels");
        rep.getDocumentations().add(doc);
    }

	@Override
	protected void describeGet(MethodInfo info) {
		super.describeGet(info);
		RequestInfo requestInfo = new RequestInfo();
        List<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
        parameterInfos.add(new ParameterInfo(PARAM_USER,false,"string",ParameterStyle.TEMPLATE,"VCell user id"));
        parameterInfos.add(new ParameterInfo(PARAM_SIM_ID,false,"string",ParameterStyle.QUERY,"VCell simulation database id"));
        parameterInfos.add(new ParameterInfo(PARAM_START_ROW,false,"string",ParameterStyle.QUERY,"index of first record returned (default is 1)"));
        parameterInfos.add(new ParameterInfo(PARAM_MAX_ROWS,false,"string",ParameterStyle.QUERY,"max number of records returned (default is 10)"));
 		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}
	
	@Override
    public SimDataRepresentation get_json() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.prohibitInvalidCredentials);
		
        return getSimDataRepresentation(vcellUser);
    }
    
	@Override
	public Representation get_html() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
		SimDataRepresentation simData = null;
		try {
			simData = getSimDataRepresentation(vcellUser);
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
		Map<String,Object> dataModel = new HashMap<String,Object>();
		
		dataModel.put("loginurl", "/"+VCellApiApplication.LOGINFORM);  // +"?"+VCellApiApplication.REDIRECTURL_FORMNAME+"="+getRequest().getResourceRef().toUrl());
		dataModel.put("logouturl", "/"+VCellApiApplication.LOGOUT+"?"+VCellApiApplication.REDIRECTURL_FORMNAME+"="+Reference.encode(getRequest().getResourceRef().toUrl().toString()));
		if (vcellUser!=null){
			dataModel.put("userid",vcellUser.getName());
		}
		
		dataModel.put("userId", getAttribute(PARAM_USER));
		dataModel.put("simId", getQueryValue(PARAM_SIM_ID));
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

		dataModel.put("simdata", simData);
		
		Gson gson = new Gson();
		dataModel.put("jsonResponse",gson.toJson(simData));
		
		Configuration templateConfiguration = application.getTemplateConfiguration();

		Representation formFtl = new ClientResource(LocalReference.createClapReference("/simdata.ftl")).get();
		TemplateRepresentation templateRepresentation = new TemplateRepresentation(formFtl, templateConfiguration, dataModel, MediaType.TEXT_HTML);
		return templateRepresentation;
	}


	private SimDataRepresentation getSimDataRepresentation(User vcellUser) {
//		if (!application.authenticate(getRequest(), getResponse())){
//			// not authenticated
//			return new SimulationTaskRepresentation[0];
//		}else{
			RestDatabaseService restDatabaseService = ((VCellApiApplication)getApplication()).getRestDatabaseService();
			try {
				DataSetMetadata dataSetMetadata = restDatabaseService.getDataSetMetadata(this,vcellUser);
				SimulationRep simRep = restDatabaseService.getSimulationRep(dataSetMetadata.getSimKey());
				SimDataRepresentation simDataRepresentation = new SimDataRepresentation(dataSetMetadata,simRep.getScanCount());
				return simDataRepresentation;
			} catch (PermissionException e) {
				e.printStackTrace();
				throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "not authorized");
			} catch (ObjectNotFoundException e) {
				e.printStackTrace();
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "simulation metadata not found");
			} catch (Exception e){
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			}
//		}
	}
 }

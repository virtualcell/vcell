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
import org.vcell.rest.common.SimDataValuesRepresentation;
import org.vcell.rest.common.SimDataValuesResource;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

import com.google.gson.Gson;

import cbit.vcell.simdata.DataSetTimeSeries;
import freemarker.template.Configuration;

public class SimDataValuesServerResource extends AbstractServerResource implements SimDataValuesResource {

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
    public SimDataValuesRepresentation get_json() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.prohibitInvalidCredentials);
		
        return getSimDataValuesRepresentation(vcellUser);
    }
    
	@Override
	public Representation get_html() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
		
		SimDataValuesRepresentation simDataValues = getSimDataValuesRepresentation(vcellUser);
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

		dataModel.put("simdatavalues", simDataValues);
		
		int numVars = simDataValues.getVariables().length;
		if (numVars>1){
			StringBuffer buffer = new StringBuffer();
			String firstRow = "\"";
			for (int i=0; i<numVars; i++){
				firstRow += simDataValues.getVariables()[i].getName();
				if (i<numVars-1){
					firstRow += ",";
				}
			}
			firstRow += "\\n\" + \n";
			buffer.append(firstRow);
			
			int numTimes = simDataValues.getVariables()[0].getValues().length;
			
			for (int t=0; t<numTimes; t++){
				String row = "\"";
				for (int v=0; v<numVars; v++){
					row += simDataValues.getVariables()[v].getValues()[t];
					if (v<numVars-1){
						row += ",";
					}
				}
				row += "\\n\"";
				if (t<numTimes-1){
					row += " + \n";
				}
				buffer.append(row);
			}
			String csvdata = buffer.toString();
//			String csvdata = "\"t,x,y\\n\" + \n" +
//					"\"0,0,0\\n\" + \n" +
//					"\"1,1,1\\n\" + \n" +
//					"\"2,2,4\\n\" + \n" +
//					"\"3,3,9\\n\" + \n" +
//					"\"4,4,16\\n\" + \n" +
//					"\"5,5,25\\n\"";
			
			dataModel.put("csvdata",csvdata);
		}
		
		Gson gson = new Gson();
		dataModel.put("jsonResponse",gson.toJson(simDataValues));
		
		Configuration templateConfiguration = application.getTemplateConfiguration();

		Representation formFtl = new ClientResource(LocalReference.createClapReference("/simdatavalues.ftl")).get();
		TemplateRepresentation templateRepresentation = new TemplateRepresentation(formFtl, templateConfiguration, dataModel, MediaType.TEXT_HTML);
		return templateRepresentation;
	}


	private SimDataValuesRepresentation getSimDataValuesRepresentation(User vcellUser) {
//		if (!application.authenticate(getRequest(), getResponse())){
//			// not authenticated
//			return new SimulationTaskRepresentation[0];
//		}else{
			RestDatabaseService restDatabaseService = ((VCellApiApplication)getApplication()).getRestDatabaseService();
			try {
				DataSetTimeSeries dataSetTimeSeries = restDatabaseService.getDataSetTimeSeries(this,vcellUser);
				SimDataValuesRepresentation simDataRepresentation = new SimDataValuesRepresentation(dataSetTimeSeries);
				return simDataRepresentation;
			} catch (PermissionException e) {
				e.printStackTrace();
				throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "not authorized to stop simulation");
			} catch (ObjectNotFoundException e) {
				e.printStackTrace();
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "simulation not found");
			} catch (Exception e){
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			}
//		}
	}
 }

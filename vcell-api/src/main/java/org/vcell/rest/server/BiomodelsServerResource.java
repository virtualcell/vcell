package org.vcell.rest.server;

import java.sql.SQLException;
import java.text.ParseException;
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
import org.vcell.rest.common.BiomodelRepresentation;
import org.vcell.rest.common.BiomodelsResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

import com.google.gson.Gson;

import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.parser.ExpressionException;
import freemarker.template.Configuration;

public class BiomodelsServerResource extends AbstractServerResource implements BiomodelsResource {

	public static final String PARAM_USER = "user";
	public static final String PARAM_BM_NAME = "bmName";
	public static final String PARAM_BM_ID = "bmId";
	public static final String PARAM_SAVED_HIGH = "savedHigh";
	public static final String PARAM_SAVED_LOW = "savedLow";
	public static final String PARAM_START_ROW = "startRow";
	public static final String PARAM_MAX_ROWS = "maxRows";
	public static final String PARAM_BM_OWNER = "owner";
	public static final String PARAM_ORDERBY = "orderBy";
	public static final String PARAM_ORDERBY_DATE_ASC = "date_asc";
	public static final String PARAM_ORDERBY_DATE_DESC = "date_desc";
	public static final String PARAM_ORDERBY_YEAR_ASC = "year_asc";
	public static final String PARAM_ORDERBY_YEAR_DESC = "year_desc";
	public static final String PARAM_ORDERBY_NAME_ASC = "name_asc";
	public static final String PARAM_ORDERBY_NAME_DESC = "name_desc";
	public static final String PARAM_CATEGORY = "category";
	public static final String PARAM_CATEGORY_ALL = "all";
	public static final String PARAM_CATEGORY_PUBLIC = "public";
	public static final String PARAM_CATEGORY_SHARED = "shared";
	public static final String PARAM_CATEGORY_MINE = "mine";
	public static final String PARAM_CATEGORY_TUTORIAL = "tutorial";
	public static final String PARAM_CATEGORY_EDUCATION = "education";

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
        parameterInfos.add(new ParameterInfo(PARAM_BM_NAME,false,"string",ParameterStyle.QUERY,"VCell biomodel name"));
        parameterInfos.add(new ParameterInfo(PARAM_BM_ID,false,"string",ParameterStyle.QUERY,"VCell biomodel database id"));
        parameterInfos.add(new ParameterInfo(PARAM_SAVED_LOW,false,"string",ParameterStyle.QUERY,"earliest saved timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_SAVED_HIGH,false,"string",ParameterStyle.QUERY,"latest saved timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_START_ROW,false,"string",ParameterStyle.QUERY,"index of first record returned (default is 1)"));
        parameterInfos.add(new ParameterInfo(PARAM_MAX_ROWS,false,"string",ParameterStyle.QUERY,"max number of records returned (default is 10)"));
        parameterInfos.add(new ParameterInfo(PARAM_BM_OWNER,false,"string",ParameterStyle.QUERY,"biomodel owner"));
        parameterInfos.add(new ParameterInfo(PARAM_CATEGORY,false,"string",ParameterStyle.QUERY,"category (all,public,shared,mine,tutorial,education)"));
        parameterInfos.add(new ParameterInfo(PARAM_ORDERBY,false,"string",ParameterStyle.QUERY,"order ( (default is 10)"));
 		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}
	
	@Override
    public BiomodelRepresentation[] get_json() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.prohibitInvalidCredentials);
		
		BiomodelRepresentation[] bmReps = new BiomodelRepresentation[0];
		try {
			bmReps = getBiomodelRepresentations(vcellUser);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return bmReps;
    }
    
	@Override
	public Representation get_html() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
		
		BiomodelRepresentation[] biomodels = new BiomodelRepresentation[0];
		boolean bFormatErr = false;
		try {
			biomodels = getBiomodelRepresentations(vcellUser);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bFormatErr = true;
		}
		Map<String,Object> dataModel = new HashMap<String,Object>();
		
		dataModel.put("loginurl", "/"+VCellApiApplication.LOGINFORM);  // +"?"+VCellApiApplication.REDIRECTURL_FORMNAME+"="+getRequest().getResourceRef().toUrl());
		dataModel.put("logouturl", "/"+VCellApiApplication.LOGOUT+"?"+VCellApiApplication.REDIRECTURL_FORMNAME+"="+Reference.encode(getRequest().getResourceRef().toUrl().toString()));
		if (vcellUser!=null){
			dataModel.put("userid",vcellUser.getName());
		}
		
		dataModel.put("userId", getAttribute(PARAM_USER));
		dataModel.put("bmName", getQueryValue(PARAM_BM_NAME));
		dataModel.put("bmId", getQueryValue(PARAM_BM_ID));
		dataModel.put("savedLow", (bFormatErr?"Error":getQueryValue(PARAM_SAVED_LOW)));
		dataModel.put("savedHigh", (bFormatErr?"Error":getQueryValue(PARAM_SAVED_HIGH)));
		dataModel.put("ownerName", getQueryValue(PARAM_BM_OWNER));
		dataModel.put("category", getQueryValue(PARAM_CATEGORY));
		dataModel.put("orderBy", getQueryValue(PARAM_ORDERBY));
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

		dataModel.put("biomodels", Arrays.asList(biomodels));
		
		
		Gson gson = new Gson();
		dataModel.put("jsonResponse",gson.toJson(biomodels));
		
		Configuration templateConfiguration = application.getTemplateConfiguration();

		Representation formFtl = new ClientResource(LocalReference.createClapReference("/biomodels.ftl")).get();
		TemplateRepresentation templateRepresentation = new TemplateRepresentation(formFtl, templateConfiguration, dataModel, MediaType.TEXT_HTML);
		return templateRepresentation;
	}


	private BiomodelRepresentation[] getBiomodelRepresentations(User vcellUser) throws ParseException{
//		if (!application.authenticate(getRequest(), getResponse())){
//			// not authenticated
//			return new SimulationTaskRepresentation[0];
//		}else{
			ArrayList<BiomodelRepresentation> biomodelReps = new ArrayList<BiomodelRepresentation>();
			RestDatabaseService restDatabaseService = ((VCellApiApplication)getApplication()).getRestDatabaseService();
			try {
				BioModelRep[] bioModelReps = restDatabaseService.query(this,vcellUser);
				for (BioModelRep bioModelRep : bioModelReps) {
					BiomodelRepresentation biomodelRep = new BiomodelRepresentation(bioModelRep);
					biomodelReps.add(biomodelRep);
				}
			} catch (PermissionException ee){
				ee.printStackTrace();
				throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "not authorized");
			} catch (DataAccessException | SQLException | ExpressionException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to retrieve biomodels from VCell Database : "+e.getMessage());
			}
			return biomodelReps.toArray(new BiomodelRepresentation[0]);
//		}
	}
 }

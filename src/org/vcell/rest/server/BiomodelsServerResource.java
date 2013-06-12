package org.vcell.rest.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.vcell.rest.common.BiomodelRepresentation;
import org.vcell.rest.common.BiomodelsResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import cbit.vcell.modeldb.BioModelRep;

import com.google.gson.Gson;

import freemarker.template.Configuration;

public class BiomodelsServerResource extends AbstractServerResource implements BiomodelsResource {

	public static final String PARAM_USER = "user";
	public static final String PARAM_BM_ID = "bmId";
	public static final String PARAM_SAVED_HIGH = "savedHigh";
	public static final String PARAM_SAVED_LOW = "savedLow";
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
        parameterInfos.add(new ParameterInfo(PARAM_BM_ID,false,"string",ParameterStyle.QUERY,"VCell biomodel database id"));
        parameterInfos.add(new ParameterInfo(PARAM_SAVED_LOW,false,"string",ParameterStyle.QUERY,"earliest saved timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_SAVED_HIGH,false,"string",ParameterStyle.QUERY,"latest saved timestamp (seconds since 1/1/1970)"));
        parameterInfos.add(new ParameterInfo(PARAM_MAX_ROWS,false,"string",ParameterStyle.QUERY,"max number of records returned (default is 10)"));
 		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}
	
	@Override
    public BiomodelRepresentation[] get_json() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		org.restlet.security.User autheticatedUser = getClientInfo().getUser();
		User vcellUser = application.getVCellUser(autheticatedUser);
		
        return getBiomodelRepresentations(vcellUser);
    }
    
	@Override
	public Representation get_html() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		org.restlet.security.User autheticatedUser = getClientInfo().getUser();
		User vcellUser = application.getVCellUser(autheticatedUser);
		
		BiomodelRepresentation[] biomodels = getBiomodelRepresentations(vcellUser);
		Map<String,Object> dataModel = new HashMap<String,Object>();
		
		dataModel.put("userId", getAttribute(PARAM_USER));
		dataModel.put("bmId", getQueryValue(PARAM_BM_ID));
		dataModel.put("savedLow", getLongQueryValue(PARAM_SAVED_LOW));
		dataModel.put("savedHigh", getLongQueryValue(PARAM_SAVED_HIGH));
		Long maxRowsParam = getLongQueryValue(PARAM_MAX_ROWS);
		if (maxRowsParam!=null){
			dataModel.put("maxRows", maxRowsParam);
		}else{
			dataModel.put("maxRows", 10);
		}

		dataModel.put("biomodels", Arrays.asList(biomodels));
		
		
		if (vcellUser!=null){
			dataModel.put("userid",vcellUser.getName());
		}
		
		Gson gson = new Gson();
		dataModel.put("jsonResponse",gson.toJson(biomodels));
		
		Configuration templateConfiguration = application.getTemplateConfiguration();

		Representation formFtl = new ClientResource(LocalReference.createClapReference("/biomodels.ftl")).get();
		TemplateRepresentation templateRepresentation = new TemplateRepresentation(formFtl, templateConfiguration, dataModel, MediaType.TEXT_HTML);
		return templateRepresentation;
	}


	private BiomodelRepresentation[] getBiomodelRepresentations(User vcellUser) {
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
			} catch (DataAccessException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to retrieve biomodels from VCell Database : "+e.getMessage());
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to retrieve biomodels from VCell Database : "+e.getMessage());
			}
			return biomodelReps.toArray(new BiomodelRepresentation[0]);
//		}
	}
 }

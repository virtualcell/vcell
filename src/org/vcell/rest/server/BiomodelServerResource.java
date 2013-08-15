package org.vcell.rest.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.RequestInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.common.BiomodelRepresentation;
import org.vcell.rest.common.BiomodelResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;

import cbit.vcell.modeldb.BioModelRep;

import com.google.gson.Gson;

import freemarker.template.Configuration;

public class BiomodelServerResource extends AbstractServerResource implements BiomodelResource {

	private String biomodelid;
	
	
    @Override
    protected RepresentationInfo describe(MethodInfo methodInfo,
            Class<?> representationClass, Variant variant) {
        RepresentationInfo result = new RepresentationInfo(variant);
        result.setReference("biomodel");
        return result;
    }

    /**
     * Retrieve the account identifier based on the URI path variable
     * "accountId" declared in the URI template attached to the application
     * router.
     */
    @Override
    protected void doInit() throws ResourceException {
        String simTaskIdAttribute = getAttribute(VCellApiApplication.BIOMODELID);

        if (simTaskIdAttribute != null) {
            this.biomodelid = simTaskIdAttribute;
            setName("Resource for biomodel \"" + this.biomodelid + "\"");
            setDescription("The resource describing the simulation task id \"" + this.biomodelid + "\"");
        } else {
            setName("simulation task resource");
            setDescription("The resource describing a simulation task");
        }
    }
	

	@Override
	protected void describeGet(MethodInfo info) {
		super.describeGet(info);
		RequestInfo requestInfo = new RequestInfo();
        List<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
        parameterInfos.add(new ParameterInfo("biomodelid",false,"string",ParameterStyle.TEMPLATE,"VCell biomodel id"));
 		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}

	@Override
	public BiomodelRepresentation get_json() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse());
		
        BiomodelRepresentation biomodelRep = getBiomodelRepresentation(vcellUser);
        
        if (biomodelRep != null){
        	return biomodelRep;
        }
        throw new RuntimeException("biomodel not found");
	}

	@Override
	public Representation get_html() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse());
		
		BiomodelRepresentation biomodel = getBiomodelRepresentation(vcellUser);
		if (biomodel==null){
			throw new RuntimeException("biomodel not found");
		}
		Map<String,Object> dataModel = new HashMap<String,Object>();
		
		dataModel.put("bmId", getQueryValue(VCellApiApplication.BIOMODELID));

		dataModel.put("biomodel", biomodel);
		
		
		if (vcellUser!=null){
			dataModel.put("userid",vcellUser.getName());
		}
		
		Gson gson = new Gson();
		dataModel.put("jsonResponse",gson.toJson(biomodel));
		
		Configuration templateConfiguration = application.getTemplateConfiguration();

		Representation formFtl = new ClientResource(LocalReference.createClapReference("/biomodel.ftl")).get();
		TemplateRepresentation templateRepresentation = new TemplateRepresentation(formFtl, templateConfiguration, dataModel, MediaType.TEXT_HTML);
		return templateRepresentation;
	}
	
	private BiomodelRepresentation getBiomodelRepresentation(User vcellUser) {
//		if (!application.authenticate(getRequest(), getResponse())){
//			// not authenticated
//			return new SimulationTaskRepresentation[0];
//		}else{
			RestDatabaseService restDatabaseService = ((VCellApiApplication)getApplication()).getRestDatabaseService();
			try {
				BioModelRep bioModelRep = restDatabaseService.query(this,vcellUser);
				BiomodelRepresentation biomodelRep = new BiomodelRepresentation(bioModelRep);
				return biomodelRep;
			} catch (DataAccessException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to retrieve biomodels from VCell Database : "+e.getMessage());
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to retrieve biomodels from VCell Database : "+e.getMessage());
			}
//		}
	}


}

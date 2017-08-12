package org.vcell.rest.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
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
import org.vcell.rest.VCellApiApplication.AuthenticationPolicy;
import org.vcell.rest.common.SimulationRepresentation;
import org.vcell.rest.common.SimulationResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

import com.google.gson.Gson;

import cbit.vcell.mapping.MappingException;
import cbit.vcell.math.MathException;
import cbit.vcell.matrix.MatrixException;
import cbit.vcell.model.ModelException;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.xml.XmlParseException;
import freemarker.template.Configuration;

public class BiomodelSimulationServerResource extends AbstractServerResource implements SimulationResource {

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
        parameterInfos.add(new ParameterInfo(VCellApiApplication.BIOMODELID,false,"string",ParameterStyle.TEMPLATE,"VCell biomodel id"));
        parameterInfos.add(new ParameterInfo(VCellApiApplication.SIMULATIONID,false,"string",ParameterStyle.TEMPLATE,"VCell simulation id"));
 		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}

	@Override
	public SimulationRepresentation get_json() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.prohibitInvalidCredentials);
		
        SimulationRepresentation simulationRep = getBiomodelSimulationRepresentation(vcellUser);
        
        if (simulationRep != null){
        	return simulationRep;
        }
        throw new RuntimeException("simulation not found");
	}

	@Override
	public Representation get_html() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
		
		SimulationRepresentation simulation = getBiomodelSimulationRepresentation(vcellUser);
		if (simulation==null){
			throw new RuntimeException("simulation not found");
		}
		Map<String,Object> dataModel = new HashMap<String,Object>();
		
		dataModel.put("simulation", simulation);
		
		
		dataModel.put("loginurl", "/"+VCellApiApplication.LOGINFORM);  // +"?"+VCellApiApplication.REDIRECTURL_FORMNAME+"="+getRequest().getResourceRef().toUrl());
		dataModel.put("logouturl", "/"+VCellApiApplication.LOGOUT+"?"+VCellApiApplication.REDIRECTURL_FORMNAME+"="+Reference.encode(getRequest().getResourceRef().toUrl().toString()));
		if (vcellUser!=null){
			dataModel.put("userid",vcellUser.getName());
		}
		
		Gson gson = new Gson();
		dataModel.put("jsonResponse",gson.toJson(simulation));
		
		Configuration templateConfiguration = application.getTemplateConfiguration();

		Representation formFtl = new ClientResource(LocalReference.createClapReference("/simulation.ftl")).get();
		TemplateRepresentation templateRepresentation = new TemplateRepresentation(formFtl, templateConfiguration, dataModel, MediaType.TEXT_HTML);
		return templateRepresentation;
	}
	
	private SimulationRepresentation getBiomodelSimulationRepresentation(User vcellUser) {
//		if (!application.authenticate(getRequest(), getResponse())){
//			// not authenticated
//			return new SimulationTaskRepresentation[0];
//		}else{
			RestDatabaseService restDatabaseService = ((VCellApiApplication)getApplication()).getRestDatabaseService();
			try {
				SimulationRepresentation simulationRepresentation = restDatabaseService.query(this,vcellUser);
				return simulationRepresentation;
			} catch (PermissionException ee){
				ee.printStackTrace();
				throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "not authorized");
			} catch (DataAccessException | SQLException | ExpressionException | XmlParseException | MappingException | MathException
					| MatrixException | ModelException e) {
				e.printStackTrace();
				throw new RuntimeException("failed to retrieve biomodels from VCell Database : "+e.getMessage());
			}
//		}
	}


}

package org.vcell.rest.server;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.RequestInfo;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.VCellApiApplication.AuthenticationPolicy;
import org.vcell.rest.common.BiomodelSimulationSaveResource;
import org.vcell.rest.common.OverrideRepresentation;
import org.vcell.rest.server.RestDatabaseService.SimulationSaveResponse;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

public class BiomodelSimulationSaveServerResource extends AbstractServerResource implements BiomodelSimulationSaveResource {

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
            setDescription("The resource for saving a modified version of the simulation task id \"" + this.biomodelid + "\"");
        } else {
            setName("simulation task resource");
            setDescription("The resource describing a simulation task");
        }
    }
	

	@Override
	protected void describePost(MethodInfo info) {
		super.describePost(info);
		RequestInfo requestInfo = new RequestInfo();
        List<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
        parameterInfos.add(new ParameterInfo(VCellApiApplication.BIOMODELID,false,"string",ParameterStyle.TEMPLATE,"VCell biomodel id"));
        parameterInfos.add(new ParameterInfo(VCellApiApplication.SIMULATIONID,false,"string",ParameterStyle.TEMPLATE,"VCell simulation id"));
 		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}

	@Override
	public void save(JsonRepresentation jsonOverrides) throws JSONException {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.prohibitInvalidCredentials);
		ArrayList<OverrideRepresentation> overrideRepresentations = new ArrayList<OverrideRepresentation>();
		if (jsonOverrides!=null && jsonOverrides.getMediaType().isCompatible(MediaType.APPLICATION_JSON)){
			JSONObject obj = jsonOverrides.getJsonObject();
			JSONArray overrideArray = obj.getJSONArray("overrides");
			for (int i=0; i<overrideArray.length(); i++){
				JSONObject overrideObj = overrideArray.getJSONObject(i);
				String name = overrideObj.getString("name");
				String type = overrideObj.getString("type");
				int cardinality = overrideObj.getInt("cardinality");
				String[] values = new String[0];
				if (overrideObj.has("values")){
					JSONArray valuesArray = overrideObj.getJSONArray("values");
					values = new String[valuesArray.length()];
					for (int j=0; j<valuesArray.length(); j++){
						values[j] = valuesArray.getString(j);
					}
				}
				String expression = null;
				if (overrideObj.has("expression")){
					expression = overrideObj.getString("expression");
				}
				OverrideRepresentation overrideRep = new OverrideRepresentation(name, type, cardinality, values, expression);
				overrideRepresentations.add(overrideRep);
			}
		}
		RestDatabaseService restDatabaseService = application.getRestDatabaseService();
		try {
			if (vcellUser==null){
				throw new PermissionException("must be authenticated to copy simulation");
			}
			SimulationSaveResponse simulationSavedResponse = restDatabaseService.saveSimulation(this, vcellUser, overrideRepresentations);
			JSONObject responseJson = new JSONObject();
			String redirectURL = "/"+VCellApiApplication.BIOMODEL+
					"/"+simulationSavedResponse.newBioModel.getVersion().getVersionKey()+
					"/"+VCellApiApplication.SIMULATION+
					"/"+simulationSavedResponse.newSimulation.getKey().toString();
			
			responseJson.put("redirect",redirectURL);
			responseJson.put("status","simulation saved");
			JsonRepresentation representation = new JsonRepresentation(responseJson);

			redirectSeeOther(redirectURL);
			
			//return representation;
		} catch (PermissionException e) {
			e.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "not authorized to save simulation");
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "simulation not found");
		} catch (Exception e){
			e.printStackTrace(System.out);
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}


}

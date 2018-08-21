package org.vcell.rest.server;

import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.ResourceException;
import org.vcell.api.client.VCellOptClient;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.common.OptimizationResource;

public class OptimizationServerResource extends AbstractServerResource implements OptimizationResource {

	@Override
	public JsonRepresentation get_json() {
		try {
//			VCellApiApplication application = ((VCellApiApplication)getApplication());
//			User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.prohibitInvalidCredentials);
			String optimizationId = (String)getRequestAttributes().get(VCellApiApplication.OPTIMIZATIONID);
			
			VCellOptClient optClient = new VCellOptClient("opt",8080);
			String optRunString = optClient.getOptRunJson(optimizationId);
			JsonRepresentation optRunJsonRep = new JsonRepresentation(optRunString);
			return optRunJsonRep;
//		} catch (PermissionException e) {
//			e.printStackTrace();
//			throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "permission denied to requested resource");
		} catch (Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}
}

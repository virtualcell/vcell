package org.vcell.restopt.server;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.protocol.TJSONProtocol;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.wadl.WadlServerResource;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.vcell.optimization.thrift.OptProblem;
import org.vcell.restopt.VCellOptApplication;
import org.vcell.restopt.common.OptimizationRunResource;
import org.vcell.util.PermissionException;

public class OptimizationRunServerResource extends WadlServerResource implements OptimizationRunResource {	

	@Override
	@Post
	public Representation run(Representation optProblemJson) throws JSONException {
		try {
			VCellOptApplication application = ((VCellOptApplication)getApplication());
			//User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
			if (optProblemJson!=null && optProblemJson.getMediaType().isCompatible(MediaType.APPLICATION_JSON)){
				JsonRepresentation jsonRep = new JsonRepresentation(optProblemJson);
				JSONObject json = jsonRep.getJsonObject();
				System.out.println(json);
				TDeserializer deserializer = new TDeserializer(new TJSONProtocol.Factory());
				OptProblem optProblem = new OptProblem();
				deserializer.deserialize(optProblem, json.toString().getBytes());
	
				String optimizationId = application.getOptServerImpl().submit(optProblem);
				
				String redirectURL = "/"+VCellOptApplication.OPTIMIZATION+"/"+optimizationId;
				System.out.println("should be redirected to "+redirectURL+" but leaving as client responsibility for now to create new url");
				getResponse().setLocationRef(redirectURL);
				getResponse().setStatus(Status.SUCCESS_ACCEPTED);
				Representation representation = new StringRepresentation(optimizationId,MediaType.TEXT_PLAIN);
				return representation;
			}else{
				throw new RuntimeException("unexpected post representation "+optProblemJson);
			}
		} catch (PermissionException e) {
			e.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "not authorized to submit optimization");
		} catch (Exception e){
			e.printStackTrace(System.out);
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}


}

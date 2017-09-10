package org.vcell.rest.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.protocol.TJSONProtocol;
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
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.vcell.optimization.thrift.OptProblem;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.common.OptimizationRunResource;
import org.vcell.util.PermissionException;

public class OptimizationRunServerResource extends AbstractServerResource implements OptimizationRunResource {

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
	@Post
	public Representation run(Representation optProblemJson) throws JSONException {
		try {
			VCellApiApplication application = ((VCellApiApplication)getApplication());
			//User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
			if (optProblemJson!=null && optProblemJson.getMediaType().isCompatible(MediaType.APPLICATION_JSON)){
				JsonRepresentation jsonRep = new JsonRepresentation(optProblemJson);
				JSONObject json = jsonRep.getJsonObject();
				System.out.println(json);
				TDeserializer deserializer = new TDeserializer(new TJSONProtocol.Factory());
				OptProblem optProblem = new OptProblem();
				deserializer.deserialize(optProblem, json.toString().getBytes());
	
				String optimizationId = application.getOptServerImpl().submit(optProblem);
				
				String redirectURL = "/"+VCellApiApplication.OPTIMIZATION+"/"+optimizationId;
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

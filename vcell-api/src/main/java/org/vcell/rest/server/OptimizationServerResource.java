package org.vcell.rest.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TJSONProtocol;
import org.json.JSONObject;
import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.RequestInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.vcell.optimization.CopasiServicePython;
import org.vcell.optimization.OptServerImpl.OptRunContext;
import org.vcell.optimization.thrift.OptProblem;
import org.vcell.optimization.thrift.OptRun;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.VCellApiApplication.AuthenticationPolicy;
import org.vcell.rest.common.OptimizationResource;
import org.vcell.util.FileUtils;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

import cbit.vcell.resource.ResourceUtil;
import freemarker.template.Configuration;

public class OptimizationServerResource extends AbstractServerResource implements OptimizationResource {

	private String publicationid;
	
	
    @Override
    protected RepresentationInfo describe(MethodInfo methodInfo,
            Class<?> representationClass, Variant variant) {
        RepresentationInfo result = new RepresentationInfo(variant);
        result.setReference("publication");
        return result;
    }

    /**
     * Retrieve the account identifier based on the URI path variable
     * "accountId" declared in the URI template attached to the application
     * router.
     */
    @Override
    protected void doInit() throws ResourceException {
        String pubIdAttribute = getAttribute(VCellApiApplication.PUBLICATIONID);

        if (pubIdAttribute != null) {
            this.publicationid = pubIdAttribute;
            setName("Resource for publication \"" + this.publicationid + "\"");
            setDescription("The resource describing the publication id \"" + this.publicationid + "\"");
        } else {
            setName("publication resource");
            setDescription("The resource describing a publication");
        }
    }
	

	@Override
	protected void describeGet(MethodInfo info) {
		super.describeGet(info);
		RequestInfo requestInfo = new RequestInfo();
        List<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
        parameterInfos.add(new ParameterInfo("publicationid",false,"string",ParameterStyle.TEMPLATE,"VCell publication id"));
 		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}

	@Override
	public JsonRepresentation get_json() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.prohibitInvalidCredentials);
		
        OptRun optRun = getOptRun(vcellUser);
        
        try {
			TSerializer serializer = new TSerializer(new TJSONProtocol.Factory());
			String optRunJson = new String(serializer.serialize(optRun));
			JsonRepresentation optRunJsonRep = new JsonRepresentation(optRunJson);
			return optRunJsonRep;
		} catch (Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}

	@Override
	public Representation get_html() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
		
		OptRun optRun = getOptRun(vcellUser);
		if (optRun==null){
			throw new RuntimeException("optimization not found");
		}
		try {
			Map<String,Object> dataModel = new HashMap<String,Object>();
			
			dataModel.put("loginurl", "/"+VCellApiApplication.LOGINFORM);  // +"?"+VCellApiApplication.REDIRECTURL_FORMNAME+"="+getRequest().getResourceRef().toUrl());
			dataModel.put("logouturl", "/"+VCellApiApplication.LOGOUT+"?"+VCellApiApplication.REDIRECTURL_FORMNAME+"="+Reference.encode(getRequest().getResourceRef().toUrl().toString()));
			if (vcellUser!=null){
				dataModel.put("userid",vcellUser.getName());
			}
			
			dataModel.put("optId", getQueryValue(VCellApiApplication.OPTIMIZATIONID));
	
			TSerializer serializer = new TSerializer(new TJSONProtocol.Factory());
			String optRunJson = new String(serializer.serialize(optRun));

			dataModel.put("optimization", new JSONObject(optRunJson));
			
			dataModel.put("jsonResponse",optRunJson);
			
			Configuration templateConfiguration = application.getTemplateConfiguration();
	
			Representation formFtl = new ClientResource(LocalReference.createClapReference("/optimization.ftl")).get();
			TemplateRepresentation templateRepresentation = new TemplateRepresentation(formFtl, templateConfiguration, dataModel, MediaType.TEXT_HTML);
			return templateRepresentation;
		}catch (Exception e){
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}
	
	private OptRun getOptRun(User vcellUser) {
//		if (!application.authenticate(getRequest(), getResponse())){
//			// not authenticated
//			return new SimulationTaskRepresentation[0];
//		}else{
			try {
				String optimizationId = (String)getRequestAttributes().get(VCellApiApplication.OPTIMIZATIONID);
				VCellApiApplication application = ((VCellApiApplication)getApplication());
				OptRunContext optRunContext = application.getOptServerImpl().getOptRunContextByOptimizationId(optimizationId);
				if (optRunContext == null){
					throw new ObjectNotFoundException("optimization id '"+optimizationId+"' not found");
				}
				switch (optRunContext.getStatus()){
					case Complete:{
						OptRun optRun = CopasiServicePython.readOptRun(optRunContext.getOptRunBinaryFile());
						return optRun;
					}
					case Queued:
					case Running:
					case Failed:{
						OptProblem optProblem = CopasiServicePython.readOptProblem(optRunContext.getOptProblemBinaryFile());
						OptRun optRun = new OptRun();
						optRun.setOptProblem(optProblem);
						optRun.setStatus(optRunContext.getStatus());
						optRun.setStatusMessage(optRunContext.getStatus().name());
						return optRun;
					}
					default:{
						throw new RuntimeException("unexpected optimization status '"+optRunContext.getStatus()+"'");
					}
				}
			} catch (PermissionException e) {
				e.printStackTrace();
				throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "permission denied to requested resource");
			} catch (ObjectNotFoundException e) {
				e.printStackTrace();
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "optimization not found");
			} catch (Exception e){
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			}
//		}
	}


}

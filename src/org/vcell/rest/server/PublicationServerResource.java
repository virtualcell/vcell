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
import org.vcell.rest.common.PublicationRepresentation;
import org.vcell.rest.common.PublicationResource;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

import com.google.gson.Gson;

import cbit.vcell.modeldb.PublicationRep;
import freemarker.template.Configuration;

public class PublicationServerResource extends AbstractServerResource implements PublicationResource {

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
	public PublicationRepresentation get_json() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.prohibitInvalidCredentials);
		
        PublicationRepresentation publicationRep = getPublicationRepresentation(vcellUser);
        
        if (publicationRep != null){
        	return publicationRep;
        }
        throw new RuntimeException("publication not found");
	}

	@Override
	public Representation get_html() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
		
		PublicationRepresentation publication = getPublicationRepresentation(vcellUser);
		if (publication==null){
			throw new RuntimeException("publication not found");
		}
		Map<String,Object> dataModel = new HashMap<String,Object>();
		
		dataModel.put("loginurl", "/"+VCellApiApplication.LOGINFORM);  // +"?"+VCellApiApplication.REDIRECTURL_FORMNAME+"="+getRequest().getResourceRef().toUrl());
		dataModel.put("logouturl", "/"+VCellApiApplication.LOGOUT+"?"+VCellApiApplication.REDIRECTURL_FORMNAME+"="+Reference.encode(getRequest().getResourceRef().toUrl().toString()));
		if (vcellUser!=null){
			dataModel.put("userid",vcellUser.getName());
		}
		
		dataModel.put("pubId", getQueryValue(VCellApiApplication.PUBLICATIONID));

		dataModel.put("publication", publication);
		
		
		Gson gson = new Gson();
		dataModel.put("jsonResponse",gson.toJson(publication));
		
		Configuration templateConfiguration = application.getTemplateConfiguration();

		Representation formFtl = new ClientResource(LocalReference.createClapReference("/publication.ftl")).get();
		TemplateRepresentation templateRepresentation = new TemplateRepresentation(formFtl, templateConfiguration, dataModel, MediaType.TEXT_HTML);
		return templateRepresentation;
	}
	
	private PublicationRepresentation getPublicationRepresentation(User vcellUser) {
//		if (!application.authenticate(getRequest(), getResponse())){
//			// not authenticated
//			return new SimulationTaskRepresentation[0];
//		}else{
			RestDatabaseService restDatabaseService = ((VCellApiApplication)getApplication()).getRestDatabaseService();
			try {
				PublicationRep publicationRep = restDatabaseService.query(this,vcellUser);
				PublicationRepresentation publicationRepresentation = new PublicationRepresentation(publicationRep);
				return publicationRepresentation;
			} catch (PermissionException e) {
				e.printStackTrace();
				throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "permission denied to requested resource");
			} catch (ObjectNotFoundException e) {
				e.printStackTrace();
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "publication not found");
			} catch (Exception e){
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
			}
//		}
	}


}

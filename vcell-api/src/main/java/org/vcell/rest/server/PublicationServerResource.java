package org.vcell.rest.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.restlet.data.Form;
import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Parameter;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.RequestInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.VCellApiApplication.AuthenticationPolicy;
import org.vcell.rest.common.BiomodelReferenceRepresentation;
import org.vcell.rest.common.MathmodelReferenceRepresentation;
import org.vcell.rest.common.PublicationRepresentation;
import org.vcell.rest.common.PublicationResource;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.User.SPECIALS;

import com.google.gson.Gson;

import cbit.vcell.modeldb.BioModelReferenceRep;
import cbit.vcell.modeldb.MathModelReferenceRep;
import cbit.vcell.modeldb.PublicationRep;
import cbit.vcell.resource.PropertyLoader;
import freemarker.template.Configuration;

public class PublicationServerResource extends AbstractServerResource implements PublicationResource {

	private static final String AUTOMATICALLY_GENERATED = "Automatically Generated";
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
		Object pubIdObj = getRequestAttributes().get(VCellApiApplication.PUBLICATIONID);
        PublicationRepresentation publicationRep = getPublicationRepresentation(((VCellApiApplication)getApplication()).getRestDatabaseService(),vcellUser,new KeyValue(pubIdObj.toString()));
        
        if (publicationRep != null){
        	return publicationRep;
        }
        throw new RuntimeException("publication not found");
	}

	@Post
    public Representation handleForm(Representation entity) {
		try {
        	String myHost = getHostRef().getHostDomain();
        	String allowedPublicationHost = PropertyLoader.getProperty(PropertyLoader.vcellPublicationHost,"");
			if(!allowedPublicationHost.equals(myHost)) {
        		throw new PermissionException("Host '"+myHost+"' not allowed to edit publications");
        	}
        Form form = new Form(entity);
    	VCellApiApplication application = ((VCellApiApplication)getApplication());
    	User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);

		if (vcellUser==null){
			throw new PermissionException("must be authenticated to edit publication info");
		}

        // The form contains input with names "user" and "password"
        String pubop = form.getFirstValue("pubop");
//        String password = form.getFirstValue("password");
        Map<String,Object> dataModel = new HashMap<String,Object>();
        if(pubop == null) {
        	return new StringRepresentation(("value of publication 'operation' cannot be null"));
        }else if(pubop.equals("editNew")) {
        	PublicationRepresentation value = new PublicationRepresentation();
        	value.pubKey = AUTOMATICALLY_GENERATED;
			dataModel.put("publicationRepr", value);
        }else if(pubop.equals("editWithKey")) {
        	Object pubidObj = getRequestAttributes().get(VCellApiApplication.PUBLICATIONID);
        	PublicationRepresentation publication = getPublicationRepresentation(((VCellApiApplication)getApplication()).getRestDatabaseService(),vcellUser,new KeyValue(pubidObj.toString()));
        	dataModel.put("publicationRepr", publication);
        }else if(pubop.equals("applyEdit")) {
        	SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.US);
//        	PublicationRepresentation publication = getPublicationRepresentation(((VCellApiApplication)getApplication()).getRestDatabaseService(),vcellUser,new KeyValue(form.getFirstValue("pubid")));
        	String[] authors = StringUtils.split(form.getFirstValue("authors"), ";");
        	for (int i = 0; i < authors.length; i++) {
				authors[i] = authors[i].trim();
			}
        	String thePubID = form.getFirstValue("pubId");
        	BioModelReferenceRep[] bioModelReferenceReps = (BioModelReferenceRep[])convertToReferenceRep(form.getFirstValue("biomodelReferences"),BioModelReferenceRep.class);
			MathModelReferenceRep[] mathModelReferenceReps = (MathModelReferenceRep[])convertToReferenceRep(form.getFirstValue("mathmodelReferences"),MathModelReferenceRep.class);
			PublicationRep publicationRep = new PublicationRep(
        		(thePubID == null || thePubID.equals(AUTOMATICALLY_GENERATED)?null:new KeyValue(Integer.valueOf(thePubID).toString())),
        		form.getFirstValue("title"),
        		authors,
        		(form.getFirstValue("year")==null || form.getFirstValue("year").trim().length()==0?null:Integer.valueOf(form.getFirstValue("year"))),
        		form.getFirstValue("citation"), 
        		form.getFirstValue("pubmedid"),
        		form.getFirstValue("doi"),
        		form.getFirstValue("endnoteid"),
        		form.getFirstValue("url"),
        		bioModelReferenceReps,
        		mathModelReferenceReps,
        		form.getFirstValue("wittid"),
        		(form.getFirstValue("pubdate")==null || form.getFirstValue("pubdate").trim().length()==0?null:sdf.parse(form.getFirstValue("pubdate"))),
        		(form.getFirstValue("bcurated")==null || form.getFirstValue("bcurated").trim().length()==0?false:(form.getFirstValue("bcurated").equalsIgnoreCase("T")?true:false)));
        	KeyValue savedOrEditedPubID = ((VCellApiApplication)getApplication()).getRestDatabaseService().savePublicationRep(publicationRep, vcellUser);
//        	String address = getRequest().getClientInfo().getAddress();
//        	int port = getRequest().getClientInfo().getPort();
        	StringRepresentation s = getPubInfoHtml(myHost, savedOrEditedPubID);
        	return s;
        }else if(pubop.equals("makepublic")) {
        	String[] bmKeys = form.getValuesArray("bmpublic");
        	KeyValue[] publishTheseBiomodels = new KeyValue[(bmKeys==null?0:bmKeys.length)];
        	for (int i = 0; i < publishTheseBiomodels.length; i++) {
        		publishTheseBiomodels[i] = new KeyValue(bmKeys[i]);
			}
        	String[] mmKeys = form.getValuesArray("mmpublic");
        	KeyValue[] publishTheseMathmodels = new KeyValue[(mmKeys==null?0:mmKeys.length)];
        	for (int i = 0; i < publishTheseMathmodels.length; i++) {
        		publishTheseMathmodels[i] = new KeyValue(mmKeys[i]);
			}
        	if(publishTheseBiomodels.length>0 || publishTheseMathmodels.length > 0){
        		((VCellApiApplication)getApplication()).getRestDatabaseService().publishDirectly(publishTheseBiomodels, publishTheseMathmodels, vcellUser);
        	}
        	Object pubidObj = getRequestAttributes().get(VCellApiApplication.PUBLICATIONID);
        	StringRepresentation s = getPubInfoHtml(myHost, new KeyValue(pubidObj.toString()));
        	return s;
        }else {
        	return new StringRepresentation(("value of pubop="+pubop+" not expected").toCharArray());
        }

		Configuration templateConfiguration = application.getTemplateConfiguration();
		Representation myRepresentation = new ClientResource(LocalReference.createClapReference("/newpublication.ftl")).get();
		TemplateRepresentation templateRepresentation = new TemplateRepresentation(myRepresentation, templateConfiguration, dataModel, MediaType.TEXT_HTML);
		return templateRepresentation;
		}catch(Exception e) {
			return new StringRepresentation(e.getClass().getName()+" "+e.getMessage());
		}
    }

	private StringRepresentation getPubInfoHtml(String myHost, KeyValue savedOrEditedPubID) {
		String rootRefStr = "https://"+myHost;
		StringRepresentation  s = new StringRepresentation(("<html>" + 
		"<head><meta http-equiv=\"refresh\" content=\"0; URL='"+rootRefStr+"/publication/"+savedOrEditedPubID.toString()+"' /></head>"
				+ "<body>If you are not redirected automatically, click the saved/updated publication ID <a href=\""+rootRefStr+"/publication/"+savedOrEditedPubID.toString()+"\">"+savedOrEditedPubID.toString()+"</a>.\n" + 
				"</boady></html>").toCharArray());
		s.setMediaType(MediaType.TEXT_HTML);
		return s;
	}
	private Object convertToReferenceRep(String str,Class classType) throws Exception{
		if(str == null || str.trim().length()==0) {
			return null;
		}
		String[] modelKeys = StringUtils.split(str, ", ");
		for (int i = 0; modelKeys != null && i < modelKeys.length; i++) {
			modelKeys[i] = modelKeys[i].trim();
		}
		if(classType.equals(BioModelReferenceRep.class)) {
			BioModelReferenceRep[] biomodelReferenceReps = new BioModelReferenceRep[modelKeys.length];
			for (int i = 0; i < modelKeys.length; i++) {
				biomodelReferenceReps[i] = new BioModelReferenceRep(new KeyValue(modelKeys[i]), null, null,null);
			}
			return biomodelReferenceReps;
		}else if(classType.equals(MathModelReferenceRep.class)) {
			MathModelReferenceRep[] mathmodelReferenceReps = new MathModelReferenceRep[modelKeys.length];
			for (int i = 0; i < modelKeys.length; i++) {
				mathmodelReferenceReps[i] = new MathModelReferenceRep(new KeyValue(modelKeys[i]), null, null,null);
			}	
			return mathmodelReferenceReps;
		}
		throw new Exception("Unexpected class type "+classType.getName());
	}
	@Override
	public Representation get_html() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
		PublicationRepresentation publication = null;
		Map<String,Object> dataModel = new HashMap<String,Object>();
		Object pubIdObj = getRequestAttributes().get(VCellApiApplication.PUBLICATIONID);
		if(pubIdObj != null) {
			publication = getPublicationRepresentation(((VCellApiApplication)getApplication()).getRestDatabaseService(),vcellUser,new KeyValue(pubIdObj.toString()));
		}else {
			throw new RuntimeException("publication not found");
		}
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
	
	private static PublicationRepresentation getPublicationRepresentation(RestDatabaseService restDatabaseService,User vcellUser,KeyValue pubID) {
//		if (!application.authenticate(getRequest(), getResponse())){
//			// not authenticated
//			return new SimulationTaskRepresentation[0];
//		}else{
//			RestDatabaseService restDatabaseService = ((VCellApiApplication)getApplication()).getRestDatabaseService();
			try {
				PublicationRep publicationRep = restDatabaseService.getPublicationRep(pubID, vcellUser);
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

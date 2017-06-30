package org.vcell.rest.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.data.LocalReference;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
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
import org.vcell.rest.VCellApiApplication.AuthenticationPolicy;
import org.vcell.rest.common.PublicationRepresentation;
import org.vcell.rest.common.PublicationsResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.User;

import com.google.gson.Gson;

import cbit.vcell.modeldb.PublicationRep;
import cbit.vcell.parser.ExpressionException;
import freemarker.template.Configuration;

public class PublicationsServerResource extends AbstractServerResource implements PublicationsResource {

	public static final String PARAM_PUB_ID = "pubId";
	public static final String PARAM_ORDERBY = "orderBy";
	public static final String PARAM_ORDERBY_TITLE_ASC = "title_asc";
	public static final String PARAM_ORDERBY_TITLE_DESC = "title_desc";
	public static final String PARAM_ORDERBY_YEAR_ASC = "year_asc";
	public static final String PARAM_ORDERBY_YEAR_DESC = "year_desc";

	@Override
	protected void doInit() throws ResourceException {
		setName("Publications resource");
		setDescription("The resource containing the list of Publications");
	}
	
	@Override
    protected void describe(ApplicationInfo applicationInfo) {
        RepresentationInfo rep = new RepresentationInfo(MediaType.APPLICATION_JSON);
        rep.setIdentifier(VCellApiApplication.PUBLICATION);
        applicationInfo.getRepresentations().add(rep);

        DocumentationInfo doc = new DocumentationInfo();
        doc.setTitle(VCellApiApplication.PUBLICATION);
        doc.setTextContent("jdom containing list of biomodels");
        rep.getDocumentations().add(doc);
    }

	@Override
	protected void describeGet(MethodInfo info) {
		super.describeGet(info);
		RequestInfo requestInfo = new RequestInfo();
        List<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
        parameterInfos.add(new ParameterInfo(PARAM_ORDERBY,false,"string",ParameterStyle.QUERY,"order by"));
 		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}
	
	@Override
    public PublicationRepresentation[] get_json() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.prohibitInvalidCredentials);
		
        return getPublicationRepresentations(vcellUser);
    }
    
	@Override
	public Representation get_html() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
		
		PublicationRepresentation[] publications = getPublicationRepresentations(vcellUser);
		Map<String,Object> dataModel = new HashMap<String,Object>();
		
		dataModel.put("loginurl", "/"+VCellApiApplication.LOGINFORM);  // +"?"+VCellApiApplication.REDIRECTURL_FORMNAME+"="+getRequest().getResourceRef().toUrl());
		dataModel.put("logouturl", "/"+VCellApiApplication.LOGOUT+"?"+VCellApiApplication.REDIRECTURL_FORMNAME+"="+Reference.encode(getRequest().getResourceRef().toUrl().toString()));
		if (vcellUser!=null){
			dataModel.put("userid",vcellUser.getName());
		}
		
		dataModel.put("pubId", getQueryValue(PARAM_PUB_ID));
		dataModel.put("orderBy", getQueryValue(PARAM_ORDERBY));

		dataModel.put("publications", Arrays.asList(publications));
		
		
		Gson gson = new Gson();
		dataModel.put("jsonResponse",gson.toJson(publications));
		
		Configuration templateConfiguration = application.getTemplateConfiguration();

		Representation formFtl = new ClientResource(LocalReference.createClapReference("/publications.ftl")).get();
		TemplateRepresentation templateRepresentation = new TemplateRepresentation(formFtl, templateConfiguration, dataModel, MediaType.TEXT_HTML);
		return templateRepresentation;
	}


	private PublicationRepresentation[] getPublicationRepresentations(User vcellUser) {
		ArrayList<PublicationRepresentation> publicationRepresentations = new ArrayList<PublicationRepresentation>();
		RestDatabaseService restDatabaseService = ((VCellApiApplication)getApplication()).getRestDatabaseService();
		try {
			PublicationRep[] publicationReps = restDatabaseService.query(this, vcellUser);
			for (PublicationRep publicationRep : publicationReps) {
				PublicationRepresentation publicationRepresentation = new PublicationRepresentation(publicationRep);
				publicationRepresentations.add(publicationRepresentation);
			}
		} catch (PermissionException ee){
			ee.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "not authorized");
		} catch (DataAccessException | SQLException | ExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to retrieve biomodels from VCell Database : "+e.getMessage());
		}
		return publicationRepresentations.toArray(new PublicationRepresentation[0]);
	}
 }

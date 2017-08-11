package org.vcell.rest.server;

import java.util.ArrayList;
import java.util.List;

import org.restlet.data.CacheDirective;
import org.restlet.data.MediaType;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.DocumentationInfo;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.RequestInfo;
import org.restlet.resource.ResourceException;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.common.AccessTokenRepresentation;
import org.vcell.rest.common.AccessTokenResource;
import org.vcell.util.document.User;

import cbit.vcell.modeldb.ApiAccessToken;
import cbit.vcell.modeldb.ApiClient;

public class AccessTokenServerResource extends AbstractServerResource implements AccessTokenResource {

	public static final String PARAM_USER_ID = "user_id";
	public static final String PARAM_USER_PASSWORD = "user_password";
	public static final String PARAM_CLIENT_ID = "client_id";

	@Override
	protected void doInit() throws ResourceException {
		setName("SimulationTask resource");
		setDescription("The resource containing the list of Biomodels");
	}
	
	@Override
    protected void describe(ApplicationInfo applicationInfo) {
        RepresentationInfo rep = new RepresentationInfo(MediaType.APPLICATION_JSON);
        rep.setIdentifier(VCellApiApplication.BIOMODEL);
        applicationInfo.getRepresentations().add(rep);

        DocumentationInfo doc = new DocumentationInfo();
        doc.setTitle(VCellApiApplication.BIOMODEL);
        doc.setTextContent("jdom containing list of biomodels");
        rep.getDocumentations().add(doc);
    }

	@Override
	protected void describeGet(MethodInfo info) {
		super.describeGet(info);
		RequestInfo requestInfo = new RequestInfo();
        List<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
        parameterInfos.add(new ParameterInfo(PARAM_CLIENT_ID,false,"string",ParameterStyle.QUERY,"VCell Client id"));
        parameterInfos.add(new ParameterInfo(PARAM_USER_ID,false,"string",ParameterStyle.QUERY,"VCell User id"));
        parameterInfos.add(new ParameterInfo(PARAM_USER_PASSWORD,false,"string",ParameterStyle.QUERY,"VCell simulation database id"));
  		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}
	
	@Override
    public AccessTokenRepresentation get_json() {
		VCellApiApplication application = ((VCellApiApplication)getApplication());
		String clientId = getQueryValue(PARAM_CLIENT_ID);
		String userId = getQueryValue(PARAM_USER_ID);
		String userPassword = getQueryValue(PARAM_USER_PASSWORD);
		
		try {
			ApiClient apiClient = application.getUserVerifier().getApiClient(clientId);
			if (apiClient==null){
				throw new RuntimeException("client not found");
			}
			
			User authenticatedUser = application.getUserVerifier().authenticateUser(userId, userPassword.toCharArray());
			
			if (authenticatedUser == null){
				throw new RuntimeException("unable to authenticate user");
			}
			
			ApiAccessToken apiAccessToken = application.getUserVerifier().generateApiAccessToken(apiClient.getKey(), authenticatedUser);
	
			AccessTokenRepresentation tokenRep = new AccessTokenRepresentation(apiAccessToken);
			
			//
			// indicate no caching of response.
			//
			ArrayList<CacheDirective> cacheDirectives = new ArrayList<CacheDirective>();
			cacheDirectives.add(CacheDirective.noCache());
			getResponse().setCacheDirectives(cacheDirectives);
			
	        return tokenRep;
	        
		}catch (Exception e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage(),e);
		}
    }
    

 }

package org.vcell.rest.auth;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.CacheDirective;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.ext.json.JsonRepresentation;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.common.AccessTokenRepresentation;
import org.vcell.util.document.User;

import com.google.gson.Gson;

import cbit.vcell.modeldb.ApiAccessToken;
import cbit.vcell.modeldb.ApiClient;

public class AuthenticationTokenRestlet extends Restlet {
	
	public static final String PARAM_USER_ID = "user_id";
	public static final String PARAM_USER_PASSWORD = "user_password";
	public static final String PARAM_CLIENT_ID = "client_id";
	private static Logger lg = LogManager.getLogger(AuthenticationTokenRestlet.class);

	public AuthenticationTokenRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request req, Response response) {
		if (req.getMethod().equals(Method.GET)){
			try {
				VCellApiApplication application = ((VCellApiApplication)getApplication());
				HttpRequest request = (HttpRequest)req;
				Form form = request.getResourceRef().getQueryAsForm();

				String userId = form.getFirstValue(PARAM_USER_ID, false);
				if (userId==null) {
					throw new RuntimeException("expecting "+PARAM_USER_ID+" query parameter");
				}
				String clientId = form.getFirstValue(PARAM_CLIENT_ID, false);
				if (clientId==null) {
					throw new RuntimeException("expecting "+PARAM_CLIENT_ID+" query parameter");
				}
				String userPassword = form.getFirstValue(PARAM_USER_PASSWORD, false);
				if (userPassword==null) {
					throw new RuntimeException("expecting "+PARAM_USER_PASSWORD+" query parameter");
				}
				ApiClient apiClient = application.getUserVerifier().getApiClient(clientId);
				if (apiClient==null){
					if (lg.isWarnEnabled()) lg.warn("client not found");
					response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
					response.setEntity("authentication error, unknown client", MediaType.TEXT_PLAIN);
					return;
				}
				
				User authenticatedUser = application.getUserVerifier().authenticateUser(userId, userPassword.toCharArray());
				
				if (authenticatedUser == null){
					if (lg.isWarnEnabled()) lg.warn("unable to authenticate user");
					response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
					response.setEntity("authentication error, either userid or password is incorrect", MediaType.TEXT_PLAIN);
					return;
				}
				
				ApiAccessToken apiAccessToken = application.getUserVerifier().generateApiAccessToken(apiClient.getKey(), authenticatedUser);
		
				AccessTokenRepresentation tokenRep = new AccessTokenRepresentation(apiAccessToken);
				
				//
				// indicate no caching of response.
				//
				ArrayList<CacheDirective> cacheDirectives = new ArrayList<CacheDirective>();
				cacheDirectives.add(CacheDirective.noCache());
				response.setCacheDirectives(cacheDirectives);
				
				Gson gson = new Gson();
				String tokenRepJSON = gson.toJson(tokenRep);
				response.setStatus(Status.SUCCESS_OK, "authentication token returned");
				response.setEntity(new JsonRepresentation(tokenRepJSON));
			}catch (Exception e){
				lg.error(e.getMessage(), e);
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				response.setEntity("internal error returning authentication token: "+e.getMessage(), MediaType.TEXT_PLAIN);
			}
		}
	}
}

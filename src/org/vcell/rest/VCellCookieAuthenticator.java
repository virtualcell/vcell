package org.vcell.rest;

import java.sql.SQLException;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.Cookie;
import org.restlet.data.Form;
import org.restlet.data.Parameter;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.crypto.CookieAuthenticator;
import org.restlet.representation.Representation;
import org.vcell.rest.auth.CustomAuthHelper;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.modeldb.ApiAccessToken;
import cbit.vcell.modeldb.ApiClient;

public class VCellCookieAuthenticator extends CookieAuthenticator {
	
	private VCellApiApplication vcellApiApplication = null;

	public VCellCookieAuthenticator(VCellApiApplication vcellApiApplication, boolean optional, String realm, byte[] encryptSecretKey) {
		super(vcellApiApplication.getContext(), optional, realm, encryptSecretKey);
		this.vcellApiApplication = vcellApiApplication;
	}

	@Override
	protected void login(Request request, Response response) {
        // Login detected
        Representation entity = request.getEntity();
		Form form = new Form(entity);
        Parameter identifier = form.getFirst(getIdentifierFormName());
        Parameter secret = form.getFirst(getSecretFormName());
        Parameter redirectURL = form.getFirst(getRedirectQueryName());
        
		UserLoginInfo.DigestedPassword digestedPassword = new UserLoginInfo.DigestedPassword(secret.getValue());
		try {
			User user = vcellApiApplication.getUserVerifier().authenticateUser(identifier.getValue(), digestedPassword.getString().toCharArray());
			if (user == null){
				response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				return;
			}
			ApiClient apiClient = vcellApiApplication.getUserVerifier().getApiClient(VCellApiApplication.BROWSER_CLIENTID);
			ApiAccessToken accessToken = vcellApiApplication.getUserVerifier().generateApiAccessToken(apiClient.getKey(), user);

			// Set credentials
	        ChallengeResponse cr = new ChallengeResponse(getScheme(), CustomAuthHelper.ACCESS_TOKEN, accessToken.getToken());
	        request.setChallengeResponse(cr);
	        
	        
	        getCredentialsCookie(request, response).setMaxAge(0);

	        getLogger().log(Level.INFO,"MyCookieAuthenticator.login(request,response) - created new accessToken '"+accessToken.getToken()+"' and assignd to ChallengeResponse, redirectURL='"+redirectURL.getValue()+"'");

	        response.redirectSeeOther(Reference.decode(redirectURL.getValue()));
		} catch (SQLException e) {
			e.printStackTrace();
			getLogger().log(Level.SEVERE,"MyCookieAuthenticator.login(request,response) - exception",e);
		} catch (DataAccessException e) {
			e.printStackTrace();
			getLogger().log(Level.SEVERE,"MyCookieAuthenticator.login(request,response) - exception",e);
		}
	}

	@Override
	protected int logout(Request request, Response response) {
		try {
	        Cookie credentialsCookie = request.getCookies().getFirst(getCookieName());
	        if (credentialsCookie != null) {
	        	ChallengeResponse challengeResponse = parseCredentials(credentialsCookie.getValue());
	        	ApiAccessToken apiAccessToken = vcellApiApplication.getApiAccessToken(challengeResponse);
	        	if (apiAccessToken!=null){
	        		vcellApiApplication.getUserVerifier().invalidateApiAccessToken(apiAccessToken.getToken());
	        		getLogger().log(Level.INFO,"MyCookieAuthenticator.login(request,response) - invalidated accessToken '"+apiAccessToken.getToken()+"'");
	        	}
	        }

		}catch (Exception e){
			e.printStackTrace(System.out);
			getLogger().log(Level.SEVERE,"MyCookieAuthenticator.logout(request,response) - exception while invalidating '"+CustomAuthHelper.ACCESS_TOKEN+"'",e);
		}
		
		return super.logout(request, response);
	}

};




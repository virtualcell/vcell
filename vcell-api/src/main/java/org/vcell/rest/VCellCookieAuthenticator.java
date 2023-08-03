package org.vcell.rest;

import cbit.vcell.modeldb.ApiAccessToken;
import cbit.vcell.modeldb.ApiClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.*;
import org.restlet.ext.crypto.CookieAuthenticator;
import org.restlet.representation.Representation;
import org.vcell.rest.auth.CookieVerifier;
import org.vcell.rest.auth.CustomAuthHelper;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import java.sql.SQLException;
import java.util.logging.Level;

public class VCellCookieAuthenticator extends CookieAuthenticator {
	private final static Logger lg = LogManager.getLogger(VCellCookieAuthenticator.class);
	
	private UserService userService = null;
	private CookieVerifier cookieVerifier = null;

	public VCellCookieAuthenticator(UserService userService, CookieVerifier cookieVerifier, Context context, boolean optional, String realm, byte[] encryptSecretKey) {
		super(context, optional, realm, encryptSecretKey);
		setVerifier(cookieVerifier);
		this.cookieVerifier = cookieVerifier;
		this.userService = userService;
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
			User user = userService.authenticateUser(identifier.getValue(), digestedPassword.getString().toCharArray());
			if (user == null){
				response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
				return;
			}
			ApiClient apiClient = userService.getApiClient(VCellApiApplication.BROWSER_CLIENTID);
			ApiAccessToken accessToken = userService.generateApiAccessToken(apiClient.getKey(), user);

			// Set credentials
	        ChallengeResponse cr = new ChallengeResponse(getScheme(), CustomAuthHelper.ACCESS_TOKEN, accessToken.getToken());
	        request.setChallengeResponse(cr);
	        
	        
	        CookieSetting crendentialCookie = getCredentialsCookie(request, response);
			crendentialCookie.setMaxAge(0);

	        getLogger().log(Level.INFO,"MyCookieAuthenticator.login(request,response) - created new accessToken '"+accessToken.getToken()+"' and assignd to ChallengeResponse, redirectURL='"+redirectURL.getValue()+"'");

	        response.redirectSeeOther(Reference.decode(redirectURL.getValue()));
		} catch (SQLException e) {
			lg.error(e);
			getLogger().log(Level.SEVERE,"MyCookieAuthenticator.login(request,response) - exception",e);
		} catch (DataAccessException e) {
			lg.error(e);
			getLogger().log(Level.SEVERE,"MyCookieAuthenticator.login(request,response) - exception",e);
		}
	}

	@Override
	protected int logout(Request request, Response response) {
		try {
	        Cookie credentialsCookie = request.getCookies().getFirst(getCookieName());
	        if (credentialsCookie != null) {
	        	ChallengeResponse challengeResponse = parseCredentials(credentialsCookie.getValue());
	        	ApiAccessToken apiAccessToken = cookieVerifier.getApiAccessToken(challengeResponse);
	        	if (apiAccessToken!=null){
	        		userService.invalidateApiAccessToken(apiAccessToken.getToken());
	        		getLogger().log(Level.INFO,"MyCookieAuthenticator.login(request,response) - invalidated accessToken '"+apiAccessToken.getToken()+"'");
	        	}
	        }

		}catch (Exception e){
			lg.error(e.getMessage(), e);
			getLogger().log(Level.SEVERE,"MyCookieAuthenticator.logout(request,response) - exception while invalidating '"+CustomAuthHelper.ACCESS_TOKEN+"'",e);
		}
		
		return super.logout(request, response);
	}

	@Override
	public ChallengeResponse parseCredentials(String cookieValue) {
		// increase visibility of this method
		return super.parseCredentials(cookieValue);
	}

};




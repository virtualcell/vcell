package org.vcell.rest.auth;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeRequest;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Header;
import org.restlet.data.Reference;
import org.restlet.engine.header.ChallengeWriter;
import org.restlet.engine.security.AuthenticatorHelper;
import org.restlet.util.Series;

public class CustomAuthHelper extends AuthenticatorHelper {

	public static final String ACCESS_TOKEN = "access_token";

	public CustomAuthHelper() {
		super(new ChallengeScheme("HTTP_CUSTOM","vcell_custom"), true, false);
	}

	@Override
	public void formatRequest(ChallengeWriter cw, ChallengeRequest challenge, Response response, Series<Header> httpHeaders) throws IOException {
		// TODO Auto-generated method stub
		super.formatRequest(cw, challenge, response, httpHeaders);
	}

	@Override
	public void formatResponse(ChallengeWriter cw, ChallengeResponse challenge, Request request, Series<Header> httpHeaders) {
		// TODO Auto-generated method stub
		super.formatResponse(cw, challenge, request, httpHeaders);
	}

	@Override
	public ChallengeScheme getChallengeScheme() {
		// TODO Auto-generated method stub
		return super.getChallengeScheme();
	}

	@Override
	public Logger getLogger() {
		// TODO Auto-generated method stub
		return super.getLogger();
	}

	@Override
	public boolean isClientSide() {
		// TODO Auto-generated method stub
		return super.isClientSide();
	}

	@Override
	public boolean isServerSide() {
		// TODO Auto-generated method stub
		return super.isServerSide();
	}

	@Override
	public void parseRequest(ChallengeRequest challenge, Response response, Series<Header> httpHeaders) {
		// TODO Auto-generated method stub
		super.parseRequest(challenge, response, httpHeaders);
	}

	@Override
	public void parseResponse(ChallengeResponse challenge, Request request, Series<Header> httpHeaders) {
        try {
			// expecting "CUSTOM access_token=123445"
			String[] tokens = challenge.getRawValue().split("=");
			String accessToken = null;
			if (tokens.length==2 && tokens[0].equals(ACCESS_TOKEN)){
				accessToken = tokens[1];
			}
        	
            if (accessToken == null) {
                getLogger()
                        .info("Cannot decode credentials: "
                                + challenge.getRawValue());
            }
            challenge.setIdentifier(ACCESS_TOKEN);
            challenge.setSecret(accessToken);
        } catch (Exception e) {
            getLogger().log(Level.INFO,
                    "Unable to decode the VCell Access Code", e);
        }
	}

	@Override
	public void setChallengeScheme(ChallengeScheme challengeScheme) {
		// TODO Auto-generated method stub
		super.setChallengeScheme(challengeScheme);
	}

	@Override
	public void setClientSide(boolean clientSide) {
		// TODO Auto-generated method stub
		super.setClientSide(clientSide);
	}

	@Override
	public void setServerSide(boolean serverSide) {
		// TODO Auto-generated method stub
		super.setServerSide(serverSide);
	}

	@Override
	public Reference updateReference(Reference resourceRef, ChallengeResponse challengeResponse, Request request) {
		// TODO Auto-generated method stub
		return super.updateReference(resourceRef, challengeResponse, request);
	}
	
	

}

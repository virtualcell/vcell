package org.vcell.rest.auth;

import cbit.vcell.modeldb.ApiAccessToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.security.Verifier;
import org.vcell.rest.UserService;
import org.vcell.util.DataAccessException;

import java.sql.SQLException;


public class BearerTokenVerifier implements Verifier {
    private static final Logger lg = LogManager.getLogger(BearerTokenVerifier.class);
    private final UserService userService;

    public BearerTokenVerifier(UserService userService) {
        this.userService = userService;
    }

    @Override
    public int verify(Request request, Response response) {
        ChallengeResponse cr = request.getChallengeResponse();
        if (cr == null || !cr.getScheme().getName().equals(ChallengeScheme.HTTP_OAUTH_BEARER.getName())
                || cr.getRawValue() == null){
            return Verifier.RESULT_MISSING;
        }
        String token = cr.getRawValue();
        try {
            if (userService.getApiAccessToken(token) != null) {
                return RESULT_VALID;
            } else {
                lg.info("token invalid from Bearer Token Scheme", e);
                return RESULT_INVALID;
            }
        } catch (SQLException | DataAccessException e) {
            lg.error("exception while validating token from Bearer Token Scheme", e);
            return RESULT_INVALID;
        }
    }

    public ApiAccessToken getApiAccessToken(ChallengeResponse response) throws SQLException, DataAccessException {
        if (response==null){
            lg.info("challenge response was null");
            return null;
        }else if (response.getScheme().getName().equals(ChallengeScheme.HTTP_OAUTH_BEARER.getName()) && response.getRawValue() != null) {
            String token = new String(response.getRawValue());
            ApiAccessToken accessToken = userService.getApiAccessToken(token);
            return accessToken;
        }else{
            lg.info("challenge response was present, but rawValue was missing or not Bearer token scheme");
            return null;
        }
    }

}
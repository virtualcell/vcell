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


public class CookieVerifier implements Verifier {
    private static final Logger lg = LogManager.getLogger(CookieVerifier.class);
    private final UserService userService;

    public CookieVerifier(UserService userService) {
        this.userService = userService;
    }

    @Override
    public int verify(Request request, Response response) {
        ChallengeResponse challengeResponse = request.getChallengeResponse();
        if (challengeResponse != null && challengeResponse.getScheme().equals(ChallengeScheme.HTTP_COOKIE) && challengeResponse.getSecret() != null) {
            String token = new String(challengeResponse.getSecret());
            try {
                if (userService.getApiAccessToken(token, false) != null) {
                    return RESULT_VALID;
                } else {
                    lg.info("invalid token from Cookie Scheme");
                    request.getCookies().removeAll("org.vcell.auth");
                    response.getCookieSettings().removeAll("org.vcell.auth");
                    return RESULT_INVALID;
                }
            } catch (SQLException | DataAccessException e) {
                lg.error("exception while validating token from Cookie Scheme", e);
                request.getCookies().removeAll("org.vcell.auth");
                response.getCookieSettings().removeAll("org.vcell.auth");
                return RESULT_INVALID;
            }
        } else {
            return RESULT_MISSING;
        }
    }

    public ApiAccessToken getApiAccessToken(ChallengeResponse response) throws SQLException, DataAccessException {
        if (response==null){
            lg.trace("challenge response was null");
            return null;
        }else if (response.getSecret() != null && response.getScheme().equals(ChallengeScheme.HTTP_COOKIE)) {
            ApiAccessToken accessToken = userService.getApiAccessToken(new String(response.getSecret()), false);
            return accessToken;
        }else{
            lg.trace("response response was preset, but secret was missing or not Cookie Scheme");
            return null;
        }
    }

}
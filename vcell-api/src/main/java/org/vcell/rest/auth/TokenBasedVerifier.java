package org.vcell.rest.auth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jose4j.jwt.MalformedClaimException;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeResponse;
import org.restlet.security.Verifier;
import org.vcell.auth.JWTUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class TokenBasedVerifier implements Verifier {
    private static final Logger lg = LogManager.getLogger(TokenBasedVerifier.class);

    @Override
    public int verify(Request request, Response response) {
        List<String> noAuthPaths = Arrays.asList(
                "/access_token",
                "/swversion",
                "/newuser",
                "/lostpassword");
        if (noAuthPaths.contains(request.getResourceRef().getPath())){
            return Verifier.RESULT_VALID;
        }
        ChallengeResponse cr = request.getChallengeResponse();
        if (cr == null){
            return Verifier.RESULT_MISSING;
        }
        String token = cr.getRawValue();
        try {
            boolean valid = JWTUtils.verifyJWS(token);
            if (valid) {
                return Verifier.RESULT_VALID;
            } else {
                return Verifier.RESULT_INVALID;
            }
        } catch (MalformedClaimException e) {
            lg.error("token was not valid", e);
            return Verifier.RESULT_UNKNOWN;
        }
    }
}
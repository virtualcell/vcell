package org.vcell.restq.handlers;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.Tokens;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import io.quarkus.logging.Log;
import io.quarkus.security.AuthenticationFailedException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.vcell.restq.models.AuthCodeResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Path("/api/auth")
public class AuthResource {

    @ConfigProperty(name = "quarkus.oidc.client-id")
    String clientId;

    @ConfigProperty(name = "quarkus.oidc.credentials.secret")
    String clientSecret;

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @GET
    @Path("code-flow")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthCodeResponse getAccessToken(
            @QueryParam("code") String authorization_code,
            @QueryParam("redirectURL") String redirectURL) throws ParseException, AuthenticationFailedException, URISyntaxException, IOException {

        // Token Request
        TokenRequest tokenRequest = new TokenRequest(
                new URI(authServerUrl + "/protocol/openid-connect/token"),
                new ClientSecretBasic(new ClientID(clientId), new Secret(clientSecret)),
                new AuthorizationCodeGrant(new AuthorizationCode(authorization_code), new URI(redirectURL))
        );

        // Send the token request
        HTTPResponse httpResponse = tokenRequest.toHTTPRequest().send();

        // Parse the response
        TokenResponse tokenResponse = TokenResponse.parse(httpResponse);

        if (tokenResponse instanceof AccessTokenResponse accessTokenResponse) {
            if (accessTokenResponse.indicatesSuccess()) {
                Tokens tokens = accessTokenResponse.getTokens();
                String accessToken = tokens.getAccessToken().getValue();
                String refreshToken = tokens.getRefreshToken().getValue();
                String idToken = null;
                if (tokens instanceof OIDCTokens oidcTokens) {
                    idToken = oidcTokens.getIDTokenString();
                }
                Log.debug("Access Token: " + accessToken);
                Log.debug("ID Token: " + idToken);
                Log.debug("Refresh Token: " + refreshToken);
                return new AuthCodeResponse(accessToken, idToken, refreshToken);
            } else {
                Log.error("Token request failed: " + tokenResponse.toHTTPResponse().getStatusCode());
                ErrorObject errorObject = accessTokenResponse.toErrorResponse().getErrorObject();
                System.out.println(errorObject);
                throw new AuthenticationFailedException("Token request failed with error: " + errorObject.getDescription());
            }
        } else {
            Log.error("Unexpected token response type: " + tokenResponse.getClass());
            throw new AuthenticationFailedException("Unexpected token response type: " + tokenResponse.getClass());
        }
    }
}

package org.vcell.restclient.auth;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import org.vcell.restclient.ApiClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.security.cert.X509Certificate;
import java.util.function.Consumer;


public class AuthApiClient extends ApiClient {
    private final URI oidcProviderTokenEndpoint;
    private RefreshToken refreshToken;
    private AccessToken accessToken;
    private long accessTokenExpirationTimeMS;
    private final RenewingRequestInterceptor renewingRequestInterceptor = new RenewingRequestInterceptor();

    private class RenewingRequestInterceptor implements Consumer<HttpRequest.Builder> {
        @Override
        public void accept(HttpRequest.Builder request) {
            final long one_minute_ms = 60 * 1000;
            if (System.currentTimeMillis() > accessTokenExpirationTimeMS - one_minute_ms) {
                try {
                    refreshAccessToken();
                } catch (IOException | ParseException e) {
                    throw new RuntimeException("failed to refresh accessToken: " + e.getMessage(), e);
                }
            }
            request.header("Authorization", "Bearer " + accessToken.getValue());
        }
    }

    public AuthApiClient(URI apiBaseUrl, URI oidcProviderTokenEndpoint, AccessToken accessToken, RefreshToken refreshToken, boolean ignoreSSLCertProblems) {
        allowInsecureCertificates(ignoreSSLCertProblems);
        this.oidcProviderTokenEndpoint = oidcProviderTokenEndpoint;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpirationTimeMS = System.currentTimeMillis() + accessToken.getLifetime() * 1000;

        this.setRequestInterceptor(renewingRequestInterceptor);
        this.setHost(apiBaseUrl.getHost());
        this.setPort(apiBaseUrl.getPort());
        this.setBasePath(apiBaseUrl.getPath());
    }

    public void refreshAccessToken() throws IOException, ParseException {
        if (refreshToken == null) {
            throw new RuntimeException("No refresh token available");
        }

        // Create the token request
        TokenRequest request = new TokenRequest(
                oidcProviderTokenEndpoint,
                new ClientID("your-client-id"),
                new RefreshTokenGrant(refreshToken)
        );

        HTTPResponse httpResponse = request.toHTTPRequest().send();
        TokenResponse response = OIDCTokenResponseParser.parse(httpResponse);

        if (response instanceof TokenErrorResponse errorResponse) {
            // Handle error
            System.err.println(errorResponse.getErrorObject());
        } else {
            AccessTokenResponse successResponse = (AccessTokenResponse) response;
            this.accessToken = successResponse.getTokens().getAccessToken();
            this.accessTokenExpirationTimeMS = System.currentTimeMillis() + this.accessToken.getLifetime() * 1000;
            this.refreshToken = successResponse.getTokens().getRefreshToken();
        }
    }

    public String getAccessToken(){
        return accessToken.getValue();
    }

    public String getRefreshToken(){
        return refreshToken.getValue();
    }

}

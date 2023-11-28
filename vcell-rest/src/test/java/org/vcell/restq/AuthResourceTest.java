package org.vcell.restq;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseMode;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.Prompt;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import net.minidev.json.JSONObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.isA;

@QuarkusTest
public class AuthResourceTest {

    @ConfigProperty(name = "quarkus.oidc.client-id")
    String clientId;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @Test
    public void testAdminAccess() throws URISyntaxException, IOException, ParseException, InterruptedException {
        String authServerUrl = keycloakClient.getAuthServerUrl();

        InetSocketAddress addr = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 8080);
        String callback_endpoint_path = "/oidc_test_callback";
        String redirectURI = "http://" + addr.getHostName() + ":" + addr.getPort() + callback_endpoint_path;
        Log.debug("redirectURI = " + redirectURI);

        BlockingQueue<String> authorizationCodeQueue = new LinkedBlockingQueue<>(1);

        HttpServer httpServer = null;
        try {
            httpServer = HttpServer.create(addr, 0);

            httpServer.createContext(callback_endpoint_path, new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    String url = exchange.getRequestURI().toString();
                    String parameter = "code=";
                    String authorizationCode = url.substring(url.indexOf(parameter) + parameter.length());
                    Log.debug("HttpServer: authentication code from KeyCloak is " + authorizationCode);
                    authorizationCodeQueue.add(authorizationCode);
                }
            });
            httpServer.setExecutor(null);
            Log.debug("http server starting");
            httpServer.start();
            Log.debug("http server started");


            Log.debug("authServerUrl = " + authServerUrl);
            Log.debug("retrieving metadata from " + authServerUrl + "/.well-known/openid-configuration");
            // Retrieve OpenID Provider Metadata
            URI metadata_endpoint = new URI(authServerUrl + "/.well-known/openid-configuration");
            HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.GET, metadata_endpoint);
            HTTPResponse metaDataResponse = httpRequest.send();
            JSONObject jsonObject = metaDataResponse.getContentAsJSONObject();
            OIDCProviderMetadata oidcProviderMetadata = OIDCProviderMetadata.parse(jsonObject);
            URI authorization_endpoint = oidcProviderMetadata.getAuthorizationEndpointURI();
            Log.debug("retrieved authorization_endpoint = " + authorization_endpoint);


            // Create Authentication Request
            AuthenticationRequest authRequest = new AuthenticationRequest.Builder(
                    new ResponseType("code"),
                    new Scope("openid"), // , "profile", "email"),
                    new ClientID(clientId),
                    new URI(redirectURI))
                    .state(new State())
                    .nonce(new Nonce())
                    .prompt(Prompt.Type.LOGIN)
                    .responseMode(ResponseMode.QUERY)
                    .endpointURI(authorization_endpoint)
                    .build();

            HTTPRequest authHTTPRequest = authRequest.toHTTPRequest();
            String queryString = authHTTPRequest.getQuery();
            String url = authHTTPRequest.getURL() + "?" + queryString;
            Log.debug("constructed authRequest url = " + url);
            try (WebClient webClient = new WebClient()) {
                webClient.getOptions().setTimeout(1000);
                webClient.getOptions().setConnectionTimeToLive(1000);
                webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
                webClient.getOptions().setThrowExceptionOnScriptError(true);
                webClient.getOptions().setCssEnabled(false); // true causes excessive logging
                webClient.getOptions().setRedirectEnabled(true);
                webClient.getOptions().setJavaScriptEnabled(false);
                webClient.setAjaxController(new NicelyResynchronizingAjaxController());
                Log.info("WebClient: about to get page " + url);
                HtmlPage page = webClient.getPage(url);
                webClient.waitForBackgroundJavaScript(10000);
                Log.debug("WebClient: got page " + url);

                HtmlForm form = (HtmlForm) page.getElementById("kc-form-login");
                HtmlTextInput inputUsername = form.getInputByName("username");
                HtmlPasswordInput inputPassword = form.getInputByName("password");
                inputUsername.type("alice");
                inputPassword.type("alice");
                HtmlSubmitInput buttonLogin = form.getInputByName("login");
                Log.debug("WebClient: about to press login button");
                buttonLogin.click();
                Log.info("WebClient: completed - not expected");
            } catch (Exception ex) {
                Log.warn("WebClient: failed to complete (this is expected): " + ex.getMessage());
            }

            Log.debug("waiting for authorizationCode to be retrieved from HttpServer");
            String authorizationCode = authorizationCodeQueue.poll(30, TimeUnit.SECONDS); // wait for server to process the request
            Log.debug("authorizationCode retrieved from HttpServer = " + authorizationCode);
            Assertions.assertNotNull(authorizationCode, "authorizationCode is null");

            Log.debug("sending request to /api/auth/code-flow/?code=" + authorizationCode + "&redirectURL=" + redirectURI);
            given()
                    .when().get("/api/auth/code-flow/" + "?code=" + authorizationCode + "&redirectURL=" + redirectURI)
                    .then()
                    .statusCode(200)
                    .body("access_token", isA(String.class))
//                    .body("id_token", isA(String.class))
                    .body("refresh_token", isA(String.class));
            Log.debug("successfully exchanged code for tokens using /api/auth/code-flow/{authorization_code}");
        } finally {
            Log.debug("shutting down HttpServer");
            httpServer.stop(0);
            Log.debug("stopped HttpServer");
        }
    }
}

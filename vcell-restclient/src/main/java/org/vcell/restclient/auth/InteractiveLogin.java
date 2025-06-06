package org.vcell.restclient.auth;

import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import com.sun.net.httpserver.HttpServer;
import net.minidev.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class InteractiveLogin {
    public final static String authClientID = "cjoWhd7W8A8znf7Z7vizyvKJCiqTgRtf";
    public final static String authDomain = "https://dev-dzhx7i2db3x3kkvq.us.auth0.com";



    private InteractiveLogin() {
    }

    public static AuthApiClient login() throws URISyntaxException, IOException, ParseException {
        return login(new URI(authDomain), new URI("https://vcell.cam.uchc.edu"), false);
    }

    /**
     *  1. Goes to the authorization server, gather metadata about it's OIDC configuration
     *  (ex. Scopes supported, signing methods supported, auth and token endpoint, response types...)
     *  <br>
     *  2. Find a port available on local host, specifying the server to be created with a preset path for authentication related calls.
     *  The path, port, and domain (localhost) need to be approved on the Auth0 end pre-emptively.
     *  <br>
     *  3. Create a custom URI that contains scope, callback URL, code-verifier, state, and OIDC metadata
     *  <br>
     *  4. Spin up the local host callback URL, waiting to receive notification from the Auth0 server
     *  <br>
     *  5. Once the Auth0 server responds to the callback URL, which in this case is our local host server. The exchange token is given.
     *  <br>
     *  6. The exchange token is sent to Auth0 server, used to receive an OIDC token. One time use only.
     *  <br>
     *  7. Create an HTTP client with the OIDC token suite (refresh, ID, access, etc...), and return it.
     *  <br>
     *  P.S: This HTTP client created has an automated refresh capability for the access token, allowing users to stay logged in
     *  for an extended period of time.
     * @param authServerUri
     * @param apiBaseUri
     * @param ignoreSSLCertProblems
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ParseException
     */

    public static AuthApiClient login(URI authServerUri, URI apiBaseUri, boolean ignoreSSLCertProblems) throws URISyntaxException, IOException, ParseException {
        URI successRedirectURI = new URI(apiBaseUri+( apiBaseUri.getHost().equals("localhost")?  "" : "/login_success"));

        // Retrieve OpenID Provider Metadata
        URI metadata_endpoint = new URI(authServerUri + "/.well-known/openid-configuration");
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.GET, metadata_endpoint);
        HTTPResponse metaDataResponse = httpRequest.send();
        JSONObject jsonObject = metaDataResponse.getContentAsJSONObject();
        OIDCProviderMetadata oidcProviderMetadata = OIDCProviderMetadata.parse(jsonObject);

        State state = new State();

        // all ports must be registered as http://localhost:<port>/oidc_test_callback in Auth0 redirect URI.
        // specific ports (ending with 111) are chosen from dynamic/private port range 49152-65535
        // to avoid conflicts with other services and be tolerated by firewalls.
        //
        // corresponding redirect URI registration in Auth0:
        //   http://localhost:51111/oidc_test_callback,http://localhost:52111/oidc_test_callback,
        //   http://localhost:53111/oidc_test_callback,http://localhost:54111/oidc_test_callback,
        //   http://localhost:55111/oidc_test_callback,http://localhost:56111/oidc_test_callback,
        //   http://localhost:57111/oidc_test_callback,http://localhost:58111/oidc_test_callback,
        //   http://localhost:59111/oidc_test_callback,http://localhost:60111/oidc_test_callback,
        //   http://localhost:61111/oidc_test_callback,http://localhost:62111/oidc_test_callback,
        //   http://localhost:63111/oidc_test_callback,http://localhost:64111/oidc_test_callback,
        //   http://localhost:65111/oidc_test_callback

        // selected ports which are registered
        List<Integer> auth0_redirect_ports = Arrays.asList(51111, 52111, 53111, 54111, 55111, 56111, 57111, 58111, 59111,
                60111, 61111, 62111, 63111, 64111, 65111);
        int localHttpServerPort = findAvailablePort(auth0_redirect_ports);
        String callback_endpoint_path = "/oidc_test_callback";

        URI redirectURI = new URI("http://" + "localhost" + ":" + localHttpServerPort + callback_endpoint_path);
        Scope scope = new Scope("openid", "email", "profile", "offline_access"); //, "email"); //, "profile", "offline_access");

        CodeVerifier codeVerifier = new CodeVerifier();
        URI authRequestURI = getAuthRequestURI(oidcProviderMetadata, redirectURI, new ClientID(authClientID), scope, state, codeVerifier, apiBaseUri);

        final AuthorizationResponse authorizationResponse;
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            // set up web server to receive redirect and send URL to system browser - will be redirected back to http://localhost:9999/oidc_test_callback
            System.out.println("launched browser with login window, will intercept the authentication response sent to local web server");
            authorizationResponse = getAuthorizationResponseAutomated(localHttpServerPort, callback_endpoint_path, authRequestURI, successRedirectURI);
            if (authorizationResponse != null) {
                if (Desktop.getDesktop().isSupported(Desktop.Action.APP_REQUEST_FOREGROUND)) {
                    System.out.println("requesting foreground");
                    try {
                        Desktop.getDesktop().requestForeground(true);
                        System.out.println("foreground requested");
                    } catch (Exception e) {
                        System.err.println("failed to request foreground: " + e.getMessage());
                    }
                } else {
                    System.err.println("foreground not supported");
                }
            }
        } else {
            // manual copy/paste of redirect URL into browser, and copy/paste of redirect URL back into console
            authorizationResponse = getAuthorizationResponseManual(authRequestURI);
        }

        OIDCTokens oidcTokens = exchangeCodeForTokens(authorizationResponse, oidcProviderMetadata.getTokenEndpointURI(), new ClientID(authClientID), scope, redirectURI, codeVerifier);

        return new AuthApiClient(apiBaseUri, oidcProviderMetadata.getTokenEndpointURI(), oidcTokens.getAccessToken(), oidcTokens.getRefreshToken(), ignoreSSLCertProblems);
    }

    static int findAvailablePort(List<Integer> potentialPorts) {
        for (int port: potentialPorts) {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                // The port is available
                return port;
            } catch (IOException e) {
                // The port is not available
            }
        }
        throw new IllegalStateException("Could not find an available dynamic port (49152-65535) to receive authorization code from Authentication provider");
    }

    private static AuthorizationResponse getAuthorizationResponseManual(URI authRequestURI) throws IOException, ParseException {
        AuthorizationResponse authorizationResponse;
        System.out.println(
                "1) Please open the following URL in your browser:\n    " + authRequestURI + "\n" +
                        "2) Grant the requested permissions\n" +
                        "3) Copy the URL of the page you are redirected to and paste it below:");
        // read code from console
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String authorizationResponseURL = br.readLine();
            authorizationResponse = AuthorizationResponse.parse(URI.create(authorizationResponseURL));
        }
        return authorizationResponse;
    }

    /**
     * 1. Create an HTTP server with a /ping endpoint for ensuring it's alive, and a call back endpoint that has already been approved by
     * Auth0.
     * <br>
     * 2. Set the call back path to accept the redirect URI given in an HTTP data packet.
     * <br>
     * 3. Redirect the user to the auth request URI in their browser, which either prompts them to a login screen or directly
     * takes them to the redirect URL if they are already logged in.
     * <br>
     * 4. Receive the authorization response when the Auth0 endpoint redirects us to our localhost server
     * @param localHttpServerPort
     * @param callback_endpoint_path
     * @param authRequestURI
     * @param successRedirectURI
     * @return AuthorizationResponse
     * @throws IOException
     * @throws ParseException
     */

    private static AuthorizationResponse getAuthorizationResponseAutomated(int localHttpServerPort, String callback_endpoint_path, URI authRequestURI, URI successRedirectURI) throws IOException, ParseException {
        AuthorizationResponse authorizationResponse;
        final BlockingQueue<String> authorizationCodeURIQueue = new LinkedBlockingQueue<>(1);

        InetSocketAddress addr = new InetSocketAddress("localhost", localHttpServerPort);
        HttpServer httpServer = null;
        try {
            httpServer = HttpServer.create(addr, 0);
            String expectedPingResponse = "alive";
            httpServer.createContext("/ping", exchange -> {
                exchange.sendResponseHeaders(200, expectedPingResponse.length());
                exchange.getResponseBody().write(expectedPingResponse.getBytes());
                exchange.getResponseBody().close();
            });
            httpServer.createContext(callback_endpoint_path, exchange -> {
                System.out.println("received redirect URI with authorization code from web server");
                authorizationCodeURIQueue.add(exchange.getRequestURI().toString());
                System.out.println("added redirect URI to queue");
                // redirect the user to https://vcellapi-test.cam.uchc.edu with a 303 status
                exchange.getResponseHeaders().add("Location", String.valueOf(successRedirectURI));
                //exchange.getResponseHeaders().add("Location", "https://vcellapi-test.cam.uchc.edu/login_success");
                exchange.sendResponseHeaders(303, -1);
                exchange.close();
            });

            httpServer.setExecutor(null);
            httpServer.start();
            System.out.println("http server started on port "+localHttpServerPort+" and listening on paths "+callback_endpoint_path+" and /ping");

            // ping http server to make sure it is running at path /ping, expecting response of "alive"
            //URL url = new URL("http://" + addr.getHostName() + ":" + addr.getPort() + "/ping?hello=world");
            URL url = new URL("http://" + "localhost" + ":" + addr.getPort() + "/ping?hello=world");
            System.out.println("pinging http server at "+url);
            pingHttpServer(url, expectedPingResponse);
            System.out.println("ping response received - http server is running");

            // open browser to authRequestURI
            System.out.println("opening browser to "+authRequestURI);
            Desktop.getDesktop().browse(authRequestURI);

            System.out.println("waiting up to 10 minutes for authorization code from web server via queue");
            // wait for authorization response from web server
            String authorizationCodeURI = authorizationCodeURIQueue.poll(600, TimeUnit.SECONDS); // wait for server to process the request
            System.out.println("authorization code " + authorizationCodeURI + " received from web server via queue");
            if (authorizationCodeURI == null) {
                String message = "If you desire to login, please click Account -> Login, for the login window has expired.";
                JOptionPane.showMessageDialog(null, message,
                        "Restart the Application", JOptionPane.ERROR_MESSAGE, null);
                throw new RuntimeException("Authorization code not received");
            }
            authorizationResponse = AuthorizationResponse.parse(URI.create(authorizationCodeURI));
            System.out.println("authorization code parsed");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.println("shutting down http server");
            httpServer.stop(0);
            System.out.println("shut down http server");
        }
        return authorizationResponse;
    }

    private static void pingHttpServer(URL url, String expectedPingResponse) throws IOException, InterruptedException {
        System.out.println("sleeping 1 second to allow http server to start");
        //Thread.sleep(1000);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(600 * 100); //milliseconds to seconds
        con.connect();
        int responseCode = con.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("http server ping failed with response code " + responseCode);
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in .readLine()) != null) {
            response.append(inputLine);
        }
        in .close();
        if (!expectedPingResponse.contentEquals(response)) {
            throw new RuntimeException("http server ping failed with response " + response);
        }
        System.out.println("http server ping successful");
    }

    /**
     * Create a URI that contains the clientID, scope, redirectURI, state (essentially a session ID for the current transaction),
     * and a challenge code.
     * This URI is used to initiate the authentication process.
     * @param oidcProviderMetadata
     * @param redirectURI
     * @param clientID
     * @param scope
     * @param state
     * @param codeVerifier
     * @return URI
     * @throws URISyntaxException
     * @throws IOException
     * @throws ParseException
     */
    private static URI getAuthRequestURI(OIDCProviderMetadata oidcProviderMetadata, URI redirectURI, ClientID clientID,
                                         Scope scope, State state, CodeVerifier codeVerifier, URI audience) throws URISyntaxException, IOException, ParseException {
        // Create the authorization request
        URI authorizationEndpoint = oidcProviderMetadata.getAuthorizationEndpointURI();

        String audiencePort = audience.getHost().equals("localhost") ? ":" + audience.getPort() : "";
        var authorizationRequest = new AuthorizationRequest.Builder(new ResponseType("code"), clientID)
                .endpointURI(authorizationEndpoint)
                .redirectionURI(redirectURI)
                .customParameter("audience", audience.getScheme() + "://" + audience.getHost() + audiencePort)
                .customParameter("prompt", "select_account")
                .state(state)
                .scope(scope) // Add any other required scopes
                .codeChallenge(codeVerifier, CodeChallengeMethod.S256)
                .build();

        return authorizationRequest.toURI();
    }

    /**
     * 1. Take the authorization response. Inside it has a one use token which gets extracted.
     * <br>
     * 2. With this one use token in conjecture with the code verifier, they can be exchanged for the ID token, access token, and refresh token
     * if that is allowed.
     * <br>
     * P.S: The code verifier is used so that Auth0 is sure that who started this authentication process is the same individual who's
     * exchanging this token.
     * @param authorizationResponse
     * @param tokenEndpoint
     * @param clientID
     * @param scope
     * @param redirectURI
     * @param codeVerifier
     * @return
     * @throws URISyntaxException
     * @throws IOException
     * @throws ParseException
     */

    private static OIDCTokens exchangeCodeForTokens(AuthorizationResponse authorizationResponse, URI tokenEndpoint, ClientID clientID, Scope scope, URI redirectURI, CodeVerifier codeVerifier) throws URISyntaxException, IOException, ParseException {

        if (!authorizationResponse.indicatesSuccess()) {
            throw new RuntimeException("Authorization failed!");
        }

        AuthorizationSuccessResponse successResponse = (AuthorizationSuccessResponse) authorizationResponse;

        final AuthorizationCode authorizationCode = successResponse.getAuthorizationCode();

        AuthorizationCodeGrant codeGrant = new AuthorizationCodeGrant(authorizationCode, redirectURI, codeVerifier);

        TokenRequest tokenRequest = new TokenRequest(tokenEndpoint, clientID, codeGrant, scope);
        System.out.println("tokenRequest URL = " + tokenRequest.toHTTPRequest().getURL());
        System.out.println("tokenRequest Query = " + tokenRequest.toHTTPRequest().getQuery());
        System.out.println("tokenRequest Headers = " + tokenRequest.toHTTPRequest().getHeaderMap());
        System.out.println("tokenRequest Method = " + tokenRequest.toHTTPRequest().getMethod());
        HTTPResponse tokenHTTPResp = tokenRequest.toHTTPRequest().send();
        TokenResponse response = OIDCTokenResponseParser.parse(tokenHTTPResp);

        if (!response.indicatesSuccess()) {
            // The request failed, check the error message
            TokenErrorResponse errorResponse = (TokenErrorResponse) response;
            throw new RuntimeException("Request failed, error: " + errorResponse.getErrorObject());
        }

        OIDCTokenResponse oidcTokenResponse = (OIDCTokenResponse) response;
        return oidcTokenResponse.getOIDCTokens();
    }

    public static void logOut(){

    }
}
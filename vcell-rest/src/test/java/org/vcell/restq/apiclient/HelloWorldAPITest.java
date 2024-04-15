package org.vcell.restq.apiclient;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.api.HelloWorldApi;
import org.vcell.restclient.model.HelloWorldMessage;

@QuarkusTest
public class HelloWorldAPITest {
    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();
    @Test
    public void getHelloWorld() throws ApiException {
        String nonpubuser = "bob";
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        String accessToken = keycloakClient.getAccessToken(nonpubuser);
        Log.warn("TODO: get access token from OIDC server instead of KeycloakTestClient");
        defaultClient.setRequestInterceptor(request -> request.header("Authorization", "Bearer " + accessToken));
        defaultClient.setScheme("http");
        defaultClient.setHost("localhost");
        defaultClient.setPort(testPort);

        HelloWorldApi helloWorldApi = new HelloWorldApi(defaultClient);

        HelloWorldMessage helloWorldMessage = helloWorldApi.getHelloWorld();

        Assertions.assertEquals("Hello Security Roles: [user]", helloWorldMessage.getMessage());
    }
}

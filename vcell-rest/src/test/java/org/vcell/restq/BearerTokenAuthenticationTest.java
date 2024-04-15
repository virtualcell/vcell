package org.vcell.restq;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
public class BearerTokenAuthenticationTest {

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @Test
    public void testUserAccess() {
        RestAssured.given().auth().oauth2(getAccessToken("alice"))
                .when().get("/api/v1/users/me")
                .then()
                .statusCode(200);
        RestAssured.given().auth().oauth2(getAccessToken("bob"))
                .when().get("/api/v1/users/me")
                .then()
                .statusCode(200);
    }

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}
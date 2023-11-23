package org.vcell.restq;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.RestAssured;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.Test;

@QuarkusTest
@Category(org.vcell.test.Quarkus.class)
public class BearerTokenAuthenticationTest {

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @Test
    public void testAdminAccess() {
        RestAssured.given().auth().oauth2(getAccessToken("alice"))
                .when().get("/api/admin")
                .then()
                .statusCode(200);
        RestAssured.given().auth().oauth2(getAccessToken("bob"))
                .when().get("/api/admin")
                .then()
                .statusCode(403);
    }

    @Test
    public void testUserAccess() {
        RestAssured.given().auth().oauth2(getAccessToken("alice"))
                .when().get("/api/users/me")
                .then()
                .statusCode(200);
        RestAssured.given().auth().oauth2(getAccessToken("bob"))
                .when().get("/api/users/me")
                .then()
                .statusCode(200);
    }

    protected String getAccessToken(String userName) {
        return keycloakClient.getAccessToken(userName);
    }
}
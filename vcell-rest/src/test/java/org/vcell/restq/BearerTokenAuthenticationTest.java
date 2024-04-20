package org.vcell.restq;

import cbit.vcell.resource.PropertyLoader;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.vcell.restq.config.CDIVCellConfigProvider;

@QuarkusTest
public class BearerTokenAuthenticationTest {
    @BeforeAll
    public static void setupConfig(){
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

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
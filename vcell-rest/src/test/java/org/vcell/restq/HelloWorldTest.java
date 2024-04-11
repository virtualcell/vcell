package org.vcell.restq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.vcell.util.DataAccessException;

import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class HelloWorldTest {

    @Inject
    public ObjectMapper objectMapper;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @Test
    public void testHelloWorldMessage() throws JsonProcessingException, SQLException, DataAccessException {
        String nonpubuser = "bob";

        // verify that list publications is empty
       given().auth().oauth2(keycloakClient.getAccessToken(nonpubuser))
                .when().get("/api/v1/helloworld")
                .then()
                .statusCode(200)
                .body("message", is("Hello Security Roles: [user]"));

    }
}
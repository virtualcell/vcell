package org.vcell.restq;

import cbit.vcell.modeldb.DatabaseServerImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vcell.restq.auth.AuthUtils;
import org.vcell.restq.db.PublicationService;
import org.vcell.restq.models.BiomodelRef;
import org.vcell.restq.models.MathmodelRef;
import org.vcell.restq.models.Publication;
import org.vcell.util.DataAccessException;

import java.sql.SQLException;
import java.util.Date;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;

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
                .when().get("/api/helloworld")
                .then()
                .statusCode(200)
                .body(".message", is("Hello Security: " + nonpubuser));
    }
}
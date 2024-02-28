package org.vcell.restq;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.vcell.util.DataAccessException;

import java.io.IOException;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;

public class BioModelTest {
    @Inject
    public ObjectMapper objectMapper;

//    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    // TODO: Right now the biomodel endpoint doesn't implement authentication, but when it does it'll need to
    @Test
    public void testAddAndRemove() throws IOException, SQLException, DataAccessException {
        String pubuser = "alice";
        String nonpubuser = "bob";

        String vcmlString = IOUtils.toString(getClass().getResourceAsStream("/TestVCML.vcml"));
        // create a test publication using org.vcell.rest.model.Publication and add it to the list


        // insert publication1 as no user
        Response uploadResponse = given().baseUri("http://localhost:9000")
                .body(vcmlString)
                .header("Content-Type", MediaType.TEXT_XML)
                .when()
                .post("/api/bioModel/upload_bioModel");
        uploadResponse.then().statusCode(200);
        String uploadedID = uploadResponse.body().print();

        Response jsonBody = given().baseUri("http://localhost:9000")
                .when().get("/api/bioModel/" + uploadedID);
        jsonBody.then().statusCode(200);
        jsonBody.body().print();

        given().baseUri("http://localhost:9000")
                .when()
                .delete("/api/bioModel/" + uploadedID)
                .then()
                .statusCode(204);
        // insert publication1 as nonpubuser (doesn't have proper permission)

        // insert publication1 as user pubuser (has proper permission)
        // get publication1 as no user

    }
}

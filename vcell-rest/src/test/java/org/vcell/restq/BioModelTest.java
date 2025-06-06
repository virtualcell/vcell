package org.vcell.restq;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restclient.model.SaveBioModel;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class BioModelTest {
    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();
    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    private ApiClient aliceAPIClient;

    @BeforeAll
    public static void setupConfig(){
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void createClients(){
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
    }

    // TODO: Right now the biomodel endpoint doesn't implement authentication, but when it does it'll need to
    @Test
    public void testAddAndRemove() throws IOException, ApiException, XmlParseException, PropertyVetoException {

        String vcmlString = IOUtils.toString(getClass().getResourceAsStream("/TestVCML.vcml"));
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlString));
        bioModel.setName("BioModelTest_testAddAndRemove");
        bioModel.clearVersion();
        vcmlString = XmlHelper.bioModelToXML(bioModel);
        // create a test publication using org.vcell.rest.model.Publication and add it to the list

        boolean mapped = new UsersResourceApi(aliceAPIClient).mapUser(TestEndpointUtils.vcellNagiosUserLoginInfo);
        Assertions.assertTrue(mapped);

        // insert publication1 as user
        Response uploadResponse = given()
                .auth().oauth2(keycloakClient.getAccessToken(TestEndpointUtils.TestOIDCUsers.alice.name()))
                .body(vcmlString)
                .header("Content-Type", MediaType.APPLICATION_XML)
                .when()
                .post("/api/v1/bioModel");
        uploadResponse.then().statusCode(200);
        String bioModelVCML = uploadResponse.body().print();
        BioModel bioModel1 = XmlHelper.XMLToBioModel(new XMLSource(bioModelVCML));
        String bioModelKey = bioModel1.getVersion().getVersionKey().toString();

        Response jsonBody = given()
                .auth().oauth2(keycloakClient.getAccessToken(TestEndpointUtils.TestOIDCUsers.alice.name()))
                .when().get("/api/v1/bioModel/" + bioModelKey);
        jsonBody.then().statusCode(200);
        jsonBody.body().print();

        given()
                .auth().oauth2(keycloakClient.getAccessToken(TestEndpointUtils.TestOIDCUsers.alice.name()))
                .when()
                .delete("/api/v1/bioModel/" + bioModelKey)
                .then()
                .statusCode(204);
        // insert publication1 as nonpubuser (doesn't have proper permission)

        // insert publication1 as user pubuser (has proper permission)
        // get publication1 as no user

    }
}

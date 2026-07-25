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
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

/**
 * HTTP-level tests for GET /api/v1/vcInfoContainer (the REST replacement for the legacy RPC
 * UserMetaDbServer.getVCInfoContainer). Verifies anonymous access returns public-only records and
 * that an authenticated request includes the requester's own records, in a single bulk response.
 */
@QuarkusTest
public class VCInfoContainerTest {

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    private ApiClient aliceAPIClient;

    @BeforeAll
    public static void setupConfig() {
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void createClients() {
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
    }

    @AfterEach
    public void cleanup() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
    }

    @Test
    public void testAnonymousReturnsPublicOnlyContainer() {
        // no bearer token -> anonymous -> 200 with the four summary arrays present (public-only)
        given()
                .when().get("/api/v1/vcInfoContainer")
                .then()
                .statusCode(200)
                .body("bioModelSummaries", notNullValue())
                .body("mathModelSummaries", notNullValue())
                .body("geometrySummaries", notNullValue())
                .body("vcImageSummaries", notNullValue());
    }

    @Test
    public void testAuthenticatedIncludesOwnBioModel() throws IOException, ApiException, XmlParseException, PropertyVetoException {
        boolean mapped = new UsersResourceApi(aliceAPIClient).mapUser(TestEndpointUtils.vcellNagiosUserLoginInfo);
        Assertions.assertTrue(mapped);

        String vcmlString = IOUtils.toString(getClass().getResourceAsStream("/TestVCML.vcml"));
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlString));
        bioModel.setName("VCInfoContainerTest_model");
        bioModel.clearVersion();
        vcmlString = XmlHelper.bioModelToXML(bioModel);

        Response uploadResponse = given()
                .auth().oauth2(keycloakClient.getAccessToken(TestEndpointUtils.TestOIDCUsers.alice.name()))
                .body(vcmlString)
                .header("Content-Type", MediaType.APPLICATION_XML)
                .when().post("/api/v1/bioModel");
        uploadResponse.then().statusCode(200);

        // the freshly saved biomodel must appear in the bulk container for its owner
        given()
                .auth().oauth2(keycloakClient.getAccessToken(TestEndpointUtils.TestOIDCUsers.alice.name()))
                .when().get("/api/v1/vcInfoContainer")
                .then()
                .statusCode(200)
                .body("bioModelSummaries.size()", greaterThanOrEqualTo(1));
    }
}

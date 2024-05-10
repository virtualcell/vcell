package org.vcell.restq;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.restassured.response.Response;
import jakarta.inject.Inject;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class AdminResourceTest {
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
    }

    // TODO: Right now the biomodel endpoint doesn't implement authentication, but when it does it'll need to
    @Test
    public void testGetUsage() throws IOException, ApiException, XmlParseException, PropertyVetoException {

        boolean mapped = new UsersResourceApi(aliceAPIClient).mapUser(TestEndpointUtils.administratorUserLoginInfo);
        Assertions.assertTrue(mapped);

        Response response = given()
                .auth().oauth2(keycloakClient.getAccessToken(TestEndpointUtils.TestOIDCUsers.alice.name()))
                .when().get("/api/v1/admin/usage")
                .then().extract().response();

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("application/pdf", response.getContentType());

        byte[] pdfBytes = response.asByteArray();
        Assertions.assertTrue(pdfBytes.length > 0);
    }
}

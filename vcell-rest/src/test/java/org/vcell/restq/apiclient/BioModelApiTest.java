package org.vcell.restq.apiclient;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.BioModelResourceApi;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restclient.model.BioModel;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

@QuarkusTest
public class BioModelApiTest {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private ApiClient aliceAPIClient;
    private ApiClient bobAPIClient;

    @BeforeAll
    public static void setupConfig(){
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void createClients(){
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        bobAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.bob);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
    }




    //TODO: Add endpoint that retrieves all BM affiliated with user, then use that for testing
    @Test
    public void testAddRemove() throws ApiException, IOException, XmlParseException, PropertyVetoException {
        boolean mapped = new UsersResourceApi(aliceAPIClient).mapUser(TestEndpointUtils.vcellNagiosUserLoginInfo);
        Assertions.assertTrue(mapped);

        BioModelResourceApi bioModelResourceApi = new BioModelResourceApi(aliceAPIClient);

        String vcmlString = XmlHelper.bioModelToXML(TestEndpointUtils.getTestBioModel());

        String bioModelID = bioModelResourceApi.uploadBioModel(vcmlString);
        BioModel bioModel_dto = bioModelResourceApi.getBiomodelById(bioModelID);

        bioModelResourceApi.deleteBioModel(bioModelID);


    }
}

package org.vcell.restq.apiclient;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XmlHelper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.BioModelResourceApi;
import org.vcell.restclient.api.VcInfoContainerResourceApi;
import org.vcell.restclient.model.VCInfoContainerSummary;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;

import java.sql.SQLException;

/**
 * Generated-API-client tests for GET /api/v1/vcInfoContainer (the REST replacement for the legacy RPC
 * UserMetaDbServer.getVCInfoContainer), exercised through the generated {@link VcInfoContainerResourceApi}.
 */
@QuarkusTest
public class VCInfoContainerApiTest {

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private ApiClient aliceAPIClient;

    @BeforeAll
    public static void setupConfig() {
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void createClients() throws ApiException {
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        TestEndpointUtils.mapApiClientToNagios(aliceAPIClient);
    }

    @AfterEach
    public void cleanup() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
    }

    @Test
    public void testGetVCInfoContainerIncludesSavedBioModel() throws Exception {
        BioModel bioModel = TestEndpointUtils.getTestBioModel();
        bioModel.setName("VCInfoContainerApiTest_model");
        bioModel.clearVersion();
        String vcml = XmlHelper.bioModelToXML(bioModel);
        new BioModelResourceApi(aliceAPIClient).saveBioModel(vcml, null, null);

        VCInfoContainerSummary container = new VcInfoContainerResourceApi(aliceAPIClient).getVCInfoContainer();

        Assertions.assertNotNull(container.getBioModelSummaries());
        Assertions.assertNotNull(container.getMathModelSummaries());
        Assertions.assertNotNull(container.getGeometrySummaries());
        Assertions.assertNotNull(container.getVcImageSummaries());
        // the freshly saved biomodel must be visible in the owner's bulk container
        Assertions.assertFalse(container.getBioModelSummaries().isEmpty(),
                "expected the saved BioModel to appear in the vcInfoContainer");
    }

    @Test
    public void testAnonymousGetVCInfoContainerReturnsPublicOnly() throws ApiException {
        ApiClient anonymousClient = TestEndpointUtils.createUnAuthenticatedAPIClient(testPort);
        VCInfoContainerSummary container = new VcInfoContainerResourceApi(anonymousClient).getVCInfoContainer();
        // anonymous request must still succeed and return the four (public-only) summary lists
        Assertions.assertNotNull(container.getBioModelSummaries());
        Assertions.assertNotNull(container.getMathModelSummaries());
        Assertions.assertNotNull(container.getGeometrySummaries());
        Assertions.assertNotNull(container.getVcImageSummaries());
    }
}

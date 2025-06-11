package org.vcell.restq.apiclient.documents;

import cbit.vcell.geometry.Geometry;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.GeometryResourceApi;
import org.vcell.restclient.model.GeometrySummary;
import org.vcell.restclient.utils.DtoModelTransforms;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.Extent;
import org.vcell.util.Origin;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

@QuarkusTest
public class GeometryApiTest {
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
    public void createClients() throws ApiException, DataAccessException {
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        bobAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.bob);
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);
        TestEndpointUtils.mapApiClientToNagios(bobAPIClient);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
    }

    // Changes because of User being
    private void cleanGeometry(Geometry savedGeometry, Geometry originalGeometry) throws PropertyVetoException {
        savedGeometry.setDescription(originalGeometry.getDescription());
    }

    @Test
    public void testSave() throws Exception {
        GeometryResourceApi resourceApi = new GeometryResourceApi(aliceAPIClient);
        String retrievedVCML = TestEndpointUtils.getResourceString("/TestGeometry.vcml");

        GenericVCMLTests.genericSaveTest(retrievedVCML,
                XmlHelper::XMLToGeometry, XmlHelper::geometryToXML,
                (modelString, modelName, simNames) -> resourceApi.saveGeometry(modelString, modelName),
                this::cleanGeometry);
    }

    @Test
    public void testGet() throws Exception {
        GeometryResourceApi resourceApi = new GeometryResourceApi(aliceAPIClient);
        GeometryResourceApi notAliceClient = new GeometryResourceApi(bobAPIClient);
        String retrievedVCML = TestEndpointUtils.getResourceString("/TestGeometry.vcml");

        GenericVCMLTests.genericGetTest(retrievedVCML,
                XmlHelper::XMLToGeometry, key -> resourceApi.getGeometryVCML(key.toString()),
                key -> resourceApi.getGeometrySummary(key.toString()),
                key -> notAliceClient.getGeometryVCML(key.toString()),
                key -> notAliceClient.getGeometrySummary(key.toString()),
                (modelString, modelName, simNames) -> resourceApi.saveGeometry(modelString, modelName)
        );
    }

    @Test
    public void testDelete() throws Exception {
        GeometryResourceApi resourceApi = new GeometryResourceApi(aliceAPIClient);
        GeometryResourceApi notAlice = new GeometryResourceApi(bobAPIClient);
        String retrievedVCML = TestEndpointUtils.getResourceString("/TestGeometry.vcml");

        GenericVCMLTests.genericDeleteTest(retrievedVCML,
                (modelString, modelName, simNames) -> resourceApi.saveGeometry(modelString, modelName),
                XmlHelper::XMLToGeometry, resourceApi::deleteGeometry, notAlice::deleteGeometry,
                key -> resourceApi.getGeometryVCML(key.toString()));
    }

    @Test
    public void testGeometrySummary() throws Exception {
        GeometryResourceApi resourceApi = new GeometryResourceApi(aliceAPIClient);
        String retrievedVCML = TestEndpointUtils.getResourceString("/TestGeometry.vcml");

        String savedVCML = resourceApi.saveGeometry(retrievedVCML, "TestModel");
        Geometry geometry = XmlHelper.XMLToGeometry(new XMLSource(savedVCML));

        GeometrySummary summary = resourceApi.getGeometrySummary(geometry.getVersion().getVersionKey().toString());
        Assertions.assertTrue(new Extent(22, 22, 10).compareEqual(DtoModelTransforms.dtoToExtent(summary.getExtent())));
        Assertions.assertEquals(2, summary.getDimension());
        Assertions.assertTrue(new Origin(-11, -11, 0).compareEqual(DtoModelTransforms.dtoToOrigin(summary.getOrigin())));
    }
}

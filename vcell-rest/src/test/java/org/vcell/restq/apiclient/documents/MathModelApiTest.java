package org.vcell.restq.apiclient.documents;

import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.modeldb.DatabaseServerImpl;
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
import org.vcell.restclient.api.MathModelResourceApi;
import org.vcell.restclient.model.MathModelSummary;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

@QuarkusTest
public class MathModelApiTest {
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

    private void cleanMathModel(MathModel savedModel, MathModel testModel) throws PropertyVetoException {
        savedModel.setDescription(testModel.getDescription());
        testModel.getMathDescription().setDescription(savedModel.getMathDescription().getDescription());
        testModel.getMathDescription().getGeometry().setDescription(savedModel.getMathDescription().getGeometry().getDescription());
        for (int i = 0; i < testModel.getSimulations().length; i++){
            testModel.getSimulations()[i].setDescription(savedModel.getSimulations()[i].getDescription());
        }
        testModel.setName(savedModel.getName());
    }

    @Test
    public void testMathModelSave() throws Exception {
        MathModelResourceApi mathModelResourceApi = new MathModelResourceApi(aliceAPIClient);
        String retrievedVCML = IOUtils.toString(TestEndpointUtils.class.getResourceAsStream("/TestMath.vcml"));

        GenericVCMLTests.genericSaveTest(retrievedVCML, XmlHelper::XMLToMathModel, XmlHelper::mathModelToXML,
                mathModelResourceApi::saveMathModel, this::cleanMathModel);
    }

    @Test
    public void testMathModelGet() throws Exception {
        MathModelResourceApi mathModelResourceApi = new MathModelResourceApi(aliceAPIClient);
        MathModelResourceApi notAliceClient = new MathModelResourceApi(bobAPIClient);

        String testVCML = IOUtils.toString(TestEndpointUtils.class.getResourceAsStream("/TestMath.vcml"));
        GenericVCMLTests.genericGetTest(testVCML, XmlHelper::XMLToMathModel,
                key -> mathModelResourceApi.getVCML(key.toString()),
                key -> mathModelResourceApi.getSummary(key.toString()),
                key -> notAliceClient.getVCML(key.toString()),
                key -> notAliceClient.getSummary(key.toString()),
                mathModelResourceApi::saveMathModel);
    }

    @Test
    public void deleteTest() throws Exception {
        MathModelResourceApi mathModelResourceApi = new MathModelResourceApi(aliceAPIClient);
        MathModelResourceApi notAliceClient = new MathModelResourceApi(bobAPIClient);

        String testVCML = IOUtils.toString(TestEndpointUtils.class.getResourceAsStream("/TestMath.vcml"));

        GenericVCMLTests.genericDeleteTest(testVCML, mathModelResourceApi::saveMathModel, XmlHelper::XMLToMathModel,
                mathModelResourceApi::deleteMathModel, notAliceClient::deleteMathModel,
                key -> mathModelResourceApi.getVCML(key.toString()));
    }

    @Test
    public void getSummaryTest() throws ApiException, IOException, XmlParseException {
        MathModelResourceApi mathModelResourceApi = new MathModelResourceApi(aliceAPIClient);

        String testVCML = IOUtils.toString(TestEndpointUtils.class.getResourceAsStream("/TestMath.vcml"));
        String savedModelVCML = mathModelResourceApi.saveMathModel(testVCML, "Test", null);
        MathModel savedModel = XmlHelper.XMLToMathModel(new XMLSource(savedModelVCML));

        MathModelSummary summary = mathModelResourceApi.getSummary(savedModel.getVersion().getVersionKey().toString());
        Assertions.assertNotNull(summary);

        Assertions.assertEquals(0, summary.getPublicationInfos().size());
        Assertions.assertEquals("Deterministic", summary.getModelInfo().getModelType().getValue());
        Assertions.assertEquals("TestGeometry", summary.getModelInfo().getGeometryName());
        Assertions.assertEquals(1, summary.getModelInfo().getGeometryDimension());
        Assertions.assertEquals(1, summary.getModelInfo().getSimulationNames().size());
        Assertions.assertEquals("Bleaching DG3", summary.getModelInfo().getSimulationNames().get(0));
        Assertions.assertEquals("cloned from 'Bleaching DG3' owned by user Administrator\n" +
                "cloned from 'Bleaching DG3' owned by user rappala\n" +
                "cloned from 'Simulation0' owned by user temp", summary.getModelInfo().getSimulationAnnotations().get(0));
    }

}

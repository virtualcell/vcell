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
    private DatabaseServerImpl databaseServer;

    @BeforeAll
    public static void setupConfig(){
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void createClients() throws ApiException, DataAccessException {
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        bobAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.bob);
        databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);
        TestEndpointUtils.mapApiClientToNagios(bobAPIClient);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
    }

    private void setMathModelDescriptionAndVersion(MathModel mainModel, MathModel model2) throws PropertyVetoException {
        model2.setDescription(mainModel.getDescription());
        model2.getMathDescription().setDescription(mainModel.getMathDescription().getDescription());
        model2.getMathDescription().getGeometry().setDescription(mainModel.getMathDescription().getGeometry().getDescription());
        for (int i = 0; i < model2.getSimulations().length; i++){
            model2.getSimulations()[i].setDescription(mainModel.getSimulations()[i].getDescription());
        }
        model2.clearVersion();
        mainModel.clearVersion();
    }

    @Test
    public void testMathModelSave() throws Exception {
        MathModelResourceApi mathModelResourceApi = new MathModelResourceApi(aliceAPIClient);

        String retrievedVCML = IOUtils.toString(TestEndpointUtils.class.getResourceAsStream("/TestMath.vcml"));
        MathModel retrievedMathModel = XmlHelper.XMLToMathModel(new XMLSource(retrievedVCML));

        String savedModelVCML = mathModelResourceApi.saveMathModel(retrievedVCML, "TestMathModel", null);
        MathModel savedMathModel = XmlHelper.XMLToMathModel(new XMLSource(savedModelVCML));
        // Can't resave without changes
        try{
            mathModelResourceApi.saveMathModel(savedModelVCML, null, null);
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(500, e.getCode());
        }
        savedMathModel.setDescription("Changed description");
        savedModelVCML = mathModelResourceApi.saveMathModel(XmlHelper.mathModelToXML(savedMathModel), null, null);
        savedMathModel = XmlHelper.XMLToMathModel(new XMLSource(savedModelVCML));

        setMathModelDescriptionAndVersion(savedMathModel, retrievedMathModel);
        Assertions.assertTrue(savedMathModel.compareEqual(retrievedMathModel));

        // Can't save new BioModel under the same name if it does not have the same versionable object as the original.
        try{
            mathModelResourceApi.saveMathModel(XmlHelper.mathModelToXML(savedMathModel), "TestMathModel", null);
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(500, e.getCode());
        }
    }

    @Test
    public void testMathModelGet() throws Exception {
        MathModelResourceApi mathModelResourceApi = new MathModelResourceApi(aliceAPIClient);
        try {
            mathModelResourceApi.getSummary("44");
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(404, e.getCode());
        }
        try {
            mathModelResourceApi.getVCML("44");
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(404, e.getCode());
        }

        String testVCML = IOUtils.toString(TestEndpointUtils.class.getResourceAsStream("/TestMath.vcml"));
        String savedModelVCML = mathModelResourceApi.saveMathModel(testVCML, "Test", null);
        MathModel savedModel = XmlHelper.XMLToMathModel(new XMLSource(savedModelVCML));
        String retrievedVCML = mathModelResourceApi.getVCML(savedModel.getVersion().getVersionKey().toString());
        Assertions.assertEquals(savedModelVCML, retrievedVCML);

        try{
            new MathModelResourceApi(bobAPIClient).getVCML(savedModel.getVersion().getVersionKey().toString());
            Assertions.fail();
        } catch (ApiException e) {
            // Not found for that particular user because they don't own it
            Assertions.assertEquals(404, e.getCode());
        }
    }

    @Test
    public void deleteTest() throws ApiException, IOException, XmlParseException {
        MathModelResourceApi mathModelResourceApi = new MathModelResourceApi(aliceAPIClient);

        String testVCML = IOUtils.toString(TestEndpointUtils.class.getResourceAsStream("/TestMath.vcml"));
        String savedModelVCML = mathModelResourceApi.saveMathModel(testVCML, "Test", null);
        String saved2 = mathModelResourceApi.saveMathModel(testVCML, "Test2", null);

        MathModel savedModel = XmlHelper.XMLToMathModel(new XMLSource(savedModelVCML));
        MathModel savedModel2 = XmlHelper.XMLToMathModel(new XMLSource(saved2));

        mathModelResourceApi.deleteMathModel(savedModel.getVersion().getVersionKey().toString());
        // Can't delete twice
        Assertions.assertThrows(ApiException.class, () -> mathModelResourceApi.deleteMathModel(savedModel.getVersion().getVersionKey().toString()));
        try{
            // Can't grab what's not deleted
            mathModelResourceApi.getVCML(savedModel.getVersion().getVersionKey().toString());
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(404, e.getCode());
        }

        // Wasn't deleted so can get
        Assertions.assertDoesNotThrow(() -> mathModelResourceApi.getVCML(savedModel2.getVersion().getVersionKey().toString()));
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

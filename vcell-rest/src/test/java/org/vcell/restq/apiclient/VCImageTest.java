package org.vcell.restq.apiclient;

import cbit.image.VCImage;
import cbit.vcell.mathmodel.MathModel;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XmlHelper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.VcImageResourceApi;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;

import java.beans.PropertyVetoException;
import java.sql.SQLException;

@QuarkusTest
public class VCImageTest {

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

    }

    @Test
    public void testSave() throws Exception {
        VcImageResourceApi vcImageResourceApi = new VcImageResourceApi(aliceAPIClient);
        String retrievedVCML = TestEndpointUtils.getResourceString("/TestVCImage.vcml");
        String savedVCML = vcImageResourceApi.saveImageVCML(retrievedVCML, "TestImage");
        try{
            vcImageResourceApi.saveImageVCML(retrievedVCML, "TestImage");
            Assertions.fail();
        } catch (ApiException e) {
            Assertions.assertEquals(500, e.getCode());
            Assertions.assertTrue(e.getMessage().contains("already has"));
        }
        VCImage retrievedImage = XmlHelper.XMLToImage(retrievedVCML);
        retrievedImage.setName("TestImage");
        VCImage savedImage = XmlHelper.XMLToImage(savedVCML);
        retrievedImage.setDescription(savedImage.getDescription());

        Assertions.assertTrue(retrievedImage.compareEqual(savedImage));
        savedImage.setDescription("New description");
        vcImageResourceApi.saveImageVCML(XmlHelper.imageToXML(savedImage), null);
    }

    @Test
    public void testGet() throws Exception {
        VcImageResourceApi vcImageResourceApi = new VcImageResourceApi(aliceAPIClient);
        VcImageResourceApi notOwner = new VcImageResourceApi(bobAPIClient);
        String retrievedVCML = TestEndpointUtils.getResourceString("/TestVCImage.vcml");

        try{
            vcImageResourceApi.getImageVCML("44");
        } catch (ApiException e) {
            Assertions.assertEquals(404, e.getCode());
        }
        try{
            vcImageResourceApi.getImageSummary("44");
        } catch (ApiException e) {
            Assertions.assertEquals(404, e.getCode());
        }

        String savedVCML = vcImageResourceApi.saveImageVCML(retrievedVCML, "TestImage");
        VCImage savedImage = XmlHelper.XMLToImage(savedVCML);
        String gotVCML = vcImageResourceApi.getImageVCML(savedImage.getVersion().getVersionKey().toString());
        Assertions.assertEquals(savedVCML, gotVCML);

        try{
            notOwner.getImageSummary(savedImage.getVersion().getVersionKey().toString());
        } catch (ApiException e) {
            Assertions.assertEquals(404, e.getCode());
        }
    }

    @Test
    public void testDelete() throws Exception {
        VcImageResourceApi vcImageResourceApi = new VcImageResourceApi(aliceAPIClient);
        VcImageResourceApi notOwner = new VcImageResourceApi(bobAPIClient);
        String retrievedVCML = TestEndpointUtils.getResourceString("/TestVCImage.vcml");


        VCImage savedImage = XmlHelper.XMLToImage(vcImageResourceApi.saveImageVCML(retrievedVCML, "TestImage"));
        try{
            notOwner.deleteImageVCML(savedImage.getVersion().getVersionKey().toString());
        } catch (ApiException e) {
            Assertions.assertEquals(404, e.getCode());
        }
        vcImageResourceApi.getImageVCML(savedImage.getVersion().getVersionKey().toString());
        vcImageResourceApi.deleteImageVCML(savedImage.getVersion().getVersionKey().toString());
        try{
            vcImageResourceApi.deleteImageVCML(savedImage.getVersion().getVersionKey().toString());
        } catch (ApiException e) {
            Assertions.assertEquals(404, e.getCode());
        }
    }

}

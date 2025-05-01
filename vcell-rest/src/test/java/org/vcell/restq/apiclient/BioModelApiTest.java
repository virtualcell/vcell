package org.vcell.restq.apiclient;

import cbit.sql.QueryHashtable;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.ServerDocumentManager;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.BioModelResourceApi;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;

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
    public void createClients() throws ApiException {
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        bobAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.bob);

        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
    }


    @Test
    public void testSavingBioModel() throws ApiException, IOException, XmlParseException, PropertyVetoException, DataAccessException {
        BioModelResourceApi bioModelResourceApi = new BioModelResourceApi(aliceAPIClient);
        DatabaseServerImpl databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        ServerDocumentManager serverDocumentManager = new ServerDocumentManager(databaseServer);

        String vcmlToBeUploaded = XmlHelper.bioModelToXML(TestEndpointUtils.getTestBioModel());
        String savedBioModelVCML = bioModelResourceApi.saveBioModelAs(vcmlToBeUploaded, "BioModelApiTest_testAddRemove");
        BioModel savedBioModel = XmlHelper.XMLToBioModel(new XMLSource(savedBioModelVCML));

        try{
            bioModelResourceApi.saveBioModel(vcmlToBeUploaded);
            throw new RuntimeException("This should not execute because an ApiException error should be thrown.");
        } catch (ApiException e){
            Assertions.assertEquals(555, e.getCode(), "Can't resave BioModel that has the same name and no version.");
        }

        // BioModel in DB is properly saved and is equal to file uploaded
        String retrievedVCML = serverDocumentManager.getBioModelXML(new QueryHashtable(), TestEndpointUtils.administratorUser,
                savedBioModel.getVersion().getVersionKey(), false);
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlToBeUploaded));
        BioModel retrievedBioModel = XmlHelper.XMLToBioModel(new XMLSource(retrievedVCML));
        Assertions.assertTrue(bioModel.compareEqual(retrievedBioModel));
        Assertions.assertEquals(retrievedVCML, savedBioModelVCML);

        // Saving same BioModel works
        String doubleModelID = bioModelResourceApi.saveBioModel(retrievedVCML);
    }

    @Test
    public void testRemoveBioModel() throws DataAccessException, PropertyVetoException, XmlParseException, IOException, ApiException {
        BioModelResourceApi bioModelResourceApi = new BioModelResourceApi(aliceAPIClient);
        DatabaseServerImpl databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        ServerDocumentManager serverDocumentManager = new ServerDocumentManager(databaseServer);

        String bioModelID = bioModelResourceApi.saveBioModel(XmlHelper.bioModelToXML(TestEndpointUtils.getTestBioModel()));
        bioModelResourceApi.getBioModel(bioModelID);

        bioModelResourceApi.deleteBioModel(bioModelID);
        Assertions.assertThrowsExactly(ObjectNotFoundException.class, () ->
                serverDocumentManager.getBioModelXML(new QueryHashtable(), TestEndpointUtils.administratorUser,
                        new KeyValue(bioModelID), false));
    }
}

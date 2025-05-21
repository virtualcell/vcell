package org.vcell.restq.apiclient;

import cbit.sql.QueryHashtable;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.message.CustomObjectMapper;
import cbit.vcell.model.Species;
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
import org.vcell.restclient.model.SaveBioModel;
import org.vcell.restclient.model.VCellHTTPError;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.errors.exceptions.DataAccessWebException;
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
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
    }


    @Test
    public void testSavingBioModel() throws ApiException, IOException, XmlParseException, PropertyVetoException, DataAccessException, SQLException {
        BioModelResourceApi bioModelResourceApi = new BioModelResourceApi(aliceAPIClient);
        ServerDocumentManager serverDocumentManager = new ServerDocumentManager(databaseServer);
        String vcmlToBeUploaded = XmlHelper.bioModelToXML(TestEndpointUtils.getTestBioModel());

        SaveBioModel saveBioModel = new SaveBioModel().bioModelXML(vcmlToBeUploaded);
        String vcmlReturnedFromSave = bioModelResourceApi.saveBioModel(saveBioModel);
        BioModel savedBioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlReturnedFromSave));

        // Can't save again without changes being made
        try{
            bioModelResourceApi.saveBioModel(saveBioModel);
            throw new RuntimeException("This should not execute because an ApiException error should be thrown.");
        } catch (ApiException e){
            Assertions.assertEquals(DataAccessWebException.HTTP_CODE, e.getCode(), "Can't resave BioModel that has the same name declared.");
            VCellHTTPError error = new CustomObjectMapper().readValue(e.getResponseBody(), VCellHTTPError.class);
            Assertions.assertEquals(DataAccessException.class.getSimpleName(), error.getExceptionType());
            Assertions.assertEquals("'Administrator' already has a BioModel with name 'TestBioModel'", error.getMessage());
        }

        // BioModel in DB is properly saved and is equal to file uploaded
        String retrievedVCML = serverDocumentManager.getBioModelXML(new QueryHashtable(), TestEndpointUtils.administratorUser,
                savedBioModel.getVersion().getVersionKey(), false);
        BioModel ogBioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlToBeUploaded));
        BioModel retrievedBioModel = XmlHelper.XMLToBioModel(new XMLSource(retrievedVCML));
        Assertions.assertTrue(ogBioModel.compareEqual(retrievedBioModel));
        Assertions.assertEquals(retrievedVCML, vcmlReturnedFromSave);

        // Saving same model, but with slight change works
        retrievedBioModel.getModel().addSpecies(new Species("bob", "annotation"));
        saveBioModel.bioModelXML(XmlHelper.bioModelToXML(retrievedBioModel));
        saveBioModel.name(null);
        String secondSave = bioModelResourceApi.saveBioModel(saveBioModel);

        try {
            bioModelResourceApi.saveBioModel(new SaveBioModel());
            throw new RuntimeException("This should not execute because an ApiException error should be thrown.");
        } catch (ApiException e){
            Assertions.assertEquals(400, e.getCode(), "Validation does not allow for saving a BioModel with no text.");
        }
    }

    @Test
    public void testRemoveBioModel() throws PropertyVetoException, XmlParseException, IOException, ApiException, SQLException {
        BioModelResourceApi bioModelResourceApi = new BioModelResourceApi(aliceAPIClient);
        ServerDocumentManager serverDocumentManager = new ServerDocumentManager(databaseServer);
        SaveBioModel saveBioModel = new SaveBioModel().bioModelXML(XmlHelper.bioModelToXML(TestEndpointUtils.getTestBioModel()));

        String bioModelVCML = bioModelResourceApi.saveBioModel(saveBioModel);
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelVCML));
        KeyValue bioModelKey = bioModel.getVersion().getVersionKey();

        bioModelResourceApi.getBioModel(bioModelKey.toString());

        bioModelResourceApi.deleteBioModel(bioModelKey.toString());
        Assertions.assertThrowsExactly(ObjectNotFoundException.class, () ->
                serverDocumentManager.getBioModelXML(new QueryHashtable(), TestEndpointUtils.administratorUser,
                        bioModelKey, false));
    }
}

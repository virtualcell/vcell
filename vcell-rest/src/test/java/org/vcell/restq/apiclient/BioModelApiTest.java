package org.vcell.restq.apiclient;

import cbit.sql.QueryHashtable;
import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.model.Species;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.ServerDocumentManager;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.VCMLComparator;
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
import org.vcell.restclient.CustomObjectMapper;
import org.vcell.restclient.api.BioModelResourceApi;
import org.vcell.restclient.model.BioModelSummary;
import org.vcell.restclient.model.SaveBioModel;
import org.vcell.restclient.model.VCellHTTPError;
import org.vcell.restclient.utils.DtoModelTransforms;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.errors.exceptions.DataAccessWebException;
import org.vcell.restq.errors.exceptions.UnprocessableContentWebException;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.BioModelChildSummary;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.KeyValue;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;

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
        String vcmlReturnedFromSave = bioModelResourceApi.saveBioModel(vcmlToBeUploaded,null, null);
        BioModel savedBioModel = XmlHelper.XMLToBioModel(new XMLSource(vcmlReturnedFromSave));

        // Can't save again without changes being made
        try{
            bioModelResourceApi.saveBioModel(vcmlToBeUploaded, null, null);
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
        Assertions.assertTrue(VCMLComparator.compareEquals(retrievedVCML, vcmlReturnedFromSave, false));

        // Saving same model, but with slight change works
        retrievedBioModel.getModel().addSpecies(new Species("bob", "annotation"));
        saveBioModel.bioModelXML(XmlHelper.bioModelToXML(retrievedBioModel));
        saveBioModel.name(null);
        String secondSave = bioModelResourceApi.saveBioModel(XmlHelper.bioModelToXML(retrievedBioModel), null, null);

        try {
            bioModelResourceApi.saveBioModel(null, null, null);
            throw new RuntimeException("This should not execute because an ApiException error should be thrown.");
        } catch (ApiException e){
            Assertions.assertEquals(400, e.getCode(), "Validation does not allow for saving a BioModel with no text.");
        }
    }

    @Test
    public void testRemoveBioModel() throws PropertyVetoException, XmlParseException, IOException, ApiException, SQLException {
        BioModelResourceApi bioModelResourceApi = new BioModelResourceApi(aliceAPIClient);
        ServerDocumentManager serverDocumentManager = new ServerDocumentManager(databaseServer);

        String bioModelVCML = bioModelResourceApi.saveBioModel(XmlHelper.bioModelToXML(TestEndpointUtils.getTestBioModel()), null, null);
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelVCML));
        KeyValue bioModelKey = bioModel.getVersion().getVersionKey();

        bioModelResourceApi.getBioModel(bioModelKey.toString());

        bioModelResourceApi.deleteBioModel(bioModelKey.toString());
        Assertions.assertThrowsExactly(ObjectNotFoundException.class, () ->
                serverDocumentManager.getBioModelXML(new QueryHashtable(), TestEndpointUtils.administratorUser,
                        bioModelKey, false));
    }


    @Test
    public void testGetBioModelContext() throws PropertyVetoException, XmlParseException, IOException, SQLException, DataAccessException, MappingException, ApiException {
        BioModelResourceApi bioModelResourceApi = new BioModelResourceApi(aliceAPIClient);
        ServerDocumentManager serverDocumentManager = new ServerDocumentManager(databaseServer);
        String vcmlToBeSaved = XmlHelper.bioModelToXML(TestEndpointUtils.getTestBioModel());

        String savedVCML = serverDocumentManager.saveBioModel(new QueryHashtable(), TestEndpointUtils.administratorUser, vcmlToBeSaved, "TestBioModel",
                new String[]{});
        BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(savedVCML));

        BioModelSummary context = bioModelResourceApi.getBioModelSummary(bioModel.getVersion().getVersionKey().toString());
        BioModelInfo info = DtoModelTransforms.bioModelContextToBioModelInfo(context);
        BioModelInfo fromTheSourceInfo = databaseServer.getBioModelInfo(TestEndpointUtils.administratorUser, bioModel.getVersion().getVersionKey());

        Assertions.assertEquals(context.getSummary().getGeometryNames(), new ArrayList<>(){{add("test-geometry");}});
        Assertions.assertTrue(context.getPublicationInformation().isEmpty());

        // 8.0.0 set through Java properties, and for quarkus in the Quarkus properties
        Assertions.assertEquals("8.0.0", context.getvCellSoftwareVersion().getSoftwareVersionString());
        Assertions.assertEquals("TestBioModel", context.getVersion().getName());

        Assertions.assertEquals(fromTheSourceInfo.getVersion().getDate().toInstant(), context.getVersion().getDate().toInstant());
        Assertions.assertEquals(0, context.getVersion().getDate().getOffset().getTotalSeconds()); // Should have no offset from UTC

        Assertions.assertEquals(BioModelChildSummary.MathType.Deterministic, info.getBioModelChildSummary().getAppTypes()[0]);
        Assertions.assertEquals(new ArrayList<>(){{add("non-spatial ODE");}}, context.getSummary().getSimulationContextNames());
    }

    @Test
    public void testBadXML() throws IOException, ApiException {
        BioModelResourceApi bioModelResourceApi = new BioModelResourceApi(aliceAPIClient);
        String badXML = Files.readString(TestEndpointUtils.getResourceFile("/BadXML.xml").toPath());
        try{
            bioModelResourceApi.saveBioModel(badXML, null, null);
            Assertions.fail();
        } catch (ApiException e){
            Assertions.assertEquals(UnprocessableContentWebException.HTTP_CODE, e.getCode());
            Assertions.assertTrue(e.getMessage().contains("DOCTYPE is disallowed"));
        }
    }
}

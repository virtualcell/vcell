package org.vcell.restq.apiclient;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.BioModelResourceApi;
import org.vcell.restclient.api.MathModelResourceApi;
import org.vcell.restclient.api.PublicationResourceApi;
import org.vcell.restclient.model.MathModelSummary;
import org.vcell.restclient.model.MathmodelRef;
import org.vcell.restclient.model.Publication;
import org.vcell.restclient.model.PublishModelsRequest;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.VersionFlag;

import java.sql.SQLException;
import java.util.List;

@QuarkusTest
public class PublicationApiTest {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    private ApiClient aliceAPIClient;
    private ApiClient bobAPIClient;


    @BeforeAll
    public static void setupConfig() throws Exception {
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void createClients() throws ApiException {
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        bobAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.bob);
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);
        TestEndpointUtils.mapApiClientToNagios(bobAPIClient);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
    }


    KeycloakTestClient keycloakClient = new KeycloakTestClient();


    private void addPublications(){

    }

    @Test
    public void guestUserAccess() throws ApiException {
        PublicationResourceApi publisherAPI = new PublicationResourceApi(aliceAPIClient);
        long id = publisherAPI.createPublication(TestEndpointUtils.defaultPublication());

        ApiClient defaultClient = TestEndpointUtils.createUnAuthenticatedAPIClient(testPort);

        Publication publication = publisherAPI.getPublicationById(id);

        PublicationResourceApi guessUserAPI = new PublicationResourceApi(defaultClient);
        Publication retrievedPublication = guessUserAPI.getPublicationById(id);

        Assertions.assertEquals(publication, retrievedPublication);
        publisherAPI.deletePublication(id);
    }

    @Test
    public void testAddListRemove() throws Exception {
        Log.debug("authServerUrl: " + authServerUrl + " to be used later instead of keycloakClient");
        PublicationResourceApi apiInstance = new PublicationResourceApi(aliceAPIClient);

        // test that there are no publications initially
        List<Publication> initialPublications = apiInstance.getPublications();
        int initialPubSize = initialPublications.size();
        Assertions.assertEquals(1, initialPubSize);

        BioModel realBioModel = TestEndpointUtils.defaultBiomodel();
        String bioModelXml = XmlHelper.bioModelToXML(realBioModel, true);
        BioModelResourceApi bioModelAPI = new BioModelResourceApi(aliceAPIClient);
        BioModel savedBioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelAPI.saveBioModel(bioModelXml,null, null)));
        String savedModelKey = savedBioModel.getVersion().getVersionKey().toString();
        org.vcell.restclient.model.BioModel biomodel = bioModelAPI.getBioModel(savedModelKey);

        Publication publication = TestEndpointUtils.defaultPublication();
        assert publication.getBiomodelRefs() != null;
        publication.getBiomodelRefs().add(TestEndpointUtils.biomodelRefFromBioModel(biomodel));
        // save publication pub
        Long newPubKey = apiInstance.createPublication(publication);
        Assertions.assertNotNull(newPubKey);

        // test that pubuser can get publication pub
        Publication pub2 = apiInstance.getPublicationById(newPubKey);
        Assertions.assertNotNull(pub2);

        // test that there is one more publication now and matches pub
        List<Publication> publications = apiInstance.getPublications();
        Assertions.assertEquals(initialPubSize + 1, publications.size());
        publication.setPubKey(newPubKey);
        Log.error("TODO: fix discrepency with LocalDates (after round trip, not same)");
        publication.setDate(publications.get(initialPubSize + 0).getDate());
        Assertions.assertEquals(publication, publications.get(initialPubSize + 0));

        // test that pubuser can delete publication pub
        apiInstance.deletePublication(newPubKey);

        // test that there are no additional publications now
        List<Publication> finalPublications = apiInstance.getPublications();
        Assertions.assertEquals(initialPubSize, finalPublications.size());

        // remove added BioModel
        bioModelAPI.deleteBioModel(savedModelKey);
    }

    /**
     * A publication must be able to carry a MathModel, not just BioModels.
     *
     * <p>Attaching a MathModel was unimplemented in the webapp for a long time — the
     * editor popped "adding MathModels not yet implemented - requires API changes" —
     * and the surrounding tests only ever exercised empty mathmodelRefs, so nothing
     * covered the round trip. This asserts the reference survives create → read back
     * (issue #1741).
     */
    @Test
    public void testAddMathModelToPublication() throws Exception {
        PublicationResourceApi apiInstance = new PublicationResourceApi(aliceAPIClient);
        MathModelResourceApi mathModelAPI = new MathModelResourceApi(aliceAPIClient);

        int initialPubSize = apiInstance.getPublications().size();

        // save the VCML resource as-is, the way MathModelApiTest does; round-tripping it
        // through the object model keeps a reference to a database geometry that does not
        // exist in the ephemeral test database
        String testVCML = TestEndpointUtils.getResourceString("/TestMath.vcml");
        String savedMathModelKey = XmlHelper.XMLToMathModel(new XMLSource(
                        mathModelAPI.saveMathModel(testVCML, "MathModelForPublicationTest", null)))
                .getVersion().getVersionKey().toString();

        // the webapp builds the reference from the summary, so do the same here
        MathModelSummary summary = mathModelAPI.getSummary(savedMathModelKey);
        MathmodelRef mathmodelRef = TestEndpointUtils.mathmodelRefFromSummary(summary);

        Publication publication = TestEndpointUtils.defaultPublication();
        assert publication.getMathmodelRefs() != null;
        publication.getMathmodelRefs().add(mathmodelRef);

        Long newPubKey = apiInstance.createPublication(publication);
        Assertions.assertNotNull(newPubKey);
        try {
            Publication roundTripped = apiInstance.getPublicationById(newPubKey);
            Assertions.assertNotNull(roundTripped.getMathmodelRefs());
            Assertions.assertEquals(1, roundTripped.getMathmodelRefs().size(),
                    "the MathModel reference did not survive the round trip");

            MathmodelRef persisted = roundTripped.getMathmodelRefs().get(0);
            Assertions.assertEquals(Long.parseLong(savedMathModelKey), persisted.getMmKey());
            Assertions.assertEquals(mathmodelRef.getName(), persisted.getName());
            Assertions.assertEquals(mathmodelRef.getOwnerName(), persisted.getOwnerName());

            // and it must still be there when the publication is listed, which is what
            // the desktop client and the website both read
            Publication listed = apiInstance.getPublications().stream()
                    .filter(p -> newPubKey.equals(p.getPubKey()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("new publication missing from the list"));
            Assertions.assertEquals(1, listed.getMathmodelRefs().size());
            Assertions.assertEquals(Long.parseLong(savedMathModelKey), listed.getMathmodelRefs().get(0).getMmKey());
        } finally {
            apiInstance.deletePublication(newPubKey);
            mathModelAPI.deleteMathModel(savedMathModelKey);
        }

        Assertions.assertEquals(initialPubSize, apiInstance.getPublications().size());
    }

    @Test
    public void testPublishBioModels() throws Exception {
        PublicationResourceApi pubAPI = new PublicationResourceApi(aliceAPIClient);
        BioModelResourceApi bioModelAPI = new BioModelResourceApi(aliceAPIClient);

        // Create and save a biomodel with unique name to avoid conflicts with other tests
        BioModel realBioModel = TestEndpointUtils.defaultBiomodel();
        realBioModel.setName("PublishTest_" + System.currentTimeMillis());
        String bioModelXml = XmlHelper.bioModelToXML(realBioModel, true);
        BioModel savedBioModel = XmlHelper.XMLToBioModel(new XMLSource(bioModelAPI.saveBioModel(bioModelXml, null, null)));
        String savedModelKey = savedBioModel.getVersion().getVersionKey().toString();
        org.vcell.restclient.model.BioModel biomodel = bioModelAPI.getBioModel(savedModelKey);

        // Create a publication with the biomodel ref queried from the database
        Publication publication = TestEndpointUtils.defaultPublication();
        assert publication.getBiomodelRefs() != null;
        publication.getBiomodelRefs().add(TestEndpointUtils.biomodelRefFromBioModel(biomodel));
        Long pubKey = pubAPI.createPublication(publication);

        // Verify biomodel is not yet published
        Publication fetchedPub = pubAPI.getPublicationById(pubKey);
        Assertions.assertNotNull(fetchedPub.getBiomodelRefs());
        Assertions.assertEquals(1, fetchedPub.getBiomodelRefs().size());
        Assertions.assertNotEquals(VersionFlag.Published.getIntValue(), fetchedPub.getBiomodelRefs().get(0).getVersionFlag());

        // Publish all models linked to this publication (null request = all)
        pubAPI.publishBioModels(pubKey, null);

        // Verify biomodel is now published
        Publication publishedPub = pubAPI.getPublicationById(pubKey);
        Assertions.assertNotNull(publishedPub.getBiomodelRefs());
        Assertions.assertEquals(1, publishedPub.getBiomodelRefs().size());
        Assertions.assertEquals(VersionFlag.Published.getIntValue(), publishedPub.getBiomodelRefs().get(0).getVersionFlag());

        // Test selective publish with specific keys
        PublishModelsRequest request = new PublishModelsRequest();
        request.setBiomodelKeys(List.of(Long.parseLong(savedModelKey)));
        pubAPI.publishBioModels(pubKey, request);

        // Verify still published
        Publication rePub = pubAPI.getPublicationById(pubKey);
        Assertions.assertEquals(VersionFlag.Published.getIntValue(), rePub.getBiomodelRefs().get(0).getVersionFlag());

        // Cleanup: unpublish before deleting (published models cannot be deleted)
        setVersionFlag(savedModelKey, VersionFlag.Current.getIntValue());
        pubAPI.deletePublication(pubKey);
        bioModelAPI.deleteBioModel(savedModelKey);
    }

    private void setVersionFlag(String bioModelKey, int versionFlagValue) throws DataAccessException, SQLException {
        Object lock = new Object();
        java.sql.Connection connection = agroalConnectionFactory.getConnection(lock);
        connection.prepareStatement(
                "UPDATE vc_biomodel SET versionFlag = " + versionFlagValue +
                        " WHERE id = " + bioModelKey
        ).executeUpdate();
        connection.commit();
        connection.close();
    }

    @Test
    public void testPublishBioModelsUnauthorized() throws Exception {
        PublicationResourceApi alicePubAPI = new PublicationResourceApi(aliceAPIClient);
        PublicationResourceApi bobPubAPI = new PublicationResourceApi(bobAPIClient);

        // Create publication as alice (admin)
        Publication publication = TestEndpointUtils.defaultPublication();
        Long pubKey = alicePubAPI.createPublication(publication);

        // Bob (nagios, non-curator) tries to publish - should fail with permission error
        try {
            bobPubAPI.publishBioModels(pubKey, null);
            Assertions.fail("Expected ApiException for unauthorized user");
        } catch (ApiException e) {
            Assertions.assertTrue(e.getCode() == 403 || e.getCode() == 500,
                    "Expected 403 or 500 error, got " + e.getCode());
        }

        // Cleanup
        alicePubAPI.deletePublication(pubKey);
    }
}
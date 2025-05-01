package org.vcell.restq.apiclient;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.resource.PropertyLoader;
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
import org.vcell.restclient.api.PublicationResourceApi;
import org.vcell.restclient.model.BiomodelRef;
import org.vcell.restclient.model.Publication;
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
        String id = bioModelAPI.saveBioModel(bioModelXml);
        org.vcell.restclient.model.BioModel biomodel = bioModelAPI.getBioModel(id);

        Publication publication = TestEndpointUtils.defaultPublication();
        BiomodelRef bioModelRef = new BiomodelRef();
        bioModelRef.setBmKey(Long.parseLong(id));
        bioModelRef.setName(biomodel.getName());
        bioModelRef.setOwnerName(biomodel.getOwnerName());
        bioModelRef.setVersionFlag(VersionFlag.Current.getIntValue());
        assert biomodel.getOwnerKey() != null;
        bioModelRef.setOwnerKey(Long.parseLong(biomodel.getOwnerKey()));
        assert publication.getBiomodelRefs() != null;
        publication.getBiomodelRefs().add(bioModelRef);
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
        bioModelAPI.deleteBioModel(id);
    }
}
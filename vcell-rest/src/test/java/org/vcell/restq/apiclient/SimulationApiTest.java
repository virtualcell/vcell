package org.vcell.restq.apiclient;

import cbit.vcell.message.VCMessagingException;
import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.modeldb.SimulationRep;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.server.SimulationStatusPersistent;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.SimulationResourceApi;
import org.vcell.restq.Simulations.StatusMessage;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.db.BioModelRestDB;
import org.vcell.restq.db.SimulationRestDB;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@QuarkusTest
public class SimulationApiTest {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    @Inject
    BioModelRestDB bioModelRestDB;

    @Inject
    SimulationRestDB simulationRestDB;

    private static String previousServerID;
    private static String previousMongoHost;
    private static String previousMongoPort;
    private static String previousMongoDB;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private ApiClient aliceAPIClient;
    private ApiClient bobAPIClient;

    @BeforeAll
    public static void setupConfig(){
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
        previousServerID = PropertyLoader.getProperty(PropertyLoader.vcellServerIDProperty, null);
        PropertyLoader.setProperty(PropertyLoader.vcellServerIDProperty, "TEST2");

        previousMongoDB = PropertyLoader.getProperty(PropertyLoader.mongodbDatabase, null);
        PropertyLoader.setProperty(PropertyLoader.mongodbDatabase, "test");

        previousMongoHost = PropertyLoader.getProperty(PropertyLoader.mongodbHostInternal, null);
        PropertyLoader.setProperty(PropertyLoader.mongodbHostInternal, "localhost");

        previousMongoPort = PropertyLoader.getProperty(PropertyLoader.mongodbPortInternal, null);
        PropertyLoader.setProperty(PropertyLoader.mongodbPortInternal, "9080");
        // load up all properties required for simulation control
    }

    @AfterAll
    public static void tearDownConfig(){
        // remove all properties required for simulation control
        if (previousServerID != null) {PropertyLoader.setProperty(PropertyLoader.vcellServerIDProperty, previousServerID);}
        if (previousMongoHost != null) {PropertyLoader.setProperty(PropertyLoader.mongodbHostInternal, previousMongoHost);}
        if (previousMongoDB != null) {PropertyLoader.setProperty(PropertyLoader.mongodbDatabase, previousMongoDB);}
        if (previousMongoPort != null) {PropertyLoader.setProperty(PropertyLoader.mongodbPortInternal, previousMongoPort);}
    }

    @BeforeEach
    public void createClients(){
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        bobAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.bob);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
    }



    @Test
    public void testDBLayerStartStopAndStatus() throws ApiException, PropertyVetoException, XmlParseException, IOException, DataAccessException, SQLException, VCMessagingException {
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);

        String testBioModel = XmlHelper.bioModelToXML(TestEndpointUtils.getTestBioModel());
        String testBioModelID = bioModelRestDB.saveBioModel(TestEndpointUtils.administratorUser, testBioModel).toString();
        BioModelRep bioModelRep = bioModelRestDB.getBioModelRep(new org.vcell.util.document.KeyValue(testBioModelID), TestEndpointUtils.administratorUser);
        org.vcell.util.document.KeyValue simKey = bioModelRep.getSimKeyList()[0];

        SimulationStatusPersistent statusPersistent = simulationRestDB.getSimulationStatus(simKey.toString(), TestEndpointUtils.administratorUser);
        Assertions.assertNull(statusPersistent);

        ArrayList<StatusMessage> statusMessages = simulationRestDB.startSimulation(simKey.toString(), TestEndpointUtils.administratorUser);
        StatusMessage statusMessage = statusMessages.get(0);

        Assertions.assertEquals(1, statusMessages.size());
        Assertions.assertEquals(SimulationJobStatus.SchedulerStatus.WAITING, statusMessage.jobStatus().getSchedulerStatus());
        Assertions.assertEquals(TestEndpointUtils.administratorUser.getName(), statusMessage.userName());
        Assertions.assertEquals(simKey.toString(), statusMessage.jobStatus().getVCSimulationIdentifier().getSimulationKey().toString());
        statusPersistent = simulationRestDB.getSimulationStatus(simKey.toString(), TestEndpointUtils.administratorUser);
        Assertions.assertEquals("waiting: too many jobs", statusPersistent.getStatusString());


        SimulationRep simulationRep = simulationRestDB.stopSimulation(simKey.toString(), TestEndpointUtils.administratorUser);
        statusPersistent = simulationRestDB.getSimulationStatus(simKey.toString(), TestEndpointUtils.administratorUser);
        Assertions.assertEquals("stopped", statusPersistent.getStatusString());
        Assertions.assertEquals(simKey.toString(), simulationRep.getKey().toString());
        Assertions.assertEquals(TestEndpointUtils.administratorUser, simulationRep.getOwner());
    }




    //TODO: Test that ensures the proper request to start or stop a simulation was made to the RPC. A different test
    // class should ensure this RPC actually fulfills on its promise. Don't need actual simulations in DB for this
    @Test
    public void testStartAndStop() throws Exception {
        
    }
}

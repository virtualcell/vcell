package org.vcell.restq.apiclient;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.mapping.MappingException;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.solver.server.SimulationMessagePersistent;
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
import org.vcell.restclient.model.SchedulerStatus;
import org.vcell.restclient.model.Status;
import org.vcell.restq.Simulations.SimulationStatusPersistentRecord;
import org.vcell.restq.Simulations.StatusMessage;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.services.BioModelRestService;
import org.vcell.restq.services.SimulationRestService;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@QuarkusTest
public class SimulationApiTest {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    @Inject
    BioModelRestService bioModelRestService;

    @Inject
    SimulationRestService simulationRestService;

    private static String previousServerID;
    private static String previousMongoHost;
    private static String previousMongoPort;
    private static String previousMongoDB;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private ApiClient aliceAPIClient;
    private ApiClient bobAPIClient;
    private BioModelRep bioModelRep = null;

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
    public void createClients() throws ApiException, XmlParseException, DataAccessException, PropertyVetoException, IOException, SQLException, MappingException {
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        bobAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.bob);
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);


        String testBioModel = XmlHelper.bioModelToXML(TestEndpointUtils.getTestBioModel());
        BioModel bioModel = bioModelRestService.save(TestEndpointUtils.administratorUser, testBioModel, null, null);
        bioModelRep = bioModelRestService.getBioModelRep(bioModel.getVersion().getVersionKey(), TestEndpointUtils.administratorUser);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
    }



    @Test
    public void testDBLayerStartStopAndStatus() throws ApiException, PropertyVetoException, XmlParseException, IOException, DataAccessException, SQLException, VCMessagingException {
        KeyValue simKey = bioModelRep.getSimKeyList()[0];

        SimulationStatusPersistentRecord statusPersistent = simulationRestService.getBioModelSimulationStatus(simKey.toString(), bioModelRep.getBmKey().toString(), TestEndpointUtils.administratorUser);
        Assertions.assertNull(statusPersistent);

        ArrayList<StatusMessage> statusMessages = simulationRestService.startSimulation(simKey.toString(), TestEndpointUtils.administratorUser);
        StatusMessage statusMessage = statusMessages.get(0);

        Assertions.assertEquals(1, statusMessages.size());
        Assertions.assertEquals(SimulationJobStatus.SchedulerStatus.WAITING, statusMessage.jobStatus().fieldSchedulerStatus());
        Assertions.assertEquals(TestEndpointUtils.administratorUser.getName(), statusMessage.userName());
        Assertions.assertEquals(simKey.toString(), statusMessage.jobStatus().fieldVCSimID().getSimulationKey().toString());

        simulationRestService.stopSimulation(simKey.toString(), TestEndpointUtils.administratorUser);
        statusPersistent = simulationRestService.getBioModelSimulationStatus(simKey.toString(), bioModelRep.getBmKey().toString(), TestEndpointUtils.administratorUser);
        Assertions.assertEquals(SimulationStatusPersistentRecord.Status.STOPPED.statusDescription, statusPersistent.status().statusDescription);
    }




    //TODO: Test that ensures the proper request to start or stop a simulation was made to the RPC. A different test
    // class should ensure this RPC actually fulfills on its promise. Don't need actual simulations in DB for this
    @Test
    public void testStartAndStop() throws Exception {
        KeyValue simKey = bioModelRep.getSimKeyList()[0];

        SimulationResourceApi simulationResourceApi = new SimulationResourceApi(aliceAPIClient);
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);

        List<org.vcell.restclient.model.StatusMessage> startStatus = simulationResourceApi.startSimulation(simKey.toString());
        Assertions.assertEquals(1, startStatus.size());
        Assertions.assertEquals(TestEndpointUtils.administratorUser.getName(), startStatus.get(0).getUserName());
        Assertions.assertEquals(SchedulerStatus.WAITING, startStatus.get(0).getJobStatus().getFieldSchedulerStatus());

        org.vcell.restclient.model.SimulationStatusPersistentRecord getStatus = simulationResourceApi.getSimulationStatus(simKey.toString(), bioModelRep.getBmKey().toString(), null);
        Assertions.assertNotNull(getStatus);
        Assertions.assertEquals(Status.WAITING, getStatus.getStatus());
        Assertions.assertEquals(SimulationMessagePersistent.MESSAGE_JOB_WAITING.getDisplayMessage(), getStatus.getDetails());

        List<org.vcell.restclient.model.StatusMessage> stopStatus = simulationResourceApi.stopSimulation(simKey.toString());
        Assertions.assertEquals(1, stopStatus.size());
        Assertions.assertEquals(TestEndpointUtils.administratorUser.getName(), stopStatus.get(0).getUserName());
        Assertions.assertEquals(SchedulerStatus.STOPPED, stopStatus.get(0).getJobStatus().getFieldSchedulerStatus());

    }
}

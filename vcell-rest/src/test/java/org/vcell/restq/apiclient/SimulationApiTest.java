package org.vcell.restq.apiclient;

import cbit.rmi.event.WorkerEvent;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.server.dispatcher.SimulationDatabaseDirect;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.BioModelRep;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.SimulationJobStatus;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.server.SimulationMessage;
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
import cbit.vcell.message.server.dispatcher.SimulationDispatcherEngine;
import org.vcell.restq.Simulations.DTO.SimulationStatus;
import org.vcell.restq.Simulations.DTO.StatusMessage;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.db.BioModelRestDB;
import org.vcell.restq.Simulations.SimulationRestDB;
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
    BioModelRestDB bioModelRestDB;

    @Inject
    SimulationRestDB simulationRestDB;


    AdminDBTopLevel adminDBTopLevel;
    SimulationDatabaseDirect simulationDatabaseDirect;
    DatabaseServerImpl databaseServer;

    private static String previousServerID;
    private static String previousMongoHost;
    private static String previousMongoPort;
    private static String previousMongoDB;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private ApiClient aliceAPIClient;
    private ApiClient bobAPIClient;
    private BioModelRep bioModelRep;

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
    public void createClients() throws ApiException, XmlParseException, DataAccessException, PropertyVetoException, IOException, SQLException {
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        bobAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.bob);
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);

        String testBioModel = XmlHelper.bioModelToXML(TestEndpointUtils.getTestBioModel());
        String testBioModelID = bioModelRestDB.saveBioModel(TestEndpointUtils.administratorUser, testBioModel).toString();
        bioModelRep = bioModelRestDB.getBioModelRep(new org.vcell.util.document.KeyValue(testBioModelID), TestEndpointUtils.administratorUser);

        databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        adminDBTopLevel = new AdminDBTopLevel(agroalConnectionFactory);
        simulationDatabaseDirect = new SimulationDatabaseDirect(adminDBTopLevel, databaseServer, false);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
        bioModelRestDB.deleteBioModel(TestEndpointUtils.administratorUser, bioModelRep.getBmKey());

        databaseServer = null;
        adminDBTopLevel = null;
        simulationDatabaseDirect = null;
    }



    @Test
    public void testDBLayerStartStopAndStatus() throws ApiException, PropertyVetoException, XmlParseException, IOException, DataAccessException, SQLException, VCMessagingException {
        KeyValue simKey = bioModelRep.getSimKeyList()[0];

        SimulationStatus statusPersistent = simulationRestDB.getBioModelSimulationStatus(simKey.toString(), bioModelRep.getBmKey().toString(), TestEndpointUtils.administratorUser);
        Assertions.assertNull(statusPersistent);

        ArrayList<StatusMessage> statusMessages = simulationRestDB.startSimulation(simKey.toString(), TestEndpointUtils.administratorUser);
        StatusMessage statusMessage = statusMessages.get(0);

        Assertions.assertEquals(1, statusMessages.size());
        Assertions.assertEquals(SimulationJobStatus.SchedulerStatus.WAITING, statusMessage.jobStatus().fieldSchedulerStatus());
        Assertions.assertEquals(TestEndpointUtils.administratorUser.getName(), statusMessage.userName());
        Assertions.assertEquals(simKey.toString(), statusMessage.jobStatus().fieldVCSimID().getSimulationKey().toString());

        simulationRestDB.stopSimulation(simKey.toString(), TestEndpointUtils.administratorUser);
        statusPersistent = simulationRestDB.getBioModelSimulationStatus(simKey.toString(), bioModelRep.getBmKey().toString(), TestEndpointUtils.administratorUser);
        Assertions.assertEquals(SimulationStatus.Status.STOPPED.statusDescription, statusPersistent.status().statusDescription);
    }


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

    @Test
    public void testDifferentSimulationStates() throws Exception{
        KeyValue simKey = bioModelRep.getSimKeyList()[0];

        SimulationStatus statusPersistent = simulationRestDB.getBioModelSimulationStatus(simKey.toString(), bioModelRep.getBmKey().toString(), TestEndpointUtils.administratorUser);
        Assertions.assertNull(statusPersistent);

        ArrayList<StatusMessage> statusMessages = simulationRestDB.startSimulation(simKey.toString(), TestEndpointUtils.administratorUser);

        Simulation simulation = XmlHelper.XMLToSim(databaseServer.getSimulationXML(TestEndpointUtils.administratorUser, simKey).toString());
        SimulationJob simulationJob = new SimulationJob(simulation, 0, null);
        SimulationTask simulationTask = new SimulationTask(simulationJob, 0);
        WorkerEvent workerEvent = new WorkerEvent(WorkerEvent.JOB_ACCEPTED, "testService", simulationTask, "testHost", SimulationMessage.MESSAGE_JOB_ACCEPTED);
        SimulationDispatcherEngine simulationDispatcherEngine = new SimulationDispatcherEngine();

        simulationDispatcherEngine.onWorkerEvent(workerEvent, simulationDatabaseDirect, null);

        SimulationStatus simulationStatus = simulationRestDB.getBioModelSimulationStatus(simKey.toString(), bioModelRep.getBmKey().toString(), TestEndpointUtils.administratorUser);
        Assertions.assertEquals(SimulationStatus.Status.DISPATCHED, simulationStatus.status());

        workerEvent = new WorkerEvent(WorkerEvent.JOB_PROGRESS, "testService", new VCSimulationIdentifier(simKey, TestEndpointUtils.administratorUser),
                0, "testHost", 0, .3, 1.3, SimulationMessage.MESSAGE_WORKEREVENT_PROGRESS);

        simulationDispatcherEngine.onWorkerEvent(workerEvent, simulationDatabaseDirect, null);
        simulationStatus = simulationRestDB.getBioModelSimulationStatus(simKey.toString(), bioModelRep.getBmKey().toString(), TestEndpointUtils.administratorUser);
        Assertions.assertEquals(SimulationStatus.Status.RUNNING, simulationStatus.status());

        workerEvent = new WorkerEvent(WorkerEvent.JOB_COMPLETED, "testService", new VCSimulationIdentifier(simKey, TestEndpointUtils.administratorUser),
                0, "testHost", 0, 1.0, 10.0, SimulationMessage.MESSAGE_JOB_COMPLETED);

        simulationDispatcherEngine.onWorkerEvent(workerEvent, simulationDatabaseDirect, null);
        simulationStatus = simulationRestDB.getBioModelSimulationStatus(simKey.toString(), bioModelRep.getBmKey().toString(), TestEndpointUtils.administratorUser);
        Assertions.assertEquals(SimulationStatus.Status.COMPLETED, simulationStatus.status());
    }

}

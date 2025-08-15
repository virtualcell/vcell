package org.vcell.restq.exports;

import cbit.vcell.export.server.ExportEnums;
import cbit.vcell.math.VariableType;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataIdentifier;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.ExportResourceApi;
import org.vcell.restclient.model.*;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@QuarkusTest
public class ExportRequestTest {
    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;
    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private DataServerImpl dataServer;
    private final String simulationID = "597714292";
    private ApiClient aliceAPIClient;

    @BeforeAll
    public static void setupConfig(){
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void setUp() throws IOException, DataAccessException, ApiException, SQLException {
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);
        DatabaseServerImpl databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());

        File testSimData = new File(ExportRequestTest.class.getResource("/simdata").getPath());
        TestEndpointUtils.setSystemProperties(testSimData.getAbsolutePath(), System.getProperty("java.io.tmpdir"));
        Cachetable cachetable = new Cachetable(10 * Cachetable.minute, 10000);
        DataSetControllerImpl dataSetController = new DataSetControllerImpl(cachetable, testSimData, testSimData);
        dataServer = new DataServerImpl(dataSetController, null);
        TestEndpointUtils.insertAdminsSimulation(databaseServer, agroalConnectionFactory);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
        TestEndpointUtils.restoreSystemProperties();
    }

//     Tests the clients capability to submit exports, our queue for accepting and distributing messages, retrieval of export status, and the export job itself
    @Test
    public void testExportRequestClient() throws Exception {
        final long time = Instant.now().getEpochSecond(); // Before export even starts, so that all events are grabbed from the queue
        ExportResourceApi exportResourceApi = new ExportResourceApi(aliceAPIClient);
        N5ExportRequest exportRequest = getValidExportRequestDTO(0, 1);
        exportResourceApi.exportN5(exportRequest);

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try{
                List<ExportEvent> allEvents = new ArrayList<>();
                while (allEvents.isEmpty()){
                    Thread.sleep(100);
                    allEvents = exportResourceApi.exportStatus(time);
                }
                ExportEvent eventUnderInspection = allEvents.stream().toList().get(0);
                Assertions.assertEquals(ExportEnums.ExportProgressType.EXPORT_ASSEMBLING, ExportEnums.ExportProgressType.valueOf(eventUnderInspection.getEventType().getValue()));
                while (ExportEnums.ExportProgressType.valueOf(eventUnderInspection.getEventType().getValue()) != ExportEnums.ExportProgressType.EXPORT_COMPLETE){
                    allEvents = exportResourceApi.exportStatus(time);
                    eventUnderInspection = allEvents.stream().toList().get(allEvents.size() - 1);
                    Thread.sleep(500);
                }
                Assertions.assertEquals(ExportProgressType.COMPLETE, eventUnderInspection.getEventType());
            } catch (Exception e){
                Assertions.fail();
            }
        }).orTimeout(20, TimeUnit.SECONDS);
        future.join();
    }

    private N5ExportRequest getValidExportRequestDTO(int startTimeIndex, int endTimeIndex) throws Exception {
        VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(new KeyValue(simulationID), TestEndpointUtils.administratorUser);
        VCSimulationDataIdentifier simulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);
        DataIdentifier[] dataIdentifier = dataServer.getDataIdentifiers(new OutputContext(new AnnotatedFunction[0]), TestEndpointUtils.administratorUser, simulationDataIdentifier);
        DataIdentifier volumetricDataID = getOneDIWithSpecificType(VariableType.VOLUME, dataIdentifier);
        double[] allTimes = dataServer.getDataSetTimes(TestEndpointUtils.administratorUser, simulationDataIdentifier);
        StandardExportInfo exportRequest = new StandardExportInfo()
                .simulationKey(simulationID)
                .simulationJob(0)
                .geometrySpecs(new GeometrySpecDTO().geometryMode(GeometryMode.SELECTIONS).selections(new ArrayList<>()))
                .timeSpecs(
                        new org.vcell.restclient.model.TimeSpecs().beginTimeIndex(startTimeIndex).endTimeIndex(endTimeIndex).allTimes(Arrays.stream(allTimes).boxed().toList()).mode(TimeMode.RANGE)
                )
                .variableSpecs(
                        new org.vcell.restclient.model.VariableSpecs().variableNames(new ArrayList<>(){{add(volumetricDataID.getName());}}).mode(VariableMode.ONE)
                )
                .contextName("")
                .simulationName(vcSimulationIdentifier.getID())
                .outputContext(new ArrayList<>());

        return new N5ExportRequest().standardExportInformation(exportRequest)
                .exportableDataType(ExportableDataType.PDE_VARIABLE_DATA)
                .datasetName("testExport")
                .subVolume(new java.util.HashMap<>());
    }

    private DataIdentifier getOneDIWithSpecificType(VariableType variableType, DataIdentifier[] dataIdentifiers){
        for(DataIdentifier dataIdentifier: dataIdentifiers){
            if(dataIdentifier.getVariableType().equals(variableType)){
                return dataIdentifier;
            }
        }
        throw new RuntimeException("Cannot find variable type " + variableType);
    }

}

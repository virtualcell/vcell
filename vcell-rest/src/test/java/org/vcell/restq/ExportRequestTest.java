package org.vcell.restq;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.server.*;
import cbit.vcell.math.VariableType;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.xml.XmlParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.BlockingIterable;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.vcell.restclient.ApiException;
import org.vcell.restq.activemq.ExportRequestListenerMQ;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.handlers.ExportResource;
import org.vcell.restq.services.Exports.ExportService;
import org.vcell.restq.services.Exports.ExportStatusCreator;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;

import javax.jms.JMSException;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@QuarkusTest
public class ExportRequestTest {
    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    @Inject
    ExportRequestListenerMQ requestListenerMQ;
    @Inject
    ExportService exportService;
    @Inject
    ExportStatusCreator statusCreator;

    private DatabaseServerImpl databaseServer;
    private DataServerImpl dataServer;
    private final String simulationID = "597714292";

    @BeforeAll
    public static void setupConfig(){
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void setUp() throws IOException, DataAccessException {
        databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());

        File testSimData = new File(ExportRequestTest.class.getResource("/simdata").getPath());
        TestEndpointUtils.setSystemProperties(testSimData.getAbsolutePath(), System.getProperty("java.io.tmpdir"));
        Cachetable cachetable = new Cachetable(10 * Cachetable.minute, 10000);
        DataSetControllerImpl dataSetController = new DataSetControllerImpl(cachetable, testSimData, testSimData);
        dataServer = new DataServerImpl(dataSetController, null);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
        TestEndpointUtils.restoreSystemProperties();
    }

    @Test
    public void testExportQueue() throws JMSException, JsonProcessingException, ObjectNotFoundException {
        ExportSpecs exportSpecs = new ExportSpecs(null, null, null, null, null, null, "TestSim", null);
        ExportResource.ExportJob exportJob = new ExportResource.ExportJob(1, TestEndpointUtils.administratorUser,
                exportSpecs, new AnnotatedFunction[]{});
        exportService.addExportJobToQueue(exportJob);
        Awaitility.await().atMost(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Assertions.assertFalse(requestListenerMQ.getAcceptedJobs().isEmpty());
                    Assertions.assertEquals(exportJob.exportSpecs().getSimulationName(), requestListenerMQ.getAcceptedJobs().get(0).exportSpecs().getSimulationName());
                });
        Multi<ExportEvent> status = statusCreator.getUsersExportStatus(TestEndpointUtils.administratorUser, exportJob.id());

        // This request should fail
        Awaitility.await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            Assertions.assertThrows(Exception.class, () -> statusCreator.getUsersExportStatus(exportJob.user(), exportJob.id()));
        });
    }

    @Test
    public void testExportStatus() throws Exception {
        long jobID = 1;
        ExportSpecs exportSpecs = getValidExportSpec(0, 1);
        Multi<ExportEvent> status = createExportListener(exportSpecs, jobID);
        BlockingIterable<ExportEvent> blockingIterable = status.subscribe().asIterable();
        CompletableFuture.runAsync(() -> {
            try {
                ExportServiceImpl.makeRemoteFile(new OutputContext(new AnnotatedFunction[]{}), TestEndpointUtils.administratorUser,
                        dataServer, exportSpecs, statusCreator, jobID);
            } catch (Exception e) {
                // If an exception is thrown during the export process, the blocking iterable will hang because no finish statement has been sent
                throw new RuntimeException(e);
            }
        });
        int i = 0;
        for (ExportEvent exportEvent : blockingIterable) {
            switch (i){
                case 0:
                    Assertions.assertNull(exportEvent.getProgress());
                    Assertions.assertEquals(ExportEvent.EXPORT_START, exportEvent.getEventTypeID());
                    break;
                case 1:
                    Assertions.assertEquals(.25,exportEvent.getProgress());
                    Assertions.assertEquals(ExportEvent.EXPORT_PROGRESS, exportEvent.getEventTypeID());
                    break;
                case 2:
                    Assertions.assertEquals(.8125,exportEvent.getProgress());
                    Assertions.assertEquals(ExportEvent.EXPORT_PROGRESS, exportEvent.getEventTypeID());
                    Assertions.assertNull(exportEvent.getLocation());
                    break;
                case 3:
                    Assertions.assertNull(exportEvent.getProgress());
                    Assertions.assertEquals(ExportEvent.EXPORT_COMPLETE, exportEvent.getEventTypeID());
                    Assertions.assertNotNull(exportEvent.getLocation());
                    break;
                default:
                    Assertions.fail();
            }
            Assertions.assertEquals(1, exportEvent.getJobID());
            Assertions.assertEquals(simulationID, exportEvent.getDataKey().toString());
            Assertions.assertEquals(ExportFormat.N5.name(), exportEvent.getFormat());
            i++;
        }

        long badJobID = 2;
        ExportSpecs badExportSpecs = new ExportSpecs(exportSpecs.getVCDataIdentifier(), null, null, null, null, null, "TestSim", null);
        status = createExportListener(badExportSpecs, badJobID);
        CompletableFuture.runAsync(() -> {
            try {
                ExportResource.ExportJob exportJob = new ExportResource.ExportJob(badJobID, TestEndpointUtils.administratorUser,
                        badExportSpecs, new AnnotatedFunction[]{});
                requestListenerMQ.startJob(exportJob);
                Assertions.fail();
            } catch (Exception e) {
                Assertions.assertInstanceOf(NullPointerException.class, e);
            }
        });
        blockingIterable = status.subscribe().asIterable();
        for (ExportEvent exportEvent : blockingIterable) {
            Assertions.assertEquals(ExportEvent.EXPORT_FAILURE, exportEvent.getEventTypeID());
        }
    }


    private ExportResource.ExportRequest getValidExportRequest(int startTimeIndex, int endTimeIndex) throws Exception {
        N5Specs.CompressionLevel compressionLevel = N5Specs.CompressionLevel.RAW;
        VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(new KeyValue(simulationID), TestEndpointUtils.administratorUser);
        VCSimulationDataIdentifier simulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);
        DataIdentifier[] dataIdentifier = dataServer.getDataIdentifiers(new OutputContext(new AnnotatedFunction[0]), TestEndpointUtils.administratorUser, simulationDataIdentifier);
        DataIdentifier volumetricDataID = getOneDIWithSpecificType(VariableType.VOLUME, dataIdentifier);

        VariableSpecs variableSpecs = new VariableSpecs(new ArrayList<>(){{add(volumetricDataID.getName());}}, ExportSpecss.VariableMode.VARIABLE_ONE);
        GeometrySpecs geometrySpecs = new GeometrySpecs(new SpatialSelection[0], 0, 0, ExportSpecss.GeometryMode.GEOMETRY_SELECTIONS);
        N5Specs n5Specs = new N5Specs(ExportSpecss.ExportableDataType.PDE_VARIABLE_DATA, ExportFormat.N5, compressionLevel, simulationID);

        double[] allTimes = dataServer.getDataSetTimes(TestEndpointUtils.administratorUser, simulationDataIdentifier);
        TimeSpecs timeSpecs = new TimeSpecs(startTimeIndex, endTimeIndex, allTimes, ExportSpecss.TimeMode.TIME_RANGE);

        return new ExportResource.ExportRequest(new ArrayList<>(){},ExportFormat.N5, simulationDataIdentifier,
                n5Specs, geometrySpecs, timeSpecs, variableSpecs, vcSimulationIdentifier.getID(), "");
    }

    private ExportSpecs getValidExportSpec(int startTimeIndex, int endTimeIndex) throws Exception {
        ExportResource.ExportRequest request = getValidExportRequest(startTimeIndex, endTimeIndex);
        return new ExportSpecs(request.dataIdentifier(), request.exportFormat(), request.variableSpecs(),
                request.timeSpecs(), request.geometrySpecs(), request.formatSpecificSpecs(), request.simulationName(),
                request.contextName());
    }

    private DataIdentifier getOneDIWithSpecificType(VariableType variableType, DataIdentifier[] dataIdentifiers){
        for(DataIdentifier dataIdentifier: dataIdentifiers){
            if(dataIdentifier.getVariableType().equals(variableType)){
                return dataIdentifier;
            }
        }
        throw new RuntimeException("Cannot find variable type " + variableType);
    }

    private Multi<ExportEvent> createExportListener(ExportSpecs exportSpecs, long jobID) throws Exception {
        HashMap<Integer, String> dummyMaskInfo = new HashMap<>(){{put(0, "Dummy"); put(1, "Test");}};
        exportSpecs.setExportMetaData(new HumanReadableExportData("", "", "", new ArrayList<>(), "", "", false, dummyMaskInfo));

        ExportResource.ExportJob exportJob = new ExportResource.ExportJob(jobID, TestEndpointUtils.administratorUser,
                exportSpecs, new AnnotatedFunction[]{});
        statusCreator.addServerExportListener(TestEndpointUtils.administratorUser, exportJob.id());

        return statusCreator.getUsersExportStatus(TestEndpointUtils.administratorUser, exportJob.id());
    }

}

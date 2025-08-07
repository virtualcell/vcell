package org.vcell.restq.exports;

import cbit.rmi.event.ExportEvent;
import cbit.vcell.export.server.*;
import cbit.vcell.math.VariableType;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.*;
import cbit.vcell.solver.AnnotatedFunction;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.BlockingIterable;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.jms.JMSException;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.junit.jupiter.api.*;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.activemq.ExportMQInterface;
import org.vcell.restq.activemq.ExportRequestListenerMQ;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.handlers.ExportResource;
import org.vcell.restq.services.Exports.ExportService;
import org.vcell.restq.services.Exports.ServerExportEventController;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@QuarkusTest
public class ExportServerTest {
    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    @Inject
    Instance<ExportMQInterface> requestListenerMQ;
    @Inject
    ServerExportEventController statusCreator;
    @Inject
    ExportService exportService;
    @Inject
    ObjectMapper mapper;

    private DataServerImpl dataServer;
    private final String simulationID = "597714292";
    private final HashMap<Integer, String> dummyMaskInfo = new HashMap<>(){{put(0, "Dummy"); put(1, "Test");}};

    @BeforeAll
    public static void setupConfig() throws JMSException {
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void setUp() throws IOException, DataAccessException, SQLException {
        File testSimData = new File(ExportServerTest.class.getResource("/simdata").getPath());
        TestEndpointUtils.setSystemProperties(testSimData.getAbsolutePath(), System.getProperty("java.io.tmpdir"));
        Cachetable cachetable = new Cachetable(10 * Cachetable.minute, 10000);
        DataSetControllerImpl dataSetController = new DataSetControllerImpl(cachetable, testSimData, testSimData);
        dataServer = new DataServerImpl(dataSetController, null);
        DatabaseServerImpl databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        TestEndpointUtils.insertAdminsSimulation(databaseServer, agroalConnectionFactory);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
        TestEndpointUtils.restoreSystemProperties();
    }


    // TODO: Add test for the queue itself, ensuring that the ack's and nack's are handled appropriately.


    @Test //Tests export listener, and the export service directly without any asynchronous behavior
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
                    Assertions.assertEquals(ExportEnums.ExportProgressType.EXPORT_START, exportEvent.getEventType());
                    break;
                case 1:
                    Assertions.assertEquals(.25,exportEvent.getProgress());
                    Assertions.assertEquals(ExportEnums.ExportProgressType.EXPORT_PROGRESS, exportEvent.getEventType());
                    break;
                case 2:
                    Assertions.assertEquals(.8125,exportEvent.getProgress());
                    Assertions.assertEquals(ExportEnums.ExportProgressType.EXPORT_PROGRESS, exportEvent.getEventType());
                    Assertions.assertNull(exportEvent.getLocation());
                    break;
                case 3:
                    Assertions.assertNull(exportEvent.getProgress());
                    Assertions.assertEquals(ExportEnums.ExportProgressType.EXPORT_COMPLETE, exportEvent.getEventType());
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
    }

    @Test
    public void testInvalidInputException() throws Exception {
        ExportSpecs exportSpecs = getValidExportSpec(0, 1);
        long badJobID = 2;
        ExportSpecs badExportSpecs = new ExportSpecs(exportSpecs.getVCDataIdentifier(), null, null, null,
                new GeometrySpecs(new SpatialSelection[]{}, 1, 1, ExportEnums.GeometryMode.GEOMETRY_SLICE), null, "TestSim", null);
        Multi<ExportEvent> status = createExportListener(badExportSpecs, badJobID);
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            VCSimulationDataIdentifier vcSimulationDataIdentifier = (VCSimulationDataIdentifier) exportSpecs.getVCDataIdentifier();
            ExportRequestListenerMQ.ExportJob exportJob = new ExportRequestListenerMQ.ExportJob(badJobID, TestEndpointUtils.administratorUser,
                    new AnnotatedFunction[]{}, vcSimulationDataIdentifier.getVcSimID(), vcSimulationDataIdentifier.getJobIndex(), null, null, null, null, null,"TestSim", null);
            CompletableFuture<Void> job = null;
            try {
                job = requestListenerMQ.get().startJob(Message.of(mapper.writeValueAsString(exportJob)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            job.join();
            Assertions.assertTrue(job.isDone());
        });
        BlockingIterable<ExportEvent> blockingIterable = status.subscribe().asIterable();
        for (ExportEvent exportEvent : blockingIterable) {
            Assertions.assertEquals(ExportEnums.ExportProgressType.EXPORT_FAILURE, exportEvent.getEventType());
        }
        future.join();
    }

    @Test
    public void testLongRunningThread() throws Exception {
        ExportRequestListenerMQ.ExportJob exportJob = exportService.createExportJobFromRequest(TestEndpointUtils.administratorUser, getValidExportRequest(0, 3).standardExportInformation(),
                new N5Specs(ExportEnums.ExportableDataType.PDE_VARIABLE_DATA, ExportFormat.N5, "TestDataset", dummyMaskInfo), ExportFormat.N5);
        statusCreator.addServerExportListener(TestEndpointUtils.administratorUser, exportJob.id());

        ((ExportRequestListenerMQ) requestListenerMQ.get()).setThreadWaitTimeUnit(TimeUnit.MILLISECONDS);
        CompletableFuture<Void> job = ((ExportRequestListenerMQ) requestListenerMQ.get()).startJob(Message.of(mapper.writeValueAsString(exportJob)), false);
        try{
            job.join();
            Assertions.fail();
        } catch (CompletionException e) {
            Assertions.assertEquals(TimeoutException.class, e.getCause().getClass());
        }
        Assertions.assertTrue(job.isCompletedExceptionally());
        ((ExportRequestListenerMQ) requestListenerMQ.get()).setThreadWaitTimeUnit(TimeUnit.MINUTES);
    }


    private ExportResource.N5ExportRequest getValidExportRequest(int startTimeIndex, int endTimeIndex) throws Exception {
        VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(new KeyValue(simulationID), TestEndpointUtils.administratorUser);
        VCSimulationDataIdentifier simulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);
        DataIdentifier[] dataIdentifier = dataServer.getDataIdentifiers(new OutputContext(new AnnotatedFunction[0]), TestEndpointUtils.administratorUser, simulationDataIdentifier);
        DataIdentifier volumetricDataID = getOneDIWithSpecificType(VariableType.VOLUME, dataIdentifier);

        VariableSpecs variableSpecs = new VariableSpecs(new ArrayList<>(){{add(volumetricDataID.getName());}}, ExportEnums.VariableMode.VARIABLE_ONE);
        ExportResource.GeometrySpecDTO geometrySpecs = new ExportResource.GeometrySpecDTO(new SpatialSelection[0], 0, 0, ExportEnums.GeometryMode.GEOMETRY_SELECTIONS);

        double[] allTimes = dataServer.getDataSetTimes(TestEndpointUtils.administratorUser, simulationDataIdentifier);
        TimeSpecs timeSpecs = new TimeSpecs(startTimeIndex, endTimeIndex, allTimes, ExportEnums.TimeMode.TIME_RANGE);
        HashMap<Integer, String> dummyMaskInfo = new HashMap<>(){{put(0, "Dummy"); put(1, "Test");}};

        ExportResource.StandardExportInfo request = new ExportResource.StandardExportInfo(new ArrayList<>(){},"", "TestSim", simulationID, 0,
                geometrySpecs, timeSpecs, variableSpecs);
        return new ExportResource.N5ExportRequest(request, dummyMaskInfo, ExportEnums.ExportableDataType.PDE_VARIABLE_DATA, "TestDataset");
    }

    private ExportSpecs getValidExportSpec(int startTimeIndex, int endTimeIndex) throws Exception {
        ExportResource.N5ExportRequest request = getValidExportRequest(startTimeIndex, endTimeIndex);
        VCSimulationIdentifier vcSimulationIdentifier = new VCSimulationIdentifier(new KeyValue(simulationID), TestEndpointUtils.administratorUser);
        VCSimulationDataIdentifier simulationDataIdentifier = new VCSimulationDataIdentifier(vcSimulationIdentifier, 0);
        ExportResource.GeometrySpecDTO geometrySpecs = request.standardExportInformation().geometrySpecs();
        N5Specs n5Specs = new N5Specs(request.exportableDataType(), ExportFormat.N5, request.datasetName(), dummyMaskInfo);

        return new ExportSpecs(simulationDataIdentifier, ExportFormat.N5, request.standardExportInformation().variableSpecs(),
                request.standardExportInformation().timeSpecs(), new GeometrySpecs(geometrySpecs.selections(), geometrySpecs.axis(), geometrySpecs.sliceNumber(), geometrySpecs.geometryMode()),
                n5Specs, request.standardExportInformation().simulationName(),
                request.standardExportInformation().contextName(), new HumanReadableExportData(
                        request.standardExportInformation().simulationName(), "fds",
                        "tretre", new ArrayList<>(), "fsdfds",
                        "fdsfsd", false, request.subVolume()));
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
        exportSpecs.setExportMetaData(new HumanReadableExportData("", "", "", new ArrayList<>(), "", "", false, dummyMaskInfo));
        ExportResource.GeometrySpecDTO geometrySpecDTO = new ExportResource.GeometrySpecDTO(exportSpecs.getGeometrySpecs().getSelections(),
                exportSpecs.getGeometrySpecs().getAxis(), exportSpecs.getGeometrySpecs().getSliceNumber(), exportSpecs.getGeometrySpecs().getMode());

        VCSimulationDataIdentifier vcSimulationDataIdentifier = (VCSimulationDataIdentifier) exportSpecs.getVCDataIdentifier();
        ExportRequestListenerMQ.ExportJob exportJob = new ExportRequestListenerMQ.ExportJob(jobID, TestEndpointUtils.administratorUser,
                new AnnotatedFunction[]{}, vcSimulationDataIdentifier.getVcSimID(), vcSimulationDataIdentifier.getJobIndex(), exportSpecs.getFormat(), exportSpecs.getVariableSpecs(), exportSpecs.getTimeSpecs(),
                geometrySpecDTO, exportSpecs.getFormatSpecificSpecs(), exportSpecs.getSimulationName(), exportSpecs.getContextName());
        statusCreator.addServerExportListener(TestEndpointUtils.administratorUser, exportJob.id());

        return statusCreator.getUsersExportStatus(TestEndpointUtils.administratorUser, exportJob.id());
    }

}

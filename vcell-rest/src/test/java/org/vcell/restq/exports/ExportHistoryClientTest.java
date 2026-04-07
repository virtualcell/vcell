package org.vcell.restq.exports;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.DataServerImpl;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@QuarkusTest
public class ExportHistoryClientTest {
    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;
    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    private static final Logger lg = LogManager.getLogger(ExportHistoryClientTest.class);

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private DataServerImpl dataServer;
    private final String simulationID = "597714292";
    private BioModel frapModel;
    private ApiClient aliceAPIClient;

    @BeforeAll
    public static void setupConfig(){
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void setUp() throws IOException, DataAccessException, ApiException, SQLException, PropertyVetoException, XmlParseException {
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);

        dataServer = TestEndpointUtils.createDataServerImpl();
        frapModel = TestEndpointUtils.insertFrapModel(agroalConnectionFactory);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.clearAllBioModelEntries(agroalConnectionFactory);
        TestEndpointUtils.restoreSystemProperties();
    }

//     Tests the clients capability to submit exports, our queue for accepting and distributing messages, retrieval of export status, and the export job itself
    @Test
    public void testExportRequestClient() throws Exception {
        ExportResourceApi exportResourceApi = new ExportResourceApi(aliceAPIClient);
        N5ExportRequest exportRequest = ExportHelper.getValidExportRequestDTO(0, 1, dataServer,
                frapModel.getSimulation(0), frapModel);
        ExportEvent completedExport = ExportHelper.exportAndWait(exportResourceApi, exportRequest);
        List<ExportHistory> exportHistories = exportResourceApi.getExportHistory();
        Assertions.assertFalse(exportHistories.isEmpty());
        Assertions.assertEquals(exportHistories.size(), 1);
        Assertions.assertEquals(exportHistories.get(0).getExportJobID(), completedExport.getJobID());
        Assertions.assertEquals(exportHistories.get(0).getSimName(), frapModel.getSimulation(0).getName());
        Assertions.assertEquals(exportHistories.get(0).getModelName(), frapModel.getName());
        Assertions.assertTrue(requestEqualHistory(exportRequest, exportHistories.get(0)));
    }

    @Test
    public void testExportHistorySequence() throws Exception {
        ExportResourceApi exportResourceApi = new ExportResourceApi(aliceAPIClient);
        N5ExportRequest exportRequest = ExportHelper.getValidExportRequestDTO(0, 1, dataServer,
                frapModel.getSimulation(0), frapModel);
        N5ExportRequest exportRequest2 = ExportHelper.getValidExportRequestDTO(2, 4, dataServer,
                frapModel.getSimulation(0), frapModel);
        ExportEvent completedExport1 = ExportHelper.exportAndWait(exportResourceApi, exportRequest);
        ExportEvent completedExport2 = ExportHelper.exportAndWait(exportResourceApi, exportRequest2);
        List<ExportHistory> exportHistories = exportResourceApi.getExportHistory();

        Assertions.assertFalse(exportHistories.isEmpty());
        Assertions.assertEquals(exportHistories.size(), 2);
        Assertions.assertTrue(requestEqualHistory(exportRequest, exportHistories.get(1)));
        Assertions.assertTrue(requestEqualHistory(exportRequest2, exportHistories.get(0)));

        Assertions.assertTrue(exportHistories.get(0).getExportDate().isAfter(exportHistories.get(1).getExportDate()));
    }

    private boolean requestEqualHistory(N5ExportRequest request, ExportHistory history){
        List<Double> allTimes = request.getStandardExportInformation().getTimeSpecs().getAllTimes();
        boolean modelEqual = request.getStandardExportInformation().getBioModelKey().equals(history.getBioModelRef());
        boolean simEqual = request.getStandardExportInformation().getSimulationKey().equals(history.getSimulationRef());
        boolean startTimeEqual = allTimes.get(request.getStandardExportInformation().getTimeSpecs().getBeginTimeIndex()).equals(history.getStartTimeValue());
        boolean endTimeEqual = allTimes.get(request.getStandardExportInformation().getTimeSpecs().getEndTimeIndex()).equals(history.getEndTimeValue());
        boolean exportComplete = history.getEventStatus().equals(ExportProgressType.COMPLETE);
        boolean correctFormat = history.getExportFormat().equals(ExportFormat.N5);
        boolean variablesEqual = List.of(request.getStandardExportInformation().getVariableSpecs().getVariableNames()).equals(List.of(history.getVariables()));
        return modelEqual && simEqual && startTimeEqual && endTimeEqual && exportComplete && correctFormat && variablesEqual;
    }
}

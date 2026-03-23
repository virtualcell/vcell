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
import org.vcell.restq.handlers.AdminResource;
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

import static org.vcell.restq.exports.ExportHelper.getValidExportRequestDTO;

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
        dataServer = TestEndpointUtils.createDataServerImpl();
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
        ExportResourceApi exportResourceApi = new ExportResourceApi(aliceAPIClient);
        N5ExportRequest exportRequest = getValidExportRequestDTO(0, 1, dataServer, simulationID);
        ExportHelper.exportAndWait(exportResourceApi, exportRequest);
    }

}

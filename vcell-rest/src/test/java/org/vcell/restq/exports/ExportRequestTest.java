package org.vcell.restq.exports;

import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.simdata.*;
import cbit.vcell.xml.XmlParseException;
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

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.SQLException;

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
    public void setUp() throws IOException, DataAccessException, ApiException, SQLException, PropertyVetoException, XmlParseException {
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);
        dataServer = TestEndpointUtils.createDataServerImpl();
        TestEndpointUtils.insertFrapModel(agroalConnectionFactory);
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

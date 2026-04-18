package org.vcell.restq;

import cbit.vcell.resource.PropertyLoader;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.api.OptimizationResourceApi;
import org.vcell.restclient.model.OptJobStatus;
import org.vcell.restclient.model.OptimizationJobStatus;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.testresources.ArtemisTestResource;
import org.vcell.restq.testresources.OpenWireOptSubmitStub;
import org.vcell.util.DataAccessException;

import java.io.File;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cross-protocol integration test for the optimization messaging pipeline.
 *
 * Tests the full round-trip:
 *   REST submit → AMQP 1.0 publish (vcell-rest) → Artemis broker → OpenWire JMS consume (stub)
 *   → OpenWire JMS publish (stub) → Artemis broker → AMQP 1.0 consume (vcell-rest) → DB update
 *   → REST poll returns results
 *
 * The OpenWire stub mimics vcell-submit's OptimizationBatchServer using the same ActiveMQ
 * OpenWire protocol. This catches cross-protocol issues like ANYCAST/MULTICAST routing mismatches
 * and AMQP address mapping errors.
 */
@QuarkusTest
@Tag("Quarkus")
@QuarkusTestResource(ArtemisTestResource.class)
public class OptimizationCrossProtocolTest {
    private static final Logger lg = LogManager.getLogger(OptimizationCrossProtocolTest.class);

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @ConfigProperty(name = "vcell.optimization.parest-data-dir")
    String parestDataDir;

    @ConfigProperty(name = "artemis.openwire.host")
    String openwireHost;

    @ConfigProperty(name = "artemis.openwire.port")
    int openwirePort;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();
    OpenWireOptSubmitStub stub;

    @BeforeAll
    public static void setupConfig() {
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void startStub() throws Exception {
        stub = new OpenWireOptSubmitStub(openwireHost, openwirePort);
        stub.start();
    }

    @AfterEach
    public void cleanup() throws Exception {
        if (stub != null) {
            stub.stop();
        }
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
    }

    /**
     * Tests the full cross-protocol round-trip: submit via REST, messages flow through Artemis
     * between AMQP 1.0 (vcell-rest) and OpenWire JMS (stub), results returned via REST polling.
     */
    @Test
    public void testCrossProtocol_submitPollComplete() throws Exception {
        // Set up authenticated API client (same as desktop client)
        ApiClient apiClient = TestEndpointUtils.createAuthenticatedAPIClient(
                keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        TestEndpointUtils.mapApiClientToAdmin(apiClient);
        OptimizationResourceApi optApi = new OptimizationResourceApi(apiClient);

        // Submit optimization via REST (triggers AMQP 1.0 publish to opt-request)
        org.vcell.restclient.model.OptProblem optProblem = createClientOptProblem();
        OptimizationJobStatus submitResult = optApi.submitOptimization(optProblem);
        assertNotNull(submitResult.getId());
        assertEquals(OptJobStatus.SUBMITTED, submitResult.getStatus());
        lg.info("Submitted optimization job: {}", submitResult.getId());

        // Poll for completion — the OpenWire stub processes the request through Artemis
        // and sends status back, which vcell-rest consumes via AMQP 1.0
        long jobId = Long.parseLong(submitResult.getId());
        OptimizationJobStatus finalStatus = null;
        long startTime = System.currentTimeMillis();
        long timeoutMs = 30_000;

        while ((System.currentTimeMillis() - startTime) < timeoutMs) {
            OptimizationJobStatus status = optApi.getOptimizationStatus(jobId);
            lg.info("Poll: job={}, status={}", jobId, status.getStatus());

            if (status.getStatus() == OptJobStatus.COMPLETE) {
                finalStatus = status;
                break;
            }
            if (status.getStatus() == OptJobStatus.FAILED) {
                fail("Optimization job failed: " + status.getStatusMessage());
            }

            Thread.sleep(500);
        }

        // Verify the full round-trip produced correct results
        assertNotNull(finalStatus, "Should have received COMPLETE status before timeout");
        assertEquals(OptJobStatus.COMPLETE, finalStatus.getStatus());
        assertNotNull(finalStatus.getResults(), "COMPLETE status should include results");
        assertNotNull(finalStatus.getResults().getOptResultSet(), "Results should have optResultSet");
        assertEquals(0.001, finalStatus.getResults().getOptResultSet().getObjectiveFunction(), 0.0001);

        var params = finalStatus.getResults().getOptResultSet().getOptParameterValues();
        assertNotNull(params);
        assertEquals(1.5, params.get("k1"), 0.001);
        assertEquals(2.5, params.get("k2"), 0.001);
    }

    private org.vcell.restclient.model.OptProblem createClientOptProblem() {
        var optProblem = new org.vcell.restclient.model.OptProblem();
        optProblem.setMathModelSbmlContents("<sbml>test</sbml>");
        optProblem.setNumberOfOptimizationRuns(1);

        var p1 = new org.vcell.restclient.model.ParameterDescription();
        p1.setName("k1");
        p1.setMinValue(0.0);
        p1.setMaxValue(10.0);
        p1.setInitialValue(1.0);
        p1.setScale(1.0);

        var p2 = new org.vcell.restclient.model.ParameterDescription();
        p2.setName("k2");
        p2.setMinValue(0.0);
        p2.setMaxValue(10.0);
        p2.setInitialValue(2.0);
        p2.setScale(1.0);

        optProblem.setParameterDescriptionList(java.util.List.of(p1, p2));
        optProblem.setDataSet(java.util.List.of(
                java.util.List.of(0.0, 1.0, 2.0),
                java.util.List.of(1.0, 1.5, 2.5)
        ));

        var rv1 = new org.vcell.restclient.model.ReferenceVariable();
        rv1.setVarName("t");
        rv1.setReferenceVariableType(org.vcell.restclient.model.ReferenceVariableReferenceVariableType.INDEPENDENT);
        var rv2 = new org.vcell.restclient.model.ReferenceVariable();
        rv2.setVarName("x");
        rv2.setReferenceVariableType(org.vcell.restclient.model.ReferenceVariableReferenceVariableType.DEPENDENT);

        optProblem.setReferenceVariable(java.util.List.of(rv1, rv2));

        var method = new org.vcell.restclient.model.CopasiOptimizationMethod();
        method.setOptimizationMethodType(org.vcell.restclient.model.CopasiOptimizationMethodOptimizationMethodType.EVOLUTIONARYPROGRAM);
        method.setOptimizationParameter(java.util.List.of());
        optProblem.setCopasiOptimizationMethod(method);

        return optProblem;
    }
}

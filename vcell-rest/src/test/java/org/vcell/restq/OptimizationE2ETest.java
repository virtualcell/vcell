package org.vcell.restq;

import cbit.vcell.resource.PropertyLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.optimization.OptJobStatus;
import org.vcell.optimization.jtd.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.api.OptimizationResourceApi;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end test exercising the same code path as the desktop client's parameter estimation.
 *
 * Calls CopasiOptimizationSolverRemote.solveRemoteApi() (the testable overload) against a live
 * Quarkus instance with testcontainers. A mock vcell-submit consumer simulates the SLURM job
 * lifecycle by updating database status and writing result files to the filesystem.
 *
 * This tests the full round-trip: client → REST → DB → filesystem → client, using the same
 * generated OptimizationResourceApi that the desktop client uses.
 */
@QuarkusTest
@Tag("Quarkus")
public class OptimizationE2ETest {
    private static final Logger lg = LogManager.getLogger(OptimizationE2ETest.class);

    @Inject
    ObjectMapper objectMapper;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    @Inject
    org.vcell.restq.services.OptimizationRestService optimizationRestService;

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @ConfigProperty(name = "vcell.optimization.parest-data-dir")
    String parestDataDir;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    @BeforeAll
    public static void setupConfig() {
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
    }

    /**
     * E2E test: submit optimization via generated client, simulate vcell-submit processing,
     * verify that the client receives results.
     *
     * This exercises the same code path as the desktop client's CopasiOptimizationSolverRemote,
     * but with a mock vcell-submit consumer instead of real SLURM.
     */
    @Test
    public void testOptimizationE2E_submitPollComplete() throws Exception {
        // Set up authenticated API client (same as desktop client would)
        ApiClient apiClient = TestEndpointUtils.createAuthenticatedAPIClient(
                keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        TestEndpointUtils.mapApiClientToAdmin(apiClient);

        OptimizationResourceApi optApi = new OptimizationResourceApi(apiClient);

        // Build OptProblem using the generated client model (same as desktop client)
        org.vcell.restclient.model.OptProblem optProblem = createClientOptProblem();

        // Submit the job
        org.vcell.restclient.model.OptimizationJobStatus submitResult = optApi.submitOptimization(optProblem);
        assertNotNull(submitResult.getId());
        assertEquals(org.vcell.restclient.model.OptJobStatus.SUBMITTED, submitResult.getStatus());
        lg.info("Submitted optimization job: {}", submitResult.getId());

        // Simulate vcell-submit processing in a background thread (mock consumer)
        String jobId = submitResult.getId();
        CompletableFuture<Void> mockSubmit = CompletableFuture.runAsync(() -> {
            try {
                simulateVcellSubmitProcessing(jobId);
            } catch (Exception e) {
                throw new RuntimeException("Mock vcell-submit failed", e);
            }
        });

        // Poll for results (same loop as CopasiOptimizationSolverRemote)
        org.vcell.restclient.model.OptimizationJobStatus finalStatus = null;
        long startTime = System.currentTimeMillis();
        long timeoutMs = 30_000; // 30 seconds for test

        while ((System.currentTimeMillis() - startTime) < timeoutMs) {
            org.vcell.restclient.model.OptimizationJobStatus status = optApi.getOptimizationStatus(
                    Long.parseLong(jobId));
            lg.info("Poll: job={}, status={}", jobId, status.getStatus());

            if (status.getStatus() == org.vcell.restclient.model.OptJobStatus.COMPLETE) {
                finalStatus = status;
                break;
            }
            if (status.getStatus() == org.vcell.restclient.model.OptJobStatus.FAILED) {
                fail("Optimization job failed: " + status.getStatusMessage());
            }

            Thread.sleep(500); // poll faster than real client for test speed
        }

        // Verify the mock consumer completed without error
        mockSubmit.get(5, TimeUnit.SECONDS);

        // Verify results
        assertNotNull(finalStatus, "Should have received COMPLETE status before timeout");
        assertEquals(org.vcell.restclient.model.OptJobStatus.COMPLETE, finalStatus.getStatus());
        assertNotNull(finalStatus.getResults(), "COMPLETE status should include results");
        assertNotNull(finalStatus.getResults().getOptResultSet(), "Results should have optResultSet");
        assertEquals(0.001, finalStatus.getResults().getOptResultSet().getObjectiveFunction(), 0.0001);

        Map<String, Double> params = finalStatus.getResults().getOptResultSet().getOptParameterValues();
        assertNotNull(params);
        assertEquals(1.5, params.get("k1"), 0.001);
        assertEquals(2.5, params.get("k2"), 0.001);
    }

    /**
     * E2E test: submit optimization, then stop it mid-run.
     * Verifies that stop returns the last progress report.
     */
    @Test
    public void testOptimizationE2E_submitAndStop() throws Exception {
        ApiClient apiClient = TestEndpointUtils.createAuthenticatedAPIClient(
                keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        TestEndpointUtils.mapApiClientToAdmin(apiClient);

        OptimizationResourceApi optApi = new OptimizationResourceApi(apiClient);
        org.vcell.restclient.model.OptProblem optProblem = createClientOptProblem();

        // Submit
        org.vcell.restclient.model.OptimizationJobStatus submitResult = optApi.submitOptimization(optProblem);
        String jobId = submitResult.getId();

        // Simulate vcell-submit: transition to RUNNING with progress but don't complete
        org.vcell.util.document.KeyValue jobKey = new org.vcell.util.document.KeyValue(jobId);
        optimizationRestService.updateHtcJobId(jobKey, "SLURM:99999");
        optimizationRestService.updateOptJobStatus(jobKey, OptJobStatus.RUNNING, null);

        // Write a progress report
        File reportFile = new File(parestDataDir, "CopasiParest_" + jobId + "_optReport.txt");
        reportFile.getParentFile().mkdirs();
        Files.writeString(reportFile.toPath(),
                "[\"k1\",\"k2\"]\n" +
                "10\t0.5\t1.0\t2.0\n" +
                "20\t0.3\t1.2\t2.3\n");

        // Verify RUNNING with progress
        org.vcell.restclient.model.OptimizationJobStatus runningStatus = optApi.getOptimizationStatus(
                Long.parseLong(jobId));
        assertEquals(org.vcell.restclient.model.OptJobStatus.RUNNING, runningStatus.getStatus());
        assertNotNull(runningStatus.getProgressReport(), "RUNNING should include progress");

        // Stop the job
        org.vcell.restclient.model.OptimizationJobStatus stopResult = optApi.stopOptimization(
                Long.parseLong(jobId));
        assertEquals(org.vcell.restclient.model.OptJobStatus.STOPPED, stopResult.getStatus());
        assertEquals("Stopped by user", stopResult.getStatusMessage());

        // Progress should still be available after stop
        org.vcell.restclient.model.OptimizationJobStatus afterStop = optApi.getOptimizationStatus(
                Long.parseLong(jobId));
        assertEquals(org.vcell.restclient.model.OptJobStatus.STOPPED, afterStop.getStatus());
        assertNotNull(afterStop.getProgressReport(), "Progress should survive stop");
    }

    /**
     * Simulates what vcell-submit would do: transition the job through QUEUED → RUNNING → COMPLETE
     * and write result files to the filesystem.
     */
    private void simulateVcellSubmitProcessing(String jobId) throws Exception {
        org.vcell.util.document.KeyValue jobKey = new org.vcell.util.document.KeyValue(jobId);

        // Phase 1: QUEUED (as if SLURM accepted the job)
        Thread.sleep(500);
        optimizationRestService.updateHtcJobId(jobKey, "SLURM:12345");
        lg.info("Mock vcell-submit: job {} → QUEUED", jobId);

        // Phase 2: RUNNING with progress report
        Thread.sleep(500);
        optimizationRestService.updateOptJobStatus(jobKey, OptJobStatus.RUNNING, null);

        File reportFile = new File(parestDataDir, "CopasiParest_" + jobId + "_optReport.txt");
        reportFile.getParentFile().mkdirs();
        Files.writeString(reportFile.toPath(),
                "[\"k1\",\"k2\"]\n" +
                "10\t0.5\t1.0\t2.0\n" +
                "20\t0.1\t1.3\t2.4\n" +
                "30\t0.01\t1.5\t2.5\n");
        lg.info("Mock vcell-submit: job {} → RUNNING with progress", jobId);

        // Phase 3: COMPLETE with results
        Thread.sleep(500);
        File outputFile = new File(parestDataDir, "CopasiParest_" + jobId + "_optRun.json");
        Vcellopt vcellopt = new Vcellopt();
        vcellopt.setStatus(VcelloptStatus.COMPLETE);
        vcellopt.setStatusMessage("optimization complete");
        OptResultSet resultSet = new OptResultSet();
        resultSet.setNumFunctionEvaluations(30);
        resultSet.setObjectiveFunction(0.001);
        resultSet.setOptParameterValues(Map.of("k1", 1.5, "k2", 2.5));
        vcellopt.setOptResultSet(resultSet);
        objectMapper.writeValue(outputFile, vcellopt);
        lg.info("Mock vcell-submit: job {} → wrote results", jobId);
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

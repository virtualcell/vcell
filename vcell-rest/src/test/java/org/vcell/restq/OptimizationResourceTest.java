package org.vcell.restq;

import cbit.vcell.resource.PropertyLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.junit.jupiter.api.*;
import org.vcell.optimization.CopasiUtils;
import org.vcell.optimization.OptJobStatus;
import org.vcell.optimization.jtd.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.models.OptimizationJobStatus;
import org.vcell.restq.services.OptimizationRestService;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@Tag("Quarkus")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OptimizationResourceTest {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    @Inject
    OptimizationRestService optimizationRestService;

    @Inject
    @org.eclipse.microprofile.config.inject.ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private ApiClient aliceAPIClient;

    @BeforeAll
    public static void setupConfig() {
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void createClients() {
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
    }

    @Test
    @Order(1)
    public void testSubmitAndGetStatus() throws Exception {
        // Map alice to admin user
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);
        String accessToken = keycloakClient.getAccessToken("alice");

        // Create a minimal OptProblem
        OptProblem optProblem = createTestOptProblem();

        // Submit via REST
        String responseJson = given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(optProblem))
                .when()
                .post("/api/v1/optimization")
                .then()
                .statusCode(200)
                .extract().body().asString();

        OptimizationJobStatus submitStatus = objectMapper.readValue(responseJson, OptimizationJobStatus.class);
        assertNotNull(submitStatus.id());
        assertEquals(OptJobStatus.SUBMITTED, submitStatus.status());
        assertNull(submitStatus.progressReport());
        assertNull(submitStatus.results());

        // Query status
        String statusJson = given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/api/v1/optimization/" + submitStatus.id())
                .then()
                .statusCode(200)
                .extract().body().asString();

        OptimizationJobStatus getStatus = objectMapper.readValue(statusJson, OptimizationJobStatus.class);
        assertEquals(submitStatus.id(), getStatus.id());
        assertEquals(OptJobStatus.SUBMITTED, getStatus.status());
    }

    @Test
    @Order(2)
    public void testListJobs() throws Exception {
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);
        String accessToken = keycloakClient.getAccessToken("alice");
        User adminUser = TestEndpointUtils.administratorUser;

        File tempDir = Files.createTempDirectory("parest_test").toFile();
        try {
            // Submit two jobs directly via service
            OptProblem optProblem = createTestOptProblem();
            OptimizationJobStatus job1 = optimizationRestService.submitOptimization(optProblem, tempDir, adminUser);
            OptimizationJobStatus job2 = optimizationRestService.submitOptimization(optProblem, tempDir, adminUser);

            // List via REST
            String listJson = given()
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .get("/api/v1/optimization")
                    .then()
                    .statusCode(200)
                    .extract().body().asString();

            OptimizationJobStatus[] jobs = objectMapper.readValue(listJson, OptimizationJobStatus[].class);
            assertTrue(jobs.length >= 2, "Expected at least 2 jobs, got " + jobs.length);

            // Most recent first
            for (OptimizationJobStatus job : jobs) {
                assertNull(job.progressReport(), "List should not include progressReport");
                assertNull(job.results(), "List should not include results");
            }
        } finally {
            deleteRecursive(tempDir);
        }
    }

    @Test
    @Order(3)
    public void testStatusWithProgressReport() throws Exception {
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);
        String accessToken = keycloakClient.getAccessToken("alice");
        User adminUser = TestEndpointUtils.administratorUser;

        File tempDir = Files.createTempDirectory("parest_test").toFile();
        try {
            OptProblem optProblem = createTestOptProblem();
            OptimizationJobStatus job = optimizationRestService.submitOptimization(optProblem, tempDir, adminUser);

            // Simulate vcell-submit reporting RUNNING
            optimizationRestService.updateHtcJobId(job.id(), "SLURM:12345");
            optimizationRestService.updateOptJobStatus(job.id(), OptJobStatus.RUNNING, null);

            // Write a mock progress report file
            File reportFile = new File(tempDir, "CopasiParest_" + job.id() + "_optReport.txt");
            // Header: JSON array of param names, then TSV rows: evaluations \t objective \t param values
            Files.writeString(reportFile.toPath(),
                    "[\"k1\",\"k2\"]\n" +
                    "10\t0.5\t1.0\t2.0\n" +
                    "20\t0.1\t1.1\t2.1\n");

            // Query status
            String statusJson = given()
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .get("/api/v1/optimization/" + job.id())
                    .then()
                    .statusCode(200)
                    .extract().body().asString();

            OptimizationJobStatus status = objectMapper.readValue(statusJson, OptimizationJobStatus.class);
            assertEquals(OptJobStatus.RUNNING, status.status());
            assertEquals("SLURM:12345", status.htcJobId());
            assertNotNull(status.progressReport(), "RUNNING status should include progress report");
            assertNull(status.results(), "RUNNING status should not include results");
        } finally {
            deleteRecursive(tempDir);
        }
    }

    @Test
    @Order(4)
    public void testStatusWithResults() throws Exception {
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);
        String accessToken = keycloakClient.getAccessToken("alice");
        User adminUser = TestEndpointUtils.administratorUser;

        File tempDir = Files.createTempDirectory("parest_test").toFile();
        try {
            OptProblem optProblem = createTestOptProblem();
            OptimizationJobStatus job = optimizationRestService.submitOptimization(optProblem, tempDir, adminUser);
            optimizationRestService.updateOptJobStatus(job.id(), OptJobStatus.RUNNING, null);

            // Write a mock output file (simulates solver completion)
            File outputFile = new File(tempDir, "CopasiParest_" + job.id() + "_optRun.json");
            Vcellopt vcellopt = new Vcellopt();
            vcellopt.setOptProblem(optProblem);
            vcellopt.setStatus(VcelloptStatus.COMPLETE);
            vcellopt.setStatusMessage("optimization complete");
            OptResultSet resultSet = new OptResultSet();
            resultSet.setNumFunctionEvaluations(50);
            resultSet.setObjectiveFunction(0.001);
            resultSet.setOptParameterValues(Map.of("k1", 1.5, "k2", 2.5));
            vcellopt.setOptResultSet(resultSet);
            objectMapper.writeValue(outputFile, vcellopt);

            // Query status — should auto-transition to COMPLETE
            String statusJson = given()
                    .header("Authorization", "Bearer " + accessToken)
                    .when()
                    .get("/api/v1/optimization/" + job.id())
                    .then()
                    .statusCode(200)
                    .extract().body().asString();

            OptimizationJobStatus status = objectMapper.readValue(statusJson, OptimizationJobStatus.class);
            assertEquals(OptJobStatus.COMPLETE, status.status());
            assertNotNull(status.results(), "COMPLETE status should include results");
            assertEquals(VcelloptStatus.COMPLETE, status.results().getStatus());
        } finally {
            deleteRecursive(tempDir);
        }
    }

    @Test
    @Order(5)
    public void testStopJob() throws Exception {
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);
        String accessToken = keycloakClient.getAccessToken("alice");
        User adminUser = TestEndpointUtils.administratorUser;

        File tempDir = Files.createTempDirectory("parest_test").toFile();
        try {
            OptProblem optProblem = createTestOptProblem();
            OptimizationJobStatus job = optimizationRestService.submitOptimization(optProblem, tempDir, adminUser);
            optimizationRestService.updateHtcJobId(job.id(), "SLURM:99999");
            optimizationRestService.updateOptJobStatus(job.id(), OptJobStatus.RUNNING, null);

            // Stop via REST
            String stopJson = given()
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType("application/json")
                    .when()
                    .post("/api/v1/optimization/" + job.id() + "/stop")
                    .then()
                    .statusCode(200)
                    .extract().body().asString();

            OptimizationJobStatus stopStatus = objectMapper.readValue(stopJson, OptimizationJobStatus.class);
            assertEquals(OptJobStatus.STOPPED, stopStatus.status());
            assertEquals("Stopped by user", stopStatus.statusMessage());
        } finally {
            deleteRecursive(tempDir);
        }
    }

    @Test
    @Order(6)
    public void testUnauthorizedAccess() throws Exception {
        // Submit as alice/admin
        TestEndpointUtils.mapApiClientToAdmin(aliceAPIClient);
        User adminUser = TestEndpointUtils.administratorUser;

        File tempDir = Files.createTempDirectory("parest_test").toFile();
        try {
            OptProblem optProblem = createTestOptProblem();
            OptimizationJobStatus job = optimizationRestService.submitOptimization(optProblem, tempDir, adminUser);

            // Try to access as bob (different user)
            ApiClient bobAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.bob);
            TestEndpointUtils.mapApiClientToNagios(bobAPIClient);
            String bobToken = keycloakClient.getAccessToken("bob");

            given()
                    .header("Authorization", "Bearer " + bobToken)
                    .when()
                    .get("/api/v1/optimization/" + job.id())
                    .then()
                    .statusCode(500); // DataAccessException mapped to 500
        } finally {
            deleteRecursive(tempDir);
        }
    }

    @Test
    @Order(7)
    public void testUnauthenticatedAccess() {
        // No auth token — should get 401
        given()
                .when()
                .get("/api/v1/optimization")
                .then()
                .statusCode(401);
    }

    private OptProblem createTestOptProblem() {
        OptProblem optProblem = new OptProblem();
        optProblem.setMathModelSbmlContents("<sbml>test</sbml>");
        optProblem.setNumberOfOptimizationRuns(1);

        ParameterDescription p1 = new ParameterDescription();
        p1.setName("k1");
        p1.setMinValue(0.0);
        p1.setMaxValue(10.0);
        p1.setInitialValue(1.0);
        p1.setScale(1.0);

        ParameterDescription p2 = new ParameterDescription();
        p2.setName("k2");
        p2.setMinValue(0.0);
        p2.setMaxValue(10.0);
        p2.setInitialValue(2.0);
        p2.setScale(1.0);

        optProblem.setParameterDescriptionList(List.of(p1, p2));
        optProblem.setDataSet(List.of(
                List.of(0.0, 1.0, 2.0),
                List.of(1.0, 1.5, 2.5)
        ));

        ReferenceVariable rv1 = new ReferenceVariable();
        rv1.setVarName("t");
        rv1.setReferenceVariableType(ReferenceVariableReferenceVariableType.INDEPENDENT);
        ReferenceVariable rv2 = new ReferenceVariable();
        rv2.setVarName("x");
        rv2.setReferenceVariableType(ReferenceVariableReferenceVariableType.DEPENDENT);

        optProblem.setReferenceVariable(List.of(rv1, rv2));

        CopasiOptimizationMethod method = new CopasiOptimizationMethod();
        method.setOptimizationMethodType(CopasiOptimizationMethodOptimizationMethodType.EVOLUTIONARY_PROGRAM);
        method.setOptimizationParameter(List.of());
        optProblem.setCopasiOptimizationMethod(method);

        return optProblem;
    }

    private static void deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        file.delete();
    }
}

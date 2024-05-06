package org.vcell.restq.apiclient;

import cbit.vcell.resource.PropertyLoader;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.AdminResourceApi;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restclient.model.UsageSummary;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;

import java.sql.SQLException;

@QuarkusTest
public class AdminApiTest {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private ApiClient aliceAPIClient;
    private ApiClient bobAPIClient;

    @BeforeAll
    public static void setupConfig(){
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void createClients(){
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        bobAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.bob);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
    }

    @Test
    public void testUsageReport_AdminRole_AdminUser() throws ApiException {
        // alice has admin role, mapped to an admin user - should succeed
        boolean mapped = new UsersResourceApi(aliceAPIClient).mapUser(TestEndpointUtils.administratorUserLoginInfo);
        Assertions.assertTrue(mapped);
        UsageSummary usageSummary = new AdminResourceApi(aliceAPIClient).getUsage();
        Assertions.assertNotNull(usageSummary);

        Assertions.assertEquals(usageSummary.getBiomodelCount(), 0);
        Assertions.assertEquals(usageSummary.getTotalUsers(), 4);
    }

    @Test
    public void testUsageReport_AdminRole_NonadminUser() throws ApiException {
        // alice has an admin role, but is mapped to a non-admin user - should fail
        boolean mapped = new UsersResourceApi(aliceAPIClient).mapUser(TestEndpointUtils.vcellNagiosUserLoginInfo);
        Assertions.assertTrue(mapped);
        ApiException apiException = Assertions.assertThrows(ApiException.class, () -> {
            new AdminResourceApi(aliceAPIClient).getUsage();
        });
        Assertions.assertEquals(403, apiException.getCode());
    }

    @Test
    public void testUsageReport_AdminRole_NotMapped() throws ApiException {
        // alice has admin role, but is not mapped any user - should fail
        ApiException apiException = Assertions.assertThrows(ApiException.class, () -> {
            new AdminResourceApi(aliceAPIClient).getUsage();
        });
        Assertions.assertEquals(401, apiException.getCode());
    }

    @Test
    public void testUsageReport_NonadminRole_AdminUser() throws ApiException {
        // bob does not have admin role, mapped to admin user - should fail
        boolean mapped = new UsersResourceApi(bobAPIClient).mapUser(TestEndpointUtils.administratorUserLoginInfo);
        Assertions.assertTrue(mapped);
        ApiException apiException = Assertions.assertThrows(ApiException.class, () -> {
            new AdminResourceApi(aliceAPIClient).getUsage();
        });
        Assertions.assertEquals(401, apiException.getCode());
    }

    @Test
    public void testUsageReport_NonadminRole_NonadminUser() throws ApiException {
        // bob is not an admin, mapped to a non-admin user - should fail
        boolean mapped = new UsersResourceApi(bobAPIClient).mapUser(TestEndpointUtils.vcellNagiosUserLoginInfo);
        Assertions.assertTrue(mapped);
        ApiException apiException = Assertions.assertThrows(ApiException.class, () -> {
            new AdminResourceApi(aliceAPIClient).getUsage();
        });
        Assertions.assertEquals(401, apiException.getCode());
    }

    @Test
    public void testUsageReport_NonadminRole_NotMapped() throws ApiException {
        // bob is not an admin, and is not mapped to any user - should fail
        ApiException apiException = Assertions.assertThrows(ApiException.class, () -> {
            new AdminResourceApi(aliceAPIClient).getUsage();
        });
        Assertions.assertEquals(401, apiException.getCode());
    }
}

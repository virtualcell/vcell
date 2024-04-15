package org.vcell.restq.apiclient;

import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.UserIdentity;
import cbit.vcell.modeldb.UserIdentityTable;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restclient.model.AccesTokenRepresentationRecord;
import org.vcell.restclient.model.UserIdentityJSONSafe;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.db.OracleAgroalConnectionFactory;
import org.vcell.util.DataAccessException;

import java.sql.SQLException;

@QuarkusIntegrationTest
public class UsersApiTest {
    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    SecurityIdentity securityIdentity;


    @Inject
    OracleAgroalConnectionFactory oracleAgroalConnectionFactory;
    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private ApiClient aliceAPIClient;
    private ApiClient bobAPIClient;

    @BeforeEach
    public void createClients(){
        aliceAPIClient = TestEndpointUtils.createAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        bobAPIClient = TestEndpointUtils.createAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.bob);
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        AdminDBTopLevel adminDBTopLevel = new DatabaseServerImpl(oracleAgroalConnectionFactory, oracleAgroalConnectionFactory.getKeyFactory()).getAdminDBTopLevel();
        adminDBTopLevel.removeAllUsersIdentities(TestEndpointUtils.vcellNagiosUser, true);
        adminDBTopLevel.removeAllUsersIdentities(TestEndpointUtils.administratorUser, true);
    }

    @Test
    public void testMapUser() throws ApiException, SQLException, DataAccessException {
        AdminDBTopLevel adminDBTopLevel = new DatabaseServerImpl(oracleAgroalConnectionFactory, oracleAgroalConnectionFactory.getKeyFactory()).getAdminDBTopLevel();
        UsersResourceApi aliceUserResourceAPI = new UsersResourceApi(aliceAPIClient);
        Boolean mapped = TestEndpointUtils.mapClientToNagiosUser(aliceUserResourceAPI);
        assert (mapped.booleanValue());

        UserIdentityJSONSafe apiRetrievedIdentity = aliceUserResourceAPI.apiUsersGetIdentityGet();
        UserIdentity dbRetrievedIdentity = adminDBTopLevel.getUserIdentityFromSubjectAndIdentityProvider(TestEndpointUtils.TestOIDCUsers.alice.name(),
                UserIdentityTable.IdentityProvider.KEYCLOAK,true);
        assert (apiRetrievedIdentity != null);

        Assertions.assertEquals(apiRetrievedIdentity.getUserName(), TestEndpointUtils.vcellNagiosUser.getName());
        Assertions.assertEquals(apiRetrievedIdentity.getSubject(), TestEndpointUtils.TestOIDCUsers.alice.name());

        Assertions.assertEquals(apiRetrievedIdentity.getUserName(), dbRetrievedIdentity.user().getName());
        Assertions.assertEquals(apiRetrievedIdentity.getSubject(), dbRetrievedIdentity.subject());

        adminDBTopLevel.deleteUserIdentityFromIdentityProvider(TestEndpointUtils.vcellNagiosUser, UserIdentityTable.IdentityProvider.KEYCLOAK, true);
        apiRetrievedIdentity = aliceUserResourceAPI.apiUsersGetIdentityGet();
        assert (apiRetrievedIdentity.getSubject() == null);

//        assert (userIdentity.getUser());
//        adminDBTopLevel.deleteUserIdentityFromIdentityProvider(testUser, oidcName, UserIdentityTable.IdentityProvider.KEYCLOAK, true);
    }

    //    https://quarkus.io/guides/security-oidc-bearer-token-authentication#integration-testing-wiremock
//    https://quarkus.io/guides/security-testing
    @Test
    public void testOldAPITokenGeneration() throws ApiException {
        UsersResourceApi aliceUserApi = new UsersResourceApi(aliceAPIClient);
        UsersResourceApi bobUserApi = new UsersResourceApi(bobAPIClient);
        TestEndpointUtils.mapClientToNagiosUser(aliceUserApi);

        AccesTokenRepresentationRecord token = aliceUserApi.getLegacyApiToken(TestEndpointUtils.userNagiosID, "", "123");
        assert (token != null && !token.getToken().isEmpty());

        token = aliceUserApi.getLegacyApiToken(TestEndpointUtils.userAdminID, "", "123");
        assert (token.getToken() == null);


        // Bob requests
        token = bobUserApi.getLegacyApiToken(TestEndpointUtils.userNagiosID, "", "123");
        assert (token.getToken() == null);

        token = bobUserApi.getLegacyApiToken(TestEndpointUtils.userAdminID, "", "123");
        assert (token.getToken() == null);

        TestEndpointUtils.mapClientToAdminUser(bobUserApi);
        token = bobUserApi.getLegacyApiToken(TestEndpointUtils.userAdminID, "", "123");
        assert (token != null && !token.getToken().isEmpty());
    }
}

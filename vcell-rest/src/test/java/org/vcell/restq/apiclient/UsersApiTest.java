package org.vcell.restq.apiclient;

import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.UserIdentity;
import cbit.vcell.modeldb.UserIdentityTable;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restclient.model.AccesTokenRepresentationRecord;
import org.vcell.restclient.model.AccessTokenRepresentation;
import org.vcell.restclient.model.UserIdentityJSONSafe;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.db.OracleAgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.math.BigDecimal;
import java.sql.SQLException;

@QuarkusTest
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

    AdminDBTopLevel adminDBTopLevel;

    private ApiClient aliceAPIClient;
    private ApiClient bobAPIClient;

//    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
//        adminDBTopLevel.removeAllUsersIdentities(TestEndpointUtils.vcellNagiosUser, true);
//        adminDBTopLevel.removeAllUsersIdentities(TestEndpointUtils.administratorUser, true);
    }

    @Test
    public void testMapUser() throws ApiException, SQLException, DataAccessException {
        adminDBTopLevel = new DatabaseServerImpl(oracleAgroalConnectionFactory, oracleAgroalConnectionFactory.getKeyFactory()).getAdminDBTopLevel();
        ApiClient aliceClient = TestEndpointUtils.createAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        UsersResourceApi usersResourceApi = new UsersResourceApi(aliceClient);
        User testUser = new User("vcellNagios", new KeyValue(new BigDecimal(3)));
        String oidcName = "alice";

        Boolean mapped = TestEndpointUtils.mapClientToNagiosUser(usersResourceApi);
        assert (mapped.booleanValue());

        UserIdentityJSONSafe apiRetrievedIdentity = usersResourceApi.getVCellIdentity();
        UserIdentity dbRetrievedIdentity = adminDBTopLevel.getUserIdentityFromSubjectAndIdentityProvider(oidcName, UserIdentityTable.IdentityProvider.KEYCLOAK,true);
        assert (apiRetrievedIdentity != null);

        Assertions.assertEquals(apiRetrievedIdentity.getUserName(), "vcellNagios");
        Assertions.assertEquals(apiRetrievedIdentity.getSubject(), oidcName);

        Assertions.assertEquals(apiRetrievedIdentity.getUserName(), dbRetrievedIdentity.user().getName());
        Assertions.assertEquals(apiRetrievedIdentity.getSubject(), dbRetrievedIdentity.subject());

        adminDBTopLevel.deleteUserIdentityFromIdentityProvider(testUser, UserIdentityTable.IdentityProvider.KEYCLOAK, true);
        apiRetrievedIdentity = usersResourceApi.getVCellIdentity();
        assert (apiRetrievedIdentity.getSubject() == null);

//        assert (userIdentity.getUser());
//        adminDBTopLevel.deleteUserIdentityFromIdentityProvider(testUser, oidcName, UserIdentityTable.IdentityProvider.KEYCLOAK, true);
    }

//    https://quarkus.io/guides/security-oidc-bearer-token-authentication#integration-testing-wiremock
//    https://quarkus.io/guides/security-testing
    @Test
    public void testOldAPITokenGeneration() throws ApiException {
        ApiClient aliceClient = TestEndpointUtils.createAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        ApiClient bobClient = TestEndpointUtils.createAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.bob);
        UsersResourceApi aliceUserApi = new UsersResourceApi(aliceClient);
        UsersResourceApi bobUserApi = new UsersResourceApi(bobClient);
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

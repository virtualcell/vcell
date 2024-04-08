package org.vcell.restq.apiclient;

import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.UserIdentity;
import cbit.vcell.modeldb.UserIdentityTable;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import io.quarkus.test.security.TestSecurity;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restclient.model.IdentityProvider;
import org.vcell.restclient.model.MapUser;
import org.vcell.restclient.model.UserIdentityJSONSafe;
import org.vcell.restq.db.OracleAgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

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

    @Test
    @TestSecurity(user = "alice", roles = {"admin", "user"})
    public void testMapUser() throws ApiException, SQLException, DataAccessException {
        adminDBTopLevel = new DatabaseServerImpl(oracleAgroalConnectionFactory, oracleAgroalConnectionFactory.getKeyFactory()).getAdminDBTopLevel();
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setHost("localhost");
        defaultClient.setPort(testPort);

        UsersResourceApi usersResourceApi = new UsersResourceApi(defaultClient);
        MapUser userToMap = new MapUser();
        User testUser = new User("vcellNagios", new KeyValue("3"));
        userToMap.setUserID("vcellNagios");
        userToMap.setPassword("1700596370261");
        Boolean mapped = usersResourceApi.apiUsersMapUserPost(userToMap);
        assert (mapped.booleanValue());

        String oidcName = usersResourceApi.apiUsersMeGet().getPrincipalName();

        UserIdentityJSONSafe userIdentityJSONSafe = usersResourceApi.apiUsersGetIdentityGet();
        UserIdentity userIdentity = adminDBTopLevel.getUserIdentityFromSubjectAndIdentityProvider(oidcName, UserIdentityTable.IdentityProvider.KEYCLOAK,true);
        assert (userIdentityJSONSafe != null);
        assert (userIdentityJSONSafe.getSubject().equals(oidcName));
        assert (userIdentityJSONSafe.getUserName().equals("vcellNagios"));
        assert (userIdentityJSONSafe.getUserName().equals(userIdentity.user().getName()));
        assert (userIdentityJSONSafe.getSubject().equals(userIdentity.subject()));
//        assert (userIdentity.getUser());
//        adminDBTopLevel.deleteUserIdentityFromIdentityProvider(testUser, oidcName, UserIdentityTable.IdentityProvider.KEYCLOAK, true);
    }

//    https://quarkus.io/guides/security-oidc-bearer-token-authentication#integration-testing-wiremock
//    https://quarkus.io/guides/security-testing
    @Test
    public void testOIDCUser() throws ApiException {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setHost("localhost");
        defaultClient.setPort(testPort);

        UsersResourceApi usersResourceApi = new UsersResourceApi(defaultClient);
        usersResourceApi.apiUsersMeGet();
    }
}

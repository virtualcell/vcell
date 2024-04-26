package org.vcell.restq;

import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.model.UserLoginInfoForMapping;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.sql.SQLException;

public class TestEndpointUtils {

    public static final String userAdminID = "2";
    public static final String userNagiosID = "3";
    public static final User vcellNagiosUser = new User("vcellNagios", new KeyValue(userNagiosID));
    public static final User administratorUser = new User("Administrator", new KeyValue(userAdminID));

    public static final UserLoginInfoForMapping vcellNagiosUserLoginInfo = new UserLoginInfoForMapping().userID("vcellNagios").password("1700596370261");
    public static final UserLoginInfoForMapping administratorUserLoginInfo = new UserLoginInfoForMapping().userID("Administrator").password("1700596370260");

    public static void removeAllMappings(AgroalConnectionFactory agroalConnectionFactory) throws DataAccessException, SQLException {
        AdminDBTopLevel adminDBTopLevel = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory()).getAdminDBTopLevel();
        adminDBTopLevel.getUserIdentitiesFromUser(TestEndpointUtils.vcellNagiosUser, true).stream().forEach(userIdentity -> {
            try {
                adminDBTopLevel.deleteUserIdentity(userIdentity.user(), userIdentity.subject(), userIdentity.issuer(), true);
            } catch (SQLException | DataAccessException e) {
                throw new RuntimeException(e);
            }
        });
        adminDBTopLevel.getUserIdentitiesFromUser(TestEndpointUtils.administratorUser, true).stream().forEach(userIdentity -> {
            try {
                adminDBTopLevel.deleteUserIdentity(userIdentity.user(), userIdentity.subject(), userIdentity.issuer(), true);
            } catch (SQLException | DataAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public enum TestOIDCUsers{
        alice,
        bob;
    }

    public static ApiClient createAuthenticatedAPIClient(KeycloakTestClient keycloakClient, int testPort, TestOIDCUsers oidcUser){
        ApiClient apiClient = new ApiClient();
        String oidcAccessToken = keycloakClient.getAccessToken(oidcUser.name());
        apiClient.setRequestInterceptor(request -> request.header("Authorization", "Bearer " + oidcAccessToken));
        apiClient.setScheme("http");
        apiClient.setHost("localhost");
        apiClient.setPort(testPort);
        return apiClient;
    }
}

package org.vcell.restq;

import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.Configuration;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restclient.model.Publication;
import org.vcell.restclient.model.UserLoginInfoForMapping;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

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

    public static void mapApiClientToAdmin(ApiClient apiClient) throws ApiException {
        boolean mapped = new UsersResourceApi(apiClient).mapUser(administratorUserLoginInfo);
        if (!mapped) throw new ApiException("Mapping failed");
    }

    public static void mapApiClientToNagios(ApiClient apiClient) throws ApiException {
        boolean mapped = new UsersResourceApi(apiClient).mapUser(vcellNagiosUserLoginInfo);
        if (!mapped) throw new ApiException("Mapping failed");
    }

    public enum TestOIDCUsers{
        alice,
        bob;
    }

    public static ApiClient createAuthenticatedAPIClient(KeycloakTestClient keycloakClient, int testPort, TestOIDCUsers oidcUser){
        ApiClient apiClient = createUnAuthenticatedAPIClient(testPort);
        String oidcAccessToken = keycloakClient.getAccessToken(oidcUser.name());
        apiClient.setRequestInterceptor(request -> request.header("Authorization", "Bearer " + oidcAccessToken));
        return apiClient;
    }

    public static ApiClient createUnAuthenticatedAPIClient(int testPort){
        ApiClient apiClient = new ApiClient();
        apiClient.setScheme("http");
        apiClient.setHost("localhost");
        apiClient.setPort(testPort);
        return apiClient;
    }

    public static Publication defaultPublication(){
        Publication publication = new Publication();
        publication.setAuthors(Arrays.asList("author1", "author2"));
        publication.setCitation("citation");
        publication.setDoi("doi");
        publication.setEndnoteid(0);
        publication.setPubmedid("pubmedId");
        publication.setTitle("publication 1");
        publication.setUrl("url");
        publication.setWittid(0);
        publication.setYear(1994);
        publication.setBiomodelRefs(new ArrayList<>());
        publication.setMathmodelRefs(new ArrayList<>());
        publication.setDate(LocalDate.now());
        return publication;
    }
}

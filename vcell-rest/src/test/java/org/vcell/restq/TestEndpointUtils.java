package org.vcell.restq;

import io.quarkus.test.keycloak.client.KeycloakTestClient;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restclient.model.MapUser;
import org.vcell.restclient.model.UserLoginInfoForMapping;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

public class TestEndpointUtils {

    public static final String userAdminID = "2";
    public static final String userNagiosID = "3";
    public static final User vcellNagiosUser = new User("vcellNagios", new KeyValue(userNagiosID));
    public static final User administratorUser = new User("Administrator", new KeyValue(userAdminID));

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

//    public static ApiClient createBobClient(KeycloakTestClient keycloakClient, int testPort){
//        ApiClient aliceClient = Configuration.getDefaultApiClient();
//        String oidcAccessToken = keycloakClient.getAccessToken("bob");
//        aliceClient.setRequestInterceptor(request -> request.header("Authorization", "Bearer " + oidcAccessToken));
//        aliceClient.setHost("localhost");
//        aliceClient.setPort(testPort);
//        return aliceClient;
//    }

    public static boolean mapClientToNagiosUser(ApiClient authenticatedApiClient) throws ApiException {
        UsersResourceApi usersResourceApi = new UsersResourceApi(authenticatedApiClient);
        UserLoginInfoForMapping bioModelOwner = new UserLoginInfoForMapping();
        bioModelOwner.setUserID("vcellNagios");
        bioModelOwner.setPassword("1700596370261");
        return usersResourceApi.setVCellIdentity(bioModelOwner);
    }

    public static boolean mapClientToAdminUser(ApiClient authenticatedApiClient) throws ApiException {
        UsersResourceApi usersResourceApi = new UsersResourceApi(authenticatedApiClient);
        UserLoginInfoForMapping bioModelOwner = new UserLoginInfoForMapping();
        bioModelOwner.setUserID("Administrator");
        bioModelOwner.setPassword("1700596370260");
        return usersResourceApi.setVCellIdentity(bioModelOwner);
    }
}

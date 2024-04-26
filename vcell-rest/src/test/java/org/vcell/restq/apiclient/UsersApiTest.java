package org.vcell.restq.apiclient;

import cbit.vcell.resource.PropertyLoader;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.*;
import org.vcell.auth.JWTUtils;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restclient.model.AccesTokenRepresentationRecord;
import org.vcell.restclient.model.UserIdentityJSONSafe;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;

import java.sql.SQLException;

@QuarkusTest
public class UsersApiTest {
    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;
    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private ApiClient aliceAPIClient;
    private ApiClient bobAPIClient;

    @BeforeEach
    public void createClients() throws JoseException {
        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
        bobAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.bob);
        RsaJsonWebKey rsaJsonWebKey = JWTUtils.createNewJsonWebKey("k1");
        JWTUtils.setRsaJsonWebKey(rsaJsonWebKey);
    }
    @BeforeAll
    public static void setupConfig(){
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @AfterEach
    public void removeOIDCMappings() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
    }

    @Test
    public void testMapUser() throws ApiException, SQLException, DataAccessException, InvalidJwtException, MalformedClaimException {
        UsersResourceApi aliceUsersResourceApi = new UsersResourceApi(aliceAPIClient);

        // map once, true - map twice return false
        boolean mapped = aliceUsersResourceApi.setVCellIdentity(TestEndpointUtils.vcellNagiosUserLoginInfo);
        Assertions.assertTrue(mapped);
        mapped = aliceUsersResourceApi.setVCellIdentity(TestEndpointUtils.vcellNagiosUserLoginInfo);
        Assertions.assertTrue(mapped);

        // when already mapped, try to map with different user, return false (will reject mapping)
        mapped = aliceUsersResourceApi.setVCellIdentity(TestEndpointUtils.administratorUserLoginInfo);
        Assertions.assertFalse(mapped);

        // when already mapped, try to unmap with different user, return false (will reject mapping)
        // then unmap with mapped user - true first time, false second time
        boolean unmapped = aliceUsersResourceApi.clearVCellIdentity(TestEndpointUtils.administratorUserLoginInfo.getUserID());
        Assertions.assertFalse(unmapped);
        unmapped = aliceUsersResourceApi.clearVCellIdentity(TestEndpointUtils.vcellNagiosUserLoginInfo.getUserID());
        Assertions.assertTrue(unmapped);
        unmapped = aliceUsersResourceApi.clearVCellIdentity(TestEndpointUtils.vcellNagiosUserLoginInfo.getUserID());
        Assertions.assertFalse(unmapped);

        // not mapped, getIdentity() should throw exception ApiException with 404
        Assertions.assertThrows(ApiException.class, aliceUsersResourceApi::getVCellIdentity);

        // map again, true
        mapped = aliceUsersResourceApi.setVCellIdentity(TestEndpointUtils.vcellNagiosUserLoginInfo);
        Assertions.assertTrue(mapped);

        // getIdentity() should return the mapped user
        UserIdentityJSONSafe apiRetrievedIdentity = aliceUsersResourceApi.getVCellIdentity();
        Assertions.assertNotNull(apiRetrievedIdentity);

        // verify that getIdentity() return subject is the same as the subject in the token
        String oidcAccessToken = keycloakClient.getAccessToken(TestEndpointUtils.TestOIDCUsers.alice.name());
        JwtClaims claims = JWTUtils.getClaimsFromUntrustedToken(oidcAccessToken);
        Assertions.assertEquals(claims.getSubject(), apiRetrievedIdentity.getSubject());

        // cleanup, remove mapping
        aliceUsersResourceApi.clearVCellIdentity(TestEndpointUtils.vcellNagiosUserLoginInfo.getUserID());
        Assertions.assertThrows(ApiException.class, () -> aliceUsersResourceApi.getVCellIdentity());
    }

    //    https://quarkus.io/guides/security-oidc-bearer-token-authentication#integration-testing-wiremock
//    https://quarkus.io/guides/security-testing
    @Test
    public void testOldAPITokenGeneration() throws ApiException {

        UsersResourceApi usersResourceApi1 = new UsersResourceApi(aliceAPIClient);
        usersResourceApi1.setVCellIdentity(TestEndpointUtils.vcellNagiosUserLoginInfo);

        UsersResourceApi aliceUserApi = new UsersResourceApi(aliceAPIClient);
        UsersResourceApi bobUserApi = new UsersResourceApi(bobAPIClient);
        UsersResourceApi usersResourceApi = new UsersResourceApi(aliceAPIClient);
        usersResourceApi.setVCellIdentity(TestEndpointUtils.vcellNagiosUserLoginInfo);

        AccesTokenRepresentationRecord token = aliceUserApi.getLegacyApiToken();
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.getToken().isEmpty());
        Assertions.assertEquals ("vcellNagios", token.getUserId());

        // Bob requests
        token = bobUserApi.getLegacyApiToken();
        assert (token.getToken() == null);

        UsersResourceApi usersResourceApi2 = new UsersResourceApi(bobAPIClient);
        usersResourceApi2.setVCellIdentity(TestEndpointUtils.administratorUserLoginInfo);
        token = bobUserApi.getLegacyApiToken();
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.getToken().isEmpty());
        Assertions.assertEquals("Administrator", token.getUserId());
    }
}

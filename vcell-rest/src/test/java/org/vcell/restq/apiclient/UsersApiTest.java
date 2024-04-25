package org.vcell.restq.apiclient;

import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.UserIdentity;
import cbit.vcell.resource.PropertyLoader;
import io.quarkus.security.identity.SecurityIdentity;
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
import java.util.List;

@QuarkusTest
public class UsersApiTest {
    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    SecurityIdentity securityIdentity;


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
        AdminDBTopLevel adminDBTopLevel = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory()).getAdminDBTopLevel();
        boolean mapped = TestEndpointUtils.mapClientToNagiosUser(aliceAPIClient);
        assert (mapped);

        UsersResourceApi aliceUserResourceAPI = new UsersResourceApi(aliceAPIClient);
        UserIdentityJSONSafe apiRetrievedIdentity = aliceUserResourceAPI.getVCellIdentity();
        Assertions.assertNotNull(apiRetrievedIdentity);

        String oidcAccessToken = keycloakClient.getAccessToken(TestEndpointUtils.TestOIDCUsers.alice.name());
        JwtClaims claims = JWTUtils.getClaimsFromUntrustedToken(oidcAccessToken);
        List<UserIdentity> dbRetrievedIdentity = adminDBTopLevel.getUserIdentitiesFromSubjectAndIssuer(claims.getSubject(), claims.getIssuer(),true);

        Assertions.assertEquals(apiRetrievedIdentity.getUserName(), TestEndpointUtils.vcellNagiosUser.getName());

        Assertions.assertEquals(apiRetrievedIdentity.getUserName(), dbRetrievedIdentity.get(0).user().getName());
        Assertions.assertEquals(apiRetrievedIdentity.getSubject(), dbRetrievedIdentity.get(0).subject());
        Assertions.assertEquals(claims.getSubject(), dbRetrievedIdentity.get(0).subject());
        Assertions.assertEquals(claims.getIssuer(), dbRetrievedIdentity.get(0).issuer());

        adminDBTopLevel.deleteUserIdentity(TestEndpointUtils.vcellNagiosUser, claims.getSubject(), claims.getIssuer(), true);
        apiRetrievedIdentity = aliceUserResourceAPI.getVCellIdentity();
        assert (apiRetrievedIdentity.getSubject() == null);

//        assert (userIdentity.getUser());
//        adminDBTopLevel.deleteUserIdentityFromIdentityProvider(testUser, oidcName, UserIdentityTable.IdentityProvider.KEYCLOAK, true);
    }

    //    https://quarkus.io/guides/security-oidc-bearer-token-authentication#integration-testing-wiremock
//    https://quarkus.io/guides/security-testing
    @Test
    public void testOldAPITokenGeneration() throws ApiException {

        TestEndpointUtils.mapClientToNagiosUser(aliceAPIClient);

        UsersResourceApi aliceUserApi = new UsersResourceApi(aliceAPIClient);
        UsersResourceApi bobUserApi = new UsersResourceApi(bobAPIClient);
        TestEndpointUtils.mapClientToNagiosUser(aliceAPIClient);

        AccesTokenRepresentationRecord token = aliceUserApi.getLegacyApiToken();
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.getToken().isEmpty());
        Assertions.assertEquals ("vcellNagios", token.getUserId());

        // Bob requests
        token = bobUserApi.getLegacyApiToken();
        assert (token.getToken() == null);

        TestEndpointUtils.mapClientToAdminUser(bobAPIClient);
        token = bobUserApi.getLegacyApiToken();
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.getToken().isEmpty());
        Assertions.assertEquals("Administrator", token.getUserId());
    }
}

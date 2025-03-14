package org.vcell.restq.apiclient;

import cbit.vcell.resource.PropertyLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.eclipse.jetty.http.HttpStatus;
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
import org.vcell.restclient.api.PublicationResourceApi;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restclient.model.AccesTokenRepresentationRecord;
import org.vcell.restclient.model.UserIdentityJSONSafe;
import org.vcell.restclient.model.UserLoginInfoForMapping;
import org.vcell.restclient.model.UserRegistrationInfo;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;

import java.sql.SQLException;
import java.util.Random;

@QuarkusTest
public class UsersApiTest {
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
    public void testMapUser() throws ApiException, InvalidJwtException, MalformedClaimException, JsonProcessingException {
        UsersResourceApi aliceUsersResourceApi = new UsersResourceApi(aliceAPIClient);

        // map once, true - map twice return true also
        boolean mapped = aliceUsersResourceApi.mapUser(TestEndpointUtils.vcellNagiosUserLoginInfo);
        Assertions.assertTrue(mapped);
        mapped = aliceUsersResourceApi.mapUser(TestEndpointUtils.vcellNagiosUserLoginInfo);
        Assertions.assertTrue(mapped);

        // when already mapped, try to map with different user, return false (will reject mapping)
        mapped = aliceUsersResourceApi.mapUser(TestEndpointUtils.administratorUserLoginInfo);
        Assertions.assertFalse(mapped);

        // when already mapped, try to unmap with different user, return false (will reject mapping)
        // then unmap with mapped user - true first time, false second time
        boolean unmapped = aliceUsersResourceApi.unmapUser(TestEndpointUtils.administratorUserLoginInfo.getUserID());
        Assertions.assertFalse(unmapped);
        unmapped = aliceUsersResourceApi.unmapUser(TestEndpointUtils.vcellNagiosUserLoginInfo.getUserID());
        Assertions.assertTrue(unmapped);
        unmapped = aliceUsersResourceApi.unmapUser(TestEndpointUtils.vcellNagiosUserLoginInfo.getUserID());
        Assertions.assertFalse(unmapped);

        // not mapped
        UserIdentityJSONSafe unmappedUser = new UserIdentityJSONSafe().userName(null).id(null).subject(null).insertDate(null).mapped(false);
        Assertions.assertEquals(Boolean.FALSE, aliceUsersResourceApi.getMappedUser().getMapped());
        ObjectMapper objectMapper = new ObjectMapper();
        Assertions.assertEquals(objectMapper.writeValueAsString(unmappedUser), objectMapper.writeValueAsString(aliceUsersResourceApi.getMappedUser()));

        // try to map using incorrect password
        Assertions.assertEquals(Boolean.FALSE, aliceUsersResourceApi.mapUser(new UserLoginInfoForMapping().userID("vcellNagios").password("incorrect")));

        // map again, true
        mapped = aliceUsersResourceApi.mapUser(TestEndpointUtils.vcellNagiosUserLoginInfo);
        Assertions.assertTrue(mapped);

        // getIdentity() should return the mapped user
        UserIdentityJSONSafe apiRetrievedIdentity = aliceUsersResourceApi.getMappedUser();
        Assertions.assertNotNull(apiRetrievedIdentity);

        // verify that getIdentity() return subject is the same as the subject in the token
        String oidcAccessToken = keycloakClient.getAccessToken(TestEndpointUtils.TestOIDCUsers.alice.name());
        JwtClaims claims = JWTUtils.getClaimsFromUntrustedToken(oidcAccessToken);
        Assertions.assertEquals(claims.getSubject(), apiRetrievedIdentity.getSubject());

        // cleanup, remove mapping
        aliceUsersResourceApi.unmapUser(TestEndpointUtils.vcellNagiosUserLoginInfo.getUserID());
        Assertions.assertEquals(Boolean.FALSE, aliceUsersResourceApi.getMappedUser().getMapped());
    }

    @Test
    public void testNewUser() throws ApiException {
        UsersResourceApi aliceUsersResourceApi = new UsersResourceApi(aliceAPIClient);

        // map once, true - map twice return false
        String newUserID = "userid_"+Math.abs(new Random().nextInt());
        var userRegistrationInfo = new UserRegistrationInfo();
        userRegistrationInfo.setUserID(newUserID);
        userRegistrationInfo.setTitle("title");
        userRegistrationInfo.setOrganization("organization");
        userRegistrationInfo.setCountry("country");
        userRegistrationInfo.emailNotification(true);

        // should work the first time
        aliceUsersResourceApi.mapNewUser(userRegistrationInfo);
        Assertions.assertEquals(newUserID, aliceUsersResourceApi.getMappedUser().getUserName());

        // should fail the second time (user already exists)
        Assertions.assertThrowsExactly(ApiException.class, () -> aliceUsersResourceApi.mapNewUser(userRegistrationInfo), "userid already used");

        // cleanup, remove mapping
        aliceUsersResourceApi.unmapUser(newUserID);
        Assertions.assertEquals(Boolean.FALSE, aliceUsersResourceApi.getMappedUser().getMapped());
    }

    //    https://quarkus.io/guides/security-oidc-bearer-token-authentication#integration-testing-wiremock
//    https://quarkus.io/guides/security-testing
    @Test
    public void testOldAPITokenGeneration() throws ApiException {

        UsersResourceApi usersResourceApi1 = new UsersResourceApi(aliceAPIClient);
        usersResourceApi1.mapUser(TestEndpointUtils.vcellNagiosUserLoginInfo);

        UsersResourceApi aliceUserApi = new UsersResourceApi(aliceAPIClient);
        UsersResourceApi bobUserApi = new UsersResourceApi(bobAPIClient);
        UsersResourceApi usersResourceApi = new UsersResourceApi(aliceAPIClient);
        usersResourceApi.mapUser(TestEndpointUtils.vcellNagiosUserLoginInfo);

        AccesTokenRepresentationRecord token = aliceUserApi.getLegacyApiToken();
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.getToken().isEmpty());
        Assertions.assertEquals ("vcellNagios", token.getUserId());

        // Bob requests
        try{
            bobUserApi.getLegacyApiToken();
            Assertions.fail("Should throw 401 since only users mapped to VCell Users can authenticate to this endpoint.");
        } catch (ApiException e){
            Assertions.assertEquals(401, e.getCode());
        }

        UsersResourceApi usersResourceApi2 = new UsersResourceApi(bobAPIClient);
        usersResourceApi2.mapUser(TestEndpointUtils.administratorUserLoginInfo);
        token = bobUserApi.getLegacyApiToken();
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.getToken().isEmpty());
        Assertions.assertEquals("Administrator", token.getUserId());
    }

    /**
     * If there is no user mapping for the client or the user does not have a JWT token in the Authorization header for HTTP,
     * throw 401.
     * If the user is anonymous to Auth0 they must ask for a Guest token.
     * A token with the user id "vcellguest" and the user key "140220477".
     */
    @Test
    public void testOldAPITokenGenerationForGuest() throws ApiException {
        ApiClient anonymous = TestEndpointUtils.createUnAuthenticatedAPIClient(testPort);
        UsersResourceApi usersResourceApi = new UsersResourceApi(anonymous);

        Assertions.assertThrowsExactly(ApiException.class, () -> usersResourceApi.getLegacyApiToken(), "Should throw 401 since only clients with role user can call it.");

        AccesTokenRepresentationRecord guestApiToken = usersResourceApi.getGuestLegacyApiToken();
        Assertions.assertNotNull(guestApiToken);
        Assertions.assertFalse(guestApiToken.getToken().isEmpty());
        Assertions.assertEquals("vcellguest", guestApiToken.getUserId());
        Assertions.assertEquals("140220477", guestApiToken.getUserKey());
    }

    @Test
    public void testUserMiddleWare() throws ApiException{
        ApiClient anonymous = TestEndpointUtils.createUnAuthenticatedAPIClient(testPort);
        PublicationResourceApi publicationResourceApi = new PublicationResourceApi(anonymous);

        // public available to everyone
        Assertions.assertDoesNotThrow(publicationResourceApi::getPublications);

        // anonymous user can not delete a publication (other tests exist to ensure role based access is used too)
        try{
            publicationResourceApi.deletePublication(1L);
            Assertions.fail("Should throw 401 since guests can't create a publication.");
        } catch (ApiException e){
            Assertions.assertEquals(HttpStatus.UNAUTHORIZED_401, e.getCode());
        }
    }

    /**
     * Every guest user has the same exact specifications, the only difference will be the session token they use to identify themselves.
     * @throws ApiException
     */
    @Test
    public void testOldAPITokenGenerationForMultipleGuests() throws ApiException {
        ApiClient defaultUser = TestEndpointUtils.createUnAuthenticatedAPIClient(testPort);
        ApiClient secondDefaultUser = TestEndpointUtils.createUnAuthenticatedAPIClient(testPort);
        UsersResourceApi defaultUserApi = new UsersResourceApi(defaultUser);
        UsersResourceApi secondDefaultUserApi = new UsersResourceApi(secondDefaultUser);

        AccesTokenRepresentationRecord guestApiToken = defaultUserApi.getGuestLegacyApiToken();
        AccesTokenRepresentationRecord secondGuestApiToken = secondDefaultUserApi.getGuestLegacyApiToken();
        Assertions.assertNotNull(guestApiToken);
        Assertions.assertFalse(guestApiToken.getToken().isEmpty());
        Assertions.assertEquals("vcellguest", guestApiToken.getUserId());

        Assertions.assertNotNull(secondGuestApiToken);
        Assertions.assertFalse(secondGuestApiToken.getToken().isEmpty());
        Assertions.assertEquals("vcellguest", secondGuestApiToken.getUserId());

        Assertions.assertEquals(guestApiToken.getUserId(), secondGuestApiToken.getUserId());
        Assertions.assertEquals(guestApiToken.getUserKey(), secondGuestApiToken.getUserKey());
        Assertions.assertNotEquals(guestApiToken.getToken(), secondGuestApiToken.getToken());
    }
}








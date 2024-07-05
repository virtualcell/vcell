package org.vcell.restq.apiclient;

import cbit.vcell.modeldb.UserDbDriver;
import cbit.vcell.resource.PropertyLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.mailer.reactive.ReactiveMailer;
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
import org.vcell.restclient.model.UserRegistrationInfo;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.db.UserRestDB;
import org.vcell.restq.handlers.UsersResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@QuarkusTest
public class UsersApiTest {
    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    MockMailbox mockMailbox;
    @Inject
    Mailer mailer;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;
    @Inject
    UserRestDB userRestDB;
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

        // map once, true - map twice return false
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
    public void testNewUser() throws ApiException, SQLException, DataAccessException, InvalidJwtException, MalformedClaimException {
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
        token = bobUserApi.getLegacyApiToken();
        assert (token.getToken() == null);

        UsersResourceApi usersResourceApi2 = new UsersResourceApi(bobAPIClient);
        usersResourceApi2.mapUser(TestEndpointUtils.administratorUserLoginInfo);
        token = bobUserApi.getLegacyApiToken();
        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.getToken().isEmpty());
        Assertions.assertEquals("Administrator", token.getUserId());
    }

    @Test
    public void testOldAPITokenGenerationForGuest() throws ApiException {
        ApiClient defaultUser = TestEndpointUtils.createUnAuthenticatedAPIClient(testPort);
        UsersResourceApi usersResourceApi = new UsersResourceApi(defaultUser);

        Assertions.assertThrowsExactly(ApiException.class, () -> usersResourceApi.getLegacyApiToken(), "Should throw 401 since only clients with role user can call it.");

        AccesTokenRepresentationRecord guestApiToken = usersResourceApi.getGuestLegacyApiToken();
        Assertions.assertNotNull(guestApiToken);
        Assertions.assertFalse(guestApiToken.getToken().isEmpty());
        Assertions.assertEquals("vcellguest", guestApiToken.getUserId());
        Assertions.assertEquals("140220477", guestApiToken.getUserKey());
    }

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


    public void testResetPassword() throws SQLException, DataAccessException {
        PropertyLoader.setProperty(PropertyLoader.vcellSMTPHostName, "host");
        PropertyLoader.setProperty(PropertyLoader.vcellSMTPPort, "9090");
        PropertyLoader.setProperty(PropertyLoader.vcellSMTPEmailAddress, "vcell@test.com");

        UserDbDriver userDbDriver = new UserDbDriver();
        String startPassword = "1700596370260"; // set in the init.sql script
        Object lock = new Object();
        Connection connection = agroalConnectionFactory.getConnection(lock);
        UserInfo oldUserLoginInfo = userDbDriver.getUserInfo(connection, TestEndpointUtils.administratorUser.getID());

        Assertions.assertNotNull(oldUserLoginInfo);
        Assertions.assertEquals(new UserLoginInfo.DigestedPassword(startPassword).getString(), oldUserLoginInfo.digestedPassword0.getString());


        userDbDriver.sendLostPassword(connection, TestEndpointUtils.administratorUser.getName());
        List<Mail> mails = mockMailbox.getMailsSentTo(oldUserLoginInfo.email);
        Mail mail = mails.get(0);
        String passwordReset = mail.getText();
        String[] strings = passwordReset.split("\\.");
        strings = strings[0].split(" ");
        String newPassword = strings[strings.length - 1];
        newPassword = newPassword.replace("'", "");

        UserInfo newUserLoginInfo = userDbDriver.getUserInfo(connection, TestEndpointUtils.administratorUser.getID());
        Assertions.assertEquals(newUserLoginInfo.digestedPassword0.getString(), new UserLoginInfo.DigestedPassword(newPassword).getString());

        // reset changes
        userDbDriver.updateUserInfo(connection, oldUserLoginInfo);
        newUserLoginInfo = userDbDriver.getUserInfo(connection, TestEndpointUtils.administratorUser.getID());
        Assertions.assertEquals(oldUserLoginInfo.digestedPassword0.getString(), newUserLoginInfo.digestedPassword0.getString());

        agroalConnectionFactory.release(connection, lock);
    }

}








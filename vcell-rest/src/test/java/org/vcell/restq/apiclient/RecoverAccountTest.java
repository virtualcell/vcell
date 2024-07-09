package org.vcell.restq.apiclient;

import cbit.vcell.resource.PropertyLoader;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.*;
import org.vcell.auth.JWTUtils;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restclient.ApiResponse;
import org.vcell.restclient.api.UsersResourceApi;
import org.vcell.restclient.model.UserIdentityJSONSafe;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.DataAccessException;

import java.sql.SQLException;
import java.util.List;

@QuarkusTest
public class RecoverAccountTest {

    @Inject
    MockMailbox mailbox;

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private ApiClient aliceAPIClient;

    @BeforeEach
    public void createClients() throws JoseException {
        mailbox.clear();

        aliceAPIClient = TestEndpointUtils.createAuthenticatedAPIClient(keycloakClient, testPort, TestEndpointUtils.TestOIDCUsers.alice);
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
    public void testRequestRecoveryEmail() throws ApiException {
        UsersResourceApi aliceUsersResourceApi = new UsersResourceApi(aliceAPIClient);

        // map user
        boolean mapped = aliceUsersResourceApi.mapUser(TestEndpointUtils.vcellNagiosUserLoginInfo);
        Assertions.assertTrue(mapped);

        // unmap user
        String userid = TestEndpointUtils.vcellNagiosUserLoginInfo.getUserID();
        String email = userid+"@example.com";
        Assertions.assertTrue(aliceUsersResourceApi.unmapUser(userid), "Failed to unmap user");

        // verify that the user is not mapped
        ApiResponse<UserIdentityJSONSafe> mappedUserResponse1 = aliceUsersResourceApi.getMappedUserWithHttpInfo();
        Assertions.assertEquals(200, mappedUserResponse1.getStatusCode());
        Assertions.assertEquals(Boolean.FALSE, mappedUserResponse1.getData().getMapped());

        // request recovery with wrong email
        Assertions.assertThrows(ApiException.class, () -> aliceUsersResourceApi.requestRecoveryEmail(email+"abc", userid));
        // request recovery with wrong userid
        Assertions.assertThrows(ApiException.class, () -> aliceUsersResourceApi.requestRecoveryEmail(email, userid+"abc"));
        // request recovery with correct email and userid
        ApiResponse<Void> response1 = aliceUsersResourceApi.requestRecoveryEmailWithHttpInfo(email, userid);
        Assertions.assertEquals(200, response1.getStatusCode());

        // observe the email body captured from the mock SMTP service
        List<Mail> capturedEmails = mailbox.getMailsSentTo(email);
        Assertions.assertEquals(1, capturedEmails.size());
        Mail mail = capturedEmails.get(0);
        String body = mail.getHtml();
        String magicLink = body.substring(body.indexOf("<a href=\"") + 9, body.indexOf("\">"));
        Assertions.assertTrue(magicLink.contains("/api/v1/users/processMagicLink?magic="));
        // extract 'magic' query parameter from url in 'magicLink'
        String magicLinkToken = magicLink.substring(magicLink.indexOf("magic=") + 6);

        ApiResponse<Void> response2 = aliceUsersResourceApi.processMagicLinkWithHttpInfo(magicLinkToken);
        Assertions.assertEquals(200, response2.getStatusCode());

        // verify that the user is now mapped
        ApiResponse<UserIdentityJSONSafe> mappedUserResponse2 = aliceUsersResourceApi.getMappedUserWithHttpInfo();
        Assertions.assertEquals(200, mappedUserResponse2.getStatusCode());
        Assertions.assertEquals(Boolean.TRUE, mappedUserResponse2.getData().getMapped());
    }
}
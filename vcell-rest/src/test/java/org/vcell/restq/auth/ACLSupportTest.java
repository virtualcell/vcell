package org.vcell.restq.auth;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.UserIdentity;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.UserRegistrationOP;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;
import cbit.vcell.xml.XmlParseException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.*;
import org.vcell.restclient.ApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.restq.TestEndpointUtils;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.BigString;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.BioModelInfo;
import org.vcell.util.document.SpecialUser;
import org.vcell.util.document.User;
import org.vcell.util.document.VersionableType;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@QuarkusTest
public class ACLSupportTest {
    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String authServerUrl;

    @ConfigProperty(name = "quarkus.http.test-port")
    Integer testPort;

    @Inject
    AgroalConnectionFactory agroalConnectionFactory;

    KeycloakTestClient keycloakClient = new KeycloakTestClient();

    private final String randomUserOIDCSubject = "auth0|13432432432bv2344b962345";
    private final String randomUserOIDCIssuer = "https://dev-dzhx7i2db3x3kkvq.us.auth0.com/";
    private final String adminUserOIDCSubject = "auth0|65cb6311365d79c2fb96a005";
    private final String adminUserOIDCIssuer = "https://dev-dzhx7i2db3x3kkvq.us.auth0.com/";

    private ApiClient aliceAPIClient;
    private ApiClient bobAPIClient;

    @BeforeAll
    public static void setupConfig(){
        PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
    }

    @BeforeEach
    public void mapUsers() throws ApiException, DataAccessException, SQLException {
        DatabaseServerImpl databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        databaseServer.getAdminDBTopLevel().setUserIdentityFromIdentityProvider(
                TestEndpointUtils.administratorUser, adminUserOIDCSubject, adminUserOIDCIssuer, true
        );
        databaseServer.getAdminDBTopLevel().setUserIdentityFromIdentityProvider(
                TestEndpointUtils.randomUser, randomUserOIDCSubject, randomUserOIDCIssuer, true
        );
    }

    @AfterEach
    public void unmapUsers() throws SQLException, DataAccessException {
        TestEndpointUtils.removeAllMappings(agroalConnectionFactory);
    }

    @Test
    public void testOthersAccessToVCellSupport() throws IOException, DataAccessException, XmlParseException, SQLException {
        DatabaseServerImpl databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        User adminUser = databaseServer.getAdminDBTopLevel().getUserIdentitiesFromSubjectAndIssuer(adminUserOIDCSubject, adminUserOIDCIssuer, true).get(0).user();
        User randomUser = databaseServer.getAdminDBTopLevel().getUserIdentitiesFromSubjectAndIssuer(randomUserOIDCSubject, randomUserOIDCIssuer, true).get(0).user();

        // Add BioModel owned by VCell Nagios, share it with VCell Support, ensure Admin has access and random user does not have access
        String bioModelVCML = TestEndpointUtils.getResourceString("/TestVCML.vcml");
        BigString savedModelString = databaseServer.saveBioModel(TestEndpointUtils.vcellNagiosUser, new BigString(bioModelVCML), null);
        BioModel savedModel = XmlHelper.XMLToBioModel(new XMLSource(savedModelString.toString()));

        databaseServer.groupAddUser(TestEndpointUtils.vcellNagiosUser, VersionableType.BioModelMetaData, savedModel.getVersion().getVersionKey(),
                TestEndpointUtils.vcellSupportUser.getName(), true);

        BioModelInfo[] infos = databaseServer.getBioModelInfos(adminUser, true);

        // Only model admin has is the shared one, and the one saved by Nagios User
        Assertions.assertEquals(1, infos.length);
        Assertions.assertTrue(infos[0].getVersion().getVersionKey().compareEqual(savedModel.getVersion().getVersionKey()));
        // Group access is only for VCell support
        Assertions.assertTrue( infos[0].getVersion().getGroupAccess().isMember(TestEndpointUtils.vcellSupportUser));

        // Should not have access to VCell support roles because random user does not have the support role
        BioModelInfo[] randomUserInfos = databaseServer.getBioModelInfos(randomUser, true);
        Assertions.assertEquals(0, randomUserInfos.length);
    }

    @Test
    public void testSpecialClaimsPeopleHave() throws SQLException, DataAccessException {
        AdminDBTopLevel adminDBTopLevel = new AdminDBTopLevel(agroalConnectionFactory);
        List<UserIdentity> randomUser = adminDBTopLevel.getUserIdentitiesFromSubjectAndIssuer(randomUserOIDCSubject, randomUserOIDCIssuer, true);

        // Random user has no special privileges
        Assertions.assertEquals(1, randomUser.size());
        SpecialUser userIdentity = (SpecialUser) randomUser.get(0).user();
        Assertions.assertEquals("5", userIdentity.getID().toString());
        Assertions.assertEquals(0, userIdentity.getMySpecials().length);

        // Admin has all privileges
        List<UserIdentity> adminIdentities = adminDBTopLevel.getUserIdentitiesFromSubjectAndIssuer(adminUserOIDCSubject, adminUserOIDCIssuer, true);
        Assertions.assertEquals(1, adminIdentities.size());
        SpecialUser adminUser = (SpecialUser) adminIdentities.get(0).user();
        Assertions.assertEquals("2", adminUser.getID().toString());
        Assertions.assertEquals(4, adminUser.getMySpecials().length);

        Assertions.assertTrue(adminUser.isAdmin());
        Assertions.assertTrue(adminUser.isVCellSupport());
        Assertions.assertTrue(adminUser.isPublisher());
    }

    @Test
    public void testVCellSupportIDGrabbing(){
        Assertions.assertEquals(PropertyLoader.getRequiredProperty(PropertyLoader.vcellSupportId), "4");
    }
}

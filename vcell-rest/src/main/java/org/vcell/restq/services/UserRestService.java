package org.vcell.restq.services;

import cbit.vcell.modeldb.*;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.vcell.auth.JWTUtils;
import org.vcell.restq.auth.CustomSecurityIdentityAugmentor;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.restq.errors.exceptions.*;
import org.vcell.restq.handlers.UsersResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.*;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserRestService {

    private final AdminDBTopLevel adminDBTopLevel;
    private final DatabaseServerImpl databaseServer;
    public final static String DEFAULT_CLIENTID = "85133f8d-26f7-4247-8356-d175399fc2e6";

    @Inject
    public UserRestService(AgroalConnectionFactory agroalConnectionFactory) {
        try {
            adminDBTopLevel = new AdminDBTopLevel(agroalConnectionFactory);
            databaseServer = new DatabaseServerImpl(agroalConnectionFactory, agroalConnectionFactory.getKeyFactory());
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeWebException("database error during initialization", e);
        }
    }


    // TODO: add some short-lived caching here to avoid repeated database calls

    public User getUserFromIdentity(SecurityIdentity securityIdentity) throws NotAuthenticatedWebException, DataAccessWebException {
        List<UserIdentity> userIdentities = getUserIdentities(securityIdentity);
        if (userIdentities == null || userIdentities.isEmpty()){
            throw new NotAuthenticatedWebException("User is not authenticated.");
        }
        if (userIdentities.size() > 1){
            throw new WebApplicationException("Multiple identities found for user.");
        }
        return userIdentities.get(0).user();
    }

    public User getUserOrAnonymousFromIdentity(SecurityIdentity securityIdentity) throws DataAccessWebException {
        try{
            if (securityIdentity.isAnonymous()){
                return null;
            }
            List<UserIdentity> userIdentities = getUserIdentities(securityIdentity);
            if (userIdentities == null || userIdentities.isEmpty()){
                return null;
            }
            if (userIdentities.size() > 1){
                throw new WebApplicationException("Multiple identities found for user.");
            }
            return userIdentities.get(0).user();
        } catch (NotAuthenticatedWebException e){
            return null;
        }
    }

    // TODO: add some short-lived caching here to avoid repeated database calls
    public List<UserIdentity> getUserIdentities(SecurityIdentity securityIdentity) throws NotAuthenticatedWebException, DataAccessWebException {
        if (securityIdentity.isAnonymous()){
            return null;
        }
        JsonWebToken jwt = CustomSecurityIdentityAugmentor.getJsonWebToken(securityIdentity);
        if (jwt == null) {
            throw new NotAuthenticatedWebException("securityIdentity is missing jwt");
        }
        String subject = jwt.getSubject();
        String issuer = jwt.getIssuer();
        if (subject == null) {
            throw new NotAuthenticatedWebException("securityIdentity is missing subject");
        }
        if (issuer == null) {
            throw new NotAuthenticatedWebException("securityIdentity is missing issuer");
        }
        try {
            return adminDBTopLevel.getUserIdentitiesFromSubjectAndIssuer(subject, issuer, true);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeWebException("database error while retrieving user identity: ", e);
        }
    }

    public boolean mapUserIdentity(JWTUtils.MagicTokenClaims magicTokenClaims) throws NotAuthenticatedWebException, DataAccessWebException {
       try {
            String subject = magicTokenClaims.requesterSubject();
            String issuer = magicTokenClaims.requesterIssuer();
            User userToMap = magicTokenClaims.user();
            if (subject == null) {
                throw new NotAuthenticatedWebException("magic link token is missing subject");
            }
            if (issuer == null) {
                throw new NotAuthenticatedWebException("magic link token is missing issuer");
            }

            // check existing mappings, if already mapped to this user, return true
            List<UserIdentity> userIdentities = adminDBTopLevel.getUserIdentitiesFromSubjectAndIssuer(subject, issuer, true);
            for (UserIdentity userIdentity : userIdentities) {
                if (userIdentity.user().getID().equals(magicTokenClaims.user().getID())) {
                    return true;
                }
            }

            // check existing mappings, if already mapped to a different user, return false (can't remap)
            for (UserIdentity userIdentity : userIdentities) {
                if (!userIdentity.user().getID().equals(magicTokenClaims.user().getID())) {
                    return false;
                }
            }

            adminDBTopLevel.setUserIdentityFromIdentityProvider(userToMap, subject, issuer, true);
            return true;
        } catch (DataAccessException e){
           throw new DataAccessWebException(e.getMessage(), e);
       } catch (SQLException e) {
            throw new RuntimeWebException("database error while mapping user identity", e);
        }
    }

    public boolean mapUserIdentity(SecurityIdentity securityIdentity, UsersResource.UserLoginInfoForMapping mapUser) throws NotAuthenticatedWebException, DataAccessWebException {
        if (securityIdentity.isAnonymous()){
            return false;
        }
        try {
            User user = adminDBTopLevel.getUser(mapUser.userID(), new UserLoginInfo.DigestedPassword(mapUser.password()), true, false);
            if(user == null){
                return false;
            }
            JsonWebToken jwt = CustomSecurityIdentityAugmentor.getJsonWebToken(securityIdentity);
            if (jwt == null) {
                throw new NotAuthenticatedWebException("securityIdentity is missing jwt");
            }
            String subject = jwt.getSubject();
            String issuer = jwt.getIssuer();
            if (subject == null) {
                throw new NotAuthenticatedWebException("securityIdentity is missing subject");
            }
            if (issuer == null) {
                throw new NotAuthenticatedWebException("securityIdentity is missing issuer");
            }

            // check existing mappings, if already mapped to this user, return true
            List<UserIdentity> userIdentities = adminDBTopLevel.getUserIdentitiesFromSubjectAndIssuer(subject, issuer, true);
            for (UserIdentity userIdentity : userIdentities) {
                if (userIdentity.user().getID().equals(user.getID())) {
                    return true;
                }
            }

            // check existing mappings, if already mapped to a different user, return false (can't remap)
            for (UserIdentity userIdentity : userIdentities) {
                if (!userIdentity.user().getID().equals(user.getID())) {
                    return false;
                }
            }

            adminDBTopLevel.setUserIdentityFromIdentityProvider(user, subject, issuer, true);
            return true;
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeWebException("database error while mapping user identity", e);
        }
    }

    public void createUserIdentity(String subject, String issuer, String email, String name, UsersResource.UserRegistrationInfo userRegistrationInfo) throws UseridIDExistsException, DataAccessException {
        UserInfo newUserInfo = new UserInfo();
        newUserInfo.userid = userRegistrationInfo.userID();
        newUserInfo.email = email;
        newUserInfo.notify = (userRegistrationInfo.emailNotification() != null) ? userRegistrationInfo.emailNotification() : false;
        newUserInfo.wholeName = name;
        newUserInfo.country = userRegistrationInfo.country();
        newUserInfo.company = userRegistrationInfo.organization();
        newUserInfo.title = userRegistrationInfo.title();
        newUserInfo.digestedPassword0 = new UserLoginInfo.DigestedPassword(UUID.randomUUID().toString());
        newUserInfo.insertDate = new Date();
        try {
            KeyValue newUserKey = adminDBTopLevel.insertUserInfo(newUserInfo, true);
            User user = new User(newUserInfo.userid, newUserKey);
            adminDBTopLevel.setUserIdentityFromIdentityProvider(user, subject, issuer, true);
        } catch (SQLException e) {
            throw new RuntimeWebException("Database exception", e);
        }
    }

    public boolean unmapUserIdentity(SecurityIdentity securityIdentity, String userName) throws NotAuthenticatedWebException, DataAccessWebException {
        if (securityIdentity.isAnonymous()){
            return false;
        }
        try {
            JsonWebToken jwt = CustomSecurityIdentityAugmentor.getJsonWebToken(securityIdentity);
            if (jwt == null) {
                return false;
            }
            String subject = jwt.getSubject();
            String issuer = jwt.getIssuer();
            if (subject == null) {
                throw new NotAuthenticatedWebException("securityIdentity is missing subject");
            }
            if (issuer == null) {
                throw new NotAuthenticatedWebException("securityIdentity is missing issuer");
            }

            User user = null;
            List<UserIdentity> userIdentities = adminDBTopLevel.getUserIdentitiesFromSubjectAndIssuer(subject, issuer, true);
            for (UserIdentity userIdentity : userIdentities) {
                if (userIdentity.user().getName().equals(userName)) {
                    user = userIdentity.user();
                }
            }
            if (user == null) {
                return false;
            }

            // remove existing mapping for this user, true = success
            return adminDBTopLevel.deleteUserIdentity(user, subject, issuer, true);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeWebException("database error while mapping user identity", e);
        }
    }


    // Old API Compatability Functions

    public ApiAccessToken generateApiAccessToken(KeyValue apiClientKey, User user) throws DataAccessWebException {
        try {
            return adminDBTopLevel.generateApiAccessToken(apiClientKey, user, getNewExpireDate(), true);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeWebException("database error while generating access token", e);
        }
    }

    public void sendOldLegacyPassword(String userID) throws SQLException, DataAccessException {
        adminDBTopLevel.sendLostPassword(userID, true);
    }

    public ApiClient getAPIClient() throws DataAccessWebException {
        try {
            return adminDBTopLevel.getApiClient(DEFAULT_CLIENTID, true);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeWebException("database error while retrieving api client info", e);
        }
    }

    private Date getNewExpireDate() {
        long oneHourMs = 1000*60*60;
        long oneDayMs = oneHourMs * 24;
        long tokenLifetimeMs = oneDayMs * 30;
        Date expireTime = new Date(System.currentTimeMillis() + tokenLifetimeMs);
        return expireTime;
    }

    public UserInfo getUserInfo(String userID) throws NotFoundWebException, DataAccessWebException {
        try {
            User user = adminDBTopLevel.getUser(userID, true);
            if (user == null){
                return null;
            }
            return adminDBTopLevel.getUserInfo(user.getID(), true);
        } catch (ObjectNotFoundException e) {
            throw new NotFoundWebException(e.getMessage(), e);
        } catch (DataAccessException e){
            throw new DataAccessWebException(e.getMessage(), e);
        } catch (SQLException e){
            throw new RuntimeWebException("Database Error" ,e);
        }
    }


    public VersionInfo groupAddUser(User user, VersionableType vType, KeyValue key, String usernameToAdd, boolean isHidden) throws DataAccessException {
        return databaseServer.groupAddUser(user, vType, key, usernameToAdd, isHidden);
    }

    public VersionInfo groupRemoveUser(User user, VersionableType vType, KeyValue key, String usernameToRemove, boolean isHiddenFromOwner) throws DataAccessException {
        return databaseServer.groupRemoveUser(user, vType, key, usernameToRemove, isHiddenFromOwner);
    }

    public VersionInfo groupSetPrivate(User user, VersionableType vType, KeyValue key) throws DataAccessException {
        return databaseServer.groupSetPrivate(user, vType, key);
    }

    public VersionInfo groupSetPublic(User user, VersionableType vType, KeyValue key) throws DataAccessException {
        return databaseServer.groupSetPublic(user, vType, key);
    }

}

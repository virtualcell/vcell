package org.vcell.restq.db;

import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.ApiAccessToken;
import cbit.vcell.modeldb.ApiClient;
import cbit.vcell.modeldb.UserIdentity;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.vcell.auth.JWTUtils;
import org.vcell.restq.auth.CustomSecurityIdentityAugmentor;
import org.vcell.restq.handlers.UsersResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.UseridIDExistsException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;
import org.w3c.www.http.HTTP;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserRestDB {

    private final AdminDBTopLevel adminDBTopLevel;
    public final static String DEFAULT_CLIENTID = "85133f8d-26f7-4247-8356-d175399fc2e6";

    @Inject
    public UserRestDB(AgroalConnectionFactory agroalConnectionFactory) throws DataAccessException {
        try {
            adminDBTopLevel = new AdminDBTopLevel(agroalConnectionFactory);
        } catch (SQLException e) {
            throw new DataAccessException("database error during initialization", e);
        }
    }


    // TODO: add some short-lived caching here to avoid repeated database calls

    /**
     * Get the users identity from the requests that they make, usually this is a JWT token, placed within the
     * HTTP Authorization header. If allowAnonymous is true, then null will be returned for anonymous users, otherwise
     * an error will be thrown about the user being unauthenticated.
     */
    public enum UserRequirement{
        ALLOW_ANONYMOUS,
        REQUIRE_USER
    }
    public User getUserFromIdentity(SecurityIdentity securityIdentity, UserRequirement allowAnonymous) throws DataAccessException, WebApplicationException {
        List<UserIdentity> userIdentities = getUserIdentities(securityIdentity);
        if (userIdentities == null || userIdentities.isEmpty()){
            if (allowAnonymous == UserRequirement.ALLOW_ANONYMOUS){
                return null;
            } else {
                throw new WebApplicationException("User is not authenticated.", HTTP.UNAUTHORIZED);
            }
        }
        if (userIdentities.size() > 1){
            throw new DataAccessException("multiple identities found for user");
        }
        return userIdentities.get(0).user();
    }

    // TODO: add some short-lived caching here to avoid repeated database calls
    public List<UserIdentity> getUserIdentities(SecurityIdentity securityIdentity) throws DataAccessException {
        if (securityIdentity.isAnonymous()){
            return null;
        }
        JsonWebToken jwt = CustomSecurityIdentityAugmentor.getJsonWebToken(securityIdentity);
        if (jwt == null) {
            throw new DataAccessException("securityIdentity is missing jwt");
        }
        String subject = jwt.getSubject();
        String issuer = jwt.getIssuer();
        if (subject == null) {
            throw new DataAccessException("securityIdentity is missing subject");
        }
        if (issuer == null) {
            throw new DataAccessException("securityIdentity is missing issuer");
        }
        try {
            return adminDBTopLevel.getUserIdentitiesFromSubjectAndIssuer(subject, issuer, true);
        } catch (SQLException e) {
            throw new DataAccessException("database error while retrieving user identity: "+e.getMessage(), e);
        }
    }

    public boolean mapUserIdentity(JWTUtils.MagicTokenClaims magicTokenClaims) throws DataAccessException {
       try {
            String subject = magicTokenClaims.requesterSubject();
            String issuer = magicTokenClaims.requesterIssuer();
            User userToMap = magicTokenClaims.user();
            if (subject == null) {
                throw new DataAccessException("magic link token is missing subject");
            }
            if (issuer == null) {
                throw new DataAccessException("magic link token is missing issuer");
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
        } catch (SQLException e) {
            throw new DataAccessException("database error while mapping user identity: "+e.getMessage(), e);
        }
    }

    public boolean mapUserIdentity(SecurityIdentity securityIdentity, UsersResource.UserLoginInfoForMapping mapUser) throws DataAccessException {
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
                throw new DataAccessException("securityIdentity is missing jwt");
            }
            String subject = jwt.getSubject();
            String issuer = jwt.getIssuer();
            if (subject == null) {
                throw new DataAccessException("securityIdentity is missing subject");
            }
            if (issuer == null) {
                throw new DataAccessException("securityIdentity is missing issuer");
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
        } catch (SQLException e) {
            throw new DataAccessException("database error while mapping user identity: "+e.getMessage(), e);
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
            throw new DataAccessException("Database exception", e);
        }
    }

    public boolean unmapUserIdentity(SecurityIdentity securityIdentity, String userName) throws DataAccessException {
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
                throw new DataAccessException("securityIdentity is missing subject");
            }
            if (issuer == null) {
                throw new DataAccessException("securityIdentity is missing issuer");
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
        } catch (SQLException e) {
            throw new DataAccessException("database error while mapping user identity: "+e.getMessage(), e);
        }
    }


    // Old API Compatability Functions

    public ApiAccessToken generateApiAccessToken(KeyValue apiClientKey, User user) throws DataAccessException {
        try {
            return adminDBTopLevel.generateApiAccessToken(apiClientKey, user, getNewExpireDate(), true);
        } catch (SQLException e) {
            throw new DataAccessException("database error while generating access token", e);
        }
    }

    public void sendOldLegacyPassword(String userID) throws SQLException, DataAccessException {
        adminDBTopLevel.sendLostPassword(userID, true);
    }

    public ApiClient getAPIClient() throws DataAccessException {
        try {
            return adminDBTopLevel.getApiClient(DEFAULT_CLIENTID, true);
        } catch (SQLException e) {
            throw new DataAccessException("database error while retrieving api client info", e);
        }
    }

    private Date getNewExpireDate() {
        long oneHourMs = 1000*60*60;
        long oneDayMs = oneHourMs * 24;
        long tokenLifetimeMs = oneDayMs * 30;
        Date expireTime = new Date(System.currentTimeMillis() + tokenLifetimeMs);
        return expireTime;
    }

    public UserInfo getUserInfo(String userID) throws SQLException, DataAccessException {
        try {
            User user = adminDBTopLevel.getUser(userID, true);
            if (user == null){
                return null;
            }
            return adminDBTopLevel.getUserInfo(user.getID(), true);
        } catch (ObjectNotFoundException e) {
            return null;
        }
    }
}

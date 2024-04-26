package org.vcell.restq.db;

import cbit.vcell.modeldb.*;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.vcell.restq.auth.CustomSecurityIdentityAugmentor;
import org.vcell.restq.handlers.UsersResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

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
    public User getUserFromIdentity(SecurityIdentity securityIdentity) throws DataAccessException {
        List<UserIdentity> userIdentities = getUserIdentities(securityIdentity);
        if (userIdentities == null || userIdentities.isEmpty()){
            return null;
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
            String subject = jwt.getSubject();
            String issuer = jwt.getIssuer();
            if (subject == null) {
                throw new DataAccessException("securityIdentity is missing subject");
            }
            if (issuer == null) {
                throw new DataAccessException("securityIdentity is missing issuer");
            }
            adminDBTopLevel.setUserIdentityFromIdentityProvider(user, subject, issuer, true);
            return true;
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

}

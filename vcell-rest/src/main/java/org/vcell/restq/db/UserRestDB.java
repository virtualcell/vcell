package org.vcell.restq.db;

import cbit.vcell.modeldb.*;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.vcell.restq.auth.AuthUtils;
import org.vcell.restq.auth.CustomSecurityIdentityAugmentor;
import org.vcell.restq.handlers.UsersResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import java.sql.SQLException;
import java.util.Date;

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
        UserIdentity userIdentity = getUserIdentity(securityIdentity);
        return userIdentity == null ? null : userIdentity.user();
    }

    // TODO: add some short-lived caching here to avoid repeated database calls
    public UserIdentity getUserIdentity(SecurityIdentity securityIdentity) throws DataAccessException {
        if (securityIdentity.isAnonymous()){
            return null;
        }
        String oidcName = securityIdentity.getPrincipal().getName();
        UserIdentityTable.IdentityProvider identityProvider = AuthUtils.determineIdentityProvider(securityIdentity);
        try {
            return adminDBTopLevel.getUserIdentityFromSubjectAndIdentityProvider(oidcName, identityProvider, true);
        } catch (SQLException e) {
            throw new DataAccessException("database error while retrieving user identity: "+e.getMessage(), e);
        }
    }

    public boolean mapUserIdentity(SecurityIdentity securityIdentity, UsersResource.MapUser mapUser) throws DataAccessException {
        if (securityIdentity.isAnonymous()){
            return false;
        }
        try {
            User user = adminDBTopLevel.getUser(mapUser.userID(), new UserLoginInfo.DigestedPassword(mapUser.password()), true, false);
            if(user == null){
                return false;
            }
            String identity = CustomSecurityIdentityAugmentor.getAuth0ID(securityIdentity);
            UserIdentityTable.IdentityProvider identityProvider = AuthUtils.determineIdentityProvider(securityIdentity);
            adminDBTopLevel.setUserIdentityFromIdentityProvider(user, identity, identityProvider, true);
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

package org.vcell.restq.db;

import cbit.vcell.modeldb.*;
import io.quarkus.security.identity.SecurityIdentity;
import org.vcell.auth.JWTUtils;
import org.vcell.restq.auth.AuthUtils;
import org.vcell.restq.auth.CustomSecurityIdentityAugmentor;
import org.vcell.restq.handlers.UsersResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import java.sql.SQLException;
import java.util.Date;

public class UserRestDB {

    private final AdminDBTopLevel adminDBTopLevel;
    public final static String DEFAULT_CLIENTID = "85133f8d-26f7-4247-8356-d175399fc2e6";

    public UserRestDB(OracleAgroalConnectionFactory oracleAgroalConnectionFactory) throws SQLException {
        adminDBTopLevel = new AdminDBTopLevel(oracleAgroalConnectionFactory);
        JWTUtils.setRsaPublicAndPrivateKey();
    }

    public User getUserFromIdentity(SecurityIdentity securityIdentity) throws SQLException, DataAccessException {
        UserIdentity userIdentity = getUserIdentity(securityIdentity);
        return userIdentity == null ? null : userIdentity.user();
    }

    public UserIdentity getUserIdentity(SecurityIdentity securityIdentity) throws SQLException, DataAccessException {
        String oidcName = securityIdentity.getPrincipal().getName();
        UserIdentityTable.IdentityProvider identityProvider = AuthUtils.determineIdentityProvider(securityIdentity);
        return adminDBTopLevel.getUserIdentityFromSubjectAndIdentityProvider(oidcName, identityProvider, true);
    }

    public boolean mapUserIdentity(SecurityIdentity securityIdentity, UsersResource.MapUser mapUser) throws SQLException, DataAccessException {
        org.vcell.util.document.User user = adminDBTopLevel.getUser(mapUser.userID(), new UserLoginInfo.DigestedPassword(mapUser.password()), true, false);
        if(user == null){
            return false;
        }
        String identity = CustomSecurityIdentityAugmentor.getAuth0ID(securityIdentity);
        UserIdentityTable.IdentityProvider identityProvider = AuthUtils.determineIdentityProvider(securityIdentity);
        adminDBTopLevel.setUserIdentityFromIdentityProvider(user, identity, identityProvider, true);
        return true;
    }


    // Old API Compatability Functions

    public ApiAccessToken generateApiAccessToken(KeyValue apiClientKey, User user) throws ObjectNotFoundException, DataAccessException, SQLException{
        return adminDBTopLevel.generateApiAccessToken(apiClientKey, user, getNewExpireDate(), true);
    }

    public ApiClient getAPIClient() throws SQLException, DataAccessException {
        return adminDBTopLevel.getApiClient(DEFAULT_CLIENTID, true);
    }

    private Date getNewExpireDate() {
        long oneHourMs = 1000*60*60;
        long oneDayMs = oneHourMs * 24;
        long tokenLifetimeMs = oneDayMs * 30;
        Date expireTime = new Date(System.currentTimeMillis() + tokenLifetimeMs);
        return expireTime;
    }

}

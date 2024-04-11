package org.vcell.restq.auth;

import cbit.vcell.modeldb.UserIdentityTable;
import io.quarkus.security.identity.SecurityIdentity;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

public class AuthUtils {
    public static final User DUMMY_USER = new User("VOID_VCELL_USER", new KeyValue("2222"));
    public static final User.SpecialUser POWER_USER = new User.SpecialUser("POWER_USER", new KeyValue("3333"), new User.SPECIAL_CLAIM[]{User.SPECIAL_CLAIM.powerUsers});
    public static final User.SpecialUser ADMIN_USER = new User.SpecialUser("ADMIN_USER", new KeyValue("4444"), new User.SPECIAL_CLAIM[]{User.SPECIAL_CLAIM.admins});
    public static final User.SpecialUser PUBLICATION_USER = new User.SpecialUser("PUBLICATION_USER", new KeyValue("5555"), new User.SPECIAL_CLAIM[]{User.SPECIAL_CLAIM.publicationEditors});

    private static final String devAuth0 = "https://dev-dzhx7i2db3x3kkvq.us.auth0.com/";
    public static UserIdentityTable.IdentityProvider determineIdentityProvider(SecurityIdentity securityIdentity){
        JsonWebToken jsonWebToken = (JsonWebToken) securityIdentity.getPrincipal();
        String issuer = jsonWebToken.getClaim("iss");
        if(issuer.equals(devAuth0)){
            return UserIdentityTable.IdentityProvider.AUTH0;
        } else if (issuer.contains("localhost")) { //need to change in future
            return UserIdentityTable.IdentityProvider.KEYCLOAK;
        }
        return null;
    }
}

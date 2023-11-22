package org.vcell.rest.auth;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

public class AuthUtils {
    public static final User DUMMY_USER = new User("VOID_VCELL_USER", new KeyValue("2222"));
    public static final User.SpecialUser POWER_USER = new User.SpecialUser("POWER_USER", new KeyValue("3333"), new User.SPECIAL_CLAIM[]{User.SPECIAL_CLAIM.powerUsers});
    public static final User.SpecialUser ADMIN_USER = new User.SpecialUser("ADMIN_USER", new KeyValue("4444"), new User.SPECIAL_CLAIM[]{User.SPECIAL_CLAIM.admins});
    public static final User.SpecialUser PUBLICATION_USER = new User.SpecialUser("PUBLICATION_USER", new KeyValue("5555"), new User.SPECIAL_CLAIM[]{User.SPECIAL_CLAIM.publicationEditors});
}

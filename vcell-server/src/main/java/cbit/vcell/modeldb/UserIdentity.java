package cbit.vcell.modeldb;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.util.Date;

public record UserIdentity(
        KeyValue id,
        User user,
        String auth0Subject,
        Date insertDate
) {

}

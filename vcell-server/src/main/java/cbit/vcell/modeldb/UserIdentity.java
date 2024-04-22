package cbit.vcell.modeldb;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

public record UserIdentity(
        BigDecimal id,
        User user,
        String subject,
        LocalDateTime insertDate
) {

}

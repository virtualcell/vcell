package cbit.vcell.modeldb;

import org.vcell.util.document.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UserIdentity(
        BigDecimal id,
        User user,
        String subject,
        String issuer,
        LocalDateTime insertDate
) {

}

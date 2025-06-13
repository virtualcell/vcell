package cbit.vcell.modeldb;

import org.vcell.util.document.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UserIdentityBuilder {
    private final BigDecimal id;
    private final String subject;
    private final String issuer;
    private final LocalDateTime insertDate;
    private final User.SpecialUserBuilder userBuilder;

    public UserIdentityBuilder(BigDecimal id, User.SpecialUserBuilder specialUserBuilder,
                               String subject, String issuer, LocalDateTime insertDate){
        this.id = id;
        this.subject = subject;
        this.issuer = issuer;
        this.insertDate = insertDate;
        this.userBuilder = specialUserBuilder;
    }

    public BigDecimal getId(){
        return id;
    }

    public User.SpecialUserBuilder getUserBuilder(){
        return userBuilder;
    }

    public UserIdentity build(){
        return new UserIdentity(id, userBuilder.build(), subject, issuer, insertDate);
    }
}

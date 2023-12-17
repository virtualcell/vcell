package org.vcell.auth;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.vcell.auth.JWTUtils.verifyJWS;

@Tag("Fast")
public class JWTUtilsTest {

    @Test
    public void testJWT() throws JoseException, MalformedClaimException {
        // Generate an RSA key pair, which will be used for signing and verification of the JWT, wrapped in a JWK
        RsaJsonWebKey rsaJsonWebKey = JWTUtils.createNewJsonWebKey("k1");
        JWTUtils.setRsaJsonWebKey(rsaJsonWebKey);

        User user = new User.SpecialUser("testuser", new KeyValue("12345"), new User.SPECIAL_CLAIM[0]);


        NumericDate expirationDate = NumericDate.now();
        expirationDate.addSeconds(1);
        String token1 = JWTUtils.createToken(user, expirationDate);
        assertTrue(verifyJWS(token1), "expect valid token");

        // test expiration date by setting the expiration date 31 seconds in the past (there is a 30 second tolerance)
        expirationDate = NumericDate.now();
        expirationDate.addSeconds(-31);
        String token2 = JWTUtils.createToken(user, expirationDate);
        assertFalse(verifyJWS(token2), "expect timeout");

        // install a different JsonWebKey and see that both tokens are invalid
        RsaJsonWebKey rsaJsonWebKey2 = JWTUtils.createNewJsonWebKey("k2");
        JWTUtils.setRsaJsonWebKey(rsaJsonWebKey2);
        assertFalse(verifyJWS(token1), "expect invalid token1, wrong signature");
        assertFalse(verifyJWS(token2), "expect invalid token2, wrong signature");
    }
}

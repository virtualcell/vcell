package org.vcell.auth;

import cbit.vcell.resource.PropertyLoader;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.vcell.auth.JWTUtils.verifyJWS;

@Tag("Fast")
public class JWTUtilsTest {

    private static Path temp_publicKey_file;
    private static Path temp_privateKey_file;

    @BeforeAll
    public static void setup() throws IOException {
        //
        // temporary key pair files generated using the following commands:
        //
        //     openssl genpkey -algorithm RSA -out $PRIV_KEY_FILE_NAME -pkeyopt rsa_keygen_bits:2048
        //     openssl rsa -pubout -in $PRIV_KEY_FILE_NAME -out $PUB_KEY_FILE_NAME
        //
        String test_publicKey = """
                -----BEGIN PUBLIC KEY-----
                MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuumzGJQK9Ho8ZHkQ5mfS
                mpu1m9kd0eAr8B52Hz8QDHXiRSShN0e4vHfskjhUKdoY5y+IcKFQfiQ+X58jN6Pw
                i7Hx+tREQqON8EL1R5UAjfgBKNRK5QkeHzfGhBaV9+F1qV44RoMyH3JYRNhQV0Tu
                Zk+RO/w15UjQg3Y5nHv9gWtjXICr486WfS188HuHbaDIRuUBB5TL6hjh2Qf5baa/
                2exYeY624yUh9QbP/qXuHAnEXO5Hqti6GZnl5AZtSrT3XC8OAGa1zflCuE0dz2oN
                DYUDdCpYkXuJIQU1B8jyQGSV9UKBo+jzibTa2gEGff4SWjir3b0S246MGXpyie6J
                wQIDAQAB
                -----END PUBLIC KEY-----
                """;
        String test_privateKey = """
                -----BEGIN PRIVATE KEY-----
                MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC66bMYlAr0ejxk
                eRDmZ9Kam7Wb2R3R4CvwHnYfPxAMdeJFJKE3R7i8d+ySOFQp2hjnL4hwoVB+JD5f
                nyM3o/CLsfH61ERCo43wQvVHlQCN+AEo1ErlCR4fN8aEFpX34XWpXjhGgzIfclhE
                2FBXRO5mT5E7/DXlSNCDdjmce/2Ba2NcgKvjzpZ9LXzwe4dtoMhG5QEHlMvqGOHZ
                B/ltpr/Z7Fh5jrbjJSH1Bs/+pe4cCcRc7keq2LoZmeXkBm1KtPdcLw4AZrXN+UK4
                TR3Pag0NhQN0KliRe4khBTUHyPJAZJX1QoGj6POJtNraAQZ9/hJaOKvdvRLbjowZ
                enKJ7onBAgMBAAECggEACgtZ/6r2UGoAidOvB/qx/h36VfGGdotUlnbvQ8GYpAVy
                ZI3R3g3MIdCoqrjn3kc6nwiBDVsV327Pgpr4169x00f7ks5KYXG27x+Msnfm4OTg
                HGjAIkEUWHrKVQ8o/aT6NOD70Yo7rNCbvrizJJAZ5u9FWj209b6Cegtl5YC3laHv
                Q8ryW609uKciNINiJQ+gIErwzxgeYVSkgo4dixXfHEAGHyN83WncgD24gU3ch1me
                jK/UWYPqaOJKaXLgCchJbJirXDu5WRZIITjDoC8D63LBDj/uI19//EnKiamQV/HL
                tsqSTZc9ib3oIhr1FKPV62jv0mhmyY3S+yXKPKZDmQKBgQDxdbPenr7h8u9nYww9
                oWy+fAwmX554xftHIfFAHz1EfTFohPNvEomx4xn07aF8OWiMFJbyUAifckE1teo3
                QsL73DMqPct953rsQ98uYXAwixLA6RoH6olA/8d0nLgmPxoAWCVTU3SYX5DePWl/
                ftaujPsJ+R3nrJqDu4AZH4IQHQKBgQDGKxzYnMpObmOHkThiWLRSSzxoMST+RIuq
                704OvPRgiwkl+eSQNWYaIa7F76/VnKSxJ6n3bVRU5HnNC224mBCR1IPScWTdcKR1
                rLX7SCGEnlvJWG/Dr3DnpmmPRX1rLqCWtbkLXPQ1GdNoUwxAxYqeYjdNvfuzQt/J
                QCGfQBs29QKBgGLQTF/ajztgc+DXg/bkzN/XXBGIKA4812xw043uOuCAa2venNDj
                wT3vNnwpk2CHDeXltzBK2HyIAW+9lrAuviTcJIQw7coEpSVzUS5l6cez4mTYADMl
                JC5ZfoKWcDYWtMlXSU8kZB7HXT3V3Aq1GZ4tS771F+vrkwA+B1pArrVxAoGAOPJQ
                ssqztEAyp9WKf3CAORnXvRVKTStDe5aTNtcD3u51bY1XKj2+HopJYCYVwGvoQ+Rj
                CnBHiLGzcsHzANXqBQ4t536gfqYNDKfaD+DViPe85qpcS76R0m/W684LUY6vpT+J
                coN+xfeq8dLTtZVO1V7iB5wtxYDVME8Wml3gtDUCgYA9atRKNWcWU06FIKfuls2z
                42oLS1Gi76E8bdWtGuiLiVxH3CTYXVmLIq/RE6yKw27Q4F1XuNo/MUZcSpQ58oTO
                8eqtTXusQwc1tURy82g9K4HE1ppb5EVy9PXwhn3s7T9XkAfHUYyTrWIsJ1loC4s/
                4pPyagxT85vcy2aqdMwBFQ==
                -----END PRIVATE KEY-----
                """;

        // write keys to temp files
        temp_publicKey_file = Files.createTempFile("jwt_key_",".pub.pem");
        temp_privateKey_file = Files.createTempFile("jwt_key_",".pem");
        Files.writeString(temp_publicKey_file, test_publicKey);
        Files.writeString(temp_privateKey_file, test_privateKey);
        PropertyLoader.setProperty(PropertyLoader.vcellapiPublicKey, temp_publicKey_file.toString());
        PropertyLoader.setProperty(PropertyLoader.vcellapiPrivateKey, temp_privateKey_file.toString());
    }

    @AfterAll
    public static void cleanup() throws IOException {
        Files.deleteIfExists(temp_publicKey_file);
        Files.deleteIfExists(temp_privateKey_file);
    }

    @Test
    public void testJWT() throws JoseException, MalformedClaimException {
        // Generate an RSA key pair, which will be used for signing and verification of the JWT, wrapped in a JWK
        RsaJsonWebKey rsaJsonWebKey = JWTUtils.createNewJsonWebKey("k1");
        JWTUtils.setRsaJsonWebKey(rsaJsonWebKey);

        User user = new User.SpecialUser("testuser", new KeyValue("12345"), new User.SPECIAL_CLAIM[0]);


        NumericDate expirationDate = NumericDate.now();
        expirationDate.addSeconds(1);
        String token1 = JWTUtils.createLegacyAccessToken(user, expirationDate);
        assertTrue(verifyJWS(token1), "expect valid token");

        // test expiration date by setting the expiration date 31 seconds in the past (there is a 30 second tolerance)
        expirationDate = NumericDate.now();
        expirationDate.addSeconds(-31);
        String token2 = JWTUtils.createLegacyAccessToken(user, expirationDate);
        assertFalse(verifyJWS(token2), "expect timeout");

        // install a different JsonWebKey and see that both tokens are invalid
        RsaJsonWebKey rsaJsonWebKey2 = JWTUtils.createNewJsonWebKey("k2");
        JWTUtils.setRsaJsonWebKey(rsaJsonWebKey2);
        assertFalse(verifyJWS(token1), "expect invalid token1, wrong signature");
        assertFalse(verifyJWS(token2), "expect invalid token2, wrong signature");
    }

    @Test
    public void testJWTWithStoredKeys() throws JoseException, MalformedClaimException {
        // test with keys stored in files
        JWTUtils.setRsaJsonWebKey(null);
        User user = new User.SpecialUser("testuser", new KeyValue("12345"), new User.SPECIAL_CLAIM[0]);

        NumericDate expirationDate = NumericDate.now();
        expirationDate.addSeconds(1);
        String token1 = JWTUtils.createLegacyAccessToken(user, expirationDate);
        assertTrue(verifyJWS(token1), "expect valid token");

        // test expiration date by setting the expiration date 31 seconds in the past (there is a 30 second tolerance)
        expirationDate = NumericDate.now();
        expirationDate.addSeconds(-31);
        String token2 = JWTUtils.createLegacyAccessToken(user, expirationDate);
        assertFalse(verifyJWS(token2), "expect timeout");

        // install a different JsonWebKey and see that both tokens are invalid
        RsaJsonWebKey rsaJsonWebKey2 = JWTUtils.createNewJsonWebKey("k2");
        JWTUtils.setRsaJsonWebKey(rsaJsonWebKey2);
        assertFalse(verifyJWS(token1), "expect invalid token1, wrong signature");
        assertFalse(verifyJWS(token2), "expect invalid token2, wrong signature");
    }

    @Test
    public void testMagicLinkJWT() throws JoseException, MalformedClaimException, InvalidJwtException {
        // Generate an RSA key pair, which will be used for signing and verification of the JWT, wrapped in a JWK
        RsaJsonWebKey rsaJsonWebKey = JWTUtils.createNewJsonWebKey("k1");
        JWTUtils.setRsaJsonWebKey(rsaJsonWebKey);

        final String email = "myemail@company.com";
        final User user = new User("myuserid", new KeyValue("123455433"));
        final String requesterSubject = "myauth0subject";
        final String requesterIssuer = "myrequestorissuer";

        JWTUtils.MagicTokenClaims magicTokenClaims = new JWTUtils.MagicTokenClaims(email, requesterSubject, requesterIssuer, user);
        NumericDate expirationDate = NumericDate.now();
        expirationDate.addSeconds(JWTUtils.MAGIC_LINK_DURATION_SECONDS);

        // create magic link token
        String token = JWTUtils.createMagicLinkJWT(magicTokenClaims, JWTUtils.MAGIC_LINK_DURATION_SECONDS);
        assertTrue(JWTUtils.verifyJWS(token), "expect valid token");

        // decode magic link token
        JWTUtils.MagicTokenClaims claims = JWTUtils.decodeMagicLinkJWT(token);
        assertEquals(email, claims.email(), "expect email to match");
        assertEquals(requesterSubject, claims.requesterSubject(), "expect auth0subject to match");
        assertEquals(requesterIssuer, claims.requesterIssuer(), "expect auth0subject to match");
        assertEquals(claims.user().getName(), user.getName(), "expect user to match");
        assertEquals(claims.user().getID(), user.getID(), "expect user to match");
    }
}

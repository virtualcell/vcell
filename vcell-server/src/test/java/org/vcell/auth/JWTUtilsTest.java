package org.vcell.auth;

import cbit.vcell.resource.PropertyLoader;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        //     openssl genrsa -out $PRIV_KEY_FILE_NAME 4096
        //     openssl rsa -pubout -in $PRIV_KEY_FILE_NAME -out $PUB_KEY_FILE_NAME
        //
        String test_publicKey = """
-----BEGIN RSA PUBLIC KEY-----
MIIBigKCAYEA1YNWKVbjd+u6HOGo2AVPGHUUrgrXK/+ABquJWN0TTgez7eWUZFrd
Ghh5+GnOtW8bSZDWXxbbYZexIa2wKS8M6pj34vhgeqqdBWVIB1ynWE9IHDYm+ETP
gIjI1nv4TG8nvdA5pEMpp4JZj93TsG/wtdzquahsaIS5PW1v5EhNXcDNoqEdAWjW
0tPji2STgm7IPct+COd8WvFIU4V9Mfy+nHxx5nKokvUddQYEMcC3VY39Bqr/eDwL
eqzq92uXa0XX+uwPzZvbgGoCZPWljj1zzNvhe4v7SQQuREsDKY4a3ZKZVmQILd3A
SkKMcCf5KOjXhlAbbVnJ6Ps3KlMfY6TPOK1b7LBozTQ9XXmxV6yu/woQf/8iw2uC
pV3TBNYy9B21ZIeROTDT2EWeYCVUQSc7ibOPLfAFqjHU5KTCA7od8Qhc9JyA9qAh
ht18uwyOvhN4fNmMUcTJEat04GM599fMlSUs68AMSu2dXaGPwoSQ8SYP2Uc1oQVP
mx8UEBAn8Bj9AgMBAAE=
-----END RSA PUBLIC KEY-----
""";
        String test_privateKey = """
-----BEGIN RSA PRIVATE KEY-----
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
-----END RSA PRIVATE KEY-----
""";
        // create a temp file
        temp_publicKey_file = Files.createTempFile("jwt_key_",".pub.pem");
        temp_privateKey_file = Files.createTempFile("jwt_key_",".pem");
        Files.writeString(temp_publicKey_file, test_publicKey);
        Files.writeString(temp_privateKey_file, test_privateKey);
        PropertyLoader.setProperty(PropertyLoader.vcellapiPublicKey, temp_publicKey_file.toString());
        PropertyLoader.setProperty(PropertyLoader.vcellapiPrivateKey, temp_privateKey_file.toString());

        JWTUtils.setRsaPublicAndPrivateKey();
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

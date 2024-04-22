package org.vcell.auth;

import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.ErrorCodes;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.vcell.util.document.User;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class JWTUtils {

    private final static Logger log = LogManager.getLogger(JWTUtils.class);

    public final static int MAX_JWT_SIZE_BYTES = 4000;
    private final static Logger lg = LogManager.getLogger(JWTUtils.class);
    private static RsaJsonWebKey rsaJsonWebKey = null;

    public static String VCELL_JWT_AUDIENCE = "VCellService";
    public static String VCELL_JWT_ISSUER = "VCellService";

    public static void setRsaJsonWebKey(RsaJsonWebKey rsaJsonWebKey) {
        JWTUtils.rsaJsonWebKey = rsaJsonWebKey;
    }


    synchronized static void createRsaJsonWebKeyIfNeeded() throws JoseException {
        if (rsaJsonWebKey != null) {
            return;
        }
        final String privateKeyFilePath = PropertyLoader.getProperty(PropertyLoader.vcellapiPrivateKey, null);
        final String publicKeyFilePath = PropertyLoader.getProperty(PropertyLoader.vcellapiPublicKey, null);
        if (privateKeyFilePath == null || publicKeyFilePath == null) {
            log.warn("No public/private key files found. Generating new keys.");
            setRsaJsonWebKey(createNewJsonWebKey("k1"));
        }
        try {
            // Read public key from file
            FileReader reader = new FileReader(publicKeyFilePath);
            PEMParser pemParser = new PEMParser(reader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(new BouncyCastleProvider());
            PublicKey publicKey = converter.getPublicKey((org.bouncycastle.asn1.x509.SubjectPublicKeyInfo) pemParser.readObject());

            // Read private key from file
            reader = new FileReader(privateKeyFilePath);
            pemParser = new PEMParser(reader);
            PrivateKey privateKey = converter.getPrivateKey((org.bouncycastle.asn1.pkcs.PrivateKeyInfo) pemParser.readObject());

            // Create RsaJsonWebKey
            RsaJsonWebKey rsaJsonWebKey = new RsaJsonWebKey((java.security.interfaces.RSAPublicKey) publicKey);
            rsaJsonWebKey.setPrivateKey(privateKey);

            rsaJsonWebKey.setKeyId("k1");

            JWTUtils.rsaJsonWebKey = rsaJsonWebKey;
        } catch (IOException e) {
            throw new RuntimeException("Error reading public/private key files", e);
        }
    }

    public static RsaJsonWebKey createNewJsonWebKey(String keyId) throws JoseException {
        // Generate an RSA key pair, which will be used for signing and verification of the JWT, wrapped in a JWK
        RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);

        // Give the JWK a Key ID (kid), which is just the polite thing to do
        rsaJsonWebKey.setKeyId(keyId);
        return rsaJsonWebKey;
    }

    public static String createToken(User user, NumericDate expirationTime) throws JoseException {
        createRsaJsonWebKeyIfNeeded();

        // Create the Claims, which will be the content of the JWT
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(VCELL_JWT_ISSUER);  // who creates the token and signs it
        claims.setAudience(VCELL_JWT_AUDIENCE); // to whom the token is intended to be sent
//        claims.setExpirationTimeMinutesInTheFuture(10); // time when the token will expire (10 minutes from now)
        claims.setExpirationTime(expirationTime); // time when the token will expire)
        claims.setGeneratedJwtId(); // a unique identifier for the token
        claims.setIssuedAtToNow();  // when the token was issued/created (now)
        claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
        claims.setSubject(user.toSubject()); // the subject/principal is whom the token is about
        if (user instanceof User.SpecialUser){
            User.SPECIAL_CLAIM[] specialClaims = ((User.SpecialUser)user).getMySpecials();
            if (specialClaims != null && specialClaims.length>0){
                List<String> groups = Arrays.stream(specialClaims).map(specialClaim -> specialClaim.name()).collect(Collectors.toList());
                claims.setStringListClaim("groups", groups); // multi-valued claims will end up as a JSON array
            }
        }
//        claims.setClaim("email","mail@example.com"); // additional claims/attributes about the subject can be added

        // A JWT is a JWS and/or a JWE with JSON claims as the payload.
        // Here we prefer to use JWS (unencrypted claims/payload with signature) so that it is easier to debug/understand.
        JsonWebSignature jws = new JsonWebSignature();

        // The payload of the JWS is JSON content of the JWT Claims
        jws.setPayload(claims.toJson());

        // The JWT is signed using the private key
        jws.setKey(rsaJsonWebKey.getRsaPrivateKey());

        // Set the Key ID (kid) header because it's just the polite thing to do.
        // We only have one key in this example but a using a Key ID helps
        // facilitate a smooth key rollover process
//        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());

        // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        // Sign the JWS and produce the compact serialization or the complete JWT/JWS
        // representation, which is a string consisting of three dot ('.') separated
        // base64url-encoded parts in the form Header.Payload.Signature
        // If you wanted to encrypt it, you can simply set this jwt as the payload
        // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
        String jwt = jws.getCompactSerialization();
        if (jwt.getBytes(StandardCharsets.UTF_8).length > MAX_JWT_SIZE_BYTES){
            throw new RuntimeException("VCells tokens have a max size of "+MAX_JWT_SIZE_BYTES+" bytes");
        }
        return jwt;
    }

    public static boolean verifyJWS(String jwt) throws MalformedClaimException, JoseException {
        createRsaJsonWebKeyIfNeeded();

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime() // the JWT must have an expiration time
                .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
                .setRequireSubject() // the JWT must have a subject claim
                .setExpectedIssuer(VCELL_JWT_ISSUER) // whom the JWT needs to have been issued by
                .setExpectedAudience(VCELL_JWT_AUDIENCE) // to whom the JWT is intended for
                .setVerificationKey(rsaJsonWebKey.getPublicKey()) // verify the signature with the public key
                .setJwsAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
                        AlgorithmConstraints.ConstraintType.PERMIT, AlgorithmIdentifiers.RSA_USING_SHA256) // which is only RS256 here
                .build(); // create the JwtConsumer instance

        try
        {
            //  Validate the JWT and process it to the Claims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
            lg.debug("JWT validation succeeded! " + jwtClaims);
            return true;
        }
        catch (InvalidJwtException e)
        {
            // InvalidJwtException will be thrown, if the JWT failed processing or validation in any way.
            // Hopefully with meaningful explanations(s) about what went wrong.
            lg.warn("Invalid JWT! ", e);

            // Programmatic access to (some) specific reasons for JWT invalidity is also possible
            // should you want different error handling behavior for certain conditions.

            // Whether or not the JWT has expired being one common reason for invalidity
            if (e.hasExpired())
            {
                lg.info("JWT expired at " + e.getJwtContext().getJwtClaims().getExpirationTime());
            }

            // Or maybe the audience was invalid
            if (e.hasErrorCode(ErrorCodes.AUDIENCE_INVALID))
            {
                lg.info("JWT had wrong audience: " + e.getJwtContext().getJwtClaims().getAudience());
            }
            return false;
        }
    }

}

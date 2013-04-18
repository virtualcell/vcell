
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;


public class JWTTest {
	static RSAPrivateKey privateKey = null;
	static RSAPublicKey publicKey = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			generateKeys();
			testJWS();
			testJWE();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	private static void generateKeys() throws NoSuchAlgorithmException{
		System.out.println("\n\ngenerateKeys()\n\n");
		KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
//		gen.initialize(2048);
		gen.initialize(1024);
		KeyPair key = gen.generateKeyPair();
		privateKey = (RSAPrivateKey)key.getPrivate();
		publicKey = (RSAPublicKey)key.getPublic();

	}
	
	private static void testJWE() throws JOSEException, ParseException, NoSuchAlgorithmException{
		System.out.println("\n\ntestJWE()\n\n");
		
		// Compose the JWT claims set
		JWTClaimsSet jwtClaims = new JWTClaimsSet();
		jwtClaims.setIssuer("https://openid.net");
		jwtClaims.setSubject("alice");
		List<String> aud = new ArrayList<String>();
		aud.add("https://app-one.com");
		aud.add("https://app-two.com");
		jwtClaims.setAudience(aud);
		// Set expiration in 10 minutes
		jwtClaims.setExpirationTime(new Date(new Date().getTime() + 1000*60*10));
		jwtClaims.setNotBeforeTime(new Date());
		jwtClaims.setIssueTime(new Date());
		jwtClaims.setJWTID(UUID.randomUUID().toString());

		System.out.println(jwtClaims.toJSONObject());
		// Produces 
		// { 
		//   "iss" : "https:\/\/openid.net",
		//   "sub" : "alice",
		//   "aud" : [ "https:\/\/app-one.com" , "https:\/\/app-two.com" ],
		//   "exp" : 1364293137871,
		//   "nbf" : 1364292537871,
		//   "iat" : 1364292537871,
		//   "jti" : "165a7bab-de06-4695-a2dd-9d8d6b40e443"
		// }

		// Request JWT encrypted with RSA-OAEP and 128-bit AES/GCM
		JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128GCM);

		// Create the encrypted JWT object
		EncryptedJWT jwt = new EncryptedJWT(header, jwtClaims);

		// Create an encrypter with the specified public RSA key
		RSAEncrypter encrypter = new RSAEncrypter(publicKey);

		// Do the actual encryption
		jwt.encrypt(encrypter);

		// Serialise to JWT compact form
		String jwtString = jwt.serialize();

		System.out.println(jwtString);
		// Produces 
		//
		// eyJhbGciOiJSU0EtT0FFUCIsImVuYyI6IkExMjhHQ00ifQ.K52jFwAQJH-
		// DxMhtaq7sg5tMuot_mT5dm1DR_01wj6ZUQQhJFO02vPI44W5nDjC5C_v4p
		// W1UiJa3cwb5y2Rd9kSvb0ZxAqGX9c4Z4zouRU57729ML3V05UArUhck9Zv
		// ssfkDW1VclingL8LfagRUs2z95UkwhiZyaKpmrgqpKX8azQFGNLBvEjXnx
		// -xoDFZIYwHOno290HOpig3aUsDxhsioweiXbeLXxLeRsivaLwUWRUZfHRC
		// _HGAo8KSF4gQZmeJtRgai5mz6qgbVkg7jPQyZFtM5_ul0UKHE2y0AtWm8I
		// zDE_rbAV14OCRZJ6n38X5urVFFE5sdphdGsNlA.gjI_RIFWZXJwaO9R.oa
		// E5a-z0N1MW9FBkhKeKeFa5e7hxVXOuANZsNmBYYT8G_xlXkMD0nz4fIaGt
		// uWd3t9Xp-kufvvfD-xOnAs2SBX_Y1kYGPto4mibBjIrXQEjDsKyKwndxzr
		// utN9csmFwqWhx1sLHMpJkgsnfLTi9yWBPKH5Krx23IhoDGoSfqOquuhxn0
		// y0WkuqH1R3z-fluUs6sxx9qx6NFVS1NRQ-LVn9sWT5yx8m9AQ_ng8MBWz2
		// BfBTV0tjliV74ogNDikNXTAkD9rsWFV0IX4IpA.sOLijuVySaKI-FYUaBy
		// wpg


		// Parse back
		jwt = EncryptedJWT.parse(jwtString);

		// Create a decrypter with the specified private RSA key
		RSADecrypter decrypter = new RSADecrypter(privateKey);

		// Decrypt
		jwt.decrypt(decrypter);

		// Retrieve JWT claims
		System.out.println("       issuer: "+jwt.getJWTClaimsSet().getIssuer());;
		System.out.println("      subject: "+jwt.getJWTClaimsSet().getSubject());
		System.out.println(" audienceSize: "+jwt.getJWTClaimsSet().getAudience().size());
		System.out.println("    expirTime: "+jwt.getJWTClaimsSet().getExpirationTime());
		System.out.println("notBeforeTime: "+jwt.getJWTClaimsSet().getNotBeforeTime());
		System.out.println("    issueTime: "+jwt.getJWTClaimsSet().getIssueTime());
		System.out.println("       issuer: "+jwt.getJWTClaimsSet().getJWTID());
	}
	
	private static void testJWS() throws JOSEException, ParseException{
		System.out.println("\n\ntestJWS()\n\n");
		// Create JWS payload
		Payload payload = new Payload("Hello world!");
		                
		// Create JWS header with HS256 algorithm
		JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
		header.setContentType("text/plain");
		                
		// Create JWS object
		JWSObject jwsObject = new JWSObject(header, payload);
		                
		// Create HMAC signer
		String sharedKey = "a0a2abd8-6162-41c3-83d6-1cf559b46afc";
		                
		JWSSigner signer = new MACSigner(sharedKey.getBytes());
		jwsObject.sign(signer);
		                        
		// Serialise JWS object to compact format
		String s = jwsObject.serialize();
		System.out.println("Serialised JWS object: " + s);
		                
		// Parse back and check signature
		jwsObject = JWSObject.parse(s);

		JWSVerifier verifier = new MACVerifier(sharedKey.getBytes());
		                
		boolean verifiedSignature = jwsObject.verify(verifier);

		if (verifiedSignature)
		    System.out.println("Verified JWS signature!");
		else
		    System.out.println("Bad JWS signature!");

		System.out.println("Recovered payload message: " + jwsObject.getPayload());

	}

}

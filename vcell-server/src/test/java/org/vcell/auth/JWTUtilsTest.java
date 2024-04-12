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
                -----BEGIN PUBLIC KEY-----
                MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEA4NPq1dC2eB6O+5W20RvU
                K2t9b8V75C7jSvfUl2HGD72s6DEIfFCtO53NesLLlhiT/MyiD5z8PMEZ/gP8KgQI
                LsolyX2SFotGki2gRxOP1eN39ddVG2Fu5TbqIWvxw6p5GIaV0RBnUmNrHfCXaAbj
                QbzDT4rUI7WA/vVabTfMfjDvZUvisCH6KFMKic9xJ9lUuRnA2Ji2zeGLwxdFlqOy
                0BBtYP0vhAzVWp/Jz2WU6o87catDxq4sttqT7wIWzJYP6qfx2FYcW0KNLOlZLXih
                BxapgCVVmwsK9dp58gKy3SAxLaf4Faoi80X8igKeoSQBLqCd/mNjq/DyZKeOsjkr
                qtqtEi6dZexS1OdVl1wRqPutOXzMQdhHxvBgGypP6MR6VdI1/NEEk5F6Trxl5cUB
                twllLwZiEtgZ4KUlowcQNkGGWAKNX7G81K+h8yFDr8kYLewZ/W8ja7mTYwPMtVLr
                Kwq5CYenpDmnR84dG0M9oFp6w0bCVJJxOvmQAd5/a3K0MIM2Vg4A6RP/09dxkGe0
                eRmUi+36qgMarv2Zfs5G75jOUq/5NkfFfLk/6brukoj6WaWCelZAks+kfRMcIr63
                jCrJQ8g624W6ESMnJVJO5uOf+NjTWQa7KDrMD9AomTaPunEAxWeo50ErwM/mY8dx
                m/UJmMLZhGxm5WFQwt76GUkCAwEAAQ==
                -----END PUBLIC KEY-----
                """;
        String test_privateKey = """
                -----BEGIN PRIVATE KEY-----
                MIIJQgIBADANBgkqhkiG9w0BAQEFAASCCSwwggkoAgEAAoICAQDg0+rV0LZ4Ho77
                lbbRG9Qra31vxXvkLuNK99SXYcYPvazoMQh8UK07nc16wsuWGJP8zKIPnPw8wRn+
                A/wqBAguyiXJfZIWi0aSLaBHE4/V43f111UbYW7lNuoha/HDqnkYhpXREGdSY2sd
                8JdoBuNBvMNPitQjtYD+9VptN8x+MO9lS+KwIfooUwqJz3En2VS5GcDYmLbN4YvD
                F0WWo7LQEG1g/S+EDNVan8nPZZTqjztxq0PGriy22pPvAhbMlg/qp/HYVhxbQo0s
                6VkteKEHFqmAJVWbCwr12nnyArLdIDEtp/gVqiLzRfyKAp6hJAEuoJ3+Y2Or8PJk
                p46yOSuq2q0SLp1l7FLU51WXXBGo+605fMxB2EfG8GAbKk/oxHpV0jX80QSTkXpO
                vGXlxQG3CWUvBmIS2BngpSWjBxA2QYZYAo1fsbzUr6HzIUOvyRgt7Bn9byNruZNj
                A8y1UusrCrkJh6ekOadHzh0bQz2gWnrDRsJUknE6+ZAB3n9rcrQwgzZWDgDpE//T
                13GQZ7R5GZSL7fqqAxqu/Zl+zkbvmM5Sr/k2R8V8uT/puu6SiPpZpYJ6VkCSz6R9
                ExwivreMKslDyDrbhboRIyclUk7m45/42NNZBrsoOswP0CiZNo+6cQDFZ6jnQSvA
                z+Zjx3Gb9QmYwtmEbGblYVDC3voZSQIDAQABAoICAAh9YB0GVlmXV9FewCpJyXuB
                QdmWZL0DW0BhtuSng8Xk5PJYpagiGMSLSy7TS8JFd9MgPA9oX2sA5LUfm+ZPRCrW
                JdKjvAA/bqEG+Yotji0mu4ksQjG4PmqH5hPwgk7f8VYwgNhF7QzipbEZ/HGobuz0
                zpBCnwuhJzeZdrpwlLeALA4QbUU25z4eEqQNEgoN7iHWO97B94czOOabSpyrXk12
                jVbQALsVC/px1bpP7O1ljrpWXAcU3Snje5oXI1rC4ON8x4hiigwVKYUUY7Jwn24H
                2Es3PbIO2vFZOGkf2na4qDq/4MGarNqwXh6tBiXyGNxUotqwEJu1y8VqwVvwBjrJ
                i/V//tEOU93ydU/STVyMBgrWLN/jTXFC6EjRe7dyC8bSV/9w8xLp7E1lqE11OOhp
                QOBpEspogSo7qjg6G0cZrugzYEVrHki6EIqjuShlsNKTLqcQjjIP7Rv3KwQZa7k8
                H+7vePN5IB7Mi+OlfsE0RZNaMSFas8aaOHXoJgC++ObC7gkGOYPh4t7g2ktw9Ga7
                lpOtMgGw31iLe0McmynTNEiSbTrRZiUBS2ErpXMFKkucukh860rQUMcHQNd2Nmmc
                /SXADoqxNT8c554OQVNMkHVfbBLDJLYTfMD4fQl1NjT9h4/Kd7yycTsd/dWOjRrG
                8bVCXt1uexKdSaYiFmuBAoIBAQDytlYl9q0VP/o8O9UhHuporkV4tUOhNiYnfN4u
                lJSj8CsbodR9gIfGo5vkiCzvcMwnNmuelC0rI1H84c/uZplGDubZqcbZkCS2BYGa
                Q2S5wpINDvPiF6ztSVuPuHzrLQywsk2lHF4K3AF5GWK4FtTBJy8+WksJlC4OVjRi
                15tpmszoTuby7YaDre/OTA3qb1v53tJ0wIn7Yx745hdzOPUQf2b00GHHaxSMOiMC
                +SR18VEyd3JTpa5LvXMTQ35LMASD4NKr2I+9HpFDE+IftCqHnQfhAzto3G4W3Nrg
                Vod+pzxUUp1VlGDNneK3sH208g2n7zlqSjwF+9gdsvoCHXFBAoIBAQDtIu0svyEz
                dTOmImSts4eHEI2ihQABz/W6emzgXfOCJIvIn2dNv0hIMP+nt77vVnUYTmaEaW2m
                Lg6BvJTrCvwucC/h9Gb4OLP5K2iuaKFztHC1W3Cp9SgDvmPNCx2KwEz4H9Ak3SgF
                TZI+Sfc+OYYorAldAAtp+MScf28G0u8mLYLCakVC5du1MpjixsaOWsq7LmQuVwYa
                l4dzRwKCuRv8E+CC7pC7UhBOSsBugCHZfMJPuj6VDcu0BVAUfzfNg3Y7UwRX6X+c
                B2/TYSJ4d8QNNi3HsNs1ZL40tcX+OXfETfN4twUGka0lpE8Vwj4QXGcDUpLacpjs
                BEUCazvyS54JAoIBAEh/nowIevr1+VE0rh0OlwZdx/Kecm0dN5hj/Bn918iAXOAM
                HuS+F9CF3nMNGh8Lmm+8GblsmXFhQ4Q+D1Yu32nF2b76r6MdI1obmvLxXoz+SZIM
                /VxzpfvbBbaQbP/ynO/K0taW+qBTxzzdEFfcugBYLAx7/B4IXRl7Nnqv3IRW8Q4T
                Q3kc6ik7M35zyVRVnyusG0E0ogKtQPsn/6kzQkCDTcWi2RIwMhsQ4pRoxzNTueVR
                OJ0zyDRnipAegQMEfNTHuyYJsiXTk6BtuAWwU2PezBOyW3SfI2NA5UBNmHuEiMjH
                RKfjgmbo2mf0GBiMlPesxYGyWuuOpqd4ZqVrFgECggEBAMfDLFkNiUJ38NJ7FqTd
                H8Y/w5CsyZy9OxukFiPMAV4Yu0h+P9/Wy2VFIZh/yXCFPI8FPIc+6QectMvFuQ1n
                a5kTGMMXx8ZCvhmg38ZMjfWSg3/DT91ckCDXOet4+aHw7OX4prbnNteAyTl/xa+9
                da69fgXCC9txnO7YywbMmnlmzUhJjZ8VU0gJH5GqMYmqIi4cnngLBzFPhOMvZoYI
                ufJYLEEgBvxF3UaW4hQ0DE/SMT5Ifn/B51/3OcaKhM+zdt3yyS3lnYIrIaDHquog
                uh8HDPmzmnYYL8LnfarzblkH8Zo3Uxx9i4ayK40A3Jebv4uk++DZuMjZr+ihvRYX
                bUECggEAa9JvVqbw61yyRpVXOBTc3m8sfPw7KmV1FpueLjqssn/bJrgHkT1Rypei
                IGeoUtIlYYUhAB854cxzR2UeeAcHLDvnT7YUgp8UckX0WTEw7h18kQ19AB6zqZg9
                gnT/5je7SRZJzrt5Q74EZ7GbLTaJ9e7oVCo1oQdoKgaqm5ijCOGAuvUOH/VecxYB
                0bUFXwollHaiLNPeKU2aXjezfPfKN74Jp7NNixXZhgYoidsWjd0jeRSAV84K9XAL
                1PWewKw9c/It7PUxwY3/hCCzsBuxnlZdgmeL+y7tqlb+12cgIwhlS0QH1xV1KLH3
                PSowPcjIsm0ZcArYcnpoquNbsU04Eg==
                -----END PRIVATE KEY-----
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

package org.vcell.restclient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.cert.X509Certificate;

public class CustomApiClientCode {
    public static HttpClient.Builder createInsecureHttpClientBuilder() {
        try {
            HttpClient.Builder customBuilder = HttpClient.newBuilder();
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                    }
            };

            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            customBuilder.sslContext(sslContext);

            // Create an HttpClient that uses the custom SSLContext
            return customBuilder;
        } catch (Exception e) {
            throw new RuntimeException("failed to create custom HttpClient: " + e.getMessage(), e);
        }
    }
}

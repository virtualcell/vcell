package org.vcell.util.network;

import cbit.vcell.resource.PropertyLoader;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.Duration;

public class ClientDownloader {

    public static String downloadBytes(final URL url, Duration timeout) {
        try {
            boolean bIgnoreHostMismatch = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreHostMismatch, false);
            boolean bIgnoreCertProblems = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreCertProblems, false);
            boolean ignoreCertProblems = bIgnoreCertProblems || bIgnoreHostMismatch;

            HttpClient client;
            if (ignoreCertProblems) {
                // Create a trust manager that does not validate certificate chains
                TrustManager[] trustAllCerts = new TrustManager[] {
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                            public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                            public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                        }
                };

                // Install the all-trusting trust manager
                SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

                // Create an HttpClient that uses the custom SSL context
                client = HttpClient.newBuilder()
                        .sslContext(sslContext)
                        .build();
            } else {
                // Use default SSL verification
                client = HttpClient.newHttpClient();
            }

            // Send a GET request
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder().uri(url.toURI()).timeout(timeout).GET().build();
            java.net.http.HttpResponse<String> response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (NoSuchAlgorithmException | KeyManagementException | URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

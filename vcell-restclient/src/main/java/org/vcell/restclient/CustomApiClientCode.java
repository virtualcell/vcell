package org.vcell.restclient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.security.cert.X509Certificate;

public class CustomApiClientCode {

    /**
     * issue #2051 - restore the retry behaviour the desktop client had
     * before its save/delete calls were migrated from /api/v0 to /api/v1.
     *
     * Those calls used to go through Apache HttpClient 4 over HTTP/1.1, whose default
     * DefaultHttpRequestRetryHandler transparently retried a request sent into a connection
     * the server had already recycled. The generated client uses java.net.http, which
     * defaults to HTTP/2, and there:
     *
     *   - a GOAWAY surfaces as a plain IOException, NOT a ConnectionExpiredException, so
     *     MultiExchange.retryOnFailure() rejects it before idempotency is even considered -
     *     nothing is retried, whatever the method;
     *   - nginx sends GOAWAY as routine housekeeping (keepalive_requests 1000), so an
     *     ordinary long session hits it.
     *
     * The result was a failed "save biomodel" with no server-side fault. Two changes together
     * restore the old semantics, and NEITHER is sufficient alone:
     *
     *   1. HTTP/1.1, so a recycled connection raises ConnectionExpiredException, which
     *      java.net.http does consider retryable;
     *   2. jdk.httpclient.enableAllMethodRetry, because otherwise only GET and HEAD are
     *      retried - and every affected call is a POST or a DELETE.
     *
     * TEMPORARY. JDK 24 added retryAsUnprocessed/isUnprocessedByPeer, which retries streams a
     * GOAWAY reports the peer never processed, regardless of method - the correct HTTP/2
     * behaviour and a better fix than this one. Once VCell ships on Java 24+, delete this and
     * let the JDK handle it. HttpClientRetryWorkaroundTest fails on Java 24+ to make sure that
     * happens rather than being forgotten.
     */
    static {
        // read once into a static final in MultiExchange, so it must be set before the first
        // HttpClient is built - which is why it lives here, on the path every client takes.
        if (System.getProperty("jdk.httpclient.enableAllMethodRetry") == null) {
            System.setProperty("jdk.httpclient.enableAllMethodRetry", "true");
        }
    }

    /** True while the HTTP/1.1 + all-method-retry workaround is still needed (see above). */
    public static boolean isRetryWorkaroundNeeded() {
        return Runtime.version().feature() < 24;
    }

    /**
     * The builder every VCell ApiClient should use.
     *
     * @param ignoreSSLCertProblems trust any certificate (test sites with self-signed certs)
     */
    public static HttpClient.Builder createHttpClientBuilder(boolean ignoreSSLCertProblems) {
        HttpClient.Builder builder = ignoreSSLCertProblems
                ? createInsecureHttpClientBuilder()
                : HttpClient.newBuilder();
        if (isRetryWorkaroundNeeded()) {
            builder.version(HttpClient.Version.HTTP_1_1);
        }
        return builder;
    }

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

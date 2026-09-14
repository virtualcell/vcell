package org.vcell.restclient;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Guards the issue #2051 workaround, and — deliberately — FAILS once it is no longer needed.
 *
 * Background. The desktop client's save and delete calls moved from /api/v0 (Apache
 * HttpClient 4, HTTP/1.1) to /api/v1 (java.net.http, HTTP/2) when those endpoints were
 * migrated. The old stack retried a request sent into a connection the server had already
 * recycled; the new one does not, so nginx's routine GOAWAY (keepalive_requests 1000) began
 * surfacing to users as a failed save. 13 write operations are affected — every save and
 * every delete the client performs.
 *
 * The workaround is HTTP/1.1 plus jdk.httpclient.enableAllMethodRetry. Both are required:
 * on HTTP/2 a GOAWAY arrives as a plain IOException, which MultiExchange.retryOnFailure()
 * rejects before idempotency is considered, so the flag alone changes nothing; and on
 * HTTP/1.1 without the flag only GET and HEAD are retried, so a POST still fails.
 *
 * Why this test expires. JDK 24 added retryAsUnprocessed / Stream.isUnprocessedByPeer(),
 * which retries streams a GOAWAY reports the peer never processed — regardless of method.
 * That is the correct HTTP/2 behaviour and strictly better than forcing HTTP/1.1 on every
 * call. So when VCell ships on Java 24+ the workaround should be REMOVED, and this test
 * fails at that point to make that a decision rather than an oversight.
 *
 * Verified against the JDK sources: absent in 17, 21 and 23; present in 24 and 25.
 */
@Tag("Fast")
public class HttpClientRetryWorkaroundTest {

    /** The release that made the workaround unnecessary. */
    private static final int JDK_WITH_UNPROCESSED_RETRY = 24;

    /**
     * The Java version VCell SHIPS (from maven.compiler.target, passed by surefire), not the
     * JDK this test happens to run on. Keying the reminder off the running JDK would fail the
     * build for any developer on a newer one while CI, on the shipped version, stayed green.
     */
    private static int shippedJavaTarget() {
        String v = System.getProperty("vcell.shipped.java.target");
        return v == null ? Runtime.version().feature() : Integer.parseInt(v.trim());
    }

    @Test
    public void removeTheWorkaroundOnceVCellShipsJava24OrLater() {
        int shipped = shippedJavaTarget();
        if (shipped >= JDK_WITH_UNPROCESSED_RETRY) {
            fail("VCell now ships Java " + shipped + ". Since Java " + JDK_WITH_UNPROCESSED_RETRY
                    + " the JDK retries streams a GOAWAY reports as unprocessed, regardless of HTTP "
                    + "method — the correct HTTP/2 behaviour, and better than forcing HTTP/1.1 on "
                    + "every call.\n\n"
                    + "REMOVE the workaround in CustomApiClientCode (the HTTP_1_1 pin, the "
                    + "jdk.httpclient.enableAllMethodRetry static block, and isRetryWorkaroundNeeded), "
                    + "put both call sites back to configuring the builder only for certificates, "
                    + "and delete this test. See issue #2051.\n\n"
                    + "Re-test a real save against a deployed server first: the Java 24+ conclusion "
                    + "was read from the JDK source, not observed.");
        }
    }

    @Test
    public void theClientMatchesWhatTheWorkaroundClaims() {
        // isRetryWorkaroundNeeded() keys off the RUNNING JDK, which is right: a developer on
        // Java 24+ already has the JDK's own fix and should not be pinned to HTTP/1.1.
        boolean needed = CustomApiClientCode.isRetryWorkaroundNeeded();
        assertEquals(Runtime.version().feature() < JDK_WITH_UNPROCESSED_RETRY, needed,
                "the workaround should report itself needed exactly on JDKs below "
                        + JDK_WITH_UNPROCESSED_RETRY);

        HttpClient secure = CustomApiClientCode.createHttpClientBuilder(false).build();
        HttpClient insecure = CustomApiClientCode.createHttpClientBuilder(true).build();
        if (needed) {
            assertEquals(HttpClient.Version.HTTP_1_1, secure.version(),
                    "HTTP/2 GOAWAY is never retried, so the client must speak HTTP/1.1 (issue #2051)");
            assertEquals(HttpClient.Version.HTTP_1_1, insecure.version(),
                    "the ignore-certificates client must carry the same workaround");
            assertEquals("true", System.getProperty("jdk.httpclient.enableAllMethodRetry"),
                    "only GET and HEAD are retried without this, and every affected call is a "
                            + "POST or a DELETE");
        } else {
            assertEquals(HttpClient.Version.HTTP_2, secure.version(),
                    "on a JDK that retries unprocessed streams, leave HTTP/2 alone");
        }
    }
}

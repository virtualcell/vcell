package org.vcell.restclient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.vcell.restclient.api.BioModelResourceApi;

import java.io.ByteArrayInputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Regression test for VCML text being re-encoded once per open-and-save on Windows.
 * <p>
 * vcell-rest sends and accepts VCML as UTF-8. {@link CustomObjectMapper} - the mapper
 * {@code AuthApiClient} installs after login - decoded every String response body with the
 * JVM default charset. That is UTF-8 on macOS and Linux but windows-1252 on Windows (Java 17),
 * so a Windows client turned each non-ASCII character of a loaded model into 2-3 characters of
 * mojibake, then sent those back UTF-8 encoded on save: the text roughly doubled per cycle, and a
 * 12.4 MB model with a micro-sign-laden annotation eventually produced 25.7 MiB save bodies.
 * <p>
 * The default charset cannot be changed after JVM start, so this class only means something in a
 * JVM launched with {@code -Dfile.encoding=windows-1252}: the {@code windows-1252-default-charset}
 * surefire execution in this module's pom does exactly that, and sets
 * {@code vcell.test.windows1252Fork} so a misconfigured fork fails here instead of passing
 * vacuously. Run anywhere else (an IDE, the default execution) it is skipped.
 */
@Tag("Fast")
public class CustomObjectMapperCharsetTest {

    /** The annotation that was corrupted in the field, plus a few other non-ASCII scripts. */
    static final String TEXT = "typically spanning from 10⁻⁶ to 10⁻³ μm²/s "
            + "(10⁻¹⁸ to 10⁻¹⁵ m²/s) — café, Ångström, 中文";
    static final String VCML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<vcml><BioModel Name=\"charset\"><freetext>" + TEXT + "</freetext></BioModel></vcml>\n";

    private HttpServer server;
    private final AtomicReference<byte[]> stored = new AtomicReference<>();
    private final AtomicReference<byte[]> lastSaveBody = new AtomicReference<>();

    @BeforeEach
    void requireWindows1252Default() throws Exception {
        boolean inFork = Boolean.getBoolean("vcell.test.windows1252Fork");
        boolean cp1252 = Charset.defaultCharset().name().equalsIgnoreCase("windows-1252");
        if (inFork) {
            assertEquals("windows-1252", Charset.defaultCharset().name(),
                    "the windows-1252 surefire fork did not get -Dfile.encoding=windows-1252");
        } else {
            assumeTrue(cp1252, "needs a JVM started with -Dfile.encoding=windows-1252 (see the module pom)");
        }

        // A stand-in for vcell-rest: GET returns the stored VCML as UTF-8 text/xml, POST stores the
        // request body bytes verbatim and echoes them back, as BioModelResource.save does.
        stored.set(VCML.getBytes(StandardCharsets.UTF_8));
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/v1/bioModel", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                byte[] body = exchange.getRequestBody().readAllBytes();
                lastSaveBody.set(body);
                stored.set(body);
                exchange.getResponseHeaders().add("Content-Type", "application/xml;charset=UTF-8");
            } else {
                exchange.getResponseHeaders().add("Content-Type", "text/xml;charset=UTF-8");
            }
            byte[] out = stored.get();
            exchange.sendResponseHeaders(200, out.length);
            exchange.getResponseBody().write(out);
            exchange.close();
        });
        server.start();
    }

    @AfterEach
    void stopServer() {
        if (server != null) server.stop(0);
    }

    @Test
    void stringBodiesAreDecodedAsUtf8() throws Exception {
        CustomObjectMapper mapper = new CustomObjectMapper();
        byte[] utf8 = VCML.getBytes(StandardCharsets.UTF_8);
        assertEquals(VCML, mapper.readValue(new ByteArrayInputStream(utf8), String.class));
        assertEquals(VCML, mapper.readValue(new ByteArrayInputStream(utf8), new TypeReference<String>() {}));
    }

    /** Open-and-save, three times over, through the generated API exactly as the desktop client does. */
    @Test
    void openAndSaveCyclesDoNotGrowTheDocument() throws Exception {
        ApiClient apiClient = new ApiClient();
        apiClient.setScheme("http");
        apiClient.setHost("127.0.0.1");
        apiClient.setPort(server.getAddress().getPort());
        apiClient.setObjectMapper(new CustomObjectMapper());   // what AuthApiClient installs
        BioModelResourceApi api = new BioModelResourceApi(apiClient);

        byte[] original = VCML.getBytes(StandardCharsets.UTF_8);
        List<Integer> expectedSizes = new ArrayList<>();
        List<Integer> saveBodySizes = new ArrayList<>();
        for (int cycle = 1; cycle <= 3; cycle++) {
            String opened = api.getBioModelVCML("1");            // LocalUserMetaDbServerMessaging.getBioModelXML
            api.saveBioModel(opened, null, null);                // LocalUserMetaDbServerMessaging.saveBioModel
            expectedSizes.add(original.length);
            saveBodySizes.add(lastSaveBody.get().length);
        }
        // Before the fix: expected [224, 224, 224] but was [284, 404, 663] - every cycle re-encoded the text.
        assertEquals(expectedSizes, saveBodySizes, "save body size (bytes) per open-and-save cycle");
        assertArrayEquals(original, stored.get(), "stored VCML differs after three open-and-save cycles");
        assertEquals(VCML, api.getBioModelVCML("1"), "VCML corrupted on open");
        assertEquals(VCML, api.saveBioModel(VCML, null, null), "VCML corrupted in the save response");
    }
}

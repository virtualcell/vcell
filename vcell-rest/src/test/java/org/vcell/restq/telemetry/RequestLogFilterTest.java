package org.vcell.restq.telemetry;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import org.jboss.logmanager.ExtLogRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vcell.restq.config.CDIVCellConfigProvider;

import cbit.vcell.resource.PropertyLoader;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;

/** Real requests through the filter: the log line carries the route template, status, client and request id. */
@QuarkusTest
public class RequestLogFilterTest {

	private final List<ExtLogRecord> records = new CopyOnWriteArrayList<>();
	private final Handler capture = new Handler() {
		@Override public void publish(LogRecord r) {
			if (r instanceof ExtLogRecord e && RequestLogLine.MESSAGE.equals(e.getMessage())) {
				records.add(e);
			}
		}
		@Override public void flush() { }
		@Override public void close() { }
	};
	private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(RequestLogLine.class.getName());

	@BeforeAll
	public static void setupConfig() {
		PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
	}

	@BeforeEach
	public void attach() { logger.addHandler(capture); }

	@AfterEach
	public void detach() { logger.removeHandler(capture); }

	private ExtLogRecord lineFor(String requestId) throws InterruptedException {
		for (int i = 0; i < 50; i++) {   // the line is written when the response ends, possibly just after the client returns
			for (ExtLogRecord r : records) {
				if (requestId.equals(r.getMdc("request.id"))) return r;
			}
			Thread.sleep(20);
		}
		return null;
	}

	@Test
	public void aRequestIsLoggedWithItsRouteTemplateStatusAndClient() throws InterruptedException {
		Response response = given().header(RequestLogFilter.REQUEST_ID_HEADER, "test-req-0001")
				.header(RequestLogFilter.CLIENT_HEADER, "vcell-desktop")
				.header(RequestLogFilter.CLIENT_VERSION_HEADER, "8.3.5.01")
				.when().get("/api/v1/publications/999999999");
		assertEquals("test-req-0001", response.getHeader(RequestLogFilter.REQUEST_ID_HEADER), "request id returned to the caller");
		ExtLogRecord line = lineFor("test-req-0001");
		assertNotNull(line, "one line per request");
		assertEquals("/api/v1/publications/{id}", line.getMdc("http.route"), "the template, never the raw path");
		assertEquals("GET", line.getMdc("http.request.method"));
		assertEquals(Integer.toString(response.statusCode()), line.getMdc("http.response.status_code"));
		assertEquals("8.3.5.01", line.getMdc("client.version"));
		assertEquals("anonymous", line.getMdc("user.class"));
		assertTrue(Long.parseLong(line.getMdc("http.durationMs")) >= 0);
	}

	@Test
	public void aRequestWithoutAnIdGetsOneBack() throws InterruptedException {
		Response response = given().when().get("/api/v1/helloworld");
		String id = response.getHeader(RequestLogFilter.REQUEST_ID_HEADER);
		assertNotNull(id);
		assertTrue(id.matches("[0-9a-f]{32}"));
		ExtLogRecord line = lineFor(id);
		assertNotNull(line);
		assertEquals("/api/v1/helloworld", line.getMdc("http.route"));
		assertEquals("none", line.getMdc("client.version"));
	}
}

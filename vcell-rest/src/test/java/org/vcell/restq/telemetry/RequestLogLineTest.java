package org.vcell.restq.telemetry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class RequestLogLineTest {

	@Test
	public void anAnonymousRequestRecordsRouteStatusSizeAndClient() {
		Map<String, String> f = new RequestLogLine(System.nanoTime()).method("GET").route("/api/v1/publications/{id}")
				.caller(null).client("vcell-desktop", "8.3.5.01").requestId("abc12345").finish(200, 512).fields();
		assertEquals("http", f.get("event.action"));
		assertEquals("GET", f.get("http.request.method"));
		assertEquals("/api/v1/publications/{id}", f.get("http.route"));
		assertEquals("200", f.get("http.response.status_code"));
		assertEquals("512", f.get("http.response.bytes"));
		assertEquals("anonymous", f.get("user.class"));
		assertFalse(f.containsKey("user.id"));
		assertEquals("8.3.5.01", f.get("client.version"));
		assertEquals("abc12345", f.get("request.id"));
		assertTrue(Long.parseLong(f.get("http.durationMs")) >= 0);
	}

	@Test
	public void missingClientHeadersAreRecordedAsNone() {
		Map<String, String> f = new RequestLogLine(System.nanoTime()).method("POST").route("/api/v1/bioModel")
				.caller(null).client(null, " ").requestId("r1234567").finish(400, -1).fields();
		assertEquals("none", f.get("client.name"));
		assertEquals("none", f.get("client.version"));
		assertFalse(f.containsKey("http.response.bytes"), "unknown size is omitted, not -1");
		f.values().forEach(v -> assertTrue(v != null && !v.isEmpty()));
	}

	@Test
	public void routeTemplatesJoinClassAndMethodPaths() {
		assertEquals("/api/v1/publications/{id}", RequestLogFilter.join("/api/v1/publications", "{id}"));
		assertEquals("/api/v1/publications/{id}/publish", RequestLogFilter.join("/api/v1/publications/", "/{id}/publish"));
		assertEquals("/api/v1/helloworld", RequestLogFilter.join("/api/v1/helloworld", ""));
	}

	@Test
	public void aSaneIncomingRequestIdIsKeptAndAnythingElseReplaced() {
		assertEquals("4bf92f3577b34da6", RequestLogFilter.requestId("4bf92f3577b34da6"));
		String replaced = RequestLogFilter.requestId("bad id\r\nX-Injected: 1");
		assertNotEquals("bad id\r\nX-Injected: 1", replaced);
		assertTrue(replaced.matches("[0-9a-f]{32}"));
		assertTrue(RequestLogFilter.requestId(null).matches("[0-9a-f]{32}"));
	}
}

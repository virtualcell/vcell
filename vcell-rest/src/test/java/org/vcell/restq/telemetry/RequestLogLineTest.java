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
				.caller(null).client("vcell-desktop", "8.3.5.01", "Java-http-client/17.0.21").requestId("abc12345")
				.finish(200, 512, false).fields();
		assertEquals("http-request", f.get("event.action"));
		assertEquals("success", f.get("event.outcome"));
		assertEquals("GET", f.get("http.request.method"));
		assertEquals("/api/v1/publications/{id}", f.get("http.route"));
		assertEquals("200", f.get("http.response.status_code"));
		assertEquals("512", f.get("http.response.body.bytes"));
		assertEquals("abc12345", f.get("http.request.id"));
		assertFalse(f.containsKey("user.id"), "an anonymous request has no user fields");
		assertFalse(f.containsKey("user.roles"));
		assertEquals("vcell-desktop", f.get("user_agent.name"));
		assertEquals("8.3.5.01", f.get("user_agent.version"));
		assertEquals("Java-http-client/17.0.21", f.get("user_agent.original"));
		assertTrue(Long.parseLong(f.get("event.duration")) >= 0, "nanoseconds");
	}

	@Test
	public void missingValuesAreOmitted() {
		Map<String, String> f = new RequestLogLine(System.nanoTime()).method("POST").route("/api/v1/bioModel")
				.caller(null).client(null, " ", null).requestId("r1234567").finish(400, -1, false).fields();
		assertEquals("failure", f.get("event.outcome"), "4xx is a failure");
		assertFalse(f.containsKey("user_agent.name"));
		assertFalse(f.containsKey("user_agent.version"));
		assertFalse(f.containsKey("http.response.body.bytes"), "unknown size is omitted, not -1");
		f.values().forEach(v -> assertTrue(v != null && !v.isEmpty()));
	}

	@Test
	public void anAbortedResponseHasAnUnknownOutcome() {
		Map<String, String> f = new RequestLogLine(System.nanoTime()).method("GET").route("/x").requestId("r1234567")
				.finish(200, 10, true).fields();
		assertEquals("unknown", f.get("event.outcome"));
	}

	@Test
	public void callerSuppliedHeadersAreCleaned() {
		Map<String, String> f = new RequestLogLine(System.nanoTime()).method("GET").route("/x").requestId("r1234567")
				.client("a\r\nb", "1".repeat(500), "ua\u0007").finish(200, 1, false).fields();
		assertEquals("a??b", f.get("user_agent.name"));
		assertEquals(64, f.get("user_agent.version").length());
		assertEquals("ua?", f.get("user_agent.original"));
	}

	@Test
	public void logRestoresMdcValuesThatWereAlreadySet() {
		org.jboss.logging.MDC.put("http.request.id", "outer-request");
		try {
			new RequestLogLine(System.nanoTime()).method("GET").route("/x").requestId("inner123").finish(200, 1, false).log();
			assertEquals("outer-request", org.jboss.logging.MDC.get("http.request.id"));
			assertEquals(null, org.jboss.logging.MDC.get("http.route"), "keys that were not set before are removed");
		} finally {
			org.jboss.logging.MDC.remove("http.request.id");
		}
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

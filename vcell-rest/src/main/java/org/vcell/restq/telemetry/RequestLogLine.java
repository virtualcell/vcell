package org.vcell.restq.telemetry;

import java.util.LinkedHashMap;
import java.util.Map;

import io.quarkus.security.identity.SecurityIdentity;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

/** The fields of one request's log line (docs/api-style-guide.md §7.1), written into the MDC. */
public final class RequestLogLine {

	static final Logger lg = Logger.getLogger(RequestLogLine.class);
	public static final String MESSAGE = "http request";

	private final long startNanos;
	private String method;
	private String route;
	private String callerClass = "anonymous";
	private String userId;
	private String clientName;
	private String clientVersion;
	private String requestId;
	private int status = -1;
	private long bytes = -1;
	private long durationMs = -1;

	RequestLogLine(long startNanos) { this.startNanos = startNanos; }

	RequestLogLine method(String method) { this.method = method; return this; }
	RequestLogLine route(String route) { this.route = route; return this; }
	RequestLogLine client(String name, String version) { this.clientName = name; this.clientVersion = version; return this; }
	RequestLogLine requestId(String id) { this.requestId = id; return this; }
	String requestId() { return requestId; }

	RequestLogLine caller(SecurityIdentity identity) {
		if (identity == null || identity.isAnonymous()) {
			callerClass = "anonymous";
			userId = null;
		} else {
			callerClass = identity.hasRole("admin") ? "admin" : "user";
			userId = identity.getPrincipal() == null ? null : identity.getPrincipal().getName();
		}
		return this;
	}

	RequestLogLine finish(int status, long bytes) {
		this.status = status;
		this.bytes = bytes;
		this.durationMs = (System.nanoTime() - startNanos) / 1_000_000L;
		return this;
	}

	/** The fields written with the line; values are never null. */
	Map<String, String> fields() {
		Map<String, String> f = new LinkedHashMap<>();
		f.put("event.action", "http");
		f.put("http.request.method", orNone(method));
		f.put("http.route", orNone(route));
		f.put("http.response.status_code", Integer.toString(status));
		f.put("http.durationMs", Long.toString(durationMs));
		if (bytes >= 0) f.put("http.response.bytes", Long.toString(bytes));
		f.put("user.class", callerClass);
		if (userId != null) f.put("user.id", userId);
		f.put("client.name", orNone(clientName));
		f.put("client.version", orNone(clientVersion));
		f.put("request.id", orNone(requestId));
		return f;
	}

	/** Writes the line. Never throws. */
	void log() {
		Map<String, String> f;
		try {
			f = fields();
		} catch (RuntimeException e) {
			return;
		}
		try {
			f.forEach(MDC::put);
			lg.info(MESSAGE);
		} catch (RuntimeException e) {
			lg.debug("request telemetry failed: " + e.getMessage());
		} finally {
			f.keySet().forEach(MDC::remove);
		}
	}

	private static String orNone(String s) {
		return (s == null || s.isBlank()) ? "none" : s;
	}
}

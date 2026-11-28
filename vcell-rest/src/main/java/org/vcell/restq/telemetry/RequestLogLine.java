package org.vcell.restq.telemetry;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import cbit.vcell.resource.ClientIdentification;
import io.quarkus.security.identity.SecurityIdentity;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

/**
 * The fields of one request's log line (docs/api-style-guide.md §7.1), written into the MDC. Names follow
 * Elastic Common Schema (ECS), and OpenTelemetry's semantic conventions where ECS has none
 * ({@code http.route}). Absent values are omitted, and every caller-supplied value is cleaned
 * ({@link ClientIdentification#clean}).
 */
public final class RequestLogLine {

	static final Logger lg = Logger.getLogger(RequestLogLine.class);
	public static final String MESSAGE = "http request";

	private final long startNanos;
	private String method;
	private String route;
	private String role;
	private String userId;
	private String clientName;
	private String clientVersion;
	private String userAgent;
	private String requestId;
	private int status = -1;
	private long bytes = -1;
	private boolean aborted = false;
	private long durationNanos = -1;

	RequestLogLine(long startNanos) { this.startNanos = startNanos; }

	RequestLogLine method(String method) { this.method = method; return this; }
	RequestLogLine route(String route) { this.route = route; return this; }
	RequestLogLine client(String name, String version, String userAgent) {
		this.clientName = name;
		this.clientVersion = version;
		this.userAgent = userAgent;
		return this;
	}
	RequestLogLine requestId(String id) { this.requestId = id; return this; }
	String requestId() { return requestId; }

	RequestLogLine caller(SecurityIdentity identity) {
		if (identity == null || identity.isAnonymous()) {
			role = null;
			userId = null;
		} else {
			role = identity.hasRole("admin") ? "admin" : "user";
			userId = identity.getPrincipal() == null ? null : identity.getPrincipal().getName();
		}
		return this;
	}

	/** @param aborted true when the connection closed before the response was completely sent */
	RequestLogLine finish(int status, long bytes, boolean aborted) {
		this.status = status;
		this.bytes = bytes;
		this.aborted = aborted;
		this.durationNanos = System.nanoTime() - startNanos;
		return this;
	}

	/** {@code success} below 400, {@code failure} from 400 on, {@code unknown} when the client went away. */
	String outcome() {
		if (aborted || status < 0) {
			return "unknown";
		}
		return status < 400 ? "success" : "failure";
	}

	/** The fields written with the line, in ECS/OpenTelemetry names; absent values are omitted. */
	Map<String, String> fields() {
		Map<String, String> f = new LinkedHashMap<>();
		f.put("event.action", "http-request");
		f.put("event.outcome", outcome());
		if (durationNanos >= 0) f.put("event.duration", Long.toString(durationNanos));
		put(f, "http.request.method", method);
		put(f, "http.route", route);
		if (status >= 0) f.put("http.response.status_code", Integer.toString(status));
		if (bytes >= 0) f.put("http.response.body.bytes", Long.toString(bytes));
		put(f, "http.request.id", requestId);
		put(f, "user.id", userId);
		put(f, "user.roles", role);
		put(f, "user_agent.name", ClientIdentification.clean(clientName, ClientIdentification.MAX_NAME_LENGTH));
		put(f, "user_agent.version", ClientIdentification.clean(clientVersion, ClientIdentification.MAX_NAME_LENGTH));
		put(f, "user_agent.original", ClientIdentification.clean(userAgent, ClientIdentification.MAX_USER_AGENT_LENGTH));
		return f;
	}

	/** Writes the line, restoring any MDC values these keys already had. Never throws. */
	void log() {
		Map<String, String> f;
		try {
			f = fields();
		} catch (RuntimeException e) {
			return;
		}
		Map<String, Object> previous = new HashMap<>();
		try {
			for (Map.Entry<String, String> e : f.entrySet()) {
				Object before = MDC.get(e.getKey());
				if (before != null) {
					previous.put(e.getKey(), before);
				}
				MDC.put(e.getKey(), e.getValue());
			}
			lg.info(MESSAGE);
		} catch (RuntimeException e) {
			lg.debug("request telemetry failed: " + e.getMessage());
		} finally {
			for (String key : f.keySet()) {
				Object before = previous.get(key);
				if (before != null) {
					MDC.put(key, before);
				} else {
					MDC.remove(key);
				}
			}
		}
	}

	private static void put(Map<String, String> f, String key, String value) {
		if (value != null && !value.isBlank()) {
			f.put(key, value);
		}
	}
}

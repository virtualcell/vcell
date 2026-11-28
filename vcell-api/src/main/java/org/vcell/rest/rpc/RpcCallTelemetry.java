package org.vcell.rest.rpc;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.resource.ClientIdentification;

/**
 * One structured log line per legacy RPC call (docs/api-v0-to-v1-migration-plan.md Phase 0): which
 * method, through which queue, for which kind of caller and client version, how long it took and how big
 * the request and reply were. The fields go into the log context, so the JSON layout writes them as fields
 * that LogQL can filter and aggregate. This is the evidence for size limits (D-10) and for retiring each
 * v0 method by client version.
 * <p>
 * Field names follow Elastic Common Schema (ECS), and OpenTelemetry's semantic conventions where ECS has
 * none ({@code rpc.*}); see docs/api-style-guide.md §7.1. Absent values are omitted, not written as
 * placeholders, and every caller-supplied value is cleaned ({@link ClientIdentification#clean}).
 */
final class RpcCallTelemetry {

	static final Logger lg = LogManager.getLogger(RpcCallTelemetry.class);
	static final String MESSAGE = "rpc call";
	static final String RPC_SYSTEM = "vcell-rpc";
	/** Longest RPC queue or method name kept. */
	static final int MAX_RPC_NAME_LENGTH = 128;

	enum Outcome { success, failure }

	private final long startNanos = System.nanoTime();
	private String destination;
	private String method;
	private String userName;
	private boolean guest;
	private String clientName;
	private String clientVersion;
	private String userAgent;
	private long requestBytes = -1;
	private long replyBytes = -1;
	private Outcome outcome = Outcome.failure;
	private String errorType;
	private boolean decided = false;

	RpcCallTelemetry destination(String destination) { this.destination = destination; return this; }
	RpcCallTelemetry method(String method) { this.method = method; return this; }
	RpcCallTelemetry user(String userName, boolean guest) { this.userName = userName; this.guest = guest; return this; }
	RpcCallTelemetry client(String clientName, String clientVersion, String userAgent) {
		this.clientName = clientName;
		this.clientVersion = clientVersion;
		this.userAgent = userAgent;
		return this;
	}
	RpcCallTelemetry requestBytes(long bytes) { this.requestBytes = bytes; return this; }
	RpcCallTelemetry replyBytes(long bytes) { this.replyBytes = bytes; return this; }
	RpcCallTelemetry succeeded() { this.outcome = Outcome.success; this.errorType = null; this.decided = true; return this; }
	/** A call refused before it ran (a method the caller may not use); {@code error.type} says why. */
	RpcCallTelemetry rejected(Throwable cause) { this.outcome = Outcome.failure; this.errorType = typeOf(cause); this.decided = true; return this; }
	RpcCallTelemetry failed(Throwable cause) { this.outcome = Outcome.failure; this.errorType = typeOf(cause); this.decided = true; return this; }

	/** True until an outcome has been recorded. */
	boolean isUnset() { return !decided; }

	/** The fields written with the log line, in ECS/OpenTelemetry names; absent values are omitted. */
	Map<String, String> fields(long durationNanos) {
		Map<String, String> f = new LinkedHashMap<>();
		f.put("event.action", "rpc-call");
		f.put("event.outcome", outcome.name());
		f.put("event.duration", Long.toString(durationNanos));
		put(f, "error.type", errorType);
		f.put("rpc.system", RPC_SYSTEM);
		put(f, "rpc.service", ClientIdentification.clean(destination, MAX_RPC_NAME_LENGTH));
		put(f, "rpc.method", ClientIdentification.clean(method, MAX_RPC_NAME_LENGTH));
		if (requestBytes >= 0) f.put("http.request.body.bytes", Long.toString(requestBytes));
		if (replyBytes >= 0) f.put("http.response.body.bytes", Long.toString(replyBytes));
		if (userName != null) {
			f.put("user.name", userName);
			f.put("user.roles", guest ? "guest" : "user");
		}
		put(f, "user_agent.name", ClientIdentification.clean(clientName, ClientIdentification.MAX_NAME_LENGTH));
		put(f, "user_agent.version", ClientIdentification.clean(clientVersion, ClientIdentification.MAX_NAME_LENGTH));
		put(f, "user_agent.original", ClientIdentification.clean(userAgent, ClientIdentification.MAX_USER_AGENT_LENGTH));
		return f;
	}

	/** Writes the line. Never throws: telemetry must not break a call. */
	void log() {
		try {
			long durationNanos = System.nanoTime() - startNanos;
			// CloseableThreadContext restores any values these keys had before, on this thread
			try (CloseableThreadContext.Instance ctc = CloseableThreadContext.putAll(fields(durationNanos))) {
				lg.info(MESSAGE);
			}
		} catch (RuntimeException e) {
			lg.debug("rpc telemetry failed: " + e.getMessage());
		}
	}

	private static void put(Map<String, String> f, String key, String value) {
		if (value != null) {
			f.put(key, value);
		}
	}

	private static String typeOf(Throwable t) {
		return t == null ? null : t.getClass().getSimpleName();
	}
}

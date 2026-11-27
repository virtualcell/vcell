package org.vcell.rest.rpc;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * One structured log line per legacy RPC call (docs/api-v0-to-v1-migration-plan.md Phase 0): which
 * method, through which queue, for which kind of caller and client version, how long it took and how big
 * the request and reply were. The fields go into the log context, so the JSON layout writes them as fields
 * that LogQL can filter and aggregate. This is the evidence for size limits (D-10) and for retiring each
 * v0 method by client version.
 */
final class RpcCallTelemetry {

	static final Logger lg = LogManager.getLogger(RpcCallTelemetry.class);
	static final String MESSAGE = "rpc call";

	enum Outcome { ok, error, rejected }

	private final long startNanos = System.nanoTime();
	private String destination;
	private String method;
	private String userName;
	private boolean guest;
	private String clientName;
	private String clientVersion;
	private long requestBytes = -1;
	private long replyBytes = -1;
	private Outcome outcome = Outcome.error;
	private String errorType;
	private boolean decided = false;

	RpcCallTelemetry destination(String destination) { this.destination = destination; return this; }
	RpcCallTelemetry method(String method) { this.method = method; return this; }
	RpcCallTelemetry user(String userName, boolean guest) { this.userName = userName; this.guest = guest; return this; }
	RpcCallTelemetry client(String clientName, String clientVersion) { this.clientName = clientName; this.clientVersion = clientVersion; return this; }
	RpcCallTelemetry requestBytes(long bytes) { this.requestBytes = bytes; return this; }
	RpcCallTelemetry replyBytes(long bytes) { this.replyBytes = bytes; return this; }
	RpcCallTelemetry succeeded() { this.outcome = Outcome.ok; this.errorType = null; this.decided = true; return this; }
	RpcCallTelemetry rejected(Throwable cause) { this.outcome = Outcome.rejected; this.errorType = typeOf(cause); this.decided = true; return this; }
	RpcCallTelemetry failed(Throwable cause) { this.outcome = Outcome.error; this.errorType = typeOf(cause); this.decided = true; return this; }

	/** True until an outcome has been recorded. */
	boolean isUnset() { return !decided; }

	/** The fields written with the log line; values are never null. */
	Map<String, String> fields(long durationMs) {
		Map<String, String> f = new LinkedHashMap<>();
		f.put("event.action", "rpc");
		f.put("rpc.destination", orNone(destination));
		f.put("rpc.method", orNone(method));
		f.put("rpc.outcome", outcome.name());
		if (errorType != null) f.put("rpc.errorType", errorType);
		f.put("rpc.durationMs", Long.toString(durationMs));
		if (requestBytes >= 0) f.put("rpc.requestBytes", Long.toString(requestBytes));
		if (replyBytes >= 0) f.put("rpc.replyBytes", Long.toString(replyBytes));
		if (userName != null) f.put("user.name", userName);
		f.put("user.class", userName == null ? "anonymous" : (guest ? "guest" : "user"));
		f.put("client.name", orNone(clientName));
		f.put("client.version", orNone(clientVersion));
		return f;
	}

	/** Writes the line. Never throws: telemetry must not break a call. */
	void log() {
		try {
			long durationMs = (System.nanoTime() - startNanos) / 1_000_000L;
			try (CloseableThreadContext.Instance ctc = CloseableThreadContext.putAll(fields(durationMs))) {
				lg.info(MESSAGE);
			}
		} catch (RuntimeException e) {
			lg.debug("rpc telemetry failed: " + e.getMessage());
		}
	}

	private static String typeOf(Throwable t) {
		return t == null ? null : t.getClass().getSimpleName();
	}

	private static String orNone(String s) {
		return (s == null || s.isBlank()) ? "none" : s;
	}
}

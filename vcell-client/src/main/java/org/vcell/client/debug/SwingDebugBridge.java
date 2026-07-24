/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.client.debug;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Loopback-only HTTP control surface over {@link SwingInspector} that lets a
 * developer or an automated agent observe and drive the running Swing UI the
 * way a browser's DevTools / Playwright drive a web page.
 *
 * <p><b>Opt-in only.</b> Started only when {@code -Dvcell.debugBridge=true}
 * (or env {@code VCELL_DEBUG_BRIDGE=true}). In a normal production launch
 * {@link #startIfEnabled()} is a no-op, so this has zero runtime footprint.
 *
 * <p>Binds exclusively to the loopback interface (127.0.0.1) so it is never
 * reachable off the machine.
 *
 * <pre>
 *   GET /health                 -&gt; "ok"
 *   GET /windows                -&gt; JSON, showing top-level windows (depth 0)
 *   GET /tree[?maxDepth=N]      -&gt; JSON, full component tree of every window
 *   GET /screenshot[?window=N]  -&gt; JSON {"path": "...png"}; N omitted = active window
 *   GET /click?path=0/3/2       -&gt; JSON {"clicked": true|false}
 *   GET /setText?path=..&amp;text=..[&amp;enter=true]  -&gt; JSON {"set": true|false}
 *   GET /selectTab?path=..&amp;index=N            -&gt; JSON {"selected": true|false}
 * </pre>
 *
 * Example: {@code curl -s localhost:9123/tree?maxDepth=6 | jq}
 */
public final class SwingDebugBridge {

	private static final Logger LG = LogManager.getLogger(SwingDebugBridge.class);

	private static final String ENABLE_PROP = "vcell.debugBridge";
	private static final String ENABLE_ENV = "VCELL_DEBUG_BRIDGE";
	private static final String PORT_PROP = "vcell.debugBridge.port";
	private static final String DIR_PROP = "vcell.debugBridge.dir";
	private static final int DEFAULT_PORT = 9123;

	private static HttpServer server;

	private SwingDebugBridge() {
	}

	/** @return true if the bridge is switched on via system property or environment variable. */
	public static boolean isEnabled() {
		if (Boolean.parseBoolean(System.getProperty(ENABLE_PROP, "false"))) {
			return true;
		}
		return Boolean.parseBoolean(System.getenv(ENABLE_ENV));
	}

	/** Screenshot output directory ({@code -Dvcell.debugBridge.dir}, else {@code <tmp>/vcell-debug}). */
	public static File outputDir() {
		String configured = System.getProperty(DIR_PROP);
		if (configured != null && !configured.isEmpty()) {
			return new File(configured);
		}
		return new File(System.getProperty("java.io.tmpdir"), "vcell-debug");
	}

	/**
	 * Start the bridge if enabled. Safe to call more than once (subsequent calls
	 * are no-ops). Never throws; failures are logged so a debug facility can
	 * never take down the client.
	 */
	public static synchronized void startIfEnabled() {
		if (!isEnabled()) {
			return;
		}
		if (server != null) {
			return;
		}
		int port = Integer.getInteger(PORT_PROP, DEFAULT_PORT);
		try {
			HttpServer s = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
			s.createContext("/health", ex -> respond(ex, 200, "text/plain", "ok".getBytes(StandardCharsets.UTF_8)));
			s.createContext("/windows", wrap(SwingDebugBridge::handleWindows));
			s.createContext("/tree", wrap(SwingDebugBridge::handleTree));
			s.createContext("/screenshot", wrap(SwingDebugBridge::handleScreenshot));
			s.createContext("/click", wrap(SwingDebugBridge::handleClick));
			s.createContext("/setText", wrap(SwingDebugBridge::handleSetText));
			s.createContext("/selectTab", wrap(SwingDebugBridge::handleSelectTab));
			s.setExecutor(null); // default single-threaded executor is fine for a debug surface
			s.start();
			server = s;
			LG.warn("Swing debug bridge listening on http://127.0.0.1:{} (screenshots -> {})", port, outputDir());
		} catch (Exception e) {
			LG.error("failed to start Swing debug bridge on port " + port, e);
		}
	}

	/** Stop the bridge if running. */
	public static synchronized void stop() {
		if (server != null) {
			server.stop(0);
			server = null;
		}
	}

	// ---------------------------------------------------------------------
	// Handlers
	// ---------------------------------------------------------------------

	private interface ThrowingHandler {
		String handle(HttpExchange ex) throws Exception;
	}

	private static com.sun.net.httpserver.HttpHandler wrap(ThrowingHandler h) {
		return ex -> {
			try {
				String json = h.handle(ex);
				respond(ex, 200, "application/json", json.getBytes(StandardCharsets.UTF_8));
			} catch (Exception e) {
				LG.error("debug bridge handler error for " + ex.getRequestURI(), e);
				String msg = e.getClass().getSimpleName() + ": " + String.valueOf(e.getMessage());
				respond(ex, 500, "application/json", ("{\"error\":\"" + jsonEscape(msg) + "\"}").getBytes(StandardCharsets.UTF_8));
			}
		};
	}

	private static String handleWindows(HttpExchange ex) {
		return SwingInspector.dumpWindowsJson(0);
	}

	private static String handleTree(HttpExchange ex) {
		Map<String, String> q = query(ex);
		int maxDepth = q.containsKey("maxDepth") ? Integer.parseInt(q.get("maxDepth")) : Integer.MAX_VALUE;
		return SwingInspector.dumpWindowsJson(maxDepth);
	}

	private static String handleScreenshot(HttpExchange ex) throws Exception {
		Map<String, String> q = query(ex);
		int windowIndex = q.containsKey("window") ? Integer.parseInt(q.get("window")) : -1;
		File png = SwingInspector.screenshot(windowIndex, outputDir());
		return "{\"path\":\"" + jsonEscape(png.getAbsolutePath()) + "\"}";
	}

	private static String handleClick(HttpExchange ex) {
		Map<String, String> q = query(ex);
		String path = q.get("path");
		if (path == null || path.isEmpty()) {
			return "{\"error\":\"missing 'path' query parameter\"}";
		}
		boolean clicked = SwingInspector.click(path);
		return "{\"clicked\":" + clicked + ",\"path\":\"" + jsonEscape(path) + "\"}";
	}

	private static String handleSetText(HttpExchange ex) {
		Map<String, String> q = query(ex);
		String path = q.get("path");
		if (path == null || path.isEmpty()) {
			return "{\"error\":\"missing 'path' query parameter\"}";
		}
		String text = q.getOrDefault("text", "");
		boolean commit = Boolean.parseBoolean(q.getOrDefault("enter", "false"));
		boolean ok = SwingInspector.setText(path, text, commit);
		return "{\"set\":" + ok + ",\"path\":\"" + jsonEscape(path) + "\"}";
	}

	private static String handleSelectTab(HttpExchange ex) {
		Map<String, String> q = query(ex);
		String path = q.get("path");
		if (path == null || path.isEmpty() || !q.containsKey("index")) {
			return "{\"error\":\"require 'path' and 'index' query parameters\"}";
		}
		boolean ok = SwingInspector.selectTab(path, Integer.parseInt(q.get("index")));
		return "{\"selected\":" + ok + ",\"path\":\"" + jsonEscape(path) + "\"}";
	}

	// ---------------------------------------------------------------------
	// HTTP helpers
	// ---------------------------------------------------------------------

	private static void respond(HttpExchange ex, int status, String contentType, byte[] body) throws IOException {
		ex.getResponseHeaders().set("Content-Type", contentType);
		ex.sendResponseHeaders(status, body.length);
		try (OutputStream os = ex.getResponseBody()) {
			os.write(body);
		}
	}

	private static Map<String, String> query(HttpExchange ex) {
		Map<String, String> map = new HashMap<>();
		URI uri = ex.getRequestURI();
		String raw = uri.getRawQuery();
		if (raw == null || raw.isEmpty()) {
			return map;
		}
		for (String pair : raw.split("&")) {
			int eq = pair.indexOf('=');
			if (eq < 0) {
				map.put(urlDecode(pair), "");
			} else {
				map.put(urlDecode(pair.substring(0, eq)), urlDecode(pair.substring(eq + 1)));
			}
		}
		return map;
	}

	private static String urlDecode(String s) {
		return java.net.URLDecoder.decode(s, StandardCharsets.UTF_8);
	}

	private static String jsonEscape(String s) {
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}

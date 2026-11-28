package org.vcell.rest.rpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.Property;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Fast")
public class RpcCallTelemetryTest {

	@Test
	public void aSuccessfulCallRecordsItsMethodSizesAndClient() {
		Map<String, String> f = new RpcCallTelemetry().destination("DataRequestQueue").method("getSimDataBlock")
				.user("alice", false).client("vcell-desktop", "Rel_Version_8.1.9_build_02", "Apache-HttpClient/4.5.14 (Java/17)")
				.requestBytes(812).replyBytes(4_200_000).succeeded().fields(37_000_000L);
		assertEquals("rpc-call", f.get("event.action"));
		assertEquals("success", f.get("event.outcome"));
		assertEquals("37000000", f.get("event.duration"), "ECS event.duration is in nanoseconds");
		assertFalse(f.containsKey("error.type"));
		assertEquals("vcell-rpc", f.get("rpc.system"));
		assertEquals("DataRequestQueue", f.get("rpc.service"));
		assertEquals("getSimDataBlock", f.get("rpc.method"));
		assertEquals("812", f.get("http.request.body.bytes"));
		assertEquals("4200000", f.get("http.response.body.bytes"));
		assertEquals("alice", f.get("user.name"));
		assertEquals("user", f.get("user.roles"));
		assertEquals("vcell-desktop", f.get("user_agent.name"));
		assertEquals("Rel_Version_8.1.9_build_02", f.get("user_agent.version"));
		assertEquals("Apache-HttpClient/4.5.14 (Java/17)", f.get("user_agent.original"));
	}

	@Test
	public void aGuestRefusalIsAFailureWithItsType() {
		RpcCallTelemetry t = new RpcCallTelemetry().method("saveBioModel").user("vcellguest", true)
				.rejected(new IllegalArgumentException("guest"));
		Map<String, String> f = t.fields(1);
		assertEquals("failure", f.get("event.outcome"));
		assertEquals("IllegalArgumentException", f.get("error.type"));
		assertEquals("guest", f.get("user.roles"));
		assertFalse(t.isUnset());
	}

	@Test
	public void absentValuesAreOmittedNotPlaceholders() {
		RpcCallTelemetry t = new RpcCallTelemetry();
		assertTrue(t.isUnset(), "no outcome recorded yet");
		Map<String, String> f = t.failed(new IllegalStateException("boom")).fields(5);
		assertEquals("failure", f.get("event.outcome"));
		assertFalse(f.containsKey("rpc.method"));
		assertFalse(f.containsKey("user_agent.version"), "an old client sends no version header");
		assertFalse(f.containsKey("user.name"), "an anonymous call has no user fields");
		assertFalse(f.containsKey("user.roles"));
		assertFalse(f.containsKey("http.request.body.bytes"));
		f.values().forEach(v -> assertTrue(v != null && !v.isEmpty()));
	}

	@Test
	public void callerSuppliedValuesAreCleanedAndCapped() {
		Map<String, String> f = new RpcCallTelemetry().method("get\r\nForged: line").destination("q")
				.client("x".repeat(5000), "v\u0000", "ua\nua").succeeded().fields(1);
		assertEquals("get??Forged: line", f.get("rpc.method"));
		assertEquals(64, f.get("user_agent.name").length());
		assertEquals("v?", f.get("user_agent.version"));
		assertEquals("ua?ua", f.get("user_agent.original"));
	}

	/** The fields must reach the log event's context, which the JSON layout writes out as fields. */
	@Test
	public void theFieldsAreWrittenIntoTheLogEventContext() {
		CapturingAppender appender = CapturingAppender.attachTo(RpcCallTelemetry.class);
		try {
			new RpcCallTelemetry().destination("DbRequestQueue").method("getVCInfoContainer").user("bob", false)
					.client("vcell-desktop", "8.3.5.01", null).succeeded().log();
		} finally {
			appender.detach();
		}
		assertEquals(1, appender.events.size(), "one line per call");
		LogEvent event = appender.events.get(0);
		assertEquals(RpcCallTelemetry.MESSAGE, event.getMessage().getFormattedMessage());
		assertEquals(Level.INFO, event.getLevel());
		assertEquals("getVCInfoContainer", event.getContextData().getValue("rpc.method"));
		assertEquals("8.3.5.01", event.getContextData().getValue("user_agent.version"));
		assertTrue(event.getContextData().getValue("event.duration") != null);
	}

	private static final class CapturingAppender extends AbstractAppender {
		final List<LogEvent> events = new ArrayList<>();
		private final Logger logger;
		private final Level previous;

		private CapturingAppender(Logger logger) {
			super("rpc-telemetry-capture", null, null, true, Property.EMPTY_ARRAY);
			this.logger = logger;
			this.previous = logger.getLevel();
		}

		static CapturingAppender attachTo(Class<?> cls) {
			Logger logger = ((LoggerContext) LogManager.getContext(false)).getLogger(cls.getName());
			CapturingAppender a = new CapturingAppender(logger);
			a.start();
			logger.addAppender(a);
			Configurator.setLevel(cls.getName(), Level.INFO);
			return a;
		}

		void detach() {
			logger.removeAppender(this);
			Configurator.setLevel(logger.getName(), previous);
			stop();
		}

		@Override
		public void append(LogEvent event) {
			events.add(event.toImmutable());
		}
	}
}

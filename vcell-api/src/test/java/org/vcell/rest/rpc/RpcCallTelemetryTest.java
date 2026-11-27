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
				.user("alice", false).client("vcell-desktop", "Rel_Version_8.1.9_build_02")
				.requestBytes(812).replyBytes(4_200_000).succeeded().fields(37);
		assertEquals("rpc", f.get("event.action"));
		assertEquals("DataRequestQueue", f.get("rpc.destination"));
		assertEquals("getSimDataBlock", f.get("rpc.method"));
		assertEquals("ok", f.get("rpc.outcome"));
		assertFalse(f.containsKey("rpc.errorType"));
		assertEquals("37", f.get("rpc.durationMs"));
		assertEquals("812", f.get("rpc.requestBytes"));
		assertEquals("4200000", f.get("rpc.replyBytes"));
		assertEquals("alice", f.get("user.name"));
		assertEquals("user", f.get("user.class"));
		assertEquals("vcell-desktop", f.get("client.name"));
		assertEquals("Rel_Version_8.1.9_build_02", f.get("client.version"));
	}

	@Test
	public void aGuestRefusalIsRecordedAsRejected() {
		RpcCallTelemetry t = new RpcCallTelemetry().method("saveBioModel").user("vcellguest", true)
				.rejected(new IllegalArgumentException("guest"));
		Map<String, String> f = t.fields(1);
		assertEquals("rejected", f.get("rpc.outcome"));
		assertEquals("IllegalArgumentException", f.get("rpc.errorType"));
		assertEquals("guest", f.get("user.class"));
		assertFalse(t.isUnset());
	}

	@Test
	public void aFailureBeforeAnythingIsKnownStillProducesAWellFormedLine() {
		RpcCallTelemetry t = new RpcCallTelemetry();
		assertTrue(t.isUnset(), "no outcome recorded yet");
		Map<String, String> f = t.failed(new IllegalStateException("boom")).fields(5);
		assertEquals("error", f.get("rpc.outcome"));
		assertEquals("none", f.get("rpc.method"));
		assertEquals("none", f.get("client.version"), "an old client sends no version header");
		assertEquals("anonymous", f.get("user.class"));
		assertFalse(f.containsKey("rpc.requestBytes"));
		f.values().forEach(v -> assertTrue(v != null && !v.isEmpty()));
	}

	/** The fields must reach the log event's context, which the JSON layout writes out as fields. */
	@Test
	public void theFieldsAreWrittenIntoTheLogEventContext() {
		CapturingAppender appender = CapturingAppender.attachTo(RpcCallTelemetry.class);
		try {
			new RpcCallTelemetry().destination("DbRequestQueue").method("getVCInfoContainer").user("bob", false)
					.client("vcell-desktop", "8.3.5.01").succeeded().log();
		} finally {
			appender.detach();
		}
		assertEquals(1, appender.events.size(), "one line per call");
		LogEvent event = appender.events.get(0);
		assertEquals(RpcCallTelemetry.MESSAGE, event.getMessage().getFormattedMessage());
		assertEquals(Level.INFO, event.getLevel());
		assertEquals("getVCInfoContainer", event.getContextData().getValue("rpc.method"));
		assertEquals("8.3.5.01", event.getContextData().getValue("client.version"));
		assertTrue(event.getContextData().getValue("rpc.durationMs") != null);
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

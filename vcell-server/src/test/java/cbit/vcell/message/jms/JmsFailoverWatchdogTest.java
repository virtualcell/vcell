package cbit.vcell.message.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
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
public class JmsFailoverWatchdogTest {

	/**
	 * Spin up an embedded broker, attach the watchdog to a connection backed by
	 * a tightly-bounded failover URL, then stop the broker. After the failover
	 * transport exhausts its reconnect budget, the TransportListener fires with
	 * an IOException and the watchdog should run the injected terminal handler.
	 */
	@Test
	public void attach_firesTerminalHandlerWhenFailoverGivesUp() throws Exception {
		BrokerService broker = new BrokerService();
		broker.setPersistent(false);
		broker.setUseJmx(false);
		broker.setUseShutdownHook(false);
		TransportConnector connector = broker.addConnector("tcp://localhost:0");
		broker.start();
		broker.waitUntilStarted();
		int port = connector.getConnectUri().getPort();

		String url = "failover:(tcp://localhost:" + port + ")"
				+ "?maxReconnectAttempts=2"
				+ "&startupMaxReconnectAttempts=-1"
				+ "&initialReconnectDelay=50"
				+ "&maxReconnectDelay=100";
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
		Connection connection = factory.createConnection();
		try {
			CountDownLatch terminal = new CountDownLatch(1);
			new JmsFailoverWatchdog(terminal::countDown).attach(connection);
			connection.start();

			broker.stop();
			broker.waitUntilStopped();

			assertTrue(terminal.await(10, TimeUnit.SECONDS),
					"watchdog terminal handler should fire after maxReconnectAttempts exhausted");
		} finally {
			try { connection.close(); } catch (Exception ignored) {}
			try { broker.stop(); } catch (Exception ignored) {}
		}
	}

	/**
	 * A first connect must not be reported as a "resume".
	 *
	 * <p>Callers that create a connection per message (ConsumerContextJms) fire
	 * transportResumed once per connection; production logged ~3,300 of those a minute
	 * at INFO with zero interruptions, which buried everything else. "Resumed" is only
	 * meaningful after an interruption, so a plain connect is logged at DEBUG.
	 */
	@Test
	public void transportResumed_isInfoOnlyAfterAnInterruption() throws Exception {
		// Transport events are fired directly rather than provoked with a real broker:
		// createConnection() can establish the transport before attach() registers the
		// listener, so waiting for a natural connect is a race (it passed locally and
		// failed in CI). ActiveMQConnection exposes these callbacks, so both branches
		// are driven deterministically with no timing involved.
		ActiveMQConnection connection =
				(ActiveMQConnection) new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false")
						.createConnection();
		ListAppender captured = ListAppender.attachTo(JmsFailoverWatchdog.class);
		try {
			JmsFailoverWatchdog.logOnly().attach(connection);

			// a plain connect: not a resumption, so it must not reach INFO
			connection.transportResumed();
			assertTrue(captured.hasMessage("JMS transport connected"),
					"a first connect should be reported as 'connected'");
			assertEquals(0, captured.countAtLevel(Level.INFO),
					"a first connect must not log at INFO — that is the per-message log flood");

			// a genuine failover: interrupted then resumed, still fully visible
			connection.transportInterupted();
			connection.transportResumed();
			assertEquals(1, captured.countAtLevel(Level.WARN), "interruption should log once at WARN");
			assertEquals(1, captured.countAtLevel(Level.INFO), "a real resumption should log once at INFO");
			assertTrue(captured.hasMessage("JMS transport resumed"),
					"a resumption after an interruption should say 'resumed'");
		} finally {
			captured.detach();
			try { connection.close(); } catch (Exception ignored) {}
		}
	}

	/** Captures events from one logger so a test can assert on level and message. */
	private static final class ListAppender extends AbstractAppender {
		private final List<LogEvent> events = Collections.synchronizedList(new ArrayList<>());
		private final Logger logger;
		private final Level previousLevel;

		private ListAppender(Logger logger) {
			super("watchdog-test-capture", null, null, true, Property.EMPTY_ARRAY);
			this.logger = logger;
			this.previousLevel = logger.getLevel();
		}

		static ListAppender attachTo(Class<?> cls) {
			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
			Logger logger = ctx.getLogger(cls.getName());
			ListAppender appender = new ListAppender(logger);
			appender.start();
			logger.addAppender(appender);
			// Configurator, not logger.setLevel(). The root level is warn (log4j2-test.xml), so
			// the level has to be lowered for a DEBUG/INFO event to reach us at all. setLevel()
			// on this Logger lowers it only for *this* instance, and the logger the class under
			// test holds is a different instance of the same name (log4j creates one per message
			// factory, and warns about exactly that mismatch here). Configurator updates the
			// shared LoggerConfig and calls updateLoggers(), so both instances see DEBUG.
			// The instance-local form passed locally but failed in CI's shared-JVM Fast shard.
			Configurator.setLevel(cls.getName(), Level.DEBUG);
			return appender;
		}

		void detach() {
			logger.removeAppender(this);
			Configurator.setLevel(logger.getName(), previousLevel);
			stop();
		}

		@Override
		public void append(LogEvent event) {
			events.add(event.toImmutable());
		}

		boolean hasMessage(String needle) {
			synchronized (events) {
				return events.stream().anyMatch(e -> e.getMessage().getFormattedMessage().contains(needle));
			}
		}

		long countAtLevel(Level level) {
			synchronized (events) {
				return events.stream().filter(e -> e.getLevel() == level).count();
			}
		}
	}
}

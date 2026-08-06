package cbit.vcell.message.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
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
		ListAppender captured = ListAppender.attachTo(JmsFailoverWatchdog.class);
		BrokerService broker = new BrokerService();
		broker.setPersistent(false);
		broker.setUseJmx(false);
		broker.setUseShutdownHook(false);
		TransportConnector connector = broker.addConnector("tcp://localhost:0");
		broker.start();
		broker.waitUntilStarted();
		int port = connector.getConnectUri().getPort();

		String url = "failover:(tcp://localhost:" + port + ")"
				+ "?startupMaxReconnectAttempts=-1"
				+ "&initialReconnectDelay=50"
				+ "&maxReconnectDelay=100";
		Connection connection = new ActiveMQConnectionFactory(url).createConnection();
		try {
			JmsFailoverWatchdog.logOnly().attach(connection);
			connection.start();   // first connect — a plain connect, not a resumption

			assertTrue(captured.awaitMessage("JMS transport connected", 10, TimeUnit.SECONDS),
					"a first connect should be reported as 'connected'");
			assertEquals(0, captured.countAtLevel(Level.INFO),
					"a first connect must not log at INFO — that is the per-message log flood");
		} finally {
			captured.detach();
			try { connection.close(); } catch (Exception ignored) {}
			try { broker.stop(); } catch (Exception ignored) {}
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
			logger.setLevel(Level.DEBUG);   // so a DEBUG "connected" reaches us
			return appender;
		}

		void detach() {
			logger.removeAppender(this);
			logger.setLevel(previousLevel);
			stop();
		}

		@Override
		public void append(LogEvent event) {
			events.add(event.toImmutable());
		}

		boolean awaitMessage(String needle, long timeout, TimeUnit unit) throws InterruptedException {
			long deadline = System.nanoTime() + unit.toNanos(timeout);
			while (System.nanoTime() < deadline) {
				synchronized (events) {
					for (LogEvent e : events) {
						if (e.getMessage().getFormattedMessage().contains(needle)) {
							return true;
						}
					}
				}
				Thread.sleep(50);
			}
			return false;
		}

		long countAtLevel(Level level) {
			synchronized (events) {
				return events.stream().filter(e -> e.getLevel() == level).count();
			}
		}
	}
}

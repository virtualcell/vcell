package cbit.vcell.message.jms;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCellQueue;

/**
 * Takes a real broker away from a real consumer, which is what actually happened on 2026-08-24.
 *
 * {@link ConsumerContextJmsTest} injects the failure at the MessageConsumer so that only the
 * consumer-side escalation can fire; that isolates the fix but leaves its central assumption
 * untested -- that a broker going away is what makes receive() throw
 * {@link javax.jms.IllegalStateException} in the first place. If the provider raised something
 * else, the fix would never trigger and every test in that class would still pass.
 *
 * So this one proves the assumption against the real thing, and then proves the recovery the
 * production wiring depends on: that a process which exits and comes back reconnects and resumes
 * consuming. It is the rehearsal for the deploy, not a unit test.
 */
@Tag("Fast")
@ResourceLock("activemqBrokerRegistry")
public class BrokerOutageRecoveryTest {

	private static final String BROKER_NAME = "BrokerOutageRecoveryTestBroker";
	private static final VCellQueue TEST_QUEUE = new VCellQueue("BrokerOutageRecoveryTestQueue");

	private BrokerService broker;
	private final List<TestMessagingService> services = new ArrayList<>();

	@AfterEach
	public void cleanup() throws Exception {
		for (TestMessagingService s : services) {
			try { s.close(); } catch (Exception ignored) { }
		}
		services.clear();
		stopBroker();
	}

	/** A broker on a fixed port, so it can be stopped and started again at the same address. */
	private int startBroker(int port) throws Exception {
		broker = new BrokerService();
		// never "localhost": BrokerRegistry is JVM-global and other tests run their own -- issue #1852
		broker.setBrokerName(BROKER_NAME);
		broker.setPersistent(false);
		broker.setUseJmx(false);
		broker.setUseShutdownHook(false);
		TransportConnector connector = broker.addConnector("tcp://localhost:" + port);
		broker.start();
		broker.waitUntilStarted();
		return connector.getConnectUri().getPort();
	}

	private void stopBroker() throws Exception {
		if (broker != null) {
			broker.stop();
			broker.waitUntilStopped();
			broker = null;
		}
	}

	/**
	 * The assumption the whole fix rests on, checked against a real broker rather than assumed.
	 *
	 * Measured sequence when the broker is stopped under a live consumer: the poll loop first
	 * sees a plain {@code JMSException: Connection refused}, which is transient and correctly
	 * falls through to onException; the session then reports itself gone and the NEXT receive()
	 * raises {@link javax.jms.IllegalStateException}, which is what the escalation keys on. So
	 * the fix does fire in the real case -- but only on the second exception, which is why it
	 * has to be proven here and not assumed from the first one.
	 */
	@Test
	public void aRealBrokerOutageEscalatesRatherThanKillingTheConsumer() throws Exception {
		int port = startBroker(0);
		CountDownLatch terminal = new CountDownLatch(1);
		TestMessagingService service = newService(port, terminal);

		CountDownLatch delivered = new CountDownLatch(1);
		service.addMessageConsumer(new VCQueueConsumer(TEST_QUEUE,
				(m, s) -> delivered.countDown(), null, "outage test consumer", 1));
		sendOneMessage(service);
		assertTrue(delivered.await(30, TimeUnit.SECONDS), "consumer should be live before the outage");

		// Capture the watchdog: on a real outage BOTH routes reach it -- the transport when
		// failover gives up, and the consumer when its session dies. They are distinguishable by
		// the reason string, which is the only way to show the consumer-side escalation actually
		// fires in the real case rather than the transport carrying it.
		ListAppender watchdogLog = ListAppender.attachTo(JmsFailoverWatchdog.class);
		try {
			stopBroker();

			assertTrue(terminal.await(60, TimeUnit.SECONDS),
					"a real broker outage must reach the terminal handler");

			// the poll loop needs a turn or two past failover giving up before its session
			// reports itself gone
			boolean escalated = false;
			for (int i = 0; i < 30 && !escalated; i++) {
				escalated = watchdogLog.hasMessage("consumer session for");
				if (!escalated) {
					Thread.sleep(500);
				}
			}
			assertTrue(escalated,
					"the consumer must escalate its own lost session on a real broker outage, "
							+ "not rely on the transport noticing. Watchdog saw: " + watchdogLog.dump());
		} finally {
			watchdogLog.detach();
		}
	}

	/**
	 * The other half of the production wiring: exiting is only the right answer if coming back
	 * works. A fresh service against a broker that went away and returned must reconnect and
	 * consume -- that is what the pod restart buys.
	 */
	@Test
	public void aServiceStartedAfterTheOutageReconnectsAndConsumes() throws Exception {
		int port = startBroker(0);
		TestMessagingService first = newService(port, new CountDownLatch(1));
		CountDownLatch before = new CountDownLatch(1);
		first.addMessageConsumer(new VCQueueConsumer(TEST_QUEUE,
				(m, s) -> before.countDown(), null, "pre-outage consumer", 1));
		sendOneMessage(first);
		assertTrue(before.await(30, TimeUnit.SECONDS), "consumer should be live before the outage");

		stopBroker();
		try { first.close(); } catch (Exception ignored) { }
		services.remove(first);

		// the broker returns at the same address, as a restarted pod's broker would
		startBroker(port);

		TestMessagingService restarted = newService(port, new CountDownLatch(1));
		CountDownLatch after = new CountDownLatch(1);
		restarted.addMessageConsumer(new VCQueueConsumer(TEST_QUEUE,
				(m, s) -> after.countDown(), null, "post-restart consumer", 1));
		sendOneMessage(restarted);
		assertTrue(after.await(60, TimeUnit.SECONDS),
				"a service started after the broker returned must reconnect and consume");
	}

	private TestMessagingService newService(int port, CountDownLatch terminal) {
		TestMessagingService service = new TestMessagingService(port);
		service.setFailoverWatchdog(new JmsFailoverWatchdog(terminal::countDown));
		services.add(service);
		return service;
	}

	private void sendOneMessage(TestMessagingService service) throws Exception {
		VCMessageSession producer = service.createProducerSession();
		try {
			producer.sendQueueMessage(TEST_QUEUE, producer.createTextMessage("ping"), Boolean.FALSE, 60000L);
		} finally {
			producer.close();
		}
	}

	/**
	 * Production's messaging service against a test broker. The failover budget is the only
	 * departure from {@code VCMessagingServiceActiveMQ}: 20 attempts backing off to 30s takes
	 * eight minutes to exhaust, which is right in production and useless in a test.
	 */
	private static final class TestMessagingService extends VCMessagingServiceJms {
		private final int port;

		TestMessagingService(int port) {
			this.port = port;
		}

		@Override
		public ConnectionFactory createConnectionFactory() {
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
					"failover:(tcp://localhost:" + port + ")"
							+ "?maxReconnectAttempts=3"
							+ "&startupMaxReconnectAttempts=-1"
							+ "&useExponentialBackOff=true"
							+ "&initialReconnectDelay=100"
							+ "&maxReconnectDelay=500");
			factory.setTrustedPackages(Arrays.asList("java.lang", "java.util", "java.math", "cbit.vcell", "org.vcell"));
			factory.setWatchTopicAdvisories(false);   // mirror production, see issue #1863
			return factory;
		}

		@Override
		public MessageConsumer createConsumer(Session jmsSession, VCDestination vcDestination,
				VCMessageSelector vcSelector, int prefetchLimit) throws JMSException {
			Destination destination = (vcDestination instanceof VCellQueue)
					? jmsSession.createQueue(vcDestination.getName())
					: jmsSession.createTopic(vcDestination.getName());
			return (vcSelector == null)
					? jmsSession.createConsumer(destination)
					: jmsSession.createConsumer(destination, vcSelector.getSelectionString());
		}
	}

	/** Captures events from one logger, including their throwables. */
	private static final class ListAppender extends AbstractAppender {
		private final List<LogEvent> events = Collections.synchronizedList(new ArrayList<>());
		private final Logger logger;
		private final Level previousLevel;

		private ListAppender(Logger logger) {
			super("outage-test-capture", null, null, true, Property.EMPTY_ARRAY);
			this.logger = logger;
			this.previousLevel = logger.getLevel();
		}

		static ListAppender attachTo(Class<?> cls) {
			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
			Logger logger = ctx.getLogger(cls.getName());
			ListAppender appender = new ListAppender(logger);
			appender.start();
			logger.addAppender(appender);
			// root level is warn in log4j2-test.xml; Configurator (not logger.setLevel) so every
			// instance of this logger name sees DEBUG -- see JmsFailoverWatchdogTest
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

		String dump() {
			synchronized (events) {
				StringBuilder sb = new StringBuilder();
				for (LogEvent e : events) {
					sb.append("   [").append(e.getLevel()).append("] ")
					  .append(e.getMessage().getFormattedMessage());
					if (e.getThrown() != null) {
						sb.append("   <<").append(e.getThrown().getClass().getName())
						  .append(": ").append(e.getThrown().getMessage()).append(">>");
					}
					sb.append('\n');
				}
				return sb.toString();
			}
		}

		boolean hasMessage(String needle) {
			synchronized (events) {
				return events.stream().anyMatch(e -> e.getMessage().getFormattedMessage().contains(needle));
			}
		}
	}
}

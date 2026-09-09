package cbit.vcell.message.jms;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCellQueue;

/**
 * What the polling loop in {@link ConsumerContextJms} does when {@code receive()} fails.
 *
 * The distinction that matters is between a session closed because we asked for it and a
 * session that died underneath a consumer we were still meant to be polling. Both surface
 * as {@link javax.jms.IllegalStateException}; only the first is shutdown. Treating both as
 * shutdown is what left dev's submit service running for 6h50m consuming nothing, with a
 * pod that still reported healthy -- issue #2031.
 */
@Tag("Fast")
@ResourceLock("activemqBrokerRegistry")
public class ConsumerContextJmsTest {

	private static final String BROKER_NAME = "ConsumerContextJmsTestBroker";
	private static final VCellQueue TEST_QUEUE = new VCellQueue("ConsumerContextJmsTestQueue");

	private BrokerService broker;
	private TestMessagingService service;
	/** counts down the first time the watchdog's terminal action runs */
	private CountDownLatch terminal;

	@BeforeEach
	public void startBroker() throws Exception {
		broker = new BrokerService();
		// never "localhost": BrokerRegistry is JVM-global and other tests run their own -- issue #1852
		broker.setBrokerName(BROKER_NAME);
		broker.setPersistent(false);
		broker.setUseJmx(false);
		broker.setUseShutdownHook(false);
		broker.start();
		broker.waitUntilStarted();

		terminal = new CountDownLatch(1);
		service = new TestMessagingService();
		service.setFailoverWatchdog(new JmsFailoverWatchdog(terminal::countDown));
	}

	@AfterEach
	public void stopBroker() throws Exception {
		if (service != null) {
			try { service.close(); } catch (Exception ignored) { }
		}
		if (broker != null) {
			broker.stop();
			broker.waitUntilStopped();
		}
	}

	/**
	 * The regression. A session that dies under a live consumer has to be escalated, not
	 * swallowed.
	 *
	 * The failure is injected at the consumer rather than by stopping the broker on purpose:
	 * stopping the broker also drives the failover transport to its own terminal handler, so
	 * the test would pass on the broken code for the wrong reason. Here the transport stays
	 * healthy and the consumer's own escalation is the only thing that can fire the latch.
	 */
	@Test
	public void aSessionLostWhileStillPollingIsEscalated() throws Exception {
		CountDownLatch delivered = new CountDownLatch(1);
		VCQueueConsumer consumer = new VCQueueConsumer(TEST_QUEUE,
				(vcMessage, session) -> delivered.countDown(),
				null, "ConsumerContextJmsTest consumer", 1);
		service.addMessageConsumer(consumer);

		// prove the consumer is genuinely polling before breaking it
		sendOneMessage();
		assertTrue(delivered.await(30, TimeUnit.SECONDS), "the consumer should be live to begin with");

		service.sessionIsDead.set(true);

		assertTrue(terminal.await(30, TimeUnit.SECONDS),
				"a consumer whose session died while it was still processing must reach the "
						+ "terminal handler, not exit quietly");
	}

	/**
	 * The negative control for the test above: an ordinary shutdown looks the same at
	 * receive() and must stay silent. Without this, escalating everything would pass.
	 */
	@Test
	public void anOrdinaryShutdownIsNotEscalated() throws Exception {
		CountDownLatch delivered = new CountDownLatch(1);
		VCQueueConsumer consumer = new VCQueueConsumer(TEST_QUEUE,
				(vcMessage, session) -> delivered.countDown(),
				null, "ConsumerContextJmsTest shutdown consumer", 1);
		service.addMessageConsumer(consumer);

		sendOneMessage();
		assertTrue(delivered.await(30, TimeUnit.SECONDS), "the consumer should be live to begin with");

		// closeAll() clears bProcessing, waits out a poll interval, then closes the session --
		// exactly the ordering that makes !bProcessing sufficient to recognise shutdown
		service.close();

		assertFalse(terminal.await(2, TimeUnit.SECONDS),
				"stopping a consumer on purpose must not invoke the terminal handler");
	}

	/**
	 * A JMSException that is not a lost session is a transient failure of one poll. The loop
	 * reports it and keeps going; it must not be escalated and must not end the thread.
	 */
	@Test
	public void aTransientPollFailureIsNotEscalated() throws Exception {
		CountDownLatch delivered = new CountDownLatch(1);
		VCQueueConsumer consumer = new VCQueueConsumer(TEST_QUEUE,
				(vcMessage, session) -> delivered.countDown(),
				null, "ConsumerContextJmsTest transient consumer", 1);
		service.addMessageConsumer(consumer);

		service.transientFailures.set(2);
		// receive() returns every CONSUMER_POLLING_INTERVAL_MS, so two failures need two
		// turns of the loop plus room for scheduling
		assertTrue(service.transientFailuresConsumed.await(30, TimeUnit.SECONDS),
				"the poll loop should have hit both transient failures");

		sendOneMessage();
		assertTrue(delivered.await(30, TimeUnit.SECONDS),
				"the consumer should still be polling after a transient receive() failure");
		assertFalse(terminal.await(1, TimeUnit.SECONDS),
				"a transient receive() failure must not invoke the terminal handler");
	}

	private void sendOneMessage() throws Exception {
		VCMessageSession producerSession = service.createProducerSession();
		try {
			producerSession.sendQueueMessage(TEST_QUEUE,
					producerSession.createTextMessage("ping"), Boolean.FALSE, 60000L);
		} finally {
			producerSession.close();
		}
	}

	/**
	 * A messaging service on an in-VM broker whose consumers can be told to fail, so a lost
	 * session can be reproduced without taking the broker (and with it the transport) down.
	 */
	private final class TestMessagingService extends VCMessagingServiceJms {
		/** once set, every receive() reports the session as gone, as a real one would */
		final AtomicBoolean sessionIsDead = new AtomicBoolean(false);
		/** how many further receive() calls should fail with an unrelated, transient JMSException */
		final AtomicInteger transientFailures = new AtomicInteger(0);
		final CountDownLatch transientFailuresConsumed = new CountDownLatch(1);

		@Override
		public ConnectionFactory createConnectionFactory() {
			ActiveMQConnectionFactory factory =
					new ActiveMQConnectionFactory("vm://" + BROKER_NAME + "?create=false");
			factory.setTrustedPackages(Arrays.asList("java.lang", "java.util", "java.math", "cbit.vcell", "org.vcell"));
			// mirror production, see VCMessagingServiceActiveMQ and issue #1863
			factory.setWatchTopicAdvisories(false);
			return factory;
		}

		@Override
		public MessageConsumer createConsumer(Session jmsSession, VCDestination vcDestination,
				VCMessageSelector vcSelector, int prefetchLimit) throws JMSException {
			Destination destination = (vcDestination instanceof VCellQueue)
					? jmsSession.createQueue(vcDestination.getName())
					: jmsSession.createTopic(vcDestination.getName());
			MessageConsumer delegate = (vcSelector == null)
					? jmsSession.createConsumer(destination)
					: jmsSession.createConsumer(destination, vcSelector.getSelectionString());
			return new FailableConsumer(delegate);
		}

		private final class FailableConsumer implements MessageConsumer {
			private final MessageConsumer delegate;

			FailableConsumer(MessageConsumer delegate) {
				this.delegate = delegate;
			}

			private void failIfAsked() throws JMSException {
				if (sessionIsDead.get()) {
					// what ActiveMQ raises once the session behind a consumer is gone;
					// ConnectionClosedException extends javax.jms.IllegalStateException
					throw new javax.jms.IllegalStateException("The Session is closed");
				}
				if (transientFailures.getAndUpdate(n -> n > 0 ? n - 1 : 0) > 0) {
					if (transientFailures.get() == 0) {
						transientFailuresConsumed.countDown();
					}
					throw new JMSException("simulated transient poll failure");
				}
			}

			@Override
			public Message receive(long timeout) throws JMSException {
				failIfAsked();
				return delegate.receive(timeout);
			}

			@Override
			public Message receive() throws JMSException {
				failIfAsked();
				return delegate.receive();
			}

			@Override
			public Message receiveNoWait() throws JMSException {
				failIfAsked();
				return delegate.receiveNoWait();
			}

			@Override
			public String getMessageSelector() throws JMSException {
				return delegate.getMessageSelector();
			}

			@Override
			public MessageListener getMessageListener() throws JMSException {
				return delegate.getMessageListener();
			}

			@Override
			public void setMessageListener(MessageListener listener) throws JMSException {
				delegate.setMessageListener(listener);
			}

			@Override
			public void close() throws JMSException {
				delegate.close();
			}
		}
	}
}

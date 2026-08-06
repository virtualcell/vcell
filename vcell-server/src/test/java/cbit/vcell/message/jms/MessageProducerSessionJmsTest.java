package cbit.vcell.message.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCellQueue;

/**
 * Pins the cost of a {@link MessageProducerSessionJms}: it opens a JMS connection when it is
 * first used to send something, not when it is constructed.
 *
 * This matters because ConsumerContextJms constructs one for every message it receives and
 * hands it to the listener, and most listeners never send through it.
 */
@Tag("Fast")
public class MessageProducerSessionJmsTest {

	private static final String BROKER_NAME = "MessageProducerSessionJmsTestBroker";
	private static final VCellQueue TEST_QUEUE = new VCellQueue("MessageProducerSessionJmsTestQueue");

	private static CountingMessagingService service;

	@BeforeAll
	public static void startBroker() throws Exception {
		service = new CountingMessagingService();
		service.startBroker();
	}

	@AfterAll
	public static void stopBroker() throws Exception {
		if (service != null) {
			try {
				service.close();
			} finally {
				service.stopBroker();
			}
		}
	}

	@Test
	public void producerSessionOpensNoConnectionUntilItIsUsed() throws Exception {
		int before = service.connectionsOpened.get();
		MessageProducerSessionJms producerSession = new MessageProducerSessionJms(service);
		try {
			assertEquals(before, service.connectionsOpened.get(),
					"constructing a producer session must not open a JMS connection");

			producerSession.createTextMessage("first use");
			assertEquals(before + 1, service.connectionsOpened.get(),
					"the first use should open exactly one connection");

			producerSession.createTextMessage("second use");
			assertEquals(before + 1, service.connectionsOpened.get(),
					"later uses should reuse the connection already open");
		} finally {
			producerSession.close();
		}
	}

	/** commit/rollback/close on a session that was never used must not open one to do nothing with. */
	@Test
	public void anUnusedProducerSessionCostsNothing() throws Exception {
		int before = service.connectionsOpened.get();
		MessageProducerSessionJms producerSession = new MessageProducerSessionJms(service);
		producerSession.commit();
		producerSession.rollback();
		producerSession.close();
		assertEquals(before, service.connectionsOpened.get(),
				"an unused producer session must never open a connection");
	}

	/** Long-lived sessions are always used, so they still open up front. */
	@Test
	public void createProducerSessionOpensEagerly() throws Exception {
		int before = service.connectionsOpened.get();
		VCMessageSession producerSession = service.createProducerSession();
		try {
			assertEquals(before + 1, service.connectionsOpened.get(),
					"a long-lived producer session should open at creation, so a broker problem surfaces there");
		} finally {
			producerSession.close();
		}
	}

	/**
	 * The regression this was written for: delivering messages to a listener that never sends
	 * anything used to cost one connection (and one temporary queue) per message.
	 */
	@Test
	public void consumerOpensNoConnectionPerMessage() throws Exception {
		int messageCount = 5;
		CountDownLatch delivered = new CountDownLatch(messageCount);

		VCQueueConsumer consumer = new VCQueueConsumer(TEST_QUEUE,
				(vcMessage, session) -> delivered.countDown(),
				null, "MessageProducerSessionJmsTest consumer", 1);
		service.addMessageConsumer(consumer);

		VCMessageSession producerSession = service.createProducerSession();
		try {
			int afterSetup = service.connectionsOpened.get();

			for (int i = 0; i < messageCount; i++) {
				producerSession.sendQueueMessage(TEST_QUEUE,
						producerSession.createTextMessage("message " + i), Boolean.FALSE, 60000L);
			}
			assertTrue(delivered.await(30, TimeUnit.SECONDS),
					"all " + messageCount + " messages should reach the listener");

			assertEquals(afterSetup, service.connectionsOpened.get(),
					"a listener that never sends must not cost a connection per message");
		} finally {
			producerSession.close();
			service.removeMessageConsumer(consumer);
		}
	}

	/** An embedded-broker messaging service that counts every JMS connection opened through it. */
	private static final class CountingMessagingService extends VCMessagingServiceJms {
		final AtomicInteger connectionsOpened = new AtomicInteger();
		private BrokerService broker = null;

		void startBroker() throws Exception {
			broker = new BrokerService();
			broker.setBrokerName(BROKER_NAME);   // not "localhost" -- other tests run their own vm:// broker
			broker.setPersistent(false);
			broker.setUseJmx(false);
			broker.setUseShutdownHook(false);
			broker.start();
			broker.waitUntilStarted();
		}

		void stopBroker() throws Exception {
			if (broker != null) {
				broker.stop();
				broker.waitUntilStopped();
			}
		}

		@Override
		public ConnectionFactory createConnectionFactory() {
			return new ActiveMQConnectionFactory("vm://" + BROKER_NAME + "?create=false") {
				@Override
				public Connection createConnection() throws JMSException {
					connectionsOpened.incrementAndGet();
					return super.createConnection();
				}
			};
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
}

package cbit.vcell.message.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCRpcMessageHandler;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.resource.PropertyLoader;

import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

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
			// the consumer is left registered so service.close() shuts it down and closes its
			// connection; removeMessageConsumer only stops the thread and leaks the connection
			producerSession.close();
		}
	}

	/**
	 * Deferring the connection moved the point where a broker problem is noticed from the
	 * constructor into the send. It must still be noticed: a listener told that its send
	 * succeeded would let ConsumerContextJms commit the message it was handling.
	 */
	@Test
	public void aSendThatCannotOpenAConnectionThrows() throws Exception {
		VCMessage message = service.createProducerSession().createTextMessage("never sent");

		MessageProducerSessionJms producerSession = new MessageProducerSessionJms(service);
		service.failConnections = true;
		try {
			assertThrows(VCMessagingException.class,
					() -> producerSession.sendQueueMessage(TEST_QUEUE, message, Boolean.FALSE, 60000L),
					"a send that cannot open its connection must not report success");
		} finally {
			service.failConnections = false;
			producerSession.close();
		}
	}

	/**
	 * sendRpcMessage builds its request message on a session of its own, so that forming a large
	 * request does not touch the session the RPC is sent on (RpcService shares one producer
	 * session across request threads). That session used to be a whole second
	 * MessageProducerSessionJms -- a connection, session and temporary queue per RPC.
	 */
	@Test
	public void anRpcOpensNoConnectionToBuildItsMessage() throws Exception {
		String previous = System.getProperty(PropertyLoader.jmsBlobMessageUseMongo);
		System.setProperty(PropertyLoader.jmsBlobMessageUseMongo, "false");
		VCMessageSession producerSession = service.createProducerSession();
		try {
			int afterSessionOpened = service.connectionsOpened.get();

			VCRpcRequest request = new VCRpcRequest(new User("testuser", new KeyValue("1")),
					VCRpcRequest.RpcServiceType.TESTING_SERVICE, "aMethod", new Object[0]);
			producerSession.sendRpcMessage(TEST_QUEUE, request, false, 60000L, null, null, null);

			assertEquals(afterSessionOpened, service.connectionsOpened.get(),
					"an RPC must not open a connection just to build its request message");
		} finally {
			producerSession.close();
			if (previous == null) {
				System.clearProperty(PropertyLoader.jmsBlobMessageUseMongo);
			} else {
				System.setProperty(PropertyLoader.jmsBlobMessageUseMongo, previous);
			}
		}
	}

	/** An RPC round trip still works: request out, reply back through the temporary queue. */
	@Test
	public void rpcRoundTripReturnsTheAnswer() throws Exception {
		String previous = setBlobProperty();
		VCellQueue rpcQueue = new VCellQueue("MessageProducerSessionJmsTestRpcQueue-roundtrip");
		VCQueueConsumer responder = startEchoResponder(rpcQueue);
		VCMessageSession producerSession = service.createProducerSession();
		try {
			Object answer = producerSession.sendRpcMessage(rpcQueue, echoRequest("hello"),
					true, 30000L, null, null, null);
			assertEquals("hello", answer, "the RPC should return what the responder echoed");
		} finally {
			producerSession.close();
			service.removeMessageConsumer(responder);
			restoreBlobProperty(previous);
		}
	}

	/**
	 * The concurrency fix: every RPC gets its own JMS session, so concurrent callers on one
	 * producer session cannot corrupt each other's producer, transaction or reply consumer.
	 */
	@Test
	public void concurrentRpcsEachGetTheirOwnAnswer() throws Exception {
		String previous = setBlobProperty();
		// its own queue: removeMessageConsumer stops the polling thread without closing the
		// consumer, so a responder from another test would sit on prefetched messages forever
		VCellQueue rpcQueue = new VCellQueue("MessageProducerSessionJmsTestRpcQueue-concurrent");
		VCQueueConsumer responder = startEchoResponder(rpcQueue);
		VCMessageSession producerSession = service.createProducerSession();
		ExecutorService pool = Executors.newFixedThreadPool(6);
		try {
			CountDownLatch startTogether = new CountDownLatch(1);
			List<Future<Object>> answers = new ArrayList<>();
			for (int i = 0; i < 6; i++) {
				final String payload = "request-" + i;
				answers.add(pool.submit((Callable<Object>) () -> {
					startTogether.await();
					return producerSession.sendRpcMessage(rpcQueue, echoRequest(payload),
							true, 60000L, null, null, null);
				}));
			}
			startTogether.countDown();

			for (int i = 0; i < answers.size(); i++) {
				assertEquals("request-" + i, answers.get(i).get(90, TimeUnit.SECONDS),
						"each concurrent caller must get back its own reply, not another caller's");
			}
		} finally {
			pool.shutdownNow();
			producerSession.close();
			service.removeMessageConsumer(responder);
			restoreBlobProperty(previous);
		}
	}

	private static VCRpcRequest echoRequest(String payload) {
		return new VCRpcRequest(new User("testuser", new KeyValue("1")),
				VCRpcRequest.RpcServiceType.TESTING_SERVICE, "echo", new Object[] { payload });
	}

	/** Answers RPCs on RPC_QUEUE by echoing the argument back. */
	private static VCQueueConsumer startEchoResponder(VCellQueue rpcQueue) {
		VCRpcMessageHandler handler = new VCRpcMessageHandler(new EchoService(), rpcQueue);
		VCQueueConsumer responder = new VCQueueConsumer(rpcQueue, handler, null, "echo responder", 1);
		service.addMessageConsumer(responder);
		return responder;
	}

	public static class EchoService {
		public String echo(String value) {
			return value;
		}
	}

	private static String setBlobProperty() {
		String previous = System.getProperty(PropertyLoader.jmsBlobMessageUseMongo);
		System.setProperty(PropertyLoader.jmsBlobMessageUseMongo, "false");
		return previous;
	}

	private static void restoreBlobProperty(String previous) {
		if (previous == null) {
			System.clearProperty(PropertyLoader.jmsBlobMessageUseMongo);
		} else {
			System.setProperty(PropertyLoader.jmsBlobMessageUseMongo, previous);
		}
	}

	/** An embedded-broker messaging service that counts every JMS connection opened through it. */
	private static final class CountingMessagingService extends VCMessagingServiceJms {
		final AtomicInteger connectionsOpened = new AtomicInteger();
		volatile boolean failConnections = false;
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
			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://" + BROKER_NAME + "?create=false") {
				@Override
				public Connection createConnection() throws JMSException {
					if (failConnections) {
						throw new JMSException("simulated broker outage");
					}
					connectionsOpened.incrementAndGet();
					return super.createConnection();
				}
			};
			// VCRpcRequest travels as an ObjectMessage, and ActiveMQ refuses to deserialize
			// classes outside its allowlist. Only the packages this test actually puts on the
			// wire are listed -- trusting everything is what production does, and it is what
			// the standing java/unsafe-deserialization alert on VCMessageJms is about.
			factory.setTrustedPackages(Arrays.asList("java.lang", "java.util", "java.math", "cbit.vcell", "org.vcell"));
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
}

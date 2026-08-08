package cbit.vcell.message.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.AdvisorySupport;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.DestinationInfo;
import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCRpcMessageHandler;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;
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
@ResourceLock("activemqBrokerRegistry")
public class MessageProducerSessionJmsTest {

	private static final String BROKER_NAME = "MessageProducerSessionJmsTestBroker";
	private static final VCellQueue TEST_QUEUE = new VCellQueue("MessageProducerSessionJmsTestQueue");

	private static CountingMessagingService service;
	private static TempQueueAdvisoryWatcher advisories;

	@BeforeAll
	public static void startBroker() throws Exception {
		service = new CountingMessagingService();
		service.startBroker();
		advisories = new TempQueueAdvisoryWatcher(service.createConnectionFactory());
	}

	@AfterAll
	public static void stopBroker() throws Exception {
		if (advisories != null) {
			advisories.close();
		}
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
		// Its own service instance, sharing only the broker. This test deliberately breaks
		// connection creation, and doing that to the service every other test shares tore down
		// connections their temporary reply destinations live on -- a later RPC then published
		// to a deleted temp queue and waited out its whole timeout (issue #1855). failConnections
		// is per-instance, so an isolated service cannot reach the shared one.
		CountingMessagingService isolated = new CountingMessagingService();
		VCMessageSession messageBuilder = isolated.createProducerSession();
		MessageProducerSessionJms producerSession = new MessageProducerSessionJms(isolated);
		try {
			VCMessage message = messageBuilder.createTextMessage("never sent");
			isolated.failConnections = true;
			assertThrows(VCMessagingException.class,
					() -> producerSession.sendQueueMessage(TEST_QUEUE, message, Boolean.FALSE, 60000L),
					"a send that cannot open its connection must not report success");
		} finally {
			isolated.failConnections = false;
			producerSession.close();  // never opened a connection -- the send failed at createConnection
			messageBuilder.close();   // was leaked into the shared service before
			// `isolated` itself is deliberately not close()d: both its sessions are closed above and
			// it has no consumers, so close() would reclaim nothing -- it would only add its
			// unconditional 4s Thread.sleep. The one thing an instance does leave behind, the daemon
			// Timer built in the VCMessagingServiceJms constructor, is not cancelled by close() either.
		}

		// The actual regression from #1855: simulating the outage must leave the shared service
		// untouched. An RPC is the sharp check, because it needs both a live connection and a live
		// temporary reply queue -- dead reply destinations were what made a later test wait out its
		// whole timeout. Asserting it here pins it deterministically instead of relying on a later
		// test happening to run afterwards.
		String previous = setBlobProperty();
		VCellQueue afterOutageQueue = new VCellQueue("MessageProducerSessionJmsTestRpcQueue-afterOutage");
		VCQueueConsumer responder = startEchoResponder(afterOutageQueue);
		VCMessageSession sharedSession = service.createProducerSession();
		try {
			assertEquals("still alive",
					sharedSession.sendRpcMessage(afterOutageQueue, echoRequest("still alive"),
							true, 30000L, null, null, null),
					"the shared service must still complete an RPC after the simulated outage");
		} finally {
			sharedSession.close();
			service.removeMessageConsumer(responder);
			restoreBlobProperty(previous);
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
				Object answer;
				try {
					answer = answers.get(i).get(90, TimeUnit.SECONDS);
				} catch (Exception e) {
					// issue #1863: a caller times out because the shared reply queue was deleted
					// mid-test. The advisory record names the connection that removed it, which
					// inference across three rounds of this flake never managed to pin down.
					throw new AssertionError("concurrent caller " + i + " did not get an answer.\n"
							+ advisories.report(), e);
				}
				assertEquals("request-" + i, answer,
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

	/**
	 * removeMessageConsumer used to stop the polling thread without closing the consumer, so the
	 * broker went on dispatching to it: with prefetchLimit=1 a message would sit in a consumer
	 * nobody was reading and was not redelivered until the connection died.
	 */
	@Test
	public void removingAConsumerDoesNotStrandMessages() throws Exception {
		VCellQueue queue = new VCellQueue("MessageProducerSessionJmsTestStrandedQueue");
		VCQueueConsumer discarded = new VCQueueConsumer(queue, (m, s) -> { }, null, "discarded consumer", 1);
		service.addMessageConsumer(discarded);
		service.removeMessageConsumer(discarded);

		int messageCount = 3;
		CountDownLatch delivered = new CountDownLatch(messageCount);
		VCQueueConsumer replacement = new VCQueueConsumer(queue, (m, s) -> delivered.countDown(),
				null, "replacement consumer", 1);

		VCMessageSession producerSession = service.createProducerSession();
		try {
			// sent while only the removed consumer could still be registered -- with a prefetch
			// limit of one, a leaked consumer takes one of these and never gives it back
			for (int i = 0; i < messageCount; i++) {
				producerSession.sendQueueMessage(queue,
						producerSession.createTextMessage("message " + i), Boolean.FALSE, 60000L);
			}
			service.addMessageConsumer(replacement);

			assertTrue(delivered.await(20, TimeUnit.SECONDS),
					"all " + messageCount + " messages should reach the replacement consumer; a removed "
							+ "consumer must not still be holding one");
		} finally {
			producerSession.close();
		}
	}

	/**
	 * sendQueueMessage has always closed its producer in a finally; sendTopicMessage did not, so
	 * every publish left one behind. The sessions that publish (SimDataServer's data and export
	 * events, SimulationStateMachine, StatusMessage) live as long as the server, so the producers
	 * accumulated for the whole run.
	 */
	@Test
	public void sendTopicMessageDoesNotLeakProducers() throws Exception {
		ProducerCountingService counting = new ProducerCountingService();
		VCellTopic topic = new VCellTopic("MessageProducerSessionJmsTestTopic");
		VCMessageSession producerSession = counting.createProducerSession();
		try {
			for (int i = 0; i < 5; i++) {
				producerSession.sendTopicMessage(topic, producerSession.createTextMessage("message " + i));
			}
			assertEquals(0, counting.producersOpen.get(),
					"every publish must close the producer it created");
		} finally {
			producerSession.close();
		}
	}

	/**
	 * Wraps the JMS objects in proxies so producers opened and closed can be counted in-process --
	 * no broker bookkeeping, no timing, so the count is exact.
	 */
	private static final class ProducerCountingService extends VCMessagingServiceJms {
		final AtomicInteger producersOpen = new AtomicInteger();

		@Override
		public ConnectionFactory createConnectionFactory() {
			return new ActiveMQConnectionFactory("vm://" + BROKER_NAME + "?create=false") {
				@Override
				public Connection createConnection() throws JMSException {
					return (Connection) countingProxy(Connection.class, super.createConnection());
				}
			};
		}

		@Override
		public MessageConsumer createConsumer(Session jmsSession, VCDestination vcDestination,
				VCMessageSelector vcSelector, int prefetchLimit) {
			throw new UnsupportedOperationException("this service only produces");
		}

		private Object countingProxy(Class<?> iface, Object target) {
			return Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] { iface },
					(proxy, method, args) -> {
						Object result;
						try {
							result = method.invoke(target, args);
						} catch (InvocationTargetException e) {
							throw e.getCause();
						}
						if ("createSession".equals(method.getName())) {
							return countingProxy(Session.class, result);
						}
						if ("createProducer".equals(method.getName())) {
							producersOpen.incrementAndGet();
							return countingProxy(MessageProducer.class, result);
						}
						if ("close".equals(method.getName()) && target instanceof MessageProducer) {
							producersOpen.decrementAndGet();
						}
						return result;
					});
		}
	}

	/**
	 * Subscribes to ActiveMQ's temp-queue advisory topic and records every create/destroy, with
	 * the id of the connection that asked for it.
	 *
	 * Added for issue #1863, the fourth appearance of a reply queue being deleted while RPCs
	 * still referenced it. Three plausible mechanisms were probed and disproved (the creating
	 * session closing, consumer churn, a publishing connection closing), so the next occurrence
	 * should name its cause rather than be inferred: an advisory REMOVE carries the requesting
	 * connection id, which says whether the owner tore its own queue down or something else did.
	 */
	private static final class TempQueueAdvisoryWatcher implements MessageListener, AutoCloseable {
		private final List<String> events = Collections.synchronizedList(new ArrayList<>());
		private final Connection connection;

		TempQueueAdvisoryWatcher(ConnectionFactory factory) throws JMSException {
			connection = factory.createConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			session.createConsumer(AdvisorySupport.TEMP_QUEUE_ADVISORY_TOPIC).setMessageListener(this);
		}

		@Override
		public void onMessage(Message message) {
			if (!(message instanceof ActiveMQMessage)) {
				return;
			}
			Object data = ((ActiveMQMessage) message).getDataStructure();
			if (!(data instanceof DestinationInfo)) {
				return;
			}
			DestinationInfo info = (DestinationInfo) data;
			boolean removed = info.getOperationType() == DestinationInfo.REMOVE_OPERATION_TYPE;
			String line = String.format("TEMPQ-ADVISORY %-6s %s  requested-by-connection=%s",
					removed ? "REMOVE" : "ADD", info.getDestination(), info.getConnectionId());
			events.add(line);
			System.out.println(line);
			if (removed) {
				// what was running when it went away
				for (Map.Entry<Thread, StackTraceElement[]> e : Thread.getAllStackTraces().entrySet()) {
					for (StackTraceElement frame : e.getValue()) {
						if (frame.getClassName().startsWith("cbit.vcell.message")) {
							String at = "TEMPQ-ADVISORY   thread " + e.getKey().getName() + " at " + frame;
							events.add(at);
							System.out.println(at);
							break;
						}
					}
				}
			}
		}

		String report() {
			synchronized (events) {
				return events.isEmpty()
						? "temp-queue advisories: (none recorded)"
						: "temp-queue advisories:\n  " + String.join("\n  ", events);
			}
		}

		@Override
		public void close() {
			try {
				connection.close();
			} catch (JMSException ignored) {
			}
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

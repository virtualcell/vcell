package cbit.vcell.message.jms.artemis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import cbit.vcell.message.SimpleMessagingDelegate;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCTopicConsumer;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;

/**
 * Exercises {@link VCMessagingServiceArtemis} against a real Artemis broker in a container —
 * the same image vcell-rest's cross-protocol test uses, with both AMQP (5672) and OpenWire
 * (61616) exposed.
 *
 * A real broker rather than an embedded one because the interesting failures here are broker
 * semantics, not plumbing: anycast vs multicast routing, whether a destination-URI suffix is
 * honoured or silently becomes part of the address name, and whether an OpenWire producer and
 * an AMQP consumer actually meet on the same address.
 */
@Tag("Fast")
public class VCMessagingServiceArtemisTest {

	private static final String IMAGE = "quay.io/artemiscloud/activemq-artemis-broker:1.0.25";
	private static GenericContainer<?> artemis;
	private static VCMessagingServiceArtemis service;

	@BeforeAll
	public static void startBroker() {
		Assumptions.assumeTrue(DockerClientFactory.instance().isDockerAvailable(),
				"needs Docker for the Artemis broker container");
		artemis = new GenericContainer<>(IMAGE)
				.withExposedPorts(5672, 61616)
				.withEnv("AMQ_USER", "guest")
				.withEnv("AMQ_PASSWORD", "guest")
				.waitingFor(Wait.forListeningPort());
		artemis.start();

		service = new VCMessagingServiceArtemis();
		service.setConfiguration(new SimpleMessagingDelegate(),
				artemis.getHost(), artemis.getMappedPort(61616));
	}

	@AfterAll
	public static void stopBroker() throws Exception {
		if (service != null) {
			service.close();
		}
		if (artemis != null) {
			artemis.stop();
		}
	}

	/** The framework's basic contract: a queue message reaches a registered consumer. */
	@Test
	public void queueMessageReachesItsConsumer() throws Exception {
		VCellQueue queue = new VCellQueue("artemis.test.queue");
		CountDownLatch delivered = new CountDownLatch(1);
		List<String> received = Collections.synchronizedList(new ArrayList<>());

		VCQueueConsumer consumer = new VCQueueConsumer(queue, (vcMessage, session) -> {
			received.add(vcMessage.getTextContent());
			delivered.countDown();
		}, null, "artemis queue consumer", 1);
		service.addMessageConsumer(consumer);

		VCMessageSession producer = service.createProducerSession();
		try {
			producer.sendQueueMessage(queue, producer.createTextMessage("over artemis"), Boolean.FALSE, 60000L);
			assertTrue(delivered.await(30, TimeUnit.SECONDS), "queue message should reach the consumer");
			assertEquals("over artemis", received.get(0));
		} finally {
			producer.close();
			service.removeMessageConsumer(consumer);
		}
	}

	/**
	 * Topics are multicast addresses on Artemis, and every subscriber must get its own copy.
	 * Getting this wrong is silent — a topic routed anycast delivers to one subscriber only.
	 */
	@Test
	public void topicMessageReachesEverySubscriber() throws Exception {
		VCellTopic topic = new VCellTopic("artemis.test.topic");
		CountDownLatch both = new CountDownLatch(2);

		VCTopicConsumer first = new VCTopicConsumer(topic, (m, s) -> both.countDown(), null, "sub one", 1);
		VCTopicConsumer second = new VCTopicConsumer(topic, (m, s) -> both.countDown(), null, "sub two", 1);
		service.addMessageConsumer(first);
		service.addMessageConsumer(second);

		VCMessageSession producer = service.createProducerSession();
		try {
			producer.sendTopicMessage(topic, producer.createTextMessage("broadcast"));
			assertTrue(both.await(30, TimeUnit.SECONDS),
					"both subscribers must receive it — a topic routed anycast would deliver to one");
		} finally {
			producer.close();
			service.removeMessageConsumer(first);
			service.removeMessageConsumer(second);
		}
	}

	/** Selectors must still filter, since RPC replies depend on correlation-id selection. */
	@Test
	public void selectorsFilterOnArtemis() throws Exception {
		VCellQueue queue = new VCellQueue("artemis.test.selector");
		Connection connection = service.createConnectionFactory().createConnection();
		connection.start();
		try {
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(queue.getName());
			MessageProducer producer = session.createProducer(destination);
			for (String id : new String[] { "wanted", "unwanted" }) {
				TextMessage m = session.createTextMessage("payload-" + id);
				m.setJMSCorrelationID(id);
				producer.send(m);
			}
			MessageConsumer selective =
					session.createConsumer(destination, "JMSCorrelationID='wanted'");
			TextMessage got = (TextMessage) selective.receive(20000);
			assertNotNull(got, "the selector should match the 'wanted' message");
			assertEquals("payload-wanted", got.getText());
		} finally {
			connection.close();
		}
	}

	/**
	 * ActiveMQ's "?consumer.prefetchSize=N" destination suffix is parsed and stripped by the
	 * OpenWire *client*, so against Artemis it still resolves to the plain address — measured
	 * here, not assumed (an earlier version of this test asserted the opposite and was wrong).
	 *
	 * It works, but it is client-specific syntax that an AMQP client would not interpret: the
	 * suffix would then become part of the address and the consumer would sit on a queue nobody
	 * publishes to. {@link VCMessagingServiceArtemis} therefore sets prefetch on the connection
	 * factory instead, so the destination name stays portable across clients.
	 */
	@Test
	public void theActiveMQPrefetchSuffixIsClientSideSugar() throws Exception {
		Connection connection = service.createConnectionFactory().createConnection();
		connection.start();
		try {
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			String plain = "artemis.test.prefetch";
			MessageProducer producer = session.createProducer(session.createQueue(plain));
			producer.send(session.createTextMessage("sent to the plain address"));

			MessageConsumer suffixed = session.createConsumer(
					session.createQueue(plain + "?consumer.prefetchSize=1"));
			TextMessage got = (TextMessage) suffixed.receive(20000);
			assertNotNull(got,
					"the OpenWire client strips the suffix, so this resolves to the plain address");
			assertEquals("sent to the plain address", got.getText());
			assertEquals(plain, ((javax.jms.Queue) got.getJMSDestination()).getQueueName(),
					"the broker only ever saw the plain address name");
		} finally {
			connection.close();
		}
	}

	/**
	 * The interop that matters for the migration: a message published through the IoC framework
	 * must be readable by a consumer that is not using the framework — here a raw OpenWire
	 * client standing in for the other side of a shared destination.
	 */
	@Test
	public void aMessageSentThroughTheFrameworkIsReadableByAPlainClient() throws Exception {
		VCellQueue queue = new VCellQueue("artemis.test.interop");
		VCMessageSession producer = service.createProducerSession();
		ActiveMQConnectionFactory plainFactory = new ActiveMQConnectionFactory(
				"tcp://" + artemis.getHost() + ":" + artemis.getMappedPort(61616));
		plainFactory.setTrustAllPackages(true);
		Connection plain = plainFactory.createConnection();
		plain.start();
		try {
			VCMessage message = producer.createTextMessage("{\"command\":\"submit\"}");
			producer.sendQueueMessage(queue, message, Boolean.FALSE, 60000L);

			Session session = plain.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageConsumer consumer = session.createConsumer(session.createQueue(queue.getName()));
			TextMessage got = (TextMessage) consumer.receive(30000);
			assertNotNull(got, "a plain client must be able to read what the framework sent");
			assertEquals("{\"command\":\"submit\"}", got.getText(),
					"JSON text survives the framework unchanged -- portable payloads are what "
							+ "make a destination shareable with the AMQP side");
		} finally {
			producer.close();
			plain.close();
		}
	}

	/**
	 * The policy the optimization bridge previously lacked: it connected with a plain
	 * "tcp://host:port" URL, so a wedged transport could never escalate to the failover
	 * watchdog. Routing it through this service is what restores that.
	 */
	@Test
	public void theServiceUsesABoundedFailoverUrl() {
		String url = VCMessagingServiceArtemis.jmsUrl("broker.example", 61616);
		assertTrue(url.startsWith("failover:("),
				"a plain tcp:// URL cannot escalate a wedged transport -- that was the gap");
		assertTrue(url.contains("maxReconnectAttempts=20"), "reconnects must be bounded");
		assertTrue(url.contains("startupMaxReconnectAttempts=-1"),
				"initial connect stays unbounded so a pod tolerates a slow broker at boot");
	}
}

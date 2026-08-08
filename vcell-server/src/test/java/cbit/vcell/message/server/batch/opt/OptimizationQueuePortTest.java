package cbit.vcell.message.server.batch.opt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.jms.Connection;
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
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.jms.artemis.VCMessagingServiceArtemis;

/**
 * Pins the wire contract of the optimization bridge after moving it from hand-rolled JMS onto
 * {@link VCMessagingService}.
 *
 * The other side of these two queues is vcell-rest, publishing and consuming over AMQP 1.0 on
 * the same Artemis addresses. So what has to survive the port is not the Java plumbing but the
 * observable contract: JSON text on {@code opt-request} and {@code opt-status}, readable by a
 * client that shares no code with this one.
 */
@Tag("Fast")
public class OptimizationQueuePortTest {

	private static final String IMAGE = "quay.io/artemiscloud/activemq-artemis-broker:1.0.25";
	private static final VCellQueue OPT_REQUEST = new VCellQueue("opt-request");
	private static final VCellQueue OPT_STATUS = new VCellQueue("opt-status");

	private static GenericContainer<?> artemis;

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
	}

	@AfterAll
	public static void stopBroker() {
		if (artemis != null) {
			artemis.stop();
		}
	}

	private static VCMessagingServiceArtemis service() {
		VCMessagingServiceArtemis service = new VCMessagingServiceArtemis();
		service.setConfiguration(new SimpleMessagingDelegate(),
				artemis.getHost(), artemis.getMappedPort(61616));
		return service;
	}

	/**
	 * A JSON request published by a plain client — standing in for vcell-rest — must reach a
	 * consumer registered through the framework, unchanged.
	 */
	@Test
	public void aJsonRequestFromAnotherClientReachesAFrameworkConsumer() throws Exception {
		VCMessagingServiceArtemis service = service();
		AtomicReference<String> seen = new AtomicReference<>();
		java.util.concurrent.CountDownLatch arrived = new java.util.concurrent.CountDownLatch(1);

		VCQueueConsumer consumer = new VCQueueConsumer(OPT_REQUEST, (vcMessage, session) -> {
			seen.set(vcMessage.getTextContent());
			arrived.countDown();
		}, null, "opt request listener", 1);
		service.addMessageConsumer(consumer);

		Connection plain = plainConnection();
		try {
			Session session = plain.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(session.createQueue(OPT_REQUEST.getName()));
			producer.send(session.createTextMessage(
					"{\"command\":\"submit\",\"jobId\":\"job-42\"}"));

			assertTrue(arrived.await(30, TimeUnit.SECONDS), "the framework consumer should receive it");
			assertEquals("{\"command\":\"submit\",\"jobId\":\"job-42\"}", seen.get(),
					"JSON must pass through the framework byte-for-byte -- the AMQP side parses this");
		} finally {
			plain.close();
			service.removeMessageConsumer(consumer);
			service.close();
		}
	}

	/**
	 * And the reply direction: a status published through the framework must be readable as a
	 * plain TextMessage, since vcell-rest consumes it over AMQP with no VCell classes involved.
	 */
	@Test
	public void aStatusSentThroughTheFrameworkIsPlainJsonOnTheWire() throws Exception {
		VCMessagingServiceArtemis service = service();
		Connection plain = plainConnection();
		try {
			Session session = plain.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageConsumer consumer = session.createConsumer(session.createQueue(OPT_STATUS.getName()));

			VCMessageSession producer = service.createProducerSession();
			try {
				String json = "{\"jobId\":\"job-42\",\"status\":\"RUNNING\"}";
				producer.sendQueueMessage(OPT_STATUS, producer.createTextMessage(json),
						Boolean.FALSE, 3600_000L);

				TextMessage got = (TextMessage) consumer.receive(30000);
				assertNotNull(got, "a plain AMQP/OpenWire consumer must see the status");
				assertEquals(json, got.getText());
			} finally {
				producer.close();
			}
		} finally {
			plain.close();
			service.close();
		}
	}

	private static Connection plainConnection() throws Exception {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(
				"tcp://" + artemis.getHost() + ":" + artemis.getMappedPort(61616));
		factory.setTrustAllPackages(true);
		Connection connection = factory.createConnection();
		connection.start();
		return connection;
	}
}

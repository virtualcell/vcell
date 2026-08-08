package cbit.vcell.message.jms.activeMQ;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQDestination;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

/**
 * Pins the reason the production connection factory disables advisory watching.
 *
 * ActiveMQConnection.isDeleted() does not ask the broker — it answers from the connection's own
 * advisory-populated set of temp destinations, and callers refuse to publish to anything absent
 * from it. A connection that has not yet learned about another connection's reply queue reports
 * it as deleted while it is alive, and the RPC caller then waits out its full timeout (#1863).
 *
 * The race itself is timing-dependent and does not reproduce on an unloaded machine, so this
 * pins the mechanism rather than the race: with watching off, the answer is unconditional.
 */
@Tag("Fast")
@ResourceLock("activemqBrokerRegistry")
public class VCMessagingServiceActiveMQTest {

	private static final String BROKER = "VCMessagingServiceActiveMQTestBroker";
	private static BrokerService broker;

	@BeforeAll
	public static void startBroker() throws Exception {
		broker = new BrokerService();
		broker.setBrokerName(BROKER);
		broker.setPersistent(false);
		broker.setUseJmx(false);
		broker.setUseShutdownHook(false);
		broker.start();
		broker.waitUntilStarted();
	}

	@AfterAll
	public static void stopBroker() throws Exception {
		if (broker != null) {
			broker.stop();
			broker.waitUntilStopped();
		}
	}

	/** The production factory must not watch advisories, or replies can be spuriously refused. */
	@Test
	public void productionFactoryDoesNotWatchTopicAdvisories() {
		VCMessagingServiceActiveMQ service = new VCMessagingServiceActiveMQ();
		service.setConfiguration(new cbit.vcell.message.SimpleMessagingDelegate(), "localhost", 61616);
		ConnectionFactory factory = service.createConnectionFactory();
		assertFalse(((ActiveMQConnectionFactory) factory).isWatchTopicAdvisories(),
				"watching advisories lets a connection call a live temp queue 'deleted' (#1863)");
	}

	/**
	 * The behaviour that setting controls: a connection that knows nothing about a temporary
	 * queue must not claim it is deleted.
	 */
	@Test
	public void aConnectionThatNeverSawTheAdvisoryMustNotCallTheQueueDeleted() throws Exception {
		ActiveMQConnectionFactory ownerFactory =
				new ActiveMQConnectionFactory("vm://" + BROKER + "?create=false");
		Connection owner = ownerFactory.createConnection();
		owner.start();
		try {
			Session ownerSession = owner.createSession(false, Session.AUTO_ACKNOWLEDGE);
			TemporaryQueue foreign = ownerSession.createTemporaryQueue();

			ActiveMQConnectionFactory replierFactory =
					new ActiveMQConnectionFactory("vm://" + BROKER + "?create=false");
			replierFactory.setWatchTopicAdvisories(false);   // what production now does
			ActiveMQConnection replier = (ActiveMQConnection) replierFactory.createConnection();
			replier.start();
			try {
				assertFalse(replier.isDeleted((ActiveMQDestination) foreign),
						"with advisory watching off the client must defer to the broker, "
								+ "never declare another connection's live queue deleted");
			} finally {
				replier.close();
			}

			// and the converse: with watching on, the answer depends on advisory state, which is
			// exactly the fragility being removed -- assert only that the setting is honoured
			ActiveMQConnectionFactory watchingFactory =
					new ActiveMQConnectionFactory("vm://" + BROKER + "?create=false");
			assertTrue(watchingFactory.isWatchTopicAdvisories(),
					"ActiveMQ's default is to watch advisories, which is why this must be set explicitly");
		} finally {
			owner.close();
		}
	}
}

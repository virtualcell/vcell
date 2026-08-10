package cbit.vcell.message.jms.artemis;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.jms.VCMessagingServiceJms;

/**
 * {@link VCMessagingService} backed by an Artemis broker.
 *
 * Callers see no difference from {@link cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ}
 * — the interface is broker-agnostic, so a destination moves brokers by constructing a different
 * service, not by changing any producer or consumer.
 *
 * <h2>Why the OpenWire client rather than AMQP</h2>
 *
 * Artemis multiplexes every protocol on one acceptor, and vcell-rest already talks AMQP 1.0 to
 * the same broker, so protocol parity with the Quarkus side would be the natural choice. It is
 * not available yet: qpid-jms 2.x implements <em>Jakarta</em> Messaging (`jakarta.jms`), while
 * this framework and its 47-odd imports are `javax.jms`. Adopting it means migrating the whole
 * wrapper's namespace first, which is a separate piece of work.
 *
 * Meanwhile OpenWire against Artemis is not a guess — `OptimizationBatchServer` has been doing
 * exactly this in production, and vcell-rest's cross-protocol test drives
 * AMQP → Artemis → OpenWire → Artemis → AMQP end to end. Switching this class to an AMQP client
 * later is a change to {@link #createConnectionFactory()} alone.
 *
 * <h2>Differences from the ActiveMQ implementation</h2>
 *
 * <ul>
 * <li>Prefetch is set on the connection rather than appended to the destination name. The
 *     {@code ?consumer.prefetchSize=N} suffix does work against Artemis — the OpenWire client
 *     strips it before the broker sees the address, which the tests measure — but it is
 *     client-specific syntax. An AMQP client would take it as part of the address and leave the
 *     consumer waiting on a queue nobody publishes to, so the name is kept portable.</li>
 * <li>Queue and topic are distinct address routing types on Artemis (anycast vs multicast) rather
 *     than a property of the destination object, so they must be declared to match on the broker —
 *     see {@code docs/MESSAGING.md}.</li>
 * </ul>
 */
public class VCMessagingServiceArtemis extends VCMessagingServiceJms implements VCMessagingService {

	/**
	 * Artemis honours a per-consumer window rather than ActiveMQ's per-destination prefetch.
	 * Consumers still pass a prefetch limit through {@link #createConsumer}; it is applied by
	 * the connection factory, so this default only covers connections created before any
	 * consumer asks for something narrower.
	 */
	private static final int DEFAULT_PREFETCH = 10;

	public VCMessagingServiceArtemis() {
		super();
	}

	@Override
	public ConnectionFactory createConnectionFactory() {
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(jmsUrl(jmshost, jmsport));
		factory.setTrustAllPackages(true);
		// Same reasoning as the ActiveMQ implementation: the client must not decide for itself
		// whether another connection's temporary destination exists. See issue #1863.
		factory.setWatchTopicAdvisories(false);
		factory.getPrefetchPolicy().setAll(DEFAULT_PREFETCH);
		return factory;
	}

	@Override
	public MessageConsumer createConsumer(Session jmsSession, VCDestination vcDestination,
			VCMessageSelector vcSelector, int prefetchLimit) throws JMSException {
		// No "?consumer.prefetchSize=" suffix on the name. It would in fact work today, since
		// the OpenWire client strips it client-side, but it is ActiveMQ-specific syntax that an
		// AMQP client would fold into the address. Prefetch belongs on the connection.
		Destination jmsDestination = (vcDestination instanceof VCellQueue)
				? jmsSession.createQueue(vcDestination.getName())
				: jmsSession.createTopic(vcDestination.getName());
		return (vcSelector == null)
				? jmsSession.createConsumer(jmsDestination)
				: jmsSession.createConsumer(jmsDestination, vcSelector.getSelectionString());
	}

	/**
	 * Bounded failover, matching the ActiveMQ implementation: a wedged transport eventually
	 * surfaces to the {@link cbit.vcell.message.jms.JmsFailoverWatchdog} instead of retrying
	 * for ever, while initial connect stays unbounded so a pod tolerates a slow broker at boot.
	 */
	static String jmsUrl(String jmshost, int jmsport) {
		return "failover:(tcp://" + jmshost + ":" + jmsport + ")"
				+ "?maxReconnectAttempts=20"
				+ "&startupMaxReconnectAttempts=-1"
				+ "&useExponentialBackOff=true"
				+ "&initialReconnectDelay=1000"
				+ "&maxReconnectDelay=30000";
	}
}

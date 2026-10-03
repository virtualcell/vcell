package cbit.vcell.message.jms.activeMQ;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.jms.JmsFailoverWatchdog;
import cbit.vcell.message.jms.VCMessagingServiceJms;
import cbit.vcell.resource.PropertyLoader;


public class VCMessagingServiceActiveMQ extends VCMessagingServiceJms implements VCMessagingService {

	public VCMessagingServiceActiveMQ() {
		super();
	}

	/**
	 * A messaging service for a long-lived server process whose only job is to consume
	 * from the broker (submit, sched, data, db).
	 *
	 * The failover transport gives up after {@code maxReconnectAttempts} (set in
	 * jmsUrl below), which is deliberate -- in K8s a pod restart is the right response
	 * to a sustained broker outage. That only holds if something acts on it, so these
	 * processes exit on a terminal failure and let K8s recycle them. Without it the
	 * process stays up around a connection that can never be used again: dev's submit
	 * service ran for 6h50m in that state, consuming nothing and still reporting healthy
	 * (issue #2031).
	 *
	 * Short-lived batch processes (SolverPreprocessor, SolverPostprocessor,
	 * JavaSimulationExecutable) and the API server keep the log-only default -- they
	 * outlive neither the broker outage nor their own task.
	 */
	public static VCMessagingServiceActiveMQ createForLongLivedConsumerService() {
		VCMessagingServiceActiveMQ service = new VCMessagingServiceActiveMQ();
		service.setFailoverWatchdog(JmsFailoverWatchdog.jvmExitOnTerminal());
		return service;
	}
	
	@Override
	public ConnectionFactory createConnectionFactory(){
		//return new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false&broker.useJmx=false&create=false");
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(jmsUrl(jmshost, jmsport ));
		activeMQConnectionFactory.setTrustAllPackages(true);
		// Do not let the client second-guess whether a temporary destination exists.
		//
		// ActiveMQConnection.isDeleted() answers from the connection's OWN advisory-populated
		// set of temp destinations, not from the broker, and refuses to publish to anything
		// missing from it. A connection that has not yet received the advisory for someone
		// else's reply queue therefore reports it as deleted while it is alive. Since
		// ConsumerContextJms opens a connection per message, an RPC reply is published from a
		// connection that may be milliseconds old -- measured in CI, a reply was refused 3.4ms
		// after the queue was created, and the genuine removal advisory arrived 60s later at
		// teardown. The caller then waits out its whole timeout for a reply that was never sent.
		//
		// With advisory watching off, isDeleted() always returns false and the broker decides,
		// which is the correct authority. See docs/MESSAGING.md and issue #1863.
		activeMQConnectionFactory.setWatchTopicAdvisories(false);
		return activeMQConnectionFactory;
	}
	
	@Override
	public MessageConsumer createConsumer(Session jmsSession, VCDestination vcDestination, VCMessageSelector vcSelector, int prefetchLimit) throws JMSException {
		Destination jmsDestination;
		MessageConsumer jmsMessageConsumer;
		if (vcDestination instanceof VCellQueue){
			jmsDestination = jmsSession.createQueue(vcDestination.getName()+"?consumer.prefetchSize="+prefetchLimit);							
		}else{
			jmsDestination = jmsSession.createTopic(vcDestination.getName()+"?consumer.prefetchSize="+prefetchLimit);							
		}
		if (vcSelector==null){
			jmsMessageConsumer = jmsSession.createConsumer(jmsDestination);
		}else{
			jmsMessageConsumer = jmsSession.createConsumer(jmsDestination,vcSelector.getSelectionString());
		}
		return jmsMessageConsumer;
	}
	
	/**
	 * lazily retrieve from {@link PropertyLoader#jmsURL}
	 * @return static string
	 */
	private String jmsUrl(String jmshost, int jmsport) {
		// Bound the failover reconnect loop so a wedged transport (e.g. ActiveMQ client
		// "Timer already cancelled" race) eventually surfaces an IOException to the
		// TransportListener, letting the JVM exit and K8s recycle the pod.
		// startupMaxReconnectAttempts=-1 keeps initial connect unbounded so pod boot
		// tolerates a slow broker.
		return "failover:(tcp://" + jmshost + ":" + jmsport + ")"
				+ "?maxReconnectAttempts=20"
				+ "&startupMaxReconnectAttempts=-1"
				+ "&useExponentialBackOff=true"
				+ "&initialReconnectDelay=1000"
				+ "&maxReconnectDelay=30000";
	}
}

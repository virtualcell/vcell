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
import cbit.vcell.message.jms.VCMessagingServiceJms;
import cbit.vcell.resource.PropertyLoader;


public class VCMessagingServiceActiveMQ extends VCMessagingServiceJms implements VCMessagingService {

	public VCMessagingServiceActiveMQ() {
		super();
	}
	
	@Override
	public ConnectionFactory createConnectionFactory(){
		//return new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false&broker.useJmx=false&create=false");
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(jmsUrl(jmshost, jmsport ));
		activeMQConnectionFactory.setTrustAllPackages(true);
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

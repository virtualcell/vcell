package cbit.vcell.message.jms.activeMQ;

import java.util.Objects;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.scijava.Priority;
import org.scijava.plugin.Plugin;

import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.jms.VCMessagingServiceJms;
import cbit.vcell.resource.PropertyLoader;

@Plugin(type = VCMessagingService.class)
public class VCMessagingServiceActiveMQ extends VCMessagingServiceJms {
	private static String JMS_URL;
	
	public VCMessagingServiceActiveMQ() throws VCMessagingException {
		super();
		setPriority(Priority.NORMAL_PRIORITY);
//		String jmsProvider = PropertyLoader.getRequiredProperty(PropertyLoader.jmsProvider);
//		if (!jmsProvider.equalsIgnoreCase(PropertyLoader.jmsProviderValueActiveMQ)){
//			throw new RuntimeException("unrecognized jms provider : "+jmsProvider);
//		}
	}
	
	@Override
	public ConnectionFactory createConnectionFactory(){
		//return new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false&broker.useJmx=false&create=false");
		ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(jmsUrl( ));
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
	private String jmsUrl( ) {
		if (JMS_URL == null) {
			String jmshost = PropertyLoader.getRequiredProperty(PropertyLoader.jmsHostInternal);
			String jmsport = PropertyLoader.getRequiredProperty(PropertyLoader.jmsPortInternal);
			String jmsurl = "failover:(tcp://"+jmshost+":"+jmsport+")";
			JMS_URL = jmsurl;
			Objects.requireNonNull(JMS_URL);
		}
		return JMS_URL;
	}
}

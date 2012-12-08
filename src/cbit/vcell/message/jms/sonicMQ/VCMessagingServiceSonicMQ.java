package cbit.vcell.message.jms.sonicMQ;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.vcell.util.PropertyLoader;

import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.jms.VCMessagingServiceJms;

public class VCMessagingServiceSonicMQ extends VCMessagingServiceJms {
	
	public VCMessagingServiceSonicMQ() {
		super();
	}
	
	@Override
	public ConnectionFactory createConnectionFactory() throws JMSException {
		//return new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false&broker.useJmx=false&create=false");
		String jmsUrl = PropertyLoader.getRequiredProperty(PropertyLoader.jmsURL);
		String jmsUser = PropertyLoader.getRequiredProperty(PropertyLoader.jmsUser);
		String jmsPassword = PropertyLoader.getRequiredProperty(PropertyLoader.jmsPassword);
		progress.message.jclient.ConnectionFactory sonicConnectionFactory = new progress.message.jclient.ConnectionFactory(jmsUrl, jmsUser, jmsPassword);
		sonicConnectionFactory.setFaultTolerant(true);
		return sonicConnectionFactory;
	}
	
	@Override
	protected void init(boolean bStartBroker) throws VCMessagingException {
		if (bStartBroker){
			throw new VCMessagingException("embedded broker not supported");
		}
	}
	

	@Override
	public MessageConsumer createConsumer(Session jmsSession, VCDestination vcDestination, VCMessageSelector vcSelector, int prefetchLimit) throws JMSException {
		Destination jmsDestination;
		progress.message.jclient.MessageConsumer sonicMessageConsumer;
		if (vcDestination instanceof VCellQueue){
			jmsDestination = jmsSession.createQueue(vcDestination.getName());							
		}else{
			jmsDestination = jmsSession.createTopic(vcDestination.getName());							
		}
		if (vcSelector==null){
			sonicMessageConsumer = (progress.message.jclient.MessageConsumer)jmsSession.createConsumer(jmsDestination);
		}else{
			sonicMessageConsumer = (progress.message.jclient.MessageConsumer)jmsSession.createConsumer(jmsDestination,vcSelector.getSelectionString());
		}
		sonicMessageConsumer.setPrefetchCount(prefetchLimit);
		return sonicMessageConsumer;
	}
}

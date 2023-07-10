package cbit.vcell.message.jms.activeMQ;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessageSelector;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.jms.VCMessagingServiceJms;

public class VCMessagingServiceEmbedded extends VCMessagingServiceJms implements VCMessagingService {
	private final static Logger lg = LogManager.getLogger(VCMessagingServiceEmbedded.class);
	private BrokerService broker = null;
	private boolean initialized = false;
	
	public VCMessagingServiceEmbedded() {
		super();
	}
	
	@Override
	public ConnectionFactory createConnectionFactory() throws VCMessagingException {
		if (!initialized){
			initialized = true;
			init();
		}
		return new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false&broker.useJmx=false&create=false");
	}
	
	private void init() throws VCMessagingException {
		this.broker = new BrokerService();
		this.broker.setPersistent(false);
		this.broker.setUseJmx(false);
		try {
//			TransportConnector connector = new TransportConnector();
//			String jmsUrl = PropertyLoader.getRequiredProperty(PropertyLoader.jmsURL);
//			connector.setUri(new URI(jmsUrl));
//			broker.addConnector(connector);
			broker.start();
		} catch (Exception e) {
			lg.error(e);
			throw new VCMessagingException(e.getMessage(),e);
		}
	}

	@Override
	public MessageConsumer createConsumer(Session jmsSession, VCDestination vcDestination, VCMessageSelector vcSelector, int prefetchLimit) throws JMSException, VCMessagingException {
		if (!initialized){
			initialized = true;
			init();
		}
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

}

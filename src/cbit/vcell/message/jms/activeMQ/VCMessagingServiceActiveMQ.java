package cbit.vcell.message.jms.activeMQ;

import java.net.URI;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.vcell.util.PropertyLoader;

import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.jms.VCMessagingServiceJms;

public class VCMessagingServiceActiveMQ extends VCMessagingServiceJms {
	private BrokerService broker = null;
	
	public VCMessagingServiceActiveMQ() {
		super();
	}
	
	@Override
	public ConnectionFactory createConnectionFactory(){
		//return new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false&broker.useJmx=false&create=false");
		String jmsUrl = PropertyLoader.getRequiredProperty(PropertyLoader.jmsURL);
		return new ActiveMQConnectionFactory(jmsUrl);
	}
	
	@Override
	protected void init(boolean bStartBroker) throws VCMessagingException {
		if (bStartBroker){
			this.broker = new BrokerService();
	
			try {
				TransportConnector connector = new TransportConnector();
				String jmsUrl = PropertyLoader.getRequiredProperty(PropertyLoader.jmsURL);
				connector.setUri(new URI(jmsUrl));
				broker.addConnector(connector);
				broker.start();
			} catch (Exception e) {
				e.printStackTrace();
				throw new VCMessagingException(e.getMessage());
			}
		}
	}

}

package cbit.vcell.message.jms.sonicMQ;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.vcell.util.PropertyLoader;

import cbit.vcell.message.VCMessagingException;
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

}

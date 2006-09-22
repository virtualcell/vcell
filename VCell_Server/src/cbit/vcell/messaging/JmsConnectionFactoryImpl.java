package cbit.vcell.messaging;
import javax.jms.JMSException;

/**
 * Insert the type's description here.
 * Creation date: (7/24/2003 11:35:47 AM)
 * @author: Fei Gao
 */
public class JmsConnectionFactoryImpl implements JmsConnectionFactory {
	private JmsProvider jmsProvider = null;

/**
 * JmsConnectionFactory constructor comment.
 */
public JmsConnectionFactoryImpl() throws JMSException {
	super();
	jmsProvider = JmsProviderFactory.getJmsProvider();
}


/**
 * JmsConnectionFactory constructor comment.
 */
public JmsConnectionFactoryImpl(JmsProvider jmsProvider0) {
	super();
	jmsProvider = jmsProvider0;
}


/**
 * JmsConnectionFactory constructor comment.
 */
public JmsConnectionFactoryImpl(String provider, String url, String userid, String password) throws JMSException {
	super();
	jmsProvider = JmsProviderFactory.getJmsProvider(provider, url, userid, password);
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2003 11:36:45 AM)
 * @return cbit.vcell.messaging.JmsQueueConnection
 * @param jmsFactory cbit.vcell.messaging.JmsFactory
 */
public VCellQueueConnection createQueueConnection() throws JMSException {
	return new JmsQueueConnection(jmsProvider);
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2003 11:36:45 AM)
 * @return cbit.vcell.messaging.JmsQueueConnection
 * @param jmsFactory cbit.vcell.messaging.JmsFactory
 */
public VCellTopicConnection createTopicConnection() throws JMSException {
	return new JmsTopicConnection(jmsProvider);
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2003 11:36:45 AM)
 * @return cbit.vcell.messaging.JmsQueueConnection
 * @param jmsFactory cbit.vcell.messaging.JmsFactory
 */
public VCellXAQueueConnection createXAQueueConnection() throws JMSException {
	return new JmsXAQueueConnection(jmsProvider);
}


/**
 * Insert the method's description here.
 * Creation date: (7/24/2003 11:36:45 AM)
 * @return cbit.vcell.messaging.JmsQueueConnection
 * @param jmsFactory cbit.vcell.messaging.JmsFactory
 */
public VCellXATopicConnection createXATopicConnection() throws JMSException {
	return new JmsXATopicConnection(jmsProvider);
}
}
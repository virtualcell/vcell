package cbit.vcell.messaging;
import javax.jms.JMSException;

/**
 * Insert the type's description here.
 * Creation date: (2/24/2004 2:24:10 PM)
 * @author: Fei Gao
 */
public interface JmsConnectionFactory {
	public VCellXATopicConnection createXATopicConnection() throws JMSException;

	public VCellQueueConnection createQueueConnection() throws JMSException;


	public VCellTopicConnection createTopicConnection() throws JMSException;


	public VCellXAQueueConnection createXAQueueConnection() throws JMSException;
}
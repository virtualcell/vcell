package cbit.vcell.messaging;
import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (7/31/2003 8:40:09 AM)
 * @author: Fei Gao
 */
public interface VCellQueueSession extends VCellJmsSession {
	public java.util.List browseAllMessages(String queueName, String msgSelector) throws JMSException;


	public Message receiveMessage(String queueName, long timeout) throws JMSException;


	public Message receiveMessage(String queueName, String selector, long timeout) throws JMSException;


public Message request(Object source, String queueName, Message message, int deliveryMode, long timeout) throws JMSException;


public void sendMessage(String queueName, Message message, int deliveryMode, long timeToLive) throws JMSException;


public void sendMessage(Queue queue, Message message, int deliveryMode, long timeToLive) throws JMSException;


public void setPrefetchCount(int pc) throws JMSException;


public void setPrefetchThreshold(int pt) throws JMSException;
}
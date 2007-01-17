package cbit.vcell.messaging;
import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (7/31/2003 8:40:09 AM)
 * @author: Fei Gao
 */
public interface VCellTopicSession extends VCellJmsSession {
	public void publishMessage(String topicName, Message message) throws JMSException;


	public void publishMessage(Topic topic, Message message) throws JMSException;


public Message request(Object source, String queueName, Message message, long timeout) throws JMSException;
}
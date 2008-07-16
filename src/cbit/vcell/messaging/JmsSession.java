package cbit.vcell.messaging;

import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (10/8/2003 1:13:29 PM)
 * @author: Fei Gao
 */
public interface JmsSession {
	// for queue and topic
	public void close() throws JMSException;
	public void commit() throws JMSException;
	public Message createMessage() throws JMSException;
	public ObjectMessage createObjectMessage(java.io.Serializable obj) throws JMSException;
	public Message createTextMessage(String text) throws JMSException;
	public void rollback() throws JMSException;		
	void setupOnException();
	void setupSession() throws JMSException;
	
	// for queue
	//public List<?> browseAllMessages(String queueName, String msgSelector) throws JMSException;
	public Message receiveMessage(String queueName, long timeout) throws JMSException;
	public Message receiveMessage(String queueName, String selector, long timeout) throws JMSException;
	public Message queueRequest(Object source, String queueName, Message message, int deliveryMode, long timeout) throws JMSException;
	public void sendMessage(String queueName, Message message, int deliveryMode, long timeToLive) throws JMSException;	
	public void sendMessage(Queue queue, Message message, int deliveryMode, long timeToLive) throws JMSException;	
	public void setPrefetchCount(int pc) throws JMSException;	
	public void setPrefetchThreshold(int pt) throws JMSException;
	public void setupQueueListener(String queueName, String msgSelector, MessageListener listener) throws JMSException;
	
	// for topic
	public void publishMessage(String topicName, Message message) throws JMSException;
	public void publishMessage(Topic topic, Message message) throws JMSException;
	public Message topicRequest(Object source, String topicName, Message message, long timeout) throws JMSException;
	public void setupTopicListener(String topicName, String msgSelector, MessageListener listener) throws JMSException;
}
package cbit.vcell.messaging;
import java.io.Serializable;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;
import javax.jms.TopicPublisher;
import javax.jms.TopicSubscriber;

/**
 * Insert the type's description here.
 * Creation date: (10/8/2003 1:09:25 PM)
 * @author: Fei Gao
 */
public class JmsTopicSession extends AbstractJmsSession implements VCellTopicSession {
	protected javax.jms.TopicSession topicSession = null;
	private VCellTopicConnection vcTopicConn = null;

/**
 * JmsTopicSession constructor comment.
 */
protected JmsTopicSession() {
	super();
}


/**
 * JmsTopicSession constructor comment.
 */
public JmsTopicSession(VCellTopicConnection topicConn0, boolean transac, int ackMode0) {
	super(transac, ackMode0);
	vcTopicConn = topicConn0;
}


/**
 * close method comment.
 */
public void close() throws JMSException {
	synchronized (this) {
		topicSession.close();	
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 1:34:12 PM)
 */
public final void commit() throws JMSException {
	if (transactional) {
		synchronized (this) {
			topicSession.commit();	
		}
	} else {
		throw new JMSException("Commiting back on a non-transactional session");
	}
		
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:35:52 AM)
 * @return javax.jms.Destination
 */
protected final MessageConsumer createConsumer(Destination dest, String msgSelector) throws JMSException {
	synchronized (this) {
		return topicSession.createSubscriber((Topic)dest, msgSelector, true);	
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:27:23 AM)
 * @return javax.jms.Destination
 */
protected final Destination createDestination(String topicName) throws JMSException {
	synchronized (this) {
		return topicSession.createTopic(topicName);			
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:35:52 AM)
 * @return javax.jms.Destination
 */
protected final MessageConsumer createDurableSubscriber(Destination dest, String subscriptionName, String msgSelector) throws JMSException {
	synchronized (this) {
		return topicSession.createDurableSubscriber((Topic)dest, subscriptionName, msgSelector, true);	
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2003 9:17:36 AM)
 * @return javax.jms.ObjectMessage
 * @param obj java.lang.Object
 */
public final Message createMessage() throws JMSException {
	//checkConnection();

	synchronized (this) {
		return topicSession.createMessage();	
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (5/22/2003 9:17:36 AM)
 * @return javax.jms.ObjectMessage
 * @param obj java.lang.Object
 */
public final ObjectMessage createObjectMessage(Serializable obj) throws JMSException {
	//checkConnection();

	synchronized (this) {
		return topicSession.createObjectMessage(obj);	
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:46:45 AM)
 * @return javax.jms.Destination
 */
protected final MessageProducer createProducer(Destination dest) throws JMSException {
	synchronized (this) {
		return topicSession.createPublisher((Topic)dest);	
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:40:47 AM)
 * @return javax.jms.Destination
 */
protected final Destination createTempDestination() throws JMSException {
	synchronized (this) {
		return topicSession.createTemporaryTopic();	
	}	
}


/**
 * createTextMessage method comment.
 */
public final Message createTextMessage(String text) throws JMSException {
	//checkConnection();

	synchronized (this) {
		return topicSession.createTextMessage(text);	
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 10:49:27 AM)
 * @return javax.jms.QueueReceiver
 * @param queueName java.lang.String
 */
private void deleteTempTopic(Object source) throws JMSException {
	if (tempDestMap == null) {
		return;
	}

	TemporaryTopic tt = null;
	synchronized(tempDestMap) {
		tt = (TemporaryTopic)tempDestMap.remove(source);	
	}

	if (tt != null) {
		tt.delete();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.TopicPublisher
 * @param topicName java.lang.String
 */
private TopicPublisher getPublisher(Topic topic) throws JMSException {	
	return (TopicPublisher)getProducer(topic);
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.TopicSender
 * @param topicName java.lang.String
 */
private TopicSubscriber getSubscriber(Topic topic, String msgSelector) throws JMSException {
	return (TopicSubscriber)getConsumer(topic, msgSelector);
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
private Topic getTempTopic(Object source) throws JMSException {
	return (Topic)getTempDestination(source);
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.TopicSender
 * @param topicName java.lang.String
 */
private Topic getTopic(String topicName) throws JMSException {
	return (Topic)getDestination(topicName);
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
public final void publishMessage(String topicName, Message message) throws JMSException {
	publishMessage(getTopic(topicName), message);
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
public final void publishMessage(Topic topic, Message message) throws JMSException {
	//checkConnection();
	
	TopicPublisher tp = getPublisher(topic);	
	synchronized (this) {			
		tp.publish(message);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (6/16/2003 11:49:46 AM)
 * @return javax.jms.Message
 * @param message javax.jms.Message
 * @exception javax.jms.JMSException The exception description.
 */
public final Message request(Object source, String topicName, Message message, long timeout) throws JMSException {
	//checkConnection();
	
	Topic tempTopic = getTempTopic(source);		
	message.setJMSReplyTo(tempTopic);
	TopicSubscriber ts = getSubscriber(tempTopic, null); // since topic messages don't get retained, have to create receiver before publish the messages
	publishMessage(topicName, message);	 // message id gets set after the message is sent, so can't set filter before publishing the message

	Message reply = null;
	synchronized (this) {
		reply = ts.receive(timeout);
	}
	if (reply != null) {		
		String corrID = reply.getJMSCorrelationID();
		if (corrID != null && corrID.equals(message.getJMSMessageID())) {
			return reply;
		}
	}
	
	return null;
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 1:34:12 PM)
 */
public final void rollback() throws JMSException {
	if (transactional) {
		synchronized (this) {
			topicSession.rollback();	
		}
	} else {
		throw new JMSException("Rolling back on a non-transactional session");
	}
		
}


/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 12:02:14 PM)
 * @param queueConnl javax.jms.QueueConnection
 */
protected synchronized void setupSession() throws JMSException {
	topicSession = vcTopicConn.getConnection().createTopicSession(transactional, ackMode);
}
}
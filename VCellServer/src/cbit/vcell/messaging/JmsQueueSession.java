package cbit.vcell.messaging;
import javax.jms.*;
import java.util.*;

import cbit.util.SessionLog;

import java.io.Serializable;
import javax.jms.Queue;

/**
 * Insert the type's description here.
 * Creation date: (10/8/2003 10:44:46 AM)
 * only one receiver is allowed in one session
 * @author: Fei Gao
 */
public class JmsQueueSession extends AbstractJmsSession implements VCellQueueSession {
	protected javax.jms.QueueSession queueSession = null;
	private VCellQueueConnection vcQueueConn = null;
	private int prefetchCount = -1; // use default value
	private int prefetchThreshold = -1; // use default value

/**
 * JmsQueueSession constructor comment.
 */
protected JmsQueueSession() throws JMSException {
	super();
}


/**
 * JmsQueueSession constructor comment.
 */
public JmsQueueSession(VCellQueueConnection queueConn0, boolean transac, int ackMode0) throws JMSException {
	super(transac, ackMode0);	
	vcQueueConn = queueConn0;
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
public List browseAllMessages(String queueName, String selector) throws JMSException {
	//checkConnection();
	
	synchronized (this) {
		try {
			Queue queue = getQueue(queueName);
			QueueBrowser queueBrowser = queueSession.createBrowser(queue, selector);
			java.util.Enumeration enum1 = queueBrowser.getEnumeration();
				
			List msgList = new ArrayList();
			while (enum1.hasMoreElements()){
				Object obj = enum1.nextElement();
				if (obj == null) {
					break;
				}					
				msgList.add(obj);
			}
			queueBrowser.close();
			
			return msgList;
		} catch (Exception ex) {
			throw new JMSException(ex.getMessage());
		}
	}
}


/**
 * close method comment.
 */
public void close() throws JMSException {
	synchronized (this) {
		queueSession.close();	
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 10:55:52 AM)
 */
public final void commit() throws JMSException {
	if (transactional) {
		synchronized (this) {			
			queueSession.commit();
		}
	} else {
		throw new JMSException("Commiting back on a non-transactional session");
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:35:25 AM)
 * @return javax.jms.Destination
 */
protected final MessageConsumer createConsumer(Destination dest, String msgSelector) throws JMSException {
	synchronized (this) {
		QueueReceiver qr = queueSession.createReceiver((Queue)dest, msgSelector);
		if (prefetchCount > 0) {
			vcQueueConn.getJmsProvider().setPrefetchCount(qr, prefetchCount);
		}
		if (prefetchThreshold >= 0) {
			vcQueueConn.getJmsProvider().setPrefetchThreshold(qr, prefetchThreshold);
		}
		return qr;
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:26:03 AM)
 * @return javax.jms.Destination
 */
protected final Destination createDestination(String queueName) throws JMSException {
	synchronized (this) {
		return queueSession.createQueue(queueName);		
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
		return queueSession.createMessage();	
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
		return queueSession.createObjectMessage(obj);			
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:46:14 AM)
 * @return javax.jms.Destination
 */
protected final MessageProducer createProducer(Destination dest) throws JMSException {	
	synchronized (this) {
		return queueSession.createSender((Queue)dest);		
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:40:02 AM)
 * @return javax.jms.Destination
 */
protected  final Destination createTempDestination() throws JMSException {
	//checkConnection();
	
	synchronized (this) {
		return queueSession.createTemporaryQueue();	
	}	
}


/**
 * createTextMessage method comment.
 */
public final Message createTextMessage(String text) throws JMSException {
	//checkConnection();

	synchronized (this) {
		return queueSession.createTextMessage(text);			
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 10:49:27 AM)
 * @return javax.jms.QueueReceiver
 * @param queueName java.lang.String
 */
private void deleteTempQueue(Object source) throws JMSException {
	if (tempDestMap == null) {
		return;
	}

	TemporaryQueue tq = null;
	synchronized(tempDestMap) {
		tq = (TemporaryQueue)tempDestMap.remove(source);	
	}

	if (tq != null) {
		tq.delete();
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
private Queue getQueue(String queueName) throws JMSException {	
	return (Queue)getDestination(queueName);
}


/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 10:49:27 AM)
 * @return javax.jms.QueueReceiver
 * @param queueName java.lang.String
 */
private QueueReceiver getReceiver(String queueName, String msgSelector) throws JMSException {	
	return getReceiver(getQueue(queueName), msgSelector);
}


/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 10:49:27 AM)
 * @return javax.jms.QueueReceiver
 * @param queueName java.lang.String
 */
private QueueReceiver getReceiver(Queue queue, String msgSelector) throws JMSException {
	return (QueueReceiver)getConsumer(queue, msgSelector);
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
private QueueSender getSender(Queue queue) throws JMSException {
	return (QueueSender)getProducer(queue);
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
private TemporaryQueue getTempQueue(Object source) throws JMSException {
	return (TemporaryQueue)getTempDestination(source);
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
public final Message receiveMessage(String queueName, long timeout) throws JMSException {
	return receiveMessage(queueName, null, timeout);
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
public final Message receiveMessage(String queueName, String selector, long timeout) throws JMSException {
	//checkConnection();
	
	QueueReceiver qr = getReceiver(queueName, selector);
	synchronized (this) {
		return qr.receive(timeout);			
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (6/16/2003 11:49:46 AM)
 * @return javax.jms.Message
 * @param message javax.jms.Message
 * @exception javax.jms.JMSException The exception description.
 */
public final Message request(Object source, String queueName, Message message, int deliveryMode, long timeout) throws JMSException {
	//checkConnection();

	TemporaryQueue tempQueue = getTempQueue(source);		
	message.setJMSReplyTo(tempQueue);
	sendMessage(getQueue(queueName), message, deliveryMode, Message.DEFAULT_PRIORITY, timeout);
	
	String filter = MessageConstants.JMSCORRELATIONID_PROPERTY + "='" + message.getJMSMessageID() + "'";
	QueueReceiver qr = getReceiver(tempQueue, filter);
	Message reply = null;
	synchronized (this) {
		reply = qr.receive(timeout);
		closeAConsumer(qr);
	}

	if (reply == null) {
		System.out.println("Request timed out");
		deleteTempQueue(source);
	}

	return reply;
	
}


/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 10:56:23 AM)
 */
public final void rollback() throws JMSException {
	if (transactional) {
		synchronized (this) {
			queueSession.rollback();	
		}
	} else {
		throw new JMSException("Rolling back on a non-transactional session");
	}
}


/**
 * sendMessage method comment.
 */
public void sendMessage(String queueName, javax.jms.Message message, int deliveryMode, long timeToLive) throws javax.jms.JMSException {
	sendMessage(getQueue(queueName), message, deliveryMode, Message.DEFAULT_PRIORITY, timeToLive);
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
private final void sendMessage(Queue queue, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
	//checkConnection();
	
	QueueSender qs = getSender(queue);	
	synchronized (this) {
		qs.send(message, deliveryMode, priority, timeToLive);			
	}
}


/**
 * sendMessage method comment.
 */
public void sendMessage(javax.jms.Queue queue, javax.jms.Message message, int deliveryMode, long timeToLive) throws javax.jms.JMSException {
	sendMessage(queue, message, deliveryMode, Message.DEFAULT_PRIORITY, timeToLive);
}


/**
 * setPrefetchCount method comment.
 */
public void setPrefetchCount(int pc) throws javax.jms.JMSException {
	prefetchCount = pc;
}


/**
 * setPrefetchThreshold method comment.
 */
public void setPrefetchThreshold(int pt) throws javax.jms.JMSException {
	prefetchThreshold = pt;
}


/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 12:02:14 PM)
 * @param queueConnl javax.jms.QueueConnection
 */
protected synchronized void setupSession() throws JMSException {
	queueSession = vcQueueConn.getConnection().createQueueSession(transactional, ackMode);
}
}
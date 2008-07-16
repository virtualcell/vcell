package cbit.vcell.messaging;
import javax.jms.*;
import javax.jms.Queue;
import java.io.Serializable;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (10/8/2003 10:45:11 AM)
 * @author: Fei Gao
 */
public class JmsSessionImp implements JmsSession {
	/**
	 * A session is a single-threaded context for producing and consuming messages. 
	 * You use sessions to create message producers, message consumers, and messages. 
	 * Sessions serialize the execution of message listeners;
	 */
	
	protected List<MessageProducer> producerList = null;
	protected List<Destination> destList = null;
	protected Map<Object, TemporaryQueue> tempQueueMap = null;
	protected Map<Object, TemporaryTopic> tempTopicMap = null;
	protected List<MessageConsumer> consumerList = null;
	protected boolean transactional = false;
	protected int ackMode = Session.AUTO_ACKNOWLEDGE;
	protected List<MessageConsumer> listenerList = null;

	protected Session session = null;
	
	private JmsConnection vcConn = null;
	private int prefetchCount = -1; // use default value
	private int prefetchThreshold = -1; // use default value

/**
 * JmsSession constructor comment.
 */
protected JmsSessionImp() {
	super();
}

protected JmsSessionImp(JmsConnection c, boolean transac, int ackMode0) {
	super();
	vcConn = c;
	transactional = transac;
	ackMode = ackMode0;
}

/**
 * Insert the method's description here.
 * Creation date: (10/9/2003 11:45:24 AM)
 * @param queueConn cbit.vcell.messaging.VCellQueueConnection
 */
protected final void clearLists() {
	if (destList != null) {
		synchronized (destList) {
			destList.clear();			
		}
	}
	if (tempQueueMap != null) {
		synchronized (tempQueueMap) {
			tempQueueMap.clear();			
		}
	}	
	if (tempTopicMap != null) {
		synchronized (tempTopicMap) {
			tempTopicMap.clear();			
		}
	}	
	if (producerList != null) {
		synchronized (producerList) {
			producerList.clear();			
		}
	}
	if (consumerList != null) {
		synchronized (consumerList) {
			consumerList.clear();	
		}		
	}
}

public void close() throws JMSException {
	synchronized (this) {
		session.close();	
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 10:55:52 AM)
 */
public final void commit() throws JMSException {
	if (transactional) {
		synchronized (this) {			
			session.commit();
		}
	} else {
		throw new JMSException("Commiting back on a non-transactional session");
	}
}

public final void rollback() throws JMSException {
	if (transactional) {
		synchronized (this) {
			session.rollback();	
		}
	} else {
		throw new JMSException("Rolling back on a non-transactional session");
	}
}

/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 10:49:27 AM)
 * @return javax.jms.QueueReceiver
 * @param queueName java.lang.String
 */
protected void closeAConsumer(MessageConsumer mc) throws JMSException {
	if (consumerList == null) {
		return;
	}
	
	synchronized(consumerList) {
		consumerList.remove(mc);	
	}

	synchronized (mc) {
		mc.close();
		mc = null;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:25:41 AM)
 * @return javax.jms.Destination
 */
protected final MessageConsumer createConsumer(Destination dest, String msgSelector) throws JMSException {
	synchronized (this) {
		MessageConsumer qr = session.createConsumer(dest, msgSelector);
		if (prefetchCount > 0) {
			vcConn.getJmsProvider().setPrefetchCount(qr, prefetchCount);
		}
		if (prefetchThreshold >= 0) {
			vcConn.getJmsProvider().setPrefetchThreshold(qr, prefetchThreshold);
		}
		return qr;
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:25:41 AM)
 * @return javax.jms.Destination
 */
protected final Queue createQueue(String destName) throws JMSException {
	synchronized (this) {
		return session.createQueue(destName);		
	}
}

protected final Topic createTopic(String topicName) throws JMSException {
	synchronized (this) {
		return session.createTopic(topicName);		
	}	
}

/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:25:41 AM)
 * @return javax.jms.Destination
 */
protected final MessageProducer createProducer(Destination dest) throws JMSException {	
	synchronized (this) {
		return session.createProducer(dest);		
	}	
}

/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:25:41 AM)
 * @return javax.jms.Destination
 */
protected  final TemporaryQueue createTempQueue() throws JMSException {
	//checkConnection();
	
	synchronized (this) {
		return session.createTemporaryQueue();	
	}	
}

protected  final TemporaryTopic createTempTopic() throws JMSException {
	//checkConnection();
	
	synchronized (this) {
		return session.createTemporaryTopic();	
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 10:49:27 AM)
 * @return javax.jms.QueueReceiver
 * @param queueName java.lang.String
 */
protected final MessageConsumer getConsumer(Destination dest, String msgSelector) throws JMSException {
	if (dest == null) {
		return null;
	}
	if (consumerList == null) {
		consumerList = Collections.synchronizedList(new ArrayList<MessageConsumer>());
	}	
	
	synchronized(consumerList) {
		for (MessageConsumer mc : consumerList) {
			if (mc == null) {
				continue;
			}				
			if (getDestinationName(mc).equals(getDestinationName(dest)) && 
					(mc.getMessageSelector() == null && msgSelector == null || mc.getMessageSelector() != null && msgSelector != null && mc.getMessageSelector().equals(msgSelector))) {
				return mc;
			}
		}

		MessageConsumer mc = createConsumer(dest, msgSelector);
		consumerList.add(mc);
		return mc;		
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param destName java.lang.String
 */
protected final Destination getQueue(String destName) throws JMSException {
	if (destList == null) {
		destList = Collections.synchronizedList(new ArrayList<Destination>());
	}

	synchronized(destList) {	
		for (Destination dest : destList) {
			if (dest instanceof Queue && destName.equals(getDestinationName(dest))) {
				return dest;
			}
		}

		Destination dest = createQueue(destName);	
		destList.add(dest);	
		return dest;		
	}
}

protected final Destination getTopic(String destName) throws JMSException {
	if (destList == null) {
		destList = Collections.synchronizedList(new ArrayList<Destination>());
	}

	synchronized(destList) {	
		for (Destination dest : destList) {
			if (dest instanceof Topic && destName.equals(getDestinationName(dest))) {
				return dest;
			}
		}

		Destination dest = createTopic(destName);	
		destList.add(dest);	
		return dest;		
	}
}

private String getDestinationName(Destination dest) throws JMSException {
	if (dest == null) {
		return null;
	}		
	return (dest instanceof Queue) ? ((Queue)dest).getQueueName() : ((Topic)dest).getTopicName();
}

/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:55:24 AM)
 * @return java.lang.String
 * @param dest javax.jms.Destination
 */
private String getDestinationName(MessageConsumer mc) throws JMSException {
	if (mc == null) {
		return null;
	}		
	return (mc instanceof QueueReceiver) ? ((QueueReceiver)mc).getQueue().getQueueName() : ((TopicSubscriber)mc).getTopic().getTopicName();
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:55:24 AM)
 * @return java.lang.String
 * @param dest javax.jms.Destination
 */
private String getDestinationName(MessageProducer mp) throws JMSException {
	if (mp == null) {
		return null;
	}
		
	return (mp instanceof QueueSender) ? ((QueueSender)mp).getQueue().getQueueName() : ((TopicPublisher)mp).getTopic().getTopicName();
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
protected final MessageProducer getProducer(Destination dest) throws JMSException {
	if (dest == null) {
		return null;
	}	
	if (producerList == null) {
		producerList = Collections.synchronizedList(new ArrayList<MessageProducer>());
	}
		
	synchronized(producerList) {	
		for (MessageProducer mp : producerList) {
			if (mp == null) {
				continue;
			}				
			if (getDestinationName(dest).equals(getDestinationName(mp))) {
				return mp;
			}
		}

		MessageProducer mp =  createProducer(dest);	
		producerList.add(mp);
		return mp;			
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
protected final TemporaryQueue getTempQueue(Object source) throws JMSException {
	if (tempQueueMap == null) {
		tempQueueMap = Collections.synchronizedMap(new HashMap<Object, TemporaryQueue>());
	}

	TemporaryQueue tempQueue = null;
	synchronized(tempQueueMap) {
		tempQueue = tempQueueMap.get(source);
		if (tempQueue == null) {
			tempQueue = createTempQueue();	
			tempQueueMap.put(source, tempQueue);	
		}
	}

	return tempQueue;
}

protected final TemporaryTopic getTempTopic(Object source) throws JMSException {
	if (tempTopicMap == null) {
		tempTopicMap = Collections.synchronizedMap(new HashMap<Object, TemporaryTopic>());
	}

	TemporaryTopic tempTopic = null;
	synchronized(tempTopicMap) {
		tempTopic = tempTopicMap.get(source);
		if (tempTopic == null) {
			tempTopic = createTempTopic();	
			tempTopicMap.put(source, tempTopic);	
		}
	}

	return tempTopic;
}

/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 1:54:28 PM)
 */
public synchronized void setupOnException() {	
	try {
		clearLists();
		setupSession();
			
		if (listenerList != null) {
			List<MessageConsumer> tempList = new ArrayList<MessageConsumer>();
			tempList.addAll(listenerList);
			listenerList.clear();

			for (MessageConsumer mc : tempList) {
				setupListener(mc);
			}
			
			tempList.clear();
		}
			
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
public final void setupQueueListener(String queueName, String msgSelector, MessageListener listener) throws JMSException {
	Destination dest = getQueue(queueName);
	setupListener(dest, msgSelector, listener);
}

public final void setupTopicListener(String topicName, String msgSelector, MessageListener listener) throws JMSException {
	Destination	dest = getTopic(topicName);
	setupListener(dest, msgSelector, listener);
}


public final void setupListener(Destination dest, String msgSelector, MessageListener listener) throws JMSException {
	MessageConsumer mc = createConsumer(dest, msgSelector);
	mc.setMessageListener(listener);	

	if (listenerList == null) {
		listenerList = Collections.synchronizedList(new ArrayList<MessageConsumer>());
	}

	synchronized (listenerList) {
		listenerList.add(mc);
	}		
	System.out.println("----Set up Listener:[" + dest + ", " + mc.getMessageSelector() + "]");
}

public final void setupListener(MessageConsumer mc) throws JMSException {
	Destination dest = null;
	String destName = null;
	if (mc instanceof QueueReceiver) {
		destName = ((QueueReceiver)mc).getQueue().getQueueName();
		dest = getQueue(destName);
	} else {
		destName = ((TopicSubscriber)mc).getTopic().getTopicName();
		dest = getTopic(destName);
	}
	setupListener(dest, mc.getMessageSelector(), mc.getMessageListener());
}

/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:25:41 AM)
 * @return javax.jms.Destination
 */
public synchronized void setupSession() throws JMSException {
	session = vcConn.getConnection().createSession(transactional, ackMode);
}

public final Message createMessage() throws JMSException {
	//checkConnection();

	synchronized (this) {
		return session.createMessage();	
	}	
}

public final ObjectMessage createObjectMessage(Serializable obj) throws JMSException {
	//checkConnection();

	synchronized (this) {
		return session.createObjectMessage(obj);			
	}	
}

public final Message createTextMessage(String text) throws JMSException {
	//checkConnection();

	synchronized (this) {
		return session.createTextMessage(text);			
	}	
}

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
	
	MessageConsumer qr = getConsumer(getQueue(queueName), selector);
	synchronized (this) {
		return qr.receive(timeout);			
	}	
}

public void sendMessage(String queueName, javax.jms.Message message, int deliveryMode, long timeToLive) throws javax.jms.JMSException {
	sendMessage((Queue)getQueue(queueName), message, deliveryMode, Message.DEFAULT_PRIORITY, timeToLive);
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
private final void sendMessage(Queue queue, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
	//checkConnection();
	
	MessageProducer qs = getProducer(queue);	
	synchronized (this) {
		qs.send(message, deliveryMode, priority, timeToLive);			
	}
}


/**
 * sendMessage method comment.
 */
public void sendMessage(Queue queue, javax.jms.Message message, int deliveryMode, long timeToLive) throws javax.jms.JMSException {
	sendMessage(queue, message, deliveryMode, Message.DEFAULT_PRIORITY, timeToLive);
}

/**
 * Insert the method's description here.
 * Creation date: (6/16/2003 11:49:46 AM)
 * @return javax.jms.Message
 * @param message javax.jms.Message
 * @exception javax.jms.JMSException The exception description.
 */
public final Message queueRequest(Object source, String queueName, Message message, int deliveryMode, long timeout) throws JMSException {
	//checkConnection();

	Destination tempDest = getTempQueue(source);		
	message.setJMSReplyTo(tempDest);
	sendMessage((Queue)getQueue(queueName), message, deliveryMode, Message.DEFAULT_PRIORITY, timeout);
	
	String filter = MessageConstants.JMSCORRELATIONID_PROPERTY + "='" + message.getJMSMessageID() + "'";
	MessageConsumer qr = getConsumer(tempDest, filter);
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
		
public final Message topicRequest(Object source, String topicName, Message message, long timeout) throws JMSException {
	Destination tempDest = getTempTopic(source);		
	message.setJMSReplyTo(tempDest);
	MessageConsumer ts = getConsumer(tempDest, null); // since topic messages don't get retained, have to create receiver before publish the messages
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

private void deleteTempQueue(Object source) throws JMSException {
	if (tempQueueMap == null) {
		return;
	}

	TemporaryQueue tq = null;
	synchronized(tempQueueMap) {
		tq = tempQueueMap.remove(source);	
	}

	if (tq != null) {
		tq.delete();
	}
}

public final void publishMessage(String topicName, Message message) throws JMSException {
	publishMessage((Topic)getTopic(topicName), message);
}

/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
public final void publishMessage(Topic topic, Message message) throws JMSException {
	//checkConnection();
	
	MessageProducer tp = getProducer(topic);	
	synchronized (this) {			
		tp.send(message);
	}
}

public void setPrefetchCount(int pc) throws javax.jms.JMSException {
	prefetchCount = pc;
}


public void setPrefetchThreshold(int pt) throws javax.jms.JMSException {
	prefetchThreshold = pt;
}
}
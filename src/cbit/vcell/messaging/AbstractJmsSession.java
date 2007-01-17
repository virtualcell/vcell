package cbit.vcell.messaging;
import javax.jms.*;
import java.util.*;
import javax.jms.Queue;
import cbit.vcell.server.SessionLog;

/**
 * Insert the type's description here.
 * Creation date: (10/8/2003 10:45:11 AM)
 * @author: Fei Gao
 */
public abstract class AbstractJmsSession implements VCellJmsSession {
	protected List producerList = null;
	protected List destList = null;
	protected Map tempDestMap = null;
	protected List consumerList = null;
	protected boolean transactional = false;
	protected int ackMode = Session.AUTO_ACKNOWLEDGE;
	protected List listenerList = null;

/**
 * JmsSession constructor comment.
 */
protected AbstractJmsSession() {
	super();
}


/**
 * JmsSession constructor comment.
 */
protected AbstractJmsSession(boolean transac, int ackMode0) {
	super();
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

	if (tempDestMap != null) {
		synchronized (tempDestMap) {
			tempDestMap.clear();			
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
protected abstract MessageConsumer createConsumer(Destination dest, String msgSelector) throws JMSException;


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:25:41 AM)
 * @return javax.jms.Destination
 */
protected abstract Destination createDestination(String destName) throws JMSException;


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:25:41 AM)
 * @return javax.jms.Destination
 */
protected abstract MessageProducer createProducer(Destination dest) throws JMSException;


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:25:41 AM)
 * @return javax.jms.Destination
 */
protected abstract Destination createTempDestination() throws JMSException;


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
		consumerList = Collections.synchronizedList(new ArrayList());
	}
	
	MessageConsumer mc = null;	
	synchronized(consumerList) {
		Iterator iter = consumerList.iterator();
		while (iter.hasNext()) {
			mc = (MessageConsumer)iter.next();
			if (mc == null) {
				continue;
			}				
			if (getDestinationName(mc).equals(getDestinationName(dest)) && 
					(mc.getMessageSelector() == null && msgSelector == null || mc.getMessageSelector() != null && msgSelector != null && mc.getMessageSelector().equals(msgSelector))) {
				return mc;
			}
		}

		mc = createConsumer(dest, msgSelector);
		consumerList.add(mc);
		return mc;		
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2003 2:20:38 PM)
 * @return javax.jms.QueueSender
 * @param queueName java.lang.String
 */
protected final Destination getDestination(String destName) throws JMSException {
	Destination dest = null;

	if (destList == null) {
		destList = Collections.synchronizedList(new ArrayList());
	}

	synchronized(destList) {
		Iterator iter = destList.iterator();
		while (iter.hasNext()) {
			dest = (Destination)iter.next();
			if (destName.equals(getDestinationName(dest))) {
				return dest;
			}
		}

		dest = createDestination(destName);	
		destList.add(dest);	
		return dest;		
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:55:24 AM)
 * @return java.lang.String
 * @param dest javax.jms.Destination
 */
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
		producerList = Collections.synchronizedList(new ArrayList());
	}
		
	MessageProducer mp = null;
	synchronized(producerList) {	
		Iterator iter = producerList.iterator();
		while (iter.hasNext()) {
			mp = (MessageProducer)iter.next();
			if (mp == null) {
				continue;
			}				
			if (getDestinationName(dest).equals(getDestinationName(mp))) {
				return mp;
			}
		}

		mp =  createProducer(dest);	
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
protected final Destination getTempDestination(Object source) throws JMSException {
	if (tempDestMap == null) {
		tempDestMap = Collections.synchronizedMap(new HashMap());
	}

	Destination dest = null;
	synchronized(tempDestMap) {
		dest = (Destination)tempDestMap.get(source);
		if (dest == null) {
			dest = createTempDestination();	
			tempDestMap.put(source, dest);	
		}
	}

	return dest;
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 1:54:28 PM)
 */
protected synchronized void selfSetupOnException() {	
	try {
		clearLists();
		setupSession();
			
		if (listenerList != null) {
			List tempList = new ArrayList();
			tempList.addAll(listenerList);
			listenerList.clear();

			Iterator iter = tempList.iterator();
			MessageConsumer mc = null;
			while (iter.hasNext()) {
				mc = (MessageConsumer)iter.next();
				setupListener(getDestinationName(mc), mc.getMessageSelector(), mc.getMessageListener());
				mc = null;
			}
			
			tempList.clear();
			tempList = null;
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
public final void setupListener(String destName, String msgSelector, MessageListener listener) throws JMSException {
	MessageConsumer mc = createConsumer(getDestination(destName), msgSelector);
	mc.setMessageListener(listener);	

	if (listenerList == null) {
		listenerList = Collections.synchronizedList(new ArrayList());
	}

	synchronized (listenerList) {
		listenerList.add(mc);
	}		
	System.out.println("----Setting up Listener:[" + destName + ", " + msgSelector + "]");
}


/**
 * Insert the method's description here.
 * Creation date: (10/13/2003 9:25:41 AM)
 * @return javax.jms.Destination
 */
protected abstract void setupSession() throws JMSException;
}
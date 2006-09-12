package cbit.vcell.messaging;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Insert the type's description here.
 * Creation date: (9/26/2003 10:48:42 AM)
 * @author: Fei Gao
 */
public abstract class QueueListenerImpl implements javax.jms.MessageListener {
/**
 * QueueListenerImpl constructor comment.
 */
public QueueListenerImpl() {
	super();
}
/**
 * onMessage method comment.
 */
public final void onMessage(Message message) {
	try {
		onQueueMessage(message);
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}	
}
	public abstract void onQueueMessage(Message message) throws JMSException ;
}

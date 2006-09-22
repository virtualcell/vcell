package cbit.vcell.messaging;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * Insert the type's description here.
 * Creation date: (5/9/2003 12:05:19 PM)
 * @author: Fei Gao
 */
public abstract class TopicListenerImpl implements MessageListener {
	
	

/**
 * Insert the method's description here.
 * Creation date: (7/17/2003 10:09:06 AM)
 */
public TopicListenerImpl() {
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 3:58:52 PM)
 * @param message javax.jms.Message
 */
public final void onMessage(Message message) {
	try {
		onTopicMessage(message);
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 3:58:52 PM)
 * @param message javax.jms.Message
 */
public abstract void onTopicMessage(Message message) throws JMSException ; 
}
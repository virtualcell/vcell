package cbit.vcell.messaging;

import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (5/22/2003 2:26:44 PM)
 * @author: Fei Gao
 */
public interface QueueListener {
/**
 * Insert the method's description here.
 * Creation date: (5/22/2003 2:27:23 PM)
 */
void onQueueMessage(javax.jms.Message message) throws JMSException;
}

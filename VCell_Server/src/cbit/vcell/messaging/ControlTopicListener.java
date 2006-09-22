package cbit.vcell.messaging;

import javax.jms.JMSException;

/**
 * Insert the type's description here.
 * Creation date: (5/22/2003 2:43:23 PM)
 * @author: Fei Gao
 */
public interface ControlTopicListener {
void onControlTopicMessage(javax.jms.Message message) throws JMSException;
}

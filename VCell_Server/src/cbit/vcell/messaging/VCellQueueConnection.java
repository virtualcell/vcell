package cbit.vcell.messaging;

import javax.jms.JMSException;
import javax.jms.QueueConnection;

/**
 * Insert the type's description here.
 * Creation date: (7/31/2003 8:40:09 AM)
 * @author: Fei Gao
 */
public interface VCellQueueConnection extends VCellJMSConnection {
/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 3:26:42 PM)
 * @return cbit.vcell.messaging.JmsQueueSession
 * @param transactional boolean
 */
public VCellQueueSession getAutoSession() throws JMSException;
/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 3:26:42 PM)
 * @return cbit.vcell.messaging.JmsQueueSession
 * @param transactional boolean
 */
public VCellQueueSession getClientAckSession() throws JMSException;
/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 3:26:42 PM)
 * @return cbit.vcell.messaging.JmsQueueSession
 * @param transactional boolean
 */
public QueueConnection getConnection();
/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 3:26:42 PM)
 * @return cbit.vcell.messaging.JmsQueueSession
 * @param transactional boolean
 */
public VCellQueueSession getTransactedSession() throws JMSException;
}

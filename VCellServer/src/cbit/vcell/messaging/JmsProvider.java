package cbit.vcell.messaging;

import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (2/24/2004 10:31:52 AM)
 * @author: Fei Gao
 */
public interface JmsProvider {
	public final static String SONICMQ = "SonicMQ";
	//public final static String JBOSSMQ = "JBossMQ";
/**
 * Insert the method's description here.
 * Creation date: (12/16/2001 9:51:02 AM)
 * @return javax.jms.QueueConnectionFactory
 */
public abstract QueueConnection createQueueConnection() throws JMSException;
/**
 * Insert the method's description here.
 * Creation date: (12/16/2001 9:51:02 AM)
 * @return javax.jms.QueueConnectionFactory
 */
public abstract TopicConnection createTopicConnection() throws JMSException;
/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 2:51:57 PM)
 * @return javax.transaction.xa.XAResource
 */
public abstract XAQueueConnection createXAQueueConnection() throws JMSException;
/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 2:51:57 PM)
 * @return javax.transaction.xa.XAResource
 */
public abstract XATopicConnection createXATopicConnection() throws JMSException;
/**
 * Insert the method's description here.
 * Creation date: (6/6/2003 11:51:19 AM)
 * @return int
 */
public abstract int getErrorCode(JMSException ex);
/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 2:51:57 PM)
 * @return javax.transaction.xa.XAResource
 */
public abstract QueueConnection getQueueConnection(XAQueueConnection xaQueueConnection) throws JMSException;
/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 2:51:57 PM)
 * @return javax.transaction.xa.XAResource
 */
public abstract TopicConnection getTopicConnection(XATopicConnection xaTopicConnection) throws JMSException;
	public boolean isBadConnection(JMSException ex);
/**
 * Insert the method's description here.
 * Creation date: (6/12/2003 10:26:07 AM)
 * @param pingInterval int
 */
public abstract void setPingInterval(int pingInterval, Connection connection);
/**
 * Insert the method's description here.
 * Creation date: (6/12/2003 10:26:07 AM)
 * @param pingInterval int
 */
public abstract void setPrefetchCount(QueueReceiver qr, int pc) throws JMSException;
/**
 * Insert the method's description here.
 * Creation date: (6/12/2003 10:26:07 AM)
 * @param pingInterval int
 */
public abstract void setPrefetchThreshold(QueueReceiver qr, int pt) throws JMSException;
}

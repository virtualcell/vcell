package cbit.vcell.messaging;

import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (10/10/2003 10:21:50 AM)
 * @author: Fei Gao
 */
public interface JmsConnection extends ExceptionListener {
	public void close() throws JMSException;
	public void closeSession(JmsSession session) throws JMSException;
	JmsProvider getJmsProvider();
	public boolean isBadConnection(JMSException ex);
	public boolean isConnectionDropped();
	public void startConnection() throws JMSException;
	public void stopConnection() throws JMSException;
	
	public JmsSession getAutoSession() throws JMSException;
	public JmsSession getClientAckSession() throws JMSException;
	Connection getConnection();
	public JmsSession getTransactedSession() throws JMSException;

}

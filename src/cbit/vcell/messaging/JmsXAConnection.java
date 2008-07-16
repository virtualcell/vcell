package cbit.vcell.messaging;

import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (9/2/2003 8:32:58 AM)
 * @author: Fei Gao
 */
public interface JmsXAConnection extends JmsConnection {
	XAConnection getXAConnection();
	public JmsXASession getXASession() throws JMSException;
}

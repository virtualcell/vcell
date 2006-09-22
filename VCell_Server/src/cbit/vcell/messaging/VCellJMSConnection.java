package cbit.vcell.messaging;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

/**
 * Insert the type's description here.
 * Creation date: (10/10/2003 10:21:50 AM)
 * @author: Fei Gao
 */
public interface VCellJMSConnection extends ExceptionListener {
		public void close() throws JMSException;
public void closeSession(VCellJmsSession session) throws JMSException;
/**
 * Insert the method's description here.
 * Creation date: (3/31/2004 3:08:32 PM)
 * @return cbit.vcell.messaging.JmsProvider
 */
JmsProvider getJmsProvider();
	public boolean isBadConnection(JMSException ex);
	public boolean isConnectionDropped();
	public void startConnection() throws JMSException;
	public void stopConnection() throws JMSException;
}

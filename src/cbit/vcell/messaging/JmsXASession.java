package cbit.vcell.messaging;

import javax.jms.*;
import javax.transaction.TransactionManager;

/**
 * Insert the type's description here.
 * Creation date: (10/8/2003 1:13:29 PM)
 * @author: Fei Gao
 */
public interface JmsXASession extends JmsSession {
	void setupXASession() throws JMSException;
	boolean joinTransaction(TransactionManager tm) throws JMSException;
}

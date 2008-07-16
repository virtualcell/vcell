package cbit.vcell.messaging;

import javax.jms.*;

import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;

/**
 * Insert the type's description here.
 * Creation date: (10/8/2003 11:36:23 AM)
 * @author: Fei Gao
 */
public class JmsXASessionImpl extends JmsSessionImp implements JmsXASession {
	private XASession xaSession = null;
	private JmsXAConnection vcXAConn = null;
/**
 * JmsXAQueueSession constructor comment.
 * @param queueConn javax.jms.QueueConnection
 * @exception javax.jms.JMSException The exception description.
 */
public JmsXASessionImpl(JmsXAConnection xaconn) throws JMSException {
	super();
	vcXAConn = xaconn;
}

@Override
public void close() throws JMSException {
	synchronized (this) {
		session.close();
		xaSession.close();		
	}	
}

/**
 * Insert the method's description here.
 * Creation date: (7/28/2003 3:48:42 PM)
 */
public boolean joinTransaction(TransactionManager ttmm) throws JMSException {
	if (vcXAConn.isConnectionDropped()) {
		return false;
	}

	try {
		XAResource xaRes = xaSession.getXAResource();
		if (xaRes == null || !ttmm.getTransaction().enlistResource(xaRes)) {
			return false;
		}
		return true;		
	} catch (Exception ex) {
		throw new JMSException(ex + ":" + ex.getMessage());
	}
}

@Override
public synchronized void setupOnException() {
	try {
		clearLists();
		setupXASession();		
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
}

/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 12:02:14 PM)
 * @param queueConnl javax.jms.QueueConnection
 */
public synchronized void setupXASession() throws JMSException {	
	xaSession = vcXAConn.getXAConnection().createXASession();
	session = xaSession.getSession();
}
}

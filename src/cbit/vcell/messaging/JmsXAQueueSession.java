package cbit.vcell.messaging;

import javax.jms.*;
import java.util.*;
import javax.transaction.TransactionManager;
import cbit.vcell.server.SessionLog;
import javax.transaction.xa.XAResource;

/**
 * Insert the type's description here.
 * Creation date: (10/8/2003 11:36:23 AM)
 * @author: Fei Gao
 */
public class JmsXAQueueSession extends JmsQueueSession implements VCellXAQueueSession {
	private XAQueueSession xaQueueSession = null;
	private VCellXAQueueConnection vcXAQueueConn = null;
/**
 * JmsXAQueueSession constructor comment.
 * @param queueConn javax.jms.QueueConnection
 * @exception javax.jms.JMSException The exception description.
 */
public JmsXAQueueSession(VCellXAQueueConnection xaconn) throws JMSException {
	super();
	vcXAQueueConn = xaconn;
}
/**
 * close method comment.
 */
public void close() throws JMSException {
	synchronized (this) {
		queueSession.close();
		xaQueueSession.close();		
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (7/28/2003 3:48:42 PM)
 */
public boolean joinTransaction(TransactionManager ttmm) throws JMSException {
	if (vcXAQueueConn.isConnectionDropped()) {
		return false;
	}

	try {
		XAResource xaRes = xaQueueSession.getXAResource();
		if (xaRes == null || !ttmm.getTransaction().enlistResource(xaRes)) {
			return false;
		}
		return true;		
	} catch (Exception ex) {
		throw new JMSException(ex + ":" + ex.getMessage());
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 1:54:28 PM)
 */
protected synchronized void selfSetupOnException() {	
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
protected synchronized void setupXASession() throws JMSException {	
	xaQueueSession = vcXAQueueConn.getXAConnection().createXAQueueSession();
	queueSession = xaQueueSession.getQueueSession();
}
}

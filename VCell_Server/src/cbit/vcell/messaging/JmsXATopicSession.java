package cbit.vcell.messaging;

import javax.jms.*;
import java.util.*;
import javax.transaction.TransactionManager;

import cbit.util.SessionLog;

import javax.transaction.xa.XAResource;

/**
 * Insert the type's description here.
 * Creation date: (10/8/2003 1:09:45 PM)
 * @author: Fei Gao
 */
public class JmsXATopicSession extends JmsTopicSession implements VCellXATopicSession {
	private XATopicSession xaTopicSession = null;
	private VCellXATopicConnection vcXATopicConn = null;
/**
 * JmsXATopicSession constructor comment.
 */
public JmsXATopicSession(VCellXATopicConnection xaconn) {
	super();
	vcXATopicConn = xaconn;
}
/**
 * close method comment.
 */
public void close() throws JMSException {
	synchronized (this) {
		topicSession.close();
		xaTopicSession.close();
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (7/28/2003 3:48:42 PM)
 */
public boolean joinTransaction(TransactionManager ttmm) throws JMSException {
	if (vcXATopicConn.isConnectionDropped()) {
		return false;
	}

	try {
		XAResource xaRes = xaTopicSession.getXAResource();
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
		xaTopicSession = null;	
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
	xaTopicSession = vcXATopicConn.getXAConnection().createXATopicSession();
	topicSession = xaTopicSession.getTopicSession();	
}
}

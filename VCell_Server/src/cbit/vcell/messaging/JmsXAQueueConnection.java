package cbit.vcell.messaging;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.XAQueueConnection;

/**
 * Insert the type's description here.
 * Creation date: (7/22/2003 11:29:13 AM)
 * @author: Fei Gao
 */
public class JmsXAQueueConnection extends AbstractJmsConnection implements VCellXAQueueConnection {
	private XAQueueConnection xaQueueConnection = null;
	private QueueConnection queueConnection = null;	

/**
 * Insert the method's description here.
 * Creation date: (8/13/2003 2:37:58 PM)
 */
public JmsXAQueueConnection(JmsProvider jmsProvider0) throws JMSException {
	super(jmsProvider0);
	setupConnection();
}


/**
 * Insert the method's description here.
 * Creation date: (7/28/2003 3:48:42 PM)
 */
public void close() throws JMSException {	
	closeAllSessions();
	queueConnection.close();	
}


/**
 * getXAConnection method comment.
 */
public javax.jms.XAQueueConnection getXAConnection() {
	return xaQueueConnection;
}


/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 1:14:14 PM)
 * @return cbit.vcell.messaging.VCellXAQueueSession
 */
public VCellXAQueueSession getXASession() throws JMSException {
	JmsXAQueueSession session = new JmsXAQueueSession(this);
	session.setupXASession();
	synchronized (sessionList) {
		sessionList.add(session);	
	}
	
	return session;	
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 1:54:28 PM)
 */
protected synchronized void selfSetupOnException() {
	try {
		List tempList = new ArrayList();
		tempList.addAll(sessionList);
		sessionList.clear();
		
		if (tempList.size() != 0) {
			Iterator iter = tempList.iterator();
			while (iter.hasNext()) {
				JmsXAQueueSession session = (JmsXAQueueSession)iter.next();
				session.selfSetupOnException();				
				sessionList.add(session);
			}
		}
			
		startConnection();
	} catch (Exception ex) {
		ex.printStackTrace(System.out);
	}
	
}


/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 2:51:57 PM)
 * @return java.sql.Connection
 */
public synchronized void setupConnection() {	
	xaQueueConnection = null;	
	while (xaQueueConnection == null) {
		try {		
			xaQueueConnection = jmsProvider.createXAQueueConnection();
			queueConnection = jmsProvider.getQueueConnection(xaQueueConnection);
			queueConnection.setExceptionListener(this);
		} catch (Exception jmse) {
			//log.exception(jmse);
			System.out.println(this + ": Cannot connect to Message Server...Pausing " + JMSCONNECTION_RETRY_INTERVAL/1000 + " seconds before retry.");	
			try {
				Thread.sleep(JMSCONNECTION_RETRY_INTERVAL);
			} catch (InterruptedException ie) {
				ie.printStackTrace(System.out);
			}
		}
	}
	System.out.println(this + ": Connection re-established.");
}


/**
 * startConnection method comment.
 */
public void startConnection() throws javax.jms.JMSException {
	queueConnection.start();
}


/**
 * stopConnection method comment.
 */
public void stopConnection() throws javax.jms.JMSException {
	queueConnection.stop();
}
}
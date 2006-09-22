package cbit.vcell.messaging;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.Session;

/**
 * Insert the type's description here.
 * Creation date: (5/21/2003 1:20:41 PM)
 * @author: Fei Gao
 */
public class JmsQueueConnection extends AbstractJmsConnection implements VCellQueueConnection {
	private QueueConnection queueConnection = null;	

/**
 * Insert the method's description here.
 * Creation date: (8/13/2003 2:36:58 PM)
 */
public JmsQueueConnection(JmsProvider jmsProvider0) throws JMSException {
	super(jmsProvider0);
	setupConnection();
}


/**
 * Insert the method's description here.
 * Creation date: (7/30/2003 1:22:40 PM)
 */
public void close() throws javax.jms.JMSException {
	closeAllSessions();	
	queueConnection.close();
}


/**
 * Insert the method's description here.
 * Creation date: (12/17/2003 8:32:36 AM)
 * @return cbit.vcell.messaging.JmsQueueSession
 * @param transactional boolean
 */
public VCellQueueSession getAutoSession() throws JMSException {
	return getSession(false, Session.AUTO_ACKNOWLEDGE);
}


/**
 * Insert the method's description here.
 * Creation date: (12/17/2003 8:32:36 AM)
 * @return cbit.vcell.messaging.JmsQueueSession
 * @param transactional boolean
 */
public VCellQueueSession getClientAckSession() throws JMSException {
	return getSession(false, Session.CLIENT_ACKNOWLEDGE);
}


/**
 * Insert the method's description here.
 * Creation date: (10/9/2003 9:46:41 AM)
 * @return javax.jms.QueueConnection
 */
public javax.jms.QueueConnection getConnection() {
	return queueConnection;
}


/**
 * Insert the method's description here.
 * Creation date: (10/8/2003 11:51:41 AM)
 * @return cbit.vcell.messaging.VCellQueueSession
 * @param transactional boolean
 */
private VCellQueueSession getSession(boolean transactional, int ackMode) throws JMSException {
	checkConnection();
	
	JmsQueueSession session = new JmsQueueSession(this, transactional, ackMode);
	session.setupSession();
	synchronized (sessionList) {
		sessionList.add(session);	
	}
	
	return session;	
}


/**
 * Insert the method's description here.
 * Creation date: (12/17/2003 8:32:36 AM)
 * @return cbit.vcell.messaging.JmsQueueSession
 * @param transactional boolean
 */
public VCellQueueSession getTransactedSession() throws JMSException {
	return getSession(true, Session.AUTO_ACKNOWLEDGE);
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
				JmsQueueSession session = (JmsQueueSession)iter.next();
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
 * Creation date: (5/21/2003 1:54:28 PM)
 */
protected synchronized void setupConnection() {
	queueConnection = null;	
	// Wait for a connection.
	while (queueConnection == null) {
		//queueSession = null;		
		try {			
			//System.out.println("JmsQueueConnection(): Attempting to create connection...");			
			queueConnection = jmsProvider.createQueueConnection();
			queueConnection.setExceptionListener(this);
			jmsProvider.setPingInterval(JMSCONNECTION_PING_INTERVAL, queueConnection);
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
	System.out.println(this + ": Connection established.");
}


/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 1:34:12 PM)
 */
public void startConnection() throws JMSException {
	queueConnection.start();
}


/**
 * stopConnection method comment.
 */
public void stopConnection() throws javax.jms.JMSException {
	queueConnection.stop();
}
}
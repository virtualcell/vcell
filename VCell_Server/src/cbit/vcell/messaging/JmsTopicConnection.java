package cbit.vcell.messaging;
import javax.jms.*;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (5/21/2003 1:20:41 PM)
 * @author: Fei Gao
 */
public class JmsTopicConnection extends AbstractJmsConnection implements VCellTopicConnection {
	private TopicConnection topicConnection = null; // one connection per user	

/**
 * Client constructor comment.
 */
public JmsTopicConnection(JmsProvider jmsProvider0) throws JMSException {
	super(jmsProvider0);
	setupConnection();
}


/**
 * Insert the method's description here.
 * Creation date: (7/30/2003 1:22:46 PM)
 */
public void close() throws javax.jms.JMSException {
	closeAllSessions();
	topicConnection.close();
}


/**
 * getAutoSession method comment.
 */
public VCellTopicSession getAutoSession() throws JMSException {
	return getSession(false, Session.AUTO_ACKNOWLEDGE);
}


/**
 * getNonAutoSession method comment.
 */
public VCellTopicSession getClientAckSession() throws JMSException {
	return getSession(false, Session.CLIENT_ACKNOWLEDGE);
}


/**
 * getConnection method comment.
 */
public javax.jms.TopicConnection getConnection() {
	return topicConnection;
}


/**
 * getSession method comment.
 */
private VCellTopicSession getSession(boolean transactional, int ackMode) throws JMSException {
	checkConnection();
	
	JmsTopicSession session = new JmsTopicSession(this, transactional, ackMode);
	session.setupSession();
	synchronized (sessionList) {
		sessionList.add(session);	
	}
	
	return session;	
}


/**
 * getTransactedSession method comment.
 */
public VCellTopicSession getTransactedSession() throws JMSException {
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
				JmsTopicSession session = (JmsTopicSession)iter.next();
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
	topicConnection = null;	
	// Wait for a connection.
	while (topicConnection == null) {
		try {
			//System.out.println("JmsTopicConnection(): Attempting to create connection...");
			topicConnection = jmsProvider.createTopicConnection();
			topicConnection.setExceptionListener(this);
			jmsProvider.setPingInterval(JMSCONNECTION_PING_INTERVAL, topicConnection);
		} catch (JMSException e) {
			//log.exception(e);
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
 * Insert the method's description here.
 * Creation date: (5/21/2003 1:34:12 PM)
 */
public void startConnection() throws JMSException {
	topicConnection.start();
}


/**
 * stopConnection method comment.
 */
public void stopConnection() throws javax.jms.JMSException {
	topicConnection.stop();
}
}
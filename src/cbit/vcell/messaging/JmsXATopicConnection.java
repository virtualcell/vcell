package cbit.vcell.messaging;
import javax.jms.*;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (7/22/2003 11:32:20 AM)
 * @author: Fei Gao
 */
public class JmsXATopicConnection extends AbstractJmsConnection implements VCellXATopicConnection {
	private XATopicConnection xaTopicConnection = null;
	private TopicConnection topicConnection = null; // one connection per user	

/**
 * JtaJmsTopicConnection constructor comment.
 * @param jjf cbit.vcell.transaction.JtaJmsFactory
 * @param jf cbit.vcell.messaging.JmsFactory
 * @exception javax.naming.NamingException The exception description.
 * @exception javax.jms.JMSException The exception description.
 */
public JmsXATopicConnection(JmsProvider jmsProvider0) throws JMSException {
	super(jmsProvider0);
	setupConnection();
}


/**
 * Insert the method's description here.
 * Creation date: (7/28/2003 3:49:27 PM)
 */
public void close() throws JMSException {
	if (topicConnection != null) {
		closeAllSessions();
		topicConnection.close();
	}
}


/**
 * getXAConnection method comment.
 */
public javax.jms.XATopicConnection getXAConnection() {
	return xaTopicConnection;
}


/**
 * getXASession method comment.
 */
public VCellXATopicSession getXASession() throws JMSException {
	JmsXATopicSession session = new JmsXATopicSession(this);
	session.setupXASession();

	synchronized (sessionList) {
		sessionList.add(session);		
	}	
	return session;	
}


/**
 * Insert the method's description here.
 * Creation date: (10/9/2003 12:08:31 PM)
 */
protected synchronized void selfSetupOnException() {
	try {
		List tempList = new ArrayList();
		tempList.addAll(sessionList);
		sessionList.clear();
		
		if (tempList.size() != 0) {
			Iterator iter = tempList.iterator();
			while (iter.hasNext()) {
				JmsXATopicSession session = (JmsXATopicSession)iter.next();
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
	xaTopicConnection = null;	
	while (xaTopicConnection == null) {
		xaTopicConnection = null;
		
		try {
			xaTopicConnection = jmsProvider.createXATopicConnection();	
			topicConnection = jmsProvider.getTopicConnection(xaTopicConnection);
			topicConnection.setExceptionListener(this);
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
	topicConnection.start();
}


/**
 * stopConnection method comment.
 */
public void stopConnection() throws javax.jms.JMSException {
	topicConnection.stop();
}
}
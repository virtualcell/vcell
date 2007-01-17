package cbit.vcell.messaging;

import javax.jms.*;
import java.util.*;
import cbit.vcell.server.SessionLog;

/**
 * Insert the type's description here.
 * Creation date: (6/6/2003 11:16:18 AM)
 * @author: Fei Gao
 */
public abstract class AbstractJmsConnection implements ExceptionListener, VCellJMSConnection {
	protected JmsProvider jmsProvider = null;
	protected boolean connectionDropped = false;
	protected boolean inSetup = false;

	protected static final int JMSCONNECTION_PING_INTERVAL = 30; // second
	protected static final long JMSCONNECTION_RETRY_INTERVAL = 10 * MessageConstants.SECOND; //second
		
	protected List sessionList = Collections.synchronizedList(new ArrayList());
/**
 * JmsConnection constructor comment.
 */
protected AbstractJmsConnection(JmsProvider argJmsProvider) throws JMSException {
	jmsProvider = argJmsProvider;
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 1:54:28 PM)
 */
protected final synchronized void checkConnection() {
	while (connectionDropped) {
		try {
			System.out.println("I am waiting for Queue Connection");
			wait();
		} catch (InterruptedException ex) {
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/30/2003 1:22:40 PM)
 */
protected final void closeAllSessions() {
	synchronized (sessionList) {
		Iterator iter = sessionList.iterator();
		while (iter.hasNext()) {
			VCellJmsSession session = (VCellJmsSession)iter.next();
			try {
				session.close();
			} catch (JMSException ex) {
				// try to close it, if can't, it's ok
			}
		}		
	}
	sessionList.clear();
}
/**
 * Insert the method's description here.
 * Creation date: (7/30/2003 1:22:40 PM)
 */
public final void closeSession(VCellJmsSession session) throws JMSException {
	synchronized (sessionList) {
		session.close();
		sessionList.remove(session);
		session = null;	
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/2004 3:08:49 PM)
 * @return cbit.vcell.messaging.JmsProvider
 */
public JmsProvider getJmsProvider() {
	return jmsProvider;
}
/**
 * Insert the method's description here.
 * Creation date: (6/6/2003 11:51:19 AM)
 * @return int
 */
public final boolean isBadConnection(JMSException ex){
	return jmsProvider.isBadConnection(ex);
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 1:54:28 PM)
 */
public final boolean isConnectionDropped() {
	return connectionDropped;
}
/**
 * onException method comment.
 */
public final void onException(JMSException jmse) { 
	// See if connection was dropped.

	// Tell the user that there is a problem.

	// See if the error is a dropped connection. If so, try to reconnect.
	// NOTE: the test is against SonicMQ error codes.
	if (isBadConnection(jmse)) {	
		// Reestablish the connection
		// If we are in connection setup, the setupConnection method itself will retry.
		if (!inSetup) {
			System.out.println(this + ":Please wait while the application tries to re-establish the connection...");
			synchronized (this) {
				connectionDropped = true;
				inSetup = true;
				setupConnection();			
				selfSetupOnException();
				inSetup = false;
				connectionDropped = false;					
				notifyAll();
			}
		}	
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 1:54:28 PM)
 */
protected abstract void selfSetupOnException(); 
/**
 * Insert the method's description here.
 * Creation date: (5/21/2003 1:54:28 PM)
 */
protected abstract void setupConnection(); 
}

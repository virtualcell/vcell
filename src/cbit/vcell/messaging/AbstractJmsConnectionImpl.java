package cbit.vcell.messaging;

import javax.jms.*;
import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (6/6/2003 11:16:18 AM)
 * @author: Fei Gao
 */
public abstract class AbstractJmsConnectionImpl implements ExceptionListener, JmsConnection {
	protected JmsProvider jmsProvider = null;
	protected boolean connectionDropped = false;
	protected boolean inSetup = false;
	protected Connection connection = null;	

	protected static final int JMSCONNECTION_PING_INTERVAL = 30; // second
	protected static final long JMSCONNECTION_RETRY_INTERVAL = 10 * MessageConstants.SECOND; //second
		
	protected List<JmsSession> sessionList = Collections.synchronizedList(new ArrayList<JmsSession>());

	protected AbstractJmsConnectionImpl(JmsProvider argJmsProvider) throws JMSException {
		jmsProvider = argJmsProvider;
	}

	protected final synchronized void checkConnection() {
		while (connectionDropped) {
			try {
				System.out.println("I am waiting for Queue Connection");
				wait();
			} catch (InterruptedException ex) {
			}
		}
	}

	protected final void closeAllSessions() {
		synchronized (sessionList) {
			for (JmsSession session : sessionList) {
				try {
					session.close();
				} catch (JMSException ex) {
					// try to close it, if can't, it's ok
				}
			}		
		}
		sessionList.clear();
	}

	public final void closeSession(JmsSession session) throws JMSException {
		synchronized (sessionList) {
			session.close();
			sessionList.remove(session);	
		}
	}

	public final JmsProvider getJmsProvider() {
		return jmsProvider;
	}
	
	public final boolean isBadConnection(JMSException ex){
		return jmsProvider.isBadConnection(ex);
	}

	public final boolean isConnectionDropped() {
		return connectionDropped;
	}
	
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
					setupOnException();
					inSetup = false;
					connectionDropped = false;					
					notifyAll();
				}
			}	
		}
	}

	protected final synchronized void setupOnException() {
		try {
			List<JmsSession> tempList = new ArrayList<JmsSession>();
			tempList.addAll(sessionList);
			sessionList.clear();
			
			for (JmsSession session : tempList) {
				session.setupOnException();				
				sessionList.add(session);
			}
				
			startConnection();
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
		
	}
 
	protected abstract void setupConnection();
 
	public final void close() throws javax.jms.JMSException {
		closeAllSessions();	
		connection.close();
	}
	
	public final JmsSession getAutoSession() throws JMSException {
		return getSession(false, Session.AUTO_ACKNOWLEDGE);
	}
	
	
	public final JmsSession getClientAckSession() throws JMSException {
		return getSession(false, Session.CLIENT_ACKNOWLEDGE);
	}
	
	public final JmsSession getTransactedSession() throws JMSException {
		return getSession(true, Session.AUTO_ACKNOWLEDGE);
	}
	
	public final javax.jms.Connection getConnection() {
		return connection;
	}

	private final JmsSession getSession(boolean transactional, int ackMode) throws JMSException {
		checkConnection();
		
		JmsSession session = new JmsSessionImp(this, transactional, ackMode);
		session.setupSession();
		synchronized (sessionList) {
			sessionList.add(session);	
		}
		
		return session;	
	}
	
	public final void startConnection() throws JMSException {
		connection.start();
	}
	
	
	public final void stopConnection() throws javax.jms.JMSException {
		connection.stop();
	}
}

package cbit.vcell.messaging;
import javax.jms.*;

public class JmsXAConnectionImp extends AbstractJmsConnectionImpl implements JmsXAConnection {
	private XAConnection xaConnection = null;	

	public JmsXAConnectionImp(JmsProvider jmsProvider0) throws JMSException {
		super(jmsProvider0);
		setupConnection();
	}
	
	public XAConnection getXAConnection() {
		return xaConnection;
	}
	
	public JmsXASession getXASession() throws JMSException {
		JmsXASession session = new JmsXASessionImpl(this);
		session.setupXASession();
		synchronized (sessionList) {
			sessionList.add(session);	
		}
		
		return session;	
	}

	@Override
	protected void setupConnection() {
		xaConnection = null;	
		while (xaConnection == null) {
			try {		
				xaConnection = jmsProvider.createXAConnection();
				connection = jmsProvider.getConnection(xaConnection);
				jmsProvider.setPingInterval(JMSCONNECTION_PING_INTERVAL, connection);
				connection.setExceptionListener(this);
				System.out.println(this + ": Connection established.");
	 		} catch (Exception jmse) {
				System.out.println(this + ": Cannot connect to Message Server [" + jmse.getMessage() + "], Pausing " + JMSCONNECTION_RETRY_INTERVAL/1000 + " seconds before retry.");
				try {
					Thread.sleep(JMSCONNECTION_RETRY_INTERVAL);
				} catch (InterruptedException ie) {
					ie.printStackTrace(System.out);
				}
			}
		}		
	}
}
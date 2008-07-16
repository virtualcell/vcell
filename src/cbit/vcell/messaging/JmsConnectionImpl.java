package cbit.vcell.messaging;

import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (6/6/2003 11:16:18 AM)
 * @author: Fei Gao
 */
public class JmsConnectionImpl extends AbstractJmsConnectionImpl {

	protected JmsConnectionImpl(JmsProvider argJmsProvider) throws JMSException {
		super(argJmsProvider);
		setupConnection();
	}

	protected void setupConnection() {
		connection = null;	
		// Wait for a connection.
		while (connection == null) {		
			try {						
				connection = jmsProvider.createConnection();
				connection.setExceptionListener(this);
				jmsProvider.setPingInterval(JMSCONNECTION_PING_INTERVAL, connection);
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

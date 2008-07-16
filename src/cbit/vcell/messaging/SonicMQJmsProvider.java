package cbit.vcell.messaging;
import javax.jms.*;

/**
 * Insert the type's description here.
 * Creation date: (12/16/2001 9:54:16 AM)
 * @author: Jim Schaff
 */
public class SonicMQJmsProvider extends AbstractJmsProvider {
	private String BROKER = "localhost:2506";
	//private final static int DEFAULT_PREFETCH_COUNT = 3;
	//private final static int DEFAULT_PREFETCH_THRESHOLD = 1;

/**
 * Insert the method's description here.
 * Creation date: (12/16/2001 5:04:13 PM)
 * @param host java.lang.String
 * @param userid java.lang.String
 * @param password java.lang.String
 */
public SonicMQJmsProvider(String url, String userid, String password) {
	super(userid,password);
	BROKER = (url.indexOf(":") >= 0) ? url : url + ":2506";
}


public Connection createConnection() throws JMSException {
	if (connectionFactory == null) {
		connectionFactory = new progress.message.jclient.QueueConnectionFactory(BROKER,null,fieldUserid,fieldPassword);
	}

	return connectionFactory.createConnection();
}

public XAConnection createXAConnection() throws JMSException {
	if (xaConnectionFactory == null) {
		xaConnectionFactory = new progress.message.jclient.xa.XAQueueConnectionFactory(BROKER, null,fieldUserid, fieldPassword);		
	}
	
	return xaConnectionFactory.createXAConnection();
}

/**
 * Insert the method's description here.
 * Creation date: (6/6/2003 11:52:07 AM)
 * @return int
 */
public int getErrorCode(JMSException ex) {
	if (progress.message.jclient.ErrorCodes.testException(ex, progress.message.jclient.ErrorCodes.ERR_CONNECTION_DROPPED)) {
		return JmsErrorCode.ERR_CONNECTION_DROPPED;
	}

	return JmsErrorCode.ERR_UNKNOWN;
}

public Connection getConnection(XAConnection xaConnection) throws JMSException {
	return ((progress.message.jclient.xa.XAConnection)xaConnection).getConnection();
}


/**
 * Insert the method's description here.
 * Creation date: (6/12/2003 10:26:16 AM)
 * @param pingInterval int
 */
public void setPingInterval(int pingInterval, Connection connection) {
	((progress.message.jclient.Connection)connection).setPingInterval(pingInterval);
}


public void setPrefetchCount(MessageConsumer qr, int pc) throws JMSException {
	if (qr != null) {
		((progress.message.jclient.MessageConsumer)qr).setPrefetchCount(pc);
	}
}


public void setPrefetchThreshold(MessageConsumer qr, int pt) throws javax.jms.JMSException {
	if (qr != null) {
		((progress.message.jclient.MessageConsumer)qr).setPrefetchThreshold(pt);
	}	
}
}
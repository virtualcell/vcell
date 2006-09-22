package cbit.vcell.messaging;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.TopicConnection;
import javax.jms.XAQueueConnection;
import javax.jms.XATopicConnection;

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


/**
 * Insert the method's description here.
 * Creation date: (1/4/2002 2:19:03 AM)
 * @return javax.jms.QueueConnection
 */
public QueueConnection createQueueConnection() throws JMSException {
	if (queueConnectionFactory == null) {
		queueConnectionFactory = new progress.message.jclient.QueueConnectionFactory(BROKER,null,fieldUserid,fieldPassword);
	}

	return queueConnectionFactory.createQueueConnection();
}


/**
 * Insert the method's description here.
 * Creation date: (1/4/2002 2:19:03 AM)
 * @return javax.jms.QueueConnection
 */
public TopicConnection createTopicConnection() throws JMSException {
	if (topicConnectionFactory == null){
		topicConnectionFactory = new progress.message.jclient.TopicConnectionFactory(BROKER,null,fieldUserid,fieldPassword);
	}
	return topicConnectionFactory.createTopicConnection();
}


/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 2:51:57 PM)
 * @return javax.transaction.xa.XAResource
 */
public XAQueueConnection createXAQueueConnection() throws JMSException {
	if (xaQueueConnectionFactory == null) {
		xaQueueConnectionFactory = new progress.message.jclient.xa.XAQueueConnectionFactory(BROKER, null,fieldUserid, fieldPassword);		
	}
	
	return xaQueueConnectionFactory.createXAQueueConnection();
}


/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 2:51:57 PM)
 * @return javax.transaction.xa.XAResource
 */
public XATopicConnection createXATopicConnection() throws JMSException {
	if (xaTopicConnectionFactory == null) {
		xaTopicConnectionFactory = new progress.message.jclient.xa.XATopicConnectionFactory(BROKER, null,fieldUserid, fieldPassword);
	}
		
	return xaTopicConnectionFactory.createXATopicConnection();
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


/**
 * Insert the method's description here.
 * Creation date: (7/21/2003 2:51:57 PM)
 * @return javax.transaction.xa.XAResource
 */
public QueueConnection getQueueConnection(XAQueueConnection xaQueueConnection) throws JMSException {
	return ((progress.message.jclient.xa.XAQueueConnection)xaQueueConnection).getQueueConnection();
}


/**
 * Insert the method's description here.
 * Creation date: (7/28/2003 9:46:43 AM)
 * @return javax.transaction.xa.XAResource
 */
public TopicConnection getTopicConnection(XATopicConnection xaTopicConnection) throws JMSException {
	return ((progress.message.jclient.xa.XATopicConnection)xaTopicConnection).getTopicConnection();
}


/**
 * Insert the method's description here.
 * Creation date: (6/12/2003 10:26:16 AM)
 * @param pingInterval int
 */
public void setPingInterval(int pingInterval, Connection connection) {
	((progress.message.jclient.Connection)connection).setPingInterval(pingInterval);
}


/**
 * Insert the method's description here.
 * Creation date: (3/31/2004 3:01:43 PM)
 * @param pingInterval int
 */
public void setPrefetchCount(QueueReceiver qr, int pc) throws JMSException {
	if (qr != null) {
		((progress.message.jclient.QueueReceiver)qr).setPrefetchCount(pc);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (3/15/2005 9:44:05 AM)
 * @param pingInterval int
 */
public void setPrefetchThreshold(javax.jms.QueueReceiver qr, int pt) throws javax.jms.JMSException {
	if (qr != null) {
		((progress.message.jclient.QueueReceiver)qr).setPrefetchThreshold(pt);
	}	
}
}
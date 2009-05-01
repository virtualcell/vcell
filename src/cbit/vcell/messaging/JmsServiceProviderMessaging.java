package cbit.vcell.messaging;
import javax.jms.*;
import static cbit.vcell.messaging.admin.ManageConstants.*;
import cbit.vcell.messaging.admin.ServiceInstanceStatus;
import cbit.vcell.messaging.server.ServiceProvider;

/**
 * Insert the type's description here.
 * Creation date: (7/2/2003 3:00:11 PM)
 * @author: Fei Gao
 */
public abstract class JmsServiceProviderMessaging implements ControlTopicListener {
	protected JmsConnectionFactory jmsConnFactory = null;
	protected org.vcell.util.SessionLog log = null;
	protected JmsConnection jmsConn = null;
	protected JmsSession listenTopicSession = null;
	protected ServiceProvider jmsServiceProvider = null;

/**
 * JmsMessaging constructor comment.
 */
protected JmsServiceProviderMessaging(ServiceProvider serviceProvider0, org.vcell.util.SessionLog log0) throws JMSException {
	log = log0;
	jmsConnFactory = new JmsConnectionFactoryImpl();
	jmsServiceProvider = serviceProvider0;
}


/**
 * Insert the method's description here.
 * Creation date: (12/17/2003 10:46:29 AM)
 */
private void closeJmsConnection() {
	try {
		if (jmsConn != null) {
			jmsConn.close();
		}
	} catch (JMSException ex) {
		log.exception(ex);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (12/3/2003 10:28:36 AM)
 * @return java.lang.String
 */
private final String getDaemonControlFilter() {
	return MESSAGE_TYPE_PROPERTY + " NOT IN " 
		+ "('" + MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE + "'"
		+ ",'" + MESSAGE_TYPE_REFRESHSERVERMANAGER_VALUE + "'"
		+ ",'" + MESSAGE_TYPE_IAMALIVE_VALUE + "'"
		+ ",'" + MESSAGE_TYPE_STARTSERVICE_VALUE + "'"
		+ ")";		
}


/**
 * Insert the method's description here.
 * Creation date: (10/31/2003 11:49:03 AM)
 * @return cbit.vcell.messaging.VCellQueueConnection
 */
public JmsConnection getJmsConnection() {
	return jmsConn;
}

/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 3:58:52 PM)
 * @param message javax.jms.Message
 * Got message from server_control topic 
 */
public void onControlTopicMessage(Message message) {
	onDaemonMessage(listenTopicSession, message, jmsServiceProvider.getServiceInstanceStatus());
}


/**
 * onMessage method comment.
 */
public final void onDaemonMessage(JmsSession controlSession, javax.jms.Message message, ServiceInstanceStatus serviceInstanceStatus) {
	try {
		String msgType = (String)JmsUtils.parseProperty(message, MESSAGE_TYPE_PROPERTY, String.class);
		String serviceID = null;
		
		if (msgType == null) {
			return;
		}
		
		log.print("JmsMessaging: onDaemonMessage:onMessage [" + JmsUtils.toString(message) + "]");	
		
		if (msgType.equals(MESSAGE_TYPE_ISSERVICEALIVE_VALUE)) {			
			Message reply = controlSession.createObjectMessage(serviceInstanceStatus);
			reply.setStringProperty(MESSAGE_TYPE_PROPERTY, MESSAGE_TYPE_IAMALIVE_VALUE);
			reply.setStringProperty(SERVICE_ID_PROPERTY, serviceInstanceStatus.getID());
			log.print("sending reply [" + JmsUtils.toString(reply) + "]");
			if (message.getJMSReplyTo() != null) {
				reply.setJMSCorrelationID(message.getJMSMessageID());
				controlSession.publishMessage((Topic)message.getJMSReplyTo(), reply);
			} else {
				controlSession.publishMessage(JmsUtils.getTopicDaemonControl(), reply);
			}		
		} else if (msgType.equals(MESSAGE_TYPE_ASKPERFORMANCESTATUS_VALUE)) {				
			Message reply = controlSession.createObjectMessage(serviceInstanceStatus);
			reply.setStringProperty(MESSAGE_TYPE_PROPERTY, MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE);
			reply.setStringProperty(SERVICE_ID_PROPERTY, serviceInstanceStatus.getID());
			controlSession.publishMessage(JmsUtils.getTopicDaemonControl(), reply);			
			log.print("sending reply [" + JmsUtils.toString(reply) + "]");
			
		} else if (msgType.equals(MESSAGE_TYPE_STOPSERVICE_VALUE)) {
			serviceID = (String)JmsUtils.parseProperty(message, SERVICE_ID_PROPERTY, String.class);
			if (serviceID != null && serviceID.equalsIgnoreCase(serviceInstanceStatus.getID()))  {
				stopService();
			}
		}
	} catch (MessagePropertyNotFoundException ex) {
		log.exception(ex);
	} catch (JMSException ex) {
		log.exception(ex);
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 5:29:47 PM)
 */
protected void reconnect() throws JMSException {
	jmsConn = jmsConnFactory.createConnection();
	listenTopicSession = jmsConn.getAutoSession();	
	listenTopicSession.setupTopicListener(JmsUtils.getTopicDaemonControl(), getDaemonControlFilter(), new ControlMessageCollector(this));
}


/**
 * Insert the method's description here.
 * Creation date: (12/10/2003 8:42:49 AM)
 */
protected void stopService() {
	try {
		Thread t = new Thread() {
			public void run() {
				jmsServiceProvider.stop();
				closeJmsConnection();
			}
		};
		t.start();
		t.join(3000);	
	} catch (InterruptedException ex) {
	} finally {
		System.exit(0);
	}
}
}
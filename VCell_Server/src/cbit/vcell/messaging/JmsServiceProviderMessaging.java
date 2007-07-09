package cbit.vcell.messaging;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;

import cbit.vcell.messaging.server.ServiceProvider;

/**
 * Insert the type's description here.
 * Creation date: (7/2/2003 3:00:11 PM)
 * @author: Fei Gao
 */
public abstract class JmsServiceProviderMessaging implements ControlTopicListener {
	protected JmsConnectionFactory jmsConnFactory = null;
	protected org.vcell.util.SessionLog log = null;
	protected VCellQueueConnection queueConn = null;
	protected VCellTopicConnection topicConn = null;
	protected VCellTopicSession listenTopicSession = null;
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
		if (queueConn != null) {
			queueConn.close();
		}
		if (topicConn != null) {
			topicConn.close();
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
private final String getDaemonControlFilter(VCServiceInfo serviceInfo) {
	return ManageConstants.MESSAGE_TYPE_PROPERTY + " NOT IN " 
		+ "('" + ManageConstants.MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE + "'"
		+ ",'" + ManageConstants.MESSAGE_TYPE_CHANGECONFIG_VALUE + "'"
		+ ",'" + ManageConstants.MESSAGE_TYPE_IAMALIVE_VALUE + "'"
		+ ",'" + ManageConstants.MESSAGE_TYPE_OPENSERVICELOG_VALUE + "'"
		+ ",'" + ManageConstants.MESSAGE_TYPE_OPENSERVERLOG_VALUE + "'"
		+ ",'" + ManageConstants.MESSAGE_TYPE_ISSERVERALIVE_VALUE + "'"
		+ ",'" + ManageConstants.MESSAGE_TYPE_ISSERVICEALIVE_VALUE + "'"
		+ ",'" + ManageConstants.MESSAGE_TYPE_STARTBOOTSTRAP_VALUE + "'"
		+ ",'" + ManageConstants.MESSAGE_TYPE_STARTSERVICE_VALUE + "'"
		+ ")" 
		+ " OR ("
		+ ManageConstants.MESSAGE_TYPE_PROPERTY + "='" + ManageConstants.MESSAGE_TYPE_ISSERVICEALIVE_VALUE + "'"
		+ " AND " + ManageConstants.HOSTNAME_PROPERTY + "='" + serviceInfo.getHostName() + "'"  
		+ ")";
		
}


/**
 * Insert the method's description here.
 * Creation date: (10/31/2003 11:49:03 AM)
 * @return cbit.vcell.messaging.VCellQueueConnection
 */
public VCellQueueConnection getQueueConnection() {
	return queueConn;
}


/**
 * Insert the method's description here.
 * Creation date: (10/31/2003 11:49:23 AM)
 * @return cbit.vcell.messaging.VCellTopicConnection
 */
protected VCellTopicConnection getTopicConnection() {
	return topicConn;
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 3:58:52 PM)
 * @param message javax.jms.Message
 * Got message from server_control topic 
 */
public void onControlTopicMessage(Message message) {
	onDaemonMessage(listenTopicSession, message, jmsServiceProvider.getServiceInfo());
}


/**
 * onMessage method comment.
 */
public final void onDaemonMessage(VCellTopicSession controlSession, javax.jms.Message message, VCServiceInfo serviceInfo) {
	try {
		String msgType = (String)JmsUtils.parseProperty(message, ManageConstants.MESSAGE_TYPE_PROPERTY, String.class);
		String hostname = null, serviceType = null, serviceName = null;
		
		if (msgType == null) {
			return;
		}
		
		log.print("JmsMessaging: onDaemonMessage:onMessage [" + JmsUtils.toString(message) + "]");	
		
		if (msgType.equals(ManageConstants.MESSAGE_TYPE_ISSERVICEALIVE_VALUE)) {
			hostname = (String)JmsUtils.parseProperty(message, ManageConstants.HOSTNAME_PROPERTY, String.class);
			if (hostname == null || !hostname.equalsIgnoreCase(serviceInfo.getHostName())) {
				return;
			}

			try {
				serviceType = (String)JmsUtils.parseProperty(message, ManageConstants.SERVICETYPE_PROPERTY, String.class);
				serviceName = (String)JmsUtils.parseProperty(message, ManageConstants.SERVICENAME_PROPERTY, String.class);
			} catch (MessagePropertyNotFoundException ex) {
				// it's ok
			}
			if (serviceType == null && serviceName == null || 
				serviceType != null && serviceName != null && serviceType.equalsIgnoreCase(serviceInfo.getServiceType()) && serviceName.equalsIgnoreCase(serviceInfo.getServiceName()))  {
				Message reply = controlSession.createMessage();
				reply.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_IAMALIVE_VALUE);
				reply.setStringProperty(ManageConstants.HOSTNAME_PROPERTY, serviceInfo.getHostName());
				reply.setStringProperty(ManageConstants.SERVICETYPE_PROPERTY, serviceInfo.getServiceType());
				reply.setStringProperty(ManageConstants.SERVICENAME_PROPERTY, serviceInfo.getServiceName());
				log.print("sending reply [" + JmsUtils.toString(reply) + "]");
				if (message.getJMSReplyTo() != null) {
					reply.setJMSCorrelationID(message.getJMSMessageID());
					controlSession.publishMessage((Topic)message.getJMSReplyTo(), reply);
				} else {
					controlSession.publishMessage(JmsUtils.getTopicDaemonControl(), reply);
				}
			}
		} else if (msgType.equals(ManageConstants.MESSAGE_TYPE_ASKPERFORMANCESTATUS_VALUE)) {			
			serviceInfo.setPerformance(JmsUtils.getServicePerformance());			
			Message reply = controlSession.createObjectMessage(serviceInfo);
			reply.setStringProperty(ManageConstants.MESSAGE_TYPE_PROPERTY, ManageConstants.MESSAGE_TYPE_REPLYPERFORMANCESTATUS_VALUE);
			controlSession.publishMessage(JmsUtils.getTopicDaemonControl(), reply);			
			log.print("sending reply [" + JmsUtils.toString(reply) + "]");
			
		} else if (msgType.equals(ManageConstants.MESSAGE_TYPE_STOPSERVICE_VALUE)) {
			hostname = (String)JmsUtils.parseProperty(message, ManageConstants.HOSTNAME_PROPERTY, String.class);
			serviceType = (String)JmsUtils.parseProperty(message, ManageConstants.SERVICETYPE_PROPERTY, String.class);
			serviceName = (String)JmsUtils.parseProperty(message, ManageConstants.SERVICENAME_PROPERTY, String.class);
			if (hostname != null && hostname.equalsIgnoreCase(serviceInfo.getHostName()) 
				&& serviceType != null && serviceType.equalsIgnoreCase(serviceInfo.getServiceType()) 
				&& serviceName != null && serviceName.equalsIgnoreCase(serviceInfo.getServiceName()))  {
				stopService();
			}
			
		} else if (msgType.equals(ManageConstants.MESSAGE_TYPE_STOPBOOTSTRAP_VALUE)) {
			hostname = (String)JmsUtils.parseProperty(message, ManageConstants.HOSTNAME_PROPERTY, String.class);
			String serverType = (String)JmsUtils.parseProperty(message, ManageConstants.SERVER_TYPE_PROPERTY, String.class);
			if (hostname != null && hostname.equalsIgnoreCase(serviceInfo.getHostName()) 
				&& serverType != null && serverType.equalsIgnoreCase(ManageConstants.SERVER_TYPE_BOOTSTRAP))  {
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
	topicConn = jmsConnFactory.createTopicConnection();
	listenTopicSession = topicConn.getAutoSession();	
	listenTopicSession.setupListener(JmsUtils.getTopicDaemonControl(), getDaemonControlFilter(jmsServiceProvider.getServiceInfo()), new ControlMessageCollector(this));
	topicConn.startConnection();
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
		t.join(5000);	
	} catch (InterruptedException ex) {
	} finally {
		System.exit(0);
	}
}
}
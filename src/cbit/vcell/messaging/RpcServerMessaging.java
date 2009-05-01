package cbit.vcell.messaging;
import javax.jms.*;

import org.vcell.util.MessageConstants;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;

import java.io.Serializable;
import cbit.rmi.event.ExportEvent;
import cbit.vcell.messaging.server.RpcServer;
import cbit.vcell.messaging.server.RpcRequest;

/**
 * Insert the type's description here.
 * Creation date: (7/15/2003 10:08:03 AM)
 * @author: Fei Gao
 */
public class RpcServerMessaging extends JmsServiceProviderMessaging implements QueueListener, ControlTopicListener {
	private JmsSession clientRequestReceiver = null;
	private String queueName = null;
	private String msgSelector = null;

/**
 * RpcMessaging constructor comment.
 * @param argJmsFactory cbit.vcell.messaging.JmsFactory
 * @param slog cbit.vcell.server.SessionLog
 */
public RpcServerMessaging(RpcServer rpcServer, String qname, String selector, SessionLog log0) throws javax.jms.JMSException {
	super(rpcServer, log0);
	queueName = qname;
	msgSelector = selector;

	reconnect();
}


/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 3:58:52 PM)
 * @param message javax.jms.Message
 */
public void onQueueMessage(Message message) {
	try {
		long t = System.currentTimeMillis();
		
		if (message == null) {
			try {
				clientRequestReceiver.rollback(); // no message so far
			} catch (Exception ex) {
				log.exception(ex);
			}			
			return;
		}
			
		log.print("onClientRequestMessage[" + JmsUtils.toString(message) + "]");
		if (!(message instanceof ObjectMessage)) {
			clientRequestReceiver.commit(); // ignore the bad messages
			return;
		}

		Object obj = ((ObjectMessage) message).getObject();
		if (!(obj instanceof RpcRequest)) {
			clientRequestReceiver.commit(); // ignore the bad messages
			return;
		}
		
		RpcRequest request = (RpcRequest)obj;	
		log.print(request + "");
			
		java.io.Serializable returnValue = null;
		try {
			returnValue = (Serializable) ((RpcServer)jmsServiceProvider).dispatchRPC(request);
		} catch (Exception ex) {
			log.exception(ex);
			returnValue = ex; // if exception occurs, send client the exception
		}

		if (returnValue != null && returnValue.getClass().isArray()) {
			Class<?> componentClass = returnValue.getClass().getComponentType();
			if (!componentClass.isPrimitive() && !Serializable.class.isAssignableFrom(componentClass)) {
				returnValue = new ClassCastException("Not serializable:" + componentClass);
			}
		}
		t = System.currentTimeMillis() - t;

		// if client is not waiting any more, why bother sending the reply. Plus the temporary queue
		// has been deleted if client has timed out.
		long clientTimeoutMS = Long.parseLong(org.vcell.util.PropertyLoader.getRequiredProperty(org.vcell.util.PropertyLoader.vcellClientTimeoutMS)); 
		if (t < clientTimeoutMS) {		
			Queue replyTo = (Queue)message.getJMSReplyTo();
			if (replyTo != null) {
				Message replyMessage = clientRequestReceiver.createObjectMessage(returnValue);
				replyMessage.setStringProperty(MessageConstants.METHOD_NAME_PROPERTY, request.getMethodName());
				replyMessage.setJMSCorrelationID(message.getJMSMessageID());
				
				clientRequestReceiver.sendMessage(replyTo, replyMessage, DeliveryMode.NON_PERSISTENT, clientTimeoutMS);
				
				if (returnValue == null) {
					log.print("sendClientResponse[null]");
				} else {
					log.print("sendClientResponse[" + returnValue.getClass() + "@" + Integer.toHexString(returnValue.hashCode()) + "]");
				}			
			}
		}
		
		clientRequestReceiver.commit();		//commit
			
	} catch (JMSException e) {
		log.exception(e);
		
		// roll it back if fail
		try {
			clientRequestReceiver.rollback();
		} catch (Exception ex) {
			log.exception(ex);
		}		
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 11:08:09 PM)
 * @param simulation cbit.vcell.solver.Simulation
 */
public void publishDataStatus(cbit.rmi.event.DataJobEvent event) throws JMSException  {
	try {
		JmsSession dataSession = jmsConn.getAutoSession();
		Message rpcMessage = dataSession.createObjectMessage(event);
		rpcMessage.setStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_DATA_EVENT_VALUE);
		rpcMessage.setStringProperty(MessageConstants.USERNAME_PROPERTY, event.getUser().getName());
		
		dataSession.publishMessage(JmsUtils.getTopicClientStatus(), rpcMessage);
		log.print("publishing data status: " + event);		
		jmsConn.closeSession(dataSession);
			
	} catch (Exception e){
		log.exception(e);
		throw new JMSException("RpcServerMessaging.publishExportStatus(): " + e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 11:08:09 PM)
 * @param simulation cbit.vcell.solver.Simulation
 */
public void publishExportStatus(ExportEvent event) throws JMSException  {
	try {
		JmsSession exportSession = jmsConn.getAutoSession();
		Message rpcMessage = exportSession.createObjectMessage(event);
		rpcMessage.setStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY, MessageConstants.MESSAGE_TYPE_EXPORT_EVENT_VALUE);
		rpcMessage.setStringProperty(MessageConstants.USERNAME_PROPERTY, event.getUser().getName());
		
		exportSession.publishMessage(JmsUtils.getTopicClientStatus(), rpcMessage);
		log.print("publishing export status: " + event);		
		jmsConn.closeSession(exportSession);
			
	} catch (Exception e){
		log.exception(e);
		throw new JMSException("RpcServerMessaging.publishExportStatus(): " + e.getMessage());
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 5:29:47 PM)
 */
protected void reconnect() throws JMSException {
	super.reconnect();
	
	clientRequestReceiver = jmsConn.getTransactedSession(); // transactional	
	int servicePrefetchCount = Integer.parseInt(PropertyLoader.getProperty(PropertyLoader.jmsServicePrefetchCount, "-1"));
	if (servicePrefetchCount > 0) {
		log.print("servicePrefetchCount=" + servicePrefetchCount);
		clientRequestReceiver.setPrefetchCount(servicePrefetchCount); // get messages one by one
	}
	clientRequestReceiver.setupQueueListener(queueName, msgSelector, new QueueMessageCollector(this));
	jmsConn.startConnection();
}


/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 5:29:47 PM)
 */
public void startListening() throws JMSException {
	log.print("I am starting to take requests!");
	jmsConn.startConnection();
}
}
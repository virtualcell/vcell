package cbit.vcell.messaging;
import javax.jms.*;

import org.vcell.util.MessageConstants;
import org.vcell.util.SessionLog;
import org.vcell.util.MessageConstants.ServiceType;

import cbit.vcell.messaging.server.RpcRequest;

/**
 * Insert the type's description here.
 * Creation date: (10/18/2001 4:31:45 PM)
 * @author: Fei Gao
 */
public class JmsClientMessaging {

	private JmsSession responseRequestor = null;
	private JmsConnection queueConn = null;

	private SessionLog log = null;
	private long timeSinceLastMessage = System.currentTimeMillis();

/**
 * Client constructor comment.
 */
public JmsClientMessaging(JmsConnection queueConn0, SessionLog log0) throws JMSException {
	log = log0;
	queueConn = queueConn0;
	
	reconnect();
}


/**
 * Insert the method's description here.
 * Creation date: (4/19/2004 12:08:02 PM)
 * @return long
 */
public long getTimeSinceLastMessage() {
	return timeSinceLastMessage;
}


/**
 * Insert the method's description here.
 * Creation date: (11/19/2001 5:29:47 PM)
 */
private void reconnect() throws JMSException {
	responseRequestor = queueConn.getAutoSession();
	queueConn.startConnection();
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 11:08:09 PM)
 * @param simulation cbit.vcell.solver.Simulation
 * Since clientMessaging only send messages, we don't consider onException() because that's asynchronized. 
 */
public synchronized Object rpc(RpcRequest request, String queueName, boolean returnRequired, String[] specialProperties, Object[] specialValues) throws Exception {
	return rpc(request, queueName, returnRequired, specialProperties, specialValues, true);
}


/**
 * Insert the method's description here.
 * Creation date: (10/24/2001 11:08:09 PM)
 * @param simulation cbit.vcell.solver.Simulation
 * Since clientMessaging only send messages, we don't consider onException() because that's asynchronized. 
 */
private synchronized Object rpc(RpcRequest request, String queueName, boolean returnRequired, String[] specialProperties, Object[] specialValues, boolean bEnableRetry) throws Exception {
	ServiceType serviceType = request.getRequestedServiceType();
	String methodName = request.getMethodName();
	
	try {		
		ObjectMessage rpcMessage = responseRequestor.createObjectMessage(request);
		rpcMessage.setStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY,MessageConstants.MESSAGE_TYPE_RPC_SERVICE_VALUE);
		rpcMessage.setStringProperty(MessageConstants.SERVICE_TYPE_PROPERTY,serviceType.getName());
		if (specialValues != null) {
			for (int i = 0; i < specialValues.length; i ++) {
				rpcMessage.setObjectProperty(specialProperties[i], specialValues[i]);
			}
		}

		log.print("Sending request[" + serviceType + "," + methodName + "] to " + queueName);
		setTimeSinceLastMessage(System.currentTimeMillis());
		if (returnRequired) {
			long clientTimeoutMS = Long.parseLong(org.vcell.util.PropertyLoader.getRequiredProperty(org.vcell.util.PropertyLoader.vcellClientTimeoutMS)); 
			Message msg = responseRequestor.queueRequest(this, queueName, rpcMessage, DeliveryMode.PERSISTENT, clientTimeoutMS); 
		
			if (msg == null || !(msg instanceof ObjectMessage)) {
				throw new JMSException("Server is temporarily not responding, please try again later. If problem persists, contact VCell_Support@uchc.edu. (server=" + serviceType + ", method=" + methodName +")");
			} else {				
				String methodResponseName = (String)JmsUtils.parseProperty(msg, MessageConstants.METHOD_NAME_PROPERTY, String.class);				
				if (methodResponseName != null && methodResponseName.equals(methodName)){
					log.print("server responded to RPC call (server=" + serviceType + ", method=" + methodName + ")");
					Object returnValue = ((ObjectMessage)msg).getObject();
					if (returnValue instanceof Exception){
						throw (Exception)returnValue;
					} else {
						return returnValue;
					}
				} else {
					return rpc(request, queueName, returnRequired, specialProperties, specialValues, false); // try again
				}			
			} 
		} else {			
			responseRequestor.sendMessage(queueName, rpcMessage, DeliveryMode.PERSISTENT, 0); // never expires
			log.print("(service=" + serviceType + ", method=" + methodName + "), but no return is required");
			return null;
		}
	} catch (JMSException e){
		log.print("ClientMessaging.rpc() failed " + e.getMessage());
		// if the connection is dropped, will retry
		if (queueConn.isBadConnection(e) && bEnableRetry) {
			log.print("ClientMessaging.rpc() retrying ");
			return rpc(request, queueName, returnRequired, specialProperties, specialValues, false);
		} else {
			throw e;
		}
	}	
}


/**
 * Insert the method's description here.
 * Creation date: (4/19/2004 12:08:02 PM)
 * @param newTimeSinceLastMessage long
 */
public void setTimeSinceLastMessage(long newTimeSinceLastMessage) {
	timeSinceLastMessage = newTimeSinceLastMessage;
}
}
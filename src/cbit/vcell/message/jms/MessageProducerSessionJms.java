package cbit.vcell.message.jms;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.vcell.util.MessageConstants;

import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingInvocationTargetException;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.mongodb.VCMongoMessage;

public class MessageProducerSessionJms implements VCMessageSession {
		
		private VCMessagingServiceJms vcMessagingServiceJms = null;
		private TemporaryQueue commonTemporaryQueue = null;
		private Connection connection = null;
		private Session session = null;
		protected boolean bIndependent;
		
		public MessageProducerSessionJms(VCMessagingServiceJms vcMessagingServiceJms) throws JMSException {
			this.vcMessagingServiceJms = vcMessagingServiceJms;
			this.connection = vcMessagingServiceJms.createConnectionFactory().createConnection();
			this.connection.setExceptionListener(new ExceptionListener() {
				public void onException(JMSException arg0) {
					MessageProducerSessionJms.this.onException(arg0);
				}
			});
			this.connection.start();
			boolean bTransacted = true;
			this.session = connection.createSession(bTransacted, Session.AUTO_ACKNOWLEDGE);
			this.commonTemporaryQueue = session.createTemporaryQueue();
			this.bIndependent = true;
		}

		public MessageProducerSessionJms(Session session) {
			this.session = session;
			this.bIndependent = false;
		}

		public Object sendRpcMessage(VCellQueue queue, VCRpcRequest vcRpcRequest, boolean returnRequired, long timeoutMS, String[] specialProperties, Object[] specialValues) throws VCMessagingException, VCMessagingInvocationTargetException {
			MessageProducer messageProducer = null;
			try {
				if (!bIndependent){
					throw new VCMessagingException("cannot invoke RpcMessage from within another transaction, create an independent message producer");
				}
				Destination destination = session.createQueue(queue.getName());
				messageProducer = session.createProducer(destination);				
				ObjectMessage rpcMessage = session.createObjectMessage(vcRpcRequest);
				rpcMessage.setStringProperty(MessageConstants.MESSAGE_TYPE_PROPERTY,MessageConstants.MESSAGE_TYPE_RPC_SERVICE_VALUE);
				rpcMessage.setStringProperty(MessageConstants.SERVICE_TYPE_PROPERTY,vcRpcRequest.getRequestedServiceType().getName());
				if (specialValues != null) {
					for (int i = 0; i < specialValues.length; i ++) {
						rpcMessage.setObjectProperty(specialProperties[i], specialValues[i]);
					}
				}

				if (returnRequired) {
					rpcMessage.setJMSReplyTo(commonTemporaryQueue);
					messageProducer.setTimeToLive(timeoutMS);
					messageProducer.send(rpcMessage);
					session.commit();
System.out.println("rpcMessage sent: id='"+rpcMessage.getJMSMessageID()+"'");
					String filter = MessageConstants.JMSCORRELATIONID_PROPERTY + "='" + rpcMessage.getJMSMessageID() + "'";
					MessageConsumer replyConsumer = session.createConsumer(commonTemporaryQueue,filter);
					Message replyMessage = replyConsumer.receive(timeoutMS);
					replyConsumer.close();
					if (replyMessage == null) {
						System.out.println("Request timed out");
					}

					if (replyMessage == null || !(replyMessage instanceof ObjectMessage)) {
						throw new JMSException("Server is temporarily not responding, please try again later. If problem persists, contact VCell_Support@uchc.edu." +
								" (server=" + vcRpcRequest.getRequestedServiceType().getName() + ", method=" + vcRpcRequest.getMethodName() +")");
					} else {				
						Object returnValue = ((ObjectMessage)replyMessage).getObject();
						if (returnValue instanceof Exception){
							throw new VCMessagingInvocationTargetException((Exception)returnValue);
						} else {
							return returnValue;
						}
					} 
				} else {
					messageProducer.send(rpcMessage);
					commit();
					return null;
				}
			} catch (JMSException e){
				onException(e);
				throw new VCMessagingException(e.getMessage(),e);
			} finally {
				try {
					if (messageProducer!=null){
						messageProducer.close();
					}
				} catch (JMSException e) {
					onException(e);
				}
			}
		}

		public void sendQueueMessage(VCellQueue queue, VCMessage message) throws VCMessagingException {
			if (message instanceof VCMessageJms){
				try {
					Destination destination = session.createQueue(queue.getName());
					MessageProducer messageProducer = session.createProducer(destination);
					messageProducer.send(((VCMessageJms)message).getJmsMessage());
					if (bIndependent){
						session.commit();
					}
					VCMongoMessage.sendJmsMessageSent(message,queue);
				} catch (JMSException e) {
					onException(e);
				}
			}else{
				throw new RuntimeException("expected JMS message for JMS message service");
			}
		}
		
		public void sendTopicMessage(VCellTopic topic, VCMessage message) throws VCMessagingException {
			if (message instanceof VCMessageJms){
				VCMessageJms jmsMessage = (VCMessageJms)message;
				try {
					MessageProducer producer = session.createProducer(session.createTopic(topic.getName()));
					producer.send(jmsMessage.getJmsMessage());
					if (bIndependent){
						session.commit();
					}
					VCMongoMessage.sendJmsMessageSent(message,topic);
				} catch (JMSException e) {
					e.printStackTrace(System.out);
					onException(e);
				}				
			}else{
				throw new RuntimeException("must send a JMS message to a JMS messaging service");
			}
		}

		public void rollback() {
			try {
				session.rollback();
			}catch (JMSException e){
				onException(e);
			}
		}

		public void commit() {
			try {
				session.commit();
			}catch (JMSException e){
				onException(e);
			}
		}

		public VCMessage createTextMessage(String text) {
			try {
				Message jmsMessage = session.createTextMessage(text);
				return new VCMessageJms(jmsMessage);
			} catch (JMSException e) {
				e.printStackTrace(System.out);
				onException(e);
				throw new RuntimeException("unable to create text message");
			}
		}
		public VCMessage createObjectMessage(Serializable object) {
			try {
				Message jmsMessage = session.createObjectMessage(object);
				return new VCMessageJms(jmsMessage);
			} catch (JMSException e) {
				e.printStackTrace(System.out);
				onException(e);
				throw new RuntimeException("unable to create text message");
			}
		}
		public VCMessage createMessage() {
			try {
				Message jmsMessage = session.createMessage();
				return new VCMessageJms(jmsMessage);
			} catch (JMSException e) {
				e.printStackTrace(System.out);
				onException(e);
				throw new RuntimeException("unable to create message");
			}
		}

		private void onException(JMSException e){
			e.printStackTrace(System.out);
		}

		public void close() {
			try {
				if (session!=null){
					session.close();
				}
				if (commonTemporaryQueue!=null){
					commonTemporaryQueue.delete();
				}
				if (connection!=null){
					connection.close();
				}
			}catch (JMSException e){
				onException(e);
			}
		}
	}
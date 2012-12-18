package cbit.vcell.message.jms;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;

import cbit.vcell.message.RollbackException;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessagingConsumer;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCQueueConsumer;
import cbit.vcell.message.VCRpcConsumer;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCTopicConsumer;
import cbit.vcell.message.messages.MessageConstants;
import cbit.vcell.mongodb.VCMongoMessage;

public class ConsumerContextJms implements Runnable {
	public static final long CONSUMER_POLLING_INTERVAL_MS = 2000;
	private VCMessagingServiceJms vcMessagingServiceJms = null;
	private VCMessagingConsumer vcConsumer = null;
	private Session jmsSession = null;
	private Connection jmsConnection = null;
	private MessageConsumer jmsMessageConsumer = null;
	private boolean bProcessing = false;
	private Thread thread = null;
	private SessionLog log = new StdoutSessionLog("consumer");
	
	public ConsumerContextJms(VCMessagingServiceJms vcMessagingServiceJms, VCMessagingConsumer consumer){
		this.vcMessagingServiceJms = vcMessagingServiceJms;
		this.vcConsumer = consumer;
	}
	
	public void run(){
		bProcessing = true;
		System.out.println(toString()+" consumer thread starting.");
		while (bProcessing){
			try {
				Message jmsMessage = jmsMessageConsumer.receive(CONSUMER_POLLING_INTERVAL_MS);
				if (jmsMessage!=null){
//						System.out.println(toString()+"===============message received within "+CONSUMER_POLLING_INTERVAL_MS+" ms");
					if (vcConsumer instanceof VCQueueConsumer){
						VCQueueConsumer queueConsumer = (VCQueueConsumer)vcConsumer;
						VCMessageJms vcMessage = new VCMessageJms(jmsMessage);
						vcMessage.loadBlobFile();
						VCMongoMessage.sendJmsMessageReceived(vcMessage,vcConsumer.getVCDestination());
						MessageProducerSessionJms temporaryMessageProducerSession = new MessageProducerSessionJms(jmsSession);
						queueConsumer.getQueueListener().onQueueMessage(vcMessage, temporaryMessageProducerSession);
						jmsSession.commit();
						vcMessage.removeBlobFile();
					} else if (vcConsumer instanceof VCTopicConsumer){
						VCTopicConsumer topicConsumer = (VCTopicConsumer)vcConsumer;
						VCMessageJms vcMessage = new VCMessageJms(jmsMessage);
						vcMessage.loadBlobFile();
						VCMongoMessage.sendJmsMessageReceived(vcMessage,vcConsumer.getVCDestination());
						MessageProducerSessionJms temporaryMessageProducerSession = new MessageProducerSessionJms(jmsSession);
						topicConsumer.getTopicListener().onTopicMessage(vcMessage, temporaryMessageProducerSession);
						jmsSession.commit();
						vcMessage.removeBlobFile();
					} else {
						VCRpcConsumer rpcConsumer = (VCRpcConsumer)vcConsumer;
						if (!(jmsMessage instanceof ObjectMessage)){
							jmsSession.commit();
							throw new VCMessagingException("expecting ObjectMessage");
						}
						ObjectMessage objectMessage = (ObjectMessage)jmsMessage;
						VCMessageJms rpcVCMessage = new VCMessageJms(objectMessage);
						rpcVCMessage.loadBlobFile();
						VCMongoMessage.sendJmsMessageReceived(rpcVCMessage,vcConsumer.getVCDestination());
						Serializable object = (Serializable)rpcVCMessage.getObjectContent();
						if (!(object instanceof VCRpcRequest)){
							jmsSession.commit();
							throw new VCMessagingException("expecting RpcRequest in message");
						}
						VCRpcRequest vcRpcRequest = null;
						if (object instanceof VCRpcRequest){
							vcRpcRequest = (VCRpcRequest)object;
						}
						
						java.io.Serializable returnValue = null;
						try {
							//
							// invoke the local RPC implementation and collect either the return value or the exception that was thrown
							//

							returnValue = (Serializable) vcRpcRequest.rpc(rpcConsumer.getServiceImplementation(), log);
						} catch (Exception ex) {
							log.exception(ex);
							returnValue = ex; // if exception occurs, send client the exception
						}

						// check the return value for non-seriablable objects
						if (returnValue != null && returnValue.getClass().isArray()) {
							Class<?> componentClass = returnValue.getClass().getComponentType();
							if (!componentClass.isPrimitive() && !Serializable.class.isAssignableFrom(componentClass)) {
								returnValue = new ClassCastException("Not serializable:" + componentClass);
							}
						}

						// reply to "reply-to" queue with the return value or exception.
						long clientTimeoutMS = Long.parseLong(org.vcell.util.PropertyLoader.getRequiredProperty(org.vcell.util.PropertyLoader.vcellClientTimeoutMS)); 
						Queue replyTo = (Queue)jmsMessage.getJMSReplyTo();
						
						//
						// use MessageProducerSessionJms to create the replyMessage (allows "Blob" messages to be formed as needed).
						//
						MessageProducerSessionJms tempMessageProducerSessionJms = new MessageProducerSessionJms(jmsSession);
						VCMessageJms vcReplyMessage = (VCMessageJms)tempMessageProducerSessionJms.createObjectMessage(returnValue);
						Message replyMessage = vcReplyMessage.getJmsMessage();
						
						replyMessage.setStringProperty(MessageConstants.METHOD_NAME_PROPERTY, vcRpcRequest.getMethodName());
						replyMessage.setJMSCorrelationID(jmsMessage.getJMSMessageID());
//{
//Enumeration<String> props = replyMessage.getPropertyNames();
//while (props.hasMoreElements()){
//	System.out.println("ReplyMessage property="+props.nextElement());
//}
//}
						MessageProducer replyProducer = jmsSession.createProducer(replyTo);
						replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
						replyProducer.setTimeToLive(clientTimeoutMS);
						replyProducer.send(replyMessage);
						replyProducer.close();
						jmsSession.commit();		//commit		
						VCMongoMessage.sendRpcRequestProcessed(vcRpcRequest,rpcVCMessage);
						rpcVCMessage.removeBlobFile();
					}
				}else{
//						System.out.println(toString()+"no message received within "+CONSUMER_POLLING_INTERVAL_MS+" ms");
				}
			} catch (JMSException e) {
				onException(e);
			} catch (RollbackException e) {
				log.exception(e);
				try {
					jmsSession.rollback();
				} catch (JMSException e1) {
					onException(e1);
				}
			} catch (Exception e) {
				log.exception(e);
			}
		}
		System.out.println(toString()+" consumer thread exiting.");
	}
	
	
	
	public void start() {
		if (bProcessing){
			throw new RuntimeException("consumer already started");
		}
		setThread(new Thread(this,vcConsumer.getThreadName()));
		getThread().setDaemon(true);
		getThread().start();
	}
	public void stop(){
		if (bProcessing){
			bProcessing=false;
			System.out.println(toString()+" consumer thread stop requested");
		}
	}
	public void init() throws JMSException {
		boolean bTransacted = true;
		int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;
		try {
			this.jmsConnection = vcMessagingServiceJms.createConnectionFactory().createConnection();
			this.jmsConnection.setExceptionListener(new ExceptionListener() {
				public void onException(JMSException arg0) {
					ConsumerContextJms.this.onException(arg0);
				}
			});
			this.jmsConnection.start();
			this.jmsSession = this.jmsConnection.createSession(bTransacted, acknowledgeMode);
			this.jmsMessageConsumer = this.vcMessagingServiceJms.createConsumer(this.jmsSession, vcConsumer.getVCDestination(), vcConsumer.getSelector(), vcConsumer.getPrefetchLimit());
		}catch (JMSException e){
			e.printStackTrace(System.out);
			onException(e);
		}
	}
	
	private void onException(JMSException e){
		VCMongoMessage.sendException(e);
		e.printStackTrace(System.out);
	}
	
	public VCMessagingConsumer getVCConsumer() {
		return vcConsumer;
	}
	
	void close() {
		try {
			if (jmsMessageConsumer!=null){
				jmsMessageConsumer.close();
			}
			if (jmsSession!=null){
				jmsSession.close();
			}
			if (jmsConnection!=null){
				jmsConnection.stop();
				jmsConnection.close();
			}
		}catch (JMSException e){
			onException(e);
		}
	}
	public Thread getThread() {
		return thread;
	}
	public void setThread(Thread thread) {
		this.thread = thread;
	}
}
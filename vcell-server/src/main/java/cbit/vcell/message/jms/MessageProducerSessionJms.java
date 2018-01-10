package cbit.vcell.message.jms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.bson.types.ObjectId;
import org.vcell.util.BeanUtils;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingConstants;
import cbit.vcell.message.VCMessagingDelegate;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingInvocationTargetException;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;
import cbit.vcell.mongodb.VCMongoDbDriver;
import cbit.vcell.resource.PropertyLoader;

public class MessageProducerSessionJms implements VCMessageSession {
//		private static int tmpQCnt = 0;
		private VCMessagingServiceJms vcMessagingServiceJms = null;
		private TemporaryQueue commonTemporaryQueue = null;
		private Connection connection = null;
		private Session session = null;
		protected boolean bIndependent;

		public MessageProducerSessionJms(VCMessagingServiceJms vcMessagingServiceJms) throws JMSException, VCMessagingException {
//			System.out.println("-----\nmpjms MessageProducerSessionJms(VCMessagingServiceJms vcMessagingServiceJms)\ntmpQCnt="+(++tmpQCnt)+"----------");
//			Thread.dumpStack();
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

//		public MessageProducerSessionJms(Session session, VCMessagingServiceJms vcMessagingServiceJms) {
//			System.out.println("-----\nmpjms MessageProducerSessionJms(Session session, VCMessagingServiceJms vcMessagingServiceJms)\ntmpQCnt="+(++tmpQCnt)+"----------");
//			Thread.dumpStack();
//			this.vcMessagingServiceJms = vcMessagingServiceJms;
//			this.session = session;
//			this.bIndependent = false;
//		}

		public /*synchronized*/ Object sendRpcMessage(VCellQueue queue, VCRpcRequest vcRpcRequest, boolean returnRequired, long timeoutMS, String[] specialProperties, Object[] specialValues, UserLoginInfo userLoginInfo) throws VCMessagingException, VCMessagingInvocationTargetException {
			MessageProducer messageProducer = null;
			MessageProducerSessionJms tempMessageProducerSessionJms = null;
			try {
				if (!bIndependent){
					throw new VCMessagingException("cannot invoke RpcMessage from within another transaction, create an independent message producer");
				}
				Destination destination = session.createQueue(queue.getName());
				messageProducer = session.createProducer(destination);

				//
				// use MessageProducerSessionJms to create the rpcRequest message (allows "Blob" messages to be formed as needed).
				//
				tempMessageProducerSessionJms = new MessageProducerSessionJms(vcMessagingServiceJms);
//				tempMessageProducerSessionJms = new MessageProducerSessionJms(session,vcMessagingServiceJms);
				VCMessageJms vcRpcRequestMessage = (VCMessageJms)tempMessageProducerSessionJms.createObjectMessage(vcRpcRequest);
				Message rpcMessage = vcRpcRequestMessage.getJmsMessage();

				rpcMessage.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY,VCMessagingConstants.MESSAGE_TYPE_RPC_SERVICE_VALUE);
				rpcMessage.setStringProperty(VCMessagingConstants.SERVICE_TYPE_PROPERTY,vcRpcRequest.getRequestedServiceType().getName());
//				rpcMessage.setJMSExpiration(5000);
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
					vcMessagingServiceJms.getDelegate().onRpcRequestSent(vcRpcRequest, userLoginInfo, vcRpcRequestMessage);
System.out.println("MessageProducerSessionJms.sendRpcMessage(): looking for reply message with correlationID = "+rpcMessage.getJMSMessageID());
					String filter = VCMessagingConstants.JMSCORRELATIONID_PROPERTY + "='" + rpcMessage.getJMSMessageID() + "'";
					MessageConsumer replyConsumer = null;
					Message replyMessage = null;
					try {
					    replyConsumer = session.createConsumer(commonTemporaryQueue,filter);
						replyMessage = replyConsumer.receive(timeoutMS);
					}
					finally {
						replyConsumer.close();
					}
					if (replyMessage == null) {
						System.out.println("Request timed out");
					}

					if (replyMessage == null || !(replyMessage instanceof ObjectMessage)) {
						throw new JMSException("Server is temporarily not responding, please try again. If problem persists, contact VCell_Support@uchc.edu." +
								" (server=" + vcRpcRequest.getRequestedServiceType().getName() + ", method=" + vcRpcRequest.getMethodName() +")");
					} else {
						VCMessageJms vcReplyMessage = new VCMessageJms(replyMessage, vcMessagingServiceJms.getDelegate());
						vcReplyMessage.loadBlobFile();
						Object returnValue = vcReplyMessage.getObjectContent();
						vcReplyMessage.removeBlobFile();
						if (returnValue instanceof Exception){
							throw new VCMessagingInvocationTargetException((Exception)returnValue);
						} else {
							return returnValue;
						}
					}
				} else {
					rpcMessage.setJMSReplyTo(commonTemporaryQueue);
					messageProducer.setTimeToLive(timeoutMS);
					messageProducer.send(rpcMessage);
					commit();
					vcMessagingServiceJms.getDelegate().onRpcRequestSent(vcRpcRequest, userLoginInfo, vcRpcRequestMessage);
					return null;
				}
			} catch (JMSException e){
				onException(e);
				throw new VCMessagingException(e.getMessage(),e);
			} finally {
				try {

					if(tempMessageProducerSessionJms != null){
						tempMessageProducerSessionJms.commit();
						tempMessageProducerSessionJms.close();
					}

					if (messageProducer!=null){
						messageProducer.close();
					}
//					try{Thread.sleep(10000);}catch(Exception e){e.printStackTrace();}
				} catch (JMSException e) {
					onException(e);
				}
			}
		}

		@Override
		public void sendQueueMessage(VCellQueue queue, VCMessage message, Boolean persistent, Long timeToLiveMS) throws VCMessagingException {
			if (message instanceof VCMessageJms){
				MessageProducer messageProducer = null;
				try {
					Destination destination = session.createQueue(queue.getName());
					messageProducer = session.createProducer(destination);
					if (persistent==null || persistent.booleanValue()){
						messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
					}else{
						messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
					}
					if (timeToLiveMS!=null){
						messageProducer.setTimeToLive(timeToLiveMS);
					}
					messageProducer.send(((VCMessageJms)message).getJmsMessage());
					if (bIndependent){
						session.commit();
					}
					vcMessagingServiceJms.getDelegate().onMessageSent(message,queue);
				} catch (JMSException e) {
					onException(e);
				} finally {
					if (messageProducer != null){
						try {
							messageProducer.close();
						} catch (JMSException e) {
							e.printStackTrace();
						}
					}
				}
			}else{
				throw new RuntimeException("expected JMS message for JMS message service");
			}
		}

		public void sendQueueMessage(VCellQueue queue, VCMessage message) throws VCMessagingException {
			sendQueueMessage(queue,message,null,null);
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
					vcMessagingServiceJms.getDelegate().onMessageSent(message,topic);
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
				return new VCMessageJms(jmsMessage, vcMessagingServiceJms.getDelegate());
			} catch (JMSException e) {
				e.printStackTrace(System.out);
				onException(e);
				throw new RuntimeException("unable to create text message",e);
			}
		}
		public VCMessage createObjectMessage(Serializable object) {
			try {
				// if the serialized object is very large, send it as a BlobMessage (ActiveMQ specific).
				long t1 = System.currentTimeMillis();
				byte[] serializedBytes = null;

				if (object!=null){
					serializedBytes = BeanUtils.toSerialized(object);
				}

				long blobMessageSizeThreshold = Long.parseLong(PropertyLoader.getProperty(PropertyLoader.jmsBlobMessageMinSize, "100000"));
				boolean USE_MONGO = Boolean.parseBoolean(PropertyLoader.getRequiredProperty(PropertyLoader.jmsBlobMessageUseMongo));
				if (serializedBytes!=null && serializedBytes.length > blobMessageSizeThreshold){
					if (!USE_MONGO) {
						//
						// get (or create) directory to store Message BLOBs
						//
						File tempdir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.jmsBlobMessageTempDir));
						if (!tempdir.exists()){
							tempdir.mkdirs();
						}
	
						//
						// write serialized message to "temp" file.
						//
						File blobFile = File.createTempFile("BlobMessage",".data",tempdir);
						FileOutputStream fileOutputStream = new FileOutputStream(blobFile);
						FileChannel channel = fileOutputStream.getChannel();
						channel.write(ByteBuffer.wrap(serializedBytes));
						channel.close();
						fileOutputStream.close();
	
						ObjectMessage objectMessage = session.createObjectMessage("emptyObject");
						objectMessage.setStringProperty(VCMessageJms.BLOB_MESSAGE_PERSISTENCE_TYPE, VCMessageJms.BLOB_MESSAGE_PERSISTENCE_TYPE_FILE);
						objectMessage.setStringProperty(VCMessageJms.BLOB_MESSAGE_PRODUCER_TEMPDIR, tempdir.getAbsolutePath());
						objectMessage.setStringProperty(VCMessageJms.BLOB_MESSAGE_FILE_NAME, blobFile.getName());
						objectMessage.setStringProperty(VCMessageJms.BLOB_MESSAGE_OBJECT_TYPE, object.getClass().getName());
						objectMessage.setIntProperty(VCMessageJms.BLOB_MESSAGE_OBJECT_SIZE, serializedBytes.length);
						vcMessagingServiceJms.getDelegate().onTraceEvent("MessageProducerSessionJms.createObjectMessage: (BLOB) size="+serializedBytes.length+", type="+object.getClass().getName()+", elapsedTime = "+(System.currentTimeMillis()-t1)+" ms");
						return new VCMessageJms(objectMessage,object, vcMessagingServiceJms.getDelegate());
					} else {
						String hexString = Long.toHexString(Math.abs(new Random().nextLong()));
						ObjectId objectId = VCMongoDbDriver.getInstance().storeBLOB("jmsblob_name_"+hexString, "jmsblob", serializedBytes);
						ObjectMessage objectMessage = session.createObjectMessage("emptyObject");
						objectMessage.setStringProperty(VCMessageJms.BLOB_MESSAGE_PERSISTENCE_TYPE, VCMessageJms.BLOB_MESSAGE_PERSISTENCE_TYPE_MONGODB);
						objectMessage.setStringProperty(VCMessageJms.BLOB_MESSAGE_MONGODB_OBJECTID, objectId.toHexString());
						objectMessage.setStringProperty(VCMessageJms.BLOB_MESSAGE_OBJECT_TYPE, object.getClass().getName());
						objectMessage.setIntProperty(VCMessageJms.BLOB_MESSAGE_OBJECT_SIZE, serializedBytes.length);
						vcMessagingServiceJms.getDelegate().onTraceEvent("MessageProducerSessionJms.createObjectMessage: (BLOB) size="+serializedBytes.length+", type="+object.getClass().getName()+", elapsedTime = "+(System.currentTimeMillis()-t1)+" ms");
						return new VCMessageJms(objectMessage,object, vcMessagingServiceJms.getDelegate());
					}
				}else{
					ObjectMessage objectMessage = (ObjectMessage)session.createObjectMessage(object);
					int size = (serializedBytes!=null)?(serializedBytes.length):(0);
					String objectType = (serializedBytes!=null)?(object.getClass().getName()):("NULL");
					vcMessagingServiceJms.getDelegate().onTraceEvent("MessageProducerSessionJms.createObjectMessage: (NOBLOB) size="+size+", type="+objectType+", elapsedTime = "+(System.currentTimeMillis()-t1)+" ms");
					return new VCMessageJms(objectMessage, vcMessagingServiceJms.getDelegate());
				}
			} catch (JMSException e) {
				e.printStackTrace(System.out);
				onException(e);
				throw new RuntimeException("unable to create object message",e);
			} catch (Exception e){
				e.printStackTrace();
				throw new RuntimeException(e.getMessage(),e);
			}
		}

		public VCMessage createMessage() {
			try {
				Message jmsMessage = session.createMessage();
				return new VCMessageJms(jmsMessage, vcMessagingServiceJms.getDelegate());
			} catch (JMSException e) {
				e.printStackTrace(System.out);
				onException(e);
				throw new RuntimeException("unable to create message",e);
			}
		}

		private void onException(JMSException e){
			if (getDelegate()!=null){
				getDelegate().onException(e);
			}
			e.printStackTrace(System.out);
		}

		public void close() {
			try {
//				System.out.println("---------------\nmpjms close()\ntmpQCnt="+(--tmpQCnt)+"--------------------");
//				Thread.dumpStack();
////				if(msgProducers.size() > 0){
//					Thread.dumpStack();
//				}
				if (session!=null){
					session.close();
				}
				if (commonTemporaryQueue!=null){
					commonTemporaryQueue.delete();
				}
				if (connection!=null){
					connection.stop();
					connection.close();
				}
			}catch (JMSException e){
				onException(e);
			}
		}

		@Override
		public VCMessagingDelegate getDelegate() {
			return vcMessagingServiceJms.getDelegate();
		}
	}
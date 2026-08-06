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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.vcell.util.CompressionUtils;
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
    private VCMessagingServiceJms vcMessagingServiceJms = null;
    private TemporaryQueue commonTemporaryQueue = null;
    private Connection connection = null;
    private Session session = null;
    protected boolean bIndependent;
    private static Logger lg = LogManager.getLogger(MessageProducerSessionJms.class);

    public MessageProducerSessionJms(VCMessagingServiceJms vcMessagingServiceJms) throws JMSException, VCMessagingException{
        this.vcMessagingServiceJms = vcMessagingServiceJms;
        this.bIndependent = true;
    }

    /**
     * Opens the connection and session on first use rather than in the constructor.
     *
     * ConsumerContextJms builds one of these for every message it receives and hands it to
     * the listener, but most listeners never send anything through it: the pooled consumers
     * (SimDataServer, DatabaseServer, HtcSimulationWorker) substitute their own shared
     * session, and the client-status topic listener ignores the argument altogether. Opening
     * eagerly therefore cost a connection per message for consumers that had no use for one,
     * which is what turned a redelivery loop in production into ~3,300 connections a minute.
     * Deferring means those paths pay nothing, while the paths that do send (the dispatcher's
     * worker-event and sim-request handlers) behave exactly as before.
     */
    private synchronized Session getSession() throws JMSException{
        if(session == null){
            Connection newConnection = createConnection();
            try {
                newConnection.start();
                boolean bTransacted = true;
                this.session = newConnection.createSession(bTransacted, Session.AUTO_ACKNOWLEDGE);
                this.connection = newConnection;
            } catch(JMSException e){
                try {
                    newConnection.close();
                } catch(JMSException ignored){
                }
                throw e;
            }
        }
        return session;
    }

    /**
     * Opens the connection now instead of on first use. Long-lived sessions handed out by
     * {@link VCMessagingServiceJms#createProducerSession()} are opened at server startup and
     * are always used, so they open eagerly and a broker problem still surfaces there rather
     * than at the first send.
     */
    void open() throws JMSException{
        getSession();
    }

    private Connection createConnection() throws JMSException{
        Connection newConnection;
        try {
            newConnection = vcMessagingServiceJms.createConnectionFactory().createConnection();
        } catch(VCMessagingException e){
            // callers of getSession() can only propagate JMSException
            JMSException jmsException = new JMSException("unable to create JMS connection: " + e.getMessage());
            jmsException.initCause(e);
            throw jmsException;
        }
        vcMessagingServiceJms.getFailoverWatchdog().attach(newConnection);
        newConnection.setExceptionListener(new ExceptionListener() {
            public void onException(JMSException arg0){
                MessageProducerSessionJms.this.onException(arg0);
            }
        });
        return newConnection;
    }

    /** The open connection, opening it if necessary. Unlike Session, Connection is thread-safe. */
    private synchronized Connection getConnection() throws JMSException{
        getSession();
        return connection;
    }

    /**
     * Opens the session for a send, failing loudly if it cannot be opened.
     *
     * The send methods deliberately swallow a JMSException from send() itself, but failing
     * to open the connection is not that: before the connection was deferred it happened in
     * the constructor, so ConsumerContextJms saw it before the listener ran and left the
     * incoming message uncommitted for redelivery. Throwing here keeps that -- otherwise a
     * listener would be told its send succeeded and the inbound message would be committed.
     */
    private Session openSessionForSend() throws VCMessagingException{
        try {
            return getSession();
        } catch(JMSException e){
            onException(e);
            throw new VCMessagingException("unable to open JMS session: " + e.getMessage(), e);
        }
    }

    /**
     * The temporary queue is a reply destination for sendRpcMessage and nothing else, so it
     * is created with the first RPC rather than alongside the session.
     */
    private synchronized TemporaryQueue getReplyQueue() throws JMSException{
        if(commonTemporaryQueue == null){
            commonTemporaryQueue = getSession().createTemporaryQueue();
        }
        return commonTemporaryQueue;
    }

    public /*synchronized*/ Object sendRpcMessage(VCellQueue queue, VCRpcRequest vcRpcRequest, boolean returnRequired, long timeoutMS, String[] specialProperties, Object[] specialValues, UserLoginInfo userLoginInfo) throws VCMessagingException, VCMessagingInvocationTargetException{
        MessageProducer messageProducer = null;
        Session messageSession = null;
        try {
            if(!bIndependent){
                throw new VCMessagingException("cannot invoke RpcMessage from within another transaction, create an independent message producer");
            }
            Session jmsSession = getSession();
            Destination destination = jmsSession.createQueue(queue.getName());
            messageProducer = jmsSession.createProducer(destination);

            //
            // Build the request message on a session of its own so that forming it -- which for a
            // large request means serializing and writing a BLOB -- does not touch the session this
            // RPC is being sent on. RpcService hands a single producer session to every request
            // thread, so that session is in concurrent use (see the commented-out `synchronized` on
            // this method).
            //
            // Bug 4668 (2016) got that isolation by building a whole second MessageProducerSessionJms
            // here, which meant a JMS connection, session and temporary queue per RPC. It had to be
            // independent only because the same commit added close() in the finally block, and
            // closing a shared session would have closed the caller's. A session off the connection
            // we already hold gives the same isolation: Connection is thread-safe, Session is not,
            // and creating one costs no new connection.
            //
            messageSession = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
            VCMessageJms vcRpcRequestMessage = (VCMessageJms) createObjectMessage(messageSession, vcRpcRequest);
            Message rpcMessage = vcRpcRequestMessage.getJmsMessage();

            rpcMessage.setStringProperty(VCMessagingConstants.MESSAGE_TYPE_PROPERTY, VCMessagingConstants.MESSAGE_TYPE_RPC_SERVICE_VALUE);
            rpcMessage.setStringProperty(VCMessagingConstants.SERVICE_TYPE_PROPERTY, vcRpcRequest.getRequestedServiceType().getName());
//				rpcMessage.setJMSExpiration(5000);
            if(specialValues != null){
                for(int i = 0; i < specialValues.length; i++){
                    rpcMessage.setObjectProperty(specialProperties[i], specialValues[i]);
                }
            }

            if(returnRequired){
                rpcMessage.setJMSReplyTo(getReplyQueue());
                messageProducer.setTimeToLive(timeoutMS);
                messageProducer.send(rpcMessage);
                jmsSession.commit();
                vcMessagingServiceJms.getDelegate().onRpcRequestSent(vcRpcRequest, userLoginInfo, vcRpcRequestMessage);
                if(lg.isTraceEnabled())
                    lg.trace("MessageProducerSessionJms.sendRpcMessage(): looking for reply message with correlationID = " + rpcMessage.getJMSMessageID());
                String filter = VCMessagingConstants.JMSCORRELATIONID_PROPERTY + "='" + rpcMessage.getJMSMessageID() + "'";
                MessageConsumer replyConsumer = null;
                Message replyMessage = null;
                try {
                    replyConsumer = jmsSession.createConsumer(getReplyQueue(), filter);
                    replyMessage = replyConsumer.receive(timeoutMS);
                } finally {
                    replyConsumer.close();
                }
                if(replyMessage == null){
                    lg.info("Request timed out");
                }

                if(replyMessage == null || !(replyMessage instanceof ObjectMessage)){
                    throw new JMSException("Server is temporarily not responding, please try again. If problem persists, contact VCell_Support@uchc.edu." +
                            " (server " + vcRpcRequest.getRequestedServiceType().getName() + ", method " + vcRpcRequest.getMethodName() + ")");
                } else {
                    VCMessageJms vcReplyMessage = new VCMessageJms(replyMessage, vcMessagingServiceJms.getDelegate());
                    vcReplyMessage.loadBlobFile();
                    Object returnValue = vcReplyMessage.getObjectContent();
                    vcReplyMessage.removeBlobFile();
                    if(returnValue instanceof Exception){
                        throw new VCMessagingInvocationTargetException((Exception) returnValue);
                    } else {
                        return returnValue;
                    }
                }
            } else {
                rpcMessage.setJMSReplyTo(getReplyQueue());
                messageProducer.setTimeToLive(timeoutMS);
                messageProducer.send(rpcMessage);
                commit();
                vcMessagingServiceJms.getDelegate().onRpcRequestSent(vcRpcRequest, userLoginInfo, vcRpcRequestMessage);
                return null;
            }
        } catch(JMSException e){
            onException(e);
            throw new VCMessagingException(e.getMessage(), e);
        } finally {
            try {

                if(messageSession != null){
                    // not transacted and never sent on, so there is nothing to commit
                    messageSession.close();
                }

                if(messageProducer != null){
                    messageProducer.close();
                }
            } catch(JMSException e){
                onException(e);
            }
        }
    }

    @Override
    public void sendQueueMessage(VCellQueue queue, VCMessage message, Boolean persistent, Long timeToLiveMS) throws VCMessagingException{
        if(message instanceof VCMessageJms){
            Session jmsSession = openSessionForSend();
            MessageProducer messageProducer = null;
            try {
                Destination destination = jmsSession.createQueue(queue.getName());
                messageProducer = jmsSession.createProducer(destination);
                if(persistent == null || persistent.booleanValue()){
                    messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
                } else {
                    messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
                }
                if(timeToLiveMS != null){
                    messageProducer.setTimeToLive(timeToLiveMS);
                }
                messageProducer.send(((VCMessageJms) message).getJmsMessage());
                if(bIndependent){
                    jmsSession.commit();
                }
                vcMessagingServiceJms.getDelegate().onMessageSent(message, queue);
            } catch(JMSException e){
                onException(e);
            } finally {
                if(messageProducer != null){
                    try {
                        messageProducer.close();
                    } catch(JMSException e){
                        lg.error(e.getMessage(), e);
                    }
                }
            }
        } else {
            throw new RuntimeException("expected JMS message for JMS message service");
        }
    }

    public void sendQueueMessage(VCellQueue queue, VCMessage message) throws VCMessagingException{
        sendQueueMessage(queue, message, null, null);
    }

    public void sendTopicMessage(VCellTopic topic, VCMessage message) throws VCMessagingException{
        if(message instanceof VCMessageJms){
            VCMessageJms jmsMessage = (VCMessageJms) message;
            Session jmsSession = openSessionForSend();
            try {
                MessageProducer producer = jmsSession.createProducer(jmsSession.createTopic(topic.getName()));
                producer.send(jmsMessage.getJmsMessage());
                if(bIndependent){
                    jmsSession.commit();
                }
                vcMessagingServiceJms.getDelegate().onMessageSent(message, topic);
            } catch(JMSException e){
                onException(e);
            }
        } else {
            throw new RuntimeException("must send a JMS message to a JMS messaging service");
        }
    }

    public synchronized void rollback(){
        if(session == null){
            return;   // never opened, so there is nothing to roll back
        }
        try {
            session.rollback();
        } catch(JMSException e){
            onException(e);
        }
    }

    public synchronized void commit(){
        if(session == null){
            return;   // never opened, so there is nothing to commit
        }
        try {
            session.commit();
        } catch(JMSException e){
            onException(e);
        }
    }

    public VCMessage createTextMessage(String text){
        try {
            Message jmsMessage = getSession().createTextMessage(text);
            return new VCMessageJms(jmsMessage, vcMessagingServiceJms.getDelegate());
        } catch(JMSException e){
            onException(e);
            throw new RuntimeException("unable to create text message", e);
        }
    }

    public VCMessage createObjectMessage(Serializable object){
        try {
            return createObjectMessage(getSession(), object);
        } catch(JMSException e){
            onException(e);
            throw new RuntimeException("unable to create object message", e);
        }
    }

    /**
     * Builds the message on the given session rather than this one, so that sendRpcMessage can
     * keep message construction off a session that other request threads may be using.
     */
    private VCMessage createObjectMessage(Session jmsSession, Serializable object){
        try {
            // if the serialized object is very large, send it as a BlobMessage (ActiveMQ specific).
            long t1 = System.currentTimeMillis();
            byte[] serializedBytes = null;

            if(object != null){
                serializedBytes = CompressionUtils.toSerialized(object);
            }

            long blobMessageSizeThreshold = Long.parseLong(PropertyLoader.getProperty(PropertyLoader.jmsBlobMessageMinSize, "100000"));
            boolean USE_MONGO = Boolean.parseBoolean(PropertyLoader.getRequiredProperty(PropertyLoader.jmsBlobMessageUseMongo));
            if(serializedBytes != null && serializedBytes.length > blobMessageSizeThreshold){
                if(!USE_MONGO){
                    //
                    // get (or create) directory to store Message BLOBs
                    //
                    File tempdir = new File(PropertyLoader.getRequiredProperty(PropertyLoader.jmsBlobMessageTempDir));
                    if(!tempdir.exists()){
                        tempdir.mkdirs();
                    }

                    //
                    // write serialized message to "temp" file.
                    //
                    File blobFile = File.createTempFile("BlobMessage", ".data", tempdir);
                    FileOutputStream fileOutputStream = new FileOutputStream(blobFile);
                    FileChannel channel = fileOutputStream.getChannel();
                    channel.write(ByteBuffer.wrap(serializedBytes));
                    channel.close();
                    fileOutputStream.close();

                    ObjectMessage objectMessage = jmsSession.createObjectMessage("emptyObject");
                    objectMessage.setStringProperty(VCMessageJms.BLOB_MESSAGE_PERSISTENCE_TYPE, VCMessageJms.BLOB_MESSAGE_PERSISTENCE_TYPE_FILE);
                    objectMessage.setStringProperty(VCMessageJms.BLOB_MESSAGE_PRODUCER_TEMPDIR, tempdir.getAbsolutePath());
                    objectMessage.setStringProperty(VCMessageJms.BLOB_MESSAGE_FILE_NAME, blobFile.getName());
                    objectMessage.setStringProperty(VCMessageJms.BLOB_MESSAGE_OBJECT_TYPE, object.getClass().getName());
                    objectMessage.setIntProperty(VCMessageJms.BLOB_MESSAGE_OBJECT_SIZE, serializedBytes.length);
                    vcMessagingServiceJms.getDelegate().onTraceEvent("MessageProducerSessionJms.createObjectMessage: (BLOB) size=" + serializedBytes.length + ", type=" + object.getClass().getName() + ", elapsedTime = " + (System.currentTimeMillis() - t1) + " ms");
                    return new VCMessageJms(objectMessage, object, vcMessagingServiceJms.getDelegate());
                } else {
                    String hexString = Long.toHexString(Math.abs(new Random().nextLong()));
                    ObjectId objectId = VCMongoDbDriver.getInstance().storeBLOB("jmsblob_name_" + hexString, "jmsblob", serializedBytes);
                    ObjectMessage objectMessage = jmsSession.createObjectMessage("emptyObject");
                    objectMessage.setStringProperty(VCMessageJms.BLOB_MESSAGE_PERSISTENCE_TYPE, VCMessageJms.BLOB_MESSAGE_PERSISTENCE_TYPE_MONGODB);
                    objectMessage.setStringProperty(VCMessageJms.BLOB_MESSAGE_MONGODB_OBJECTID, objectId.toHexString());
                    objectMessage.setStringProperty(VCMessageJms.BLOB_MESSAGE_OBJECT_TYPE, object.getClass().getName());
                    objectMessage.setIntProperty(VCMessageJms.BLOB_MESSAGE_OBJECT_SIZE, serializedBytes.length);
                    vcMessagingServiceJms.getDelegate().onTraceEvent("MessageProducerSessionJms.createObjectMessage: (BLOB) size=" + serializedBytes.length + ", type=" + object.getClass().getName() + ", elapsedTime = " + (System.currentTimeMillis() - t1) + " ms");
                    return new VCMessageJms(objectMessage, object, vcMessagingServiceJms.getDelegate());
                }
            } else {
                ObjectMessage objectMessage = (ObjectMessage) jmsSession.createObjectMessage(object);
                int size = (serializedBytes != null) ? (serializedBytes.length) : (0);
                String objectType = (serializedBytes != null) ? (object.getClass().getName()) : ("NULL");
                vcMessagingServiceJms.getDelegate().onTraceEvent("MessageProducerSessionJms.createObjectMessage: (NOBLOB) size=" + size + ", type=" + objectType + ", elapsedTime = " + (System.currentTimeMillis() - t1) + " ms");
                return new VCMessageJms(objectMessage, vcMessagingServiceJms.getDelegate());
            }
        } catch(JMSException e){
            onException(e);
            throw new RuntimeException("unable to create object message", e);
        } catch(Exception e){
            lg.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public VCMessage createMessage(){
        try {
            Message jmsMessage = getSession().createMessage();
            return new VCMessageJms(jmsMessage, vcMessagingServiceJms.getDelegate());
        } catch(JMSException e){
            onException(e);
            throw new RuntimeException("unable to create message", e);
        }
    }

    private void onException(JMSException e){
        if(getDelegate() != null){
            getDelegate().onException(e);
        }
        lg.error(e.getMessage(), e);
        lg.error(e);
    }

    public synchronized void close(){
        // a session that was never opened has nothing to close -- see getSession()
        try {
            if(session != null){
                session.close();
            }
            if(commonTemporaryQueue != null){
                commonTemporaryQueue.delete();
            }
            if(connection != null){
                connection.stop();
                connection.close();
            }
        } catch(JMSException e){
            onException(e);
        }
    }

    @Override
    public VCMessagingDelegate getDelegate(){
        return vcMessagingServiceJms.getDelegate();
    }
}
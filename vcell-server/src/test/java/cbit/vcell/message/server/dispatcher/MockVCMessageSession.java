package cbit.vcell.message.server.dispatcher;

import cbit.vcell.message.*;
import cbit.vcell.message.jms.VCMessageJms;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.vcell.util.document.UserLoginInfo;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class MockVCMessageSession implements VCMessageSession {


    public MockVCMessageSession(){ }

    private final HashMap<VCellTopic, Queue<VCMessage>> topics = new HashMap<>(){{
        put(VCellTopic.ClientStatusTopic, new LinkedList<>());
        put(VCellTopic.ServiceControlTopic, new LinkedList<>());
    }};
    private final HashMap<VCellQueue, Queue<VCMessage>> queues = new HashMap<>(){{
        put(VCellQueue.WorkerEventQueue, new LinkedList<>());
        put(VCellQueue.DbRequestQueue, new LinkedList<>());
        put(VCellQueue.DataRequestQueue, new LinkedList<>());
        put(VCellQueue.SimReqQueue, new LinkedList<>());
        put(VCellQueue.SimJobQueue, new LinkedList<>());
    }};

    @Override
    public Object sendRpcMessage(VCellQueue queue, VCRpcRequest vcRpcRequest, boolean returnRequired, long timeoutMS, String[] specialProperties, Object[] specialValues, UserLoginInfo userLoginInfo) throws VCMessagingException, VCMessagingInvocationTargetException {
        return null;
    }

    @Override
    public void sendQueueMessage(VCellQueue queue, VCMessage message, Boolean persistent, Long clientTimeoutMS) throws VCMessagingException {
        queues.get(queue).add(message);
    }

    @Override
    public void sendTopicMessage(VCellTopic topic, VCMessage message) throws VCMessagingException {
        topics.get(topic).add(message);
    }

    @Override
    public void rollback() {

    }

    @Override
    public void commit() {

    }

    @Override
    public VCMessage createTextMessage(String text) {
        TextMessage textMessage = new ActiveMQTextMessage();
        try {
            textMessage.setText(text);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        return new VCMessageJms(textMessage, null);
    }

    @Override
    public VCMessage createMessage() {
        return new VCMessageJms(new ActiveMQMessage(), null, null);
    }

    @Override
    public VCMessage createObjectMessage(Serializable object) {
        ObjectMessage objectMessage = new ActiveMQObjectMessage();
        try {
            objectMessage.setObjectProperty(VCMessageJms.BLOB_MESSAGE_FILE_NAME, "");
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        return new VCMessageJms(objectMessage, object, null);

    }

    @Override
    public VCMessagingDelegate getDelegate() {
        return null;
    }

    @Override
    public void close() {

    }

    public VCMessage getTopicMessage(VCellTopic vCellTopic){
        return topics.get(vCellTopic).remove();
    }

    public VCMessage getQueueMessage(VCellQueue vCellQueue){
        return queues.get(vCellQueue).remove();
    }

}

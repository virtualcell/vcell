package cbit.vcell.message.local;

import java.io.Serializable;

import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingInvocationTargetException;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.message.VCellTopic;

public abstract class VCMessageSessionLocal implements VCMessageSession {

	@Override
	public Object sendRpcMessage(VCellQueue queue, VCRpcRequest vcRpcRequest, boolean returnRequired, long timeoutMS, String[] specialProperties, Object[] specialValues, UserLoginInfo userLoginInfo) 
			throws VCMessagingException, VCMessagingInvocationTargetException {
		throw new RuntimeException("rpc not implemented - must override sendRpcMessage() to implement");
	}

	@Override
	public void sendQueueMessage(VCellQueue queue, VCMessage message, Boolean persistent, Long timeToLiveMS) throws VCMessagingException {
		throw new RuntimeException("send Queue Message not implemented - must override sendQueueMessage() to implement");
	}

	@Override
	public void sendTopicMessage(VCellTopic topic, VCMessage message) throws VCMessagingException {
		throw new RuntimeException("send Topic Message not implemented - must override sendTopicMessage() to implement");
	}

	@Override
	public void rollback() {
	}

	@Override
	public void commit() {
	}

	@Override
	public VCMessage createTextMessage(String text) {
		return new VCMessageBSON(text);
	}

	@Override
	public VCMessage createMessage() {
		return new VCMessageBSON();
	}

	@Override
	public VCMessage createObjectMessage(Serializable object) {
		return new VCMessageBSON(object);
	}

	@Override
	public void close() {
	}

}

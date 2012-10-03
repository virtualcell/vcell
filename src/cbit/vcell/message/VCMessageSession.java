package cbit.vcell.message;

import java.io.Serializable;

public interface VCMessageSession {
	public Object sendRpcMessage(VCellQueue queue, VCRpcRequest vcRpcRequest, boolean returnRequired, long timeoutMS, String[] specialProperties, Object[] specialValues) throws VCMessagingException, VCMessagingInvocationTargetException;	
	public void sendQueueMessage(VCellQueue queue, VCMessage message) throws VCMessagingException;
	
	public void sendTopicMessage(VCellTopic topic, VCMessage message) throws VCMessagingException;
	
	void rollback();

	void commit();

	public abstract VCMessage createTextMessage(String text);

	public abstract VCMessage createMessage();

	public abstract VCMessage createObjectMessage(Serializable object);

	public abstract void close();
}
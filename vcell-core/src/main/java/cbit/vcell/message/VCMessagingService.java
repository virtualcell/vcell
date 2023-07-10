package cbit.vcell.message;

import java.util.List;

public interface VCMessagingService extends AutoCloseable {
	
	VCMessageSession createProducerSession();
	
	void addMessageConsumer(VCMessagingConsumer vcMessagingConsumer);
	
	void removeMessageConsumer(VCMessagingConsumer vcMessagingConsumer);
	
	List<VCMessagingConsumer> getMessageConsumers();
		
	void close() throws VCMessagingException;

	VCMessageSelector createSelector(String clientMessageFilter);
	
	VCMessagingDelegate getDelegate();
	
	void setConfiguration(VCMessagingDelegate delegate, String jmshost, int jmsport);
	
}





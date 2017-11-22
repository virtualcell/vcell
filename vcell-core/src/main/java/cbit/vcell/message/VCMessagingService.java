package cbit.vcell.message;

import java.util.List;

import org.vcell.service.VCellService;

public interface VCMessagingService extends VCellService, AutoCloseable {
	
	VCMessageSession createProducerSession();
	
	void addMessageConsumer(VCMessagingConsumer vcMessagingConsumer);
	
	void removeMessageConsumer(VCMessagingConsumer vcMessagingConsumer);
	
	List<VCMessagingConsumer> getMessageConsumers();
		
	void close() throws VCMessagingException;

	VCMessageSelector createSelector(String clientMessageFilter);
	
	VCMessagingDelegate getDelegate();
	
	void setDelegate(VCMessagingDelegate delegate);
	
}





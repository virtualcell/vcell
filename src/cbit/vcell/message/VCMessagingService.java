package cbit.vcell.message;

import java.util.List;

import org.vcell.util.PropertyLoader;

import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.message.jms.sonicMQ.VCMessagingServiceSonicMQ;

public abstract class VCMessagingService {
	
	public interface VCMessagingDelegate {
		public void onMessagingException(Exception e);
	}

	protected VCMessagingDelegate delegate = null;
	
	public VCMessagingService(){
	}
	
	public abstract VCMessageSession createProducerSession();
	
	public abstract void addMessageConsumer(VCMessagingConsumer vcMessagingConsumer);
	
	public abstract void removeMessageConsumer(VCMessagingConsumer vcMessagingConsumer);
	
	public abstract List<VCMessagingConsumer> getMessageConsumers();
	
	protected abstract void init(boolean bStartBroker) throws VCMessagingException;
	
	public abstract void closeAll() throws VCMessagingException;

	public void setDelegate(VCMessagingDelegate delegate) {
		this.delegate = delegate;
	}
	
	public VCMessagingDelegate getDelegate(){
		return this.delegate;
	}

	public abstract VCMessageSelector createSelector(String clientMessageFilter);
	
	public static VCMessagingService createInstance() throws VCMessagingException{
		VCMessagingService messagingService = null;
		
		String jmsProvider = PropertyLoader.getRequiredProperty(PropertyLoader.jmsProvider);
		if (jmsProvider.equalsIgnoreCase(PropertyLoader.jmsProviderValueSonicMQ)){
			messagingService = new VCMessagingServiceSonicMQ();
		}else if (jmsProvider.equalsIgnoreCase(PropertyLoader.jmsProviderValueActiveMQ)){
			messagingService = new VCMessagingServiceActiveMQ();
		}else{
			throw new RuntimeException("unrecognized jms provider : "+jmsProvider);
		}
		messagingService.init(false);
		return messagingService;
	}
	
}





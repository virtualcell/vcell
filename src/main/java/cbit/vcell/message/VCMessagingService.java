package cbit.vcell.message;

import java.util.List;

import org.vcell.util.PropertyLoader;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceEmbedded;

public abstract class VCMessagingService {
	
	public interface VCMessagingDelegate {
		public void onMessageReceived(VCMessage vcMessage, VCDestination vcDestination);
		public void onException(Exception e);
		public void onRpcRequestSent(VCRpcRequest vcRpcRequest, UserLoginInfo userLoginInfo, VCMessage vcRpcRequestMessage);
		public void onTraceEvent(String string);
		public void onMessageSent(VCMessage message, VCDestination desintation);
		public void onRpcRequestProcessed(VCRpcRequest vcRpcRequest, VCMessage rpcVCMessage);
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

	public VCMessagingDelegate getDelegate(){
		return this.delegate;
	}

	public abstract VCMessageSelector createSelector(String clientMessageFilter);
	
	public static VCMessagingService createInstance(VCMessagingDelegate messagingDelegate) throws VCMessagingException{
		VCMessagingService messagingService = null;
		String jmsProvider = PropertyLoader.getRequiredProperty(PropertyLoader.jmsProvider);
		if (jmsProvider.equalsIgnoreCase(PropertyLoader.jmsProviderValueActiveMQ)){
			messagingService = new VCMessagingServiceActiveMQ();
		}else{
			throw new RuntimeException("unrecognized jms provider : "+jmsProvider);
		}
		messagingService.delegate = messagingDelegate;
		messagingService.init(false);
		return messagingService;
	}
	
	public static VCMessagingServiceEmbedded createEmbeddedInstance(VCMessagingDelegate messagingDelegate) throws VCMessagingException{
		VCMessagingServiceEmbedded messagingService = new VCMessagingServiceEmbedded();
		messagingService.delegate = messagingDelegate;
		messagingService.init(true);
		return messagingService;
	}
	
}





package org.vcell.rest.server;

import java.io.Serializable;

import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCMessagingException;
import cbit.vcell.message.VCMessagingInvocationTargetException;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.resource.PropertyLoader;

public class RpcService {
	
	VCMessagingService vcMessagingService = null;

	public RpcService(VCMessagingService vcMessagingService) {
		this.vcMessagingService = vcMessagingService;
	}
		
	public Serializable sendRpcMessage(
			VCellQueue queue, 
			VCRpcRequest vcRpcRequest, 
			boolean returnRequired,
			String[] specialProperties, Object[] specialValues, 
			UserLoginInfo userLoginInfo)
					throws VCMessagingException, VCMessagingInvocationTargetException {
		
		int timeoutMS = Integer.parseInt(PropertyLoader.getProperty(PropertyLoader.vcellClientTimeoutMS, "120000")); // default to 2 minutes.
		try (VCMessageSession vcMessagingSession = vcMessagingService.createProducerSession();){
			return (Serializable)vcMessagingSession.sendRpcMessage(queue, vcRpcRequest, returnRequired, timeoutMS, specialProperties, specialValues, userLoginInfo);
		}
	}
	
}

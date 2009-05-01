package cbit.vcell.messaging.server;

import org.vcell.util.MessageConstants.ServiceType;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2004 3:16:36 PM)
 * @author: Fei Gao
 */
public interface RpcServerProxy {
	public Object rpc(ServiceType serviceType, String methodName, Object[] args, boolean returnRequired) throws Exception;
}

package cbit.vcell.messaging.server;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2004 3:16:36 PM)
 * @author: Fei Gao
 */
public interface RpcServerProxy {
public Object rpc(String serviceType, String methodName, Object[] args, boolean returnRequired) throws Exception;
}

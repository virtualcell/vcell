package cbit.vcell.messaging.server;

/**
 * Insert the type's description here.
 * Creation date: (2/20/2004 3:03:15 PM)
 * @author: Fei Gao
 */
public interface RpcServer extends ServiceProvider {
public Object dispatchRPC(RpcRequest request) throws Exception;
}

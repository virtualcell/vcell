package cbit.vcell.messaging.server;

import org.vcell.util.SessionLog;

/**
 * Insert the type's description here.
 * Creation date: (7/11/2006 11:31:12 AM)
 * @author: Jim Schaff
 */
public class RpcBNGServerImpl extends AbstractRpcServerImpl {
	private cbit.vcell.server.bionetgen.BNGServerImpl bngServerImpl = null;

/**
 * RpcBNGServerImpl constructor comment.
 * @param slog cbit.vcell.server.SessionLog
 */
protected RpcBNGServerImpl(SessionLog slog) {
	super(slog);
	bngServerImpl = new cbit.vcell.server.bionetgen.BNGServerImpl(log);
}


/**
 * Insert the method's description here.
 * Creation date: (7/11/2006 11:31:12 AM)
 * @return java.lang.Object
 */
public Object getServerImpl() {
	return bngServerImpl;
}
}
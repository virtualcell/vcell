package org.vcell.restq.rpc;

import cbit.vcell.message.VCMessageSession;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.resource.PropertyLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.document.UserLoginInfo;

public abstract class AbstractRpcServerProxy {
    public static final Logger lg = LogManager.getLogger(AbstractRpcServerProxy.class);

    private VCMessageSession vcMessagingSession = null;
    protected UserLoginInfo userLoginInfo = null;
    private VCellQueue queue = null;

    protected AbstractRpcServerProxy(UserLoginInfo userLoginInfo, VCMessageSession vcMessageSession, VCellQueue queue) {
        super();
        this.userLoginInfo = userLoginInfo;
        this.vcMessagingSession = vcMessageSession;
        this.queue = queue;
    }

    public Object rpc(VCRpcRequest.RpcServiceType serviceType, String methodName, Object[] args, boolean returnRequired) throws Exception {
        return rpc(serviceType, methodName, args, returnRequired, null, null);
    }
    
    public Object rpc(VCRpcRequest.RpcServiceType serviceType, String methodName, Object[] args, boolean returnRequired, String[] specialProperties, Object[] specialValues) throws Exception {
        VCRpcRequest vcRpcRequest = new VCRpcRequest(userLoginInfo.getUser(), serviceType, methodName, args);
        int timeoutMS = Integer.parseInt(PropertyLoader.getProperty(PropertyLoader.vcellClientTimeoutMS, "120000")); // default to 2 minutes.
        return vcMessagingSession.sendRpcMessage(queue, vcRpcRequest, returnRequired, timeoutMS, specialProperties, specialValues, userLoginInfo);
    }
}

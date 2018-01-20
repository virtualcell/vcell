package cbit.vcell.message.server.bootstrap.client;

import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;

public interface RpcSender {
	public Object sendRpcMessage(VCellQueue queue, VCRpcRequest vcRpcRequest, boolean returnRequired, int timeoutMS,
			String[] specialProperties, Object[] specialValues, UserLoginInfo userLoginInfo) throws Exception;
}

package cbit.vcell.message.server.bootstrap.client;

import java.io.IOException;

import org.vcell.util.document.UserLoginInfo;

import cbit.rmi.event.MessageEvent;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCellQueue;

public interface RpcSender {
	public Object sendRpcMessage(VCellQueue queue, VCRpcRequest vcRpcRequest, boolean returnRequired, int timeoutMS,
			String[] specialProperties, Object[] specialValues, UserLoginInfo userLoginInfo) throws Exception;

	public MessageEvent[] getMessageEvents() throws IOException;
}

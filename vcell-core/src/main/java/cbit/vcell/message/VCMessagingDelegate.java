package cbit.vcell.message;

import org.vcell.util.document.UserLoginInfo;

public interface VCMessagingDelegate {
	public void onMessageReceived(VCMessage vcMessage, VCDestination vcDestination);
	public void onException(Exception e);
	public void onRpcRequestSent(VCRpcRequest vcRpcRequest, UserLoginInfo userLoginInfo, VCMessage vcRpcRequestMessage);
	public void onTraceEvent(String string);
	public void onMessageSent(VCMessage message, VCDestination desintation);
	public void onRpcRequestProcessed(VCRpcRequest vcRpcRequest, VCMessage rpcVCMessage);
}
package cbit.vcell.message.server;

import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessagingDelegate;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.mongodb.VCMongoMessage;

public class ServerMessagingDelegate implements VCMessagingDelegate {

	@Override
	public void onMessageReceived(VCMessage vcMessage, VCDestination vcDestination) {
		VCMongoMessage.sendJmsMessageReceived(vcMessage, vcDestination);
	}

	@Override
	public void onException(Exception e) {
		VCMongoMessage.sendException(e);
	}

	@Override
	public void onRpcRequestSent(VCRpcRequest vcRpcRequest, UserLoginInfo userLoginInfo, VCMessage vcRpcRequestMessage) {
		VCMongoMessage.sendRpcRequestSent(vcRpcRequest, userLoginInfo, vcRpcRequestMessage);
	}

	@Override
	public void onTraceEvent(String string) {
		VCMongoMessage.sendTrace(string);
	}

	@Override
	public void onMessageSent(VCMessage message, VCDestination destination) {
		VCMongoMessage.sendJmsMessageSent(message, destination);
	}

	@Override
	public void onRpcRequestProcessed(VCRpcRequest vcRpcRequest, VCMessage rpcVCMessage) {
		VCMongoMessage.sendRpcRequestProcessed(vcRpcRequest, rpcVCMessage);
	}

}

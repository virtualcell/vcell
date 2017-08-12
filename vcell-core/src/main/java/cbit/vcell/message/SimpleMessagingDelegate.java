package cbit.vcell.message;

import org.vcell.util.document.UserLoginInfo;

public class SimpleMessagingDelegate implements VCMessagingDelegate {

	@Override
	public void onMessageReceived(VCMessage vcMessage, VCDestination vcDestination) {
		System.out.println("message received on "+vcDestination.getName()+", message="+vcMessage.show());
	}

	@Override
	public void onException(Exception e) {
		e.printStackTrace(System.out);
	}

	@Override
	public void onRpcRequestSent(VCRpcRequest vcRpcRequest, UserLoginInfo userLoginInfo, VCMessage vcRpcRequestMessage) {
		System.out.println("rpcRequest sent: "+vcRpcRequest.toString());
	}

	@Override
	public void onTraceEvent(String string) {
		System.out.println("trace: "+string);
	}

	@Override
	public void onMessageSent(VCMessage message, VCDestination destination) {
		System.out.println("message sent on "+destination.getName()+", message: "+message.show());
	}

	@Override
	public void onRpcRequestProcessed(VCRpcRequest vcRpcRequest, VCMessage rpcVCMessage) {
		System.out.println("rpcRequest processed: "+vcRpcRequest.toString());
	}

}

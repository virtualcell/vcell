package org.vcell.rest.rpc;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.security.Role;
import org.vcell.api.client.VCellApiClient;
import org.vcell.api.client.VCellApiClient.VCellApiRpcBody;
import org.vcell.api.client.VCellApiRpcRequest;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.VCellApiApplication.AuthenticationPolicy;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCRpcRequest.RpcServiceType;
import cbit.vcell.message.VCellQueue;

public final class RpcRestlet extends Restlet {
	public RpcRestlet(Context context) {
		super(context);
	}

	@Override
	public void handle(Request req, Response response) {
		if (req.getMethod().equals(Method.POST)){
			String destination = null;
			String method = null;
			try {
				VCellApiApplication application = ((VCellApiApplication)getApplication());
				User vcellUser = application.getVCellUser(req.getChallengeResponse(),AuthenticationPolicy.prohibitInvalidCredentials);
				HttpRequest request = (HttpRequest)req;
				String username = vcellUser.getName();
				String userkey = vcellUser.getID().toString();
				destination = request.getHeaders().getFirstValue("Destination");
				method = request.getHeaders().getFirstValue("Method");
				String returnRequired = request.getHeaders().getFirstValue("ReturnRequired");
				String timeoutMS = request.getHeaders().getFirstValue("TimeoutMS");
				String compressed = request.getHeaders().getFirstValue("Compressed");
				String klass = request.getHeaders().getFirstValue("Class");
				
				StringBuffer buffer = new StringBuffer();
				buffer.append("username="+username+", userkey="+userkey+", destination="+destination+", method="+method+"\n");
				buffer.append("returnRequired="+returnRequired+", timeoutMS="+timeoutMS+", compressed="+compressed+"\n");
				buffer.append("class="+klass);
				
				System.out.println(buffer.toString());
				
				req.bufferEntity();
				Serializable rpcRequestBodyObject = VCellApiClient.fromCompressedSerialized(req.getEntity().getStream());
				
				if (!(rpcRequestBodyObject instanceof VCellApiRpcBody)) {
					throw new RuntimeException("expecting post content of type VCellApiRpcBody");
				}
				VCellApiRpcBody rpcBody = (VCellApiRpcBody)rpcRequestBodyObject;
				RpcServiceType st = null;
				VCellQueue queue = null;
				switch (rpcBody.rpcRequest.rpcDestination) {
				case DataRequestQueue:{
					st = RpcServiceType.DATA;
					queue = VCellQueue.DataRequestQueue;
					break;
				}
				case DbRequestQueue:{
					st = RpcServiceType.DB;
					queue = VCellQueue.DbRequestQueue;
					break;
				}
				case SimReqQueue:{
					st = RpcServiceType.DISPATCH;
					queue = VCellQueue.SimReqQueue;
					break;
				}
				default:{
					throw new RuntimeException("unsupported RPC Destination: "+rpcBody.rpcDestination);
				}
				}
				VCellApiRpcRequest vcellapiRpcRequest = rpcBody.rpcRequest;
				Object[] arglist = vcellapiRpcRequest.args;
				String[] specialProperties = rpcBody.specialProperties;
				Object[] specialValues = rpcBody.specialValues;
				VCRpcRequest vcRpcRequest = new VCRpcRequest(vcellUser, st, method, arglist);
				VCellApiApplication vcellApiApplication = (VCellApiApplication)getApplication();
				RpcService rpcService = vcellApiApplication.getRpcService();
				Serializable result = rpcService.sendRpcMessage(
						queue, vcRpcRequest, new Boolean(rpcBody.returnedRequired), specialProperties, specialValues, new UserLoginInfo(username,null));
				
				byte[] serializedResultObject = VCellApiClient.toCompressedSerialized(result);
				response.setStatus(Status.SUCCESS_OK, "rpc method="+method+" succeeded");
				response.setEntity(new ByteArrayRepresentation(serializedResultObject));
			} catch (Exception e) {
				getLogger().severe("internal error invoking "+destination+":"+method+"(): "+e.getMessage());
				e.printStackTrace();
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				response.setEntity("internal error invoking "+destination+":"+method+"(): "+e.getMessage(), MediaType.TEXT_PLAIN);
			}
		}
	}
}
package org.vcell.rest.rpc;

import org.vcell.api.types.rpc.VCellApiRpcBody;
import cbit.vcell.clientdb.ServerRejectedSaveException;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCRpcRequest.RpcServiceType;
import cbit.vcell.message.VCellQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.representation.ByteArrayRepresentation;
import org.vcell.api.types.rpc.VCellApiRpcRequest;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.VCellApiApplication.AuthenticationPolicy;
import org.vcell.rest.server.RestDatabaseService;
import org.vcell.util.CompressionUtils;
import org.vcell.util.DataAccessException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.api.types.utils.DTOModelTransformerV0;

import java.io.ObjectInputFilter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class RpcRestlet extends Restlet {
	private static Logger lg = LogManager.getLogger(RpcRestlet.class);
	RestDatabaseService restDatabaseService;
	private static final List<String> vcellguestAllowed;
	//MUST keep sycnhronized with org.vcell.api.messaging.RemoteProxyVCellConnectionFactory
	static {
		String[] temp =  new String[] {
				"getVCInfoContainer",
				"getBioModelXML",
				"getMathModelXML",
				"getSimulationStatus",
				"getParticleDataExists",
				"getMesh",
				"getDataSetTimes",
				"getDataIdentifiers",
				"getPreferences",
				"getSimDataBlock",
				"doDataOperation",//Read post processing data
				"getODEData",
				"getNFSimMolecularConfigurations",
		};
		Arrays.sort(temp);
		vcellguestAllowed = Collections.unmodifiableList(Arrays.asList(temp));
	}
	/**
	 * Classes an RPC body may contain: the JDK basics, VCell's own types, and the model libraries VCell types
	 * hold as fields. Anything else (in particular the usual deserialization-gadget libraries) is rejected before
	 * its readObject can run. Operators can replace the pattern without a rebuild through the system property
	 * {@value #RPC_BODY_FILTER_PROPERTY} (JEP 290 pattern syntax).
	 */
	static final String RPC_BODY_FILTER_PROPERTY = "vcell.rpc.deserializationFilter";
	static final String RPC_BODY_FILTER_DEFAULT =
			"java.lang.*;java.util.**;java.math.*;java.time.**;java.io.File;" +
			"cbit.**;org.vcell.**;" +
			"ucar.units_vcell.**;net.sourceforge.interval.**;jscl.**;org.jmathml.**;" +
			"org.jdom2.**;org.openrdf.model.**;org.sbpax.**;org.jlibsedml.**;" +
			"!*";
	static final ObjectInputFilter RPC_BODY_FILTER = RpcRestlet.createRpcBodyFilter(
			System.getProperty(RpcRestlet.RPC_BODY_FILTER_PROPERTY, RpcRestlet.RPC_BODY_FILTER_DEFAULT));

	static ObjectInputFilter createRpcBodyFilter(String pattern) {
		ObjectInputFilter patternFilter = ObjectInputFilter.Config.createFilter(pattern);
		return filterInfo -> {
			ObjectInputFilter.Status status = patternFilter.checkInput(filterInfo);
			if (status == ObjectInputFilter.Status.REJECTED) {
				lg.warn("rejected class in rpc body: " + filterInfo.serialClass()
						+ " (extend -D" + RpcRestlet.RPC_BODY_FILTER_PROPERTY + " if it is legitimate)");
			}
			return status;
		};
	}

	public RpcRestlet(Context context,RestDatabaseService restDatabaseService) {
		super(context);
		this.restDatabaseService = restDatabaseService;
	}

	/**
	 * The service methods take the acting user as an ordinary argument that the client serializes. Never
	 * trust it: refuse a user other than the one the access token belongs to, and replace the client's copy
	 * with the database's, so a client can neither act as another account nor forge special claims
	 * (GHSA-9hpw-2h4g-fjhj).
	 */
	private Object[] bindUserArguments(Object[] clientArgs, User authenticatedUser) throws DataAccessException {
		if (clientArgs == null) return null;
		Object[] boundArgs = clientArgs.clone();
		User rpcCaller = null;
		for (int i = 0; i < boundArgs.length; i++) {
			if (!(boundArgs[i] instanceof User clientUser)) continue;
			boolean bSameUser = Objects.equals(authenticatedUser.getID(), clientUser.getID())
					&& Objects.equals(authenticatedUser.getName(), clientUser.getName());
			if (!bSameUser) {
				throw new PermissionException("rpc user argument '" + clientUser.getName()
						+ "' does not match authenticated user '" + authenticatedUser.getName() + "'");
			}
			if (rpcCaller == null) rpcCaller = this.restDatabaseService.getRpcCaller(authenticatedUser);
			boundArgs[i] = rpcCaller;
		}
		return boundArgs;
	}

	@Override
	public void handle(Request req, Response response) {
		if (req.getMethod().equals(Method.GET)){
			try {
				VCellApiApplication application = ((VCellApiApplication)getApplication());
				User vcellUser = application.getVCellUser(req.getChallengeResponse(),AuthenticationPolicy.prohibitInvalidCredentials);
				HttpRequest request = (HttpRequest)req;

				Form form = request.getResourceRef().getQueryAsForm();
				if (form.getFirst("stats") != null) {
					String requestTypeString = form.getFirstValue("stats", true);//get .../rpc?stats=value 'value'
					if ((vcellUser.getName().equals("frm") ||
							vcellUser.getName().equals("les") ||
							vcellUser.getName().equals("ion") ||
							vcellUser.getName().equals("danv") ||
							vcellUser.getName().equals("mblinov") ||
							vcellUser.getName().equals("schaff") ||
							vcellUser.getName().equals("ACowan"))) {
						String result = restDatabaseService.getBasicStatistics();
						response.setStatus(Status.SUCCESS_OK);
						response.setEntity(result, MediaType.TEXT_HTML);
						return;
					}
				}
			} catch (Exception e) {
				String errMesg = "<html><body>Error RpcRestlet.handle(...) req='"+req.toString()+"' <br>err='"+e.getMessage()+"'</br>"+"</body></html>";
				getLogger().severe(errMesg);
				lg.error(e.getMessage(), e);
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				response.setEntity(errMesg, MediaType.TEXT_HTML);
			}

		}else if (req.getMethod().equals(Method.POST)){
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
				
				if (lg.isTraceEnabled()) {
					lg.trace("username="+username+", userkey="+userkey+", destination="+destination+", method="+method+
							", returnRequired="+returnRequired+", timeoutMS="+timeoutMS+", compressed="+compressed+", class="+klass);
				}
				
				// The Method header names the method that is invoked; authorize it before touching the body.
				if (method == null || (User.isGuest(username) && !vcellguestAllowed.contains(method))) {
					throw new IllegalArgumentException(User.createGuestErrorMessage(method));
				}

				req.bufferEntity();
				Serializable rpcRequestBodyObject = CompressionUtils.fromCompressedSerialized(req.getEntity().getStream().readAllBytes(), RpcRestlet.RPC_BODY_FILTER);

				if (!(rpcRequestBodyObject instanceof VCellApiRpcBody)) {
					throw new RuntimeException("expecting post content of type VCellApiRpcBody");
				}
				VCellApiRpcBody rpcBody = (VCellApiRpcBody)rpcRequestBodyObject;
				if (!method.equals(rpcBody.rpcRequest.methodName)) {
					throw new IllegalArgumentException("rpc Method header '" + method + "' does not match body method '" + rpcBody.rpcRequest.methodName + "'");
				}
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
				Serializable serializableResultObject = null;
				/*if(vcellapiRpcRequest.methodName != null && vcellapiRpcRequest.methodName.equals("getVCInfoContainer")) {
					serializableResultObject = restDatabaseService.getVCInfoContainer(vcellUser);
				}else if(vcellapiRpcRequest.methodName != null && vcellapiRpcRequest.methodName.equals("getBioModelXML")) {
					serializableResultObject = restDatabaseService.getBioModelXML((KeyValue)vcellapiRpcRequest.args[1], vcellUser);
				}else if(vcellapiRpcRequest.methodName != null && vcellapiRpcRequest.methodName.equals("getMathModelXML")) {
					serializableResultObject = restDatabaseService.getMathModelXML((KeyValue)vcellapiRpcRequest.args[1], vcellUser);
				}else */if(vcellapiRpcRequest.methodName != null && vcellapiRpcRequest.methodName.equals("getSimulationStatus")) {
					if(vcellapiRpcRequest.args[1] instanceof KeyValue[]) {
						serializableResultObject = restDatabaseService.getSimulationStatus((KeyValue[])vcellapiRpcRequest.args[1], vcellUser);
					}else if (vcellapiRpcRequest.args[1] instanceof KeyValue){
						serializableResultObject = restDatabaseService.getSimulationStatus((KeyValue)vcellapiRpcRequest.args[1], vcellUser);						
					}
				}else if(vcellapiRpcRequest.methodName != null && vcellapiRpcRequest.methodName.equals("getSpecialUsers")) {
					serializableResultObject = restDatabaseService.getSpecialUsers(vcellUser);
				}else {
					Object[] arglist = this.bindUserArguments(vcellapiRpcRequest.args, vcellUser);
					String[] specialProperties = rpcBody.specialProperties;
					Object[] specialValues = rpcBody.specialValues;
					VCRpcRequest vcRpcRequest = new VCRpcRequest(vcellUser, st, method, arglist);
					VCellApiApplication vcellApiApplication = (VCellApiApplication)getApplication();
					RpcService rpcService = vcellApiApplication.getRpcService();
					serializableResultObject = rpcService.sendRpcMessage(
							queue, vcRpcRequest, rpcBody.returnedRequired, specialProperties, specialValues, new UserLoginInfo(username));
				}
				byte[] serializedResultObject = DTOModelTransformerV0.toCompressedSerialized(serializableResultObject);
				response.setStatus(Status.SUCCESS_OK, "rpc method="+method+" succeeded");
				response.setEntity(new ByteArrayRepresentation(serializedResultObject));
			} catch (Exception e) {
				lg.error("internal error invoking "+destination+":"+method+"(): "+e.getMessage(), e);
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				if(e.getCause() instanceof ServerRejectedSaveException) {//send back actual exception, client needs specific cause
					try {
						byte[] serializedResultObject = DTOModelTransformerV0.toCompressedSerialized(e.getCause());
						response.setEntity(new ByteArrayRepresentation(serializedResultObject));
						return;
					} catch (Exception e1) {
						lg.error(e1.getMessage(), e1);
						//continue and send error message
					}
				}
				response.setEntity("internal error invoking "+destination+":"+method+"(): "+e.getMessage(), MediaType.TEXT_PLAIN);
			}
		}
	}
}
package org.vcell.rest.rpc;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.engine.adapter.HttpRequest;
import org.restlet.engine.adapter.HttpResponse;
import org.restlet.engine.header.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.util.Series;
import org.vcell.api.client.VCellApiClient;
import org.vcell.api.client.VCellApiClient.VCellApiRpcBody;
import org.vcell.api.client.VCellApiRpcRequest;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.VCellApiApplication.AuthenticationPolicy;
import org.vcell.rest.server.RestDatabaseService;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.clientdb.ServerRejectedSaveException;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.VCRpcRequest.RpcServiceType;
import cbit.vcell.message.VCellQueue;
import cbit.vcell.resource.PropertyLoader;

public final class RpcRestlet extends Restlet {
	private static Logger lg = LogManager.getLogger(RpcRestlet.class);
	RestDatabaseService restDatabaseService;
	private static final List<String> vcellguestAllowed;
	//MUST keep sycnhronized with cbit.vcell.message.server.bootstrap.client.RemoteProxyVCellConnectionFactory
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
	public RpcRestlet(Context context,RestDatabaseService restDatabaseService) {
		super(context);
		this.restDatabaseService = restDatabaseService;
	}

	@Override
	public void handle(Request req, Response response) {
		if (req.getMethod().equals(Method.GET)){
			try {
				User authuser = null;
				HttpRequest request = (HttpRequest)req;
				//Use "WWW-Authenticate - Basic" authentication scheme
				//Browser takes care of asking user for credentials and sending them
				//Must be used with https connection to hide credentials
				Header authHeader = request.getHeaders().getFirst("Authorization");
				if(authHeader != null) {//caller included a user and password
					String typeAndCredential = authHeader.getValue();
//					System.out.println("--"+up);
					java.util.StringTokenizer st = new java.util.StringTokenizer(typeAndCredential," ");
					String type=st.nextToken();
					String userAndPasswordB64 = st.nextToken();
					String s = new String(Base64.getDecoder().decode(userAndPasswordB64));
//					System.out.println("type="+type+" decoded="+s);
					if(type.equals("Basic")) {
						java.util.StringTokenizer st2 = new java.util.StringTokenizer(s,":");
						if(st2.countTokens() == 2) {
							String usr=st2.nextToken();
							String pw = st2.nextToken();
	//						System.out.println("user="+usr+" password="+pw);
							UserLoginInfo.DigestedPassword dpw = new UserLoginInfo.DigestedPassword(pw);
	//						System.out.println(dpw);
							VCellApiApplication application = ((VCellApiApplication)getApplication());
							authuser = application.getUserVerifier().authenticateUser(usr,dpw.getString().toCharArray());
	//						System.out.println(authuser);
						}
					}
				}
				if(authuser == null) {
					//If we get here either there was not user/pw or user/pw didn't authenticate
					//We need to add a response header
					//Response headers container might be null so add one if necessary
					if(((HttpResponse)response).getHeaders() == null) {
						((HttpResponse)response).getAttributes().
							put(HeaderConstants.ATTRIBUTE_HEADERS,new Series(Header.class));
					}
					//Tell whoever called us we want a user and password that we will check against admin vcell users
					HttpResponse.addHeader(response,"WWW-Authenticate", "Basic");
					response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
					return;
				}
				
				Form form = request.getResourceRef().getQueryAsForm();
				if (form.getFirst("stats") != null){
					String requestTypeString = form.getFirstValue("stats", true);//get .../rpc?stats=value 'value'
					if((authuser.getName().equals("frm") || 
							authuser.getName().equals("les") ||
							authuser.getName().equals("ion") ||
							authuser.getName().equals("danv") ||
							authuser.getName().equals("mblinov") ||
							authuser.getName().equals("ACowan"))) {
							String result = restDatabaseService.getBasicStatistics();
							response.setStatus(Status.SUCCESS_OK);
							response.setEntity(result, MediaType.TEXT_HTML);
							return;
						} 								

				}else if(form.getValues("route") != null && form.getValues("type") != null && form.getValues("simid") != null && form.getValues("jobid") != null) {
					HttpClient httpClient = new HttpClient();
//					HostConfiguration hostConfig = new HostConfiguration();
//					hostConfig.setHost("vcelltest_data", 55555, Protocol.HTTPS.toString());
//					String identifier = req.getResourceRef().getIdentifier();
//					identifier+= "&userkey="+authuser.getID().toString()+"&userid="+authuser.getName();
					int webDataPort = Integer.parseInt(System.getProperty(PropertyLoader.webDataServerPort));
					String identifier = "https://data:"+webDataPort+"/"+
							form.getValues("route")+"/"+form.getValues("type")+
							"?simid="+form.getValues("simid")+"&jobid="+URLEncoder.encode(form.getValues("jobid"), "UTF-8")+
							"&userkey="+authuser.getID().toString()+"&userid="+URLEncoder.encode(authuser.getName(), "UTF-8");
					GetMethod httpMethod = new GetMethod(identifier);
					//Don't download the file here, let the browser do it
					httpMethod.setFollowRedirects(false);
					//Call data webserver, it will create the hdf5 file and save it to the 'export' dir
					//and return url location which we pass on to the original caller
					httpClient.executeMethod(httpMethod);
					//Pass the redirect to the original caller
					response.redirectSeeOther(httpMethod.getResponseHeader("Location").getValue());
				}
			} catch (Exception e) {
				String errMesg = "<html><body>Error RpcRestlet.handle(...) req='"+req.toString()+"' <br>err='"+e.getMessage()+"'</br>"+"</body></html>";
				getLogger().severe(errMesg);
				e.printStackTrace();
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
				
				req.bufferEntity();
				Serializable rpcRequestBodyObject = VCellApiClient.fromCompressedSerialized(req.getEntity().getStream());
				
				if (!(rpcRequestBodyObject instanceof VCellApiRpcBody)) {
					throw new RuntimeException("expecting post content of type VCellApiRpcBody");
				}
				VCellApiRpcBody rpcBody = (VCellApiRpcBody)rpcRequestBodyObject;
				if(rpcBody.rpcRequest.methodName == null || (User.isGuest(username) && !vcellguestAllowed.contains(rpcBody.rpcRequest.methodName))) {
					throw new IllegalArgumentException(User.createGuestErrorMessage(rpcBody.rpcRequest.methodName));
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
					Object[] arglist = vcellapiRpcRequest.args;
					String[] specialProperties = rpcBody.specialProperties;
					Object[] specialValues = rpcBody.specialValues;
					VCRpcRequest vcRpcRequest = new VCRpcRequest(vcellUser, st, method, arglist);
					VCellApiApplication vcellApiApplication = (VCellApiApplication)getApplication();
					RpcService rpcService = vcellApiApplication.getRpcService();
					serializableResultObject = rpcService.sendRpcMessage(
							queue, vcRpcRequest, new Boolean(rpcBody.returnedRequired), specialProperties, specialValues, new UserLoginInfo(username,null));
				}
				byte[] serializedResultObject = VCellApiClient.toCompressedSerialized(serializableResultObject);
				response.setStatus(Status.SUCCESS_OK, "rpc method="+method+" succeeded");
				response.setEntity(new ByteArrayRepresentation(serializedResultObject));
			} catch (Exception e) {
				getLogger().severe("internal error invoking "+destination+":"+method+"(): "+e.getMessage());
				e.printStackTrace();
				response.setStatus(Status.SERVER_ERROR_INTERNAL);
				if(e.getCause() instanceof ServerRejectedSaveException) {//send back actual exception, client needs specific cause
					try {
						byte[] serializedResultObject = VCellApiClient.toCompressedSerialized(e.getCause());
						response.setEntity(new ByteArrayRepresentation(serializedResultObject));
						return;
					} catch (Exception e1) {
						e1.printStackTrace();
						//continue and send error message
					}
				}
				response.setEntity("internal error invoking "+destination+":"+method+"(): "+e.getMessage(), MediaType.TEXT_PLAIN);
			}
		}
	}
}
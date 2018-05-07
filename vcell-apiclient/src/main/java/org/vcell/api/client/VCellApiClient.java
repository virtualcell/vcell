package org.vcell.api.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.api.client.query.BioModelsQuerySpec;
import org.vcell.api.client.query.SimTasksQuerySpec;
import org.vcell.api.common.AccessTokenRepresentation;
import org.vcell.api.common.BiomodelRepresentation;
import org.vcell.api.common.SimulationRepresentation;
import org.vcell.api.common.SimulationTaskRepresentation;
import org.vcell.api.common.UserInfo;
import org.vcell.api.common.events.EventWrapper;

import com.google.gson.Gson;

/**
 * This example demonstrates the use of the {@link ResponseHandler} to simplify
 * the process of processing the HTTP response and releasing associated
 * resources.
 */
public class VCellApiClient {
	
	private static final Logger lg = LogManager.getLogger(VCellApiClient.class);
	private HttpHost httpHost;
	private String clientID;
	private CloseableHttpClient httpclient;
	private HttpClientContext httpClientContext;
	boolean bIgnoreCertProblems = true;
	boolean bIgnoreHostMismatch = true;
	private final static String DEFAULT_CLIENTID = "85133f8d-26f7-4247-8356-d175399fc2e6";


	// Create a custom response handler
	public static class VCellStringResponseHandler implements ResponseHandler<String> {

		private final String methodCallString;
		private final HttpGet httpget;
		private final HttpPost httppost;
		
		public VCellStringResponseHandler(String methodCallString, HttpGet httpget) {
			this.methodCallString = methodCallString;
			this.httpget = httpget;
			this.httppost = null;
		}

		public VCellStringResponseHandler(String methodCallString, HttpPost httppost) {
			this.methodCallString = methodCallString;
			this.httpget = null;
			this.httppost = httppost;
		}
		
		@Override
		public String handleResponse(HttpResponse response) throws HttpResponseException, IOException {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity) : null;
			} else {
				HttpEntity entity = response.getEntity();
				String message = null;
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));){
					message = reader.lines().collect(Collectors.joining());
				}
				final URI uri;
				if (httpget!=null) {
					uri = httpget.getURI();
				}else {
					uri = httppost.getURI();
				}
				lg.error(methodCallString+" ("+uri+") failed: response status: " + status + "\nreason: " + message);
				throw new HttpResponseException(status, methodCallString+" failed: response status: " + status + "\nreason: " + message);
			}
		}
	}

	public VCellApiClient(String host, int port, boolean bIgnoreCertProblems, boolean bIgnoreHostMismatch) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		this.httpHost = new HttpHost(host,port,"https");
		this.clientID = DEFAULT_CLIENTID;
		this.bIgnoreCertProblems = bIgnoreCertProblems;
		this.bIgnoreHostMismatch = bIgnoreHostMismatch;
		initClient();
	}
	
	private void initClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

		SSLContextBuilder builder = new SSLContextBuilder();
		
		if (bIgnoreCertProblems){
			builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
		}
		SSLConnectionSocketFactory sslsf = null;

		if (bIgnoreHostMismatch){
			X509HostnameVerifier hostNameVerifier = new AllowAllHostnameVerifier();
			sslsf = new SSLConnectionSocketFactory(builder.build(),hostNameVerifier);
		}else{
			sslsf = new SSLConnectionSocketFactory(builder.build());
		}

		httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).setRedirectStrategy(new DefaultRedirectStrategy()).build();
		httpClientContext = HttpClientContext.create();
	}
	
	public void close() throws IOException {
		if (httpclient!=null){
			httpclient.close();
		}
	}
	
	

	public SimulationTaskRepresentation[] getSimTasks(SimTasksQuerySpec simTasksQuerySpec) throws IOException {

		HttpGet httpget = new HttpGet("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/simtask?"+simTasksQuerySpec.getQueryString());

		if (lg.isInfoEnabled()) {
			lg.info("Executing request to retrieve simulation tasks " + httpget.getRequestLine());
		}

		String responseBody = httpclient.execute(httpget, new VCellStringResponseHandler("getSimTasks()", httpget), httpClientContext);
		String simTasksJson = responseBody;
		if (lg.isInfoEnabled()) {
			lg.info("returned: "+toStringTruncated(simTasksJson));
		}

		Gson gson = new Gson();
		SimulationTaskRepresentation[] simTaskReps = gson.fromJson(simTasksJson,SimulationTaskRepresentation[].class);
		return simTaskReps;
	}
	
	public BiomodelRepresentation[] getBioModels(BioModelsQuerySpec bioModelsQuerySpec) throws IOException {
		  
		HttpGet httpget = new HttpGet("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/biomodel?"+bioModelsQuerySpec.getQueryString());

		if (lg.isInfoEnabled()) {
			lg.info("Executing request to retrieve biomodels " + httpget.getRequestLine());
		}

		String responseBody = httpclient.execute(httpget, new VCellStringResponseHandler("getBioModels()", httpget), httpClientContext);
		String bimodelsJson = responseBody;
		if (lg.isInfoEnabled()) {
			lg.info("returned: "+toStringTruncated(bimodelsJson));
		}

		Gson gson = new Gson();
		BiomodelRepresentation[] biomodelReps = gson.fromJson(bimodelsJson,BiomodelRepresentation[].class);
		return biomodelReps;
	}
	
	public EventWrapper[] getEvents(long beginTimestamp) throws IOException {
		  
		HttpGet httpget = new HttpGet("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/events?beginTimestamp="+beginTimestamp);

		if (lg.isInfoEnabled()) {
			lg.info("Executing request to retrieve user events " + httpget.getRequestLine());
		}

		String responseBody = httpclient.execute(httpget, new VCellStringResponseHandler("getEvents()", httpget), httpClientContext);
		String eventWrappersJson = responseBody;
		if (lg.isInfoEnabled()) {
			lg.info("returned: "+toStringTruncated(eventWrappersJson));
		}

		Gson gson = new Gson();
		EventWrapper[] eventWrappers = gson.fromJson(eventWrappersJson,EventWrapper[].class);
		return eventWrappers;
	}
	
	public BiomodelRepresentation getBioModel(String bmId) throws IOException {
		  
		HttpGet httpget = new HttpGet("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/biomodel/"+bmId);
		
		if (lg.isInfoEnabled()) {
			lg.info("Executing request to retrieve biomodel " + httpget.getRequestLine());
		}

		String responseBody = httpclient.execute(httpget, new VCellStringResponseHandler("getBioModel()", httpget), httpClientContext);
		String bimodelsJson = responseBody;
		if (lg.isInfoEnabled()) {
			lg.info("returned: "+toStringTruncated(bimodelsJson));
		}

		Gson gson = new Gson();
		BiomodelRepresentation biomodelRep = gson.fromJson(bimodelsJson,BiomodelRepresentation.class);
		return biomodelRep;
	}
	
	public String getBioModelVCML(String bmId) throws IOException {
		  
		HttpGet httpget = new HttpGet("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/biomodel/"+bmId+"/biomodel.vcml");
		
		if (lg.isInfoEnabled()) {
			lg.info("Executing request to retrieve biomodel " + httpget.getRequestLine());
		}

		String responseBody = httpclient.execute(httpget, new VCellStringResponseHandler("getBioModelVCML()", httpget), httpClientContext);
		String vcml = responseBody;

		return vcml;
	}
	
	public SimulationRepresentation getSimulation(String bmId, String simKey) throws IOException {
		  
		HttpGet httpget = new HttpGet("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/biomodel/"+bmId+"/simulation/"+simKey);
		
		if (lg.isInfoEnabled()) {
			lg.info("Executing request to retrieve simulation " + httpget.getRequestLine());
		}

		String responseBody = httpclient.execute(httpget, new VCellStringResponseHandler("getSimulation()", httpget), httpClientContext);
		String simulationJson = responseBody;
		if (lg.isInfoEnabled()) {
			lg.info("returned: "+toStringTruncated(simulationJson));
		}

		Gson gson = new Gson();
		SimulationRepresentation simulationRepresentation = gson.fromJson(simulationJson,SimulationRepresentation.class);
		return simulationRepresentation;
	}
	
	public String getOptRunJson(String optimizationId) throws IOException {
		HttpGet httpget = new HttpGet("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/optimization/"+optimizationId);
		
		if (lg.isInfoEnabled()) {
			lg.info("Executing request to retrieve optimization run " + httpget.getRequestLine());
		}

		String responseBody = httpclient.execute(httpget, new VCellStringResponseHandler("getOptRunJson()", httpget), httpClientContext);
		if (lg.isInfoEnabled()) {
			lg.info("returned: "+toStringTruncated(responseBody));
		}
		return responseBody;
	}

	public String submitOptimization(String optProblemJson) throws IOException, URISyntaxException {
		  
		HttpPost httppost = new HttpPost("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/optimization");
		StringEntity input = new StringEntity(optProblemJson);
		input.setContentType("application/json");
		httppost.setEntity(input);
		
		if (lg.isInfoEnabled()) {
			lg.info("Executing request to submit optProblem " + httppost.getRequestLine());
		}

		ResponseHandler<String> handler = new ResponseHandler<String>() {

			public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status == 202) {
					HttpEntity entity = response.getEntity();
					if (lg.isInfoEnabled()) {
						try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));){
							lg.info("optimizationId = "+reader.readLine());
						}
					}
			        final Header locationHeader = response.getFirstHeader("location");
			        if (locationHeader == null) {
			            // got a redirect response, but no location header
			            throw new ClientProtocolException(
			                    "Received redirect response " + response.getStatusLine()
			                    + " but no location header");
			        }
			        final String location = locationHeader.getValue();
			        URI uri = createLocationURI(location);
					return uri.toString();
				} else {
					HttpEntity entity = response.getEntity();
					String message = null;
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));){
						message = reader.lines().collect(Collectors.joining());
					}
					final URI uri = httppost.getURI();
					lg.error("submitOptimization() ("+uri+") failed: response status: " + status + "\nreason: " + message);
					throw new ClientProtocolException("submitOptimization() failed: response status: " + status + "\nreason: " + message);
				}
			}

		};
		String responseUri = httpclient.execute(httppost,handler,httpClientContext);
		if (lg.isInfoEnabled()) {
			lg.info("returned: "+toStringTruncated(responseUri));
		}

		String optimizationId = responseUri.substring(responseUri.lastIndexOf('/') + 1);
		return optimizationId;
	}
	
	/**
	 * from org.apache.http.impl.client.DefaultRedirectStrategy
	 * 
	 * @param location
	 * @return
	 * @throws ProtocolException
	 */
    private URI createLocationURI(final String location) throws ClientProtocolException {
        try {
            final URIBuilder b = new URIBuilder(new URI(location).normalize());
            final String host = b.getHost();
            if (host != null) {
                b.setHost(host.toLowerCase(Locale.US));
            }
            final String path = b.getPath();
            if (TextUtils.isEmpty(path)) {
                b.setPath("/");
            }
            return b.build();
        } catch (final URISyntaxException ex) {
            throw new ClientProtocolException("Invalid redirect URI: " + location, ex);
        }
    }

    public SimulationTaskRepresentation[] startSimulation(String bmId, String simKey) throws IOException {
		  
		HttpPost httppost = new HttpPost("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/biomodel/"+bmId+"/simulation/"+simKey+"/startSimulation");
		
		if (lg.isInfoEnabled()) {
			lg.info("Executing request to retrieve simulation " + httppost.getRequestLine());
		}

		String responseBody = httpclient.execute(httppost, new VCellStringResponseHandler("startSimulation()", httppost), httpClientContext);
		String simTaskReps = responseBody;
		if (lg.isInfoEnabled()) {
			lg.info("returned: "+toStringTruncated(simTaskReps));
		}

		if (responseBody.equals("simulation start failed")){
			throw new RuntimeException("simulation start failed for SimID="+simKey+" within BioModelKey="+bmId);
		}else{
			Gson gson = new Gson();
			SimulationTaskRepresentation[] simulationTaskRepresentations = gson.fromJson(simTaskReps,SimulationTaskRepresentation[].class);
			return simulationTaskRepresentations;
		}
	}
	
	public SimulationTaskRepresentation[] stopSimulation(String bmId, String simKey) throws IOException {
		  
		HttpPost httppost = new HttpPost("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/biomodel/"+bmId+"/simulation/"+simKey+"/stopSimulation");
		
		if (lg.isInfoEnabled()) {
			lg.info("Executing request to retrieve simulation " + httppost.getRequestLine());
		}

		String responseBody = httpclient.execute(httppost, new VCellStringResponseHandler("stopSimulation()", httppost), httpClientContext);
		String simTaskReps = responseBody;
		if (lg.isInfoEnabled()) {
			lg.info("returned: "+toStringTruncated(simTaskReps));
		}

		Gson gson = new Gson();
		SimulationTaskRepresentation[] simulationTaskRepresentations = gson.fromJson(simTaskReps,SimulationTaskRepresentation[].class);
		return simulationTaskRepresentations;
	}
	
	public AccessTokenRepresentation authenticate(String userid, String password, boolean alreadyDigested) throws ClientProtocolException, IOException {
		// hash the password
		String digestedPassword = (alreadyDigested)?(password):createdDigestPassword(password);
		
		HttpGet httpget = new HttpGet("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/access_token?user_id="+userid+"&user_password="+digestedPassword+"&client_id="+clientID);

		if (lg.isInfoEnabled()) {
			lg.info("Executing request to retrieve access_token " + httpget.getRequestLine());
		}

		String responseBody = httpclient.execute(httpget, new VCellStringResponseHandler("authenticate()", httpget));
		String accessTokenJson = responseBody;
		if (lg.isInfoEnabled()) {
			lg.info("returned: "+accessTokenJson);
		}

		Gson gson = new Gson();
		AccessTokenRepresentation accessTokenRep = gson.fromJson(accessTokenJson,AccessTokenRepresentation.class);
		
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(
		        new AuthScope(httpHost.getHostName(),httpHost.getPort()),
		        new UsernamePasswordCredentials("access_token", accessTokenRep.getToken()));

		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(httpHost, basicAuth);

		// Add AuthCache to the execution context
		httpClientContext = HttpClientContext.create();
		httpClientContext.setCredentialsProvider(credsProvider);
		httpClientContext.setAuthCache(authCache);
		
		return accessTokenRep;
	}
	
	public void clearAuthentication() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		initClient();
		httpClientContext = HttpClientContext.create();
	}


	private String createdDigestPassword(String clearTextPassword) {
		if (clearTextPassword == null || clearTextPassword.length() == 0) {
			throw new RuntimeException("Empty password not allowed");
		}
		java.security.MessageDigest messageDigest = null;
		try {
			messageDigest = java.security.MessageDigest.getInstance("SHA-1");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error processing password, Couldn't get instance of MessageDigest " + e.getMessage());
		}
		messageDigest.reset();
		messageDigest.update(clearTextPassword.getBytes());
		byte[] digestedPasswordBytes = messageDigest.digest();
		StringBuffer sb = new StringBuffer(digestedPasswordBytes.length * 2);
		for (int i = 0; i < digestedPasswordBytes.length; i++) {
			int v = digestedPasswordBytes[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString().toUpperCase();
	}

	public UserInfo insertUserInfo(UserInfo newUserInfo) throws ClientProtocolException, IOException,IllegalArgumentException {
		  
		HttpPost httppost = new HttpPost("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/newuser");
		Gson gson = new Gson();
		String newUserInfoJSON = gson.toJson(newUserInfo);
		StringEntity input = new StringEntity(newUserInfoJSON);
		input.setContentType(ContentType.APPLICATION_JSON.getMimeType());
		httppost.setEntity(input);
		
		if (lg.isInfoEnabled()) {
			lg.info("Executing request to submit new user " + httppost.getRequestLine());
		}

		ResponseHandler<UserInfo> handler = new ResponseHandler<UserInfo>() {

			public UserInfo handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status == HttpStatus.SC_CREATED) {
					HttpEntity entity = response.getEntity();
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));){
						String json = reader.lines().collect(Collectors.joining());
						UserInfo userInfo = gson.fromJson(json, UserInfo.class);
						return userInfo;
					}
				} else if(status == HttpStatus.SC_FORBIDDEN){
					HttpEntity entity = response.getEntity();
					String message = null;
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));){
						message = reader.lines().collect(Collectors.joining());
					}
					final URI uri = httppost.getURI();
					lg.error("insertUserInfo() ("+uri+") failed: response status: " + status + "\nreason: " + message);
					throw new IllegalArgumentException("insertUserInfo() failed: response status: " + status + "\nreason: " + message);
				} else {
					HttpEntity entity = response.getEntity();
					String message = null;
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));){
						message = reader.lines().collect(Collectors.joining());
					}
					final URI uri = httppost.getURI();
					lg.error("insertUserInfo() ("+uri+") failed: response status: " + status + "\nreason: " + message);
					throw new ClientProtocolException("insertUserInfo() failed: response status: " + status + "\nreason: " + message);
				}
			}

		};
		UserInfo insertedUserInfo = httpclient.execute(httppost,handler,httpClientContext);
		if (lg.isInfoEnabled()) {
			lg.info("returned userinfo: "+insertedUserInfo);
		}

		return insertedUserInfo;
	}

	public void sendLostPassword(String userid) throws ClientProtocolException, IOException {
		HttpPost httppost = new HttpPost("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/lostpassword");
		StringEntity input = new StringEntity(userid);
		input.setContentType(ContentType.TEXT_PLAIN.getMimeType());
		httppost.setEntity(input);
		
		if (lg.isInfoEnabled()) {
			lg.info("Executing request to send lost password " + httppost.getRequestLine());
		}

		ResponseHandler<String> handler = new ResponseHandler<String>() {

			public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status == HttpStatus.SC_ACCEPTED) {
					HttpEntity entity = response.getEntity();
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));){
						String message = reader.lines().collect(Collectors.joining());
						return message;
					}
				} else {
					HttpEntity entity = response.getEntity();
					String message = null;
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));){
						message = reader.lines().collect(Collectors.joining());
					}
					final URI uri = httppost.getURI();
					lg.error("sendLostPassword() ("+uri+") failed: response status: " + status + "\nreason: " + message);
					throw new ClientProtocolException("Failed to request lost password, response status: " + status + "\nreason: " + message);
				}
			}

		};
		String message = httpclient.execute(httppost,handler,httpClientContext);
		if (lg.isInfoEnabled()) {
			lg.info("requested lost password for user "+userid+", server returned "+message);
		}
	}
	
	public enum RpcDestination {
		DataRequestQueue, DbRequestQueue, SimReqQueue;
	}
	
	public static class VCellApiRpcBody implements Serializable {
		public final RpcDestination rpcDestination;
		public final VCellApiRpcRequest rpcRequest;
		public final boolean returnedRequired;
		public final int timeoutMS;
		public final String[] specialProperties;
		public final Object[] specialValues;

		public VCellApiRpcBody(RpcDestination rpcDestination, VCellApiRpcRequest rpcRequest,
				boolean returnedRequired, int timeoutMS, String[] specialProperties, Object[] specialValues) {
			this.rpcDestination = rpcDestination;
			this.rpcRequest = rpcRequest;
			this.returnedRequired = returnedRequired;
			this.timeoutMS = timeoutMS;
			this.specialProperties = specialProperties;
			this.specialValues = specialValues;
		}
	}

	public Serializable sendRpcMessage(RpcDestination rpcDestination, VCellApiRpcRequest rpcRequest, boolean returnRequired, int timeoutMS, String[] specialProperties, Object[] specialValues) throws ClientProtocolException, IOException {
		HttpPost httppost = new HttpPost("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/rpc");
		VCellApiRpcBody vcellapiRpcBody = new VCellApiRpcBody(rpcDestination, rpcRequest, returnRequired, timeoutMS, specialProperties, specialValues);
		byte[] compressedSerializedRpcBody = null;
		try {
			compressedSerializedRpcBody = toCompressedSerialized(vcellapiRpcBody);
		} catch (IOException e2) {
			e2.printStackTrace();
			throw new RuntimeException("vcellapi rpc failure serializing request body, method="+rpcRequest.methodName+": "+e2.getMessage(),e2);
		}
		ByteArrayEntity input = new ByteArrayEntity(compressedSerializedRpcBody);
		input.setContentType(ContentType.APPLICATION_OCTET_STREAM.getMimeType());
		httppost.setEntity(input);
		httppost.addHeader("username", rpcRequest.username);
		httppost.addHeader("destination", rpcRequest.rpcDestination.name());
		httppost.addHeader("method", rpcRequest.methodName);
		httppost.addHeader("returnRequired", Boolean.toString(returnRequired));
		httppost.addHeader("timeoutMS", Integer.toString(timeoutMS));
		httppost.addHeader("compressed", "zip");
		httppost.addHeader("class",VCellApiRpcBody.class.getCanonicalName());
		if (specialProperties!=null) {
			httppost.addHeader("specialProperties", Arrays.asList(specialProperties).toString());
		}
	
		if (lg.isInfoEnabled()) {
			lg.info("Executing request to submit rpc call " + httppost.getRequestLine());
		}

		ResponseHandler<Serializable> handler = new ResponseHandler<Serializable>() {

			public Serializable handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				if (lg.isInfoEnabled()) {
					lg.info("in rpc response handler, status="+status);
				}
				if (status == 200) {
					HttpEntity entity = response.getEntity();
					try {
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						entity.writeTo(byteArrayOutputStream);
						byte[] returnValueBytes = byteArrayOutputStream.toByteArray();
						Serializable returnValue = fromCompressedSerialized(returnValueBytes);
						if (returnRequired) {
							if (returnValue instanceof Exception){
								Exception e = (Exception)returnValue;
								e.printStackTrace();
								lg.error("vcellapi rpc failure, method="+rpcRequest.methodName+": "+e.getMessage(),e);
								throw new ClientProtocolException("vcellapi rpc failure, method="+rpcRequest.methodName+": "+e.getMessage(),e);
							} else {
								if (lg.isInfoEnabled()) {
									lg.info("returning normally from rpc response handler ("+toStringTruncated(returnValue)+")");
								}
								return returnValue;
							}
						} else {
							if (lg.isInfoEnabled()) {
								lg.info("returning null from rpc response handler (returnRequired==false)");
							}
							return null;
						}
					} catch (ClassNotFoundException | IllegalStateException e1) {
						e1.printStackTrace();
						lg.error("vcellapi rpc failure deserializing return value, method="+rpcRequest.methodName+": "+e1.getMessage(),e1);
						throw new RuntimeException("vcellapi rpc failure deserializing return value, method="+rpcRequest.methodName+": "+e1.getMessage(),e1);
					}
					
				} else {
					HttpEntity entity = response.getEntity();
					String message = null;
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));){
						message = reader.lines().collect(Collectors.joining());
					}
					lg.error("RPC method "+rpcDestination+":"+rpcRequest.methodName+"() failed: response status: " + status + "\nreason: " + message);
					throw new ClientProtocolException("RPC method "+rpcDestination+":"+rpcRequest.methodName+"() failed: response status: " + status + "\nreason: " + message);
				}
			}

		};
		Serializable returnedValue = httpclient.execute(httppost,handler,httpClientContext);
		if (lg.isInfoEnabled()) {
			lg.info("returned from vcellapi rpc method="+rpcDestination+":"+rpcRequest.methodName+"()");
		}
		return returnedValue;
	}
	
	private static String toStringTruncated(Object obj) {
		return toStringTruncated(obj, 30);
	}

	private static String toStringTruncated(Object obj, int maxlength) {
		if (obj == null) {
			return "null";
		}
		String str = obj.toString();
		if (str.length() <= maxlength) {
			return str;
		}else {
			return str.substring(0, maxlength-4)+"...";
		}
	}
	
	public static byte[] toCompressedSerialized(Serializable cacheObj) throws java.io.IOException {
		java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
		DeflaterOutputStream dos = new DeflaterOutputStream(bos);
		java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(dos);
		oos.writeObject(cacheObj);
		oos.flush();
		dos.close();
		bos.flush();
		byte[] objData = bos.toByteArray();
		oos.close();
		bos.close();
		return objData;
	}
	
	public static Serializable fromCompressedSerialized(InputStream is) throws ClassNotFoundException, java.io.IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		InflaterInputStream iis = new InflaterInputStream(bis);
		java.io.ObjectInputStream ois = new java.io.ObjectInputStream(iis);
		Serializable cacheClone = (Serializable) ois.readObject();
		ois.close();
		bis.close();
		return cacheClone;
	}
	
	public static Serializable fromCompressedSerialized(byte[] objData) throws ClassNotFoundException, java.io.IOException {
		java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(objData);
		InflaterInputStream iis = new InflaterInputStream(bis);
		java.io.ObjectInputStream ois = new java.io.ObjectInputStream(iis);
		Serializable cacheClone = (Serializable) ois.readObject();
		ois.close();
		bis.close();
		return cacheClone;
	}

	public String getServerSoftwareVersion() throws ClientProtocolException, IOException {
		  
		HttpGet httpget = new HttpGet("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/swversion");

		if (lg.isInfoEnabled()) {
			lg.info("Executing request to retrieve server software version " + httpget.getRequestLine());
		}

		String vcellSoftwareVersion = httpclient.execute(httpget, new VCellStringResponseHandler("getServerSoftwareVersion()", httpget), httpClientContext);
		return vcellSoftwareVersion;
	}
	
}

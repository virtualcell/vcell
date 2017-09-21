package org.vcell.api.client;

import java.io.BufferedReader;

/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.vcell.api.client.query.BioModelsQuerySpec;
import org.vcell.api.client.query.SimTasksQuerySpec;
import org.vcell.api.common.AccessTokenRepresentation;
import org.vcell.api.common.BiomodelRepresentation;
import org.vcell.api.common.SimulationRepresentation;
import org.vcell.api.common.SimulationTaskRepresentation;

import com.google.gson.Gson;

/**
 * This example demonstrates the use of the {@link ResponseHandler} to simplify
 * the process of processing the HTTP response and releasing associated
 * resources.
 */
public class VCellApiClient {
	
	private HttpHost httpHost;
	private String clientID;
	private CloseableHttpClient httpclient;
	private HttpClientContext httpClientContext;
	boolean bIgnoreCertProblems = true;
	boolean bIgnoreHostMismatch = true;

	// Create a custom response handler
	private ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

		public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity) : null;
			} else {
				throw new ClientProtocolException("Unexpected response status: " + status);
			}
		}

	};

	public VCellApiClient(String host, int port, String clientID, boolean bIgnoreCertProblems, boolean bIgnoreHostMismatch) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		this.httpHost = new HttpHost(host,port,"https");
		this.clientID = clientID;
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

		System.out.println("Executing request to retrieve simulation tasks " + httpget.getRequestLine());

		String responseBody = httpclient.execute(httpget, responseHandler, httpClientContext);
		String simTasksJson = responseBody;
		System.out.println("returned: "+simTasksJson);

		Gson gson = new Gson();
		SimulationTaskRepresentation[] simTaskReps = gson.fromJson(simTasksJson,SimulationTaskRepresentation[].class);
		return simTaskReps;
	}
	
	public BiomodelRepresentation[] getBioModels(BioModelsQuerySpec bioModelsQuerySpec) throws IOException {
		  
		HttpGet httpget = new HttpGet("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/biomodel?"+bioModelsQuerySpec.getQueryString());

		System.out.println("Executing request to retrieve biomodels " + httpget.getRequestLine());

		String responseBody = httpclient.execute(httpget, responseHandler, httpClientContext);
		String bimodelsJson = responseBody;
		System.out.println("returned: "+bimodelsJson);

		Gson gson = new Gson();
		BiomodelRepresentation[] biomodelReps = gson.fromJson(bimodelsJson,BiomodelRepresentation[].class);
		return biomodelReps;
	}
	
	public BiomodelRepresentation getBioModel(String bmId) throws IOException {
		  
		HttpGet httpget = new HttpGet("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/biomodel/"+bmId);
		
		System.out.println("Executing request to retrieve biomodel " + httpget.getRequestLine());

		String responseBody = httpclient.execute(httpget, responseHandler, httpClientContext);
		String bimodelsJson = responseBody;
		System.out.println("returned: "+bimodelsJson);

		Gson gson = new Gson();
		BiomodelRepresentation biomodelRep = gson.fromJson(bimodelsJson,BiomodelRepresentation.class);
		return biomodelRep;
	}
	
	public String getBioModelVCML(String bmId) throws IOException {
		  
		HttpGet httpget = new HttpGet("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/biomodel/"+bmId+"/biomodel.vcml");
		
		System.out.println("Executing request to retrieve biomodel " + httpget.getRequestLine());

		String responseBody = httpclient.execute(httpget, responseHandler, httpClientContext);
		String vcml = responseBody;

		return vcml;
	}
	
	public SimulationRepresentation getSimulation(String bmId, String simKey) throws IOException {
		  
		HttpGet httpget = new HttpGet("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/biomodel/"+bmId+"/simulation/"+simKey);
		
		System.out.println("Executing request to retrieve simulation " + httpget.getRequestLine());

		String responseBody = httpclient.execute(httpget, responseHandler, httpClientContext);
		String bimodelsJson = responseBody;
		System.out.println("returned: "+bimodelsJson);

		Gson gson = new Gson();
		SimulationRepresentation simulationRepresentation = gson.fromJson(bimodelsJson,SimulationRepresentation.class);
		return simulationRepresentation;
	}
	
	public String getOptRunJson(String optimizationId) throws IOException {
		HttpGet httpget = new HttpGet("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/optimization/"+optimizationId);
		
		System.out.println("Executing request to retrieve optimization run " + httpget.getRequestLine());

		String responseBody = httpclient.execute(httpget, responseHandler, httpClientContext);
		System.out.println("returned: "+responseBody);
		return responseBody;
	}

	public String submitOptimization(String optProblemJson) throws IOException, URISyntaxException {
		  
		HttpPost httppost = new HttpPost("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/optimization");
		StringEntity input = new StringEntity(optProblemJson);
		input.setContentType("application/json");
		httppost.setEntity(input);
		
		System.out.println("Executing request to submit optProblem " + httppost.getRequestLine());

		ResponseHandler<String> handler = new ResponseHandler<String>() {

			public String handleResponse(final HttpResponse response) throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status == 202) {
					HttpEntity entity = response.getEntity();
					try (BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));){
						System.out.println("optimizationId = "+reader.readLine());
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
					throw new ClientProtocolException("Unexpected response status: " + status);
				}
			}

		};
		String responseUri = httpclient.execute(httppost,handler,httpClientContext);
		System.out.println("returned: "+responseUri);

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
		
		System.out.println("Executing request to retrieve simulation " + httppost.getRequestLine());

		String responseBody = httpclient.execute(httppost, responseHandler, httpClientContext);
		String simTaskReps = responseBody;
		System.out.println("returned: "+simTaskReps);

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
		
		System.out.println("Executing request to retrieve simulation " + httppost.getRequestLine());

		String responseBody = httpclient.execute(httppost, responseHandler, httpClientContext);
		String simTaskReps = responseBody;
		System.out.println("returned: "+simTaskReps);

		Gson gson = new Gson();
		SimulationTaskRepresentation[] simulationTaskRepresentations = gson.fromJson(simTaskReps,SimulationTaskRepresentation[].class);
		return simulationTaskRepresentations;
	}
	
	public void authenticate(String userid, String password) throws ClientProtocolException, IOException {
		// hash the password
		String digestedPassword = createdDigestPassword(password);
		
		HttpGet httpget = new HttpGet("https://"+httpHost.getHostName()+":"+httpHost.getPort()+"/access_token?user_id="+userid+"&user_password="+digestedPassword+"&client_id="+clientID);

		System.out.println("Executing request to retrieve access_token " + httpget.getRequestLine());

		String responseBody = httpclient.execute(httpget, responseHandler);
		String accessTokenJson = responseBody;
		System.out.println("returned: "+accessTokenJson);

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

}

package org.vcell.api.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This example demonstrates the use of the {@link ResponseHandler} to simplify
 * the process of processing the HTTP response and releasing associated
 * resources.
 */
public class VCellOptClient {
	
	private static final Logger lg = LogManager.getLogger(VCellOptClient.class);
	private HttpHost httpHost;
	private CloseableHttpClient httpclient;
	private HttpClientContext httpClientContext;

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

	public VCellOptClient(String host, int port) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException{
		this.httpHost = new HttpHost(host,port,"http");
		initClient();
	}
	
	private void initClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

		HttpClientBuilder httpClientBuilder = HttpClients.custom();
		httpclient = httpClientBuilder.setRedirectStrategy(new DefaultRedirectStrategy()).build();
		httpClientContext = HttpClientContext.create();
	}
	
	public void close() throws IOException {
		if (httpclient!=null){
			httpclient.close();
		}
	}
	
	public String getOptRunJson(String optimizationId) throws IOException {
		HttpGet httpget = new HttpGet("http://"+httpHost.getHostName()+":"+httpHost.getPort()+"/optimization/"+optimizationId);
		
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
		  
		HttpPost httppost = new HttpPost("http://"+httpHost.getHostName()+":"+httpHost.getPort()+"/optimization");
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
	
}

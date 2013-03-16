package org.vcell.rest.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MapVerifier;
import org.restlet.util.Series;

import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.messaging.db.SimulationJobStatus;

public class SimApplication extends Application {
	public static final String ROOT_URI = "file:///c:/temp/";  

	SimulationDatabase simulationDatabase = null;
	
	public SimApplication(SimulationDatabase simulationDatabase) {
		this.simulationDatabase = simulationDatabase;
	}

	@Override  
    public Restlet createInboundRoot() {  
    	
    	// Create a root router  
    	Router router = new Router(getContext());  
    	  
    	// Attach a guard to secure access to the directory  
    	ChallengeAuthenticator guard = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "Tutorial");  
    	MapVerifier verifier = new MapVerifier();  
    	verifier.getLocalSecrets().put("scott", "tiger".toCharArray());  
    	guard.setVerifier(verifier);

    	router.attach("/docs/", guard);  
    	  
    	// Create a directory able to expose a hierarchy of files  
    	Directory directory = new Directory(getContext(), ROOT_URI);  
    	guard.setNext(directory);  
    	  
    	// Create the account handler  
    	Restlet account = new Restlet() {  
    	    @Override  
    	    public void handle(Request request, Response response) {  
    	        // Print the requested URI path  
    	        String message = "Account of user \""  
    	                + request.getAttributes().get("user") + "\"";  
    	        response.setEntity(message, MediaType.TEXT_PLAIN);  
    	    }  
    	};  
    	  
    	// Create the orders handler  
    	Restlet orders = new Restlet(getContext()) {  
    	    @Override  
    	    public void handle(Request request, Response response) {  
    	        // Print the user name of the requested orders  
    	        String message = "Orders of user \""  
    	                + request.getAttributes().get("user") + "\"";  
    	        response.setEntity(message, MediaType.TEXT_PLAIN);  
    	    }  
    	};  
    	  
    	// Create the order handler  
    	Restlet order = new Restlet(getContext()) {  
    	    @Override  
    	    public void handle(Request request, Response response) {  
    	        // Print the user name of the requested orders  
    	        String message = "Order \""  
    	                + request.getAttributes().get("order")  
    	                + "\" for user \""  
    	                + request.getAttributes().get("user") + "\"";  
    	        response.setEntity(message, MediaType.TEXT_PLAIN);  
    	    }  
    	};  
    	
    	Restlet allsims = new Restlet(getContext()){
    	    @Override  
    	    public void handle(Request request, Response response) {  
    	        // Print the user name of the requested orders  
    	    	try {
    	    		SimulationJobStatus[] simJobStatusList = simulationDatabase.getActiveJobs();
    	    		final String FIELD_ID 			= "ID";
    	    		final String FIELD_USER 		= "User";
    	    		final String FIELD_JOBID 		= "Job ID";
    	    		final String FIELD_STATUS 		= "Status";
    	    		final String FIELD_STARTDATE 	= "Start Date";
    	    		final String FIELD_JOBINDEX 	= "Job Index";
    	    		final String FIELD_TASKID 		= "Task ID";
    	    		final String FIELD_MESSAGE 		= "Message";
//    	    		Series headers = (Series)request.getAttributes().get("org.restlet.http.headers");
//    	    		String[] acceptsArray = headers.getValuesArray("Accept");
//    	    		List<Preference<MediaType>> mediaTypes = request.getClientInfo().getAcceptedMediaTypes();

    	    		ArrayList<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
    	    		supportedMediaTypes.add(MediaType.APPLICATION_JSON);
    	    		supportedMediaTypes.add(MediaType.TEXT_HTML);
    	    		
    	    		MediaType preferredMediaType = request.getClientInfo().getPreferredMediaType(supportedMediaTypes);
    	    		
    	    		if (MediaType.APPLICATION_JSON.equals(preferredMediaType)){
    	    			JSONArray array = new JSONArray();
	    	    		for (SimulationJobStatus simJobStatus : simJobStatusList) {
	    	    			JSONObject obj = new JSONObject();
	    	    			obj.put(FIELD_ID, 			simJobStatus.getVCSimulationIdentifier().getID());
	    	    			obj.put(FIELD_USER, 		simJobStatus.getVCSimulationIdentifier().getOwner().getName());
	    	    			obj.put(FIELD_JOBID, 		simJobStatus.getSimulationExecutionStatus().getHtcJobID());
	    	    			obj.put(FIELD_STATUS, 		simJobStatus.getSchedulerStatus().getDescription());
	    	    			obj.put(FIELD_STARTDATE, 	simJobStatus.getStartDate());
	    	    			obj.put(FIELD_JOBINDEX, 	simJobStatus.getJobIndex());
	    	    			obj.put(FIELD_TASKID, 		simJobStatus.getTaskID());
	    	    			obj.put(FIELD_MESSAGE, 		simJobStatus.getSimulationMessage());
	    	    			array.put(obj);
	    	    		}
	    	    		JSONObject results = new JSONObject();
	    	    		results.put("results",array);
	    	    		response.setEntity(results.toString(), MediaType.APPLICATION_JSON);  
    	    		}else if (MediaType.TEXT_HTML.equals(preferredMediaType)){// if (accepts.contains("text/html")){
    	    			StringBuffer buffer = new StringBuffer();
    	    			buffer.append("<!DOCTYPE html>\n");
    	    			buffer.append("<html>\n");
    	    			buffer.append("<head>\n");
    	    			buffer.append("<title>Running VCell Simulations</title>\n");
    	    			buffer.append("</head>\n");
    	    			buffer.append("<body>\n");
    	    			
    	    			buffer.append("<h1>Running VCell Simulations</h1>");
    	    			
    	    			buffer.append("<table border='1'>\n");
    	    			buffer.append("<tr>\n");
    	    			buffer.append("<th>"+FIELD_ID+"</th>\n");
    	    			buffer.append("<th>"+FIELD_USER+"</th>\n");
    	    			buffer.append("<th>"+FIELD_JOBID+"</th>\n");
    	    			buffer.append("<th>"+FIELD_STATUS+"</th>\n");
    	    			buffer.append("<th>"+FIELD_STARTDATE+"</th>\n");
    	    			buffer.append("<th>"+FIELD_JOBINDEX+"</th>\n");
    	    			buffer.append("<th>"+FIELD_TASKID+"</th>\n");
    	    			buffer.append("<th>"+FIELD_MESSAGE+"</th>\n");
    	    			buffer.append("</tr>\n");
	    	    		for (SimulationJobStatus simJobStatus : simJobStatusList) {
	       	    			buffer.append("<tr>\n");
	       	    			buffer.append("<td>"+simJobStatus.getVCSimulationIdentifier().getID()+"</td>\n");
	       	    			buffer.append("<td>"+simJobStatus.getVCSimulationIdentifier().getOwner().getName()+"</td>\n");
	       	    			buffer.append("<td>"+simJobStatus.getSimulationExecutionStatus().getHtcJobID()+"</td>\n");
	       	    			buffer.append("<td>"+simJobStatus.getSchedulerStatus().getDescription()+"</td>\n");
	       	    			buffer.append("<td>"+simJobStatus.getStartDate()+"</td>\n");
	       	    			buffer.append("<td>"+simJobStatus.getJobIndex()+"</td>\n");
	       	    			buffer.append("<td>"+simJobStatus.getTaskID()+"</td>\n");
	       	    			buffer.append("<td>"+simJobStatus.getSimulationMessage()+"</td>\n");
	       	    			buffer.append("</tr>\n");
	       	    		}
    	    			buffer.append("</table>\n");

    	    			buffer.append("</body>\n");
    	    			buffer.append("</html>\n");
	    	    		response.setEntity(buffer.toString(), MediaType.TEXT_HTML);  
    	    		}else{
    	    			StringBuffer buffer = new StringBuffer();
    	    			buffer.append("unsupported media types: ");
    	    			for (Preference<MediaType> preference : request.getClientInfo().getAcceptedMediaTypes()){
    	    				buffer.append(preference.getMetadata()+" ");
    	    			}
	    	    		response.setEntity(buffer.toString(), MediaType.TEXT_PLAIN);  
    	    		}
    	    			
				} catch (Exception e) {
					e.printStackTrace();
					response.setEntity("failed retrieving data : "+e.getMessage(), MediaType.TEXT_PLAIN);  
				}
    	    }  
    	};
    	  
    	// Attach the handlers to the root router  
    	router.attach("/users/{user}", account);  
    	router.attach("/users/{user}/orders", orders);  
    	router.attach("/users/{user}/orders/{order}", order);  
    	router.attach("/sims", allsims);
    	 
    	return router;  
    }  
	
}

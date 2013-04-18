package org.vcell.rest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.routing.Router;
import org.vcell.rest.common.SimsSearchResponse;
import org.vcell.rest.common.SimulationTaskRepresentation;
import org.vcell.rest.server.SimulationTaskServerResource;
import org.vcell.rest.server.SimulationTasksServerResource;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;
import org.vcell.util.document.VCellServerID;

import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.messaging.db.SimulationExecutionStatus;
import cbit.vcell.messaging.db.SimulationJobStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SchedulerStatus;
import cbit.vcell.messaging.db.SimulationJobStatus.SimulationQueueID;
import cbit.vcell.messaging.db.SimulationQueueEntryStatus;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.solver.SimulationMessage;
import cbit.vcell.solver.VCSimulationIdentifier;

import com.google.gson.Gson;

public class VCellApiApplication extends WadlApplication {
	public static final String ROOT_URI = "file:///c:/temp/";  

	public SimulationDatabase simulationDatabase = null;
	public AdminDBTopLevel adminDBTopLevel = null;
	
	public VCellApiApplication(SimulationDatabase simulationDatabase, AdminDBTopLevel adminDbTopLevel) {
        setName("RESTful VCell API application");
        setDescription("Simulation management API");
        setOwner("VCell Project/UCHC");
        setAuthor("VCell Team");
		this.simulationDatabase = simulationDatabase;
		this.adminDBTopLevel = adminDbTopLevel;
	}

	@Override  
    public Restlet createInboundRoot() {  
    	
	    System.setProperty("java.net.preferIPv4Stack", "true");
	    
	    
   	// Create a root router  
    	Router router = new Router(getContext());  
    	  
//    	// Attach a guard to secure access to the directory  
//    	ChallengeAuthenticator guard = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "Tutorial");  
//    	MapVerifier verifier = new MapVerifier();  
//    	verifier.getLocalSecrets().put("scott", "tiger".toCharArray());  
//    	guard.setVerifier(verifier);
//
//    	router.attach("/docs/", guard);  
//    	  
//    	// Create a directory able to expose a hierarchy of files  
//    	Directory directory = new Directory(getContext(), ROOT_URI);  
//    	guard.setNext(directory);  
    	  
     	
    	Restlet allsims = new Restlet(getContext()){
    		private SimulationJobStatus[] getSimulationJobStatus() throws DataAccessException, SQLException{
    			if (simulationDatabase!=null){
    				return simulationDatabase.getActiveJobs();
    			}else{
    				return new SimulationJobStatus[] { 
    						new SimulationJobStatus(VCellServerID.getServerID("JIMS"),
									new VCSimulationIdentifier(new KeyValue("123"),new User("schaff",new KeyValue("222"))), 
									0, // jobIndex
									new Date(), // submission date
									SchedulerStatus.RUNNING,
									0, // taskID
									SimulationMessage.MESSAGE_JOB_RUNNING_UNKNOWN,
									new SimulationQueueEntryStatus(new Date(), 1, SimulationQueueID.QUEUE_ID_SIMULATIONJOB),
									new SimulationExecutionStatus(new Date(), "myhost", new Date(), null, false, null)),
    						new SimulationJobStatus(VCellServerID.getServerID("JIMS"),
									new VCSimulationIdentifier(new KeyValue("124"),new User("schaff",new KeyValue("222"))), 
									0, // jobIndex
									new Date(), // submission date
									SchedulerStatus.QUEUED,
									0, // taskID
									SimulationMessage.MESSAGE_JOB_RUNNING_UNKNOWN,
									new SimulationQueueEntryStatus(new Date(), 1, SimulationQueueID.QUEUE_ID_SIMULATIONJOB),
									new SimulationExecutionStatus(new Date(), "myhost", new Date(), null, false, null)) 
    				};
    			}
    		}
    		
    	    @Override  
    	    public void handle(Request request, Response response) {  
    	        // Print the user name of the requested orders  
    	    	try {
    	    		SimulationJobStatus[] simJobStatusList = getSimulationJobStatus();
    	    		Gson gson = new Gson();
    	    		SimsSearchResponse simsResponse = new SimsSearchResponse();
    	    		simsResponse.query = ""+request.getResourceRef();
    	    		simsResponse.results = new ArrayList<SimulationTaskRepresentation>();
    	    		for (SimulationJobStatus simJobStatus : simJobStatusList) {
						SimulationTaskRepresentation holder = new SimulationTaskRepresentation(simJobStatus);
						simsResponse.results.add(holder);
					}
    	    		final String FIELD_SIMKEY		= "SimKey";
    	    		final String FIELD_USERNAME		= "UserName";
    	    		final String FIELD_USERKEY		= "UserKey";
    	    		final String FIELD_JOBINDEX 	= "Job Index";
    	    		final String FIELD_TASKID 		= "Task ID";
    	    		final String FIELD_HTCJOBID		= "HTC JobID";
    	    		final String FIELD_STATUS 		= "Status";
    	    		final String FIELD_STARTDATE 	= "Start Date";
    	    		final String FIELD_MESSAGE 		= "Message";
    	    		final String FIELD_SITE 		= "Site";
    	    		final String FIELD_COMPUTEHOST	= "ComputeHost";
//    	    		Series headers = (Series)request.getAttributes().get("org.restlet.http.headers");
//    	    		String[] acceptsArray = headers.getValuesArray("Accept");
//    	    		List<Preference<MediaType>> mediaTypes = request.getClientInfo().getAcceptedMediaTypes();

    	    		ArrayList<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
    	    		supportedMediaTypes.add(MediaType.APPLICATION_JSON);
    	    		supportedMediaTypes.add(MediaType.TEXT_HTML);
    	    		
    	    		MediaType preferredMediaType = request.getClientInfo().getPreferredMediaType(supportedMediaTypes);
    	    		
    	    		if (MediaType.APPLICATION_JSON.equals(preferredMediaType)){
    	    			String jsonString = gson.toJson(simsResponse);
 	    	    		response.setEntity(jsonString, MediaType.APPLICATION_JSON);  
    	    		}else if (MediaType.TEXT_HTML.equals(preferredMediaType)){// if (accepts.contains("text/html")){
    	    			StringBuffer buffer = new StringBuffer();
    	    			buffer.append("<!DOCTYPE html>\n");
    	    			buffer.append("<html>\n");
    	    			buffer.append("<head>\n");
    	    			buffer.append("<title>Running VCell Simulations</title>\n");
    	    			buffer.append("</head>\n");
    	    			buffer.append("<body>\n");
    	    			
    	    			buffer.append("<h1>Running VCell Simulations</h1>");
    	    			
    	    			buffer.append("<br/>");
    	    			buffer.append(gson.toJson(simsResponse));
       	    			buffer.append("<br/>");
       	    			buffer.append("<br/>");
       	    			
    	    			buffer.append("<table border='1'>\n");
    	    			buffer.append("<tr>\n");
    	    			buffer.append("<th>"+FIELD_SIMKEY+"</th>\n");
    	    			buffer.append("<th>"+FIELD_USERNAME+"</th>\n");
    	    			buffer.append("<th>"+FIELD_USERKEY+"</th>\n");
    	    			buffer.append("<th>"+FIELD_JOBINDEX+"</th>\n");
    	    			buffer.append("<th>"+FIELD_TASKID+"</th>\n");
    	    			buffer.append("<th>"+FIELD_HTCJOBID+"</th>\n");
    	    			buffer.append("<th>"+FIELD_STATUS+"</th>\n");
    	    			buffer.append("<th>"+FIELD_STARTDATE+"</th>\n");
    	    			buffer.append("<th>"+FIELD_MESSAGE+"</th>\n");
    	    			buffer.append("<th>"+FIELD_SITE+"</th>\n");
    	    			buffer.append("<th>"+FIELD_COMPUTEHOST+"</th>\n");
    	    			buffer.append("</tr>\n");
	    	    		for (SimulationTaskRepresentation simulationTaskRepresentation : simsResponse.results) {
	       	    			buffer.append("<tr>\n");
	       	    			buffer.append("<td>"+simulationTaskRepresentation.simKey+"</td>\n");
	       	    			buffer.append("<td>"+simulationTaskRepresentation.userName+"</td>\n");
	       	    			buffer.append("<td>"+simulationTaskRepresentation.userKey+"</td>\n");
	       	    			buffer.append("<td>"+simulationTaskRepresentation.jobIndex+"</td>\n");
	       	    			buffer.append("<td>"+simulationTaskRepresentation.taskId+"</td>\n");
	       	    			buffer.append("<td>"+simulationTaskRepresentation.htcJobId+"</td>\n");
	       	    			buffer.append("<td>"+simulationTaskRepresentation.status+"</td>\n");
	       	    			buffer.append("<td>"+simulationTaskRepresentation.startdate+"</td>\n");
	       	    			buffer.append("<td>"+simulationTaskRepresentation.message+"</td>\n");
	       	    			buffer.append("<td>"+simulationTaskRepresentation.site+"</td>\n");
	       	    			buffer.append("<td>"+simulationTaskRepresentation.computeHost+"</td>\n");
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
    	///router.attach("/users/{user}", account);  
    	router.attach("/users/{user}/simulationTask", SimulationTasksServerResource.class);  
    	router.attach("/users/{user}/simulationTask/{simTaskID}", SimulationTaskServerResource.class);  
    	 
    	return router;  
    }  
	
}

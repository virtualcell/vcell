package org.vcell.rest;

import org.restlet.Request;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.routing.Router;
import org.vcell.rest.server.SimulationTaskServerResource;
import org.vcell.rest.server.SimulationTasksServerResource;

import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.modeldb.AdminDBTopLevel;

public class VCellApiApplication extends WadlApplication {
	public static final String ROOT_URI = "file:///c:/temp/";  

	public SimulationDatabase simulationDatabase = null;
	public AdminDBTopLevel adminDBTopLevel = null;
	
	@Override
	protected Variant getPreferredWadlVariant(Request request) {
		return new Variant(MediaType.APPLICATION_WADL);
	}

	@Override
	protected Representation createHtmlRepresentation(ApplicationInfo applicationInfo) {
		// TODO Auto-generated method stub
		return super.createHtmlRepresentation(applicationInfo);
	}

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
    	  
     	
    	  
    	// Attach the handlers to the root router  
    	///router.attach("/users/{user}", account);  
    	router.attach("/users/{user}/simulationTask", SimulationTasksServerResource.class);  
    	router.attach("/users/{user}/simulationTask/{simTaskID}", SimulationTaskServerResource.class);  
    	 
    	return router;  
    }  
	
}

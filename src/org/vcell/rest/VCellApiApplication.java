package org.vcell.rest;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.security.ChallengeAuthenticator;
import org.vcell.rest.server.SimulationTaskServerResource;
import org.vcell.rest.server.SimulationTasksServerResource;
import org.vcell.rest.server.filters.Tracer;

import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.modeldb.AdminDBTopLevel;

public class VCellApiApplication extends WadlApplication {
	public static final String ROOT_URI = "file:///c:/temp/";  

	public SimulationDatabase simulationDatabase = null;
	public AdminDBTopLevel adminDBTopLevel = null;
	public UserVerifier userVerifier = null;
	
	@Override
	protected Variant getPreferredWadlVariant(Request request) {
		return new Variant(MediaType.APPLICATION_WADL);
	}

	@Override
	protected Representation createHtmlRepresentation(ApplicationInfo applicationInfo) {
		// TODO Auto-generated method stub
		return super.createHtmlRepresentation(applicationInfo);
	}

	public VCellApiApplication(SimulationDatabase simulationDatabase, AdminDBTopLevel adminDbTopLevel, UserVerifier userVerifier) {
        setName("RESTful VCell API application");
        setDescription("Simulation management API");
        setOwner("VCell Project/UCHC");
        setAuthor("VCell Team");
		this.simulationDatabase = simulationDatabase;
		this.adminDBTopLevel = adminDbTopLevel;
		this.userVerifier = userVerifier;
	}

	@Override  
    public Restlet createInboundRoot() {  
    	
	    System.setProperty("java.net.preferIPv4Stack", "true");
	    
	    
		// Attach a guard to secure access to user parts of the api 
		ChallengeAuthenticator guard = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "VCellUser");  
		guard.setVerifier(userVerifier);
	
		Router userRouter = new Router(getContext());
		userRouter.attach("/simulationTask", SimulationTasksServerResource.class);  
		userRouter.attach("/simulationTask/{simTaskID}", SimulationTaskServerResource.class);  
//		userRouter.attach(new Restlet() {
//
//		    @Override
//		    public void handle(Request request, Response response) {
//		        String entity = "<html><body><h3>unknown page in user portion of web site</h3>" +
//		        		"Method       : " + request.getMethod()
//		                + "\nResource URI : " 
//		                + request.getResourceRef()
//		                + "\nIP address   : " 
//		                + request.getClientInfo().getAddress()
//		                + "\nAgent name   : " 
//		                + request.getClientInfo().getAgentName()
//		                + "\nAgent version: "
//		                + request.getClientInfo().getAgentVersion()
//		                + "</body></html>";
//		        response.setEntity(entity, MediaType.TEXT_HTML);
//		    }
//
//		});
		
		guard.setNext(userRouter);
		
		// Create a root router  
		Router rootRouter = new Router(getContext());  
		rootRouter.setDefaultMatchingMode(Template.MODE_STARTS_WITH);
		rootRouter.attach("/users/{user}", guard); 
//		rootRouter.attach(new Restlet() {
//
//		    @Override
//		    public void handle(Request request, Response response) {
//		        String entity = "<html><body><h3>unknown page in unauthenticated portion of web site</h3>" +
//		        		"Method       : " + request.getMethod()
//		                + "\nResource URI : " 
//		                + request.getResourceRef()
//		                + "\nIP address   : " 
//		                + request.getClientInfo().getAddress()
//		                + "\nAgent name   : " 
//		                + request.getClientInfo().getAgentName()
//		                + "\nAgent version: "
//		                + request.getClientInfo().getAgentVersion()
//		                + "</body></html>";
//		        response.setEntity(entity, MediaType.TEXT_HTML);
//		    }
//
//		});
     	    	 
    	return rootRouter;  
    }  
	
}

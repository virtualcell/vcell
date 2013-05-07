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
import org.restlet.security.ChallengeAuthenticator;
import org.vcell.rest.server.SimulationTaskServerResource;
import org.vcell.rest.server.SimulationTasksServerResource;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.modeldb.AdminDBTopLevel;
import freemarker.template.Configuration;

public class VCellApiApplication extends WadlApplication {
	public static final String ROOT_URI = "file:///c:/temp/";  

	private SimulationDatabase simulationDatabase = null;
	private AdminDBTopLevel adminDBTopLevel = null;
	private UserVerifier userVerifier = null;
//	private ChallengeAuthenticator challengeAuthenticator = null;
	private Configuration templateConfiguration = null;
	
	@Override
	protected Variant getPreferredWadlVariant(Request request) {
		return new Variant(MediaType.APPLICATION_WADL);
	}

	@Override
	protected Representation createHtmlRepresentation(ApplicationInfo applicationInfo) {
		// TODO Auto-generated method stub
		return super.createHtmlRepresentation(applicationInfo);
	}

	public VCellApiApplication(SimulationDatabase simulationDatabase, AdminDBTopLevel adminDbTopLevel, UserVerifier userVerifier, Configuration templateConfiguration) {
        setName("RESTful VCell API application");
        setDescription("Simulation management API");
        setOwner("VCell Project/UCHC");
        setAuthor("VCell Team");
		this.simulationDatabase = simulationDatabase;
		this.adminDBTopLevel = adminDbTopLevel;
		this.userVerifier = userVerifier;
		this.templateConfiguration = templateConfiguration;
	}
	
	private ChallengeAuthenticator createChallengeAuthenticator(UserVerifier userVerifier){
	    Context context = getContext();
	    boolean optional = true;
	    ChallengeScheme challengeScheme = ChallengeScheme.HTTP_BASIC;
	    String realm = "VCellUser";
		ChallengeAuthenticator guard = new ChallengeAuthenticator(getContext(), optional, challengeScheme, realm){

			@Override
			protected boolean authenticate(Request request, Response response) {
				if (request.getChallengeResponse() == null){
					return false;
				}else{
					return super.authenticate(request, response);
				}
			}
			
		};
		guard.setVerifier(userVerifier);
		return guard;
	}

	@Override  
    public Restlet createInboundRoot() {  
    	
	    System.setProperty("java.net.preferIPv4Stack", "true");
	    
//	    this.challengeAuthenticator = createChallengeAuthenticator(this.userVerifier);
	    
	    
		// Attach a guard to secure access to user parts of the api 
	
		Router rootRouter = new Router(getContext());
		rootRouter.attach("/biomodel", BiomodelsServerResource.class);  
		rootRouter.attach("/biomodel/{biomodelID}", BiomodelServerResource.class);  
		rootRouter.attach("/simulationTask", SimulationTasksServerResource.class);  
		rootRouter.attach("/simulationTask/{simTaskID}", SimulationTaskServerResource.class);  
		
//		this.challengeAuthenticator.setNext(rootRouter);
		
     	    	 
    	return rootRouter;  
    }  
	
    public boolean authenticate(Request request, Response response) {
//        if (!request.getClientInfo().isAuthenticated()) {
//            challengeAuthenticator.challenge(response, false);
//            return false;
//        }
        return true;
    }

	public AdminDBTopLevel getAdminDBTopLevel() {
		return this.adminDBTopLevel;
	}

	public Configuration getTemplateConfiguration() {
		return this.templateConfiguration;
	}
	
	public User getVCellUser(org.restlet.security.User authenticatedUser){
		return new User("schaff",new KeyValue("17"));
//		return userVerifier.getVCellUser(authenticatedUser);
	}

	
}

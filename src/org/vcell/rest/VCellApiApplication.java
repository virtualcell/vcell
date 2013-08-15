package org.vcell.rest;

import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.Restlet;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.MediaType;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.routing.Router;
import org.vcell.rest.server.AccessTokenServerResource;
import org.vcell.rest.server.BiomodelServerResource;
import org.vcell.rest.server.BiomodelSimulationServerResource;
import org.vcell.rest.server.BiomodelSimulationStartServerResource;
import org.vcell.rest.server.BiomodelSimulationStopServerResource;
import org.vcell.rest.server.BiomodelsServerResource;
import org.vcell.rest.server.RestDatabaseService;
import org.vcell.rest.server.SimDataServerResource;
import org.vcell.rest.server.SimDataValuesServerResource;
import org.vcell.rest.server.SimulationTaskServerResource;
import org.vcell.rest.server.SimulationTasksServerResource;
import org.vcell.util.document.User;

import cbit.vcell.modeldb.ApiAccessToken;
import freemarker.template.Configuration;

public class VCellApiApplication extends WadlApplication {
	
	public static final String PARAM_ACCESS_TOKEN = "token";
	public static final String AUTHENTICATED_TOKEN_ATTR_NAME = "authenticatedToken";

	public static final String ACCESSTOKEN = "access_token";
	public static final String VCELLAPI = "vcellapi";

	public static final String BIOMODEL = "biomodel";
	public static final String BIOMODELID = "biomodelid";

	public static final String SIMTASK = "simtask";
	public static final String SIMTASKID = "simtaskid";
	
	public static final String SIMULATION = "simulation";
	public static final String SIMULATIONID = "simulationid";
	
	public static final String SIMDATA = "simdata";
	public static final String SIMDATAID = "simdataid";
	
	public static final String JOBINDEX = "jobindex";
	
	public static final String STARTSIMULATION = "startSimulation";
	public static final String STOPSIMULATION = "stopSimulation";
	
	
	private RestDatabaseService restDatabaseService = null;
	private UserVerifier userVerifier = null;
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

	public VCellApiApplication(RestDatabaseService restDatabaseService, UserVerifier userVerifier, Configuration templateConfiguration) {
        setName("RESTful VCell API application");
        setDescription("Simulation management API");
        setOwner("VCell Project/UCHC");
        setAuthor("VCell Team");
		setStatusService(new VCellStatusService());
		this.restDatabaseService = restDatabaseService;
		this.userVerifier = userVerifier;
		this.templateConfiguration = templateConfiguration;
		getLogger().setLevel(Level.FINE);
	}
	
//	private Authenticator createAuthenticator(){
//		Authenticator guard = new Authenticator(getContext(),false) {
//
//			@Override
//			protected int authenticated(Request request, Response response) {
//				// TODO Auto-generated method stub
//				return super.authenticated(request, response);
//			}
//
//			@Override
//			protected int beforeHandle(Request request, Response response) {
//				// TODO Auto-generated method stub
//				return super.beforeHandle(request, response);
//			}
//
//			@Override
//			public Enroler getEnroler() {
//				// TODO Auto-generated method stub
//				return super.getEnroler();
//			}
//
//			@Override
//			public boolean isMultiAuthenticating() {
//				// TODO Auto-generated method stub
//				return super.isMultiAuthenticating();
//			}
//
//			@Override
//			public boolean isOptional() {
//				// TODO Auto-generated method stub
//				return super.isOptional();
//			}
//
//			@Override
//			protected void afterHandle(Request request, Response response) {
//				// TODO Auto-generated method stub
//				super.afterHandle(request, response);
//			}
//
//			@Override
//			protected int doHandle(Request request, Response response) {
//				// TODO Auto-generated method stub
//				return super.doHandle(request, response);
//			}
//
//			@Override
//			public Restlet getNext() {
//				// TODO Auto-generated method stub
//				return super.getNext();
//			}
//
//			@Override
//			public boolean hasNext() {
//				// TODO Auto-generated method stub
//				return super.hasNext();
//			}
//
//			@Override
//			public synchronized void start() throws Exception {
//				// TODO Auto-generated method stub
//				super.start();
//			}
//
//			@Override
//			protected boolean authenticate(Request request, Response response) {
//				boolean isAllowed = false;
//				
//				Series<Header> headers = (Series<Header>)request.getAttributes().get("org.restlet.http.headers");
//				String authorizationString = headers.getFirstValue("Authorization");
//				// expecting "CUSTOM access_token=123445"
//				String[] tokens = authorizationString.split(" ");
//				String accessToken = null;
//				if (tokens.length==2 && tokens[0].equals("CUSTOM") && tokens[1].startsWith("access_token=")){
//					accessToken = tokens[1].replace("access_token=","");
//					accessToken.replace("\"","");
//				}
//				if (accessToken == null) {
//					//
//					// Notice that we can set standard HTTP/REST error codes
//					// very easily
//					// using the Restlet API. Whenever the response is sent to
//					// the user, this
//					// error code will be set to the unauthorized error code
//					// (unless a filter
//					// or restlet further down the chain changes it).
//					//
//					response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED, "Missing access token.");
//				} else {
//					ApiAccessToken apiAccessToken = null;
//					try {
//						apiAccessToken = userVerifier.getApiAccessToken(accessToken);
//					} catch (SQLException e){
//						e.printStackTrace(System.out);
//					} catch (DataAccessException e) {
//						e.printStackTrace(System.out);
//					}
//					if (apiAccessToken != null) {
//						isAllowed = true;
//						request.getAttributes().put(AUTHENTICATED_TOKEN_ATTR_NAME, apiAccessToken);
//					} else {
//						response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED,
//								String.format("%s is not an allowed in " + PARAM_ACCESS_TOKEN + ".", accessToken));
//					}
//				}
//				return isAllowed;
//			}
//
//		};
//		return guard;
//	}

	@Override  
    public Restlet createInboundRoot() {  
    	
	    System.setProperty("java.net.preferIPv4Stack", "true");
	    
		/**
		 * curl --insecure
		 * "https://nrcamdev5.cam.uchc.edu:8080/access_token?user_id=schaff&user_password=056F4508E0DE1ED22D4D6F541E91460694A00E16&client_id=85133f8d-26f7-4247-8356-d175399fc2e6"
		 * 
		 * {"token":"9002b7ba-004f-40aa-a639-9136364850a6","creationDateSeconds"
		 * :1376586968,"expireDateSeconds":1376673368,"userId":"schaff",
		 * "userKey":"17"}
		 * 
		 * curl --verbose --insecure -H
		 * "Authorization: CUSTOM access_token=9002b7ba-004f-40aa-a639-9136364850a6"
		 * https://nrcamdev5.cam.uchc.edu:8080/simtask?running=on
		 * 
		 * [{"simKey":"83642386","simName":"Simulation5","userName":"schaff",
		 * "userKey"
		 * :"17","htcJobId":"SGE:283488","status":"running","startdate":
		 * 1375408007000
		 * ,"jobIndex":0,"taskId":0,"message":"0.7202440000182919","site"
		 * :"REL","computeHost"
		 * :"compute-2-0.local","schedulerStatus":"running","hasData"
		 * :true,"scanCount"
		 * :1,"bioModelLink":{"bioModelKey":"83642503","bioModelBranchId"
		 * :"83640470"
		 * ,"bioModelName":"CSHL 2011 Exercise 6_Jim_2013","simContextKey"
		 * :"83642357","simContextBranchId":"83642358","simContextName":
		 * "smoldyn_repressor_activator_oscillations"
		 * }},{"simKey":"38442198","simName"
		 * :"Simulation0","userName":"schaff","userKey"
		 * :"17","status":"running","startdate"
		 * :1264828384000,"jobIndex":0,"taskId"
		 * :0,"message":"processing geometry..."
		 * ,"site":"SCHAFF_WSAD","computeHost"
		 * :"\u003c\u003clocal\u003e\u003e","schedulerStatus"
		 * :"running","hasData"
		 * :false,"scanCount":1,"mathModelLink":{"mathModelKey"
		 * :"38442201","mathModelBranchId"
		 * :"38442202","mathModelName":"tempMath77"}}]
		 */
		//	    System.out.println(UUID.randomUUID());
//	    System.out.println(UUID.randomUUID());
//	    
//	    Router mainRouter = new Router(this.getContext());
//	    mainRouter.attach("/"+ACCESSTOKEN, AccessTokenServerResource.class,Router.MODE_FIRST_MATCH);
//
//	    this.authenticator = createAuthenticator();
//	    mainRouter.attach("/"+VCELLAPI, authenticator, Router.MODE_FIRST_MATCH);
	    
	    
		// Attach a guard to secure access to user parts of the api 
	
		Router rootRouter = new Router(getContext());
	    rootRouter.attach("/"+ACCESSTOKEN, AccessTokenServerResource.class);
		rootRouter.attach("/"+BIOMODEL, BiomodelsServerResource.class);  
		rootRouter.attach("/"+BIOMODEL+"/{"+BIOMODELID+"}", BiomodelServerResource.class);  
		rootRouter.attach("/"+BIOMODEL+"/{"+BIOMODELID+"}/"+SIMULATION+"/{"+SIMULATIONID+"}", BiomodelSimulationServerResource.class);  
		rootRouter.attach("/"+BIOMODEL+"/{"+BIOMODELID+"}/"+SIMULATION+"/{"+SIMULATIONID+"}/"+STARTSIMULATION, BiomodelSimulationStartServerResource.class);  
		rootRouter.attach("/"+BIOMODEL+"/{"+BIOMODELID+"}/"+SIMULATION+"/{"+SIMULATIONID+"}/"+STOPSIMULATION, BiomodelSimulationStopServerResource.class);  
		rootRouter.attach("/"+SIMTASK, SimulationTasksServerResource.class);  
		rootRouter.attach("/"+SIMTASK+"/{"+SIMTASKID+"}", SimulationTaskServerResource.class);  
		rootRouter.attach("/"+SIMDATA+"/{"+SIMDATAID+"}", SimDataServerResource.class);  
		rootRouter.attach("/"+SIMDATA+"/{"+SIMDATAID+"}/jobindex/{"+JOBINDEX+"}", SimDataValuesServerResource.class);  
		
//	    authenticator.setNext(rootRouter);
     	    	 
    	return rootRouter;  
    }  
	
	public RestDatabaseService getRestDatabaseService() {
		return this.restDatabaseService;
	}

	public Configuration getTemplateConfiguration() {
		return this.templateConfiguration;
	}
	
	public UserVerifier getUserVerifier(){
		return userVerifier;
	}

	public User getVCellUser(ChallengeResponse response) {
		if (response==null || response.getIdentifier()==null || !response.getIdentifier().equals("access_token") || response.getSecret()==null || response.getSecret().length==0){
			throw new RuntimeException("missing authentication");
		}
		try {
			ApiAccessToken accessToken = userVerifier.getApiAccessToken(new String(response.getSecret()));
			if (accessToken!=null){
				if (accessToken.isValid()){
					return accessToken.getUser();
				}else{
					throw new RuntimeException("invalid access token (expired?)");
				}
			}else{
				throw new RuntimeException("missing authentication");
			
			}
		}catch (Exception e){
			throw new RuntimeException("error authenticating token",e);
		}
	}
	
	
		
}

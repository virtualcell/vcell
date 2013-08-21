package org.vcell.rest;

import java.sql.SQLException;
import java.util.logging.Level;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Router;
import org.vcell.rest.UserVerifier.AuthenticationStatus;
import org.vcell.rest.auth.CustomAuthHelper;
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
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.modeldb.ApiAccessToken;
import cbit.vcell.modeldb.ApiAccessToken.AccessTokenStatus;
import freemarker.template.Configuration;

public class VCellApiApplication extends WadlApplication {
	
	public enum AuthenticationPolicy {
		ignoreInvalidCredentials,
		prohibitInvalidCredentials
	}

	public static final String LOGIN = "login";
	public static final String LOGINFORM = "loginform";
	public static final String IDENTIFIER_FORMNAME = "user";
	public static final String SECRET_FORMNAME = "password";
	public static final String LOGOUT = "logout";
	public static final String REDIRECTURL_FORMNAME = "redirecturl";
	
	public static final String BROWSER_CLIENTID = "dskeofihdslksoihe";
	
	public static final String PARAM_ACCESS_TOKEN = "token";
	public static final String AUTHENTICATED_TOKEN_ATTR_NAME = "authenticatedToken";

	/** https://nrcamdev5.cam.uchc.edu:8080/access_token?user_id=schaff&user_password=056F4508E0DE1ED22D4D6F541E91460694A00E16&client_id=85133f8d-26f7-4247-8356-d175399fc2e6 */
	public static final String ACCESSTOKENRESOURCE = "access_token";  // this is the authentication end point (resource) for the (GET) query parameter authentication (returns a JSON access token)
	
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
	public static final User DUMMY_USER = new User("VOID_VCELL_USER", new KeyValue("11111111111111"));
	
	
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
	    
	
		// Attach a guard to secure access to user parts of the api 

		boolean bCookieOptional = true;
        
        
		final VCellCookieAuthenticator cookieAuthenticator = new VCellCookieAuthenticator(this, bCookieOptional, "My cookie realm", "MyExtraSecretKey".getBytes());

        
        cookieAuthenticator.setLoginPath("/"+LOGIN);
        cookieAuthenticator.setLogoutPath("/"+LOGOUT);
        cookieAuthenticator.setCookieName("org.vcell.auth");
        cookieAuthenticator.setLoginFormPath("/"+LOGINFORM);
        cookieAuthenticator.setIdentifierFormName(IDENTIFIER_FORMNAME);
        cookieAuthenticator.setSecretFormName(SECRET_FORMNAME);
        cookieAuthenticator.setRedirectQueryName(REDIRECTURL_FORMNAME);
        cookieAuthenticator.setVerifier(userVerifier);

	    
		Router rootRouter = new Router(getContext());
	    rootRouter.attach("/"+ACCESSTOKENRESOURCE, AccessTokenServerResource.class);
		rootRouter.attach("/"+BIOMODEL, BiomodelsServerResource.class);  
		rootRouter.attach("/"+BIOMODEL+"/{"+BIOMODELID+"}", BiomodelServerResource.class);  
		rootRouter.attach("/"+BIOMODEL+"/{"+BIOMODELID+"}/"+SIMULATION+"/{"+SIMULATIONID+"}", BiomodelSimulationServerResource.class);  
		rootRouter.attach("/"+BIOMODEL+"/{"+BIOMODELID+"}/"+SIMULATION+"/{"+SIMULATIONID+"}/"+STARTSIMULATION, BiomodelSimulationStartServerResource.class);  
		rootRouter.attach("/"+BIOMODEL+"/{"+BIOMODELID+"}/"+SIMULATION+"/{"+SIMULATIONID+"}/"+STOPSIMULATION, BiomodelSimulationStopServerResource.class);  
		rootRouter.attach("/"+SIMTASK, SimulationTasksServerResource.class);  
		rootRouter.attach("/"+SIMTASK+"/{"+SIMTASKID+"}", SimulationTaskServerResource.class);  
		rootRouter.attach("/"+SIMDATA+"/{"+SIMDATAID+"}", SimDataServerResource.class);  
		rootRouter.attach("/"+SIMDATA+"/{"+SIMDATAID+"}/jobindex/{"+JOBINDEX+"}", SimDataValuesServerResource.class);  
		
		rootRouter.attach("/"+LOGIN, new Restlet(getContext()){
            @Override
            public void handle(Request request, Response response) {
		        String html = 
		        	"<html>" +
		        		"/login ... should have been redirected."+
		        	"</html>";
		        response.setEntity(html, MediaType.TEXT_HTML);
            }
		});
		
		rootRouter.attach("/"+LOGINFORM, new Restlet(getContext()){
            @Override
            public void handle(Request request, Response response) {
            	org.restlet.data.Reference reference = request.getReferrerRef();
               	String html = "<html>" +
        					"<form name='login' action='"+"/"+LOGIN+ "' method='post'>" +
        							"VCell userid <input type='text' name='"+IDENTIFIER_FORMNAME+"' value=''/><br/>" +
        							"VCell password <input type='password' name='"+SECRET_FORMNAME+"' value=''/><br/>" +
        							"<input type='hidden' name='"+REDIRECTURL_FORMNAME+"' value='"+reference+"'>" +
        							"<input type='submit' value='sign in'/>" +
        					"</form>" +
          				  "</html>";
                	response.setEntity(html, MediaType.TEXT_HTML);
            }
		});
		
        cookieAuthenticator.setNext(rootRouter);
     	    	 
    	return cookieAuthenticator;  
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

	public User getVCellUser(ChallengeResponse response, AuthenticationPolicy authPolicy) {
		try {
			ApiAccessToken accessToken = getApiAccessToken(response);
			if (accessToken!=null){
				if (accessToken.isExpired()){
					if (authPolicy == AuthenticationPolicy.ignoreInvalidCredentials){
						getLogger().log(Level.INFO,"VCellApiApplication.getVCellUser(response) - ApiAccessToken has expired ... returning user = null");
						return null;
					}else{
						throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "access_token expired");
					}
				}else if (accessToken.getStatus()==AccessTokenStatus.invalidated){
					if (authPolicy == AuthenticationPolicy.ignoreInvalidCredentials){
						getLogger().log(Level.INFO,"VCellApiApplication.getVCellUse(response) - ApiAccessToken has been invalidated ... returning user = null");
						return null;
					}else{
						throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "access_token invalid");
					}
				}else{
					getLogger().log(Level.INFO,"VCellApiApplication.getVCellUse(response) - ApiAccessToken is valid ... returning user = "+accessToken.getUser().getName());
					return accessToken.getUser();
				}
			}else{ // accessToken is null
				AuthenticationStatus authStatus = userVerifier.verify(response);
				if (authStatus==AuthenticationStatus.missing){
					getLogger().log(Level.INFO,"VCellApiApplication.getVCellUse(response) - ApiAccessToken not provided ... returning user = null");
					return null;
				}else{
					if (authPolicy == AuthenticationPolicy.ignoreInvalidCredentials){
						getLogger().log(Level.INFO,"VCellApiApplication.getVCellUse(response) - ApiAccessToken not found in database ... returning user = null");
						return null;
					}else{
						throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "access_token invalid");
					}
				}
			}
		}catch (Exception e){
			if (authPolicy == AuthenticationPolicy.ignoreInvalidCredentials){
				getLogger().log(Level.SEVERE,"VCellApiApplication.getVCellUse(response) - error authenticating user", e);
				return null;
			}else{
				throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED,e.getMessage());
			}
		}
	}
	
	ApiAccessToken getApiAccessToken(ChallengeResponse response) throws SQLException, DataAccessException{
		if (response==null){
			getLogger().log(Level.INFO,"VCellApiApplication.getApiAccessToken(response) - response was null");
			return null;
		}else if (response.getIdentifier()==null){
			getLogger().log(Level.INFO,"VCellApiApplication.getApiAccessToken(response) - response.getIdentifier() was null");
			return null;
		}else if (!response.getIdentifier().equals(CustomAuthHelper.ACCESS_TOKEN)){
			getLogger().log(Level.INFO,"VCellApiApplication.getApiAccessToken(response) - response.getIdentifier() was '"+response.getIdentifier()+"', expecting '"+CustomAuthHelper.ACCESS_TOKEN+"'");
			return null;
		}else if (response.getSecret()==null){
			getLogger().log(Level.INFO,"VCellApiApplication.getApiAccessToken(response) - response.getSecret() was null");
			return null;
		}
		ApiAccessToken accessToken = userVerifier.getApiAccessToken(new String(response.getSecret()));
		return accessToken;
	}
	
	
		
}

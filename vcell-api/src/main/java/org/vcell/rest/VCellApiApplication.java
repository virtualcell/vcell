package org.vcell.rest;

import cbit.vcell.modeldb.ApiAccessToken;
import cbit.vcell.modeldb.ApiAccessToken.AccessTokenStatus;
import cbit.vcell.resource.PropertyLoader;
import freemarker.template.Configuration;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.*;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Directory;
import org.restlet.resource.ResourceException;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.vcell.rest.admin.AdminJobsRestlet;
import org.vcell.rest.admin.AdminService;
import org.vcell.rest.admin.AdminStatsRestlet;
import org.vcell.rest.auth.AuthenticationTokenRestlet;
import org.vcell.rest.auth.BearerTokenVerifier;
import org.vcell.rest.auth.CookieVerifier;
import org.vcell.rest.events.EventsRestlet;
import org.vcell.rest.events.RestEventService;
import org.vcell.rest.health.HealthRestlet;
import org.vcell.rest.n5data.N5GetInfoRestlet;
import org.vcell.rest.n5data.N5ExportRestlet;
import org.vcell.rest.health.HealthService;
import org.vcell.rest.rpc.RpcRestlet;
import org.vcell.rest.rpc.RpcService;
import org.vcell.rest.server.*;
import org.vcell.rest.users.*;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.SpecialUser;
import org.vcell.util.document.User;

import java.io.File;
import java.util.logging.Level;

public class VCellApiApplication extends WadlApplication {
	
	public enum AuthenticationPolicy {
		ignoreInvalidCredentials,
		prohibitInvalidCredentials
	}
	
	public final static String NEWUSERID_FORMNAME				= "newuserid";
	public final static String NEWPASSWORD1_FORMNAME			= "newpassword1";
	public final static String NEWPASSWORD2_FORMNAME			= "newpassword2";
	public final static String NEWFIRSTNAME_FORMNAME			= "newfirstname";
	public final static String NEWLASTNAME_FORMNAME				= "newlastname";
	public final static String NEWEMAIL_FORMNAME				= "newemail";
	public final static String NEWINSTITUTE_FORMNAME			= "newinstitute";
	public final static String NEWCOUNTRY_FORMNAME				= "newcountry";
	public final static String NEWNOTIFY_FORMNAME				= "newnotify";
	public final static String NEWERRORMESSAGE_FORMNAME			= "newerrormsg";
	public final static String NEWFORMPROCESSING_FORMNAME		= "newform";
	
	public final static String EMAILVERIFYTOKEN_FORMNAME	= "verifytoken";

	public static final String IDENTIFIER_FORMNAME = "user";
	public static final String SECRET_FORMNAME = "password";
	public static final String REDIRECTURL_FORMNAME = "redirecturl";

	public static final String LOGINFORM = "loginform";
	public static final String LOGIN = "login";
	public static final String LOGOUT = "logout";
	
	public static final String REGISTRATIONFORM = "registrationform";
	public static final String NEWUSER = "newuser";
	public static final String NEWUSER_VERIFY = "newuserverify";
	public static final String LOSTPASSWORD = "lostpassword";
	public static final String CONTACTUS = "contactus";
	public static final String SWVERSION = "swversion";

	public static final String BROWSER_CLIENTID = "dskeofihdslksoihe";
	
	public static final String PARAM_ACCESS_TOKEN = "token";
	public static final String AUTHENTICATED_TOKEN_ATTR_NAME = "authenticatedToken";

	/** https://nrcamdev5.cam.uchc.edu:8080/access_token?user_id=schaff&user_password=056F4508E0DE1ED22D4D6F541E91460694A00E16&client_id=85133f8d-26f7-4247-8356-d175399fc2e6 */
	public static final String ACCESSTOKENRESOURCE = "access_token";  // this is the authentication end point (resource) for the (GET) query parameter authentication (returns a JSON access token)
	
	public static final String VCELLAPI = "vcellapi";

	public static final String WEBAPP = "webapp";
	
	public static final String RPC = "rpc";
	
	public static final String OPTIMIZATION = "optimization";
	public static final String RUNOPTIMIZATION = "run";
	public static final String OPTIMIZATIONID = "optimizationid";
	
	public static final String PUBLICATION = "publication";
	public static final String PUBLICATIONID = "publicationid";
	
	public static final String BIOMODEL = "biomodel";
	public static final String MODELBRICK = "modelbrick";
	public static final String BIOMODELID = "biomodelid";
	public static final String MODELNAME = "modelname";

	public static final String SIMSTATUS = "simstatus";
	
	public static final String SIMTASK = "simtask";
	public static final String SIMTASKID = "simtaskid";
	
	public static final String VCML_DOWNLOAD = "biomodel.vcml";
	public static final String SBML_DOWNLOAD = "biomodel.sbml";
	public static final String OMEX_DOWNLOAD = "biomodel.omex";
	public static final String BNGL_DOWNLOAD = "biomodel.bngl";
	public static final String DIAGRAM_DOWNLOAD = "diagram";
	public static final String SIMULATION = "simulation";
	public static final String SIMULATIONID = "simulationid";
	
	public static final String SIMDATA = "simdata";
	public static final String SIMDATAID = "simdataid";
	
	public static final String EVENTS = "events";
	public static final String EVENTS_BEGINTIMESTAMP = "beginTimestamp";

	public static final String N5DATA = "n5data";
	public static final String N5_INFO = "info";
	public static final String N5_EXPORT = "export";
	public static final String N5_INFO_TYPE = "typeOfInfo";
	public static final String N5_INFO_SUPPORTED_SPECIES = "supported_species";
	public static final String N5_EXPORT_SPECIES = "species";
	public static final String N5_SIMID = "simid";
	public static final String N5_COMPRESSION = "compression_level";
	public static final String N5_EXPORT_COMPRESSION_RAW = "raw";
	public static final String N5_EXPORT_COMPRESSION_GZIP = "gzip";
	public static final String N5_EXPORT_COMPRESSION_BZIP = "bzip";
	
	public static final String HEALTH = "health";

	public static final String HEALTH_CHECK = "check";
	public static final String 	HEALTH_CHECK_SIM = "sim";
	public static final String 	HEALTH_CHECK_STATUS_TIMESTAMP = "status_timestamp";
	public static final String 	HEALTH_CHECK_ALL = "all";
	public static final String     HEALTH_CHECK_ALL_START_TIMESTAMP = "start_timestamp";
	public static final String     HEALTH_CHECK_ALL_END_TIMESTAMP = "end_timestamp";
	
	public static final String ADMIN = "admin";
	public static final String 	ADMIN_JOBS = "jobs";
	public static final String 	ADMIN_STATS = "stats";

	public static final String JOBINDEX = "jobindex";
	
	public static final String SAVESIMULATION = "save";
	public static final String STARTSIMULATION = "startSimulation";
	public static final String STOPSIMULATION = "stopSimulation";

	public static final User DUMMY_USER = new User("VOID_VCELL_USER", new KeyValue("11111111111111"));
	
	public static final String USERNAME_EDUCATION = "Education";
	public static final String USERNAME_TUTORIAL = "tutorial";

	public static final String PSEUDOOWNER_PUBLIC = "all_public";
	public static final String PSEUDOOWNER_SHARED = "shared";
	public static final String PSEUDOOWNER_EDUCATION = "Education";
	public static final String PSEUDOOWNER_TUTORIAL = "tutorial";
	
	public static final String CATEGORY_PUBLIC = "public";
	public static final String CATEGORY_SHARED = "shared";
	public static final String CATEGORY_EDUCATION = "education";
	public static final String CATEGORY_TUTORIAL = "tutorial";
	public static final String CATEGORY_MINE = "mine";
	public static final String CATEGORY_ALL = "all";
	
	
	private RestDatabaseService restDatabaseService = null;
	private UserService userService = null;

	private BearerTokenVerifier bearerTokenVerifier = null;
	private CookieVerifier cookieVerifier = null;
	private Configuration templateConfiguration = null;
	private File javascriptDir = null;
	private RpcService rpcService = null;
	private RestEventService restEventService = null;
	private HealthService healthService = null;
	private AdminService adminService = null;
	
	@Override
	protected Variant getPreferredWadlVariant(Request request) {
		return new Variant(MediaType.APPLICATION_WADL);
	}
		
	public RpcService getRpcService() {
		return rpcService;
	}

	public RestEventService getEventsService() {
		return restEventService;
	}
	
	public HealthService getHealthService() {
		return healthService;
	}

	@Override
	protected Representation createHtmlRepresentation(ApplicationInfo applicationInfo) {
		return super.createHtmlRepresentation(applicationInfo);
	}

	public VCellApiApplication(
			RestDatabaseService restDatabaseService, 
			UserService userService,
			RpcService rpcService, 
			RestEventService restEventService, 
			AdminService adminService,
			Configuration templateConfiguration, 
			HealthService healthService,
			File javascriptDir) {
		
        setName("RESTful VCell API application");
        setDescription("Simulation management API");
        setOwner("VCell Project/UCHC");
        setAuthor("VCell Team");
		setStatusService(new VCellStatusService());
		this.javascriptDir = javascriptDir;
		this.restDatabaseService = restDatabaseService;
		this.adminService = adminService;
		this.userService = userService;
		this.rpcService = rpcService;
		this.restEventService = restEventService;
		this.templateConfiguration = templateConfiguration;
		this.healthService = healthService;
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

        String ROOT_URI = javascriptDir.toURI().toString();
		String PATH_PREFIX = PropertyLoader.getRequiredProperty(PropertyLoader.vcellServerPrefixV0);
        String WEBAPP_URI = new File(javascriptDir.getParentFile(),"webapp").toURI().toString();
        getLogger().info("using uri="+ROOT_URI+" for scripts directory");
        String SCRIPTS = "scripts";
        
		Router rootRouter = new Router(getContext());
		rootRouter.attach("/"+SWVERSION, new SWVersionRestlet(getContext())); // for backward compatibility
		rootRouter.attach(PATH_PREFIX+"/", new SWVersionRestlet(getContext()));
		rootRouter.attach(PATH_PREFIX+"/"+SWVERSION, new SWVersionRestlet(getContext()));

		rootRouter.attach(PATH_PREFIX+"/"+SCRIPTS, new Directory(getContext(), ROOT_URI));
	    rootRouter.attach(PATH_PREFIX+"/"+ACCESSTOKENRESOURCE, new AuthenticationTokenRestlet(getContext()));
	    rootRouter.attach(PATH_PREFIX+"/"+WEBAPP, new Directory(getContext(), WEBAPP_URI));
	    rootRouter.attach(PATH_PREFIX+"/"+OPTIMIZATION, OptimizationRunServerResource.class);
	    rootRouter.attach(PATH_PREFIX+"/"+OPTIMIZATION+"/{"+OPTIMIZATIONID+"}", OptimizationRunServerResource.class);
	    rootRouter.attach(PATH_PREFIX+"/"+PUBLICATION, PublicationsServerResource.class);
	    rootRouter.attach(PATH_PREFIX+"/"+PUBLICATION+"/{"+PUBLICATIONID+"}", PublicationServerResource.class);
		rootRouter.attach(PATH_PREFIX+"/"+BIOMODEL, BiomodelsServerResource.class);  
		rootRouter.attach(PATH_PREFIX+"/"+BIOMODEL+"/{"+BIOMODELID+"}", BiomodelServerResource.class);  
		rootRouter.attach(PATH_PREFIX+"/"+BIOMODEL+"/{"+BIOMODELID+"}/"+VCML_DOWNLOAD, BiomodelVCMLServerResource.class);
		rootRouter.attach(PATH_PREFIX+"/"+BIOMODEL+"/{"+BIOMODELID+"}/"+SBML_DOWNLOAD, BiomodelSBMLServerResource.class);
		rootRouter.attach(PATH_PREFIX+"/"+BIOMODEL+"/{"+BIOMODELID+"}/"+OMEX_DOWNLOAD, BiomodelOMEXServerResource.class);
		rootRouter.attach(PATH_PREFIX+"/"+BIOMODEL+"/{"+BIOMODELID+"}/"+BNGL_DOWNLOAD, BiomodelBNGLServerResource.class);
		rootRouter.attach(PATH_PREFIX+"/"+MODELBRICK, BiomodelVCMLModelInfoResource.class);//Expects queryparameters
		rootRouter.attach(PATH_PREFIX+"/"+BIOMODEL+"/{"+BIOMODELID+"}/"+DIAGRAM_DOWNLOAD, BiomodelDiagramServerResource.class);  
		rootRouter.attach(PATH_PREFIX+"/"+BIOMODEL+"/{"+BIOMODELID+"}/"+SIMULATION+"/{"+SIMULATIONID+"}", BiomodelSimulationServerResource.class);  
		rootRouter.attach(PATH_PREFIX+"/"+BIOMODEL+"/{"+BIOMODELID+"}/"+SIMULATION+"/{"+SIMULATIONID+"}/"+SAVESIMULATION, BiomodelSimulationSaveServerResource.class);  
		rootRouter.attach(PATH_PREFIX+"/"+BIOMODEL+"/{"+BIOMODELID+"}/"+SIMULATION+"/{"+SIMULATIONID+"}/"+STARTSIMULATION, BiomodelSimulationStartServerResource.class);  
		rootRouter.attach(PATH_PREFIX+"/"+BIOMODEL+"/{"+BIOMODELID+"}/"+SIMULATION+"/{"+SIMULATIONID+"}/"+STOPSIMULATION, BiomodelSimulationStopServerResource.class);  
		rootRouter.attach(PATH_PREFIX+"/"+SIMSTATUS, SimulationStatusServerResource.class);  
		rootRouter.attach(PATH_PREFIX+"/"+SIMTASK, SimulationTasksServerResource.class);  
		rootRouter.attach(PATH_PREFIX+"/"+SIMTASK+"/{"+SIMTASKID+"}", SimulationTaskServerResource.class);  
		rootRouter.attach(PATH_PREFIX+"/"+SIMDATA+"/{"+SIMDATAID+"}", SimDataServerResource.class);  
		rootRouter.attach(PATH_PREFIX+"/"+SIMDATA+"/{"+SIMDATAID+"}/jobindex/{"+JOBINDEX+"}", SimDataValuesServerResource.class);  
		
		rootRouter.attach(PATH_PREFIX+"/"+LOGIN, new LoginRestlet(getContext()));
		
		rootRouter.attach(PATH_PREFIX+"/"+LOGINFORM, new LoginFormRestlet(getContext()));
		
		rootRouter.attach(PATH_PREFIX+"/"+REGISTRATIONFORM, new RegistrationFormRestlet(getContext()));
		
		rootRouter.attach(PATH_PREFIX+"/"+NEWUSER, new NewUserRestlet(getContext()));
		
		rootRouter.attach(PATH_PREFIX+"/"+NEWUSER_VERIFY, new EmailTokenVerifyRestlet(getContext()));
		
		rootRouter.attach(PATH_PREFIX+"/"+LOSTPASSWORD, new LostPasswordRestlet(getContext()));
		
		rootRouter.attach(PATH_PREFIX+"/"+CONTACTUS, new ContactUsRestlet(getContext()));

	    rootRouter.attach(PATH_PREFIX+"/"+RPC, new RpcRestlet(getContext(),restDatabaseService));

	    rootRouter.attach(PATH_PREFIX+"/"+EVENTS, new EventsRestlet(getContext()));

	    rootRouter.attach(PATH_PREFIX+"/"+HEALTH, new HealthRestlet(getContext()));

		rootRouter.attach(PATH_PREFIX+"/"+ N5DATA + "/{" + N5_SIMID + "}/" + N5_EXPORT, new N5ExportRestlet(getContext()));
		rootRouter.attach(PATH_PREFIX+"/" + N5DATA + "/{" + N5_SIMID + "}/" + N5_INFO, new N5GetInfoRestlet(getContext()));

		rootRouter.attach(PATH_PREFIX+"/"+ADMIN+"/"+ADMIN_JOBS, new AdminJobsRestlet(getContext()));
		rootRouter.attach(PATH_PREFIX+"/"+ADMIN+"/"+ADMIN_STATS, new AdminStatsRestlet(getContext(), restDatabaseService));

	    rootRouter.attach(PATH_PREFIX+"/auth/user", new Restlet(getContext()){

			@Override
			public void handle(Request request, Response response) {
				if (request.getMethod().equals(Method.GET)){
					VCellApiApplication application = ((VCellApiApplication)getApplication());
					User vcellUser = application.getVCellUser(request.getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
					String jsonString = "{}";
					if (vcellUser != null){
						jsonString = "{user: \""+vcellUser.getName()+"\"}";
					}
			    	response.setEntity(new StringRepresentation(jsonString));
				}
			}
			
		});

		// Attach an auth bearer guard to secure access to user parts of the api via
		boolean bAuthOptional = true;
		ChallengeAuthenticator bearerTokenAuthenticator = new ChallengeAuthenticator(
				getContext(), bAuthOptional, ChallengeScheme.HTTP_OAUTH_BEARER, "testRealm");
		bearerTokenVerifier = new BearerTokenVerifier(userService);
		bearerTokenAuthenticator.setMultiAuthenticating(false); // if it is already authenticated via cookies, then don't authenticate again
		bearerTokenAuthenticator.setVerifier(bearerTokenVerifier);

		// Attach a cookie based Basic auth guard to secure access to browser access.
		boolean bCookieOptional = true;
		cookieVerifier = new CookieVerifier(userService);
		final VCellCookieAuthenticator cookieAuthenticator = new VCellCookieAuthenticator(
				userService, cookieVerifier, getContext(), bCookieOptional, "My cookie realm", "MyExtraSecretKey".getBytes());
		cookieAuthenticator.setMultiAuthenticating(false);
		cookieAuthenticator.setLoginPath(PATH_PREFIX+"/"+LOGIN);
		cookieAuthenticator.setLogoutPath(PATH_PREFIX+"/"+LOGOUT);
		cookieAuthenticator.setCookieName("org.vcell.auth");
		cookieAuthenticator.setLoginFormPath(PATH_PREFIX+"/"+LOGINFORM);
		cookieAuthenticator.setIdentifierFormName(IDENTIFIER_FORMNAME);
		cookieAuthenticator.setSecretFormName(SECRET_FORMNAME);
		cookieAuthenticator.setRedirectQueryName(REDIRECTURL_FORMNAME);
		cookieAuthenticator.setMaxCookieAge(15*60); // 15 minutes (in units of seconds).

		bearerTokenAuthenticator.setNext(rootRouter);
		cookieAuthenticator.setNext(bearerTokenAuthenticator);
    	return cookieAuthenticator;
    }  
	
   public RestDatabaseService getRestDatabaseService() {
		return this.restDatabaseService;
	}

	public Configuration getTemplateConfiguration() {
		return this.templateConfiguration;
	}
	
	public UserService getUserService(){
		return userService;
	}

	public AdminService getAdminService() {
		return this.adminService;
	}

	public SpecialUser.SPECIAL_CLAIM[] getSpecialClaims(ApiAccessToken apiAccessToken) throws DataAccessException {
		User user = apiAccessToken.getUser();
		return restDatabaseService.getSpecialClaims(user);
	}

	public User getVCellUser(ChallengeResponse response, AuthenticationPolicy authPolicy) throws ResourceException {
		ApiAccessToken apiAccessToken = getApiAccessToken(response, authPolicy);
		if (apiAccessToken != null){
			return apiAccessToken.getUser();
		} else {
			return null;
		}
	}

	public ApiAccessToken getApiAccessToken(ChallengeResponse response, AuthenticationPolicy authPolicy) throws ResourceException {
		try {
			ApiAccessToken accessToken = bearerTokenVerifier.getApiAccessToken(response);
			if (accessToken == null){
				accessToken = cookieVerifier.getApiAccessToken(response);
			}
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
						getLogger().log(Level.INFO,"VCellApiApplication.getVCellUser(response) - ApiAccessToken has been invalidated ... returning user = null");
						return null;
					}else{
						throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "access_token invalid");
					}
				}else{
					getLogger().log(Level.FINE,"VCellApiApplication.getVCellUser(response) - ApiAccessToken is valid ... returning user = "+accessToken.getUser().getName());
					return accessToken;
				}
			}else{ // accessToken is null
				if (authPolicy == AuthenticationPolicy.ignoreInvalidCredentials){
					getLogger().log(Level.INFO,"VCellApiApplication.getVCellUser(response) - ApiAccessToken not found in database ... returning user = null");
					return null;
				}else{
					throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "access_token invalid");
				}
			}
		}catch (Exception e){
			if (authPolicy == AuthenticationPolicy.ignoreInvalidCredentials){
				getLogger().log(Level.SEVERE,"VCellApiApplication.getVCellUser(response) - error authenticating user", e);
				return null;
			}else{
				throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED,e.getMessage());
			}
		}
	}
}

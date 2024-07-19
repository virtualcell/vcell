package org.vcell.restq;

import cbit.vcell.resource.PropertyLoader;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.vcell.db.DatabaseService;
import org.vcell.restq.config.CDIVCellConfigProvider;
import org.vcell.restq.db.AgroalConnectionFactory;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import java.io.IOException;

@QuarkusMain
public class Main {
    public static void main(String... args) {
        Quarkus.run(MyApp.class, args);
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
//    public static final String OMEX_DOWNLOAD = "biomodel.omex";
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
    public static final String 	HEALTH_CHECK_LOGIN = "login";
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

    public static class MyApp implements QuarkusApplication {

        @Override
        public int run(String... args) throws Exception {
            // do startup logic here
            setupConfiguration();
            setupDatabase();

            System.out.println("Starting VCell REST with " + io.quarkus.runtime.LaunchMode.current() + " mode.");

            Quarkus.waitForExit();
            return 0;
        }

        private void setupDatabase() {
            // this reconciles the CDI database with the PropertyLoader, replacing PropertyLoader's default
            // database provider which is backed by System properties.
            DatabaseService.getInstance().setConnectionFactory(new AgroalConnectionFactory());
        }

        private void setupConfiguration() throws IOException {
            // this reconciles the CDI configuration with the PropertyLoader, replacing PropertyLoader's default
            // config provider which is backed by System properties.
            PropertyLoader.setConfigProvider(new CDIVCellConfigProvider());
        }
    }
}
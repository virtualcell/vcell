package org.vcell.rest;

import cbit.vcell.message.*;
import cbit.vcell.message.jms.activeMQ.VCMessagingServiceActiveMQ;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.LocalAdminDbServer;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.resource.EnvironmentConfigProvider;
import cbit.vcell.resource.PropertyLoader;
import com.google.inject.Guice;
import com.google.inject.Injector;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Client;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.ext.wadl.WadlComponent;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.dependency.server.VCellServerModule;
import org.vcell.rest.admin.AdminService;
import org.vcell.rest.events.RestEventService;
import org.vcell.rest.health.HealthService;
import org.vcell.rest.rpc.RpcService;
import org.vcell.rest.server.RestDatabaseService;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;

import java.io.File;

public class VCellApiMain {
	private final static Logger lg = LogManager.getLogger(VCellApiMain.class);
	private final static String TEST_USER = PropertyLoader.TESTACCOUNT_USERID;

	private void init(String[] args) throws Exception {
		if (args.length != 2) {
			lg.info("usage: VCellApiMain javascriptDir port");
			System.exit(1);
		}
		File javascriptDir = new File(args[0]);
		if (!javascriptDir.isDirectory()) {
			throw new RuntimeException("javascriptDir '" + args[0] + "' is not a directory");
		}

		String portString = args[1];
		Integer port=null; // was hard-coded at 8080
		try {
			port = Integer.parseInt(portString);
		}catch (NumberFormatException e){
			lg.error(e);
			throw new RuntimeException("failed to parse port argument '"+portString+"'",e);
		}

		lg.trace("connecting to database");

		lg.trace("oracle factory (next)");
		ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory();
		KeyFactory keyFactory = conFactory.getKeyFactory();
		lg.trace("database impl (next)");
		DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory);
		lg.trace("local db server (next)");
		LocalAdminDbServer localAdminDbServer = new LocalAdminDbServer(conFactory, keyFactory);
		lg.trace("admin db server (next)");
		AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory);

		lg.trace("messaging service (next)");
		VCMessagingService vcMessagingService_int = new VCMessagingServiceActiveMQ();
		VCMessagingDelegate delegate = new VCMessagingDelegate() {

			@Override
			public void onTraceEvent(String string) {
				if (lg.isTraceEnabled()) lg.trace("onTraceEvent(): "+string);
			}

			@Override
			public void onRpcRequestSent(VCRpcRequest vcRpcRequest, UserLoginInfo userLoginInfo, VCMessage vcRpcRequestMessage) {
				if (lg.isTraceEnabled()) lg.trace("onRpcRequestSent(): "+vcRpcRequest.getMethodName());
			}

			@Override
			public void onRpcRequestProcessed(VCRpcRequest vcRpcRequest, VCMessage rpcVCMessage) {
				if (lg.isTraceEnabled()) lg.trace("onRpcRequestProcessed(): "+vcRpcRequest.getMethodName());
			}

			@Override
			public void onMessageSent(VCMessage message, VCDestination desintation) {
				if (lg.isTraceEnabled()) lg.trace("onMessageSent(): "+message);
			}

			@Override
			public void onMessageReceived(VCMessage vcMessage, VCDestination vcDestination) {
				if (lg.isTraceEnabled()) lg.trace("onMessageReceived(): "+vcMessage);
			}

			@Override
			public void onException(Exception e) {
				lg.error(e.getMessage(), e);
			}
		};
		String jmshost_int = PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntHostInternal);
		int jmsport_int = Integer.parseInt(PropertyLoader.getRequiredProperty(PropertyLoader.jmsIntPortInternal));
		vcMessagingService_int.setConfiguration(delegate, jmshost_int, jmsport_int);

		lg.trace("rest database service (next)");
		RestDatabaseService restDatabaseService = new RestDatabaseService(databaseServerImpl, localAdminDbServer, vcMessagingService_int);

		lg.trace("rest event service (next)");
		RestEventService restEventService = new RestEventService(vcMessagingService_int);

		lg.trace("use verifier (next)");
		UserService userService = new UserService(adminDbTopLevel);

		lg.trace("mongo (next)");
		VCMongoMessage.enabled=true;
		VCMongoMessage.serviceStartup(ServiceName.unknown, port, args);

		lg.info("setting up server configuration");

		lg.trace("register engine (next)");
		Engine.register(true);

		WadlComponent component = new WadlComponent();
		//Server httpServer = component.getServers().add(Protocol.HTTP, 80);
		//Server httpsServer = component.getServers().add(Protocol.HTTPS, 443);

//			Client httpsClient = component.getClients().add(Protocol.HTTPS);
//			Client httpClient = component.getClients().add(Protocol.HTTP);
		lg.trace("adding FILE protcol");
		@SuppressWarnings("unused")
		Client httpClient = component.getClients().add(Protocol.FILE);
		lg.trace("adding CLAP protcol");
		@SuppressWarnings("unused")
		Client clapClient = component.getClients().add(Protocol.CLAP);

		// HTTP only. TLS is terminated by the ingress in every deployment, and the https branch
		// that used to live here -- a Restlet HTTPS server reading a JKS keystore whose password
		// was itself PBE-encrypted with the database password -- was dead: every site passes
		// protocol=http, and /run/secrets/keystorefile is not mounted in any container.
		lg.trace("adding HTTP");
		component.getServers().add(Protocol.HTTP, port);

		lg.trace("create config");
		Configuration templateConfiguration = new Configuration();
		templateConfiguration.setObjectWrapper(new DefaultObjectWrapper());

		lg.trace("create app");
		boolean bIgnoreHostMismatchForHealthService = true; // HealthService connects via localhost, this will never match host in production cert
		boolean bIgnoreCertProblemsForHealthService = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreCertProblems, false);
		User testUser = localAdminDbServer.getUser(TEST_USER);
		UserInfo testUserInfo = localAdminDbServer.getUserInfo(testUser.getID()); // lookup hashed auth credentials in database.
		HealthService healthService = new HealthService(vcMessagingService_int, databaseServerImpl, restEventService,
				bIgnoreCertProblemsForHealthService, bIgnoreHostMismatchForHealthService,
				testUserInfo);
		AdminService adminService = new AdminService(adminDbTopLevel, databaseServerImpl);
		RpcService rpcService = new RpcService(vcMessagingService_int);
		WadlApplication app = new VCellApiApplication(restDatabaseService, userService, rpcService, restEventService, adminService, templateConfiguration, healthService, javascriptDir);
		lg.trace("attach app");
		component.getDefaultHost().attach(app);

		lg.info("component start()");
		lg.trace("start component");
		component.start();
		lg.info("component ended.");
		lg.trace("component started");

		lg.trace("start VCell Health Monitoring service");
		healthService.start();
	}

	public static void main(String[] args) {
		try {

			// A standalone service takes its configuration from the container environment. The desktop
			// client, the CLI and the admin tools deliberately do not -- they run on machines whose
			// environment VCell does not control -- so this is installed per service rather than being
			// the default in PropertyLoader. vcell-rest installs CDIVCellConfigProvider for the same reason.
			PropertyLoader.setConfigProvider(new EnvironmentConfigProvider());
			PropertyLoader.loadProperties(REQUIRED_SERVICE_PROPERTIES);
			lg.debug("properties loaded");

			Injector injector = Guice.createInjector(new VCellServerModule());

			VCellApiMain vCellApiMain = injector.getInstance(VCellApiMain.class);
			vCellApiMain.init(args);

		} catch (Throwable e) {
			lg.error("VCellApiMain failed", e);
			System.exit(1);
		}
	}

	private static final String REQUIRED_SERVICE_PROPERTIES[] = {
			PropertyLoader.vcellServerIDProperty,
			PropertyLoader.vcellServerPrefixV0,
			PropertyLoader.vcellSoftwareVersion,
			PropertyLoader.installationRoot,
			PropertyLoader.dbConnectURL,
			PropertyLoader.dbDriverName,
			PropertyLoader.dbUserid,
			PropertyLoader.dbPasswordFile,
			PropertyLoader.userTimezone,
			PropertyLoader.mongodbHostInternal,
			PropertyLoader.mongodbPortInternal,
			PropertyLoader.mongodbDatabase,
			PropertyLoader.jmsIntHostInternal,
			PropertyLoader.jmsIntPortInternal,
			PropertyLoader.simdataCacheSizeProperty,
			PropertyLoader.n5DataDir,
//			PropertyLoader.jmsUser,
//			PropertyLoader.jmsPasswordFile,
			PropertyLoader.jmsBlobMessageUseMongo,
			PropertyLoader.vcellSMTPHostName,
			PropertyLoader.vcellSMTPPort,
			PropertyLoader.vcellSMTPEmailAddress,
			PropertyLoader.vcellapiPublicKey,
			PropertyLoader.vcellapiPrivateKey
};

}

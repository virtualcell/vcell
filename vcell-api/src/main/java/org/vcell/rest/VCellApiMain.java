package org.vcell.rest;

import java.io.File;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.restlet.Client;
import org.restlet.Server;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.ext.wadl.WadlComponent;
import org.restlet.util.Series;
import org.vcell.api.client.VCellApiClient;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.optimization.OptServerImpl;
import org.vcell.rest.admin.AdminService;
import org.vcell.rest.events.RestEventService;
import org.vcell.rest.health.HealthService;
import org.vcell.rest.rpc.RpcService;
import org.vcell.rest.server.RestDatabaseService;
import org.vcell.service.VCellServiceHelper;
import org.vcell.util.SessionLog;
import org.vcell.util.document.User;
import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCellServerID;
import org.vcell.util.logging.WatchLogging;

import cbit.vcell.message.VCDestination;
import cbit.vcell.message.VCMessage;
import cbit.vcell.message.VCMessagingDelegate;
import cbit.vcell.message.VCMessagingService;
import cbit.vcell.message.VCRpcRequest;
import cbit.vcell.message.server.ManageUtils;
import cbit.vcell.message.server.ServiceInstanceStatus;
import cbit.vcell.message.server.ServiceProvider;
import cbit.vcell.message.server.bootstrap.ServiceType;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabasePolicySQL;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.DbDriver;
import cbit.vcell.modeldb.LocalAdminDbServer;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.PythonSupport;
import cbit.vcell.resource.PythonSupport.PythonPackage;
import cbit.vcell.resource.StdoutSessionLog;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

public class VCellApiMain {
	
	private final static String TEST_USER = "vcellNagios";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			WatchLogging.init(TimeUnit.MINUTES.toMillis(5), "vcell.watchLog4JInterval");
			//don't use static field -- want to initialize logging first
			Logger lg = Logger.getLogger(VCellApiMain.class);
			DatabasePolicySQL.lg.setLevel(Level.WARN);
			DbDriver.lg.setLevel(Level.WARN);
			VCellApiClient.lg.setLevel(java.util.logging.Level.INFO);

			if (args.length!=3){
				System.out.println("usage: VCellApiMain javascriptDir (-|logDir) port");
				System.exit(1);
			}
			File javascriptDir = new File(args[0]);
			if (!javascriptDir.isDirectory()){
				throw new RuntimeException("javascriptDir '"+args[0]+"' is not a directory");
			}

			PropertyLoader.loadProperties( ); //don't validate
			
			lg.trace("properties loaded");
			
			//
			// Redirect output to the logfile (append if exists)
			//
			String logdir = args[1];
			ServiceInstanceStatus serviceInstanceStatus = new ServiceInstanceStatus(VCellServerID.getSystemServerID(), ServiceType.API, 1, ManageUtils.getHostName(), new Date(), true);
			if (lg.isTraceEnabled()) {
				lg.trace("log redirection to " + logdir);
			}
			ServiceProvider.initLog(serviceInstanceStatus, logdir);
			
			String portString = args[2];
			Integer port=null; // was hard-coded at 8080
			try {
				port = Integer.parseInt(portString);
			}catch (NumberFormatException e){
				e.printStackTrace();
				throw new RuntimeException("failed to parse port argument '"+portString+"'",e);
			}
			
		    System.out.println("connecting to database");

			final SessionLog log = new StdoutSessionLog("VCellWebApi");
			lg.trace("oracle factory (next)");
			ConnectionFactory conFactory = DatabaseService.getInstance().createConnectionFactory(log);
			KeyFactory keyFactory = conFactory.getKeyFactory();
			lg.trace("database impl (next)");
			DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory, log);
			lg.trace("local db server (next)");
			LocalAdminDbServer localAdminDbServer = new LocalAdminDbServer(conFactory, keyFactory, log);
			lg.trace("admin db server (next)");
			AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory, log);
			
			lg.trace("messaging service (next)");
			VCMessagingService vcMessagingService = VCellServiceHelper.getInstance().loadService(VCMessagingService.class);
			vcMessagingService.setDelegate(new VCMessagingDelegate() {
				
				@Override
				public void onTraceEvent(String string) {
					System.out.println("Trace: "+string);
				}
				
				@Override
				public void onRpcRequestSent(VCRpcRequest vcRpcRequest, UserLoginInfo userLoginInfo, VCMessage vcRpcRequestMessage) {
					System.out.println("request sent:");
				}
				
				@Override
				public void onRpcRequestProcessed(VCRpcRequest vcRpcRequest, VCMessage rpcVCMessage) {
					System.out.println("request processed:");
				}
				
				@Override
				public void onMessageSent(VCMessage message, VCDestination desintation) {
					System.out.println("message sent:");
				}
				
				@Override
				public void onMessageReceived(VCMessage vcMessage, VCDestination vcDestination) {
					System.out.println("message received");
				}
				
				@Override
				public void onException(Exception e) {
					System.out.println("Exception: "+e.getMessage());
					e.printStackTrace();
				}
			});
							
			lg.trace("rest database service (next)");
			RestDatabaseService restDatabaseService = new RestDatabaseService(databaseServerImpl, localAdminDbServer, vcMessagingService, log);
			
			lg.trace("rest event service (next)");
			RestEventService restEventService = new RestEventService(vcMessagingService, log);
			
			lg.trace("use verifier (next)");
			UserVerifier userVerifier = new UserVerifier(adminDbTopLevel);

			lg.trace("mongo (next)");
			VCMongoMessage.enabled=true;
			VCMongoMessage.serviceStartup(ServiceName.unknown, port, args);

			System.out.println("setting up server configuration");

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
			
			lg.trace("adding CLAP https");
			File keystorePath = new File(PropertyLoader.getRequiredProperty(PropertyLoader.vcellapiKeystoreFile));
			String keystorePassword = PropertyLoader.getSecretValue(PropertyLoader.vcellapiKeystorePswd, PropertyLoader.vcellapiKeystorePswdFile);
			try {
	        		//
	        		// keystorePassword may be encrypted with dbPassword, if it is decypt it.
				//
		        String dbPassword = PropertyLoader.getSecretValue(PropertyLoader.dbPasswordValue, PropertyLoader.dbPasswordFile);
		        SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
		        SecretKey key = kf.generateSecret(new PBEKeySpec(dbPassword.toCharArray())); 
		        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES"); 
		        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(new byte[] {32,11,55,121,01,42,89,11}, 20)); 
		        keystorePassword = new String(pbeCipher.doFinal(DatatypeConverter.parseBase64Binary(keystorePassword))); 
			}catch (Exception e){
				System.out.println("password unhashing didn't work - trying clear text password");
				e.printStackTrace();
			}
			Server httpsServer = component.getServers().add(Protocol.HTTPS,port);
			Series<Parameter> parameters = httpsServer.getContext().getParameters();
			parameters.add("keystorePath", keystorePath.toString());
			parameters.add("keystorePassword", keystorePassword);
			parameters.add("keystoreType", "JKS");
			parameters.add("keyPassword", keystorePassword);
			parameters.add("disabledCipherSuites",
					"SSL_RSA_WITH_3DES_EDE_CBC_SHA "
					+ "SSL_DHE_RSA_WITH_DES_CBC_SHA "
					+ "SSL_DHE_DSS_WITH_DES_CBC_SHA");
			parameters.add("enabledCipherSuites",
					"TLS_DHE_DSS_WITH_AES_128_CBC_SHA "
					+ "TLS_DHE_RSA_WITH_AES_128_CBC_SHA "
					+ "TLS_RSA_WITH_AES_128_CBC_SHA "
					+ "TLS_DHE_DSS_WITH_AES_256_CBC_SHA "
					+ "TLS_DHE_RSA_WITH_AES_256_CBC_SHA "
					+ "TLS_RSA_WITH_AES_256_CBC_SHA");
			
			lg.trace("create config");
			Configuration templateConfiguration = new Configuration();
			templateConfiguration.setObjectWrapper(new DefaultObjectWrapper());
			
			lg.trace("verify python installation");
			PythonSupport.verifyInstallation(new PythonPackage[] { PythonPackage.COPASI, PythonPackage.LIBSBML, PythonPackage.THRIFT });

			lg.trace("start Optimization Service");
			OptServerImpl optServerImpl = new OptServerImpl();
			optServerImpl.start();
			
			
			lg.trace("create app");
			boolean bIgnoreHostProblems = true;
			boolean bIgnoreCertProblems = true;
			User testUser = localAdminDbServer.getUser(TEST_USER);
			UserInfo testUserInfo = localAdminDbServer.getUserInfo(testUser.getID()); // lookup hashed auth credentials in database.
			HealthService healthService = new HealthService(restEventService, "localhost", port, bIgnoreCertProblems, bIgnoreHostProblems, testUserInfo.userid, testUserInfo.digestedPassword0);
			AdminService adminService = new AdminService(adminDbTopLevel, databaseServerImpl);
			RpcService rpcService = new RpcService(vcMessagingService);
			WadlApplication app = new VCellApiApplication(restDatabaseService, userVerifier, optServerImpl, rpcService, restEventService, adminService, templateConfiguration, healthService, javascriptDir);
			lg.trace("attach app");
			component.getDefaultHost().attach(app);  

			System.out.println("component start()");
			lg.trace("start component");
			component.start();
			System.out.println("component ended.");
			lg.trace("component started");

			lg.trace("start VCell Health Monitoring service");
			healthService.start();
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}

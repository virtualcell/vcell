package org.vcell.restopt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.engine.Engine;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.ext.wadl.WadlComponent;
import org.vcell.optimization.OptServerImpl;

import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.mongodb.VCMongoMessage.ServiceName;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.PythonSupport;
import cbit.vcell.resource.PythonSupport.PythonPackage;

public class VCellOptMain {
	
	private final static Logger lg = LogManager.getLogger(VCellOptMain.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if (args.length!=1){
				System.out.println("usage: VCellApiMain port");
				System.exit(1);
			}
			
			PropertyLoader.loadProperties( ); //don't validate
			
			lg.debug("properties loaded");
			
			String portString = args[0];
			Integer port=null; // was hard-coded at 8080
			try {
				port = Integer.parseInt(portString);
			}catch (NumberFormatException e){
				e.printStackTrace();
				throw new RuntimeException("failed to parse port argument '"+portString+"'",e);
			}
			
			lg.trace("mongo (next)");
			VCMongoMessage.enabled=true;
			VCMongoMessage.serviceStartup(ServiceName.unknown, port, args);

			System.out.println("setting up server configuration");

			lg.trace("register engine (next)");
			Engine.register(true);

			WadlComponent component = new WadlComponent();
			Server httpServer = component.getServers().add(Protocol.HTTP,port);
			
			lg.trace("verify python installation");
			PythonSupport.verifyInstallation(new PythonPackage[] { PythonPackage.COPASI, PythonPackage.LIBSBML, PythonPackage.THRIFT });

			lg.trace("start Optimization Service");
			OptServerImpl optServerImpl = new OptServerImpl();
			optServerImpl.start();
			
			
			lg.trace("create app");
//			HealthService healthService = new HealthService(restEventService, "localhost", port, bIgnoreCertProblems, bIgnoreHostProblems, testUserInfo.userid, testUserInfo.digestedPassword0);
			WadlApplication app = new VCellOptApplication(optServerImpl);
			lg.trace("attach app");
			component.getDefaultHost().attach(app);  

			System.out.println("component start()");
			lg.trace("start component");
			component.start();
			System.out.println("component ended.");
			lg.trace("component started");

//			lg.trace("start VCell Health Monitoring service");
//			healthService.start();
			
		} catch (Exception e) {
			lg.error(e.getMessage(), e);
		}
	}
}

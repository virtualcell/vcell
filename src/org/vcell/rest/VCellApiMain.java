package org.vcell.rest;

import org.restlet.Client;
import org.restlet.Server;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.ext.wadl.WadlComponent;
import org.restlet.util.Series;
import org.vcell.util.PropertyLoader;
import org.vcell.util.SessionLog;
import org.vcell.util.StdoutSessionLog;

import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.vcell.message.server.dispatcher.SimulationDatabase;
import cbit.vcell.message.server.dispatcher.SimulationDatabaseDirect;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.modeldb.DbDriver;
import cbit.vcell.modeldb.ResultSetDBTopLevel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

public class VCellApiMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if (args.length!=2){
				System.out.println("usage: VCellApiMain keystorePath keystorePassword");
				System.exit(1);
			}
			String keystorePath = args[0];
			String keystorePassword = args[1];
			
			System.out.println("connecting to database");
			
			SimulationDatabase simulationDatabase = null;
			AdminDBTopLevel adminDbTopLevel = null;
			
			try {
				PropertyLoader.loadProperties();
				final SessionLog log = new StdoutSessionLog("VCellWebApi");
				KeyFactory keyFactory = new OracleKeyFactory();
				DbDriver.setKeyFactory(keyFactory);
				ConnectionFactory conFactory = new OraclePoolingConnectionFactory(log);
				DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory, log);
				adminDbTopLevel = new AdminDBTopLevel(conFactory, log);
				ResultSetDBTopLevel resultSetDbTopLevel = new ResultSetDBTopLevel(conFactory, log);
				simulationDatabase = new SimulationDatabaseDirect(resultSetDbTopLevel, adminDbTopLevel, databaseServerImpl,log);
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("setting up server configuration");

			WadlComponent component = new WadlComponent();
			//Server httpServer = component.getServers().add(Protocol.HTTP, 8182);
			//Server httpsServer = component.getServers().add(Protocol.HTTPS, 443);
			Client clapClient = component.getClients().add(Protocol.CLAP);
			Server httpsServer = component.getServers().add(Protocol.HTTPS,8080);
			Series<Parameter> parameters = httpsServer.getContext().getParameters();
			parameters.add("keystorePath", keystorePath);
			parameters.add("keystorePassword", keystorePassword);
			parameters.add("keystoreType", "JKS");
			parameters.add("keyPassword", keystorePassword);
			
			UserVerifier userVerifier = new UserVerifier(adminDbTopLevel);
			
			Configuration templateConfiguration = new Configuration();
			templateConfiguration.setObjectWrapper(new DefaultObjectWrapper());
			
			WadlApplication app = new VCellApiApplication(simulationDatabase,adminDbTopLevel,userVerifier,templateConfiguration);
			component.getDefaultHost().attach(app);  

			System.out.println("component start()");
			component.start();
			System.out.println("component ended.");

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}

package org.vcell.rest.server;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
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

public class ServerTest extends ServerResource {
	// URI of the root directory.  
	public static final String ROOT_URI = "file:///c:/temp/";  

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			PropertyLoader.loadProperties();
			final SessionLog log = new StdoutSessionLog("SimInfoServer");

//			KeyFactory keyFactory = new OracleKeyFactory();
//			DbDriver.setKeyFactory(keyFactory);
//			ConnectionFactory conFactory = new OraclePoolingConnectionFactory(log);
//			DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory, log);
//			AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory, log);
//			ResultSetDBTopLevel resultSetDbTopLevel = new ResultSetDBTopLevel(conFactory, log);
//			final SimulationDatabase simulationDatabase = new SimulationDatabaseDirect(resultSetDbTopLevel, adminDbTopLevel, databaseServerImpl,log);
			final SimulationDatabase simulationDatabase = null;
			
			// // Create the HTTP server and listen on port 8182
			// new Server(Protocol.HTTP, 8182, ServerTest.class).start();

			// Create a new Restlet component and add a HTTP server connector to
			// it
			Component component = new Component();
			component.getServers().add(Protocol.HTTP, 8182);
			component.getClients().add(Protocol.FILE);
			
			Application app = new SimApplication(simulationDatabase);
			  
			// Attach the application to the component and start it  
			component.getDefaultHost().attach(app);  
			// Then attach it to the local host
//			component.getDefaultHost().attach("/trace", ServerTest.class);



			// Now, let's start the component!
			// Note that the HTTP server connector is also automatically
			// started.
			component.start();
			System.out.println("component ended.");

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	@Get
	public String toString() {
		return "Resource URI  : " + getReference() + '\n' + "Root URI      : "
				+ getRootRef() + '\n' + "Routed part   : "
				+ getReference().getBaseRef() + '\n' + "Remaining part: "
				+ getReference().getRemainingPart();
	}

};

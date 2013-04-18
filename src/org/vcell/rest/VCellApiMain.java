package org.vcell.rest;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.data.Protocol;
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

public class VCellApiMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			PropertyLoader.loadProperties();
			final SessionLog log = new StdoutSessionLog("VCellWebApi");
			KeyFactory keyFactory = new OracleKeyFactory();
			DbDriver.setKeyFactory(keyFactory);
			ConnectionFactory conFactory = new OraclePoolingConnectionFactory(log);
			DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(conFactory, keyFactory, log);
			AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(conFactory, log);
			ResultSetDBTopLevel resultSetDbTopLevel = new ResultSetDBTopLevel(conFactory, log);
			SimulationDatabase simulationDatabase = new SimulationDatabaseDirect(resultSetDbTopLevel, adminDbTopLevel, databaseServerImpl,log);
//			SimulationDatabase simulationDatabase = null;

			Component component = new Component();
			component.getServers().add(Protocol.HTTP, 8182);
			
			Application app = new VCellApiApplication(simulationDatabase,adminDbTopLevel);
			component.getDefaultHost().attach(app);  
			component.start();
			System.out.println("component ended.");

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
};

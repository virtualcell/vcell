package org.vcell.rest;

import org.restlet.data.Protocol;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.ext.wadl.WadlComponent;
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

			WadlComponent component = new WadlComponent();
			component.getServers().add(Protocol.HTTP, 8182);
			
			WadlApplication app = new VCellApiApplication(simulationDatabase,adminDbTopLevel);
			component.getDefaultHost().attach(app);  
			component.start();
			System.out.println("component ended.");

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
};

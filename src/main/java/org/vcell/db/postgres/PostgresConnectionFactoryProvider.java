package org.vcell.db.postgres;

import java.sql.SQLException;

import org.scijava.plugin.Plugin;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.KeyFactory;
import org.vcell.db.spi.Database;
import org.vcell.util.SessionLog;

import oracle.ucp.UniversalConnectionPoolException;

@Plugin(type = Database.class)
public class PostgresConnectionFactoryProvider implements Database {

	@Override
	public ConnectionFactory createConnctionFactory(SessionLog sessionLog, String argDriverName, String argConnectURL, String argUserid, String argPassword) {
		try {
			return new PostgresConnectionFactory(sessionLog, argDriverName, argConnectURL, argUserid, argPassword);
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException 
				| UniversalConnectionPoolException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create OraclePoolingConnectionFactory: "+e.getMessage(),e);
		}
	}

	@Override
	public KeyFactory createKeyFactory() {
		return new PostgresKeyFactory();
	}

	@Override
	public String getDriverClassName() {
		return "org.postgresql.Driver";
	}

}

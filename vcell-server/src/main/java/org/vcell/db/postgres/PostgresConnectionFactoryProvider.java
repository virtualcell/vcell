package org.vcell.db.postgres;

import org.scijava.plugin.Plugin;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.KeyFactory;
import org.vcell.db.spi.Database;

@Plugin(type = Database.class)
public class PostgresConnectionFactoryProvider implements Database {

	public static final String POSTGRESQL_DRIVER_NAME = "org.postgresql.Driver";

	@Override
	public ConnectionFactory createConnctionFactory(String argDriverName, String argConnectURL, String argUserid, String argPassword) {
		try {
			return new PostgresConnectionFactory(argDriverName, argConnectURL, argUserid, argPassword);
		} catch (Exception e) {
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
		return POSTGRESQL_DRIVER_NAME;
	}

}

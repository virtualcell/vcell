package org.vcell.db.oracle;

import java.sql.SQLException;

import org.scijava.plugin.Plugin;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.KeyFactory;
import org.vcell.db.spi.Database;

import oracle.ucp.UniversalConnectionPoolException;

@Plugin(type = Database.class)
public class OraclePoolingConnectionFactoryProvider implements Database {

	public static final String ORACLE_DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
	@Override
	public ConnectionFactory createConnctionFactory(String argDriverName, String argConnectURL, String argUserid, String argPassword) {
		try {
			return new OraclePoolingConnectionFactory(argDriverName, argConnectURL, argUserid, argPassword);
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException 
				| UniversalConnectionPoolException e) {
			e.printStackTrace();
			throw new RuntimeException("failed to create OraclePoolingConnectionFactory: "+e.getMessage(),e);
		}
	}

	@Override
	public KeyFactory createKeyFactory() {
		return new OracleKeyFactory();
	}

	@Override
	public String getDriverClassName() {
		return ORACLE_DRIVER_NAME;
	}

}

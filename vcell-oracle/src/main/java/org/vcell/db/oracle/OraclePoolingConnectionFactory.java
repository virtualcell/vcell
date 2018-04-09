/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.db.oracle;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseSyntax;
import org.vcell.db.KeyFactory;
import org.vcell.util.ConfigurationException;

import cbit.vcell.resource.PropertyLoader;
import oracle.ucp.UniversalConnectionPoolException;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.ValidConnection;

/**
 * This type was created in VisualAge.y
 */
public final class OraclePoolingConnectionFactory implements ConnectionFactory  {

	private String connectionCacheName = null;
	private PoolDataSource poolDataSource = null;
	private static final Logger lg = LogManager.getLogger(OraclePoolingConnectionFactory.class);

OraclePoolingConnectionFactory() throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException, ConfigurationException, UniversalConnectionPoolException {
	this(PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName),
			PropertyLoader.getRequiredProperty(PropertyLoader.dbConnectURL),
			PropertyLoader.getRequiredProperty(PropertyLoader.dbUserid),
		    PropertyLoader.getSecretValue(PropertyLoader.jmsPasswordValue, PropertyLoader.jmsPasswordFile)
);
}

OraclePoolingConnectionFactory(String argDriverName, String argConnectURL, String argUserid, String argPassword) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException, UniversalConnectionPoolException {
	connectionCacheName = "UCP_ImplicitPool_" + System.nanoTime();

	poolDataSource = PoolDataSourceFactory.getPoolDataSource();
	poolDataSource.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
	poolDataSource.setValidateConnectionOnBorrow(true);
	poolDataSource.setMaxConnectionReuseTime(300);
	poolDataSource.setAbandonedConnectionTimeout(300);
	poolDataSource.setInactiveConnectionTimeout(300);
	poolDataSource.setConnectionPoolName(connectionCacheName);
	// set DataSource properties
	poolDataSource.setURL(argConnectURL);
	poolDataSource.setUser(argUserid);
	poolDataSource.setPassword(argPassword);
	// set cache properties
	poolDataSource.setMinPoolSize(2);
	poolDataSource.setMaxPoolSize(10);
	poolDataSource.setInitialPoolSize(2);
}

@Override
public synchronized void close() throws java.sql.SQLException {
}

public void failed(Connection con, Object lock) throws SQLException {
	if (lg.isTraceEnabled()) lg.trace("OraclePoolingConnectionFactory.failed("+con+")");
	((ValidConnection)con).setInvalid();
	release(con, lock);
}

@Override
public synchronized Connection getConnection(Object lock) throws SQLException {
	Connection conn = null;
	try {
		conn = poolDataSource.getConnection();
	} catch (SQLException ex) {
		// might be invalid or stale connection
		if (lg.isTraceEnabled()) lg.trace("first time #getConnection( ) fail " + ex.getMessage() + ", state " + ex.getSQLState() + ", attempting refresh");
		// get connection again.
		try {
			conn = poolDataSource.getConnection();
		} catch (SQLException e) {
			if (lg.isTraceEnabled()) lg.trace("refresh failed");
			lg.error(e.getMessage(),e);
		}
	}
	if (conn == null) {
		throw new SQLException("Cannot get a connection to the database. This could be caused by\n" +
				"1. Max connection limit has reached. No connections are available.\n" +
				"2. there is a problem with database server.\n" +
				"3. there is a problem with network.\n");
	}
	return conn;
}

@Override
public void release(Connection con, Object lock) throws SQLException {
	if (con != null) {
		con.close();
	}
}

@Override
public KeyFactory getKeyFactory() {
	return new OracleKeyFactory();
}

@Override
public DatabaseSyntax getDatabaseSyntax() {
	return DatabaseSyntax.ORACLE;
}

}

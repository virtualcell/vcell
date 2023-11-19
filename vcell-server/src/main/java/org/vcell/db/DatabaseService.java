/*
 * Copyright (C) 1999-2017 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.db;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cbit.vcell.resource.PropertyLoader;
import org.vcell.db.oracle.OraclePoolingConnectionFactoryProvider;
import org.vcell.db.postgres.PostgresConnectionFactoryProvider;

/**
 * Service for database connection factory
 */
public class DatabaseService {
	private static final Logger lg = LogManager.getLogger(DatabaseService.class);
	
	private static DatabaseService service;

	private ConnectionFactory connectionFactory;

	private DatabaseService() {

	}
	
	public static synchronized DatabaseService getInstance(){
		if (service == null){
			service = new DatabaseService();
		}
		return service;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		if (this.connectionFactory != null) {
			throw new RuntimeException("Initialization Error, overriding existing connection factory");
		}
		if (connectionFactory == null) {
			throw new RuntimeException("Initialization Error, cannot set null connection factory");
		}
		this.connectionFactory = connectionFactory;
	}
	
	public ConnectionFactory createConnectionFactory() throws SQLException {
		if (connectionFactory == null) {
			connectionFactory = createConnectionFactory(
					PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName),
					PropertyLoader.getRequiredProperty(PropertyLoader.dbConnectURL),
					PropertyLoader.getRequiredProperty(PropertyLoader.dbUserid),
					PropertyLoader.getSecretValue(PropertyLoader.dbPasswordValue, PropertyLoader.dbPasswordFile));
		}
		return connectionFactory;
	}
	
	private ConnectionFactory createConnectionFactory(String argDriverName, String argConnectURL, String argUserid, String argPassword) throws SQLException {
		switch (argDriverName) {
			case OraclePoolingConnectionFactoryProvider.ORACLE_DRIVER_NAME: {
				Database database = new OraclePoolingConnectionFactoryProvider();
				return database.createConnctionFactory(argDriverName, argConnectURL, argUserid, argPassword);
			}
			case PostgresConnectionFactoryProvider.POSTGRESQL_DRIVER_NAME: {
				Database database = new PostgresConnectionFactoryProvider();
				return database.createConnctionFactory(argDriverName, argConnectURL, argUserid, argPassword);
			}
			default:
				throw new SQLException("no database provider found");
		}
	}
	
}

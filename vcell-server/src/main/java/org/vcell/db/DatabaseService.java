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
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.db.spi.Database;

import cbit.vcell.resource.PropertyLoader;

/**
 * Service for database connection factory
 */
public class DatabaseService {
	private static final Logger lg = LogManager.getLogger(DatabaseService.class);
	
	private static DatabaseService service;

	private DatabaseService() {

	}
	
	public static synchronized DatabaseService getInstance(){
		if (service == null){
			service = new DatabaseService();
		}
		return service;
	}
	
	public ConnectionFactory createConnectionFactory() throws SQLException {
		return createConnectionFactory(
						PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName),
						PropertyLoader.getRequiredProperty(PropertyLoader.dbConnectURL),
						PropertyLoader.getRequiredProperty(PropertyLoader.dbUserid),
						PropertyLoader.getSecretValue(PropertyLoader.dbPasswordValue,PropertyLoader.dbPasswordFile));
	}
	
	public ConnectionFactory createConnectionFactory(String argDriverName, String argConnectURL, String argUserid, String argPassword) throws SQLException {
		try {
			ServiceLoader<Database> databases = ServiceLoader.load(Database.class);
			for (Database database : databases){
				if (database.getDriverClassName().equals(argDriverName)){
					return database.createConnctionFactory(argDriverName, argConnectURL, argUserid, argPassword);
				}
			}
			throw new SQLException("no database provider found");
		} catch (ServiceConfigurationError serviceError){
			lg.error(serviceError);
			throw new SQLException("database provider configuration error: "+serviceError.getMessage(),serviceError);
		}
	}
	
}

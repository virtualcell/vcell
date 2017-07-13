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
import java.util.List;
import java.util.ServiceConfigurationError;

import org.scijava.SciJava;
import org.vcell.db.spi.Database;
import org.vcell.util.SessionLog;

import cbit.vcell.resource.PropertyLoader;

/**
 * Service for database connection factory
 */
public class DatabaseService {
	
	private static DatabaseService service;
	private SciJava scijava;
	
	private DatabaseService() {
		scijava = new SciJava();
	}
	
	public static synchronized DatabaseService getInstance(){
		if (service == null){
			service = new DatabaseService();
		}
		return service;
	}
	
	public ConnectionFactory createConnectionFactory(SessionLog sessionLog) throws SQLException {
		try {
			List<Database> databases = scijava.plugin().createInstancesOfType(Database.class);
			if (databases.size()>0){
				Database database = databases.get(0);
				return database.createConnctionFactory(sessionLog,
						PropertyLoader.getRequiredProperty(PropertyLoader.dbDriverName),
						PropertyLoader.getRequiredProperty(PropertyLoader.dbConnectURL),
						PropertyLoader.getRequiredProperty(PropertyLoader.dbUserid),
						PropertyLoader.getRequiredProperty(PropertyLoader.dbPassword));
			}else{
				throw new SQLException("no database provider found");
			}
		} catch (ServiceConfigurationError serviceError){
			serviceError.printStackTrace();
			throw new SQLException("database provider configuration error: "+serviceError.getMessage(),serviceError);
		}
	}
	
	public ConnectionFactory createConnectionFactory(SessionLog sessionLog, String argDriverName, String argConnectURL, String argUserid, String argPassword) throws SQLException {
		try {
			List<Database> databases = scijava.plugin().createInstancesOfType(Database.class);
			if (databases.size()>0){
				Database database = databases.get(0);
				return database.createConnctionFactory(sessionLog, argDriverName, argConnectURL, argUserid, argPassword);
			}else{
				throw new SQLException("no database provider found");
			}
		} catch (ServiceConfigurationError serviceError){
			serviceError.printStackTrace();
			throw new SQLException("database provider configuration error: "+serviceError.getMessage(),serviceError);
		}
	}
	
	public KeyFactory createKeyFactory() throws SQLException {
		try {
			List<Database> databases = scijava.plugin().createInstancesOfType(Database.class);
			if (databases.size()>0){
				Database database = databases.get(0);
				return database.createKeyFactory();
			}else{
				throw new SQLException("no database provider found");
			}
		} catch (ServiceConfigurationError serviceError){
			serviceError.printStackTrace();
			throw new SQLException("database provider configuration error: "+serviceError.getMessage(),serviceError);
		}
	}
	
}

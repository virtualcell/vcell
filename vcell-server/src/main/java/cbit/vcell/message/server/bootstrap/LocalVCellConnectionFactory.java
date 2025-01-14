/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.message.server.bootstrap;

import cbit.vcell.export.server.ExportServiceImpl;
import cbit.vcell.message.server.dispatcher.SimulationDatabaseDirect;
import cbit.vcell.modeldb.AdminDBTopLevel;
import cbit.vcell.modeldb.DatabaseServerImpl;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.Auth0ConnectionUtils;
import cbit.vcell.server.ConnectionException;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.server.VCellConnectionFactory;
import cbit.vcell.simdata.Cachetable;
import cbit.vcell.simdata.DataSetControllerImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.api.client.VCellApiClient;
import org.vcell.db.ConnectionFactory;
import org.vcell.db.DatabaseService;
import org.vcell.db.KeyFactory;
import org.vcell.util.DataAccessException;
import org.vcell.util.document.UserLoginInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

/**
 * This type was created in VisualAge.
 */
public class LocalVCellConnectionFactory implements VCellConnectionFactory {
	public static final Logger lg = LogManager.getLogger(LocalVCellConnectionFactory.class);

	private ConnectionFactory connectionFactory = null;
	private final Auth0ConnectionUtils auth0ConnectionUtils;
	private final VCellApiClient vcellApiClient;

	public LocalVCellConnectionFactory() {
        try {
            this.vcellApiClient = new VCellApiClient("vcell-dev.cam.uchc.edu", 443, "/api/v0");
			this.auth0ConnectionUtils = new Auth0ConnectionUtils(vcellApiClient);
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
            throw new RuntimeException(e);
        }
	}


	@Override
	public VCellConnection createVCellConnection(UserLoginInfo userLoginInfo) throws ConnectionException {
		try {
			if (connectionFactory == null) {
				connectionFactory = DatabaseService.getInstance().createConnectionFactory();
			}

			KeyFactory keyFactory = connectionFactory.getKeyFactory();
			LocalVCellConnection.setDatabaseResources(connectionFactory, keyFactory);
			AdminDBTopLevel adminDbTopLevel = new AdminDBTopLevel(connectionFactory);
			DatabaseServerImpl databaseServerImpl = new DatabaseServerImpl(connectionFactory,keyFactory);

			userLoginInfo.setUser(adminDbTopLevel.getUser(userLoginInfo.getUserName(), true));

			boolean bCache = false;
			Cachetable cacheTable = null;
			DataSetControllerImpl dataSetControllerImpl = new DataSetControllerImpl(cacheTable,
					new File(PropertyLoader.getRequiredProperty(PropertyLoader.primarySimDataDirInternalProperty)),
					new File(PropertyLoader.getRequiredProperty(PropertyLoader.secondarySimDataDirInternalProperty)));
			SimulationDatabaseDirect simulationDatabase = new SimulationDatabaseDirect(adminDbTopLevel, databaseServerImpl, bCache);
			ExportServiceImpl exportServiceImpl = new ExportServiceImpl();
			LocalVCellConnection vcConn = new LocalVCellConnection(userLoginInfo, simulationDatabase, dataSetControllerImpl, exportServiceImpl);
			return vcConn;
		} catch (SQLException | FileNotFoundException | DataAccessException apiException){
			throw new RuntimeException(apiException);
		}
    }

	@Override
	public Auth0ConnectionUtils getAuth0ConnectionUtils() {
		return auth0ConnectionUtils;
	}

}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.standalone;

import cbit.vcell.client.VCellClient;
import cbit.vcell.client.VCellLookAndFeel;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.resource.ErrorUtils;
import cbit.vcell.resource.LibraryLoaderThread;
import cbit.vcell.resource.PropertyLoader;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.apache.commons.lang3.ArrayUtils;
import org.vcell.dependency.server.VCellServerModule;
import org.vcell.util.document.UserLoginInfo;
import org.vcell.util.document.VCDocument;

import java.io.IOException;

@Singleton
public class VCellClientDevMain {

	private final VCellClient vCellClient;

	@Inject
	public VCellClientDevMain(VCellClient vcellClient) throws IOException {
		this.vCellClient = vcellClient;
		String user = null;
		String password = null;
		VCDocument initialDocument = null;
		UserLoginInfo.DigestedPassword digestedPassword = null;
		if (password != null && password.length() != 0) {
			digestedPassword = new UserLoginInfo.DigestedPassword(password);
		}
		ClientServerInfo csInfo = ClientServerInfo.createLocalServerInfo(user, digestedPassword);
		//call in main thread, since it's quick and not necessarily thread safe
		VCellLookAndFeel.setVCellLookAndFeel();

		vcellClient.startClient(initialDocument, csInfo);
	}

	public static void main(String[] args) {
		try {
			ErrorUtils.setDebug(true);
			PropertyLoader.loadProperties(ArrayUtils.addAll(REQUIRED_CLIENT_PROPERTIES, REQUIRED_LOCAL_PROPERTIES));
			Injector injector = Guice.createInjector(new VCellServerModule());

			VCellClientDevMain vcellClientStandalone = injector.getInstance(VCellClientDevMain.class);

			//starting loading libraries
			new LibraryLoaderThread(true).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


/**
 * array of properties required for correct operation
 */
private static final String REQUIRED_CLIENT_PROPERTIES[] = {
	PropertyLoader.installationRoot,
	PropertyLoader.vcellSoftwareVersion,
};

/**
 * array of properties required for correct local operation
 */
private static final String REQUIRED_LOCAL_PROPERTIES[] = {
	PropertyLoader.primarySimDataDirInternalProperty,
	PropertyLoader.secondarySimDataDirInternalProperty,
	PropertyLoader.dbPasswordValue,
	PropertyLoader.dbUserid,
	PropertyLoader.dbDriverName,
	PropertyLoader.dbConnectURL,
	PropertyLoader.userTimezone,
	PropertyLoader.vcellServerIDProperty,
	PropertyLoader.mongodbDatabase,
	PropertyLoader.mongodbHostInternal,
//	PropertyLoader.mongodbLoggingCollection,
	PropertyLoader.mongodbPortInternal,
	PropertyLoader.maxJobsPerScan
};
}

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

import org.vcell.util.AuthenticationException;
import org.vcell.util.SessionLog;
import org.vcell.util.document.UserLoginInfo;

import cbit.sql.ConnectionFactory;
import cbit.sql.KeyFactory;
import cbit.sql.OracleKeyFactory;
import cbit.sql.OraclePoolingConnectionFactory;
import cbit.vcell.server.ConnectionException;
import cbit.vcell.server.VCellConnection;
import cbit.vcell.server.VCellConnectionFactory;
import ncsa.hdf.object.FileFormat;
/**
 * This type was created in VisualAge.
 */
public class LocalVCellConnectionFactory implements VCellConnectionFactory {
	private UserLoginInfo userLoginInfo;
	private SessionLog sessionLog = null;
	private ConnectionFactory connectionFactory = null;

/**
 * LocalVCellConnectionFactory constructor comment.
 */
public LocalVCellConnectionFactory(UserLoginInfo userLoginInfo, SessionLog sessionLog) {
	this.userLoginInfo = userLoginInfo;
	this.sessionLog = sessionLog;
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 12:08:06 PM)
 * @param userID java.lang.String
 * @param password java.lang.String
 */
public void changeUser(UserLoginInfo userLoginInfo) {
	this.userLoginInfo = userLoginInfo;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.server.VCellConnection
 */
public VCellConnection createVCellConnection() throws AuthenticationException, ConnectionException {
	try {
		if (connectionFactory == null) {
			connectionFactory = new OraclePoolingConnectionFactory(sessionLog);
		}
		KeyFactory keyFactory = new OracleKeyFactory();
		LocalVCellConnection.setDatabaseResources(connectionFactory, keyFactory);
		LocalVCellServer vcServer = (LocalVCellServer)(new LocalVCellServerFactory(null,null,"<<local>>",null,connectionFactory, keyFactory, sessionLog)).getVCellServer();
		VCellConnection vcc = vcServer.getVCellConnection(userLoginInfo);
		linkHDFLib();
		return vcc; 
	} catch (AuthenticationException exc) {
		sessionLog.exception(exc);
		throw exc;
	} catch (Throwable exc) {
		sessionLog.exception(exc);
		throw new ConnectionException(exc.getMessage());
	}
}

/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 12:34:14 PM)
 * @param newConFactory cbit.sql.ConnectionFactory
 */
public void setConnectionFactory(cbit.sql.ConnectionFactory newConnectionFactory) {
	connectionFactory = newConnectionFactory;
}

/**
 * trigger loading of HDF library when running local
 */
private void linkHDFLib( ) {
	try { //lifted from hdf5group website
		Class<?> fileclass = Class.forName("ncsa.hdf.object.h5.H5File");
		FileFormat fileformat = (FileFormat)fileclass.newInstance();
		if (fileformat != null) {
			FileFormat.addFileFormat(FileFormat.FILE_TYPE_HDF5, fileformat);
		}
	} catch(Throwable t) {
		t.printStackTrace();
	}
}
}

/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.server;

import java.io.*;

import org.vcell.util.SessionLog;

import cbit.sql.*;
import cbit.vcell.simdata.*;
/**
 * This type was created in VisualAge.
 */
public class LocalVCellConnectionFactory implements VCellConnectionFactory {
	private UserLoginInfo userLoginInfo;
	private SessionLog sessionLog = null;
	private ConnectionFactory connectionFactory = null;
	private boolean bLocal = false;


/**
 * LocalVCellConnectionFactory constructor comment.
 */
public LocalVCellConnectionFactory(UserLoginInfo userLoginInfo, SessionLog sessionLog, boolean bLocal0) {
	this.userLoginInfo = userLoginInfo;
	this.sessionLog = sessionLog;
	bLocal = bLocal0;
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
			connectionFactory = new cbit.sql.OraclePoolingConnectionFactory(sessionLog);
		}
		KeyFactory keyFactory = new cbit.sql.OracleKeyFactory();
		LocalVCellConnection.setDatabaseResources(connectionFactory, keyFactory);
		cbit.vcell.messaging.JmsConnectionFactory jmsConnFactory = null;
		
		if (!bLocal) {
			jmsConnFactory = new cbit.vcell.messaging.JmsConnectionFactoryImpl();
		}
		LocalVCellServer vcServer = (LocalVCellServer)(new LocalVCellServerFactory(null,null,"<<local>>",jmsConnFactory,connectionFactory, keyFactory, sessionLog)).getVCellServer();
		return vcServer.getVCellConnection(userLoginInfo);
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
}

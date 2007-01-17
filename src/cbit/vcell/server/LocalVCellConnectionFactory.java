package cbit.vcell.server;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.io.*;
import cbit.sql.*;
import cbit.vcell.simdata.*;
/**
 * This type was created in VisualAge.
 */
public class LocalVCellConnectionFactory implements VCellConnectionFactory {
	private String userID = null;
	private String password = null;
	private SessionLog sessionLog = null;
	private ConnectionFactory connectionFactory = null;
	private boolean bLocal = false;
/**
 * LocalVCellConnectionFactory constructor comment.
 */
public LocalVCellConnectionFactory(String userID, String password, SessionLog sessionLog) {
	this.userID = userID;
	this.password = password;
	this.sessionLog = sessionLog;
}
/**
 * LocalVCellConnectionFactory constructor comment.
 */
public LocalVCellConnectionFactory(String userID, String password, SessionLog sessionLog, boolean bLocal0) {
	this.userID = userID;
	this.password = password;
	this.sessionLog = sessionLog;
	bLocal = bLocal0;
}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 12:08:06 PM)
 * @param userID java.lang.String
 * @param password java.lang.String
 */
public void changeUser(java.lang.String userID, java.lang.String password) {
	this.userID = userID;
	this.password = password;
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
		DBCacheTable dbCacheTable = new cbit.sql.DBCacheTable(1000*60*30);
		LocalVCellConnection.setDatabaseResources(connectionFactory, keyFactory, dbCacheTable);
		cbit.vcell.messaging.JmsConnectionFactory jmsConnFactory = null;
		
		if (!bLocal) {
			jmsConnFactory = new cbit.vcell.messaging.JmsConnectionFactoryImpl();
		}
		LocalVCellServer vcServer = (LocalVCellServer)(new LocalVCellServerFactory(null,null,"<<local>>",jmsConnFactory,connectionFactory, keyFactory, dbCacheTable, sessionLog)).getVCellServer();
		return vcServer.getVCellConnection(userID, password);
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

package cbit.vcell.server;

import cbit.gui.PropertyLoader;
import cbit.sql.DBCacheTable;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.DataAccessException;
import cbit.util.SessionLog;
import cbit.util.User;
import cbit.vcell.simdata.*;
/**
 * This type was created in VisualAge.
 */
public class LocalVCellServerFactory implements VCellServerFactory {
	private VCellServer vcServer = null;
/**
 * LocalVCellConnectionFactory constructor comment.
 */
public LocalVCellServerFactory(String userid, String password, String hostName, cbit.sql.ConnectionFactory conFactory, cbit.sql.KeyFactory keyFactory, DBCacheTable dbCacheTable, SessionLog sessionLog) throws java.sql.SQLException, java.io.FileNotFoundException, DataAccessException {
	this(userid, password, hostName, null, conFactory, keyFactory, dbCacheTable, sessionLog);
}
/**
 * LocalVCellConnectionFactory constructor comment.
 */
public LocalVCellServerFactory(String userid, String password, String hostName, cbit.vcell.messaging.JmsConnectionFactory jmsConnFactory, cbit.sql.ConnectionFactory conFactory, cbit.sql.KeyFactory keyFactory, DBCacheTable dbCacheTable, SessionLog sessionLog) throws java.sql.SQLException, java.io.FileNotFoundException, DataAccessException {
	try {
		AdminDatabaseServer adminDbServer = new cbit.vcell.modeldb.LocalAdminDbServer(conFactory,keyFactory,sessionLog);
		User adminUser = null;
		if (userid!=null && password!=null){			
			adminUser = adminDbServer.getUser(userid,password);
			if (adminUser==null){
				throw new PermissionException("failed to authenticate user userid "+userid);
			}
			if (!adminUser.getName().equals(PropertyLoader.ADMINISTRATOR_ACCOUNT)){
				throw new PermissionException("userid "+userid+" does not have sufficient privilage");
			}
		}
		cbit.vcell.modeldb.ResultSetCrawler rsCrawler = new cbit.vcell.modeldb.ResultSetCrawler(conFactory,adminDbServer,sessionLog,dbCacheTable);
		vcServer = new LocalVCellServer(true, hostName, jmsConnFactory, adminDbServer, rsCrawler, false);
	} catch (java.rmi.RemoteException e){
	}
}
/**
 * getVCellConnection method comment.
 */
public VCellServer getVCellServer() throws AuthenticationException, ConnectionException {
	return vcServer;
}
}
